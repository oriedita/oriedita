package oriedita.editor.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import origami.crease_pattern.LineSegmentSet;
import origami.folding.FoldedFigure;
import oriedita.editor.swing.component.BulletinBoard;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Camera;

public class FoldingEstimateTask {
    private static Logger logger = LogManager.getLogger(FoldingEstimateTask.class);

    private final BulletinBoard bulletinBoard;
    private final CanvasModel canvasModel;
    private final Camera creasePatternCamera;

    public FoldingEstimateTask(Camera creasePatternCamera, BulletinBoard bulletinBoard, CanvasModel canvasModel) {
        this.creasePatternCamera = creasePatternCamera;
        this.bulletinBoard = bulletinBoard;
        this.canvasModel = canvasModel;
    }

    public void execute(LineSegmentSet lineSegmentsForFolding, FoldedFigure_Drawer selectedFigure, FoldedFigure.EstimationOrder estimationOrder) {
        TaskExecutor.executeTask("Folding Estimate", () -> {
            long start = System.currentTimeMillis();

            if (selectedFigure == null) {
                return;
            }

            try {
                selectedFigure.foldedFigure.estimationOrder = estimationOrder;
                selectedFigure.folding_estimated(creasePatternCamera, lineSegmentsForFolding);
            } catch (Exception e) {
                selectedFigure.foldedFigure.estimated_initialize();
                bulletinBoard.clear();

                logger.error("Folding estimation got interrupted.", e);
            }

            canvasModel.markDirty();

            long stop = System.currentTimeMillis();
            long L = stop - start;
            selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

            canvasModel.markDirty();
        });
    }
}
