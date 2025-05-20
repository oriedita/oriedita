package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class SetMouseModeAction extends AbstractOrieditaAction implements MouseModeAction {
    private final MouseMode mouseMode;
    private final CanvasModel canvasModel;

    public SetMouseModeAction(CanvasModel canvasModel, MouseMode mouseMode) {
        this.mouseMode = mouseMode;
        this.canvasModel = canvasModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(mouseMode);
    }

    @Override
    public MouseMode getMouseMode() {
        return mouseMode;
    }
}
