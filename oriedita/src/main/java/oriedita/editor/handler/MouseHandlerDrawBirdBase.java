package oriedita.editor.handler;

import java.awt.Graphics2D;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.element.Point;

enum DrawBirbBaseStep {
    SELECT_P1,
    SELECT_P2
}

@ApplicationScoped
@Handles(MouseMode.DRAW_BLINTZ)
public class MouseHandlerDrawBirdBase extends StepMouseHandler<DrawBirbBaseStep> {
    private Point p1, p2;

    @Inject
    public MouseHandlerDrawBirdBase() {
        super(DrawBirbBaseStep.SELECT_P1);
        steps.addNode(StepNode.createNode_MD_R(DrawBirbBaseStep.SELECT_P1, this::move_drag_select_p1,
                this::release_select_p1));
        steps.addNode(StepNode.createNode_MD_R(DrawBirbBaseStep.SELECT_P2, this::move_drag_select_p2,
                this::release_select_p2));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, d.getLineColor(), camera, d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        p1 = null;
        p2 = null;
    }

    // Select point 1
    private void move_drag_select_p1(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        p1 = p;
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            p1 = tmpPoint;
        }
    }

    private DrawBirbBaseStep release_select_p1(Point p) {
        if (p.distance(d.getClosestPoint(p)) > d.getSelectionDistance())
            return DrawBirbBaseStep.SELECT_P1;
        return DrawBirbBaseStep.SELECT_P2;
    }

    // Select point 1
    private void move_drag_select_p2(Point p) {
        Point tmpPoint = d.getClosestPoint(p);
        p2 = p;
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            p2 = tmpPoint;
        }
    }

    private DrawBirbBaseStep release_select_p2(Point p) {
        if (p.distance(d.getClosestPoint(p)) > d.getSelectionDistance())
            return DrawBirbBaseStep.SELECT_P2;
        reset();
        return DrawBirbBaseStep.SELECT_P1;
    }
}
