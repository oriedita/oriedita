package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami_editor.editor.App;
import origami.folding.FoldedFigure;

public class FoldingEstimateSpecificTask implements Runnable{
    private final App app;

    public FoldingEstimateSpecificTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        if (app.foldedFigureModel.getFoldedCases() == app.OZ.foldedFigure.discovered_fold_cases) {
            app.OZ.foldedFigure.text_result = "Number of found solutions = " + app.OZ.foldedFigure.discovered_fold_cases + "  ";
        }
        int objective = app.foldedFigureModel.getFoldedCases();
        try {
            while (objective > app.OZ.foldedFigure.discovered_fold_cases) {
                app.folding_estimated();
                app.repaintCanvas();

                app.OZ.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;
                if (!app.OZ.foldedFigure.findAnotherOverlapValid) {
                    objective = app.OZ.foldedFigure.discovered_fold_cases;
                }
            }
        } catch (InterruptedException | FoldingException e) {
            app.OZ.foldedFigure.estimated_initialize();
            System.out.println(e);
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.foldedFigure.text_result = app.OZ.foldedFigure.text_result + "     Computation time " + L + " msec.";

        app.repaintCanvas();
    }
}
