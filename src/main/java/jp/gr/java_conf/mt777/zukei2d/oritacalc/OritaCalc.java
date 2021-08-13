package jp.gr.java_conf.mt777.zukei2d.oritacalc;

import jp.gr.java_conf.mt777.zukei2d.en.Circle;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.StraightLine;
import jp.gr.java_conf.mt777.zukei2d.senbun.LineSegment;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OritaCalc {
    //Change d2s double to string Rounded to the second decimal place (""); d2s
    public String d2s(double d0) {
        BigDecimal bd = new BigDecimal(d0);

        //Rounded to the first decimal place
        BigDecimal bd1 = bd.setScale(1, RoundingMode.HALF_UP);

        return bd1.toString();
    }


    //Just System.out.println("String");
    public void display(String s0) {
        System.out.println(s0);
    }


    //Find the position of the shadow of the point p on the straight line t (the position on the straight line t closest to the point p).
    public Point shadow_request(StraightLine t, Point p) {
        StraightLine t1 = new StraightLine();
        t1.set(t);
        t1.orthogonalize(p);//Find the straight line u1 that passes through the point p1 and is orthogonal to t.
        return findIntersection(t, t1);
    }

    //Find the position of the shadow of the point p on the straight line t passing through the points P0 and P1 (the position on the straight line t closest to the point p).
    public Point shadow_request(Point p0, Point p1, Point p) {
        StraightLine t = new StraightLine(p0, p1);
        return shadow_request(t, p);
    }

    //Find the position of the shadow of the point p on the straight line t including the line segment s0 (the position on the straight line t closest to the point p).
    public Point shadow_request(LineSegment s0, Point p) {
        return shadow_request(s0.getA(), s0.getB(), p);
    }


    //A function that determines whether two points are in the same position (true) or different (false) -------------------------------- -
    public boolean equal(Point p1, Point p2) {
        return equal(p1, p2, 0.1);//The error is defined here.
    }

    public boolean equal(Point p1, Point p2, double r) {//r is the error tolerance. Strict judgment if r is negative.
        //Strict judgment。
        if (r <= 0.0) {
            if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
                return true;
            }
        }
        //Tolerate error。
        if (r > 0) {
            if (distance(p1, p2) <= r) {
                return true;
            }
        }
        return false;
    }

    //Function to find the distance (integer) between two points----------------------------------------------------
    public double distance(Point p0, Point p1) {
        return p0.distance(p1);
    }

    //A function that finds the angle between the vector ab and the x-axis by specifying a and b between two points. If a = b, return -10000.0 ----------------------------------------- -----------
    public double angle(Point a, Point b) {
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
    public double angle(LineSegment s) {
        return angle(s.getA(), s.getB());
    }

    //A function that specifies the line segment and finds the angle between the vector ab and the x-axis. Returns -10000.0 if a = b----------------------------------------------------
    public double angle_difference(LineSegment s, double a) {
        double b;//Residual when the actual angle is divided by a
        b = angle(s) % a;
        if (a - b < b) {
            b = a - b;
        }
        return b;
    }

    //A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment ending at the two points p1 and p2 at the points p1 and p2.
    public int isInside(Point p1, Point pa, Point p2) {
        StraightLine u1 = new StraightLine(p1, p2);
        u1.orthogonalize(p1);//Find the straight line u1 that passes through the point p1 and is orthogonal to t.
        StraightLine u2 = new StraightLine(p1, p2);
        u2.orthogonalize(p2);//Find the straight line u2 that passes through the point p2 and is orthogonal to t.

        if (u1.assignmentCalculation(pa) * u2.assignmentCalculation(pa) == 0.0) {
            return 1;
        }
        if (u1.assignmentCalculation(pa) * u2.assignmentCalculation(pa) < 0.0) {
            return 2;
        }
        return 0;//If outside the box
    }


    // A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment ending at the two points p1 and p2 at the points p1 and p2. This is considered to be inside the rectangle even if it protrudes a little.
    // Specifically, when determining whether there is a point inside the line segment, if the point is slightly outside the line segment, it is judged to be sweet if it is inside the line segment. When drawing a development drawing with a drawing craftsman, if you do not use this sweet one, the intersection division of the T-shaped line segment will fail
    // But for some reason, using this sweeter one for folding estimation seems to result in an infinite loop, which doesn't work. This exact elucidation is unresolved 20161105
    public int isInside_sweet(Point p1, Point pa, Point p2) {
        StraightLine u1 = new StraightLine(p1, p2);
        u1.orthogonalize(p1);//Find the straight line u1 that passes through the point p1 and is orthogonal to t.
        StraightLine u2 = new StraightLine(p1, p2);
        u2.orthogonalize(p2);//Find the straight line u2 that passes through the point p2 and is orthogonal to t.

        if (u1.calculateDistance(pa) < 0.00001) {
            return 1;
        }
        if (u2.calculateDistance(pa) < 0.00001) {
            return 1;
        }

        if (u1.assignmentCalculation(pa) * u2.assignmentCalculation(pa) < 0.0) {
            return 2;
        }
        return 0;//If outside the box
    }


    // A function that determines where the point p is close to the specified line segment (within r) ------------------------ ---------
    // 0 = not close, 1 = close to point a, 2 = close to point b, 3 = close to handle
    public int lineSegment_endpoint_search(Point p, LineSegment s0, double r) {
        if (r > distance(p, s0.getA())) {
            return 1;
        }//Whether it is close to point a
        if (r > distance(p, s0.getB())) {
            return 2;
        }//Whether it is close to point b
        if (r > distance_lineSegment(p, s0)) {
            return 3;
        }//Whether it is close to the handle
        return 0;
    }


    //Function to find the distance between the point p0 and the line segment with the two points p1 and p2 at both ends --------------------------- -------------------------
    public double distance_lineSegment(Point p0, Point p1, Point p2) {
        //When p1 and p2 are the same
        if (distance(p1, p2) == 0.0) {
            return distance(p0, p1);
        }

        //When p1 and p2 are different
        StraightLine t = new StraightLine(p1, p2);//p1,Find the straight line t passing through p2。
        StraightLine u = new StraightLine(p1, p2);
        u.orthogonalize(p0);//Find a straight line u that passes through the point p0 and is orthogonal to t.

        if (isInside(p1, findIntersection(t, u), p2) >= 1) {
            return t.calculateDistance(p0);
        }//When the intersection of t and u is between p1 and p2.
        return Math.min(distance(p0, p1), distance(p0, p2));//When the intersection of t and u is not between p1 and p2.
    }

    //A function that finds the distance between the point p0 and the line segment s ----------------------------------- -----------------
    public double distance_lineSegment(Point p0, LineSegment s) {
        Point p1 = new Point();
        p1.set(s.getA());
        Point p2 = new Point();
        p2.set(s.getB());
        return distance_lineSegment(p0, p1, p2);
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
    public int line_intersect_decide(LineSegment s1, LineSegment s2) {
        return line_intersect_decide(s1, s2, 0.01, 0.01);
    }

    public int line_intersect_decide_sweet(LineSegment s1, LineSegment s2) {
        return line_intersect_decide_sweet(s1, s2, 0.01, 0.01);
    }

    public int line_intersect_decide(LineSegment s1, LineSegment s2, double rhit, double rhei) {    //r_hitosii and r_heikouhantei are the allowable degree of deviation between hitosii and heikou_hantei
        double x1max = s1.getAX();
        double x1min = s1.getAX();
        double y1max = s1.getAY();
        double y1min = s1.getAY();
        if (x1max < s1.getBX()) {
            x1max = s1.getBX();
        }
        if (x1min > s1.getBX()) {
            x1min = s1.getBX();
        }
        if (y1max < s1.getBY()) {
            y1max = s1.getBY();
        }
        if (y1min > s1.getBY()) {
            y1min = s1.getBY();
        }
        double x2max = s2.getAX();
        double x2min = s2.getAX();
        double y2max = s2.getAY();
        double y2min = s2.getAY();
        if (x2max < s2.getBX()) {
            x2max = s2.getBX();
        }
        if (x2min > s2.getBX()) {
            x2min = s2.getBX();
        }
        if (y2max < s2.getBY()) {
            y2max = s2.getBY();
        }
        if (y2min > s2.getBY()) {
            y2min = s2.getBY();
        }

        if (x1max + rhit + 0.1 < x2min) {
            return 0;
        }
        if (x1min - rhit - 0.1 > x2max) {
            return 0;
        }
        if (y1max + rhit + 0.1 < y2min) {
            return 0;
        }
        if (y1min - rhit - 0.1 > y2max) {
            return 0;
        }

        Point p1 = new Point();
        p1.set(s1.getA());
        Point p2 = new Point();
        p2.set(s1.getB());
        Point p3 = new Point();
        p3.set(s2.getA());
        Point p4 = new Point();
        p4.set(s2.getB());

        StraightLine t1 = new StraightLine(p1, p2);
        StraightLine t2 = new StraightLine(p3, p4);

        //Exception handling: When line segment s1 and line segment s2 are points
        if (((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))
                &&
                ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY()))) {
            if ((p1.getX() == p3.getX()) && (p1.getY() == p3.getY())) {
                return 4;
            }
            return 0;
        }

        //Exception handling: When the line segment s1 is a point
        if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
            if ((isInside(p3, p1, p4) >= 1) && (t2.assignmentCalculation(p1) == 0.0)) {
                return 5;
            }
            return 0;
        }

        //Exception handling: When the line segment s2 is a point
        if ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY())) {
            if ((isInside(p1, p3, p2) >= 1) && (t1.assignmentCalculation(p3) == 0.0)) {
                return 6;
            }
            return 0;
        }

        if (parallel_judgement(t1, t2, rhei) == ParallelJudgement.NOT_PARALLEL) {    //Two straight lines are not parallel
            Point pk = new Point();
            pk.set(findIntersection(t1, t2));    //<<<<<<<<<<<<<<<<<<<<<<<
            if ((isInside(p1, pk, p2) >= 1)
                    && (isInside(p3, pk, p4) >= 1)) {
                if (equal(p1, p3, rhit)) {
                    return 21;
                }//L-shaped
                if (equal(p1, p4, rhit)) {
                    return 22;
                }//L-shaped
                if (equal(p2, p3, rhit)) {
                    return 23;
                }//L-shaped
                if (equal(p2, p4, rhit)) {
                    return 24;
                }//L-shaped
                if (equal(p1, pk, rhit)) {
                    return 25;
                }//T-shaped s1 is a vertical bar
                if (equal(p2, pk, rhit)) {
                    return 26;
                }//T-shaped s1 is a vertical bar
                if (equal(p3, pk, rhit)) {
                    return 27;
                }//T-shaped s2 is a vertical bar
                if (equal(p4, pk, rhit)) {
                    return 28;
                }//T-shaped s2 is a vertical bar
                return 1;                    // <<<<<<<<<<<<<<<<< return 1;
            }
            return 0;
        }

        if (parallel_judgement(t1, t2, rhei) == ParallelJudgement.PARALLEL_NOT_EQUAL) { //Two straight lines are parallel and y-intercept does not match
            return 0;
        }

        // The two line segments are exactly the same
        if (equal(p1, p3, rhit) && equal(p2, p4, rhit)) {
            return 31;
        }
        if (equal(p1, p4, rhit) && equal(p2, p3, rhit)) {
            return 31;
        }

        //The two straight lines are parallel and the y-intercept matches
        if (parallel_judgement(t1, t2, rhei) == ParallelJudgement.PARALLEL_EQUAL) {
            if (equal(p1, p3, rhit)) { //When the endpoints of two line segments overlap at one point
                if (isInside(p1, p4, p2) == 2) {
                    return 321;
                }
                if (isInside(p3, p2, p4) == 2) {
                    return 322;
                }
                if (isInside(p2, p1, p4) == 2) {
                    return 323;
                }//Two line segments only overlap at one point, not at any other point
            }

            if (equal(p1, p4, rhit)) {
                if (isInside(p1, p3, p2) == 2) {
                    return 331;
                }
                if (isInside(p4, p2, p3) == 2) {
                    return 332;
                }
                if (isInside(p2, p1, p3) == 2) {
                    return 333;
                }//Two line segments only overlap at one point, not at any other point
            }

            if (equal(p2, p3, rhit)) {
                if (isInside(p2, p4, p1) == 2) {
                    return 341;
                }
                if (isInside(p3, p1, p4) == 2) {
                    return 342;
                }
                if (isInside(p1, p2, p4) == 2) {
                    return 343;
                }//Two line segments only overlap at one point, not at any other point
            }

            if (equal(p2, p4, rhit)) {
                if (isInside(p2, p3, p1) == 2) {
                    return 351;
                }
                if (isInside(p4, p1, p3) == 2) {
                    return 352;
                }
                if (isInside(p1, p2, p3) == 2) {
                    return 353;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            //2つの線分の端点どうしが重ならない場合
            if ((isInside(p1, p3, p4) == 2) && (isInside(p3, p4, p2) == 2)) {
                return 361;
            }//線分(p1,p2)に線分(p3,p4)が含まれる
            if ((isInside(p1, p4, p3) == 2) && (isInside(p4, p3, p2) == 2)) {
                return 362;
            }//線分(p1,p2)に線分(p3,p4)が含まれる

            if ((isInside(p3, p1, p2) == 2) && (isInside(p1, p2, p4) == 2)) {
                return 363;
            }//線分(p3,p4)に線分(p1,p2)が含まれる
            if ((isInside(p3, p2, p1) == 2) && (isInside(p2, p1, p4) == 2)) {
                return 364;
            }//線分(p3,p4)に線分(p1,p2)が含まれる


            if ((isInside(p1, p3, p2) == 2) && (isInside(p3, p2, p4) == 2)) {
                return 371;
            }//線分(p1,p2)のP2側と線分(p3,p4)のP3側が部分的に重なる
            if ((isInside(p1, p4, p2) == 2) && (isInside(p4, p2, p3) == 2)) {
                return 372;
            }//線分(p1,p2)のP2側と線分(p4,p3)のP4側が部分的に重なる

            if ((isInside(p3, p1, p4) == 2) && (isInside(p1, p4, p2) == 2)) {
                return 373;
            }//線分(p3,p4)のP4側と線分(p1,p2)のP1側が部分的に重なる
            if ((isInside(p4, p1, p3) == 2) && (isInside(p1, p3, p2) == 2)) {
                return 374;
            }//線分(p4,p3)のP3側と線分(p1,p2)のP1側が部分的に重なる

            return 0;
        }
        return -1;//This passes in case of some error 。

    }


    // The sweet part of senbun_kousa_hantei_amai is that if ((hakononaka (p1, pk, p2)> = 1) && (hakononaka (p3, pk, p4)> = 1), which is the premise of return 21 to return 28. )) Instead of
    // (hakononaka_amai (p1, pk, p2)> = 1) && (hakononaka_amai (p3, pk, p4) is used. Hakononaka_amai is
    // A function that returns 2 if the point pa is in a rectangle containing two line segments that is orthogonal to the line segment with the two points p1 and p2 as the end points at the points p1 and p2. This is considered to be inside the rectangle even if it protrudes a little.
    // Specifically, when determining whether there is a point inside the line segment, if the point is slightly outside the line segment, it is judged to be sweet if it is inside the line segment. When drawing a development drawing with a drawing craftsman, if you do not use this sweet one, the intersection division of the T-shaped line segment will fail
    // But for some reason, using this sweeter one for folding estimation seems to result in an infinite loop, which doesn't work. This exact elucidation is unresolved 20161105

    public int line_intersect_decide_sweet(LineSegment s1, LineSegment s2, double rhit, double rhei) {    //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
        double x1max = s1.getAX();
        double x1min = s1.getAX();
        double y1max = s1.getAY();
        double y1min = s1.getAY();
        if (x1max < s1.getBX()) {
            x1max = s1.getBX();
        }
        if (x1min > s1.getBX()) {
            x1min = s1.getBX();
        }
        if (y1max < s1.getBY()) {
            y1max = s1.getBY();
        }
        if (y1min > s1.getBY()) {
            y1min = s1.getBY();
        }
        double x2max = s2.getAX();
        double x2min = s2.getAX();
        double y2max = s2.getAY();
        double y2min = s2.getAY();
        if (x2max < s2.getBX()) {
            x2max = s2.getBX();
        }
        if (x2min > s2.getBX()) {
            x2min = s2.getBX();
        }
        if (y2max < s2.getBY()) {
            y2max = s2.getBY();
        }
        if (y2min > s2.getBY()) {
            y2min = s2.getBY();
        }

        if (x1max + rhit + 0.1 < x2min) {
            return 0;
        }
        if (x1min - rhit - 0.1 > x2max) {
            return 0;
        }
        if (y1max + rhit + 0.1 < y2min) {
            return 0;
        }
        if (y1min - rhit - 0.1 > y2max) {
            return 0;
        }

        Point p1 = new Point();
        p1.set(s1.getA());
        Point p2 = new Point();
        p2.set(s1.getB());
        Point p3 = new Point();
        p3.set(s2.getA());
        Point p4 = new Point();
        p4.set(s2.getB());

        StraightLine t1 = new StraightLine(p1, p2);
        StraightLine t2 = new StraightLine(p3, p4);

        //例外処理　線分s1と線分s2が点の場合
        if (((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))
                &&
                ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY()))) {
            if ((p1.getX() == p3.getX()) && (p1.getY() == p3.getY())) {
                return 4;
            }
            return 0;
        }

        //例外処理　線分s1が点の場合
        if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
            if ((isInside(p3, p1, p4) >= 1) && (t2.assignmentCalculation(p1) == 0.0)) {
                return 5;
            }
            return 0;
        }

        //例外処理　線分s2が点の場合
        if ((p3.getX() == p4.getX()) && (p3.getY() == p4.getY())) {
            if ((isInside(p1, p3, p2) >= 1) && (t1.assignmentCalculation(p3) == 0.0)) {
                return 6;
            }
            return 0;
        }

        // System.out.println("AAAAAAAAAAAA");
        if (parallel_judgement(t1, t2, rhei) == ParallelJudgement.NOT_PARALLEL) {    //２つの直線が平行でない
            Point pk = new Point();
            pk.set(findIntersection(t1, t2));    //<<<<<<<<<<<<<<<<<<<<<<<
            if ((isInside_sweet(p1, pk, p2) >= 1)
                    && (isInside_sweet(p3, pk, p4) >= 1)) {
                if (equal(p1, p3, rhit)) {
                    return 21;
                }//L-shaped
                if (equal(p1, p4, rhit)) {
                    return 22;
                }//L字型
                if (equal(p2, p3, rhit)) {
                    return 23;
                }//L字型
                if (equal(p2, p4, rhit)) {
                    return 24;
                }//L字型
                if (equal(p1, pk, rhit)) {
                    return 25;
                }//T字型 s1が縦棒
                if (equal(p2, pk, rhit)) {
                    return 26;
                }//T字型 s1が縦棒
                if (equal(p3, pk, rhit)) {
                    return 27;
                }//T字型 s2が縦棒
                if (equal(p4, pk, rhit)) {
                    return 28;
                }//T字型 s2が縦棒
                return 1;
            }
            return 0;
        }

        if (parallel_judgement(t1, t2, rhei) == ParallelJudgement.PARALLEL_NOT_EQUAL) { //２つの直線が平行で、y切片は一致しない
            return 0;
        }

        // The two line segments are exactly the same
        if (equal(p1, p3, rhit) && equal(p2, p4, rhit)) {
            return 31;
        }
        if (equal(p1, p4, rhit) && equal(p2, p3, rhit)) {
            return 31;
        }

        //The two straight lines are parallel and the y-intercept matches
        if (parallel_judgement(t1, t2, rhei) == ParallelJudgement.PARALLEL_EQUAL) {
            if (equal(p1, p3, rhit)) { //2つの線分の端点どうしが1点で重なる場合
                if (isInside(p1, p4, p2) == 2) {
                    return 321;
                }//長い線分に短い線分が含まれる
                if (isInside(p3, p2, p4) == 2) {
                    return 322;
                }//長い線分に短い線分が含まれる
                if (isInside(p2, p1, p4) == 2) {
                    return 323;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p1, p4, rhit)) {
                if (isInside(p1, p3, p2) == 2) {
                    return 331;
                }//長い線分に短い線分が含まれる
                if (isInside(p4, p2, p3) == 2) {
                    return 332;
                }//長い線分に短い線分が含まれる
                if (isInside(p2, p1, p3) == 2) {
                    return 333;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p3, rhit)) {
                if (isInside(p2, p4, p1) == 2) {
                    return 341;
                }//長い線分に短い線分が含まれる
                if (isInside(p3, p1, p4) == 2) {
                    return 342;
                }//長い線分に短い線分が含まれる
                if (isInside(p1, p2, p4) == 2) {
                    return 343;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            if (equal(p2, p4, rhit)) {
                if (isInside(p2, p3, p1) == 2) {
                    return 351;
                }//A long line segment contains a short line segment
                if (isInside(p4, p1, p3) == 2) {
                    return 352;
                }//長い線分に短い線分が含まれる
                if (isInside(p1, p2, p3) == 2) {
                    return 353;
                }//2つの線分は1点で重なるだけで、それ以外では重ならない
            }

            //2つの線分の端点どうしが重ならない場合
            if ((isInside(p1, p3, p4) == 2) && (isInside(p3, p4, p2) == 2)) {
                return 361;
            }//線分(p1,p2)に線分(p3,p4)が含まれる
            if ((isInside(p1, p4, p3) == 2) && (isInside(p4, p3, p2) == 2)) {
                return 362;
            }//線分(p1,p2)に線分(p3,p4)が含まれる

            if ((isInside(p3, p1, p2) == 2) && (isInside(p1, p2, p4) == 2)) {
                return 363;
            }//線分(p3,p4)に線分(p1,p2)が含まれる
            if ((isInside(p3, p2, p1) == 2) && (isInside(p2, p1, p4) == 2)) {
                return 364;
            }//線分(p3,p4)に線分(p1,p2)が含まれる

            if ((isInside(p1, p3, p2) == 2) && (isInside(p3, p2, p4) == 2)) {
                return 371;
            }
            if ((isInside(p1, p4, p2) == 2) && (isInside(p4, p2, p3) == 2)) {
                return 372;
            }
            if ((isInside(p3, p1, p4) == 2) && (isInside(p1, p4, p2) == 2)) {
                return 373;
            }
            if ((isInside(p4, p1, p3) == 2) && (isInside(p1, p3, p2) == 2)) {
                return 374;
            }

            return 0;
        }
        return -1;//ここは何らかのエラーの時に通る。

    }


    public enum ParallelJudgement {
        NOT_PARALLEL(0),
        PARALLEL_NOT_EQUAL(1),
        PARALLEL_EQUAL(2);

        private final int value;

        ParallelJudgement(int value) {
            this.value = value;
        }
    }

    //A function that determines whether two straight lines are parallel.
    public ParallelJudgement parallel_judgement(StraightLine t1, StraightLine t2) {
        return parallel_judgement(t1, t2, 0.1);
    }

    //A function that determines whether two line segments are parallel.
    public ParallelJudgement parallel_judgement(LineSegment s1, LineSegment s2, double r) {
        return parallel_judgement(lineSegmentToStraightLine(s1), lineSegmentToStraightLine(s2), r);
    }

    public ParallelJudgement parallel_judgement(StraightLine t1, StraightLine t2, double r) {//rは誤差の許容度。rが負なら厳密判定。
        //0 = not parallel, 1 = parallel and 2 straight lines do not match, 2 = parallel and 2 straight lines match
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        //System.out.print("平行判定のr　＝　");System.out.println(r);
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
                //double kyoriT=Math.abs(c1/Math.sqrt(a1*a1+b1*b1)-c2/Math.sqrt(a2*a2+b2*b2));//20181027、ver3.049までのバグありの処理
                //double kyoriT=Math.abs(   Math.abs(  c1/Math.sqrt(a1*a1+b1*b1)  )  -   Math.abs(  c2/Math.sqrt(a2*a2+b2*b2)  )      );//20181027、ver3.050以降のバグ無しの処理
                double kyoriT = t2.calculateDistance(t1.findShadow(new Point(0.0, 0.0)));//t1上の点とt2との距離//t1.kage_motome(new Ten(0.0,0.0))   は点（0,0）のt1上の影を求める（t1上の点ならなんでもいい）//20181115修正


                if (kyoriT < r) {//誤差を許容。
                    return ParallelJudgement.PARALLEL_EQUAL;
                }
                //２直線が異なる場合
                else {
                    return ParallelJudgement.PARALLEL_NOT_EQUAL;
                }
            }
        }

        //２直線が非平行の場合-------------------------------------------------
        return ParallelJudgement.NOT_PARALLEL;
    }

    //Function to find the intersection of two straight lines
    public Point findIntersection(StraightLine t1, StraightLine t2) {
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }

    //Function to find the intersection of two straight lines (replication)
    public Point findIntersection_01(StraightLine t1, StraightLine t2) {
        double a1 = t1.getA(), b1 = t1.getB(), c1 = t1.getC();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。
        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }

    public StraightLine lineSegmentToStraightLine(LineSegment s) {//Get a straight line containing a line segment
        return new StraightLine(s.getA(), s.getB());
    }

    //A function that finds the intersection of two line segments as a straight line. Even if it does not intersect as a line segment, it returns the intersection when it intersects as a straight line
    public Point findIntersection(LineSegment s1, LineSegment s2) {
        return findIntersection(lineSegmentToStraightLine(s1), lineSegmentToStraightLine(s2));
    }

    //A function that considers a line segment as a straight line and finds the intersection with another straight line. Even if it does not intersect as a line segment, it returns the intersection when it intersects as a straight line
    public Point findIntersection(StraightLine t1, LineSegment s2) {
        return findIntersection(t1, lineSegmentToStraightLine(s2));
    }

    //A function that considers a line segment as a straight line and finds the intersection with another straight line. Even if it does not intersect as a line segment, it returns the intersection when it intersects as a straight line
    public Point findIntersection(LineSegment s1, StraightLine t2) {
        return findIntersection(lineSegmentToStraightLine(s1), t2);
    }

    //A function that moves a line segment in parallel to the side (returns a new line segment without changing the original line segment)
    public LineSegment moveParallel(LineSegment s, double d) {
        StraightLine t = new StraightLine(s.getA(), s.getB());
        StraightLine ta = new StraightLine(s.getA(), s.getB());
        StraightLine tb = new StraightLine(s.getA(), s.getB());
        ta.orthogonalize(s.getA());
        tb.orthogonalize(s.getB());
        StraightLine td = new StraightLine(s.getA(), s.getB());
        td.translate(d);

        return new LineSegment(findIntersection_01(ta, td), findIntersection_01(tb, td));
    }

    //------------------------------------
    //A function that returns a point obtained by rotating point b by d degrees around point a (returns a new point without changing the original point)
    public Point point_rotate(Point a, Point b, double d) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = Mcd * (b.getX() - a.getX()) - Msd * (b.getY() - a.getY()) + a.getX();
        double by1 = Msd * (b.getX() - a.getX()) + Mcd * (b.getY() - a.getY()) + a.getY();

        return new Point(bx1, by1);
    }

    //------------------------------------
    //A function that rotates point b by d degrees around point a and returns a point whose ab distance is r times (returns a new point without changing the original point)
    public Point point_rotate(Point a, Point b, double d, double r) {

        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = r * (Mcd * (b.getX() - a.getX()) - Msd * (b.getY() - a.getY())) + a.getX();
        double by1 = r * (Msd * (b.getX() - a.getX()) + Mcd * (b.getY() - a.getY())) + a.getY();

        return new Point(bx1, by1);
    }

    //------------------------------------
    //A function that returns a point centered on point a and based on point b with a distance of ab times r (returns a new point without changing the original point) 20161224 Unverified
    public Point point_double(Point a, Point b, double r) {
        double bx1 = r * (b.getX() - a.getX()) + a.getX();
        double by1 = r * (b.getY() - a.getY()) + a.getY();

        return new Point(bx1, by1);
    }

    //線分abをcを中心にr倍してd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public LineSegment lineSegment_rotate(LineSegment s0, Point c, double d, double r) {
        return new LineSegment(point_rotate(s0.getA(), c, d, r), point_rotate(s0.getB(), c, d, r));
    }


