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

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.SYMMETRIC_DRAW_10)
public class MouseHandlerSymmetricDraw extends BaseMouseHandlerInputRestricted {
    private Point p = new Point();
    private StepCollection<Step> steps = new StepCollection<>(Step.SELECT_2L_OR_3P, this::action_select_2L_or_3P);

    private int counter_3P = 0;
    private int counter_2L = 0;
    private List<Point> pointsList_3P = Arrays.asList(null, null, null);
    private List<LineSegment> segmentsList_2L = Arrays.asList(null, null);

    private enum Step {
        SELECT_2L_OR_3P,
        SELECT_3P,
        SELECT_2L,
    }

    @Inject
    public MouseHandlerSymmetricDraw() { initializeSteps(); }

    public void mousePressed(Point p0) {}

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) { steps.runCurrentAction(); }

    private void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);

        switch (steps.getCurrentStep()) {
            case SELECT_2L_OR_3P: {
                double pointDistance = p.distance(d.getClosestPoint(p));
                double segmentDistance = OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p));
                boolean isPointInRadius = pointDistance < d.getSelectionDistance();
                boolean isSegmentInRadius = segmentDistance < d.getSelectionDistance();

                if (isPointInRadius) {
                    pointsList_3P.set(counter_3P, d.getClosestPoint(p));
                } else pointsList_3P.set(counter_3P, null);

                if (!isPointInRadius && isSegmentInRadius) {
                    segmentsList_2L.set(counter_2L, d.getClosestLineSegment(p).withColor(LineColor.GREEN_6));
                } else segmentsList_2L.set(counter_2L, null);
                return;
            }
            case SELECT_3P: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    pointsList_3P.set(counter_3P, d.getClosestPoint(p));
                } else pointsList_3P.set(counter_3P, null);
                return;
            }
            case SELECT_2L: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    segmentsList_2L.set(counter_2L, d.getClosestLineSegment(p).withColor(LineColor.GREEN_6));
                } else segmentsList_2L.set(counter_2L, null);
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(0), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(1), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(2), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segmentsList_2L.get(0), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segmentsList_2L.get(1), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        counter_3P = 0;
        counter_2L = 0;
        pointsList_3P = Arrays.asList(null, null, null);
        segmentsList_2L = Arrays.asList(null, null);
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepCollection<>(Step.SELECT_2L_OR_3P, this::action_select_2L_or_3P);
        steps.addNode(Step.SELECT_3P, this::action_select_3P);
        steps.addNode(Step.SELECT_2L, this::action_select_2L);
    }

    private void action_select_2L_or_3P() {
        if (pointsList_3P.get(counter_3P) != null) {
            counter_3P++;
            steps.setCurrentStep(Step.SELECT_3P);
        }
        if (segmentsList_2L.get(counter_2L) != null) {
            counter_2L++;
            steps.setCurrentStep(Step.SELECT_2L);
        }
    }

    private void action_select_3P() {
        if(pointsList_3P.get(counter_3P) == null) return;
        counter_3P++;
        if (counter_3P < 3) return;
        LineSegment s1 = new LineSegment(pointsList_3P.get(0), pointsList_3P.get(1));
        LineSegment s2 = new LineSegment(pointsList_3P.get(1), pointsList_3P.get(2));
        reflectLine(s1, s2);
    }

    private void action_select_2L() {
        if(segmentsList_2L.get(counter_2L) == null) return;
        counter_2L++;
        if (counter_2L < 2) return;
        reflectLine(segmentsList_2L.get(0), segmentsList_2L.get(1));
    }

    private void reflectLine(LineSegment s1, LineSegment s2) {
        Point cross = OritaCalc.findIntersection(s1, s2);
        Point t_taisyou = OritaCalc.findLineSymmetryPoint(cross, s2.determineFurthestEndpoint(cross), s1.determineFurthestEndpoint(cross));
        LineSegment add_sen = new LineSegment(cross, t_taisyou);
        add_sen = d.extendToIntersectionPoint(add_sen);
        add_sen = add_sen.withColor(d.getLineColor());

        if (Epsilon.high.gt0(add_sen.determineLength())) {
            d.addLineSegment(add_sen);
            d.record();
            reset();
        }
    }
}
