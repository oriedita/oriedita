package oriedita.editor.handler.step;

import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.MouseModeHandler;
import origami.crease_pattern.element.Point;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjCoordStepNode<T extends Enum<T>> extends AbstractStepNode<T> implements IStepNode<T>, ICameraStepNode {
    private final Consumer<Point> moveAction;
    private final BiFunction<Point, MouseModeHandler.Feature, T> pressAction;
    private final Consumer<Point> dragAction;
    private final Function<Point, T> releaseAction;
    private Camera camera;

    protected ObjCoordStepNode(T step,
                            Consumer<Point> moveAction,
                            BiFunction<Point, MouseModeHandler.Feature, T> pressAction,
                            Consumer<Point> dragAction,
                            Function<Point, T> releaseAction,
                               Camera camera) {
        super(step);
        this.moveAction = moveAction;
        this.pressAction = pressAction;
        this.dragAction = dragAction;
        this.releaseAction = releaseAction;
        this.camera = camera;
    }

    // TODO: use stepFactory everywhere so this isnt needed
    @Deprecated
    @Override
    public void setCamera(Camera camera) { this.camera = camera; }

    @Deprecated
    public static <T extends Enum<T>> ObjCoordStepNode<T> createNode(T step, Consumer<Point> moveAction, Consumer<Point> pressAction, Consumer<Point> dragAction, Function<Point, T> releaseAction) {
        return new ObjCoordStepNode<>(step, moveAction,
                (p, b) -> {
                    pressAction.accept(p);
                    return step;
                },
                dragAction, releaseAction, null);
    }

    @Deprecated
    public static <T extends Enum<T>> ObjCoordStepNode<T> createNode_MD_R(T step, Consumer<Point> moveDragAction, Function<Point, T> releaseAction) {
        return new ObjCoordStepNode<>(step, moveDragAction, (p, b) -> step, moveDragAction, releaseAction, null);
    }

    // Only release or press action decides which step to change to
    @Override
    public void runHighlightSelection(Point mousePos) { moveAction.accept(camera.TV2object(mousePos)); }
    @Override
    public T runPressAction(Point mousePos, MouseModeHandler.Feature button) { return pressAction.apply(camera.TV2object(mousePos), button); }
    @Override
    public void runDragAction(Point mousePos) { dragAction.accept(camera.TV2object(mousePos)); }
    @Override
    public T runReleaseAction(Point mousePos) { return releaseAction.apply(camera.TV2object(mousePos)); }

    @Override
    public String toString() {
        return "StepNode{" +
                "\nstep=" + getStep() +
                ",\nmoveAction=" + moveAction.toString() +
                ",\npressAction=" + pressAction.toString() +
                ",\ndragAction=" + dragAction.toString() +
                ",\nreleaseAction=" + releaseAction .toString()+
                '}';
    }
}
