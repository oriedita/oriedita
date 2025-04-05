package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_48)
public class MouseHandlerCircleDrawConcentric extends BaseMouseHandler {
    private Point p = new Point();
    private StepGraph<Step> steps = new StepGraph<>(Step.SELECT_CIRCLE, this::action_select_circle);

    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment radiusDifference;
    private Circle originalCircle;
    private Circle newCircle;

    private enum Step {
        SELECT_CIRCLE,
        CLICK_DRAG_POINT,
        RELEASE_POINT
    }

    @Inject
    public MouseHandlerCircleDrawConcentric() { initializeSteps(); }

    @Override
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    public void mouseReleased(Point p0) {
        if (steps.getCurrentStep() == Step.CLICK_DRAG_POINT) return;
        steps.runCurrentAction();
    }

    private void highlightSelection(Point p0) {
        p = p0 != null ? d.getCamera().TV2object(p0) : p;
        switch (steps.getCurrentStep()) {
            case SELECT_CIRCLE: {
                if (OritaCalc.distance_circumference(p, d.getClosestCircleMidpoint(p)) < d.getSelectionDistance()) {
                    originalCircle = new Circle(d.getClosestCircleMidpoint(p));
                    originalCircle.setColor(LineColor.GREEN_6);
                } else originalCircle = null;
                return;
            }
            case CLICK_DRAG_POINT: {
                anchorPoint = p;
                if (anchorPoint.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    anchorPoint = d.getClosestPoint(p);
                } else anchorPoint = null;
                return;
            }
            case RELEASE_POINT: {
                releasePoint = p;

                if (anchorPoint.equals(releasePoint)) {
                    newCircle = null;
                    radiusDifference = null;
                    return;
                }

                radiusDifference = new LineSegment(anchorPoint, releasePoint, LineColor.CYAN_3);
                newCircle = new Circle(originalCircle.determineCenter(),
                        originalCircle.getR() + radiusDifference.determineLength(), LineColor.CYAN_3);
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawCircleStep(g2, originalCircle, camera);
        DrawingUtil.drawCircleStep(g2, newCircle, camera);
        DrawingUtil.drawLineStep(g2, radiusDifference, camera, settings.getLineWidth(), d.getGridInputAssist());

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        originalCircle = null;
        newCircle = null;
        radiusDifference = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.SELECT_CIRCLE, this::action_select_circle);
        steps.addNode(Step.CLICK_DRAG_POINT, this::action_click_drag_point);
        steps.addNode(Step.RELEASE_POINT, this::action_release_point);

        steps.connectNodes(Step.SELECT_CIRCLE, Step.CLICK_DRAG_POINT);
        steps.connectNodes(Step.CLICK_DRAG_POINT, Step.RELEASE_POINT);
        steps.connectNodes(Step.RELEASE_POINT, Step.CLICK_DRAG_POINT);
    }

    private Step action_select_circle() {
        if (originalCircle == null) return null;
        return Step.CLICK_DRAG_POINT;
    }

    private Step action_click_drag_point() {
        if (anchorPoint == null) return null;
        return Step.RELEASE_POINT;
    }

    private Step action_release_point() {
        if (releasePoint == null
                || releasePoint.distance(d.getClosestPoint(p)) > d.getSelectionDistance()) {
            anchorPoint = null;
            releasePoint = null;
            radiusDifference = null;
            newCircle = null;
            return Step.CLICK_DRAG_POINT;
        }

        releasePoint = d.getClosestPoint(p);
        radiusDifference = new LineSegment(anchorPoint, releasePoint);
        newCircle = new Circle(originalCircle.determineCenter(),
                originalCircle.getR() + radiusDifference.determineLength(), LineColor.CYAN_3);
        d.addCircle(newCircle);
        d.record();
        reset();
        return null;
    }
}


