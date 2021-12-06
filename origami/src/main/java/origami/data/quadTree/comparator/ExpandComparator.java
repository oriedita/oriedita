package origami.data.quadTree.comparator;

import origami.data.quadTree.QuadTree.Node;

/**
 * ExpandComparator is the opposite of ShrinkComparator, and would work better
 * for faces as it will help creating a better quad tree. However one must keep
 * in mind that this comparator cannot be used for collision detection.
 */
public class ExpandComparator extends QuadTreeComparator {

    public final static ExpandComparator instance = new ExpandComparator();

    private ExpandComparator() {}

    @Override
    public boolean contains(Node node, double l, double r, double b, double t) {
        return l > node.l - EPSILON && r < node.r + EPSILON && b > node.b - EPSILON && t < node.t + EPSILON;
    }
}
