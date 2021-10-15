package origami_editor.editor.canvas;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseFree extends BaseMouseHandler {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_FREE_1;
    }

    public void mouseMoved(Point p0) {
        if (d.gridInputAssist) {
            d.lineCandidate.clear();

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            if (p.distance(closestPoint) < d.selectionDistance) {
                candidate.set(closestPoint, closestPoint);
            } else {
                candidate.set(p, p);
            }

            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                candidate.setColor(d.lineColor);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                candidate.setColor(d.auxLineColor);
            }

            d.lineCandidate.add(candidate);
        }
    }

    public void mousePressed(Point p0) {
        LineSegment s = new LineSegment();
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) < d.selectionDistance) {
            s.set(p, closestPoint);
        } else {
            s.set(p, p);
        }

        if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
            s.setColor(d.lineColor);
        }
        if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
            s.setColor(d.auxLineColor);
        }

        d.lineStepAdd(s);
    }

    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (!d.gridInputAssist) {
            d.lineStep.get(0).setA(p);
        }

        if (d.gridInputAssist) {
            d.lineCandidate.clear();

            Point closestPoint = d.getClosestPoint(p);

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            if (p.distance(closestPoint) < d.selectionDistance) {
                candidate.set(closestPoint, closestPoint);
            } else {
                candidate.set(p, p);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                candidate.setColor(d.lineColor);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                candidate.setColor(d.auxLineColor);
            }
            d.lineCandidate.add(candidate);
            d.lineStep.get(0).setA(candidate.getA());
        }
    }

    public void mouseReleased(Point p0) {
        Point p = new Point();

        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);
        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStep.get(0).setA(closestPoint);
        }
        if (d.lineStep.get(0).determineLength() > 0.00000001) {
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                d.addLineSegment(d.lineStep.get(0));
                d.record();
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                d.addLineSegment_auxiliary(d.lineStep.get(0));
                d.auxRecord();
            }
        }

        d.lineStep.clear();
    }
}
