package oriedita.editor.canvas;

import origami.crease_pattern.element.Rectangle;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

public class OperationFrame {
    private final Rectangle rect;
    private boolean active = false;

    public OperationFrame() {
        rect = new Rectangle();
    }

    public void setFramePoint(int index, Point p) {
        rect.set(index, p);
    }

    public void setFramePointX(int index, double x) {
        rect.set(index, rect.get(index).withX(x));
    }

    public void setFramePointY(int index, double y) {
        rect.set(index, rect.get(index).withY(y));
    }

    public Point getP1() {
        return rect.getP1();
    }

    public Point getP2() {
        return rect.getP2();
    }

    public Point getP3() {
        return rect.getP3();
    }

    public Point getP4() {
        return rect.getP4();
    }

    public Polygon getPolygon() {
        return rect;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Point getFramePoint(int i) {
        return rect.get(i);
    }
}
