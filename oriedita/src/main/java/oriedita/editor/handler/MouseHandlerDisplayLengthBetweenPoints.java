package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

public  abstract class MouseHandlerDisplayLengthBetweenPoints extends StepMouseHandler<MouseHandlerDisplayLengthBetweenPoints.Step> {
    public enum Step {
        SELECT_POINT_1,
        SELECT_POINT_2,
    }
    private Point p1, p2;
    private final MeasuresModel measuresModel;

    @Inject
    public MouseHandlerDisplayLengthBetweenPoints(MeasuresModel measuresModel) {
        this.measuresModel = measuresModel;
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var sg = new StepGraph<>(Step.SELECT_POINT_1);
        sg.addNode(stepFactory.createSelectPointNode(Step.SELECT_POINT_1, null, false,
                (p) -> p1 = p,
                p -> {
                    if (p != null) {
                        return Step.SELECT_POINT_2;
                    }
                    return Step.SELECT_POINT_1;
                }));
        sg.addNode(stepFactory.createSelectPointNode(Step.SELECT_POINT_2, null, false,
                (p) -> {p2 = p;
                    if (p2 == null) {return;}
                    setLength(measuresModel, p1.distance(p2));
                },
                p -> {
                    if (p != null) {
                        reset();
                        return Step.SELECT_POINT_1;
                    }
                    return Step.SELECT_POINT_2;
                }));
        return sg;
    }

    @Override
    public void reset() {
        super.reset();
        p1 = p2 = null;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, LineColor.MAGENTA_5, camera, settings.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, LineColor.MAGENTA_5, camera, settings.getGridInputAssist());
    }

    protected abstract void setLength(MeasuresModel measuresModel, double length);
}


@ApplicationScoped
@Handles(MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53)
class Length1 extends MouseHandlerDisplayLengthBetweenPoints {

    @Inject
    public Length1(MeasuresModel measuresModel) {
        super(measuresModel);
    }

    @Override
    protected void setLength(MeasuresModel measuresModel, double length) {
        measuresModel.setMeasuredLength1(length);
    }
}

@ApplicationScoped
@Handles(MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54)
class Length2 extends MouseHandlerDisplayLengthBetweenPoints {

    @Inject
    public Length2(MeasuresModel measuresModel) {
        super(measuresModel);
    }

    @Override
    protected void setLength(MeasuresModel measuresModel, double length) {
        measuresModel.setMeasuredLength2(length);
    }
}