package jp.gr.java_conf.mt777.origami.dougu.keijiban;

import jp.gr.java_conf.mt777.origami.orihime.*;

import java.awt.*;

public class Keijiban {

    int ix0 = 120;//掲示板の左上端のX座標
    int iy0 = 200;//掲示板の左上端のY座標
    int i_kankaku = 20;//掲示板の行の間隔

    String s01 = "";
    String s02 = "";
    String s03 = "";
    String s04 = "";
    String s05 = "";
    String s06 = "";
    String s07 = "";
    String s08 = "";
    String s09 = "";
    String s10 = "";
    App orihime_app;


    //コンストラクタ
    public Keijiban(App app0) {

        orihime_app = app0;
    }

    //入力
    public void set_s01(String s0) {
        s01 = s0;
    }

    public void set_s02(String s0) {
        s02 = s0;
    }

    public void set_s03(String s0) {
        s03 = s0;
    }

    public void set_s04(String s0) {
        s04 = s0;
    }

    public void set_s05(String s0) {
        s05 = s0;
    }

    public void set_s06(String s0) {
        s06 = s0;
    }

    public void set_s07(String s0) {
        s07 = s0;
    }

    public void set_s08(String s0) {
        s08 = s0;
    }

    public void set_s09(String s0) {
        s09 = s0;
    }

    public void set_s10(String s0) {
        s10 = s0;
    }


    public void tuiki(String s0) {
        s01 = s02;
        s02 = s03;
        s03 = s04;
        s04 = s05;
        s05 = s06;
        s06 = s07;
        s07 = s08;
        s08 = s09;
        s09 = s10;
        s10 = s0;
        repaint();
    }

    public void kakikae(int i, String s0) {
        if (i == 1) {
            s01 = s0;
        }
        if (i == 2) {
            s02 = s0;
        }
        if (i == 3) {
            s03 = s0;
        }
        if (i == 4) {
            s04 = s0;
        }
        if (i == 5) {
            s05 = s0;
        }
        if (i == 6) {
            s06 = s0;
        }
        if (i == 7) {
            s07 = s0;
        }
        if (i == 8) {
            s08 = s0;
        }
        if (i == 9) {
            s09 = s0;
        }
        if (i == 10) {
            s10 = s0;
        }
        repaint();
    }

    public void clear() {

        s01 = "";
        s02 = "";
        s03 = "";
        s04 = "";
        s05 = "";
        s06 = "";
        s07 = "";
        s08 = "";
        s09 = "";
        s10 = "";

        repaint();

    }

    //出力
    //public int get(){return 1;}


    public void reset() {
        ;
    }


    public void repaint() {
        orihime_app.repaint();
    }


    //
    public void keiji(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(Color.blue);
        g.drawString(s01, ix0, iy0 + 1 * i_kankaku);
        g.drawString(s02, ix0, iy0 + 2 * i_kankaku);
        g.drawString(s03, ix0, iy0 + 3 * i_kankaku);
        g.drawString(s04, ix0, iy0 + 4 * i_kankaku);
        g.drawString(s05, ix0, iy0 + 5 * i_kankaku);
        g.drawString(s06, ix0, iy0 + 6 * i_kankaku);
        g.drawString(s07, ix0, iy0 + 7 * i_kankaku);
        g.drawString(s08, ix0, iy0 + 8 * i_kankaku);
        g.drawString(s09, ix0, iy0 + 9 * i_kankaku);
        g.drawString(s10, ix0, iy0 + 10 * i_kankaku);

    }

}
