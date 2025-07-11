package origami.crease_pattern.element;

public class Rectangle extends Polygon {

    public Rectangle() {
        super();

        add(new Point(0, 0));
        add(new Point(0, 0));
        add(new Point(0, 0));
        add(new Point(0, 0));
    }

    public Rectangle(Point p1, Point p2, Point p3, Point p4) {
        add(p1);
        add(p2);
        add(p3);
        add(p4);
    }

    public Point getP1() {
        return get(0);
    }

    public Point getP2() {
        return get(1);
    }

    public Point getP3() {
        return get(2);
    }

    public Point getP4() {
        return get(3);
    }
}
