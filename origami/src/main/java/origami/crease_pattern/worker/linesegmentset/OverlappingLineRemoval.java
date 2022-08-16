package origami.crease_pattern.worker.linesegmentset;

import origami.Epsilon;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.LineSegmentEndPointAdapter;
import origami.data.quadTree.collector.EndPointCollector;
import origami.data.quadTree.collector.QuadTreeCollector;

import java.util.ArrayList;
import java.util.List;

public class OverlappingLineRemoval {
    public static void apply(LineSegmentSet input) {
        overlapping_line_removal(input, Epsilon.UNKNOWN_001);
    }

    public static void overlapping_line_removal(LineSegmentSet lineSegmentSet, double r) {
        QuadTree qtA = new QuadTree(new LineSegmentEndPointAdapter(lineSegmentSet, LineSegmentSet::getA));
        QuadTree qtB = new QuadTree(new LineSegmentEndPointAdapter(lineSegmentSet, LineSegmentSet::getB));

        boolean[] removal_flg = new boolean[lineSegmentSet.getNumLineSegments()];
        List<LineSegment> snew = new ArrayList<>();

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            LineSegment si = lineSegmentSet.get(i);
            Point p1 = si.getA();
            Point p2 = si.getB();
            QuadTreeCollector c = new EndPointCollector(p1, i);
            for (int j : qtA.collect(c)) {
                LineSegment sj = lineSegmentSet.get(j);
                Point p3 = sj.getA();
                Point p4 = sj.getB();
                if (OritaCalc.equal(p1, p3, r) && OritaCalc.equal(p2, p4, r)) {
                    removal_flg[j] = true;
                }
            }
            for (int j : qtB.collect(c)) {
                LineSegment sj = lineSegmentSet.get(j);
                Point p3 = sj.getA();
                Point p4 = sj.getB();
                if (OritaCalc.equal(p1, p4, r) && OritaCalc.equal(p2, p3, r)) {
                    removal_flg[j] = true;
                }
            }
        }

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            if (!removal_flg[i]) {
                snew.add(lineSegmentSet.get(i));
            }
        }

        lineSegmentSet.reset(snew.size());

        for (int i = 0; i < snew.size(); i++) {
            LineSegment lineSegment = snew.get(i);
            lineSegmentSet.get(i).set(lineSegment);
        }
    }
}
