package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.service.ResetService;
import origami.crease_pattern.element.Rectangle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;


@ApplicationScoped
@Handles(MouseMode.BACKGROUND_CHANGE_POSITION_26)
public class MouseHandlerBackgroundChangePosition extends StepMouseHandler<MouseHandlerBackgroundChangePosition.Step> {
    private Point point1;
    private Point point2;
    private Point point3;
    private Point point4;

    public enum Step {
        SELECT_POINT_1,
        SELECT_POINT_2,
        SELECT_POINT_3,
        SELECT_POINT_4,
    }

    private final BackgroundModel backgroundModel;

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var graph = new StepGraph<>(Step.SELECT_POINT_1);
        graph.addNode(stepFactory.createSelectPointNode(
                Step.SELECT_POINT_1, LineColor.ORANGE_4,
                true, false, p -> this.point1 = p,
                p -> Step.SELECT_POINT_2
        ));
        graph.addNode(stepFactory.createSelectPointNode(
                Step.SELECT_POINT_2, LineColor.CYAN_3,
                true, false, p -> this.point2 = p,
                p -> Step.SELECT_POINT_3
        ));
        graph.addNode(stepFactory.createSelectPointNode(
                Step.SELECT_POINT_3, LineColor.BLUE_2,
                true, true, p -> this.point3 = p,
                p -> Step.SELECT_POINT_4
        ));
        graph.addNode(stepFactory.createSelectPointNode(
                Step.SELECT_POINT_4, LineColor.RED_1,
                true, true, p -> this.point4 = p,
                p -> {moveBackground(); return Step.SELECT_POINT_1;}
        ));
        return graph;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, point1, LineColor.ORANGE_4, camera);
        DrawingUtil.drawStepVertex(g2, point2, LineColor.CYAN_3, camera);
        DrawingUtil.drawStepVertex(g2, point3, LineColor.BLUE_2, camera);
        DrawingUtil.drawStepVertex(g2, point4, LineColor.RED_1, camera);
    }

    @Inject
    public MouseHandlerBackgroundChangePosition(BackgroundModel backgroundModel) {
        this.backgroundModel = backgroundModel;
    }

    private void moveBackground() {
        var oldLock = backgroundModel.isLockBackground();
        backgroundModel.setLockBackground(false);
        backgroundModel.setBackgroundPosition(new Rectangle(
                d.getCamera().object2TV(point1),
                d.getCamera().object2TV(point2),
                d.getCamera().object2TV(point3),
                d.getCamera().object2TV(point4)));
        backgroundModel.setLockBackground(oldLock);
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        point1 = null;
        point2 = null;
        point3 = null;
        point4 = null;
    }
}
