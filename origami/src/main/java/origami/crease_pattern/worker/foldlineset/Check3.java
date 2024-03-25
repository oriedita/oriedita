package origami.crease_pattern.worker.foldlineset;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;
import origami.folding.util.WeightedValue;

public class Check3 {
    //Check the number of lines around the vertex
    public static void apply(FoldLineSet foldLineSet) {
        double r = Epsilon.UNKNOWN_1EN4;
        foldLineSet.getCheck3LineSegment().clear();
        foldLineSet.unselect_all();
        for (var si : foldLineSet.getLineSegmentsIterable()) {
            if (si.getColor() != LineColor.CYAN_3) {
                int tss;    //頂点の周りの折線の数。　tss%2==0 偶数、==1 奇数
                int tss_red;    //Number of mountain fold lines around the vertex 。
                int tss_blue;    //頂点の周りの谷折線の数。
                int tss_black;    //頂点の周りの境界線の数。
                int tss_hojyo_kassen;    //頂点の周りの補助活線の数。

                //-----------------
                Point p = si.getA();
                tss = vertex_surrounding_lineCount(foldLineSet, p, r);
                tss_red = vertex_surrounding_lineCount_red(foldLineSet, p, r);
                tss_blue = vertex_surrounding_lineCount_blue(foldLineSet, p, r);
                tss_black = vertex_surrounding_lineCount_black(foldLineSet, p, r);
                tss_hojyo_kassen = vertex_surrounding_lineCount_auxiliary_live_line(foldLineSet, p, r);

                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点

                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (!extended_fushimi_decide_inside(foldLineSet, p)) {
                        foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (!extended_fushimi_decide_sides(foldLineSet, p)) {
                        foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                //-----------------
                p = si.getB();
                tss = vertex_surrounding_lineCount(foldLineSet, p, r);
                tss_red = vertex_surrounding_lineCount_red(foldLineSet, p, r);
                tss_blue = vertex_surrounding_lineCount_blue(foldLineSet, p, r);
                tss_black = vertex_surrounding_lineCount_black(foldLineSet, p, r);
                tss_hojyo_kassen = vertex_surrounding_lineCount_auxiliary_live_line(foldLineSet, p, r);

                //-----------------
                if ((tss_black != 0) && (tss_black != 2)) {//黒線がないか2本以外の場合はおかしい。
                    foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                }

                if (tss_black == 0) {//黒線がない場合
                    if (tss - tss_hojyo_kassen == tss_red + tss_blue) {//（前提として境界は黒で、山谷未設定折線はないこと。）頂点周囲に赤か青しかない。つまり、用紙内部の点
                        if (Math.abs(tss_red - tss_blue) != 2) {//用紙内部の点で前川定理を満たさないのはダメ
                            foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                        }
                    }
                    if (!extended_fushimi_decide_inside(foldLineSet, p)) {
                        foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                    }
                }

                if (tss_black == 2) {//黒線が2本の場合
                    if (!extended_fushimi_decide_sides(foldLineSet, p)) {
                        foldLineSet.getCheck3LineSegment().add(new LineSegment(p, p));//set_select(i,2);
                    }
                }
            }
        }
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many line segments are present (the number of line segments having an end point within the vertex and r).
    public static int vertex_surrounding_lineCount(FoldLineSet foldLineSet, Point p, double r) {
        Point q = foldLineSet.closestPoint(p);//qは点pに近い方の端点

        int i_return = 0;

        for (var si : foldLineSet.getLineSegmentsIterable()) {
            Point p_temp = si.getA();
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp = si.getB();
            }

            if (q.distanceSquared(p_temp) < r * r) {
                i_return++;
            }
        }

        return i_return;
    }

    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many red line segments appear (the number of line segments having an end point within the vertex and r).
    public static int vertex_surrounding_lineCount_red(FoldLineSet foldLineSet, Point p, double r) {
        Point q = foldLineSet.closestPoint(p);//q is the end point closer to the point p

        int i_return = 0;

        for (var si : foldLineSet.getLineSegmentsIterable()) {
            Point p_temp = si.getA();
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp = si.getB();
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (si.getColor() == LineColor.RED_1) {
                    i_return++;
                }
            }
        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many blue line segments appear (the number of line segments having an end point within the vertex and r).
    public static int vertex_surrounding_lineCount_blue(FoldLineSet foldLineSet, Point p, double r) {
        Point q = foldLineSet.closestPoint(p);//qは点pに近い方の端点

        int i_return = 0;

        for (var si : foldLineSet.getLineSegmentsIterable()) {
            Point p_temp = si.getA();
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp = si.getB();
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (si.getColor() == LineColor.BLUE_2) {
                    i_return++;
                }
            }

        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many black line segments appear (the number of line segments having an end point within the vertex and r).
    public static int vertex_surrounding_lineCount_black(FoldLineSet foldLineSet, Point p, double r) {
        Point q = foldLineSet.closestPoint(p);//qは点pに近い方の端点

        int i_return = 0;

        for (var si : foldLineSet.getLineSegmentsIterable()) {
            Point p_temp = si.getA();
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp = si.getB();
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (si.getColor() == LineColor.BLACK_0) {
                    i_return++;
                }
            }
        }

        return i_return;
    }

    //--------------------------------------------
    //If the end point of the line segment closest to the point p and the end point closer to the point p is the vertex, how many auxiliary live lines are present (the number of line segments having an end point within the vertex and r).
    public static int vertex_surrounding_lineCount_auxiliary_live_line(FoldLineSet foldLineSet, Point p, double r) {
        Point q = foldLineSet.closestPoint(p);//qは点pに近い方の端点

        int i_return = 0;

        for (var si : foldLineSet.getLineSegmentsIterable()) {
            Point p_temp = si.getA();
            if (q.distanceSquared(si.getB()) < q.distanceSquared(si.getA())) {
                p_temp = si.getB();
            }
            if (q.distanceSquared(p_temp) < r * r) {
                if (!si.getColor().isFoldingLine()) {
                    i_return = i_return + 1;
                }
            }
        }

        return i_return;
    }

    //Determine if the endpoint inside the paper closest to Point p satisfies the extended Fushimi theorem
    public static boolean extended_fushimi_decide_inside(FoldLineSet foldLineSet, Point p) {//return　0=満たさない、　1=満たす。　
        Point t1 = foldLineSet.closestPointOfFoldLine(p);//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        SortingBox<LineSegment> nbox = new SortingBox<>();
        for (var si : foldLineSet.getLineSegmentsIterable()) {
            if (si.getColor().isFoldingLine()) { //この段階で補助活線は除く
                if (t1.distance(si.getA()) < Epsilon.FLAT) {
                    nbox.addByWeight(si, OritaCalc.angle(si.getA(), si.getB()));
                } else if (t1.distance(si.getB()) < Epsilon.FLAT) {
                    nbox.addByWeight(si, OritaCalc.angle(si.getB(), si.getA()));
                }
            }
        }

        return extended_fushimi_decide_inside(nbox);
    }

    public static boolean extended_fushimi_decide_inside(SortingBox<LineSegment> nbox) {//return　0=満たさない、　1=満たす。　
        if (nbox.getTotal() % 2 == 1) {//t1を端点とする折線の数が奇数のとき
            return false;
        }

        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき
            if (nbox.getValue(1).getColor() != nbox.getValue(2).getColor()) {//2本の線種が違うなら角度関係なしにダメ
                return false;
            }

            //The following is when the two line types are blue-blue or red-red
            LineSegment.Intersection i_senbun_kousa_hantei = OritaCalc.determineLineSegmentIntersection(nbox.getValue(1), nbox.getValue(2), Epsilon.FLAT);

            switch (i_senbun_kousa_hantei) {
                case PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323:
                case PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333:
                case PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353:
                case PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343:
                    return true;
                default:
                    return false;
            }
        }

        //以下はt1を端点とする折線の数が4以上の偶数のとき
        double fushimi_decision_angle_goukei = 360.0;

        SortingBox<LineSegment> nbox1 = new SortingBox<>();

        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する
            SortingBox<LineSegment> result = null;//Operation to make three adjacent angles into one angle by the extended Fushimi theorem
            SortingBox<LineSegment> nboxtemp = new SortingBox<>();
            SortingBox<LineSegment> nbox11 = new SortingBox<>();
            int tikai_orisen_jyunban;
            int tooi_orisen_jyunban;

            double kakudo_min = 10000.0;

            //角度の最小値kakudo_minを求める
            for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
                tikai_orisen_jyunban = k;
                if (tikai_orisen_jyunban > nbox.getTotal()) {
                    tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getTotal();
                }
                tooi_orisen_jyunban = k + 1;
                if (tooi_orisen_jyunban > nbox.getTotal()) {
                    tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getTotal();
                }

                double temp_kakudo = OritaCalc.angle_between_0_kmax(
                        OritaCalc.angle_between_0_kmax(nbox.getWeight(tooi_orisen_jyunban), fushimi_decision_angle_goukei)
                                -
                                OritaCalc.angle_between_0_kmax(nbox.getWeight(tikai_orisen_jyunban), fushimi_decision_angle_goukei)

                        , fushimi_decision_angle_goukei
                );

                if (temp_kakudo < kakudo_min) {
                    kakudo_min = temp_kakudo;
                }
            }

            for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
                double temp_kakudo = OritaCalc.angle_between_0_kmax(nbox.getWeight(2) - nbox.getWeight(1), fushimi_decision_angle_goukei);

                if (Math.abs(temp_kakudo - kakudo_min) < Epsilon.FLAT) {
                    if (nbox.getValue(1).getColor() != nbox.getValue(2).getColor()) {//この場合に隣接する３角度を1つの角度にする
                        // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。

                        double kijyun_kakudo = nbox.getWeight(3);

                        for (int i = 1; i <= nbox.getTotal(); i++) {
                            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                            i_d_0.set(nbox.getWeightedValue(i));

                            i_d_0.setWeight(
                                    OritaCalc.angle_between_0_kmax(i_d_0.getWeight() - kijyun_kakudo, fushimi_decision_angle_goukei)
                            );

                            nboxtemp.add(i_d_0);
                        }

                        for (int i = 3; i <= nboxtemp.getTotal(); i++) {
                            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                            i_d_0.set(nboxtemp.getWeightedValue(i));

                            nbox11.add(i_d_0);
                        }

                        fushimi_decision_angle_goukei = fushimi_decision_angle_goukei - 2.0 * kakudo_min;
                        result = nbox11;
                        break;
                    }
                }
                nbox.shift();

            }
            if (result == null) {// 折線を2つ減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
                for (int i = 1; i <= nbox.getTotal(); i++) {
                    nbox11.add(nbox.getWeightedValue(i));
                }
                result = nbox11;
            }

            nbox1.set(result);
            if (nbox1.getTotal() == nbox.getTotal()) {
                return false;
            }
            nbox.set(nbox1);
        }

