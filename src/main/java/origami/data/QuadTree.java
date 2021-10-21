package origami.data;

import java.util.*;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;

public class QuadTree {

    private static final double EPSILON = 0.001;
    private static final int CAPACITY = 8;

    private final Node root;
    private final LineSegmentSet set;

    /** The index of next LineSegment in the list. */
    private final int[] next;

    /** Which node contains the line. */
    private final Node[] map;

    public QuadTree(LineSegmentSet set) {
        this.set = set;
        int count = set.getNumLineSegments();
        next = new int[count];
        map = new Node[count];

        // Determine the root size.
        Double l = null, r = null, t = null, b = null;
        for (int i = 0; i < count; i++) {
            next[i] = -1;
            Line L = new Line(i);
            if (l == null || l > L.x) {
                l = L.x;
            }
            if (r == null || r < L.X) {
                r = L.X;
            }
            if (t == null || t < L.Y) {
                t = L.Y;
            }
            if (b == null || b > L.y) {
                b = L.y;
            }
        }

        // We enlarge the root by at least 2 * EPSILON to avoid rounding errors.
        // Also, we strategically offset the center of the root, since it is very common
        // for origami to have creases that are on exactly half of the sheet, 1/4 of the
        // sheet etc.
        root = new Node(l - 2 * EPSILON, r + 3 * EPSILON, b - 2 * EPSILON, t + 3 * EPSILON, null);

        for (int i = 0; i < count; i++) {
            root.addLineSegment(i);
        }
    }

    /** This only returns lines that are of greater index. */
    public Iterable<Integer> getPossibleCollision(int i) {
        SortedSet<Integer> set = new TreeSet<Integer>();

        // Collect all the lines upwards.
        Node node = map[i].parent;
        while (node != null) {
            collect(node, i, set);
            node = node.parent;
        }

        collectDownwards(map[i], i, set);
        return set;
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
            cursor = next[cursor];
        }
    }

    private class Line {
        double x, X, y, Y;

        Line(int i) {
            Point A = set.getA(i);
            Point B = set.getB(i);
            double ax = A.getX(), ay = A.getY();
            double bx = B.getX(), by = B.getY();
            x = Math.min(ax, bx);
            X = Math.max(ax, bx);
            y = Math.min(ay, by);
            Y = Math.max(ay, by);
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

        boolean addLineSegment(int i) {
            return addLineSegment(i, new Line(i));
        }

        private boolean addLineSegment(int i, Line l) {
            if (size >= CAPACITY) {
                if (children[0] == null) {
                    split();
                }
                for (int c = 0; c < 4; c++) {
                    if (children[c].addLineSegment(i, l)) {
                        return true;
                    }
                }
            }
            return addLineSegmentCore(i, l);
        }

        private boolean addLineSegmentCore(int i, Line l) {
            if (containsLine(l)) {
                addLine(i);
                return true;
            } else {
                return false;
            }
        }

        private void addLine(int i) {
            next[i] = head;
            map[i] = this;
            head = i;
            size++;
        }

        private boolean containsLine(Line L) {
            return L.x > l + EPSILON && L.X < r - EPSILON && L.y > b + EPSILON && L.Y < t - EPSILON;
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
                int n = next[i], c;
                Line l = new Line(i);
                for (c = 0; c < 4; c++) {
                    if (children[c].addLineSegmentCore(i, l)) {
                        break;
                    }
                }
                if (c == 4) {
                    addLine(i);
                }
                i = n;
            }
        }
    }
}
