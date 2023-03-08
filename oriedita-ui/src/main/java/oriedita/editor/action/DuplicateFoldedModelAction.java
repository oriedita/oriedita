package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.service.FoldingService;
import origami.folding.FoldedFigure;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.duplicate)
public class DuplicateFoldedModelAction extends AbstractOrieditaAction {
    @Inject
    FoldingService foldingService;
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    public DuplicateFoldedModelAction(){}

    @Override
    public void actionPerformed(ActionEvent e) {
        foldingService.duplicate(foldedFiguresList.getActiveItem().getFoldedFigure());
    }
}
