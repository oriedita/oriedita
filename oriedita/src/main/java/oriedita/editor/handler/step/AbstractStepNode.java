package oriedita.editor.handler.step;

public abstract class AbstractStepNode<T extends Enum<T>> implements IStepNode<T> {
    private final T step;

    protected AbstractStepNode(T step) {
        this.step = step;
    }

    @Override
    public T getStep() { return step; }
}
