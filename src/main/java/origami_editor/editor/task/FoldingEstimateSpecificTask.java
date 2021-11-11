package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami_editor.editor.App;
import origami.folding.FoldedFigure;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

public class FoldingEstimateSpecificTask implements Runnable{
    private final App app;

    public FoldingEstimateSpecificTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

        if (selectedFigure == null) {
            return;
        }

        if (app.foldedFigureModel.getFoldedCases() == selectedFigure.foldedFigure.discovered_fold_cases) {
            selectedFigure.foldedFigure.text_result = "Number of found solutions = " + selectedFigure.foldedFigure.discovered_fold_cases + "  ";
        }
        int objective = app.foldedFigureModel.getFoldedCases();
        try {
            while (objective > selectedFigure.foldedFigure.discovered_fold_cases) {
                app.foldingService.folding_estimated(selectedFigure);
                app.repaintCanvas();

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

        app.repaintCanvas();
    }
}
