package origami.data.quadTree.adapter;

import origami.crease_pattern.PointSet;
import origami.data.quadTree.QuadTreeItem;

public class PointSetPointAdapter extends PointSetAdapter {

    public PointSetPointAdapter(PointSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumPoints();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        // PointSet is 1-based
        double x = set.getPointX(index + 1);
        double y = set.getPointY(index + 1);
        return new QuadTreeItem(x, x, y, y);
    }
}
