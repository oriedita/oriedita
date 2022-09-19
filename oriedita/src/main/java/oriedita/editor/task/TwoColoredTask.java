package oriedita.editor.task;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.Foldable;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.FoldingService;
import oriedita.editor.swing.component.BulletinBoard;
import origami.folding.FoldedFigure;

public class TwoColoredTask implements OrieditaTask {

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

        Foldable selectedFigure = foldingService.initFoldedFigure();

        try {
            selectedFigure.setEstimationOrder(FoldedFigure.EstimationOrder.ORDER_5);
            selectedFigure.createTwoColorCreasePattern(creasePatternCamera, foldingService.getLineSegmentsForFolding());
        } catch (InterruptedException e) {
            selectedFigure.estimated_initialize();
            bulletinBoard.clear();
            Logger.warn(e, "Two colored cp creation got cancelled");
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.setTextResult(selectedFigure.getTextResult() + "     Computation time " + L + " msec.");

        canvasModel.markDirty();
    }

    @Override
    public String getName() {
        return "Two Colored CP";
    }
}
