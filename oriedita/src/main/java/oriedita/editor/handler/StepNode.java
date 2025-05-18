package oriedita.editor.handler;

import origami.crease_pattern.element.Point;

import java.util.function.Consumer;
import java.util.function.Function;

public class StepNode <T extends Enum<T>> {
    private final T step;
    private final Consumer<Point> moveAction;
    private final Consumer<Point> pressAction;
    private final Consumer<Point> dragAction;
    private final Function<Point, T> releaseAction;

    public T getStep() { return step; }

    public StepNode(T step, Consumer<Point> moveAction, Consumer<Point> pressAction, Consumer<Point> dragAction, Function<Point, T> releaseAction) {
        this.step = step;
        this.moveAction = moveAction;
        this.pressAction = pressAction;
        this.dragAction = dragAction;
        this.releaseAction = releaseAction;
    }

    public static <T extends Enum<T>> StepNode<T> createNode(T step, Consumer<Point> moveAction, Consumer<Point> pressAction, Consumer<Point> dragAction, Function<Point, T> releaseAction) {
        return new StepNode<>(step, moveAction, pressAction, dragAction, releaseAction);
    }

    public static <T extends Enum<T>> StepNode<T> createNode_MD_R(T step, Consumer<Point> moveDragAction, Function<Point, T> releaseAction) {
        return new StepNode<>(step, moveDragAction, (p) -> {}, moveDragAction, releaseAction);
    }

    // Only release action decides which step to change to
    public void runHighlightSelection(Point mousePos) { moveAction.accept(mousePos); }
    public void runPressAction(Point mousePos) { pressAction.accept(mousePos); }
    public void runDragAction(Point mousePos) { dragAction.accept(mousePos); }
    public T runReleaseAction(Point mousePos) { return releaseAction.apply(mousePos); }

    @Override
    public String toString() {
        return "StepNode{" +
                "\nstep=" + step +
                ",\nmoveAction=" + moveAction.toString() +
                ",\npressAction=" + pressAction.toString() +
                ",\ndragAction=" + dragAction.toString() +
                ",\nreleaseAction=" + releaseAction .toString()+
                '}';
    }
}
