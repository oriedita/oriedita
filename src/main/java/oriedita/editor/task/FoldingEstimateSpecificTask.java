package oriedita.editor.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import origami.crease_pattern.FoldingException;
import origami.folding.FoldedFigure;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.FoldingService;

public class FoldingEstimateSpecificTask implements Runnable{
    private static final Logger logger = LogManager.getLogger(FoldingEstimateSpecificTask.class);
    private final FoldedFigureModel foldedFigureModel;
    private final FoldingService foldingService;
    private final CanvasModel canvasModel;
    private final FoldedFiguresList foldedFiguresList;

    public FoldingEstimateSpecificTask(FoldedFigureModel foldedFigureModel, FoldingService foldingService, CanvasModel canvasModel, FoldedFiguresList foldedFiguresList) {
        this.foldedFigureModel = foldedFigureModel;
        this.foldingService = foldingService;
        this.canvasModel = canvasModel;
        this.foldedFiguresList = foldedFiguresList;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

        if (selectedFigure == null) {
            return;
        }

        if (foldedFigureModel.getFoldedCases() == selectedFigure.foldedFigure.discovered_fold_cases) {
            selectedFigure.foldedFigure.text_result = "Number of found solutions = " + selectedFigure.foldedFigure.discovered_fold_cases + "  ";
        }
        int objective = foldedFigureModel.getFoldedCases();
        try {
            while (objective > selectedFigure.foldedFigure.discovered_fold_cases) {
                foldingService.folding_estimated(selectedFigure);
                canvasModel.markDirty();

                selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;
                if (!selectedFigure.foldedFigure.findAnotherOverlapValid) {
                    objective = selectedFigure.foldedFigure.discovered_fold_cases;
                }
            }
        } catch (InterruptedException | FoldingException e) {
            selectedFigure.foldedFigure.estimated_initialize();
            logger.error("Folding got cancelled", e);
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        canvasModel.markDirty();
    }
}
