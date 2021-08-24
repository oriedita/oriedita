package jp.gr.java_conf.mt777.origami.orihime;

class SubThread extends Thread {
    //Variable declaration
    App orihime_app;

    //Process executed when thread is created
    public SubThread(App app0) {
        orihime_app = app0;
    }

    public void run() {
        long start = System.currentTimeMillis();

        // -----------------------------------------------------------------
        if (orihime_app.i_sub_mode == 0) {

            orihime_app.folding_estimated();
            orihime_app.repaint();
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 1) {

            String fname = orihime_app.selectFileName("file name for Img save");
            if (fname != null) {
                orihime_app.OZ.matome_write_image_jikkoutyuu = true;//まとめ書き出し実行中の意味

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
                orihime_app.OZ.matome_write_image_jikkoutyuu = false;
            }
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 2) {
            if (orihime_app.i_folded_cases == orihime_app.OZ.discovered_fold_cases) {
                orihime_app.OZ.text_kekka = "Number of found solutions = " + orihime_app.OZ.discovered_fold_cases + "  ";
            }

            int objective = orihime_app.i_folded_cases;

            while (objective > orihime_app.OZ.discovered_fold_cases) {
                orihime_app.folding_estimated();
                orihime_app.repaint();

                orihime_app.OZ.i_estimated_order = 6;
                if (!orihime_app.OZ.findAnotherOverlapValid) {
                    objective = orihime_app.OZ.discovered_fold_cases;
                }

            }

            //orihime_ap.OZ.i_suitei_jissi_umu=1;
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 3) {
            orihime_app.es1.ap_check4(orihime_app.d_ap_check4);
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 4) {//Two-color development drawing
            orihime_app.folding_settings_two_color();
        }
        // -----------------------------------------------------------------

        long stop = System.currentTimeMillis();
        long L = stop - start;
        orihime_app.OZ.text_kekka = orihime_app.OZ.text_kekka + "     Computation time " + L + " msec.";

        orihime_app.i_SubThread = 0;
        orihime_app.repaint();
    }
}
