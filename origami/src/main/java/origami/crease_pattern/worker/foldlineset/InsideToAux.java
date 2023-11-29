package origami.crease_pattern.worker.foldlineset;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.util.ArrayList;

public class InsideToAux {
    public static boolean apply(FoldLineSet foldLineSet, Polygon b) {
        boolean i_r = false;

        int okikae_suu = 0;
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            LineSegment s = foldLineSet.get(i);
            if (s.getColor().isFoldingLine()) {
                if (b.totu_boundary_inside(s)) {
                    okikae_suu = okikae_suu + 1;

                    LineSegment add_sen = new LineSegment(s);
                    add_sen.setColor(LineColor.CYAN_3);

                    foldLineSet.deleteLine(i);
                    foldLineSet.addLine(add_sen);
                    i = i - 1;

                    i_r = true;
                }
            }
        }

        int kawatteinai_kazu = foldLineSet.getTotal() - okikae_suu;
        if (kawatteinai_kazu == 0) {
            divideLineSegmentIntersections(foldLineSet);
        }
        if (kawatteinai_kazu >= 1) {
            if (okikae_suu >= 1) {
                foldLineSet.divideLineSegmentWithNewLines(foldLineSet.getTotal() - okikae_suu, foldLineSet.getTotal());
            }
        }
//上２行の場合わけが必要な理由は、kousabunkatu()をやってしまうと折線と補助活線との交点で折線が分割されるから。kousabunkatu(1,sousuu-okikae_suu,sousuu-okikae_suu+1,sousuu)だと折線は分割されない。


