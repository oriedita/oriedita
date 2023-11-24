package origami.crease_pattern.worker.linesegmentset;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.LineSegmentSetLineAdapter;

public class IntersectDivide {
    /**
     * Divide the two line segments at the intersection of the two intersecting line segments. If there were two line segments that completely overlapped, both would remain without any processing.
     */
    public static void apply(LineSegmentSet lineSegmentSet) throws InterruptedException {
        QuadTree qt = new QuadTree(new LineSegmentSetLineAdapter(lineSegmentSet));
        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            boolean found = false;
            for (int j : qt.getPotentialCollision(i)) {
                int added = intersect_divide(lineSegmentSet, i, j); // Side effect
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
    private static int intersect_divide(LineSegmentSet lineSegmentSet, int i, int j) {
        if (i == j) {
            return -1;
        }

        LineSegment si = lineSegmentSet.get(i);
        LineSegment sj = lineSegmentSet.get(j);

        Point p1 = new Point(si.getA());
        Point p2 = new Point(si.getB());
        Point p3 = new Point(sj.getA());
        Point p4 = new Point(sj.getB());

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

        Point pk;
        switch (intersect_decide) {
            case INTERSECTS_1:
                pk = (OritaCalc.findIntersection(si, sj));

                si.setA(p1);
                si.setB(pk);
                sj.setA(p3);
                sj.setB(pk);
                lineSegmentSet.addLine(p2, pk, si.getColor());
                lineSegmentSet.addLine(p4, pk, sj.getColor());
                return 2;
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
            case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                pk = (OritaCalc.findIntersection(si, sj));

                sj.setA(p3);
                sj.setB(pk);
                lineSegmentSet.addLine(p4, pk, sj.getColor());
                return 1;

            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
            case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                pk = (OritaCalc.findIntersection(si, sj));

                si.setA(p1);
                si.setB(pk);
                lineSegmentSet.addLine(p2, pk, si.getColor());
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

                lineSegmentSet.addLine(p2, p4, si.getColor());
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

                lineSegmentSet.addLine(p2, p3, si.getColor());

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

                lineSegmentSet.addLine(p2, p4, sj.getColor());

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

                lineSegmentSet.addLine(p2, p3, sj.getColor());

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
                lineSegmentSet.addLine(p2, p3, overlapping_col);
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
                lineSegmentSet.addLine(p2, p4, overlapping_col);
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
                lineSegmentSet.addLine(p1, p4, overlapping_col);
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
                lineSegmentSet.addLine(p1, p3, overlapping_col);
                return 1;
            }
            default:
                return -1;
        }
    }
}
