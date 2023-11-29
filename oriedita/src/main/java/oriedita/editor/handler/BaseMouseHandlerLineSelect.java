package oriedita.editor.handler;

import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.tools.SnappingUtil;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.event.MouseEvent;

/**
 * Mouse handler for modes which perform some action based on a drawn (magenta) line.
 */
public abstract class BaseMouseHandlerLineSelect extends BaseMouseHandler {
    protected final AngleSystemModel angleSystemModel;
    protected LineSegment selectionLine;
    protected boolean snapping = false;

    public BaseMouseHandlerLineSelect(AngleSystemModel angleSystemModel) {
        this.angleSystemModel = angleSystemModel;
    }

    @Override
    public void mouseMoved(Point p0) {
        //Display candidate points that can be selected with the mouse. If there is an established point nearby, that point is the candidate point, and if not, the mouse position itself is the candidate point.
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            Point candidatePoint = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(candidatePoint);
            if (candidatePoint.distance(closestPoint) < d.getSelectionDistance()) {
                candidatePoint = closestPoint;
            }
            LineSegment candidate = new LineSegment(
                    candidatePoint, candidatePoint, LineColor.MAGENTA_5, LineSegment.ActiveState.ACTIVE_BOTH_3);
            d.getLineCandidate().add(candidate);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        LineSegment s = new LineSegment(p, p);

        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.getSelectionDistance()) {
            s = s.withB(closest_point);
        }
        s.setColor(LineColor.MAGENTA_5);
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);
        d.lineStepAdd(s);
        selectionLine = s;
    }

    @Override
    public void mouseDragged(Point p0, MouseEvent e) {
        snapping = e.isControlDown();
        mouseDragged(p0);
    }

    private void snapLine() {
        selectionLine = selectionLine.withA(SnappingUtil.snapToClosePointInActiveAngleSystem(
                d, selectionLine.getB(), selectionLine.getA(),
                angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles()));

        d.getLineStep().get(0).setA(SnappingUtil.snapToClosePointInActiveAngleSystem(
                d, d.getLineStep().get(0).getB(), d.getLineStep().get(0).getA(),
                angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles()));
    }

    @Override
    public void mouseDragged(Point p0) {
        //近くの既成点かマウス位置表示

        Point p = d.getCamera().TV2object(p0);

        d.getLineStep().get(0).setA(p);
        selectionLine.setA(p);

        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.getSelectionDistance() && !snapping) {
            selectionLine = new LineSegment(closestPoint, selectionLine.getB(), LineColor.MAGENTA_5);
        } else {
            selectionLine = new LineSegment(p, selectionLine.getB(), LineColor.MAGENTA_5);
        }
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();
            d.getLineCandidate().add(selectionLine);
        }
        d.getLineStep().get(0).setA(selectionLine.getA());
        if (snapping) {
            snapLine();
        }
    }

    @Override
    public void mouseReleased(Point p0) {
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        selectionLine = null;
        snapping = false;
        d.getLineStep().clear();
        d.getLineCandidate().clear();
    }
}
