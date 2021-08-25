package jp.gr.java_conf.mt777.graphic2d.point;

/**
 * Point_p is an addition of functions such as selection status to Point, and has been added from Orihime ver.3.037. Development drawing Used in the set of points possessed by craftsmen.
 */
public class Point_p extends Point {

    /**
     * Selected state
     */
    boolean i_state = false;

    public void setPointState1() {
        i_state = true;
    }

    public void setPointState0() {
        i_state = false;
    }

    public void changePointState() {
        i_state = !i_state;
    }

    public boolean getPointState() {
        return i_state;
    }
}

