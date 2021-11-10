package origami.folding.algorithm.italiano;

import java.util.*;

import origami.folding.HierarchyList;

/**
 * TraceableItalianoAlgorithm keeps a record of the depths where each change is
 * made.
 * 
 * @author Mu-Tsun Tsai
 */
public class TraceableItalianoAlgorithm extends RestorableItalianoAlgorithm {

    private final Map<Integer, Integer> history = new HashMap<>();
    private int depth;

    public TraceableItalianoAlgorithm(int size) {
        super(size);
    }

    public void setDepth(int d) {
        depth = d;
    }

    public int getDepth(int a, int b) {
        return history.getOrDefault((a << 16) | b, 0);
    }

    public int get(int i, int j) {
        if (matrix[i][j] != 0) return HierarchyList.ABOVE_1;
        else if (matrix[j][i] != 0) return HierarchyList.BELOW_0;
        else return HierarchyList.UNKNOWN_N50;
    }

    @Override
    public void restore() {
        history.clear();
        depth = 0;
        super.restore();
    }

    @Override
    protected void meld(int x, int j, int u, int v) {
        history.put((x << 16) | v, depth);
        super.meld(x, j, u, v);
    }
}
