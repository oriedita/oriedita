package oriedita.editor.action;

import oriedita.editor.action.drawing.CircleDrawer;
import oriedita.editor.action.selector.BaseMouseHandler_WithSelector;
import oriedita.editor.action.selector.CreasePatternPointSelector;
import oriedita.editor.action.selector.ElementSelector;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDrawThreePoint extends BaseMouseHandler_WithSelector {
    ElementSelector<Point> firstPoint;
    ElementSelector<Point> secondPoint;
    ElementSelector<Circle> finalCircle;

    @Inject
    public MouseHandlerCircleDrawThreePoint() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_THREE_POINT_43;
    }

    @Override
    protected void setupSelectors() {
        firstPoint = registerStartingSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                        LineColor.CYAN_3
                ),
                () -> secondPoint
        );
        secondPoint = registerSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                        LineColor.CYAN_3
                ),
                () -> finalCircle
        );
        finalCircle = registerSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                        LineColor.CYAN_3
                ).thenGet(point -> {
                    if (!OritaCalc.collinear(firstPoint.getSelection(), secondPoint.getSelection(), point)) {
                        LineSegment sen1 = new LineSegment(firstPoint.getSelection(), secondPoint.getSelection());
                        LineSegment sen2 = new LineSegment(secondPoint.getSelection(), point);
                        StraightLine t1 = new StraightLine(sen1);
                        t1.orthogonalize(OritaCalc.internalDivisionRatio(sen1.getA(), sen1.getB(), 1.0, 1.0));
                        StraightLine t2 = new StraightLine(sen2);
                        t2.orthogonalize(OritaCalc.internalDivisionRatio(sen2.getA(), sen2.getB(), 1.0, 1.0));
                        return new Circle(
                                OritaCalc.findIntersection(t1, t2),
                                OritaCalc.distance(firstPoint.getSelection(), OritaCalc.findIntersection(t1, t2)), LineColor.CYAN_3);
                    }
                    return null;
                }, new CircleDrawer()),
                null
        );
        finalCircle.onFinish(circle -> {
            d.addCircle(circle);
            d.record();
        });
    }
}
