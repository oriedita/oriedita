package origami.data.quadTree.collector;

import origami.crease_pattern.element.LineSegment;

/**
 * Get all items that might partially contains the given line.
 */
public class LineSegmentCollector extends RectangleCollector {
    public LineSegmentCollector(LineSegment line) {
        super(line.getA(), line.getB());
    }
}
