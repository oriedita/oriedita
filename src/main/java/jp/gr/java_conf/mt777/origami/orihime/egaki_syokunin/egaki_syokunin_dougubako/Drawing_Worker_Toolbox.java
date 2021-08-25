package jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin.egaki_syokunin_dougubako;

import jp.gr.java_conf.mt777.origami.dougu.orisensyuugou.*;

import jp.gr.java_conf.mt777.graphic2d.point.*;
import jp.gr.java_conf.mt777.graphic2d.linesegment.*;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.*;
import jp.gr.java_conf.mt777.graphic2d.oritacalc.straightline.*;

public class Drawing_Worker_Toolbox {
    FoldLineSet ori_s;

    public Drawing_Worker_Toolbox(FoldLineSet o_s) {  //コンストラクタ
        ori_s = o_s;
    }

    // Extend the vector ab (= s0) from point a to b until it first intersects another polygonal line
    LineSegment lengthenUntilIntersectionLineSegment = new LineSegment();
    Point lengthenUntilIntersectionPoint = new Point();
    StraightLine.Intersection lengthenUntilIntersection_flg = StraightLine.Intersection.NONE_0;//The situation of the first intersection where ab was extended
    int lengthenUntilIntersectionFoldLineIncluded_flg = 0;//If ab is straightened, including existing polygonal lines, 3
    LineSegment lengthenUntilIntersectionFirstLineSegment = new LineSegment();//Straightening ab and the existing polygonal line that hits first

    //Extend the vector ab (= s0) from point a to b, until it first intersects another fold line (ignoring the line segment contained in the straight line) // If it does not intersect another fold line, set Point a return
    public void lengthenUntilIntersectionDisregardIncludedLineSegment(Point a, Point b) {
        LineSegment s0 = new LineSegment();
        s0.set(a, b);
        LineSegment addLine = new LineSegment();
        addLine.set(s0);
        Point cross_point = new Point(1000000.0, 1000000.0); //This method can cause errors. If it is true, you should take points of x_max and y_max or more for all lines. Will be fixed in the future 20161120
        double crossPointDistance = cross_point.distance(addLine.getA());
        StraightLine straightLine = new StraightLine(addLine.getA(), addLine.getB());
        StraightLine.Intersection i_cross_flg;

        lengthenUntilIntersection_flg = StraightLine.Intersection.NONE_0;
        lengthenUntilIntersectionFoldLineIncluded_flg = 0;
        for (int i = 1; i <= ori_s.getTotal(); i++) {
            i_cross_flg = straightLine.lineSegment_intersect_reverse_detail(ori_s.get(i));//0 = This straight line does not intersect a given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
            if ((i_cross_flg == StraightLine.Intersection.INTERSECT_X_1 || i_cross_flg == StraightLine.Intersection.INTERSECT_T_A_21) || i_cross_flg == StraightLine.Intersection.INTERSECT_T_B_22) {

                cross_point.set(OritaCalc.findIntersection(straightLine, ori_s.get(i)));//A function that considers a line segment as a straight line and finds the intersection with another straight line. Even if it does not intersect as a line segment, it returns the intersection when it intersects as a straight line

                if (cross_point.distance(addLine.getA()) > 0.00001) {

                    if (cross_point.distance(addLine.getA()) < crossPointDistance) {
                        double d_kakudo = OritaCalc.angle(addLine.getA(), addLine.getB(), addLine.getA(), cross_point);

                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {

                            crossPointDistance = cross_point.distance(addLine.getA());
                            addLine.set(addLine.getA(), cross_point);

                            lengthenUntilIntersection_flg = i_cross_flg;
                            lengthenUntilIntersectionFirstLineSegment.set(ori_s.get(i));
                        }
                    }
                }
            }
        }

        lengthenUntilIntersectionLineSegment.set(addLine);
        lengthenUntilIntersectionPoint.set(addLine.getB());
    }


