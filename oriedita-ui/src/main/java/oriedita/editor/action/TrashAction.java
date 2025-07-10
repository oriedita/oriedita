package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.trashAction)
public class TrashAction extends AbstractOrieditaAction {
    @Inject
    FoldedFiguresList foldedFiguresList;

    @Inject
    CanvasModel canvasModel;

    @Inject
    public TrashAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (foldedFiguresList.getSize() == 0) {
            return;
        }

        Object selectedItem = foldedFiguresList.getSelectedItem();

        if (selectedItem == null) {
            selectedItem = foldedFiguresList.getElementAt(0);
        }

        foldedFiguresList.removeElement(selectedItem);
        if (foldedFiguresList.getSize() == 0) {
            canvasModel.deactivateFoldingTab();
        }
    }
}
