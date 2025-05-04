package oriedita.editor.handler;

import java.util.function.Supplier;

public class StepNode <T extends Enum<T>> {
    private final T step;
    private final Runnable moveAction;
    private final Runnable pressAction;
    private final Runnable dragAction;
    private final Supplier<T> releaseAction;

    public T getStep() { return step; }

    public StepNode(T step, Runnable moveAction, Runnable pressAction, Runnable dragAction, Supplier<T> releaseAction) {
        this.step = step;
        this.moveAction = moveAction;
        this.pressAction = pressAction;
        this.dragAction = dragAction;
        this.releaseAction = releaseAction;
    }

    public static <T extends Enum<T>> StepNode<T> createNode(T step, Runnable moveAction, Runnable pressAction, Runnable dragAction, Supplier<T> releaseAction) {
        return new StepNode<>(step, moveAction, pressAction, dragAction, releaseAction);
    }

    public static <T extends Enum<T>> StepNode<T> createNode_MD_R(T step, Runnable moveDragAction, Supplier<T> releaseAction) {
        return new StepNode<>(step, moveDragAction, () -> {}, moveDragAction, releaseAction);
    }

    // Only release action decides which step to change to
    public void runHighlightSelection() { moveAction.run(); }
    public void runPressAction() { pressAction.run(); }
    public void runDragAction() { dragAction.run(); }
    public T runReleaseAction() { return releaseAction.get(); }

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
