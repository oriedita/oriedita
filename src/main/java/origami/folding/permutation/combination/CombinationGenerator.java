package origami.folding.permutation.combination;

import java.util.*;

import origami.folding.HierarchyList;
import origami.folding.algorithm.italiano.ReductionItalianoAlgorithm;
import origami.folding.algorithm.swapping.SwappingAlgorithm;
import origami.folding.element.SubFace;
import origami.folding.permutation.PermutationGenerator;
import origami.folding.util.EquivalenceCondition;

public class CombinationGenerator {

    private final Constraint[] constraints;
    private final ReductionItalianoAlgorithm ia;
    private final SwappingAlgorithm<Constraint> swapper = new SwappingAlgorithm<>();

    private boolean firstRound = true;

    public CombinationGenerator(SubFace s, int[] faceIdMapArray, HierarchyList hierarchyList) {
        int faceIdCount = s.getFaceIdCount();
        ia = new ReductionItalianoAlgorithm(faceIdCount);
        for (int i = 1; i <= faceIdCount; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                int state = hierarchyList.get(s.getFaceId(i), s.getFaceId(j));
                if (state == HierarchyList.ABOVE_1) ia.add(i, j);
                else if (state == HierarchyList.BELOW_0) ia.add(j, i);
            }
        }
        ia.save();

        // Gather 3EC
        List<Constraint> constraints = new ArrayList<>();
        for (EquivalenceCondition ec : s.getEquivalenceConditions()) {
            int a = faceIdMapArray[ec.getA()];
            int b = faceIdMapArray[ec.getB()];
            int d = faceIdMapArray[ec.getD()];
            if (ia.get(b, d) == HierarchyList.BELOW_0) {
                int temp = b;
                b = d;
                d = temp;
            }
            constraints.add(new TernaryConstraint(a, b, d, ia));
        }

        // Gather 4EC
        for (EquivalenceCondition ec : s.getUEquivalenceConditions()) {
            int a = faceIdMapArray[ec.getA()];
            int b = faceIdMapArray[ec.getB()];
            int c = faceIdMapArray[ec.getC()];
            int d = faceIdMapArray[ec.getD()];
            if (ia.get(a, b) == HierarchyList.BELOW_0) {
                int temp = a;
                a = b;
                b = temp;
            }
            if (ia.get(c, d) == HierarchyList.BELOW_0) {
                int temp = c;
                c = d;
                d = temp;
            }
            constraints.add(new QuaternaryConstraint(a, b, c, d, ia));
        }

        this.constraints = new Constraint[constraints.size() + 1];
        for (int i = 0; i < constraints.size(); i++) {
            this.constraints[i + 1] = constraints.get(i);
        }
    }

    public boolean process() throws InterruptedException {
        // Perform swapping only for finding the first solution; after that the sequence
        // needs to be fixed, or we will get the same solution again and again.
        boolean swap = firstRound;

        if (!firstRound && !backtrack(constraints.length)) return false;
        firstRound = false;
        while (true) {
            int depth = 0;
            boolean deadEnd = false;
            ia.restore();
            for (int i = 1; i < constraints.length; i++) {
                ia.setDepth(i);
                if (!constraints[i].nextIfReset()) {
                    depth = i;
                    if (swap) swapper.record(i);
                    else deadEnd = constraints[i].isDeadEnd();
                    break;
                }
                constraints[i].write();
            }
            if (depth == 0) {
                return true;
            }
            constraints[depth].reset();
            if (!deadEnd && !backtrack(depth)) {
                return false;
            }
            if (swap) {
                swapper.process(constraints, constraints.length - 1);
            } else if (deadEnd) {
                int c = findConflict(depth);
                if (!backtrack(c + 1)) return false;
            }

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

    public void addGuide(PermutationGenerator pg) {
        for (int entry : ia.getReduction()) {
            pg.addGuide(entry >>> 16, entry & 0xFFFF);
        }
    }
}
