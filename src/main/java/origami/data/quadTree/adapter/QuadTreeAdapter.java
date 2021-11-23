package origami.data.quadTree.adapter;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.*;

/**
 * QuadTreeAdapter interface adapts different types of collection to
 * {@link QuadTree}.
 * 
 * @author Mu-Tsun Tsai
 */
public interface QuadTreeAdapter {

    /** Get the item count in the collection. */
    public int getCount();

    /** Get the item from the collection and convert it to QuadTreeItem. */
    public QuadTreeItem getItem(int index);

    /** Get the number of points for initialization. */
    public int getPointCount();

    /** Get a point for initialization. */
    public Point getPoint(int index);

    /** Get index offset. */
    public int getOffset();

    public static QuadTreeItem createItem(Point A, Point B) {
        double ax = A.getX(), ay = A.getY();
        double bx = B.getX(), by = B.getY();
        return new QuadTreeItem(Math.min(ax, bx), Math.max(ax, bx), Math.min(ay, by), Math.max(ay, by));
    }
}
