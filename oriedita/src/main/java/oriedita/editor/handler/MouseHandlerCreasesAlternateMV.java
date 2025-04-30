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
import origami.folding.util.SortingBox;

import java.awt.Graphics2D;

@ApplicationScoped
@Handles(MouseMode.CREASES_ALTERNATE_MV_36)
public class MouseHandlerCreasesAlternateMV extends BaseMouseHandlerInputRestricted {
    private Point p = new Point();
    private StepCollection<Step> steps;

    private Point anchorPoint;
    private Point releasePoint;
    private LineSegment dragSegment;

    private enum Step {
        CLICK_DRAG_POINT,
        RELEASE_POINT,
    }

    @Inject
    public MouseHandlerCreasesAlternateMV() { initializeSteps(); }

    public void mousePressed(Point p0) { steps.runCurrentAction(); }

    public void mouseMoved(Point p0) { highlightSelection(p0); }

    public void mouseDragged(Point p0) { highlightSelection(p0); }

    public void mouseReleased(Point p0) { steps.runCurrentAction(); }

    private void highlightSelection(Point p0) {
        p = d.getCamera().TV2object(p0);
        switch (steps.getCurrentStep()) {
            case CLICK_DRAG_POINT: {
                anchorPoint = p;
                return;
            }
            case RELEASE_POINT: {
                releasePoint = p;
                dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
            }
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());

        double textPosX = p.getX() + 20 / camera.getCameraZoomX();
        double textPosY = p.getY() + 20 / camera.getCameraZoomY();
        DrawingUtil.drawText(g2, steps.getCurrentStep().name(), p.withX(textPosX).withY(textPosY), camera);
    }

    @Override
    public void reset() {
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
        initializeSteps();
    }

    private void initializeSteps() {
        steps = new StepCollection<>(Step.CLICK_DRAG_POINT, this::action_click_drag_point);
        steps.addNode(Step.RELEASE_POINT, this::action_release_point);
    }

    private void action_click_drag_point() {
        steps.setCurrentStep(Step.RELEASE_POINT);
    }

    private void action_release_point() {
        if (dragSegment == null) {
            reset();
            return;
        }

        if (!Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
            return;
        }

        SortingBox<LineSegment> segmentBox = new SortingBox<>();
        for (var s : d.getFoldLineSet().getLineSegmentsIterable()) {
            LineSegment.Intersection lineIntersection = OritaCalc.determineLineSegmentIntersection(s, dragSegment, Epsilon.UNKNOWN_1EN4);
            if (!(lineIntersection == LineSegment.Intersection.INTERSECTS_1
                    || lineIntersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27
                    || lineIntersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28)) {
                continue;
            }
            segmentBox.addByWeight(s, OritaCalc.distance(dragSegment.getB(), OritaCalc.findIntersection(s, dragSegment)));
        }

        // TODO: weird ah color alternating
        LineColor alternateColor = d.getLineColor();
        for (int i = 1; i <= segmentBox.getTotal(); i++) {
            d.getFoldLineSet().setColor(segmentBox.getValue(i), alternateColor);
            if (alternateColor == LineColor.RED_1) { alternateColor = LineColor.BLUE_2; }
            else if (alternateColor == LineColor.BLUE_2) { alternateColor = LineColor.RED_1; }
        }

        d.record();
        reset();
    }
}

