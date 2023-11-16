package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.Foldable;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.FoldingService;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.FoldedFigure;
import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.duplicateFoldedModelAction)
public class DuplicateFoldedModelAction extends AbstractOrieditaAction {
    @Inject
    FoldingService foldingService;
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    @Named("creasePatternCamera")
    Camera creasePatternCamera;
    @Inject
    public DuplicateFoldedModelAction(){}

    @Override
    public void actionPerformed(ActionEvent e) {
        if(foldedFiguresList.getActiveItem().getFoldedFigure().estimationStep != FoldedFigure.EstimationStep.STEP_10){
            foldingService.duplicate(foldedFiguresList.getActiveItem().getFoldedFigure());
        } else {
            LineSegmentSet lines = foldedFiguresList.getActiveItem().getFoldedFigure().wireFrame_worker1.getLineStore();
            Foldable newFigure = foldingService.initFoldedFigure();
            try {
                newFigure.createTwoColorCreasePattern(creasePatternCamera, lines);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
