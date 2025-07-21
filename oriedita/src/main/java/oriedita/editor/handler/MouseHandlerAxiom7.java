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

enum Axiom7Step {
    SELECT_TARGET_POINT,
    SELECT_TARGET_SEGMENT,
    SELECT_PERPENDICULAR_SEGMENT,
    SELECT_DESTINATION_OR_INDICATOR,
}

@ApplicationScoped
@Handles(MouseMode.AXIOM_7)
public class MouseHandlerAxiom7 extends StepMouseHandler<Axiom7Step> {
    private Point targetPoint;
    private LineSegment targetSegment, perpendicularSegment, indicator, destinationSegment;

    @Inject
    public MouseHandlerAxiom7() {
        super(Axiom7Step.SELECT_TARGET_POINT);
        steps.addNode(StepNode.createNode_MD_R(Axiom7Step.SELECT_TARGET_POINT, this::move_drag_select_target_point,
                this::release_select_target_point));
        steps.addNode(StepNode.createNode_MD_R(Axiom7Step.SELECT_TARGET_SEGMENT, this::move_drag_select_target_segment,
                this::release_select_target_segment));
        steps.addNode(StepNode.createNode_MD_R(Axiom7Step.SELECT_PERPENDICULAR_SEGMENT,
                this::move_drag_select_perpendicular_segment, this::release_select_perpendicular_segment));
        steps.addNode(StepNode.createNode_MD_R(Axiom7Step.SELECT_DESTINATION_OR_INDICATOR,
                this::move_drag_select_destination_or_indicator, this::release_select_destination_or_indicator));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, targetSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, perpendicularSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicator, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        targetPoint = null;
        targetSegment = null;
        perpendicularSegment = null;
        indicator = null;
        destinationSegment = null;
    }

    // Select target point
    private void move_drag_select_target_point(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            targetPoint = d.getClosestPoint(p);
        } else
            targetPoint = null;
    }

    private Axiom7Step release_select_target_point(Point p) {
        if (targetPoint == null)
            return Axiom7Step.SELECT_TARGET_POINT;
        return Axiom7Step.SELECT_TARGET_SEGMENT;
    }

    // Select target segment
    private void move_drag_select_target_segment(Point p) {
        if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance() &&
                !OritaCalc.isPointWithinLineSpan(targetPoint, d.getClosestLineSegment(p))) {
            targetSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
        } else
            targetSegment = null;
    }

    private Axiom7Step release_select_target_segment(Point p) {
        if (targetSegment == null)
            return Axiom7Step.SELECT_TARGET_SEGMENT;
        return Axiom7Step.SELECT_PERPENDICULAR_SEGMENT;
    }

    // Select perpendicular segment
    private void move_drag_select_perpendicular_segment(Point p) {
        if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance() &&
                OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p),
                        targetSegment) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
            perpendicularSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
        } else
            perpendicularSegment = null;
    }

    private Axiom7Step release_select_perpendicular_segment(Point p) {
        if (perpendicularSegment == null)
            return Axiom7Step.SELECT_PERPENDICULAR_SEGMENT;
        drawAxiom7FoldIndicators();
        return Axiom7Step.SELECT_DESTINATION_OR_INDICATOR;
    }

    // Select destination or indicator
    private void move_drag_select_destination_or_indicator(Point p) {
        double indicatorDistance = OritaCalc.determineLineSegmentDistance(p, indicator);
        double normalDistance = OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p));
        if (indicatorDistance < normalDistance
                && indicatorDistance < d.getSelectionDistance()) {
            destinationSegment = indicator.withColor(LineColor.ORANGE_4);
        } else if (normalDistance < indicatorDistance
                && normalDistance < d.getSelectionDistance()
                && OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p),
                        indicator) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
            destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
        } else
            destinationSegment = null;
    }

    private Axiom7Step release_select_destination_or_indicator(Point p) {
        if (OritaCalc.determineLineSegmentDistance(p, indicator) < d.getSelectionDistance()) {
            d.addLineSegment(indicator.withColor(d.getLineColor()));
            d.record();
            reset();
            return Axiom7Step.SELECT_TARGET_POINT;
        }

        if (destinationSegment == null)
            return Axiom7Step.SELECT_DESTINATION_OR_INDICATOR;
        LineSegment result = getExtendedSegment(indicator, destinationSegment, d.getLineColor());
        d.addLineSegment(result);
        d.record();
        reset();
        return Axiom7Step.SELECT_TARGET_POINT;
    }

    public void drawAxiom7FoldIndicators() {
        LineSegment temp = new LineSegment(targetPoint, new Point(
                targetPoint.getX() + perpendicularSegment.determineBX() - perpendicularSegment.determineAX(),
                targetPoint.getY() + perpendicularSegment.determineBY() - perpendicularSegment.determineAY()));
        LineSegment extendLine = getExtendedSegment(temp, targetSegment, LineColor.PURPLE_8);
        if (extendLine == null)
            return;

        Point mid = OritaCalc.midPoint(targetPoint, OritaCalc.findIntersection(extendLine, targetSegment));
        indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(mid,
                OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, 1), mid), LineColor.PURPLE_8));
        indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(),
                indicator.withCoordinates(indicator.getB(), indicator.getA()));
    }

    public LineSegment getExtendedSegment(LineSegment s_o, LineSegment s_k, LineColor icolo) {
        if (OritaCalc.isLineSegmentParallel(s_o, s_k,
                Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {// 0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return null;
        }

        Point cross_point = new Point();
        if (OritaCalc.isLineSegmentParallel(s_o, s_k,
                Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {// 0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = s_k.getA();
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                cross_point = s_k.getB();
            }
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k,
                Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {// 0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = OritaCalc.findIntersection(s_o, s_k);
        }

        LineSegment add_sen = new LineSegment(cross_point, s_o.getA(), icolo);
        if (Epsilon.high.gt0(add_sen.determineLength())) {
            return add_sen;
        }

        return null;
    }
}
