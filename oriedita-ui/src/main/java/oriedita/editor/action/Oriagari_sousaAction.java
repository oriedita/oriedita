package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.handler.FoldedFigureOperationMode;

import java.awt.event.ActionEvent;

public class Oriagari_sousaAction extends AbstractOrieditaAction {
    private final FoldedFiguresList foldedFiguresList;
    private final CanvasModel canvasModel;
    private final FoldedFigureOperationMode foldedFigureOperationMode;

    public Oriagari_sousaAction(CanvasModel canvasModel, FoldedFiguresList foldedFiguresList, FoldedFigureOperationMode foldedFigureOperationMode) {
        this.foldedFigureOperationMode = foldedFigureOperationMode;
        this.canvasModel = canvasModel;
        this.foldedFiguresList = foldedFiguresList;
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
}
