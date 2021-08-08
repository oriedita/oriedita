package jp.gr.java_conf.mt777.origami.orihime;

class SubThread extends Thread {
    //変数の宣言
    App orihime_app;

    //スレッド作成時に実行される処理
    public SubThread(App app0) {
        orihime_app = app0;
    }

    public void run() {
        long start = System.currentTimeMillis();

        // -----------------------------------------------------------------
        if (orihime_app.i_sub_mode == 0) {

            orihime_app.oritatami_suitei();
            orihime_app.repaint();
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 1) {

            String fname = orihime_app.selectFileName("file name for Img save");
            if (fname != null) {
                orihime_app.OZ.matome_write_image_jikkoutyuu = true;//まとめ書き出し実行中の意味

                int mokuhyou = 100;

                for (int i = 1; i <= mokuhyou; i++) {
                    orihime_app.oritatami_suitei();
                    orihime_app.fname_and_number = fname + orihime_app.OZ.hakkenn_sita_kazu;//まとめ書き出しに使う。

                    orihime_app.w_image_jikkoutyuu = true;
                    orihime_app.repaint();

                    while (orihime_app.w_image_jikkoutyuu) {// これを入れないと、書き出し画像が抜けることがある。

                        // 10ミリ秒待機する。なお、10が適切かは不明20170611
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (orihime_app.OZ.different_search_flg == 0) {
                        mokuhyou = orihime_app.OZ.hakkenn_sita_kazu;
                    }


                }
                orihime_app.OZ.matome_write_image_jikkoutyuu = false;
            }
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 2) {
            if (orihime_app.i_oritatami_bangou == orihime_app.OZ.hakkenn_sita_kazu) {
                orihime_app.OZ.text_kekka = "Number of found solutions = " + orihime_app.OZ.hakkenn_sita_kazu + "  ";
            }

            int mokuhyou = orihime_app.i_oritatami_bangou;

            while (mokuhyou > orihime_app.OZ.hakkenn_sita_kazu) {
                orihime_app.oritatami_suitei();
                orihime_app.repaint();

                orihime_app.OZ.i_estimated_order = 6;
                if (orihime_app.OZ.different_search_flg == 0) {
                    mokuhyou = orihime_app.OZ.hakkenn_sita_kazu;
                }

            }

            //orihime_ap.OZ.i_suitei_jissi_umu=1;
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 3) {
            orihime_app.es1.ap_check4(orihime_app.d_ap_check4);
            // -----------------------------------------------------------------
        } else if (orihime_app.i_sub_mode == 4) {//二色塗りわけ展開図
            orihime_app.oritatami_suitei_2col();
        }
        // -----------------------------------------------------------------

        long stop = System.currentTimeMillis();
        Long L = stop - start;
        //if(orihime_ap.OZ.i_suitei_jissi_umu==1){orihime_ap.OZ.text_kekka=orihime_ap.OZ.text_kekka+"     Computation time " +L.toString() + " msec.";}
        orihime_app.OZ.text_kekka = orihime_app.OZ.text_kekka + "     Computation time " + L.toString() + " msec.";

        orihime_app.i_SubThread = 0;
        orihime_app.repaint();
    }
}
