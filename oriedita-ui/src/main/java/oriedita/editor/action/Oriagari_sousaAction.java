package oriedita.editor.action;

import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.handler.FoldedFigureOperationMode;

import java.awt.event.ActionEvent;

public class Oriagari_sousaAction extends AbstractOrieditaAction implements OrieditaAction {
    @Inject
    FoldedFiguresList foldedFiguresList;
    private final CanvasModel canvasModel;
    private final ActionType actionType;
    private final FoldedFigureOperationMode foldedFigureOperationMode;

    public Oriagari_sousaAction(CanvasModel canvasModel, ActionType actionType, FoldedFigureOperationMode foldedFigureOperationMode) {
        this.actionType = actionType;
        this.foldedFigureOperationMode = foldedFigureOperationMode;
        this.canvasModel = canvasModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setFoldedFigureOperationMode(foldedFigureOperationMode);
        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

        if (selectedFigure != null) {
            selectedFigure.getFoldedFigure().setAllPointStateFalse();
            selectedFigure.record();
        }

        canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);
    }

    @Override
    public ActionType getActionType(){
        return actionType;
    }
}
