package origami_editor.editor.canvas;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseMouseHandler implements MouseModeHandler {
    @Autowired
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }
}
