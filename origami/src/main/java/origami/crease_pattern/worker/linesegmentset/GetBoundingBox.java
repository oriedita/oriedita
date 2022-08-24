package origami.crease_pattern.worker.linesegmentset;

import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

public class GetBoundingBox {

    public static Polygon getBoundingBox(LineSegmentSet lineSegmentSet) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            LineSegment lineSegment = lineSegmentSet.get(i);

            minX = Math.min(minX, lineSegment.getA().getX());
            minX = Math.min(minX, lineSegment.getB().getX());

            maxX = Math.max(maxX, lineSegment.getA().getX());
            maxX = Math.max(maxX, lineSegment.getB().getX());

            minY = Math.min(minY, lineSegment.getA().getY());
            minY = Math.min(minY, lineSegment.getB().getY());

            maxY = Math.max(maxY, lineSegment.getA().getY());
            maxY = Math.max(maxY, lineSegment.getB().getY());
        }

        Point p_a = new Point(minX, minY);
        Point p_b = new Point(minX, maxY);
        Point p_c = new Point(maxX, maxY);
        Point p_d = new Point(maxX, minY);

        return new Polygon(p_a, p_b, p_c, p_d);
    }
}
