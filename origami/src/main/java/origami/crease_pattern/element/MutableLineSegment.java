package origami.crease_pattern.element;

public interface MutableLineSegment {
    Point getA();
    void setA(Point a);
    Point getB();
    void setB(Point b);
    void setAB(Point a, Point b);

    LineColor getColor();
    void setColor(LineColor color);
    LineSegment.ActiveState getActiveState();
    void setActiveState(LineSegment.ActiveState activeState);
    LineSegment asImmutable();
}
