package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class DegAction extends AbstractOrieditaAction implements OrieditaAction{
    private final CanvasModel canvasModel;
    private final AngleSystemModel angleSystemModel;
    private final ActionType actionType;
    private final MouseMode mouseMode;
    private final AngleSystemModel.AngleSystemInputType angleSystemInputType;

    public DegAction(CanvasModel canvasModel,
                     AngleSystemModel angleSystemModel,
                     ActionType actionType,
                     MouseMode mouseMode,
                     AngleSystemModel.AngleSystemInputType angleSystemInputType){
        this.canvasModel = canvasModel;
        this.angleSystemModel = angleSystemModel;
        this.actionType = actionType;
        this.mouseMode = mouseMode;
        this.angleSystemInputType = angleSystemInputType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        angleSystemModel.setAngleSystemInputType(angleSystemInputType);
        canvasModel.setMouseMode(mouseMode);
    }

    @Override
    public ActionType getActionType(){
        return actionType;
    }
}
