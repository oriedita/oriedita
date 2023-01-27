package oriedita.editor.task;

import org.tinylog.Logger;
import oriedita.editor.Foldable;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.swing.component.BulletinBoard;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.FoldedFigure;

public class FoldingEstimateTask implements OrieditaTask {
    private final BulletinBoard bulletinBoard;
    private final CanvasModel canvasModel;
    private final LineSegmentSet lineSegmentsForFolding;
    private final Foldable selectedFigure;
    private final FoldedFigure.EstimationOrder estimationOrder;
    private final Camera creasePatternCamera;

    public FoldingEstimateTask(Camera creasePatternCamera, BulletinBoard bulletinBoard, CanvasModel canvasModel, LineSegmentSet lineSegmentsForFolding, Foldable selectedFigure, FoldedFigure.EstimationOrder estimationOrder) {
        this.creasePatternCamera = creasePatternCamera;
        this.bulletinBoard = bulletinBoard;
        this.canvasModel = canvasModel;
        this.lineSegmentsForFolding = lineSegmentsForFolding;
        this.selectedFigure = selectedFigure;
        this.estimationOrder = estimationOrder;
    }

    @Override
    public String getName() {
        return "Folding Estimate";
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        if (selectedFigure == null) {
            return;
        }

        try {
            selectedFigure.setEstimationOrder(estimationOrder);
            selectedFigure.folding_estimated(creasePatternCamera, lineSegmentsForFolding);
        } catch (Exception e) {
            selectedFigure.estimated_initialize();
            bulletinBoard.clear();

            Logger.error(e, "Folding estimation got interrupted.");
        }

        canvasModel.markDirty();

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.setTextResult(selectedFigure.getTextResult() + "     Computation time " + L + " msec.");

        canvasModel.markDirty();
    }
}
