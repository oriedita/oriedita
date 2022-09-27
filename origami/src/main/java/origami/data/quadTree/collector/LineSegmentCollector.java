package origami.data.quadTree.collector;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree.Node;
import origami.data.quadTree.QuadTreeItem;
import origami.data.quadTree.adapter.QuadTreeAdapter;

/**
 * Get all items that might partially contains the given line.
 */
public class LineSegmentCollector extends RecursiveCollector {

    private QuadTreeItem item;

    public LineSegmentCollector(LineSegment line) {
        this(line.getA(), line.getB());
    }

    public LineSegmentCollector(Point p, Point q) {
        this.item = new QuadTreeItem(p, q);
    }

    @Override
    public boolean shouldGoDown() {
        return true;
    }

    @Override
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter) {
        return adapter.getItem(cursor).mightOverlap(item);
    }

    @Override
    public boolean contains(Node node) {
        return node.contains(item);
    }
}
