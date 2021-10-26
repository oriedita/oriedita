package origami.data.quadTree.adapter;

import java.util.function.BiFunction;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

public class LineSegmentEndPointAdapter implements QuadTreeAdapter {

    private final LineSegmentSet set;
    private final  BiFunction<LineSegmentSet, Integer, Point> factory;

    public LineSegmentEndPointAdapter(LineSegmentSet set, BiFunction<LineSegmentSet, Integer, Point> factory) {
        this.set = set;
        this.factory = factory;
    }

    @Override
    public int getCount() {
        return set.getNumLineSegments();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        Point p = factory.apply(set, index);
        double x = p.getX(), y = p.getY();
        return new QuadTreeItem(x, x, y, y);

    }

    @Override
    public int getPointCount() {
        return set.getNumLineSegments();
    }

    @Override
    public Point getPoint(int index) {
        return factory.apply(set, index);
    }
}
