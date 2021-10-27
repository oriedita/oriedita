package origami.data.quadTree;

import origami.Epsilon;
import origami.crease_pattern.element.Point;

public class QuadTreeItem {
    public static final double EPSILON = Epsilon.QUAD_TREE_ITEM;
    public final double l, r, b, t;

    public QuadTreeItem(double l, double r, double b, double t) {
        this.l = l;
        this.r = r;
        this.b = b;
        this.t = t;
    }

    public boolean mightContain(Point p) {
        double x = p.getX(), y = p.getY();
        return x > l - EPSILON && x < r + EPSILON && y > b - EPSILON && y < t + EPSILON;
    }

    public boolean mightOverlap(QuadTreeItem item) {
        return item.r >= l - EPSILON && item.l <= r + EPSILON && item.t >= b - EPSILON && item.b <= t + EPSILON;
    }
}
