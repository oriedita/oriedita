package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.Arrays;

enum DoubleSymmetricDrawStep {
    CLICK_DRAG_POINT
}

@ApplicationScoped
@Handles(MouseMode.DOUBLE_SYMMETRIC_DRAW_35)
public class MouseHandlerDoubleSymmetricDraw extends StepMouseHandler<DoubleSymmetricDrawStep> {
    private Point anchorPoint, releasePoint;
    private LineSegment dragSegment;

    private final LineSegment.Intersection[] validIntersections = new LineSegment.Intersection[] {
            LineSegment.Intersection.INTERSECTS_LSHAPE_S1_START_S2_START_21,
            LineSegment.Intersection.INTERSECTS_LSHAPE_S1_START_S2_END_22,
            LineSegment.Intersection.INTERSECTS_LSHAPE_S1_END_S2_START_23,
            LineSegment.Intersection.INTERSECTs_LSHAPE_S1_END_S2_END_24,
            LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25,
            LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26,
    };

    @Inject
    public MouseHandlerDoubleSymmetricDraw() {
        super(DoubleSymmetricDrawStep.CLICK_DRAG_POINT);
        steps.addNode(
                StepNode.createNode(DoubleSymmetricDrawStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {
                }, this::drag_click_drag_point, this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        } else
            anchorPoint = null;
    }

    private void drag_click_drag_point(Point p) {
        if (anchorPoint == null)
            return;
        releasePoint = p;
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
    }

    private DoubleSymmetricDrawStep release_click_drag_point(Point p) {
        if (anchorPoint == null)
            return DoubleSymmetricDrawStep.CLICK_DRAG_POINT;
        if (p.distance(d.getClosestPoint(p)) > d.getSelectionDistance())
            return DoubleSymmetricDrawStep.CLICK_DRAG_POINT;
        dragSegment = new LineSegment(anchorPoint, releasePoint);

        if (releasePoint.distance(p) > d.getSelectionDistance()) {
            reset();
            return DoubleSymmetricDrawStep.CLICK_DRAG_POINT;
        }

        if (!Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
            return DoubleSymmetricDrawStep.CLICK_DRAG_POINT;
        }

        boolean isChanged = false;
        for (var s : d.getFoldLineSet().getLineSegmentsCollection()) {
            LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersectionSweet(s, dragSegment,
                    Epsilon.UNKNOWN_001, Epsilon.UNKNOWN_001);

            if (Arrays.stream(validIntersections).anyMatch((value) -> value == intersection)) {
                Point t_moto = s.getA();
                if (OritaCalc.determineLineSegmentDistance(t_moto, dragSegment) < OritaCalc
                        .determineLineSegmentDistance(s.getB(), dragSegment)) {
                    t_moto = s.getB();
                }

                // ２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten
                // t1,Ten t2,Ten p){
                Point t_taisyou = OritaCalc.findLineSymmetryPoint(dragSegment.getA(), dragSegment.getB(), t_moto);
                LineSegment add_sen = new LineSegment(OritaCalc.findIntersection(s, dragSegment), t_taisyou);
                add_sen = d.extendToIntersectionPoint(add_sen).withColor(s.getColor());

                if (Epsilon.high.gt0(add_sen.determineLength())) {
                    isChanged = true;
                    d.addLineSegment(add_sen);
                }
            }
        }

        if (isChanged)
            d.record();
        reset();
        return DoubleSymmetricDrawStep.CLICK_DRAG_POINT;
    }
}
