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

enum FishBoneDrawStep { CLICK_DRAG_POINT }

@ApplicationScoped
@Handles(MouseMode.FISH_BONE_DRAW_33)
public class MouseHandlerFishBoneDraw extends StepMouseHandler<FishBoneDrawStep> {
    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment dragSegment;

    @Inject
    public MouseHandlerFishBoneDraw() {
        super(FishBoneDrawStep.CLICK_DRAG_POINT);
        steps.addNode(StepNode.createNode(FishBoneDrawStep.CLICK_DRAG_POINT, this::move_click_drag_point, () -> {}, this::drag_click_drag_point, this::release_click_drag_point));
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
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        steps.setCurrentStep(FishBoneDrawStep.CLICK_DRAG_POINT);
    }

    // Click-drag a line
    private void move_click_drag_point() {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        } else anchorPoint = null;
    }
    private void drag_click_drag_point() {
        if(anchorPoint == null) return;
        releasePoint = p;
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
    }
    private FishBoneDrawStep release_click_drag_point() {
        if (releasePoint.distance(d.getClosestPoint(releasePoint)) > d.getSelectionDistance()) {
            reset();
            return FishBoneDrawStep.CLICK_DRAG_POINT;
        }
        if (dragSegment == null || !Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
            return FishBoneDrawStep.CLICK_DRAG_POINT;
        }

        double dx = (dragSegment.determineAX() - dragSegment.determineBX()) * d.getGrid().getGridWidth() / dragSegment.determineLength();
        double dy = (dragSegment.determineAY() - dragSegment.determineBY()) * d.getGrid().getGridWidth() / dragSegment.determineLength();
        LineColor icol_temp = d.getLineColor();

        for (int i = 0; i <= (int) Math.floor(dragSegment.determineLength() / d.getGrid().getGridWidth()); i++) {
            double px = dragSegment.determineBX() + (double) i * dx;
            double py = dragSegment.determineBY() + (double) i * dy;
            Point pxy = new Point(px, py);

            if (d.getFoldLineSet().closestLineSegmentDistanceExcludingParallel(pxy, dragSegment) <= Epsilon.UNKNOWN_0001) {
                continue;
            }

            int i_sen = 0;
            LineSegment adds = new LineSegment(px, py, px - dy, py + dx);

            if (kouten_ari_nasi(adds) == 1) {
                adds = d.extendToIntersectionPoint(adds);
                d.addLineSegment(adds.withColor(icol_temp));
                i_sen++;
            }

            LineSegment adds2 = new LineSegment(px, py, px + dy, py - dx);
            if (kouten_ari_nasi(adds2) == 1) {
                adds2 = d.extendToIntersectionPoint(adds2);
                d.addLineSegment(adds2.withColor(icol_temp));
                i_sen++;
            }

            if (i_sen == 2) d.getFoldLineSet().del_V(pxy, d.getSelectionDistance(), Epsilon.UNKNOWN_1EN6);

            if (icol_temp == LineColor.RED_1) icol_temp = LineColor.BLUE_2;
            else if (icol_temp == LineColor.BLUE_2) icol_temp = LineColor.RED_1;
        }

        d.record();
        reset();
        return FishBoneDrawStep.CLICK_DRAG_POINT;
    }

    public int kouten_ari_nasi(LineSegment s0) {//If s0 is extended from the point a to the b direction and intersects with another polygonal line, 0 is returned if it is not 1. The intersecting line segments at the a store have no intersection with this function.
        LineSegment add_line = new LineSegment(s0);
        StraightLine tyoku1 = new StraightLine(add_line.getA(), add_line.getB());
        StraightLine.Intersection i_intersection_flg;
        for (var ls : d.getFoldLineSet().getLineSegmentsIterable()) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(ls);//0 = This straight line does not intersect a given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
            if (!i_intersection_flg.isIntersecting()) continue;

            Point intersection_point = OritaCalc.findIntersection(tyoku1, ls);
            if (intersection_point.distance(add_line.getA()) <= Epsilon.UNKNOWN_1EN5) continue;

            double d_kakudo = OritaCalc.angle(add_line.getA(), add_line.getB(), add_line.getA(), intersection_point);
            if (d_kakudo < 1.0 || d_kakudo > 359.0) return 1;
        }

        return 0;
    }
}
