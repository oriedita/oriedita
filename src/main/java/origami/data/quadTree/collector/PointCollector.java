package origami.data.quadTree.collector;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree.Node;
import origami.data.quadTree.adapter.QuadTreeAdapter;

public class PointCollector extends RecursiveCollector {

    private Point p;

    public PointCollector(Point p, QuadTreeAdapter adapter) {
        super(adapter);
        this.p = p;
    }

    @Override
    public boolean shouldGoDown() {
        return false;
    }

    @Override
    public boolean shouldCollect(int cursor) {
        return adapter.getItem(cursor).mightContain(p);
    }

    @Override
    public boolean contains(Node node) {
        return node.contains(p);
    }
}
