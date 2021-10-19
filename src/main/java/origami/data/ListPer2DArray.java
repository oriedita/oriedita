package origami.data;

import java.util.ArrayList;
import java.util.Iterator;

import origami.data.tree.*;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This is the data structure for "a list per some positions in a very large 2D
 * array". In that case, it would not be feasible to really create a large array
 * and have an ArrayList for each position. In order to balance space and speed,
 * I use BST and linked list to implement such structure. In particular, AVL
 * tree is used, since typically this structure is first filled with data and
 * then perform lots of searches.
 */
public class ListPer2DArray<T> {

    // These are all 1-based
    private final BST<BST<Integer>> heads;
    private final ArrayList<Node> nodes = new ArrayList<>();

    public ListPer2DArray(int count) {
        heads = new AVLTree<>();
        nodes.add(null);
    }

    public void add(int i, int j, T value) {
        BST<Integer> tree = heads.get(i);
        if (tree == null) {
            heads.insert(i, tree = new AVLTree<>());
        }
        Integer listHead = tree.get(j);
        if (listHead == null) {
            listHead = 0;
        }
        tree.insert(j, nodes.size());
        nodes.add(new Node(value, listHead));
    }

    public Iterable<T> get(int i, int j) {
        return () -> new Iterator<T>() {
            Node nextNode;
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
                return nextNode != null;
            }

            @Override
            public T next() {
                T result = nextNode.value;
                nextNode = nodes.get(nextNode.next);
                return result;
            }
        };
    }

    class Node {
        public T value;
        public int next;

        public Node(T value, int next) {
            this.value = value;
            this.next = next;
        }
    }
}
