package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami_editor.editor.App;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

import java.io.File;

public class FoldingEstimateSave100Task implements Runnable {
    private final App app;

    public FoldingEstimateSave100Task(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        File file = app.selectExportFile();
        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

        if (selectedFigure == null) {
            return;
        }

        if (file != null) {
            selectedFigure.foldedFigure.summary_write_image_during_execution = true;//Meaning during summary writing

            synchronized (app.w_image_running) {
                int objective = 100;
                try {

                    for (int i = 1; i <= objective; i++) {
                        app.folding_estimated(selectedFigure);

                        String filename = file.getPath();
                        if (filename.contains(".")) {
                            String extension = filename.substring(filename.lastIndexOf("."));
                            String basename = filename.substring(0, filename.lastIndexOf("."));

                            filename = basename + "_" + selectedFigure.foldedFigure.discovered_fold_cases + extension;
                        }

                        app.fileModel.setExportImageFileName(filename);

                        app.w_image_running.set(true);
                        app.repaintCanvas();

                        try {
                            app.w_image_running.wait();
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

        app.repaintCanvas();
    }
}
