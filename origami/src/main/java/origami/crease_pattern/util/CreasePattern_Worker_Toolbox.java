package origami.crease_pattern.util;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

public class CreasePattern_Worker_Toolbox {
    private final FoldLineSet ori_s;
    // Extend the vector ab (= s0) from point a to b until it first intersects another polygonal line
    private LineSegment lengthenUntilIntersectionLineSegment = new LineSegment();
    private Point lengthenUntilIntersectionPoint = new Point();
    private StraightLine.Intersection lengthenUntilIntersection_flg = StraightLine.Intersection.NONE_0;//The situation of the first intersection where ab was extended
    //If ab is straightened, including existing polygonal lines, 3
    private LineSegment lengthenUntilIntersectionFirstLineSegment = new LineSegment();//Straightening ab and the existing polygonal line that hits first

    public CreasePattern_Worker_Toolbox(FoldLineSet o_s) {  //コンストラクタ
        ori_s = o_s;
    }

    public void lengthenUntilIntersectionCalculateDisregardIncludedLineSegment_new(Point a, Point b) {//Extend the vector ab (= s0) from point a to b, until it first intersects another fold line (ignoring the line segment contained in the straight line) // If it does not intersect another fold line, Ten a return
        LineSegment addLine = new LineSegment(a, b);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_point_distance = kousa_point.distance(addLine.getA());
        StraightLine straightLine = new StraightLine(addLine.getA(), addLine.getB());
        StraightLine.Intersection i_kousa_flg;

        lengthenUntilIntersection_flg = StraightLine.Intersection.NONE_0;
        for (var ls : ori_s.getLineSegmentsIterable()) {
            if (ls.getColor().isFoldingLine()) {
// 0 = This straight line does not intersect the given line segment,
// 1 = X type intersects,
// 21 = T-shaped intersection at point a of the line segment,
// 22 = T-shaped intersection at point b of the line segment,
// 3 = Line segments are included in the straight line.
                i_kousa_flg = straightLine.lineSegment_intersect_reverse_detail(ls);//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                //if(i_kousa_flg==3){lengthenUntilIntersectionFoldLineIncluded_flg=3;}
                if ((i_kousa_flg == StraightLine.Intersection.INTERSECT_X_1 || i_kousa_flg == StraightLine.Intersection.INTERSECT_T_A_21) || i_kousa_flg == StraightLine.Intersection.INTERSECT_T_B_22) {

                    kousa_point = OritaCalc.findIntersection(straightLine, ls);//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す

                    if (kousa_point.distance(addLine.getA()) > Epsilon.UNKNOWN_1EN5) {

                        if (kousa_point.distance(addLine.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(addLine.getA(), addLine.getB(), addLine.getA(), kousa_point);

                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                kousa_point_distance = kousa_point.distance(addLine.getA());
                                addLine = new LineSegment(addLine.getA(), kousa_point);

                                lengthenUntilIntersection_flg = i_kousa_flg;
                                lengthenUntilIntersectionFirstLineSegment = new LineSegment(ls);
                            }
                        }
                    }
                }
            }
        }

        lengthenUntilIntersectionLineSegment = addLine;
        lengthenUntilIntersectionPoint = addLine.getB();
    }

    public StraightLine.Intersection getLengthenUntilIntersectionFlg_new() {// 0 = This straight line does not intersect the given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
        return lengthenUntilIntersection_flg;
    }

    public LineSegment getLengthenUntilIntersectionLineSegment_new() {
        return lengthenUntilIntersectionLineSegment;
    }

    public LineSegment getLengthenUntilIntersectionFirstLineSegment_new() {
        return lengthenUntilIntersectionFirstLineSegment;
    }

    public Point getLengthenUntilIntersectionPoint_new() {
        return lengthenUntilIntersectionPoint;
    }
}
