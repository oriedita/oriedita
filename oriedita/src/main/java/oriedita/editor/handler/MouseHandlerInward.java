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

@ApplicationScoped
@Handles(MouseMode.INWARD_8)
public class MouseHandlerInward extends BaseMouseHandlerInputRestricted {
    Point p = new Point();
    private StepCollection<Step> steps;

    Point p1, p2, p3 = null;

    private enum Step {
        POINT_1,
        POINT_2,
        POINT_3,
    }

    @Inject
    public MouseHandlerInward() { initializeSteps(); }

    public void mousePressed(Point p0) {
        steps.runCurrentAction();
    }

    @Override
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    public void mouseReleased(Point p0) {}

    private void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);

        switch (steps.getCurrentStep()) {
            case POINT_1: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    p1 = d.getClosestPoint(p);
                } else p1 = null;
                return;
            }
            case POINT_2: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    p2 = d.getClosestPoint(p);
                } else p2 = null;
                return;
            }
            case POINT_3: {
                if (p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
                    p3 = d.getClosestPoint(p);
                } else p3 = null;
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, p1, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p2, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, p3, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(p.getX() + 20).withY(p.getY() + 20), camera);
    }

    @Override
    public void reset() {
        p1 = null;
        p2 = null;
        p3 = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepCollection<>(Step.POINT_1, this::action_point_1);
        steps.addNode(Step.POINT_2, this::action_point_2);
        steps.addNode(Step.POINT_3, this::action_point_3);
    }

    private void action_point_1() {
        if(p1 == null) return;
        steps.setCurrentStep(Step.POINT_2);
    }

    private void action_point_2() {
        if(p2 == null) return;
        steps.setCurrentStep(Step.POINT_3);
    }

    private void action_point_3() {
        if(p3 == null) return;

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
    }
}
