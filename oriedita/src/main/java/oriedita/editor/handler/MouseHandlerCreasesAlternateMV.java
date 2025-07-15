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

enum CreasesAlternateMVStep {
    CLICK_DRAG_POINT
}

@ApplicationScoped
@Handles(MouseMode.CREASES_ALTERNATE_MV_36)
public class MouseHandlerCreasesAlternateMV extends StepMouseHandler<CreasesAlternateMVStep> {
    private Point anchorPoint, releasePoint;
    private LineSegment dragSegment;

    @Inject
    public MouseHandlerCreasesAlternateMV() {
        super(CreasesAlternateMVStep.CLICK_DRAG_POINT);
        steps.addNode(StepNode.createNode(CreasesAlternateMVStep.CLICK_DRAG_POINT, this::move_click_drag_point, (p) -> {
        }, this::drag_click_drag_point, this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, anchorPoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawStepVertex(g2, releasePoint, d.getLineColor(), camera, d.getGridInputAssist());
        DrawingUtil.drawLineStep(g2, dragSegment, camera, settings.getLineWidth(), d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        anchorPoint = null;
        releasePoint = null;
        dragSegment = null;
    }

    // Click drag point
    private void move_click_drag_point(Point p) {
        anchorPoint = p;
    }

    private void drag_click_drag_point(Point p) {
        releasePoint = p;
        dragSegment = new LineSegment(anchorPoint, releasePoint).withColor(d.getLineColor());
    }

    private CreasesAlternateMVStep release_click_drag_point(Point p) {
        if (!Epsilon.high.gt0(dragSegment.determineLength())) {
            reset();
            return CreasesAlternateMVStep.CLICK_DRAG_POINT;
        }

        SortingBox<LineSegment> segmentBox = new SortingBox<>();
        for (var s : d.getFoldLineSet().getLineSegmentsIterable()) {
            LineSegment.Intersection lineIntersection = OritaCalc.determineLineSegmentIntersection(s, dragSegment,
                    Epsilon.UNKNOWN_1EN4);
            if (!(lineIntersection == LineSegment.Intersection.INTERSECTS_1
                    || lineIntersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27
                    || lineIntersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28)) {
                continue;
            }
            segmentBox.addByWeight(s,
                    OritaCalc.distance(dragSegment.getB(), OritaCalc.findIntersection(s, dragSegment)));
        }

        LineColor alternateColor = d.getLineColor();
        for (int i = 1; i <= segmentBox.getTotal(); i++) {
            d.getFoldLineSet().setColor(segmentBox.getValue(i), alternateColor);
            if (alternateColor == LineColor.RED_1) {
                alternateColor = LineColor.BLUE_2;
            } else if (alternateColor == LineColor.BLUE_2) {
                alternateColor = LineColor.RED_1;
            }
        }

        d.record();
        reset();
        return CreasesAlternateMVStep.CLICK_DRAG_POINT;
    }
}
