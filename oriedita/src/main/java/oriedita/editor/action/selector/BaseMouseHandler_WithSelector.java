package oriedita.editor.action.selector;

import org.tinylog.Logger;
import oriedita.editor.action.BaseMouseHandler;
import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;

public abstract class BaseMouseHandler_WithSelector extends BaseMouseHandler {
    private final List<ElementSelector<?>> selectors = new ArrayList<>();
    private ElementSelector<?> activeSelector;
    // selectors will change their state, so they can't be used as map indices. We're using the index in the
    // selectors list instead.
    private final Map<Integer, Supplier<ElementSelector<?>>> nextSelectors = new HashMap<>();
    private final Map<Integer, FinishOn> finishActions = new HashMap<>();
    private final Deque<ElementSelector<?>> selectorHistory = new ArrayDeque<>();

    public enum FinishOn {
        RELEASE, PRESS
    }

    public final <T, F extends ElementSelector<T>> F registerStartingSelector(
            F startingSelector, Supplier<ElementSelector<?>> nextSelector
    ) {
        return registerStartingSelector(startingSelector, FinishOn.PRESS, nextSelector);
    }

    public final <T, F extends ElementSelector<T>> F registerStartingSelector(
            F startingSelector, FinishOn finishAction, Supplier<ElementSelector<?>> nextSelector
    ) {
        assert !nextSelectors.containsKey(-1);

        nextSelectors.put(-1, () -> startingSelector);
        this.activeSelector = startingSelector;
        return registerSelector(startingSelector, finishAction, nextSelector);
    }

    public final <T, F extends ElementSelector<T>> F registerSelector(
            F selector, Supplier<ElementSelector<?>> nextSelector
    ) {
        return registerSelector(selector, FinishOn.PRESS, nextSelector);
    }

    public final <T,F extends ElementSelector<T>> F registerSelector(
            F selector, FinishOn finishAction, Supplier<ElementSelector<?>> nextSelector) {
        assert !selectors.contains(selector);

        selector.setCreasePattern_Worker(d);
        this.nextSelectors.put(selectors.size(), nextSelector);
        this.finishActions.put(selectors.size(), finishAction);
        this.selectors.add(selector);
        return selector;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (ElementSelector<?> selector : selectors) {
            selector.drawPreview(g2, camera, settings);
        }
    }

    @Override
    public void mouseMoved(Point p0, MouseEvent e) {
        if (activeSelector != null) {
            MouseEventInfo eventInfo = new MouseEventInfo(e);
            this.activeSelector.update(p0, eventInfo);
        }
        super.mouseMoved(p0, e);
    }

    @Override
    public void mousePressed(Point p0, MouseEvent e) {
        processFinishAction(FinishOn.PRESS, p0, e);
        super.mousePressed(p0, e);
    }

    @Override
    public void mouseReleased(Point p0, MouseEvent e) {
        processFinishAction(FinishOn.RELEASE, p0, e);
        super.mousePressed(p0, e);
    }

    private void processFinishAction(FinishOn finishAction, Point mousePos, MouseEvent e) {
        MouseEventInfo eventInfo = new MouseEventInfo(e);
        if (activeSelector != null) {
            activeSelector.update(mousePos, eventInfo);
            if (shouldFinishOn(finishAction, activeSelector) && activeSelector.tryFinishSelection()) {
                ElementSelector<?> lastSelector = activeSelector;
                activeSelector = getNextSelector(activeSelector);
                if (activeSelector != null) {
                    selectorHistory.push(lastSelector);
                    activeSelector.update(mousePos, eventInfo); // otherwise, preview would only start after moving the mouse
                }
            }
        }
        if (activeSelector == null) {
            reset();
        }
    }

    private boolean shouldFinishOn(FinishOn finishAction, ElementSelector<?> activeSelector) {
        int ind = selectors.indexOf(activeSelector);
        return finishActions.containsKey(ind) && finishActions.get(ind) == finishAction;
    }

    public void setupAndCheckSelectors() {
        setupSelectors();
        for (ElementSelector<?> selector : selectors) {
            if (shouldFinishOn(FinishOn.RELEASE, selector) && !selector.hasFailedFinishCallback()) {
                Logger.warn("Selector {} in {} is set to finish on release, but does not have a failed finish callback",
                        selector, this);
            }
        }
    }

    /**
     * Reverts `steps` selectors, going back to that many steps before the current selector,
     * resetting all selectors in between (including the last one).
     * when steps==0, just the current step will be reset
     * @param steps number of steps to go back
     */
    protected void revert(int steps) {
        assert steps >= 0;
        assert steps <= selectorHistory.size();
        activeSelector.reset();
        for (int i = 0; i < steps; i++) {
            activeSelector = selectorHistory.pop();
            activeSelector.reset();
        }
    }

    /**
     * Reverts selectors, going back to the selector given in the argument,
     * resetting all selectors in between (including the last one).
     * if the selector is the currently active one, it will just be reset.
     * @param selector selector to go back to
     */
    protected void revertTo(ElementSelector<?> selector) {
        assert selectorHistory.contains(selector) || selector == activeSelector;
        activeSelector.reset();
        while (activeSelector != selector) {
            activeSelector = selectorHistory.pop();
            activeSelector.reset();
        }
    }

    protected void onAnyFail(Runnable callback, ElementSelector<?>... selectors) {
        for (ElementSelector<?> selector : selectors) {
            selector.onFail(callback);
        }
    }

    /**
     * register all the selectors of the tools, as well as onFinish callbacks etc.
     */
    protected abstract void setupSelectors();

    /**
     * which selector (if any) should come after the current selector. should only return selectors that were
     * registered with registerSelector beforehand, or null if no selector should come afterwards.
     */
    protected ElementSelector<?> getNextSelector(ElementSelector<?> currentSelector) {
        Supplier<ElementSelector<?>> nextSelector = nextSelectors.get(selectors.indexOf(currentSelector));
        if (nextSelector != null) {
            return nextSelector.get();
        }
        return null;
    }

    @Override
    public void mouseDragged(Point p0, MouseEvent e) {
        if (activeSelector != null) {
            MouseEventInfo eventInfo = new MouseEventInfo(e);
            this.activeSelector.update(p0, eventInfo);
        }
        super.mouseDragged(p0, e);
    }

    @Override
    public void reset() {
        super.reset();
        selectorHistory.clear();
        selectors.forEach(ElementSelector::reset);

        assert nextSelectors.containsKey(-1);

        activeSelector = nextSelectors.get(-1).get();
    }

    // override all of these because they're usually not needed by the
    // subclasses anymore
    @Override
    public void mouseMoved(Point p0) {
    }

    @Override
    public void mousePressed(Point p0) {
    }

    @Override
    public void mouseDragged(Point p0) {
    }

    @Override
    public void mouseReleased(Point p0) {
    }
}
