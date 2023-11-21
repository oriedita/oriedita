package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

@ApplicationScoped
@Handles(MouseMode.AXIOM_7)
public class MouseHandlerAxiom7 extends BaseMouseHandlerInputRestricted{
    private Point midPoint = new Point();
    @Inject
    public MouseHandlerAxiom7(){}

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

        // 1. target point
        if(d.getLineStep().isEmpty()){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // 2. target segment
        // Don't allow segment that spans through the target point
        if(d.getLineStep().size() == 1){
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            LineSegment temp = new LineSegment(d.getLineStep().get(0).getA(), closestLineSegment.determineClosestEndpoint(d.getLineStep().get(0).getA()));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance() &&
                    OritaCalc.isLineSegmentParallel(temp, closestLineSegment) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        // 3. reference segment
        if(d.getLineStep().size() == 2){
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            // Don't allow segment that is parallel to target segment
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance() &&
                    OritaCalc.isLineSegmentParallel(closestLineSegment, d.getLineStep().get(1)) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        // index 3 and 4 are the purple indicators
        // 4. destination line (case 2)
        // Don't accept segment that is parallel to the purple indicators
        if(d.getLineStep().size() == 5){
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            if (!(OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) ||
                    OritaCalc.isLineSegmentParallel(closestLineSegment, d.getLineStep().get(3)) != OritaCalc.ParallelJudgement.NOT_PARALLEL) {
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

        // First 3 are clicked
        if(d.getLineStep().size() == 3){
            midPoint = drawAxiom7FoldIndicators(d.getLineStep().get(0), d.getLineStep().get(1), d.getLineStep().get(2));
        }

        // Case 1: Click on the purple indicators auto expand the purple indicators to the nearest lines
        // (Kinda works)
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

        // Case 2: Click on destination line to extend result line from midpoint
        if(d.getLineStep().size() == 6){
            LineSegment midTemp = new LineSegment(midPoint, midPoint);
            midTemp.setB(new Point(midTemp.determineAX() + d.getLineStep().get(3).determineBX() - d.getLineStep().get(3).determineAX(), midTemp.determineAY() + d.getLineStep().get(3).determineBY() - d.getLineStep().get(3).determineAY()));
            LineSegment result = getExtendedSegment(midTemp, d.getLineStep().get(5), d.getLineColor());
            d.addLineSegment(result);
            d.record();
            d.getLineStep().clear();
        }
    }

    public Point drawAxiom7FoldIndicators(LineSegment target, LineSegment targetSegment, LineSegment refSegment){
        LineSegment temp = new LineSegment(target);
        temp.setB(new Point(temp.determineAX() + refSegment.determineBX() - refSegment.determineAX(), temp.determineAY() + refSegment.determineBY() - refSegment.determineAY()));
        LineSegment extendLine = getExtendedSegment(temp, targetSegment, LineColor.PURPLE_8);

        if (extendLine != null) {
            Point mid = OritaCalc.midPoint(target.getA(), OritaCalc.findIntersection(extendLine, targetSegment));
            d.lineStepAdd(new LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, 25), mid), LineColor.PURPLE_8));
            d.lineStepAdd(new LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, -25), mid), LineColor.PURPLE_8));
            return mid;
        }
        return null;
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

    public LineSegment getExtendedSegment(LineSegment s_o, LineSegment s_k, LineColor icolo) {
        Point cross_point = new Point();

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return null;
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point.set(s_k.getA());
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                cross_point.set(s_k.getB());
            }
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point.set(OritaCalc.findIntersection(s_o, s_k));
        }

        LineSegment add_sen = new LineSegment(cross_point, s_o.getA(), icolo);

        if (Epsilon.high.gt0(add_sen.determineLength())) {
            return add_sen;
        }

        return null;
    }
}
