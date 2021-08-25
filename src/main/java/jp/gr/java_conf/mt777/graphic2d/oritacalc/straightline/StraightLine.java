package jp.gr.java_conf.mt777.graphic2d.oritacalc.straightline;

import jp.gr.java_conf.mt777.graphic2d.linesegment.LineSegment;
import jp.gr.java_conf.mt777.graphic2d.point.Point;

public class StraightLine {
    //Note! If p1 = p2, the result will be strange, but it may be hard to notice because this function does not have a check mechanism.
    // a is 0 or more. If a = 0, make sure b is greater than or equal to 0. Otherwise, the sign of the distance to the straight line will be incorrect.
    double a, b, c;//treat a * x + b * y + c = 0, a, b, c, x, y, as integers (20181115 Isn't this comment strange?)

    public StraightLine() {  //コンストラクタ
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 1.0;
        double y2 = 1.0;

        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    public StraightLine(double a0, double b0, double c0) {
        a = a0;
        b = b0;
        c = c0;
        coefficient();
    }

    public StraightLine(Point p1, Point p2) {
        //Find the straight line a, b, and c by specifying two points
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    public StraightLine(LineSegment s0) {
        //Specify line segment to find straight line a, b, c
        double x1 = s0.getAX(), y1 = s0.getAY();
        double x2 = s0.getBX(), y2 = s0.getBY();
        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    public StraightLine(double x1, double y1, double x2, double y2) {
        //Find the straight line a, b, and c by specifying two points

        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    void coefficient() {
        if ((a < 0.0)) {
            a = -a;
            b = -b;
            c = -c;
        }
        if ((-0.1 < a) && (a < 0.1)) {
            if (b < 0.0) {
                a = -a;
                b = -b;
                c = -c;
            }
        }
    }

    public void display(String str0) {
        System.out.println(str0 + "   " + a + " x + " + b + " y + " + c + " = 0.0 ");
    }

    //translation
    public void translate(double d) {
        c = c + d * Math.sqrt(a * a + b * b);
    }

    //
    public void set(StraightLine t) {
        a = t.getA();
        b = t.getB();
        c = t.getC();
        coefficient();
    }

    public double getA() {
        return a;
    }

    public void setA(double a0) {
        a = a0;
    }

    public double getB() {
        return b;
    }

    public void setB(double b0) {
        a = b0;
    }

    public double getC() {
        return c;
    }

    public void setC(double c0) {
        a = c0;
    }

    public double calculateDistance(Point p) {// Distance between straight line and point p
        double x = p.getX();
        double y = p.getY();
        return Math.abs((a * x + b * y + c) / Math.sqrt(a * a + b * b));
    }

    public double calculateDistanceSquared(Point p) {//The square of the distance between the straight line and the point p
        double x = p.getX();
        double y = p.getY();
        return (a * x + b * y + c) * (a * x + b * y + c) / (a * a + b * b);
    }

    public void orthogonalize(Point p) { //Converted to a straight line (bx-ay + d = 0) that passes through the point (x, y) and is orthogonal to ax + by + c = 0
        double e;
        double x = p.getX();
        double y = p.getY();
        c = -b * x + a * y;
        e = a;
        a = b;
        b = -e;

        coefficient();
    }

    public int sameSide(Point p1, Point p2) {// Returns 1 if the two points are on the same side of the straight line, -1 if they are on the other side, 0 if there is a point on the straight line
        double dd = assignmentCalculation(p1) * assignmentCalculation(p2);
        return Double.compare(dd, 0.0);
    }

    public double assignmentCalculation(Point p) {
        return a * p.getX() + b * p.getY() + c;
    }  //Returns the value obtained by assigning x and y in a * x + b * y + c


    public Intersection lineSegment_intersect_reverse_detail(LineSegment s0) {//0 = This straight line does not intersect a given line segment, 1 = intersects at X type, 21 = intersects at point a of line segment at T type, 22 = intersects at point b of line segment at T type, 3 = Line segment is included in the straight line.
        double d_a2 = calculateDistanceSquared(s0.getA());
        double d_b2 = calculateDistanceSquared(s0.getB());

        if (d_a2 < 0.00000001 && d_b2 < 0.00000001) {
            return Intersection.INCLUDED_3;
        }

        if (d_a2 < 0.00000001 && d_b2 >= 0.00000001) {
            return Intersection.INTERSECT_T_A_21;
        }
        if (d_a2 >= 0.00000001 && d_b2 < 0.00000001) {
            return Intersection.INTERSECT_T_B_22;
        }

        //The following is the case when it is judged that neither point a nor point b of the line segment is on a straight line.

        double d_a = assignmentCalculation(s0.getA());
        double d_b = assignmentCalculation(s0.getB());

        if (d_a * d_b > 0.0) {
            return Intersection.NONE_0;
        }
        if (d_a * d_b < 0.0) {
            return Intersection.INTERSECT_X_1;
        }

        return Intersection.INCLUDED_3;
    }

    //Added 20170312 function to find intersections with other straight lines
    public Point findIntersection(StraightLine t2) {
        double a1 = a, b1 = b, c1 = c;//Find the coefficients of the straight lines t1, a1 * x + b1 * y + c1 = 0.
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//Find the coefficients of the straight lines t2, a2 * x + b2 * y + c2 = 0.

        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }

    //Find the position of the shadow of the point p on the straight line (the position on the straight line closest to the point p). 20170312 added
    public Point findShadow(Point p) {
        StraightLine t1 = new StraightLine(a, b, c);
        t1.orthogonalize(p);//Find a straight line that passes through the point p1 and is orthogonal to t.
        return findIntersection(t1);
    }

    /**
     * Intersection of a StraightLine and a line segment
     */
    public enum Intersection {
        NONE_0(0),
        INTERSECT_X_1(1),
        INTERSECT_T_A_21(21),
        INTERSECT_T_B_22(22),
        INCLUDED_3(3),
        ;

        int type;

        Intersection(int type) {
            this.type = type;
        }

        public boolean isIntersecting() {
            return switch (this) {
                case INTERSECT_X_1, INTERSECT_T_A_21, INTERSECT_T_B_22 -> true;
                default -> false;
            };
        }
    }
}
