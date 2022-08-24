package origami.crease_pattern.worker.foldlineset;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

public class OrganizeCircles {
    public static void apply(FoldLineSet foldLineSet) {
        for (int i = foldLineSet.getCircles().size() - 1; i >= 0; i--) {
            organizeCircles(foldLineSet, i);
        }
    }

    //Arrangement of circles -----------------------------------------
    public static boolean organizeCircles(FoldLineSet foldLineSet, int i0) {//Organize the jth circle. Returns 1 if deleted by pruning, 0 if not deleted.
        int ies3 = determineCircleState(foldLineSet, i0, 3);
        int ies4 = determineCircleState(foldLineSet, i0, 4);
        int ies5 = determineCircleState(foldLineSet, i0, 5);

        if (ies3 == 100000) {
            return false;
        }
        if (ies3 == 2) {
            return false;
        }
        if ((ies3 == 1) && (ies4 >= 1)) {
            return false;
        }
        if ((ies3 == 1) && (ies5 >= 1)) {
            return false;
        }

        foldLineSet.getCircles().remove(i0);
        return true;
    }

    public static int determineCircleState(FoldLineSet foldLineSet, int i0, int i_mode) {   //Indicates the state of the i-th circle.
        // = 100000 The radius of the i-th circle is not 0
        // =      0 The radius of the i-th circle is 0. It is far from the circumference of other circles. It is far from the center of other circles. Twice
        // =      1 1st digit number. The number that the i-th circle has a radius of 0 and overlaps the center of another circle with a radius of 0. When it overlaps with two or more, it is displayed as 2.
        // =     10 Second digit number. The number of i-th circles with a radius of 0 and other non-zero radii that overlap the center of the circle. When it overlaps with two or more, it is displayed as 2.
        // =   100 3rd digit number. The number of circles with an i-th radius of 0 that overlap the circumference of other non-zero radii. When it overlaps with two or more, it is displayed as 2.
        // =  1000 4th digit number. The number of i-th circles with a radius of 0 that overlaps with other polygonal lines. When it overlaps with two or more, it is displayed as 2.
        // = 10000 5th digit number. The number of i-th circles with a radius of 0 that overlaps with other auxiliary hot lines. When it overlaps with two or more, it is displayed as 2.
        Circle e_temp = new Circle();
        e_temp.set(foldLineSet.getCircles().get(i0));
        double er_0 = e_temp.getR();
        Point ec_0 = new Point();
        ec_0.set(e_temp.determineCenter());

        double er_1;
        Point ec_1 = new Point();

        int ir1 = 0;
        int ir2 = 0;
        int ir3 = 0;
        int ir4 = 0;
        int ir5 = 0;

        if (er_0 < Epsilon.UNKNOWN_1EN7) {
            for (int i = 0; i < foldLineSet.getCircles().size(); i++) {
                if (i != i0) {
                    e_temp.set(foldLineSet.getCircles().get(i));
                    er_1 = e_temp.getR();
                    ec_1.set(e_temp.determineCenter());
                    if (er_1 < Epsilon.UNKNOWN_1EN7) {//The radius of the other circle is 0
                        if (ec_0.distance(ec_1) < Epsilon.UNKNOWN_1EN7) {
                            ir1 = ir1 + 1;
                        }
                    } else {//The radius of the other circle is not 0
                        if (ec_0.distance(ec_1) < Epsilon.UNKNOWN_1EN7) {
                            ir2 = ir2 + 1;
                        } else if (Math.abs(ec_0.distance(ec_1) - er_1) < Epsilon.UNKNOWN_1EN7) {
                            ir3 = ir3 + 1;
                        }
                    }
                }
            }

            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineSegment si;
                si = foldLineSet.get(i);
                if (OritaCalc.determineLineSegmentDistance(ec_0, si) < Epsilon.UNKNOWN_1EN6) {

                    if (si.getColor().getNumber() <= 2) {
                        ir4 = ir4 + 1;
                    } else if (si.getColor() == LineColor.CYAN_3) {
                        ir5 = ir5 + 1;
                    }
                }
            }

            if (ir1 > 2) {
                ir1 = 2;
            }
            if (ir2 > 2) {
                ir2 = 2;
            }
            if (ir3 > 2) {
                ir3 = 2;
            }
            if (ir4 > 2) {
                ir4 = 2;
            }
            if (ir5 > 2) {
                ir5 = 2;
            }

            if (i_mode == 0) {
                return ir1 + ir2 * 10 + ir3 * 100 + ir4 * 1000 + ir5 * 10000;
            }
            if (i_mode == 1) {
                return ir1;
            }
            if (i_mode == 2) {
                return ir2;
            }
            if (i_mode == 3) {
                return ir3;
            }
            if (i_mode == 4) {
                return ir4;
            }
            if (i_mode == 5) {
                return ir5;
            }
        }

        return 100000;
    }
}
