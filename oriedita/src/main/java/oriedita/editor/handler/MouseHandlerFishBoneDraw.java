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
@Handles(MouseMode.FISH_BONE_DRAW_33)
public class MouseHandlerFishBoneDraw extends BaseMouseHandlerInputRestricted {
    private Point p = new Point();
    private StepCollection<Step> steps;

    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment dragSegment;

    private enum Step {
        CLICK_DRAG_POINT,
        RELEASE_POINT,
    }

    @Inject
    public MouseHandlerFishBoneDraw() { initializeSteps(); }

    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    public void mouseReleased(Point p0) {
        if (steps.getCurrentStep() == Step.CLICK_DRAG_POINT) return;
        steps.runCurrentAction();
    }

    private void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);
        switch (steps.getCurrentStep()) {
            case CLICK_DRAG_POINT: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    anchorPoint = d.getClosestPoint(p);
                } else anchorPoint = null;
                return;
            }
            case RELEASE_POINT: {
                releasePoint = p;
                dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepCollection<>(Step.CLICK_DRAG_POINT, this::action_click_drag_point);
        steps.addNode(Step.RELEASE_POINT, this::action_release_point);
    }

    private void action_click_drag_point() {
        if (anchorPoint == null) return;
        steps.setCurrentStep(Step.RELEASE_POINT);
    }

    private void action_release_point() {
        Point closestPoint = d.getClosestPoint(releasePoint);
        dragSegment = new LineSegment(anchorPoint, closestPoint);

        if (releasePoint.distance(closestPoint) > d.getSelectionDistance()) {
            reset();
            return;
        }
        if (!Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
            return;
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
