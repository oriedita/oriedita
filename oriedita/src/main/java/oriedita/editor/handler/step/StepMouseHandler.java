package oriedita.editor.handler.step;

import jakarta.inject.Inject;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.BaseMouseHandler;
import oriedita.editor.handler.DrawingSettings;
import oriedita.editor.tools.ResourceUtil;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public abstract class StepMouseHandler<T extends Enum<T>> extends BaseMouseHandler {
    @Inject
    private CanvasModel canvasModel;
    @Inject
    private StepFactory stepFactory;

    protected StepGraph<T> steps;
    private T startingStep;
    private String label;

    public StepMouseHandler() {
    }

    // TODO: remove all usages of this and move step graph initialization into initStepGraph
    public StepMouseHandler(T startingStep) {
        this.startingStep = startingStep;
        steps = new StepGraph<>(startingStep);
    }

    @Override
    public void init() {
        super.init();
        if (steps != null) {
            for (IStepNode<T> node : steps.getNodes()) {
                if (node instanceof ICameraStepNode cameraStepNode) {
                    cameraStepNode.setCamera(d.getCamera());
                }
            }
        }
        this.steps = initStepGraph(stepFactory);
        startingStep = steps.getCurrentStep();
        label = getStepLabel();
    }

    // TODO: make abstract after moving step initialization in all subclasses
    protected StepGraph<T> initStepGraph(StepFactory stepFactory) {
        return this.steps;
    }

    private String getStepLabel() {
        T enumInst = steps.getCurrentStep();
        String enumClass = enumInst.getClass().getSimpleName();
        String key = enumInst.name();
        String value = ResourceUtil.getBundleString("step_label", enumClass + "." + key);
        return value != null ? value : key;
    }

    @Override
    public void mousePressed(Point p0) {
    }

    @Override
    public void mousePressed(Point p0, MouseEvent e, int pressedButton) {
        var feature = Feature.getFeatures(e, pressedButton).stream().findFirst().orElseThrow();
        steps.runCurrentPressAction(canvasModel.getMousePosition(), feature);
        label = getStepLabel();
    }

    @Override
    public void mouseMoved(Point p0) {
        steps.runCurrentMoveAction(canvasModel.getMousePosition());
    }

    @Override
    public void mouseDragged(Point p0) {
        steps.runCurrentDragAction(canvasModel.getMousePosition());
    }

    @Override
    public void mouseReleased(Point p0) {
        steps.runCurrentReleaseAction(canvasModel.getMousePosition());
        label = getStepLabel();
        mouseMoved(canvasModel.getMousePosition());
    }

    protected void resetStep() {
        steps.setCurrentStep(startingStep);
        mousePressed(canvasModel.getMousePosition());
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        for (IStepNode<T> node : steps.getNodes()) {
            if (node instanceof IPreviewStepNode) {
                ((IPreviewStepNode) node).drawPreview(g2, camera, settings);
            }
        }
        double textPosX = canvasModel.getMousePosition().getX() + 20;
        double textPosY = canvasModel.getMousePosition().getY() + 20;
        if (settings.getShowCurrentStep()) {
            DrawingUtil.drawText(g2, label,
                    canvasModel.getMousePosition().withX(textPosX).withY(textPosY), Camera.defaultCamera);
        }
    }
}
