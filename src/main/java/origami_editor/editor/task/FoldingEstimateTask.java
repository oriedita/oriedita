package origami_editor.editor.task;

import origami_editor.editor.App;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

public class FoldingEstimateTask implements Runnable {
    private final App app;

    public FoldingEstimateTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

        if (selectedFigure == null) {
            return;
        }

        try {
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
