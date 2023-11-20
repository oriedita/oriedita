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

        // First point
        if(d.getLineStep().isEmpty()){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // First line
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

        // Second point
        // Won't accept any point that are at nowhere point-clickable, or inside / an extension of clicked line,
        if(d.getLineStep().size() == 2){
            Point closestPoint = d.getClosestPoint(p);
            LineSegment temp = new LineSegment(closestPoint, d.getLineStep().get(1).determineClosestEndpoint(closestPoint));
            if (p.distance(closestPoint) < d.getSelectionDistance() &&
                    OritaCalc.determineLineSegmentDistance(closestPoint, d.getLineStep().get(1)) > Epsilon.UNKNOWN_1EN4 &&
                    OritaCalc.isLineSegmentParallel(new StraightLine(temp), new StraightLine(d.getLineStep().get(1))) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // index 3 and 4 are the purple indicators
        // Destination line + mouse point
        // -> index 5 and 6
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
            drawCircleAndLineSegmentBisectors(cir, d.getLineStep().get(1));
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

                LineSegment s2 = new LineSegment(s1.getB(), OritaCalc.point_rotate(s1.getB(), s1.getA(), 180), d.getLineColor());
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
    public void drawCircleAndLineSegmentBisectors(Circle cir, LineSegment s) {
        if(cir.getR() > Epsilon.UNKNOWN_1EN7){
            double length_a = OritaCalc.determineLineSegmentDistance(cir.determineCenter(), s); //Distance between the center of a straight line and a circle

            // intersect at one point or not at all
            if(Math.abs(length_a - cir.getR()) < Epsilon.UNKNOWN_1EN6 || length_a > cir.getR()){ d.getLineStep().clear(); }
            else {  // intersect at two points
                LineSegment temp = new LineSegment(d.getLineStep().get(0).getA(), d.getLineStep().get(1).determineClosestEndpoint(d.getLineStep().get(0).getA()));
                LineSegment l = new LineSegment(d.getLineStep().get(0).getA(), d.getLineStep().get(2).getA());
                Point center = cir.determineCenter();

                Point projectPoint = new Point(OritaCalc.findProjection(d.getLineStep().get(1), center));
                LineSegment projectLine = new LineSegment(center, projectPoint);

                double length_b = Math.sqrt((cir.getR() * cir.getR()) - (length_a * length_a));


                LineSegment l1 = new LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, length_b), projectPoint));
//                    d.lineStepAdd(new LineSegment(center, l1.getB(), LineColor.ORANGE_4));
                l1.set(new LineSegment(center, l1.getB()));

                LineSegment l2 = new LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, -length_b), projectPoint));
//                    d.lineStepAdd(new LineSegment(center, l2.getB(), LineColor.ORANGE_4));
                l2.set(new LineSegment(center, l2.getB()));

                Point center1 = new Point(OritaCalc.center(center, l1.determineFurthestEndpoint(center), l.determineFurthestEndpoint(center)));
                Point center2 = new Point(OritaCalc.center(center, l2.determineFurthestEndpoint(center), l.determineFurthestEndpoint(center)));

                // if first point is not contained in/extension of first line
                if(OritaCalc.isLineSegmentParallel(new StraightLine(temp), new StraightLine(d.getLineStep().get(1))) == OritaCalc.ParallelJudgement.NOT_PARALLEL){
                    d.lineStepAdd(new LineSegment(center, center1, LineColor.PURPLE_8));
                    d.lineStepAdd(new LineSegment(center, center2, LineColor.PURPLE_8));
                }
                // if first point is contained in/extension of first line
                if(OritaCalc.isLineSegmentParallel(new StraightLine(l1), new StraightLine(l)) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                    d.lineStepAdd(new LineSegment(center, center2, LineColor.PURPLE_8));
                    d.lineStepAdd(new LineSegment(center, center2, LineColor.PURPLE_8));
                }
                if(OritaCalc.isLineSegmentParallel(new StraightLine(l2), new StraightLine(l)) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL){
                    d.lineStepAdd(new LineSegment(center, center1, LineColor.PURPLE_8));
                    d.lineStepAdd(new LineSegment(center, center1, LineColor.PURPLE_8));
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