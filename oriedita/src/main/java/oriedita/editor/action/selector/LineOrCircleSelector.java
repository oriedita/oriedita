package oriedita.editor.action.selector;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

public class LineOrCircleSelector extends ConditionalSelector<LineSegment, Circle> {

    public LineOrCircleSelector(ElementSelector<LineSegment> firstSelector, ElementSelector<Circle> secondSelector) {
        super(firstSelector, secondSelector);
    }

    @Override
    protected TypeChoice choose(LineSegment first, Circle second, Point mousePos, MouseEventInfo eventInfo) {
        Point p = d.camera.TV2object(mousePos);
        double lineDist = OritaCalc.determineLineSegmentDistance(p, first);
        double circleCenterDist = second.determineCenter().distance(p);
        double circleBorderDist = Math.abs(circleCenterDist - second.getR());
        if (lineDist >= d.selectionDistance) {
            if (circleBorderDist < d.selectionDistance || circleCenterDist < d.selectionDistance) {
                return TypeChoice.SECOND;
            }
        } else if (circleBorderDist < d.selectionDistance) {
            if (circleBorderDist < lineDist) {
                return TypeChoice.SECOND;
            } else {
                return TypeChoice.FIRST;
            }
        }
        return null;
    }
}
