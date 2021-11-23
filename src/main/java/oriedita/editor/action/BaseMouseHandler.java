package oriedita.editor.action;


import oriedita.editor.canvas.CreasePattern_Worker;

import javax.inject.Inject;

public abstract class BaseMouseHandler implements MouseModeHandler {
    @Inject
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }
}
