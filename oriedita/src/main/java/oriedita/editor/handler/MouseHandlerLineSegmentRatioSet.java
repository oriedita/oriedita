package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.InternalDivisionRatioModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum LineSegmentRatioSetStep { CLICK_DRAG_POINT }

@ApplicationScoped
@Handles(MouseMode.LINE_SEGMENT_RATIO_SET_28)
public class MouseHandlerLineSegmentRatioSet extends StepMouseHandler<LineSegmentRatioSetStep> {
    private Point anchorPoint, releasePoint;
    private LineSegment dragSegment;
    private final InternalDivisionRatioModel internalDivisionRatioModel;

    @Inject
    public MouseHandlerLineSegmentRatioSet(InternalDivisionRatioModel internalDivisionRatioModel) {
        super(LineSegmentRatioSetStep.CLICK_DRAG_POINT);
        steps.addNode(StepNode.createNode(LineSegmentRatioSetStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {}, this::drag_click_drag_point, this::release_click_drag_point));
        this.internalDivisionRatioModel = internalDivisionRatioModel;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        anchorPoint = p;
        if(p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        }
    }
    private void drag_click_drag_point(Point p) {
        releasePoint = p;
        if(p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
    }
    private LineSegmentRatioSetStep release_click_drag_point(Point p) {
        if (releasePoint == null) {
            reset();
            return LineSegmentRatioSetStep.CLICK_DRAG_POINT;
        }
        if(!Epsilon.high.gt0(dragSegment.determineLength())) return LineSegmentRatioSetStep.CLICK_DRAG_POINT;
        dragSegment = dragSegment.withAB(dragSegment.getB(), dragSegment.getA());
        double internalDivisionRatio_s = internalDivisionRatioModel.getInternalDivisionRatioS();
        double internalDivisionRatio_t = internalDivisionRatioModel.getInternalDivisionRatioT();
        if ((internalDivisionRatio_s == 0.0) && (internalDivisionRatio_t != 0.0)) {
            d.addLineSegment(dragSegment);
        }
        if ((internalDivisionRatio_s != 0.0) && (internalDivisionRatio_t == 0.0)) {
            d.addLineSegment(dragSegment);
        }
        if ((internalDivisionRatio_s != 0.0) && (internalDivisionRatio_t != 0.0)) {
            LineSegment s_ad = new LineSegment().withColor(d.getLineColor());
            double nx = (internalDivisionRatio_t * dragSegment.determineBX() + internalDivisionRatio_s * dragSegment.determineAX())
                    / (internalDivisionRatio_s + internalDivisionRatio_t);
            double ny = (internalDivisionRatio_t * dragSegment.determineBY() + internalDivisionRatio_s * dragSegment.determineAY())
                    / (internalDivisionRatio_s + internalDivisionRatio_t);
            d.addLineSegment(s_ad.withCoordinates(dragSegment.determineAX(), dragSegment.determineAY(), nx, ny));
            d.addLineSegment(s_ad.withCoordinates(dragSegment.determineBX(), dragSegment.determineBY(), nx, ny));
        }
        d.record();
        reset();
        return LineSegmentRatioSetStep.CLICK_DRAG_POINT;
    }
}
