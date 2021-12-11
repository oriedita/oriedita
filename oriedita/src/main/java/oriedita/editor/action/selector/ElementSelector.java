package oriedita.editor.action.selector;

import oriedita.editor.action.DrawingSettings;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * to simplify the drawing and implementation of tools where specific things have to be
 * clicked on using the mouse, for example points, lines, circles, etc.
 * @param <T> The kind of thing that is "selected" by clicking on it
 */
public abstract class ElementSelector<T> {
    private T selected;
    private boolean selectionFinished = false;
    private MouseEventInfo eventInfo;
    private boolean hidden = false;
    private final List<Consumer<T>> observers = new ArrayList<>();
    protected CreasePattern_Worker d;

    /**
     * returns the element that would get created if the mouse was clicked now,
     * or just an element that is close to the mouse, gets called on mouseMove/Drag
     * @param mousePos Position of the mouse on screen (usually, converting using Camera#TV2object is needed)
     */
    protected abstract T determineSelected(Point mousePos, MouseEventInfo eventInfo);

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

    public boolean tryFinishSelection() {
        if (canFinishSelection()) {
            finishSelection();
        }
        return selectionFinished;
    }

    protected final boolean canFinishSelection() {
        return eventInfo != null && validate(selected, eventInfo);
    }

    protected final void finishSelection() {
        selectionFinished = true;
        for (Consumer<T> observer : observers) {
            observer.accept(selected);
        }
    }

    public void onFinish(Consumer<T> callback) {
        this.observers.add(callback);
    }

    /**
     * @return the Element that is currently "selected". Return value does not always need to be valid
     */
    public T getSelection() {
        return this.selected;
    }

    public void reset() {
        eventInfo = null;
        selected = null;
        selectionFinished = false;
        hidden = false;
    }

    public boolean isSelectionFinished() {
        return selectionFinished;
    }

    /**
     * draws a preview of the element that would be selected onto the graphics object.
     */
    protected abstract void draw(T element, Graphics2D g2, Camera camera, DrawingSettings settings);

    public void draw(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (selected != null && !hidden) {
            draw(selected, g2, camera, settings);
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    void setCreasePattern_Worker(CreasePattern_Worker d) {
        this.d = d;
    }
}
