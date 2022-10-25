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

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));

            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                candidate.set(closestPoint, closestPoint);
            } else {
                candidate.set(p, p);
            }

            candidate.setColor(LineColor.MAGENTA_5);

            d.getLineCandidate().add(candidate);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        LineSegment s = new LineSegment(p, p);

        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) < d.getSelectionDistance()) {
            s.set(p, closest_point);
        }
        s.setColor(LineColor.MAGENTA_5);
        d.lineStepAdd(s);
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);
        selectionLine = s;
    }

    @Override
    public void mouseDragged(Point p0, MouseEvent e) {
        snapping = e.isControlDown();
        mouseDragged(p0);
    }

    private void snapLine() {
        selectionLine.setA(SnappingUtil.snapToClosePointInActiveAngleSystem(d, selectionLine.getB(), selectionLine.getA(), angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles()));

        d.getLineStep().get(0).setA(SnappingUtil.snapToClosePointInActiveAngleSystem(d, d.getLineStep().get(0).getB(), d.getLineStep().get(0).getA(), angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles()));
    }

    @Override
    public void mouseDragged(Point p0) {
        //近くの既成点かマウス位置表示

        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

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
