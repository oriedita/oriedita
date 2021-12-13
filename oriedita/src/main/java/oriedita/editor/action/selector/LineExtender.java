package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Extends the line segment supplied by segmentToExtend until it hits the (extension of the) line selected
 * by lineSelector
 */
public class LineExtender extends CalculatedElementSelector<LineSegment, LineSegment> {
    private final Supplier<LineSegment> segmentSupplier;
    private final Supplier<LineColor> lineColorSupplier;

    public LineExtender(Supplier<LineSegment> segmentToExtend,
                        Supplier<LineColor> lineColor, ElementSelector<LineSegment> lineSelector) {
        super(lineSelector, true);
        segmentSupplier = segmentToExtend;
        lineColorSupplier = lineColor;
    }

    @Override
    protected LineSegment calculate(LineSegment closestSegment) {
        LineSegment segment = this.segmentSupplier.get();
        closestSegment.setColor(LineColor.GREEN_6);
        Point startingPoint = new Point();
        if (Epsilon.high.eq0(closestSegment.determineLength())) {
            startingPoint.set(OritaCalc.findProjection(segment, closestSegment.getA()));
        } else {
            startingPoint.set(OritaCalc.findIntersection(closestSegment, segment));
        }
        return new LineSegment(segment.getA(), startingPoint, lineColorSupplier.get());
    }

    @Override
    protected boolean validate(LineSegment selected, LineSegment calculated, MouseEventInfo eventInfo) {
        return Epsilon.high.gt0(selected.determineLength());
    }

    @Override
    public void draw(LineSegment element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawLineStep(g2, element, camera, settings);
    }
}
