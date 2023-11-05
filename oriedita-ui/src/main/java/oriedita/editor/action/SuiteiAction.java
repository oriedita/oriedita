package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.service.FoldingService;
import origami.folding.FoldedFigure;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.suitei_02Action)
public class SuiteiAction extends AbstractOrieditaAction implements OrieditaAction {
    private final FoldingService foldingService;
    @Inject
    @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;
    private final ActionType actionType;
    private final FoldedFigure.EstimationOrder estimationOrder;

    public SuiteiAction(FoldingService foldingService, ActionType actionType, FoldedFigure.EstimationOrder estimationOrder) {
        this.actionType = actionType;
        this.estimationOrder = estimationOrder;
        this.foldingService = foldingService;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        foldingService.fold(estimationOrder);//引数の意味は(i_fold_type , i_suitei_meirei);
        mainCreasePatternWorker.unselect_all(false);
    }

    @Override
    public ActionType getActionType(){
        return actionType;
    }
}
