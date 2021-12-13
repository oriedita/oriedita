package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.Drawer;
import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * to simplify the drawing and implementation of tools where specific things have to be
 * clicked on using the mouse, for example points, lines, circles, etc.
 * @param <T> The kind of thing that is "selected" by clicking on it
 */
public abstract class ElementSelector<T> implements Drawer<T> {
    private T selected;
    private boolean selectionFinished = false;
    private MouseEventInfo eventInfo;
    private boolean hidden = false;
    private final List<Consumer<T>> finishObservers = new ArrayList<>();
    private final List<Runnable> failedFinishObservers = new ArrayList<>();
    protected CreasePattern_Worker d;

    /**
     * returns the element that would get created if the mouse was clicked now,
     * or just an element that is close to the mouse, gets called on mouseMove/Drag
     * @param mousePos Position of the mouse on screen (usually, converting using Camera#TV2object is needed)
     */
    protected abstract T determineSelected(Point mousePos, MouseEventInfo eventInfo);

    /**
     * updates the selection
     */
    public void update(Point mousePos, MouseEventInfo eventInfo) {
        if (selectionFinished) {
            return;
        }
        this.selected = determineSelected(mousePos, eventInfo);
        this.eventInfo = eventInfo;
    }

    /**
     * tests whether the element is valid
     */
    protected abstract boolean validate(T element, MouseEventInfo eventInfo);

    /**
     * finishes the selection (locking the selector until reset() is called, and calling onFinish callbacks)
     * if the current selection is valid. Otherwise, calls the onFail callbacks.
     * Returns whether finishing succeeded.
     * @return true if the finishing was successful, false otherwise
     */
    public boolean tryFinishSelection() {
        if (canFinishSelection()) {
            finishSelection();
        } else {
            failSelection();
        }
        return selectionFinished;
    }

    protected boolean canFinishSelection() {
        return eventInfo != null && selected != null && validate(selected, eventInfo);
    }

    protected final void finishSelection() {
        selectionFinished = true;
        finishObservers.forEach(observer -> observer.accept(getSelection()));
    }

    protected final void failSelection() {
        selectionFinished = false;
        failedFinishObservers.forEach(Runnable::run);
    }

    /**
     * Adds a callback for when the selection is finished. The callback will be called
     * with the selection as an argument
     */
    public void onFinish(Consumer<T> callback) {
        this.finishObservers.add(callback);
    }

    /**
     * Adds a callback for when finishing the selection fails (by trying to finish with an invalid selection)
     */
    public void onFail(Runnable callback) {
        this.failedFinishObservers.add(callback);
    }

    /**
     * @return the Element that is currently "selected". Return value does not always need to be valid
     */
    public T getSelection() {
        return this.selected;
    }

    /**
     * Resets the state of the selector
     */
    public void reset() {
        eventInfo = null;
        selected = null;
        selectionFinished = false;
        hidden = false;
    }

    /**
     * @return whether the selection is finished. If it is, calling update will have no effect
     * until reset() is called
     */
    public boolean isSelectionFinished() {
        return selectionFinished;
    }

    /**
     * draws a preview of the element onto the graphics object.
     */
    public abstract void draw(T element, Graphics2D g2, Camera camera, DrawingSettings settings);

    /**
     * draws a preview of the element that would be selected onto the graphics object if the
     * selector is not hidden.
     */
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (selected != null && !hidden) {
            draw(selected, g2, camera, settings);
        }
    }

    /**
     * whether the selector is hidden (if it is, drawing it won't do anything)
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * sets whether the Selector should be rendered or not
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    void setCreasePattern_Worker(CreasePattern_Worker d) {
        this.d = d;
    }

    /**
     * Creates a new ElementSelector that executes the operator on the selection of
     * this selector.
     * @param operator Operator to be applied on the selection of this selector
     * @param drawOriginal whether to draw the original
     */
    public ElementSelector<T> then(UnaryOperator<T> operator, boolean drawOriginal, boolean drawCalculated) {
        return new CalculatedElementSelector<>(this, drawOriginal) {

            @Override
            public void draw(T element, Graphics2D g2, Camera camera, DrawingSettings settings) {
                if (drawCalculated) {
                    ElementSelector.this.draw(element, g2, camera, settings);
                }
            }

            @Override
            protected T calculate(T baseSelected) {
                return operator.apply(baseSelected);
            }
        };
    }

    public <F> ElementSelector<F> thenGet(Function<T, F> getter) {
        return thenGet(getter, (e, g, c, s) -> {});
    }

    /**
     * Creates a new ElementSelector that executes the function on the selection of
     * this selector.
     * @param getter Function to be applied on the selection of this selector
     */
    public <F> ElementSelector<F> thenGet(Function<T, F> getter, Drawer<F> drawer) {
        return new CalculatedElementSelector<>(this, true) {
            @Override
            protected F calculate(T baseSelected) {
                return getter.apply(baseSelected);
            }

            @Override
            public void draw(F element, Graphics2D g2, Camera camera, DrawingSettings settings) {
                drawer.draw(element, g2, camera, settings);
            }
        };
    }

    public boolean hasFailedFinishCallback() {
        return !failedFinishObservers.isEmpty();
    }
}
