package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum CircleDrawConcentricTwoCircleSelectStep {
    SELECT_CIRCLE_1,
    SELECT_CIRCLE_2
}

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50)
public class MouseHandlerCircleDrawConcentricTwoCircleSelect extends StepMouseHandler<CircleDrawConcentricTwoCircleSelectStep> {

    private Circle circle1, circle2;

    @Inject
    public MouseHandlerCircleDrawConcentricTwoCircleSelect() {
        super(CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1);
        steps.addNode(StepNode.createNode_MD_R(CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1, this::move_drag_select_circle_1, this::release_select_circle_1));
        steps.addNode(StepNode.createNode_MD_R(CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2, this::move_drag_select_circle_2, this::release_select_circle_2));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawCircleStep(g2, circle1, camera);
        DrawingUtil.drawCircleStep(g2, circle2, camera);
    }

    @Override
    public void reset() {
        circle1 = null;
        circle2 = null;
        steps.setCurrentStep(CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1);
    }

    // Select circle 1
    private void move_drag_select_circle_1(Point p) {
        if (OritaCalc.distance_circumference(p, d.getClosestCircleMidpoint(p)) < d.getSelectionDistance()) {
            circle1 = new Circle(d.getClosestCircleMidpoint(p));
            circle1.setColor(LineColor.GREEN_6);
        } else circle1 = null;
    }
    private CircleDrawConcentricTwoCircleSelectStep release_select_circle_1(Point p) {
        if (circle1 == null) return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1;
        return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2;
    }

    // Select circle 2
    private void move_drag_select_circle_2(Point p) {
        if (OritaCalc.distance_circumference(p, d.getClosestCircleMidpoint(p)) < d.getSelectionDistance()) {
            if (Math.abs(circle1.getR() - d.getClosestCircleMidpoint(p).getR()) < Epsilon.UNKNOWN_1EN6
                    && circle1.determineCenter().equals(d.getClosestCircleMidpoint(p).determineCenter())) {
                circle2 = null;
            } else {
                circle2 = new Circle(d.getClosestCircleMidpoint(p));
                circle2.setColor(LineColor.ORANGE_4);
            }
        } else circle2 = null;
    }
    private CircleDrawConcentricTwoCircleSelectStep release_select_circle_2(Point p) {
        if (circle2 == null) return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2;

        double centerLineLength = OritaCalc.distance(circle1.determineCenter(), circle2.determineCenter());
        double concentricOffset = (centerLineLength - circle1.getR() - circle2.getR()) / 2.0;
        d.addCircle(new Circle(circle1.determineCenter(), circle1.getR() + concentricOffset, LineColor.CYAN_3));
        d.addCircle(new Circle(circle2.determineCenter(), circle2.getR() + concentricOffset, LineColor.CYAN_3));
        d.record();
        reset();
        return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1;
    }
}
