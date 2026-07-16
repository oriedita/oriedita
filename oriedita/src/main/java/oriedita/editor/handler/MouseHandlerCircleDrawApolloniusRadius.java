package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

enum CircleDrawApolloniusRadiusStep {
    SELECT_1,
    SELECT_2,
    CLICK_DRAG_POINT,
    SELECT_RESULT,
}

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_APOLLONIUS_RADIUS_109)
public class MouseHandlerCircleDrawApolloniusRadius extends StepMouseHandler<CircleDrawApolloniusRadiusStep> {
    private Point p1,p2, anchorPoint, releasePoint;
    private LineSegment l1, l2, previewSegment;
    private Circle k1, k2, result;
    private List<Circle> indicators;
    private double r;
    int numC;



    @Inject
    public MouseHandlerCircleDrawApolloniusRadius() {
        super(CircleDrawApolloniusRadiusStep.SELECT_1);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawApolloniusRadiusStep.SELECT_1,
                this::move_drag_select_1,
                this::release_select_1));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawApolloniusRadiusStep.SELECT_2,
                this::move_drag_select_2,
                this::release_select_2));
        steps.addNode(ObjCoordStepNode.createNode(CircleDrawApolloniusRadiusStep.CLICK_DRAG_POINT,
                this::move_click_drag_point,
                (p) -> {
                }, this::drag_click_drag_point, this::release_click_drag_point));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawApolloniusRadiusStep.SELECT_RESULT,
                this::move_drag_select_result,
                this::release_select_result));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);

        for (Circle indicator : indicators)
            DrawingUtil.drawCircleStep(g2, indicator, camera);

        DrawingUtil.drawCircleStep(g2, k1, camera);
        DrawingUtil.drawCircleStep(g2, k2, camera);
        DrawingUtil.drawCircleStep(g2, result, camera);

        DrawingUtil.drawLineStep(g2, l1, camera, settings.getLineWidth());
        DrawingUtil.drawLineStep(g2, l2, camera, settings.getLineWidth());
        DrawingUtil.drawLineStep(g2, previewSegment, camera, settings.getLineWidth());

        DrawingUtil.drawStepVertex(g2, p1, LineColor.GREEN_6, camera);
        DrawingUtil.drawStepVertex(g2, p2, LineColor.GREEN_6, camera);
        DrawingUtil.drawStepVertex(g2, anchorPoint, LineColor.GREEN_6, camera);
        DrawingUtil.drawStepVertex(g2, releasePoint, LineColor.GREEN_6, camera);


    }

    @Override
    public void reset() {
        resetStep();
        indicators = new ArrayList<>();
        p1 = null;
        p2 = null;
        anchorPoint = null;
        releasePoint = null;

        l1 = null;
        l2 = null;
        previewSegment = null;

        k1 = null;
        k2 = null;
        result = null;
        r = 0;
        numC = 0;
    }

    // Select first element
    private void move_drag_select_1(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            k1 = new Circle(tmpCircle);
            k1.setColor(LineColor.GREEN_6);
        } else
            k1 = null;

        LineSegment tmpSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, tmpSegment) < d.getSelectionDistance()) {
            l1 = new LineSegment(tmpSegment, LineColor.GREEN_6);
            k1 = null;
        } else
            l1 = null;

        // The solver uses 0 radius circles as points, p1-3 are only used for display
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            p1 = d.getClosestPoint(p);
            k1 = new Circle(p1.getX(), p1.getY(), 0, LineColor.CYAN_3);
            l1 = null;
        } else
            p1 = null;
    }

    private CircleDrawApolloniusRadiusStep release_select_1(Point p) {
        if (p1 == null && k1 == null && l1 == null)
            return CircleDrawApolloniusRadiusStep.SELECT_1;
        return CircleDrawApolloniusRadiusStep.SELECT_2;
    }

    // Select second element
    private void move_drag_select_2(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            k2 = new Circle(tmpCircle);
            k2.setColor(LineColor.GREEN_6);
        } else
            k2 = null;

        LineSegment tmpSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, tmpSegment) < d.getSelectionDistance()) {
            l2 = new LineSegment(tmpSegment, LineColor.GREEN_6);
            k2 = null;
        } else
            l2 = null;

        // The solver uses 0 radius circles as points, p1-3 are only used for display
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            p2 = d.getClosestPoint(p);
            k2 = new Circle(p2.getX(), p2.getY(), 0, LineColor.CYAN_3);
            l2 = null;
        } else
            p2 = null;
    }

    private CircleDrawApolloniusRadiusStep release_select_2(Point p) {
        if (p2 == null && k2 == null && l2 == null)
            return CircleDrawApolloniusRadiusStep.SELECT_2;

        // Do this here because it would be repeated continuously after this point.
        numC = ((k1!=null) ? 1:0) + ((k2!=null) ? 1:0);
        // Reshuffle to make sure they are populated correctly.
        if(l1==null && l2!=null) l1 = l2;
        if(k1==null && k2!=null) k1 = k2;

        return CircleDrawApolloniusRadiusStep.CLICK_DRAG_POINT;
    }

    // Click drag point to draw the offset line
    private void move_click_drag_point(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            anchorPoint = new Point(tmpPoint);
        } else
            anchorPoint = null;
    }

    private void drag_click_drag_point(Point p) {
        if (anchorPoint == null)
            return;

        Point tmpPoint = d.getClosestPoint(p);
        releasePoint = p;
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            releasePoint = new Point(tmpPoint);
        }

        if (releasePoint == null)
            return;

        previewSegment = new LineSegment(anchorPoint, releasePoint, LineColor.GREEN_6);
        r = anchorPoint.distance(releasePoint);

        indicators.clear();
        process_apollonius();
    }

    private CircleDrawApolloniusRadiusStep release_click_drag_point(Point p) {
        if (anchorPoint == null) {
            return CircleDrawApolloniusRadiusStep.CLICK_DRAG_POINT;

        }
        if (releasePoint == null || releasePoint.distance(d.getClosestPoint(p)) > d.getSelectionDistance()) {
            indicators.clear();
            anchorPoint = null;
            releasePoint = null;
            previewSegment = null;
            return CircleDrawApolloniusRadiusStep.CLICK_DRAG_POINT;
        }

        if(indicators.size() > 1)
            return CircleDrawApolloniusRadiusStep.SELECT_RESULT;
        else if(indicators.size() == 1) {
            indicators.get(0).setColor(LineColor.CYAN_3);
            d.addCircle(indicators.get(0));
            d.record();
            reset();
            return CircleDrawApolloniusRadiusStep.SELECT_1;
        }
        else {
            reset();
            return CircleDrawApolloniusRadiusStep.SELECT_1;
        }
    }

    // Select indicator
    private void move_drag_select_result(Point p) {
        double minDist = 100000.0;
        for (Circle indicator : indicators) {
            double dist = OritaCalc.distance_circumference(p, indicator);
            if (dist < minDist) {
                minDist = dist;
                if (dist < d.getSelectionDistance()) {
                    result = new Circle(indicator);
                    result.setColor(LineColor.ORANGE_4);
                } else
                    result = null;
            }
        }
    }

    private CircleDrawApolloniusRadiusStep release_select_result(Point p) {
        if (result == null)
            return CircleDrawApolloniusRadiusStep.SELECT_RESULT;

        result.setColor(LineColor.CYAN_3);
        d.addCircle(result);
        d.record();
        reset();
        return CircleDrawApolloniusRadiusStep.SELECT_1;
    }

    private void process_apollonius() {
        if(numC==2)  process_apollonius_CC();
        if(numC==1)  process_apollonius_CL();
        if(numC==0)  process_apollonius_LL();
    }

    // Increase & decrease radius of circles by radius. Find intersections.
    // Those are the centers of candidate circles.
    private void process_apollonius_CC() {
        final double EPS = 1e-6;
        final double MIN_R = 1e-3;
        final double MAX_R = 1e6;

        double[] offset_radii_1 = {k1.getR() + r, Math.abs(k1.getR() - r)};
        double[] offset_radii_2 = {k2.getR() + r, Math.abs(k2.getR() - r)};

        for (double offset_r_1 : offset_radii_1) {
            for (double offset_r_2 : offset_radii_2) {
                List<Point> centers = new ArrayList<>();

                Circle offset_c_1 = new Circle(k1.determineCenter(), offset_r_1, LineColor.PURPLE_8);
                Circle offset_c_2 = new Circle(k2.determineCenter(), offset_r_2, LineColor.PURPLE_8);

                // The OritaCalc function doesn't handle tangency well, so it's excluded
                double dist_circles = offset_c_1.determineCenter().distance(offset_c_2.determineCenter());
                double smaller_r = Math.min(offset_r_1, offset_r_2);
                double bigger_r  = Math.max(offset_r_1, offset_r_2);
                if(dist_circles < bigger_r)
                    smaller_r *= -1;

                // Tangency check. The smaller radius needs to be subtracted if the smaller
                // circle is inside and tangent to the bigger one.
                if(Math.abs(dist_circles - (bigger_r + smaller_r)) > EPS) {
                    LineSegment ls = OritaCalc.circle_to_circle_no_intersection_wo_musubu_lineSegment(offset_c_1, offset_c_2);

                    // Record if real solution
                    if (!(Double.isNaN(ls.determineAX()) || Double.isNaN(ls.determineAX()) ||
                          Double.isNaN(ls.determineBX()) || Double.isNaN(ls.determineBY()))) {
                        centers.add(ls.getA());
                        centers.add(ls.getB());
                    }
                }

                for (Point center : centers) {
                    if (r < MIN_R || r > MAX_R)
                        continue;
                    Circle c = new Circle(center, r, LineColor.PURPLE_8);
                    if (is_valid_circle(c, indicators, EPS)) {
                        indicators.add(c);
                    }
                }
            }
        }
    }

    // Offset line by radius, increase & decrease circle by radius. Find intersections.
    // Those are the centers of candidate circles.
    private void process_apollonius_CL() {
        final double EPS = 1e-6;
        final double MIN_R = 1e-3;
        final double MAX_R = 1e6;

        LineSegment[] offsets_lines =  new LineSegment[2];
        offsets_lines[0] = OritaCalc.moveParallel(l1, r);
        offsets_lines[1] = OritaCalc.moveParallel(l1, -r);

        double[] offset_radii = {k1.getR() + r, Math.abs(k1.getR() - r)};

        for (LineSegment offsets_line : offsets_lines) {
            for (double offset_radius : offset_radii) {
                List<Point> centers = new ArrayList<>();

                Circle offset_circle = new Circle(k1.determineCenter(), offset_radius , LineColor.PURPLE_8);

                LineSegment ls = OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(
                        offset_circle, OritaCalc.lineSegmentToStraightLine(offsets_line));

                if(!(Double.isNaN(ls.determineAX()) || Double.isNaN(ls.determineAX()) ||
                     Double.isNaN(ls.determineBX()) || Double.isNaN(ls.determineBY()))) {
                    centers.add(ls.getA());
                    centers.add(ls.getB());
                }

                for (Point center : centers) {
                    if (r < MIN_R || r > MAX_R)
                        continue;
                    Circle c = new Circle(center, r, LineColor.PURPLE_8);
                    if (is_valid_circle(c, indicators, EPS*10)) {
                        indicators.add(c);
                    }
                }
            }
        }
    }

    // Offset lines by radius. Find intersections.
    // Those are the centers of candidate circles.
    private void process_apollonius_LL() {
        final double EPS = 1e-6;
        final double MIN_R = 1e-3;
        final double MAX_R = 1e6;

        // Skip if parallel
        if(OritaCalc.isLineSegmentParallel(l1, l2) != OritaCalc.ParallelJudgement.NOT_PARALLEL)
            return;

        LineSegment[] offset_lines_1 =  new LineSegment[2];
        offset_lines_1[0] = OritaCalc.moveParallel(l1, r);
        offset_lines_1[1] = OritaCalc.moveParallel(l1, -r);

        LineSegment[] offset_lines_2 =  new LineSegment[2];
        offset_lines_2[0] = OritaCalc.moveParallel(l2, r);
        offset_lines_2[1] = OritaCalc.moveParallel(l2, -r);

        for (LineSegment offset_line_1 : offset_lines_1) {
            for (LineSegment offset_line_2 : offset_lines_2) {
                Point center = OritaCalc.findIntersection(offset_line_1, offset_line_2);

                if (r < MIN_R || r > MAX_R)
                    continue;

                Circle c = new Circle(center, r, LineColor.PURPLE_8);
                if (is_valid_circle(c, indicators, EPS*10)) {
                    indicators.add(c);
                }
            }
        }
    }

    // True for valid circle
    private boolean is_valid_circle(Circle c, List<Circle> list, double eps) {
        for (Circle other : list) {
            if(!is_valid_circle_helper(c, other, eps))
                return false;
        }
        if(k1!= null)
            if(!is_valid_circle_helper(c, k1, eps))
                return false;
        if(k2!= null)
            if(!is_valid_circle_helper(c, k2, eps))
                return false;

        return true;
    }

    // True for valid circle
    private boolean is_valid_circle_helper(Circle c, Circle other, double eps) {
        double dr = c.getR() - other.getR();
        return !((c.determineCenter().distance(other.determineCenter()) < eps) && (Math.abs(dr) < eps));
    }
}
