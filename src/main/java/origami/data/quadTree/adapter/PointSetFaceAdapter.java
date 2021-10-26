package origami.data.quadTree.adapter;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

public class PointSetFaceAdapter extends PointSetAdapter {

    private final QuadTreeItem[] cache;

    public PointSetFaceAdapter(PointSet set) {
        super(set);
        cache = new QuadTreeItem[getCount() + 1];
    }

    @Override
    public int getCount() {
        return set.getNumFaces();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        if (cache[index] != null) return cache[index];

        // Faces in PointSet are 1-based
        int count = set.getPointsCount(index + 1);
        Point p = set.getPoint(set.getPointId(index + 1, 1));
        double l = p.getX(), r = l, t = p.getY(), b = t;
        for (int i = 2; i <= count; i++) {
            p = set.getPoint(set.getPointId(index + 1, i));
            double x = p.getX(), y = p.getY();
            if (l > x) l = x;
            if (r < x) r = x;
            if (t < y) t = y;
            if (b > y) b = y;
        }
        QuadTreeItem item = new QuadTreeItem(l, r, b, t);
        cache[index] = item;
        return item;
    }
}