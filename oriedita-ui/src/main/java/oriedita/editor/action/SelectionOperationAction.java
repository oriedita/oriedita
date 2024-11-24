package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

public class SelectionOperationAction extends AbstractOrieditaAction {
    private final CanvasModel canvasModel;
    private final CanvasModel.SelectionOperationMode selectionOperationMode;
    private final MouseMode mouseMode;

    public SelectionOperationAction(CanvasModel canvasModel, CanvasModel.SelectionOperationMode selectionOperationMode, MouseMode mouseMode){
        this.selectionOperationMode = selectionOperationMode;
        this.mouseMode = mouseMode;
        this.canvasModel = canvasModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setSelectionOperationMode(selectionOperationMode);
        canvasModel.setMouseMode(mouseMode);
    }
}
