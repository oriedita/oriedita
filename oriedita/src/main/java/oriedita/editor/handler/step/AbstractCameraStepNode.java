package oriedita.editor.handler.step;

import oriedita.editor.drawing.tools.Camera;

public abstract class AbstractCameraStepNode<T extends Enum<T>> implements IStepNode<T>, ICameraStepNode {
    private final T step;
    protected Camera camera;

    protected AbstractCameraStepNode(T step) {
        this.step = step;
    }

    @Override
    public T getStep() { return step; }

    // TODO: maybe make a stepFactory or sth so this isnt needed?
    @Override
    public void setCamera(Camera camera) { this.camera = camera; }
}
