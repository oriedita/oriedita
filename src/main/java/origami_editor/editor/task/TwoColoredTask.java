package origami_editor.editor.task;

import origami.folding.FoldedFigure;
import origami_editor.editor.Canvas;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;

public class TwoColoredTask implements Runnable{
    private final BulletinBoard bulletinBoard;
    private final Canvas canvas;
    private final FoldingService foldingService;

    public TwoColoredTask(BulletinBoard bulletinBoard, Canvas canvas, FoldingService foldingService) {
        this.bulletinBoard = bulletinBoard;
        this.canvas = canvas;
        this.foldingService = foldingService;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        FoldedFigure_Drawer selectedFigure = foldingService.folding_prepare();

        try {
            selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
            selectedFigure.createTwoColorCreasePattern(canvas.creasePatternCamera, foldingService.lineSegmentsForFolding);
        } catch (InterruptedException e) {
            selectedFigure.foldedFigure.estimated_initialize();
            bulletinBoard.clear();
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        canvas.repaint();
    }
}
