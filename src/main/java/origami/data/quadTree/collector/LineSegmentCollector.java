package origami.data.quadTree.collector;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;
import origami.data.quadTree.QuadTree.Node;
import origami.data.quadTree.adapter.LineSegmentSetAdapter;
import origami.data.quadTree.adapter.QuadTreeAdapter;

public class LineSegmentCollector extends RecursiveCollector {

    private QuadTreeItem item;

    public LineSegmentCollector(Point p, Point q, QuadTreeAdapter adapter) {
        super(adapter);
        this.item = LineSegmentSetAdapter.createItem(p, q);
    }

    @Override
    public boolean shouldGoDown() {
        return true;
    }

    @Override
    public boolean shouldCollect(int cursor) {
        return adapter.getItem(cursor).mightContain(item);
    }

    @Override
    public boolean contains(Node node) {
        return node.contains(item);
    }
}
