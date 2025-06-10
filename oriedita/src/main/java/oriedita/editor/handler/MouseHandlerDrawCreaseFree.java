package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum DrawCreaseFreeStep { CLICK_DRAG_POINT }

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_FREE_1)
public class MouseHandlerDrawCreaseFree extends StepMouseHandler<DrawCreaseFreeStep> {
    private LineColor lineColor;
    private Point anchorPoint, releasePoint;
    private LineSegment dragSegment;

    @Inject
    public MouseHandlerDrawCreaseFree() {
        super(DrawCreaseFreeStep.CLICK_DRAG_POINT);
        steps.addNode(StepNode.createNode(DrawCreaseFreeStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {}, this::drag_click_drag_point, this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, lineColor, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, lineColor, camera, d.getGridInputAssist());
        if (dragSegment != null) {
            DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        }
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        steps.setCurrentStep(DrawCreaseFreeStep.CLICK_DRAG_POINT);
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        anchorPoint = p;
        if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0) {
            lineColor = d.getLineColor();
        } else if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
            lineColor = d.getAuxLineColor();
        }
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        }
    }
    private void drag_click_drag_point(Point p) {
        releasePoint = p;
        if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0) {
            lineColor = d.getLineColor();
        } else if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
            lineColor = d.getAuxLineColor();
        }
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(lineColor);
    }
    private DrawCreaseFreeStep release_click_drag_point(Point p) {
        if (anchorPoint == null) return DrawCreaseFreeStep.CLICK_DRAG_POINT;
        if (releasePoint == null
                || !Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
            return DrawCreaseFreeStep.CLICK_DRAG_POINT;
        }
        if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.POLY_LINE_0
                || d.getI_foldLine_additional() == FoldLineAdditionalInputMode.BOTH_4) {
            d.addLineSegment(dragSegment);
            d.record();
        }
        if (d.getI_foldLine_additional() == FoldLineAdditionalInputMode.AUX_LINE_1) {
            d.addLineSegment_auxiliary(dragSegment);
            d.record();
        }
        reset();
        return DrawCreaseFreeStep.CLICK_DRAG_POINT;
    }
}
