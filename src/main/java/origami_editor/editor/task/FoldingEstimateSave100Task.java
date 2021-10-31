package origami_editor.editor.task;

import origami.crease_pattern.FoldingException;
import origami_editor.editor.App;

import java.io.File;

public class FoldingEstimateSave100Task implements Runnable{
    private final App app;

    public FoldingEstimateSave100Task(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        File file = app.selectExportFile();
        if (file != null) {
            app.OZ.foldedFigure.summary_write_image_during_execution = true;//Meaning during summary writing

            synchronized (app.w_image_running) {
                int objective = 100;
                try {

                    for (int i = 1; i <= objective; i++) {
                        app.folding_estimated();

                        String filename = file.getName();
                        if (filename.contains(".")) {
                            String extension = filename.substring(filename.lastIndexOf("."));
                            String basename = filename.substring(0, filename.lastIndexOf("."));

                            filename = basename + "_" + app.OZ.foldedFigure.discovered_fold_cases + extension;
                        }

                        app.fileModel.setExportImageFileName(filename);

                        app.w_image_running.set(true);
                        app.repaintCanvas();

                        try {
                            app.w_image_running.wait();
                        } catch (InterruptedException e) {
                            return;
                        }

                        if (!app.OZ.foldedFigure.findAnotherOverlapValid) {
                            objective = app.OZ.foldedFigure.discovered_fold_cases;
                        }
                    }
                } catch (InterruptedException | FoldingException e) {
                    app.OZ.foldedFigure.estimated_initialize();
                    System.out.println(e);
                }
            }
            app.OZ.foldedFigure.summary_write_image_during_execution = false;
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.foldedFigure.text_result = app.OZ.foldedFigure.text_result + "     Computation time " + L + " msec.";

        app.repaintCanvas();
    }
}
