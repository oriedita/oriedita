package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

import java.awt.Graphics2D;

enum CircleDrawThreePointStep {
    SELECT_POINT_1,
    SELECT_POINT_2,
    SELECT_POINT_3,
}

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_THREE_POINT_43)
public class MouseHandlerCircleDrawThreePoint extends StepMouseHandler<CircleDrawThreePointStep> {
    private Point p1, p2, p3;

    @Inject
    public MouseHandlerCircleDrawThreePoint() {
        super(CircleDrawThreePointStep.SELECT_POINT_1);
        steps.addNode(StepNode.createNode_MD_R(CircleDrawThreePointStep.SELECT_POINT_1, this::move_drag_select_point_1, this::release_select_point_1));
        steps.addNode(StepNode.createNode_MD_R(CircleDrawThreePointStep.SELECT_POINT_2, this::move_drag_select_point_2, this::release_select_point_2));
        steps.addNode(StepNode.createNode_MD_R(CircleDrawThreePointStep.SELECT_POINT_3, this::move_drag_select_point_3, this::release_select_point_3));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p3, LineColor.CYAN_3, camera, d.getGridInputAssist());
    }

    @Override
    public void reset() {
        p1 = null;
        p2 = null;
        p3 = null;
        steps.setCurrentStep(CircleDrawThreePointStep.SELECT_POINT_1);
    }

    // Select point 1
    private void move_drag_select_point_1(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            p1 = d.getClosestPoint(p);
        } else p1 = null;
    }
    private CircleDrawThreePointStep release_select_point_1(Point p) {
        if (p1 == null) return CircleDrawThreePointStep.SELECT_POINT_1;
        return CircleDrawThreePointStep.SELECT_POINT_2;
    }

    // Select point 2
    private void move_drag_select_point_2(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                && !p1.equals(d.getClosestPoint(p))) {
            p2 = d.getClosestPoint(p);
        } else p2 = null;
    }
    private CircleDrawThreePointStep release_select_point_2(Point p) {
        if (p2 == null) return CircleDrawThreePointStep.SELECT_POINT_2;
        return CircleDrawThreePointStep.SELECT_POINT_3;
    }

    // Select point 3
    private void move_drag_select_point_3(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                && !p1.equals(d.getClosestPoint(p))
                && !p2.equals(d.getClosestPoint(p))) {
            p3 = d.getClosestPoint(p);
        } else p3 = null;
    }
    private CircleDrawThreePointStep release_select_point_3(Point p) {
        if (p3 == null) return CircleDrawThreePointStep.SELECT_POINT_3;

        LineSegment sen1 = new LineSegment(p1, p2);
        LineSegment sen2 = new LineSegment(p2, p3);
        LineSegment sen3 = new LineSegment(p3, p1);

        if (checkIfFlatAngle(sen1, sen2)
                || checkIfFlatAngle(sen2, sen3)
                || checkIfFlatAngle(sen3, sen1)) {
            reset();
            return CircleDrawThreePointStep.SELECT_POINT_1;
        }

        StraightLine t1 = new StraightLine(sen1).orthogonalize(
                OritaCalc.internalDivisionRatio(sen1.getA(), sen1.getB(), 1.0, 1.0));
        StraightLine t2 = new StraightLine(sen2).orthogonalize(
                OritaCalc.internalDivisionRatio(sen2.getA(), sen2.getB(), 1.0, 1.0));
        d.addCircle(OritaCalc.findIntersection(t1, t2), OritaCalc.distance(p1, OritaCalc.findIntersection(t1, t2)), LineColor.CYAN_3);
        d.record();
        reset();
        return CircleDrawThreePointStep.SELECT_POINT_1;
    }

    private boolean checkIfFlatAngle(LineSegment s1, LineSegment s2) {
        if (Math.abs(OritaCalc.angle(s1, s2) - 0.0) < Epsilon.UNKNOWN_1EN6) return true;
        if (Math.abs(OritaCalc.angle(s1, s2) - 180.0) < Epsilon.UNKNOWN_1EN6) return true;
        if (Math.abs(OritaCalc.angle(s1, s2) - 360.0) < Epsilon.UNKNOWN_1EN6) return true;
        return false;
    }
}
