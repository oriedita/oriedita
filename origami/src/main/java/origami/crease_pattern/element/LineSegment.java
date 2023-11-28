package origami.crease_pattern.element;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

public class LineSegment implements Serializable, Cloneable {
    private static final Color DEFAULT_COLOR = new Color(100, 200, 200);

    protected Point a; //Branch a point
    protected Point b; //Branch b point
    protected ActiveState active;//0 is inactive. 1 is active in a. 2 is active in b. 3 is active in both a and b.
    protected LineColor color;//Color specification 　0=black,1=blue,2=red.

    protected int customized = 0;//Custom property parameters
    protected Color customizedColor = DEFAULT_COLOR;//Color if custom made

    protected int selected;//0 is not selected. 1 or more is set appropriately according to the situation


    //コンストラクタ
    public LineSegment() {
        a = new Point(0.0, 0.0);
        b = new Point(0.0, 0.0);
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
    }

    public LineSegment(Point t1, Point t2) {
        a = t1;
        b = t2;
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
    }

    public LineSegment(Point t1, Point t2, LineColor color) {
        a = t1;
        b = t2;
        active = ActiveState.INACTIVE_0;
        this.color = color;
        selected = 0;
    }

    public LineSegment(double i1, double i2, double i3, double i4) {
        a = new Point(i1, i2);
        b = new Point(i3, i4);
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
    }

    public LineSegment(LineSegment s0){
        a = s0.getA();
        b = s0.getB();
        active = ActiveState.INACTIVE_0;
        color = s0.getColor();
        selected = 0;
        customized = s0.getCustomized();
        customizedColor = s0.getCustomizedColor();
    }

    public void reset() {
        a = new Point(0.0, 0.0);
        b = new Point(0.0, 0.0);
        active = ActiveState.INACTIVE_0;
        color = LineColor.BLACK_0;
        selected = 0;
    }

    //-------------------------------------------
    public void set(LineSegment s) {
        a = s.getA();
        b = s.getB();
        active = s.getActive();
        color = s.getColor();
        selected = s.getSelected();
        setCustomized(s.getCustomized());
        setCustomizedColor(s.getCustomizedColor());
    }

    //----------
    public void set(double ax, double ay, double bx, double by) {
        a = new Point(ax, ay);
        b = new Point(bx, by);
    }

    //----------
    public void set(double ax, double ay, double bx, double by, LineColor ic) {
        set(ax, ay, bx, by);
        color = ic;
    }

    //---------
    public void set(Point p, Point q, LineColor ic, ActiveState ia) {
        set(p, q);
        color = ic;
        active = ia;
    }

    public void set(Point p, Point q, LineColor ic) {
        set(p, q);
        color = ic;
    }

    public void set(Point p, Point q) {
        set(p.getX(), p.getY(), q.getX(), q.getY());
    }

    /**
     * Larger when rounding up the x-coordinate of the end point
     */
    public int determineMaxX() {
        return Math.max((int) Math.ceil(a.getX()), (int) Math.ceil(b.getX()));
    }

    /**
     * The smaller one when truncating the x-coordinate of the end point
     */
    public int determineMinX() {
        return Math.min((int) Math.floor(a.getX()), (int) Math.floor(b.getX()));
    }

    /**
     * Larger when rounding up the y-coordinate of the end point
     */
    public int determineMaxY() {
        return Math.max((int) Math.ceil(a.getY()), (int) Math.ceil(b.getY()));
    }

    /**
     * The smaller one when truncating the y coordinate of the end point
     */
    public int determineMinY() {
        return Math.min((int) Math.floor(a.getY()), (int) Math.floor(b.getY()));
    }

    public LineColor getColor() {
        return color;
    }

    public void setColor(LineColor i) {
        color = i;
    }

    public ActiveState getActive() {
        return active;
    }

    public void setActive(ActiveState i) {
        active = i;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int i) {
        selected = i;
    }

    //Deactivate this line segment
    public void deactivate() {
        active = ActiveState.INACTIVE_0;
    }

    //Exchange the coordinates of both end points a and b
    public void a_b_swap() {
        Point t_temp = a;
        a = b;
        b = t_temp;
    }


    public Point getA() {
        return a;
    }

    //----------
    public void setA(Point p) {
        a = p;
    }

    public Point getB() {
        return b;
    }

    public void setB(Point p) {
        b = p;
    }

    public Point determineClosestEndpoint(Point p) {//Returns the endpoint closest to point P
        if (p.distanceSquared(a) <= p.distanceSquared(b)) {
            return a;
        }
        return b;
    }

    public Point determineFurthestEndpoint(Point p) {//Returns the point P and the farther end point
        if (p.distanceSquared(a) >= p.distanceSquared(b)) {
            return a;
        }
        return b;
    }

    public double determineLength() {
        return a.distance(b);
    }

    public double determineAX() {
        return a.getX();
    }

    public double determineAY() {
        return a.getY();
    }

    public double determineBX() {
        return b.getX();
    }

    public double determineBY() {
        return b.getY();
    }

    public double determineDeltaX() {
        return b.getX() - a.getX();
    }

    public double determineDeltaY() {
        return b.getY() - a.getY();
    }

    public int getCustomized() {
        return customized;
    }

    public void setCustomized(int i) {
        customized = i;
    }

    public Color getCustomizedColor() {
        return customizedColor;
    }

    public void setCustomizedColor(Color c0) {
        customizedColor = c0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineSegment that = (LineSegment) o;
        return customized == that.customized && selected == that.selected && Objects.equals(a, that.a)
                && Objects.equals(b, that.b) && active == that.active && color == that.color
                && Objects.equals(customizedColor, that.customizedColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, active, color, customized, customizedColor, selected);
    }

    @Override
    public LineSegment clone() {
        LineSegment clone = new LineSegment(a, b, color);
        clone.setActive(active);
        clone.setSelected(selected);
        clone.setCustomizedColor(customizedColor);
        clone.setCustomized(customized);


        // TODO: copy mutable state here, so the clone can't change the internals of the original
        return clone;
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

        final int state;

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
            switch (this) {
                case PARALLEL_EQUAL_31:
                case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321:
                case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322:
                case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331:
                case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332:
                case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341:
                case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342:
                case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351:
                case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352:
                case PARALLEL_S1_INCLUDES_S2_361:
                case PARALLEL_S1_INCLUDES_S2_362:
                case PARALLEL_S2_INCLUDES_S1_363:
                case PARALLEL_S2_INCLUDES_S1_364:
                case PARALLEL_S1_END_OVERLAPS_S2_START_371:
                case PARALLEL_S1_END_OVERLAPS_S2_END_372:
                case PARALLEL_S1_START_OVERLAPS_S2_END_373:
                case PARALLEL_S1_START_OVERLAPS_S2_START_374:
                    return true;
                default:
                    return false;
            }
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
