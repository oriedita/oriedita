package oriedita.editor.handler.step;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.MouseModeHandler;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@ApplicationScoped
public class StepFactory {

    private final Camera cpCamera;
    private final CreasePattern_Worker d;

    @Inject
    public StepFactory(
            @Named("creasePatternCamera") Camera cpCamera,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker d
    ) {
        this.cpCamera = cpCamera;
        this.d = d;
    }

    public <T extends Enum<T>> ObjCoordStepNode<T> createSwitchNode(
            T step, Consumer<Point> moveAction, Function<MouseModeHandler.Feature, T> pressAction) {
        return new ObjCoordStepNode<>(step,
                moveAction, (p, f) -> pressAction.apply(f), p -> {}, p -> step, cpCamera);
    }

    public <T extends Enum<T>> ObjCoordStepNode<T> createNode(T step, Consumer<Point> moveAction, Consumer<Point> pressAction, Consumer<Point> dragAction, Function<Point, T> releaseAction) {
        return new ObjCoordStepNode<>(step, moveAction,
                (p, b) -> {
                    pressAction.accept(p);
                    return step;
                },
                dragAction, releaseAction, cpCamera);
    }

    public <T extends Enum<T>> ObjCoordStepNode<T> createNode_MD_R(T step, Consumer<Point> moveDragAction, Function<Point, T> releaseAction) {
        return new ObjCoordStepNode<>(step, moveDragAction, (p, b) -> step, moveDragAction, releaseAction, cpCamera);
    }

    /**
     * creates a step for box selection. The box is automatically drawn, and on mouseReleased, either releaseAction
     * (in case a box was drawn) or releasePointAction (in case of simple clicks without drawing a box) is called with
     * the object coordinates of the box/clicked point.
     * @param step enum value corresponding to this step
     * @param releaseAction action to be called when a box was drawn
     * @param releasePointAction action to be called when no box was drawn
     * @return new Step for Box Selection
     * @param <T> step enum type
     */
    public <T extends Enum<T>> BoxSelectStepNode<T> createBoxSelectNode(T step, Function<Polygon, T> releaseAction,
                                                                        Function<Point, T> releasePointAction) {
        return new BoxSelectStepNode<>(step,
                releaseAction, releasePointAction, p -> {}, p -> {}, cpCamera
        );
    }

    public <T extends Enum<T>> BoxSelectStepNode<T> createBoxSelectNode(T step,
                                                                        Function<Polygon, T> releaseAction,
                                                                        Function<Point, T> releasePointAction,
                                                                        Consumer<Point> moveAction,
                                                                        Consumer<Polygon> dragAction){
        return new BoxSelectStepNode<>(step, releaseAction, releasePointAction, moveAction, dragAction, cpCamera);
    }

    /**
     * creates a step that selects lines, either by clicking near it or by dragging a box. The lines are highlighted
     * on mouseMove/drag by drawing them thicker. Lines for which the lineFilter returns false will be ignored during
     * selection. On mouseReleased, lineAction will be executed with the selected line(s) as the argument.
     * @param step enum value corresponding to this step
     * @param lineAction action to be executed on mouseReleased with the selected lines
     * @param lineFilter filter for selecting lines, all lines where this returns false will be ignored
     * @return new Step for Box Selection of lines
     * @param <T> step enum type
     */
    public <T extends Enum<T>> BoxSelectStepNode<T> createBoxSelectLinesNode(T step,
                                                                             Function<Collection<LineSegment>, T> lineAction,
                                                                             Predicate<LineSegment> lineFilter){
        return new BoxSelectLinesStepNode<>(step,
                lineAction, l -> lineAction.apply(List.of(l)), p -> {}, p -> {}, lineFilter, cpCamera, d);
    }
}
