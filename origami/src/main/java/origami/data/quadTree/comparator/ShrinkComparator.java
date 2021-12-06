package origami.data.quadTree.comparator;

import origami.data.quadTree.QuadTree.Node;

/**
 * ShrinkComparator is the default QuadTreeComparator and is suitable for most
 * use cases.
 */
public class ShrinkComparator extends QuadTreeComparator {

    public final static ShrinkComparator instance = new ShrinkComparator();

    private ShrinkComparator() {}

    @Override
    public boolean contains(Node node, double l, double r, double b, double t) {
        return l > node.l + EPSILON && r < node.r - EPSILON && b > node.b + EPSILON && t < node.t - EPSILON;
    }
}
