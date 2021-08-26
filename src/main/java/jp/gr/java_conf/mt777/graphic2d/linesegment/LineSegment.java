package jp.gr.java_conf.mt777.graphic2d.linesegment;

import java.awt.*;

import jp.gr.java_conf.mt777.origami.orihime.LineColor;
import jp.gr.java_conf.mt777.graphic2d.point.Point;
//import  jp.gr.java_conf.mt777.zukei2d.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LineSegment {
    private final Point a = new Point(); //Branch a point
    private final Point b = new Point(); //Branch b point
    ActiveState active;//0 is inactive. 1 is active in a. 2 is active in b. 3 is active in both a and b.
    LineColor color;//Color specification 　0=black,1=blue,2=red.

    int customized = 0;//Custom property parameters
    Color customizedColor = new Color(100, 200, 200);//Color if custom made

    int selected;//0 is not selected. 1 or more is set appropriately according to the situation
    int maxX;//Larger when rounding up the x-coordinate of the end point
    int minX;//The smaller one when truncating the x-coordinate of the end point
    int maxY;//Larger when rounding up the y-coordinate of the end point
    int minY;//The smaller one when truncating the y coordinate of the end point

    //コンストラクタ
    public LineSegment() {
        a.set(0.0, 0.0);
        b.set(0.0, 0.0);
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
        vonoroiA = 0;
        vonoroiB = 0;
    }

    public LineSegment(Point t1, Point t2) {
        a.set(t1);
        b.set(t2);
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
        vonoroiA = 0;
        vonoroiB = 0;
    }

    public LineSegment(Point t1, Point t2, LineColor color) {
        a.set(t1);
        b.set(t2);
        active = ActiveState.INACTIVE_0;
        this.color = color;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
        vonoroiA = 0;
        vonoroiB = 0;
    }

    public LineSegment(double i1, double i2, double i3, double i4) {
        a.set(i1, i2);
        b.set(i3, i4);
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
        vonoroiA = 0;
        vonoroiB = 0;
    }

    public void reset() {
        a.set(0.0, 0.0);
        b.set(0.0, 0.0);
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
        maxX = 0;
        minX = 0;
        maxY = 0;
        minY = 0;
        vonoroiA = 0;
        vonoroiB = 0;
    }

    //d2s Double is changed to a string. Rounded to the second decimal place (""); public void display (String str0) is used only.
    public String d2s(double d0) {
        BigDecimal bd = new BigDecimal(d0);

        //Rounded to the first decimal place
        BigDecimal bd1 = bd.setScale(1, RoundingMode.HALF_UP);

        String sr;
        sr = bd1.toString();
        return sr;
    }

    public void display(String str0) {
        System.out.println(str0 + " (" + d2s(a.getX()) + " , " + d2s(a.getY()) + "),(" + d2s(b.getX()) + " , " + d2s(b.getY()) + ") ,ia=" + active + ",ic=" + color + ",is=" + selected);
    }


    //-------------------------------------------
    public void set(LineSegment s) {
        a.set(s.getA());
        b.set(s.getB());
        active = s.getActive();
        color = s.getColor();
        selected = s.getSelected();
        maxX = s.getMaxX();
        minX = s.getMinX();
        maxY = s.getMaxY();
        minY = s.getMinY();
        vonoroiA = s.getVonoroiA();
        vonoroiB = s.getVonoroiB();
        setCustomized(s.getCustomized());
        setCustomizedColor(s.getCustomizedColor());
    }

    //----------
    public void set(double ax, double ay, double bx, double by) {
        a.set(ax, ay);
        b.set(bx, by);
        maxX = (int) Math.ceil(ax);
        minX = (int) Math.floor(bx);
        if (ax < bx) {
            maxX = (int) Math.ceil(bx);
            minX = (int) Math.floor(ax);
        }
        maxY = (int) Math.ceil(ay);
        minY = (int) Math.floor(by);
        if (ay < by) {
            maxY = (int) Math.ceil(by);
            minY = (int) Math.floor(ay);
        }
    }

    //----------
    public void set(double ax, double ay, double bx, double by, LineColor ic) {
        set(ax, ay, bx, by);
        color = ic;
    }

    //----------
    public void setA(Point p) {
        set(p.getX(), p.getY(), b.getX(), b.getY());
    }

    public void setB(Point p) {
        set(a.getX(), a.getY(), p.getX(), p.getY());
    }

    //----------
    //Set the coordinates of the activated point to p !!!!!!!!!!!! If you make a mistake, this function is dangerous because it is hard to notice, preferably change it to another name 20170507
    public void set(Point p) {
        if (active == ActiveState.ACTIVE_A_1) {
            setA(p);
        }
        if (active == ActiveState.ACTIVE_B_2) {
            setB(p);
        }
    }


    //---------
    public void set(Point p, Point q, LineColor ic, ActiveState ia) {
        set(p, q);
        color = ic;
        active = ia;
    }

    public void set(Point p, Point q, LineColor ic, ActiveState ia, int v_a, int v_b) {
        set(p,q,ic,ia);
        vonoroiA = v_a;
        vonoroiB = v_b;
    }

    public void set(Point p, Point q, LineColor ic) {
        set(p, q);
        color = ic;
    }

    public void set(Point p, Point q) {
        set(p.getX(), p.getY(), q.getX(), q.getY());
    }

    //-------------------------------------------
    public int getMaxX() {
        return maxX;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }


    public void setColor(LineColor i) {
        color = i;
    }

    public LineColor getColor() {
        return color;
    }

    public void setActive(ActiveState i) {
        active = i;
    }

    public ActiveState getActive() {
        return active;
    }

    public void setSelected(int i) {
        selected = i;
    }

    public int getSelected() {
        return selected;
    }

    //This line segment is activated depending on whether it is close to a certain point.
    public void activate(Point p, double r) {
        active = ActiveState.INACTIVE_0;
        if (p.distanceSquared(a) <= r * r) {
            active = ActiveState.ACTIVE_A_1;
        }
        if (p.distanceSquared(b) <= r * r) {
            active = ActiveState.ACTIVE_B_2;
        }
    }

    //Deactivate this line segment
    public void deactivate() {
        active = ActiveState.INACTIVE_0;
    }

    //Exchange the coordinates of both end points a and b
    public void a_b_swap() {
        Point t_temp = new Point(a);
        a.set(b);
        b.set(t_temp);
    }


    public Point getA() {
        return new Point(a.getX(), a.getY());
    }

    public Point getB() {
        return new Point(b.getX(), b.getY());
    }

    public Point getClosestEndpoint(Point p) {//Returns the endpoint closest to point P
        if (p.distanceSquared(a) <= p.distanceSquared(b)) {
            return a;
        }
        return b;
    }

    public Point getFurthestEndpoint(Point p) {//Returns the point P and the farther end point
        if (p.distanceSquared(a) >= p.distanceSquared(b)) {
            return a;
        }
        return b;
    }

    public double getLength() {
        return a.distance(b);
    }

    public double getAX() {
        return a.getX();
    }

    public double getAY() {
        return a.getY();
    }

    public double getBX() {
        return b.getX();
    }

    public double getBY() {
        return b.getY();
    }

    public void setAX(double d) {
        a.setX(d);
    }

    public void setAY(double d) {
        a.setY(d);
    }

    public void setBX(double d) {
        b.setX(d);
    }

    public void setBY(double d) {
        b.setY(d);
    }


    public void setCustomized(int i) {
        customized = i;
    }

    public int getCustomized() {
        return customized;
    }

    public void setCustomizedColor(Color c0) {
        customizedColor = c0;
    }

    public Color getCustomizedColor() {
        return customizedColor;
    }

    int vonoroiA;
    int vonoroiB;

    public void setVonoroiA(int i) {
        vonoroiA = i;
    }


    public void setVonoroiB(int i) {
        vonoroiB = i;
    }

    public int getVonoroiA() {
        return vonoroiA;
    }

    public int getVonoroiB() {
        return vonoroiB;
    }

    /**
     * 0 = Do not intersect,
     * 1 = Two line segments are not parallel and intersect at one point in a crossroads shape,
     * 2nd generation = Two line segments are not parallel and intersect in a T-junction or dogleg shape at one point
     * 3 = Two line segments are parallel and intersect
     * 4 = Line segment s1 and line segment s2 intersect at a point
     * 5 = Line segment s1 intersects at a point
     * 6 = Line segment s2 intersects at a point
     * Note! If p1 and p2 are the same, or p3 and p4 are the same, the result will be strange,
     * This function itself does not have a check mechanism, so it may be difficult to notice.
     * <p>
     * Is always about two line segments s1 (p1, p2) and s2 (p3, p4)
     */
    public enum Intersection {
        ERROR(-1),
        /**
         * s1 and s2 do not intersect.
         */
        NO_INTERSECTION_0(0),
        /**
         * s1 and s2 are not parallel and intersect at a crossroad.
         */
        INTERSECTS_1(1),

        INTERSECTS_AUX_2(2),
        INTERSECTS_AUX_3(3),

        /**
         * Exception handling: When line segment s1 and line segment s2 are points.
         */
        INTERSECT_AT_POINT_4(4),
        /**
         * Exception handling: When the line segment s1 is a point.
         */
        INTERSECT_AT_POINT_S1_5(5),
        /**
         * Exception handling: When the line segment s2 is a point.
         */
        INTERSECT_AT_POINT_S2_6(6),

        /**
         * L-Shaped intersection, start of s1 (p1) intersects start of s2 (p3).
         */
        INTERSECTS_LSHAPE_S1_START_S2_START_21(21),
        /**
         * L-Shaped intersection, start of s1 (p1) intersects end of s2 (p4).
         */
        INTERSECTS_LSHAPE_S1_START_S2_END_22(22),
        /**
         * L-Shaped intersection, end of s1 (p2) intersects start of s2 (p3).
         */
        INTERSECTS_LSHAPE_S1_END_S2_START_23(23),
        /**
         * L-Shaped intersection, end of s1 (p2) intersects end of s2 (p4).
         */
        INTERSECTs_LSHAPE_S1_END_S2_END_24(24),
        /**
         * T-Shaped intersection, start of s1 (p1) intersects s2.
         */
        INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25(25),
        /**
         * T-Shaped intersection, end of s1 (p2) intersects s2.
         */
        INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26(26),
        /**
         * T-Shaped intersection, start of s2 (p3) intersects s1.
         */
        INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27(27),
        /**
         * T-Shaped intersection, end of s2 (p4) intersects s1.
         */
        INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28(28),

        /**
         * s1 and s2 are equal.
         */
        PARALLEL_EQUAL_31(31),

        INTERSECT_T_A_121(121),
        INTERSECT_T_B_122(122),
        INTERSECT_T_A_211(211),
        INTERSECT_T_B_221(221),

        /**
         * The endpoints of two line segments (p1 and p3) overlap at one point. s1 contains s2.
         */
        PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321(321),
        /**
         * The endpoints of two line segments (p1 and p3) overlap at one point. s2 contains s1.
         */
        PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322(322),
        /**
         * s1 and s2 are right after each other, with the start of s1 (p1) intersection the start of s2 (p3).
         */
        PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323(323),

        /**
         * The endpoints of two line segments (p1 and p4) overlap at one point. s1 contains s2.
         */
        PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331(331),
        /**
         * The endpoints of two line segments (p1 and p4) overlap at one point. s2 contains s1.
         */
        PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332(332),
        /**
         * s1 and s2 are right after each other, with the start of s1 (p1) intersection the end of s2 (p4).
         */
        PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333(333),

        /**
         * The endpoints of two line segments (p2 and p3) overlap at one point. s1 contains s2.
         */
        PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341(341),
        /**
         * The endpoints of two line segments (p2 and p3) overlap at one point. s2 contains s1.
         */
        PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342(342),
        /**
         * s1 and s2 are right after each other, with the end of s1 (p2) intersection the start of s2 (p3).
         */
        PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343(343),

        /**
         * The endpoints of two line segments (p2 and p4) overlap at one point. s1 contains s2.
         */
        PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351(351),
        /**
         * The endpoints of two line segments (p2 and p4) overlap at one point. s2 contains s1.
         */
        PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352(352),
        /**
         * s1 and s2 are right after each other, with the end of s1 (p2) intersection the end of s2 (p4).
         */
        PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353(353),

        /**
         * In order of p1-p3-p4-p2
         */
        PARALLEL_S1_INCLUDES_S2_361(361),
        /**
         * In order of p1-p4-p3-p2
         */
        PARALLEL_S1_INCLUDES_S2_362(362),
        /**
         * In order of p3-p1-p2-p4
         */
        PARALLEL_S2_INCLUDES_S1_363(363),
        /**
         * In order of p3-p2-p1-p4
         */
        PARALLEL_S2_INCLUDES_S1_364(364),

        /**
         * In order of p1-p3-p2-p4
         */
        PARALLEL_S1_END_OVERLAPS_S2_START_371(371),
        /**
         * In order of p1-p4-p2-p3
         */
        PARALLEL_S1_END_OVERLAPS_S2_END_372(372),
        /**
         * In order of p3-p1-p4-p2
         */
        PARALLEL_S1_START_OVERLAPS_S2_END_373(373),
        /**
         * s1 and s2 are parallel and overlap in order of p4-p1-p3-p2
         */
        PARALLEL_S1_START_OVERLAPS_S2_START_374(374),
        ;

        int state;

        Intersection(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        /**
         * @return Is this an L-Shaped or T-Shaped intersection.
         */
        public boolean isEndpointIntersection() {
            return state >= 21 && state <= 28;
        }

        /**
         * @return Is this an intersection.
         */
        public boolean isIntersection() {
            return state >= 1;
        }

        /**
         * @return If one of the line segments is completely inside the other.
         */
        public boolean isContainedInside() {
            return state >= 360;
        }

        public boolean isParallel() {
            return state >= 30;
        }

        public boolean isSegmentOverlapping() {
            return switch (this) {
                case PARALLEL_EQUAL_31,
                        PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321,
                        PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322,
                        PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331,
                        PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332,
                        PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341,
                        PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342,
                        PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351,
                        PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352,
                        PARALLEL_S1_INCLUDES_S2_361,
                        PARALLEL_S1_INCLUDES_S2_362,
                        PARALLEL_S2_INCLUDES_S1_363,
                        PARALLEL_S2_INCLUDES_S1_364,
                        PARALLEL_S1_END_OVERLAPS_S2_START_371,
                        PARALLEL_S1_END_OVERLAPS_S2_END_372,
                        PARALLEL_S1_START_OVERLAPS_S2_END_373,
                        PARALLEL_S1_START_OVERLAPS_S2_START_374 -> true;
                default -> false;
            };
        }

        @Override
        public String toString() {
            return Integer.toString(state);
        }
    }

    /**
     * 0 is inactive. 1 is active in a. 2 is active in b. 3 is active in both a and b.
     */
    public enum ActiveState {
        INACTIVE_0,
        ACTIVE_A_1,
        ACTIVE_B_2,
        ACTIVE_BOTH_3,
        MARK_FOR_DELETION_100,
    }
}