    public void lengthenUntilIntersectionCalculateDisregardIncludedLineSegment_new(Point a, Point b) {//Extend the vector ab (= s0) from point a to b, until it first intersects another fold line (ignoring the line segment contained in the straight line) // If it does not intersect another fold line, Ten a return
        LineSegment s0 = new LineSegment();
        s0.set(a, b);
        LineSegment addLine = new LineSegment();
        addLine.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_point_distance = kousa_point.distance(addLine.getA());
        StraightLine straightLine = new StraightLine(addLine.getA(), addLine.getB());
        StraightLine.Intersection i_kousa_flg;

        lengthenUntilIntersection_flg = StraightLine.Intersection.NONE_0;
        lengthenUntilIntersectionFoldLineIncluded_flg = 0;
        for (int i = 1; i <= ori_s.getTotal(); i++) {
            if (ori_s.get(i).getColor().isFoldingLine()) {
// 0 = This straight line does not intersect the given line segment,
// 1 = X type intersects,
// 21 = T-shaped intersection at point a of the line segment,
// 22 = T-shaped intersection at point b of the line segment,
// 3 = Line segments are included in the straight line.
                i_kousa_flg = straightLine.lineSegment_intersect_reverse_detail(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                //if(i_kousa_flg==3){lengthenUntilIntersectionFoldLineIncluded_flg=3;}
                if ((i_kousa_flg == StraightLine.Intersection.INTERSECT_X_1 || i_kousa_flg == StraightLine.Intersection.INTERSECT_T_A_21) || i_kousa_flg == StraightLine.Intersection.INTERSECT_T_B_22) {

                    kousa_point.set(OritaCalc.findIntersection(straightLine, ori_s.get(i)));//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す

                    if (kousa_point.distance(addLine.getA()) > 0.00001) {

                        if (kousa_point.distance(addLine.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(addLine.getA(), addLine.getB(), addLine.getA(), kousa_point);

                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                kousa_point_distance = kousa_point.distance(addLine.getA());
                                addLine.set(addLine.getA(), kousa_point);

                                lengthenUntilIntersection_flg = i_kousa_flg;
                                lengthenUntilIntersectionFirstLineSegment.set(ori_s.get(i));
                            }
                        }
                    }
                }
            }
        }

        lengthenUntilIntersectionLineSegment.set(addLine);
        lengthenUntilIntersectionPoint.set(addLine.getB());
    }

    public StraightLine.Intersection getLengthenUntilIntersectionFlg_new(Point a, Point b) {// 0 = This straight line does not intersect the given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
        return lengthenUntilIntersection_flg;
    }

    public StraightLine.Intersection getLengthenUntilIntersectionFlg(Point a, Point b) {//0 = This straight line does not intersect a given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
        lengthenUntilIntersectionDisregardIncludedLineSegment(a, b);
        return lengthenUntilIntersection_flg;
    }

    public LineSegment getLengthenUntilIntersectionLineSegment(Point a, Point b) {
        lengthenUntilIntersectionDisregardIncludedLineSegment(a, b);
        return lengthenUntilIntersectionLineSegment;
    }

    public LineSegment getLengthenUntilIntersectionLineSegment_new() {
        return lengthenUntilIntersectionLineSegment;
    }

    public LineSegment getLengthenUntilIntersectionFirstLineSegment(Point a, Point b) {
        lengthenUntilIntersectionDisregardIncludedLineSegment(a, b);
        return lengthenUntilIntersectionFirstLineSegment;
    }

    public LineSegment getLengthenUntilIntersectionFirstLineSegment_new() {
        return lengthenUntilIntersectionFirstLineSegment;
    }

    public Point getLengthenUntilIntersectionPoint(Point a, Point b) {
        lengthenUntilIntersectionDisregardIncludedLineSegment(a, b);
        return lengthenUntilIntersectionPoint;
    }

    public Point getLengthenUntilIntersectionPoint_new() {
        return lengthenUntilIntersectionPoint;
    }
}
