package origami.folding.algorithm;

import java.util.*;

import origami.folding.element.SubFace;

/**
 * This is the transitive closure algorithm described by G. F. Italiano. See
 * http://dx.doi.org/10.1016/0304-3975%2886%2990098-8.
 */
public class ItalianoAlgorithm {

    public static final int mask = (1 << 16) - 1;

    private SubFace sf;
    private int size;

    /**
     * matrix[i][j] is the node of j on the spanning tree of i, of which existence
     * implies that i > j. matrix[i][i] is the root of the spanning tree of i.
     */
    private Node[][] matrix; // 1-based

    /**
     * Prevents heap memory overflow. Still, for large projects, it is necessary to
     * allocate larger memory for JVM in the first place. Accordingly, ArrayDeque is
     * more efficient than other classes such as ArrayList, LinkedList, etc. We use
     * int64 to store 4 int16 parameters.
     */
    private ArrayDeque<Long> stack = new ArrayDeque<>();

    /** Each changed entry is represented as int32 using upper and lower bits. */
    public ArrayDeque<Integer> changes = new ArrayDeque<Integer>();

    public ItalianoAlgorithm(SubFace sf) {
        this.sf = sf;
        int size = sf.getFaceIdCount();
        this.size = size;
        this.matrix = new Node[size + 1][size + 1];
        for (int i = 1; i <= size; i++) {
            matrix[i][i] = new Node();
        }
    }

    public void addId(int i, int j) {
        add(sf.FaceIdIndex(i), sf.FaceIdIndex(j));
    }

    public void add(int i, int j) {
        if (matrix[i][j] == null) {
            for (int x = 1; x <= size; x++) {
                if (matrix[x][i] != null && matrix[x][j] == null) {
                    stackMeld(x, j, i, j);
                }
            }
            while (stack.size() > 0) {
                long r = stack.pop();
                meld((int) (r >>> 48), (int) ((r >>> 32) & mask), (int) ((r >>> 16) & mask), (int) (r & mask));
            }
        }
    }

    public Iterable<Integer> flush() {
        Iterable<Integer> result = changes;
        changes = new ArrayDeque<Integer>();
        return result;
    }

    private void meld(int x, int j, int u, int v) {
        // create new node
        Node node = new Node();
        matrix[x][v] = node;

        // add node as child of matrix[x][u]
        node.nextSibling = matrix[x][u].firstChild;
        matrix[x][u].firstChild = v;

        // add to change list
        changes.add((x << 16) | v);

        // copy subtree
        int w = matrix[j][v].firstChild;
        while (w != 0) {
            if (matrix[x][w] == null) {
                stackMeld(x, j, v, w);
            }
            w = matrix[j][w].nextSibling;
        }
    }

    private void stackMeld(long x, long j, long u, long v) {
        stack.add(x << 48 | j << 32 | u << 16 | v);
    }

    public class Node {
        // We only need these two to represent arbitrary tree!
        public int firstChild;
        public int nextSibling;
    }
}
