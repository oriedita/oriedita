package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.handler.step.ObjCoordStepNode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

enum CircleDrawConcentricSelect {
    SELECT_TARGET_CIRCLE,
    SELECT_TWO_CIRCLES,
    SELECT_INDICATOR,
}

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49)
public class MouseHandlerCircleDrawConcentricSelect extends StepMouseHandler<CircleDrawConcentricSelect> {
    private Circle targetCircle, circle1, circle2, indicator1, indicator2, resultCircle;
    private int refCount = 0;

    @Inject
    public MouseHandlerCircleDrawConcentricSelect() {
        super(CircleDrawConcentricSelect.SELECT_TARGET_CIRCLE);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawConcentricSelect.SELECT_TARGET_CIRCLE,
                this::move_drag_select_target_circle, this::release_select_target_circle));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawConcentricSelect.SELECT_TWO_CIRCLES,
                this::move_drag_select_two_circles, this::release_select_two_circle));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawConcentricSelect.SELECT_INDICATOR,
                this::move_drag_select_indicator, this::release_select_indicator));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawCircleStep(g2, targetCircle, camera);
        DrawingUtil.drawCircleStep(g2, circle1, camera);
        DrawingUtil.drawCircleStep(g2, circle2, camera);
        DrawingUtil.drawCircleStep(g2, indicator1, camera);
        DrawingUtil.drawCircleStep(g2, indicator2, camera);
        DrawingUtil.drawCircleStep(g2, resultCircle, camera);
    }

    @Override
    public void reset() {
        resetStep();
        targetCircle = null;
        circle1 = null;
        circle2 = null;
        indicator1 = null;
        indicator2 = null;
        resultCircle = null;
        refCount = 0;
    }

    // Select target circle
    private void move_drag_select_target_circle(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            targetCircle = new Circle(tmpCircle.determineCenter(), tmpCircle.getR(), LineColor.GREEN_6);
        } else
            targetCircle = null;
    }

    private CircleDrawConcentricSelect release_select_target_circle(Point p) {
        if (targetCircle == null)
            return CircleDrawConcentricSelect.SELECT_TARGET_CIRCLE;
        return CircleDrawConcentricSelect.SELECT_TWO_CIRCLES;
    }

    // Select two circles
    private void move_drag_select_two_circles(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (!tmpCircle.equals(targetCircle) && refCount == 0) {
            if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
                circle1 = new Circle(tmpCircle.determineCenter(), tmpCircle.getR(), LineColor.GREEN_6);
            } else
                circle1 = null;
        }
        if (refCount != 1)
            return;
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()
                && !tmpCircle.equals(circle1)
                && !tmpCircle.equals(targetCircle)) {
            circle2 = new Circle(tmpCircle.determineCenter(), tmpCircle.getR(), LineColor.GREEN_6);
        } else
            circle2 = null;
    }

    private CircleDrawConcentricSelect release_select_two_circle(Point p) {
        if (circle1 == null)
            return CircleDrawConcentricSelect.SELECT_TWO_CIRCLES;
        if (refCount == 0) {
            refCount++;
            return CircleDrawConcentricSelect.SELECT_TWO_CIRCLES;
        }
        if (circle2 == null)
            return CircleDrawConcentricSelect.SELECT_TWO_CIRCLES;

        double delta_r = Math.abs(circle2.getR() - circle1.getR());
        if (!Epsilon.high.eq0(delta_r)) {
            double outer_r = targetCircle.getR() + delta_r;
            double inner_r = targetCircle.getR() - delta_r;

            indicator1 = new Circle(targetCircle.determineCenter(), outer_r, LineColor.MAGENTA_5);
            if (Epsilon.high.gt0(inner_r)) {
                indicator2 = new Circle(targetCircle.determineCenter(), inner_r, LineColor.MAGENTA_5);
                return CircleDrawConcentricSelect.SELECT_INDICATOR;
            }
        }

        reset();
        return CircleDrawConcentricSelect.SELECT_TARGET_CIRCLE;
    }

    // Select indicator
    private void move_drag_select_indicator(Point p) {
        resultCircle = getValidClosestIndicator(p, Arrays.asList(indicator1, indicator2));
        if (resultCircle == null)
            return;
        resultCircle.setColor(LineColor.ORANGE_4);
    }

    private CircleDrawConcentricSelect release_select_indicator(Point p) {
        if (resultCircle == null)
            return CircleDrawConcentricSelect.SELECT_INDICATOR;
        resultCircle.setColor(LineColor.CYAN_3);
        d.addCircle(resultCircle);
        d.record();
        reset();
        return CircleDrawConcentricSelect.SELECT_TARGET_CIRCLE;
    }

    private Circle getValidClosestIndicator(Point p, List<Circle> circles) {
        Circle closest = null;
        double minEdgeToCenter = 100000.0;

        for (Circle circle : circles) {
            double dist = p.distance(circle.determineCenter());
            double sumRadii = d.getSelectionDistance() + circle.getR();
            double diffRadii = Math.abs(d.getSelectionDistance() - circle.getR());

            if (!(dist <= sumRadii && dist >= diffRadii))
                continue;

            double edgeToCenter = Math.abs(dist - circle.getR());
            if (edgeToCenter < minEdgeToCenter) {
                minEdgeToCenter = edgeToCenter;
                closest = new Circle(circle);
            }
        }

        return closest;
    }
}
