package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;



@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_48)
public class MouseHandlerCircleDrawConcentric extends StepMouseHandler<MouseHandlerCircleDrawConcentric.Step> {
    public enum Step {
        SELECT_CIRCLE,
        CLICK_DRAG_POINT,
    }
    private Point anchorPoint, releasePoint;
    private LineSegment radiusDifference;
    private Circle originalCircle, newCircle;

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var g = new StepGraph<>(Step.SELECT_CIRCLE);
        g.addNode(stepFactory.createNode_MD_R(Step.SELECT_CIRCLE, this::move_drag_select_circle,
                this::release_select_circle));
        g.addNode(stepFactory.createNode(Step.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {
        }, this::drag_click_drag_point, this::release_click_drag_point));
        return g;
    }


    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, LineColor.CYAN_3, camera);
        DrawingUtil.drawStepVertex(g2, releasePoint, LineColor.CYAN_3, camera);
        DrawingUtil.drawCircleStep(g2, originalCircle, camera);
        DrawingUtil.drawCircleStep(g2, newCircle, camera);
        DrawingUtil.drawLineStep(g2, radiusDifference, camera, settings.getLineWidth());
    }

    @Override
    public void reset() {
        resetStep();
        anchorPoint = null;
        releasePoint = null;
        originalCircle = null;
        newCircle = null;
        radiusDifference = null;
    }

    // Select circle
    private void move_drag_select_circle(Point p) {
        if (OritaCalc.distance_circumference(p, d.getClosestCircleMidpoint(p)) < d.getSelectionDistance()) {
            originalCircle = new Circle(d.getClosestCircleMidpoint(p));
            originalCircle.setColor(LineColor.GREEN_6);
        } else
            originalCircle = null;
    }

    private Step release_select_circle(Point p) {
        if (originalCircle == null)
            return Step.SELECT_CIRCLE;
        return Step.CLICK_DRAG_POINT;
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        anchorPoint = p;
        if (anchorPoint.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        } else
            anchorPoint = null;
    }

    private void drag_click_drag_point(Point p) {
        if (anchorPoint == null)
            return;
        releasePoint = p;
        if (releasePoint.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }

        if (anchorPoint.equals(releasePoint)) {
            newCircle = null;
            radiusDifference = null;
            return;
        }

        radiusDifference = new LineSegment(anchorPoint, releasePoint, LineColor.CYAN_3);
        newCircle = new Circle(originalCircle.determineCenter(),
                originalCircle.getR() + radiusDifference.determineLength(), LineColor.CYAN_3);
    }

    private Step release_click_drag_point(Point p) {
        if (anchorPoint == null)
            return Step.CLICK_DRAG_POINT;
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
        return Step.SELECT_CIRCLE;
    }
}
