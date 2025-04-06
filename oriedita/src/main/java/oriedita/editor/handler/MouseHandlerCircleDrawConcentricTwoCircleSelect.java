package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50)
public class MouseHandlerCircleDrawConcentricTwoCircleSelect extends BaseMouseHandler {
    private Point p = new Point();
    private StepGraph<Step> steps = new StepGraph<>(Step.SELECT_CIRCLE_1, this::action_select_circle_1);

    private Circle circle1;
    private Circle circle2;

    private enum Step {
        SELECT_CIRCLE_1,
        SELECT_CIRCLE_2
    }
    @Inject
    public MouseHandlerCircleDrawConcentricTwoCircleSelect() { initializeSteps(); }

    @Override
    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    public void mouseReleased(Point p0) {
        if (steps.getCurrentStep() == Step.SELECT_CIRCLE_2) return;
        steps.runCurrentAction();
    }

    private void highlightSelection(Point p0) {
        p = p0 != null ? d.getCamera().TV2object(p0) : p;
        switch (steps.getCurrentStep()) {
            case SELECT_CIRCLE_1: {
                if (OritaCalc.distance_circumference(p, d.getClosestCircleMidpoint(p)) < d.getSelectionDistance()) {
                    circle1 = new Circle(d.getClosestCircleMidpoint(p));
                    circle1.setColor(LineColor.GREEN_6);
                } else circle1 = null;
                return;
            }
            case SELECT_CIRCLE_2: {
                if (OritaCalc.distance_circumference(p, d.getClosestCircleMidpoint(p)) < d.getSelectionDistance()) {
                    circle2 = new Circle(d.getClosestCircleMidpoint(p));
                    circle2.setColor(LineColor.GREEN_6);
                } else circle2 = null;
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawCircleStep(g2, circle1, camera);
        DrawingUtil.drawCircleStep(g2, circle2, camera);

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        circle1 = null;
        circle2 = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepGraph<>(Step.SELECT_CIRCLE_1, this::action_select_circle_1);
        steps.addNode(Step.SELECT_CIRCLE_2, this::action_select_circle_2);

        steps.connectNodes(Step.SELECT_CIRCLE_1, Step.SELECT_CIRCLE_2);
    }

    private Step action_select_circle_1() {
        if (circle1 == null) return null;
        return Step.SELECT_CIRCLE_2;
    }

    private Step action_select_circle_2() {
        if (circle2 == null) return null;

        double centerLineLength = OritaCalc.distance(circle1.determineCenter(), circle2.determineCenter());
        if (Math.abs(centerLineLength - circle1.getR() - circle2.getR()) > Epsilon.UNKNOWN_1EN6) {
            double concentricOffset = (centerLineLength - circle1.getR() - circle2.getR()) / 2.0;
            d.addCircle(new Circle(circle1.determineCenter(), circle1.getR() + concentricOffset, LineColor.CYAN_3));
            d.addCircle(new Circle(circle2.determineCenter(), circle2.getR() + concentricOffset, LineColor.CYAN_3));
            d.record();
        }

        reset();
        return null;
    }
}
