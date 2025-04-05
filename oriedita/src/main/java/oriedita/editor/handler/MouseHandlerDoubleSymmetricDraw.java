package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

@ApplicationScoped
@Handles(MouseMode.DOUBLE_SYMMETRIC_DRAW_35)
public class MouseHandlerDoubleSymmetricDraw extends BaseMouseHandlerInputRestricted {
    private Point p = new Point();
    private StepGraph<Step> steps = new StepGraph<>(Step.CLICK_DRAG_POINT, this::action_click_drag_point);

    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment dragSegment;

    private enum Step {
        CLICK_DRAG_POINT,
        RELEASE_POINT,
    }

    @Inject
    public MouseHandlerDoubleSymmetricDraw() { initializeSteps(); }

    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //マウス操作(mouseMode==35　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (steps.getCurrentStep() == Step.CLICK_DRAG_POINT) return;
        steps.runCurrentAction();
    }

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

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
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
        Point closestPoint = d.getClosestPoint(releasePoint);
        dragSegment = new LineSegment(anchorPoint, closestPoint);

        if(releasePoint.distance(p) > d.getSelectionDistance()) {
            reset();
            return null;
        }

        if (!Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
            return null;
        }

        boolean isChanged = false;
        for (var s : d.getFoldLineSet().getLineSegmentsCollection()) {
            LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersectionSweet(s, dragSegment, Epsilon.UNKNOWN_001, Epsilon.UNKNOWN_001);

            if (intersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25
                || intersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {
                Point t_moto = s.getA();
                if (OritaCalc.determineLineSegmentDistance(t_moto, dragSegment) < OritaCalc.determineLineSegmentDistance(s.getB(), dragSegment)) {
                    t_moto = s.getB();
                }

                //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
                Point t_taisyou = OritaCalc.findLineSymmetryPoint(dragSegment.getA(), dragSegment.getB(), t_moto);
                LineSegment add_sen = new LineSegment(OritaCalc.findIntersection(s, dragSegment), t_taisyou);
                add_sen = d.extendToIntersectionPoint(add_sen).withColor(s.getColor());

                if (Epsilon.high.gt0(add_sen.determineLength())) {
                    isChanged = true;
                    d.addLineSegment(add_sen);
                }
            }
        }

        if (isChanged) d.record();
        reset();
        return null;
    }
}
