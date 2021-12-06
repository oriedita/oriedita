package origami.data.quadTree.adapter;

import java.util.List;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

public class PointLineMapAdapter implements QuadTreeAdapter {

    private final List<LineSegment> list;
    private final List<Point> points;

    public PointLineMapAdapter(List<LineSegment> list, List<Point> points) {
        this.list = list;
        this.points = points;
    }

    @Override
    public int getCount() {
        return points.size();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        return new QuadTreeItem(points.get(index));
    }

    @Override
    public int getPointCount() {
        return (list.size() - 1) * 2;
    }

    @Override
    public Point getPoint(int index) {
        LineSegment l = list.get(index / 2 + 1);
        return index % 2 == 0 ? l.getA() : l.getB();
    }

    @Override
    public int getOffset() {
        return 0;
    }
}
