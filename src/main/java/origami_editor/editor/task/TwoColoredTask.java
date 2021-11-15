package origami_editor.editor.task;

import javax.inject.Inject;
import javax.inject.Named;
import origami.folding.FoldedFigure;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;
import origami_editor.editor.drawing.tools.Camera;

public class TwoColoredTask implements Runnable{
    private final BulletinBoard bulletinBoard;
    private final Camera creasePatternCamera;
    private final FoldingService foldingService;
    private final CanvasModel canvasModel;

    @Inject
    public TwoColoredTask(BulletinBoard bulletinBoard, @Named("creasePatternCamera") Camera creasePatternCamera, FoldingService foldingService, CanvasModel canvasModel) {
        this.bulletinBoard = bulletinBoard;
        this.creasePatternCamera = creasePatternCamera;
        this.foldingService = foldingService;
        this.canvasModel = canvasModel;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        FoldedFigure_Drawer selectedFigure = foldingService.folding_prepare();

        try {
            selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
            selectedFigure.createTwoColorCreasePattern(creasePatternCamera, foldingService.lineSegmentsForFolding);
        } catch (InterruptedException e) {
            selectedFigure.foldedFigure.estimated_initialize();
            bulletinBoard.clear();
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        canvasModel.markDirty();
    }
}
