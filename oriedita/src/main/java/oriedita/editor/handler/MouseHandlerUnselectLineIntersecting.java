package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;

@ApplicationScoped
@Handles(MouseMode.UNSELECT_LINE_INTERSECTING_69)
public class MouseHandlerUnselectLineIntersecting extends StepMouseHandler<MouseHandlerUnselectLineIntersecting.Step> {

    public enum Step {
        SELECT_LINE
    }

    @Inject
    public MouseHandlerUnselectLineIntersecting() {
        super();
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var graph = new StepGraph<>(Step.SELECT_LINE);
        graph.addNode(stepFactory.createSelectIntersectingLinesNode(Step.SELECT_LINE, LineColor.PURPLE_8,
                lines -> {
                    if (!lines.isEmpty()) {
                        //やりたい動作はここに書く
                        for (LineSegment l : lines) {
                            d.getFoldLineSet().unselect(l);//lXは小文字のエルと大文字のエックス
                        }
                        d.refreshIsSelectionEmpty();
                    }
                    return Step.SELECT_LINE;
                }, (p) -> {}, (ls) -> {},
                ls -> ls.getSelected() == 2));
        return graph;
    }
}
