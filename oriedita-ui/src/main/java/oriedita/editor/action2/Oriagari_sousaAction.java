package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.action.FoldedFigureOperationMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.oriagari_sousaAction)
public class Oriagari_sousaAction extends AbstractOrieditaAction {
    @Inject
    CanvasModel canvasModel;
    @Inject
    FoldedFiguresList foldedFiguresList;

    @Inject
    public Oriagari_sousaAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setFoldedFigureOperationMode(FoldedFigureOperationMode.MODE_1);
        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

        if (selectedFigure != null) {
            selectedFigure.getFoldedFigure().setAllPointStateFalse();
            selectedFigure.record();
        }

        canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);
    }
}
