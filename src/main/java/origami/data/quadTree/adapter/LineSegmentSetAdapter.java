package origami.data.quadTree.adapter;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

public class LineSegmentSetAdapter implements QuadTreeAdapter {

    private final LineSegmentSet set;

    public LineSegmentSetAdapter(LineSegmentSet set) {
        this.set = set;
    }

    @Override
    public int getCount() {
        return set.getNumLineSegments();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        Point A = set.getA(index);
        Point B = set.getB(index);
        double ax = A.getX(), ay = A.getY();
        double bx = B.getX(), by = B.getY();
        return new QuadTreeItem(Math.min(ax, bx), Math.max(ax, bx), Math.min(ay, by), Math.max(ay, by));
    }

    @Override
    public int getPointCount() {
        return set.getNumLineSegments() * 2;
    }

    @Override
    public Point getPoint(int index) {
        return index % 2 == 0 ? set.getA(index / 2) : set.getB(index / 2);
    }
}
