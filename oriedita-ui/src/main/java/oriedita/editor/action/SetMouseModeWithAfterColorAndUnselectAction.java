package oriedita.editor.action;

import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class SetMouseModeWithAfterColorAndUnselectAction extends AbstractOrieditaAction implements OrieditaAction{
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final ActionType actionType;
    private final MouseMode mouseMode;

    public SetMouseModeWithAfterColorAndUnselectAction(CanvasModel canvasModel,
                                                       @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                                                       ActionType actionType,
                                                       MouseMode mouseMode){
        this.canvasModel = canvasModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.actionType = actionType;
        this.mouseMode = mouseMode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(mouseMode);
        canvasModel.setMouseModeAfterColorSelection(mouseMode);
        mainCreasePatternWorker.unselect_all();
    }

    @Override
    public ActionType getActionType(){
        return actionType;
    }
}
