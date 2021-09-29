package origami.crease_pattern;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.Save;

import java.util.ArrayList;
import java.util.List;

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

    public void setSave(Save memo1) {
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
        List<Boolean> removal_flg = new ArrayList<>();
        List<LineSegment> snew = new ArrayList<>();
        for (int i = 0; i < lineSegments.size(); i++) {
            removal_flg.add(false);
            snew.add(new LineSegment());
        }

        for (int i = 0; i < lineSegments.size(); i++) {
            LineSegment si = lineSegments.get(i);
            for (int j = i + 1; j < lineSegments.size(); j++) {
                LineSegment sj = lineSegments.get(j);
                if (r <= -9999.9) {
                    if (OritaCalc.determineLineSegmentIntersection(si, sj) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg.set(j, true);
                    }
                } else {
                    if (OritaCalc.determineLineSegmentIntersection(si, sj, r, r) == LineSegment.Intersection.PARALLEL_EQUAL_31) {
                        removal_flg.set(j, true);
                    }
                }
            }
        }

        int smax = 0;
        for (int i = 0; i < lineSegments.size(); i++) {
            if (!removal_flg.get(i)) {
                LineSegment si = lineSegments.get(i);
                snew.get(smax).set(si);
                smax = smax + 1;
            }
        }

        if (smax > lineSegments.size()) {
            while (smax > lineSegments.size()) {
                lineSegments.add(new LineSegment());
            }
        }
        for (int i = 0; i < lineSegments.size(); i++) {
            LineSegment si = lineSegments.get(i);
            si.set(snew.get(i));
        }
    }

    public void overlapping_line_removal() {
        overlapping_line_removal(-10000.0);
    }

    /**
     * Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
     */
    public void intersect_divide() throws InterruptedException {
        int i_divide = 1;//1 if there is a split, 0 if not

        ArrayList<Boolean> k_flg = new ArrayList<>();//A flag that indicates that there is an effect of crossing.

        for (int i = 0; i < lineSegments.size(); i++) {
            k_flg.add(true);
        }

        while (i_divide != 0) {
            i_divide = 0;
            for (int i = 0; i < lineSegments.size(); i++) {
                if (k_flg.get(i)) {
                    k_flg.set(i, false);
                    for (int j = 0; j < lineSegments.size(); j++) {
                        if (i != j) {
                            if (k_flg.get(j)) {
                                int old_sousuu = lineSegments.size();
                                boolean itemp = intersect_divide(i, j); // Side effect
                                if (old_sousuu < lineSegments.size()) {
                                    for (int is = old_sousuu; is < lineSegments.size(); is++) {
                                        k_flg.add(true);
                                    }
                                }
                                if (itemp) {
                                    i_divide++;
                                    k_flg.set(i, true);
                                }
                            }
                        }
                    }
                }
            }

            if (Thread.interrupted()) throw new InterruptedException();
        }
    }

    /**
     * Divide the two line segments at the intersection of the two intersecting line segments. After splitting 1. Returns 0 if not done. From Orihime 2.002, the color of the line after splitting is also controlled (if there is an overlap, it will be unified and the color will be the one with the later number).
     */
    public boolean intersect_divide(int i, int j) {
        if (i == j) {
            return false;
        }

        LineSegment si = lineSegments.get(i);
        LineSegment sj = lineSegments.get(j);

        Point p1 = new Point(si.getA());
        Point p2 = new Point(si.getB());
        Point p3 = new Point(sj.getA());
        Point p4 = new Point(sj.getB());
        Point pk = new Point();

        double ixmax = Math.max(si.getAX(), si.getBX());
        double ixmin = Math.min(si.getAX(), si.getBX());
        double iymax = Math.max(si.getAY(), si.getBY());
        double iymin = Math.min(si.getAY(), si.getBY());

        double jxmax = Math.max(sj.getAX(), sj.getBX());
        double jxmin = Math.min(sj.getAX(), sj.getBX());
        double jymax = Math.max(sj.getAY(), sj.getBY());
        double jymin = Math.min(sj.getAY(), sj.getBY());

        if (ixmax + 0.5 < jxmin) {
            return false;
        }
        if (jxmax + 0.5 < ixmin) {
            return false;
        }
        if (iymax + 0.5 < jymin) {
            return false;
        }
        if (jymax + 0.5 < iymin) {
            return false;
        }

        LineSegment.Intersection intersect_decide = OritaCalc.determineLineSegmentIntersection(si, sj);
        switch (intersect_decide) {
            case INTERSECTS_1:
                pk.set(OritaCalc.findIntersection(si, sj));

                si.setA(p1);
                si.setB(pk);
                sj.setA(p3);
                sj.setB(pk);
                addLine(p2, pk, si.getColor());
                addLine(p4, pk, sj.getColor());
                return true;
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                pk.set(OritaCalc.findIntersection(si, sj));

                sj.setA(p3);
                sj.setB(pk);
                addLine(p4, pk, sj.getColor());
                return true;

            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                pk.set(OritaCalc.findIntersection(si, sj));

                si.setA(p1);
                si.setB(pk);
                addLine(p2, pk, si.getColor());
                return true;

            case PARALLEL_EQUAL_31: //If the two line segments are exactly the same, do nothing.
                return false;
            case PARALLEL_START_OF_S1_CONTAINS_START_OF_S2_321: {//The endpoints of two line segments (p1 and p3) overlap at one point. si contains sj
                si.setA(p2);
                si.setB(p4);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S2_CONTAINS_START_OF_S1_322: {//The endpoints of two line segments (p1 and p3) overlap at one point. sj contains si
                sj.setA(p2);
                sj.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
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

                return true;
            }
            case PARALLEL_END_OF_S2_CONTAINS_START_OF_S1_332: {//The endpoints of two line segments (p1 and p4) overlap at one point. sj contains si
                sj.setA(p2);
                sj.setB(p3);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);
                return true;
            }
            case PARALLEL_END_OF_S1_CONTAINS_START_OF_S2_341: {//The endpoints of two line segments (p2 and p3) overlap at one point. si contains sj
                si.setA(p1);
                si.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_START_OF_S2_CONTAINS_END_OF_S1_342: {//The endpoints of two line segments (p2 and p3) overlap at one point. sj contains si
                sj.setA(p1);
                sj.setB(p4);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S1_CONTAINS_END_OF_S2_351: {//The endpoints of two line segments (p2 and p4) overlap at one point. si contains sj
                si.setA(p1);
                si.setB(p3);

                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                sj.setColor(overlapping_col);

                return true;
            }
            case PARALLEL_END_OF_S2_CONTAINS_END_OF_S1_352: {//The endpoints of two line segments (p2 and p4) overlap at one point. sj contains si
                sj.setA(p1);
                sj.setB(p3);
                LineColor overlapping_col = si.getColor();
                if (i < j) {
                    overlapping_col = sj.getColor();
                }
                si.setColor(overlapping_col);

                return true;
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

                return true;
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

                return true;
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

                return true;
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

                return true;
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
                return true;
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
                return true;
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
                return true;
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
                return true;
            }
            default:
                return false;
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
}
