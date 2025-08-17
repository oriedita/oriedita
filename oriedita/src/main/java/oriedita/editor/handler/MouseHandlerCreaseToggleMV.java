package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import java.util.Collection;

@ApplicationScoped
@Handles(MouseMode.CREASE_TOGGLE_MV_58)
public class MouseHandlerCreaseToggleMV extends StepMouseHandler<MouseHandlerCreaseToggleMV.Step> {
    public enum Step {
        SELECT_LINES
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
                    changeMV(lines);
                    return Step.SELECT_LINES;
                }, l -> l.getColor() == LineColor.BLUE_2 || l.getColor() == LineColor.RED_1));
        return st;
    }

    private void changeMV(Collection<LineSegment> lines) {
        if (lines.isEmpty()) {return;}
        for (LineSegment line : lines) {
            d.getFoldLineSet().setColor(line, line.getColor().changeMV());
        }
        d.record();
    }

    @Inject
    public MouseHandlerCreaseToggleMV() {
    }
}
