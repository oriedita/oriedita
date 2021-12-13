package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CircleSelectorFromIterable extends ElementSelector<Circle> {
    private final Supplier<Iterable<Circle>> circleSet;
    private final Function<Circle, LineColor> color;

    public CircleSelectorFromIterable(Supplier<Iterable<Circle>> circleSet, Function<Circle, LineColor> color) {
        this.circleSet = circleSet;
        this.color = color;
    }

    @Override
    protected Circle determineSelected(Point mousePos, MouseEventInfo eventInfo) {
        Point p = d.camera.TV2object(mousePos);
        double minBorderDist = 100000;
        Circle minBorder = null;
        double minCenterDist = 100000;
        Circle minCenter = null;
        for (Circle circle : circleSet.get()) {
            double borderDist = Math.abs(circle.determineCenter().distance(p) - circle.getR());
            double centerDist = circle.determineCenter().distance(p);
            if (borderDist < minBorderDist) {
                minBorder = circle;
                minBorderDist = borderDist;
            }
            if (centerDist < minCenterDist) {
                minCenter = circle;
                minCenterDist = centerDist;
            }
        }
        if (minBorderDist < d.selectionDistance) {
            return copyWithColor(minBorder);
        }
        if (minCenterDist < d.selectionDistance) {
            return copyWithColor(minCenter);
        }
        return null;
    }

    private Circle copyWithColor(Circle circle) {
        Circle newCircle = new Circle(circle);
        newCircle.setColor(color.apply(circle));
        return newCircle;
    }

    @Override
    protected boolean validate(Circle element, MouseEventInfo eventInfo) {
        return element != null;
    }

    @Override
    public void draw(Circle element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        DrawingUtil.drawCircleStep(g2, element, camera);
    }
}
