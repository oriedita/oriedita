package origami_editor.editor.task;

import origami.folding.FoldedFigure;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;

public class FoldingEstimateTask implements Runnable {
    private final FoldingService foldingService;
    private final BulletinBoard bulletinBoard;
    private final FoldedFigure_Drawer selectedFigure;
    private final FoldedFigure.EstimationOrder estimationOrder;
    private final CanvasModel canvasModel;

    public FoldingEstimateTask(FoldingService foldingService, BulletinBoard bulletinBoard, FoldedFigure_Drawer selectedFigure, FoldedFigure.EstimationOrder estimationOrder, CanvasModel canvasModel) {
        this.foldingService = foldingService;
        this.bulletinBoard = bulletinBoard;
        this.selectedFigure = selectedFigure;
        this.estimationOrder = estimationOrder;
        this.canvasModel = canvasModel;
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

        canvasModel.markDirty();

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        canvasModel.markDirty();
    }
}
