package origami.data.quadTree.adapter;

import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;
import origami.folding.element.Face;

public class PointSetFaceAdapter extends PointSetAdapter {

    public PointSetFaceAdapter(PointSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumFaces();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        // Faces in PointSet are 1-based
        Face face = set.getFace(index + 1);
        int count = face.getNumPoints();
        Double l = null, r = null, t = null, b = null;
        for (int i = 1; i <= count; i++) {
            Point p = set.getPoint(face.getPointId(i));
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