package origami.crease_pattern.worker.foldlineset;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

public class Fix1 {
    public static boolean apply(FoldLineSet foldLineSet) {
        foldLineSet.unselect_all();
        for (int i = 1; i <= foldLineSet.getTotal() - 1; i++) {
            LineSegment si = foldLineSet.get(i);
            if (si.getColor() != LineColor.CYAN_3) {
                for (int j = i + 1; j <= foldLineSet.getTotal(); j++) {
                    LineSegment sj = foldLineSet.get(j);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                    if (sj.getColor() != LineColor.CYAN_3) {
                        //T字型交差
                        LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersection(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL_FOR_FIX);
                        switch (intersection) {
                            case PARALLEL_EQUAL_31:
                                si.setColor(sj.getColor());
                                foldLineSet.deleteLine(j);
                                return true;
                            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321:
                            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322:
                            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331:
                            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332:
                            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341:
                            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342:
                            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351:
                            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352:
                                si.setSelected(2);
                                sj.setSelected(2);
                                break;
                            default:
                                break;
                        }
                        if (intersection.isContainedInside()) {
                            si.setSelected(2);
                            sj.setSelected(2);
                        }
                    }
                }
            }
        }
        return false;
    }
}
