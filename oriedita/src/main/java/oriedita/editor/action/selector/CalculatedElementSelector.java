package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;

/**
 * An element selector that calculates an element based on the selection from another selector.
 * @param <T> Type of the base selector
 * @param <C> Type of the calculated element
 */
public abstract class CalculatedElementSelector<T, C> extends ElementSelector<C> {
    private final boolean drawBase;
    private final ElementSelector<T> base;

    public CalculatedElementSelector(ElementSelector<T> base, boolean drawBase) {
        this.base = base;
        this.drawBase = drawBase;
    }

    @Override
    protected C determineSelected(Point mousePos, MouseEventInfo eventInfo) {
        T baseSelected = base.determineSelected(mousePos, eventInfo);
        if (baseSelected != null) {
            return calculate(baseSelected);
        }
        return null;
    }

    /**
     * Calculate the element
     * @param baseSelected selection of the base selector
     */
    protected abstract C calculate(T baseSelected);

    @Override
    protected boolean canFinishSelection() {
        return super.canFinishSelection() && base.canFinishSelection();
    }

    @Override
    public boolean tryFinishSelection() {
        if (canFinishSelection()) {
            base.finishSelection();
            super.finishSelection();
            return true;

        }
        base.failSelection();
        super.failSelection();
        return false;
    }

    @Override
    public void update(Point mousePos, MouseEventInfo eventInfo) {
        base.update(mousePos, eventInfo);
        super.update(mousePos, eventInfo);
    }

    @Override
    public void reset() {
        base.reset();
        super.reset();
    }

    @Override
    protected final boolean validate(C element, MouseEventInfo eventInfo) {
        return validate(base.getSelection(), element, eventInfo); // is only called if base is valid
    }

    protected boolean validate(T base, C calculated, MouseEventInfo eventInfo) {
        return true;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (drawBase) {
            base.drawPreview(g2, camera, settings);
        }
        super.drawPreview(g2, camera, settings);
    }

    @Override
    public boolean isSelectionFinished() {
        return base.isSelectionFinished();
    }

    @Override
    public boolean isHidden() {
        return base.isHidden();
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        base.setHidden(hidden);
    }

    @Override
    void setCreasePattern_Worker(CreasePattern_Worker d) {
        super.setCreasePattern_Worker(d);
        base.setCreasePattern_Worker(d);
    }
}
