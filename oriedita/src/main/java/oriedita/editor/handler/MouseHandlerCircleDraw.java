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
@Handles(MouseMode.CIRCLE_DRAW_42)
public class MouseHandlerCircleDraw extends BaseMouseHandler {
    private Point p = new Point();
    private StepGraph<Step> steps;

    private Point anchorPoint;
    private Point releasePoint;
    private Circle previewCircle;
    private LineSegment previewRadiusSegment;

    private enum Step {
        CLICK_DRAG_POINT,
        RELEASE_POINT
    }

    @Inject
    public MouseHandlerCircleDraw() { initializeSteps(); }

    @Override
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    //マウス操作(mouseMode==42 円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    //マウス操作(mouseMode==42 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //マウス操作(mouseMode==42 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (steps.getCurrentStep() == Step.CLICK_DRAG_POINT) return;
        steps.runCurrentAction();
    }

    private void highlightSelection(Point p0) {
        p = p0 != null ? d.getCamera().TV2object(p0) : p;
        switch (steps.getCurrentStep()) {
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
                    previewCircle = null;
                    previewRadiusSegment = null;
                    return;
                }

                    previewCircle = new Circle(anchorPoint, OritaCalc.distance(anchorPoint, releasePoint), LineColor.CYAN_3);
                    previewRadiusSegment = new LineSegment(anchorPoint, releasePoint, LineColor.CYAN_3);
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawCircleStep(g2, previewCircle, camera);
        DrawingUtil.drawLineStep(g2, previewRadiusSegment, camera, settings.getLineWidth(), d.getGridInputAssist());

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        previewCircle = null;
        previewRadiusSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.CLICK_DRAG_POINT, this::action_click_drag_point);
        steps.addNode(Step.RELEASE_POINT, this::action_release_point);

        steps.connectNodes(Step.CLICK_DRAG_POINT, Step.RELEASE_POINT);
    }

    private Step action_click_drag_point() {
        if (anchorPoint == null) return null;
        return Step.RELEASE_POINT;
    }

    private Step action_release_point() {
        if (releasePoint == null
                || releasePoint.distance(d.getClosestPoint(p)) > d.getSelectionDistance()) {
            reset();
            return Step.CLICK_DRAG_POINT;
        }
        releasePoint = d.getClosestPoint(p);
        previewCircle = new Circle(anchorPoint, OritaCalc.distance(anchorPoint, releasePoint), LineColor.CYAN_3);
        d.addCircle(previewCircle.getX(), previewCircle.getY(), previewCircle.getR(), LineColor.CYAN_3);
        d.record();
        reset();
        return null;
    }
}
