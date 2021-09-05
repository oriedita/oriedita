package origami_editor.editor;

import origami_editor.editor.folded_figure.FoldedFigure;

class SubThread extends Thread {
    //Variable declaration
    App app;

    //Process executed when thread is created
    public SubThread(App app0) {
        app = app0;
    }

    public void run() {
        long start = System.currentTimeMillis();

        switch (app.subThreadMode) {
            case FOLDING_ESTIMATE_0:
                app.folding_estimated();
                app.repaint();
                break;
            case FOLDING_ESTIMATE_SAVE_100_1:
                String fname = app.selectFileName("file name for Img save");
                if (fname != null) {
                    app.OZ.summary_write_image_during_execution = true;//Meaning during summary writing

                    synchronized (app.w_image_running) {
                        int objective = 100;

                        for (int i = 1; i <= objective; i++) {
                            app.folding_estimated();
                            app.fname_and_number = fname + app.OZ.discovered_fold_cases;//Used for bulk writing.

                            app.w_image_running.set(true);
                            app.repaint();

                            try {
                                app.w_image_running.wait();
                            } catch (InterruptedException e) {
                                return;
                            }

                            if (!app.OZ.findAnotherOverlapValid) {
                                objective = app.OZ.discovered_fold_cases;
                            }
                        }
                    }
                    app.OZ.summary_write_image_during_execution = false;
                }
                break;
            case FOLDING_ESTIMATE_SPECIFIC_2:
                if (app.foldedCases == app.OZ.discovered_fold_cases) {
                    app.OZ.text_result = "Number of found solutions = " + app.OZ.discovered_fold_cases + "  ";
                }
                int objective = app.foldedCases;
                while (objective > app.OZ.discovered_fold_cases) {
                    app.folding_estimated();
                    app.repaint();

                    app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;
                    if (!app.OZ.findAnotherOverlapValid) {
                        objective = app.OZ.discovered_fold_cases;
                    }
                }
                break;
            case CHECK_CAMV_3:
                app.mainDrawingWorker.ap_check4(app.d_ap_check4);
                break;
            //Two-color crease pattern
            case TWO_COLORED_4:
                app.createTwoColorCreasePattern();
                break;
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.text_result = app.OZ.text_result + "     Computation time " + L + " msec.";

        app.subThreadRunning = false;
        app.repaint();
    }

    enum Mode {
        /**
         * Execution of folding estimate 5. It is not a mode to put out different solutions of folding estimation at once.
         */
        FOLDING_ESTIMATE_0,
        /**
         * Execution of folding estimate 5. Another solution for folding estimation is put together.
         * <p>
         * Saves 100 image files
         */
        FOLDING_ESTIMATE_SAVE_100_1,
        FOLDING_ESTIMATE_SPECIFIC_2,
        CHECK_CAMV_3,

        /**
         * Two-color crease pattern
         */
        TWO_COLORED_4,
    }
}
