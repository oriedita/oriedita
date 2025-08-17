package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.CustomLineTypes;
import origami.crease_pattern.element.LineSegment;

import java.util.Collection;
import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.REPLACE_LINE_TYPE_SELECT_72)
public class MouseHandlerReplaceTypeSelect extends StepMouseHandler<MouseHandlerReplaceTypeSelect.Step> {

    public enum Step {
        DRAW_BOX_OR_SELECT_LINE
    }

    private final CanvasModel canvasModel;

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var sg = new StepGraph<>(Step.DRAW_BOX_OR_SELECT_LINE);
        sg.addNode(stepFactory.createBoxSelectLinesNode(Step.DRAW_BOX_OR_SELECT_LINE,
                lines -> {
                    changeColor(lines);
                    return Step.DRAW_BOX_OR_SELECT_LINE;
                }, l ->
                        canvasModel.getCustomFromLineType().matches(l.getColor())
        ));
        return sg;
    }

    private void changeColor(Collection<LineSegment> lines) {
        CustomLineTypes to = canvasModel.getCustomToLineType();
        var lc = to.getLineColor();
        var changed = d.getFoldLineSet().setColor(lines, lc);
        if (changed > 0) {
            d.record();
        }
    }

    @Inject
    public MouseHandlerReplaceTypeSelect(CanvasModel canvasModel) {
        this.canvasModel = canvasModel;
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.SWITCH_COLOR);
    }

}
