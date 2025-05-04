package oriedita.editor.handler;

import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

public abstract class StepMouseHandler <T extends Enum<T>> extends BaseMouseHandler{
    protected Point p = new Point();
    protected StepGraph<T> steps;

    public StepMouseHandler(T step) { this.steps = new StepGraph<>(step); }

    // mousePressed is never needed
    @Override
    public void mousePressed(Point p0) { steps.runCurrentPressAction(); }

    @Override
    public void mouseMoved(Point p0) {
        p = d.getCamera().TV2object(p0);
        steps.runCurrentMoveAction();
    }

    @Override
    public void mouseDragged(Point p0) {
        p = d.getCamera().TV2object(p0);
        steps.runCurrentDragAction();
    }

    @Override
    public void mouseReleased(Point p0) { steps.runCurrentReleaseAction(); }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }
}
