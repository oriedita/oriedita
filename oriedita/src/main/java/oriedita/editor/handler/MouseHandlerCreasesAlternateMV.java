package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.handler.step.IStepNode;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.folding.util.SortingBox;

import java.util.Collection;


@ApplicationScoped
@Handles(MouseMode.CREASES_ALTERNATE_MV_36)
public class MouseHandlerCreasesAlternateMV extends StepMouseHandler<MouseHandlerCreasesAlternateMV.Step> {
    private LineSegment dragSegment;

    public enum Step {
        CLICK_DRAG_POINT
    }
    @Inject
    public MouseHandlerCreasesAlternateMV() {
        super(Step.CLICK_DRAG_POINT);
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var graph = new StepGraph<>(Step.CLICK_DRAG_POINT);
        graph.addNode(stepFactory.createSelectIntersectingLinesNode(
                Step.CLICK_DRAG_POINT,
                LineColor.PURPLE_8,
                lines -> {
                    if (lines.isEmpty()) {
                        return Step.CLICK_DRAG_POINT;
                    }
                    alternateLines(lines);
                    return Step.CLICK_DRAG_POINT;
                },
                p -> {
                }, l -> dragSegment = l,
                l -> l.getColor() == LineColor.RED_1 || l.getColor() == LineColor.BLUE_2
        ));
        return graph;
    }

    private void alternateLines(Collection<LineSegment> p) {
        SortingBox<LineSegment> segmentBox = new SortingBox<>();
        for (var s : p) {
            segmentBox.addByWeight(s,
                    OritaCalc.distance(dragSegment.getB(), OritaCalc.findIntersection(s, dragSegment)));
        }

        LineColor alternateColor = d.getLineColor();
        if (alternateColor != LineColor.RED_1 && alternateColor != LineColor.BLUE_2) {
            alternateColor = LineColor.RED_1;
        }
        for (int i = 1; i <= segmentBox.getTotal(); i++) {
            d.getFoldLineSet().setColor(segmentBox.getValue(i), alternateColor);
            if (alternateColor == LineColor.RED_1) {
                alternateColor = LineColor.BLUE_2;
            } else {
                alternateColor = LineColor.RED_1;
            }
        }

        d.record();
        reset();
    }
}
