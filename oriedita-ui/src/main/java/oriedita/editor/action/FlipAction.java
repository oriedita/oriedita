package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import origami.folding.FoldedFigure;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.flipAction)
public class FlipAction extends AbstractOrieditaAction {
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    FoldedFigureModel foldedFigureModel;
    @Inject
    CanvasModel canvasModel;

    @Inject
    public FlipAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
        if (selectedFigure != null) {
            foldedFigureModel.advanceState();

            if ((canvasModel.getMouseMode() == MouseMode.MODIFY_CALCULATED_SHAPE_101) && (selectedFigure.getFoldedFigure().ip4 == FoldedFigure.State.BOTH_2)) {
                foldedFigureModel.setState(FoldedFigure.State.FRONT_0);
            }//Fold-up forecast map Added to avoid the mode that can not be moved when moving
        }
    }
}
