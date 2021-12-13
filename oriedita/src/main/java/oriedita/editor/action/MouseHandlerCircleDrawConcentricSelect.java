package oriedita.editor.action;

import oriedita.editor.action.selector.BaseMouseHandler_WithSelector;
import oriedita.editor.action.selector.CircleCalculatorFromRadius;
import oriedita.editor.action.selector.CircleSelectorFromIterable;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDrawConcentricSelect extends BaseMouseHandler_WithSelector {
    Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse

    CircleSelectorFromIterable circleSelector1;
    CircleSelectorFromIterable circleSelector2;
    CircleCalculatorFromRadius circleCalculator;

    @Inject
    public MouseHandlerCircleDrawConcentricSelect() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49;
    }

    @Override
    protected void setupSelectors() {
        circleSelector1 = registerStartingSelector(
                new CircleSelectorFromIterable(
                        d.foldLineSet::getCircles,
                        circle -> LineColor.GREEN_6
                ),
                () -> circleSelector2
        );
        circleSelector2 = registerSelector(
                new CircleSelectorFromIterable(
                        d.foldLineSet::getCircles,
                        circle -> LineColor.PURPLE_8
                ),
                () -> circleCalculator
        );
        circleCalculator = registerSelector(
                new CircleCalculatorFromRadius(
                        () -> circleSelector1.getSelection().determineCenter(),
                        new CircleSelectorFromIterable(
                            d.foldLineSet::getCircles,
                            circle -> LineColor.PURPLE_8
                    ).thenGet(circle3 -> this.calculateRadius(
                                circleSelector1.getSelection(),
                                circleSelector2.getSelection(), circle3)
                    )
                ),
                null
        );
        circleCalculator.onFinish(circle -> {
            d.addCircle(circle);
            d.record();
        });
        onAnyFail(this::reset, circleSelector1, circleSelector2, circleCalculator);
    }

    private Double calculateRadius(Circle circle1, Circle circle2, Circle circle3) {
        double add_r = circle3.getR() - circle2.getR();
        if (Epsilon.high.eq0(add_r)) {
            return null;
        }
        double new_r = circle1.getR() + add_r;
        if (Epsilon.high.le0(new_r)) {
            return null;
        }
        return new_r;
    }
}
