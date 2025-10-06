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
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

public abstract class MouseHandlerDisplayAngleBetweenThreePoints extends StepMouseHandler<MouseHandlerDisplayAngleBetweenThreePoints.Step> {
    public enum Step {
        SELECT_POINT_1,
        SELECT_POINT_2,
        SELECT_POINT_3,
    }
    private final MeasuresModel measuresModel;

    private Point p1, p2, p3;

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
                (p) -> p2 = p,
                p -> {
                    if (p != null) {
                        return Step.SELECT_POINT_3;
                    }
                    return Step.SELECT_POINT_2;
                }));
        sg.addNode(stepFactory.createSelectPointNode(Step.SELECT_POINT_3, null, false,
                (p) -> {p3 = p;
                    if (p3 == null) {return;}
                    setAngle(measuresModel, OritaCalc.angle(p2, p1, p2, p3));
            },
                p -> {
                    if (p != null) {
                        reset();
                        return Step.SELECT_POINT_1;
                    }
                    return Step.SELECT_POINT_3;
                }));
        return sg;
    }

    public abstract void setAngle(MeasuresModel measuresModel, double angle);

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, LineColor.MAGENTA_5, camera);
        DrawingUtil.drawStepVertex(g2, p2, LineColor.MAGENTA_5, camera);
        DrawingUtil.drawStepVertex(g2, p3, LineColor.MAGENTA_5, camera);
    }

    @Override
    public void reset() {
        super.reset();
        p1 = p2 = p3 = null;
    }

    @Inject
    public MouseHandlerDisplayAngleBetweenThreePoints(
            MeasuresModel measuresModel) {
        this.measuresModel = measuresModel;
    }
}

@ApplicationScoped
@Handles(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55)
class Angle1 extends MouseHandlerDisplayAngleBetweenThreePoints {
    @Inject
    public Angle1(MeasuresModel measuresModel) {
        super(measuresModel);
    }

    public void setAngle(MeasuresModel measuresModel, double angle){
        measuresModel.setMeasuredAngle1(angle);
    }
}
@ApplicationScoped
@Handles(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56)
class Angle2 extends MouseHandlerDisplayAngleBetweenThreePoints {
    @Inject
    public Angle2(MeasuresModel measuresModel) {
        super(measuresModel);
    }

    public void setAngle(MeasuresModel measuresModel, double angle){
        measuresModel.setMeasuredAngle2(angle);
    }
}
@ApplicationScoped
@Handles(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57)
class Angle3 extends MouseHandlerDisplayAngleBetweenThreePoints {
    @Inject
    public Angle3(MeasuresModel measuresModel) {
        super(measuresModel);
    }

    public void setAngle(MeasuresModel measuresModel, double angle){
        measuresModel.setMeasuredAngle3(angle);
    }
}