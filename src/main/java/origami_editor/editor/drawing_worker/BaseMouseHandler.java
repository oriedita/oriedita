package origami_editor.editor.drawing_worker;

public abstract class BaseMouseHandler implements MouseModeHandler {
    protected final DrawingWorker d;

    public BaseMouseHandler(DrawingWorker d) {
        this.d = d;
    }
}
