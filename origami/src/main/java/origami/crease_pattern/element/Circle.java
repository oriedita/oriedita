package origami.crease_pattern.element;

import origami.Epsilon;

import java.awt.Color;
import java.io.Serializable;

public class Circle implements Serializable {//Used to represent point coordinates, direction vectors, etc.

    double x, y, r;//Center coordinates and radius

    LineColor color;//Color specification 　0=black,1=blue,2=red.
    int customized = 0;//Custom property parameters
    Color customizedColor = new Color(100, 200, 200);//Color if custom made

    public Circle() {
        x = 0.0;
        y = 0.0;
        r = 0.0;
        color = LineColor.BLACK_0;
    }

    public Circle(Circle c) {
        x = c.getX();
        y = c.getY();
        r = c.getR();
        color = c.getColor();
        customized = c.getCustomized();
        customizedColor = c.getCustomizedColor();
    }

    public Circle(double i, double j, double k, LineColor m) {
        x = i;
        y = j;
        r = k;
        color = m;
    }

    public Circle(Point tc, double k, LineColor m) {
        x = tc.getX();
        y = tc.getY();
        r = k;
        color = m;
    }

    public Circle(LineSegment s0, LineColor m) {// A circle whose diameter is the constructor line segment
        x = (s0.determineAX() + s0.determineBX()) / 2.0;
        y = (s0.determineAY() + s0.determineBY()) / 2.0;
        r = s0.determineLength() / 2.0;
        color = m;
    }

    public void set(Circle e) {
        x = e.getX();
        y = e.getY();
        r = e.getR();
        color = e.getColor();
        customized = e.getCustomized();
        customizedColor = e.getCustomizedColor();
    }

    public void set(double i, double j, double k, LineColor m) {
        x = i;
        y = j;
        r = k;
        color = m;
    }

    public void set(Point tc, double k, LineColor m) {
        x = tc.getX();
        y = tc.getY();
        r = k;
        color = m;
    }

    public void set(double i, double j, double k) {
        x = i;
        y = j;
        r = k;
    }

    public void set(LineSegment s0, LineColor m) {
        x = (s0.determineAX() + s0.determineBX()) / 2.0;
        y = (s0.determineAY() + s0.determineBY()) / 2.0;
        r = s0.determineLength() / 2.0;
        color = m;
    }

    public void setR(double rr) {
        r = rr;
    }

    public double getX() {
        return x;
    }

    public void setX(double xx) {
        x = xx;
    }

    public double getY() {
        return y;
    }

    public void setY(double yy) {
        y = yy;
    }

    public double getR() {
        return r;
    }

    public void reset() {
        x = 0.0;
        y = 0.0;
        r = 0.0;
        color = LineColor.BLACK_0;
    }

    public LineColor getColor() {
        return color;
    }

    public void setColor(LineColor i) {
        color = i;
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

    public Point determineCenter() {
        return new Point(getX(), getY());
    }

    //Function that inverts other points ----------------------------------------------------
    public Point turnAround(Point t0) {//An error occurs when t0 and (x, y) are in the same position.
        double x1 = t0.getX() - x;
        double y1 = t0.getY() - y;
        double d1 = Math.sqrt(x1 * x1 + y1 * y1);
        double d2, x2, y2, x3, y3;

        if (Math.abs(d1 - r) < Epsilon.UNKNOWN_1EN7) {
            return t0;
        }
        d2 = r * r / d1;
        x2 = d2 * x1 / d1;
        y2 = d2 * y1 / d1;
        x3 = x2 + x;
        y3 = y2 + y;
        return new Point(x3, y3);
    }

    //A function that inverts another circle to a circle ----------------------------------------------------
    public Circle turnAround(Circle e0) {// For when the circumference of e0 does not pass through (x, y) // When the circumference of e0 passes through (x, y), an error occurs. Also, when (x, y) comes inside the circumference of e0, it seems that the result is strange.
        double x1 = e0.getX() - x;
        double y1 = e0.getY() - y;
        double d1 = Math.sqrt(x1 * x1 + y1 * y1);
        double da1 = d1 - e0.getR();
        double db1 = d1 + e0.getR();

        double xa1, ya1;
        double xa0, ya0;
        double xb1, yb1;
        double xb0, yb0;

        if (d1 < Epsilon.UNKNOWN_1EN6) {
            xa1 = da1;
            ya1 = 0.0;
            xa0 = xa1 + x;
            ya0 = ya1 + y;
            xb1 = db1;
            yb1 = 0.0;
            xb0 = xb1 + x;
            yb0 = yb1 + y;
        } else {
            xa1 = da1 * x1 / d1;
            ya1 = da1 * y1 / d1;
            xa0 = xa1 + x;
            ya0 = ya1 + y;
            xb1 = db1 * x1 / d1;
            yb1 = db1 * y1 / d1;
            xb0 = xb1 + x;
            yb0 = yb1 + y;
        }

        LineColor ic = LineColor.MAGENTA_5;

        return new Circle(new LineSegment(turnAround(new Point(xa0, ya0)), turnAround(new Point(xb0, yb0))), ic);
    }

    //A function that inverts another circle passing through (x, y) into a line segment----------------------------------------------------
    public LineSegment turnAround_CircleToLineSegment(Circle e0) {//For when the circumference of e0 passes through (x, y) // If the circumference of e0 does not pass through (x, y), the result will be strange.
        double x1 = e0.getX() - x, y1 = e0.getY() - y;
        Point th = turnAround(new Point(x1 * 2.0 + x, y1 * 2.0 + y));
        Point tha = new Point(th.getX() + 3.0 * y1, th.getY() - 3.0 * x1);
        Point thb = new Point(th.getX() - 3.0 * y1, th.getY() + 3.0 * x1);
        return new LineSegment(tha, thb, LineColor.CYAN_3);
    }


    //A function that inverts a line segment that does not pass through (x, y) to another circle----------------------------------------------------
    public Circle turnAround_LineSegmentToCircle(LineSegment s0) {//Weird results when s0 passes through (x, y).
        StraightLine ty = new StraightLine(s0);
        Point t0 = ty.findProjection(determineCenter());
        return new Circle(new LineSegment(turnAround(t0), determineCenter()), LineColor.MAGENTA_5);
    }

    public void setCenter(Point point) {
        setX(point.getX());
        setY(point.getY());
    }
}
