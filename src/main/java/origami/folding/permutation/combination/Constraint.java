package origami.folding.permutation.combination;

import origami.folding.algorithm.italiano.TraceableItalianoAlgorithm;
import origami.folding.util.EquivalenceCondition;

/**
 * Constraint classes are essentially {@link EquivalenceCondition}s with
 * additional knowledge about their possible stacking combination. These are the
 * main actors for the {@link CombinationGenerator}.
 * 
 * @author Mu-Tsun Tsai
 */
public abstract class Constraint {

    protected final TraceableItalianoAlgorithm ia;
    protected final boolean[] optionValid;
    protected int state = 0;

    public Constraint(TraceableItalianoAlgorithm ia, int options) {
        this.ia = ia;
        optionValid = new boolean[options];
    }

    /** Go the the next combination. */
    public final boolean next() {
        for (int i = state; i < optionValid.length; i++) {
            if (optionValid[i]) {
                state = i + 1;
                return true;
            }
        }
        return false;
    }

    public final void reset() {
        state = 0;
    }

    public final int getState() {
        return state;
    }

    public final boolean nextIfReset() {
        rules();
        if (state != 0 && optionValid[state - 1]) return true;
        return next();
    }

    private int optionRemain() {
        int result = 0;
        for (int i = state; i < optionValid.length; i++) {
            if (optionValid[i]) result++;
        }
        return result;
    }

    public final boolean isDeadEnd() {
        for (int i = 0; i < optionValid.length; i++) {
            if (optionValid[i]) return false;
        }
        return true;
    }

    public int findConflict(Constraint[] constraints, int depth) {
        int[][] checks = getChecks();
        int result = 0, backup = 0;
        for (int i = 0; i < checks.length; i++) {
            int h = ia.getDepth(checks[i][1], checks[i][0]);
            if (h > 0 && h < depth) {
                if (constraints[h].optionRemain() > 0) {
                    if (h > result) result = h;
                } else {
                    if (h > backup) backup = h;
                }
            }
        }
        return result == 0 ? backup : result;
    }

    public abstract void write();

    protected abstract void rules();

    protected abstract int[][] getChecks();
}
