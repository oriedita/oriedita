package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;

enum PerpendicularDrawStep {
    SELECT_TARGET_POINT,
    SELECT_PERPENDICULAR_SEGMENT,
    SELECT_DESTINATION_OR_INDICATOR,
}

@ApplicationScoped
@Handles(MouseMode.PERPENDICULAR_DRAW_9)
public class MouseHandlerPerpendicularDraw extends StepMouseHandler<PerpendicularDrawStep> {
    private Point targetPoint;
    private LineSegment perpendicularSegment;
    private LineSegment indicator;
    private LineSegment destinationSegment;

    @Inject
    private CanvasModel canvasModel;

    @Inject
    public MouseHandlerPerpendicularDraw() {
        super(PerpendicularDrawStep.SELECT_TARGET_POINT);
        steps.addNode(StepNode.createNode_MD_R(PerpendicularDrawStep.SELECT_TARGET_POINT, this::move_drag_select_target_point, this::release_select_target_point));
        steps.addNode(StepNode.createNode_MD_R(PerpendicularDrawStep.SELECT_PERPENDICULAR_SEGMENT, this::move_drag_select_perpendicular_segment, this::release_select_perpendicular_segment));
        steps.addNode(StepNode.createNode_MD_R(PerpendicularDrawStep.SELECT_DESTINATION_OR_INDICATOR, this::move_drag_select_destination_or_indicator, this::release_select_destination_or_indicator));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, perpendicularSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicator, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, destinationSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        targetPoint = null;
        perpendicularSegment = null;
        indicator = null;
        destinationSegment = null;
        move_drag_select_target_point(canvasModel.getMouseObjPosition());
        steps.setCurrentStep(PerpendicularDrawStep.SELECT_TARGET_POINT);
    }

    // Select target point
    private void move_drag_select_target_point(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            targetPoint = d.getClosestPoint(p);
        } else targetPoint = null;
    }
    private PerpendicularDrawStep release_select_target_point(Point p) {
        if(targetPoint == null) return PerpendicularDrawStep.SELECT_TARGET_POINT;
        move_drag_select_perpendicular_segment(p);
        return PerpendicularDrawStep.SELECT_PERPENDICULAR_SEGMENT;
    }

    // Select perpendicular segment
    private void move_drag_select_perpendicular_segment(Point p) {
        if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
            perpendicularSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
        } else perpendicularSegment = null;
    }
    private PerpendicularDrawStep release_select_perpendicular_segment(Point p) {
        if(perpendicularSegment == null) return PerpendicularDrawStep.SELECT_PERPENDICULAR_SEGMENT;

        if (OritaCalc.isPointWithinLineSpan(targetPoint, perpendicularSegment)) {
            indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(targetPoint, OritaCalc.findProjection(OritaCalc.moveParallel(perpendicularSegment, 1.0), targetPoint), LineColor.PURPLE_8));
            indicator = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), indicator.withCoordinates(indicator.getB(), indicator.getA()));
            move_drag_select_destination_or_indicator(p);
            return PerpendicularDrawStep.SELECT_DESTINATION_OR_INDICATOR;
        }

        LineSegment nonBaseResultLine = new LineSegment(targetPoint, OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(perpendicularSegment), targetPoint), d.getLineColor());

        if (Epsilon.high.gt0(nonBaseResultLine.determineLength())) {
            d.addLineSegment(nonBaseResultLine);
            d.record();
            reset();
            return PerpendicularDrawStep.SELECT_TARGET_POINT;
        }

        move_drag_select_perpendicular_segment(p);
        return PerpendicularDrawStep.SELECT_PERPENDICULAR_SEGMENT;
    }


    // Select destination or indicator
    private void move_drag_select_destination_or_indicator(Point p) {
        double indicatorDistance = OritaCalc.determineLineSegmentDistance(p, indicator);
        double normalDistance = OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p));
        double baseDistance = OritaCalc.determineLineSegmentDistance(p, perpendicularSegment);

        double minDistance = Collections.min(Arrays.asList(indicatorDistance, normalDistance, baseDistance));

        if(Math.abs(minDistance - indicatorDistance) < Epsilon.UNKNOWN_1EN6
                && indicatorDistance < d.getSelectionDistance()) {
            destinationSegment = indicator.withColor(LineColor.ORANGE_4);
        } else if (Math.abs(minDistance - baseDistance) < Epsilon.UNKNOWN_1EN6
                && baseDistance < d.getSelectionDistance()) {
            destinationSegment = null;
        } else if (Math.abs(minDistance - normalDistance) < Epsilon.UNKNOWN_1EN6
                && normalDistance < d.getSelectionDistance()
                && OritaCalc.isLineSegmentParallel(d.getClosestLineSegment(p), indicator) == OritaCalc.ParallelJudgement.NOT_PARALLEL
                && !OritaCalc.isPointWithinLineSpan(targetPoint, d.getClosestLineSegment(p))) {
            destinationSegment = d.getClosestLineSegment(p).withColor(LineColor.ORANGE_4);
        } else destinationSegment = null;
    }
    private PerpendicularDrawStep release_select_destination_or_indicator(Point p) {
        if (OritaCalc.determineLineSegmentDistance(p, indicator) < d.getSelectionDistance()) {
            d.addLineSegment(indicator.withColor(d.getLineColor()));
            d.record();
            reset();
            return PerpendicularDrawStep.SELECT_TARGET_POINT;
        }

        if (destinationSegment == null) return PerpendicularDrawStep.SELECT_DESTINATION_OR_INDICATOR;
        LineSegment temp = new LineSegment(targetPoint,
                new Point(
                        targetPoint.getX() + indicator.determineBX() - indicator.determineAX(),
                        targetPoint.getY() + indicator.determineBY() - indicator.determineAY())
        );
        LineSegment newLine = s_step_additional_intersection(temp, destinationSegment, d.getLineColor());

        d.addLineSegment(newLine);
        d.record();
        reset();
        return PerpendicularDrawStep.SELECT_TARGET_POINT;
    }

    public LineSegment s_step_additional_intersection(LineSegment s_o, LineSegment s_k, LineColor icolo) {
        Point cross_point = new Point();

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return null;
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = s_k.getA();
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                cross_point = s_k.getB();
            }
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = OritaCalc.findIntersection(s_o, s_k);
        }

        LineSegment add_sen = new LineSegment(cross_point, s_o.getA(), icolo);
        if (Epsilon.high.gt0(add_sen.determineLength())) return add_sen;
        return null;
    }
}
