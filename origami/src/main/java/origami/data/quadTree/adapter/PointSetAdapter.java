package origami.data.quadTree.adapter;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;

/**
 * PointSetAdapter is a {@link QuadTreeAdapter} that uses all points in a
 * {@link PointSet} as initial range.
 * 
 * @author Mu-Tsun Tsai
 */
public abstract class PointSetAdapter implements QuadTreeAdapter {

    protected final PointSet set;

    public PointSetAdapter(PointSet set) {
        this.set = set;
    }

    @Override
    public final int getPointCount() {
        return set.getNumPoints();
    }

    @Override
    public final Point getPoint(int index) {
        // Points in PointSet are 1-based
        return set.getPoint(index + 1);
    }
    
    @Override
    public int getOffset() {
        return 1;
    }
}
