package origami.crease_pattern.worker.foldlineset;

import org.tinylog.Logger;
import origami.Epsilon;
import origami.crease_pattern.*;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;
import origami.folding.util.WeightedValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Check4 {
    public static void apply(FoldLineSet foldLineSet) throws InterruptedException {
        foldLineSet.cAMVViolations.clear();

        PointLineMap map = new PointLineMap(foldLineSet.getLineSegments());
        Logger.info("check4_T_size() = " + map.getPoints().size());

        ExecutorService service = Executors.newWorkStealingPool();

        //Selection of whether the place to be checked can be folded flat
        for (Point point : map.getPoints()) {
            service.submit(() -> {
                Point p = new Point(point);
                try {
                    Optional<FlatFoldabilityViolation> violation = findFlatfoldabilityViolation(p, map.getLines(point));
                    violation.ifPresent(foldLineSet.cAMVViolations::add);
                } catch (InterruptedException e) {
                    // finish thread.
                }
            });
        }

        // Done adding tasks, shut down ExecutorService
        service.shutdown();
        try {
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("Check cAMV did not finish!");
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("Check cAMV did not exit!");
            }
        }
    }

    private static Optional<FlatFoldabilityViolation> findFlatfoldabilityViolation(Point p, List<LineSegment> lines)
            throws InterruptedException {
        //If the end point of the line segment closest to the point p and the end point closer to the point p is the apex, how many line segments are present (the number of line segments having an end point within the apex and r).
        int i_tss_red = 0;
        int i_tss_blue = 0;
        int i_tss_black = 0;

        SortingBox<LineSegment> nbox = new SortingBox<>();

        for (LineSegment s : lines) {
            if (s.getColor() == LineColor.RED_1) {
                i_tss_red++;
            } else if (s.getColor() == LineColor.BLUE_2) {
                i_tss_blue++;
            } else if (s.getColor() == LineColor.BLACK_0) {
                i_tss_black++;
            }

            //Put a polygonal line with p as the end point in Narabebako
            if (s.getColor().isFoldingLine()) { //Auxiliary live lines are excluded at this stage
                if (p.distance(s.getA()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getA(), s.getB()));
                } else if (p.distance(s.getB()) < Epsilon.FLAT) {
                    nbox.addByWeight(s, OritaCalc.angle(s.getB(), s.getA()));
                }
            }
        }

        // Judgment start-------------------------------------------
        if ((i_tss_black != 0) && (i_tss_black != 2)) {//It is strange if there are no black lines or if there are other than two lines.
            return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.NUMBER_OF_FOLDS,
                    FlatFoldabilityViolation.Color.UNKNOWN));
        }

        if (i_tss_black == 0) {//If there is no black line
            Optional<FlatFoldabilityViolation> angleOrLBLViolation = findFlatfoldabilityViolationInside(p, nbox);
            FlatFoldabilityViolation.Rule rule = FlatFoldabilityViolation.Rule.NONE;
            if (angleOrLBLViolation.isPresent()) {
                rule = angleOrLBLViolation.get().getViolatedRule();
            }
            if (Math.abs(i_tss_red - i_tss_blue) != 2) {//Do not satisfy Maekawa's theorem in terms of the inside of the paper
                if (rule == FlatFoldabilityViolation.Rule.LITTLE_BIG_LITTLE || rule == FlatFoldabilityViolation.Rule.NONE) {
                    rule = FlatFoldabilityViolation.Rule.MAEKAWA;
                }
                if (i_tss_blue == i_tss_red) {
                    return Optional.of(new FlatFoldabilityViolation(p, rule, FlatFoldabilityViolation.Color.EQUAL));
                } else if (Math.abs(i_tss_red - i_tss_blue) > 2) {
                    if (i_tss_blue > i_tss_red) {
                        return Optional.of(new FlatFoldabilityViolation(
                                p,
                                rule,
                                FlatFoldabilityViolation.Color.NOT_ENOUGH_MOUNTAIN));
                    }
                    return Optional.of(new FlatFoldabilityViolation(
                            p,
                            rule,
                            FlatFoldabilityViolation.Color.NOT_ENOUGH_VALLEY));
                } else {
                    if (i_tss_blue > i_tss_red) {
                        return Optional.of(new FlatFoldabilityViolation(
                                p,
                                rule,
                                FlatFoldabilityViolation.Color.NOT_ENOUGH_VALLEY));
                    }
                    return Optional.of(new FlatFoldabilityViolation(
                            p,
                            rule,
                            FlatFoldabilityViolation.Color.NOT_ENOUGH_MOUNTAIN));
                }
            }
            if (rule != FlatFoldabilityViolation.Rule.MAEKAWA && rule != FlatFoldabilityViolation.Rule.NONE) {
                if (i_tss_blue == i_tss_red) {
                    return Optional.of(new FlatFoldabilityViolation(p, rule, FlatFoldabilityViolation.Color.EQUAL));
                } else {
                    if (rule == FlatFoldabilityViolation.Rule.LITTLE_BIG_LITTLE) {
                        return angleOrLBLViolation;
                    }
                    return Optional.of(new FlatFoldabilityViolation(p, rule, FlatFoldabilityViolation.Color.CORRECT));
                }
            }
            return Optional.empty();
        }

        //When there are two black lines
        return findLittleBigLittleViolationOnSides(p, nbox);
    }

    public static Optional<FlatFoldabilityViolation> findFlatfoldabilityViolationInside(Point p, SortingBox<LineSegment> nbox) {
        if (nbox.getTotal() % 2 == 1) {//t1を端点とする折線の数が奇数のとき
            return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.NUMBER_OF_FOLDS,
                    FlatFoldabilityViolation.Color.UNKNOWN));
        }

        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき

            //The following is when the two line types are blue-blue or red-red
            LineSegment.Intersection intersectionState = OritaCalc.determineLineSegmentIntersection(nbox.getValue(1), nbox.getValue(2), Epsilon.FLAT);

            switch (intersectionState) {
                case PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323:
                case PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333:
                case PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353:
                case PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343:
                    if (nbox.getValue(1).getColor() != nbox.getValue(2).getColor()) {//2本の線種が違うなら角度関係なしにダメ
                        return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.MAEKAWA,
                                FlatFoldabilityViolation.Color.UNKNOWN));
                    }
                    return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.NONE,
                            FlatFoldabilityViolation.Color.UNKNOWN));
                default:
                    return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.ANGLES,
                            FlatFoldabilityViolation.Color.UNKNOWN));
            }
        }

        //以下はt1を端点とする折線の数が4以上の偶数のとき
        double maxAngle = 360.0;

        SortingBox<LineSegment> nbox1 = new SortingBox<>();

        if (!angularlyFlatfoldable(nbox)) {
            return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.ANGLES,
                    FlatFoldabilityViolation.Color.UNKNOWN));
        }

        LinkedHashMap<LineSegment, Boolean> littleBigLittleViolations = new LinkedHashMap<>();
        for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
            LineSegment copy = new LineSegment();
            copy.set(nbox.getValue(k));

            if (copy.getA().distance(p) > Epsilon.UNKNOWN_1EN6) {
                copy.a_b_swap();
            }
            littleBigLittleViolations.put(copy, false);
        }
        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する
            SortingBox<LineSegment> result = null;//Operation to make three adjacent angles into one angle by the extended Fushimi theorem
            SortingBox<LineSegment> nboxtemp = new SortingBox<>();
            SortingBox<LineSegment> nbox11 = new SortingBox<>();
            int currentIndex;
            int nextIndex;

            double minAngle = 10000.0;

            //角度の最小値kakudo_minを求める
            for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
                currentIndex = k;
                if (currentIndex > nbox.getTotal()) {
                    currentIndex = currentIndex - nbox.getTotal();
                }
                nextIndex = k + 1;
                if (nextIndex > nbox.getTotal()) {
                    nextIndex = nextIndex - nbox.getTotal();
                }

                double tmpAngle = OritaCalc.angle_between_0_kmax(
                        OritaCalc.angle_between_0_kmax(nbox.getWeight(nextIndex), maxAngle)
                                -
                                OritaCalc.angle_between_0_kmax(nbox.getWeight(currentIndex), maxAngle)

                        , maxAngle
                );

                if (tmpAngle < minAngle) {
                    minAngle = tmpAngle;
                }
            }
            for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
                double tmpAngle = OritaCalc.angle_between_0_kmax(nbox.getWeight(2) - nbox.getWeight(1), maxAngle);


                if (Math.abs(tmpAngle - minAngle) < Epsilon.FLAT) {

                    if (nbox.getValue(1).getColor() != nbox.getValue(2).getColor()) {
                        //この場合に隣接する３角度を1つの角度にする
                        // 折線を2つ減らせる条件に適合したので、新たにnbox1を作ってリターンする。
                        double nextAngle = nbox.getWeight(3);

                        for (int i = 1; i <= nbox.getTotal(); i++) {
                            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                            i_d_0.set(nbox.getWeightedValue(i));

                            i_d_0.setWeight(
                                    OritaCalc.angle_between_0_kmax(i_d_0.getWeight() - nextAngle, maxAngle)
                            );

                            nboxtemp.add(i_d_0);
                        }

                        for (int i = 3; i <= nboxtemp.getTotal(); i++) {
                            WeightedValue<LineSegment> i_d_0 = new WeightedValue<>();
                            i_d_0.set(nboxtemp.getWeightedValue(i));

                            nbox11.add(i_d_0);
                        }

                        maxAngle = maxAngle - 2.0 * minAngle;
                        result = nbox11;
                        break;
                    } else {
                        LineSegment copy = new LineSegment();
                        copy.set(nbox.getValue(1));

                        if (copy.getA().distance(p) > Epsilon.UNKNOWN_1EN6) {
                            copy.a_b_swap();
                        }
                        littleBigLittleViolations.put(copy, true);
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
                return Optional.of(new LittleBigLittleViolation(p, littleBigLittleViolations));
            }
            nbox.set(nbox1);
        }

        double temp_kakudo = OritaCalc.angle_between_0_kmax(
                OritaCalc.angle_between_0_kmax(nbox.getWeight(1), maxAngle)
                        -
                        OritaCalc.angle_between_0_kmax(nbox.getWeight(2), maxAngle)
                , maxAngle
        );

        return Math.abs(maxAngle - temp_kakudo * 2.0) < Epsilon.FLAT ?
                Optional.empty() :
                Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.ANGLES,
                        FlatFoldabilityViolation.Color.UNKNOWN));//この0だけ、角度がおかしいという意味
    }

    public static Optional<FlatFoldabilityViolation> findLittleBigLittleViolationOnSides(Point p, SortingBox<LineSegment> nbox) {//return　0=満たさない、　1=満たす。　
        if (nbox.getTotal() == 2) {//t1を端点とする折線の数が2のとき
            if (nbox.getValue(1).getColor() != LineColor.BLACK_0 || nbox.getValue(2).getColor() != LineColor.BLACK_0) {//1本目が黒でないならダメ
                return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.MAEKAWA,
                        FlatFoldabilityViolation.Color.UNKNOWN));
            }
            //2本目が黒でないならダメ
            return Optional.empty();

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
            return Optional.of(new FlatFoldabilityViolation(p, FlatFoldabilityViolation.Rule.MAEKAWA,
                    FlatFoldabilityViolation.Color.UNKNOWN));
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

        LinkedHashMap<LineSegment, Boolean> littleBigLittleViolations = new LinkedHashMap<>();
        for (int k = 1; k <= nbox.getTotal(); k++) {//kは角度の順番
            LineSegment copy = new LineSegment();
            copy.set(nbox.getValue(k));

            if (copy.getA().distance(p) > Epsilon.UNKNOWN_1EN6) {
                copy.a_b_swap();
            }
            littleBigLittleViolations.put(copy, false);
        }

        while (nbox.getTotal() > 2) {//点から出る折線の数が2になるまで実行する

            nbox1.set(littleBigLittleSingleStep(nbox, littleBigLittleViolations, p));
            if (nbox1.getTotal() == nbox.getTotal()) {
                return Optional.of(new LittleBigLittleViolation(p, littleBigLittleViolations));
            }
            nbox.set(nbox1);
        }

        return Optional.empty();
    }

    public static SortingBox<LineSegment> littleBigLittleSingleStep(
            SortingBox<LineSegment> nbox0, LinkedHashMap<LineSegment, Boolean> violating, Point p) {
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
                } else {
                    LineSegment copy = new LineSegment();
                    copy.set(nbox0.getValue(k));

                    if (copy.getA().distance(p) > Epsilon.UNKNOWN_1EN6) {
                        copy.a_b_swap();
                    }
                    violating.put(copy, true);
                }
            }
        }


        // 折線を減らせる条件に適合した角がなかった場合nbox0とおなじnbox1を作ってリターンする。
        for (int i = 1; i <= nbox0.getTotal(); i++) {
            nbox1.add(nbox0.getWeightedValue(i));
        }
        return nbox1;
    }

    private static boolean angularlyFlatfoldable(SortingBox<LineSegment> lines) {
        double even = 0;
        double odd = 0;
        for (int k = 1; k <= lines.getTotal(); k++) {//kは角度の順番
            if (k % 2 == 0) {
                even += lines.getWeight(k) - lines.getWeight(k - 1);
            } else {
                if (k == 1) {
                    odd += lines.getWeight(k) - (lines.getWeight(lines.getTotal()) - 360);
                } else {
                    odd += lines.getWeight(k) - lines.getWeight(k - 1);
                }
            }

        }
        odd = Math.abs(odd);
        even = Math.abs(even);
        boolean flat = Math.abs(even - odd) < Epsilon.FLAT;
        return flat;
    }
}
