package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;

public abstract class ConditionalSelector<T,U> extends ElementSelector<Object>{
    private final ElementSelector<T> firstSelector;
    private final ElementSelector<U> secondSelector;
    private TypeChoice activeSelector;

    public ConditionalSelector(ElementSelector<T> firstSelector,
                               ElementSelector<U> secondSelector
    ) {
        this.firstSelector = firstSelector;
        this.secondSelector = secondSelector;
    }

    @Override
    protected Object determineSelected(Point mousePos, MouseEventInfo eventInfo) {
        T first = firstSelector.determineSelected(mousePos, eventInfo);
        U second = secondSelector.determineSelected(mousePos, eventInfo);
        if (first == null) {
            if (second != null) {
                activeSelector = TypeChoice.SECOND;
            } else {
                activeSelector = null;
            }
            return second;
        } else if (second == null) {
            activeSelector = TypeChoice.FIRST;
            return first;
        }
        TypeChoice c = choose(first, second, mousePos, eventInfo);
        if (c == TypeChoice.FIRST) {
            activeSelector = TypeChoice.FIRST;
            return first;
        } else if (c == TypeChoice.SECOND) {
            activeSelector = TypeChoice.SECOND;
            return second;
        }
        activeSelector = null;
        return null;
    }

    @Override
    protected boolean validate(Object element, MouseEventInfo eventInfo) {
        if (activeSelector == TypeChoice.FIRST) {
            return firstSelector.validate((T) element, eventInfo);
        } else if (activeSelector == TypeChoice.SECOND) {
            return secondSelector.validate((U) element, eventInfo);
        }
        return false;
    }

    @Override
    public void draw(Object element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (activeSelector == TypeChoice.FIRST) {
            firstSelector.draw((T) element, g2, camera, settings);
        } else if (activeSelector == TypeChoice.SECOND) {
            secondSelector.draw((U) element, g2, camera, settings);
        }
    }

    @Override
    public void reset() {
        super.reset();
        firstSelector.reset();
        secondSelector.reset();
        activeSelector = null;
    }

    @Override
    public void update(Point mousePos, MouseEventInfo eventInfo) {
        firstSelector.update(mousePos, eventInfo);
        secondSelector.update(mousePos, eventInfo);
        super.update(mousePos, eventInfo);
    }

    public enum TypeChoice {
        FIRST, SECOND
    }

    @Override
    void setCreasePattern_Worker(CreasePattern_Worker d) {
        super.setCreasePattern_Worker(d);
        firstSelector.setCreasePattern_Worker(d);
        secondSelector.setCreasePattern_Worker(d);
    }

    protected abstract TypeChoice choose(T first, U second, Point mousePos, MouseEventInfo eventInfo);
}
