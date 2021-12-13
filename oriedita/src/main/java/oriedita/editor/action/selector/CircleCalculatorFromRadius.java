package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.function.Supplier;

public class CircleCalculatorFromRadius extends CalculatedElementSelector<Double, Circle> {
    private final Supplier<Point> center;

    public CircleCalculatorFromRadius(Supplier<Point> center, ElementSelector<Double> radius) {
        super(radius, true);
        this.center = center;
    }

    @Override
    protected Circle calculate(Double radius) {
        return new Circle(center.get(), radius, LineColor.CYAN_3);
    }

    @Override
    public void draw(Circle element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawCircleStep(g2, element, camera);
    }
}
