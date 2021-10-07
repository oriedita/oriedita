package origami.folding.algorithm;

import java.util.*;

import origami.folding.element.SubFace;

/**
 * This is the transitive closure algorithm described by G. F. Italiano. See
 * http://dx.doi.org/10.1016/0304-3975%2886%2990098-8.
 */
public class ItalianoAlgorithm {

    int[] idMap;
    int size;

    /**
     * Node[i][j] is the node of j on the spanning tree of i, of which existence
     * implies that i > j. Node[i][i] is the root of the spanning tree of i.
     */
    Node[][] matrix;

    /**
     * Prevents heap memory overflow. Still, for large projects, it is necessary to
     * allocate larger memory for JVM in the first place.
     */
    private Queue<MeldRequest> queue = new LinkedList<>();

    public List<Node> changes = new ArrayList<Node>();

    public ItalianoAlgorithm(SubFace sf) {
        int size = sf.getFaceIdCount();
        this.size = size;
        this.matrix = new Node[size + 1][size + 1];
        int max = 0;
        for (int i = 1; i <= size; i++) {
            matrix[i][i] = new Node(i, i);
            if (max < sf.getFaceId(i)) {
                max = sf.getFaceId(i);
            }
        }
        this.idMap = new int[max + 1];
        for (int i = 1; i <= size; i++) {
            idMap[sf.getFaceId(i)] = i;
        }
    }

    public void addId(int i, int j) {
        add(idMap[i], idMap[j]);
    }

    public void add(int i, int j) {
        if (matrix[i][j] == null) {
            for (int x = 1; x <= size; x++) {
                if (matrix[x][i] != null && matrix[x][j] == null) {
                    queue.add(new MeldRequest(x, j, i, j));
                }
            }
            while (queue.size() > 0) {
                MeldRequest r = queue.poll();
                meld(r.x, r.j, r.u, r.v);
            }
        }
    }

    public Iterable<Node> flush() {
        Iterable<Node> result = changes;
        changes = new ArrayList<Node>();
        return result;
    }

    private void meld(int x, int j, int u, int v) {
        Node node = new Node(x, v);
        matrix[x][v] = node;
        matrix[x][u].children.add(node);
        changes.add(node);
        for (Node w : matrix[j][v].children) {
            if (matrix[x][w.j] == null) {
                queue.add(new MeldRequest(x, j, v, w.j));
            }
        }
    }

    public class MeldRequest {
        public int x, j, u, v;

        public MeldRequest(int x, int j, int u, int v) {
            this.x = x;
            this.j = j;
            this.u = u;
            this.v = v;
        }
    }

    public class Node {
        public int i, j;
        public List<Node> children = new ArrayList<Node>();

        public Node(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }
}
