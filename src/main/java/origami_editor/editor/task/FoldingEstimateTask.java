package origami_editor.editor.task;

import origami.crease_pattern.LineSegmentSet;
import origami.folding.FoldedFigure;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FoldingService;
import origami_editor.tools.Camera;

public class FoldingEstimateTask {
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

                System.err.println("Folding estimation got interrupted.");
                System.err.println(e.getMessage());
                e.printStackTrace();
            }

            canvasModel.markDirty();

            long stop = System.currentTimeMillis();
            long L = stop - start;
            selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

            canvasModel.markDirty();
        });
    }
}
