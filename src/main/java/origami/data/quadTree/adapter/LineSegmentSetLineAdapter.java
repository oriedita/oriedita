package origami.data.quadTree.adapter;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

/**
 * LineSegmentSetLineAdapter is a {@link LineSegmentSetAdapter} of which items
 * are the lines.
 * 
 * @author Mu-Tsun Tsai
 */
public class LineSegmentSetLineAdapter extends LineSegmentSetAdapter {

    public LineSegmentSetLineAdapter(LineSegmentSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumLineSegments();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        return createItem(set.getA(index), set.getB(index));
    }

    public static QuadTreeItem createItem(Point A, Point B) {
        double ax = A.getX(), ay = A.getY();
        double bx = B.getX(), by = B.getY();
        return new QuadTreeItem(Math.min(ax, bx), Math.max(ax, bx), Math.min(ay, by), Math.max(ay, by));
    }
}
