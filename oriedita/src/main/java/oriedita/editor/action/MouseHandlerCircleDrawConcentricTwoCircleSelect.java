package oriedita.editor.action;

import oriedita.editor.action.selector.BaseMouseHandler_WithSelector;
import oriedita.editor.action.selector.CircleSelectorFromIterable;
import oriedita.editor.action.selector.ElementSelector;
import oriedita.editor.action.selector.TouchingConcentricCirclesCalculator;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDrawConcentricTwoCircleSelect extends BaseMouseHandler_WithSelector {

    private CircleSelectorFromIterable circleSelector1;
    private ElementSelector<Iterable<Circle>> concentricCircleCalculator;

    @Inject
    public MouseHandlerCircleDrawConcentricTwoCircleSelect() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50;
    }

    @Override
    protected void setupSelectors() {
        circleSelector1 = registerStartingSelector(
                new CircleSelectorFromIterable(
                        d.foldLineSet::getCircles,
                        (circle) -> LineColor.GREEN_6
                ),
                () -> concentricCircleCalculator
        );
        concentricCircleCalculator = registerSelector(
                new TouchingConcentricCirclesCalculator(
                        circleSelector1::getSelection,
                        new CircleSelectorFromIterable(
                                d.foldLineSet::getCircles,
                                (circle) -> LineColor.GREEN_6
                        )
                ),
                null
        );
        concentricCircleCalculator.onFinish(circles -> {
            for (Circle circle : circles) {
                d.addCircle(circle);
            }
            d.record();
        });
        concentricCircleCalculator.onFail(this::reset);
    }
}
