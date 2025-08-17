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
@Handles(MouseMode.CREASE_UNSELECT_20)
public class MouseHandlerCreaseUnselect extends StepMouseHandler<MouseHandlerCreaseUnselect.Step> {
    public enum Step {
        SELECT_LINES
    }

    private final CreasePattern_Worker d;

    @Inject
    public MouseHandlerCreaseUnselect(
            @Named("mainCreasePattern_Worker") CreasePattern_Worker d) {
        this.d = d;
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var st = new StepGraph<>(Step.SELECT_LINES);
        st.addNode(stepFactory.createBoxSelectLinesNode(Step.SELECT_LINES,
                lines -> {
                    unselectLines(lines);
                    return Step.SELECT_LINES;
                }, l -> l.getSelected() != 0));
        return st;
    }

    private void unselectLines(Collection<LineSegment> lines) {
        if (lines.isEmpty()) {return;}
        for (LineSegment line : lines) {
            line.setSelected(0);
        }
        d.refreshIsSelectionEmpty();
        d.record();
    }
}
