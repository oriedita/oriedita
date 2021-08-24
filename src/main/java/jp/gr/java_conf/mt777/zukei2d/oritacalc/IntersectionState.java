package jp.gr.java_conf.mt777.zukei2d.oritacalc;

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
 * Is always about two lines s1 (p1, p2) and s2 (p3, p4)
 */
public enum IntersectionState {
    ERROR(-1),
    /**
     * s1 and s2 do not intersect.
     */
    NO_INTERSECTION_0(0),
    /**
     * s1 and s2 are not parallel and intersect at a crossroad.
     */
    INTERSECTS_1(1),

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

    IntersectionState(int state) {
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
