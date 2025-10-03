package oriedita.editor.handler;

import java.awt.Graphics2D;

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
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

enum CircleDrawInvertedStep {
    SELECT_1ST_CIRCLE_OR_SEGMENT,
    SELECT_2ND_CIRCLE_OR_SEGMENT,
    SELECT_CIRCLE,
}

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_INVERTED_46)
public class MouseHandlerCircleDrawInverted extends StepMouseHandler<CircleDrawInvertedStep> {
    private Circle circle1, circle2;
    private LineSegment segment;

    @Inject
    public MouseHandlerCircleDrawInverted() {
        super(CircleDrawInvertedStep.SELECT_1ST_CIRCLE_OR_SEGMENT);
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawInvertedStep.SELECT_1ST_CIRCLE_OR_SEGMENT,
                this::move_drag_select_first_circle_or_line,
                this::release_select_first_circle_or_line));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawInvertedStep.SELECT_2ND_CIRCLE_OR_SEGMENT,
                this::move_drag_select_second_circle_or_segment,
                this::release_select_second_circle_or_segment));
        steps.addNode(ObjCoordStepNode.createNode_MD_R(CircleDrawInvertedStep.SELECT_CIRCLE,
                this::move_drag_select_circle, this::release_select_circle));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawCircleStep(g2, circle1, camera);
        DrawingUtil.drawCircleStep(g2, circle2, camera);
        DrawingUtil.drawLineStep(g2, segment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        circle1 = null;
        circle2 = null;
        segment = null;
    }

    // Select circle or line
    private void move_drag_select_first_circle_or_line(Point p) {
        LineSegment tmpSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, tmpSegment) < d.getSelectionDistance()) {
            segment = new LineSegment(tmpSegment, LineColor.GREEN_6);
        } else
            segment = null;

        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            segment = null;
            circle1 = new Circle(tmpCircle);
            circle1.setColor(LineColor.GREEN_6);
        } else
            circle1 = null;
    }

    private CircleDrawInvertedStep release_select_first_circle_or_line(Point p) {
        if (circle1 == null && segment == null)
            return CircleDrawInvertedStep.SELECT_1ST_CIRCLE_OR_SEGMENT;
        if (circle1 != null)
            return CircleDrawInvertedStep.SELECT_2ND_CIRCLE_OR_SEGMENT;
        return CircleDrawInvertedStep.SELECT_CIRCLE;
    }

    // If circle selected first, select another circle or a segment
    private void move_drag_select_second_circle_or_segment(Point p) {
        LineSegment tmpSegment = d.getClosestLineSegment(p);
        if (OritaCalc.determineLineSegmentDistance(p, tmpSegment) < d.getSelectionDistance()) {
            segment = new LineSegment(tmpSegment, LineColor.ORANGE_4);
        } else
            segment = null;

        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if ((!circle1.determineCenter().equals(tmpCircle.determineCenter())
                || Math.abs(circle1.getR() - tmpCircle.getR()) > Epsilon.UNKNOWN_1EN6)
                && OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            segment = null;
            circle2 = new Circle(tmpCircle);
            circle2.setColor(LineColor.ORANGE_4);
        } else
            circle2 = null;
    }

    private CircleDrawInvertedStep release_select_second_circle_or_segment(Point p) {
        if (circle2 == null && segment == null)
            return CircleDrawInvertedStep.SELECT_2ND_CIRCLE_OR_SEGMENT;

        processResultCircle();
        reset();
        return CircleDrawInvertedStep.SELECT_1ST_CIRCLE_OR_SEGMENT;
    }

    // If segment selected first, select a circle
    private void move_drag_select_circle(Point p) {
        Circle tmpCircle = d.getClosestCircleMidpoint(p);
        if (OritaCalc.distance_circumference(p, tmpCircle) < d.getSelectionDistance()) {
            circle2 = new Circle(tmpCircle);
            circle2.setColor(LineColor.GREEN_6);
        } else
            circle2 = null;
    }

    private CircleDrawInvertedStep release_select_circle(Point p) {
        if (segment == null && circle1 == null && circle2 == null) {
            reset();
            return CircleDrawInvertedStep.SELECT_1ST_CIRCLE_OR_SEGMENT;
        }

        processResultCircle();
        reset();
        return CircleDrawInvertedStep.SELECT_1ST_CIRCLE_OR_SEGMENT;
    }

    public void add_hanten(Circle e0, Circle eh) {
        // e0の円周が(x,y)を通るとき
        if (Math.abs(OritaCalc.distance(e0.determineCenter(), eh.determineCenter())
                - e0.getR()) < Epsilon.UNKNOWN_1EN7) {
            LineSegment s_add = eh.turnAround_CircleToLineSegment(e0);
            // s_add.setcolor(3);
            d.addLineSegment(s_add);
            d.record();
            return;
        }

        // e0の円周が(x,y)を通らないとき。e0の円周の外部に(x,y)がくるとき//e0の円周の内部に(x,y)がくるとき
        Circle e_add = new Circle();
        e_add.set(eh.turnAround(e0));
        e_add.setColor(LineColor.CYAN_3);
        d.addCircle(e_add);
        d.record();
    }

    public void add_hanten(LineSegment s0, Circle eh) {
        StraightLine ty = new StraightLine(s0);
        // s0上に(x,y)がくるとき
        if (ty.calculateDistance(eh.determineCenter()) < Epsilon.UNKNOWN_1EN7) {
            return;
        }

        // s0が(x,y)を通らないとき。
        Circle e_add = new Circle();
        e_add.set(eh.turnAround_LineSegmentToCircle(s0));
        e_add.setColor(LineColor.CYAN_3);
        d.addCircle(e_add);
        d.record();
    }

    private void processResultCircle() {
        if (segment != null) {
            Circle validCircle = circle1 != null ? circle1 : circle2;
            if (validCircle == null)
                return;
            add_hanten(segment, validCircle);
        } else {
            add_hanten(circle1, circle2);
        }
    }
}
