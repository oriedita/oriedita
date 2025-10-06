package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum DrawPointStep {
    SELECT_POINT
}

@ApplicationScoped
@Handles(MouseMode.DRAW_POINT_14)
public class MouseHandlerDrawPoint extends StepMouseHandler<DrawPointStep> {
    private Point targetPoint;

    @Inject
    public MouseHandlerDrawPoint() {
        super(DrawPointStep.SELECT_POINT);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(DrawPointStep.SELECT_POINT, this::move_drag_select_point,
                this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera);
    }

    @Override
    public void reset() {
        resetStep();
        targetPoint = null;
    }

    // Click drag point
    private void move_drag_select_point(Point p) {
        targetPoint = p;
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            targetPoint = d.getClosestPoint(p);
        }
    }

    private DrawPointStep release_click_drag_point(Point p) {
        LineSegment mts = new LineSegment(d.getFoldLineSet().closestLineSegmentSearch(targetPoint));
        if (OritaCalc.determineLineSegmentDistance(targetPoint, mts) > d.getSelectionDistance()) {
            reset();
            return DrawPointStep.SELECT_POINT;
        }

        Point pk = OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(mts), targetPoint);
        if (OritaCalc.isInside(mts.getA(), pk, mts.getB()) == 2) {
            d.getFoldLineSet().applyLineSegmentDivide(mts, pk);
            d.record();
        }

        reset();
        return DrawPointStep.SELECT_POINT;
    }
}
