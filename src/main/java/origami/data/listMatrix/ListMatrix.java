package origami.data.listMatrix;

import java.util.ArrayList;
import java.util.Iterator;

import origami.data.tree.*;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This is the data structure for "a list of int per some positions in a very
 * large matrix". In that case, it would not be feasible to really create a
 * large 2D array (or a HashMap using a pair of keys) and have an ArrayList (or
 * a native LinkedList) for each position. In order to balance space and speed,
 * I use BST and customized linked list to implement such structure. In
 * particular, AVL tree is used, since typically this structure is first filled
 * with data and then perform lots of searches.
 */
public class ListMatrix {

    private static final long mask = (1L << 32) - 1;

    // These are all 1-based
    private final BST<BST<Integer>> heads;
    private final ArrayList<Long> nodes = new ArrayList<>();

    public ListMatrix(int count) {
        heads = new AVLTree<>();
        nodes.add(0L);
    }

    public void add(int i, int j, int value) {
        BST<Integer> tree = heads.get(i);
        if (tree == null) {
            heads.insert(i, tree = new AVLTree<>());
        }
        Integer listHead = tree.get(j);
        if (listHead == null) {
            listHead = 0;
        }
        tree.insert(j, nodes.size());
        nodes.add(((long) listHead << 32) | (long) value);
    }

    public Iterable<Integer> get(int i, int j) {
        return () -> new Iterator<Integer>() {
            long nextNode;
            {
                BST<Integer> tree = heads.get(i);
                if (tree != null) {
                    Integer head = tree.get(j);
                    if (head != null) {
                        nextNode = nodes.get(head);
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return nextNode != 0;
            }

            @Override
            public Integer next() {
                int result = (int) (nextNode & mask);
                nextNode = nodes.get((int) (nextNode >>> 32));
                return result;
            }
        };
    }
}
