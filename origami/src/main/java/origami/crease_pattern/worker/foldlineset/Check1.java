package origami.crease_pattern.worker.foldlineset;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

public class Check1 {
    public static void apply(FoldLineSet foldLineSet) {
        foldLineSet.getCheck1LineSegment().clear();
        foldLineSet.unselect_all();
        for (int i = 1; i <= foldLineSet.getTotal() - 1; i++) {
            LineSegment si = foldLineSet.get(i);

            if (si.getColor() != LineColor.CYAN_3) {

                for (int j = i + 1; j <= foldLineSet.getTotal(); j++) {
                    LineSegment sj = foldLineSet.get(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                    if (sj.getColor() != LineColor.CYAN_3) {

                        LineSegment si1 = new LineSegment();
                        si1.set(si);
                        LineSegment sj1 = new LineSegment();
                        sj1.set(sj);

                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersection(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL);
                        switch (intersection) {
                            case PARALLEL_EQUAL_31:
                            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321:
                            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322:
                            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331:
                            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332:
                            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341:
                            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342:
                            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351:
                            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352:
                                foldLineSet.getCheck1LineSegment().add(si1);
                                foldLineSet.getCheck1LineSegment().add(sj1);   /* set_select(i,2);set_select(j,2); */
                                break;
                            default:
                                break;
                        }
                        if (intersection.isContainedInside()) {
                            foldLineSet.getCheck1LineSegment().add(si1);
                            foldLineSet.getCheck1LineSegment().add(sj1);   /* set_select(i,2);set_select(j,2); */
                        }

                    }
                }
            }
        }
    }
}
