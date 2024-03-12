package origami.crease_pattern.element;

public class Box extends Polygon {
    public Box() {
        super(4);

        add(new Point());
        add(new Point());
        add(new Point());
        add(new Point());
    }

    public Box(Point p1, Point p2, Point p3, Point p4) {
        super(4);

        add(p1);
        add(p2);
        add(p3);
        add(p4);
    }
}
