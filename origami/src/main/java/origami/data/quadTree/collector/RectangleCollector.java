package origami.data.quadTree.collector;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.QuadTreeItem;
import origami.data.quadTree.adapter.QuadTreeAdapter;

public class RectangleCollector extends RecursiveCollector{
    private final QuadTreeItem item;

    public RectangleCollector(Point p, Point q) {
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
    public boolean contains(QuadTree.Node node) {
        return node.contains(item);
    }
}
