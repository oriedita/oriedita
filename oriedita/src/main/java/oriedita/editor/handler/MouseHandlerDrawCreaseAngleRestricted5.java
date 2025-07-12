package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.tools.SnappingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.EnumSet;

enum DrawDeg5ActionStep { CLICK_DRAG_POINT }

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37)
public class MouseHandlerDrawCreaseAngleRestricted5 extends StepMouseHandler<DrawDeg5ActionStep> {
    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.ANGLE_SYSTEM);
    }

    @Inject
    private AngleSystemModel angleSystemModel;
    @Inject
    private CanvasModel canvasModel;

    private Point anchorPoint, releasePoint;
    private LineSegment dragSegment;

    @Inject
    public MouseHandlerDrawCreaseAngleRestricted5() {
        super(DrawDeg5ActionStep.CLICK_DRAG_POINT);
        steps.addNode(StepNode.createNode(DrawDeg5ActionStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {}, this::drag_click_drag_point, this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        move_click_drag_point(canvasModel.getMouseObjPosition());
        steps.setCurrentStep(DrawDeg5ActionStep.CLICK_DRAG_POINT);
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        anchorPoint = p;
        if(p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            anchorPoint = d.getClosestPoint(p);
        }
    }
    private void drag_click_drag_point(Point p) {
        if (anchorPoint == null) return;
        releasePoint = kouho_point_A_37(syuusei_point_A_37(p));
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
    }
    private DrawDeg5ActionStep release_click_drag_point(Point p) {
        if (anchorPoint == null
                || dragSegment == null
                || !Epsilon.high.gt0(dragSegment.determineLength())) {
            return DrawDeg5ActionStep.CLICK_DRAG_POINT;
        }
        d.addLineSegment(dragSegment);
        d.record();
        reset();
        return DrawDeg5ActionStep.CLICK_DRAG_POINT;
    }

    public Point syuusei_point_A_37(Point p) {
        return SnappingUtil.snapToActiveAngleSystem(d, anchorPoint, p, angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles());
    }

    // ---
    public Point kouho_point_A_37(Point syuusei_point) {
        Point closestPoint = d.getClosestPoint(syuusei_point);
        double zure_kakudo = OritaCalc.angle(anchorPoint, syuusei_point, anchorPoint, closestPoint);
        boolean zure_flg = (Epsilon.UNKNOWN_1EN5 < zure_kakudo) && (zure_kakudo <= 360.0 - Epsilon.UNKNOWN_1EN5);
        if (zure_flg || (syuusei_point.distance(closestPoint) > d.getSelectionDistance())) {
            return syuusei_point;
        } else {//最寄点が角度系にのっていて、修正点とも近い場合
            return closestPoint;
        }
    }
}
