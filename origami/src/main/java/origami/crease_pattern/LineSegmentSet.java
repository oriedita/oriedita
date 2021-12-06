package origami.crease_pattern;

import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.LineSegmentEndPointAdapter;
import origami.data.quadTree.adapter.LineSegmentSetLineAdapter;
import origami.data.quadTree.collector.EndPointCollector;
import origami.data.quadTree.collector.QuadTreeCollector;
import origami.data.save.LineSegmentSave;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection of line segments. Used when calculating folded shapes and two-colored cp.
 */
public class LineSegmentSet {
    List<LineSegment> lineSegments = new ArrayList<>(); //Instantiation of line segments

    public LineSegmentSet() {
        reset();
    }

    public LineSegmentSet(PointSet pointSet) {
        reset(pointSet.getNumLines());
        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            lineSegments.get(i-1).set(pointSet.getPoint(pointSet.getBegin(i)), pointSet.getPoint(pointSet.getEnd(i)), pointSet.getColor(i), LineSegment.ActiveState.INACTIVE_0);
        }
    }

    public void reset() {
        lineSegments.clear();
    }

    public void set(LineSegmentSet ss) {
        reset(ss.getNumLineSegments());

        for (int i = 0; i < lineSegments.size(); i++) {
            LineSegment s = lineSegments.get(i);
            s.set(ss.lineSegments.get(i));
        }
    }

    //Get the total number of line segments
    public int getNumLineSegments() {
        return lineSegments.size();
    }

    public void reset(int size) {
        reset();

        for (int i = 0; i < size; i++) {
            lineSegments.add(new LineSegment());
        }
    }

    //Get the endpoint of the i-th line segment
    public Point getA(int i) {
        LineSegment s = lineSegments.get(i);
        return s.getA();
    }

    public Point getB(int i) {
        LineSegment s = lineSegments.get(i);
        return s.getB();
    }

    /**
     * Output the color of the i-th line segment
     */
    public LineColor getColor(int i) {
        LineSegment s = lineSegments.get(i);
        return s.getColor();
    }

    public void setSave(LineSegmentSave memo1) {
        lineSegments.clear();
        for (LineSegment s :
                memo1.getLineSegments()) {
            LineSegment s0 = new LineSegment();
            s0.set(s);
            lineSegments.add(s0);
        }
    }

    //Remove dotted line segments
    public void point_removal() {
        lineSegments.removeIf(s -> OritaCalc.equal(s.getA(), s.getB()));
    }

    /**
     * When there are two completely overlapping line segments, the one with the latest number is deleted.
     */
    public void overlapping_line_removal(double r) {
        QuadTree qtA = new QuadTree(new LineSegmentEndPointAdapter(this, (set, i)->set.getA(i)));
        QuadTree qtB = new QuadTree(new LineSegmentEndPointAdapter(this, (set, i)->set.getB(i)));

        boolean[] removal_flg = new boolean[lineSegments.size()];
        List<LineSegment> snew = new ArrayList<>();

        for (int i = 0; i < lineSegments.size(); i++) {
            LineSegment si = lineSegments.get(i);
            Point p1 = si.getA();
            Point p2 = si.getB();
            QuadTreeCollector c = new EndPointCollector(p1, i);
            for (int j : qtA.collect(c)) {
                LineSegment sj = lineSegments.get(j);
                Point p3 = sj.getA();
                Point p4 = sj.getB();
                if (OritaCalc.equal(p1, p3, r) && OritaCalc.equal(p2, p4, r)) {
                    removal_flg[j] = true;
                }
            }
            for (int j : qtB.collect(c)) {
                LineSegment sj = lineSegments.get(j);
                Point p3 = sj.getA();
                Point p4 = sj.getB();
                if (OritaCalc.equal(p1, p4, r) && OritaCalc.equal(p2, p3, r)) {
                    removal_flg[j] = true;
                }
            }
        }

        for (int i = 0; i < lineSegments.size(); i++) {
            if (!removal_flg[i]) {
                snew.add(lineSegments.get(i));
            }
        }
        lineSegments = snew;
    }

    public void overlapping_line_removal() {
        // The same epsilon value as in OritaCalc.determineLineSegmentIntersection
        overlapping_line_removal(Epsilon.UNKNOWN_001);
    }

    /**
     * Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
     */
    public void intersect_divide() throws InterruptedException {
        QuadTree qt = new QuadTree(new LineSegmentSetLineAdapter(this));
        for (int i = 0; i < lineSegments.size(); i++) {
            boolean found = false;
            for (int j : qt.getPotentialCollision(i)) {
                int added = intersect_divide(i, j); // Side effect
                if (added >= 0) {
                    found = true;
                    qt.grow(added);
                    qt.update(j);
                }
                if (Thread.interrupted()) throw new InterruptedException();
            }
            if (found) qt.update(i);
        }
    }

    /**
     * Divide the two line segments at the intersection of the two intersecting line
     * segments. Returns the number of added lines (-1 means nothing was done). From
     * Orihime 2.002, the color of the line after splitting is also controlled (if
     * there is an overlap, it will be unified and the color will be the one with
     * the later number).
     */
    private int intersect_divide(int i, int j) {
        if (i == j) {
            return -1;
        }

        LineSegment si = lineSegments.get(i);
        LineSegment sj = lineSegments.get(j);

        Point p1 = new Point(si.getA());
        Point p2 = new Point(si.getB());
        Point p3 = new Point(sj.getA());
        Point p4 = new Point(sj.getB());
        Point pk = new Point();

        double ixmax = Math.max(si.determineAX(), si.determineBX());
        double ixmin = Math.min(si.determineAX(), si.determineBX());
        double iymax = Math.max(si.determineAY(), si.determineBY());
        double iymin = Math.min(si.determineAY(), si.determineBY());

        double jxmax = Math.max(sj.determineAX(), sj.determineBX());
        double jxmin = Math.min(sj.determineAX(), sj.determineBX());
        double jymax = Math.max(sj.determineAY(), sj.determineBY());
        double jymin = Math.min(sj.determineAY(), sj.determineBY());

        if (ixmax + 0.05 < jxmin) {
            return -1;
        }
        if (jxmax + 0.05 < ixmin) {
            return -1;
        }
        if (iymax + 0.05 < jymin) {
            return -1;
        }
        if (jymax + 0.05 < iymin) {
            return -1;
        }

        // It is important to use the sweet version here, or glitches may occur
        LineSegment.Intersection intersect_decide = OritaCalc.determineLineSegmentIntersectionSweet(si, sj);

        switch (intersect_decide) {
            case INTERSECTS_1:
                pk.set(OritaCalc.findIntersection(si, sj));

                si.setA(p1);
                si.setB(pk);
                sj.setA(p3);
                sj.setB(pk);
                addLine(p2, pk, si.getColor());
                addLine(p4, pk, sj.getColor());
                return 2;
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                pk.set(OritaCalc.findIntersection(si, sj));

                sj.setA(p3);
                sj.setB(pk);
                addLine(p4, pk, sj.getColor());
                return 1;

            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                pk.set(OritaCalc.findIntersection(si, sj));

                si.setA(p1);
                si.setB(pk);
                addLine(p2, pk, si.getColor());
                return 1;

            case PARALLEL_EQUAL_31: //If the two line segments are exactly the same, do nothing.
                return -1;
            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321: {//The endpoints of two line segments (p1 and p3) overlap at one point. si contains sj
                si.setA(p2);
                si.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return 0;
            }
            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322: {//The endpoints of two line segments (p1 and p3) overlap at one point. sj contains si
                sj.setA(p2);
                sj.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return 0;
            }
            case PARALLEL_START_OF_S1_CONTAINS_END_OF_S2_331: {//The endpoints of two line segments (p1 and p4) overlap at one point. si contains sj
                si.setA(p2);
                si.setB(p3);

                LineColor overlapping_col;
                overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return 0;
            }
            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332: {//The endpoints of two line segments (p1 and p4) overlap at one point. sj contains si
                sj.setA(p2);
                sj.setB(p3);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);
                return 0;
            }
            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341: {//The endpoints of two line segments (p2 and p3) overlap at one point. si contains sj
                si.setA(p1);
                si.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return 0;
            }
            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342: {//The endpoints of two line segments (p2 and p3) overlap at one point. sj contains si
                sj.setA(p1);
                sj.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return 0;
            }
            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351: {//The endpoints of two line segments (p2 and p4) overlap at one point. si contains sj
                si.setA(p1);
                si.setB(p3);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return 0;
            }
            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352: {//The endpoints of two line segments (p2 and p4) overlap at one point. sj contains si
                sj.setA(p1);
                sj.setB(p3);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return 0;
            }
            case PARALLEL_S1_INCLUDES_S2_361: {//In order of p1-p3-p4-p2
                si.setA(p1);
                si.setB(p3);

                addLine(p2, p4, si.getColor());
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return 1;
            }
            case PARALLEL_S1_INCLUDES_S2_362: {//In order of p1-p4-p3-p2
                si.setA(p1);
                si.setB(p4);

                addLine(p2, p3, si.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return 1;
            }
            case PARALLEL_S2_INCLUDES_S1_363: {//In order of p3-p1-p2-p4
                sj.setA(p1);
                sj.setB(p3);

                addLine(p2, p4, sj.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return 1;
            }
            case PARALLEL_S2_INCLUDES_S1_364: {//In order of p3-p2-p1-p4
                sj.setA(p1);
                sj.setB(p4);

                addLine(p2, p3, sj.getColor());

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return 1;
            }
            case PARALLEL_S1_END_OVERLAPS_S2_START_371: {//In order of p1-p3-p2-p4
                si.setA(p1);
                si.setB(p3);

                sj.setA(p2);
                sj.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p2, p3, overlapping_col);
                return 1;
            }
            case PARALLEL_S1_END_OVERLAPS_S2_END_372: {//In order of p1-p4-p2-p3
                si.setA(p1);
                si.setB(p4);

                sj.setA(p3);
                sj.setB(p2);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p2, p4, overlapping_col);
                return 1;
            }
            case PARALLEL_S1_START_OVERLAPS_S2_END_373: {//In order of p3-p1-p4-p2
                sj.setA(p1);
                sj.setB(p3);
                si.setA(p2);
                si.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p1, p4, overlapping_col);
                return 1;
            }
            case PARALLEL_S1_START_OVERLAPS_S2_START_374: {//In order of p4-p1-p3-p2
                sj.setA(p1);
                sj.setB(p4);
                si.setA(p3);
                si.setB(p2);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                addLine(p1, p3, overlapping_col);
                return 1;
            }
            default:
                return -1;
        }
    }

    /**
     * Add line segment
     */
    private void addLine(Point pi, Point pj, LineColor i_c) {
        LineSegment s = new LineSegment();
        s.set(pi, pj, i_c);

        lineSegments.add(s);
    }

    public boolean contentEquals(LineSegmentSet other) {
        if (other == null || other.getNumLineSegments() != this.getNumLineSegments()) {
            return false;
        }
        Set<LineSegment> own = new HashSet<>(lineSegments);
        for (LineSegment lineSegment : other.lineSegments) {
            if (!own.contains(lineSegment)) {
                return false;
            }
        }
        return true;
    }
}
