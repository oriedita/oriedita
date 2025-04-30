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

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_THREE_POINT_43)
public class MouseHandlerCircleDrawThreePoint extends BaseMouseHandler {
    private Point p = new Point();
    private StepCollection<Step> steps;

    private Point p1;
    private Point p2;
    private Point p3;

    private enum Step {
        SELECT_POINT_1,
        SELECT_POINT_2,
        SELECT_POINT_3,
    }

    @Inject
    public MouseHandlerCircleDrawThreePoint() { initializeSteps(); }

    @Override
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    public void mouseReleased(Point p0) {}

    private void highlightSelection(Point p0) {
        p = p0 != null ? d.getCamera().TV2object(p0) : p;
        switch (steps.getCurrentStep()) {
            case SELECT_POINT_1: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    p1 = d.getClosestPoint(p);
                } else p1 = null;
                return;
            }
            case SELECT_POINT_2: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                        && !p1.equals(d.getClosestPoint(p))) {
                    p2 = d.getClosestPoint(p);
                } else p2 = null;
                return;
            }
            case SELECT_POINT_3: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                        && !p1.equals(d.getClosestPoint(p))
                        && !p2.equals(d.getClosestPoint(p))) {
                    p3 = d.getClosestPoint(p);
                } else p3 = null;
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p3, LineColor.CYAN_3, camera, d.getGridInputAssist());

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        p1 = null;
        p2 = null;
        p3 = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepCollection<>(Step.SELECT_POINT_1, this::action_select_point_1);
        steps.addNode(Step.SELECT_POINT_2, this::action_select_point_2);
        steps.addNode(Step.SELECT_POINT_3, this::action_select_point_3);

        steps.connectNodes(Step.SELECT_POINT_1, Step.SELECT_POINT_2);
        steps.connectNodes(Step.SELECT_POINT_2, Step.SELECT_POINT_3);
    }

    private void action_select_point_1() {
        if (p1 == null) return;
        steps.setCurrentStep(Step.SELECT_POINT_2);
    }

    private void action_select_point_2() {
        if (p2 == null) return;
        steps.setCurrentStep(Step.SELECT_POINT_3);
    }

    private void action_select_point_3() {
        if (p3 == null) return;

        LineSegment sen1 = new LineSegment(p1, p2);
        LineSegment sen2 = new LineSegment(p2, p3);
        LineSegment sen3 = new LineSegment(p3, p1);

        if (checkIfFlatAngle(sen1, sen2)
                || checkIfFlatAngle(sen2, sen3)
                || checkIfFlatAngle(sen3, sen1)) {
            reset();
            return;
        }

        StraightLine t1 = new StraightLine(sen1)
                .orthogonalize(OritaCalc.internalDivisionRatio(
                        sen1.getA(), sen1.getB(),
                        1.0, 1.0));
        StraightLine t2 = new StraightLine(sen2)
                .orthogonalize(OritaCalc.internalDivisionRatio(
                        sen2.getA(), sen2.getB(),
                        1.0, 1.0));
        d.addCircle(OritaCalc.findIntersection(t1, t2), OritaCalc.distance(p1, OritaCalc.findIntersection(t1, t2)), LineColor.CYAN_3);
        d.record();
        reset();
    }

    private boolean checkIfFlatAngle(LineSegment s1, LineSegment s2) {
        if (Math.abs(OritaCalc.angle(s1, s2) - 0.0) < Epsilon.UNKNOWN_1EN6) return true;
        if (Math.abs(OritaCalc.angle(s1, s2) - 180.0) < Epsilon.UNKNOWN_1EN6) return true;
        if (Math.abs(OritaCalc.angle(s1, s2) - 360.0) < Epsilon.UNKNOWN_1EN6) return true;
        return false;
    }
}
