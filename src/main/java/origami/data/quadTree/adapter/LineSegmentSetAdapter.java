package origami.data.quadTree.adapter;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;

/**
 * LineSegmentSetAdapter is a {@link QuadTreeAdapter} that uses all points in a
 * {@link LineSegmentSet} as initial range.
 * 
 * @author Mu-Tsun Tsai
 */
public abstract class LineSegmentSetAdapter implements QuadTreeAdapter {

    protected final LineSegmentSet set;

    public LineSegmentSetAdapter(LineSegmentSet set) {
        this.set = set;
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
