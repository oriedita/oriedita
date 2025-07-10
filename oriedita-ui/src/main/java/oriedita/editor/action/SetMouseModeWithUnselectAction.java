package oriedita.editor.action;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class SetMouseModeWithUnselectAction extends AbstractOrieditaAction implements MouseModeAction {
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final MouseMode mouseMode;

    public SetMouseModeWithUnselectAction(CanvasModel canvasModel,
                                          CreasePattern_Worker mainCreasePatternWorker,
                                          MouseMode mouseMode){
        this.canvasModel = canvasModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.mouseMode = mouseMode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(mouseMode);
        mainCreasePatternWorker.unselect_all();
    }

    @Override
    public MouseMode getMouseMode() {
        return mouseMode;
    }
}
