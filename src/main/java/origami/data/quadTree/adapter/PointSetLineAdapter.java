package origami.data.quadTree.adapter;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Line;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

public class PointSetLineAdapter extends PointSetAdapter {

    public PointSetLineAdapter(PointSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumLines();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        // Lines in PointSet are 1-based
        Line l = set.getLine(index + 1);
        Point A = set.getPoint(l.getBegin());
        Point B = set.getPoint(l.getEnd());
        double ax = A.getX(), ay = A.getY();
        double bx = B.getX(), by = B.getY();
        return new QuadTreeItem(Math.min(ax, bx), Math.max(ax, bx), Math.min(ay, by), Math.max(ay, by));
    }
}
