package origami.folding.algorithm;

import origami.crease_pattern.worker.HierarchyList_Worker.HierarchyListStatus;
import origami.folding.HierarchyList;
import origami.folding.HierarchyList.HierarchyListCondition;
import origami.folding.element.SubFace;
import origami.folding.util.EquivalenceCondition;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This class is the result of refactoring the original additional_estimation().
 * It does basically the same thing, but greatly improves readability. It also
 * improves the performance by removing outer loops that are theoretically
 * redundant.
 */
public class AdditionalEstimationAlgorithm {

    private static final HierarchyListCondition ABOVE = HierarchyListCondition.ABOVE_1;
    private static final HierarchyListCondition BELOW = HierarchyListCondition.BELOW_0;

    private HierarchyList hierarchyList;
    private SubFace[] subFaces; // indices start from 1

    public AdditionalEstimationAlgorithm(HierarchyList hierarchyList, SubFace[] s) {
        this.hierarchyList = hierarchyList;
        this.subFaces = s;
    }

    public HierarchyListStatus run() {
        int new_relations;
        boolean found;

        System.out.println("additional_estimation start---------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        do {
            new_relations = 0;
            System.out.println("additional_estimation------------------------");

            try {
                // The outer do-while loop in the original algorithm is redundant.
                for (int iS = 1; iS < subFaces.length; iS++) {
                    int changes = checkTransitivity(subFaces[iS]);
                    new_relations += changes;
                }
            } catch (InferenceFailureException e) {
                return HierarchyListStatus.CONTRADICTED_2;
            }

            // Reset hierarchyList Make sure that it is done properly

            try {
                do {
                    found = false;
                    for (EquivalenceCondition tg : hierarchyList.getEquivalenceConditions()) {
                        int changes = checkTripleConstraint(tg);
                        found = found || changes > 0;
                        new_relations += changes;
                    }
                } while (found);
            } catch (InferenceFailureException e) {
                return HierarchyListStatus.CONTRADICTED_3;
            }

            try {
                do {
                    found = false;
                    for (EquivalenceCondition tg : hierarchyList.getUEquivalenceConditions()) {
                        int changes = checkQuadrupleConstraint(tg);
                        found = found || changes > 0;
                        new_relations += changes;
                    }
                } while (found);
            } catch (InferenceFailureException e) {
                return HierarchyListStatus.CONTRADICTED_4;
            }

            // ----------------

            System.out.print("Total number of inferred relations ＝ ");
            System.out.println(new_relations);

        } while (new_relations > 0);

        System.out.println("additional_estimation finished------------------------＊＊＊＊ここまで20150310＊＊＊＊＊＊＊＊＊＊＊");

        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    /**
     * This part of the algorithm is essentially identical to the Warshall algorithm
     * of finding the transitive closure of a digraph. Warshall algorithm guarantees
     * that the closure will be found after processing all vertices once, so there's
     * no need for the outer while loop, as in Mr.Meguro's original implementation.
     * 
     * Warshall algorithm, however, is not a suitable choice for our use case here.
     * Every time a new relation is established, it still takes O(n^3) steps to
     * construct the closure.
     */
    public int checkTransitivity(SubFace sf) throws InferenceFailureException {
        int changes = 0;
        int faceIdCount = sf.getFaceIdCount();
        for (int iM = 1; iM <= faceIdCount; iM++) {// 3面の比較で中間にくる面
            int[] upperFaceId = new int[faceIdCount + 1];// S面に含まれるあるMenの上がわにあるid番号を記録する。これが20ということは、
            int[] lowerFaceId = new int[faceIdCount + 1];// S面に含まれるあるMenの下がわにあるid番号を記録する。これが20ということは、
            int Mid = sf.getFaceId(iM); // The side that comes in the middle when comparing the three sides

            // Thinking: Think about a certain side Mid of a certain SubFace.
            // Other than this SubFace, it is assumed that surface A is above the surface
            // Mid and surface B is below the surface Mid.
            // Generally, in separate SubFace, surface A cannot be determined to be above
            // surface B just because surface A is above surface Mid and surface B is below
            // surface Mid.
            // However, this is the point, but if there is a SubFace that includes surface
            // A, surface Mid, and surface B together, even if you do not know the
            // hierarchical relationship of that SubFace
            // Surface A is above surface B. So, the information we get from SubFace in this
            // operation is whether there are three sides together.
            // There is no need for a hierarchical relationship within a SubFace.
            // //
            // The operation here is collecting the hierarchical relationship of a certain
            // SubFace from the upper and lower tables.
            for (int i = 1; i <= faceIdCount; i++) {// faceId[iM]より上にある面。
                if (iM != i) {
                    int id = sf.getFaceId(i);
                    if (hierarchyList.get(Mid, id) == BELOW) {
                        upperFaceId[++upperFaceId[0]] = id;
                    }
                    if (hierarchyList.get(Mid, id) == ABOVE) {
                        lowerFaceId[++lowerFaceId[0]] = id;
                    }
                }
            }

            for (int u = 1; u <= upperFaceId[0]; u++) {// faceId[iM]より上にある面。
                for (int l = 1; l <= lowerFaceId[0]; l++) {// faceId[iM]より下にある面。
                    changes += tryInferAbove(upperFaceId[u], lowerFaceId[l]);
                }
            }
        }
        return changes;
    }

    public int checkTripleConstraint(EquivalenceCondition ec) throws InferenceFailureException {
        int changes = 0;
        int a = ec.getA(), b = ec.getB(), d = ec.getD();
        if (hierarchyList.get(a, b) == ABOVE) {
            changes += tryInferAbove(a, d);
        } else if (hierarchyList.get(a, b) == BELOW) {
            changes += tryInferAbove(d, a);
        }
        if (hierarchyList.get(a, d) == ABOVE) {
            changes += tryInferAbove(a, b);
        } else if (hierarchyList.get(a, d) == BELOW) {
            changes += tryInferAbove(b, a);
        }
        return changes;
    }

    public int checkQuadrupleConstraint(EquivalenceCondition ec) throws InferenceFailureException {
        int changes = 0;
        int a = ec.getA(), b = ec.getB(), c = ec.getC(), d = ec.getD();

        // If only a> b> c, the position of d cannot be determined

        // a> c && b> d then a> d && b> c
        if (hierarchyList.get(a, c) == ABOVE && hierarchyList.get(b, d) == ABOVE) {
            changes += tryInferAbove(a, d) + tryInferAbove(b, c);
        }
        // If a> d && b> c then a> c && b> d
        if (hierarchyList.get(a, d) == ABOVE && hierarchyList.get(b, c) == ABOVE) {
            changes += tryInferAbove(a, c) + tryInferAbove(b, d);
        }
        // If a <c && b <d, then a <d && b <c
        if (hierarchyList.get(a, c) == BELOW && hierarchyList.get(b, d) == BELOW) {
            changes += tryInferAbove(d, a) + tryInferAbove(c, b);
        }
        // If a <d && b <c then a <c && b <d
        if (hierarchyList.get(a, d) == BELOW && hierarchyList.get(b, c) == BELOW) {
            changes += tryInferAbove(c, a) + tryInferAbove(d, b);
        }

        /////////////////////////

        // If a> c> b, then a> d> b
        if (hierarchyList.get(a, c) == ABOVE && hierarchyList.get(c, b) == ABOVE) {
            // Noticed that we don't need to infer a > b here, since that part will be done
            // in the transitivity check anyway. The same is true for the rest.
            changes += tryInferAbove(a, d) + tryInferAbove(d, b);
        }
        // a>d>b なら a>c>b
        if (hierarchyList.get(a, d) == ABOVE && hierarchyList.get(d, b) == ABOVE) {
            changes += tryInferAbove(a, c) + tryInferAbove(c, b);
        }
        // b>c>a なら b>d>a
        if (hierarchyList.get(b, c) == ABOVE && hierarchyList.get(c, a) == ABOVE) {
            changes += tryInferAbove(b, d) + tryInferAbove(d, a);
        }
        // b>d>a なら b>c>a
        if (hierarchyList.get(b, d) == ABOVE && hierarchyList.get(d, a) == ABOVE) {
            changes += tryInferAbove(b, c) + tryInferAbove(c, a);
        }
        // c>a>d なら c>b>d
        if (hierarchyList.get(c, a) == ABOVE && hierarchyList.get(a, d) == ABOVE) {
            changes += tryInferAbove(c, b) + tryInferAbove(b, d);
        }
        // c>b>d なら c>a>d
        if (hierarchyList.get(c, b) == ABOVE && hierarchyList.get(b, d) == ABOVE) {
            changes += tryInferAbove(c, a) + tryInferAbove(a, d);
        }
        // d>a>c なら d>b>c
        if (hierarchyList.get(d, a) == ABOVE && hierarchyList.get(a, c) == ABOVE) {
            changes += tryInferAbove(d, b) + tryInferAbove(b, c);
        }
        // d>b>c なら d>a>c
        if (hierarchyList.get(d, b) == ABOVE && hierarchyList.get(b, c) == ABOVE) {
            changes += tryInferAbove(d, a) + tryInferAbove(a, c);
        }

        return changes;
    }

    /** Make inference that i > j. */
    public int tryInferAbove(int i, int j) throws InferenceFailureException {
        int changes = 0;
        if (hierarchyList.get(i, j) == BELOW || hierarchyList.get(j, i) == ABOVE) {
            throw new InferenceFailureException();
        }
        if (hierarchyList.get(i, j).isEmpty()) {
            hierarchyList.set(i, j, ABOVE);
            changes++;
        }
        if (hierarchyList.get(j, i).isEmpty()) {
            hierarchyList.set(j, i, BELOW);
            changes++;
        }
        return changes;
    }

    public static class InferenceFailureException extends Exception {
    }
}
