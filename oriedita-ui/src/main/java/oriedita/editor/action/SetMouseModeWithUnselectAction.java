package oriedita.editor.action;

import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class SetMouseModeWithUnselectAction extends AbstractOrieditaAction {
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final MouseMode mouseMode;

    public SetMouseModeWithUnselectAction(CanvasModel canvasModel,
                                          @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
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
}
