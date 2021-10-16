package origami_editor.editor.task;

import origami_editor.editor.App;

public class FoldingEstimateTask implements Runnable {
    private final App app;

    public FoldingEstimateTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        try {
            app.folding_estimated();
        } catch (InterruptedException e) {
            app.OZ.estimated_initialize();
            app.bulletinBoard.clear();

            System.err.println("Folding estimation got interrupted.");
        }
        app.repaintCanvas();

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.text_result = app.OZ.text_result + "     Computation time " + L + " msec.";

        app.repaintCanvas();
    }
}
