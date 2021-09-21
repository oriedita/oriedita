package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseFree extends BaseMouseHandler {

    public MouseHandlerDrawCreaseFree(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_FREE_1;
    }

    public void mouseMoved(Point p0) {
        if (d.gridInputAssist) {
            d.line_candidate[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.i_candidate_stage = 1;
            d.closest_point.set(d.getClosestPoint(p));

            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.line_candidate[1].set(d.closest_point, d.closest_point);
            } else {
                d.line_candidate[1].set(p, p);
            }

            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                d.line_candidate[1].setColor(d.lineColor);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                d.line_candidate[1].setColor(d.auxLineColor);
            }

        }
    }

    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;
        d.line_step[1].setActive(LineSegment.ActiveState.ACTIVE_B_2);
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.closest_point.set(d.getClosestPoint(d.p));
        if (p.distance(d.closest_point) < d.selectionDistance) {
            d.line_step[1].set(d.p, d.closest_point);
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                d.line_step[1].setColor(d.lineColor);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                d.line_step[1].setColor(d.auxLineColor);
            }
            return;
        }

        d.line_step[1].set(p, p);
        if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
            d.line_step[1].setColor(d.lineColor);
        }
        if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
            d.line_step[1].setColor(d.auxLineColor);
        }
    }

    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (!d.gridInputAssist) {
            d.line_step[1].setA(p);
        }

        if (d.gridInputAssist) {
            d.closest_point.set(d.getClosestPoint(p));
            d.i_candidate_stage = 1;
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.line_candidate[1].set(d.closest_point, d.closest_point);
            } else {
                d.line_candidate[1].set(p, p);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                d.line_candidate[1].setColor(d.lineColor);
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                d.line_candidate[1].setColor(d.auxLineColor);
            }
            d.line_step[1].setA(d.line_candidate[1].getA());
        }
    }

    public void mouseReleased(Point p0) {
        Point p = new Point();

        d.i_drawing_stage = 0;
        p.set(d.camera.TV2object(p0));
        d.line_step[1].setA(p);
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) <= d.selectionDistance) {
            d.line_step[1].setA(d.closest_point);
        }
        if (d.line_step[1].getLength() > 0.00000001) {
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.POLY_LINE_0) {
                d.addLineSegment(d.line_step[1]);
                d.record();
            }
            if (d.i_foldLine_additional == FoldLineAdditionalInputMode.AUX_LINE_1) {
                d.addLineSegment_auxiliary(d.line_step[1]);
                d.auxRecord();
            }
        }
    }
}
