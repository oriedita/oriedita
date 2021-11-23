package oriedita.editor.action;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

/**
 * Mouse handler for modes which perform some action based on a drawn (magenta) line.
 */
public abstract class BaseMouseHandlerLineSelect extends BaseMouseHandler {
    @Override
    public void mouseMoved(Point p0) {
        //Display candidate points that can be selected with the mouse. If there is an established point nearby, that point is the candidate point, and if not, the mouse position itself is the candidate point.
        if (d.gridInputAssist) {
            d.lineCandidate.clear();

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point p = new Point();
            p.set(d.camera.TV2object(p0));

            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.selectionDistance) {
                candidate.set(closestPoint, closestPoint);
            } else {
                candidate.set(p, p);
            }

            candidate.setColor(LineColor.MAGENTA_5);

            d.lineCandidate.add(candidate);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        LineSegment s = new LineSegment(p, p);

        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.selectionDistance) {
            s.set(p, closest_point);
        }
        s.setColor(LineColor.MAGENTA_5);
        d.lineStepAdd(s);
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);
    }

    @Override
    public void mouseDragged(Point p0) {
        //近くの既成点かマウス位置表示

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.lineStep.get(0).setA(p);

        if (d.gridInputAssist) {
            d.lineCandidate.clear();
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineCandidate.add(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
            } else {
                d.lineCandidate.add(new LineSegment(p, p, LineColor.MAGENTA_5));
            }
            d.lineStep.get(0).setA(d.lineCandidate.get(0).getA());
        }
    }
}
