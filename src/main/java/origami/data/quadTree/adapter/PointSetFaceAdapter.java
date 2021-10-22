package origami.data.quadTree.adapter;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

public class PointSetFaceAdapter implements QuadTreeAdapter {

    private final PointSet set;

    public PointSetFaceAdapter(PointSet set) {
        this.set = set;
    }

    @Override
    public int getCount() {
        return set.getNumFaces();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        // Faces in PointSet are 1-based
        int count = set.getPointsCount(index + 1);
        Double l = null, r = null, t = null, b = null;
        for (int i = 1; i <= count; i++) {
            Point p = set.getPoint(set.getPointId(index + 1, i));
            double x = p.getX(), y = p.getY();
            if (l == null || l > x) {
                l = x;
            }
            if (r == null || r < x) {
                r = x;
            }
            if (t == null || t < y) {
                t = y;
            }
            if (b == null || b > y) {
                b = y;
            }
        }
        return new QuadTreeItem(l, r, b, t);
    }
}