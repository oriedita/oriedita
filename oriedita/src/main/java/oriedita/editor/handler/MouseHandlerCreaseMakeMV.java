package oriedita.editor.handler;

import java.awt.Graphics2D;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.folding.util.SortingBox;

enum CreaseMakeMVStep {
    CLICK_DRAG_POINT
}

@ApplicationScoped
@Handles(MouseMode.CREASE_MAKE_MV_34)
public class MouseHandlerCreaseMakeMV extends StepMouseHandler<CreaseMakeMVStep> {
    @Inject
    public MouseHandlerCreaseMakeMV() {
        super(CreaseMakeMVStep.CLICK_DRAG_POINT);
        steps.addNode(ObjCoordStepNode.createNode(CreaseMakeMVStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {
        }, this::drag_click_drag_point, this::release_click_drag_point));
    }

    private Point anchorPoint, releasePoint;
    private LineSegment previewSegment;

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, previewSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        anchorPoint = null;
        releasePoint = null;
        previewSegment = null;
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        anchorPoint = p;
        if (anchorPoint.distance(tmpPoint) < d.getSelectionDistance()) {
            anchorPoint = tmpPoint;
        }
    }

    private void drag_click_drag_point(Point p) {
        if (anchorPoint == null)
            return;
        if (anchorPoint.distance(d.getClosestPoint(anchorPoint)) > d.getSelectionDistance()) {
            anchorPoint = null;
            return;
        }

        Point tmpPoint = d.getClosestPoint(p);
        releasePoint = p;
        if (releasePoint.distance(tmpPoint) < d.getSelectionDistance()) {
            releasePoint = tmpPoint;
        }

        previewSegment = new LineSegment(anchorPoint, releasePoint, d.getLineColor());
    }

    private CreaseMakeMVStep release_click_drag_point(Point p) {
        if (anchorPoint == null) {
            return CreaseMakeMVStep.CLICK_DRAG_POINT;
        }
        if (releasePoint.distance(d.getClosestPoint(releasePoint)) > d.getSelectionDistance()) {
            reset();
            return CreaseMakeMVStep.CLICK_DRAG_POINT;
        }

        SortingBox<LineSegment> nbox = new SortingBox<>();

        if (Epsilon.high.gt0(previewSegment.determineLength())) {
            for (var s : d.getFoldLineSet().getLineSegmentsIterable()) {
                if (OritaCalc.isLineSegmentOverlapping(s, previewSegment)) {
                    nbox.addByWeight(s,
                            OritaCalc.determineLineSegmentDistance(previewSegment.getA(), s));
                }
            }

            LineColor icol_temp = d.getLineColor();

            for (int i = 1; i <= nbox.getTotal(); i++) {
                d.getFoldLineSet().setColor(nbox.getValue(i), icol_temp);

                if (icol_temp == LineColor.RED_1) {
                    icol_temp = LineColor.BLUE_2;
                } else if (icol_temp == LineColor.BLUE_2) {
                    icol_temp = LineColor.RED_1;
                }
            }
            d.record();
        }

        reset();
        return CreaseMakeMVStep.CLICK_DRAG_POINT;
    }
}
