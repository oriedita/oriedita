package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.service.FoldingService;
import origami.folding.FoldedFigure;

import java.awt.event.ActionEvent;

@ApplicationScoped
public class SuiteiAction extends AbstractOrieditaAction {
    private final FoldingService foldingService;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFigure.EstimationOrder estimationOrder;

    public SuiteiAction(FoldingService foldingService,
                        @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                        FoldedFigure.EstimationOrder estimationOrder) {
        this.foldingService = foldingService;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.estimationOrder = estimationOrder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        foldingService.fold(estimationOrder);//引数の意味は(i_fold_type , i_suitei_meirei);
        mainCreasePatternWorker.unselect_all(false);
    }
}
