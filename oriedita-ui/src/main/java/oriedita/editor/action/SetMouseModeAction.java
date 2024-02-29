package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class SetMouseModeAction extends AbstractOrieditaAction implements OrieditaAction{
    private final CanvasModel canvasModel;
    private final ActionType actionType;
    private final MouseMode mouseMode;

    public SetMouseModeAction(CanvasModel canvasModel, ActionType actionType, MouseMode mouseMode){
        this.canvasModel = canvasModel;
        this.actionType = actionType;
        this.mouseMode = mouseMode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(mouseMode);
    }

    @Override
    public ActionType getActionType(){
        return actionType;
    }
}
