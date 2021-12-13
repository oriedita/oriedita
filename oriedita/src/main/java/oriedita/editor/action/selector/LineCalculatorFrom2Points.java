package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.function.Supplier;

public class LineCalculatorFrom2Points extends CalculatedElementSelector<Point, LineSegment> {
    private final Supplier<Point> firstPoint;
    private final Supplier<LineColor> lineColor;

    public LineCalculatorFrom2Points(
            Supplier<Point> firstPoint, Supplier<LineColor> lineColor, ElementSelector<Point> secondPoint) {
        super(secondPoint, true);
        this.firstPoint = firstPoint;
        this.lineColor = lineColor;
    }

    @Override
    protected LineSegment calculate(Point baseSelected) {
        return new LineSegment(firstPoint.get(), baseSelected, lineColor.get());
    }

    @Override
    public void draw(LineSegment element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawLineStep(g2, element, camera, settings);
    }
}
