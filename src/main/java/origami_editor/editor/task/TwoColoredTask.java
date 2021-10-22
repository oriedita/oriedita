package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
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

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

        if (selectedFigure == null) {
            return;
        }

        try {
            app.createTwoColorCreasePattern();
        } catch (InterruptedException | FoldingException e) {
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
