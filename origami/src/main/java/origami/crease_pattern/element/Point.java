package origami.crease_pattern.element;

import java.io.Serializable;
import java.util.Objects;

public class Point implements Serializable {
    //Used to represent point coordinates, direction vectors, etc.
    double x, y;

    public Point(Point p) {
        set(p);
    }

    public Point() {
        x = 0;
        y = 0;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Point(double i, double j) {
        x = i;
        y = j;
    }

    public static Point mid(Point p, Point q) {
        return new Point((p.x + q.x) / 2, (p.y + q.y) / 2);
    }

    public Point(double a, Point p, double b, Point q) {
        x = a * p.getX() + b * q.getX();
        y = a * p.getY() + b * q.getY();
    }

    public void set(Point p) {
        x = p.getX();
        y = p.getY();
    }

    public void set(double i, double j) {
        x = i;
        y = j;
    }

    public void set(double a, Point p, double b, Point q) {
        x = a * p.getX() + b * q.getX();
        y = a * p.getY() + b * q.getY();
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

    public void reset() {
        x = 0.0;
        y = 0.0;
    }

    public void parallel_move(double x1, double y1) {
        x = x + x1;
        y = y + y1;
    }

    /**
     * Function to find the distance (double) to other points ----------------------------------------------------
     */
    public double distance(Point p) {
        double x1 = p.getX() - x, y1 = p.getY() - y;
        return Math.sqrt(x1 * x1 + y1 * y1);
    }

    /**
     * A function that finds the square of the distance to another point ----------------------------------------------------
     */
    public double distanceSquared(Point p) {
        double x1 = p.getX() - x, y1 = p.getY() - y;
        return x1 * x1 + y1 * y1;
    }

    /**
     * When looking at the own Point as a reference, the positions of other points are returned as Points.
     */
    public Point other_Point_position(Point taPoint) {
        Point rPoint = new Point();
        rPoint.setX(taPoint.getX() - x);
        rPoint.setY(taPoint.getY() - y);
        return rPoint;
    }

    public void move(Point addPoint) {
        x = x + addPoint.getX();
        y = y + addPoint.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * @return new Point with the coordinates rounded to full numbers
     */
    public Point rounded() {
        return new Point(Math.round(x), Math.round(y));
    }
}
