package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum PolygonSetNoCorners {
    SELECT_POINT_1,
    SELECT_POINT_2
}

@ApplicationScoped
@Handles(MouseMode.POLYGON_SET_NO_CORNERS_29)
public class MouseHandlerPolygonSetNoCorners extends StepMouseHandler<PolygonSetNoCorners> {
    private Point p1, p2;

    @Inject
    public MouseHandlerPolygonSetNoCorners() {
        super(PolygonSetNoCorners.SELECT_POINT_1);
        steps.addNode(StepNode.createNode_MD_R(PolygonSetNoCorners.SELECT_POINT_1, this::move_drag_select_point_1, this::release_select_point_1));
        steps.addNode(StepNode.createNode_MD_R(PolygonSetNoCorners.SELECT_POINT_2, this::move_drag_select_point_2, this::release_select_point_2));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, d.getLineColor(), camera, d.getGridInputAssist());
    }

    @Override
    public void reset() {
        p1 = null;
        p2 = null;
        steps.setCurrentStep(PolygonSetNoCorners.SELECT_POINT_1);
    }

    // Select point 1
    private void move_drag_select_point_1() {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            p1 = d.getClosestPoint(p);
        } else p1 = null;
    }
    private PolygonSetNoCorners release_select_point_1() {
        if (p1 == null) return PolygonSetNoCorners.SELECT_POINT_1;
        return PolygonSetNoCorners.SELECT_POINT_2;
    }

    // Select point 2
    private void move_drag_select_point_2() {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                && !p1.equals(d.getClosestPoint(p))) {
            p2 = d.getClosestPoint(p);
        } else p2 = null;
    }
    private PolygonSetNoCorners release_select_point_2() {
        if (p2 == null) return PolygonSetNoCorners.SELECT_POINT_2;
        LineSegment s_tane = new LineSegment(p1, p2, d.getLineColor());
        d.addLineSegment(s_tane);
        for (int i = 2; i <= d.getNumPolygonCorners(); i++) {
            LineSegment s_deki = OritaCalc.lineSegment_rotate(s_tane, (double) (d.getNumPolygonCorners() - 2) * 180.0 / (double) d.getNumPolygonCorners());
            s_tane = new LineSegment(s_deki.getB(), s_deki.getA(), d.getLineColor());
            d.addLineSegment(s_tane);
        }
        d.record();
        reset();
        return PolygonSetNoCorners.SELECT_POINT_1;
    }
}
