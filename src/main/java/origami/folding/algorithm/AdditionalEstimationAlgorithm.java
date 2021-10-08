package origami.folding.algorithm;

import origami.crease_pattern.worker.HierarchyList_Worker.HierarchyListStatus;
import origami.folding.HierarchyList;
import origami.folding.element.SubFace;
import origami.folding.util.EquivalenceCondition;

import java.util.*;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This class is the result of refactoring the original additional_estimation().
 * It does basically the same thing, but greatly improves readability. It also
 * improves the performance by removing outer loops that are theoretically
 * redundant.
 */
public class AdditionalEstimationAlgorithm {

    private static final int ABOVE = HierarchyList.ABOVE_1;
    private static final int BELOW = HierarchyList.BELOW_0;

    private HierarchyList hierarchyList;
    private SubFace[] subFaces; // indices start from 1

    private ItalianoAlgorithm[] IA;
    private Map<Integer, List<Integer>> relationObservers;

    /**
     * Decides whether to use linear search to notify ItalianoAlgorithm to update,
     * or use observer pattern. The latter is faster, but requires more memory and
     * is not suitable for large CP.
     */
    private boolean linearMode = true;

    public AdditionalEstimationAlgorithm(HierarchyList hierarchyList, SubFace[] s) {
        this.hierarchyList = hierarchyList;
        this.subFaces = s;
        IA = new ItalianoAlgorithm[subFaces.length];

        // Decide whether to use linear mode or not
        int count = hierarchyList.getFacesTotal();
        if (count < 5000) {
            linearMode = false;
            relationObservers = new HashMap<>(count * count / 20); // more or less a fair guess
        }
    }

    public HierarchyListStatus run() {
        int new_relations;
        boolean found;

        System.out.println("additional_estimation start---------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        initializeItalianoAlgorithm();

        do {
            new_relations = 0;
            System.out.println("additional_estimation------------------------");

            try {
                // The outer do-while loop in the original algorithm is redundant.
                for (int iS = 1; iS < subFaces.length; iS++) {
                    int changes = checkTransitivity(subFaces[iS], IA[iS]);
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

    private void initializeItalianoAlgorithm() {
        for (int s = 1; s < subFaces.length; s++) {
            IA[s] = new ItalianoAlgorithm(subFaces[s]);
            int count = subFaces[s].getFaceIdCount();
            for (int i = 1; i <= count; i++) {
                for (int j = 1; j <= count; j++) {
                    int I = subFaces[s].getFaceId(i);
                    int J = subFaces[s].getFaceId(j);
                    if (!linearMode && hierarchyList.isEmpty(I, J)) {
                        // Observing potential changes to the relation
                        int pos = (I << 16) | J;
                        List<Integer> list = relationObservers.get(pos);
                        if (list == null) {
                            relationObservers.put(pos, list = new ArrayList<>());
                        }
                        list.add(s);
                    } else if (hierarchyList.get(I, J) == ABOVE) {
                        IA[s].add(i, j);
                    }
                }
            }
        }
    }

    /**
     * Originally Mr.Meguro implemented this part using what is essentially the
     * Warshall algorithm, but it is an offline algorithm that is not suitable for
     * the dynamic use case here. I re-implemented it using the Italiano algorithm,
     * which is way faster here.
     */
    public int checkTransitivity(SubFace sf, ItalianoAlgorithm ia) throws InferenceFailureException {
        int changes = 0;
        for (int n : ia.flush()) {
            changes += tryInferAbove(sf.getFaceId(n >>> 16), sf.getFaceId(n & ItalianoAlgorithm.mask));
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
        if (hierarchyList.get(i, j) == BELOW) {
            throw new InferenceFailureException();
        }
        if (hierarchyList.isEmpty(i, j)) {
            hierarchyList.set(i, j, ABOVE);
            changes++;

            // Notifying the ItalianoAlgorithm to update.
            if (linearMode) {
                for (int s = 1; s < subFaces.length; s++) {
                    int I = subFaces[s].FaceIdIndex(i);
                    int J = subFaces[s].FaceIdIndex(j);
                    if (I != 0 && J != 0) {
                        IA[s].add(I, J);
                    }
                }
            } else {
                int pos = (i << 16) | j;
                List<Integer> list = relationObservers.get(pos);
                if (list != null) {
                    for (int s : list) {
                        IA[s].addId(i, j);
                    }

                    // After that it is safe to remove the list.
                    list.clear();
                    relationObservers.remove(pos);
                }
            }

        }
        return changes;
    }

    private static class InferenceFailureException extends Exception {
    }
}
