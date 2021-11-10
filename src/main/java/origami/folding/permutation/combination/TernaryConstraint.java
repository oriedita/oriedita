package origami.folding.permutation.combination;

import origami.folding.HierarchyList;
import origami.folding.algorithm.italiano.TraceableItalianoAlgorithm;

/**
 * TernaryConstraint only have two possible combinations: a-b-d, or b-d-a. Here
 * we assume that b-d are sorted.
 */
public class TernaryConstraint extends Constraint {

    private final int a;
    private final int b;
    private final int d;

    public TernaryConstraint(int a, int b, int d, TraceableItalianoAlgorithm ia) {
        super(ia, 2);
        this.a = a;
        this.b = b;
        this.d = d;
    }

    @Override
    public void write() {
        if (state == 1) ia.add(a, b);
        else if (state == 2) ia.add(d, a);
    }

    @Override
    protected void rules() {
        optionValid[0] = ia.get(a, b) != HierarchyList.BELOW_0;
        optionValid[1] = ia.get(d, a) != HierarchyList.BELOW_0;
    }

    @Override
    protected int[][] getChecks()  {
        return new int[][] { { a, b }, { d, a } };
    }
}
