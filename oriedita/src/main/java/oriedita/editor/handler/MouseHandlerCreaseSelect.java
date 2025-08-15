package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineSegment;

import java.util.Collection;

@ApplicationScoped
@Handles(MouseMode.CREASE_SELECT_19)
public class MouseHandlerCreaseSelect extends StepMouseHandler<MouseHandlerCreaseSelect.Step> {
    public enum Step {
        SELECT_LINE
    }

    private final CreasePattern_Worker d;

    @Inject
    public MouseHandlerCreaseSelect(
            @Named("mainCreasePattern_Worker") CreasePattern_Worker d) {
        this.d = d;
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var st = new StepGraph<>(Step.SELECT_LINE);
        st.addNode(stepFactory.createBoxSelectLinesNode(Step.SELECT_LINE,
                lines -> {
                    selectLines(lines);
                    return Step.SELECT_LINE;
                }, l -> true));
        return st;
    }

    private void selectLines(Collection<LineSegment> lines) {
        int beforeSelectNum = d.getFoldLineTotalForSelectFolding();

        for (LineSegment line : lines) {
            line.setSelected(2);
        }
        if (!lines.isEmpty()) {
            d.setIsSelectionEmpty(false);
        }
        int afterSelectNum = d.getFoldLineTotalForSelectFolding();
        if (afterSelectNum != beforeSelectNum) {
            d.record();
        }
    }
}