        return i_r;

    }

    //---------------------
    //交差している２つの線分の交点で２つの線分を分割する。　まったく重なる線分が２つあった場合は、なんの処理もなされないまま２つとも残る。
    public static void divideLineSegmentIntersections(FoldLineSet foldLineSet) {
        int ibunkatu = 1;//分割があれば1、なければ0
        ArrayList<Boolean> k_flg = new ArrayList<>();//交差分割の影響があることを示すフラッグ。

        for (int i = 0; i <= foldLineSet.getTotal() + 1; i++) {
            k_flg.add(true);
        }

        while (ibunkatu != 0) {
            ibunkatu = 0;
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                if (k_flg.get(i)) {
                    k_flg.set(i, false);
                    for (int j = 1; j <= foldLineSet.getTotal(); j++) {
                        if (i != j) {
                            if (k_flg.get(j)) {
                                int old_sousuu = foldLineSet.getTotal();
                                boolean itemp = divideLineSegmentIntersections(foldLineSet, i, j);
                                if (old_sousuu < foldLineSet.getTotal()) {
                                    for (int is = old_sousuu + 1; is <= foldLineSet.getTotal(); is++) {
                                        k_flg.add(true);
                                    }
                                }
                                if (itemp) {
                                    ibunkatu = ibunkatu + 1;
                                    k_flg.set(i, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    //円の追加-------------------------------

    //Divide the two line segments at the intersection of the two intersecting line segments. After splitting 1. Returns 0 if not done. From Orihime 2.002, the color of the line after splitting is also controlled (if there is an overlap, it will be unified and the color with the later number will be used).
    public static boolean divideLineSegmentIntersections(FoldLineSet foldLineSet, int i, int j) {
        if (i == j) {
            return false;
        }

        LineSegment si = foldLineSet.get(i);
        LineSegment sj = foldLineSet.get(j);

        if (si.determineMaxX() < sj.determineMinX()) {
            return false;
        }//これはSenbunにi_max_xがちゃんと定義されているときでないとうまくいかない
        if (sj.determineMaxX() < si.determineMinX()) {
            return false;
        }//これはSenbunにi_min_xがちゃんと定義されているときでないとうまくいかない
        if (si.determineMaxY() < sj.determineMinY()) {
            return false;
        }//これはSenbunにi_max_yがちゃんと定義されているときでないとうまくいかない
        if (sj.determineMaxY() < si.determineMinY()) {
            return false;
        }//これはSenbunにi_min_yがちゃんと定義されているときでないとうまくいかない

        Point p1 = si.getA();
        Point p2 = si.getB();
        Point p3 = sj.getA();
        Point p4 = sj.getB();

        double ixmax = si.determineAX();
        double ixmin = si.determineAX();
        double iymax = si.determineAY();
        double iymin = si.determineAY();

        if (ixmax < si.determineBX()) {
            ixmax = si.determineBX();
        }
        if (ixmin > si.determineBX()) {
            ixmin = si.determineBX();
        }
        if (iymax < si.determineBY()) {
            iymax = si.determineBY();
        }
        if (iymin > si.determineBY()) {
            iymin = si.determineBY();
        }

        double jxmax = sj.determineAX();
        double jxmin = sj.determineAX();
        double jymax = sj.determineAY();
        double jymin = sj.determineAY();

        if (jxmax < sj.determineBX()) {
            jxmax = sj.determineBX();
        }
        if (jxmin > sj.determineBX()) {
            jxmin = sj.determineBX();
        }
        if (jymax < sj.determineBY()) {
            jymax = sj.determineBY();
        }
        if (jymin > sj.determineBY()) {
            jymin = sj.determineBY();
        }

        if (ixmax + Epsilon.UNKNOWN_05 < jxmin) {
            return false;
        }
        if (jxmax + Epsilon.UNKNOWN_05 < ixmin) {
            return false;
        }
        if (iymax + Epsilon.UNKNOWN_05 < jymin) {
            return false;
        }
        if (jymax + Epsilon.UNKNOWN_05 < iymin) {
            return false;
        }

        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersection(si, sj);
        Point pk;
        switch (intersection) {
            case INTERSECTS_1:
                pk = (OritaCalc.findIntersection(si, sj));
                si.setA(p1);
                si.setB(pk);
                sj.setA(p3);
                sj.setB(pk);
                foldLineSet.addLine(p2, pk, si.getColor());
                foldLineSet.addLine(p4, pk, sj.getColor());
                return true;
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                pk = OritaCalc.findIntersection(si, sj);
                sj.setA(p3);
                sj.setB(pk);
                foldLineSet.addLine(p4, pk, sj.getColor());
                return true;
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                pk = OritaCalc.findIntersection(si, sj);
                si.setA(p1);
                si.setB(pk);
                foldLineSet.addLine(p2, pk, si.getColor());
                return true;
            case NO_INTERSECTION_0: //このifないと本来この後で処理されるべき条件がここで処理されてしまうことある
                if (OritaCalc.determineLineSegmentDistance(si.getA(), sj) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(si.getA(), sj, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk = OritaCalc.findIntersection(si, sj);
                        sj.setA(p3);
                        sj.setB(pk);
                        foldLineSet.addLine(p4, pk, sj.getColor());
                        return true;
                    }
                }

                if (OritaCalc.determineLineSegmentDistance(si.getB(), sj) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(si.getB(), sj, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk = OritaCalc.findIntersection(si, sj);
                        sj.setA(p3);
                        sj.setB(pk);
                        foldLineSet.addLine(p4, pk, sj.getColor());
                        return true;
                    }
                }

                if (OritaCalc.determineLineSegmentDistance(sj.getA(), si) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(sj.getA(), si, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk = OritaCalc.findIntersection(si, sj);
                        si.setA(p1);
                        si.setB(pk);
                        foldLineSet.addLine(p2, pk, si.getColor());
                        return true;
                    }
                }

                if (OritaCalc.determineLineSegmentDistance(sj.getB(), si) < Epsilon.UNKNOWN_001) {
                    if (OritaCalc.determineClosestLineSegmentEndpoint(sj.getB(), si, Epsilon.UNKNOWN_001) == 3) { //20161107 わずかに届かない場合
                        pk = OritaCalc.findIntersection(si, sj);    //<<<<<<<<<<<<<<<<<<<<<<<
                        si.setA(p1);
                        si.setB(pk);
                        foldLineSet.addLine(p2, pk, si.getColor());
                        return true;
                    }
                }

                break;
            case PARALLEL_EQUAL_31: //2つの線分がまったく同じ場合は、何もしない。
                return false;
            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321: {//2つの線分の端点どうし(p1とp3)が1点で重なる。siにsjが含まれる
                si.setA(p2);
                si.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322: {//2つの線分の端点どうし(p1とp3)が1点で重なる。sjにsiが含まれる
                sj.setA(p2);
                sj.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331: {//2つの線分の端点どうし(p1とp4)が1点で重なる。siにsjが含まれる
                si.setA(p2);
                si.setB(p3);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332: {//2つの線分の端点どうし(p1とp4)が1点で重なる。sjにsiが含まれる
                sj.setA(p2);
                sj.setB(p3);
                LineColor overlapping_col;
                overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);
                return true;
            }
            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341: {//2つの線分の端点どうし(p2とp3)が1点で重なる。siにsjが含まれる
                si.setA(p1);
                si.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342: {//2つの線分の端点どうし(p2とp3)が1点で重なる。sjにsiが含まれる
                sj.setA(p1);
                sj.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351: {//2つの線分の端点どうし(p2とp4)が1点で重なる。siにsjが含まれる
                si.setA(p1);
                si.setB(p3);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352: {//2つの線分の端点どうし(p2とp4)が1点で重なる。sjにsiが含まれる
                sj.setA(p1);
                sj.setB(p3);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S1_INCLUDES_S2_361: {//p1-p3-p4-p2の順
                si.setA(p1);
                si.setB(p3);

                foldLineSet.addLine(p2, p4, si.getColor());
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S1_INCLUDES_S2_362: {//p1-p4-p3-p2の順
                si.setA(p1);
                si.setB(p4);

                foldLineSet.addLine(p2, p3, si.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S2_INCLUDES_S1_363: {//p3-p1-p2-p4の順
                sj.setA(p1);
                sj.setB(p3);

                foldLineSet.addLine(p2, p4, sj.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S2_INCLUDES_S1_364: {//p3-p2-p1-p4の順
                sj.setA(p1);
                sj.setB(p4);

                foldLineSet.addLine(p2, p3, sj.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_S1_END_OVERLAPS_S2_START_371: {//p1-p3-p2-p4の順
                si.setA(p1);
                si.setB(p3);

                sj.setA(p2);
                sj.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                foldLineSet.addLine(p2, p3, overlapping_col);
                return true;
            }
            case PARALLEL_S1_END_OVERLAPS_S2_END_372: {//p1-p4-p2-p3の順
                si.setA(p1);
                si.setB(p4);

                sj.setA(p3);
                sj.setB(p2);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                foldLineSet.addLine(p2, p4, overlapping_col);
                return true;
            }
            case PARALLEL_S1_START_OVERLAPS_S2_END_373: {//p3-p1-p4-p2の順
                sj.setA(p1);
                sj.setB(p3);
                si.setA(p2);
                si.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                foldLineSet.addLine(p1, p4, overlapping_col);
                return true;
            }
            case PARALLEL_S1_START_OVERLAPS_S2_START_374: {//p4-p1-p3-p2の順
                sj.setA(p1);
                sj.setB(p4);
                si.setA(p3);
                si.setB(p2);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                foldLineSet.addLine(p1, p3, overlapping_col);
                return true;
            }
            default:
                break;
        }

        return false;
    }
}
