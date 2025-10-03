package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineSegment;

import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.DELETE_LINE_TYPE_SELECT_73)
public class MouseHandlerDeleteTypeSelect extends StepMouseHandler<MouseHandlerDeleteTypeSelect.Step> {

    public enum Step{
        SELECT_LINES
    }

    private final CanvasModel canvasModel;

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var sg = new StepGraph<>(Step.SELECT_LINES);
        sg.addNode(stepFactory.createBoxSelectLinesNode(Step.SELECT_LINES,
                lineSegments -> {
                    for (LineSegment lineSegment : lineSegments) {
                        d.getFoldLineSet().deleteLine(lineSegment);
                    }
                    d.record();
                    return Step.SELECT_LINES;
                }, lineSegment -> canvasModel.getDelLineType().matches(lineSegment.getColor())));
        return sg;
    }

    @Inject
    public MouseHandlerDeleteTypeSelect(CanvasModel canvasModel) {
        this.canvasModel = canvasModel;
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.ERASER_COLOR);
    }

}
