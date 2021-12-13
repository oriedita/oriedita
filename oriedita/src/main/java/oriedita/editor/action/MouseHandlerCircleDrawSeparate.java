package oriedita.editor.action;

import oriedita.editor.action.selector.BaseMouseHandler_WithSelector;
import oriedita.editor.action.selector.CircleCalculatorFromRadius;
import oriedita.editor.action.selector.CreasePatternPointSelector;
import oriedita.editor.action.selector.LineCalculatorFrom2Points;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDrawSeparate extends BaseMouseHandler_WithSelector {

    private CreasePatternPointSelector center;
    private CreasePatternPointSelector radiusFirstPoint;
    private CircleCalculatorFromRadius circleCalculator;

    @Inject
    public MouseHandlerCircleDrawSeparate() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_SEPARATE_44;
    }

    @Override
    public void setupSelectors() {
        center = registerStartingSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                        LineColor.CYAN_3
                ),
                () -> radiusFirstPoint
        );
        radiusFirstPoint = registerSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                        LineColor.CYAN_3
                ),
                () -> circleCalculator
        );
        circleCalculator = registerSelector(
                new CircleCalculatorFromRadius(
                        center::getSelection,
                        new LineCalculatorFrom2Points(
                                radiusFirstPoint::getSelection,
                                () -> LineColor.CYAN_3,
                                new CreasePatternPointSelector(
                                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                                        LineColor.CYAN_3
                                )
                        ).thenGet(LineSegment::determineLength)
                ),
                FinishOn.RELEASE,
                () -> null
        );
        circleCalculator.onFail(() -> revertTo(radiusFirstPoint));
        circleCalculator.onFinish(circle -> {
            if (Epsilon.high.gt0(circle.getR())) {
                d.addCircle(circle);
                d.record();
            }
        });
    }
}
