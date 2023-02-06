package origami.data.quadTree.adapter;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTreeItem;

/**
 * InitialAdapter is used for gathering the initial {@link PointSet}.
 *
 * @author Mu-Tsun Tsai
 */
public class InitialAdapter extends LineSegmentSetAdapter {

    Point[] addPoint;
    int addPointNum = 0;

    public InitialAdapter(LineSegmentSet set) {
        // If you do not add +1 you will get an error when the number of faces is 1.
        this(set, set.getNumLineSegments()+1);
    }

    public InitialAdapter(LineSegmentSet set, int capacity) {
        super(set);
        addPoint = new Point[capacity];
    }

    @Override
    public int getCount() {
        return addPointNum;
    }

    @Override
    public QuadTreeItem getItem(int index) {
        double x = addPoint[index].getX();
        double y = addPoint[index].getY();
        return new QuadTreeItem(x, x, y, y);
    }

    public void add(Point p) {
        addPoint[addPointNum++] = p;
    }

    public Point get(int index) {
        return addPoint[index];
    }
}
