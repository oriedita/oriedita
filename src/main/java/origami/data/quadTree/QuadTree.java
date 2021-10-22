package origami.data.quadTree;

import java.util.*;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.adapter.*;

/**
 * Author: Mu-Tsun Tsai
 * 
 * QuadTree is a classical data structure for organizing objects in a 2D space.
 */
public class QuadTree {

    private static final double EPSILON = 0.001;
    private static final int CAPACITY = 8;

    private final Node root;
    private final QuadTreeAdapter adapter;

    /** The index of next QuadTreeItem in the list. */
    private final ArrayList<Integer> next;

    /** Which node contains the QuadTreeItem. */
    private final ArrayList<Node> map;

    private int count;

    public QuadTree(LineSegmentSet set) {
        this(new LineSegmentSetAdapter(set));
    }

    public QuadTree(QuadTreeAdapter adapter) {
        this.adapter = adapter;
        next = new ArrayList<>();
        map = new ArrayList<>();

        // Determine the root size.
        Double l = null, r = null, t = null, b = null;
        for (int i = 0; i < adapter.getPointCount(); i++) {
            Point p = adapter.getPoint(i);
            double x = p.getX(), y = p.getY();
            if (l == null || l > x) {
                l = x;
            }
            if (r == null || r < x) {
                r = x;
            }
            if (t == null || t < y) {
                t = y;
            }
            if (b == null || b > y) {
                b = y;
            }
        }

        // We enlarge the root by at least 2 * EPSILON to avoid rounding errors.
        // Also, we strategically offset the center of the root, since it is very common
        // for origami to have creases that are on exactly half of the sheet, 1/4 of the
        // sheet etc.
        root = new Node(l - 2 * EPSILON, r + 3 * EPSILON, b - 2 * EPSILON, t + 3 * EPSILON, null);

        grow(adapter.getCount());
    }

    public void grow(int num) {
        int new_count = count + num;
        for (int i = count; i < new_count; i++) {
            next.add(-1);
            map.add(null);
            root.addItem(i);
        }
        count = new_count;
    }

    /** This only returns items that are of greater index. */
    public Iterable<Integer> getPotentialCollision(int i) {
        SortedSet<Integer> set = new TreeSet<Integer>();

        // Collect all the items upwards.
        Node node = map.get(i).parent;
        while (node != null) {
            collect(node, i, set);
            node = node.parent;
        }

        collectDownwards(map.get(i), i, set);
        return set;
    }

    public Iterable<Integer> getPotentialContainer(Point p) {
        SortedSet<Integer> set = new TreeSet<Integer>();

        // Collect all the items upwards.
        Node node = findContainerNode(root, p);
        while (node != null) {
            collect(node, -1, set); // -1 means collect all
            node = node.parent;
        }
        return set;
    }

    private Node findContainerNode(Node node, Point p) {
        if (!node.contains(p)) {
            return null;
        }
        if (node.children[0] != null) {
            for (int j = 0; j < 4; j++) {
                Node n = findContainerNode(node.children[j], p);
                if (n != null) {
                    return n;
                }
            }
        }
        return node;
    }

    private void collectDownwards(Node node, int i, Set<Integer> set) {
        collect(node, i, set);
        if (node.children[0] != null) {
            for (int j = 0; j < 4; j++) {
                collectDownwards(node.children[j], i, set);
            }
        }
    }

    private void collect(Node node, int i, Set<Integer> set) {
        int cursor = node.head;
        while (cursor != -1) {
            if (cursor > i) {
                set.add(cursor);
            }
            cursor = next.get(cursor);
        }
    }

    private class Node {
        final double l, r, b, t;
        final Node[] children = new Node[4];
        final Node parent;
        int size;
        int head = -1;

        Node(double l, double r, double b, double t, Node parent) {
            this.l = l;
            this.r = r;
            this.b = b;
            this.t = t;
            this.parent = parent;
        }

        boolean addItem(int i) {
            return addItem(i, adapter.getItem(i));
        }

        boolean contains(Point p) {
            double x = p.getX(), y = p.getY();
            return x > l + EPSILON && x < r - EPSILON && y > b + EPSILON && y < t - EPSILON;
        }

        private boolean addItem(int i, QuadTreeItem item) {
            if (!containsItem(item)) {
                return false;
            }
            if (size >= CAPACITY) {
                if (children[0] == null) {
                    split();
                }
                for (int c = 0; c < 4; c++) {
                    if (children[c].addItem(i, item)) {
                        return true;
                    }
                }
            }
            addIndex(i);
            return true;
        }

        private void addIndex(int i) {
            next.set(i, head);
            map.set(i, this);
            head = i;
            size++;
        }

        private boolean containsItem(QuadTreeItem item) {
            return item.l > l + EPSILON && item.r < r - EPSILON && item.b > b + EPSILON && item.t < t - EPSILON;
        }

        private void split() {
            double w = (r - l) / 2, h = (t - b) / 2;
            children[0] = new Node(l, l + w, b, b + h, this);
            children[1] = new Node(l + w, r, b, b + h, this);
            children[2] = new Node(l, l + w, b + h, t, this);
            children[3] = new Node(l + w, r, b + h, t, this);
            int i = head;
            head = -1;
            size = 0;
            while (i != -1) {
                int n = next.get(i), c;
                QuadTreeItem item = adapter.getItem(i);
                for (c = 0; c < 4; c++) {
                    if (children[c].containsItem(item)) {
                        children[c].addIndex(i);
                        break;
                    }
                }
                if (c == 4) {
                    addIndex(i);
                }
                i = n;
            }
        }
    }
}
