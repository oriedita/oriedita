package origami_editor.editor;

import origami_editor.editor.oriagari_zu.FoldedFigure;

class SubThread extends Thread {
    //Variable declaration
    App app;

    //Process executed when thread is created
    public SubThread(App app0) {
        app = app0;
    }

    enum Mode {
        /**
         * Execution of folding estimate 5. It is not a mode to put out different solutions of folding estimation at once.
         */
        FOLDING_ESTIMATE_0,
        /**
         * Execution of folding estimate 5. Another solution for folding estimation is put together.
         *
         * Saves 100 image files
         */
        FOLDING_ESTIMATE_SAVE_100_1,
        FOLDING_ESTIMATE_SPECIFIC_2,
        CHECK_CAMV_3,

        /**
         * Two-color development drawing
         */
        TWO_COLORED_4,
    }

    public void run() {
        long start = System.currentTimeMillis();

        //Two-color development drawing
        switch (app.subThreadMode) {
            case FOLDING_ESTIMATE_0:
                app.folding_estimated();
                app.repaint();
                break;
            case FOLDING_ESTIMATE_SAVE_100_1:
                String fname = app.selectFileName("file name for Img save");
                if (fname != null) {
                    app.OZ.summary_write_image_during_execution = true;//まとめ書き出し実行中の意味

                    int objective = 100;

                    for (int i = 1; i <= objective; i++) {
                        app.folding_estimated();
                        app.fname_and_number = fname + app.OZ.discovered_fold_cases;//まとめ書き出しに使う。

                        app.w_image_running = true;
                        app.repaint();

                        while (app.w_image_running) {// If this is not included, the exported image may be omitted.
                            // Wait 10 milliseconds. In addition, it is unknown whether 10 is appropriate 20170611
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                            }
                        }
                        if (!app.OZ.findAnotherOverlapValid) {
                            objective = app.OZ.discovered_fold_cases;
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
                app.es1.ap_check4(app.d_ap_check4);
                break;
            case TWO_COLORED_4:
                app.folding_settings_two_color();
                break;
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.text_result = app.OZ.text_result + "     Computation time " + L + " msec.";

        app.subThreadRunning = false;
        app.repaint();
    }
}
