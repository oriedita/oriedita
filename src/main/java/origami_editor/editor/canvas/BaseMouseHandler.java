package origami_editor.editor.canvas;

public abstract class BaseMouseHandler implements MouseModeHandler {
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }

    public void setDrawingWorker(CreasePattern_Worker d) {
        this.d = d;
    }
}
