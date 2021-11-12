package origami_editor.editor.canvas;


import javax.inject.Inject;

public abstract class BaseMouseHandler implements MouseModeHandler {
    @Inject
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }
}
