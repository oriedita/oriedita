package oriedita.editor.canvas;

import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

public class OperationFrame {
    private final Polygon rect;
    private boolean active = false;

    public OperationFrame() {
        rect = new Polygon(4);
    }

    public void setFramePoint(int index, Point p) {
        rect.set(index, p);
    }

    public void setFramePointX(int index, double x) {
        rect.set(index, new Point(x, rect.get(index).getY()));
    }

    public void setFramePointY(int index, double y) {
        rect.set(index, new Point(rect.get(index).getX(), y));
    }

    public Point getP1() {
        return rect.get(1);
    }

    public Point getP2() {
        return rect.get(2);
    }

    public Point getP3() {
        return rect.get(3);
    }

    public Point getP4() {
        return rect.get(4);
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
