package origami.data.quadTree.adapter;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;

public abstract class PointSetAdapter implements QuadTreeAdapter {

    protected final PointSet set;

    public PointSetAdapter(PointSet set) {
        this.set = set;
    }

    @Override
    public int getPointCount() {
        return set.getNumPoints();
    }

    @Override
    public Point getPoint(int index) {
        // Points in PointSet are 1-based
        return set.getPoint(index + 1);
    }
}
