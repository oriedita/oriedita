package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.CustomLineTypes;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.REPLACE_LINE_TYPE_SELECT_72)
public class MouseHandlerReplaceTypeSelect extends StepMouseHandler<MouseHandlerReplaceTypeSelect.Step> {

    public enum Step {
        DRAW_BOX_OR_SELECT_LINE
    }

    private final Collection<LineSegment> highlightedLines = new ArrayList<>();

    private final CanvasModel canvasModel;

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var sg = new StepGraph<>(Step.DRAW_BOX_OR_SELECT_LINE);
        sg.addNode(stepFactory.createBoxSelectNode_M_D_R(Step.DRAW_BOX_OR_SELECT_LINE, p -> {
            CustomLineTypes from = canvasModel.getCustomFromLineType();
            highlightedLines.clear();
            d.getFoldLineSet().closestLineSegmentInRange(p, d.getSelectionDistance()).ifPresent(l -> {
                if (from.matches(l.getColor())){
                    highlightedLines.add(l);
                }
            });
        }, p -> {
            highlightedLines.clear();
            CustomLineTypes from = canvasModel.getCustomFromLineType();
            for (LineSegment highlightedLine : d.getFoldLineSet().lineSegmentsInside(p)) {
                if (from.matches(highlightedLine.getColor())) {
                    highlightedLines.add(highlightedLine);
                }
            }
        }, p -> {
            CustomLineTypes from = canvasModel.getCustomFromLineType();
            CustomLineTypes to = canvasModel.getCustomToLineType();
            d.getFoldLineSet().insideToReplaceType(p, from, to);
            return Step.DRAW_BOX_OR_SELECT_LINE;
        }, p -> {
            CustomLineTypes from = canvasModel.getCustomFromLineType();
            CustomLineTypes to = canvasModel.getCustomToLineType();
            changeNearestLine(p, from, to);
            return Step.DRAW_BOX_OR_SELECT_LINE;
        }));
        return sg;
    }

    @Inject
    public MouseHandlerReplaceTypeSelect(CanvasModel canvasModel) {
        this.canvasModel = canvasModel;
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.SWITCH_COLOR);
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        for (LineSegment highlightedLine : highlightedLines) {
            DrawingUtil.drawLineStep(g2, highlightedLine, camera, settings.getLineWidth() + 1, d.getGridInputAssist());
        }
    }

    private void changeNearestLine(Point p, CustomLineTypes from, CustomLineTypes to) {
        d.getFoldLineSet().closestLineSegmentInRange(p, d.getSelectionDistance()).ifPresent(s -> {
            if (from.matches(s.getColor())){
                d.getFoldLineSet().deleteLine(s);
                s = s.withColor(to.getLineColor());
                d.addLineSegment(s);
                d.record();
            }
        });
    }

}
