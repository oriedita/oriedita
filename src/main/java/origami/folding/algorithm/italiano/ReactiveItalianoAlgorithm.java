package origami.folding.algorithm.italiano;

import origami.data.StackArray;

/**
 * Author: Mu-Tsun Tsai
 * 
 * ReactiveItalianoAlgorithm writes changes to a centralized StackArray, so that
 * changes can be react upon.
 */
public class ReactiveItalianoAlgorithm extends RestorableItalianoAlgorithm {

    private final int id;

    /** Each changed entry is represented as int32 using upper and lower bits. */
    private final StackArray changes;

    public ReactiveItalianoAlgorithm(int size, int id, StackArray changes) {
        super(size);
        this.id = id;
        this.changes = changes;
    }

    protected void meld(int x, int j, int u, int v) {
        // add to change list
        changes.add(id, (x << 16) | v);

        super.meld(x, j, u, v);
    }
}
