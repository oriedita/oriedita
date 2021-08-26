package jp.gr.java_conf.mt777.origami.orihime;

import jp.gr.java_conf.mt777.origami.orihime.oriagari_zu.FoldedFigure;

class SubThread extends Thread {
    //Variable declaration
    App orihime_app;

    //Process executed when thread is created
    public SubThread(App app0) {
        orihime_app = app0;
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

        switch (orihime_app.subThreadMode) {
            case FOLDING_ESTIMATE_0 -> {
                orihime_app.folding_estimated();
                orihime_app.repaint();
            }

            case FOLDING_ESTIMATE_SAVE_100_1 -> {
                String fname = orihime_app.selectFileName("file name for Img save");
                if (fname != null) {
                    orihime_app.OZ.summary_write_image_during_execution = true;//まとめ書き出し実行中の意味

                    int objective = 100;

                    for (int i = 1; i <= objective; i++) {
                        orihime_app.folding_estimated();
                        orihime_app.fname_and_number = fname + orihime_app.OZ.discovered_fold_cases;//まとめ書き出しに使う。

                        orihime_app.w_image_running = true;
                        orihime_app.repaint();

                        while (orihime_app.w_image_running) {// If this is not included, the exported image may be omitted.
                            // Wait 10 milliseconds. In addition, it is unknown whether 10 is appropriate 20170611
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                            }
                        }
                        if (!orihime_app.OZ.findAnotherOverlapValid) {
                            objective = orihime_app.OZ.discovered_fold_cases;
                        }
                    }
                    orihime_app.OZ.summary_write_image_during_execution = false;
                }
            }

            case FOLDING_ESTIMATE_SPECIFIC_2 -> {
                if (orihime_app.i_folded_cases == orihime_app.OZ.discovered_fold_cases) {
                    orihime_app.OZ.text_result = "Number of found solutions = " + orihime_app.OZ.discovered_fold_cases + "  ";
                }
                int objective = orihime_app.i_folded_cases;
                while (objective > orihime_app.OZ.discovered_fold_cases) {
                    orihime_app.folding_estimated();
                    orihime_app.repaint();

                    orihime_app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;
                    if (!orihime_app.OZ.findAnotherOverlapValid) {
                        objective = orihime_app.OZ.discovered_fold_cases;
                    }
                }
            }

            case CHECK_CAMV_3 -> orihime_app.es1.ap_check4(orihime_app.d_ap_check4);

            //Two-color development drawing
            case TWO_COLORED_4 -> orihime_app.folding_settings_two_color();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        orihime_app.OZ.text_result = orihime_app.OZ.text_result + "     Computation time " + L + " msec.";

        orihime_app.subThreadRunning = false;
        orihime_app.repaint();
    }
}
