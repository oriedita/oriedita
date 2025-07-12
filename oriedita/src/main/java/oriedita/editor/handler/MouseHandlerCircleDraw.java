package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum CircleDrawStep { CLICK_DRAG_POINT }

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_42)
public class MouseHandlerCircleDraw extends StepMouseHandler<CircleDrawStep> {
    private Point anchorPoint, releasePoint;
    private Circle previewCircle;
    private LineSegment previewRadiusSegment;

    @Inject
    private CanvasModel canvasModel;

    @Inject
    public MouseHandlerCircleDraw() {
        super(CircleDrawStep.CLICK_DRAG_POINT);
        steps.addNode(StepNode.createNode(CircleDrawStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {}, this::drag_click_drag_point, this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawCircleStep(g2, previewCircle, camera);
        DrawingUtil.drawLineStep(g2, previewRadiusSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        previewCircle = null;
        previewRadiusSegment = null;
        move_click_drag_point(canvasModel.getMouseObjPosition());
        steps.setCurrentStep(CircleDrawStep.CLICK_DRAG_POINT);
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        anchorPoint = p;
        if (anchorPoint.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        } else anchorPoint = null;
    }
    private void drag_click_drag_point(Point p) {
        if(anchorPoint == null) return;

        releasePoint = p;
        if (releasePoint.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }

        if (anchorPoint.equals(releasePoint)) {
            previewCircle = null;
            previewRadiusSegment = null;
            return;
        }

        previewCircle = new Circle(anchorPoint, OritaCalc.distance(anchorPoint, releasePoint), LineColor.CYAN_3);
        previewRadiusSegment = new LineSegment(anchorPoint, releasePoint, LineColor.CYAN_3);
    }
    private CircleDrawStep release_click_drag_point(Point p) {
        if (anchorPoint == null
                || (releasePoint == null
                || releasePoint.distance(d.getClosestPoint(p)) > d.getSelectionDistance())) {
            reset();
            return CircleDrawStep.CLICK_DRAG_POINT;
        }
        releasePoint = d.getClosestPoint(p);
        previewCircle = new Circle(anchorPoint, OritaCalc.distance(anchorPoint, releasePoint), LineColor.CYAN_3);
        d.addCircle(previewCircle.getX(), previewCircle.getY(), previewCircle.getR(), LineColor.CYAN_3);
        d.record();
        reset();
        return CircleDrawStep.CLICK_DRAG_POINT;
    }
}
