package oriedita.editor.task;

import org.tinylog.Logger;
import oriedita.editor.Canvas;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.FoldingService;
import origami.crease_pattern.FoldingException;

import java.io.File;

public class FoldingEstimateSave100Task implements OrieditaTask {

    private final Canvas canvas;
    private final CanvasModel canvasModel;
    private final FoldingService foldingService;
    private final FileSaveService fileSaveService;
    private final FoldedFiguresList foldedFiguresList;

    public FoldingEstimateSave100Task(Canvas canvas, CanvasModel canvasModel, FoldingService foldingService, FileSaveService fileSaveService, FoldedFiguresList foldedFiguresList) {
        this.canvas = canvas;
        this.canvasModel = canvasModel;
        this.foldingService = foldingService;
        this.fileSaveService = fileSaveService;
        this.foldedFiguresList = foldedFiguresList;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        File file = fileSaveService.selectExportFile();
        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

        if (selectedFigure == null) {
            return;
        }

        if (file != null) {
            selectedFigure.getFoldedFigure().summary_write_image_during_execution = true;//Meaning during summary writing

            synchronized (canvasModel.getW_image_running()) {
                int objective = 100;
                try {

                    for (int i = 1; i <= objective; i++) {
                        foldingService.folding_estimated(selectedFigure);

                        String filename = file.getPath();
                        if (filename.contains(".")) {
                            String extension = filename.substring(filename.lastIndexOf("."));
                            String basename = filename.substring(0, filename.lastIndexOf("."));

                            filename = basename + "_" + selectedFigure.getFoldedFigure().discovered_fold_cases + extension;
                        }

                        canvas.writeImageFile(new File(filename));

                        if (!selectedFigure.getFoldedFigure().findAnotherOverlapValid) {
                            objective = selectedFigure.getFoldedFigure().discovered_fold_cases;
                        }
                    }
                } catch (InterruptedException | FoldingException e) {
                    selectedFigure.getFoldedFigure().estimated_initialize();
                    Logger.warn(e, "Folding estimate save 100 got interrupted");
                }
            }
            selectedFigure.getFoldedFigure().summary_write_image_during_execution = false;
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.getFoldedFigure().text_result = selectedFigure.getFoldedFigure().text_result + "     Computation time " + L + " msec.";

        canvasModel.markDirty();
    }

    @Override
    public String getName() {
        return "Folding Estimate Save 100";
    }
}
