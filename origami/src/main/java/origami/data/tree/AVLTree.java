package origami.data.tree;

/**
 * AVL tree (Adelson-Velsky and Landis Tree) is a classical data structure that
 * is a self-balancing binary search tree. It uses O(n) space and O(log n) time
 * for searching, inserting and deleting. AVL tree maintains its balance at all
 * times, making its searching faster than other BST.
 *
 * @author Mu-Tsun Tsai
 */
public class AVLTree<T> implements BST<T> {

    private Node root;

    public void insert(int key, T value) {
        root = insert(root, key, value);
    }

    private Node insert(Node n, int key, T value) {
        if (n == null) {
            return new Node(key, value);
        } else if (n.key > key) {
            n.left = insert(n.left, key, value);
        } else if (n.key < key) {
            n.right = insert(n.right, key, value);
        } else {
            n.value = value;
            return n; // no need to re-balance here.
        }
        return balance(n);
    }

    public void delete(int key) {
        root = delete(root, key);
    }

    private Node delete(Node n, int key) {
        if (n == null) {
            return null;
        } else if (n.key > key) {
            n.left = delete(n.left, key);
        } else if (n.key < key) {
            n.right = delete(n.right, key);
        } else {
            if (n.left == null) {
                return n.right;
            } else if (n.right == null) {
                return n.left;
            } else {
                Node rightMin = min(n.right);
                n.key = rightMin.key;
                n.value = rightMin.value;
                n.right = delete(n.right, n.key);
            }
        }
        return balance(n);
    }

    private Node min(Node n) {
        while (n.left != null) {
            n = n.left;
        }
        return n;
    }

    public T get(int key) {
        Node n = root;
        while (n != null) {
            if (n.key == key) {
                return n.value;
            } else if (n.key < key) {
                n = n.right;
            } else {
                n = n.left;
            }
        }
        return null;
    }

    private void updateHeight(Node n) {
        n.height = 1 + Math.max(getHeight(n.left), getHeight(n.right));
    }

    private int getHeight(Node n) {
        return n == null ? -1 : n.height;
    }

    private int getBalance(Node n) {
        return n == null ? 0 : getHeight(n.right) - getHeight(n.left);
    }

    private Node rotateRight(Node n) {
        Node x = n.left;
        n.left = x.right;
        x.right = n;
        updateHeight(n);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node n) {
        Node x = n.right;
        n.right = x.left;
        x.left = n;
        updateHeight(n);
        updateHeight(x);
        return x;
    }

    private Node balance(Node n) {
        updateHeight(n);
        int balance = getBalance(n);
        if (balance > 1) {
            if (getHeight(n.right.right) <= getHeight(n.right.left)) {
                n.right = rotateRight(n.right);
            }
            n = rotateLeft(n);
        } else if (balance < -1) {
            if (getHeight(n.left.left) <= getHeight(n.left.right)) {
                n.left = rotateLeft(n.left);
            }
            n = rotateRight(n);
        }
        return n;
    }

    class Node {
        public int key;
        public T value;
        public int height;
        public Node left;
        public Node right;

        public Node(int key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
