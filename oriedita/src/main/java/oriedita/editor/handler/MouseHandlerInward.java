package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum InwardStep {
    POINT_1,
    POINT_2,
    POINT_3,
}

@ApplicationScoped
@Handles(MouseMode.INWARD_8)
public class MouseHandlerInward extends StepMouseHandler<InwardStep> {
    Point p1, p2, p3;

    @Inject
    public MouseHandlerInward() {
        super(InwardStep.POINT_1);
        steps.addNode(StepNode.createNode_MD_R(InwardStep.POINT_1, this::move_drag_select_point_1, this::release_select_point_1));
        steps.addNode(StepNode.createNode_MD_R(InwardStep.POINT_2, this::move_drag_select_point_2, this::release_select_point_2));
        steps.addNode(StepNode.createNode_MD_R(InwardStep.POINT_3, this::move_drag_select_point_3, this::release_select_point_3));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p3, d.getLineColor(), camera, d.getGridInputAssist());
    }

    @Override
    public void reset() {
        p1 = null;
        p2 = null;
        p3 = null;
        steps.setCurrentStep(InwardStep.POINT_1);
    }

    // Select point 1
    private void move_drag_select_point_1(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            p1 = d.getClosestPoint(p);
        } else p1 = null;
    }
    private InwardStep release_select_point_1(Point p) {
        if(p1 == null) return InwardStep.POINT_1;
        return InwardStep.POINT_2;
    }

    // Select point 2
    private void move_drag_select_point_2(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                && !p1.equals(d.getClosestPoint(p))) {
            p2 = d.getClosestPoint(p);
        } else p2 = null;
    }
    private InwardStep release_select_point_2(Point p) {
        if(p2 == null) return InwardStep.POINT_2;
        return InwardStep.POINT_3;
    }

    // Select point 3
    private void move_drag_select_point_3(Point p) {
        if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()
                && !p1.equals(d.getClosestPoint(p))
                && !p2.equals(d.getClosestPoint(p))) {
            p3 = d.getClosestPoint(p);
        } else p3 = null;
    }
    private InwardStep release_select_point_3(Point p) {
        if(p3 == null) return InwardStep.POINT_3;

        //三角形の内心を求める	public Ten oc.center(Ten ta,Ten tb,Ten tc)
        Point center = OritaCalc.center(p1, p2, p3);

        LineSegment add_sen1 = new LineSegment(p1, center, d.getLineColor());
        if (Epsilon.high.gt0(add_sen1.determineLength())) {
            d.addLineSegment(add_sen1);
        }
        LineSegment add_sen2 = new LineSegment(p2, center, d.getLineColor());
        if (Epsilon.high.gt0(add_sen2.determineLength())) {
            d.addLineSegment(add_sen2);
        }
        LineSegment add_sen3 = new LineSegment(p3, center, d.getLineColor());
        if (Epsilon.high.gt0(add_sen3.determineLength())) {
            d.addLineSegment(add_sen3);
        }

        d.record();
        reset();
        return InwardStep.POINT_1;
    }
}
