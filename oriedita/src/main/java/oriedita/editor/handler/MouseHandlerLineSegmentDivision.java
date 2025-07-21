package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.EnumSet;

enum LineSegmentDivisionStep {
    CLICK_DRAG_POINT
}

@ApplicationScoped
@Handles(MouseMode.LINE_SEGMENT_DIVISION_27)
public class MouseHandlerLineSegmentDivision extends StepMouseHandler<LineSegmentDivisionStep> {
    private Point anchorPoint, releasePoint;
    private LineSegment dragSegment;

    @Inject
    public MouseHandlerLineSegmentDivision() {
        super(LineSegmentDivisionStep.CLICK_DRAG_POINT);
        steps.addNode(
                StepNode.createNode(LineSegmentDivisionStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {
                }, this::drag_click_drag_point, this::release_click_drag_point));
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.LINE_DIVISION_COUNT);
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
        resetStep();
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        anchorPoint = p;
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        }
    }

    private void drag_click_drag_point(Point p) {
        releasePoint = p;
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
    }

    private LineSegmentDivisionStep release_click_drag_point(Point p) {
        if (releasePoint == null) {
            reset();
            return LineSegmentDivisionStep.CLICK_DRAG_POINT;
        }
        if (!Epsilon.high.gt0(dragSegment.determineLength()))
            return LineSegmentDivisionStep.CLICK_DRAG_POINT;
        for (int i = 0; i <= d.getFoldLineDividingNumber() - 1; i++) {
            double ax = ((double) (d.getFoldLineDividingNumber() - i) * dragSegment.determineAX()
                    + (double) i * dragSegment.determineBX()) / ((double) d.getFoldLineDividingNumber());
            double ay = ((double) (d.getFoldLineDividingNumber() - i) * dragSegment.determineAY()
                    + (double) i * dragSegment.determineBY()) / ((double) d.getFoldLineDividingNumber());
            double bx = ((double) (d.getFoldLineDividingNumber() - i - 1) * dragSegment.determineAX()
                    + (double) (i + 1) * dragSegment.determineBX()) / ((double) d.getFoldLineDividingNumber());
            double by = ((double) (d.getFoldLineDividingNumber() - i - 1) * dragSegment.determineAY()
                    + (double) (i + 1) * dragSegment.determineBY()) / ((double) d.getFoldLineDividingNumber());
            LineSegment s_ad = new LineSegment(ax, ay, bx, by).withColor(d.getLineColor());
            d.addLineSegment(s_ad);
        }
        d.record();
        reset();
        return LineSegmentDivisionStep.CLICK_DRAG_POINT;
    }
}
