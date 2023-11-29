package origami.crease_pattern.element;

public class LineSegmentVoronoi extends LineSegment {
    int voronoiA;
    int voronoiB;

    public LineSegmentVoronoi(LineSegment ls) {
        super(ls);
        voronoiA = 0;
        voronoiB = 0;
    }

    public LineSegmentVoronoi(Point t1, Point t2) {
        super(t1, t2);
        voronoiA = 0;
        voronoiB = 0;
    }

    public LineSegmentVoronoi(Point t1, Point t2, LineColor color) {
        super(t1, t2, color);
        voronoiA = 0;
        voronoiB = 0;
    }

    public LineSegmentVoronoi(double i1, double i2, double i3, double i4) {
        super(i1, i2, i3, i4);
        voronoiA = 0;
        voronoiB = 0;
    }

    public LineSegmentVoronoi(LineSegmentVoronoi s) {
        super(s);
        voronoiA = s.getVoronoiA();
        voronoiB = s.getVoronoiB();
    }

    @Override
    public void reset() {
        super.reset();
        voronoiA = 0;
        voronoiB = 0;
    }

    public void set(Point p, Point q, LineColor ic, ActiveState ia, int v_a, int v_b) {
        set(p, q, ic, ia);
        voronoiA = v_a;
        voronoiB = v_b;
    }

    public int getVoronoiA() {
        return voronoiA;
    }

    public void setVoronoiA(int i) {
        voronoiA = i;
    }

    public int getVoronoiB() {
        return voronoiB;
    }

    public void setVoronoiB(int i) {
        voronoiB = i;
    }

    @Override
    public LineSegmentVoronoi clone() {
        LineSegmentVoronoi clone = new LineSegmentVoronoi(a, b, color);
        clone.setActive(active);
        clone.setSelected(selected);
        clone.setCustomizedColor(customizedColor);
        clone.setCustomized(customized);
        clone.setVoronoiA(voronoiA);
        clone.setVoronoiB(voronoiB);
        return clone;
    }
}
