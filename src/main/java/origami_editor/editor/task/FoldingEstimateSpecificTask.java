package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami.folding.FoldedFigure;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.FoldedFiguresList;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;

public class FoldingEstimateSpecificTask implements Runnable{
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
            System.out.println(e);
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        canvasModel.markDirty();
    }
}
