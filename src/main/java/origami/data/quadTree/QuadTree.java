package origami.data.quadTree;

import java.util.*;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.adapter.QuadTreeAdapter;
import origami.data.quadTree.collector.*;
import origami.data.quadTree.comparator.QuadTreeComparator;
import origami.data.quadTree.comparator.ShrinkComparator;

/**
 * QuadTree is a classical data structure for organizing objects in a 2D space.
 * 
 * @author Mu-Tsun Tsai
 */
public class QuadTree {

    private static final int CAPACITY = 8;

    private final Node root;
    private final QuadTreeAdapter adapter;
    private final QuadTreeComparator comparator;

    /** The index of next QuadTreeItem in the list. */
    private final ArrayList<Integer> next;

    /** Which node contains the QuadTreeItem. */
    private final ArrayList<Node> map;

    private final int offset;
    private int count;

    public QuadTree(QuadTreeAdapter adapter) {
        this(adapter, ShrinkComparator.instance);
    }

    public QuadTree(QuadTreeAdapter adapter, QuadTreeComparator comparator) {
        this.adapter = adapter;
        this.offset = adapter.getOffset();
        this.comparator = comparator;
        next = new ArrayList<>();
        map = new ArrayList<>();

        // Determine the root size.
        Point p = adapter.getPoint(0);
        double l = p.getX(), r = l, t = p.getY(), b = t;
        for (int i = 1; i < adapter.getPointCount(); i++) {
            p = adapter.getPoint(i);
            double x = p.getX(), y = p.getY();
            if (l > x) l = x;
            if (r < x) r = x;
            if (t < y) t = y;
            if (b > y) b = y;
        }

        root = new Node(comparator.createRoot(l, r, b, t));
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

    public void update(int i) {
        Node n = map.get(i);
        int old_next = next.get(i);
        if (n.children[0] != null) {
            for (int j = 0; j < 4; j++) {
                if (n.children[j].addItem(i)) {
                    n.removeIndex(i, old_next);
                    return;
                }
            }
        }
    }

    /** This only returns items that are of greater index. */
    public Iterable<Integer> getPotentialCollision(int i) {
        return getPotentialCollision(i, i);
    }

    public Iterable<Integer> getPotentialCollision(int i, int min) {
        return collect(new CollisionCollector(i - offset, min - offset, map));
    }

    public Iterable<Integer> collect(QuadTreeCollector collector) {
        StaticMinHeap heap = new StaticMinHeap(count);
        Node node = collector.findInitial(root);
        if (collector.shouldGoDown()) {
            collectDownwards(node, collector, heap);
            node = node.parent;
        }
        while (node != null) {
            collectNode(node, collector, heap);
            node = node.parent;
        }
        return heap;
    }

    private void collectDownwards(Node node, QuadTreeCollector collector, StaticMinHeap heap) {
        collectNode(node, collector, heap);
        if (node.children[0] != null) {
            for (int j = 0; j < 4; j++) {
                collectDownwards(node.children[j], collector, heap);
            }
        }
    }

    private void collectNode(Node node, QuadTreeCollector collector, StaticMinHeap heap) {
        int cursor = node.head;
        while (cursor != -1) {
            if (collector.shouldCollect(cursor, adapter)) {
                heap.add(cursor + offset);
            }
            cursor = next.get(cursor);
        }
    }

    public class Node {
        public final double l, r, b, t;
        public final Node[] children = new Node[4];
        final Node parent;
        int size;
        int head = -1;

        Node(QuadTreeItem item) {
            this(item.l, item.r, item.b, item.t, null);
        }

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

        public boolean contains(Point p) {
            double x = p.getX(), y = p.getY();
            return comparator.contains(this, x, x, y, y);
        }

        public boolean contains(QuadTreeItem item) {
            return comparator.contains(this, item.l, item.r, item.b, item.t);
        }

        private boolean addItem(int i, QuadTreeItem item) {
            if (!contains(item)) {
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

        void removeIndex(int i, int old_next) {
            size--;
            int cursor = head;
            if (cursor == i) {
                head = old_next;
            } else {
                while (true) {
                    int n = next.get(cursor);
                    if (n == i) {
                        next.set(cursor, old_next);
                        return;
                    }
                    cursor = n;
                }
            }
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
                    if (children[c].contains(item)) {
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
