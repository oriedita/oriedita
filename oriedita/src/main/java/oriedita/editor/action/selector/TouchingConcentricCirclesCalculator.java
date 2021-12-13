package oriedita.editor.action.selector;

import oriedita.editor.action.selector.drawing.DrawingSettings;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Supplier;

public class TouchingConcentricCirclesCalculator extends CalculatedElementSelector<Circle, Iterable<Circle>> {
    private final Supplier<Circle> firstCircle;

    public TouchingConcentricCirclesCalculator(Supplier<Circle> firstCircle, ElementSelector<Circle> base) {
        super(base, true);
        this.firstCircle = firstCircle;
    }

    @Override
    protected Iterable<Circle> calculate(Circle baseSelected) {
        Circle circle1 = new Circle(firstCircle.get());
        Circle circle2 = new Circle(baseSelected);
        double add_r = (OritaCalc.distance(circle1.determineCenter(), circle2.determineCenter()) - circle1.getR() - circle2.getR()) / 2;

        if (!Epsilon.high.eq0(add_r)) {
            double new_r1 = add_r + circle1.getR();
            double new_r2 = add_r + circle2.getR();

            if (Epsilon.high.gt0(new_r1) && Epsilon.high.gt0(new_r2)) {
                circle1.setR(new_r1);
                circle1.setColor(LineColor.CYAN_3);
                circle2.setR(new_r2);
                circle2.setColor(LineColor.CYAN_3);
                return Arrays.asList(circle1, circle2);
            }
        }
        return null;
    }

    @Override
    public void draw(Iterable<Circle> element, Graphics2D g2, Camera camera, DrawingSettings settings) {
        for (Circle circle : element) {
            DrawingUtil.drawCircleStep(g2, circle, camera);
        }
    }
}
