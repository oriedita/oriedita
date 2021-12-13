package oriedita.editor.action;

import oriedita.editor.action.selector.*;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDraw extends BaseMouseHandler_WithSelector {
    private CreasePatternPointSelector center;
    private ElementSelector<Circle> circleCalculator;

    @Inject
    public MouseHandlerCircleDraw() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_42;
    }

    @Override
    public void setupSelectors() {
        center = registerStartingSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                        LineColor.CYAN_3
                ),
                () -> circleCalculator
        );
        circleCalculator = registerSelector(
                new CircleCalculator(
                        center::getSelection,
                        new LineCalculatorFrom2Points(
                                center::getSelection,
                                () -> LineColor.CYAN_3,
                                new CreasePatternPointSelector(
                                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                                        LineColor.CYAN_3
                                )
                        )
                ),
                FinishOn.RELEASE,
                null
        );
        circleCalculator.onFinish(circle -> {
            if (Epsilon.high.gt0(circle.getR())) {
                d.addCircle(circle);
                d.record();
            }
        });
    }
}
