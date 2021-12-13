package oriedita.editor.action;

import oriedita.editor.action.selector.*;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.element.LineColor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDrawConcentric extends BaseMouseHandler_WithSelector {
    private CircleSelectorFromIterable circleSelector;
    private CreasePatternPointSelector firstPointSelector;
    private CircleCalculatorFromRadius circleCalculator;

    @Inject
    public MouseHandlerCircleDrawConcentric() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_CONCENTRIC_48;
    }

    @Override
    public void setupSelectors() {
        circleSelector = registerStartingSelector(
                new CircleSelectorFromIterable(
                        () -> d.foldLineSet.getCircles(),
                        circle -> LineColor.GREEN_6
                ),
                () -> firstPointSelector
        );
        firstPointSelector = registerSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_OR_FREE,
                        LineColor.MAGENTA_5
                ),
                () -> circleCalculator
        );
        circleCalculator = registerSelector(
                new CircleCalculatorFromRadius(
                        () -> circleSelector.getSelection().determineCenter(),
                        new LineCalculatorFrom2Points(
                                firstPointSelector::getSelection,
                                () -> LineColor.MAGENTA_5,
                                new CreasePatternPointSelector(
                                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_OR_FREE,
                                        LineColor.MAGENTA_5
                                )
                        ).thenGet(lineSegment -> circleSelector.getSelection().getR() + lineSegment.determineLength())
                ),
                FinishOn.RELEASE,
                null
        );
        circleCalculator.onFail(() -> revertTo(firstPointSelector));
        circleCalculator.onFinish(circle -> {
            d.addCircle(circle);
            d.record();
        });
    }
}
