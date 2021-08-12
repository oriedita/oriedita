package jp.gr.java_conf.mt777.zukei2d.ten;

/**
 * Point_p is an addition of functions such as selection status to Point, and has been added from Orihime ver.3.037. Development drawing Used in the set of points possessed by craftsmen.
 */
public class Point_p extends Point {

    /**
     * Selected state
     */
    byte i_state = 0;

    public void setPointState1() {
        i_state = 1;
    }

    public void setPointState0() {
        i_state = 0;
    }

    public void changePointState() {
        if (i_state == 1) {
            i_state = 0;
        } else if (i_state == 0) {
            i_state = 1;
        }
    }

    public byte getPointState() {
        return i_state;
    }
}

