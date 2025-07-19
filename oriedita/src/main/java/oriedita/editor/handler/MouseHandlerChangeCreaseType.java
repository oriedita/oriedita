package oriedita.editor.handler;

import java.awt.Graphics2D;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

enum ChangeCreaseTypeStep {
    SELECT_SEGMENT,
}

@ApplicationScoped
@Handles(MouseMode.CHANGE_CREASE_TYPE_4)
public class MouseHandlerChangeCreaseType extends StepMouseHandler<ChangeCreaseTypeStep> {
    private LineSegment segment;

    @Inject
    public MouseHandlerChangeCreaseType() {
        super(ChangeCreaseTypeStep.SELECT_SEGMENT);
        steps.addNode(StepNode.createNode_MD_R(ChangeCreaseTypeStep.SELECT_SEGMENT,
                this::move_drag_select_segment, this::release_select_segment));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawLineStep(g2, segment, camera, 3.0f, d.getGridInputAssist());
    }

    @Override
    public void reset() {
        resetStep();
        segment = null;
    }

    // Select a segment
    private void move_drag_select_segment(Point p) {
        LineSegment tmpSegment = d.getFoldLineSet().closestLineSegmentSearch(p);
        if (OritaCalc.determineLineSegmentDistance(p, tmpSegment) < d.getSelectionDistance()) {
            segment = new LineSegment(tmpSegment);
        } else
            segment = null;
    }

    private ChangeCreaseTypeStep release_select_segment(Point p) {
        if (segment == null)
            return ChangeCreaseTypeStep.SELECT_SEGMENT;

        LineColor ic_temp = segment.getColor();
        if (ic_temp.isFoldingLine()) {
            d.getFoldLineSet().setColor(segment, ic_temp.advanceFolding());
            d.record();
        }

        reset();
        return ChangeCreaseTypeStep.SELECT_SEGMENT;
    }
}
