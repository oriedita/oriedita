package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_FREE_1)
public class MouseHandlerDrawCreaseFree extends BaseMouseHandler {
    @Inject
    public MouseHandlerDrawCreaseFree() {
    }

    public void mouseMoved(Point p0) {
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            Point p = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                p = closestPoint;
            }
            LineSegment candidate = new LineSegment(p, p);

            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0) {
                candidate.setColor(d.getLineColor());
            }
            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
                candidate.setColor(d.getAuxLineColor());
            }
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

            d.getLineCandidate().add(candidate);
        }
    }

    public void mousePressed(Point p0) {

        Point p = d.getCamera().TV2object(p0);

        LineSegment s = new LineSegment(p, p);
        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) < d.getSelectionDistance()) {
            s = s.withB(closestPoint);
        }

        if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0) {
            s.setColor(d.getLineColor());
        }
        if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
            s.setColor(d.getAuxLineColor());
        }
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);

        d.lineStepAdd(s);
    }

    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        if (!d.getGridInputAssist()) {
            d.getLineStep().set(0, d.getLineStep().get(0).withA(p));

            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
                d.getLineStep().get(0).setColor(d.getAuxLineColor());
            } else {
                d.getLineStep().get(0).setColor(d.getLineColor());
            }
        }

        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                p = closestPoint;
            }

            LineSegment candidate = new LineSegment(p, p);
            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0) {
                candidate.setColor(d.getLineColor());
            }
            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
                candidate.setColor(d.getAuxLineColor());
            }
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            d.getLineCandidate().add(candidate);
        }
    }

    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        d.getLineStep().set(0, d.getLineStep().get(0).withA(p));
        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) <= d.getSelectionDistance()) {
            d.getLineStep().set(0, d.getLineStep().get(0).withA(closestPoint));
        }
        if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0) {
                d.addLineSegment(d.getLineStep().get(0));
                d.record();
            }
            if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
                d.addLineSegment_auxiliary(d.getLineStep().get(0));
                d.auxRecord();
            }
        }

        d.getLineStep().clear();
    }
}
