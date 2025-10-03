package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum DeletePointStep {
    SELECT_POINT
}

@ApplicationScoped
@Handles(MouseMode.DELETE_POINT_15)
public class MouseHandlerDeletePoint extends StepMouseHandler<DeletePointStep> {
    private Point targetPoint;

    @Inject
    public MouseHandlerDeletePoint() {
        super(DeletePointStep.SELECT_POINT);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(DeletePointStep.SELECT_POINT, this::move_drag_select_point,
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

    private void move_drag_select_point(Point p) {
        targetPoint = p;
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            targetPoint = d.getClosestPoint(p);
        }
    }

    private DeletePointStep release_click_drag_point(Point p) {
        if (d.getFoldLineSet().del_V(p, d.getSelectionDistance(), Epsilon.UNKNOWN_1EN6)) {
            d.record();
        }
        reset();
        return DeletePointStep.SELECT_POINT;
    }
}
