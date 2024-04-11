package origami.crease_pattern.element;

import java.io.Serializable;
import java.util.Objects;

public class Point implements Serializable {
    // Used to represent point coordinates, direction vectors, etc.

    /* UPDATE: this class should only be used for Point usage for semantic reason.
        To semantically create vector, use Vector subclass
     */

    private final double x;
    private final double y;

    public Point(Point p) {
        x = p.getX();
        y = p.getY();
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

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Point mid(Point p, Point q) {
        return new Point((p.x + q.x) / 2, (p.y + q.y) / 2);
    }

    public Point(double a, Point p, double b, Point q) {
        x = a * p.getX() + b * q.getX();
        y = a * p.getY() + b * q.getY();
    }

    public double getX() {
        return x;
    }

    public Point withX(double x) {
        return new Point(x, this.y);
    }

    public Point withY(double y) {
        return new Point(this.x, y);
    }

    public double getY() {
        return y;
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
     * <p>
     * example: <code>new Point(1, 2).delta(new Point(3,2)); // returns (2, 0)</code>
     * </p>
     */
    public Point delta(Point taPoint) {
        return new Point(taPoint.getX() - x, taPoint.getY() - y);
    }

    /**
     * Returns a new Point which is the old point, moved by addPoint
     * @param addPoint amount to move
     * @return moved point
     */
    public Point move(Point addPoint) {
        return new Point(x + addPoint.getX(), y + addPoint.getY());
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

}
