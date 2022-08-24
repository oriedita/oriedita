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

    public LineSegment get(int i) {
        return lineSegments.get(i);
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
     * Add line segment
     */
    public void addLine(Point pi, Point pj, LineColor i_c) {
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
