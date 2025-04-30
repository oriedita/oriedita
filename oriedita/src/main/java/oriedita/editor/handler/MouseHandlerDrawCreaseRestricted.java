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
@Handles(MouseMode.DRAW_CREASE_RESTRICTED_11)
public class MouseHandlerDrawCreaseRestricted extends BaseMouseHandlerInputRestricted {
    private Point p = new Point();
    private StepCollection<Step> steps;

    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment dragSegment;

    private enum Step {
        CLICK_DRAG_POINT,
        RELEASE_POINT,
    }
    @Inject
    public MouseHandlerDrawCreaseRestricted() { initializeSteps(); }

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }//近い既存点のみ表示
    @Override
    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseReleased(Point p0) { steps.runCurrentAction(); }

    private void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);

        switch (steps.getCurrentStep()) {
            case CLICK_DRAG_POINT: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    anchorPoint = d.getClosestPoint(p);
                } else anchorPoint = null;
                return;
            }
            case RELEASE_POINT: {
                releasePoint = p;
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    releasePoint = d.getClosestPoint(p);
                }
                dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
            }
        }
    }

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
        steps = new StepCollection<>(Step.CLICK_DRAG_POINT, this::action_click_drag_point);
        steps.addNode(Step.RELEASE_POINT, this::action_release_point);
    }

    private void action_click_drag_point() {
        if (anchorPoint == null) return;
        steps.setCurrentStep(Step.RELEASE_POINT);
    }

    private void action_release_point() {
        if (releasePoint == null
                || p.distance(d.getClosestPoint(p)) > d.getSelectionDistance()
                || !Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
        }
        d.addLineSegment(dragSegment);
        d.record();
        reset();
        steps.setCurrentStep(Step.CLICK_DRAG_POINT);
    }
}
