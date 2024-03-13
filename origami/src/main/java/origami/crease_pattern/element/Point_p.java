package origami.crease_pattern.element;

/**
 * Point_p is an addition of functions such as selection status to Point, and has been added from Orihime ver.3.037. Crease pattern Used in the set of points possessed by craftsmen.
 */
public class Point_p extends Point {

    public Point_p(Point p) {
        super(p);
    }

    public Point_p(Point p, boolean state) {
        this(p);
        this.i_state = state;
    }

    public Point_p() {
    }

    /**
     * Selected state
     */
    boolean i_state = false;

    public void setPointStateTrue() {
        i_state = true;
    }

    public void setPointStateFalse() {
        i_state = false;
    }

    public boolean getPointState() {
        return i_state;
    }
}

