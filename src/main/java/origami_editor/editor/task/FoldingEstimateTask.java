package origami_editor.editor.task;

import origami.folding.FoldedFigure;
import origami_editor.editor.App;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

public class FoldingEstimateTask implements Runnable {
    private final App app;
    private final FoldedFigure_Drawer selectedFigure;
    private final FoldedFigure.EstimationOrder estimationOrder;

    public FoldingEstimateTask(App app, FoldedFigure_Drawer selectedFigure, FoldedFigure.EstimationOrder estimationOrder) {
        this.app = app;
        this.selectedFigure = selectedFigure;
        this.estimationOrder = estimationOrder;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        if (selectedFigure == null) {
            return;
        }

        try {
            selectedFigure.foldedFigure.estimationOrder = estimationOrder;
            app.folding_estimated();
        } catch (Exception e) {
            selectedFigure.foldedFigure.estimated_initialize();
            app.bulletinBoard.clear();

            System.err.println("Folding estimation got interrupted.");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        app.repaintCanvas();

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        app.repaintCanvas();
    }
}
