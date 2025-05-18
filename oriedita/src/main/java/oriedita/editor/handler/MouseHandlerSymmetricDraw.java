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

enum SymmetricDrawStep {
    SELECT_2L_OR_3P,
    SELECT_3P,
    SELECT_2L,
}

@ApplicationScoped
@Handles(MouseMode.SYMMETRIC_DRAW_10)
public class MouseHandlerSymmetricDraw extends StepMouseHandler<SymmetricDrawStep> {
    private int counter_3P = 0;
    private int counter_2L = 0;
    private List<Point> pointsList_3P = Arrays.asList(null, null, null);
    private List<LineSegment> segmentsList_2L = Arrays.asList(null, null);

    @Inject
    public MouseHandlerSymmetricDraw() {
        super(SymmetricDrawStep.SELECT_2L_OR_3P);
        steps.addNode(StepNode.createNode_MD_R(SymmetricDrawStep.SELECT_2L_OR_3P, this::move_drag_select_2L_or_3P, this::release_select_2L_or_3P));
        steps.addNode(StepNode.createNode_MD_R(SymmetricDrawStep.SELECT_3P, this::move_drag_select_3P, this::release_select_3P));
        steps.addNode(StepNode.createNode_MD_R(SymmetricDrawStep.SELECT_2L, this::move_drag_select_2L, this::release_select_2L));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(0), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(1), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, pointsList_3P.get(2), d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segmentsList_2L.get(0), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segmentsList_2L.get(1), camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        counter_3P = 0;
        counter_2L = 0;
        pointsList_3P = Arrays.asList(null, null, null);
        segmentsList_2L = Arrays.asList(null, null);
        steps.setCurrentStep(SymmetricDrawStep.SELECT_2L_OR_3P);
    }

    // Select 2 lines or 3 points
    private void move_drag_select_2L_or_3P(Point p) {
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
    }
    private SymmetricDrawStep release_select_2L_or_3P(Point p) {
        if (pointsList_3P.get(counter_3P) != null) {
            counter_3P++;
            return SymmetricDrawStep.SELECT_3P;
        }
        if (segmentsList_2L.get(counter_2L) != null) {
            counter_2L++;
            return SymmetricDrawStep.SELECT_2L;
        }
        return SymmetricDrawStep.SELECT_2L_OR_3P;
    }

    // Select 3 points
    private void move_drag_select_3P(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            pointsList_3P.set(counter_3P, d.getClosestPoint(p));
        } else pointsList_3P.set(counter_3P, null);
    }
    private SymmetricDrawStep release_select_3P(Point p) {
        if(pointsList_3P.get(counter_3P) == null) return SymmetricDrawStep.SELECT_3P;
        counter_3P++;
        if (counter_3P < 3) return SymmetricDrawStep.SELECT_3P;
        if(OritaCalc.isPointWithinLineSpan(pointsList_3P.get(0), new LineSegment(pointsList_3P.get(1), pointsList_3P.get(2)))) {
            reset();
            return SymmetricDrawStep.SELECT_2L_OR_3P;
        }
        LineSegment s1 = new LineSegment(pointsList_3P.get(0), pointsList_3P.get(1));
        LineSegment s2 = new LineSegment(pointsList_3P.get(1), pointsList_3P.get(2));
        return reflectLine(s1, s2, steps.getCurrentStep());
    }

    // Select 2 lines
    private void move_drag_select_2L(Point p) {
        if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
            if(counter_2L == 0) {
                segmentsList_2L.set(counter_2L, d.getClosestLineSegment(p).withColor(LineColor.GREEN_6));
            } else {
                if(OritaCalc.isLineSegmentParallel(segmentsList_2L.get(0), d.getClosestLineSegment(p)) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    segmentsList_2L.set(counter_2L, d.getClosestLineSegment(p).withColor(LineColor.GREEN_6));
                }
            }
        } else segmentsList_2L.set(counter_2L, null);
    }
    private SymmetricDrawStep release_select_2L(Point p) {
        if(segmentsList_2L.get(counter_2L) == null) return SymmetricDrawStep.SELECT_2L;
        counter_2L++;
        if (counter_2L < 2) return SymmetricDrawStep.SELECT_2L;
        return reflectLine(segmentsList_2L.get(0), segmentsList_2L.get(1), steps.getCurrentStep());
    }

    private SymmetricDrawStep reflectLine(LineSegment s1, LineSegment s2, SymmetricDrawStep step) {
        Point cross = OritaCalc.findIntersection(s1, s2);
        Point t_taisyou = OritaCalc.findLineSymmetryPoint(cross, s2.determineFurthestEndpoint(cross), s1.determineFurthestEndpoint(cross));
        LineSegment add_sen = new LineSegment(cross, t_taisyou);
        add_sen = d.extendToIntersectionPoint(add_sen);
        add_sen = add_sen.withColor(d.getLineColor());

        if (Epsilon.high.gt0(add_sen.determineLength())) {
            d.addLineSegment(add_sen);
            d.record();
            reset();
            return SymmetricDrawStep.SELECT_2L_OR_3P;
        }
        return step;
    }
}