        double temp_kakudo = OritaCalc.angle_between_0_kmax(
                OritaCalc.angle_between_0_kmax(nbox.getWeight(1), fushimi_decision_angle_goukei)
                        -
                        OritaCalc.angle_between_0_kmax(nbox.getWeight(2), fushimi_decision_angle_goukei)
                , fushimi_decision_angle_goukei
        );

        return Math.abs(fushimi_decision_angle_goukei - temp_kakudo * 2.0) < Epsilon.FLAT;//この0だけ、角度がおかしいという意味
    }

    //Point p に最も近い用紙辺部の端点が拡張伏見定理を満たすか判定
    public static boolean extended_fushimi_decide_sides(FoldLineSet foldLineSet, Point p) {//return　0=満たさない、　1=満たす。　
        Point t1 = foldLineSet.closestPointOfFoldLine(p);//点pに最も近い、「線分の端点」を返すori_s.closestPointは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        //t1を端点とする折線をNarabebakoに入れる
        SortingBox<LineSegment> nbox = new SortingBox<>();
        for (var s : foldLineSet.getLineSegmentsIterable()) {
            if (s.getColor().isFoldingLine()) { //この段階で補助活線は除く
                if (t1.distance(s.getA()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                } else if (t1.distance(s.getB()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                }
            }
        }

        return extended_fushimi_decide_sides(nbox);
    }

    // ---------------------------------
    public static boolean extended_fushimi_decide_sides(SortingBox<LineSegment> nbox) {//return　0=満たさない、　1=満たす。　
        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき
            if (nbox.getValue(1).getColor() != LineColor.BLACK_0) {//1本目が黒でないならダメ
                return false;
            }
            //2本目が黒でないならダメ
            return nbox.getValue(2).getColor() == LineColor.BLACK_0;

            //2本の線種が黒黒
        }


        //以下はt1を端点とする折線の数が3以上の偶数のとき

        //fushimi_decision_angle_goukei=360.0;


        //辺の折線が,ならべばこnbox,の一番目と最後の順番になるようにする。

        int saisyo_ni_suru = -10;
        for (int i = 1; i <= nbox.getTotal() - 1; i++) {
            if ((nbox.getValue(i).getColor() == LineColor.BLACK_0) &&
                    (nbox.getValue(i + 1).getColor() == LineColor.BLACK_0)) {
                saisyo_ni_suru = i + 1;
            }
        }

        if ((nbox.getValue(nbox.getTotal()).getColor() == LineColor.BLACK_0) &&
                (nbox.getValue(1).getColor() == LineColor.BLACK_0)) {
            saisyo_ni_suru = 1;
        }

        if (saisyo_ni_suru < 0) {
            return false;
        }

        for (int i = 1; i <= saisyo_ni_suru - 1; i++) {
            nbox.shift();
        }

        //ならべばこnbox,の一番目の折線がx軸となす角度が0になるようにする。
        SortingBox<LineSegment> nbox1 = new SortingBox<>();

        double sasihiku_kakudo = nbox.getWeight(1);

        for (int i = 1; i <= nbox.getTotal(); i++) {
            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
            i_d_0.set(nbox.getWeightedValue(i));

            i_d_0.setWeight(OritaCalc.angle_between_0_360(i_d_0.getWeight() - sasihiku_kakudo));
            nbox1.add(i_d_0);
        }

        nbox.set(nbox1);

        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する
            nbox1.set(extended_fushimi_determine_sides_theorem(nbox));
            if (nbox1.getTotal() == nbox.getTotal()) {
                return false;
            }
            nbox.set(nbox1);
        }

        return true;
    }

    //Operation to make three angles adjacent to each other at the points of the side into one angle or to cut the corner of the side like the extended Fushimi theorem
    public static SortingBox<LineSegment> extended_fushimi_determine_sides_theorem(SortingBox<LineSegment> nbox0) {
        SortingBox<LineSegment> nbox1 = new SortingBox<>();

        double angle_min = 10000.0;
        double temp_angle;

        //Find the minimum angle angle_min
        for (int k = 1; k <= nbox0.getTotal() - 1; k++) {//kは角度の順番
            temp_angle = nbox0.getWeight(k + 1) - nbox0.getWeight(k);
            if (temp_angle < angle_min) {
                angle_min = temp_angle;
            }
        }

        temp_angle = nbox0.getWeight(2) - nbox0.getWeight(1);
        if (Math.abs(temp_angle - angle_min) < Epsilon.FLAT) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 2; i <= nbox0.getTotal(); i++) {
                WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                i_d_0.set(nbox0.getWeightedValue(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        temp_angle = nbox0.getWeight(nbox0.getTotal()) - nbox0.getWeight(nbox0.getTotal() - 1);
        if (Math.abs(temp_angle - angle_min) < Epsilon.FLAT) {// 折線を1つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
            for (int i = 1; i <= nbox0.getTotal() - 1; i++) {
                WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                i_d_0.set(nbox0.getWeightedValue(i));
                nbox1.add(i_d_0);
            }
            return nbox1;
        }

        for (int k = 2; k <= nbox0.getTotal() - 2; k++) {//kは角度の順番
            temp_angle = nbox0.getWeight(k + 1) - nbox0.getWeight(k);
            if (Math.abs(temp_angle - angle_min) < Epsilon.FLAT) {
                if (nbox0.getValue(k).getColor() != nbox0.getValue(k + 1).getColor()) {//この場合に隣接する３角度を1つの角度にする
                    // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。

                    for (int i = 1; i <= k - 1; i++) {
                        WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                        i_d_0.set(nbox0.getWeightedValue(i));
                        nbox1.add(i_d_0);
                    }

                    for (int i = k + 2; i <= nbox0.getTotal(); i++) {
                        WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                        i_d_0.set(nbox0.getWeightedValue(i));
                        i_d_0.setWeight(
                                i_d_0.getWeight() - 2.0 * angle_min
                        );
                        nbox1.add(i_d_0);
                    }

                    return nbox1;
                }
            }
        }


        // 折線を減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
        for (int i = 1; i <= nbox0.getTotal(); i++) {
            nbox1.add(nbox0.getWeightedValue(i));
        }
        return nbox1;
    }
}
