package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

import java.util.Collection;

@ApplicationScoped
@Handles(MouseMode.CREASE_MAKE_MOUNTAIN_23)
public class MouseHandlerCreaseMakeMountain extends StepMouseHandler<MouseHandlerCreaseMakeMountain.Step> {
    @Inject
    public MouseHandlerCreaseMakeMountain() {
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
                    makeMountain(lines);
                    return Step.SELECT_LINES;
                }, l -> l.getColor() != LineColor.RED_1));
        return st;
    }

    /**
     * マウス操作(mouseMode==25 でボタンを離したとき)を行う関数
     * <p>
     * The reason for doing {@link CreasePattern_Worker#fix2()} at the end of this
     * process is to correct the T-shaped disconnection that frequently occurs in
     * the combination of the original polygonal line and the polygonal line
     * converted from the auxiliary line.
     */
    private void makeMountain(Collection<LineSegment> lines) {
        if (lines.isEmpty()) {return;}
        d.getFoldLineSet().setColor(lines, LineColor.RED_1);
        d.fix2();
        d.record();
    }
}
