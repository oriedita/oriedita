package origami.crease_pattern.worker.foldlineset;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

public class Check2 {
    public static void apply(FoldLineSet foldLineSet) {
        foldLineSet.getCheck2LineSegment().clear();

        foldLineSet.unselect_all();
        for (int i = 1; i <= foldLineSet.getTotal() - 1; i++) {
            LineSegment si = foldLineSet.get(i);
            if (si.getColor() != LineColor.CYAN_3) {

                for (int j = i + 1; j <= foldLineSet.getTotal(); j++) {
                    LineSegment sj = foldLineSet.get(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                    if (sj.getColor() != LineColor.CYAN_3) {

                        LineSegment si1 = new LineSegment(si);
                        LineSegment sj1 = new LineSegment(sj);

                        //T-shaped intersection
                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersectionSweet(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL);
                        switch (intersection) {
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
                            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
                            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                                foldLineSet.getCheck2LineSegment().add(si1);
                                foldLineSet.getCheck2LineSegment().add(sj1);   /* set_select(i,2);set_select(j,2); */
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }
}
