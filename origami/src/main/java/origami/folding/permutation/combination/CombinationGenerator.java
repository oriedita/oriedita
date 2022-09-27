package origami.folding.permutation.combination;

import origami.folding.HierarchyList;
import origami.folding.algorithm.InferenceFailureException;
import origami.folding.algorithm.italiano.ReductionItalianoAlgorithm;
import origami.folding.algorithm.swapping.SwappingAlgorithm;
import origami.folding.element.SubFace;
import origami.folding.permutation.ChainPermutationGenerator;
import origami.folding.permutation.PermutationGenerator;
import origami.folding.util.EquivalenceCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * CombinationGenerator is the latest boost to the search performance. It solves
 * the problem known as "excess permutation", where the
 * {@link ChainPermutationGenerator} has way too many possible permutations left
 * even after applying the transitivity guides, but almost all of them would not
 * pass the {@link EquivalenceCondition} checks. The idea of this class is to
 * focus on the {@link EquivalenceCondition}s first, converting them into
 * {@link Constraint}s, and search for a valid combination for them using also
 * the {@link SwappingAlgorithm}.
 *
 * @author Mu-Tsun Tsai
 */
public class CombinationGenerator {

    private final Constraint[] constraints;
    private final ReductionItalianoAlgorithm ia;
    private final SwappingAlgorithm<Constraint> swapper = new SwappingAlgorithm<>();
    private final int faceIdCount;

    // This variable can be replaced by boolean flag, but using int will also help
    // debugging.
    private int count = 0;

    public CombinationGenerator(SubFace s, int[] faceIdMapArray, HierarchyList hierarchyList) throws InferenceFailureException {
        faceIdCount = s.getFaceIdCount();
        ia = new ReductionItalianoAlgorithm(faceIdCount);
        for (int i = 1; i <= faceIdCount; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                int state = hierarchyList.get(s.getFaceId(i), s.getFaceId(j));
                if (state == HierarchyList.ABOVE_1) {
                    if (!ia.tryAdd(i, j)) throw new InferenceFailureException(i, j);
                } else if (state == HierarchyList.BELOW_0) {
                    if (!ia.tryAdd(j, i)) throw new InferenceFailureException(i, j);
                }
            }
        }
        ia.save();

        // Gather 3EC
        List<Constraint> constraints = new ArrayList<>();
        for (EquivalenceCondition ec : s.getEquivalenceConditions()) {
            int a = faceIdMapArray[ec.getA()];
            int b = faceIdMapArray[ec.getB()];
            int d = faceIdMapArray[ec.getD()];
            constraints.add(new TernaryConstraint(a, b, d, ia));
        }

        // Gather 4EC
        for (EquivalenceCondition ec : s.getUEquivalenceConditions()) {
            int a = faceIdMapArray[ec.getA()];
            int b = faceIdMapArray[ec.getB()];
            int c = faceIdMapArray[ec.getC()];
            int d = faceIdMapArray[ec.getD()];
            constraints.add(new QuaternaryConstraint(a, b, c, d, ia));
        }

        this.constraints = new Constraint[constraints.size() + 1];
        for (int i = 0; i < constraints.size(); i++) {
            this.constraints[i + 1] = constraints.get(i);
        }
    }

    public boolean process() throws InterruptedException {
        // Perform swapping only for finding the first combination; after that the
        // sequence needs to be fixed, or we will get the same combination again and
        // again.
        boolean swap = count == 0;
        int deepest = 0;

        if (count != 0 && !backtrack(constraints.length)) return false;
        count++;
        while (true) {
            int depth = 0;
            boolean deadEnd = false;
            ia.restore();
            for (int i = 1; i < constraints.length; i++) {
                if (i > deepest) deepest = i;
                ia.setDepth(i);
                if (!constraints[i].nextIfReset()) {
                    depth = i;
                    if (swap) swapper.record(i);
                    else deadEnd = constraints[i].isDeadEnd();
                    break;
                }
                constraints[i].write();
            }
            if (depth == 0) return true;

            // Make sure to reset to the deepest depth.
            for (int i = depth; i <= deepest; i++) constraints[i].reset();
            deepest = depth;

            if (!deadEnd && !backtrack(depth)) return false;

            if (swap) {
                swapper.process(constraints, constraints.length - 1);
            } else if (deadEnd) {
                // In case of dead-end, we can speed up the backtracking by going to the last
                // constraint that created conflict with the current constraint.
                int c = findConflict(depth);
                if (!backtrack(c + 1)) return false;
            }

            // After the first combination, sometimes it may take a while to find the next
            // one since we're no longer swapping.
            if (Thread.interrupted()) throw new InterruptedException();
        }
    }

    private int findConflict(int depth) {
        int result = constraints[depth].findConflict(constraints, depth);
        for (int i = result + 1; i <= depth; i++) constraints[i].reset();
        return result;
    }

    private boolean backtrack(int depth) {
        for (int i = depth - 1; i > 0; i--) {
            if (constraints[i].next()) return true;
            constraints[i].reset();
        }
        return false;
    }

    /**
     * Add the transitivity reduction to {@link PermutationGenerator}, and also
     * check and returns the first violation of its current solution.
     */
    public int addGuideAndCheck(PermutationGenerator pg) {
        int min = faceIdCount + 1;
        for (int entry : ia.getReduction()) {
            int upper = entry >>> 16;
            int lower = entry & 0xFFFF;
            if (pg.locate(upper) > pg.locate(lower)) {
                min = Math.min(min, pg.locate(lower));
            }
            pg.addGuide(upper, lower);
        }
        return min > faceIdCount ? 0 : min;
    }
}
