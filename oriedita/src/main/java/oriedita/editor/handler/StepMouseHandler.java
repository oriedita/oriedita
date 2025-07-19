package oriedita.editor.handler;

import jakarta.inject.Inject;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.tools.ResourceUtil;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

public abstract class StepMouseHandler<T extends Enum<T>> extends BaseMouseHandler {
    @Inject
    private CanvasModel canvasModel;

    protected StepGraph<T> steps;
    private final T startingStep;
    private String label;

    public StepMouseHandler(T step) {
        this.steps = new StepGraph<>(step);
        startingStep = step;
        label = getStepLabel();
    }

    private String getStepLabel() {
        T enumInst = steps.getCurrentStep();
        String enumClass = enumInst.getClass().getSimpleName();
        String key = enumInst.name();
        String value = ResourceUtil.getBundleString("step_label", enumClass + "." + key);
        return value != null ? value : key;
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
        label = getStepLabel();
        mouseMoved(canvasModel.getMouseObjPosition());
    }

    protected void resetStep() {
        steps.setCurrentStep(startingStep);
        mousePressed(canvasModel.getMouseObjPosition());
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        double textPosX = canvasModel.getMouseObjPosition().getX() + 20 / camera.getCameraZoomX();
        double textPosY = canvasModel.getMouseObjPosition().getY() + 20 / camera.getCameraZoomY();
        if (settings.getShowComments()) {
            DrawingUtil.drawText(g2, label,
                    canvasModel.getMouseObjPosition().withX(textPosX).withY(textPosY), camera);
        }
    }
}
