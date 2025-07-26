package oriedita.editor.handler.step;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.MouseModeHandler;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.util.function.Consumer;
import java.util.function.Function;

@ApplicationScoped
public class StepFactory {

    private final Camera cpCamera;

    @Inject
    public StepFactory(
            @Named("creasePatternCamera") Camera cpCamera
    ) {
        this.cpCamera = cpCamera;
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

    public <T extends Enum<T>> BoxSelectStepNode<T> createBoxSelectNode(T step, Function<Polygon, T> releaseAction) {
        return new BoxSelectStepNode<>(step,
                releaseAction, cpCamera
        );
    }
}
