package origami_editor.editor.task;

import origami.folding.FoldedFigure;
import origami_editor.editor.Canvas;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;

public class FoldingEstimateTask implements Runnable {
    private final FoldingService foldingService;
    private final BulletinBoard bulletinBoard;
    private final Canvas canvas;
    private final FoldedFigure_Drawer selectedFigure;
    private final FoldedFigure.EstimationOrder estimationOrder;

    public FoldingEstimateTask(FoldingService foldingService, BulletinBoard bulletinBoard, Canvas canvas, FoldedFigure_Drawer selectedFigure, FoldedFigure.EstimationOrder estimationOrder) {
        this.foldingService = foldingService;
        this.bulletinBoard = bulletinBoard;
        this.canvas = canvas;
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
            foldingService.folding_estimated(selectedFigure);
        } catch (Exception e) {
            selectedFigure.foldedFigure.estimated_initialize();
            bulletinBoard.clear();

            System.err.println("Folding estimation got interrupted.");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        canvas.repaint();

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        canvas.repaint();
    }
}
