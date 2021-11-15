package origami.data.quadTree.collector;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree.Node;
import origami.data.quadTree.adapter.QuadTreeAdapter;

/** Get all items that might contains the given line. */
public class PointCollector extends RecursiveCollector {

    private final Point p;

    public PointCollector(Point p) {
        this.p = p;
    }

    @Override
    public boolean shouldGoDown() {
        return false;
    }

    @Override
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter) {
        return adapter.getItem(cursor).mightContain(p);
    }

    @Override
    public boolean contains(Node node) {
        return node.contains(p);
    }
}
