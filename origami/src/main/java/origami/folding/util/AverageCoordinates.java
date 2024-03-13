package origami.folding.util;

import origami.crease_pattern.element.Point;

public class AverageCoordinates {
    double x, y;
    int total;

    public AverageCoordinates() {
        x = 0.0;
        y = 0.0;
        total = 0;
    }

    public void reset() {
        x = 0.0;
        y = 0.0;
        total = 0;
    }

    public void add(double a, double b) {
        x = x + a;
        y = y + b;
        total = total + 1;
    }

    public void addPoint(Point point) {
        x = x + point.getX();
        y = y + point.getY();
        total = total + 1;
    }

    public Point getAveragePoint() {
        return new Point(getAverageX(), getAverageY());
    }

    public double getAverageX() {
        return x / ((double) total);
    }

    public double getAverageY() {
        return y / ((double) total);
    }
}
