package oriedita.editor.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import origami.crease_pattern.FoldingException;
import oriedita.editor.Canvas;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.FoldingService;

import java.io.File;

public class FoldingEstimateSave100Task implements Runnable {
    private static final Logger logger = LogManager.getLogger(FoldingEstimateSave100Task.class);

    private final Canvas canvas;
    private final FoldingService foldingService;
    private final FileSaveService fileSaveService;
    private final FoldedFiguresList foldedFiguresList;

    public FoldingEstimateSave100Task(Canvas canvas, FoldingService foldingService, FileSaveService fileSaveService, FoldedFiguresList foldedFiguresList) {
        this.canvas = canvas;
        this.foldingService = foldingService;
        this.fileSaveService = fileSaveService;
        this.foldedFiguresList = foldedFiguresList;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        File file = fileSaveService.selectExportFile();
        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

        if (selectedFigure == null) {
            return;
        }

        if (file != null) {
            selectedFigure.foldedFigure.summary_write_image_during_execution = true;//Meaning during summary writing

            synchronized (canvas.w_image_running) {
                int objective = 100;
                try {

                    for (int i = 1; i <= objective; i++) {
                        foldingService.folding_estimated(selectedFigure);

                        String filename = file.getPath();
                        if (filename.contains(".")) {
                            String extension = filename.substring(filename.lastIndexOf("."));
                            String basename = filename.substring(0, filename.lastIndexOf("."));

                            filename = basename + "_" + selectedFigure.foldedFigure.discovered_fold_cases + extension;
                        }

                        fileSaveService.writeImageFile(new File(filename));

                        if (!selectedFigure.foldedFigure.findAnotherOverlapValid) {
                            objective = selectedFigure.foldedFigure.discovered_fold_cases;
                        }
                    }
                } catch (InterruptedException | FoldingException e) {
                    selectedFigure.foldedFigure.estimated_initialize();
                    logger.warn("Folding estimate save 100 got interrupted", e);
                }
            }
            selectedFigure.foldedFigure.summary_write_image_during_execution = false;
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        selectedFigure.foldedFigure.text_result = selectedFigure.foldedFigure.text_result + "     Computation time " + L + " msec.";

        canvas.repaint();
    }
}
