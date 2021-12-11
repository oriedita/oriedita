package oriedita.editor.action.selector;

import oriedita.editor.action.BaseMouseHandler;
import oriedita.editor.action.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseMouseHandler_WithSelector extends BaseMouseHandler {
    private final List<ElementSelector<?>> selectors = new ArrayList<>();
    private ElementSelector<?> activeSelector;
    // selectors will change their state, so they can't be used as map indices. We're using the index in the
    // selectors list instead.
    private final Map<Integer, Supplier<ElementSelector<?>>> nextSelectors = new HashMap<>();


    public final <T, F extends ElementSelector<T>> F registerStartingSelector(
            F startingSelector, Supplier<ElementSelector<?>> nextSelector
    ) {
        if (nextSelectors.containsKey(-1)) {
            throw new IllegalStateException("Starting Selector already registered");
        }
        nextSelectors.put(-1, () -> startingSelector);
        this.activeSelector = startingSelector;
        return registerSelector(startingSelector, nextSelector);
    }

    public final <T,F extends ElementSelector<T>> F registerSelector(F selector, Supplier<ElementSelector<?>> nextSelector) {
        selector.setCreasePattern_Worker(d);
        this.nextSelectors.put(selectors.size(), nextSelector);
        this.selectors.add(selector);
        return selector;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (ElementSelector<?> selector : selectors) {
            selector.draw(g2, camera, settings);
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
        MouseEventInfo eventInfo = new MouseEventInfo(e);
        if (activeSelector != null) {
            this.activeSelector.update(p0, eventInfo);
            if (activeSelector.tryFinishSelection()) {
                this.activeSelector = getNextSelector(activeSelector);
            }
        }
        if (activeSelector == null) {
            reset();
        }
        if (activeSelector != null) {
            activeSelector.update(p0, eventInfo); // otherwise, preview would only start after moving the mouse
        }
        super.mousePressed(p0, e);
    }

    /**
     * register all the selectors of the tools, as well as onFinish callbacks etc.
     */
    public abstract void setupSelectors();

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
        for (ElementSelector<?> selector : selectors) {
            selector.reset();
        }
        if (!nextSelectors.containsKey(-1)) {
            throw new IllegalStateException("No starting selector defined");
        }
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
