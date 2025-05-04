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

@ApplicationScoped
@Handles(MouseMode.CREASE_DELETE_OVERLAPPING_64)
public class MouseHandlerCreaseDeleteOverlapping extends BaseMouseHandlerInputRestricted {

    private Point p = new Point();
    private StepGraph<Step> steps;

    private Point anchorPoint, releasePoint;
    private LineSegment dragSegment;

    private enum Step { CLICK_DRAG_POINT }

    @Inject
    public MouseHandlerCreaseDeleteOverlapping() { initializeSteps(); }

    @Override
    public void mouseMoved(Point p0) {
        p = d.getCamera().TV2object(p0);
        steps.runCurrentMoveAction();
    }

    public void mousePressed(Point p0) {}

    public void mouseDragged(Point p0) {
        p = d.getCamera().TV2object(p0);
        steps.runCurrentDragAction();
    }

    public void mouseReleased(Point p0) { steps.runCurrentReleaseAction(); }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.CLICK_DRAG_POINT, this::move_click_drag_point, () -> {}, this::drag_click_drag_point, this::release_click_drag_point);
    }

    // Click drag point
    private void move_click_drag_point() {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        } else anchorPoint = null;
    }
    private void drag_click_drag_point() {
        if(anchorPoint == null) return;
        releasePoint = p;
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
    }
    private Step release_click_drag_point() {
        if (anchorPoint == null) return Step.CLICK_DRAG_POINT;
        if (releasePoint.distance(d.getClosestPoint(releasePoint)) > d.getSelectionDistance()) {
            reset();
            return Step.CLICK_DRAG_POINT;
        }
        if (Epsilon.high.gt0(dragSegment.determineLength())) {
            d.getFoldLineSet().deleteInsideLine(dragSegment, "l");//lは小文字のエル
            d.record();
        }
        reset();
        return Step.CLICK_DRAG_POINT;
    }
}
