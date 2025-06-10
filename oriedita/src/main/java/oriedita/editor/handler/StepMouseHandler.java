package oriedita.editor.handler;

import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

public abstract class StepMouseHandler <T extends Enum<T>> extends BaseMouseHandler{
    private Point mousePos = new Point();
    protected StepGraph<T> steps;

    public StepMouseHandler(T step) { this.steps = new StepGraph<>(step); }

    // mousePressed is never needed
    @Override
    public void mousePressed(Point p0) {
        mousePos = d.getCamera().TV2object(p0);
        steps.runCurrentPressAction(mousePos);
    }

    @Override
    public void mouseMoved(Point p0) {
        mousePos = d.getCamera().TV2object(p0);
        steps.runCurrentMoveAction(mousePos);
    }

    @Override
    public void mouseDragged(Point p0) {
        mousePos = d.getCamera().TV2object(p0);
        steps.runCurrentDragAction(mousePos);
    }

    @Override
    public void mouseReleased(Point p0) {
        mousePos = d.getCamera().TV2object(p0);
        steps.runCurrentReleaseAction(mousePos);
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        double textPosX = mousePos.getX() + 20 / camera.getCameraZoomX();
        double textPosY = mousePos.getY() + 20 / camera.getCameraZoomY();
        if (settings.getShowComments()) {
            DrawingUtil.drawText(g2, steps.getCurrentStep().name(), mousePos.withX(textPosX).withY(textPosY), camera);
        }
    }
}