//------------------------------------

    //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public LineSegment lineSegment_rotate(LineSegment s0, double d) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = Mcd * (s0.getBX() - s0.getAX()) - Msd * (s0.getBY() - s0.getAY()) + s0.getAX();
        double by1 = Msd * (s0.getBX() - s0.getAX()) + Mcd * (s0.getBY() - s0.getAY()) + s0.getAY();

        double ax1 = s0.getAX();
        double ay1 = s0.getAY();

        return new LineSegment(ax1, ay1, bx1, by1);
    }


// ------------------------------------

    //線分abをaを中心にr倍してd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
    public LineSegment lineSegment_rotate(LineSegment s0, double d, double r) {
        double Mcd = Math.cos(d * Math.PI / 180.0);
        double Msd = Math.sin(d * Math.PI / 180.0);

        double bx1 = r * (Mcd * (s0.getBX() - s0.getAX()) - Msd * (s0.getBY() - s0.getAY())) + s0.getAX();
        double by1 = r * (Msd * (s0.getBX() - s0.getAX()) + Mcd * (s0.getBY() - s0.getAY())) + s0.getAY();

        double ax1 = s0.getAX();
        double ay1 = s0.getAY();

        return new LineSegment(ax1, ay1, bx1, by1);
    }

    //A function that returns a line segment obtained by multiplying the line segment ab by r with a as the center (returns a new line segment without changing the original line segment)
    public LineSegment lineSegment_double(LineSegment s0, double r) {

        double bx1 = r * (s0.getBX() - s0.getAX()) + s0.getAX();
        double by1 = r * (s0.getBY() - s0.getAY()) + s0.getAY();

        double ax1 = s0.getAX();
        double ay1 = s0.getAY();

        return new LineSegment(ax1, ay1, bx1, by1);
    }

    //線分Aの、線分Jを軸とした対照位置にある線分Bを求める関数
    public LineSegment sentaisyou_lineSegment_motome(LineSegment s0, LineSegment jiku) {
        Point p_a = new Point();
        p_a.set(s0.getA());
        Point p_b = new Point();
        p_b.set(s0.getB());
        Point jiku_a = new Point();
        jiku_a.set(jiku.getA());
        Point jiku_b = new Point();
        jiku_b.set(jiku.getB());

        LineSegment s1 = new LineSegment();
        s1.set(lineSymmetry_point_find(jiku_a, jiku_b, p_a), lineSymmetry_point_find(jiku_a, jiku_b, p_b));

        return s1;
    }

    //Function to find the point at the control position of the point p with respect to the straight line t0
    public Point lineSymmetry_point_find(StraightLine t0, Point p) {
        Point p1 = new Point();  // p1.set(s.geta());
        Point p2 = new Point();  // p2.set(s.getb());

        StraightLine s1 = new StraightLine();
        s1.set(t0);
        StraightLine s2 = new StraightLine();
        s2.set(t0);

        s2.orthogonalize(p);//点pを通って s1に直行する直線s2を求める。

        p1 = findIntersection(s1, s2);
        p2.set(2.0 * p1.getX() - p.getX(), 2.0 * p1.getY() - p.getY());
        return p2;
    }

    //A function that finds a point at the control position of point p with respect to a straight line passing through two points t1 and t2.
    public Point lineSymmetry_point_find(Point t1, Point t2, Point p) {
        Point p1 = new Point();  // p1.set(s.geta());
        Point p2 = new Point();  // p2.set(s.getb());

        StraightLine s1 = new StraightLine(t1, t2);
        StraightLine s2 = new StraightLine(t1, t2);

        s2.orthogonalize(p);//Find the straight line s2 that passes through the point p and is orthogonal to s1.

        p1 = findIntersection(s1, s2);
        p2.set(2.0 * p1.getX() - p.getX(), 2.0 * p1.getY() - p.getY());
        return p2;
    }

    //A function that keeps the angle greater than -180.0 degrees and less than 180.0 degrees
    public double angle_between_m180_180(double angle) {
        while (angle <= -180.0) {
            angle = angle + 360.0;
        }
        while (angle > 180.0) {
            angle = angle - 360.0;
        }
        return angle;
    }

    //A function that keeps the angle between 0.0 degrees and 360.0 degrees
    public double angle_between_0_360(double angle) {
        while (angle < 0.0) {
            angle = angle + 360.0;
        }
        while (angle >= 360.0) {
            angle = angle - 360.0;
        }
        return angle;
    }

    //角度を0.0度以上kmax度未満に押さえる関数(円錐の頂点の伏見定理などで使う)
    public double angle_betwen_0_kmax(double angle, double kmax) {
        while (angle < 0.0) {
            angle = angle + kmax;
        }
        while (angle >= kmax) {
            angle = angle - kmax;
        }
        return angle;
    }

    //The angle between the line segments s1 and s2
    public double angle(LineSegment s1, LineSegment s2) {
        Point a = new Point();
        a.set(s1.getA());
        Point b = new Point();
        b.set(s1.getB());
        Point c = new Point();
        c.set(s2.getA());
        Point d = new Point();
        d.set(s2.getB());

        return angle_between_0_360(angle(c, d) - angle(a, b));
    }

    //Angle between vectors ab and cd
    public double angle(Point a, Point b, Point c, Point d) {
        return angle_between_0_360(angle(c, d) - angle(a, b));
    }

    /**
     * Find the inner heart of the triangle
     */
    public Point center(Point ta, Point tb, Point tc) {
        double A, B, C, XA, XB, XC, YA, YB, YC, XD, YD, XE, YE, G, H, K, L, P, Q, XN, YN;
        Point tn = new Point();
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

        tn.set(XN, YN);

        return tn;
    }

    // -------------------------------
    //Find the internal division point.
    public Point naibun(Point a, Point b, double d_naibun_s, double d_naibun_t) {
        Point r_point = new Point(-10000.0, -10000.0);
        if (distance(a, b) < 0.000001) {
            return r_point;
        }

        if ((d_naibun_s == 0.0) && (d_naibun_t == 0.0)) {
            return r_point;
        }
        if ((d_naibun_s == 0.0) && (d_naibun_t != 0.0)) {
            return a;
        }
        if ((d_naibun_s != 0.0) && (d_naibun_t == 0.0)) {
            return b;
        }
        if ((d_naibun_s != 0.0) && (d_naibun_t != 0.0)) {
            LineSegment s_ab = new LineSegment(a, b);
            double nx = (d_naibun_t * s_ab.getAX() + d_naibun_s * s_ab.getBX()) / (d_naibun_s + d_naibun_t);
            double ny = (d_naibun_t * s_ab.getAY() + d_naibun_s * s_ab.getBY()) / (d_naibun_s + d_naibun_t);
            r_point.set(nx, ny);
            return r_point;
        }
        return r_point;
    }

    /**
     * -------------------------------
     * Find the midpoint.
     */
    public Point midPoint(Point a, Point b) {

        return new Point((a.getX() + b.getX()) / 2.0, (a.getY() + b.getY()) / 2.0);
    }

    // -------------------------------
    public StraightLine circle_to_circle_no_intersection_wo_tooru_straightLine(Circle e1, Circle e2) {
        double x1 = e1.getX();
        double y1 = e1.getY();
        double r1 = e1.getRadius();
        double x2 = e2.getX();
        double y2 = e2.getY();
        double r2 = e2.getRadius();

        double a = 2.0 * x1 - 2.0 * x2;
        double b = 2.0 * y1 - 2.0 * y2;
        double c = x2 * x2 - x1 * x1 + y2 * y2 - y1 * y1 + r1 * r1 - r2 * r2;

        return new StraightLine(a, b, c);
    }

    // -------------------------------
    public LineSegment circle_to_circle_no_intersection_wo_musubu_lineSegment(Circle e1, Circle e2) {
        StraightLine t0 = new StraightLine();
        t0.set(circle_to_circle_no_intersection_wo_tooru_straightLine(e1, e2));
        StraightLine t1 = new StraightLine(e1.getCenter(), e2.getCenter());
        Point intersection_t0t1 = new Point();
        intersection_t0t1.set(findIntersection(t0, t1));
        double length_a = t0.calculateDistance(e1.getCenter());  //t0とt1の交点からe1の中心までの長さ

//double length_a=kyori(intersection_t0t1,e1.get_tyuusin());  //t0とt1の交点からe1の中心までの長さ
        double length_b = Math.sqrt(e1.getRadius() * e1.getRadius() - length_a * length_a); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがnagasa_bのものは(t0.getb()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*length_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

        return new LineSegment(
                intersection_t0t1.getX() + t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getY() - t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getX() - t0.getB() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                intersection_t0t1.getY() + t0.getA() * length_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA())
        );
    }

    // --------qqqqqqqqqqqqqqq-----------------------
    public LineSegment circle_to_straightLine_no_intersect_wo_connect_LineSegment(Circle e1, StraightLine t0) {

        Point kouten_t0t1 = new Point();
        kouten_t0t1.set(shadow_request(t0, e1.getCenter()));
        double nagasa_a = t0.calculateDistance(e1.getCenter());  //t0とt1の交点からe1の中心までの長さ

        double nagasa_b = Math.sqrt(e1.getRadius() * e1.getRadius() - nagasa_a * nagasa_a); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがnagasa_bのものは(t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

        return new LineSegment(
                kouten_t0t1.getX() + t0.getB() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getY() - t0.getA() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getX() - t0.getB() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA()),
                kouten_t0t1.getY() + t0.getA() * nagasa_b / Math.sqrt(t0.getB() * t0.getB() + t0.getA() * t0.getA())
        );
    }

    // Function to find the distance between the point p0 and the circumference of the circle e0 ------------------------------- --------------------- ---------------------
    public double distance_circumference(Point p0, Circle e0) {
        return Math.abs(distance(p0, e0.getCenter()) - e0.getRadius());
    }

    //Minを返す関数
    public double min(double d1, double d2, double d3, double d4) {
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

    public LineSegment bisection(Point t1, Point t2, double d0) {
        Point tm = new Point((t1.getX() + t2.getX()) / 2.0, (t1.getY() + t2.getY()) / 2.0);

        double bai = d0 / distance(t1, t2);

        LineSegment s1 = new LineSegment();
        s1.set(lineSegment_rotate(new LineSegment(tm, t1), 90.0, bai));
        LineSegment s2 = new LineSegment();
        s2.set(lineSegment_rotate(new LineSegment(tm, t2), 90.0, bai));

        return new LineSegment(s1.getB(), s2.getB());
    }

    //--------------------------------------------------------
    public boolean lineSegmentoverlapping(LineSegment s1, LineSegment s2) {//false do not overlap. true overlaps. 20201012 added

        int i_senbun_kousa_hantei = line_intersect_decide(s1, s2, 0.0001, 0.0001);
        boolean i_jikkou = false;
        if (i_senbun_kousa_hantei == 31) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 321) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 322) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 331) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 332) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 341) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 342) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 351) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 352) {
            i_jikkou = true;
        }

        if (i_senbun_kousa_hantei == 361) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 362) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 363) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 364) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 371) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 372) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 373) {
            i_jikkou = true;
        }
        if (i_senbun_kousa_hantei == 374) {
            i_jikkou = true;
        }
        return i_jikkou;
    }

    //--------------------------------------------------------
    public int Senbun_X_kousa_hantei(LineSegment s1, LineSegment s2) {//0はX交差しない。1は交差する。20201017追加

        int i_senbun_kousa_hantei = line_intersect_decide(s1, s2, 0.0001, 0.0001);
        int i_jikkou = 0;
        if (i_senbun_kousa_hantei == 1) {
            i_jikkou = 1;
        }

        return i_jikkou;
    }

//--------------------------------------------------------


}
