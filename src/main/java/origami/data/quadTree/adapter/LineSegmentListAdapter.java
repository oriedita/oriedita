package origami.data.quadTree.adapter;

import java.util.List;
import java.util.function.Function;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

public class LineSegmentListAdapter implements  QuadTreeAdapter {

    private final List<LineSegment> list;
    private final Function<LineSegment, Point> factory;

    public LineSegmentListAdapter(List<LineSegment> list, Function<LineSegment, Point> factory) {
        this.list = list;
        this.factory= factory;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        Point p = factory.apply(list.get(index));
        double x = p.getX(), y = p.getY();
        return new QuadTreeItem(x, x, y, y);
    }

    @Override
    public int getPointCount() {
        return list.size();
    }

    @Override
    public Point getPoint(int index) {
        return factory.apply(list.get(index));
    }

    @Override
    public int getOffset() {
        return 0;
    }
}
