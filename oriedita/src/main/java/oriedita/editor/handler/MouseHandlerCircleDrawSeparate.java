package oriedita.editor.handler;

import java.awt.Graphics2D;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

enum CircleDrawSeparateStep {
    SELECT_POINT,
    CLICK_DRAG_POINT,
}

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_SEPARATE_44)
public class MouseHandlerCircleDrawSeparate extends StepMouseHandler<CircleDrawSeparateStep> {
    private Point centerPoint, anchorPoint, releasePoint;
    private LineSegment previewSegment;
    private Circle previewCircle;

    @Inject
    public MouseHandlerCircleDrawSeparate() {
        super(CircleDrawSeparateStep.SELECT_POINT);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawSeparateStep.SELECT_POINT,
                this::move_drag_select_point, this::release_select_point));
        steps.addNode(ObjCoordStepNode.createNode(CircleDrawSeparateStep.CLICK_DRAG_POINT, this::move_click_drag_point,
                (p) -> {
                }, this::drag_click_drag_point, this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, centerPoint, LineColor.CYAN_3, camera);
        DrawingUtil.drawStepVertex(g2, anchorPoint, LineColor.CYAN_3, camera);
        DrawingUtil.drawStepVertex(g2, releasePoint, LineColor.CYAN_3, camera);
        DrawingUtil.drawLineStep(g2, previewSegment, camera, settings.getLineWidth());
        DrawingUtil.drawCircleStep(g2, previewCircle, camera);
    }

    @Override
    public void reset() {
        resetStep();
        centerPoint = null;
        anchorPoint = null;
        releasePoint = null;
        previewSegment = null;
        previewCircle = null;
    }

    // Select point for new circle center
    private void move_drag_select_point(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            centerPoint = new Point(tmpPoint);
        } else
            centerPoint = null;
    }

    private CircleDrawSeparateStep release_select_point(Point p) {
        if (centerPoint == null)
            return CircleDrawSeparateStep.SELECT_POINT;
        return CircleDrawSeparateStep.CLICK_DRAG_POINT;
    }

    // Click drag point to draw the offset line
    private void move_click_drag_point(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            anchorPoint = new Point(tmpPoint);
        } else
            anchorPoint = null;
    }

    private void drag_click_drag_point(Point p) {
        if (anchorPoint == null)
            return;

        Point tmpPoint = d.getClosestPoint(p);
        releasePoint = p;
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            releasePoint = new Point(tmpPoint);
        }

        if (releasePoint == null)
            return;

        previewSegment = new LineSegment(anchorPoint, releasePoint, LineColor.CYAN_3);
        previewCircle = new Circle(centerPoint, anchorPoint.distance(releasePoint), LineColor.CYAN_3);
    }

    private CircleDrawSeparateStep release_click_drag_point(Point p) {
        if (anchorPoint == null) {
            return CircleDrawSeparateStep.CLICK_DRAG_POINT;
        }
        if (releasePoint == null || releasePoint.distance(d.getClosestPoint(p)) > d.getSelectionDistance()) {
            anchorPoint = null;
            releasePoint = null;
            previewSegment = null;
            previewCircle = null;
            return CircleDrawSeparateStep.CLICK_DRAG_POINT;
        }

        d.addCircle(previewCircle);
        d.record();
        reset();
        return CircleDrawSeparateStep.SELECT_POINT;
    }
}
