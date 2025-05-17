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

enum ParallelDrawWidthStep {
    SELECT_SEGMENT,
    CLICK_DRAG_POINT,
    SELECT_INDICATOR
}

@ApplicationScoped
@Handles(MouseMode.PARALLEL_DRAW_WIDTH_51)
public class MouseHandlerParallelDrawWidth extends StepMouseHandler<ParallelDrawWidthStep> {
    private LineSegment selectSegment;
    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment dragSegment;
    private List<LineSegment> indicatorList = Arrays.asList(null, null);
    private LineSegment selectIndicatorSegment;

    @Inject
    public MouseHandlerParallelDrawWidth() {
        super(ParallelDrawWidthStep.SELECT_SEGMENT);
        steps.addNode(StepNode.createNode_MD_R(ParallelDrawWidthStep.SELECT_SEGMENT, this::move_drag_select_segment, this::release_select_segment));
        steps.addNode(StepNode.createNode(ParallelDrawWidthStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {}, this::drag_click_drag_point, this::release_click_drag_point));
        steps.addNode(StepNode.createNode_MD_R(ParallelDrawWidthStep.SELECT_INDICATOR, this::move_drag_select_indicator, this::release_select_indicator));
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
    }

    @Override
    public void reset() {
        selectSegment = null;
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        indicatorList = Arrays.asList(null, null);
        selectIndicatorSegment = null;
        steps.setCurrentStep(ParallelDrawWidthStep.SELECT_SEGMENT);
    }

    // Select segment
    private void move_drag_select_segment(Point p) {
        if (OritaCalc.determineLineSegmentDistance(p, d.getClosestLineSegment(p)) < d.getSelectionDistance()) {
            selectSegment = d.getClosestLineSegment(p).withColor(LineColor.GREEN_6);
        } else selectSegment = null;
    }
    private ParallelDrawWidthStep release_select_segment(Point p) {
        if (selectSegment == null) return ParallelDrawWidthStep.SELECT_SEGMENT;
        return ParallelDrawWidthStep.CLICK_DRAG_POINT;
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        } else anchorPoint = null;
    }
    private void drag_click_drag_point(Point p) {
        if(anchorPoint == null) return;
        releasePoint = p;
        if(p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            releasePoint = d.getClosestPoint(p);
        }
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(LineColor.CYAN_3);
        indicatorList.set(0, OritaCalc.moveParallel(selectSegment, dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
        indicatorList.set(1, OritaCalc.moveParallel(selectSegment, -dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
    }
    private ParallelDrawWidthStep release_click_drag_point(Point p) {
        if(anchorPoint == null || releasePoint == null) return ParallelDrawWidthStep.CLICK_DRAG_POINT;
        if(releasePoint.distance(d.getClosestPoint(releasePoint)) > d.getSelectionDistance()) return ParallelDrawWidthStep.CLICK_DRAG_POINT;
        dragSegment = dragSegment.withB(releasePoint);
        indicatorList.set(0, OritaCalc.moveParallel(selectSegment, dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
        indicatorList.set(1, OritaCalc.moveParallel(selectSegment, -dragSegment.determineLength()).withColor(LineColor.PURPLE_8));
        return ParallelDrawWidthStep.SELECT_INDICATOR;
    }

    // Select indicator
    private void move_drag_select_indicator(Point p) {
        double indicator1Distance = OritaCalc.determineLineSegmentDistance(p, indicatorList.get(0));
        double indicator2Distance = OritaCalc.determineLineSegmentDistance(p, indicatorList.get(1));

        if (indicator1Distance < indicator2Distance && indicator1Distance < d.getSelectionDistance()) {
            selectIndicatorSegment = indicatorList.get(0).withColor(LineColor.ORANGE_4);
        } else if (indicator2Distance < indicator1Distance && indicator2Distance < d.getSelectionDistance()) {
            selectIndicatorSegment = indicatorList.get(1).withColor(LineColor.ORANGE_4);
        } else selectIndicatorSegment = null;
    }
    private ParallelDrawWidthStep release_select_indicator(Point p) {
        if (selectIndicatorSegment == null) return ParallelDrawWidthStep.SELECT_INDICATOR;
        d.addLineSegment(selectIndicatorSegment.withColor(d.getLineColor()));
        d.record();
        reset();
        return ParallelDrawWidthStep.SELECT_SEGMENT;
    }
}
