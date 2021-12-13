package oriedita.editor.action.selector;

import oriedita.editor.action.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.function.Supplier;

public class CircleCalculator extends CalculatedElementSelector<LineSegment, Circle> {
    private final Supplier<Point> center;

    public CircleCalculator(Supplier<Point> center, ElementSelector<LineSegment> radius) {
        super(radius, true);
        this.center = center;
    }

    @Override
    protected Circle calculate(LineSegment radius) {
        return new Circle(center.get(), radius.determineLength(), LineColor.CYAN_3);
    }

    @Override
    protected void draw(Circle element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawCircleStep(g2, element, camera);
    }
}
