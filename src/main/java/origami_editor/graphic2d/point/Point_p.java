package origami_editor.graphic2d.point;

/**
 * Point_p is an addition of functions such as selection status to Point, and has been added from Orihime ver.3.037. Crease pattern Used in the set of points possessed by craftsmen.
 */
public class Point_p extends Point {

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

    public void changePointState() {
        i_state = !i_state;
    }

    public boolean getPointState() {
        return i_state;
    }
}

