package origami.crease_pattern;

import origami.Epsilon;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * Static utilities for calculations.
 */
public class OritaCalc {
    //Find the position of the projection of the point p on the straight line t (the position on the straight line t closest to the point p).
    public static Point findProjection(StraightLine t, Point p) {
        StraightLine t1 = t.orthogonalize(p);//Find the straight line u1 that passes through the point p1 and is orthogonal to t.
        return findIntersection(t, t1);
    }

    //Find the position of the projection of the point p on the straight line t passing through the points P0 and P1 (the position on the straight line t closest to the point p).
    public static Point findProjection(Point p0, Point p1, Point p) {
        StraightLine t = new StraightLine(p0, p1);
        return findProjection(t, p);
    }

    //Find the position of the projection of the point p on the straight line t including the line segment s0 (the position on the straight line t closest to the point p).
    public static Point findProjection(LineSegment s0, Point p) {
        return findProjection(s0.getA(), s0.getB(), p);
    }

    //A function that determines whether two points are in the same position (true) or different (false) -------------------------------- -
    public static boolean equal(Point p1, Point p2) {
        return equal(p1, p2, Epsilon.POINT);
    }

    public static boolean equal(Point p1, Point p2, double r) {//r is the error tolerance. Strict judgment if r is negative.
        //Strict judgment。
        if (r <= 0.0) {
            if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
                return true;
            }
        }
        //Tolerate error。
        if (r > 0) {
            return distance(p1, p2) <= r;
        }
        return false;
    }

    //Function to find the distance (integer) between two points----------------------------------------------------
    public static double distance(Point p0, Point p1) {
        return p0.distance(p1);
    }

    //A function that finds the angle between the vector ab and the x-axis by specifying a and b between two points. If a = b, return -10000.0 ----------------------------------------- -----------
    public static double angle(Point a, Point b) {
        double ax, ay, bx, by, x, y, L, c, ret;
        ax = a.getX();
        ay = a.getY();
        bx = b.getX();
        by = b.getY();
        x = bx - ax;
        y = by - ay;
        L = Math.sqrt(x * x + y * y);
        if (L <= 0.0) {
            return -10000.0;
        }
        c = x / L;
        if (c > 1.0) {
            c = 1.0;
        }

        ret = Math.acos(c);
        if (y < 0.0) {
            ret = -ret;
        }
        ret = 180.0 * ret / Math.PI;
        if (ret < 0) {
            ret = ret + 360.0;
        }
        return ret;
    }


    //A function that specifies the line segment and finds the angle between the vector ab and the x-axis. If a = b, return -10000.0 ----------------------------------------------------
    public static double angle(LineSegment s) {
        return angle(s.getA(), s.getB());
    }

    //A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment ending at the two points p1 and p2 at the points p1 and p2.
    public static int isInside(Point p1, Point pa, Point p2) {
        StraightLine u1 = new StraightLine(p1, p2).orthogonalize(p1);//Find the straight line u1 that passes through the point p1 and is orthogonal to t.
        StraightLine u2 = new StraightLine(p1, p2).orthogonalize(p2);//Find the straight line u2 that passes through the point p2 and is orthogonal to t.

        if (u1.assignmentCalculation(pa) * u2.assignmentCalculation(pa) == 0.0) {
            return 1;
        }
        if (u1.assignmentCalculation(pa) * u2.assignmentCalculation(pa) < 0.0) {
            return 2;
        }
        return 0;//If outside the box
    }


    // A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment ending at the two points p1 and p2 at the points p1 and p2. This is considered to be inside the rectangle even if it protrudes a little.
    // Specifically, when determining whether there is a point inside the line segment, if the point is slightly outside the line segment, it is judged to be sweet if it is inside the line segment. When drawing a crease pattern with a drawing craftsman, if you do not use this sweet one, the intersection division of the T-shaped line segment will fail
    // But for some reason, using this sweeter one for folding estimation seems to result in an infinite loop, which doesn't work. This exact elucidation is unresolved 20161105
    public static int isInside_sweet(Point p1, Point pa, Point p2) {
        StraightLine u1 = new StraightLine(p1, p2).orthogonalize(p1);//Find the straight line u1 that passes through the point p1 and is orthogonal to t.
        StraightLine u2 = new StraightLine(p1, p2).orthogonalize(p2);//Find the straight line u2 that passes through the point p2 and is orthogonal to t.

        if (u1.calculateDistance(pa) < Epsilon.SWEET_DISTANCE || u2.calculateDistance(pa) < Epsilon.SWEET_DISTANCE) {
            return 1;
        }

        if (u1.assignmentCalculation(pa) * u2.assignmentCalculation(pa) < 0.0) {
            return 2;
        }
        return 0;//If outside the box
    }


    /**
     * A function that determines where the point p is close to the specified line segment (within r) ------------------------ ---------
     * 0 = not close, 1 = close to point a, 2 = close to point b, 3 = close to handle
     */
    public static int determineClosestLineSegmentEndpoint(Point p, LineSegment s0, double r) {
        if (r > distance(p, s0.getA())) {
            return 1;
        }//Whether it is close to point a
        if (r > distance(p, s0.getB())) {
            return 2;
        }//Whether it is close to point b
        if (r > determineLineSegmentDistance(p, s0)) {
            return 3;
        }//Whether it is close to the handle
        return 0;
    }


    //Function to find the distance between the point p0 and the line segment with the two points p1 and p2 at both ends --------------------------- -------------------------
    public static double determineLineSegmentDistance(Point p0, Point p1, Point p2) {
        //When p1 and p2 are the same
        if (distance(p1, p2) == 0.0) {
            return distance(p0, p1);
        }

        //When p1 and p2 are different
        StraightLine t = new StraightLine(p1, p2);//p1,Find the straight line t passing through p2。
        StraightLine u = t.orthogonalize(p0);//Find a straight line u that passes through the point p0 and is orthogonal to t.

        if (isInside(p1, findIntersection(t, u), p2) >= 1) {
            return t.calculateDistance(p0);
        }//When the intersection of t and u is between p1 and p2.
        return Math.min(distance(p0, p1), distance(p0, p2));//When the intersection of t and u is not between p1 and p2.
    }

    //A function that finds the distance between the point p0 and the line segment s ----------------------------------- -----------------
    public static double determineLineSegmentDistance(Point p0, LineSegment s) {
        Point p1 = s.getA();
        Point p2 = s.getB();
        return determineLineSegmentDistance(p0, p1, p2);
    }

    // A function that determines whether two line segments intersect ---------------------------------- ------------------ ------------------
    // 0 = Do not intersect,
    // 1 = Two line segments are not parallel and intersect at one point in a crossroads shape,
    // 2nd generation = Two line segments are not parallel and intersect in a T-junction or dogleg shape at one point
    // 3 = Two line segments are parallel and intersect
    // 4 = Line segment s1 and line segment s2 intersect at a point
    // 5 = Line segment s1 intersects at a point
    // 6 = Line segment s2 intersects at a point
    // Note! If p1 and p2 are the same, or p3 and p4 are the same, the result will be strange,
    // This function itself does not have a check mechanism, so it may be difficult to notice.
    public static LineSegment.Intersection determineLineSegmentIntersection(LineSegment s1, LineSegment s2) {
        return determineLineSegmentIntersection(s1, s2, Epsilon.UNKNOWN_001);
    }

    public static LineSegment.Intersection determineLineSegmentIntersectionSweet(LineSegment s1, LineSegment s2) {
        return determineLineSegmentIntersectionSweet(s1, s2, Epsilon.UNKNOWN_001, Epsilon.UNKNOWN_001);
    }

    public static LineSegment.Intersection determineLineSegmentIntersection(LineSegment s1, LineSegment s2, double precision) {
        return determineLineSegmentIntersection(s1, s2, precision, precision);
    }

    public static LineSegment.Intersection determineLineSegmentIntersection(LineSegment s1, LineSegment s2, double rhit, double rhei) {    //r_hitosii and r_heikouhantei are the allowable degree of deviation between hitosii and heikou_hantei
        double x1max = s1.determineAX();
        double x1min = s1.determineAX();
        double y1max = s1.determineAY();
        double y1min = s1.determineAY();
        if (x1max < s1.determineBX()) {
            x1max = s1.determineBX();
        }
        if (x1min > s1.determineBX()) {
            x1min = s1.determineBX();
        }
        if (y1max < s1.determineBY()) {
            y1max = s1.determineBY();
        }
        if (y1min > s1.determineBY()) {
            y1min = s1.determineBY();
        }
        double x2max = s2.determineAX();
        double x2min = s2.determineAX();
        double y2max = s2.determineAY();
        double y2min = s2.determineAY();
        if (x2max < s2.determineBX()) {
            x2max = s2.determineBX();
        }
        if (x2min > s2.determineBX()) {
            x2min = s2.determineBX();
        }
        if (y2max < s2.determineBY()) {
            y2max = s2.determineBY();
        }
        if (y2min > s2.determineBY()) {
            y2min = s2.determineBY();
        }

        if (x1max + rhit + Epsilon.POINT < x2min) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (x1min - rhit - Epsilon.POINT > x2max) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (y1max + rhit + Epsilon.POINT < y2min) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (y1min - rhit - Epsilon.POINT > y2max) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        Point p1 = s1.getA();
        Point p2 = s1.getB();
        Point p3 = s2.getA();
        Point p4 = s2.getB();

        StraightLine t1 = new StraightLine(p1, p2);
        StraightLine t2 = new StraightLine(p3, p4);

        //Exception handling: When line segment s1 and line segment s2 are points
        if (((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))
                &&
                ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY()))) {
            if ((p1.getX() == p3.getX()) && (p1.getY() == p3.getY())) {
                return LineSegment.Intersection.INTERSECT_AT_POINT_4;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        //Exception handling: When the line segment s1 is a point
        if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
            if ((isInside(p3, p1, p4) >= 1) && (t2.assignmentCalculation(p1) == 0.0)) {
                return LineSegment.Intersection.INTERSECT_AT_POINT_S1_5;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        //Exception handling: When the line segment s2 is a point
        if ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY())) {
            if ((isInside(p1, p3, p2) >= 1) && (t1.assignmentCalculation(p3) == 0.0)) {
                return LineSegment.Intersection.INTERSECT_AT_POINT_S2_6;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        if (isLineSegmentParallel(t1, t2, rhei) == ParallelJudgement.NOT_PARALLEL) {    //Two straight lines are not parallel
            Point pk = findIntersection(t1, t2);    //<<<<<<<<<<<<<<<<<<<<<<<
            if ((isInside(p1, pk, p2) >= 1) && (isInside(p3, pk, p4) >= 1)) {
                if (equal(p1, p3, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_LSHAPE_S1_START_S2_START_21;
                }//L-shaped
                if (equal(p1, p4, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_LSHAPE_S1_START_S2_END_22;
                }//L-shaped
                if (equal(p2, p3, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_LSHAPE_S1_END_S2_START_23;
                }//L-shaped
                if (equal(p2, p4, rhit)) {
                    return LineSegment.Intersection.INTERSECTs_LSHAPE_S1_END_S2_END_24;
                }//L-shaped
                if (equal(p1, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25;
                }//T-shaped s1 is a vertical bar
                if (equal(p2, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26;
                }//T-shaped s1 is a vertical bar
                if (equal(p3, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27;
                }//T-shaped s2 is a vertical bar
                if (equal(p4, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28;
                }//T-shaped s2 is a vertical bar
                return LineSegment.Intersection.INTERSECTS_1;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        if (isLineSegmentParallel(t1, t2, rhei) == ParallelJudgement.PARALLEL_NOT_EQUAL) { //Two straight lines are parallel and y-intercept does not match
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        // The two line segments are exactly the same
        if (equal(p1, p3, rhit) && equal(p2, p4, rhit)) {
            return LineSegment.Intersection.PARALLEL_EQUAL_31;
        }
        if (equal(p1, p4, rhit) && equal(p2, p3, rhit)) {
            return LineSegment.Intersection.PARALLEL_EQUAL_31;
        }

        //The two straight lines are parallel and the y-intercept matches
        if (isLineSegmentParallel(t1, t2, rhei) == ParallelJudgement.PARALLEL_EQUAL) {
            if (equal(p1, p3, rhit)) { //When the endpoints of two line segments overlap at one point
                if (isInside(p1, p4, p2) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321;
                }
                if (isInside(p3, p2, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322;
                }
                if (isInside(p2, p1, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323;
                }//Two line segments only overlap at one point, not at any other point
            }

            if (equal(p1, p4, rhit)) {
                if (isInside(p1, p3, p2) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331;
                }
                if (isInside(p4, p2, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332;
                }
                if (isInside(p2, p1, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333;
                }//Two line segments only overlap at one point, not at any other point
            }

            if (equal(p2, p3, rhit)) {
                if (isInside(p2, p4, p1) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341;
                }
                if (isInside(p3, p1, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342;
                }
                if (isInside(p1, p2, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343;
                }//Two line segments only overlap at one point, not at any other point
            }

            if (equal(p2, p4, rhit)) {
                if (isInside(p2, p3, p1) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351;
                }
                if (isInside(p4, p1, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352;
                }
                if (isInside(p1, p2, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353;
                }//Two line segments only overlap at one point, not at any other point
            }

            //When the endpoints of two line segments do not overlap
            if ((isInside(p1, p3, p4) == 2) && (isInside(p3, p4, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_361;
            }//Line segment (p1, p2) includes line segment (p3, p4)
            if ((isInside(p1, p4, p3) == 2) && (isInside(p4, p3, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_362;
            }//Line segment (p1, p2) includes line segment (p3, p4)

            if ((isInside(p3, p1, p2) == 2) && (isInside(p1, p2, p4) == 2)) {
                return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_363;
            }//Line segment (p3, p4) includes line segment (p1, p2)
            if ((isInside(p3, p2, p1) == 2) && (isInside(p2, p1, p4) == 2)) {
                return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_364;
            }//Line segment (p3, p4) includes line segment (p1, p2)


            if ((isInside(p1, p3, p2) == 2) && (isInside(p3, p2, p4) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_START_371;
            }//The P2 side of the line segment (p1, p2) and the P3 side of the line segment (p3, p4) partially overlap.
            if ((isInside(p1, p4, p2) == 2) && (isInside(p4, p2, p3) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_END_372;
            }//The P2 side of the line segment (p1, p2) and the P4 side of the line segment (p4, p3) partially overlap.

            if ((isInside(p3, p1, p4) == 2) && (isInside(p1, p4, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_END_373;
            }//The P4 side of the line segment (p3, p4) and the P1 side of the line segment (p1, p2) partially overlap.
            if ((isInside(p4, p1, p3) == 2) && (isInside(p1, p3, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_START_374;
            }//The P3 side of the line segment (p4, p3) and the P1 side of the line segment (p1, p2) partially overlap.

            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        return LineSegment.Intersection.ERROR;//This passes in case of some error 。
    }


    // The sweet part of senbun_kousa_hantei_amai is that if ((hakononaka (p1, pk, p2)> = 1) && (hakononaka (p3, pk, p4)> = 1), which is the premise of return 21 to return 28. )) Instead of
    // (hakononaka_amai (p1, pk, p2)> = 1) && (hakononaka_amai (p3, pk, p4) is used. Hakononaka_amai is
    // A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment with the two points p1 and p2 as the end points at the points p1 and p2. This is considered to be inside the rectangle even if it protrudes a little.
    // Specifically, when determining whether there is a point inside the line segment, if the point is slightly outside the line segment, it is judged to be sweet if it is inside the line segment. When drawing a crease pattern with a drawing craftsman, if you do not use this sweet one, the intersection division of the T-shaped line segment will fail
    // But for some reason, using this sweeter one for folding estimation seems to result in an infinite loop, which doesn't work. This exact elucidation is unresolved 20161105

    public static LineSegment.Intersection determineLineSegmentIntersectionSweet(LineSegment s1, LineSegment s2, double rhit, double rhei) {    //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
        double x1max = s1.determineAX();
        double x1min = s1.determineAX();
        double y1max = s1.determineAY();
        double y1min = s1.determineAY();
        if (x1max < s1.determineBX()) {
            x1max = s1.determineBX();
        }
        if (x1min > s1.determineBX()) {
            x1min = s1.determineBX();
        }
        if (y1max < s1.determineBY()) {
            y1max = s1.determineBY();
        }
        if (y1min > s1.determineBY()) {
            y1min = s1.determineBY();
        }
        double x2max = s2.determineAX();
        double x2min = s2.determineAX();
        double y2max = s2.determineAY();
        double y2min = s2.determineAY();
        if (x2max < s2.determineBX()) {
            x2max = s2.determineBX();
        }
        if (x2min > s2.determineBX()) {
            x2min = s2.determineBX();
        }
        if (y2max < s2.determineBY()) {
            y2max = s2.determineBY();
        }
        if (y2min > s2.determineBY()) {
            y2min = s2.determineBY();
        }

        if (x1max + rhit + Epsilon.POINT < x2min) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (x1min - rhit - Epsilon.POINT > x2max) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (y1max + rhit + Epsilon.POINT < y2min) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        if (y1min - rhit - Epsilon.POINT > y2max) {
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        Point p1 = s1.getA();
        Point p2 = s1.getB();
        Point p3 = s2.getA();
        Point p4 = s2.getB();

        StraightLine t1 = new StraightLine(p1, p2);
        StraightLine t2 = new StraightLine(p3, p4);

        //例外処理　線分s1と線分s2が点の場合
        if (((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))
                &&
                ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY()))) {
            if ((p1.getX() == p3.getX()) && (p1.getY() == p3.getY())) {
                return LineSegment.Intersection.INTERSECT_AT_POINT_4;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        //例外処理　線分s1が点の場合
        if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
            if ((isInside(p3, p1, p4) >= 1) && (t2.assignmentCalculation(p1) == 0.0)) {
                return LineSegment.Intersection.INTERSECT_AT_POINT_S1_5;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        //例外処理　線分s2が点の場合
        if ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY())) {
            if ((isInside(p1, p3, p2) >= 1) && (t1.assignmentCalculation(p3) == 0.0)) {
                return LineSegment.Intersection.INTERSECT_AT_POINT_S2_6;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        if (isLineSegmentParallel(t1, t2, rhei) == ParallelJudgement.NOT_PARALLEL) {    //２つの直線が平行でない
            Point pk = findIntersection(t1, t2);    //<<<<<<<<<<<<<<<<<<<<<<<
            if ((isInside_sweet(p1, pk, p2) >= 1)
                    && (isInside_sweet(p3, pk, p4) >= 1)) {
                if (equal(p1, p3, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_LSHAPE_S1_START_S2_START_21;
                }//L-shaped
                if (equal(p1, p4, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_LSHAPE_S1_START_S2_END_22;
                }//L字型
                if (equal(p2, p3, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_LSHAPE_S1_END_S2_START_23;
                }//L字型
                if (equal(p2, p4, rhit)) {
                    return LineSegment.Intersection.INTERSECTs_LSHAPE_S1_END_S2_END_24;
                }//L字型
                if (equal(p1, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25;
                }//T-shaped s1 is a vertical bar
                if (equal(p2, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26;
                }//T-shaped s1 is a vertical bar
                if (equal(p3, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27;
                }//T-shaped s2 is a vertical bar
                if (equal(p4, pk, rhit)) {
                    return LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28;
                }//T-shaped s2 is a vertical bar
                return LineSegment.Intersection.INTERSECTS_1;
            }
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        if (isLineSegmentParallel(t1, t2, rhei) == ParallelJudgement.PARALLEL_NOT_EQUAL) { //２つの直線が平行で、y切片は一致しない
            return LineSegment.Intersection.NO_INTERSECTION_0;
        }

        // The two line segments are exactly the same
        if (equal(p1, p3, rhit) && equal(p2, p4, rhit)) {
            return LineSegment.Intersection.PARALLEL_EQUAL_31;
        }
        if (equal(p1, p4, rhit) && equal(p2, p3, rhit)) {
            return LineSegment.Intersection.PARALLEL_EQUAL_31;
        }

        //The two straight lines are parallel and the y-intercept matches
        if (isLineSegmentParallel(t1, t2, rhei) == ParallelJudgement.PARALLEL_EQUAL) {
            if (equal(p1, p3, rhit)) { //2つの線分の端点どうしが1点で重なる場合
                if (isInside(p1, p4, p2) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321;
                }//A long line segment contains a short line segment
                if (isInside(p3, p2, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322;
                }//A long line segment contains a short line segment
                if (isInside(p2, p1, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_START_OF_S2_323;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p1, p4, rhit)) {
                if (isInside(p1, p3, p2) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331;
                }//長い線分に短い線分が含まれる
                if (isInside(p4, p2, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332;
                }//長い線分に短い線分が含まれる
                if (isInside(p2, p1, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S1_INTERSECTS_END_OF_S2_333;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p3, rhit)) {
                if (isInside(p2, p4, p1) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341;
                }//長い線分に短い線分が含まれる
                if (isInside(p3, p1, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342;
                }//長い線分に短い線分が含まれる
                if (isInside(p1, p2, p4) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_START_OF_S2_343;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p4, rhit)) {
                if (isInside(p2, p3, p1) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351;
                }//A long line segment contains a short line segment
                if (isInside(p4, p1, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352;
                }//長い線分に短い線分が含まれる
                if (isInside(p1, p2, p3) == 2) {
                    return LineSegment.Intersection.PARALLEL_END_OF_S1_INTERSECTS_END_OF_S2_353;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            //2つの線分の端点どうしが重ならない場合
            if ((isInside(p1, p3, p4) == 2) && (isInside(p3, p4, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_361;
            }//線分(p1,p2)に線分(p3,p4)が含まれる
            if ((isInside(p1, p4, p3) == 2) && (isInside(p4, p3, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_INCLUDES_S2_362;
            }//線分(p1,p2)に線分(p3,p4)が含まれる

            if ((isInside(p3, p1, p2) == 2) && (isInside(p1, p2, p4) == 2)) {
                return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_363;
            }//線分(p3,p4)に線分(p1,p2)が含まれる
            if ((isInside(p3, p2, p1) == 2) && (isInside(p2, p1, p4) == 2)) {
                return LineSegment.Intersection.PARALLEL_S2_INCLUDES_S1_364;
            }//線分(p3,p4)に線分(p1,p2)が含まれる

            if ((isInside(p1, p3, p2) == 2) && (isInside(p3, p2, p4) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_START_371;
            }
            if ((isInside(p1, p4, p2) == 2) && (isInside(p4, p2, p3) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_END_OVERLAPS_S2_END_372;
            }
            if ((isInside(p3, p1, p4) == 2) && (isInside(p1, p4, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_END_373;
            }
            if ((isInside(p4, p1, p3) == 2) && (isInside(p1, p3, p2) == 2)) {
                return LineSegment.Intersection.PARALLEL_S1_START_OVERLAPS_S2_START_374;
            }

            return LineSegment.Intersection.NO_INTERSECTION_0;
        }
        return LineSegment.Intersection.ERROR;//ここは何らかのエラーの時に通る。

    }

    //---------------------------
    public static LineSegment extendToIntersectionPoint_2(FoldLineSet foldLineSet, LineSegment s0) {//Extend s0 from point b in the opposite direction of a to the point where it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment(s0);

        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_point_distance = kousa_point.distance(add_sen.getA());

        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_intersection_flg;//元の線分を直線としたものと、他の線分の交差状態
        LineSegment.Intersection i_lineSegment_intersection_flg;//元の線分と、他の線分の交差状態

        for (var s : foldLineSet.getLineSegmentsIterable()) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(s);//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
            i_lineSegment_intersection_flg = OritaCalc.determineLineSegmentIntersectionSweet(s0, s, Epsilon.UNKNOWN_1EN5, Epsilon.UNKNOWN_1EN5);//20180408なぜかこの行の様にs0のままだと、i_senbun_kousa_flgがおかしくならない。

            if (i_intersection_flg.isIntersecting() && !i_lineSegment_intersection_flg.isEndpointIntersection()) {
                kousa_point = OritaCalc.findIntersection(tyoku1, s);
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_point_distance = kousa_point.distance(add_sen.getA());
                            add_sen = add_sen.withB(kousa_point);
                        }
                    }
                }

            }

            if (i_intersection_flg == StraightLine.Intersection.INCLUDED_3 && i_lineSegment_intersection_flg != LineSegment.Intersection.PARALLEL_EQUAL_31) {
                kousa_point = s.getA();
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_point_distance = kousa_point.distance(add_sen.getA());
                            add_sen = add_sen.withB(kousa_point);
                        }
                    }
                }

                kousa_point = s.getB();
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_point_distance = kousa_point.distance(add_sen.getA());
                            add_sen = add_sen.withB(kousa_point);
                        }
                    }
                }
            }
        }

        add_sen = add_sen.withA(s0.getB());
        return add_sen;
    }

    //Fully extend a line until it hits a line nearest to it
    public static LineSegment fullExtendUntilHit(FoldLineSet foldLineSet, LineSegment s0){
        LineSegment temp = lineSegmentChangeLength(s0, 0.5);
        Point point = temp.getA();
        temp = extendToIntersectionPoint_2(foldLineSet, temp);
        temp = new LineSegment(point, temp.determineFurthestEndpoint(point));
        temp.setColor(s0.getColor());
        return temp;
    }

    //A function that determines whether two straight lines are parallel.
    public static ParallelJudgement isLineSegmentParallel(StraightLine t1, StraightLine t2) {
        return isLineSegmentParallel(t1, t2, Epsilon.PARALLEL_FOR_EDIT);
    }

    //A function that determines whether two straight lines are parallel.
    public static ParallelJudgement isLineSegmentParallel(LineSegment s1, LineSegment s2) {
        return isLineSegmentParallel(new StraightLine(s1), new StraightLine(s2));
    }

    //A function that determines whether two line segments are parallel.
    public static ParallelJudgement isLineSegmentParallel(LineSegment s1, LineSegment s2, double r) {
        return isLineSegmentParallel(lineSegmentToStraightLine(s1), lineSegmentToStraightLine(s2), r);
    }

    public static ParallelJudgement isLineSegmentParallel(StraightLine t1, StraightLine t2, double r) {//rは誤差の許容度。rが負なら厳密判定。
        //0 = not parallel, 1 = parallel and 2 straight lines do not match, 2 = parallel and 2 straight lines match
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        //厳密に判定----------------------------------------
        if (r <= 0.0) {
            //２直線が平行の場合
            if (a1 * b2 - a2 * b1 == 0) {
                //２直線は同一の場合
                if ((a1 * a1 + b1 * b1) * c2 * c2 == (a2 * a2 + b2 * b2) * c1 * c1) {
                    return ParallelJudgement.PARALLEL_EQUAL;
                }//厳密に判定。
                //２直線が異なる場合
                else {
                    return ParallelJudgement.PARALLEL_NOT_EQUAL;
                }
            }
        }

        //誤差を許容----------------------------------------
        if (r > 0) {
            //２直線が平行の場合
            if (Math.abs(a1 * b2 - a2 * b1) < r) {
                //２直線は同一の場合


                //原点（0、0）と各直線との距離を比較
                double kyoriT = t2.calculateDistance(t1.findProjection(new Point(0.0, 0.0)));//t1上の点とt2との距離//t1.kage_motome(new Ten(0.0,0.0))   は点（0,0）のt1上の影を求める（t1上の点ならなんでもいい）//20181115修正


                if (kyoriT < r) {//誤差を許容。
                    return ParallelJudgement.PARALLEL_EQUAL;
                }
                //２直線が異なる場合
                else {
                    return ParallelJudgement.PARALLEL_NOT_EQUAL;
                }
            }
        }

        //When two straight lines are non-parallel-------------------------------------------------
        return ParallelJudgement.NOT_PARALLEL;
    }

    //Function to find the intersection of two straight lines
    public static Point findIntersection(StraightLine t1, StraightLine t2) {
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }

    //Function to find the intersection of two straight lines (replication)
    public static Point findIntersection_01(StraightLine t1, StraightLine t2) {
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。
        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }

    public static StraightLine lineSegmentToStraightLine(LineSegment s) {//Get a straight line containing a line segment
        return new StraightLine(s.getA(), s.getB());
    }

    //A function that finds the intersection of two line segments as a straight line. Even if it does not intersect as a line segment, it returns the intersection when it intersects as a straight line
    public static Point findIntersection(LineSegment s1, LineSegment s2) {
        return findIntersection(lineSegmentToStraightLine(s1), lineSegmentToStraightLine(s2));
    }

    //A function that considers a line segment as a straight line and finds the intersection with another straight line. Even if it does not intersect as a line segment, it returns the intersection when it intersects as a straight line
    public static Point findIntersection(StraightLine t1, LineSegment s2) {
        return findIntersection(t1, lineSegmentToStraightLine(s2));
    }

    //A function that considers a line segment as a straight line and finds the intersection with another straight line. Even if it does not intersect as a line segment, it returns the intersection when it intersects as a straight line
    public static Point findIntersection(LineSegment s1, StraightLine t2) {
        return findIntersection(lineSegmentToStraightLine(s1), t2);
    }

    /**
     * Check if a line segment is fully contained inside a GeneralPath.
     * @param path a GeneralPath
     * @param lineSegment a target line segment
     * @return if the line is fully contained
     */
    public static boolean isSegmentContainedInGeneralPath(GeneralPath path, Line2D lineSegment) {
        if (!path.contains(lineSegment.getP1()) || !path.contains(lineSegment.getP2())) return false;

        PathIterator pathIterator = path.getPathIterator(null);
        double[] coords = new double[2];
        Point2D.Double lastPoint;
        Point2D.Double currentPoint = new Point2D.Double();

        while (!pathIterator.isDone()) {
            int segmentType = pathIterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    currentPoint.setLocation(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    lastPoint = (Point2D.Double) currentPoint.clone();
                    currentPoint.setLocation(coords[0], coords[1]);
                    Line2D pathSegment = new Line2D.Double(lastPoint, currentPoint);
                    if (lineSegment.intersectsLine(pathSegment)) return false;
                    break;
                case PathIterator.SEG_CLOSE: break;
            }
            pathIterator.next();
        }

        return true;
    }

    //A function that moves a line segment in parallel to the side (returns a new line segment without changing the original line segment)
    public static LineSegment moveParallel(LineSegment s, double d) {
        StraightLine ta = new StraightLine(s.getA(), s.getB()).orthogonalize(s.getA());
        StraightLine tb = new StraightLine(s.getA(), s.getB()).orthogonalize(s.getB());
        StraightLine td = new StraightLine(s.getA(), s.getB()).translate(d);

        return new LineSegment(findIntersection_01(ta, td), findIntersection_01(tb, td));
    }

    //------------------------------------
    //A function that returns a point obtained by rotating point b by d degrees around point a (returns a new point without changing the original point)
    public static Point point_rotate(Point a, Point b, double d) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = Mcd * (b.getX() - a.getX()) - Msd * (b.getY() - a.getY()) + a.getX();
        double by1 = Msd * (b.getX() - a.getX()) + Mcd * (b.getY() - a.getY()) + a.getY();

        return new Point(bx1, by1);
    }

    //------------------------------------
    //A function that rotates point b by d degrees around point a and returns a point whose ab distance is r times (returns a new point without changing the original point)
    public static Point point_rotate(Point a, Point b, double d, double r) {

        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = r * (Mcd * (b.getX() - a.getX()) - Msd * (b.getY() - a.getY())) + a.getX();
        double by1 = r * (Msd * (b.getX() - a.getX()) + Mcd * (b.getY() - a.getY())) + a.getY();

        return new Point(bx1, by1);
    }

    //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public static LineSegment lineSegment_rotate(LineSegment s0, double d) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = Mcd * (s0.determineBX() - s0.determineAX()) - Msd * (s0.determineBY() - s0.determineAY()) + s0.determineAX();
        double by1 = Msd * (s0.determineBX() - s0.determineAX()) + Mcd * (s0.determineBY() - s0.determineAY()) + s0.determineAY();

        double ax1 = s0.determineAX();
        double ay1 = s0.determineAY();

        return new LineSegment(ax1, ay1, bx1, by1, s0.getColor());
    }

    //線分abをaを中心にr倍してd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public static LineSegment lineSegment_rotate(LineSegment s0, double d, double r) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = r * (Mcd * (s0.determineBX() - s0.determineAX()) - Msd * (s0.determineBY() - s0.determineAY())) + s0.determineAX();
        double by1 = r * (Msd * (s0.determineBX() - s0.determineAX()) + Mcd * (s0.determineBY() - s0.determineAY())) + s0.determineAY();

        double ax1 = s0.determineAX();
        double ay1 = s0.determineAY();

        return new LineSegment(ax1, ay1, bx1, by1);
    }

    /**
     * A function that returns a line segment obtained by multiplying the line segment ab by lengthMultiplier with a as the center
     * (returns a new line segment without changing the original line segment)
     */

    public static LineSegment changeLength(LineSegment s0, double lengthMultiplier) {

        double bx1 = lengthMultiplier * (s0.determineBX() - s0.determineAX()) + s0.determineAX();
        double by1 = lengthMultiplier * (s0.determineBY() - s0.determineAY()) + s0.determineAY();

        double ax1 = s0.determineAX();
        double ay1 = s0.determineAY();

        return new LineSegment(ax1, ay1, bx1, by1);
    }

    //A function to find the line segment B at the control position of the line segment A with the line segment J as the axis.
    public static LineSegment findLineSymmetryLineSegment(LineSegment s0, LineSegment axis) {
        Point p_a = s0.getA();
        Point p_b = s0.getB();
        Point axisA = axis.getA();
        Point axisB = axis.getB();

        return new LineSegment(
                findLineSymmetryPoint(axisA, axisB, p_a),
                findLineSymmetryPoint(axisA, axisB, p_b));
    }

    //A function that finds a point at the control position of point p with respect to a straight line passing through two points t1 and t2.
    public static Point findLineSymmetryPoint(Point t1, Point t2, Point p) {
        Point p1;  // p1.set(s.geta());

        StraightLine s1 = new StraightLine(t1, t2);
        //Find the straight line s2 that passes through the point p and is orthogonal to s1.
        StraightLine s2 = s1.orthogonalize(p);

        p1 = findIntersection(s1, s2);
        return new Point(2.0 * p1.getX() - p.getX(), 2.0 * p1.getY() - p.getY());
    }

    //A function that keeps the angle greater than -180.0 degrees and less than 180.0 degrees
    public static double angle_between_m180_180(double angle) {
        while (angle <= -180.0) {
            angle = angle + 360.0;
        }
        while (angle > 180.0) {
            angle = angle - 360.0;
        }
        return angle;
    }

    //A function that keeps the angle between 0.0 degrees and 360.0 degrees
    public static double angle_between_0_360(double angle) {
        while (angle < 0.0) {
            angle = angle + 360.0;
        }
        while (angle >= 360.0) {
            angle = angle - 360.0;
        }
        return angle;
    }

    //角度を0.0度以上kmax度未満に押さえる関数(円錐の頂点の伏見定理などで使う)
    public static double angle_between_0_kmax(double angle, double kmax) {
        while (angle < 0.0) {
            angle = angle + kmax;
        }
        while (angle >= kmax) {
            angle = angle - kmax;
        }
        return angle;
    }

    //The angle between the line segments s1 and s2
    public static double angle(LineSegment s1, LineSegment s2) {
        Point a = s1.getA();
        Point b = s1.getB();
        Point c = s2.getA();
        Point d = s2.getB();

        return angle_between_0_360(angle(c, d) - angle(a, b));
    }

    //Angle between vectors ab and cd
    public static double angle(Point a, Point b, Point c, Point d) {
        return angle_between_0_360(angle(c, d) - angle(a, b));
    }

    /**
     * Find the inner heart of the triangle
     */
    public static Point center(Point ta, Point tb, Point tc) {
        double A, B, C, XA, XB, XC, YA, YB, YC, XD, YD, XE, YE, G, H, K, L, P, Q, XN, YN;
        XA = ta.getX();
        YA = ta.getY();
        XB = tb.getX();
        YB = tb.getY();
        XC = tc.getX();
        YC = tc.getY();

        A = Math.sqrt((XC - XB) * (XC - XB) + (YC - YB) * (YC - YB));
        B = Math.sqrt((XA - XC) * (XA - XC) + (YA - YC) * (YA - YC));
        C = Math.sqrt((XB - XA) * (XB - XA) + (YB - YA) * (YB - YA));
        XD = (C * XC + B * XB) / (B + C);
        YD = (C * YC + B * YB) / (B + C);
        XE = (C * XC + A * XA) / (A + C);
        YE = (C * YC + A * YA) / (A + C);
        G = XD - XA;
        H = YD - YA;
        K = XE - XB;
        L = YE - YB;
        P = G * YA - H * XA;
        Q = K * YB - L * XB;
        XN = (G * Q - K * P) / (H * K - G * L);
        YN = (L * P - H * Q) / (G * L - H * K);

        return new Point(XN, YN);
    }

    // -------------------------------
    //Find the internal division point.
    public static Point internalDivisionRatio(Point a, Point b, double d_internalDivisionRatio_s, double d_internalDivisionRatio_t) {
        Point r_point = new Point(-10000.0, -10000.0);
        if (distance(a, b) < Epsilon.UNKNOWN_1EN6) {
            return r_point;
        }

        if (d_internalDivisionRatio_s == 0.0) {
            if (d_internalDivisionRatio_t == 0.0) {
                return r_point;
            } else {
                return a;
            }
        } else {
            if (d_internalDivisionRatio_t == 0.0) {
                return b;
            } else {
                LineSegment s_ab = new LineSegment(a, b);
                double nx = (d_internalDivisionRatio_t * s_ab.determineAX() + d_internalDivisionRatio_s * s_ab.determineBX()) / (d_internalDivisionRatio_s + d_internalDivisionRatio_t);
                double ny = (d_internalDivisionRatio_t * s_ab.determineAY() + d_internalDivisionRatio_s * s_ab.determineBY()) / (d_internalDivisionRatio_s + d_internalDivisionRatio_t);
                return new Point(nx, ny);
            }
        }
    }

    /**
     * -------------------------------
     * Find the midpoint.
     */
    public static Point midPoint(Point a, Point b) {

        return new Point((a.getX() + b.getX()) / 2.0, (a.getY() + b.getY()) / 2.0);
    }

    // -------------------------------
    public static StraightLine circle_to_circle_no_intersection_wo_tooru_straightLine(Circle e1, Circle e2) {
        double x1 = e1.getX();
        double y1 = e1.getY();
        double r1 = e1.getR();
        double x2 = e2.getX();
        double y2 = e2.getY();
        double r2 = e2.getR();

        double a = 2.0 * x1 - 2.0 * x2;
        double b = 2.0 * y1 - 2.0 * y2;
        double c = x2 * x2 - x1 * x1 + y2 * y2 - y1 * y1 + r1 * r1 - r2 * r2;

        return new StraightLine(a, b, c);
    }

    // -------------------------------
    public static LineSegment circle_to_circle_no_intersection_wo_musubu_lineSegment(Circle e1, Circle e2) {
        StraightLine t0 = circle_to_circle_no_intersection_wo_tooru_straightLine(e1, e2);
        StraightLine t1 = new StraightLine(e1.determineCenter(), e2.determineCenter());
        Point intersection_t0t1 = findIntersection(t0, t1);
        double length_a = t0.calculateDistance(e1.determineCenter());  //t0とt1の交点からe1の中心までの長さ

//double length_a=kyori(intersection_t0t1,e1.get_tyuusin());  //t0とt1の交点からe1の中心までの長さ
        double length_b = Math.sqrt(e1.getR() * e1.getR() - length_a * length_a); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがlength_bのものは(t0.getb()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

        return new LineSegment(
                intersection_t0t1.getX() + t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getY() - t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getX() - t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getY() + t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA())
        );
    }

    // --------qqqqqqqqqqqqqqq-----------------------
    public static LineSegment circle_to_straightLine_no_intersect_wo_connect_LineSegment(Circle e1, StraightLine t0) {

        Point kouten_t0t1 = findProjection(t0, e1.determineCenter());
        double length_a = t0.calculateDistance(e1.determineCenter());  //t0とt1の交点からe1の中心までの長さ

        double length_b = Math.sqrt(e1.getR() * e1.getR() - length_a * length_a); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがnagasa_bのものは(t0.getb()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

        return new LineSegment(
                kouten_t0t1.getX() + t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getY() - t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getX() - t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getY() + t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA())
        );
    }

    // Function to find the distance between the point p0 and the circumference of the circle e0 ------------------------------- --------------------- ---------------------
    public static double distance_circumference(Point p0, Circle e0) {
        return Math.abs(distance(p0, e0.determineCenter()) - e0.getR());
    }

    //Minを返す関数
    public static double min(double d1, double d2, double d3, double d4) {
        double min_d = d1;
        if (min_d > d2) {
            min_d = d2;
        }
        if (min_d > d3) {
            min_d = d3;
        }
        if (min_d > d4) {
            min_d = d4;
        }
        return min_d;
    }

    public static LineSegment bisection(Point t1, Point t2, double d0) {
        Point tm = new Point((t1.getX() + t2.getX()) / 2.0, (t1.getY() + t2.getY()) / 2.0);

        double bai = d0 / distance(t1, t2);

        LineSegment s1 = lineSegment_rotate(new LineSegment(tm, t1), 90.0, bai);
        LineSegment s2 = lineSegment_rotate(new LineSegment(tm, t2), 90.0, bai);

        return new LineSegment(s1.getB(), s2.getB());
    }

    //--------------------------------------------------------
    public static boolean isLineSegmentOverlapping(LineSegment s1, LineSegment s2) {//false do not overlap. true overlaps. 20201012 added
        LineSegment.Intersection intersection = determineLineSegmentIntersection(s1, s2, Epsilon.UNKNOWN_1EN4);

        return intersection.isSegmentOverlapping();
    }

    //--------------------------------------------------------
    public static boolean lineSegment_X_kousa_decide(LineSegment s1, LineSegment s2) {//0はX交差しない。1は交差する。20201017追加
        return determineLineSegmentIntersection(s1, s2, Epsilon.UNKNOWN_1EN4) == LineSegment.Intersection.INTERSECTS_1;
    }

    /**
     * Return a lineSegment with a certain length (assuming A is the starting point).
     * @param s a LineSegment
     * @param newLength a double value for desired length. Use negative value to flip the segment.
     * @return a LineSegment with new endpoint to match the length
     */

    public static LineSegment lineSegmentChangeLength(LineSegment s, double newLength) {
        double dx = s.determineBX() - s.determineAX();
        double dy = s.determineBY() - s.determineAY();
        double newDx = dx / s.determineLength() * newLength;
        double nexDy = dy / s.determineLength() * newLength;
        return new LineSegment(s.getA(), new Point(s.getA().getX() + newDx, s.getA().getY() + nexDy));
    }

    /**
     * Check if a Point is within the span of a LineSegment. The span is an imaginary line infinitely expanded from the LineSegment
     * @param p0 a target Point
     * @param s0 a LineSegment
     * @return true if the target point is within the span, false if otherwise
     */
    public static boolean isPointWithinLineSpan(Point p0, LineSegment s0){ // Check if point p0 is within the span of segment s0
        if(OritaCalc.distance(p0, s0.getA()) < Epsilon.UNKNOWN_1EN7 ||
                OritaCalc.distance(p0, s0.getB()) < Epsilon.UNKNOWN_1EN7){
            return true;
        }
        LineSegment temp = new LineSegment(p0, s0.determineClosestEndpoint(p0));
        return OritaCalc.isLineSegmentParallel(temp, s0) == ParallelJudgement.PARALLEL_EQUAL;
    }

    /**
     * Check if a Point is within the span of a LineSegment (formed by 2 Points). The span is an imaginary line infinitely expanded from the LineSegment
     * @param p0 a target Point
     * @param p1 an endpoint of the LineSegment
     * @param p2 other endpoint of the LineSegment
     * @return true if the target point is within the span, false if otherwise
     */
    public static boolean isPointWithinLineSpan(Point p0, Point p1, Point p2){
        return isPointWithinLineSpan(p0, new LineSegment(p1, p2));
    }

    //--------------------------------------------------------

    public enum ParallelJudgement {
        NOT_PARALLEL,
        PARALLEL_NOT_EQUAL,
        PARALLEL_EQUAL,
    }
}
