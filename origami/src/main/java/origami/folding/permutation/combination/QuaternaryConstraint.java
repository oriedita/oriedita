package origami.folding.permutation.combination;

import origami.folding.HierarchyList;
import origami.folding.algorithm.italiano.TraceableItalianoAlgorithm;

public class QuaternaryConstraint extends Constraint {
    private final int a;
    private final int b;
    private final int c;
    private final int d;

    public QuaternaryConstraint(int a, int b, int c, int d, TraceableItalianoAlgorithm ia) {
        super(ia, 4);
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public void write() {
        if (state == 1) {
            ia.add(b, c);
        } else if (state == 2) {
            ia.add(a, c);
            ia.add(d, b);
        } else if (state == 3) {
            ia.add(c, a);
            ia.add(b, d);
        } else if (state == 4) {
            ia.add(d, a);
        }
    }

    @Override
    protected void rules() {
        optionValid[0] = ia.get(b, c) != HierarchyList.BELOW_0;
        optionValid[1] = ia.get(a, c) != HierarchyList.BELOW_0 && ia.get(d, b) != HierarchyList.BELOW_0;
        optionValid[2] = ia.get(c, a) != HierarchyList.BELOW_0 && ia.get(b, d) != HierarchyList.BELOW_0;
        optionValid[3] = ia.get(d, a) != HierarchyList.BELOW_0;
    }

    @Override
    protected int[][] getChecks() {
        return new int[][] { { b, c }, { a, c }, { d, b }, { c, a }, { b, d }, { d, a } };
    }
}
