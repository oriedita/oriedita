package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami.folding.FoldedFigure;
import origami_editor.editor.App;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

public class TwoColoredTask implements Runnable{
    private final App app;

    public TwoColoredTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        FoldedFigure_Drawer selectedFigure = app.folding_prepare();

        try {
            selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
            selectedFigure.createTwoColorCreasePattern(app.canvas.creasePatternCamera, app.lineSegmentsForFolding, app.startingFaceId);
        } catch (InterruptedException e) {
            selectedFigure.foldedFigure.estimated_initialize();
            app.bulletinBoard.clear();
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        app.repaintCanvas();
    }
}
