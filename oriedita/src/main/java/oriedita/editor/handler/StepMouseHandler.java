package oriedita.editor.handler;

import jakarta.inject.Inject;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

public abstract class StepMouseHandler <T extends Enum<T>> extends BaseMouseHandler{
    @Inject
    private CanvasModel canvasModel;
    protected StepGraph<T> steps;

    public StepMouseHandler(T step) {
        this.steps = new StepGraph<>(step);
    }

    // mousePressed is never needed
    @Override
    public void mousePressed(Point p0) {
        steps.runCurrentPressAction(canvasModel.getMouseObjPosition());
    }

    @Override
    public void mouseMoved(Point p0) {
        steps.runCurrentMoveAction(canvasModel.getMouseObjPosition());
    }

    @Override
    public void mouseDragged(Point p0) {
        steps.runCurrentDragAction(canvasModel.getMouseObjPosition());
    }

    @Override
    public void mouseReleased(Point p0) {
        steps.runCurrentReleaseAction(canvasModel.getMouseObjPosition());
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        double textPosX = canvasModel.getMouseObjPosition().getX() + 20 / camera.getCameraZoomX();
        double textPosY = canvasModel.getMouseObjPosition().getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), canvasModel.getMouseObjPosition().withX(textPosX).withY(textPosY), camera);
    }
}
