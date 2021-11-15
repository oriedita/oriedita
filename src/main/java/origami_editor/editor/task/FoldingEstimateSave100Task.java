package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami_editor.editor.Canvas;
import origami_editor.editor.databinding.FileModel;
import origami_editor.editor.databinding.FoldedFiguresList;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.service.FileSaveService;
import origami_editor.editor.service.FoldingService;

import javax.swing.*;
import java.io.File;

public class FoldingEstimateSave100Task implements Runnable {
    private final Canvas canvas;
    private final FoldingService foldingService;
    private final FileSaveService fileSaveService;
    private final FoldedFiguresList foldedFiguresList;
    private final FileModel fileModel;

    public FoldingEstimateSave100Task(Canvas canvas, FoldingService foldingService, FileSaveService fileSaveService, FoldedFiguresList foldedFiguresList, FileModel fileModel) {
        this.canvas = canvas;
        this.foldingService = foldingService;
        this.fileSaveService = fileSaveService;
        this.foldedFiguresList = foldedFiguresList;
        this.fileModel = fileModel;
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

                        fileModel.setExportImageFileName(filename);

                        canvas.w_image_running.set(true);
                        canvas.repaint();

                        try {
                            canvas.w_image_running.wait();
                        } catch (InterruptedException e) {
                            return;
                        }

                        if (!selectedFigure.foldedFigure.findAnotherOverlapValid) {
                            objective = selectedFigure.foldedFigure.discovered_fold_cases;
                        }
                    }
                } catch (InterruptedException | FoldingException e) {
                    selectedFigure.foldedFigure.estimated_initialize();
                    System.out.println(e);
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
