package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Handles(MouseMode.PARALLEL_DRAW_WIDTH_51)
public class MouseHandlerParallelDrawWidth extends BaseMouseHandler {

    private Point p = new Point();
    private StepGraph<Step> steps = new StepGraph<>(Step.SELECT_SEGMENT, this::action_select_segment);

    private LineSegment selectSegment;
    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment dragSegment;
    private List<LineSegment> indicatorList = Arrays.asList(null, null);
    private LineSegment selectIndicatorSegment;

    private enum Step {
        SELECT_SEGMENT,
        CLICK_DRAG_POINT,
        RELEASE_POINT,
        SELECT_INDICATOR
    }

    @Inject
    public MouseHandlerParallelDrawWidth() { initializeSteps(); }

    @Override
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) { highlightSelection(p0); }

    //マウス操作(mouseMode==51 平行線　幅指定入力モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (steps.getCurrentStep() == Step.CLICK_DRAG_POINT) return;
        steps.runCurrentAction();
    }

    private void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);
        switch (steps.getCurrentStep()) {
            case SELECT_SEGMENT: {
                if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
                    selectSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
                } else selectSegment = null;
                return;
            }
            case CLICK_DRAG_POINT: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    anchorPoint = d.getClosestPoint(p);
                } else anchorPoint = null;
                return;
            }
            case RELEASE_POINT: {
                releasePoint = p;
                dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(LineColor.CYAN_3);
                indicatorList.set(0, OritaCalc.moveParallel(selectSegment, dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
                indicatorList.set(1, OritaCalc.moveParallel(selectSegment, -dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
                return;
            }
            case SELECT_INDICATOR: {
                double indicator1Distance = OritaCalc.determineLineSegmentDistance(p, indicatorList.get(0));
                double indicator2Distance = OritaCalc.determineLineSegmentDistance(p, indicatorList.get(1));

                if (indicator1Distance < indicator2Distance && indicator1Distance < d.getSelectionDistance()) {
                    selectIndicatorSegment = indicatorList.get(0).withColor(LineColor.ORANGE_4);
                } else if (indicator2Distance < indicator1Distance && indicator2Distance < d.getSelectionDistance()) {
                    selectIndicatorSegment = indicatorList.get(1).withColor(LineColor.ORANGE_4);
                } else selectIndicatorSegment = null;
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawLineStep(g2, selectSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, anchorPoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, LineColor.CYAN_3, camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicatorList.get(0), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, indicatorList.get(1), camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2,selectIndicatorSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        selectSegment = null;
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        indicatorList = Arrays.asList(null, null);
        selectIndicatorSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.SELECT_SEGMENT, this::action_select_segment);
        steps.addNode(Step.CLICK_DRAG_POINT, this::action_click_drag_point);
        steps.addNode(Step.RELEASE_POINT, this::action_release_point);
        steps.addNode(Step.SELECT_INDICATOR, this::action_select_indicator);

        steps.connectNodes(Step.SELECT_SEGMENT, Step.CLICK_DRAG_POINT);
        steps.connectNodes(Step.CLICK_DRAG_POINT, Step.RELEASE_POINT);
        steps.connectNodes(Step.RELEASE_POINT, Step.SELECT_INDICATOR);
    }

    private Step action_select_segment() {
        if (selectSegment == null) return null;
        return Step.CLICK_DRAG_POINT;
    }

    private Step action_click_drag_point() {
        return Step.RELEASE_POINT;
    }

    private Step action_release_point() {
        if (releasePoint == null) return null;
        releasePoint = d.getClosestPoint(releasePoint);
        dragSegment = dragSegment.withB(releasePoint);
        indicatorList.set(0, OritaCalc.moveParallel(selectSegment, dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
        indicatorList.set(1, OritaCalc.moveParallel(selectSegment, -dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
        return Step.SELECT_INDICATOR;
    }

    private Step action_select_indicator() {
        if (selectIndicatorSegment == null) return null;
        d.addLineSegment(selectIndicatorSegment.withColor(d.getLineColor()));
        d.record();
        reset();
        return null;
    }
}
