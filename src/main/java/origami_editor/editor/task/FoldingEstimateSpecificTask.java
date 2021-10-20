package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami_editor.editor.App;
import origami_editor.editor.folded_figure.FoldedFigure;

public class FoldingEstimateSpecificTask implements Runnable{
    private final App app;

    public FoldingEstimateSpecificTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        if (app.foldedFigureModel.getFoldedCases() == app.OZ.discovered_fold_cases) {
            app.OZ.text_result = "Number of found solutions = " + app.OZ.discovered_fold_cases + "  ";
        }
        int objective = app.foldedFigureModel.getFoldedCases();
        try {
            while (objective > app.OZ.discovered_fold_cases) {
                app.folding_estimated();
                app.repaintCanvas();

                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;
                if (!app.OZ.findAnotherOverlapValid) {
                    objective = app.OZ.discovered_fold_cases;
                }
            }
        } catch (InterruptedException | FoldingException e) {
            app.OZ.estimated_initialize();
            System.out.println(e);
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.text_result = app.OZ.text_result + "     Computation time " + L + " msec.";

        app.repaintCanvas();
    }
}
