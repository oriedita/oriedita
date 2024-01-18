package origami.crease_pattern;

import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.PointLineMapAdapter;
import origami.data.quadTree.collector.PointCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PointLineMap maps all {@link Point}s in a CP to the adjacent
 * {@link LineSegment}s. Right now this object is constructed as needed, but in
 * the future it would be much more efficient for {@link FoldLineSet} to
 * maintain an instance of this map at all times.
 *
 * @author Mu-Tsun Tsai
 */
public class PointLineMap {

    private final static double eps = Epsilon.UNKNOWN_1EN4 * Epsilon.UNKNOWN_1EN4;

    private final Map<Point, List<LineSegment>> map = new HashMap<>();
    private final List<Point> points = new ArrayList<>(); // Instantiation of points to check

    public PointLineMap(List<LineSegment> lineSegments) throws InterruptedException {
        QuadTree qt = new QuadTree(new PointLineMapAdapter(lineSegments, points));
        for (int i = 1; i < lineSegments.size(); i++) {
            LineSegment si = lineSegments.get(i);
            if (si.getColor() != LineColor.CYAN_3) {
                process(si.getA(), si, qt);
                process(si.getB(), si, qt);
                if (Thread.interrupted()) throw new InterruptedException();
            }
        }
    }

    private void process(Point p, LineSegment l, QuadTree qt) {
        Point pt = null;
        for (int i : qt.collect(new PointCollector(p))) {
            Point q = points.get(i);
            if (q.distanceSquared(p) < eps) {
                pt = q;
                break;
            }
        }
        if (pt == null) {
            points.add(p);
            qt.grow(1);
        }
        map.computeIfAbsent(pt, k -> new ArrayList<>()).add(l);
    }

    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public List<LineSegment> getLines(Point p) {
        if (map.containsKey(p)){
            return Collections.unmodifiableList(map.get(p));
        }
        return Collections.emptyList();
    }

    public void replaceLine(LineSegment oldLine, LineSegment newLine) {
        replaceLine(oldLine.getA(), oldLine, newLine);
        replaceLine(oldLine.getB(), oldLine, newLine);
    }

    private void replaceLine(Point p, LineSegment oldLine, LineSegment newLine) {
        List<LineSegment> lines = map.get(p);
        if (lines != null && lines.remove(oldLine)) lines.add(newLine);
    }
}
