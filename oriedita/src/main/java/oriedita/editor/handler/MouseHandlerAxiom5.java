package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

@ApplicationScoped
@Handles(MouseMode.AXIOM_5)
public class MouseHandlerAxiom5 extends BaseMouseHandlerInputRestricted{
    @Inject
    public MouseHandlerAxiom5() {
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getLineStep().isEmpty()) {
            super.mouseMoved(p0);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        // First point (target point)
        if(d.getLineStep().isEmpty()){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // First line (target segment)
        // Won't accept any line that contains the first point, either containing it or meeting it by extension
        if(d.getLineStep().size() == 1){
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        // Second point (pivot point)
        if(d.getLineStep().size() == 2){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // index 3 and 4 are the purple indicators
        // Destination line
        if(d.getLineStep().size() == 5){
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            if (!(OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance())) {
                return;
            }

            closestLineSegment.setColor(LineColor.GREEN_6);
            d.lineStepAdd(closestLineSegment);
        }
    }

    @Override
    public void mouseDragged(Point p0) {}

    @Override
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        // first 3 are clicked
        if(d.getLineStep().size() == 3){
            Circle cir = new Circle(d.getLineStep().get(2).getA(), OritaCalc.distance(d.getLineStep().get(0).getA(), d.getLineStep().get(2).getA()), LineColor.PURPLE_8);
            drawCircleAndLineSegmentBisectors(cir, d.getLineStep().get(0).getA(), d.getLineStep().get(1), d.getLineStep().get(2).getA());
        }

        // Case 1: Click on the purple indicators auto expand the bisector from the purple indicators to the nearest lines
        // (Works but might come out a bit weirdly in some cases)
        if(d.getLineStep().size() == 5 && d.getClosestPoint(p).distance(p) > d.getSelectionDistance()){
            if (OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(3)) < d.getSelectionDistance() ||
                    OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(4)) < d.getSelectionDistance()) {
                double d1 = OritaCalc.distance(p, d.getLineStep().get(3).getB());
                double d2 = OritaCalc.distance(p, d.getLineStep().get(4).getB());

                LineSegment s1 = new LineSegment(d1 < d2 ? d.getLineStep().get(3).getA() : d.getLineStep().get(4).getA(),
                        d1 < d2 ? d.getLineStep().get(3).getB() : d.getLineStep().get(4).getB(),
                        d.getLineColor());
                s1.set(extendToIntersectionPoint_2(s1));
                d.addLineSegment(s1);

                LineSegment s2 = new LineSegment(s1.getB(), OritaCalc.point_rotate(s1.getA(), s1.getB(), 180), d.getLineColor());
                s2.set(extendToIntersectionPoint_2(s2));
                d.addLineSegment(s2);

                d.record();
                d.getLineStep().clear();
            }
        }

        // Case 2: Click on destination line and check of mouse point is closer to one of the 2 purple indicators
        if(d.getLineStep().size() == 6){
            Point intersectPoint1 = new Point(OritaCalc.findIntersection(d.getLineStep().get(3), d.getLineStep().get(5)));
            Point intersectPoint2 = new Point(OritaCalc.findIntersection(d.getLineStep().get(4), d.getLineStep().get(5)));

            double d1 = OritaCalc.distance(p, intersectPoint1);
            double d2 = OritaCalc.distance(p, intersectPoint2);
            d.addLineSegment(new LineSegment(d.getLineStep().get(2).getA(), d1 < d2 ? intersectPoint1 : intersectPoint2, d.getLineColor()));
            d.record();
            d.getLineStep().clear();
        }
    }
    public void drawCircleAndLineSegmentBisectors(Circle cir, Point target, LineSegment targetSegment, Point pivot) {
        // Make sure circle radius is not 0
        if(cir.getR() > Epsilon.UNKNOWN_1EN7){
            LineSegment secondTemp = new LineSegment(pivot, targetSegment.determineClosestEndpoint(pivot)); //  for checks alignment between pivot point and target segment
            double length_a = 0.0; //Distance between the center of the circle and target segment

            // If pivot point is not within the target segment span
            if(OritaCalc.isLineSegmentParallel(new StraightLine(secondTemp), new StraightLine(targetSegment)) != OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                length_a = OritaCalc.determineLineSegmentDistance(cir.determineCenter(), targetSegment);
            }

            // Intersect at one point or not at all
            if(Math.abs(length_a - cir.getR()) < Epsilon.UNKNOWN_1EN6 || length_a > cir.getR()){ d.getLineStep().clear(); }
            else {  // Intersect at two points
                LineSegment l = new LineSegment(target, pivot);
                Point center = new Point(cir.determineCenter());
                Point projectPoint = new Point(OritaCalc.findProjection(targetSegment, center));
                LineSegment projectLine = new LineSegment(center, projectPoint);

                // Length of the last segment of a right triangle (circle center, projection point, and the ultimate guiding point for indicators)
                double length_b = Math.sqrt((cir.getR() * cir.getR()) - (length_a * length_a));
                LineSegment l1 = new LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, length_b), projectPoint));
                l1.set(new LineSegment(center, l1.getB()));
                LineSegment l2 = new LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, -length_b), projectPoint));
                l2.set(new LineSegment(center, l2.getB()));

                // If pivot point is within the target segment span
                if(OritaCalc.isLineSegmentParallel(new StraightLine(secondTemp), new StraightLine(targetSegment)) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                    center.set(pivot);

                    boolean isOutsideSegmentA = targetSegment.determineLength() > OritaCalc.distance(targetSegment.getA(), center) &&
                            OritaCalc.distance(targetSegment.getB(), center) > targetSegment.determineLength();
                    boolean isOutsideSegmentB = targetSegment.determineLength() > OritaCalc.distance(targetSegment.getB(), center) &&
                            OritaCalc.distance(targetSegment.getA(), center) > targetSegment.determineLength();

                    // If pivot point is outside the target segment
                    if(isOutsideSegmentA){
                        l1.set(new LineSegment(center, OritaCalc.point_rotate(center, targetSegment.getB(), 180)));
                    } else{
                        l1.set(new LineSegment(center, targetSegment.getA()));
                    }
                    if(isOutsideSegmentB){
                        l2.set(new LineSegment(center, OritaCalc.point_rotate(center, targetSegment.getA(), 180)));
                    } else{
                        l2.set(new LineSegment(center, targetSegment.getB()));
                    }
                } else { // If pivot point is within the target segment span AND touching one of the ends of target segment
                    if(OritaCalc.distance(pivot, targetSegment.getA()) < Epsilon.UNKNOWN_1EN4){
                        l1.set(new LineSegment(center, OritaCalc.point_rotate(center, targetSegment.getB(), 180)));
                        l2.set(new LineSegment(center, targetSegment.getB()));
                    }
                    if(OritaCalc.distance(pivot, targetSegment.getB()) < Epsilon.UNKNOWN_1EN4){
                        l2.set(new LineSegment(center, OritaCalc.point_rotate(center, targetSegment.getA(), 180)));
                        l1.set(new LineSegment(center, targetSegment.getA()));
                    }
                }

                // Center points for placeholders to draw bisecting indicators on
                Point center1 = new Point(OritaCalc.center(center, l1.determineFurthestEndpoint(center), l.determineFurthestEndpoint(center)));
                Point center2 = new Point(OritaCalc.center(center, l2.determineFurthestEndpoint(center), l.determineFurthestEndpoint(center)));

                // If l and l1/l2 are aligned, get center from triangle formed by center, and furthest points of l1 and l2
                if(OritaCalc.isLineSegmentParallel(new StraightLine(l.determineFurthestEndpoint(center), center), new StraightLine(center, l1.determineFurthestEndpoint(center))) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                    center1 = OritaCalc.center(l.determineFurthestEndpoint(center), l1.determineFurthestEndpoint(center), l2.determineFurthestEndpoint(center));
                }
                if(OritaCalc.isLineSegmentParallel(new StraightLine(l.determineFurthestEndpoint(center), center), new StraightLine(center, l2.determineFurthestEndpoint(center))) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                    center2 = OritaCalc.center(l.determineFurthestEndpoint(center), l1.determineFurthestEndpoint(center), l2.determineFurthestEndpoint(center));
                }

                // If l1 and l2 are not aligned
                if(OritaCalc.isLineSegmentParallel(new StraightLine(l1), new StraightLine(l2)) == OritaCalc.ParallelJudgement.NOT_PARALLEL){
                    LineSegment temp = new LineSegment(target, targetSegment.determineClosestEndpoint(target)); // for check alignment between target point and target segment

                    // If target point is not within target segment span
                    if(OritaCalc.isLineSegmentParallel(new StraightLine(temp), new StraightLine(targetSegment)) == OritaCalc.ParallelJudgement.NOT_PARALLEL){
                        d.lineStepAdd(new LineSegment(center, center1, LineColor.PURPLE_8));
                        d.lineStepAdd(new LineSegment(center, center2, LineColor.PURPLE_8));
                        return;
                    }
                    // If target point is within target segment span
                    if(OritaCalc.isLineSegmentParallel(new StraightLine(l1), new StraightLine(l)) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                        d.lineStepAdd(new LineSegment(center, center2, LineColor.PURPLE_8));
                        d.lineStepAdd(new LineSegment(center, center2, LineColor.PURPLE_8));
                        return;
                    }
                    if(OritaCalc.isLineSegmentParallel(new StraightLine(l2), new StraightLine(l)) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                        d.lineStepAdd(new LineSegment(center, center1, LineColor.PURPLE_8));
                        d.lineStepAdd(new LineSegment(center, center1, LineColor.PURPLE_8));
                    }
                } else{ // If l1 and l2 are aligned, it means that both the target and pivot points must be within target segment span
                    d.lineStepAdd(new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l1, 25.0), pivot), LineColor.PURPLE_8));
                    d.lineStepAdd(new LineSegment(pivot, OritaCalc.findProjection(OritaCalc.moveParallel(l2, -25.0), pivot), LineColor.PURPLE_8));
                }
            }
        }
    }

    public LineSegment extendToIntersectionPoint_2(LineSegment s0) {//Extend s0 from point b in the opposite direction of a to the point where it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);

        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_point_distance = kousa_point.distance(add_sen.getA());

        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_intersection_flg;//元の線分を直線としたものと、他の線分の交差状態
        LineSegment.Intersection i_lineSegment_intersection_flg;//元の線分と、他の線分の交差状態

        for (int i = 1; i <= d.getFoldLineSet().getTotal(); i++) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(d.getFoldLineSet().get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
            i_lineSegment_intersection_flg = OritaCalc.determineLineSegmentIntersectionSweet(s0, d.getFoldLineSet().get(i), Epsilon.UNKNOWN_1EN5, Epsilon.UNKNOWN_1EN5);//20180408なぜかこの行の様にs0のままだと、i_senbun_kousa_flgがおかしくならない。

            if (i_intersection_flg.isIntersecting() && !i_lineSegment_intersection_flg.isEndpointIntersection()) {
                kousa_point.set(OritaCalc.findIntersection(tyoku1, d.getFoldLineSet().get(i)));
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_point_distance = kousa_point.distance(add_sen.getA());
                            add_sen.set(add_sen.getA(), kousa_point);
                        }
                    }
                }

            }

            if (i_intersection_flg == StraightLine.Intersection.INCLUDED_3 && i_lineSegment_intersection_flg != LineSegment.Intersection.PARALLEL_EQUAL_31) {
                kousa_point.set(d.getFoldLineSet().get(i).getA());
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_point_distance = kousa_point.distance(add_sen.getA());
                            add_sen.set(add_sen.getA(), kousa_point);
                        }
                    }
                }

                kousa_point.set(d.getFoldLineSet().get(i).getB());
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_point_distance = kousa_point.distance(add_sen.getA());
                            add_sen.set(add_sen.getA(), kousa_point);
                        }
                    }
                }
            }
        }

        add_sen.set(s0.getB(), add_sen.getB());
        return add_sen;
    }
}
