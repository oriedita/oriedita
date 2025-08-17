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
@Handles(MouseMode.CREASE_MAKE_AUX_60)
public class MouseHandlerCreaseMakeAux extends StepMouseHandler<MouseHandlerCreaseMakeAux.Step> {
    @Inject
    public MouseHandlerCreaseMakeAux() {
    }

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
                    makeAux(lines);
                    return Step.SELECT_LINES;
                }, l -> l.getColor().isFoldingLine()));
        return st;
    }

    private void makeAux(Collection<LineSegment> lines) {
        if (lines.isEmpty()) {return;}
        var fls = d.getFoldLineSet();
        for (var s : lines) {
            LineSegment add_sen = s.withColor(LineColor.CYAN_3);
            fls.deleteLine(s);
            fls.addLine(add_sen);
        }
        fls.divideLineSegmentWithNewLines(fls.getTotal() - lines.size(), fls.getTotal());
        d.organizeCircles();
        d.record();
    }
}
