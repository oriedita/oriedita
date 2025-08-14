package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.awt.Graphics2D;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

enum DrawCreaseSymmetricStep {
    SELECT_2P_OR_SEGMENT,
    SELECT_2P,
}

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_SYMMETRIC_12)
public class MouseHandlerDrawCreaseSymmetric extends StepMouseHandler<DrawCreaseSymmetricStep> {

    Point point1, point2;
    LineSegment segment;

    @Inject
    public MouseHandlerDrawCreaseSymmetric() {
        super(DrawCreaseSymmetricStep.SELECT_2P_OR_SEGMENT);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(DrawCreaseSymmetricStep.SELECT_2P_OR_SEGMENT,
                this::move_drag_select_2p_or_line, this::release_select_2p_or_line));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(DrawCreaseSymmetricStep.SELECT_2P,
                this::move_drag_select_2p, this::release_select_2p));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, point1, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, point2, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, segment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        point1 = null;
        point2 = null;
        segment = null;
    }

    // Select 2 points of a line or line segment itself
    private void move_drag_select_2p_or_line(Point p) {
        LineSegment tmpSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, tmpSegment) < d.getSelectionDistance()) {
            segment = new LineSegment(tmpSegment, LineColor.GREEN_6);
        } else
            segment = null;

        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            segment = null;
            point1 = new Point(tmpPoint);
        } else
            point1 = null;
    }

    private DrawCreaseSymmetricStep release_select_2p_or_line(Point p) {
        if (point1 == null && segment == null)
            return DrawCreaseSymmetricStep.SELECT_2P_OR_SEGMENT;
        if (point1 != null)
            return DrawCreaseSymmetricStep.SELECT_2P;

        mirrorSelections(segment);
        reset();
        return DrawCreaseSymmetricStep.SELECT_2P_OR_SEGMENT;
    }

    // Select another point
    private void move_drag_select_2p(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            segment = null;
            point2 = new Point(tmpPoint);
        } else
            point2 = null;
    }

    private DrawCreaseSymmetricStep release_select_2p(Point p) {
        if (point2 == null)
            return DrawCreaseSymmetricStep.SELECT_2P;
        mirrorSelections(new LineSegment(point1, point2));
        reset();
        return DrawCreaseSymmetricStep.SELECT_2P_OR_SEGMENT;
    }

    private void mirrorSelections(LineSegment segment) {
        int old_sousuu = d.getFoldLineSet().getTotal();

        for (var s : d.getFoldLineSet().getLineSegmentsCollection()) {
            if (s.getSelected() == 2) {
                LineSegment adds = OritaCalc
                        .findLineSymmetryLineSegment(s, segment)
                        .withColor(s.getColor());
                d.getFoldLineSet().addLine(adds);
            }
        }

        int new_sousuu = d.getFoldLineSet().getTotal();

        d.getFoldLineSet().divideLineSegmentWithNewLines(old_sousuu, new_sousuu);

        d.record();
        d.unselect_all(false);
    }
}
