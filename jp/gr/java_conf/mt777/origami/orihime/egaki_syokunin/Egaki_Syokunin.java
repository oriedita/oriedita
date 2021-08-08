package jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin;

import jp.gr.java_conf.mt777.origami.orihime.*;
import jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin.egaki_syokunin_dougubako.*;
import jp.gr.java_conf.mt777.origami.dougu.linestore.*;
import jp.gr.java_conf.mt777.origami.dougu.orisensyuugou.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;
import jp.gr.java_conf.mt777.origami.orihime.undo_box.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.math.BigDecimal;

import jp.gr.java_conf.mt777.kiroku.memo.*;
import jp.gr.java_conf.mt777.seiretu.narabebako.*;
import jp.gr.java_conf.mt777.zukei2d.en.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;
import jp.gr.java_conf.mt777.zukei2d.oritaoekaki.*;
import jp.gr.java_conf.mt777.zukei2d.kousi.*;
import jp.gr.java_conf.mt777.zukei2d.takakukei.Polygon;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;


// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Egaki_Syokunin {
    OritaCalc oc = new OritaCalc(); //各種計算用の関数を使うためのクラスのインスタンス化
    //double r_ten=3.0;                   //基本枝構造の直線の両端の円の半径、枝と各種ポイントの近さの判定基準
    int ir_ten = 1;

    int icol;//線分の色
    int h_icol = 4;//補助線の色

    //int taisyousei;

    int i_hanasi = 0;//マウス位置と入力点の座標を離すなら１、離さないなら０
    int i_kou_mitudo_nyuuryoku = 0;//格子表示が細かい場合用の入力補助機能を使うなら１、使わないなら０

    Point pa = new Point(); //マウスボタンが押された位置からa点までのベクトル
    Point pb = new Point(); //マウスボタンが押された位置からb点までのベクトル

    Color sen_tokutyuu_color;//円や補助活線の特注色を格納


    Undo_Box Ubox = new Undo_Box();
    Undo_Box h_Ubox = new Undo_Box();

    Point moyori_point = new Point(100000.0, 100000.0); //マウス最寄の点。get_moyori_ten(Ten p)で求める。
    Line moyori_line = new Line(100000.0, 100000.0, 100000.0, 100000.1); //マウス最寄の線分
    Line moyori_step_line = new Line(100000.0, 100000.0, 100000.0, 100000.1); //マウス最寄のstep線分(線分追加のための準備をするための線分)。なお、ここで宣言する必要はないので、どこで宣言すべきか要検討20161113
    Circle moyori_ensyuu = new Circle(100000.0, 100000.0, 10.0, 8); //マウス最寄の円周を持つ円
    Circle moyori_step_ensyuu = new Circle(100000.0, 100000.0, 10.0, 8); //マウス最寄の円周を持つstep円

    int i_orisen_hojyosen = 0;//=0は折線入力　=1は補助線入力モード(線分入力時はこの２つ)。線分削除時は更に値が以下の様になる。=0は折線の削除、=1は補助絵線削除、=2は黒線削除、=3は補助活線削除、=4は折線と補助活線と補助絵線

    int ugokasi_mode = 0;    //枝を動かす動作モード。0=なにもしない、1=a点を動かす、2=b点を動かす、3=枝を平行移動 、4=新規追加
    int ieda;              //アクティブな枝の番号

    //  int i_saigo_no_senbun_no_maru_kaku=1;	//1描く、0描かない

    public PolygonStore ori_s = new PolygonStore();    //折線を格納する
    PolygonStore hoj_s = new PolygonStore();    //補助線を格納する


    public PolygonStore ori_v = new PolygonStore();    //ボロノイ図の線を格納する

    Egaki_Syokunin_Dougubako e_s_dougubako = new Egaki_Syokunin_Dougubako(ori_s);

    private final LineStore sen_s = new LineStore();    //基本枝構造のインスタンス化
    //private Senbunsyuugou sen_s;    //基本枝構造のインスタンス化

    Polygon gomibako = new Polygon(4);    //ゴミ箱のインスタンス化
    Polygon tyuuoutai = new Polygon(4);    //中央帯のインスタンス化
    double tyuuoutai_xmin = 180.0;
    double tyuuoutai_xmax = 206.0;
    double tyuuoutai_ymin = 50.0;
    double tyuuoutai_ymax = 300.0;

    double kijyun_kakudo = 22.5; //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //int i_kakudo_kei=36;
    int id_kakudo_kei = 8;//  180/id_kakudo_keiが角度系を表す。たとえば、id_kakudo_kei=3なら180/3＝60度系、id_kakudo_kei=5なら180/5＝36度系
    //360/i_kakudo_kei-1 = id_kakudo_kei*2-1

    double d_kakudo_kei;//d_kakudo_kei=180.0/(double)id_kakudo_kei
    double kakudo;

    //入力方法用のパラメータ
    int nyuuryoku_houhou = 0;
    //int nyuuryoku_kitei=0;

    int i_orisen_bunkatu_suu = 1;
    double d_naibun_s;
    double d_naibun_t;

    double d_jiyuu_kaku_1;
    double d_jiyuu_kaku_2;
    double d_jiyuu_kaku_3;

    int i_sei_takakukei = 5;

    int kensa_houhou = 0;
    int nhi = 0;


    Point nhPoint = new Point();
    Point nhPoint1 = new Point();

    public Kousi kus = new Kousi();

    double d_hantei_haba = 50.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<入力点が既存の点や線分と近いかどうかを判定する時の値


    public int i_egaki_dankai;//折線を描く手順のどの段階にいるかの情報を格納
    int i_en_egaki_dankai;//円を描く手順のどの段階にいるかの情報を格納
    Line[] s_step = new Line[1024];//描画時の一時表示用に使う。s_step[0] は実際は使わず、s_step[1]から使う。
    Circle[] e_step = new Circle[1024];//描画時の一時表示用に使う。e_step[0] は実際は使わず、e_step[1]から使う。


    public int i_kouho_dankai;//折線を描く手順のどの候補かの情報を格納
    int i_en_kouho_dankai;//円を描く手順のどの候補かの情報を格納
    Line[] s_kouho = new Line[16];//描画時の選択候補表示用に使う。s_kouho[0] は実際は使わず、s_kouho[1]から使う。
    Circle[] e_kouho = new Circle[16];//描画時の選択候補表示用に使う。e_kouho[0] は実際は使わず、e_kouho[1]から使う。


    double sokutei_nagasa_1 = 0.0;
    double sokutei_nagasa_2 = 0.0;
    double sokutei_nagasa_3 = 0.0;
    double sokutei_kakudo_1 = 0.0;
    double sokutei_kakudo_2 = 0.0;
    double sokutei_kakudo_3 = 0.0;


    String text_cp_setumei;
    String text_cp_setumei2;

    String s_title; //フレームの最上端に出てくるタイトルを保持するために使用

    Camera camera = new Camera();

    int i_check1 = 0;//=0 check1を実施しない、1=実施する　　
    int i_check2 = 0;//=0 check2を実施しない、1=実施する　
    int i_check3 = 0;//=0 check3を実施しない、1=実施する　
    int i_check4 = 0;//=0 check4を実施しない、1=実施する　
    //---------------------------------
    int i_ck4_color_toukado = 100;

    App orihime_app;


    int icol_temp = 0;//色指定の一時的な記憶に使う


    //i_mouse_modeA==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
    Point p61_1 = new Point();//TV座標
    Point p61_2 = new Point();//TV座標
    Point p61_3 = new Point();//TV座標
    Point p61_4 = new Point();//TV座標
    public Polygon p61_TV_hako = new Polygon(4);    //選択箱(TV座標)のインスタンス化
    int p61_mode = 0;//=1 新たに選択箱を作る。=2　点を移動。３　辺を移動。４　選択箱を移動。


    Point p = new Point();

    public int i_O_F_C = 0;//外周部チェック時の外周を表す線分の入力状況。0は入力未完了、1は入力完了（線分が閉多角形になっている。）

// ****************************************************************************************************************************************
// **************　ここまで変数の定義　****************************************************************************************************
// ****************************************************************************************************************************************

    public Egaki_Syokunin(double r0, App app0) {  //コンストラクタ

        orihime_app = app0;

        //r_ten=r0;
        ugokasi_mode = 0;
        ieda = 0;
        icol = 0;
        gomibako.set(new Point(10.0, 150.0), 1, new Point(0.0, 0.0));
        gomibako.set(new Point(10.0, 150.0), 2, new Point(50.0, 0.0));
        gomibako.set(new Point(10.0, 150.0), 3, new Point(40.0, 50.0));
        gomibako.set(new Point(10.0, 150.0), 4, new Point(10.0, 50.0));

        tyuuoutai.set(1, new Point(tyuuoutai_xmin, tyuuoutai_ymin));
        tyuuoutai.set(2, new Point(tyuuoutai_xmax, tyuuoutai_ymin));
        tyuuoutai.set(3, new Point(tyuuoutai_xmax, tyuuoutai_ymax));
        tyuuoutai.set(4, new Point(tyuuoutai_xmin, tyuuoutai_ymax));

        //taisyousei=0;

        for (int i = 0; i <= 1024 - 1; i++) {
            s_step[i] = new Line();
        }
        for (int i = 0; i <= 1024 - 1; i++) {
            e_step[i] = new Circle();
        }

        for (int i = 0; i <= 16 - 1; i++) {
            s_kouho[i] = new Line();
        }
        for (int i = 0; i <= 16 - 1; i++) {
            e_kouho[i] = new Circle();
        }

        text_cp_setumei = "1/";
        text_cp_setumei2 = " ";
        s_title = "no title";

        reset();
        //reset_2();

    }


    //---------------------------------
    public void reset() {
        //r_ten=2.0;
        ir_ten = 1;
        ori_s.reset();
        hoj_s.reset();
        ugokasi_mode = 0;
        ieda = 0;
        //icol=0;
        //taisyousei=0;

        camera.reset();
        i_egaki_dankai = 0;
        i_en_egaki_dankai = 0;

    }

    public void reset_2() {
        //用紙の正方形を入力（開始）
        ori_s.addsenbun(-200.0, -200.0, -200.0, 200.0, 0);
        ori_s.addsenbun(-200.0, -200.0, 200.0, -200.0, 0);
        ori_s.addsenbun(200.0, 200.0, -200.0, 200.0, 0);
        ori_s.addsenbun(200.0, 200.0, 200.0, -200.0, 0);
        //用紙の正方形を入力（終了）


    }


    // -------------------------------------------
    public void sokutei_hyouji() {

        orihime_app.sokutei_nagasa_1_hyouji(sokutei_nagasa_1);
        orihime_app.sokutei_nagasa_2_hyouji(sokutei_nagasa_2);

        orihime_app.sokutei_kakudo_1_hyouji(sokutei_kakudo_1);
        orihime_app.sokutei_kakudo_2_hyouji(sokutei_kakudo_2);
        orihime_app.sokutei_kakudo_3_hyouji(sokutei_kakudo_3);
    }


    //------------------------------------
    public void Memo_jyouhou_toridasi(Memo memo1) {

        int i_yomi = 0;
        String[] st;
        String[] s;

        // 展開図用カメラ設定の読み込み
        i_yomi = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {
            String str = memo1.getLine(i);
            str.trim();

            if (str.equals("<camera_of_orisen_nyuuryokuzu>")) {
                i_yomi = 1;
            } else if (str.equals("</camera_of_orisen_nyuuryokuzu>")) {
                i_yomi = 0;
            } else {
                if (i_yomi == 1) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定
                    //System.out.println(st[0]+"[___________["+st[1]);
                    if (st[0].equals("<camera_ichi_x")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_camera_ichi_x(Double.parseDouble(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<camera_ichi_y")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_camera_ichi_y(Double.parseDouble(s[0]));
                    }
                    if (st[0].equals("<camera_kakudo")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_camera_kakudo(Double.parseDouble(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<camera_kagami")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_camera_kagami(Double.parseDouble(s[0]));
                    }

                    if (st[0].equals("<camera_bairitsu_x")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_camera_bairitsu_x(Double.parseDouble(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<camera_bairitsu_y")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_camera_bairitsu_y(Double.parseDouble(s[0]));
                    }

                    if (st[0].equals("<hyouji_ichi_x")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_hyouji_ichi_x(Double.parseDouble(s[0]));
                    }
                    if (st[0].equals("<hyouji_ichi_y")) {
                        s = st[1].split("<", 2);
                        orihime_app.camera_of_orisen_nyuuryokuzu.set_hyouji_ichi_y(Double.parseDouble(s[0]));
                    }
                }
            }
        }


        // ----------------------------------------- チェックボックス等の設定の読み込み
        i_yomi = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {
            String str = memo1.getLine(i);
            str.trim();

            if (str.equals("<settei>")) {
                i_yomi = 1;
            } else if (str.equals("</settei>")) {
                i_yomi = 0;
            } else {
                if (i_yomi == 1) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定


                    if (st[0].equals("<ckbox_mouse_settei")) {
                        s = st[1].split("<", 2);

						if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_mouse_settei.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_mouse_settei.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_ten_sagasi")) {
                        s = st[1].split("<", 2);
                        s[0].trim();


                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_ten_sagasi.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_ten_sagasi.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_ten_hanasi")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_ten_hanasi.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_ten_hanasi.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_kou_mitudo_nyuuryoku")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_kou_mitudo_nyuuryoku.setSelected(true);
                            set_i_kou_mitudo_nyuuryoku(1);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_kou_mitudo_nyuuryoku.setSelected(false);
                            set_i_kou_mitudo_nyuuryoku(0);
                        }
                    }

                    if (st[0].equals("<ckbox_bun")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_bun.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_bun.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_cp")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_cp.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_cp.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_a0")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_a0.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_a0.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_a1")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_a1.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_a1.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_mejirusi")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_mejirusi.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_mejirusi.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_cp_ue")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_cp_ue.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_cp_ue.setSelected(false);
                        }
                    }

                    if (st[0].equals("<ckbox_oritatami_keika")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        if (s[0].trim().equals("true")) {
                            orihime_app.ckbox_oritatami_keika.setSelected(true);
                        }
                        if (s[0].trim().equals("false")) {
                            orihime_app.ckbox_oritatami_keika.setSelected(false);
                        }
                    }


                    if (st[0].equals("<iTenkaizuSenhaba")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        orihime_app.iTenkaizuSenhaba = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;


                    if (st[0].equals("<ir_ten")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        orihime_app.ir_ten = (Integer.parseInt(s[0]));
                        set_ir_ten(orihime_app.ir_ten);
                    }        //  System.out.println(Integer.parseInt(s[0])) ;


                    if (st[0].equals("<i_orisen_hyougen")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        orihime_app.i_orisen_hyougen = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;


                    if (st[0].equals("<i_anti_alias")) {
                        s = st[1].split("<", 2);
                        s[0].trim();
                        orihime_app.i_anti_alias = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;


                }
            }
        }


        // ----------------------------------------- 格子設定の読み込み


        i_yomi = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {
            String str = memo1.getLine(i);
            str.trim();

            if (str.equals("<Kousi>")) {
                i_yomi = 1;
            } else if (str.equals("</Kousi>")) {
                i_yomi = 0;
            } else {
                if (i_yomi == 1) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定

                    if (st[0].equals("<i_kitei_jyoutai")) {
                        s = st[1].split("<", 2);
                        set_i_kitei_jyoutai(Integer.parseInt(s[0]));
                    }
                    //  System.out.println(Integer.parseInt(s[0])) ;

                    if (st[0].equals("<nyuuryoku_kitei")) {
                        s = st[1].split("<", 2);
                        orihime_app.text1.setText(s[0]);
                        orihime_app.set_kousi_bunkatu_suu();
                        //set_kousi_bunkatu_suu(Integer.parseInt(s[0]));

                    }
                    //  System.out.println(Integer.parseInt(s[0])) ;


                    if (st[0].equals("<memori_kankaku")) {
                        s = st[1].split("<", 2);
                        orihime_app.memori_kankaku = Integer.parseInt(s[0]);
                        orihime_app.text25.setText(s[0]);

                        set_a_to_heikouna_memori_kannkaku(orihime_app.memori_kankaku);
                        set_b_to_heikouna_memori_kannkaku(orihime_app.memori_kankaku);
                    }


                    if (st[0].equals("<a_to_heikouna_memori_iti")) {
                        s = st[1].split("<", 2);
                        kus.set_a_to_heikouna_memori_iti(Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<b_to_heikouna_memori_iti")) {
                        s = st[1].split("<", 2);
                        kus.set_b_to_heikouna_memori_iti(Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_senhaba")) {
                        s = st[1].split("<", 2);
                        kus.set_kousi_senhaba(Integer.parseInt(s[0]));
                    }


                    if (st[0].equals("<d_kousi_x_a")) {
                        s = st[1].split("<", 2);
                        orihime_app.text18.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_x_b")) {
                        s = st[1].split("<", 2);
                        orihime_app.text19.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_x_c")) {
                        s = st[1].split("<", 2);
                        orihime_app.text20.setText(s[0]);
                    }

                    if (st[0].equals("<d_kousi_y_a")) {
                        s = st[1].split("<", 2);
                        orihime_app.text21.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_y_b")) {
                        s = st[1].split("<", 2);
                        orihime_app.text22.setText(s[0]);
                    }
                    if (st[0].equals("<d_kousi_y_c")) {
                        s = st[1].split("<", 2);
                        orihime_app.text23.setText(s[0]);
                    }

                    if (st[0].equals("<d_kousi_kakudo")) {
                        s = st[1].split("<", 2);
                        orihime_app.text24.setText(s[0]);
                    }

                    orihime_app.set_kousi();
/*
		memo1.addGyou("<d_kousi_x_a>"+orihime_ap.d_kousi_x_a+"</d_kousi_x_a>");
		memo1.addGyou("<d_kousi_x_b>"+orihime_ap.d_kousi_x_b+"</d_kousi_x_b>");
		memo1.addGyou("<d_kousi_x_c>"+orihime_ap.d_kousi_x_c+"</d_kousi_x_c>");
		memo1.addGyou("<d_kousi_y_a>"+orihime_ap.d_kousi_y_a+"</d_kousi_y_a>");
		memo1.addGyou("<d_kousi_y_b>"+orihime_ap.d_kousi_y_b+"</d_kousi_y_b>");
		memo1.addGyou("<d_kousi_y_c>"+orihime_ap.d_kousi_y_c+"</d_kousi_y_c>");
		memo1.addGyou("<d_kousi_kakudo>"+orihime_ap.d_kousi_kakudo+"</d_kousi_kakudo>");
		memo1.addGyou("<d_kousi_x_nagasa>"+orihime_ap.d_kousi_x_nagasa+"</d_kousi_x_nagasa>");
		memo1.addGyou("<d_kousi_y_nagasa>"+orihime_ap.d_kousi_y_nagasa+"</d_kousi_y_nagasa>");

*/

                }
            }
        }


        // ----------------------------------------- 格子色設定の読み込み
        int i_kousi_color_R = 0;
        int i_kousi_color_G = 0;
        int i_kousi_color_B = 0;
        int i_kousi_memori_color_R = 0;
        int i_kousi_memori_color_G = 0;
        int i_kousi_memori_color_B = 0;

        int i_Kousi_iro_yomikomi = 0;//Kousi_iroの読み込みがあったら1、なければ0
        i_yomi = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {
            String str = memo1.getLine(i);
            str.trim();

            if (str.equals("<Kousi_iro>")) {
                i_yomi = 1;
                i_Kousi_iro_yomikomi = 1;
            } else if (str.equals("</Kousi_iro>")) {
                i_yomi = 0;
            } else {
                if (i_yomi == 1) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定


                    if (st[0].equals("<kousi_color_R")) {
                        s = st[1].split("<", 2);
                        i_kousi_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<kousi_color_G")) {
                        s = st[1].split("<", 2);
                        i_kousi_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_color_B")) {
                        s = st[1].split("<", 2);
                        i_kousi_color_B = (Integer.parseInt(s[0]));
                    }

                    if (st[0].equals("<kousi_memori_color_R")) {
                        s = st[1].split("<", 2);
                        i_kousi_memori_color_R = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_memori_color_G")) {
                        s = st[1].split("<", 2);
                        i_kousi_memori_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<kousi_memori_color_B")) {
                        s = st[1].split("<", 2);
                        i_kousi_memori_color_B = (Integer.parseInt(s[0]));
                    }


                }
            }
        }

        if (i_Kousi_iro_yomikomi == 1) {//Kousi_iroの読み込みがあったら1、なければ0
            kus.set_kousi_color(new Color(i_kousi_color_R, i_kousi_color_G, i_kousi_color_B)); //kousiの色

            System.out.println("i_kousi_memori_color_R= " + i_kousi_memori_color_R);
            System.out.println("i_kousi_memori_color_G= " + i_kousi_memori_color_G);
            System.out.println("i_kousi_memori_color_B= " + i_kousi_memori_color_B);
            orihime_app.kus.set_kousi_memori_color(new Color(i_kousi_memori_color_R, i_kousi_memori_color_G, i_kousi_memori_color_B)); //kousi_memoriの色

        }


        // 折り上がり図設定の読み込み -------------------------------------------------------------------------

        int i_oriagarizu_F_color_R = 0;
        int i_oriagarizu_F_color_G = 0;
        int i_oriagarizu_F_color_B = 0;

        int i_oriagarizu_B_color_R = 0;
        int i_oriagarizu_B_color_G = 0;
        int i_oriagarizu_B_color_B = 0;

        int i_oriagarizu_L_color_R = 0;
        int i_oriagarizu_L_color_G = 0;
        int i_oriagarizu_L_color_B = 0;


        int i_oriagarizu_yomikomi = 0;//oriagarizuの読み込みがあったら1、なければ0
        i_yomi = 0;
        for (int i = 1; i <= memo1.getLineSize(); i++) {
            String str = memo1.getLine(i);
            str.trim();

            if (str.equals("<oriagarizu>")) {
                i_yomi = 1;
                i_oriagarizu_yomikomi = 1;
            } else if (str.equals("</oriagarizu>")) {
                i_yomi = 0;
            } else {
                if (i_yomi == 1) {
                    st = str.split(">", 2);// <-----------------------------------２つに分割するときは2を指定

                    if (st[0].equals("<oriagarizu_F_color_R")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_F_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<oriagarizu_F_color_G")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_F_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<oriagarizu_F_color_B")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_F_color_B = (Integer.parseInt(s[0]));
                    }

                    if (st[0].equals("<oriagarizu_B_color_R")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_B_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<oriagarizu_B_color_G")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_B_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<oriagarizu_B_color_B")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_B_color_B = (Integer.parseInt(s[0]));
                    }

                    if (st[0].equals("<oriagarizu_L_color_R")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_L_color_R = (Integer.parseInt(s[0]));
                    }        //  System.out.println(Integer.parseInt(s[0])) ;
                    if (st[0].equals("<oriagarizu_L_color_G")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_L_color_G = (Integer.parseInt(s[0]));
                    }
                    if (st[0].equals("<oriagarizu_L_color_B")) {
                        s = st[1].split("<", 2);
                        i_oriagarizu_L_color_B = (Integer.parseInt(s[0]));
                    }

                    //  System.out.println(Integer.parseInt(s[0])) ;
                }
            }
        }

        if (i_oriagarizu_yomikomi == 1) {
            orihime_app.OZ.js.set_F_color(new Color(i_oriagarizu_F_color_R, i_oriagarizu_F_color_G, i_oriagarizu_F_color_B)); //表面の色
            orihime_app.Button_F_color.setBackground(new Color(i_oriagarizu_F_color_R, i_oriagarizu_F_color_G, i_oriagarizu_F_color_B));    //ボタンの色設定

            orihime_app.OZ.js.set_B_color(new Color(i_oriagarizu_B_color_R, i_oriagarizu_B_color_G, i_oriagarizu_B_color_B));//裏面の色
            orihime_app.Button_B_color.setBackground(new Color(i_oriagarizu_B_color_R, i_oriagarizu_B_color_G, i_oriagarizu_B_color_B));//ボタンの色設定

            orihime_app.OZ.js.set_L_color(new Color(i_oriagarizu_L_color_R, i_oriagarizu_L_color_G, i_oriagarizu_L_color_B));        //線の色
            orihime_app.Button_L_color.setBackground(new Color(i_oriagarizu_L_color_R, i_oriagarizu_L_color_G, i_oriagarizu_L_color_B));        //ボタンの色設定
        }
    }

    //-----------------------------
    public String setMemo_for_redo_undo(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用

        //Memo_jyouhou_toridasi(memo1);
        return ori_s.setMemo(memo1);
    }


    //-----------------------------
    public void setMemo_for_yomikomi(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<データ読み込み用
        Memo_jyouhou_toridasi(memo1);
        ori_s.setMemo(memo1);
        hoj_s.h_setMemo(memo1);
    }

    //-----------------------------
    public void setMemo_for_yomikomi_tuika(Memo memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<データ読み込み用
        //Memo_jyouhou_toridasi(memo1);
        //ori_s.setMemo(memo1); hoj_s.h_setMemo(memo1);

        double addx, addy;


        PolygonStore ori_s_temp = new PolygonStore();    //追加された折線だけ取り出すために使う
        ori_s_temp.setMemo(memo1);//追加された折線だけ取り出してori_s_tempを作る
        //ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。
        addx = ori_s.get_x_max() + 100.0 - ori_s_temp.get_x_min();
        addy = ori_s.get_y_max() - ori_s_temp.get_y_max();


        ori_s_temp.move(addx, addy);//全体を移動する

        int sousuu_old = ori_s.getsousuu();
        ori_s.addMemo(ori_s_temp.getMemo());
        int sousuu_new = ori_s.getsousuu();
        ori_s.kousabunkatu(1, sousuu_old, sousuu_old + 1, sousuu_new);

        ori_s.unselect_all();
        kiroku();


    }

    //-----------------------------
    public void h_setMemo(Memo memo1) {
        hoj_s.h_setMemo(memo1);
    }


    //-----------------------------
    public void setCamera(Camera cam0) {


        //camera.set_camera_id(cam0.get_camera_id());
        camera.set_camera_kagami(cam0.get_camera_kagami());


        camera.set_camera_ichi_x(cam0.get_camera_ichi_x());
        camera.set_camera_ichi_y(cam0.get_camera_ichi_y());
        camera.set_camera_bairitsu_x(cam0.get_camera_bairitsu_x());
        camera.set_camera_bairitsu_y(cam0.get_camera_bairitsu_y());
        camera.set_camera_kakudo(cam0.get_camera_kakudo());
        camera.set_hyouji_ichi_x(cam0.get_hyouji_ichi_x());
        camera.set_hyouji_ichi_y(cam0.get_hyouji_ichi_y());

        calc_d_hantei_haba();
    }


    public void set_sen_tokutyuu_color(Color c0) {
        sen_tokutyuu_color = c0;
    }


    //-----------------------------
    public void zen_yama_tani_henkan() {
        ori_s.zen_yama_tani_henkan();
    }

    //----------------
    public void eda_kesi(double r) {
        ori_s.eda_kesi(r);
    }
    //--------------------------------------------
    //public void set(Senbunsyuugou ss){ori_s.set(ss);}

    //----------------------------------------------
    public LineStore get() {
        sen_s.setMemo(ori_s.getMemo());
        return sen_s;


    }


    public LineStore get_for_oritatami() {
        sen_s.setMemo(ori_s.getMemo_for_oritatami());
        return sen_s;
    }


    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    public int get_orisensuu_for_select_oritatami() {
        return ori_s.get_orisensuu_for_select_oritatami();
    }

    public LineStore get_for_select_oritatami() {//selectした折線で折り畳み推定をする。
        sen_s.setMemo(ori_s.getMemo_for_select_oritatami());
        return sen_s;
    }


    //--------------------------------------------
    //public void set_r(double r0){r_ten=r0;}
    public void set_ir_ten(int i0) {
        ir_ten = i0;
    }

    //--------------------------------------------
    public void set_kousi_bunkatu_suu(int i) {
        kus.set_kousi_bunkatu_suu(i);
        text_cp_setumei = "1/" + kus.bunsuu();
        calc_d_hantei_haba();
    }

    public void calc_d_hantei_haba() {
        d_hantei_haba = kus.d_haba() / 4.0;
        if (camera.get_camera_bairitsu_x() * d_hantei_haba < 10.0) {
            d_hantei_haba = 10.0 / camera.get_camera_bairitsu_x();
        }
    }


    // ----------------------------------------
    public void set_d_kousi(double dkxn, double dkyn, double dkk) {
        kus.set_d_kousi(dkxn, dkyn, dkk);
    }


    //--------------------------------------------
    public void set_kensa_houhou(int i) {
        kensa_houhou = i;
    }

    //--------------------------------------------
    public int getsousuu() {
        return ori_s.getsousuu();
    }


    //-----------------------------
    public void set_kijyun_kakudo(double x) {
        kijyun_kakudo = x;
    } //<<<<<<<<<<<<<<<<<<<<<<<基準角度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //------------------------
    public Memo getMemo() {
        return ori_s.getMemo();
    }


    //getMemo(String s_title)はundo,redoのkiroku用
    public Memo getMemo(String s_title) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoのkiroku用
        Memo memo_temp = new Memo();
        memo_temp.set(ori_s.getMemo(s_title));

        Memo_jyouhou_tuika(memo_temp);
        return memo_temp;
        //return ori_s.getMemo(s_title);
    }

    //------------------------
    public Memo h_getMemo() {
        return hoj_s.h_getMemo();
    }


    //------------------------
    public Memo getMemo_for_export() {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<データ書き出し

        Memo memo_temp = new Memo();
        memo_temp.set(ori_s.getMemo());
        memo_temp.addMemo(hoj_s.h_getMemo());
        Memo_jyouhou_tuika(memo_temp);
        return memo_temp;
    }

    //------------------------svgデータ書き出し
    public Memo getMemo_for_svg_export_with_camera(int i_bun_hyouji, int i_cp_hyouji, int i_a0_hyouji, int i_a1_hyouji, float fTenkaizuSenhaba, int i_orisen_hyougen, float f_h_TenkaizuSenhaba, int p0x_max, int p0y_max, int i_mejirusi_hyouji) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        Memo memo_temp = new Memo();

        //String text=new String();//文字列処理用のクラスのインスタンス化
        //double d;
        Line s_tv = new Line();
        Point a = new Point();
        Point b = new Point();

        //Senbun s_ob =new Senbun();
        // ------------------------------------------------------

        String str = "";
        String str_stroke = "";
        String str_strokewidth;
        str_strokewidth = Integer.toString(orihime_app.iTenkaizuSenhaba);
        // ------------------------------------------------------


        //展開図の描画  補助活線以外の折線
        if (i_cp_hyouji == 1) {

            //float dash_M1[] = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
            //float dash_M2[] = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
            //float dash_V[]  = {8.0f, 8.0f};//破線

            for (int i = 1; i <= ori_s.getsousuu(); i++) {
                if (ori_s.getcolor(i) <= 3) {


                    if (ori_s.getcolor(i) == 0) {
                        str_stroke = "black";
                    } else if (ori_s.getcolor(i) == 1) {
                        str_stroke = "red";
                    } else if (ori_s.getcolor(i) == 2) {
                        str_stroke = "blue";
                    }


                    String str_stroke_dasharray;
                    str_stroke_dasharray = "";

//stroke-dasharray="10 6 2 4 2 6"

                    if (i_orisen_hyougen == 1) {
                        //g_setColor(g,ori_s.getcolor(i));
                        //g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                    }

                    if (i_orisen_hyougen == 2) {
                        //g_setColor(g,ori_s.getcolor(i));
                        if (ori_s.getcolor(i) == 0) {
                        }//基本指定A　　線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 1) {
                            str_stroke_dasharray = "stroke-dasharray=\"10 3 3 3\"";
                        }//dash_M1,一点鎖線
                        if (ori_s.getcolor(i) == 2) {
                            str_stroke_dasharray = "stroke-dasharray=\"8 8\"";
                        }//dash_V ,破線
                    }


                    if (i_orisen_hyougen == 3) {
                        str_stroke = "black";
                        if (ori_s.getcolor(i) == 0) {
                        }//基本指定A　　線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 1) {
                            str_stroke_dasharray = "stroke-dasharray=\"10 3 3 3\"";
                        }//dash_M1,一点鎖線
                        if (ori_s.getcolor(i) == 2) {
                            str_stroke_dasharray = "stroke-dasharray=\"8 8\"";
                        }//dash_V ,破線
                    }

                    if (i_orisen_hyougen == 4) {
                        str_stroke = "black";
                        if (ori_s.getcolor(i) == 0) {
                        }//基本指定A　　線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 1) {
                            str_stroke_dasharray = "stroke-dasharray=\"10 3 3 3 3 3\"";
                        }//dash_M2,二点鎖線
                        if (ori_s.getcolor(i) == 2) {
                            str_stroke_dasharray = "stroke-dasharray=\"8 8\"";
                        }//dash_V ,破線
                    }


/*
					if(i_orisen_hyougen==1){
						g_setColor(g,ori_s.getcolor(i));
						g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
					}

					if(i_orisen_hyougen==2){
						g_setColor(g,ori_s.getcolor(i));
						if(ori_s.getcolor(i)==0){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER				));}//基本指定A　　線の太さや線の末端の形状
						if(ori_s.getcolor(i)==1){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f, dash_M1, 0.0f	));}//一点鎖線//線の太さや線の末端の形状
						if(ori_s.getcolor(i)==2){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f, dash_V , 0.0f	));}//破線//線の太さや線の末端の形状
					}

					if(i_orisen_hyougen==3){
						if(ori_s.getcolor(i)==0){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER				));}//基本指定A　　線の太さや線の末端の形状
						if(ori_s.getcolor(i)==1){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f, dash_M1, 0.0f	));}//一点鎖線//線の太さや線の末端の形状
						if(ori_s.getcolor(i)==2){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f, dash_V , 0.0f	));}//破線//線の太さや線の末端の形状
					}

					if(i_orisen_hyougen==4){
						if(ori_s.getcolor(i)==0){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));}//基本指定A　　線の太さや線の末端の形状
						if(ori_s.getcolor(i)==1){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f, dash_M2, 0.0f));}//二点鎖線//線の太さや線の末端の形状
						if(ori_s.getcolor(i)==2){g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f, dash_V , 0.0f));}//破線//線の太さや線の末端の形状
					}

*/


                    s_tv.set(camera.object2TV(ori_s.get(i)));
                    a.set(s_tv.geta());
                    b.set(s_tv.getb());//a.set(s_tv.getax()+0.000001,s_tv.getay()+0.000001); b.set(s_tv.getbx()+0.000001,s_tv.getby()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため


//					g.drawLine( (int)a.getx(),(int)a.gety(),(int)b.getx(),(int)b.gety()); //直線

                    BigDecimal b_ax = new BigDecimal(String.valueOf(a.getx()));
                    double x1 = b_ax.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    BigDecimal b_ay = new BigDecimal(String.valueOf(a.gety()));
                    double y1 = b_ay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    BigDecimal b_bx = new BigDecimal(String.valueOf(b.getx()));
                    double x2 = b_bx.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    BigDecimal b_by = new BigDecimal(String.valueOf(b.gety()));
                    double y2 = b_by.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();


                    memo_temp.addLine("<line x1=\"" + x1 + "\"" +
                            " y1=\"" + y1 + "\"" +
                            " x2=\"" + x2 + "\"" +
                            " y2=\"" + y2 + "\"" +
                            " " + str_stroke_dasharray + " " +
                            " stroke=\"" + str_stroke + "\"" +
                            " stroke-width=\"" + str_strokewidth + "\"" + " />");


/*


					memo_temp.addGyou(    "<line x1=\"" + str.valueOf( b_ax.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
							           " y1=\"" + str.valueOf( b_ay.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
							           " x2=\"" + str.valueOf( b_bx.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
							           " y2=\"" + str.valueOf( b_by.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
								" "+str_stroke_dasharray+" "+
							  " stroke=\"" + str_stroke	 + "\"" +
						    " stroke-width=\"" + str_strokewidth + "\"" +" />"
													);




					memo_temp.addGyou(    "<line x1=\"" + str.valueOf(a.getx()) + "\"" +
							      " y1=\"" + str.valueOf(a.gety()) + "\"" +
							      " x2=\"" + str.valueOf(b.getx()) + "\"" +
							      " y2=\"" + str.valueOf(b.gety()) + "\"" +
								" "+str_stroke_dasharray+" "+
							  " stroke=\"" + str_stroke	 + "\"" +
						    " stroke-width=\"" + str_strokewidth + "\"" +" />"
													);

*/


                    if (ir_ten != 0) {
                        if (fTenkaizuSenhaba < 2.0f) {//頂点の黒い正方形を描く
                            //str_stroke="black";//g.setColor(Color.black);
                            //int i_haba=1;
                            //str_strokewidth = Integer.toString(ir_ten);
                            int i_haba = ir_ten;
                            //<rect style="fill:#000000;stroke:#000000;stroke-width:1"
                            //   width="49.892857"
                            //   height="46.869045"
                            //   x="0"
                            //   y="249.375" />
                            memo_temp.addLine("<rect style=\"fill:#000000;stroke:#000000;stroke-width:1\"" +
                                    " width=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " height=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " x=\"" + (x1 - (double) i_haba) + "\"" +
                                    " y=\"" + (y1 - (double) i_haba) + "\"" +
                                    " />");

                            memo_temp.addLine("<rect style=\"fill:#000000;stroke:#000000;stroke-width:1\"" +
                                    " width=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " height=\"" + (2.0 * (double) i_haba + 1.0) + "\"" +
                                    " x=\"" + (x2 - (double) i_haba) + "\"" +
                                    " y=\"" + (y2 - (double) i_haba) + "\"" +
                                    " />");

                            //g.fillRect( (int)a.getx()-i_haba,(int)a.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                            //g.fillRect( (int)b.getx()-i_haba,(int)b.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く
                        }
                    }

                    if (fTenkaizuSenhaba >= 2.0f) {//  太線
                        //g2.setStroke(new BasicStroke(1.0f+fTenkaizuSenhaba%1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (ir_ten != 0) {
                            //int i_haba=(int)fTenkaizuSenhaba+ir_ten;//int i_haba=2;
                            double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_ten;//int i_haba=2;

                            memo_temp.addLine("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                    " r=\"" + d_haba + "\"" +
                                    " cx=\"" + x1 + "\"" +
                                    " cy=\"" + y1 + "\"" +
                                    " />");


                            memo_temp.addLine("<circle style=\"fill:#ffffff;stroke:#000000;stroke-width:1\"" +
                                    " r=\"" + d_haba + "\"" +
                                    " cx=\"" + x2 + "\"" +
                                    " cy=\"" + y2 + "\"" +
                                    " />");


/*
      <circle
         id="circle4747"
         r="100"
         cy="100"
         cx="100"
         style="opacity:0.2;fill:#ff0000" />



							g.setColor(Color.white);
							g2.fill(new Ellipse2D.Double(a.getx()-d_haba, a.gety()-d_haba, 2.0*d_haba,2.0*d_haba));
							//g.fillOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円


							g.setColor(Color.black);
							g2.draw(new Ellipse2D.Double(a.getx()-d_haba, a.gety()-d_haba, 2.0*d_haba,2.0*d_haba));
							//g.drawOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円

							g.setColor(Color.white);
							g2.fill(new Ellipse2D.Double(b.getx()-d_haba, b.gety()-d_haba, 2.0*d_haba,2.0*d_haba));
							//g.fillOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円

							g.setColor(Color.black);
							g2.draw(new Ellipse2D.Double(b.getx()-d_haba, b.gety()-d_haba, 2.0*d_haba,2.0*d_haba));
						*/    //g.drawOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円
                        }
                    }


                }
            }
        }


        return memo_temp;
    }


    //---------------------------------------------------------------------------------------------------------------------------------
    public void Memo_jyouhou_tuika(Memo memo1) {
        //String str=new String();//文字列処理用のクラスのインスタンス化
        // ----------------------------------------------------------------------


        memo1.addLine("<camera_of_orisen_nyuuryokuzu>");
        memo1.addLine("<camera_ichi_x>" + camera.get_camera_ichi_x() + "</camera_ichi_x>");
        memo1.addLine("<camera_ichi_y>" + camera.get_camera_ichi_y() + "</camera_ichi_y>");
        memo1.addLine("<camera_kakudo>" + camera.get_camera_kakudo() + "</camera_kakudo>");
        memo1.addLine("<camera_kagami>" + camera.get_camera_kagami() + "</camera_kagami>");
        memo1.addLine("<camera_bairitsu_x>" + camera.get_camera_bairitsu_x() + "</camera_bairitsu_x>");
        memo1.addLine("<camera_bairitsu_y>" + camera.get_camera_bairitsu_y() + "</camera_bairitsu_y>");
        memo1.addLine("<hyouji_ichi_x>" + camera.get_hyouji_ichi_x() + "</hyouji_ichi_x>");
        memo1.addLine("<hyouji_ichi_y>" + camera.get_hyouji_ichi_y() + "</hyouji_ichi_y>");
        memo1.addLine("</camera_of_orisen_nyuuryokuzu>");


        // ----------------------------------------------------------------------
        memo1.addLine("<settei>");
        if (orihime_app.ckbox_mouse_settei.isSelected()) {
            memo1.addLine("<ckbox_mouse_settei>true </ckbox_mouse_settei>");
        }
        if (!orihime_app.ckbox_mouse_settei.isSelected()) {
            memo1.addLine("<ckbox_mouse_settei>false</ckbox_mouse_settei>");
        }

        if (orihime_app.ckbox_ten_sagasi.isSelected()) {
            memo1.addLine("<ckbox_ten_sagasi>true </ckbox_ten_sagasi>");
        }
        if (!orihime_app.ckbox_ten_sagasi.isSelected()) {
            memo1.addLine("<ckbox_ten_sagasi>false</ckbox_ten_sagasi>");
        }

        if (orihime_app.ckbox_ten_hanasi.isSelected()) {
            memo1.addLine("<ckbox_ten_hanasi>true </ckbox_ten_hanasi>");
        }
        if (!orihime_app.ckbox_ten_hanasi.isSelected()) {
            memo1.addLine("<ckbox_ten_hanasi>false</ckbox_ten_hanasi>");
        }

        if (orihime_app.ckbox_kou_mitudo_nyuuryoku.isSelected()) {
            memo1.addLine("<ckbox_kou_mitudo_nyuuryoku>true </ckbox_kou_mitudo_nyuuryoku>");
        }
        if (!orihime_app.ckbox_kou_mitudo_nyuuryoku.isSelected()) {
            memo1.addLine("<ckbox_kou_mitudo_nyuuryoku>false</ckbox_kou_mitudo_nyuuryoku>");
        }

        if (orihime_app.ckbox_bun.isSelected()) {
            memo1.addLine("<ckbox_bun>true </ckbox_bun>");
        }
        if (!orihime_app.ckbox_bun.isSelected()) {
            memo1.addLine("<ckbox_bun>false</ckbox_bun>");
        }

        if (orihime_app.ckbox_cp.isSelected()) {
            memo1.addLine("<ckbox_cp>true </ckbox_cp>");
        }
        if (!orihime_app.ckbox_cp.isSelected()) {
            memo1.addLine("<ckbox_cp>false</ckbox_cp>");
        }

        if (orihime_app.ckbox_a0.isSelected()) {
            memo1.addLine("<ckbox_a0>true </ckbox_a0>");
        }
        if (!orihime_app.ckbox_a0.isSelected()) {
            memo1.addLine("<ckbox_a0>false</ckbox_a0>");
        }

        if (orihime_app.ckbox_a1.isSelected()) {
            memo1.addLine("<ckbox_a1>true </ckbox_a1>");
        }
        if (!orihime_app.ckbox_a1.isSelected()) {
            memo1.addLine("<ckbox_a1>false</ckbox_a1>");
        }

        if (orihime_app.ckbox_mejirusi.isSelected()) {
            memo1.addLine("<ckbox_mejirusi>true </ckbox_mejirusi>");
        }
        if (!orihime_app.ckbox_mejirusi.isSelected()) {
            memo1.addLine("<ckbox_mejirusi>false</ckbox_mejirusi>");
        }

        if (orihime_app.ckbox_cp_ue.isSelected()) {
            memo1.addLine("<ckbox_cp_ue>true </ckbox_cp_ue>");
        }
        if (!orihime_app.ckbox_cp_ue.isSelected()) {
            memo1.addLine("<ckbox_cp_ue>false</ckbox_cp_ue>");
        }

        if (orihime_app.ckbox_oritatami_keika.isSelected()) {
            memo1.addLine("<ckbox_oritatami_keika>true </ckbox_oritatami_keika>");
        }
        if (!orihime_app.ckbox_oritatami_keika.isSelected()) {
            memo1.addLine("<ckbox_oritatami_keika>false</ckbox_oritatami_keika>");
        }


        //展開図の線の太さ。
        memo1.addLine("<iTenkaizuSenhaba>" +
                orihime_app.iTenkaizuSenhaba +
                "</iTenkaizuSenhaba>");

        //頂点のしるしの幅
        memo1.addLine("<ir_ten>" +
                orihime_app.ir_ten +
                "</ir_ten>");

        //折線表現を色で表す
        memo1.addLine("<i_orisen_hyougen>" +
                orihime_app.i_orisen_hyougen +
                "</i_orisen_hyougen>");

//A_A
        memo1.addLine("<i_anti_alias>" +
                orihime_app.i_anti_alias +
                "</i_anti_alias>");


        memo1.addLine("</settei>");

        // ----------------------------------------------------------------------


        memo1.addLine("<Kousi>");

        memo1.addLine("<i_kitei_jyoutai>" + get_i_kitei_jyoutai() + "</i_kitei_jyoutai>");
        memo1.addLine("<nyuuryoku_kitei>" + orihime_app.nyuuryoku_kitei + "</nyuuryoku_kitei>");

        memo1.addLine("<memori_kankaku>" + orihime_app.memori_kankaku + "</memori_kankaku>");
        memo1.addLine("<a_to_heikouna_memori_iti>" + kus.get_a_to_heikouna_memori_iti() + "</a_to_heikouna_memori_iti>");
        memo1.addLine("<b_to_heikouna_memori_iti>" + kus.get_b_to_heikouna_memori_iti() + "</b_to_heikouna_memori_iti>");
        memo1.addLine("<kousi_senhaba>" + kus.get_kousi_senhaba() + "</kousi_senhaba>");

        memo1.addLine("<d_kousi_x_a>" + orihime_app.d_kousi_x_a + "</d_kousi_x_a>");
        memo1.addLine("<d_kousi_x_b>" + orihime_app.d_kousi_x_b + "</d_kousi_x_b>");
        memo1.addLine("<d_kousi_x_c>" + orihime_app.d_kousi_x_c + "</d_kousi_x_c>");
        memo1.addLine("<d_kousi_y_a>" + orihime_app.d_kousi_y_a + "</d_kousi_y_a>");
        memo1.addLine("<d_kousi_y_b>" + orihime_app.d_kousi_y_b + "</d_kousi_y_b>");
        memo1.addLine("<d_kousi_y_c>" + orihime_app.d_kousi_y_c + "</d_kousi_y_c>");
        memo1.addLine("<d_kousi_kakudo>" + orihime_app.d_kousi_kakudo + "</d_kousi_kakudo>");
        //memo1.addGyou("<d_kousi_x_nagasa>"+orihime_ap.d_kousi_x_nagasa+"</d_kousi_x_nagasa>");
        //memo1.addGyou("<d_kousi_y_nagasa>"+orihime_ap.d_kousi_y_nagasa+"</d_kousi_y_nagasa>");


        memo1.addLine("</Kousi>");
        // ----------------------------------------------------------------------

        memo1.addLine("<Kousi_iro>");

        memo1.addLine("<kousi_color_R>" + kus.get_kousi_color().getRed() + "</kousi_color_R>");
        memo1.addLine("<kousi_color_G>" + kus.get_kousi_color().getGreen() + "</kousi_color_G>");
        memo1.addLine("<kousi_color_B>" + kus.get_kousi_color().getBlue() + "</kousi_color_B>");

        memo1.addLine("<kousi_memori_color_R>" + kus.get_kousi_memori_color().getRed() + "</kousi_memori_color_R>");
        memo1.addLine("<kousi_memori_color_G>" + kus.get_kousi_memori_color().getGreen() + "</kousi_memori_color_G>");
        memo1.addLine("<kousi_memori_color_B>" + kus.get_kousi_memori_color().getBlue() + "</kousi_memori_color_B>");

        memo1.addLine("</Kousi_iro>");


        // ----------------------------------------------------------------------

        memo1.addLine("<oriagarizu>");

        memo1.addLine("<oriagarizu_F_color_R>" + orihime_app.OZ.foldedFigure_F_color.getRed() + "</oriagarizu_F_color_R>");
        memo1.addLine("<oriagarizu_F_color_G>" + orihime_app.OZ.foldedFigure_F_color.getGreen() + "</oriagarizu_F_color_G>");
        memo1.addLine("<oriagarizu_F_color_B>" + orihime_app.OZ.foldedFigure_F_color.getBlue() + "</oriagarizu_F_color_B>");

        memo1.addLine("<oriagarizu_B_color_R>" + orihime_app.OZ.foldedFigure_B_color.getRed() + "</oriagarizu_B_color_R>");
        memo1.addLine("<oriagarizu_B_color_G>" + orihime_app.OZ.foldedFigure_B_color.getGreen() + "</oriagarizu_B_color_G>");
        memo1.addLine("<oriagarizu_B_color_B>" + orihime_app.OZ.foldedFigure_B_color.getBlue() + "</oriagarizu_B_color_B>");

        memo1.addLine("<oriagarizu_L_color_R>" + orihime_app.OZ.foldedFigure_L_color.getRed() + "</oriagarizu_L_color_R>");
        memo1.addLine("<oriagarizu_L_color_G>" + orihime_app.OZ.foldedFigure_L_color.getGreen() + "</oriagarizu_L_color_G>");
        memo1.addLine("<oriagarizu_L_color_B>" + orihime_app.OZ.foldedFigure_L_color.getBlue() + "</oriagarizu_L_color_B>");


        memo1.addLine("</oriagarizu>");

    }

    //---------------------------------
    //対称性の指定
    //public void settaisyousei(int i){
    //taisyousei=i;
    //}
    //---------------------------------
    public void setcolor(int i) {
        icol = i;
    }

    //---------------------------------
    public int get_ieda() {
        return ieda;
    }

    //不要な線分を消去する-----------------------------------------------
    public void gomisute() {

        for (int i = 1; i <= ori_s.getsousuu(); i++) {
            int idel = 0;


            if (gomibako.convex_inside(ori_s.get(i)) == 1) {
                idel = 1;
            }


            if (idel == 1) {
                ori_s.delsenbun(i);
                i = i - 1;
                ieda = ori_s.getsousuu() + 1;    //<<<<<<<<<<<<<<<<<<
            }
        }
    }


    /*
  //枝を動かした後の処理を行う関数----------------------------------------------------
	public void eda_atosyori_01() {//枝の長さを変えずに、枝全体を平行移動して微調整する。
    //アクティブな帯の位置を微調整する
    Ten ab = new Ten(1,ori_s.getb(ieda),-1,ori_s.geta(ieda));//アクティブな枝の、点aから点bへ向かうベクトル
    Ten ba = new Ten(1,ori_s.geta(ieda),-1,ori_s.getb(ieda));//アクティブな枝の、点aから点bへ向かうベクトル

    int jeda;   //アクティブな枝と近い別の枝
    int jbasyo; //アクティブな枝と近い別の枝のどこが近いのかを示すための番号

    //　アクティブな枝のa点　と　別の枝　との距離が　ｒ　より近い場合

    jeda=ori_s.senbun_sagasi(ori_s.geta(ieda),2*r_ten,ieda);//アクティブな枝のa点と近い別の枝を求める。
    jbasyo= ori_s.senbun_busyo_sagasi(jeda,ori_s.geta(ieda),2*r_ten);//別の枝のどの部所が近いかを求める。
    if( (jeda!=0) && (jbasyo==1)){ //アクティブな枝のa点と、別の枝のa点が近い場合
      ori_s.seta(ieda,ori_s.geta(jeda));
      ori_s.setb(ieda,new Ten(1,ori_s.geta(ieda),1,ab));//こう書いてもちゃんと動く様なので、このまま使う。
    }
    if( (jeda!=0) && (jbasyo==2)){ //アクティブな枝のa点と、別の枝のb点が近い場合
      ori_s.seta(ieda,ori_s.getb(jeda));
      ori_s.setb(ieda,new Ten(1,ori_s.geta(ieda),1,ab));
    }

    //　アクティブな枝のb点　と　別の枝　との距離が　ｒ　より近い場合
    jeda=ori_s.senbun_sagasi(ori_s.getb(ieda),2*r_ten,ieda);//アクティブな枝のb点と近い別の枝を求める。
    jbasyo= ori_s.senbun_busyo_sagasi(jeda,ori_s.getb(ieda),2*r_ten);//別の枝のどの部所が近いかを求める。
    if( (jeda!=0) && (jbasyo==1)){ //アクティブな枝のb点と、別の枝のa点が近い場合
      ori_s.setb(ieda,ori_s.geta(jeda));
      ori_s.seta(ieda,new Ten(1,ori_s.getb(ieda),1,ba));
    }
    if( (jeda!=0) && (jbasyo==2)){ //アクティブな枝のb点と、別の枝のb点が近い場合
        ori_s.setb(ieda,ori_s.getb(jeda));
	ori_s.seta(ieda,new Ten(1,ori_s.getb(ieda),1,ba));
    }
  }
*/
/*
  //枝を動かした後の処理を行う関数----------------------------------------------------
	public void eda_atosyori_02() {//一端の点だけを移動して反対の端の点は動かさないで微調整する。
    //アクティブな帯の位置を微調整する

    int jeda;   //アクティブな枝と近い別の枝
    int jbasyo; //アクティブな枝と近い別の枝のどこが近いのかを示すための番号
	if(ori_s.getnagasa(ieda)>=r_ten){
    //　アクティブな枝のa点　と　別の枝との距離が　ｒ　より近い場合
    jeda=ori_s.senbun_sagasi(ori_s.geta(ieda),r_ten,ieda);//アクティブな枝のa点と近い別の枝を求める。
    jbasyo= ori_s.senbun_busyo_sagasi(jeda,ori_s.geta(ieda),r_ten);//別の枝のどの部所が近いかを求める。
    if( (jeda!=0) && (jbasyo==1)){ori_s.seta(ieda,ori_s.geta(jeda));}//アクティブな枝のa点と、別の枝のa点が近い場合
    if( (jeda!=0) && (jbasyo==2)){ori_s.seta(ieda,ori_s.getb(jeda));}//アクティブな枝のa点と、別の枝のb点が近い場合

    //　アクティブな枝(ieda)のb点　と　別の枝(jeda)との距離が　ｒ　より近い場合
    jeda=ori_s.senbun_sagasi(ori_s.getb(ieda),r_ten,ieda);//アクティブな枝のb点と近い別の枝を求める。
    jbasyo= ori_s.senbun_busyo_sagasi(jeda,ori_s.getb(ieda),r_ten);//別の枝のどの部所が近いかを求める。
    if((jeda!=0) && (jbasyo==1)){ori_s.setb(ieda,ori_s.geta(jeda));}//アクティブな枝のb点と、別の枝のa点が近い場合
    if((jeda!=0) && (jbasyo==2)){ori_s.setb(ieda,ori_s.getb(jeda));}//アクティブな枝のb点と、別の枝のb点が近い場合

 //以下は070317に追加 複数の線分が集まった頂点を別の頂点近くに持っていったときの後処理用
//150312　　2*r　を　r　に修正


    //　アクティブな枝のa点　と　別の枝との距離が　ｒ　より近い場合
    jeda=ori_s.senbun_sagasi(ori_s.geta(ieda),r_ten,-10);//アクティブなieda枝のa点と近い別の枝を求める。
    jbasyo= ori_s.senbun_busyo_sagasi(jeda,ori_s.geta(ieda),r_ten);//別の枝のどの部所が近いかを求める。

    if((jeda!=0) && (jbasyo==1)){ori_s.kasseika(ori_s.geta(jeda),r_ten); ori_s.set(ori_s.geta(jeda));}//アクティブなieda枝のa点と、別の枝のa点が近い場合
    if((jeda!=0) && (jbasyo==2)){ori_s.kasseika(ori_s.getb(jeda),r_ten); ori_s.set(ori_s.getb(jeda));}//アクティブなieda枝のa点と、別の枝のb点が近い場合

    //　アクティブな枝(ieda)のb点　と　別の枝(jeda)との距離が　ｒ　より近い場合
    jeda=ori_s.senbun_sagasi(ori_s.getb(ieda),r_ten,-10);//アクティブなieda枝のb点と近い別の枝を求める。
    jbasyo= ori_s.senbun_busyo_sagasi(jeda,ori_s.getb(ieda),r_ten);//別の枝のどの部所が近いかを求める。

    if((jeda!=0) && (jbasyo==1)){ori_s.kasseika(ori_s.geta(jeda),r_ten); ori_s.set(ori_s.geta(jeda));}//アクティブなieda枝のb点と、別の枝のa点が近い場合
    if((jeda!=0) && (jbasyo==2)){ori_s.kasseika(ori_s.getb(jeda),r_ten); ori_s.set(ori_s.getb(jeda));}//アクティブなieda枝のb点と、別の枝のb点が近い場合
 	}

 }
*/
    public void bunkatu_seiri() {
        ori_s.bunkatu_seiri();
    }

    //public void  bunkatu_seiri_for_Smen_hassei(){ori_s.bunkatu_seiri_for_Smen_hassei();}//ori_sとは線分集合のこと、Senbunsyuugou ori_s =new Senbunsyuugou();

    public void kousabunkatu() {
        ori_s.kousabunkatu();
    }

    public void ten_sakujyo() {
        ori_s.ten_sakujyo();
    }

    public void ten_sakujyo(double r) {
        ori_s.ten_sakujyo(r);
    }

    public void jyuufuku_senbun_sakujyo() {
        ori_s.jyuufuku_senbun_sakujyo();
    }

    public void jyuufuku_senbun_sakujyo(double r) {
        ori_s.jyuufuku_senbun_sakujyo(r);
    }


    public String undo() {
        //Ubox.setMemo(getMemo());
        s_title = setMemo_for_redo_undo(Ubox.undo());
        //ori_s.addsenbun  delsenbunを実施しているところでcheckを実施
        if (i_check1 == 1) {
            check1(0.001, 0.5);
        }
        if (i_check2 == 1) {
            check2(0.01, 0.5);
        }
        if (i_check3 == 1) {
            check3(0.0001);
        }
        if (i_check4 == 1) {
            check4(0.0001);
        }

        return s_title;
    }


    public String redo() {
        //Ubox.setMemo(getMemo());
        s_title = setMemo_for_redo_undo(Ubox.redo());

        //ori_s.addsenbun  delsenbunを実施しているところでcheckを実施
        if (i_check1 == 1) {
            check1(0.001, 0.5);
        }
        if (i_check2 == 1) {
            check2(0.01, 0.5);
        }
        if (i_check3 == 1) {
            check3(0.0001);
        }
        if (i_check4 == 1) {
            check4(0.0001);
        }

        return s_title;
    }

    public void set_title(String s_title0) {
        s_title = s_title0;
    }

    public void kiroku() {
        //ori_s.addsenbun  delsenbunを実施しているところでcheckを実施
        if (i_check1 == 1) {
            check1(0.001, 0.5);
        }
        if (i_check2 == 1) {
            check2(0.01, 0.5);
        }
        if (i_check3 == 1) {
            check3(0.0001);
        }
        if (i_check4 == 1) {
            check4(0.0001);
        }

        Ubox.kiroku(getMemo(s_title));
    }


    public void h_undo() {
        h_setMemo(h_Ubox.undo());
    }


    public void h_redo() {
        h_setMemo(h_Ubox.redo());
    }


    public void h_kiroku() {
        h_Ubox.kiroku(h_getMemo());
    }


    public void kousi_oekaki_with_camera(Graphics g, int i_bun_hyouji, int i_cp_hyouji, int i_a0_hyouji, int i_a1_hyouji, float fTenkaizuSenhaba, int i_orisen_hyougen, float f_h_TenkaizuSenhaba, int p0x_max, int p0y_max) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        //System.out.println(" E 20170201_1");
        Graphics2D g2 = (Graphics2D) g;

        String text = "";//文字列処理用のクラスのインスタンス化
        double d;
        OritaOekaki OO = new OritaOekaki();

        Line s_tv = new Line();
        Point a = new Point();
        Point b = new Point();

        Line s_ob = new Line();


        // ------------------------------------------------------

        //格子線の描画
        if (i_kou_mitudo_nyuuryoku == 0) {
            kus.oekaki(g, camera, p0x_max, p0y_max, 0);
        }
        if (i_kou_mitudo_nyuuryoku == 1) {
            kus.oekaki(g, camera, p0x_max, p0y_max, 1);
        }
    }


    //------------------------------------------------------------------------------
//基本枝の描画111111111111111111111111111111111111111111111111111111111111111111		//System.out.println("_");
//------------------------------------------------------------------------------
    public void oekaki_with_camera(Graphics g, int i_bun_hyouji, int i_cp_hyouji, int i_a0_hyouji, int i_a1_hyouji, float fTenkaizuSenhaba, int i_orisen_hyougen, float f_h_TenkaizuSenhaba, int p0x_max, int p0y_max, int i_mejirusi_hyouji) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        //System.out.println(" E 20170201_1");
        Graphics2D g2 = (Graphics2D) g;

        String text = "";//文字列処理用のクラスのインスタンス化
        double d;
        OritaOekaki OO = new OritaOekaki();

        Line s_tv = new Line();
        Point a = new Point();
        Point b = new Point();

        Line s_ob = new Line();


        // ------------------------------------------------------

        //格子線の描画
        if (i_kou_mitudo_nyuuryoku == 0) {
            kus.oekaki(g, camera, p0x_max, p0y_max, 0);
        }
        if (i_kou_mitudo_nyuuryoku == 1) {
            kus.oekaki(g, camera, p0x_max, p0y_max, 1);
        }


        BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//線の太さや線の末端の形状

        //補助画線（折線と非干渉）の描画
        if (i_a1_hyouji == 1) {
            g2.setStroke(new BasicStroke(f_h_TenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状
            for (int i = 1; i <= hoj_s.getsousuu(); i++) {

                //if(hoj_s.getcolor(i)==4){g.setColor(Color.orange);System.out.println("hoj_s.getcolor(i)==4");}
                //if(hoj_s.getcolor(i)==7){g.setColor(Color.yellow);System.out.println("hoj_s.getcolor(i)==7");}
                g_setColor(g, hoj_s.getcolor(i));

				s_tv.set(camera.object2TV(hoj_s.get(i)));
                //a.set(s_tv.geta()); b.set(s_tv.getb());
                a.set(s_tv.getax() + 0.000001, s_tv.getay() + 0.000001);
                b.set(s_tv.getbx() + 0.000001, s_tv.getby() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g.drawLine((int) a.getx(), (int) a.gety(), (int) b.getx(), (int) b.gety()); //直線

                if (fTenkaizuSenhaba < 2.0f) {//頂点の正方形を描く
                    g.setColor(Color.black);
                    //int i_haba=1;
                    int i_haba = ir_ten;
                    g.fillRect((int) a.getx() - i_haba, (int) a.gety() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                    g.fillRect((int) b.getx() - i_haba, (int) b.gety() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く
                }

                if (fTenkaizuSenhaba >= 2.0f) {//  太線
                    //	OO.habaLine( g,s_tv,iTenkaizuSenhaba,hoj_s.getcolor(i));
                    g2.setStroke(new BasicStroke(1.0f + f_h_TenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状
                    //OO.habaLine( g,s_tv,iTenkaizuSenhaba,k.getcolor(i));

                    //int i_haba=iTenkaizuSenhaba;
                    //g.fillOval( (int)a.getx()-i_haba,(int)a.gety()-i_haba,2*i_haba,2*i_haba); //円
                    //g.fillOval( (int)b.getx()-i_haba,(int)b.gety()-i_haba,2*i_haba,2*i_haba); //円

                    if (ir_ten != 0) {

                        int i_haba = (int) fTenkaizuSenhaba + ir_ten;//int i_haba=2;
                        double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_ten;//int i_haba=2;

                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        //g.fillOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        //g.drawOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円

                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(b.getx() - d_haba, b.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        //g.fillOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(b.getx() - d_haba, b.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        //g.drawOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円
                    }

                    g2.setStroke(new BasicStroke(f_h_TenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

                }
            }
        }
        //System.out.println(" E 20170201_2");

        // ----------------------------------------------------------------

        //check結果の表示

        g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定


        //Check1Senbには0番目からsize()-1番目までデータが入っている
        if (i_check1 == 1) {
            for (int i = 0; i < ori_s.check1_size(); i++) {
                Line s_temp = new Line();
                s_temp.set(ori_s.check1_getSenbun(i));
                OO.yubisasi1(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }

        if (i_check2 == 1) {
            for (int i = 0; i < ori_s.check2_size(); i++) {
                Line s_temp = new Line();
                s_temp.set(ori_s.check2_getSenbun(i));
                OO.yubisasi2(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }

        g2.setStroke(new BasicStroke(25.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定


        //Check4Senbには0番目からsize()-1番目までデータが入っている
        //System.out.println("ori_s.check4_size() = "+ori_s.check4_size());
        if (i_check4 == 1) {
            for (int i = 0; i < ori_s.check4_size(); i++) {
                Line s_temp = new Line();
                s_temp.set(ori_s.check4_getSenbun(i));
                OO.yubisasi4(g, camera.object2TV(s_temp), i_ck4_color_toukado);
            }
        }


        //Check3Senbには0番目からsize()-1番目までデータが入っている
        if (i_check3 == 1) {
            for (int i = 0; i < ori_s.check3_size(); i++) {
                Line s_temp = new Line();
                s_temp.set(ori_s.check3_getSenbun(i));
                //OO.jyuuji(g,camera.object2TV(s_temp.geta()), 7.0 , 3.0 , 1);
                OO.yubisasi3(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }


        //System.out.println(" E 20170201_4");

        //camera中心を十字で描く
        if (i_mejirusi_hyouji == 1) {
            OO.jyuuji(g, camera.object2TV(camera.get_camera_ichi()), 5.0, 2.0, 3);
        }


        //System.out.println(" E 20170201_5");


        //円を描く　
        //System.out.println(" 円を描く ori_s.cir_size()="+ori_s.cir_size());
        if (i_a0_hyouji == 1) {
            for (int i = 1; i <= ori_s.cir_size(); i++) {

                double d_haba;
                Circle e_temp = new Circle();
                e_temp.set(ori_s.cir_getEn(i));

                a.set(camera.object2TV(e_temp.get_tyuusin()));//この場合のaは描画座標系での円の中心の位置
                //a.set(a.getx()+0.000001,a.gety()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                //g.setColor(Color.cyan);


//System.out.println("get_tpp_en("+i+") = "+ori_s.get_tpp_en(i));
                if (e_temp.get_tpp() == 0) {
                    g_setColor(g, e_temp.getcolor());
                } else if (e_temp.get_tpp() == 1) {
                    g.setColor(e_temp.get_tpp_color());
                }


                //円周の描画
                d_haba = e_temp.getr() * camera.get_camera_bairitsu_x();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。
                g2.draw(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));


                // 点t_oをお絵かきするのに必要な手順
                //Ten t_o =new Ten(100.0,100.0);//t_oを定義
                //Ten t_T =new Ten();t_T.set(camera.object2TV(t_o));//t_Tを定義し、t_oを描画用座標用にに変換下ものをsetする。
                //g.fillRect( (int)t_T.getx()-1,(int)t_T.gety()-1,3,3); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く

/*
				Ten t_o =new Ten();Ten t_T =new Ten();
				for(int h=1;h<360;h++ ){
					t_o.set(e_temp.get_tyuusin().getx()+e_temp.getr()*Math.cos((double)h),
						e_temp.get_tyuusin().gety()+e_temp.getr()*Math.sin((double)h));

					t_T.set(camera.object2TV(t_o));
					g.fillRect( (int)t_T.getx()-1,(int)t_T.gety()-1,3,3); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
				}
*/
            }
        }


        //円の中心の描画
        if (i_a0_hyouji == 1) {
            for (int i = 1; i <= ori_s.cir_size(); i++) {
                //if(ori_s.getcolor(i)==3){
                double d_haba;
                Circle e_temp = new Circle();
                e_temp.set(ori_s.cir_getEn(i));
                //System.out.println("Es1 お絵かき  "+ i+";" +e_temp.getx()+"," +e_temp.gety()+"," +e_temp.getr());

                a.set(camera.object2TV(e_temp.get_tyuusin()));//この場合のaは描画座標系での円の中心の位置
                //a.set(a.getx()+0.000001,a.gety()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                //g.setColor(Color.cyan);
                g.setColor(new Color(0, 255, 255, 255));

                //円の中心の描画
                if (fTenkaizuSenhaba < 2.0f) {//中心の黒い正方形を描く
                    g.setColor(Color.black);
                    //int i_haba=1;
                    int i_haba = ir_ten;
                    g.fillRect((int) a.getx() - i_haba, (int) a.gety() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                    //g.fillRect( (int)b.getx()-i_haba,(int)b.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く
                }

                if (fTenkaizuSenhaba >= 2.0f) {//  太線指定時の中心を示す黒い小円を描く
                    g2.setStroke(new BasicStroke(1.0f + fTenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                    if (ir_ten != 0) {
                        int i_haba = (int) fTenkaizuSenhaba + ir_ten;//int i_haba=2;
                        d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_ten;//int i_haba=2;


                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        //g.fillOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円


                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                        //g.drawOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円

                    }
                }
                //}
            }

        }


        //selectの描画
        g2.setStroke(new BasicStroke(fTenkaizuSenhaba * 2.0f + 2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        for (int i = 1; i <= ori_s.getsousuu(); i++) {
            if (ori_s.get_select(i) == 2) {
                g.setColor(Color.green);

                s_tv.set(camera.object2TV(ori_s.get(i)));

                a.set(s_tv.getax() + 0.000001, s_tv.getay() + 0.000001);
                b.set(s_tv.getbx() + 0.000001, s_tv.getby() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g.drawLine((int) a.getx(), (int) a.gety(), (int) b.getx(), (int) b.gety()); //直線

            }
        }


        //展開図の描画 補助活線のみ
        if (i_a0_hyouji == 1) {
            for (int i = 1; i <= ori_s.getsousuu(); i++) {
                if (ori_s.getcolor(i) == 3) {

                    g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                    //g.setColor(Color.cyan);


                    //g_setColor(g,ori_s.getcolor(i));
                    if (ori_s.get_tpp_sen(i) == 0) {
                        g_setColor(g, ori_s.getcolor(i));
                    } else if (ori_s.get_tpp_sen(i) == 1) {
                        g.setColor(ori_s.get_tpp_sen_color(i));
                    }


                    s_tv.set(camera.object2TV(ori_s.get(i)));
                    //a.set(s_tv.geta()); b.set(s_tv.getb());
                    a.set(s_tv.getax() + 0.000001, s_tv.getay() + 0.000001);
                    b.set(s_tv.getbx() + 0.000001, s_tv.getby() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                    g.drawLine((int) a.getx(), (int) a.gety(), (int) b.getx(), (int) b.gety()); //直線

                    if (fTenkaizuSenhaba < 2.0f) {//頂点の黒い正方形を描く
                        g.setColor(Color.black);
                        //int i_haba=1;
                        int i_haba = ir_ten;
                        g.fillRect((int) a.getx() - i_haba, (int) a.gety() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                        g.fillRect((int) b.getx() - i_haba, (int) b.gety() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く
                    }

                    if (fTenkaizuSenhaba >= 2.0f) {//  太線
                        g2.setStroke(new BasicStroke(1.0f + fTenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (ir_ten != 0) {
                            int i_haba = (int) fTenkaizuSenhaba + ir_ten;//int i_haba=2;
                            double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_ten;//int i_haba=2;


                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.fillOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円


                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.drawOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(b.getx() - d_haba, b.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.fillOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(b.getx() - d_haba, b.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.drawOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円
                        }
                    }
                }
            }

        }

        //System.out.println(" E 20170201_6");

        //展開図の描画  補助活線以外の折線
        if (i_cp_hyouji == 1) {

            g.setColor(Color.black);

            float[] dash_M1 = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
            float[] dash_M2 = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
            float[] dash_V = {8.0f, 8.0f};//破線

            g.setColor(Color.black);
            for (int i = 1; i <= ori_s.getsousuu(); i++) {
                if (ori_s.getcolor(i) != 3) {
                    if (i_orisen_hyougen == 1) {
                        g_setColor(g, ori_s.getcolor(i));
                        g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                    }

                    if (i_orisen_hyougen == 2) {
                        g_setColor(g, ori_s.getcolor(i));
                        if (ori_s.getcolor(i) == 0) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                        }//基本指定A　　線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 1) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                        }//一点鎖線//線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 2) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                        }//破線//線の太さや線の末端の形状
                    }

                    if (i_orisen_hyougen == 3) {
                        if (ori_s.getcolor(i) == 0) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                        }//基本指定A　　線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 1) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                        }//一点鎖線//線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 2) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                        }//破線//線の太さや線の末端の形状
                    }

                    if (i_orisen_hyougen == 4) {
                        if (ori_s.getcolor(i) == 0) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                        }//基本指定A　　線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 1) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M2, 0.0f));
                        }//二点鎖線//線の太さや線の末端の形状
                        if (ori_s.getcolor(i) == 2) {
                            g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                        }//破線//線の太さや線の末端の形状
                    }


                    s_tv.set(camera.object2TV(ori_s.get(i)));
                    //a.set(s_tv.geta()); b.set(s_tv.getb());
                    a.set(s_tv.getax() + 0.000001, s_tv.getay() + 0.000001);
                    b.set(s_tv.getbx() + 0.000001, s_tv.getby() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため


                    g.drawLine((int) a.getx(), (int) a.gety(), (int) b.getx(), (int) b.gety()); //直線


                    if (fTenkaizuSenhaba < 2.0f) {//頂点の黒い正方形を描く
                        g.setColor(Color.black);
                        //int i_haba=1;
                        int i_haba = ir_ten;
                        g.fillRect((int) a.getx() - i_haba, (int) a.gety() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                        g.fillRect((int) b.getx() - i_haba, (int) b.gety() - i_haba, 2 * i_haba + 1, 2 * i_haba + 1); //正方形を描く
                    }


                    if (fTenkaizuSenhaba >= 2.0f) {//  太線
                        g2.setStroke(new BasicStroke(1.0f + fTenkaizuSenhaba % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (ir_ten != 0) {
                            int i_haba = (int) fTenkaizuSenhaba + ir_ten;//int i_haba=2;
                            double d_haba = (double) fTenkaizuSenhaba / 2.0 + (double) ir_ten;//int i_haba=2;


                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.fillOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円


                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.drawOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(b.getx() - d_haba, b.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.fillOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(b.getx() - d_haba, b.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
                            //g.drawOval( (int)b.getx()-i_haba/2,(int)b.gety()-i_haba/2,i_haba,i_haba); //円
                        }

                    }
                }
            }
        }

        //i_mouse_modeA==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
        if (orihime_app.i_mouse_modeA == 61) {
            Point p1 = new Point();
            p1.set(camera.TV2object(p61_1));
            Point p2 = new Point();
            p2.set(camera.TV2object(p61_2));
            Point p3 = new Point();
            p3.set(camera.TV2object(p61_3));
            Point p4 = new Point();
            p4.set(camera.TV2object(p61_4));

            s_step[1].set(p1, p2); //縦線
            s_step[2].set(p2, p3); //横線
            s_step[3].set(p3, p4); //縦線
            s_step[4].set(p4, p1); //横線

            s_step[1].setcolor(6);
            s_step[2].setcolor(6);
            s_step[3].setcolor(6);
            s_step[4].setcolor(6);
        }

        //線分入力時の一時的なs_step線分を描く　

        if ((orihime_app.i_mouse_modeA == 61) && (i_egaki_dankai != 4)) {
		} else {
            for (int i = 1; i <= i_egaki_dankai; i++) {
                g_setColor(g, s_step[i].getcolor());
                g2.setStroke(new BasicStroke(fTenkaizuSenhaba, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

                s_tv.set(camera.object2TV(s_step[i]));
                //a.set(s_tv.geta()); b.set(s_tv.getb());
                a.set(s_tv.getax() + 0.000001, s_tv.getay() + 0.000001);
                b.set(s_tv.getbx() + 0.000001, s_tv.getby() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため


                g.drawLine((int) a.getx(), (int) a.gety(), (int) b.getx(), (int) b.gety()); //直線
                int i_haba_nyuiiryokuji = 3;
                if (i_kou_mitudo_nyuuryoku == 1) {
                    i_haba_nyuiiryokuji = 2;
                }

                if (s_step[i].getiactive() == 1) {
                    g.fillOval((int) a.getx() - i_haba_nyuiiryokuji, (int) a.gety() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                    //g.fillOval( (int)b.getx()-i_haba_nyuiiryokuji,(int)b.gety()-i_haba_nyuiiryokuji,2*i_haba_nyuiiryokuji,2*i_haba_nyuiiryokuji); //円
                }
                if (s_step[i].getiactive() == 2) {
                    //g.fillOval( (int)a.getx()-i_haba_nyuiiryokuji,(int)a.gety()-i_haba_nyuiiryokuji,2*i_haba_nyuiiryokuji,2*i_haba_nyuiiryokuji); //円
                    g.fillOval((int) b.getx() - i_haba_nyuiiryokuji, (int) b.gety() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                }
                if (s_step[i].getiactive() == 3) {
                    g.fillOval((int) a.getx() - i_haba_nyuiiryokuji, (int) a.gety() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                    g.fillOval((int) b.getx() - i_haba_nyuiiryokuji, (int) b.gety() - i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji, 2 * i_haba_nyuiiryokuji); //円
                }


                //g.fillOval( (int)b.getx()-i_haba,(int)b.gety()-i_haba,2*i_haba,2*i_haba); //円
            }
        }
        //候補入力時の候補を描く//System.out.println("_");
        //g2.setStroke(new BasicStroke(fTenkaizuSenhaba+1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A
        g2.setStroke(new BasicStroke(fTenkaizuSenhaba + 0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A


        for (int i = 1; i <= i_kouho_dankai; i++) {
            g_setColor(g, s_kouho[i].getcolor());

            s_tv.set(camera.object2TV(s_kouho[i]));
            //a.set(s_tv.geta()); b.set(s_tv.getb());
            a.set(s_tv.getax() + 0.000001, s_tv.getay() + 0.000001);
            b.set(s_tv.getbx() + 0.000001, s_tv.getby() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            g.drawLine((int) a.getx(), (int) a.gety(), (int) b.getx(), (int) b.gety()); //直線
            //int i_haba=ir_ten   +1;
            int i_haba = ir_ten + 5;
            //g.fillRect( (int)a.getx()-i_haba,(int)a.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
            //g.fillRect( (int)b.getx()-i_haba,(int)b.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く

            if (s_kouho[i].getiactive() == 1) {
                //g.fillRect( (int)a.getx()-i_haba,(int)a.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                g.drawLine((int) a.getx() - i_haba, (int) a.gety(), (int) a.getx() + i_haba, (int) a.gety()); //直線
                g.drawLine((int) a.getx(), (int) a.gety() - i_haba, (int) a.getx(), (int) a.gety() + i_haba); //直線
//g2.draw(new Ellipse2D.Double(a.getx()-(double)i_haba, a.gety()-(double)i_haba, 2.0*(double)i_haba,2.0*(double)i_haba));
            }
            if (s_kouho[i].getiactive() == 2) {
                //g.fillRect( (int)b.getx()-i_haba,(int)b.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く
                g.drawLine((int) b.getx() - i_haba, (int) b.gety(), (int) b.getx() + i_haba, (int) b.gety()); //直線
                g.drawLine((int) b.getx(), (int) b.gety() - i_haba, (int) b.getx(), (int) b.gety() + i_haba); //直線
//g2.draw(new Ellipse2D.Double(b.getx()-(double)i_haba, b.gety()-(double)i_haba, 2.0*(double)i_haba,2.0*(double)i_haba));

            }
            if (s_kouho[i].getiactive() == 3) {
                //g.fillRect( (int)a.getx()-i_haba,(int)a.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                //g.fillRect( (int)b.getx()-i_haba,(int)b.gety()-i_haba,2*i_haba+1,2*i_haba+1); //正方形を描く
                g.drawLine((int) a.getx() - i_haba, (int) a.gety(), (int) a.getx() + i_haba, (int) a.gety()); //直線
                g.drawLine((int) a.getx(), (int) a.gety() - i_haba, (int) a.getx(), (int) a.gety() + i_haba); //直線
//g2.draw(new Ellipse2D.Double(a.getx()-(double)i_haba, a.gety()-(double)i_haba, 2.0*(double)i_haba,2.0*(double)i_haba));

                g.drawLine((int) b.getx() - i_haba, (int) b.gety(), (int) b.getx() + i_haba, (int) b.gety()); //直線
                g.drawLine((int) b.getx(), (int) b.gety() - i_haba, (int) b.getx(), (int) b.gety() + i_haba); //直線
//g2.draw(new Ellipse2D.Double(b.getx()-(double)i_haba, b.gety()-(double)i_haba, 2.0*(double)i_haba,2.0*(double)i_haba));


            }
        }

        g.setColor(Color.black);

        //円入力時の一時的な線分を描く　
        //g.setColor(Color.cyan);
        for (int i = 1; i <= i_en_egaki_dankai; i++) {
            g_setColor(g, e_step[i].getcolor());
            a.set(camera.object2TV(e_step[i].get_tyuusin()));//この場合のs_tvは描画座標系での円の中心の位置
            //a.set(s_tv.geta()); b.set(s_tv.getb());
            a.set(a.getx() + 0.000001, a.gety() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            //g.drawLine( (int)a.getx(),(int)a.gety(),(int)b.getx(),(int)b.gety()); //直線
            double d_haba = e_step[i].getr() * camera.get_camera_bairitsu_x();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。

            //g2.fill(new Ellipse2D.Double(a.getx()-d_haba, a.gety()-d_haba, 2.0*d_haba,2.0*d_haba));
            g2.draw(new Ellipse2D.Double(a.getx() - d_haba, a.gety() - d_haba, 2.0 * d_haba, 2.0 * d_haba));
            //g.drawOval( (int)a.getx()-i_haba/2,(int)a.gety()-i_haba/2,i_haba,i_haba); //円
        }


        g.setColor(Color.black);


        //text_cp_setumei=text_cp_setumei+"aaaaaaaaaaaaaaaaa";
        if (i_bun_hyouji == 1) {
            g.drawString(text_cp_setumei, 120, 120);
        }
        //if(i_bun_hyouji==1){g.drawString(text_cp_setumei2,120,120); }

        //System.out.println(" E 20170201_8");
    }


    // -------------------------------------------------------------------------------------------------------------------------------
    public void g_setColor(Graphics g, int i) {
/*

		Color.black       黒を表します
		Color.blue        青を表します
		Color.cyan        シアンを表します
		Color.darkGray    ダークグレイを表します
		Color.gray        グレイを表します
		Color.green       緑を表します
		Color.lightGray   ライトグレイを表します
		Color.magenta     マゼンタを表します
		Color.orange      オレンジを表します
		Color.pink        ピンクを表します
		Color.red         赤を表します
		Color.white       白を表します
		Color.yellow      黄を表します
*/

        //icol=0 black
        //icol=1 red
        //icol=2 blue
        //icol=3 cyan
        //icol=4 orange
        //icol=5 mazenta
        //icol=6 green
        //icol=7 yellow
        //icol=8 new Color(210,0,255) //紫

        if (i == 0) {
            g.setColor(Color.black);
            return;
        }
        if (i == 1) {
            g.setColor(Color.red);
            return;
        }
        if (i == 2) {
            g.setColor(Color.blue);
            return;
        }
        //g.setColor(new Color(100, 200,200));この色は補助線用に使った方がいいかも
        if (i == 3) {
            g.setColor(new Color(100, 200, 200));
            return;
        }
        //if(i==3){g.setColor(Color.cyan);return;}
        if (i == 4) {
            g.setColor(Color.orange);
            return;
        }
        if (i == 5) {
            g.setColor(Color.magenta);
            return;
        }
        if (i == 6) {
            g.setColor(Color.green);
            return;
        }
        if (i == 7) {
            g.setColor(Color.yellow);
            return;
        }
        if (i == 8) {
            g.setColor(new Color(210, 0, 255));
            return;
        }


    }


    public void set_i_egaki_dankai(int i) {
        i_egaki_dankai = i;
    }

    public void set_i_en_egaki_dankai(int i) {
        i_en_egaki_dankai = i;
    }


    public void set_id_kakudo_kei(int i) {
        id_kakudo_kei = i;
    }


// ------------------------------------

    //	public void set_i_hanasi(int i){i_hanasi=i;}
// ------------------------------------
    public void set_i_kou_mitudo_nyuuryoku(int i) {
        i_kou_mitudo_nyuuryoku = i;
    }

// ------------------------------------


    // *************************************************************************************
//--------------------------
    public void add_en(Circle e0) {
        add_en(e0.getx(), e0.gety(), e0.getr(), e0.getcolor());
    }

    //--------------------------
//--------------------------
    public void add_en(Point t0, double dr, int ic) {
        add_en(t0.getx(), t0.gety(), dr, ic);
    }

    //--------------------------
    public void add_en(double dx, double dy, double dr, int ic) {
        ori_s.add_en(dx, dy, dr, ic);

        int imin = 1;
        int imax = ori_s.cir_size() - 1;
        int jmin = ori_s.cir_size();
        int jmax = ori_s.cir_size();

        ori_s.en_en_kouten(imin, imax, jmin, jmax);
        ori_s.Senbun_en_kouten(1, ori_s.getsousuu(), jmin, jmax);

    }


    //--------------------------
    public int addsenbun_hojyo(Line s0) {
        hoj_s.addsenbun(s0);

        return 1;
    }

    //--------------------------------------------
    public int addsenbun(Line s0) {//0=変更なし、1=色の変化のみ、2=線分追加

        ori_s.addsenbun(s0);//ori_sのsenbunの最後にs0の情報をを加えるだけ
        int sousuu_old = ori_s.getsousuu();
        ori_s.Senbun_en_kouten(ori_s.getsousuu(), ori_s.getsousuu(), 1, ori_s.cir_size());

        ori_s.kousabunkatu(1, sousuu_old - 1, sousuu_old, sousuu_old);

        return 1;
    }

    //------------------------------------------------------
    public Point get_moyori_ten_orisen_en(Point t0) {//
        //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
        //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
        Point t1 = new Point(); //折線の端点

        Point t3 = new Point(); //円の中心

        t1.set(ori_s.mottomo_tikai_Ten(t0));//ori_s.mottomo_tikai_Ten_sagasiは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        t3.set(ori_s.mottomo_tikai_Tyuusin(t0));//ori_s.mottomo_tikai_Ten_sagasiは近い点がないと p_return.set(100000.0,100000.0)と返してくる
        if (t0.kyori2jyou(t1) > t0.kyori2jyou(t3)) {
            t1.set(t3);
        }


        //if(kus.jyoutai()==0){return t1;}


        //if( t0.kyori2jyou(t1)>  t0.kyori2jyou(kus.moyori_kousi_ten(t0)) ){return kus.moyori_kousi_ten(t0);}
        return t1;
    }

//------------------------------


    public Point get_moyori_ten(Point t0) {
        //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
        //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点

        //System.out.println("*************** get_moyori_ten :20201024");
        Point t1 = new Point(); //折線の端点

        Point t3 = new Point(); //円の中心

        t1.set(ori_s.mottomo_tikai_Ten(t0));//ori_s.mottomo_tikai_Ten_sagasiは近い点がないと p_return.set(100000.0,100000.0)と返してくる

        t3.set(ori_s.mottomo_tikai_Tyuusin(t0));//ori_s.mottomo_tikai_Ten_sagasiは近い点がないと p_return.set(100000.0,100000.0)と返してくる
        if (t0.kyori2jyou(t1) > t0.kyori2jyou(t3)) {
            t1.set(t3);
        }


        if (kus.jyoutai() == 0) {
            return t1;
        }


        if (t0.kyori2jyou(t1) > t0.kyori2jyou(kus.moyori_kousi_ten(t0))) {
            return kus.moyori_kousi_ten(t0);
        }
        return t1;
    }

    //------------------------------
    public Line get_moyori_senbun(Point t0) {
        return ori_s.mottomo_tikai_Senbun(t0);
    }

    //------------------------------------------------------
    public Line get_moyori_step_senbun(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;//Senbun s1 =new Senbun(100000.0,100000.0,100000.0,100000.1);
        for (int i = imin; i <= imax; i++) {
            double sk = oc.kyori_senbun(t0, s_step[i]);
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }

        // if(minrid==0){return s1;}

        return s_step[minrid];
        //return ori_s.mottomo_tikai_Senbun(t0);
    }


    //------------------------------
    public Circle get_moyori_ensyuu(Point t0) {
        return ori_s.mottomo_tikai_ensyuu(t0);
    }

    //------------------------------------------------------
    public Circle get_moyori_step_ensyuu(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;
        for (int i = imin; i <= imax; i++) {
            double ek = oc.kyori_ensyuu(t0, e_step[i]);
            if (minr > ek) {
                minr = ek;
                minrid = i;
            }//円周に近いかどうか
        }
        return e_step[minrid];
    }


    public void set_s_step_iactive(int ia) {

        for (int i = 0; i < 1024; i++) {
            s_step[i].setiactive(ia);
        }


    }

//--------------------------------------------------------------------------------------
//マウス操作----------------------------------------------------------------------------
//--------------------------------------------------------------------------------------
//	Ten p =new Ten();

    //動作モデル001--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_001(Point p0, int i_c) {//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点が候補点となる。近くに既成の点が無いときは候補点無しなので候補点の表示も無し。
        if (i_kou_mitudo_nyuuryoku == 1) {
            s_kouho[1].setiactive(3);
            i_kouho_dankai = 0;
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_kouho_dankai = 1;
                s_kouho[1].set(moyori_point, moyori_point);
                s_kouho[1].setcolor(i_c);
            }
        }
    }


    //動作モデル002--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_002(Point p0, int i_c) {//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
        if (i_kou_mitudo_nyuuryoku == 1) {
            s_kouho[1].setiactive(3);
            p.set(camera.TV2object(p0));
            i_kouho_dankai = 1;
            moyori_point.set(get_moyori_ten(p));

            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }

            s_kouho[1].setcolor(i_c);
            return;
        }
    }

    //動作モデル003--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_003(Point p0, int i_c) {//マウスで選択できる候補点を表示する。常にマウスの位置自身が候補点となる。
        if (i_kou_mitudo_nyuuryoku == 1) {
            //s_kouho[1].setiactive(3);
            p.set(camera.TV2object(p0));
            i_kouho_dankai = 1;
            s_kouho[1].set(p, p);

            s_kouho[1].setcolor(i_c);
            return;
        }
    }


    //動作モデル00a--------------------------------------------------------------------------------------------------------
    //マウスクリック（マウスの近くの既成点を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（マウスの近くの既成点を選択）してから目的の処理をする雛形セット

    //マウスを動かしたとき----------------------------------------------
    public void mMoved_m_00a(Point p0, int i_c) {
        mMoved_m_001(p0, i_c);
    }//近い既存点のみ表示

    //マウスクリック----------------------------------------------------
    public void mPressed_m_00a(Point p0, int i_c) {
        i_egaki_dankai = 1;
        s_step[1].setiactive(2);
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            i_egaki_dankai = 0;
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(i_c);
    }

    //マウスドラッグ---------------------------------------------------
    public void mDragged_m_00a(Point p0, int i_c) {  //近い既存点のみ表示

        p.set(camera.TV2object(p0));
        s_step[1].seta(p);

        if (i_kou_mitudo_nyuuryoku == 1) {
            i_kouho_dankai = 0;
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_kouho_dankai = 1;
                s_kouho[1].set(moyori_point, moyori_point);
                s_kouho[1].setcolor(i_c);
                s_step[1].seta(s_kouho[1].geta());
            }
        }
    }

    //マウスリリース--------------------------------------------------
    public void mReleased_m_00a(Point p0) {
        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;

            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                s_step[1].seta(moyori_point);
                if (s_step[1].getnagasa() > 0.00000001) {
                    //やりたい動作はここに書く
                    //addsenbun(s_step[1]);
                    //kiroku();
                }
            }
        }
    }


    //動作モデル00b--------------------------------------------------------------------------------------------------------
    //マウスクリック（近くの既成点かマウス位置を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（近くの既成点かマウス位置を選択）してから目的の処理をする雛形セット

    //マウスを動かしたとき----------------------------------------------
    public void mMoved_m_00b(Point p0, int i_c) {
        mMoved_m_002(p0, i_c);
    }//近くの既成点かマウス位置表示

    //マウスクリック----------------------------------------------------
    public void mPressed_m_00b(Point p0, int i_c) {
        i_egaki_dankai = 1;
        s_step[1].setiactive(2);
        p.set(camera.TV2object(p0));
        s_step[1].set(p, p);

        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            s_step[1].set(p, moyori_point);
        }

        s_step[1].setcolor(i_c);
    }

    //マウスドラッグ---------------------------------------------------
    public void mDragged_m_00b(Point p0, int i_c) {  //近くの既成点かマウス位置表示

        p.set(camera.TV2object(p0));
        s_step[1].seta(p);

        if (i_kou_mitudo_nyuuryoku == 1) {
            moyori_point.set(get_moyori_ten(p));
            i_kouho_dankai = 1;
            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }
            s_kouho[1].setcolor(i_c);
            s_step[1].seta(s_kouho[1].geta());
        }
    }

    //マウスリリース--------------------------------------------------
    public void mReleased_m_00b(Point p0) {
        i_egaki_dankai = 0;
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            //やりたい動作はここに書く

        }
    }


//--------------------------------------------
//28 28 28 28 28 28 28 28  i_mouse_modeA==28線分内分入力
    //動作概要
    //i_mouse_modeA==1と線分内分以外は同じ

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_28(Point p0) {
        mMoved_m_00a(p0, icol);//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    }

    //マウス操作(i_mouse_modeA==28線分内分入力 でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_28(Point p0) {
        i_egaki_dankai = 1;
        s_step[1].setiactive(2);
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            s_step[1].set(p, moyori_point);
            s_step[1].setcolor(icol);
            return;
        }
        s_step[1].set(p, p);
        s_step[1].setcolor(icol);
    }

    //マウス操作(i_mouse_modeA==28線分入力 でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_28(Point p0) {
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);

        if (i_kou_mitudo_nyuuryoku == 1) {
            moyori_point.set(get_moyori_ten(p));
            i_kouho_dankai = 1;
            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }
            s_kouho[1].setcolor(icol);
            s_step[1].seta(s_kouho[1].geta());
        }
        return;
    }

    //マウス操作(i_mouse_modeA==28線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_28(Point p0) {
        i_egaki_dankai = 0;
        p.set(camera.TV2object(p0));

        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));

        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            if ((d_naibun_s == 0.0) && (d_naibun_t == 0.0)) {
			}
            if ((d_naibun_s == 0.0) && (d_naibun_t != 0.0)) {
                addsenbun(s_step[1]);
            }
            if ((d_naibun_s != 0.0) && (d_naibun_t == 0.0)) {
                addsenbun(s_step[1]);
            }
            if ((d_naibun_s != 0.0) && (d_naibun_t != 0.0)) {
                Line s_ad = new Line();
                s_ad.setcolor(icol);
                double nx = (d_naibun_t * s_step[1].getbx() + d_naibun_s * s_step[1].getax()) / (d_naibun_s + d_naibun_t);
                double ny = (d_naibun_t * s_step[1].getby() + d_naibun_s * s_step[1].getay()) / (d_naibun_s + d_naibun_t);
                s_ad.set(s_step[1].getax(), s_step[1].getay(), nx, ny);
                addsenbun(s_ad);
                s_ad.set(s_step[1].getbx(), s_step[1].getby(), nx, ny);
                addsenbun(s_ad);
            }
            kiroku();
        }
    }


//1 1 1 1 1 1 01 01 01 01 01 11111111111 i_mouse_modeA==1線分入力 111111111111111111111111111111111
    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_01(Point p0) {
        if (i_kou_mitudo_nyuuryoku == 1) {
            s_kouho[1].setiactive(3);

            p.set(camera.TV2object(p0));
            i_kouho_dankai = 1;
            moyori_point.set(get_moyori_ten(p));

            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }

            //s_kouho[1].setcolor(icol);
            if (i_orisen_hojyosen == 0) {
                s_kouho[1].setcolor(icol);
            }
            if (i_orisen_hojyosen == 1) {
                s_kouho[1].setcolor(h_icol);
            }

            return;
        }
    }

    //マウス操作(i_mouse_modeA==1線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_01(Point p0) {
        i_egaki_dankai = 1;
        s_step[1].setiactive(2);
        p.set(camera.TV2object(p0));

        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            s_step[1].set(p, moyori_point);
            if (i_orisen_hojyosen == 0) {
                s_step[1].setcolor(icol);
            }
            if (i_orisen_hojyosen == 1) {
                s_step[1].setcolor(h_icol);
            }
            return;
        }

        s_step[1].set(p, p);
        if (i_orisen_hojyosen == 0) {
            s_step[1].setcolor(icol);
        }
        if (i_orisen_hojyosen == 1) {
            s_step[1].setcolor(h_icol);
        }
    }

    //マウス操作(i_mouse_modeA==1線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_01(Point p0) {
        p.set(camera.TV2object(p0));

        if (i_kou_mitudo_nyuuryoku == 0) {
            s_step[1].seta(p);
        }

        if (i_kou_mitudo_nyuuryoku == 1) {
            moyori_point.set(get_moyori_ten(p));
            i_kouho_dankai = 1;
            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }
            //s_kouho[1].setcolor(icol);
            if (i_orisen_hojyosen == 0) {
                s_kouho[1].setcolor(icol);
            }
            if (i_orisen_hojyosen == 1) {
                s_kouho[1].setcolor(h_icol);
            }
            s_step[1].seta(s_kouho[1].geta());
        }
        return;
    }


    public Point get_moyori_ten_sisuu(Point p0) {
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        Point kus_sisuu = new Point(kus.get_sisuu(moyori_point));
        return kus_sisuu;
        //text_cp_setumei2="sisuu="+(int)kus_sisuu.getx()+","+(int)kus_sisuu.gety();
        //System.out.println("sisuu="+kus_sisuu.getx()+","+kus_sisuu.gety());
        //System.out.println("sisuu="+(int)kus_sisuu.getx()+","+(int)kus_sisuu.gety());
    }


    //マウス操作(i_mouse_modeA==1線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_01(Point p0) {
        i_egaki_dankai = 0;
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            if (i_orisen_hojyosen == 0) {
                addsenbun(s_step[1]);
                kiroku();
            }
            if (i_orisen_hojyosen == 1) {
                addsenbun_hojyo(s_step[1]);
                h_kiroku();
            }
        }
        //text_cp_setumei="aaaaaa"+ori_s.getsousuu();
    }

    //11 11 11 11 11 11 11 11 11 11 11
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_11(Point p0) {
        mMoved_m_00a(p0, icol);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==11線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_11(Point p0) {
        mPressed_m_00a(p0, icol);
    }

    //マウス操作(i_mouse_modeA==11線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_11(Point p0) {
        mDragged_m_00a(p0, icol);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==11線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_11(Point p0) {
        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;

            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[1].seta(moyori_point);
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                if (s_step[1].getnagasa() > 0.00000001) {
                    addsenbun(s_step[1]);
                    kiroku();
                }
            }
        }
    }


//62 62 62 62 62 i_mouse_modeA==62 ボロノイ　 Voronoi 111111111111111111111111111111111


    ArrayList Senb_boro_1p = new ArrayList(); //ボロノイ図の一点の周りの線分

// ------------------------------------------


    //マウス操作(i_mouse_modeA==62ボロノイ　マウスを動かしたとき)を行う関数
    public void mMoved_A_62(Point p0) {
        if (i_kou_mitudo_nyuuryoku == 1) {
            s_kouho[1].setiactive(3);

            p.set(camera.TV2object(p0));
            i_kouho_dankai = 1;
            moyori_point.set(get_moyori_ten(p));

            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }

            //s_kouho[1].setcolor(icol);
            if (i_orisen_hojyosen == 0) {
                s_kouho[1].setcolor(icol);
            }
            if (i_orisen_hojyosen == 1) {
                s_kouho[1].setcolor(h_icol);
            }

            return;
        }
    }


//int s_step_no_saisyo_no_sen_no_bangou() {//s_step[i]で最初に線（長さが０でない）がでてくる番号を返す。線（長さが０でない）がない場合は0を返す
//		for (int i=1; i<=i_egaki_dankai; i++ ){if(s_step[i].getnagasa()>0.00000001){return i;}
//return 0;
//}


    // ------------------------------------------------------------------------------------------------------------
    int s_step_no_1_kara_rennzoku_no_ten_no_bangou() {//s_step[i]で最初から連続でTen（長さが０）が何番目まででてくるか番号を返す。点がない場合は0を返す
        int r_i = 0;
        int i_add = 1;
        for (int i = 1; i <= i_egaki_dankai; i++) {
            if (s_step[i].getnagasa() > 0.00000001) {
                i_add = 0;
            }
            r_i = r_i + i_add;
        }
        return r_i;
    }

    // ------------------------------------------------------------------------------------------------------------
    int i_mouse_modeA_62_ten_kasanari;//新たに加えたpが今までに加えたTenと重なっていない=0、重なっている=1

    //マウス操作(i_mouse_modeA==62ボロノイ　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_62(Point p0) {
        p.set(camera.TV2object(p0));

        //i_egaki_dankaiが従来のボロノイ母点だけになるように整理(まだ、点pをs_stepとしてボロノイ母点に加えるかどうかは決めていない)
        i_egaki_dankai = s_step_no_1_kara_rennzoku_no_ten_no_bangou();//Tenの数


        //両端が新たに加えたpの最寄点からなる点状の線分s_tempを求める（最寄点がない場合はs_tempの両端はp）
        Line s_temp = new Line();
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            s_temp.set(moyori_point, moyori_point);
            s_temp.setcolor(5);
        } else {
            s_temp.set(p, p);
            s_temp.setcolor(5);
        }


        //新たに加えたpが今までに加えたTenと重なっていないことを確認
        i_mouse_modeA_62_ten_kasanari = 0;
/*
		for (int i=1; i<=i_egaki_dankai; i++ ){
			if(oc.kyori(s_step[i].geta(),s_temp.geta())<=0.00000001){
				i_mouse_modeA_62_ten_kasanari=i;
			}
		}
*/
        for (int i = 1; i <= i_egaki_dankai; i++) {
            if (oc.kyori(s_step[i].geta(), s_temp.geta()) <= d_hantei_haba) {
                i_mouse_modeA_62_ten_kasanari = i;
            }
        }


        //新たに加えたpが今までに加えたTenと重なっていないことを確認 ここまで

        if (i_mouse_modeA_62_ten_kasanari == 0) {
            //oc.hyouji("　");
            //oc.hyouji("新しいボロノイ母点を加える　開始ーーーーーーーーーーーーーーーーーーーーー");

            //１個の新しいボロノイ母点を加える(ここでやっと、点pをs_stepとしてボロノイ母点に加えると決まった)
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(s_temp);
            s_step[i_egaki_dankai].setiactive(3);//iactive=3の線は両端に円が描かれる。iactive=1の線はa端のみに円が描かれる。iactive=2の線はb端のみに円が描かれる

            //今までのボロノイ図を元に、１個の新しいボロノイ母点を加えたボロノイ図を作る--------------------------------------
            //System.out.println("---------------------------");
            //System.out.println("voronoi_() start");

            //voronoi_01();//低速、エラーはほとんどないはず
            voronoi_02();//高速、もしかしてエラー残っているかも

            //System.out.println("voronoi_() stop");


        } else if (i_mouse_modeA_62_ten_kasanari != 0) {//順番がi_mouse_modeA_62_ten_kasanariのボロノイ母点を削除
            //oc.hyouji("　");
            //oc.hyouji("ボロノイ母点を削除　開始ーーーーーーーーーーーーーーーーーーーーー");

            //順番がi_mouse_modeA_62_ten_kasanariのボロノイ母点と順番が最後(=i_egaki_dankai)のボロノイ母点を入れ替える
            //s_step[i]の入れ替え
            Line S_irekae = new Line();
            S_irekae.set(s_step[i_mouse_modeA_62_ten_kasanari]);
            s_step[i_mouse_modeA_62_ten_kasanari].set(s_step[i_egaki_dankai]);
            s_step[i_egaki_dankai].set(S_irekae);


            for (int j = 1; j <= ori_v.getsousuu(); j++) {
                //ori_v内の線分のiactiveの入れ替え
                if (ori_v.getiactive(j) == i_mouse_modeA_62_ten_kasanari) {
                    ori_v.setiactive(j, i_egaki_dankai);
                } else if (ori_v.getiactive(j) == i_egaki_dankai) {
                    ori_v.setiactive(j, i_mouse_modeA_62_ten_kasanari);
                }

                //ori_v内の線分のicolの入れ替え
                if (ori_v.getcolor(j) == i_mouse_modeA_62_ten_kasanari) {
                    ori_v.setcolor(j, i_egaki_dankai);
                } else if (ori_v.getcolor(j) == i_egaki_dankai) {
                    ori_v.setcolor(j, i_mouse_modeA_62_ten_kasanari);
                }
            }


            //順番が最後(=i_egaki_dankai)ののボロノイ母点を削除

            i_egaki_dankai = i_egaki_dankai - 1;

            PolygonStore ori_v_temp = new PolygonStore();    //修正用のボロノイ図の線を格納する

            //ori_vの線分を最初に全て非選択にする
            ori_v.unselect_all();

            //i_egaki_dankai+1のボロノイ母点からのボロノイ線分を選択状態にする
            Line s_tem = new Line();
            Line s_tem2 = new Line();
            for (int j = 1; j <= ori_v.getsousuu(); j++) {
                s_tem.set(ori_v.get(j));//s_temとしてボロノイ母点からのボロノイ線分か判定
                if (s_tem.getiactive() == i_egaki_dankai + 1) {//ボロノイ線分の2つのボロノイ頂点はiactiveとcolorに記録されている
                    ori_v.select(j);
                    for (int h = 1; h <= ori_v.getsousuu(); h++) {
                        s_tem2.set(ori_v.get(h));
                        if (s_tem.getcolor() == s_tem2.getcolor()) {
                            ori_v.select(h);
                        }
                        if (s_tem.getcolor() == s_tem2.getiactive()) {
                            ori_v.select(h);
                        }
                    }


                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    Senb_boro_1p_motome(s_tem.getcolor());

                    for (int i = 0; i < Senb_boro_1p.size(); i++) {
                        Line add_S = new Line();
                        add_S.set((Line) Senb_boro_1p.get(i));
                        Line add_S2 = new Line();


                        //ori_v_tempにadd_Sを追加するかどうかの事前チェック
                        int i_tuika = 1;//1なら追加する。0なら追加しない。
                        for (int h = 1; h <= ori_v_temp.getsousuu(); h++) {
                            add_S2.set(ori_v_temp.get(h));
                            if ((add_S.getcolor() == add_S2.getcolor()) && (add_S.getiactive() == add_S2.getiactive())) {
                                i_tuika = 0;
                            }
                            if ((add_S.getcolor() == add_S2.getiactive()) && (add_S.getiactive() == add_S2.getcolor())) {
                                i_tuika = 0;
                            }
                        }
                        //ori_v_tempにadd_Sを追加するかどうかの事前チェックはここまで

                        if (i_tuika == 1) {
                            ori_v_temp.addsenbun((Line) Senb_boro_1p.get(i));
                        }
                    }
                } else if (s_tem.getcolor() == i_egaki_dankai + 1) {//ボロノイ線分の2つのボロノイ頂点はiactiveとcolorに記録されている
                    ori_v.select(j);
                    for (int h = 1; h <= ori_v.getsousuu(); h++) {
                        s_tem2.set(ori_v.get(h));
                        if (s_tem.getiactive() == s_tem2.getcolor()) {
                            ori_v.select(h);
                        }
                        if (s_tem.getiactive() == s_tem2.getiactive()) {
                            ori_v.select(h);
                        }
                    }

                    //削除されるi_egaki_dankai+1番目のボロノイ母点と組になる、もう一つのボロノイ母点を取り囲むボロノイ線分のアレイリストを得る。
                    Senb_boro_1p_motome(s_tem.getiactive());

                    for (int i = 0; i < Senb_boro_1p.size(); i++) {
                        Line add_S = new Line();
                        add_S.set((Line) Senb_boro_1p.get(i));
                        Line add_S2 = new Line();

                        //ori_v_tempにadd_Sを追加するかどうかの事前チェック
                        int i_tuika = 1;//1なら追加する。0なら追加しない。
                        for (int h = 1; h <= ori_v_temp.getsousuu(); h++) {
                            add_S2.set(ori_v_temp.get(h));
                            if ((add_S.getcolor() == add_S2.getcolor()) && (add_S.getiactive() == add_S2.getiactive())) {
                                i_tuika = 0;
                            }
                            if ((add_S.getcolor() == add_S2.getiactive()) && (add_S.getiactive() == add_S2.getcolor())) {
                                i_tuika = 0;
                            }
                        }
                        //ori_v_tempにadd_Sを追加するかどうかの事前チェックはここまで

                        if (i_tuika == 1) {
                            ori_v_temp.addsenbun((Line) Senb_boro_1p.get(i));
                        }
                    }


                }

            }
            //選択状態のものを削除
            ori_v.del_selected_senbun_hayai();
            ori_v.del_V_all(); //この行はいらないかも

            //ori_v_tempのボロノイ線分をボロノイ母点に加える
            //ori_v_temp.hyouji("ori_v_temp---------------------");
            for (int j = 1; j <= ori_v_temp.getsousuu(); j++) {
                Line s_t = new Line();
                s_t.set(ori_v_temp.get(j));
                //s_t.hyouji("  s_t  ");
                //addsenbun_voronoi(s_t);//addsenbun_voronoiは交差分割をしている。交差分割でiactiveを使うので、iactiveを別途何かに転用したルーチンでは交差分割を使用できないのでaddsenbun_voronoiも使用できない
                ori_v.addsenbun(s_t);
            }


            ori_v.del_V_all();

        }


        //ボロノイ図も表示するようにs_stepの後にボロノイ図の線を入れる

        int imax = ori_v.getsousuu();
        if (imax > 1020) {
            imax = 1020;
        }

        //System.out.println("ボロノイ図も表示するようにs_stepの後にボロノイ図の線を入れる前");
        //System.out.println("i_egaki_dankai="+i_egaki_dankai+" :  ori_v.getsousuu()= "+ori_v.getsousuu());

        for (int i = 1; i <= imax; i++) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(ori_v.get(i));
            //s_step[i_egaki_dankai].setiactive(3);
            s_step[i_egaki_dankai].setiactive(0);
            s_step[i_egaki_dankai].setcolor(5);
        }


    }


    //--------------------------------------------
    public int addsenbun_voronoi(Line s0) {//0=変更なし、1=色の変化のみ、2=線分追加

        ori_v.addsenbun(s0);//ori_vのsenbunの最後にs0の情報をを加えるだけ
        int sousuu_old = ori_v.getsousuu();
        ori_v.Senbun_en_kouten(ori_v.getsousuu(), ori_v.getsousuu(), 1, ori_v.cir_size());

        ori_v.kousabunkatu(1, sousuu_old - 1, sousuu_old, sousuu_old);

        return 1;
    }


    // -----------------------------------------------------------------------------
    //マウス操作(i_mouse_modeA==62ボロノイ　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_62(Point p0) {
    }

    // -----------------------------------------------------------------------------
    //マウス操作(i_mouse_modeA==62ボロノイ　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_62(Point p0) {
    }

    // ------------------------------------------
    public void voronoi_01() {//i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点とそれ以前のボロノイ頂点間の2等分線をori_vに追加

        for (int i_e_d = 1; i_e_d <= i_egaki_dankai - 1; i_e_d++) {
            addsenbun_voronoi(oc.nitoubunsen(s_step[i_e_d].geta(), s_step[i_egaki_dankai].geta(), 1000.0)); //kiroku();
        }

        //ボロノイに適合するか判定
        //ori_vの線分を最初に全て非選択にする
        ori_v.unselect_all();


        //ボロノイに適合しないものを選択状態にする
        Line s_tem = new Line();
        for (int j = 1; j <= ori_v.getsousuu(); j++) {

            //System.out.println("ボロノイ j= "+j);
            s_tem.set(ori_v.get(j));//s_temとしてボロノイに適合するか判定

            //s_tenのa端とボロノイの各頂点との距離の最短値v_min_aを求める
            double v_min_a = 1000000.0;
            for (int i = 1; i <= i_egaki_dankai; i++) {
                if (oc.kyori(s_step[i].geta(), s_tem.geta()) < v_min_a) {
                    v_min_a = oc.kyori(s_step[i].geta(), s_tem.geta());
                }
            }
            //System.out.println("v_min_a= "+v_min_a);
            //s_tenのb端とボロノイの各頂点との距離の最短値v_min_bを求める
            double v_min_b = 1000000.0;
            for (int i = 1; i <= i_egaki_dankai; i++) {
                if (oc.kyori(s_step[i].geta(), s_tem.getb()) < v_min_b) {
                    v_min_b = oc.kyori(s_step[i].geta(), s_tem.getb());
                }
            }
            //System.out.println("v_min_b= "+v_min_b);


            int a_tomo_b_tomo_mottomo_tikai_voronoi_tyouten_no_kazu = 0;//aともbとも最も近いボロノイ頂点の数　これが２なら対象線分はボロノイ図として残す
            for (int i = 1; i <= i_egaki_dankai; i++) {
                if (Math.abs(oc.kyori(s_step[i].geta(), s_tem.geta()) - v_min_a) < 0.00001) {
                    if (Math.abs(oc.kyori(s_step[i].geta(), s_tem.getb()) - v_min_b) < 0.00001) {
                        a_tomo_b_tomo_mottomo_tikai_voronoi_tyouten_no_kazu = a_tomo_b_tomo_mottomo_tikai_voronoi_tyouten_no_kazu + 1;


                    }
                }
            }
            //System.out.println("a_tomo_b_tomo_mottomo_tikai_voronoi_tyouten_no_kazu= "+a_tomo_b_tomo_mottomo_tikai_voronoi_tyouten_no_kazu);

            if (a_tomo_b_tomo_mottomo_tikai_voronoi_tyouten_no_kazu != 2) {

                ori_v.select(j);
            }


        }


        //選択状態のものを削除
        ori_v.del_selected_senbun_hayai();

        ori_v.del_V_all(); //この行はいらないかも


    }


//------------------------------

    public void voronoi_02_01(int tyuusinn_ten_bangou, Line add_line) {
        //i_egaki_dankai番目のボロノイ頂点は　　s_step[i_egaki_dankai].geta()　　　

        //System.out.println("(0a)_add_senbun.getiactive()   = "+add_senbun.getiactive());
        //加える線分について整理
        //Senbun add_senbun =new Senbun();
        //add_senbun.set(oc.nitoubunsen(s_step[i_e_d].geta(),s_step[i_egaki_dankai].geta(),1000.0));
        StraightLine add_straightLine = new StraightLine(add_line);


        int i_saisyo = Senb_boro_1p.size() - 1;
        for (int i = i_saisyo; i >= 0; i--) {
            //既存の線分について整理
            Line kizon_line = new Line();
            kizon_line.set((Line) Senb_boro_1p.get(i));
            StraightLine kizon_straightLine = new StraightLine(kizon_line);


            //加える線分と既存の線分を戦わせる

//add_tyokusen.hyouji("     途中　add_tyokusen :");
//kizon_tyokusen.hyouji("     途中　kizon_tyokusen :");

            int i_heikou = oc.heikou_hantei(add_straightLine, kizon_straightLine, 0.0001);//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する


            if (i_heikou == 0) {//加える線分と既存の線分が非平行の場合
//System.out.println("     途中_加える線分と既存の線分が非平行");
                Point kouten = new Point();
                kouten.set(oc.kouten_motome(add_straightLine, kizon_straightLine));

                if ((add_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), kizon_line.geta()) <= 0) &&
                        (add_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), kizon_line.getb()) <= 0)) {
                    Senb_boro_1p.remove(i);
                } else if ((add_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), kizon_line.geta()) == 1) &&
                        (add_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), kizon_line.getb()) == -1)) {
                    kizon_line.set(kizon_line.geta(), kouten);
                    if (kizon_line.getnagasa() < 0.0000001) {
                        Senb_boro_1p.remove(i);
                    } else {
                        Senb_boro_1p.set(i, kizon_line);
                    }
                } else if ((add_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), kizon_line.geta()) == -1) &&
                        (add_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), kizon_line.getb()) == 1)) {
                    kizon_line.set(kouten, kizon_line.getb());
                    if (kizon_line.getnagasa() < 0.0000001) {
                        Senb_boro_1p.remove(i);
                    } else {
                        Senb_boro_1p.set(i, kizon_line);
                    }
                }

                //

                if ((kizon_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), add_line.geta()) <= 0) &&
                        (kizon_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), add_line.getb()) <= 0)) {
                    return;
                } else if ((kizon_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), add_line.geta()) == 1) &&
                        (kizon_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), add_line.getb()) == -1)) {
                    add_line.set(add_line.geta(), kouten);
                    if (add_line.getnagasa() < 0.0000001) {
                        return;
                    }
                } else if ((kizon_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), add_line.geta()) == -1) &&
                        (kizon_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), add_line.getb()) == 1)) {
                    add_line.set(kouten, add_line.getb());
                    if (add_line.getnagasa() < 0.0000001) {
                        return;
                    }
                }


            } else if (i_heikou == 1) {//加える線分と既存の線分が平行で２直線が一致しない場合
//System.out.println("     途中_加える線分と既存の線分が平行で２直線が一致しない");
                if (add_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), kizon_line.geta()) == -1) {
                    Senb_boro_1p.remove(i);
                } else if (kizon_straightLine.onajigawa(s_step[tyuusinn_ten_bangou].geta(), add_line.geta()) == -1) {
                    return;
                }


            } else if (i_heikou == 2) {//加える線分と既存の線分が平行で２直線が一致する場合
//System.out.println("     途中_加える線分と既存の線分が平行で２直線が一致する");
                return;
            }


            //if(add_tyokusen.senbun_kousa_hantei_kuwasii(senbun_b1p)==0){
            //if(add_tyokusen.senbun_kousa_hantei_kuwasii(new Senbun(s_step[i_egaki_dankai].geta(),senbun_b1p.geta()))==1){
            //	Senb_boro_1p.remove(i);
            //}
            //}


        }
        //System.out.println("  i_e_d="+i_e_d);
        //add_senbun.hyouji("voronoi_02_01_  add_senbun _最終");
        //System.out.println("(0b)_add_senbun.getiactive()   = "+add_senbun.getiactive());
        Senb_boro_1p.add(add_line);


    }


    // -----------------------------------------------

    public void Senb_boro_1p_motome(int tyuusinn_ten_bangou) {//s_stepがボロノイ母点だけを含む場合に使える。tyuusinn_ten_bangouの周りのボロノイ線分の集合としてSenb_boro_1pを得る
        //i_egaki_dankai番目のボロノイ頂点を取り囲むボロノイ線分のアレイリストを得る。//i_egaki_dankai番目のボロノイ頂点は　　s_step[i_egaki_dankai].geta()　　　
        Senb_boro_1p.clear();

        //System.out.println("i_egaki_dankai="+i_egaki_dankai);

        for (int i_e_d = 1; i_e_d <= i_egaki_dankai; i_e_d++) {
            if (i_e_d != tyuusinn_ten_bangou) {

                //加える線分を求める
                Line add_line = new Line();

                add_line.set(oc.nitoubunsen(s_step[i_e_d].geta(), s_step[tyuusinn_ten_bangou].geta(), 1000.0));

                if (i_e_d < tyuusinn_ten_bangou) {
                    add_line.setiactive(i_e_d);
                    add_line.setcolor(tyuusinn_ten_bangou);//ボロノイ線分の2つのボロノイ頂点はiactiveとcolorに記録する
                } else {
                    add_line.setiactive(tyuusinn_ten_bangou);
                    add_line.setcolor(i_e_d);//ボロノイ線分の2つのボロノイ頂点はiactiveとcolorに記録する
                }
                voronoi_02_01(tyuusinn_ten_bangou, add_line);

            }
        }


/*
		//できたSenb_boro_1pの表示
		oc.hyouji("-----できたSenb_boro_1pの表示-----");
		for (int i=0; i<Senb_boro_1p.size(); i++ ){
			Senbun tempS=new Senbun();tempS.set((Senbun)Senb_boro_1p.get(i));
			tempS.hyouji("   ");
		}
*/


    }

    // --------------------------------------------

    public void voronoi_02() {//i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点を取り囲むボロノイ線分のアレイリストを得る。
        Senb_boro_1p_motome(i_egaki_dankai);

        //20181109ここでori_v.の既存のボロノイ線分の整理が必要

        //ori_vの線分を最初に全て非選択にする
        ori_v.unselect_all();

        //
        Line s_mae = new Line();
        Line s_ato = new Line();

        for (int ia = 0; ia < Senb_boro_1p.size() - 1; ia++) {
            for (int ib = ia + 1; ib < Senb_boro_1p.size(); ib++) {

                s_mae.set((Line) Senb_boro_1p.get(ia));
                s_ato.set((Line) Senb_boro_1p.get(ib));

                StraightLine t_mae = new StraightLine(s_mae);
                StraightLine t_ato = new StraightLine(s_ato);

                int i_mae = s_mae.getiactive();//この場合iactiveには、そのボロノイ線分を加えたときの既存側のボロノイ母点の番号が入っている。
                int i_ato = s_ato.getiactive();//この場合iactiveには、そのボロノイ線分を加えたときの既存側のボロノイ母点の番号が入っている。


                if (i_mae > i_ato) {
                    int i_temp = i_mae;
                    i_mae = i_ato;
                    i_ato = i_temp;
                }

                //System.out.println("(1) i_mae=" +i_mae +" :  i_ato=" +i_ato);

                //新しいボロノイ母点を加えることでできる周囲のボロノイ線分が求まっている。このボロノイ線分の多角形を新セルということにする。
                //新セルをori_vに加える前に、新セルの内部に入り込んでいるori_vの既存線分がないように処理をする。

                //20181109ここでori_v.の既存のボロノイ線分(iactive()が必ずicolorより小さくなっている)を探す
                for (int j = 1; j <= ori_v.getsousuu(); j++) {
                    Line s_kizon = new Line();
                    s_kizon.set(ori_v.get(j));
                    //System.out.println("  (2) s_kizon.getiactive()=" +s_kizon.getiactive() +" :  s_kizon.getcolor()=" +s_kizon.getcolor());

                    int i_kizon_syou = s_kizon.getiactive();
                    int i_kizon_dai = s_kizon.getcolor();

                    if (i_kizon_syou > i_kizon_dai) {
                        i_kizon_dai = s_kizon.getiactive();
                        i_kizon_syou = s_kizon.getcolor();
                    }


                    if (i_kizon_syou == i_mae) {
                        if (i_kizon_dai == i_ato) {
                            //System.out.println("i_mae=" +i_mae +" :  i_ato=" +i_ato);

                            //System.out.println("ori_v.get(j)_  j=" +j);


//20181110ここポイント
//
//	-1		0		1
//-1 	何もせず	何もせず	交点まで縮小
// 0	何もせず	有り得ない	削除
// 1	交点まで縮小	削除		削除
//

                            //  if((ia+1==ib)||((ia==0)&&(ib==Senb_boro_1p.size()-1))){

                            Point kouten = new Point();
                            kouten.set(oc.kouten_motome(s_mae, s_kizon));

                            //System.out.println("kouten=" +kouten.getx()+" : "+kouten.gety());


                            if ((t_mae.onajigawa(s_step[i_egaki_dankai].geta(), s_kizon.geta()) >= 0) &&
                                    (t_mae.onajigawa(s_step[i_egaki_dankai].geta(), s_kizon.getb()) >= 0)) {
                                ori_v.select(j);
                            }

                            if ((t_mae.onajigawa(s_step[i_egaki_dankai].geta(), s_kizon.geta()) == -1) &&
                                    (t_mae.onajigawa(s_step[i_egaki_dankai].geta(), s_kizon.getb()) == 1)) {
                                //s_kizon.set(s_kizon.geta(),kouten);
                                ori_v.set(j, s_kizon.geta(), kouten);
                            }

                            if ((t_mae.onajigawa(s_step[i_egaki_dankai].geta(), s_kizon.geta()) == 1) &&
                                    (t_mae.onajigawa(s_step[i_egaki_dankai].geta(), s_kizon.getb()) == -1)) {
                                //s_kizon.set(kouten,s_kizon.getb());
                                ori_v.set(j, kouten, s_kizon.getb());
                            }

                            //  }else{
                            //		ori_v.select(j)	;
                            // }
                        }


                    }

                }


            }

        }

        //for (int i=1; i<=ori_v.getsousuu(); i++ ){System.out.println("    (1)  i= " + i +  ":  ori_v.get(i).getiactive()=  " +  ori_v.get(i).getiactive());}
        //選択状態のものを削除
        ori_v.del_selected_senbun_hayai();

        ori_v.del_V_all(); //この行はいらないかも
        //for (int i=1; i<=ori_v.getsousuu(); i++ ){System.out.println("    (2)  i= " + i +  ":  ori_v.get(i).getiactive()=  " +  ori_v.get(i).getiactive());}
        //


        //ori_vのsenbunの最後にSenb_boro_1pの線分ををを加える
        for (int i = 0; i < Senb_boro_1p.size(); i++) {
            Line add_S = new Line();
            add_S.set((Line) Senb_boro_1p.get(i));
            //System.out.println("  add_S.getiactive()=" +add_S.getiactive());
            ori_v.addsenbun((Line) Senb_boro_1p.get(i));
            //System.out.println("  ori_v.get(ori_v.getsousuu()).getiactive()=" +ori_v.get(ori_v.getsousuu()).getiactive());

        }


        //for (int i=1; i<=ori_v.getsousuu(); i++ ){System.out.println("    (3)  i= " + i +  ":  ori_v.get(i).getiactive()=  " +  ori_v.get(i).getiactive());}


    }


    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える


//-------------------------------------------------------------------------------------------------------

//--------------------------------------


    //5 5 5 5 5 55555555555555555    i_mouse_modeA==5　;線分延長モード
    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_05(Point p0) {
        mMoved_A_05or70(p0);
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_05(Point p0) {
        mPressed_A_05or70(p0);
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_05(Point p0) {
        mDragged_A_05or70(p0);
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_05(Point p0) {
        mReleased_A_05or70(p0);
    }

//------

    //70 70 70 70 70 70 70 70 70 70 70 70 70 70    i_mouse_modeA==70　;線分延長モード
    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_70(Point p0) {
        mMoved_A_05or70(p0);
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_70(Point p0) {
        mPressed_A_05or70(p0);
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_70(Point p0) {
        mDragged_A_05or70(p0);
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_70(Point p0) {
        mReleased_A_05or70(p0);
    }


//-------------------------------------------------------------------------------------------------------------------------------

    Narabebako_int_double entyou_kouho_nbox = new Narabebako_int_double();

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_05or70(Point p0) {
        mMoved_m_003(p0, icol);
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        i_kouho_dankai = 0;

        if (i_egaki_dankai == 0) {
            entyou_kouho_nbox.reset();
            i_egaki_dankai = 1;

            s_step[1].set(p, p);
            s_step[1].setcolor(5);//マゼンタ
            return;
        }

        if (i_egaki_dankai >= 2) {

            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(p, p);
            s_step[i_egaki_dankai].setcolor(5);//マゼンタ
            return;
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        if (i_egaki_dankai == 1) {
            s_step[i_egaki_dankai].setb(p);
        }
        if (i_egaki_dankai > 1) {
            s_step[i_egaki_dankai].set(p, p);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        moyori_line.set(get_moyori_senbun(p));


        if (i_egaki_dankai == 1) {

            s_step[1].setb(p);


            for (int i = 1; i <= ori_s.getsousuu(); i++) {
                int i_senbun_kousa_hantei = oc.line_intersect_decide(ori_s.get(i), s_step[1], 0.0001, 0.0001);
                int i_jikkou = 0;

                if (i_senbun_kousa_hantei == 1) {
                    i_jikkou = 1;
                }
                //if(i_senbun_kousa_hantei== 27 ){ i_jikkou=1;}
                //if(i_senbun_kousa_hantei== 28 ){ i_jikkou=1;}

                if (i_jikkou == 1) {
                    int_double i_d = new int_double(i, oc.kyori(s_step[1].geta(), oc.kouten_motome(ori_s.get(i), s_step[1])));
                    entyou_kouho_nbox.ire_i_tiisaijyun(i_d);
                }


            }
            if ((entyou_kouho_nbox.getsousuu() == 0) && (s_step[1].getnagasa() <= 0.000001)) {//延長する候補になる折線を選ぶために描いた線分s_step[1]が点状のときの処理
                if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                    int_double i_d = new int_double(ori_s.mottomo_tikai_senbun_sagasi(p), 1.0);//entyou_kouho_nboxに1本の情報しか入らないのでdoubleの部分はどうでもよいので適当に1.0にした。
                    entyou_kouho_nbox.ire_i_tiisaijyun(i_d);

                    s_step[1].setb(oc.sentaisyou_ten_motome(moyori_line.geta(), moyori_line.getb(), p));

                    s_step[1].set(//s_step[1]を短くして、表示時に目立たない様にする。
                            oc.ten_bai(oc.tyuukanten(s_step[1].geta(), s_step[1].getb()), s_step[1].geta(), 0.00001 / s_step[1].getnagasa())
                            ,
                            oc.ten_bai(oc.tyuukanten(s_step[1].geta(), s_step[1].getb()), s_step[1].getb(), 0.00001 / s_step[1].getnagasa())
                    );

                }

            }

            System.out.println(" entyou_kouho_nbox.getsousuu() = " + entyou_kouho_nbox.getsousuu());


            if (entyou_kouho_nbox.getsousuu() == 0) {
                i_egaki_dankai = 0;
                return;
            }
            if (entyou_kouho_nbox.getsousuu() >= 0) {

                i_egaki_dankai = 1 + entyou_kouho_nbox.getsousuu();

                for (int i = 2; i <= i_egaki_dankai; i++) {
                    s_step[i].set(ori_s.get(entyou_kouho_nbox.get_int(i - 1)));
                    s_step[i].setcolor(6);//グリーン
                }
                return;
            }
            return;
        }


        if (i_egaki_dankai >= 3) {
            if (oc.kyori_senbun(p, moyori_line) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                return;
            }

            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {


                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがあるかどうかを判断する。
                int i_senbun_entyou_mode = 0;// i_senbun_entyou_mode=0なら最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない。1ならある。
                for (int i = 1; i <= entyou_kouho_nbox.getsousuu(); i++) {
                    if (oc.line_intersect_decide(ori_s.get(entyou_kouho_nbox.get_int(i)), moyori_line, 0.000001, 0.000001) == 31) {//線分が同じならoc.senbun_kousa_hantei==31
                        i_senbun_entyou_mode = 1;
                    }
                }


                Line add_sen = new Line();
                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない場合
                if (i_senbun_entyou_mode == 0) {
                    int sousuu_old = ori_s.getsousuu();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getsousuu(); i++) {
                        //最初に選んだ線分と2番目に選んだ線分が平行でない場合
                        if (oc.heikou_hantei(ori_s.get(entyou_kouho_nbox.get_int(i)), moyori_line, 0.000001) == 0) { //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない
                            //s_step[1]とs_step[2]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                            Point kousa_point = new Point();
                            kousa_point.set(oc.kouten_motome(ori_s.get(entyou_kouho_nbox.get_int(i)), moyori_line));
                            //add_sen =new Senbun(kousa_ten,ori_s.get(entyou_kouho_nbox.get_int(i)).get_tikai_hasi(kousa_ten));
                            add_sen.seta(kousa_point);
                            add_sen.setb(ori_s.get(entyou_kouho_nbox.get_int(i)).get_tikai_hasi(kousa_point));


                            if (add_sen.getnagasa() > 0.00000001) {
                                if (orihime_app.i_mouse_modeA == 5) {
                                    add_sen.setcolor(icol);
                                }
                                if (orihime_app.i_mouse_modeA == 70) {
                                    add_sen.setcolor(ori_s.get(entyou_kouho_nbox.get_int(i)).getcolor());
                                }

                                //addsenbun(add_sen);
                                ori_s.addsenbun(add_sen);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                            }
                        }
                    }
                    ori_s.Senbun_en_kouten(sousuu_old, ori_s.getsousuu(), 1, ori_s.cir_size());//(3)
                    ori_s.kousabunkatu(1, sousuu_old, sousuu_old + 1, ori_s.getsousuu());//(4)


                }

                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがある場合
                if (i_senbun_entyou_mode == 1) {

                    int sousuu_old = ori_s.getsousuu();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getsousuu(); i++) {
                        Line moto_no_sen = new Line();
                        moto_no_sen.set(ori_s.get(entyou_kouho_nbox.get_int(i)));
                        Point p_point = new Point();
                        p_point.set(oc.kouten_motome(moto_no_sen, s_step[1]));

                        if (p_point.kyori(moto_no_sen.geta()) < p_point.kyori(moto_no_sen.getb())) {
                            moto_no_sen.a_b_koukan();
                        }
                        add_sen.set(kousatenmade_2(moto_no_sen));


                        if (add_sen.getnagasa() > 0.00000001) {
                            if (orihime_app.i_mouse_modeA == 5) {
                                add_sen.setcolor(icol);
                            }
                            if (orihime_app.i_mouse_modeA == 70) {
                                add_sen.setcolor(ori_s.get(entyou_kouho_nbox.get_int(i)).getcolor());
                            }

                            ori_s.addsenbun(add_sen);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                        }

                    }
                    ori_s.Senbun_en_kouten(sousuu_old, ori_s.getsousuu(), 1, ori_s.cir_size());//(3)
                    ori_s.kousabunkatu(1, sousuu_old, sousuu_old + 1, ori_s.getsousuu());//(4)


                }


                kiroku();


                i_egaki_dankai = 0;
                return;
            }
        }


    }

//--------------------------------------


//71 71 71 71 71 71 71 71 71 71 71 71 71 71    i_mouse_modeA==71　;線分延長モード

    int i_dousa_mode = 0;
    int i_dousa_mode_henkou_kanousei = 0;//動作モード変更可能性。0なら不可能、1なら可能。
    Point moyori_point_memo = new Point();

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_71(Point p0) {
        if (i_egaki_dankai == 0) {
            i_dousa_mode = 0;
            mMoved_A_01(p0);
            return;
        }

        if (i_dousa_mode == 1) {
            mMoved_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            mMoved_A_38(p0);
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_71(Point p0) {
        i_dousa_mode_henkou_kanousei = 0;

        p.set(camera.TV2object(p0));
        double hantei_kyori = 0.000001;

        if (p.kyori(moyori_point_memo) <= d_hantei_haba) {
            i_egaki_dankai = 0;
        }


        if (i_egaki_dankai == 0) {


            //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
            moyori_point.set(get_moyori_ten(p));
            moyori_point_memo.set(moyori_point);

            if (p.kyori(moyori_point) > d_hantei_haba) {
                moyori_point.set(p);
            }

            //moyori_tenを端点とする折線をNarabebakoに入れる
            Narabebako_int_double nbox = new Narabebako_int_double();
            for (int i = 1; i <= ori_s.getsousuu(); i++) {
                if ((0 <= ori_s.getcolor(i)) && (ori_s.getcolor(i) <= 2)) {
                    if (moyori_point.kyori(ori_s.geta(i)) < hantei_kyori) {
                        nbox.ire_i_tiisaijyun(new int_double(i, oc.kakudo(ori_s.geta(i), ori_s.getb(i))));
                    } else if (moyori_point.kyori(ori_s.getb(i)) < hantei_kyori) {
                        nbox.ire_i_tiisaijyun(new int_double(i, oc.kakudo(ori_s.getb(i), ori_s.geta(i))));
                    }
                }
            }
            if (nbox.getsousuu() % 2 == 0) {
                i_dousa_mode = 1;
                i_orisen_hojyosen = 0;
            }//moyori_tenを端点とする折線の数が偶数のときif{}内の処理をする
            if (nbox.getsousuu() % 2 == 1) {
                i_dousa_mode = 38;
                i_dousa_mode_henkou_kanousei = 1;
            }//moyori_tenを端点とする折線の数が奇数のときif{}内の処理をする

        }

        if (i_dousa_mode == 1) {
            mPressed_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            if (mPressed_A_38(p0) == 0) {
                if (i_egaki_dankai == 0) {
                    mPressed_A_71(p0);
                }
            }
        }


    }


    //マウス操作(ドラッグしたとき)を行う関数20200
    public void mDragged_A_71(Point p0) {
        if ((i_dousa_mode == 38) && (i_dousa_mode_henkou_kanousei == 1)) {
            //if(i_dousa_mode==38){
            p.set(camera.TV2object(p0));
            moyori_point_memo.set(moyori_point);
            if (p.kyori(moyori_point_memo) > d_hantei_haba) {
                i_dousa_mode = 1;
                i_egaki_dankai = 1;
                s_step[1].a_b_koukan();
                s_step[1].setcolor(icol);
                i_dousa_mode_henkou_kanousei = 0;
            }

        }

        if (i_dousa_mode == 1) {
            mDragged_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            mDragged_A_38(p0);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_71(Point p0) {
        if (i_dousa_mode == 1) {
            mReleased_A_01(p0);
        }
        if (i_dousa_mode == 38) {
            mReleased_A_38(p0);
        }
    }





/*
//------折り畳み可能線+格子点系入力


//71 71 71 71 71 71 71    i_mouse_modeA==71　;折り畳み可能線入力  qqqqqqqqq
int i_step_for71=0;//i_step_for71=2の場合は、step線が1本だけになっていて、次の操作で入力折線が確定する状態
//
//課題　step線と既存折線が平行の時エラー方向に線を引くことを改善すること20170407
//
//動作仕様
//（１）点を選択（既存点選択規制）
//（２a）選択点が3以上の奇数折線の頂点の場合
//（３）
//
//
//（２b）２a以外の場合
//



	//マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");


	//マウス操作(ボタンを押したとき)時の作業--------------
	public void mPressed_A_71(Ten p0) {
		//Ten p =new Ten();
		p.set(camera.TV2object(p0));

		if(i_egaki_dankai==0){i_step_for71=0;}



		//if(i_egaki_dankai==0){i_step_for71=0;}

		if(i_step_for71==0){
			double hantei_kyori=0.000001;

			//任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
			moyori_ten.set(get_moyori_ten(p));

			if(p.kyori(moyori_ten)<d_hantei_haba){
				//i_egaki_dankai=i_egaki_dankai+1;
				//s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

				//moyori_tenを端点とする折線をNarabebakoに入れる
				Narabebako_int_double nbox =new Narabebako_int_double();
				for (int i=1; i<=ori_s.getsousuu(); i++ ){ if((0<=ori_s.getcolor(i))&&(ori_s.getcolor(i)<=2)){
					if(moyori_ten.kyori(ori_s.geta(i))<hantei_kyori){
						nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(ori_s.geta(i),ori_s.getb(i)) ));
					}else if(moyori_ten.kyori(ori_s.getb(i))<hantei_kyori){
						nbox.ire_i_tiisaijyun(new int_double( i  , oc.kakudo(ori_s.getb(i),ori_s.geta(i)) ));
					}
				}}
				//System.out.println("nbox.getsousuu()="+nbox.getsousuu());
				if(nbox.getsousuu()%2==1){//moyori_tenを端点とする折線の数が奇数のときだけif{}内の処理をする
					//System.out.println("20170130_3");

					//int i_kouho_suu=0;
					for (int i=1; i<=nbox.getsousuu(); i++ ){//iは角加減値を求める最初の折線のid
						//折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
						double kakukagenti=0.0;
						//System.out.println("nbox.getsousuu()="+nbox.getsousuu());
						int tikai_orisen_jyunban;
						int tooi_orisen_jyunban;
						for (int k=1; k<=nbox.getsousuu(); k++ ){//kは角加減値を求める角度の順番
							tikai_orisen_jyunban=i+k-1;if(tikai_orisen_jyunban>nbox.getsousuu()){tikai_orisen_jyunban=tikai_orisen_jyunban-nbox.getsousuu();}
							tooi_orisen_jyunban =i+k  ;if(tooi_orisen_jyunban >nbox.getsousuu()){tooi_orisen_jyunban =tooi_orisen_jyunban -nbox.getsousuu();}

							double add_kakudo=oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban)-nbox.get_double(tikai_orisen_jyunban));
							if(k%2==1){kakukagenti=kakukagenti+add_kakudo;
							}else if(k%2==0){kakukagenti=kakukagenti-add_kakudo;
							}
							//System.out.println("i="+i+"   k="+k);
						}

if(nbox.getsousuu()==1){kakukagenti=360.0;}
						//System.out.println("kakukagenti="+kakukagenti);
						//チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
						tikai_orisen_jyunban=i  ;if(tikai_orisen_jyunban>nbox.getsousuu()){tikai_orisen_jyunban=tikai_orisen_jyunban-nbox.getsousuu();}
						tooi_orisen_jyunban =i+1;if(tooi_orisen_jyunban >nbox.getsousuu()){tooi_orisen_jyunban =tooi_orisen_jyunban -nbox.getsousuu();}

						double add_kakudo_1=oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban)-nbox.get_double(tikai_orisen_jyunban));
if(nbox.getsousuu()==1){add_kakudo_1=360.0;}

						if((kakukagenti/2.0>0.0+0.000001)&&(kakukagenti/2.0<add_kakudo_1-0.000001)){
							i_egaki_dankai=i_egaki_dankai+1;

							//線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
							Senbun s_kiso =new Senbun();
							if(moyori_ten.kyori(ori_s.geta(nbox.get_int(i)))<hantei_kyori){
								s_kiso.set(ori_s.geta(nbox.get_int(i)),ori_s.getb(nbox.get_int(i)));
							}else if(moyori_ten.kyori(ori_s.getb(nbox.get_int(i)))<hantei_kyori){
								s_kiso.set(ori_s.getb(nbox.get_int(i)),ori_s.geta(nbox.get_int(i)));
							}

							double s_kiso_nagasa=s_kiso.getnagasa();

							s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso,kakukagenti/2.0,kus.d_haba()/s_kiso_nagasa) );
						 	s_step[i_egaki_dankai].setcolor(8);
							s_step[i_egaki_dankai].setiactive(1);

						}

					}
					//if(i_kouho_suu==1){i_step_for71=2;}
					//if(i_kouho_suu>1){i_step_for71=1;}

					if(i_egaki_dankai==1){i_step_for71=2;}
					if(i_egaki_dankai>1){i_step_for71=1;}
				}

				if(i_egaki_dankai==0){//折畳み可能化線がない場合//System.out.println("_");
					i_egaki_dankai=1;
					i_step_for71=1;
					s_step[1].set(moyori_ten,moyori_ten);
				 	s_step[1].setcolor(8);
					s_step[1].setiactive(3);
				}

			}
			return;
		}



		if(i_step_for71==1){
			moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));
			if((i_egaki_dankai>=2)&&(oc.kyori_senbun( p,moyori_senbun)<d_hantei_haba)){
			//if(oc.kyori_senbun( p,moyori_senbun)<d_hantei_haba){
				//System.out.println("20170129_5");
				i_step_for71=2;
				i_egaki_dankai=1;
				s_step[1].set(moyori_senbun);
				return;
			}
			//if(oc.kyori_senbun( p,moyori_senbun)>=d_hantei_haba){
				//System.out.println("");
				moyori_ten.set(get_moyori_ten(p));
				if(p.kyori(moyori_ten)<d_hantei_haba){
					s_step[1].setb(moyori_ten);
					i_step_for71=2;i_egaki_dankai=1;
					return;
				}
				//System.out.println("20170129_7");
				i_egaki_dankai=0;i_kouho_dankai=0;
				return;
			//}
			//return;
		}



		if(i_step_for71==2){//i_step_for71==2であれば、以下でs_step[1]を入力折線を確定する
			moyori_ten.set(get_moyori_ten(p));

			//System.out.println("20170130_1");
			if(moyori_ten.kyori(s_step[1].geta())< 0.00000001 ){
				i_egaki_dankai=0;i_kouho_dankai=0;
				return;
			}
			//else if(p.kyori(s_step[1].getb())< kus.d_haba()/10.0 ){
			//else if(p.kyori(s_step[1].getb())< d_hantei_haba/2.5 ){
			//else if(p.kyori(s_step[1].getb())< d_hantei_haba ){

			if((p.kyori(s_step[1].getb())< d_hantei_haba )&&
				(
				p.kyori(s_step[1].getb())<=p.kyori(moyori_ten)
				//moyori_ten.kyori(s_step[1].getb())<0.00000001
				)){
				Senbun add_sen =new Senbun(s_step[1].geta(),s_step[1].getb(),icol);
				addsenbun(add_sen);
				kiroku();
				i_egaki_dankai=0;i_kouho_dankai=0;
				return;
			}

		//}


		//if(i_step_for39==2){

			//moyori_ten.set(get_moyori_ten(p));
			if(p.kyori(moyori_ten)<d_hantei_haba){
				s_step[1].setb(moyori_ten);return;
			}



			moyori_senbun.set(get_moyori_senbun(p));

			Senbun moyori_step_senbun =new Senbun();moyori_step_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));
			if(oc.kyori_senbun( p,moyori_senbun)>=d_hantei_haba){//最寄の既存折線が遠い場合
				//moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));


				//moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}
				//moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}



				if(oc.kyori_senbun( p,moyori_step_senbun)<d_hantei_haba){//最寄のstep_senbunが近い場合

					//moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}




					return;
				}
				//最寄のstep_senbunが遠い場合

					//moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}
				i_egaki_dankai=0;i_kouho_dankai=0;
				return;
			}

			if(oc.kyori_senbun( p,moyori_senbun)<d_hantei_haba){//最寄の既存折線が近い場合
				//moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}
				s_step[2].set(moyori_senbun);
				s_step[2].setcolor(6);
				//System.out.println("20170129_3");
				Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[1],s_step[2]));
				Senbun add_sen =new Senbun(kousa_ten,s_step[1].geta(),icol);
				if(add_sen.getnagasa()>0.00000001){//最寄の既存折線が有効の場合
					addsenbun(add_sen);
					kiroku();
					i_egaki_dankai=0;i_kouho_dankai=0;
					return;
				}
				//最寄の既存折線が無効の場合
				moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}
				//最寄のstep_senbunが近い場合
				if(oc.kyori_senbun( p,moyori_step_senbun)<d_hantei_haba){
					return;
				}
				//最寄のstep_senbunが遠い場合
				i_egaki_dankai=0;i_kouho_dankai=0;
				return;

			}
			return;
		}





	}

//マウス操作(ドラッグしたとき)を行う関数
	public void mDragged_A_71(Ten p0) {	}

//マウス操作(ボタンを離したとき)を行う関数
	public void mReleased_A_71(Ten p0){

	}












*/


//7777777777777777777    i_mouse_modeA==7;角二等分線モード　

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_07(Point p0) {
        if ((i_egaki_dankai >= 0) && (i_egaki_dankai <= 2)) {
            mMoved_A_29(p0);//近い既存点のみ表示
        }

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_07(Point p0) {


        Point p = new Point();
        p.set(camera.TV2object(p0));

        if ((i_egaki_dankai >= 0) && (i_egaki_dankai <= 2)) {
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(icol);
                return;
            }
        }

        if (i_egaki_dankai == 3) {
            moyori_line.set(get_moyori_senbun(p));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_line);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_egaki_dankai].setcolor(6);
                return;
            }
        }
        return;

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_07(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_07(Point p0) {
        if (i_egaki_dankai == 4) {
            i_egaki_dankai = 0;

            i_egaki_dankai = 0;

            //三角形の内心を求める	public Ten oc.naisin(Ten ta,Ten tb,Ten tc)
            Point naisin = new Point();
            naisin.set(oc.naisin(s_step[1].geta(), s_step[2].geta(), s_step[3].geta()));


            Line add_sen2 = new Line(s_step[2].geta(), naisin);


            //add_sen2とs_step[4]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
            Point kousa_point = new Point();
            kousa_point.set(oc.kouten_motome(add_sen2, s_step[4]));

            Line add_sen = new Line(kousa_point, s_step[2].geta(), icol);
            if (add_sen.getnagasa() > 0.00000001) {
                addsenbun(add_sen);
                kiroku();
            }


        }


    }

//------


//88888888888888888888888    i_mouse_modeA==8　;内心モード。

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_08(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_08(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_08(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_08(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;

            //三角形の内心を求める	public Ten oc.naisin(Ten ta,Ten tb,Ten tc)
            Point naisin = new Point();
            naisin.set(oc.naisin(s_step[1].geta(), s_step[2].geta(), s_step[3].geta()));

            Line add_sen1 = new Line(s_step[1].geta(), naisin, icol);
            if (add_sen1.getnagasa() > 0.00000001) {
                addsenbun(add_sen1);
            }
            Line add_sen2 = new Line(s_step[2].geta(), naisin, icol);
            if (add_sen2.getnagasa() > 0.00000001) {
                addsenbun(add_sen2);
            }
            Line add_sen3 = new Line(s_step[3].geta(), naisin, icol);
            if (add_sen3.getnagasa() > 0.00000001) {
                addsenbun(add_sen3);
            }
            kiroku();
        }


    }

    //------
    public double get_L1() {
        return sokutei_nagasa_1;
    }

    public double get_L2() {
        return sokutei_nagasa_2;
    }

    public double get_A1() {
        return sokutei_kakudo_1;
    }

    public double get_A2() {
        return sokutei_kakudo_2;
    }

    public double get_A3() {
        return sokutei_kakudo_3;
    }


    //53 53 53 53 53 53 53 53 53    i_mouse_modeA==53　;長さ測定１モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_53(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_53(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_53(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_53(Point p0) {
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 0;
            sokutei_nagasa_1 = oc.kyori(s_step[1].geta(), s_step[2].geta()) * (double) kus.bunsuu() / 400.0;

            orihime_app.sokutei_nagasa_1_hyouji(sokutei_nagasa_1);
            //kiroku();
        }


    }

//------


    //------
//54 54 54 54 54 54 54 54 54    i_mouse_modeA==54　;長さ測定2モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_54(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_54(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_54(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_54(Point p0) {
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 0;
            sokutei_nagasa_2 = oc.kyori(s_step[1].geta(), s_step[2].geta()) * (double) kus.bunsuu() / 400.0;

            orihime_app.sokutei_nagasa_2_hyouji(sokutei_nagasa_2);
            //kiroku();
        }


    }

//------


    //------
//55 55 55 55 55 55 55 55 55    i_mouse_modeA==55　;角度測定1モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_55(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_55(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_55(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_55(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;
            sokutei_kakudo_1 = oc.kakudo(s_step[2].geta(), s_step[3].geta(), s_step[2].geta(), s_step[1].geta());
            if (sokutei_kakudo_1 > 180.0) {
                sokutei_kakudo_1 = sokutei_kakudo_1 - 360.0;
            }

            orihime_app.sokutei_kakudo_1_hyouji(sokutei_kakudo_1);
            //kiroku();
        }
    }
//------


    //------
//56 56 56 56 56 56 56 56 56    i_mouse_modeA==56　;角度測定2モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_56(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_56(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_56(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_56(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;
            sokutei_kakudo_2 = oc.kakudo(s_step[2].geta(), s_step[3].geta(), s_step[2].geta(), s_step[1].geta());
            if (sokutei_kakudo_2 > 180.0) {
                sokutei_kakudo_2 = sokutei_kakudo_2 - 360.0;
            }
            orihime_app.sokutei_kakudo_2_hyouji(sokutei_kakudo_2);
            //kiroku();
        }
    }
//------

    //------
//57 57 57 57 57 57 57 57 57    i_mouse_modeA==57　;角度測定3モード。
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_57(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_57(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_57(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_57(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;
            sokutei_kakudo_3 = oc.kakudo(s_step[2].geta(), s_step[3].geta(), s_step[2].geta(), s_step[1].geta());
            if (sokutei_kakudo_3 > 180.0) {
                sokutei_kakudo_3 = sokutei_kakudo_3 - 360.0;
            }
            orihime_app.sokutei_kakudo_3_hyouji(sokutei_kakudo_3);
            //kiroku();
        }
    }
//------


//999999999999999999    i_mouse_modeA==9　;垂線おろしモード

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_09(Point p0) {
        if (i_egaki_dankai == 0) {
            mMoved_A_29(p0);//近い既存点のみ表示
        }

    }


    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_09(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_egaki_dankai == 0) {
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(icol);
                return;
            }
        }

        if (i_egaki_dankai == 1) {
            moyori_line.set(get_moyori_senbun(p));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_line);
                s_step[i_egaki_dankai].setcolor(6);
                return;
            }
            i_egaki_dankai = 0;
            return;


        }
        return;

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_09(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_09(Point p0) {
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 0;
            //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){
            //oc.Senbun2Tyokusen(Senbun s)//線分を含む直線を得る

            Line add_sen = new Line(s_step[1].geta(), oc.kage_motome(oc.Senbun2Tyokusen(s_step[2]), s_step[1].geta()), icol);
            if (add_sen.getnagasa() > 0.00000001) {
                addsenbun(add_sen);
                kiroku();
            }


        }
    }
//------
//------
//40 40 40 40 40 40     i_mouse_modeA==40　;平行線入力モード

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_40(Point p0) {
        if (i_egaki_dankai == 0) {
            mMoved_A_29(p0);//近い既存点のみ表示
        }

    }


    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_40(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_egaki_dankai == 0) {
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(icol);
                return;
            }
        }

        if (i_egaki_dankai == 1) {
            moyori_line.set(get_moyori_senbun(p));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_line);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_egaki_dankai].setcolor(6);
                return;
            }
            //i_egaki_dankai=0;
            return;
        }


        if (i_egaki_dankai == 2) {
            moyori_line.set(get_moyori_senbun(p));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_line);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_egaki_dankai].setcolor(6);
                return;
            }
            //i_egaki_dankai=0;
            return;
        }


        return;

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_40(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_40(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;
            //s_step[1]を点状から、s_step[2]に平行な線分にする。
            s_step[1].setb(new Point(s_step[1].getax() + s_step[2].getbx() - s_step[2].getax(), s_step[1].getay() + s_step[2].getby() - s_step[2].getay()));


            //Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[1],s_step[3]));

            //Senbun add_sen =new Senbun(kousa_ten,s_step[1].geta(),icol);

            if (s_step_tuika_koutenmade(3, s_step[1], s_step[3], icol) > 0) {
                addsenbun(s_step[4]);
                kiroku();
                i_egaki_dankai = 0;
                return;
            }


            //if(add_sen.getnagasa()>0.00000001){
            //	addsenbun(add_sen);
            //	kiroku();
            //	i_egaki_dankai=0;
            //	return;
            //}
        }
    }

    //------
    //i_egaki_dankaiがi_e_dのときに、線分s_oをTen aはそのままで、Ten b側をs_kの交点までのばした一時折線s_step[i_e_d+1](色はicolo)を追加。成功した場合は1、なんらかの不都合で追加できなかった場合は-500を返す。
    public int s_step_tuika_koutenmade(int i_e_d, Line s_o, Line s_k, int icolo) {

        Point kousa_point = new Point();

        if (oc.heikou_hantei(s_o, s_k, 0.0000001) == 1) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return -500;
        }

        if (oc.heikou_hantei(s_o, s_k, 0.0000001) == 2) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            kousa_point.set(s_k.geta());
            if (oc.kyori(s_o.geta(), s_k.geta()) > oc.kyori(s_o.geta(), s_k.getb())) {
                kousa_point.set(s_k.getb());
            }


        }

        if (oc.heikou_hantei(s_o, s_k, 0.0000001) == 0) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            kousa_point.set(oc.kouten_motome(s_o, s_k));
        }


        Line add_sen = new Line(kousa_point, s_o.geta(), icolo);

        if (add_sen.getnagasa() > 0.00000001) {
            s_step[i_e_d + 1].set(add_sen);
            return 1;
        }
        return -500;
    }


//10 10 10 10 10    i_mouse_modeA==10　;折り返しモード

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_10(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_10(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_10(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_10(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;

            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
            Point t_taisyou = new Point();
            t_taisyou.set(oc.sentaisyou_ten_motome(s_step[2].geta(), s_step[3].geta(), s_step[1].geta()));

            Line add_sen = new Line(s_step[2].geta(), t_taisyou);

            add_sen.set(kousatenmade(add_sen));
            add_sen.setcolor(icol);
            if (add_sen.getnagasa() > 0.00000001) {
                addsenbun(add_sen);
                kiroku();
            }
        }
    }


//52 52 52 52 52    i_mouse_modeA==52　;連続折り返しモード ****************************************

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_52(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_52(Point p0) {
        System.out.println("i_egaki_dankai=" + i_egaki_dankai);

        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));

        i_egaki_dankai = i_egaki_dankai + 1;
        if (p.kyori(moyori_point) < d_hantei_haba) {
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(icol);
        } else {
            s_step[i_egaki_dankai].set(p, p);
            s_step[i_egaki_dankai].setcolor(icol);
        }

        System.out.println("i_egaki_dankai=" + i_egaki_dankai);
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_52(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_52(Point p0) {
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 0;

            Line add_line = new Line();
            renzoku_orikaesi_new(s_step[1].geta(), s_step[2].geta());
            for (int i = 1; i <= i_egaki_dankai; i++) {

                if (s_step[i].getnagasa() > 0.00000001) {

                    add_line.set(s_step[i].geta(), s_step[i].getb());//要注意　s_stepは表示上の都合でアクティヴが0以外に設定されているのでadd_senbunにうつしかえてる20170507
                    add_line.setcolor(icol);
                    addsenbun(add_line);
                }
            }
            kiroku();

            i_egaki_dankai = 0;
        }
    }

// ------------------------------------------------------------
    //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
    //Ten t_taisyou =new Ten(); t_taisyou.set(oc.sentaisyou_ten_motome(s_step[2].geta(),s_step[3].geta(),s_step[1].geta()));


    // ------------------------------------------------------------
    public void renzoku_orikaesi_new(Point a, Point b) {//連続折り返しの改良版。
        orihime_app.repaint();

        //ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする。
        //補助活線は無視する
        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする


        //「再帰関数における、種の発芽」交点がない場合「種」が成長せずリターン。

        e_s_dougubako.kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(a, b);//一番近い交差点を見つけて各種情報を記録
        if (e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == 0) {
            return;
        }

        //「再帰関数における、種の成長」交点が見つかった場合、交点まで伸びる線分をs_step[i_egaki_dankai]に追加
        //if(e_s_dougubako.get_kousaten_made_nobasi_orisen_fukumu_flg(a,b)==3){return;}
        i_egaki_dankai = i_egaki_dankai + 1;
        if (i_egaki_dankai > 100) {
            return;
        }//念のためにs_stepの上限を100に設定した

        s_step[i_egaki_dankai].set(e_s_dougubako.get_kousaten_made_nobasi_senbun_new());//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        s_step[i_egaki_dankai].setiactive(3);

        System.out.println("20201129 saiki repaint ");

        //「再帰関数における、種の生成」求めた最も近い交点から次のベクトル（＝次の再帰関数に渡す「種」）を発生する。最も近い交点が折線とＸ字型に交差している点か頂点かで、種のでき方が異なる。

        //最も近い交点が折線とＸ字型の場合無条件に種を生成し、散布。
        if (e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == 1) {
            Line kousaten_made_nobasi_saisyono_line = new Line();
            kousaten_made_nobasi_saisyono_line.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

            Point new_a = new Point();
            new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
            Point new_b = new Point();
            new_b.set(oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_line.geta(), kousaten_made_nobasi_saisyono_line.getb(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            renzoku_orikaesi_new(new_a, new_b);//種の散布
            return;
        }

        //最も近い交点が頂点（折線端末）の場合、頂点に集まる折線の数で条件分けして、種を生成し散布、
        if ((e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == 21)
                || (e_s_dougubako.get_kousaten_made_nobasi_flg_new(a, b) == 22)) {//System.out.println("20201129 21 or 22");

            StraightLine tyoku1 = new StraightLine(a, b);
            int i_kousa_flg;

            Narabebako_int_double t_m_s_nbox = new Narabebako_int_double();

            t_m_s_nbox.set(ori_s.get_nbox_of_tyouten_b_syuui_orisen(e_s_dougubako.get_kousaten_made_nobasi_senbun_new().geta(), e_s_dougubako.get_kousaten_made_nobasi_senbun_new().getb()));

            //System.out.println("20201129 t_m_s_nbox.getsousuu() = "+ t_m_s_nbox.getsousuu());


            if (t_m_s_nbox.getsousuu() == 2) {

                //i_kousa_flg=
                //0=この直線は与えられた線分と交差しない、
                //1=X型で交差する、
                //21=線分のa点でT型で交差する、
                //22=線分のb点でT型で交差する、
                //3=線分は直線に含まれる。
                i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(1)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == 3) {
                    return;
                }

                i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(2)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == 3) {
                    return;
                }

                StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.get_int(1)));
                i_kousa_flg = tyoku2.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(2)));
                if (i_kousa_flg == 3) {
                    Line kousaten_made_nobasi_saisyono_line = new Line();
                    kousaten_made_nobasi_saisyono_line.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                    Point new_a = new Point();
                    new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                    Point new_b = new Point();
                    new_b.set(oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_line.geta(), kousaten_made_nobasi_saisyono_line.getb(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                    renzoku_orikaesi_new(new_a, new_b);//種の散布
                    return;
                }
                return;
            }


            if (t_m_s_nbox.getsousuu() == 3) {

                i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(1)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == 3) {
                    StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.get_int(2)));
                    i_kousa_flg = tyoku2.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(3)));
                    if (i_kousa_flg == 3) {
                        Line kousaten_made_nobasi_saisyono_line = new Line();
                        kousaten_made_nobasi_saisyono_line.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_line.geta(), kousaten_made_nobasi_saisyono_line.getb(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        renzoku_orikaesi_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(2)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == 3) {
                    StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.get_int(3)));
                    i_kousa_flg = tyoku2.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(1)));
                    if (i_kousa_flg == 3) {
                        Line kousaten_made_nobasi_saisyono_line = new Line();
                        kousaten_made_nobasi_saisyono_line.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_line.geta(), kousaten_made_nobasi_saisyono_line.getb(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        renzoku_orikaesi_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(3)));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (i_kousa_flg == 3) {
                    StraightLine tyoku2 = new StraightLine(ori_s.get(t_m_s_nbox.get_int(1)));
                    i_kousa_flg = tyoku2.senbun_kousa_hantei_kuwasii(ori_s.get(t_m_s_nbox.get_int(2)));
                    if (i_kousa_flg == 3) {
                        Line kousaten_made_nobasi_saisyono_line = new Line();
                        kousaten_made_nobasi_saisyono_line.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_line.geta(), kousaten_made_nobasi_saisyono_line.getb(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        renzoku_orikaesi_new(new_a, new_b);//種の散布
                        return;
                    }
                }


            }


            //
            //nbox1.set(kakutyou_fushimi_hantei_henbu_tejyun( nbox));

//getsousuu()

            //Ten new_=new Ten();new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten_new());//Ten new_aは最も近い交点


            //Senbun kousaten_made_nobasi_saisyono_senbun =new Senbun();
            //kousaten_made_nobasi_saisyono_senbun.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun_new());

            //e_s_dougubako.get_kousaten_made_nobasi_ten_new());
            //Ten new_b=new Ten();new_b.set(	oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_senbun.geta(),kousaten_made_nobasi_saisyono_senbun.getb(),a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            //renzoku_orikaesi_new(new_a,new_b);//種の散布
            return;
        }

        return;
    }

// ------------------------------------------------------------


    public void renzoku_orikaesi_1_kaime(Point a, Point b) {//連続折り返しの1回目だけここを実施する。連続折り返しの2回目以降はただのrenzoku_orikaesi関数で行う。


        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする
        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == 0) {
            return;
        }
        //if(e_s_dougubako.get_kousaten_made_nobasi_orisen_fukumu_flg(a,b)==3){return;}

        i_egaki_dankai = i_egaki_dankai + 1;
        if (i_egaki_dankai > 100) {
            return;
        }//念のためにs_stepの上限を100に設定した

        s_step[i_egaki_dankai].set(e_s_dougubako.get_kousaten_made_nobasi_senbun(a, b));//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        s_step[i_egaki_dankai].setiactive(3);

        //求めた交点で、次のベクトルを発生する。

        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == 1) {
            Line kousaten_made_nobasi_saisyono_line = new Line();
            kousaten_made_nobasi_saisyono_line.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun(a, b));

            Point new_a = new Point();
            new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten(a, b));
            Point new_b = new Point();
            new_b.set(oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_line.geta(), kousaten_made_nobasi_saisyono_line.getb(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            renzoku_orikaesi(new_a, new_b);
            return;
        }

        return;
    }
// ------------------------------------------------------------


    public void renzoku_orikaesi(Point a, Point b) {

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする
        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == 0) {
            return;
        }
        //if(e_s_dougubako.get_kousaten_made_nobasi_orisen_fukumu_flg(a,b)==3){return;}

        i_egaki_dankai = i_egaki_dankai + 1;
        if (i_egaki_dankai > 100) {
            return;
        }//念のためにs_stepの上限を100に設定した

        s_step[i_egaki_dankai].set(e_s_dougubako.get_kousaten_made_nobasi_senbun(a, b));//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        s_step[i_egaki_dankai].setiactive(3);

        //求めた交点で、次のベクトルを発生する。

        if (e_s_dougubako.get_kousaten_made_nobasi_flg(a, b) == 1) {
            Line kousaten_made_nobasi_saisyono_line = new Line();
            kousaten_made_nobasi_saisyono_line.set(e_s_dougubako.get_kousaten_made_nobasi_saisyono_senbun(a, b));

            Point new_a = new Point();
            new_a.set(e_s_dougubako.get_kousaten_made_nobasi_ten(a, b));
            Point new_b = new Point();
            new_b.set(oc.sentaisyou_ten_motome(kousaten_made_nobasi_saisyono_line.geta(), kousaten_made_nobasi_saisyono_line.getb(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            renzoku_orikaesi(new_a, new_b);
            return;
        }

        return;
    }
// ------------------------------------------------------------


//--------------------------------------------
//27 27 27 27 27 27 27 27  i_mouse_modeA==27線分分割	入力 27 27 27 27 27 27 27 27
    //動作概要　
    //i_mouse_modeA==1と線分分割以外は同じ　
    //

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_27(Point p0) {
        mMoved_m_00a(p0, icol);//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
/*
		if(i_kou_mitudo_nyuuryoku==1){s_kouho[1].setiactive(3);
			i_kouho_dankai=0;
			p.set(camera.TV2object(p0));
			moyori_ten.set(get_moyori_ten(p));
			if(p.kyori(moyori_ten)<d_hantei_haba     ){
				i_kouho_dankai=1;
				s_kouho[1].set(moyori_ten,moyori_ten);
				s_kouho[1].setcolor(icol);
				return;
			}
		}
*/
    }

    //マウス操作(i_mouse_modeA==27線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_27(Point p0) {
        i_egaki_dankai = 1;
        s_step[1].setiactive(2);
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            s_step[1].set(p, moyori_point);
            s_step[1].setcolor(icol);
            return;
        }
        s_step[1].set(p, p);
        s_step[1].setcolor(icol);
    }

    //マウス操作(i_mouse_modeA==27線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_27(Point p0) {
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);
        if (i_kou_mitudo_nyuuryoku == 1) {
            i_kouho_dankai = 0;
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_kouho_dankai = 1;
                s_kouho[1].set(moyori_point, moyori_point);
                s_kouho[1].setcolor(icol);
                s_step[1].seta(s_kouho[1].geta());
            }
        }


    }

    //マウス操作(i_mouse_modeA==27線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_27(Point p0) {
        i_egaki_dankai = 0;
        p.set(camera.TV2object(p0));

        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));

        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            for (int i = 0; i <= i_orisen_bunkatu_suu - 1; i++) {
                double ax = ((double) (i_orisen_bunkatu_suu - i) * s_step[1].getax() + (double) i * s_step[1].getbx()) / ((double) i_orisen_bunkatu_suu);
                double ay = ((double) (i_orisen_bunkatu_suu - i) * s_step[1].getay() + (double) i * s_step[1].getby()) / ((double) i_orisen_bunkatu_suu);
                double bx = ((double) (i_orisen_bunkatu_suu - i - 1) * s_step[1].getax() + (double) (i + 1) * s_step[1].getbx()) / ((double) i_orisen_bunkatu_suu);
                double by = ((double) (i_orisen_bunkatu_suu - i - 1) * s_step[1].getay() + (double) (i + 1) * s_step[1].getby()) / ((double) i_orisen_bunkatu_suu);
                Line s_ad = new Line(ax, ay, bx, by);
                s_ad.setcolor(icol);
                addsenbun(s_ad);
            }
            kiroku();
        }

    }

//--------------------------------------------
//29 29 29 29 29 29 29 29  i_mouse_modeA==29正多角形入力	入力 29 29 29 29 29 29 29 29
    //動作概要　
    //i_mouse_modeA==1と線分分割以外は同じ　
    //

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_29(Point p0) {
        if (i_kou_mitudo_nyuuryoku == 1) {
            s_kouho[1].setiactive(3);
            i_kouho_dankai = 0;
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_kouho_dankai = 1;
                s_kouho[1].set(moyori_point, moyori_point);
                s_kouho[1].setcolor(icol);
                return;
            }
        }
    }

    //マウス操作(i_mouse_modeA==29正多角形入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_29(Point p0) {
        s_step[1].setiactive(3);

        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 0) {    //第1段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(5);
            }
            return;
        }

        if (i_egaki_dankai == 1) {    //第2段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[1].setb(s_step[2].getb());
            }
            if (s_step[1].getnagasa() < 0.00000001) {
                i_egaki_dankai = 0;
            }
        }


    }

    //マウス操作(i_mouse_modeA==29正多角形入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_29(Point p0) {
	}

    //マウス操作(i_mouse_modeA==29正多角形入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_29(Point p0) {
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 0;
            Line s_tane = new Line();
            Line s_deki = new Line();


            s_tane.set(s_step[1]);
            s_tane.setcolor(icol);
            addsenbun(s_tane);
            for (int i = 2; i <= i_sei_takakukei; i++) {
                s_deki.set(oc.Senbun_kaiten(s_tane, (double) (i_sei_takakukei - 2) * 180.0 / (double) i_sei_takakukei));
                s_tane.set(s_deki.getb(), s_deki.geta());
                s_tane.setcolor(icol);
                addsenbun(s_tane);

            }
            ori_s.unselect_all();
            kiroku();
        }
    }


    //37 37 37 37 37 37 37 37 37 37 37;角度規格化
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_37(Point p0) {
        mMoved_A_29(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==37　でボタンを押したとき)時の作業-------//System.out.println("A");---------------------------------------------
    public void mPressed_A_37(Point p0) {
        s_step[1].setiactive(2);
        i_egaki_dankai = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            i_egaki_dankai = 0;
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(icol);

        s_step[2].set(s_step[1]);//ここではs_step[2]は表示されない、計算用の線分
    }

    //マウス操作(i_mouse_modeA==37　でドラッグしたとき)を行う関数--------------//System.out.println("A");--------------------------------------
    public void mDragged_A_37(Point p0) {
        Point syuusei_point = new Point(syuusei_ten_A_37(p0));
        s_step[1].seta(syuusei_point);

        if (i_kou_mitudo_nyuuryoku == 1) {
            i_kouho_dankai = 1;
            s_kouho[1].set(kouho_ten_A_37(syuusei_point), kouho_ten_A_37(syuusei_point));
            s_kouho[1].setcolor(icol);
            s_step[1].seta(kouho_ten_A_37(syuusei_point));
        }

    }


    //マウス操作(i_mouse_modeA==37　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_37(Point p0) {
        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            Point syuusei_point = new Point(syuusei_ten_A_37(p0));
            s_step[1].seta(kouho_ten_A_37(syuusei_point));
            if (s_step[1].getnagasa() > 0.00000001) {
                addsenbun(s_step[1]);
                kiroku();
                return;
            }


        }

    }


    // ---
    public Point syuusei_ten_A_37(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        Point syuusei_point = new Point();
        double d_rad = 0.0;
        s_step[2].seta(p);

        if (id_kakudo_kei != 0) {
            d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            d_rad = (Math.PI / 180) * d_kakudo_kei * (int) Math.round(oc.kakudo(s_step[2]) / d_kakudo_kei);
        } else if (id_kakudo_kei == 0) {
            double[] jk = new double[7];
            jk[0] = oc.kakudo(s_step[2]);//マウスで入力した線分がX軸となす角度
            jk[1] = d_jiyuu_kaku_1 - 180.0;
            jk[2] = d_jiyuu_kaku_2 - 180.0;
            jk[3] = d_jiyuu_kaku_3 - 180.0;
            jk[4] = 360.0 - d_jiyuu_kaku_1 - 180.0;
            jk[5] = 360.0 - d_jiyuu_kaku_2 - 180.0;
            jk[6] = 360.0 - d_jiyuu_kaku_3 - 180.0;

            double d_kakudo_sa_min = 1000.0;
            for (int i = 1; i <= 6; i++) {
                if (Math.min(oc.kakudo_osame_0_360(jk[i] - jk[0]), oc.kakudo_osame_0_360(jk[0] - jk[i])) < d_kakudo_sa_min) {
                    d_kakudo_sa_min = Math.min(oc.kakudo_osame_0_360(jk[i] - jk[0]), oc.kakudo_osame_0_360(jk[0] - jk[i]));
                    d_rad = (Math.PI / 180) * jk[i];
                }
            }
        }

        syuusei_point.set(oc.kage_motome(s_step[2].getb(), new Point(s_step[2].getbx() + Math.cos(d_rad), s_step[2].getby() + Math.sin(d_rad)), p));
        return syuusei_point;
    }


    // ---
    public Point kouho_ten_A_37(Point syuusei_point) {
        moyori_point.set(get_moyori_ten(syuusei_point));
        double zure_kakudo = oc.kakudo(s_step[2].getb(), syuusei_point, s_step[2].getb(), moyori_point);
        int zure_flg = 0;
        if ((0.00001 < zure_kakudo) && (zure_kakudo <= 359.99999)) {
            zure_flg = 1;
        }
        if ((zure_flg == 0) && (syuusei_point.kyori(moyori_point) <= d_hantei_haba)) {//最寄点が角度系にのっていて、修正点とも近い場合
            return moyori_point;
        }
        return syuusei_point;
    }


// 19 19 19 19 19 19 19 19 19 select 選択

    Point p19_1 = new Point();
    Point p19_2 = new Point();
    Point p19_3 = new Point();
    Point p19_4 = new Point();


    //------------------------------------------------------------
    public void mPressed_A_box_select(Point p0) {
        p19_1.set(p0);

        i_egaki_dankai = 0;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        s_step[1].set(p, p);
        s_step[1].setcolor(5);
        s_step[2].set(p, p);
        s_step[2].setcolor(5);
        s_step[3].set(p, p);
        s_step[3].setcolor(5);
        s_step[4].set(p, p);
        s_step[4].setcolor(5);

    }
//------------------------------------------------------------

    Point p19_a = new Point();
    Point p19_b = new Point();
    Point p19_c = new Point();
    Point p19_d = new Point();


//------------------------------------------------------------

    public void mDragged_A_box_select(Point p0) {
        p19_2.set(p19_1.getx(), p0.gety());
        p19_4.set(p0.getx(), p19_1.gety());

        p19_a.set(camera.TV2object(p19_1));
        p19_b.set(camera.TV2object(p19_2));
        p19_c.set(camera.TV2object(p0));
        p19_d.set(camera.TV2object(p19_4));

        s_step[1].set(p19_a, p19_b);
        s_step[2].set(p19_b, p19_c);
        s_step[3].set(p19_c, p19_d);
        s_step[4].set(p19_d, p19_a);

        i_egaki_dankai = 4;//s_step[4]まで描画するために、この行が必要

    }


//------------------------------------------------------------
// 19 19 19 19 19 19 19 19 19 select 選択


    int i_select_mode = 0;//=0は通常のセレクト操作

    //マウス操作(i_mouse_modeA==19  select　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_19(Point p0) {
        System.out.println("19  select_");
        System.out.println("i_egaki_dankai=" + i_egaki_dankai);

        if (i_egaki_dankai == 0) {//i_select_modeを決める
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));

/* 20200930 以下除外　折線をセレクト後格子点を選択するとすぐ作業になる仕様のための部分だが、セレクトが分かりにくくなるので取りやめ
			moyori_ten.set(get_moyori_ten_orisen_en(p));//この最寄点は格子点は対象としない
			if(p.kyori(moyori_ten)<d_hantei_haba     ){
				i_select_mode=0;
				if(ori_s.tyouten_syuui_sensuu_select(p,0.0001)>0){
					i_select_mode=orihime_ap.i_sel_mou_mode;//=1はmove、=2はmove4p、=3はcopy、=4はcopy4p、=5は鏡映像
				}
			}
*/
        }


        if (i_select_mode == 0) {
            mPressed_A_box_select(p0);
        } else if (i_select_mode == 1) {
            mPressed_A_21(p0);//move
        } else if (i_select_mode == 2) {
            mPressed_A_31(p0);//move 2p2p
        } else if (i_select_mode == 3) {
            mPressed_A_22(p0);//copy
        } else if (i_select_mode == 4) {
            mPressed_A_32(p0);//copy 2p2p
        } else if (i_select_mode == 5) {
            mPressed_A_12(p0);//鏡映
        }
    }


    //マウス操作(i_mouse_modeA==19 select　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_19(Point p0) {
        //mDragged_A_box_select( p0);
        if (i_select_mode == 0) {
            mDragged_A_box_select(p0);
        } else if (i_select_mode == 1) {
            mDragged_A_21(p0);//move
        } else if (i_select_mode == 2) {
            mDragged_A_31(p0);//move 2p2p
        } else if (i_select_mode == 3) {
            mDragged_A_22(p0);//copy
        } else if (i_select_mode == 4) {
            mDragged_A_32(p0);//copy 2p2p
        } else if (i_select_mode == 5) {
            mDragged_A_12(p0);//鏡映
        }


    }


    //マウス操作(i_mouse_modeA==19 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_19(Point p0) {
        //mReleased_A_box_select(p0);
        if (i_select_mode == 0) {
            mReleased_A_box_select(p0);
        } else if (i_select_mode == 1) {
            mReleased_A_21(p0);//move
        } else if (i_select_mode == 2) {
            mReleased_A_31(p0);//move 2p2p
        } else if (i_select_mode == 3) {
            mReleased_A_22(p0);//copy
        } else if (i_select_mode == 4) {
            mReleased_A_32(p0);//copy 2p2p
        } else if (i_select_mode == 5) {
            mReleased_A_12(p0);//鏡映
        }


    }


//------------------------------------------------------------


    public void mReleased_A_box_select(Point p0) {
        i_egaki_dankai = 0;

        select(p19_1, p0);
        if (p19_1.kyori(p0) <= 0.000001) {
            p.set(camera.TV2object(p0));
            if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.select(ori_s.mottomo_tikai_senbun_sagasi(p));
            }
        }

    }

//20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20


    //マウス操作(i_mouse_modeA==19  select　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_20(Point p0) {
        mPressed_A_box_select(p0);
    }


    //マウス操作(i_mouse_modeA==19 select　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_20(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==20 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_20(Point p0) {

        i_egaki_dankai = 0;
        unselect(p19_1, p0);

        if (p19_1.kyori(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.unselect(ori_s.mottomo_tikai_senbun_sagasi(p));
            }
        }


    }

    //---------------------
    public int get_i_egaki_dankai() {
        return i_egaki_dankai;
    }

    //---------------------
    public void select_all() {
        ori_s.select_all();
    }

    public void unselect_all() {
        ori_s.unselect_all();
    }

    public void select(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();


        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();


        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());


        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));


        ori_s.select(p_a, p_b, p_c, p_d);
    }

    //--------------------
    public void unselect(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        ori_s.unselect(p_a, p_b, p_c, p_d);
    }


//61 61 61 61 61 61 61 61 61 61 61 61 i_mouse_modeA==61//長方形内選択（paintの選択に似せた選択機能）に使う
    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_61(Point p0) {
        if (i_kou_mitudo_nyuuryoku == 1) {
            s_kouho[1].setiactive(3);

            p.set(camera.TV2object(p0));
            i_kouho_dankai = 1;
            moyori_point.set(get_moyori_ten(p));

            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }

            //s_kouho[1].setcolor(icol);
            s_kouho[1].setcolor(6);

            return;
        }
    }


    //マウス操作(i_mouse_modeA==61　長方形内選択でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_61(Point p0) {
        p.set(camera.TV2object(p0));
        Point p_new = new Point();
        Point p_ob1 = new Point();
        p_ob1.set(camera.TV2object(p61_1));
        Point p_ob2 = new Point();
        p_ob2.set(camera.TV2object(p61_2));
        Point p_ob3 = new Point();
        p_ob3.set(camera.TV2object(p61_3));
        Point p_ob4 = new Point();
        p_ob4.set(camera.TV2object(p61_4));

        double kyori_min = 100000.0;

        p61_mode = 0;
        if (i_egaki_dankai == 0) {
            p61_mode = 1;
        }
        if (i_egaki_dankai == 4) {
            if (p61_TV_hako.inside(p0) == 0) {
                p61_mode = 1;
            }
            if (p61_TV_hako.inside(p0) > 0) {
                p61_mode = 4;
            }


            kyori_min = oc.min(oc.kyori_senbun(p, p_ob1, p_ob2), oc.kyori_senbun(p, p_ob2, p_ob3), oc.kyori_senbun(p, p_ob3, p_ob4), oc.kyori_senbun(p, p_ob4, p_ob1));
            if (kyori_min < d_hantei_haba) {
                p61_mode = 3;
            }


            if (p.kyori(p_ob1) < d_hantei_haba) {
                p_new.set(p61_1);
                p61_1.set(p61_3);
                p61_3.set(p_new);
                p61_mode = 2;
            }
            if (p.kyori(p_ob2) < d_hantei_haba) {
                p_new.set(p61_2);
                p61_2.set(p61_1);
                p61_1.set(p61_4);
                p61_4.set(p61_3);
                p61_3.set(p_new);
                p61_mode = 2;
            }
            if (p.kyori(p_ob3) < d_hantei_haba) {
                p_new.set(p61_3);
                p61_1.set(p61_1);
                p61_3.set(p_new);
                p61_mode = 2;
            }
            if (p.kyori(p_ob4) < d_hantei_haba) {
                p_new.set(p61_4);
                p61_4.set(p61_1);
                p61_1.set(p61_2);
                p61_2.set(p61_3);
                p61_3.set(p_new);
                p61_mode = 2;
            }

        }


        if (p61_mode == 3) {
            while (oc.kyori_senbun(p, p_ob1, p_ob2) != kyori_min) {
                p_new.set(p61_1);
                p61_1.set(p61_2);
                p61_2.set(p61_3);
                p61_3.set(p61_4);
                p61_4.set(p_new);
                p_new.set(p_ob1);
                p_ob1.set(p_ob2);
                p_ob2.set(p_ob3);
                p_ob3.set(p_ob4);
                p_ob4.set(p_new);
            }

        }

        if (p61_mode == 1) {
            i_egaki_dankai = 4;
            //s_step[1].setiactive(2);


            p_new.set(p);

            moyori_point.set(get_moyori_ten(p));

            if (p.kyori(moyori_point) < d_hantei_haba) {
                p_new.set(moyori_point);

            }

            p61_1.set(camera.object2TV(p_new));
            p61_2.set(camera.object2TV(p_new));
            p61_3.set(camera.object2TV(p_new));
            p61_4.set(camera.object2TV(p_new));

            return;

            //s_step[1].setcolor(6);
            //s_step[2].setcolor(6);
            //s_step[3].setcolor(6);
            //s_step[4].setcolor(6);
        }
/*
		if(i_egaki_dankai==4){
			i_egaki_dankai=4;
			s_step[1].setiactive(2);
			p.set(camera.TV2object(p0));

			moyori_ten.set(get_moyori_ten(p));

			s_step[1].setcolor(6);
			if(p.kyori(moyori_ten)<d_hantei_haba     ){
				s_step[1].set(p,moyori_ten);
				return;
			}

			s_step[1].set(p,p);
			s_step[2].set(p,p);
			s_step[3].set(p,p);
			s_step[4].set(p,p);

			s_step[1].setcolor(6);
			s_step[2].setcolor(6);
			s_step[3].setcolor(6);
			s_step[4].setcolor(6);

		}
*/
    }

    //マウス操作(i_mouse_modeA==61　長方形内選択でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_61(Point p0) {

        p.set(camera.TV2object(p0));
        if (p61_mode == 2) {
            p61_mode = 1;
        }


        Point p_new = new Point();


        if (i_kou_mitudo_nyuuryoku == 0) {
            p_new.set(p);
        }

        if (i_kou_mitudo_nyuuryoku == 1) {
            moyori_point.set(get_moyori_ten(p));
            i_kouho_dankai = 1;
            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }
            s_kouho[1].setcolor(6);

            p_new.set(s_kouho[1].geta());
        }


        if (p61_mode == 3) {
            if (
                    (p61_1.getx() - p61_2.getx()) * (p61_1.getx() - p61_2.getx())
                            <
                            (p61_1.gety() - p61_2.gety()) * (p61_1.gety() - p61_2.gety())
            ) {
                p61_1.setx(camera.object2TV(p_new).getx());
                p61_2.setx(camera.object2TV(p_new).getx());
            }

            if (
                    (p61_1.getx() - p61_2.getx()) * (p61_1.getx() - p61_2.getx())
                            >
                            (p61_1.gety() - p61_2.gety()) * (p61_1.gety() - p61_2.gety())
            ) {
                p61_1.sety(camera.object2TV(p_new).gety());
                p61_2.sety(camera.object2TV(p_new).gety());
            }

        }


        if (p61_mode == 1) {
            p61_3.set(camera.object2TV(p_new));
            p61_2.set(p61_1.getx(), p61_3.gety());
            p61_4.set(p61_3.getx(), p61_1.gety());
        }
/*






			Ten p1= new Ten();p1.set(p_new);
			Ten p3= new Ten();p3.set(s_step[1].getb());

			Ten p2= new Ten();p2.set(p1.getx(),p3.gety());
			Ten p4= new Ten();p4.set(p3.getx(),p1.gety());

			s_step[1].set(p2,p3); //s_step[1]のb点は最初の地点として変更できないので、.set(p2,p3);とする必要がある。
			s_step[2].set(p3,p4);
			s_step[3].set(p4,p1);
			s_step[4].set(p1,p2);

			s_step[1].setcolor(6);
			s_step[2].setcolor(6);
			s_step[3].setcolor(6);
			s_step[4].setcolor(6);
*/

    }


    //マウス操作(i_mouse_modeA==61 長方形内選択　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_61(Point p0) {

        p.set(camera.TV2object(p0));

        Point p_new = new Point();
        p_new.set(p);
        //s_step[1].seta(p);

        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            p_new.set(moyori_point);/*s_step[1].seta(moyori_ten);*/
        }

        if (p61_mode == 3) {
            if (
                    (p61_1.getx() - p61_2.getx()) * (p61_1.getx() - p61_2.getx())
                            <
                            (p61_1.gety() - p61_2.gety()) * (p61_1.gety() - p61_2.gety())
            ) {
                p61_1.setx(camera.object2TV(p_new).getx());
                p61_2.setx(camera.object2TV(p_new).getx());
            }

            if (
                    (p61_1.getx() - p61_2.getx()) * (p61_1.getx() - p61_2.getx())
                            >
                            (p61_1.gety() - p61_2.gety()) * (p61_1.gety() - p61_2.gety())
            ) {
                p61_1.sety(camera.object2TV(p_new).gety());
                p61_2.sety(camera.object2TV(p_new).gety());
            }

        }


        if (p61_mode == 1) {
            p61_3.set(camera.object2TV(p_new));
            p61_2.set(p61_1.getx(), p61_3.gety());
            p61_4.set(p61_3.getx(), p61_1.gety());
        }


        p61_TV_hako.set(1, p61_1);
        p61_TV_hako.set(2, p61_2);
        p61_TV_hako.set(3, p61_3);
        p61_TV_hako.set(4, p61_4);


//System.out.println("aaaaaaaaa_"+p61_TV_hako.menseki_motome());

        if (p61_TV_hako.menseki_motome() * p61_TV_hako.menseki_motome() < 1.0) {
            i_egaki_dankai = 0;
        }








/*
			Ten p1= new Ten();p1.set(p_new);
			Ten p3= new Ten();p3.set(s_step[1].getb());

			Ten p2= new Ten();p2.set(p1.getx(),p3.gety());
			Ten p4= new Ten();p4.set(p3.getx(),p1.gety());

			s_step[1].set(p2,p3); //s_step[1]のb点は最初の地点として変更できないので、.set(p2,p3);とする必要がある。
			s_step[2].set(p3,p4);
			s_step[3].set(p4,p1);
			s_step[4].set(p1,p2);

			s_step[1].setcolor(6);
			s_step[2].setcolor(6);
			s_step[3].setcolor(6);
			s_step[4].setcolor(6);



		if(s_step[1].getnagasa()>0.00000001){
			i_egaki_dankai=4;
		}
*/

        //text_cp_setumei="aaaaaa"+ori_s.getsousuu();
    }


//22222222222222222222222222222222222222222222222222222222222222 展開図移動


    //public void mPressed_A_02(Ten p0) {	}//マウス操作(i_mouse_modeA==2　展開図移動でボタンを押したとき)時の作業
    //public void mDragged_A_02(Ten p0) {	}//マウス操作(i_mouse_modeA==2　展開図移動でドラッグしたとき)を行う関数
    //public void mReleased_A_02(Ten p0){	}//マウス操作(i_mouse_modeA==2　展開図移動でボタンを離したとき)を行う関数

    //3 3 3 3 3 33333333333333333333333333333333333333333333333333333333
    //マウス操作(i_mouse_modeA==3,23 "線分削除" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_03(Point p0) {
        //System.out.println("(1)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        if (i_orisen_hojyosen == 0) {
            mPressed_A_box_select(p0);
        }//折線の削除
        if (i_orisen_hojyosen == 2) {
            mPressed_A_box_select(p0);
        }//黒の折線
        if (i_orisen_hojyosen == 3) {
            mPressed_A_box_select(p0);
        }//補助活線

        if (i_orisen_hojyosen == 1) {
            mPressed_A_box_select(p0);
        }//補助絵線

        if (i_orisen_hojyosen == 4) {
            mPressed_A_box_select(p0);
        }//折線と補助活線と補助絵線
    }

    //マウス操作(i_mouse_modeA==3,23でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_03(Point p0) {
        //System.out.println("(2)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        if (i_orisen_hojyosen == 0) {
            mDragged_A_box_select(p0);
        }
        if (i_orisen_hojyosen == 2) {
            mDragged_A_box_select(p0);
        }
        if (i_orisen_hojyosen == 3) {
            mDragged_A_box_select(p0);
        }

        if (i_orisen_hojyosen == 1) {
            mDragged_A_box_select(p0);
        }

        if (i_orisen_hojyosen == 4) {
            mDragged_A_box_select(p0);
        }


    }

    //マウス操作(i_mouse_modeA==3,23 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_03(Point p0) {//折線と補助活線と円
        //System.out.println("(3_1)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        i_egaki_dankai = 0;

        //最寄の一つを削除
        if (p19_1.kyori(p0) <= 0.000001) {//最寄の一つを削除
            int i_sakujyo_mode = 10;//i_sakujyo_modeはここで定義・宣言している
            if (i_orisen_hojyosen == 0) {
                i_sakujyo_mode = 0;
            }
            if (i_orisen_hojyosen == 2) {
                i_sakujyo_mode = 2;
            }
            if (i_orisen_hojyosen == 3) {
                i_sakujyo_mode = 3;
            }
            if (i_orisen_hojyosen == 1) {
                i_sakujyo_mode = 1;
            }
            if (i_orisen_hojyosen == 4) {
                i_sakujyo_mode = 10;
                //Ten p =new Ten(); p.set(camera.TV2object(p0));
                double rs_min;
                rs_min = ori_s.mottomo_tikai_senbun_kyori(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)

                double re_min;
                re_min = ori_s.mottomo_tikai_en_kyori(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)

                double hoj_rs_min;
                hoj_rs_min = hoj_s.mottomo_tikai_senbun_kyori(p);//点pに最も近い補助絵線の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)

                if ((rs_min <= re_min) && (rs_min <= hoj_rs_min)) {
                    if (ori_s.getcolor(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p)) < 3) {
                        i_sakujyo_mode = 0;
                    } else {
                        i_sakujyo_mode = 3;
                    }
                }

                if ((re_min < rs_min) && (re_min <= hoj_rs_min)) {
                    i_sakujyo_mode = 3;
                }
                if ((hoj_rs_min < rs_min) && (hoj_rs_min < re_min)) {
                    i_sakujyo_mode = 1;
                }

            }


            if (i_sakujyo_mode == 0) { //折線の削除

                //Ten p =new Ten(); p.set(camera.TV2object(p0));
                double rs_min;
                rs_min = ori_s.mottomo_tikai_senbun_kyori(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                if (rs_min < d_hantei_haba) {
                    if (ori_s.getcolor(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p)) < 3) {
                        ori_s.delsenbun_vertex(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p));
                        en_seiri();
                        kiroku();
                    }
                }

            }


            if (i_sakujyo_mode == 2) { //黒の折線の削除

                //Ten p =new Ten(); p.set(camera.TV2object(p0));
                double rs_min;
                rs_min = ori_s.mottomo_tikai_senbun_kyori(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                if (rs_min < d_hantei_haba) {
                    if (ori_s.getcolor(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p)) == 0) {
                        ori_s.delsenbun_vertex(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p));
                        en_seiri();
                        kiroku();
                    }
                }

            }

            if (i_sakujyo_mode == 3) {  //補助活線
                //Ten p =new Ten(); p.set(camera.TV2object(p0));
                double rs_min;
                rs_min = ori_s.mottomo_tikai_senbun_kyori(p);//点pに最も近い線分(折線と補助活線)の番号での、その距離を返す
                double re_min;
                re_min = ori_s.mottomo_tikai_en_kyori(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)


                if (rs_min <= re_min) {
                    if (rs_min < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                        if (ori_s.getcolor(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p)) == 3) {
                            ori_s.delsenbun_vertex(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p));
                            en_seiri();
                            kiroku();
                        }
                    }
                } else {
                    if (re_min < d_hantei_haba) {
                        ori_s.delen(ori_s.mottomo_tikai_en_sagasi_gyakujyun(p));
                        en_seiri();
                        kiroku();
                    }
                }


            }

            if (i_sakujyo_mode == 1) { //補助絵線

                //Ten p =new Ten(); p.set(camera.TV2object(p0));
                double rs_min;
                rs_min = hoj_s.mottomo_tikai_senbun_kyori(p);//点pに最も近い補助絵線の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)

                if (rs_min < d_hantei_haba) {
                    hoj_s.delsenbun_vertex(hoj_s.mottomo_tikai_senbun_sagasi_gyakujyun(p));
                    //en_seiri();
                    kiroku();
                }

            }


        }


        //四角枠内の削除 //p19_1はselectの最初のTen。この条件は最初のTenと最後の点が遠いので、四角を発生させるということ。
        if (p19_1.kyori(p0) > 0.000001) {
            if ((i_orisen_hojyosen == 0) || (i_orisen_hojyosen == 4)) { //折線の削除	//D_nisuru(p19_1,p0)で折線だけが削除される。
                if (D_nisuru0(p19_1, p0) != 0) {
                    en_seiri();
                    kiroku();
                }
            }


            if (i_orisen_hojyosen == 2) {  //黒の折線のみ削除
                if (D_nisuru2(p19_1, p0) != 0) {
                    en_seiri();
                    kiroku();
                }
            }


            if ((i_orisen_hojyosen == 3) || (i_orisen_hojyosen == 4)) {  //補助活線  //現状では削除しないときもUNDO用に記録されてしまう20161218
                if (D_nisuru3(p19_1, p0) != 0) {
                    en_seiri();
                    kiroku();
                }
            }

            if ((i_orisen_hojyosen == 1) || (i_orisen_hojyosen == 4)) { //補助絵線	//現状では削除しないときもUNDO用に記録されてしまう20161218
                if (D_nisuru1(p19_1, p0) != 0) {
                    kiroku();
                }
            }

        }

//qqqqqqqqqqqqqqqqqqqqqqqqqqqqq//System.out.println("= ");qqqqq
//check4(0.0001);//D_nisuru0をすると、ori_s.D_nisuru0内でresetが実行されるため、check4のやり直しが必要。
        if (i_check1 == 1) {
            check1(0.001, 0.5);
        }
        if (i_check2 == 1) {
            check2(0.01, 0.5);
        }
        if (i_check3 == 1) {
            check3(0.0001);
        }
        if (i_check4 == 1) {
            check4(0.0001);
        }

    }

//--------------------

    public int D_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.D_nisuru(p_a, p_b, p_c, p_d);
    }
//--------------------

    public int D_nisuru0(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        //System.out.println("(3_2_1)zzzzz ori_s.check4_size() = "+ori_s.check4_size());
        return ori_s.D_nisuru0(p_a, p_b, p_c, p_d);
    }
//--------------------

    public int D_nisuru2(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.D_nisuru2(p_a, p_b, p_c, p_d);
    }

    //--------------------
    public int D_nisuru3(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.D_nisuru3(p_a, p_b, p_c, p_d);
    }


//--------------------


    public int chenge_property_in_4kakukei(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.chenge_property_in_4kakukei(p_a, p_b, p_c, p_d, sen_tokutyuu_color);
    }


//--------------------

    public int D_nisuru1(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return hoj_s.D_nisuru(p_a, p_b, p_c, p_d);
    }


    //59 59 59 59 59 59 59 59 59 59
    //マウス操作(i_mouse_modeA==59 "特注プロパティ指定" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_59(Point p0) {
        mPressed_A_box_select(p0);   //折線と補助活線と補助絵線
    }

    //マウス操作(i_mouse_modeA==59 "特注プロパティ指定"でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_59(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==59 "特注プロパティ指定" でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_59(Point p0) {//補助活線と円
        i_egaki_dankai = 0;
        if (p19_1.kyori(p0) > 0.000001) {//現状では削除しないときもUNDO用に記録されてしまう20161218

            //if(D_nisuru3(p19_1,p0)!=0){en_seiri();kiroku();}
            if (chenge_property_in_4kakukei(p19_1, p0) != 0) {
                //kiroku();
            }

        }

        if (p19_1.kyori(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            double rs_min;
            rs_min = ori_s.mottomo_tikai_senbun_kyori(p);//点pに最も近い補助活線の番号での、その距離を返す
            double re_min;
            re_min = ori_s.mottomo_tikai_en_kyori(p);//点pに最も近い円の番号での、その距離を返す	public double mottomo_tikai_en_kyori(Ten p)


            if (rs_min <= re_min) {
                if (rs_min < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                    if (ori_s.getcolor(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p)) == 3) {
                        ori_s.set_tpp_sen(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p), 1);
                        ori_s.set_tpp_sen_color(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p), sen_tokutyuu_color);
                        //en_seiri();kiroku();
                    }
                }
            } else {
                if (re_min < d_hantei_haba) {
                    ori_s.set_tpp_en(ori_s.mottomo_tikai_en_sagasi_gyakujyun(p), 1);
                    ori_s.set_tpp_en_color(ori_s.mottomo_tikai_en_sagasi_gyakujyun(p), sen_tokutyuu_color);

//System.out.println("get_tpp_en="+ori_s.get_tpp_en(ori_s.mottomo_tikai_en_sagasi_gyakujyun(p)));


                    //en_seiri();kiroku();
                }
            }
        }

//ppppppppppp


    }

//--------------------


    //4 4 4 4 4 444444444444444444444444444444444444444444444444444444444
    public void mPressed_A_04(Point p0) {
    }//マウス操作(i_mouse_modeA==4線_変換　でボタンを押したとき)時の作業

    public void mDragged_A_04(Point p0) {
    }//マウス操作(i_mouse_modeA==4線_変換　でドラッグしたとき)を行う関数

    //マウス操作(i_mouse_modeA==4線_変換　でボタンを離したとき)を行う関数
    public void mReleased_A_04(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));


        if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            int minrid;
            minrid = ori_s.mottomo_tikai_senbun_sagasi(p);
            int ic_temp;
            ic_temp = ori_s.getcolor(minrid);
            if (ic_temp < 3) {
                ic_temp = ic_temp + 1;
                if (ic_temp == 3) {
                    ic_temp = 0;
                }
                ori_s.setcolor(minrid, ic_temp);
                kiroku();
            }

        }




/*
		int minrid;double minr;
		minrid=ori_s.mottomo_tikai_senbun_sagasi(p);
		if(ori_s.senbun_busyo_sagasi(minrid, p, r)!=0){
			int ic_temp;
			ic_temp=ori_s.getcolor(minrid);
			ic_temp=ic_temp+1;if(ic_temp==3){ic_temp=0;}
			ori_s.setcolor(minrid,ic_temp);
		}
*/

    }

    //------
//58 58 58 58 58 58 58 58 58 58
    public void mPressed_A_58(Point p0) {
        mPressed_A_box_select(p0);
    }//マウス操作(i_mouse_modeA==58線_変換　でボタンを押したとき)時の作業

    public void mDragged_A_58(Point p0) {
        mDragged_A_box_select(p0);
    }//マウス操作(i_mouse_modeA==58線_変換　でドラッグしたとき)を行う関数

    //マウス操作(i_mouse_modeA==58線_変換　でボタンを離したとき)を行う関数
    public void mReleased_A_58(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_egaki_dankai = 0;

        if (p19_1.kyori(p0) > 0.000001) {//
            if (MV_change(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                kiroku();
            }
        }


        if (p19_1.kyori(p0) <= 0.000001) {//
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                int minrid;
                minrid = ori_s.mottomo_tikai_senbun_sagasi(p);
                int ic_temp;
                ic_temp = ori_s.getcolor(minrid);
                if (ic_temp == 1) {
                    ori_s.setcolor(minrid, 2);
                } else if (ic_temp == 2) {
                    ori_s.setcolor(minrid, 1);
                }

                fix2(0.001, 0.5);
                kiroku();
            }

        }
    }

    //------
    public int MV_change(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.MV_change(p_a, p_b, p_c, p_d);
    }
//--------------------


    //30 30 30 30 30 30 30 30 30 30 30 30 除け_線_変換
    int minrid_30;

    public void mPressed_A_30(Point p0) {    //マウス操作(i_mouse_modeA==4線_変換　でボタンを押したとき)時の作業
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        minrid_30 = -1;
        if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            minrid_30 = ori_s.mottomo_tikai_senbun_sagasi(p);
            Line s01 = new Line();
            s01.set(oc.Senbun_bai(ori_s.get(minrid_30), 0.01));
            ori_s.setb(minrid_30, s01.getb());
        }
    }

    public void mDragged_A_30(Point p0) {//マウス操作(i_mouse_modeA==4線_変換　でドラッグしたとき)を行う関数
        if (minrid_30 > 0) {

            Line s01 = new Line();
            s01.set(oc.Senbun_bai(ori_s.get(minrid_30), 100.0));
            ori_s.setb(minrid_30, s01.getb());
            minrid_30 = -1;
        }

    }


    //マウス操作(i_mouse_modeA==30 除け_線_変換　でボタンを離したとき)を行う関数（背景に展開図がある場合用）
    public void mReleased_A_30(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (minrid_30 > 0) {

            Line s01 = new Line();
            s01.set(oc.Senbun_bai(ori_s.get(minrid_30), 100.0));
            ori_s.setb(minrid_30, s01.getb());

            int ic_temp;
            ic_temp = ori_s.getcolor(minrid_30);
            int is_temp;
            is_temp = ori_s.get_select(minrid_30);

            if ((ic_temp == 0) && (is_temp == 0)) {
                ori_s.set_select(minrid_30, 2);
            } else if ((ic_temp == 0) && (is_temp == 2)) {
                ori_s.setcolor(minrid_30, 1);
                ori_s.set_select(minrid_30, 0);
            } else if ((ic_temp == 1) && (is_temp == 0)) {
                ori_s.setcolor(minrid_30, 2);
            } else if ((ic_temp == 2) && (is_temp == 0)) {
                ori_s.setcolor(minrid_30, 0);
            }

            kiroku();
        }


    }
//------


//i_mouse_modeA;マウスの動作に対する反応を規定する。
// -------------1;線分入力モード。
//2;展開図調整(移動)。
//3;"L_del"
//4;"L_chan"

// -------------5;線分延長モード。
// -------------6;2点から等距離線分モード。
// -------------7;角二等分線モード。
// -------------8;内心モード。
// -------------9;垂線おろしモード。
// -------------10;折り返しモード。
// -------------11;線分入力モード。
// -------------12;鏡映モード。

//101:折り上がり図の操作。
//102;F_move
//103;S_face

//10001;test1 入力準備として点を３つ指定する


//66666666666666666666    i_mouse_modeA==6　;2点から等距離線分モード

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_06(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_06(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_06(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;
        }


    }

//------


//------折り畳み可能線入力


//38 38 38 38 38 38 38    i_mouse_modeA==38　;折り畳み可能線入力  qqqqqqqqq


    int i_step_for38 = 0;

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_38(Point p0) {
        if (i_kou_mitudo_nyuuryoku == 1) {
            if (i_egaki_dankai == 0) {
                i_step_for38 = 0;
            }

            if (i_step_for38 == 0) {
                mMoved_A_29(p0);
            }

            if (i_step_for38 == 1) {
                s_kouho[1].setiactive(3);
                i_kouho_dankai = 0;
                //Ten p =new Ten();
                p.set(camera.TV2object(p0));

                moyori_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));
                if ((i_egaki_dankai >= 2) && (oc.kyori_senbun(p, moyori_line) < d_hantei_haba)) {

                    i_kouho_dankai = 1;
                    s_kouho[1].set(moyori_line);//s_kouho[1].setcolor(2);
                    return;
                }
            }

            if (i_step_for38 == 2) {
                i_kouho_dankai = 0;
                Point p = new Point();
                p.set(camera.TV2object(p0));

                moyori_line.set(get_moyori_senbun(p));
                if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {//最寄の既存折線が近い場合
                    i_kouho_dankai = 1;
                    s_kouho[1].set(moyori_line);
                    //s_kouho[1].setcolor(2);
                    return;
                }

            }
        }
    }


    //Ten t1 =new Ten();
//マウス操作(ボタンを押したとき)時の作業
    public int mPressed_A_38(Point p0) {//作業がすべて完了し新たな折線を追加でた場合だけ1を返す。それ以外は0を返す。
        i_kouho_dankai = 0;
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_egaki_dankai == 0) {
            i_step_for38 = 0;
        }

        if (i_step_for38 == 0) {
            double hantei_kyori = 0.000001;

            Point t1 = new Point();
            t1.set(ori_s.mottomo_tikai_Ten_with_icol_0_1_2(p));//点pに最も近い、「線分の端点」を返すori_s.mottomo_tikai_Tenは近い点がないと p_return.set(100000.0,100000.0)と返してくる

            if (p.kyori(t1) < d_hantei_haba) {
                //i_egaki_dankai=i_egaki_dankai+1;
                //s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

                //t1を端点とする折線をNarabebakoに入れる
                Narabebako_int_double nbox = new Narabebako_int_double();
                for (int i = 1; i <= ori_s.getsousuu(); i++) {
                    if ((0 <= ori_s.getcolor(i)) && (ori_s.getcolor(i) <= 2)) {
                        if (t1.kyori(ori_s.geta(i)) < hantei_kyori) {
                            nbox.ire_i_tiisaijyun(new int_double(i, oc.kakudo(ori_s.geta(i), ori_s.getb(i))));
                        } else if (t1.kyori(ori_s.getb(i)) < hantei_kyori) {
                            nbox.ire_i_tiisaijyun(new int_double(i, oc.kakudo(ori_s.getb(i), ori_s.geta(i))));
                        }
                    }
                }

                //System.out.println("20170126_4");

                if (nbox.getsousuu() % 2 == 1) {//t1を端点とする折線の数が奇数のときだけif{}内の処理をする
                    icol_temp = icol;
                    if (nbox.getsousuu() == 1) {
                        icol_temp = ori_s.get(nbox.get_int(1)).getcolor();
                    }//20180503この行追加。これは、折線が1本だけの頂点から折り畳み可能線追加機能で、その折線の延長を行った場合に、線の色を延長前の折線と合わせるため

                    //int i_kouho_suu=0;
                    for (int i = 1; i <= nbox.getsousuu(); i++) {//iは角加減値を求める最初の折線のid
                        //折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
                        double kakukagenti = 0.0;
                        //System.out.println("nbox.getsousuu()="+nbox.getsousuu());
                        int tikai_orisen_jyunban;
                        int tooi_orisen_jyunban;
                        for (int k = 1; k <= nbox.getsousuu(); k++) {//kは角加減値を求める角度の順番
                            tikai_orisen_jyunban = i + k - 1;
                            if (tikai_orisen_jyunban > nbox.getsousuu()) {
                                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getsousuu();
                            }
                            tooi_orisen_jyunban = i + k;
                            if (tooi_orisen_jyunban > nbox.getsousuu()) {
                                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getsousuu();
                            }

                            double add_kakudo = oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban) - nbox.get_double(tikai_orisen_jyunban));
                            if (k % 2 == 1) {
                                kakukagenti = kakukagenti + add_kakudo;
                            } else if (k % 2 == 0) {
                                kakukagenti = kakukagenti - add_kakudo;
                            }
                            //System.out.println("i="+i+"   k="+k);


                        }


                        if (nbox.getsousuu() == 1) {
                            kakukagenti = 360.0;
                        }

                        //System.out.println("kakukagenti="+kakukagenti);
                        //チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
                        tikai_orisen_jyunban = i;
                        if (tikai_orisen_jyunban > nbox.getsousuu()) {
                            tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getsousuu();
                        }
                        tooi_orisen_jyunban = i + 1;
                        if (tooi_orisen_jyunban > nbox.getsousuu()) {
                            tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getsousuu();
                        }

                        double add_kakudo_1 = oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban) - nbox.get_double(tikai_orisen_jyunban));
                        if (nbox.getsousuu() == 1) {
                            add_kakudo_1 = 360.0;
                        }

                        if ((kakukagenti / 2.0 > 0.0 + 0.000001) && (kakukagenti / 2.0 < add_kakudo_1 - 0.000001)) {
                            //if((kakukagenti/2.0>0.0-0.000001)&&(kakukagenti/2.0<add_kakudo_1+0.000001)){

                            i_egaki_dankai = i_egaki_dankai + 1;

                            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                            Line s_kiso = new Line();
                            if (t1.kyori(ori_s.geta(nbox.get_int(i))) < hantei_kyori) {
                                s_kiso.set(ori_s.geta(nbox.get_int(i)), ori_s.getb(nbox.get_int(i)));
                            } else if (t1.kyori(ori_s.getb(nbox.get_int(i))) < hantei_kyori) {
                                s_kiso.set(ori_s.getb(nbox.get_int(i)), ori_s.geta(nbox.get_int(i)));
                            }

                            double s_kiso_nagasa = s_kiso.getnagasa();

                            s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakukagenti / 2.0, kus.d_haba() / s_kiso_nagasa));
                            s_step[i_egaki_dankai].setcolor(8);
                            s_step[i_egaki_dankai].setiactive(0);
                        }
                    }
                    if (i_egaki_dankai == 1) {
                        i_step_for38 = 2;
                    }
                    if (i_egaki_dankai > 1) {
                        i_step_for38 = 1;
                    }
                }
            }
            return 0;
        }

        if (i_step_for38 == 1) {
            moyori_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_step_for38 = 2;
                i_egaki_dankai = 1;
                s_step[1].set(moyori_line);

                //i_egaki_dankai=i_egaki_dankai+1;
                //s_step[i_egaki_dankai].set(moyori_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                //s_step[i_egaki_dankai].setcolor(8);
                return 0;
            }
            if (oc.kyori_senbun(p, moyori_line) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                return 0;
            }
        }

        if (i_step_for38 == 2) {
            moyori_line.set(get_moyori_senbun(p));
            //Senbun moyori_step_senbun =new Senbun(get_moyori_step_senbun(p,1,i_egaki_dankai));
            Line moyori_step_line = new Line();
            moyori_step_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));
            if (oc.kyori_senbun(p, moyori_line) >= d_hantei_haba) {//最寄の既存折線が遠くて選択無効の場合
                //moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));
                if (oc.kyori_senbun(p, moyori_step_line) < d_hantei_haba) {//最寄のstep_senbunが近い場合
                    //System.out.println("20170129_1");
                    return 0;
                }

                //最寄のstep_senbunが遠い場合
                //System.out.println("20170129_2");

                i_egaki_dankai = 0;
                return 0;
            }

            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {//最寄の既存折線が近い場合

                s_step[2].set(moyori_line);
                s_step[2].setcolor(6);

                //System.out.println("20170129_3");
                Point kousa_point = new Point();
                kousa_point.set(oc.kouten_motome(s_step[1], s_step[2]));
                Line add_sen = new Line(kousa_point, s_step[1].geta(), icol_temp);//20180503変更
                if (add_sen.getnagasa() > 0.00000001) {//最寄の既存折線が有効の場合
                    addsenbun(add_sen);
                    kiroku();
                    i_egaki_dankai = 0;
                    return 1;

                }

                //最寄の既存折線が無効の場合

                //最寄のstep_senbunが近い場合
                if (oc.kyori_senbun(p, moyori_step_line) < d_hantei_haba) {
                    return 0;
                }

                //最寄のstep_senbunが遠い場合
                i_egaki_dankai = 0;
                return 0;

            }


        }


        return 0;


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_38(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_38(Point p0) {

    }


//------折り畳み可能線+格子点系入力


    //39 39 39 39 39 39 39    i_mouse_modeA==39　;折り畳み可能線入力  qqqqqqqqq
    int i_step_for39 = 0;//i_step_for39=2の場合は、step線が1本だけになっていて、次の操作で入力折線が確定する状態
//
//課題　step線と既存折線が平行の時エラー方向に線を引くことを改善すること20170407
//
//動作仕様
//（１）点を選択（既存点選択規制）
//（２a）選択点が3以上の奇数折線の頂点の場合
//（３）
//
//
//（２b）２a以外の場合
//
//Ten t1 =new Ten();


    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_39(Point p0) {
        if (i_egaki_dankai == 0) {
            i_step_for39 = 0;
        }
        if (i_kou_mitudo_nyuuryoku == 1) {
            i_kouho_dankai = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));

            if (i_egaki_dankai == 0) {
                i_step_for39 = 0;
            }
            System.out.println("i_egaki_dankai= " + i_egaki_dankai + "  ;   i_step_for39= " + i_step_for39);

            if (i_step_for39 == 0) {
                mMoved_A_29(p0);
			/*	double hantei_kyori=0.000001;
				//任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
				moyori_ten.set(get_moyori_ten(p));
				if(p.kyori(moyori_ten)<d_hantei_haba){
					i_kouho_dankai=1;
					s_kouho[1].set(moyori_ten,moyori_ten);
				 	s_kouho[1].setcolor(icol);
				}
				return;
			*/
            }

            if (i_step_for39 == 1) {
                moyori_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));
                if ((i_egaki_dankai >= 2) && (oc.kyori_senbun(p, moyori_line) < d_hantei_haba)) {
                    //System.out.println("20170129_5");
                    i_kouho_dankai = 1;
                    s_kouho[1].set(moyori_line);//s_kouho[1].setcolor(2);
                    return;
                }

                moyori_point.set(get_moyori_ten(p));
                if (p.kyori(moyori_point) < d_hantei_haba) {
                    //s_kouho[1].setb(moyori_ten);s_kouho[1].setcolor(2);
                    s_kouho[1].set(moyori_point, moyori_point);
                    s_kouho[1].setcolor(icol);
                    i_kouho_dankai = 1;
                    return;
                }
                return;
            }


            if (i_step_for39 == 2) {//i_step_for39==2であれば、以下でs_step[1]を入力折線を確定する
                moyori_point.set(get_moyori_ten(p));

                if (moyori_point.kyori(s_step[1].geta()) < 0.00000001) {
                    i_kouho_dankai = 1;
                    s_kouho[1].set(moyori_point, moyori_point);
                    s_kouho[1].setcolor(icol);
                    System.out.println("i_step_for39_2_   1");

                    return;


                }


                if ((p.kyori(s_step[1].getb()) < d_hantei_haba) && (p.kyori(s_step[1].getb()) <= p.kyori(moyori_point))) {
                    i_kouho_dankai = 1;
                    s_kouho[1].set(s_step[1].getb(), s_step[1].getb());
                    s_kouho[1].setcolor(icol);
                    System.out.println("i_step_for39_2_   2");

                    return;
                }


                if (p.kyori(moyori_point) < d_hantei_haba) {
                    i_kouho_dankai = 1;
                    s_kouho[1].set(moyori_point, moyori_point);
                    s_kouho[1].setcolor(icol);
                    System.out.println("i_step_for39_2_   3");

                    return;
                }

                moyori_line.set(get_moyori_senbun(p));
                Line moyori_step_line = new Line();
                moyori_step_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));
                if (oc.kyori_senbun(p, moyori_line) >= d_hantei_haba) {//最寄の既存折線が遠い場合
                    if (oc.kyori_senbun(p, moyori_step_line) < d_hantei_haba) {//最寄のstep_senbunが近い場合
                        return;
                    }
                    //最寄のstep_senbunが遠い場合
                    System.out.println("i_step_for39_2_   4");

                    return;
                }

                if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {//最寄の既存折線が近い場合
                    i_kouho_dankai = 1;
                    s_kouho[1].set(moyori_line);
                    s_kouho[1].setcolor(icol);

                    System.out.println("i_step_for39_2_   5");
                    return;
                }
                return;
            }

            return;
        }
    }


    //マウス操作(ボタンを押したとき)時の作業--------------
    public void mPressed_A_39(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 0) {
            i_step_for39 = 0;
        }


        //if(i_egaki_dankai==0){i_step_for39=0;}

        if (i_step_for39 == 0) {
            double hantei_kyori = 0.000001;

            //任意の点が与えられたとき、端点もしくは格子点で最も近い点を得る
            moyori_point.set(get_moyori_ten(p));

            if (p.kyori(moyori_point) < d_hantei_haba) {
                //i_egaki_dankai=i_egaki_dankai+1;
                //s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

                //moyori_tenを端点とする折線をNarabebakoに入れる
                Narabebako_int_double nbox = new Narabebako_int_double();
                for (int i = 1; i <= ori_s.getsousuu(); i++) {
                    if ((0 <= ori_s.getcolor(i)) && (ori_s.getcolor(i) <= 2)) {
                        if (moyori_point.kyori(ori_s.geta(i)) < hantei_kyori) {
                            nbox.ire_i_tiisaijyun(new int_double(i, oc.kakudo(ori_s.geta(i), ori_s.getb(i))));
                        } else if (moyori_point.kyori(ori_s.getb(i)) < hantei_kyori) {
                            nbox.ire_i_tiisaijyun(new int_double(i, oc.kakudo(ori_s.getb(i), ori_s.geta(i))));
                        }
                    }
                }
                //System.out.println("nbox.getsousuu()="+nbox.getsousuu());
                if (nbox.getsousuu() % 2 == 1) {//moyori_tenを端点とする折線の数が奇数のときだけif{}内の処理をする
                    //System.out.println("20170130_3");

                    //int i_kouho_suu=0;
                    for (int i = 1; i <= nbox.getsousuu(); i++) {//iは角加減値を求める最初の折線のid
                        //折線が奇数の頂点周りの角加減値を2.0で割ると角加減値の最初折線と、折り畳み可能にするための追加の折線との角度になる。
                        double kakukagenti = 0.0;
                        //System.out.println("nbox.getsousuu()="+nbox.getsousuu());
                        int tikai_orisen_jyunban;
                        int tooi_orisen_jyunban;
                        for (int k = 1; k <= nbox.getsousuu(); k++) {//kは角加減値を求める角度の順番
                            tikai_orisen_jyunban = i + k - 1;
                            if (tikai_orisen_jyunban > nbox.getsousuu()) {
                                tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getsousuu();
                            }
                            tooi_orisen_jyunban = i + k;
                            if (tooi_orisen_jyunban > nbox.getsousuu()) {
                                tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getsousuu();
                            }

                            double add_kakudo = oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban) - nbox.get_double(tikai_orisen_jyunban));
                            if (k % 2 == 1) {
                                kakukagenti = kakukagenti + add_kakudo;
                            } else if (k % 2 == 0) {
                                kakukagenti = kakukagenti - add_kakudo;
                            }
                            //System.out.println("i="+i+"   k="+k);
                        }

                        if (nbox.getsousuu() == 1) {
                            kakukagenti = 360.0;
                        }
                        //System.out.println("kakukagenti="+kakukagenti);
                        //チェック用に角加減値の最初の角度の中にkakukagenti/2.0があるかを確認する
                        tikai_orisen_jyunban = i;
                        if (tikai_orisen_jyunban > nbox.getsousuu()) {
                            tikai_orisen_jyunban = tikai_orisen_jyunban - nbox.getsousuu();
                        }
                        tooi_orisen_jyunban = i + 1;
                        if (tooi_orisen_jyunban > nbox.getsousuu()) {
                            tooi_orisen_jyunban = tooi_orisen_jyunban - nbox.getsousuu();
                        }

                        double add_kakudo_1 = oc.kakudo_osame_0_360(nbox.get_double(tooi_orisen_jyunban) - nbox.get_double(tikai_orisen_jyunban));
                        if (nbox.getsousuu() == 1) {
                            add_kakudo_1 = 360.0;
                        }

                        if ((kakukagenti / 2.0 > 0.0 + 0.000001) && (kakukagenti / 2.0 < add_kakudo_1 - 0.000001)) {
                            i_egaki_dankai = i_egaki_dankai + 1;

                            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)
                            Line s_kiso = new Line();
                            if (moyori_point.kyori(ori_s.geta(nbox.get_int(i))) < hantei_kyori) {
                                s_kiso.set(ori_s.geta(nbox.get_int(i)), ori_s.getb(nbox.get_int(i)));
                            } else if (moyori_point.kyori(ori_s.getb(nbox.get_int(i))) < hantei_kyori) {
                                s_kiso.set(ori_s.getb(nbox.get_int(i)), ori_s.geta(nbox.get_int(i)));
                            }

                            double s_kiso_nagasa = s_kiso.getnagasa();

                            s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakukagenti / 2.0, kus.d_haba() / s_kiso_nagasa));
                            s_step[i_egaki_dankai].setcolor(8);
                            s_step[i_egaki_dankai].setiactive(1);

                        }

                    }
                    //if(i_kouho_suu==1){i_step_for39=2;}
                    //if(i_kouho_suu>1){i_step_for39=1;}

                    if (i_egaki_dankai == 1) {
                        i_step_for39 = 2;
                    }
                    if (i_egaki_dankai > 1) {
                        i_step_for39 = 1;
                    }
                }

                if (i_egaki_dankai == 0) {//折畳み可能化線がない場合//System.out.println("_");
                    i_egaki_dankai = 1;
                    i_step_for39 = 1;
                    s_step[1].set(moyori_point, moyori_point);
                    s_step[1].setcolor(8);
                    s_step[1].setiactive(3);
                }

            }
            return;
        }


        if (i_step_for39 == 1) {
            moyori_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));
            if ((i_egaki_dankai >= 2) && (oc.kyori_senbun(p, moyori_line) < d_hantei_haba)) {
                //if(oc.kyori_senbun( p,moyori_senbun)<d_hantei_haba){
                //System.out.println("20170129_5");
                i_step_for39 = 2;
                i_egaki_dankai = 1;
                s_step[1].set(moyori_line);
                return;
            }
            //if(oc.kyori_senbun( p,moyori_senbun)>=d_hantei_haba){
            //System.out.println("");
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_step[1].setb(moyori_point);
                i_step_for39 = 2;
                i_egaki_dankai = 1;
                return;
            }
            //System.out.println("20170129_7");
            i_egaki_dankai = 0;
            i_kouho_dankai = 0;
            return;
            //}
            //return;
        }


        if (i_step_for39 == 2) {//i_step_for39==2であれば、以下でs_step[1]を入力折線を確定する
            moyori_point.set(get_moyori_ten(p));

            //System.out.println("20170130_1");
            if (moyori_point.kyori(s_step[1].geta()) < 0.00000001) {
                i_egaki_dankai = 0;
                i_kouho_dankai = 0;
                return;
            }
            //else if(p.kyori(s_step[1].getb())< kus.d_haba()/10.0 ){
            //else if(p.kyori(s_step[1].getb())< d_hantei_haba/2.5 ){
            //else if(p.kyori(s_step[1].getb())< d_hantei_haba ){

            if ((p.kyori(s_step[1].getb()) < d_hantei_haba) &&
                    (
                            p.kyori(s_step[1].getb()) <= p.kyori(moyori_point)
                            //moyori_ten.kyori(s_step[1].getb())<0.00000001
                    )) {
                Line add_sen = new Line(s_step[1].geta(), s_step[1].getb(), icol);
                addsenbun(add_sen);
                kiroku();
                i_egaki_dankai = 0;
                i_kouho_dankai = 0;
                return;
            }

            //}


            //if(i_step_for39==2){

            //moyori_ten.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_step[1].setb(moyori_point);
                return;
            }


            moyori_line.set(get_moyori_senbun(p));

            Line moyori_step_line = new Line();
            moyori_step_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));
            if (oc.kyori_senbun(p, moyori_line) >= d_hantei_haba) {//最寄の既存折線が遠い場合
                //moyori_senbun.set(get_moyori_step_senbun(p,1,i_egaki_dankai));


                //moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}
                //moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}


                if (oc.kyori_senbun(p, moyori_step_line) < d_hantei_haba) {//最寄のstep_senbunが近い場合

                    //moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}


                    return;
                }
                //最寄のstep_senbunが遠い場合

                //moyori_ten.set(get_moyori_ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}
                i_egaki_dankai = 0;
                i_kouho_dankai = 0;
                return;
            }

            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {//最寄の既存折線が近い場合
                //moyori_ten.set(ori_s.mottomo_tikai_Ten(p));if(p.kyori(moyori_ten)<d_hantei_haba){s_step[1].setb(moyori_ten);return;}
                s_step[2].set(moyori_line);
                s_step[2].setcolor(6);
                //System.out.println("20170129_3");
                Point kousa_point = new Point();
                kousa_point.set(oc.kouten_motome(s_step[1], s_step[2]));
                Line add_sen = new Line(kousa_point, s_step[1].geta(), icol);
                if (add_sen.getnagasa() > 0.00000001) {//最寄の既存折線が有効の場合
                    addsenbun(add_sen);
                    kiroku();
                    i_egaki_dankai = 0;
                    i_kouho_dankai = 0;
                    return;
                }
                //最寄の既存折線が無効の場合
                moyori_point.set(get_moyori_ten(p));
                if (p.kyori(moyori_point) < d_hantei_haba) {
                    s_step[1].setb(moyori_point);
                    return;
                }
                //最寄のstep_senbunが近い場合
                if (oc.kyori_senbun(p, moyori_step_line) < d_hantei_haba) {
                    return;
                }
                //最寄のstep_senbunが遠い場合
                i_egaki_dankai = 0;
                i_kouho_dankai = 0;
                return;

            }
            return;
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_39(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_39(Point p0) {

    }


//33 33 33 33 33 33 33 33 33 33 33魚の骨


    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_33(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示


    //マウス操作(i_mouse_modeA==33魚の骨　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_33(Point p0) {
        i_egaki_dankai = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            i_egaki_dankai = 0;
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }

    //マウス操作(i_mouse_modeA==33魚の骨　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_33(Point p0) {
        mDragged_A_11(p0);
    }

    //マウス操作(i_mouse_modeA==33魚の骨　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_33(Point p0) {
        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;

            //s_step[1]を確定する
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[1].seta(moyori_point);
            //s_step[1]を確定終了


            if (p.kyori(moyori_point) <= d_hantei_haba) {  //マウスで指定した点が、最寄点と近かったときに実施
                if (s_step[1].getnagasa() > 0.00000001) {  //s_step[1]が、線の時（=点状ではない時）に実施
                    double dx = (s_step[1].getax() - s_step[1].getbx()) * kus.d_haba() / s_step[1].getnagasa();
                    double dy = (s_step[1].getay() - s_step[1].getby()) * kus.d_haba() / s_step[1].getnagasa();
                    int icol_temp = icol;
                    //int imax=;

                    Point pxy = new Point();
                    for (int i = 0; i <= (int) Math.floor(s_step[1].getnagasa() / kus.d_haba()); i++) {

                        //System.out.println("_"+i);
                        double px = s_step[1].getbx() + (double) i * dx;
                        double py = s_step[1].getby() + (double) i * dy;
                        pxy.set(px, py);


                        //if(pxy.kyori(ori_s.mottomo_tikai_Ten(pxy) )>0.001      )         {
                        if (ori_s.mottomo_tikai_senbun_kyori_heikou_jyogai(pxy, s_step[1]) > 0.001) {

                            int i_sen = 0;

                            Line adds = new Line(px, py, px - dy, py + dx);
                            if (kouten_ari_nasi(adds) == 1) {
                                adds.set(kousatenmade(adds));
                                adds.setcolor(icol_temp);

                                addsenbun(adds);
                                i_sen = i_sen + 1;
                            }


                            Line adds2 = new Line(px, py, px + dy, py - dx);
                            if (kouten_ari_nasi(adds2) == 1) {
                                adds2.set(kousatenmade(adds2));
                                adds2.setcolor(icol_temp);

                                addsenbun(adds2);
                                i_sen = i_sen + 1;
                            }

                            //ori_s.del_V(ori_s.getsousuu()-1,ori_s.getsousuu());
                            //System.out.println("i_sen_"+i_sen);

                            if (i_sen == 2) {
                                ori_s.del_V(pxy, d_hantei_haba, 0.000001);
                            }

                        }

                        if (icol_temp == 1) {
                            icol_temp = 2;
                        } else if (icol_temp == 2) {
                            icol_temp = 1;
                        }


                    }
                    kiroku();

                }  //s_step[1]が、線の時（=点状ではない時）に実施は、ここまで
            }  //マウスで指定した点が、最寄点と近かったときに実施は、ここまで
        }
    }


//35 35 35 35 35 35 35 35 35 35 35複折り返し   入力した線分に接触している折線を折り返し　に使う

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_35(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示


    //マウス操作(i_mouse_modeA==35　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_35(Point p0) {
        i_egaki_dankai = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            i_egaki_dankai = 0;
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }

    //マウス操作(i_mouse_modeA==35　でドラッグしたとき)を行う関数----------------------------------------------------

    public void mDragged_A_35(Point p0) {
        mDragged_A_11(p0);
    }


    //マウス操作(i_mouse_modeA==35　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_35(Point p0) {

        Narabebako_int_double nbox = new Narabebako_int_double();

        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[1].seta(moyori_point);
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                if (s_step[1].getnagasa() > 0.00000001) {
                    int imax = ori_s.getsousuu();
                    for (int i = 1; i <= imax; i++) {
                        int i_senbun_kousa_hantei = oc.line_intersect_decide_sweet(ori_s.get(i), s_step[1], 0.01, 0.01);
                        int i_jikkou = 0;
                        //if(i_senbun_kousa_hantei== 21 ){ i_jikkou=1;}//L字型
                        //if(i_senbun_kousa_hantei== 22 ){ i_jikkou=1;}//L字型
                        //if(i_senbun_kousa_hantei== 23 ){ i_jikkou=1;}//L字型
                        //if(i_senbun_kousa_hantei== 24 ){ i_jikkou=1;}//L字型
                        if (i_senbun_kousa_hantei == 25) {
                            i_jikkou = 1;
                        }//T字型 s1が縦棒
                        if (i_senbun_kousa_hantei == 26) {
                            i_jikkou = 1;
                        }//T字型 s1が縦棒

                        if (i_jikkou == 1) {
                            Point t_moto = new Point();
                            t_moto.set(ori_s.geta(i));
                            System.out.println("i_senbun_kousa_hantei_" + i_senbun_kousa_hantei);
                            if (oc.kyori_senbun(t_moto, s_step[1]) < oc.kyori_senbun(ori_s.getb(i), s_step[1])) {
                                t_moto.set(ori_s.getb(i));
                            }


                            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
                            Point t_taisyou = new Point();
                            t_taisyou.set(oc.sentaisyou_ten_motome(s_step[1].geta(), s_step[1].getb(), t_moto));

                            Line add_sen = new Line(oc.kouten_motome(ori_s.get(i), s_step[1]), t_taisyou);

                            add_sen.set(kousatenmade(add_sen));
                            add_sen.setcolor(ori_s.getcolor(i));
                            if (add_sen.getnagasa() > 0.00000001) {
                                addsenbun(add_sen);
                            }
                        }

                    }


                    kiroku();

                }
            }
        }

    }


//------

    public Line kousatenmade(Line s0) {//s0を点aからb方向に、他の折線と交差するところまで延長する。新しい線分を返す//他の折線と交差しないなら、同じ線分を返す
        Line add_sen = new Line();
        add_sen.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.kyori(add_sen.geta());


        StraightLine tyoku1 = new StraightLine(add_sen.geta(), add_sen.getb());
        int i_kousa_flg;
        for (int i = 1; i <= ori_s.getsousuu(); i++) {
            i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            if ((i_kousa_flg == 1 || i_kousa_flg == 21) || i_kousa_flg == 22) {
                kousa_point.set(oc.kouten_motome(tyoku1, ori_s.get(i)));
                if (kousa_point.kyori(add_sen.geta()) > 0.00001) {

                    if (kousa_point.kyori(add_sen.geta()) < kousa_ten_kyori) {

                        double d_kakudo = oc.kakudo(add_sen.geta(), add_sen.getb(), add_sen.geta(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            //i_kouten_ari_nasi=1;
                            kousa_ten_kyori = kousa_point.kyori(add_sen.geta());
                            add_sen.set(add_sen.geta(), kousa_point);
                        }
                    }
                }
            }
        }
        return add_sen;
    }

//------


//------

    public Line kousatenmade_2(Line s0) {//s0を点bからaの反対方向に、他の折線と交差するところまで延長する。新しい線分を返す//他の折線と交差しないなら、同じ線分を返す
        Line add_sen = new Line();
        add_sen.set(s0);
        //Senbun add_sen;add_sen=s0;


        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.kyori(add_sen.geta());

        StraightLine tyoku1 = new StraightLine(add_sen.geta(), add_sen.getb());
        int i_kousa_flg;//元の線分を直線としたものと、他の線分の交差状態
        int i_senbun_kousa_flg;//元の線分と、他の線分の交差状態

        System.out.println("AAAAA_");
        for (int i = 1; i <= ori_s.getsousuu(); i++) {
            i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            //i_senbun_kousa_flg=oc.senbun_kousa_hantei_amai( add_sen,ori_s.get(i),0.00001,0.00001);//20180408なぜかこの行の様にadd_senを使うと、i_senbun_kousa_flgがおかしくなる
            i_senbun_kousa_flg = oc.line_intersect_decide_sweet(s0, ori_s.get(i), 0.00001, 0.00001);//20180408なぜかこの行の様にs0のままだと、i_senbun_kousa_flgがおかしくならない。
            if ((i_kousa_flg == 1 || i_kousa_flg == 21) || i_kousa_flg == 22) {
                if (i_senbun_kousa_flg < 21 || i_senbun_kousa_flg > 28) {
                    //System.out.println("i_kousa_flg = "+i_kousa_flg  +      " ; i_senbun_kousa_flg = "+i_senbun_kousa_flg);
                    kousa_point.set(oc.kouten_motome(tyoku1, ori_s.get(i)));
                    if (kousa_point.kyori(add_sen.geta()) > 0.00001) {
                        if (kousa_point.kyori(add_sen.geta()) < kousa_ten_kyori) {
                            double d_kakudo = oc.kakudo(add_sen.geta(), add_sen.getb(), add_sen.geta(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_ten_kyori = kousa_point.kyori(add_sen.geta());
                                add_sen.set(add_sen.geta(), kousa_point);
                            }
                        }
                    }


                }
            }

            if (i_kousa_flg == 3) {
                if (i_senbun_kousa_flg != 31) {


                    System.out.println("i_kousa_flg = " + i_kousa_flg + " ; i_senbun_kousa_flg = " + i_senbun_kousa_flg);


                    kousa_point.set(ori_s.get(i).geta());
                    if (kousa_point.kyori(add_sen.geta()) > 0.00001) {
                        if (kousa_point.kyori(add_sen.geta()) < kousa_ten_kyori) {
                            double d_kakudo = oc.kakudo(add_sen.geta(), add_sen.getb(), add_sen.geta(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_ten_kyori = kousa_point.kyori(add_sen.geta());
                                add_sen.set(add_sen.geta(), kousa_point);
                            }
                        }
                    }

                    kousa_point.set(ori_s.get(i).getb());
                    if (kousa_point.kyori(add_sen.geta()) > 0.00001) {
                        if (kousa_point.kyori(add_sen.geta()) < kousa_ten_kyori) {
                            double d_kakudo = oc.kakudo(add_sen.geta(), add_sen.getb(), add_sen.geta(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_ten_kyori = kousa_point.kyori(add_sen.geta());
                                add_sen.set(add_sen.geta(), kousa_point);
                            }
                        }
                    }


                }
            }


        }


        add_sen.set(s0.getb(), add_sen.getb());
        return add_sen;
    }

//------
//------

    public int kouten_ari_nasi(Line s0) {//s0を点aからb方向に延長したら、他の折線と交差するなら、１しないなら０を返す。a店での交差する線分は、この関数では交差なしになる。
        Line add_sen = new Line();
        add_sen.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.kyori(add_sen.geta());


        StraightLine tyoku1 = new StraightLine(add_sen.geta(), add_sen.getb());
        int i_kousa_flg;
        for (int i = 1; i <= ori_s.getsousuu(); i++) {
            i_kousa_flg = tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            if ((i_kousa_flg == 1 || i_kousa_flg == 21) || i_kousa_flg == 22) {
                kousa_point.set(oc.kouten_motome(tyoku1, ori_s.get(i)));
                if (kousa_point.kyori(add_sen.geta()) > 0.00001) {


                    double d_kakudo = oc.kakudo(add_sen.geta(), add_sen.getb(), add_sen.geta(), kousa_point);
                    if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                        return 1;

                    }

                }
            }
        }
        return 0;
    }


//21 21 21 21 21    i_mouse_modeA==21　;移動モード

    //マウスを動かしたとき
    public void mMoved_A_21(Point p0) {
        mMoved_m_00b(p0, 5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_21(Point p0) {
        mPressed_m_00b(p0, 5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_21(Point p0) {
        mDragged_m_00b(p0, 5);
    }

    //マウスリリース----------------------------------------------------
    public void mReleased_A_21(Point p0) {

        i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
/*
		i_egaki_dankai=0;
		p.set(camera.TV2object(p0));
		moyori_ten.set(get_moyori_ten(p));

		if(p.kyori(moyori_ten)<=d_hantei_haba){
			s_step[1].seta(moyori_ten);
			if(s_step[1].getnagasa()>0.00000001){
				//やりたい動作はここに書く

				double addx,addy;
				addx=-s_step[1].getbx()+s_step[1].getax();
				addy=-s_step[1].getby()+s_step[1].getay();

				Orisensyuugou ori_s_temp =new Orisensyuugou();    //セレクトされた折線だけ取り出すために使う
				ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
				ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。
				ori_s_temp.move(addx,addy);//全体を移動する

				int sousuu_old=ori_s.getsousuu();
				ori_s.addMemo(ori_s_temp.getMemo());
				int sousuu_new=ori_s.getsousuu();
				ori_s.kousabunkatu(1,sousuu_old,sousuu_old+1,sousuu_new);

				ori_s.unselect_all();
				kiroku();


			}
		}
*/
        i_egaki_dankai = 0;
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            //やりたい動作はここに書く

            double addx, addy;
            addx = -s_step[1].getbx() + s_step[1].getax();
            addy = -s_step[1].getby() + s_step[1].getay();

            PolygonStore ori_s_temp = new PolygonStore();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。
            ori_s_temp.move(addx, addy);//全体を移動する

            int sousuu_old = ori_s.getsousuu();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getsousuu();
            ori_s.kousabunkatu(1, sousuu_old, sousuu_old + 1, sousuu_new);

            ori_s.unselect_all();
            kiroku();

            orihime_app.i_mouse_modeA = 19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行

        }


    }


//-------------------------

//22 22 22 22 22    i_mouse_modeA==22　;コピペモード

    //マウスを動かしたとき
    public void mMoved_A_22(Point p0) {
        mMoved_m_00b(p0, 5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_22(Point p0) {
        mPressed_m_00b(p0, 5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_22(Point p0) {
        mDragged_m_00b(p0, 5);
    }

    //マウスリリース----------------------------------------------------
    public void mReleased_A_22(Point p0) {

        i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
/*
		i_egaki_dankai=0;
		p.set(camera.TV2object(p0));
		moyori_ten.set(get_moyori_ten(p));

		if(p.kyori(moyori_ten)<=d_hantei_haba){
			s_step[1].seta(moyori_ten);
			if(s_step[1].getnagasa()>0.00000001){
				//やりたい動作はここに書く

				double addx,addy;
				addx=-s_step[1].getbx()+s_step[1].getax();
				addy=-s_step[1].getby()+s_step[1].getay();

				Orisensyuugou ori_s_temp =new Orisensyuugou();    //セレクトされた折線だけ取り出すために使う
				ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
				//ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。moveと　copyの違いはこの行が有効かどうかの違い
				ori_s_temp.move(addx,addy);//全体を移動する

				int sousuu_old=ori_s.getsousuu();
				ori_s.addMemo(ori_s_temp.getMemo());
				int sousuu_new=ori_s.getsousuu();
				ori_s.kousabunkatu(1,sousuu_old,sousuu_old+1,sousuu_new);

				ori_s.unselect_all();
				kiroku();
			}
		}
*/
        i_egaki_dankai = 0;
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            //やりたい動作はここに書く

            double addx, addy;
            addx = -s_step[1].getbx() + s_step[1].getax();
            addy = -s_step[1].getby() + s_step[1].getay();

            PolygonStore ori_s_temp = new PolygonStore();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            //ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。moveと　copyの違いはこの行が有効かどうかの違い
            ori_s_temp.move(addx, addy);//全体を移動する

            int sousuu_old = ori_s.getsousuu();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getsousuu();
            ori_s.kousabunkatu(1, sousuu_old, sousuu_old + 1, sousuu_new);

            ori_s.unselect_all();
            kiroku();

            orihime_app.i_mouse_modeA = 19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }

    }


//--------------------------------------------
//31 31 31 31 31 31 31 31  i_mouse_modeA==31move2p2p	入力 31 31 31 31 31 31 31 31

//動作概要　
//i_mouse_modeA==1と線分分割以外は同じ　
//

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_31(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==31move2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_31(Point p0) {
        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 0) {    //第1段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(5);
            }
            return;
        }

        if (i_egaki_dankai == 1) {    //第2段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                //点の選択が失敗した場合もi_select_mode=0にしないと、セレクトのつもりが動作モードがmove2p2pになったままになる
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

            }
            if (oc.kyori(s_step[1].geta(), s_step[2].geta()) < 0.00000001) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            }
            return;
        }


        if (i_egaki_dankai == 2) {    //第3段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

                return;

            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

            }
            return;
        }


        if (i_egaki_dankai == 3) {    //第4段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

            }
            if (oc.kyori(s_step[3].geta(), s_step[4].geta()) < 0.00000001) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            }
            return;
        }


    }

    //マウス操作(i_mouse_modeA==31move2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_31(Point p0) {
	}

    //マウス操作(i_mouse_modeA==31move2p2p　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_31(Point p0) {
        if (i_egaki_dankai == 4) {
            i_egaki_dankai = 0;
            i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            //double addx,addy;
            //addx=s_step[1].getbx()-s_step[1].getax();
            //addy=s_step[1].getby()-s_step[1].getay();


            PolygonStore ori_s_temp = new PolygonStore();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。
            ori_s_temp.move(s_step[1].geta(), s_step[2].geta(), s_step[3].geta(), s_step[4].geta());//全体を移動する

            int sousuu_old = ori_s.getsousuu();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getsousuu();
            ori_s.kousabunkatu(1, sousuu_old, sousuu_old + 1, sousuu_new);

            ori_s.unselect_all();
            kiroku();
            orihime_app.i_mouse_modeA = 19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }
    }

//  ********************************************


//--------------------------------------------
//32 32 32 32 32 32 32 32  i_mouse_modeA==32copy2p2p	入力 32 32 32 32 32 32 32 32

//動作概要　
//i_mouse_modeA==1と線分分割以外は同じ　
//

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_32(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==32copy2p2p2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_32(Point p0) {
        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 0) {    //第1段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(5);
            }
            return;
        }

        if (i_egaki_dankai == 1) {    //第2段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

            }
            if (oc.kyori(s_step[1].geta(), s_step[2].geta()) < 0.00000001) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


        if (i_egaki_dankai == 2) {    //第3段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

            }
            return;
        }


        if (i_egaki_dankai == 3) {    //第4段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

            }
            if (oc.kyori(s_step[3].geta(), s_step[4].geta()) < 0.00000001) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


    }

    //マウス操作(i_mouse_modeA==32copy2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_32(Point p0) {
	}

    //マウス操作(i_mouse_modeA==32copy2p2pp　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_32(Point p0) {
        if (i_egaki_dankai == 4) {
            i_egaki_dankai = 0;
            i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            //double addx,addy;
            //addx=s_step[1].getbx()-s_step[1].getax();
            //addy=s_step[1].getby()-s_step[1].getay();


            PolygonStore ori_s_temp = new PolygonStore();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setMemo(ori_s.getMemo_select_sentaku(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            //ori_s.del_selected_senbun_hayai();//セレクトされた折線を削除する。
            ori_s_temp.move(s_step[1].geta(), s_step[2].geta(), s_step[3].geta(), s_step[4].geta());//全体を移動する

            int sousuu_old = ori_s.getsousuu();
            ori_s.addMemo(ori_s_temp.getMemo());
            int sousuu_new = ori_s.getsousuu();
            ori_s.kousabunkatu(1, sousuu_old, sousuu_old + 1, sousuu_new);

            //ori_s.unselect_all();
            kiroku();
            orihime_app.i_mouse_modeA = 19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }
    }

//  ********************************************

    //12 12 12 12 12    i_mouse_modeA==12　;鏡映モード
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_12(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==12鏡映モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_12(Point p0) {

        p.set(camera.TV2object(p0));


        if (i_egaki_dankai == 0) {    //第1段階として、点を選択


            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(5);

                //s_step[i_egaki_dankai].set(moyori_senbun);        s_step[i_egaki_dankai].setcolor(5);

            }
            return;
        }

        if (i_egaki_dankai == 1) {    //第2段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[1].setb(s_step[2].getb());
            }
            if (s_step[1].getnagasa() < 0.00000001) {
                i_egaki_dankai = 0;
                i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }

/*


		moyori_ten.set(get_moyori_ten(p));
		if(p.kyori(moyori_ten)>d_hantei_haba){i_egaki_dankai=0;}
		s_step[1].set(p,moyori_ten);s_step[1].setcolor(icol);
		//k.addsenbun(p,p);
		//ieda=k.getsousuu();
		//k.setcolor(ieda,icol);
*/
    }

    //マウス操作(i_mouse_modeA==12鏡映モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_12(Point p0) {

        //Ten p =new Ten(); p.set(camera.TV2object(p0));
        //s_step[1].seta(p);

        //k.seta(ieda, p);
    }

    //マウス操作(i_mouse_modeA==12鏡映モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_12(Point p0) {
        Line adds = new Line();
        //Orisensyuugou ori_s_temp =new Orisensyuugou();
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 0;
            i_select_mode = 0;//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            int old_sousuu = ori_s.getsousuu();

            for (int i = 1; i <= ori_s.getsousuu(); i++) {
                if (ori_s.get_select(i) == 2) {
                    //Senbun adds =new Senbun();
                    adds.set(oc.sentaisyou_senbun_motome(ori_s.get(i), s_step[1]));
                    adds.setcolor(ori_s.getcolor(i));
                    //addsenbun(adds);

                    //ori_s_temp.addsenbun(adds);


                    //ori_s_temp.addsenbun(adds.geta(),adds.getb());
                    //ori_s_temp.setcolor(ori_s.getsousuu(),ori_s.getcolor(i));

                    ori_s.addsenbun(adds.geta(), adds.getb());
                    ori_s.setcolor(ori_s.getsousuu(), ori_s.getcolor(i));
                }
            }

            int new_sousuu = ori_s.getsousuu();

            ori_s.kousabunkatu(1, old_sousuu, old_sousuu + 1, new_sousuu);
/*

			for (int i=1; i<=ori_s_temp.getsousuu(); i++ ){
				adds.set(	ori_s_temp.get(i));
				adds.setcolor(	ori_s_temp.getcolor(i));
				addsenbun(adds);
			}


*/


            ori_s.unselect_all();
            kiroku();
            orihime_app.i_mouse_modeA = 19;//20200930 add セレクトした折線に作業して、その後またセレクトできる状態に戻すための行
        }
    }

    //Ten p =new Ten(); p.set(camera.TV2object(p0));
    //moyori_ten.set(get_moyori_ten(p));
    //s_step[1].seta(moyori_ten);
    //if(p.kyori(moyori_ten)<=d_hantei_haba){
    //	if(s_step[1].getnagasa()>0.00000001){

    //addsenbun(adds);
    //ieda=ori_s.getsousuu();
    //ori_s.setcolor(ieda,icol); qqqqqqqqq
    //ori_s.kousabunkatu_symple();
    //ori_s.kousabunkatu();ori_s.kousabunkatu_symple();


    //}
    //kiroku();
    //}


    //-------------------------
    public void del_selected_senbun() {
        //ori_s.del_selected_senbun();
        ori_s.del_selected_senbun_hayai();
        //Memo memo_temp = new Memo();memo_temp.set(ori_s.getMemo_select_jyogai(2));
        //ori_s.reset();
        //ori_s.setMemo(memo_temp);

    }

//34 34 34 34 34 34 34 34 34 34 34入力した線分に重複している折線を順に山谷にする

    public void mMoved_A_34(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==34　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_34(Point p0) {
        i_egaki_dankai = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            i_egaki_dankai = 0;
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }

    //マウス操作(i_mouse_modeA==34　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_34(Point p0) {
        mDragged_A_11(p0);
    }
/*
	public void mDragged_A_34(Ten p0) {

		Ten p =new Ten(); p.set(camera.TV2object(p0));
		s_step[1].seta(p);

		//k.seta(ieda, p);
	}
*/

    //マウス操作(i_mouse_modeA==34　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_34(Point p0) {

        Narabebako_int_double nbox = new Narabebako_int_double();

        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[1].seta(moyori_point);
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                if (s_step[1].getnagasa() > 0.00000001) {
                    for (int i = 1; i <= ori_s.getsousuu(); i++) {
						/*
						int i_senbun_kousa_hantei=oc.senbun_kousa_hantei(ori_s.get(i),s_step[1],0.0001,0.0001);
						int i_jikkou=0;
						if(i_senbun_kousa_hantei== 31 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 321 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 322 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 331 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 332 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 341 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 342 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 351 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 352 ){ i_jikkou=1;}

						if(i_senbun_kousa_hantei== 361 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 362 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 363 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 364 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 371 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 372 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 373 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 374 ){ i_jikkou=1;}
					*/
                        //if( i_jikkou==1){
                        if (oc.Senbun_kasanari_hantei(ori_s.get(i), s_step[1]) == 1) {
                            int_double i_d = new int_double(i, oc.kyori_senbun(s_step[1].getb(), ori_s.get(i)));
                            nbox.ire_i_tiisaijyun(i_d);
                        }

                    }

                    //System.out.println("i_d_sousuu"+nbox.getsousuu());

                    int icol_temp = icol;

                    for (int i = 1; i <= nbox.getsousuu(); i++) {

                        ori_s.setcolor(nbox.get_int(i), icol_temp);


                        if (icol_temp == 1) {
                            icol_temp = 2;
                        } else if (icol_temp == 2) {
                            icol_temp = 1;
                        }
                    }


                    kiroku();

                }
            }
        }

    }


//64 64 64 64 64 64 64 64 64 64 64 64 64入力した線分に重複している折線を削除する


    public void mMoved_A_64(Point p0) {
        mMoved_A_11(p0);
    }//近い既存点のみ表示

    //マウス操作(i_mouse_modeA==64　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_64(Point p0) {
        i_egaki_dankai = 1;

        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            i_egaki_dankai = 0;
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(5);

    }

    //マウス操作(i_mouse_modeA==64　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_64(Point p0) {
        mDragged_A_11(p0);
    }


    //マウス操作(i_mouse_modeA==64　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_64(Point p0) {

        Narabebako_int_double nbox = new Narabebako_int_double();

        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[1].seta(moyori_point);
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                if (s_step[1].getnagasa() > 0.00000001) {

                    ori_s.D_nisuru_line(s_step[1], "l");//lは小文字のエル

                    kiroku();

                }
            }
        }

    }


//65 65 65 65 65 65 65 65 65 65 65 65 65入力した線分に重複している折線やX交差している折線を削除する

    //マウスを動かしたとき
    public void mMoved_A_65(Point p0) {
        mMoved_m_00b(p0, 5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_65(Point p0) {
        mPressed_m_00b(p0, 5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_65(Point p0) {
        mDragged_m_00b(p0, 5);
    }

    //マウス操作(i_mouse_modeA==65　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_65(Point p0) {

        i_egaki_dankai = 0;
        p.set(camera.TV2object(p0));

        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            //やりたい動作はここに書く
            ori_s.D_nisuru_line(s_step[1], "lX");//lXは小文字のエルと大文字のエックス
            kiroku();
        }
    }

//----------------------------------------------------------------------------------------
//多角形を入力(既存頂点への引き寄せあるが既存頂点が遠い場合は引き寄せ無し)し、何らかの作業を行うセット
    //マウス操作(マウスを動かしたとき)を行う関数

    int i_takakukei_kansei = 0;//多角形が完成したら1、未完成なら0

    public void mMoved_takakukei_and_sagyou(Point p0) {

        //mMoved_m_002(p0,5);

        //マウス操作(マウスを動かしたとき)を行う関数
//	public void mMoved_m_002(Ten p0,int i_c) //マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。
        if (i_kou_mitudo_nyuuryoku == 1) {
            s_kouho[1].setiactive(3);
            p.set(camera.TV2object(p0));
            i_kouho_dankai = 1;
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) > p.kyori(s_step[1].geta())) {
                moyori_point.set(s_step[1].geta());
            }

            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }

            s_kouho[1].setcolor(5);
            //return;
        }

    }

    //マウス操作(ボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_takakukei_and_sagyou(Point p0) {
//i_egaki_dankai==0なのはこの操作ボタンを押した直後の段階か、多角形が完成して、その後ボタンを押した後
        if (i_takakukei_kansei == 1) {
            i_takakukei_kansei = 0;
            i_egaki_dankai = 0;
        }

        i_egaki_dankai = i_egaki_dankai + 1;
        s_step[i_egaki_dankai].setcolor(5);
        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 1) {
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) > d_hantei_haba) {
                moyori_point.set(p);
            }
            s_step[i_egaki_dankai].set(moyori_point, p);

        } else {//ここでi_egaki_dankai=0となることはない。
            s_step[i_egaki_dankai].set(s_step[i_egaki_dankai - 1].getb(), p);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------

    public void mDragged_takakukei_and_sagyou(Point p0) {
        //if(i_takakukei_kansei==0)//ここにくるときは必ずi_takakukei_kansei==0なのでif分は無意味

        p.set(camera.TV2object(p0));
        s_step[i_egaki_dankai].setb(p);


        if (i_kou_mitudo_nyuuryoku == 1) {
            i_kouho_dankai = 1;
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) > p.kyori(s_step[1].geta())) {
                moyori_point.set(s_step[1].geta());
            }


            if (p.kyori(moyori_point) < d_hantei_haba) {
                s_kouho[1].set(moyori_point, moyori_point);
            } else {
                s_kouho[1].set(p, p);
            }

            //s_kouho[i_egaki_dankai].setcolor(icol);
            s_step[i_egaki_dankai].setb(s_kouho[1].geta());
        }


//mDragged_m_00b(Ten p0,int i_c)
    }


    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_takakukei_and_sagyou(Point p0, int i_mode) {
        //i_kouho_dankai=0;
        //if(i_takakukei_kansei==0){//ここにくるときは必ずi_takakukei_kansei==0なのでif分は無意味

        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            moyori_point.set(p);
        }

        s_step[i_egaki_dankai].setb(moyori_point);


        if (i_egaki_dankai >= 2) {
            if (p.kyori(s_step[1].geta()) <= d_hantei_haba) {
                s_step[i_egaki_dankai].setb(s_step[1].geta());
                //i_O_F_C=1;
                i_takakukei_kansei = 1;
            }
        }

        //if(i_O_F_C==1){if(i_egaki_dankai==2){
        //i_egaki_dankai=0;
        //}}
        //}

        //int i_tekisetu=1;//外周部の形状が適切なら1、適切でないなら0
        //if(i_O_F_C==1){
        if (i_takakukei_kansei == 1) {
            Polygon Taka = new Polygon(i_egaki_dankai);
            for (int i = 1; i <= i_egaki_dankai; i++) {
                Taka.set(i, s_step[i].geta());
            }


            //各動作モードで独自に行う作業は以下に条件分けして記述する
            if (i_mode == 66) {
                ori_s.select_Takakukei(Taka, "select");
            }//66 66 66 66 66 多角形を入力し、それに全体が含まれる折線をselectする
            if (i_mode == 67) {
                ori_s.select_Takakukei(Taka, "unselect");
            }//67 67 67 67 67 多角形を入力し、それに全体が含まれる折線を折線をunselectする
            //各動作モードで独自に行う作業はここまで

            //i_egaki_dankai=0;
        }
    }


//20201024高密度入力がオンならばapのrepaint（画面更新）のたびにTen kus_sisuu=new Ten(es1.get_moyori_ten_sisuu(p_mouse_TV_iti));で最寄り点を求めているので、この描き職人内で別途最寄り点を求めていることは二度手間になっている。


    //66 66 66 66 66 多角形を入力し、それに全体が含まれる折線をselectする
    public void mMoved_A_66(Point p0) {
        mMoved_takakukei_and_sagyou(p0);
    }    //マウス操作(マウスを動かしたとき)を行う関数

    public void mPressed_A_66(Point p0) {
        mPressed_takakukei_and_sagyou(p0);
    }    //マウス操作でボタンを押したとき)時の作業----------------------------------------------------

    public void mDragged_A_66(Point p0) {
        mDragged_takakukei_and_sagyou(p0);
    }    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------

    public void mReleased_A_66(Point p0) {
        mReleased_takakukei_and_sagyou(p0, 66);
    }    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------


    //67 67 67 67 67 多角形を入力し、それに全体が含まれる折線を折線をunselectする
    public void mMoved_A_67(Point p0) {
        mMoved_takakukei_and_sagyou(p0);
    }    //マウス操作(マウスを動かしたとき)を行う関数

    public void mPressed_A_67(Point p0) {
        mPressed_takakukei_and_sagyou(p0);
    }    //マウス操作でボタンを押したとき)時の作業----------------------------------------------------

    public void mDragged_A_67(Point p0) {
        mDragged_takakukei_and_sagyou(p0);
    }    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------

    public void mReleased_A_67(Point p0) {
        mReleased_takakukei_and_sagyou(p0, 67);
    }    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------


//68 68 68 68 68 入力した線分に重複している折線やX交差している折線をselectする

    //マウスを動かしたとき
    public void mMoved_A_68(Point p0) {
        mMoved_m_00b(p0, 5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_68(Point p0) {
        mPressed_m_00b(p0, 5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_68(Point p0) {
        mDragged_m_00b(p0, 5);
    }

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_68(Point p0) {

        //i_egaki_dankai=0;
        p.set(camera.TV2object(p0));

        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            //やりたい動作はここに書く
            ori_s.select_lX(s_step[1], "select_lX");//lXは小文字のエルと大文字のエックス

        }

    }


//69 69 69 69 69 入力した線分に重複している折線やX交差している折線をunselectする

    //マウスを動かしたとき
    public void mMoved_A_69(Point p0) {
        mMoved_m_00b(p0, 5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mPressed_A_69(Point p0) {
        mPressed_m_00b(p0, 5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mDragged_A_69(Point p0) {
        mDragged_m_00b(p0, 5);
    }

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_69(Point p0) {

        //i_egaki_dankai=0;
        p.set(camera.TV2object(p0));

        s_step[1].seta(p);
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) <= d_hantei_haba) {
            s_step[1].seta(moyori_point);
        }
        if (s_step[1].getnagasa() > 0.00000001) {
            //やりたい動作はここに書く
            ori_s.select_lX(s_step[1], "unselect_lX");//lXは小文字のエルと大文字のエックス

        }

    }


//36 36 36 36 36 36 36 36 36 36 36入力した線分にX交差している折線を順に山谷にする


    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_36(Point p0) {
        mMoved_A_28(p0);
    }//近い既存点のみ表示


    //マウス操作(i_mouse_modeA==36　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_36(Point p0) {
        i_egaki_dankai = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            moyori_point.set(p);
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(icol);
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }

    //マウス操作(i_mouse_modeA==36　でドラッグしたとき)を行う関数----------------------------------------------------

    public void mDragged_A_36(Point p0) {
        mDragged_A_28(p0);
    }
/*
	public void mDragged_A_36(Ten p0) {

		Ten p =new Ten(); p.set(camera.TV2object(p0));
		s_step[1].seta(p);

		//k.seta(ieda, p);
	}
*/

    //マウス操作(i_mouse_modeA==36　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_36(Point p0) {

        Narabebako_int_double nbox = new Narabebako_int_double();

        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) > d_hantei_haba) {
                moyori_point.set(p);
            }
            s_step[1].seta(moyori_point);
            //if(p.kyori(moyori_ten)<=d_hantei_haba){
            if (s_step[1].getnagasa() > 0.00000001) {
                for (int i = 1; i <= ori_s.getsousuu(); i++) {
                    int i_senbun_kousa_hantei = oc.line_intersect_decide(ori_s.get(i), s_step[1], 0.0001, 0.0001);
                    int i_jikkou = 0;
                    if (i_senbun_kousa_hantei == 1) {
                        i_jikkou = 1;
                    }
                    if (i_senbun_kousa_hantei == 27) {
                        i_jikkou = 1;
                    }
                    if (i_senbun_kousa_hantei == 28) {
                        i_jikkou = 1;
                    }
                    //if(i_senbun_kousa_hantei== 31 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 321 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 322 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 331 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 332 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 341 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 342 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 351 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 352 ){ i_jikkou=1;}

                    //if(i_senbun_kousa_hantei== 361 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 362 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 363 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 364 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 371 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 372 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 373 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 374 ){ i_jikkou=1;}

                    if (i_jikkou == 1) {
                        int_double i_d = new int_double(i, oc.kyori(s_step[1].getb(), oc.kouten_motome(ori_s.get(i), s_step[1])));
                        nbox.ire_i_tiisaijyun(i_d);
                    }

                }

                System.out.println("i_d_sousuu" + nbox.getsousuu());

                int icol_temp = icol;

                for (int i = 1; i <= nbox.getsousuu(); i++) {

                    ori_s.setcolor(nbox.get_int(i), icol_temp);


                    if (icol_temp == 1) {
                        icol_temp = 2;
                    } else if (icol_temp == 2) {
                        icol_temp = 1;
                    }
                }


                kiroku();

            }
            //}
        }

    }


//63 63 63 外周部の折り畳みチェック


    //マウス操作(マウスを動かしたとき)を行う関数
    //public void mMoved_A_63(Ten p0) {mMoved_A_01(p0);}//近い既存点のみ表示


    public void mMoved_A_63(Point p0) {
		/* if(i_kou_mitudo_nyuuryoku==1){s_kouho[1].setiactive(3);

			p.set(camera.TV2object(p0));
			i_kouho_dankai=1;
			moyori_ten.set(get_moyori_ten(p));

			if(p.kyori(moyori_ten)<d_hantei_haba){  s_kouho[1].set(moyori_ten,moyori_ten);}
			else{					s_kouho[1].set(p,p);}

			//s_kouho[1].setcolor(icol);
			if(i_orisen_hojyosen==0){s_kouho[1].setcolor(icol);}
			if(i_orisen_hojyosen==1){s_kouho[1].setcolor(h_icol);}

			return;
		}
*/
    }


//icol=3 cyan
//icol=4 orange
//icol=5 mazenta
//icol=6 green
//icol=7 yellow


    //マウス操作(i_mouse_modeA==63　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_63(Point p0) {
        if (i_egaki_dankai == 0) {
            i_O_F_C = 0;
            i_egaki_dankai = i_egaki_dankai + 1;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            //moyori_ten.set(get_moyori_ten(p));
            //if(p.kyori(moyori_ten)>d_hantei_haba){moyori_ten.set(p);}
            s_step[i_egaki_dankai].set(p, p);
            s_step[i_egaki_dankai].setcolor(7);
            //k.addsenbun(p,p);
            //ieda=k.getsousuu();
            //k.setcolor(ieda,icol);
        } else {
            if (i_O_F_C == 0) {
                i_egaki_dankai = i_egaki_dankai + 1;
                p.set(camera.TV2object(p0));
                s_step[i_egaki_dankai].set(s_step[i_egaki_dankai - 1].getb(), p);
                s_step[i_egaki_dankai].setcolor(7);
                //   s_step[i_egaki_dankai-1].getb();
                //k.addsenbun(p,p);
                //ieda=k.getsousuu();
                //k.setcolor(ieda,icol);
            }
        }

    }


    //マウス操作(i_mouse_modeA==63　でドラッグしたとき)を行う関数----------------------------------------------------

    public void mDragged_A_63(Point p0) {
        if (i_O_F_C == 0) {
            p.set(camera.TV2object(p0));
            s_step[i_egaki_dankai].setb(p);
        }
    }

/*
	public void mDragged_A_36(Ten p0) {

		Ten p =new Ten(); p.set(camera.TV2object(p0));
		s_step[1].seta(p);

		//k.seta(ieda, p);
	}
*/

    //マウス操作(i_mouse_modeA==63　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_63(Point p0) {


        if (i_O_F_C == 0) {
            p.set(camera.TV2object(p0));
            s_step[i_egaki_dankai].setb(p);


            if (p.kyori(s_step[1].geta()) <= d_hantei_haba) {
                s_step[i_egaki_dankai].setb(s_step[1].geta());
                i_O_F_C = 1;
                //System.out.println("i_egaki_dankai = " + i_egaki_dankai );
                //System.out.println("i_O_F_C = " +i_O_F_C );
            }


            if (i_O_F_C == 1) {
                if (i_egaki_dankai == 2) {
                    i_egaki_dankai = 0;
                }
            }


        }

        int i_tekisetu = 1;//外周部の黄色い線と外周部の全折線の交差が適切（全てX型の交差）なら1、1つでも適切でないなら0
        if (i_O_F_C == 1) {
            Narabebako_int_double goukei_nbox = new Narabebako_int_double();
            Narabebako_int_double nbox = new Narabebako_int_double();
            for (int i_s_step = 1; i_s_step <= i_egaki_dankai; i_s_step++) {
                nbox.reset();
                for (int i = 1; i <= ori_s.getsousuu(); i++) {

                    int i_senbun_kousa_hantei = oc.line_intersect_decide(ori_s.get(i), s_step[i_s_step], 0.0001, 0.0001);
                    int i_jikkou = 0;

                    if ((i_senbun_kousa_hantei != 0) && (i_senbun_kousa_hantei != 1)) {
                        i_tekisetu = 0;
                    }

                    if (i_senbun_kousa_hantei == 1) {
                        i_jikkou = 1;
                    }
                    //if(i_senbun_kousa_hantei== 27 ){ i_jikkou=1;}
                    //if(i_senbun_kousa_hantei== 28 ){ i_jikkou=1;}

                    if (ori_s.get(i).getcolor() >= 3) {
                        i_jikkou = 0;
                    }


                    if (i_jikkou == 1) {
                        int_double i_d = new int_double(i, oc.kyori(s_step[i_s_step].geta(), oc.kouten_motome(ori_s.get(i), s_step[i_s_step])));
                        nbox.ire_i_tiisaijyun(i_d);
                    }
                }


                for (int i = 1; i <= nbox.getsousuu(); i++) {
                    int_double i_d = new int_double(nbox.get_int(i), goukei_nbox.getsousuu());
                    goukei_nbox.ire_i_tiisaijyun(i_d);
                }


            }
            System.out.println(" --------------------------------");

            //if (i_tekisetu==0){	JOptionPane.showMessageDialog(null, "Message");    }
            if (i_tekisetu == 1) {

                int i_hantai_color = 5;//判定結果を表す色番号。5（マゼンタ、赤紫）は折畳不可。3（シアン、水色）は折畳可。

                if (goukei_nbox.getsousuu() % 2 != 0) {//外周部として選択した折線の数が奇数
                    i_hantai_color = 5;
                } else if (goukei_nbox.getsousuu() == 0) {//外周部として選択した折線の数が0
                    i_hantai_color = 3;
                } else {//外周部として選択した折線の数が偶数
                    Line s_idou = new Line();
                    s_idou.set(ori_s.get(goukei_nbox.get_int(1)));

                    for (int i = 2; i <= goukei_nbox.getsousuu(); i++) {
                        //System.out.println(" i = "+i+"    Line No = " +goukei_nbox.get_int(i));
                        s_idou.set(oc.sentaisyou_senbun_motome(s_idou, ori_s.get(goukei_nbox.get_int(i))));
                    }
                    i_hantai_color = 5;
                    if (oc.hitosii(ori_s.get(goukei_nbox.get_int(1)).geta(), s_idou.geta(), 0.0001)) {
                        if (oc.hitosii(ori_s.get(goukei_nbox.get_int(1)).getb(), s_idou.getb(), 0.0001)) {
                            i_hantai_color = 3;
                        }
                    }
                }

                for (int i_s_step = 1; i_s_step <= i_egaki_dankai; i_s_step++) {
                    s_step[i_s_step].setcolor(i_hantai_color);
                }

                System.out.println(" --------------------------------");
            }
        }
//20201010

/*


		if(i_egaki_dankai==1000){
			i_egaki_dankai=0;
			//Ten p =new Ten();
			p.set(camera.TV2object(p0));
			moyori_ten.set(get_moyori_ten(p));
			if(p.kyori(moyori_ten)>d_hantei_haba){moyori_ten.set(p);}
			s_step[1].seta(moyori_ten);
			//if(p.kyori(moyori_ten)<=d_hantei_haba){
				if(s_step[1].getnagasa()>0.00000001){
					for (int i=1; i<=ori_s.getsousuu(); i++ ){
						int i_senbun_kousa_hantei=oc.senbun_kousa_hantei(ori_s.get(i),s_step[1],0.0001,0.0001);
						int i_jikkou=0;
						if(i_senbun_kousa_hantei== 1 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 27 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 28 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 31 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 321 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 322 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 331 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 332 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 341 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 342 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 351 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 352 ){ i_jikkou=1;}

						//if(i_senbun_kousa_hantei== 361 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 362 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 363 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 364 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 371 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 372 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 373 ){ i_jikkou=1;}
						//if(i_senbun_kousa_hantei== 374 ){ i_jikkou=1;}

						if( i_jikkou==1){
							int_double i_d	=new int_double(i,oc.kyori(s_step[1].getb(),oc.kouten_motome(ori_s.get(i),s_step[1])   ) );
							nbox.ire_i_tiisaijyun(i_d);
						}

					}

					System.out.println("i_d_sousuu"+nbox.getsousuu());

					int icol_temp=icol;

					for (int i=1; i<=nbox.getsousuu(); i++ ){

						ori_s.setcolor(nbox.get_int(i),icol_temp);


						if(icol_temp==1){icol_temp=2;
						}else if(icol_temp==2){icol_temp=1;
						}
					}




					kiroku();

				}
			//}
		}
*/
    }


//--------------------------------------------------------------------------------
//13 13 13 13 13 13    i_mouse_modeA==13　;角度系モード//線分指定、交点まで

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_13(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }

        int i_jyunnbi_step_suu = 1;//動作の準備として人間が選択するステップ数

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 0) {    //第１段階として、線分を選択
            moyori_line.set(get_moyori_senbun(p));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = 1;
                s_step[1].set(moyori_line);
                s_step[1].setcolor(5);
            }
        }

        if (i_egaki_dankai == i_jyunnbi_step_suu) {    //if(i_egaki_dankai==1){        //動作の準備として人間が選択するステップ数が終わった状態で実行
            int i_jyun;//i_jyunは線を描くとき順番に色を変えたいとき使う
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d) //    double d_kakudo_kei;double kakudo;

            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }

            if (id_kakudo_kei != 0) {
                Line s_kiso = new Line(s_step[1].geta(), s_step[1].getb());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                }

                s_kiso.set(s_step[1].getb(), s_step[1].geta());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                }
            }
            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;

                Line s_kiso = new Line(s_step[1].geta(), s_step[1].getb());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = jk[i];
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 2) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                    if (i == 3) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 4) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 5) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 6) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                }

                s_kiso.set(s_step[1].getb(), s_step[1].geta());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = jk[i];
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 2) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                    if (i == 3) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 4) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 5) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 6) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                }
            }


            return;
        }


        if (i_egaki_dankai == i_jyunnbi_step_suu + (honsuu) + (honsuu)) {//19     //動作の準備としてソフトが返答するステップ数が終わった状態で実行

            int i_tikai_s_step_suu = 0;

            //s_step[2から10]までとs_step[11から19]まで
            moyori_line.set(get_moyori_step_senbun(p, 2, 1 + (honsuu)));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_egaki_dankai].set(moyori_line);    //s_step[i_egaki_dankai].setcolor(2);//s_step[20]にinput
            }

            //s_step[2から10]までとs_step[11から19]まで
            moyori_line.set(get_moyori_step_senbun(p, 1 + (honsuu) + 1, 1 + (honsuu) + (honsuu)));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_egaki_dankai].set(moyori_line);    //s_step[i_egaki_dankai].setcolor(icol);
            }

            if (i_tikai_s_step_suu == 2) { //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //=     1+ (honsuu) +(honsuu) +  2 ){i_egaki_dankai=0; //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //例外処理としてs_step[20]とs_step[21]が平行の場合、より近いほうをs_stepが[20]とし、s_stepを[20]としてリターン（この場合はまだ処理は終われない）。
                //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
                //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する

                if (oc.heikou_hantei(s_step[i_egaki_dankai - 1], s_step[i_egaki_dankai], 0.1) != 0) {//ここは安全を見て閾値を0.1と大目にとっておこのがよさそう

                    //s_step[20]とs_step[21]と点pの距離  //public double kyori_senbun(Ten p0,Senbun s)
                    //if(oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1]) >  oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1+1])          ){
                    //     s_step[1+     (honsuu) +(honsuu)   +1].set(  s_step[1+     (honsuu) +(honsuu)   +1+1]                   )    ;
                    //}

                    //i_egaki_dankai=i_egaki_dankai-1;
                    //i_egaki_dankai=i_egaki_dankai-2;
                    i_egaki_dankai = 0;
                    return;
                }


                i_egaki_dankai = 0;

                //s_step[20]とs_step[21]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                Point kousa_point = new Point();
                kousa_point.set(oc.kouten_motome(s_step[1 + (honsuu) + (honsuu) + 1], s_step[1 + (honsuu) + (honsuu) + 1 + 1]));

                Line add_sen = new Line(kousa_point, s_step[1 + (honsuu) + (honsuu) + 1].geta());
                add_sen.setcolor(icol);
                if (add_sen.getnagasa() > 0.00000001) {
                    addsenbun(add_sen);
                }

                Line add_sen2 = new Line(kousa_point, s_step[1 + (honsuu) + (honsuu) + 1 + 1].geta());
                add_sen2.setcolor(icol);
                if (add_sen.getnagasa() > 0.00000001) {
                    addsenbun(add_sen2);
                }
                kiroku();
            }

            i_egaki_dankai = 0;
            return;
        }
        return;
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_13(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_13(Point p0) {
    }

//------


//--------------------------------------------------------------------------------
//17 17 17 17 17 17    i_mouse_modeA==17　;角度系モード

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_17(Point p0) {
        if (i_egaki_dankai <= 1) {
            mMoved_A_11(p0);//近い既存点のみ表示
        }
    }


    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_17(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }

        int i_jyunnbi_step_suu = 2;//動作の準備として人間が選択するステップ数

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 0) {    //第1段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(5);

                //s_step[i_egaki_dankai].set(moyori_senbun);        s_step[i_egaki_dankai].setcolor(5);

            }
            return;
        }

        if (i_egaki_dankai == 1) {    //第2段階として、点を選択
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                return;
            }
            if (p.kyori(moyori_point) < d_hantei_haba) {

                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[1].setb(s_step[2].getb());


            }

        }
// ------------------------------------------

        if (i_egaki_dankai == i_jyunnbi_step_suu) {    //if(i_egaki_dankai==1){        //動作の準備として人間が選択するステップ数が終わった状態で実行
            int i_jyun;//i_jyunは線を描くとき順番に色を変えたいとき使う
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d) //    double d_kakudo_kei;double kakudo;

            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }

            if (id_kakudo_kei != 0) {

                Line s_kiso = new Line(s_step[1].geta(), s_step[1].getb());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                }

                s_kiso.set(s_step[1].getb(), s_step[1].geta());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i_jyun == 0) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                }
            }
            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;

                Line s_kiso = new Line(s_step[1].geta(), s_step[1].getb());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = jk[i];
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 2) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                    if (i == 3) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 4) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 5) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 6) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                }

                s_kiso.set(s_step[1].getb(), s_step[1].geta());
                kakudo = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = jk[i];
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 10.0));
                    if (i == 1) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 2) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                    if (i == 3) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 4) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 5) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 6) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                }
            }


            return;
        }


        if (i_egaki_dankai == i_jyunnbi_step_suu + (honsuu) + (honsuu)) {//19     //動作の準備としてソフトが返答するステップ数が終わった状態で実行

            int i_tikai_s_step_suu = 0;

            //s_step[2から10]までとs_step[11から19]まで
            moyori_line.set(get_moyori_step_senbun(p, 3, 2 + (honsuu)));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_egaki_dankai].set(moyori_line);    //s_step[i_egaki_dankai].setcolor(2);//s_step[20]にinput
            }

            //s_step[2から10]までとs_step[11から19]まで
            moyori_line.set(get_moyori_step_senbun(p, 2 + (honsuu) + 1, 2 + (honsuu) + (honsuu)));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                s_step[i_egaki_dankai].set(moyori_line);    //s_step[i_egaki_dankai].setcolor(icol);
            }

            if (i_tikai_s_step_suu == 2) { //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //=     1+ (honsuu) +(honsuu) +  2 ){i_egaki_dankai=0; //この段階でs_stepが[21]までうまってたら、s_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //例外処理としてs_step[20]とs_step[21]が平行の場合、より近いほうをs_stepが[20]とし、s_stepを[20]としてリターン（この場合はまだ処理は終われない）。
                //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
                //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する

                if (oc.heikou_hantei(s_step[i_egaki_dankai - 1], s_step[i_egaki_dankai], 0.1) != 0) {//ここは安全を見て閾値を0.1と大目にとっておこのがよさそう

                    //s_step[20]とs_step[21]と点pの距離  //public double kyori_senbun(Ten p0,Senbun s)
                    //if(oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1]) >  oc.kyori_senbun(p, s_step[1+     (honsuu) +(honsuu)   +1+1])          ){
                    //     s_step[1+     (honsuu) +(honsuu)   +1].set(  s_step[1+     (honsuu) +(honsuu)   +1+1]                   )    ;
                    //}

                    //i_egaki_dankai=i_egaki_dankai-1;
                    //i_egaki_dankai=i_egaki_dankai-2;
                    i_egaki_dankai = 0;
                    return;
                }

//System.out.println("aaaaaaaaaaaaaa");
//System.out.println("bbbbbbbbbbbbb");
//System.out.println("cccccccccccccc");


                i_egaki_dankai = 0;

                //s_step[20]とs_step[21]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                Point kousa_point = new Point();
                kousa_point.set(oc.kouten_motome(s_step[2 + (honsuu) + (honsuu) + 1], s_step[2 + (honsuu) + (honsuu) + 1 + 1]));

                Line add_sen = new Line(kousa_point, s_step[2 + (honsuu) + (honsuu) + 1].geta());
                add_sen.setcolor(icol);
                if (add_sen.getnagasa() > 0.00000001) {
                    addsenbun(add_sen);
                }

                Line add_sen2 = new Line(kousa_point, s_step[2 + (honsuu) + (honsuu) + 2].geta());
                add_sen2.setcolor(icol);
                if (add_sen.getnagasa() > 0.00000001) {
                    addsenbun(add_sen2);
                }
                kiroku();
            }

            i_egaki_dankai = 0;
            return;

        }

        //i_egaki_dankai=               1+ (honsuu) +(honsuu) ;
        return;
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_17(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_17(Point p0) {
    }

//------


//VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

//16 16 16 16 16 16    i_mouse_modeA==16　;角度系モード

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_16(Point p0) {
        mMoved_A_17(p0);
    }


    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_16(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }


        double kakudo_kei = 36.0;
        double kakudo = 0.0;
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if ((i_egaki_dankai == 0) || (i_egaki_dankai == 1)) {
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                if (i_egaki_dankai == 0) {
                    return;
                }
            }
        }


        if (i_egaki_dankai == 2) {
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)


            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }

            if (id_kakudo_kei != 0) {


                Line s_kiso = new Line(s_step[2].geta(), s_step[1].geta());
                kakudo = 0.0;

                int i_jyun;
                i_jyun = 0;//i_jyunは線を描くとき順番に色を変えたいとき使う
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }

                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 1.0));
                    if (i_jyun == 0) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                }

            }

            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;


                Line s_kiso = new Line(s_step[2].geta(), s_step[1].geta());
                kakudo = 0.0;


                for (int i = 1; i <= 6; i++) {

                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = jk[i];
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 1.0));
                    if (i == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 2) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 3) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                    if (i == 4) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 5) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 6) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                }


            }


            return;
        }


        if (i_egaki_dankai == 2 + (honsuu)) {
            moyori_line.set(get_moyori_step_senbun(p, 3, 2 + (honsuu)));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_line);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_egaki_dankai].setcolor(2);
                return;
            }
            if (oc.kyori_senbun(p, moyori_line) >= d_hantei_haba) {
                i_egaki_dankai = 0;
                return;
            }
        }


        if (i_egaki_dankai == 2 + (honsuu) + 1) {

            moyori_line.set(get_moyori_senbun(p));
            if (oc.kyori_senbun(p, moyori_line) >= d_hantei_haba) {//最寄折線が遠かった場合
                i_egaki_dankai = 0;
                return;
            }

            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_line);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                s_step[i_egaki_dankai].setcolor(6);
                //return;
            }
        }

//		if(i_egaki_dankai==13){
        if (i_egaki_dankai == 2 + (honsuu) + 1 + 1) {
            i_egaki_dankai = 0;

            //s_step[12]とs_step[13]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
//			Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[12],s_step[13]));
            Point kousa_point = new Point();
            kousa_point.set(oc.kouten_motome(s_step[2 + (honsuu) + 1], s_step[2 + (honsuu) + 1 + 1]));
            Line add_sen = new Line(kousa_point, s_step[2].geta(), icol);
            if (add_sen.getnagasa() > 0.00000001) {
                addsenbun(add_sen);
                kiroku();
            }
            return;
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_16(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_16(Point p0) {
    }

//------


//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

//VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

//18 18 18 18 18 18    i_mouse_modeA==18　;角度系モード

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_A_18(Point p0) {
        mMoved_A_17(p0);
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_18(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (id_kakudo_kei != 0) {
            honsuu = id_kakudo_kei * 2 - 1;
        } else if (id_kakudo_kei == 0) {
            honsuu = 6;
        }


        double kakudo_kei = 36.0;
        double kakudo = 0.0;
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if ((i_egaki_dankai == 0) || (i_egaki_dankai == 1)) {
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                i_egaki_dankai = i_egaki_dankai + 1;
                s_step[i_egaki_dankai].set(moyori_point, moyori_point);
                s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                if (i_egaki_dankai == 0) {
                    return;
                }
            }
        }


        if (i_egaki_dankai == 2) {
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)

            if (id_kakudo_kei != 0) {
                d_kakudo_kei = 180.0 / (double) id_kakudo_kei;
            } else if (id_kakudo_kei == 0) {
                d_kakudo_kei = 180.0 / 4.0;
            }


            if (id_kakudo_kei != 0) {
                Line s_kiso = new Line(s_step[2].geta(), s_step[1].geta());
                kakudo = 0.0;

                int i_jyun;
                i_jyun = 0;//i_jyunは線を描くとき順番に色を変えたいとき使う
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = kakudo + d_kakudo_kei;
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 100.0));
                    if (i_jyun == 0) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i_jyun == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                }
            }


            if (id_kakudo_kei == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d_jiyuu_kaku_2;
                jk[2] = d_jiyuu_kaku_1;
                jk[3] = d_jiyuu_kaku_3;
                jk[4] = 360.0 - d_jiyuu_kaku_2;
                jk[5] = 360.0 - d_jiyuu_kaku_1;
                jk[6] = 360.0 - d_jiyuu_kaku_3;

                Line s_kiso = new Line(s_step[2].geta(), s_step[1].geta());
                kakudo = 0.0;


                for (int i = 1; i <= 6; i++) {
                    //i_jyun=i_jyun+1;if(i_jyun==2){i_jyun=0;}

                    i_egaki_dankai = i_egaki_dankai + 1;
                    kakudo = jk[i];
                    s_step[i_egaki_dankai].set(oc.Senbun_kaiten(s_kiso, kakudo, 100.0));
                    if (i == 1) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 2) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 3) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                    if (i == 4) {
                        s_step[i_egaki_dankai].setcolor(4);
                    }
                    if (i == 5) {
                        s_step[i_egaki_dankai].setcolor(6);
                    }
                    if (i == 6) {
                        s_step[i_egaki_dankai].setcolor(8);
                    }
                }
            }


            return;
        }


        //if(i_egaki_dankai==11){
        if (i_egaki_dankai == 2 + (honsuu)) {
            i_egaki_dankai = 0;
            moyori_step_line.set(get_moyori_step_senbun(p, 3, 2 + (honsuu)));
            if (oc.kyori_senbun(p, moyori_step_line) >= d_hantei_haba) {
                return;
            }

            if (oc.kyori_senbun(p, moyori_step_line) < d_hantei_haba) {
                Point mokuhyou_point = new Point();
                mokuhyou_point.set(oc.kage_motome(moyori_step_line, p));

                moyori_line.set(get_moyori_senbun(p));
                if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {//最寄折線が近い場合
                    if (oc.heikou_hantei(moyori_step_line, moyori_line, 0.000001) == 0) {//最寄折線が最寄step折線と平行の場合は除外
                        Point mokuhyou_point2 = new Point();
                        mokuhyou_point2.set(oc.kouten_motome(moyori_step_line, moyori_line));
                        if (p.kyori(mokuhyou_point) * 2.0 > p.kyori(mokuhyou_point2)) {
                            mokuhyou_point.set(mokuhyou_point2);
                        }

                    }

                }

                Line add_sen = new Line();
                add_sen.set(mokuhyou_point, s_step[2].geta());
                add_sen.setcolor(icol);
                addsenbun(add_sen);
                kiroku();
            }


			/*
			  oc.kouten_motome(s_step[12],s_step[13]);


				(s_step[1].geta(),oc.kage_motome(oc.Senbun2Tyokusen(s_step[2]),s_step[1].geta()));



			if(add_sen.getnagasa()>0.00000001){addsenbun(add_sen);}

				i_egaki_dankai=i_egaki_dankai+1;
				s_step[i_egaki_dankai].set(moyori_step_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
				s_step[i_egaki_dankai].setcolor(2);
				return;
			}

			}

//		if(i_egaki_dankai==12){
		if(i_egaki_dankai==2+ (honsuu)  +1 ){
			moyori_senbun.set(get_moyori_senbun(p));
			if(oc.kyori_senbun( p,moyori_senbun)<d_hantei_haba){
				i_egaki_dankai=i_egaki_dankai+1;
				s_step[i_egaki_dankai].set(moyori_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);

			//return;
			}
		}

//		if(i_egaki_dankai==13){
		if(i_egaki_dankai==2+ (honsuu)  +1  +1){
			i_egaki_dankai=0;

			//s_step[12]とs_step[13]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
//			Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[12],s_step[13]));
			Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(s_step[2+ (honsuu)  +1 ],s_step[2+ (honsuu)  +1  +1]));
			Senbun add_sen =new Senbun(kousa_ten,s_step[2].geta());
			if(add_sen.getnagasa()>0.00000001){addsenbun(add_sen);kiroku();}
			return;
	*/
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_18(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_18(Point p0) {
    }

//------


//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA


//14 14 14 14 14 14 14 14 14    i_mouse_modeA==14　;V追加モード

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_14(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        int mts_id;
        mts_id = ori_s.mottomo_tikai_senbun_sagasi(p);//mts_idは点pに最も近い線分の番号	public int ori_s.mottomo_tikai_senbun_sagasi(Ten p)
        Line mts = new Line(ori_s.geta(mts_id), ori_s.getb(mts_id));//mtsは点pに最も近い線分

        if (oc.kyori_senbun(p, mts) < d_hantei_haba) {
            //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
            //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
            Point pk = new Point();
            pk.set(oc.kage_motome(oc.Senbun2Tyokusen(mts), p));//pkは点pの（線分を含む直線上の）影

            //点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数	public int oc.hakononaka(Ten p1,Ten pa,Ten p2){}

            if (oc.hakononaka(mts.geta(), pk, mts.getb()) == 2) {
                //線分の分割-----------------------------------------
                ori_s.senbun_bunkatu(mts_id, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                kiroku();
            }

        }
        return;

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_14(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_14(Point p0) {
    }

//------


    public void v_del_all() {
        int sousuu_old = ori_s.getsousuu();
        ori_s.del_V_all();
        if (sousuu_old != ori_s.getsousuu()) {
            kiroku();
        }
    }

    public void v_del_all_cc() {
        int sousuu_old = ori_s.getsousuu();
        ori_s.del_V_all_cc();
        if (sousuu_old != ori_s.getsousuu()) {
            kiroku();
        }
    }

    // ------------------------------------------------------------
    public void all_s_step_to_orisen() {//20181014

        Line add_sen = new Line();
        for (int i = 1; i <= i_egaki_dankai; i++) {

            if (s_step[i].getnagasa() > 0.00000001) {
                add_sen.set(s_step[i]);
                add_sen.setcolor(icol);
                addsenbun(add_sen);
            } else {

                add_en(s_step[i].getax(), s_step[i].getay(), 5.0, 3);
            }
        }
        kiroku();

        //i_kouho_dankai//int sousuu_old =ori_s.getsousuu();
        //ori_s.del_V_all_cc();
        //if(sousuu_old !=ori_s.getsousuu()){kiroku();}
    }

//15 15 15 15 15 15 15 15 15    i_mouse_modeA==15　;V削除モード

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_15(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）	public int tyouten_syuui_sennsuu(Ten p) {

        ori_s.del_V(p, d_hantei_haba, 0.000001);
        kiroku();


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_15(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_15(Point p0) {
    }


//------

//41 41 41 41 41 41 41 41    i_mouse_modeA==41　;V削除モード(2つの折線の色が違った場合カラーチェンジして、点削除する。黒赤は赤赤、黒青は青青、青赤は黒にする)

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_41(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）	public int tyouten_syuui_sennsuu(Ten p) {

        ori_s.del_V_cc(p, d_hantei_haba, 0.000001);


        kiroku();


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_41(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_41(Point p0) {
    }


//------


    //-------------------------
//23 23 23 23 23
    //マウス操作(i_mouse_modeA==23 "->M" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_23(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==23でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_23(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==23 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_23(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_egaki_dankai = 0;

        if (p19_1.kyori(p0) > 0.000001) {//現状では赤を赤に変えたときもUNDO用に記録されてしまう20161218
            if (M_nisuru(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                kiroku();
            }
        }
        if (p19_1.kyori(p0) <= 0.000001) {//現状では赤を赤に変えたときもUNDO用に記録されてしまう20161218
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.setcolor(ori_s.mottomo_tikai_senbun_sagasi(p), 1);
                fix2(0.001, 0.5);
                kiroku();
            }
        }

    }

    //--------------------
    public int M_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.M_nisuru(p_a, p_b, p_c, p_d);
    }


    //---------------------
//24 24 24 24 24
    //マウス操作(i_mouse_modeA==24 "->V" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_24(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==24でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_24(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==24 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_24(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_egaki_dankai = 0;

        if (p19_1.kyori(p0) > 0.000001) {
            //V_nisuru(p19_1,p0);
            if (V_nisuru(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                kiroku();
            }
        }
        if (p19_1.kyori(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.setcolor(ori_s.mottomo_tikai_senbun_sagasi(p), 2);
                fix2(0.001, 0.5);
                kiroku();
            }
        }


    }

    //--------------------
    public int V_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.V_nisuru(p_a, p_b, p_c, p_d);
    }

    //---------------------
//25 25 25 25 25
    //マウス操作(i_mouse_modeA==25 "->E" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_25(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==25でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_25(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==25 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_25(Point p0) {//ここの処理の終わりに fix2(0.001,0.5);　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        i_egaki_dankai = 0;


        //E_nisuru(p19_1,p0);

        if (p19_1.kyori(p0) > 0.000001) {
            if (E_nisuru(p19_1, p0) != 0) {
                fix2(0.001, 0.5);
                kiroku();
            }
        }

        if (p19_1.kyori(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                ori_s.setcolor(ori_s.mottomo_tikai_senbun_sagasi(p), 0);
                fix2(0.001, 0.5);
                kiroku();
            }
        }


    }

    //--------------------
    public int E_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.E_nisuru(p_a, p_b, p_c, p_d);
    }

//---------------------

    //60 60 60 60 60
    //マウス操作(i_mouse_modeA==60 "->HK" でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_60(Point p0) {
        mPressed_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==60でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_60(Point p0) {
        mDragged_A_box_select(p0);
    }

    //マウス操作(i_mouse_modeA==60 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_60(Point p0) {
        i_egaki_dankai = 0;

        if (p19_1.kyori(p0) > 0.000001) {
            if (HK_nisuru(p19_1, p0) != 0) {
                kiroku();
            }//この関数は不完全なのでまだ未公開20171126
        }

        if (p19_1.kyori(p0) <= 0.000001) {
            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            if (ori_s.mottomo_tikai_senbun_kyori(p) < d_hantei_haba) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                //ori_s.setcolor(ori_s.mottomo_tikai_senbun_sagasi(p),3);
                if (ori_s.getcolor(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p)) < 3) {

                    Line add_sen = new Line();
                    add_sen.set(ori_s.get(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p)));
                    add_sen.setcolor(3);

                    ori_s.delsenbun_vertex(ori_s.mottomo_tikai_senbun_sagasi_gyakujyun(p));
                    addsenbun(add_sen);

                    en_seiri();
                    kiroku();
                }


                //kiroku();
            }
        }


    }

    //--------------------
    public int HK_nisuru(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getx(), p0a.gety());
        p0_b.set(p0a.getx(), p0b.gety());
        p0_c.set(p0b.getx(), p0b.gety());
        p0_d.set(p0b.getx(), p0a.gety());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return ori_s.HK_nisuru(p_a, p_b, p_c, p_d);
    }


//camera.object2TV

    public Line get_s_step(int i) {
        return s_step[i];
    }


//26 26 26 26    i_mouse_modeA==26　;背景setモード。

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_26(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 4;
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                p.set(moyori_point);
            }
            s_step[4].set(p, p);
            s_step[4].setcolor(i_egaki_dankai);
        }

        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 3;
            moyori_point.set(get_moyori_ten(p));
            if (p.kyori(moyori_point) < d_hantei_haba) {
                p.set(moyori_point);
            }
            s_step[3].set(p, p);
            s_step[3].setcolor(i_egaki_dankai);
        }

        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 2;
            s_step[2].set(p, p);
            s_step[2].setcolor(i_egaki_dankai);
        }

        if (i_egaki_dankai == 0) {
            i_egaki_dankai = 1;
            s_step[1].set(p, p);
            s_step[1].setcolor(i_egaki_dankai);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_26(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public int mReleased_A_26(Point p0) {
        return i_egaki_dankai;
    }

//------


//42 42 42 42 42 42 42 42 42 42 42 42 42 42 42　ここから

    //マウス操作(i_mouse_modeA==42 円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_42(Point p0) {
        i_egaki_dankai = 1;
        i_en_egaki_dankai = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;
        }
        s_step[1].set(p, moyori_point);
        s_step[1].setcolor(3);
        e_step[1].set(moyori_point.getx(), moyori_point.gety(), 0.0);
        e_step[1].setcolor(3);


//System.out.println("20170225  s_step[1].getax()"+s_step[1].getax());

//s_step[1].set(new Ten(200.0,200.0),new Ten(300.0,300.0));
        //k.addsenbun(p,p);
        //ieda=k.getsousuu();
        //k.setcolor(ieda,icol);
    }

    //マウス操作(i_mouse_modeA==42 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_42(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);
        e_step[1].setr(oc.kyori(s_step[1].geta(), s_step[1].getb()));

        //k.seta(ieda, p);
    }

    //マウス操作(i_mouse_modeA==42 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_42(Point p0) {
        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[1].seta(moyori_point);
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                if (s_step[1].getnagasa() > 0.00000001) {
                    //addsenbun(s_step[1]);
                    add_en(s_step[1].getbx(), s_step[1].getby(), s_step[1].getnagasa(), 3);
                    kiroku();
                }
            }

//System.out.println("20170227  ********************1");
//			Memo mtemp =new Memo();
//System.out.println("20170227  ********************2");
//			mtemp.set(getMemo());
//System.out.println("20170227  ********************3");
//			setMemo(mtemp);


        }

    }

//42 42 42 42 42 42 42 42 42 42 42 42 42 42 42  ここまで


//47 47 47 47 47 47 47 47 47 47 47 47 47 47 47　ここから

    //マウス操作(i_mouse_modeA==47 円入力(フリー　)　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_47(Point p0) {
        i_egaki_dankai = 1;
        i_en_egaki_dankai = 1;

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) > d_hantei_haba) {
            s_step[1].set(p, p);
            s_step[1].setcolor(3);
            e_step[1].set(p.getx(), p.gety(), 0.0);
            e_step[1].setcolor(3);
        } else {
            s_step[1].set(p, moyori_point);
            s_step[1].setcolor(3);
            e_step[1].set(moyori_point.getx(), moyori_point.gety(), 0.0);
            e_step[1].setcolor(3);
        }
    }

    //マウス操作(i_mouse_modeA==47 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_47(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        s_step[1].seta(p);
        e_step[1].setr(oc.kyori(s_step[1].geta(), s_step[1].getb()));
    }

    //マウス操作(i_mouse_modeA==47 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_47(Point p0) {
        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));

            if (p.kyori(moyori_point) <= d_hantei_haba) {
                s_step[1].seta(moyori_point);
            } else {
                s_step[1].seta(p);
            }

            if (s_step[1].getnagasa() > 0.00000001) {
                add_en(s_step[1].getbx(), s_step[1].getby(), s_step[1].getnagasa(), 3);
                kiroku();
            }
        }
    }

//47 47 47 47 47 47 47 47 47 47 47 47 47 47 47  ここまで


//44 44 44 44 44 44 44 44 44 44 44 44 44 44 44　ここから

    //マウス操作(i_mouse_modeA==44 円 分離入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_44(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));

        if (i_egaki_dankai == 0) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;
            if (p.kyori(moyori_point) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 1;
            i_en_egaki_dankai = 0;
            s_step[1].set(moyori_point, moyori_point);
            s_step[1].setcolor(3);
            return;
        }

        if (i_egaki_dankai == 1) {
            i_egaki_dankai = 1;
            i_en_egaki_dankai = 0;
            if (p.kyori(moyori_point) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 2;
            i_en_egaki_dankai = 1;
            s_step[2].set(p, moyori_point);
            s_step[2].setcolor(3);
            e_step[1].set(s_step[1].geta(), 0.0, 3);
            return;
        }


    }

    //マウス操作(i_mouse_modeA==44 円 分離入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_44(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 2;
            i_en_egaki_dankai = 1;
            s_step[2].seta(p);
            e_step[1].setr(s_step[2].getnagasa());
        }
    }

    //マウス操作(i_mouse_modeA==44 円 分離入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_44(Point p0) {
        if (i_egaki_dankai == 2) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[2].seta(moyori_point);
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                if (s_step[2].getnagasa() > 0.00000001) {
                    addsenbun(s_step[2]);
                    add_en(s_step[1].geta(), s_step[2].getnagasa(), 3);
                    kiroku();
                }
            }

//System.out.println("20170227  ********************1");
//			Memo mtemp =new Memo();
//System.out.println("20170227  ********************2");
//			mtemp.set(getMemo());
//System.out.println("20170227  ********************3");
//			setMemo(mtemp);


        }

    }

//44 44 44 44 44 44 44 44 44 44 44 44 44 44 44  ここまで


//48 48 48 48 48 48 48 48 48 48 48 48 48 48 48　ここから

    //マウス操作(i_mouse_modeA==48 同心円　線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_48(Point p0) {

        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_ensyuu.set(get_moyori_ensyuu(p));
        moyori_point.set(get_moyori_ten(p));

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 0)) {
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 1;
            e_step[1].set(moyori_ensyuu);
            e_step[1].setcolor(6);
            return;
        }


        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 1)) {
            if (p.kyori(moyori_point) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 1;
            i_en_egaki_dankai = 2;
            s_step[1].set(p, moyori_point);
            s_step[1].setcolor(3);
            e_step[2].set(e_step[1]);
            e_step[2].setcolor(3);
            return;
        }
    }

    //マウス操作(i_mouse_modeA==48 同心円　線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_48(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if ((i_egaki_dankai == 1) && (i_en_egaki_dankai == 2)) {
            s_step[1].seta(p);
            e_step[2].setr(e_step[1].getr() + s_step[1].getnagasa());
        }
    }

    //マウス操作(i_mouse_modeA==48 同心円　線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_48(Point p0) {
        if ((i_egaki_dankai == 1) && (i_en_egaki_dankai == 2)) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;

            //Ten p =new Ten();
            p.set(camera.TV2object(p0));
            moyori_point.set(get_moyori_ten(p));
            s_step[1].seta(moyori_point);
            if (p.kyori(moyori_point) <= d_hantei_haba) {
                if (s_step[1].getnagasa() > 0.00000001) {
                    addsenbun(s_step[1]);
                    e_step[2].setr(e_step[1].getr() + s_step[1].getnagasa());
                    add_en(e_step[2]);
                    kiroku();
                }
            }
        }
    }

//48 48 48 48 48 48 48 48 48 48 48 48 48 48 48  ここまで

//49 49 49 49 49 49 49 49 49 49 49 49 49 49 49　ここから

    //マウス操作(i_mouse_modeA==49 同心円　同心円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_49(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_ensyuu.set(get_moyori_ensyuu(p));
        moyori_point.set(get_moyori_ten(p));

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 0)) {
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 1;
            e_step[1].set(moyori_ensyuu);
            e_step[1].setcolor(6);
            return;
        }

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 1)) {
            //if(p.kyori(moyori_ten)>d_hantei_haba){return;}
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 2;
            e_step[2].set(moyori_ensyuu);
            e_step[2].setcolor(8);
            return;
        }

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 2)) {
            //if(p.kyori(moyori_ten)>d_hantei_haba){return;}
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 3;
            e_step[3].set(moyori_ensyuu);
            e_step[3].setcolor(8);
            return;
        }
    }

    //マウス操作(i_mouse_modeA==49 同心円　同心円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_49(Point p0) {

    }

    //マウス操作(i_mouse_modeA==49 同心円　同心円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_49(Point p0) {
        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 3)) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;
            double add_r = e_step[3].getr() - e_step[2].getr();
            if (Math.abs(add_r) > 0.00000001) {
                double new_r = add_r + e_step[1].getr();

                if (new_r > 0.00000001) {
                    e_step[1].setr(new_r);
					e_step[1].setcolor(3);
                    add_en(e_step[1]);
                    kiroku();
                }
            }
        }
    }

//49 49 49 49 49 49 49 49 49 49 49 49 49 49 49  ここまで

//51 51 51 51 51 51 51 51 51 51 51 51 51 51 51　ここから

    //マウス操作(i_mouse_modeA==51 平行線　幅指定入力モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_A_51(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 0)) {
            moyori_line.set(get_moyori_senbun(p));
            if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
                i_egaki_dankai = 1;
                i_en_egaki_dankai = 0;
                s_step[1].set(moyori_line);
                s_step[1].setcolor(6);
            }
            return;
        }

        if ((i_egaki_dankai == 1) && (i_en_egaki_dankai == 0)) {
            if (p.kyori(moyori_point) > d_hantei_haba) {
                return;
            }
            i_egaki_dankai = 4;
            i_en_egaki_dankai = 0;
            s_step[2].set(p, moyori_point);
            s_step[2].setcolor(3);
            s_step[3].set(s_step[1]);
            s_step[3].setcolor(8);
            s_step[4].set(s_step[1]);
            s_step[4].setcolor(8);
            return;
        }


        if ((i_egaki_dankai == 4) && (i_en_egaki_dankai == 0)) {
            //if(p.kyori(moyori_ten)>d_hantei_haba){return;}

            i_egaki_dankai = 3;
            i_en_egaki_dankai = 0;
            moyori_step_line.set(get_moyori_step_senbun(p, 3, 4));

            //if(oc.kyori_senbun(p,moyori_step_senbun)>d_hantei_haba){return;}
            s_step[3].set(moyori_step_line);
            return;
        }


    }

    //マウス操作(i_mouse_modeA==51 平行線　幅指定入力モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mDragged_A_51(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        if ((i_egaki_dankai == 4) && (i_en_egaki_dankai == 0)) {
            s_step[2].seta(p);
            s_step[3].set(oc.moveParallel(s_step[1], s_step[2].getnagasa()));
            s_step[3].setcolor(8);
            s_step[4].set(oc.moveParallel(s_step[1], -s_step[2].getnagasa()));
            s_step[4].setcolor(8);
        }
    }

    //マウス操作(i_mouse_modeA==51 平行線　幅指定入力モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_A_51(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));

        if ((i_egaki_dankai == 4) && (i_en_egaki_dankai == 0)) {
            if (p.kyori(moyori_point) >= d_hantei_haba) {
                i_egaki_dankai = 1;
                i_en_egaki_dankai = 0;
                return;
            }

            s_step[2].seta(moyori_point);

            if (s_step[2].getnagasa() < 0.00000001) {
                i_egaki_dankai = 1;
                i_en_egaki_dankai = 0;
                return;
            }
            s_step[3].set(oc.moveParallel(s_step[1], s_step[2].getnagasa()));
            s_step[3].setcolor(8);
            s_step[4].set(oc.moveParallel(s_step[1], -s_step[2].getnagasa()));
            s_step[4].setcolor(8);
        }


        if ((i_egaki_dankai == 3) && (i_en_egaki_dankai == 0)) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;

            s_step[3].setcolor(icol);
            addsenbun(s_step[3]);
            kiroku();

            return;
        }


    }

//51 51 51 51 51 51 51 51 51 51 51 51 51 51 51  ここまで

//45 45 45 45 45 45 45 45 45   i_mouse_modeA==45　;2円の共通接線入力モード。

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_45(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_ensyuu.set(get_moyori_ensyuu(p));

        if (i_en_egaki_dankai == 0) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 1;
            e_step[1].set(moyori_ensyuu);
            e_step[1].setcolor(6);
            return;
        }

        if (i_en_egaki_dankai == 1) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 1;
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 2;
            e_step[2].set(moyori_ensyuu);
            e_step[2].setcolor(6);
            return;
        }

        if (i_egaki_dankai > 1) {//			i_egaki_dankai=0;i_en_egaki_dankai=1;
            moyori_step_line.set(get_moyori_step_senbun(p, 1, i_egaki_dankai));

            if (oc.kyori_senbun(p, moyori_step_line) > d_hantei_haba) {
                return;
            }
            s_step[1].set(moyori_step_line);
            i_egaki_dankai = 1;
            i_en_egaki_dankai = 2;

            return;
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_45(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_45(Point p0) {
        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 2)) {
            Point c1 = new Point();
            c1.set(e_step[1].get_tyuusin());
            Point c2 = new Point();
            c2.set(e_step[2].get_tyuusin());

            double x1 = e_step[1].getx();
            double y1 = e_step[1].gety();
            double r1 = e_step[1].getr();
            double x2 = e_step[2].getx();
            double y2 = e_step[2].gety();
            double r2 = e_step[2].getr();
            //0,0,r,        xp,yp,R
            double xp = x2 - x1;
            double yp = y2 - y1;

            if (c1.kyori(c2) < 0.000001) {
                i_egaki_dankai = 0;
                i_en_egaki_dankai = 0;
                return;
            }//接線0本の場合

            if ((xp * xp + yp * yp) < (r1 - r2) * (r1 - r2)) {
                i_egaki_dankai = 0;
                i_en_egaki_dankai = 0;
                return;
            }//接線0本の場合

            if (Math.abs((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)) < 0.0000001) {//外接線1本の場合
                Point kouten = new Point();
                kouten.set(oc.naibun(c1, c2, -r1, r2));
                StraightLine ty = new StraightLine(c1, kouten);
                ty.tyokkouka(kouten);
                s_step[1].set(oc.en_to_tyokusen_no_kouten_wo_musubu_senbun(new Circle(kouten, (r1 + r2) / 2.0, 0), ty));

                i_egaki_dankai = 1;
                i_en_egaki_dankai = 2;
            }

            if (((r1 - r2) * (r1 - r2) < (xp * xp + yp * yp)) && ((xp * xp + yp * yp) < (r1 + r2) * (r1 + r2))) {//外接線2本の場合
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線

                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.tyokkouka(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.tyokkouka(new Point(xr2, yr2));

                s_step[1].set(new Point(xr1, yr1), oc.kage_motome(t1, new Point(x2, y2)));
                s_step[1].setcolor(8);
                s_step[2].set(new Point(xr2, yr2), oc.kage_motome(t2, new Point(x2, y2)));
                s_step[2].setcolor(8);

                i_egaki_dankai = 2;
                i_en_egaki_dankai = 2;

            }

            if (Math.abs((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)) < 0.0000001) {//外接線2本と内接線1本の場合
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線

                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.tyokkouka(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.tyokkouka(new Point(xr2, yr2));

                s_step[1].set(new Point(xr1, yr1), oc.kage_motome(t1, new Point(x2, y2)));
                s_step[1].setcolor(8);
                s_step[2].set(new Point(xr2, yr2), oc.kage_motome(t2, new Point(x2, y2)));
                s_step[2].setcolor(8);

                // -----------------------

                Point kouten = new Point();
                kouten.set(oc.naibun(c1, c2, r1, r2));
                StraightLine ty = new StraightLine(c1, kouten);
                ty.tyokkouka(kouten);
                s_step[3].set(oc.en_to_tyokusen_no_kouten_wo_musubu_senbun(new Circle(kouten, (r1 + r2) / 2.0, 0), ty));
                s_step[3].setcolor(8);
                // -----------------------

                i_egaki_dankai = 3;
                i_en_egaki_dankai = 2;
            }

            if ((r1 + r2) * (r1 + r2) < (xp * xp + yp * yp)) {//外接線2本と内接線2本の場合
                //             ---------------------------------------------------------------
                //                                     -------------------------------------
                //                 -------               -------------   -------   -------       -------------
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq3 = r1 * (xp * (r1 + r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double yq3 = r1 * (yp * (r1 + r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double xq4 = r1 * (xp * (r1 + r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double yq4 = r1 * (yp * (r1 + r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線


                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;
                double xr3 = xq3 + x1;
                double yr3 = yq3 + y1;
                double xr4 = xq4 + x1;
                double yr4 = yq4 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.tyokkouka(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.tyokkouka(new Point(xr2, yr2));
                StraightLine t3 = new StraightLine(x1, y1, xr3, yr3);
                t3.tyokkouka(new Point(xr3, yr3));
                StraightLine t4 = new StraightLine(x1, y1, xr4, yr4);
                t4.tyokkouka(new Point(xr4, yr4));

                s_step[1].set(new Point(xr1, yr1), oc.kage_motome(t1, new Point(x2, y2)));
                s_step[1].setcolor(8);
                s_step[2].set(new Point(xr2, yr2), oc.kage_motome(t2, new Point(x2, y2)));
                s_step[2].setcolor(8);
                s_step[3].set(new Point(xr3, yr3), oc.kage_motome(t3, new Point(x2, y2)));
                s_step[3].setcolor(8);
                s_step[4].set(new Point(xr4, yr4), oc.kage_motome(t4, new Point(x2, y2)));
                s_step[4].setcolor(8);

                //e_step[1].setcolor(3);
                //e_step[2].setcolor(3);

                i_egaki_dankai = 4;
                i_en_egaki_dankai = 2;

            }
        }

        if (i_egaki_dankai == 1) {

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;

            s_step[1].setcolor(icol);
            addsenbun(s_step[1]);
            kiroku();

            return;
        }


    }

//45 45 45 45 45 45 45 45 45  ここまで  ------


//50 50 50 50 50 50 50 50 50   i_mouse_modeA==50　;2円に幅同じで接する同心円を加える。

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_50(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_ensyuu.set(get_moyori_ensyuu(p));
        moyori_point.set(get_moyori_ten(p));

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 0)) {
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 1;
            e_step[1].set(moyori_ensyuu);
            e_step[1].setcolor(6);
            return;
        }

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 1)) {
            //if(p.kyori(moyori_ten)>d_hantei_haba){return;}
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 2;
            e_step[2].set(moyori_ensyuu);
            e_step[2].setcolor(6);
            return;
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_50(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_50(Point p0) {
        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 2)) {
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;
            double add_r = (oc.kyori(e_step[1].get_tyuusin(), e_step[2].get_tyuusin()) - e_step[1].getr() - e_step[2].getr()) * 0.5;


            if (Math.abs(add_r) > 0.00000001) {
                double new_r1 = add_r + e_step[1].getr();
                double new_r2 = add_r + e_step[2].getr();

                if ((new_r1 > 0.00000001) && (new_r2 > 0.00000001)) {
                    e_step[1].setr(new_r1);
					e_step[1].setcolor(3);
                    add_en(e_step[1]);
                    e_step[2].setr(new_r2);
					e_step[2].setcolor(3);
                    add_en(e_step[2]);
                    kiroku();
                }
            }
        }

    }

//50 50 50 50 50 50 50 50 50  ここまで  ------


//46 46 46 46 46 46 46 46 46   i_mouse_modeA==46　;反転入力モード。

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_46(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));

        moyori_ensyuu.set(get_moyori_ensyuu(p));

        if (i_egaki_dankai + i_en_egaki_dankai == 0) {
            moyori_line.set(get_moyori_senbun(p));


            if (oc.kyori_senbun(p, moyori_line) < oc.kyori_ensyuu(p, moyori_ensyuu)) {//線分の方が円周より近い
                i_egaki_dankai = 0;
                i_en_egaki_dankai = 0;
                if (oc.kyori_senbun(p, moyori_line) > d_hantei_haba) {
                    return;
                }
                i_egaki_dankai = 1;
                i_en_egaki_dankai = 0;
                s_step[1].set(moyori_line);
                s_step[1].setcolor(6);
                return;
            }


            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }

            i_egaki_dankai = 0;
            i_en_egaki_dankai = 1;
            e_step[1].set(moyori_ensyuu);
            e_step[1].setcolor(6);
            return;
        }

        if (i_egaki_dankai + i_en_egaki_dankai == 1) {
            if (oc.kyori_ensyuu(p, moyori_ensyuu) > d_hantei_haba) {
                return;
            }
            i_en_egaki_dankai = i_en_egaki_dankai + 1;
            e_step[i_en_egaki_dankai].set(moyori_ensyuu);
            e_step[i_en_egaki_dankai].setcolor(1);
            return;
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_46(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_46(Point p0) {
        if ((i_egaki_dankai == 1) && (i_en_egaki_dankai == 1)) {

            add_hanten(s_step[1], e_step[1]);
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;

        }

        if ((i_egaki_dankai == 0) && (i_en_egaki_dankai == 2)) {
            add_hanten(e_step[1], e_step[2]);
            i_egaki_dankai = 0;
            i_en_egaki_dankai = 0;
        }

    }

//46 46 46 46 46 46 46 46 46  ここまで  ------


//43 43 43 43 43 43 43 43 43   i_mouse_modeA==43　;円3点入力モード。

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_43(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
        }


    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_43(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_43(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;

            Line sen1 = new Line(s_step[1].geta(), s_step[2].geta());
            if (sen1.getnagasa() < 0.00000001) {
                return;
            }
            Line sen2 = new Line(s_step[2].geta(), s_step[3].geta());
            if (sen2.getnagasa() < 0.00000001) {
                return;
            }
            Line sen3 = new Line(s_step[3].geta(), s_step[1].geta());
            if (sen3.getnagasa() < 0.00000001) {
                return;
            }

            if (Math.abs(oc.kakudo(sen1, sen2) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(oc.kakudo(sen1, sen2) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(oc.kakudo(sen1, sen2) - 360.0) < 0.000001) {
                return;
            }

            if (Math.abs(oc.kakudo(sen2, sen3) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(oc.kakudo(sen2, sen3) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(oc.kakudo(sen2, sen3) - 360.0) < 0.000001) {
                return;
            }

            if (Math.abs(oc.kakudo(sen3, sen1) - 0.0) < 0.000001) {
                return;
            }
            if (Math.abs(oc.kakudo(sen3, sen1) - 180.0) < 0.000001) {
                return;
            }
            if (Math.abs(oc.kakudo(sen3, sen1) - 360.0) < 0.000001) {
                return;
            }


            StraightLine t1 = new StraightLine(sen1);
            t1.tyokkouka(oc.naibun(sen1.geta(), sen1.getb(), 1.0, 1.0));
            StraightLine t2 = new StraightLine(sen2);
            t2.tyokkouka(oc.naibun(sen2.geta(), sen2.getb(), 1.0, 1.0));
            add_en(oc.kouten_motome(t1, t2), oc.kyori(s_step[1].geta(), oc.kouten_motome(t1, t2)), 3);
            kiroku();
        }
    }

//43 43 43 43 43 43 43 43 43  ここまで  ------





















/*

//5555555555555555555555    i_mouse_modeA==5　

//マウス操作(ボタンを押したとき)時の作業
	public void mPressed_A_05(Ten p0) {
		//Ten p =new Ten();
		p.set(camera.TV2object(p0));
		moyori_senbun.set(get_moyori_senbun(p));
		if(oc.kyori_senbun( p,moyori_senbun)<d_hantei_haba){
			i_egaki_dankai=i_egaki_dankai+1;
			s_step[i_egaki_dankai].set(moyori_senbun);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
s_step[i_egaki_dankai].setcolor(6);
		}



	}

//マウス操作(ドラッグしたとき)を行う関数
	public void mDragged_A_05(Ten p0) {	}

//マウス操作(ボタンを離したとき)を行う関数
	public void mReleased_A_05(Ten p0){			if(i_egaki_dankai==3){i_egaki_dankai=0;}}

//------

//66666666666666666666    i_mouse_modeA==6　

//マウス操作(ボタンを押したとき)時の作業
	public void mPressed_A_06(Ten p0) {


		//Ten p =new Ten();
		p.set(camera.TV2object(p0));
		moyori_ten.set(get_moyori_ten(p));
		if(p.kyori(moyori_ten)<d_hantei_haba){
			i_egaki_dankai=i_egaki_dankai+1;
			s_step[i_egaki_dankai].set(moyori_ten,moyori_ten);s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
		}


	}

//マウス操作(ドラッグしたとき)を行う関数
	public void mDragged_A_06(Ten p0) {	}

//マウス操作(ボタンを離したとき)を行う関数
	public void mReleased_A_06(Ten p0){
		if(i_egaki_dankai==3){i_egaki_dankai=0;}



	}

//------



*/


//10001

    //マウス操作(i_mouse_modeA==10001　でボタンを押したとき)時の作業
    public void mPressed_A_10001(Point p0) {


        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_point.set(get_moyori_ten(p));
        if (p.kyori(moyori_point) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_point, moyori_point);
            s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
        }


    }

    //マウス操作(i_mouse_modeA==10001　でドラッグしたとき)を行う関数
    public void mDragged_A_10001(Point p0) {
    }

    //マウス操作(i_mouse_modeA==10001　でボタンを離したとき)を行う関数
    public void mReleased_A_10001(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;
        }


    }

//------
//10002

    //マウス操作(i_mouse_modeA==10002　でボタンを押したとき)時の作業
    public void mPressed_A_10002(Point p0) {
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        moyori_line.set(get_moyori_senbun(p));
        if (oc.kyori_senbun(p, moyori_line) < d_hantei_haba) {
            i_egaki_dankai = i_egaki_dankai + 1;
            s_step[i_egaki_dankai].set(moyori_line);//s_step[i_egaki_dankai].setcolor(i_egaki_dankai);
            s_step[i_egaki_dankai].setcolor(6);
        }


    }

    //マウス操作(i_mouse_modeA==10002　でドラッグしたとき)を行う関数
    public void mDragged_A_10002(Point p0) {
    }

    //マウス操作(i_mouse_modeA==10002　でボタンを離したとき)を行う関数
    public void mReleased_A_10002(Point p0) {
        if (i_egaki_dankai == 3) {
            i_egaki_dankai = 0;
        }
    }

//------
//------
//10003

    //マウス操作(i_mouse_modeA==10003　でボタンを押したとき)時の作業
    public void mPressed_A_10003(Point p0) {
    }

    //マウス操作(i_mouse_modeA==10003　でドラッグしたとき)を行う関数
    public void mDragged_A_10003(Point p0) {
    }

    //マウス操作(i_mouse_modeA==10003　でボタンを離したとき)を行う関数
    public void mReleased_A_10003(Point p0) {
    }

//------


    public void set_i_kitei_jyoutai(int i) {
        kus.set_i_kitei_jyoutai(i);
    }

    public int get_i_kitei_jyoutai() {
        return kus.jyoutai();
    }


//public int  getsousuu() {return ori_s.getsousuu();}


    public void set_i_orisen_bunkatu_suu(int i) {
        i_orisen_bunkatu_suu = i;
        if (i_orisen_bunkatu_suu < 1) {
            i_orisen_bunkatu_suu = 1;
        }
    }


    public void set_d_naibun_st(double ds, double dt) {
        d_naibun_s = ds;
        d_naibun_t = dt;
    }

    public void set_d_jiyuu_kaku(double d_1, double d_2, double d_3) {
        d_jiyuu_kaku_1 = d_1;
        d_jiyuu_kaku_2 = d_2;
        d_jiyuu_kaku_3 = d_3;
    }

    public void set_i_sei_takakukei(int i) {
        i_sei_takakukei = i;
        if (i_sei_takakukei < 3) {
            i_orisen_bunkatu_suu = 3;
        }
    }

    // ------------
    int i_orisen_hojyosen_old = 0;

    public void set_i_orisen_hojyosen(int i) {
        i_orisen_hojyosen_old = i_orisen_hojyosen;
        i_orisen_hojyosen = i;
    }

    public void modosi_i_orisen_hojyosen() {
        i_orisen_hojyosen = i_orisen_hojyosen_old;
    }
// ------------


    public void check1(double r_hitosii, double heikou_hantei) {
        ori_s.check1(r_hitosii, heikou_hantei);
    }//ori_sにおいて、チェックしておかしい折線をセレクト状態にする。

    public void fix1(double r_hitosii, double heikou_hantei) {
        while (ori_s.fix1(r_hitosii, heikou_hantei) == 1) {
		}
        //ori_s.addsenbun  delsenbunを実施しているところでcheckを実施
        if (i_check1 == 1) {
            check1(0.001, 0.5);
        }
        if (i_check2 == 1) {
            check2(0.01, 0.5);
        }
        if (i_check3 == 1) {
            check3(0.0001);
        }
        if (i_check4 == 1) {
            check4(0.0001);
        }

    }

    public void set_i_check1(int i) {
        i_check1 = i;
    }

    public void check2(double r_hitosii, double heikou_hantei) {
        ori_s.check2(r_hitosii, heikou_hantei);
    }

    public void fix2(double r_hitosii, double heikou_hantei) {
        while (ori_s.fix2(r_hitosii, heikou_hantei) == 1) {
		}
        //ori_s.addsenbun  delsenbunを実施しているところでcheckを実施
        if (i_check1 == 1) {
            check1(0.001, 0.5);
        }
        if (i_check2 == 1) {
            check2(0.01, 0.5);
        }
        if (i_check3 == 1) {
            check3(0.0001);
        }
        if (i_check4 == 1) {
            check4(0.0001);
        }

    }

    public void set_i_check2(int i) {
        i_check2 = i;
    }

    public void check3(double r) {
        ori_s.check3(r);
    }

    //public void  check4(double r){ori_s.check4(r);}
    public void check4(double r) {
        orihime_app.check4(r);
    }

    public void ap_check4(double r) {
        ori_s.check4(r);
    }


    public void set_i_check3(int i) {
        i_check3 = i;
    }

    public void set_i_check4(int i) {
        i_check4 = i;
    }


// *******************************************************************************************************

    int i_ck4_color_toukado_sabun = 10;

    public void ck4_color_sage() {
        i_ck4_color_toukado = i_ck4_color_toukado - i_ck4_color_toukado_sabun;
        if (i_ck4_color_toukado < 50) {
            i_ck4_color_toukado = i_ck4_color_toukado + i_ck4_color_toukado_sabun;
        }
    }

    public void ck4_color_age() {
        i_ck4_color_toukado = i_ck4_color_toukado + i_ck4_color_toukado_sabun;
        if (i_ck4_color_toukado > 250) {
            i_ck4_color_toukado = i_ck4_color_toukado - i_ck4_color_toukado_sabun;
        }
    }


    //public void  fix3(double r_hitosii,double heikou_hantei){while(ori_s.fix3(r_hitosii,heikou_hantei)==1){;}}

    public void h_setcolor(int i) {
        h_icol = i;
    }

    public void set_Ubox_undo_suu(int i) {
        Ubox.set_i_undo_suu(i);
    }

    public void set_h_Ubox_undo_suu(int i) {
        h_Ubox.set_i_undo_suu(i);
    }

    public void en_seiri() {//全ての円を対象に整理をする。
        ori_s.en_seiri();
    }


    // ---------------------------
    public void add_hanten(Circle e0, Circle eh) {


        //e0の円周が(x,y)を通るとき
        if (Math.abs(oc.kyori(e0.get_tyuusin(), eh.get_tyuusin()) - e0.getr()) < 0.0000001) {
            Line s_add = new Line();
            s_add.set(eh.hanten_En2Senbun(e0));
            //s_add.setcolor(3);
            addsenbun(s_add);
            kiroku();
            return;
        }


        //e0の円周の内部に(x,y)がくるとき
        //if(oc.kyori(e0.get_tyuusin(),eh.get_tyuusin())<e0.getr()){
        //	return;
        //}

//System.out.println("20170315  ********************3");
        //e0の円周が(x,y)を通らないとき。e0の円周の外部に(x,y)がくるとき//e0の円周の内部に(x,y)がくるとき
        Circle e_add = new Circle();
        e_add.set(eh.hanten(e0));
        add_en(e_add);
        kiroku();
    }

    // ---------------------------
    public void add_hanten(Line s0, Circle eh) {
        StraightLine ty = new StraightLine(s0);
        //s0上に(x,y)がくるとき
        if (ty.calculateDistance(eh.get_tyuusin()) < 0.0000001) {
            return;
        }

        //s0が(x,y)を通らないとき。
        Circle e_add = new Circle();
        e_add.set(eh.hanten_Senbun2En(s0));
        add_en(e_add);
        kiroku();
    }


    //public double get_kus.d_haba()(){return kus.d_haba();	}

    public double get_d_hantei_haba() {
        return d_hantei_haba;
    }


    public void set_a_to_heikouna_memori_kannkaku(int i) {
        kus.set_a_to_heikouna_memori_kannkaku(i);
    }

    public void set_b_to_heikouna_memori_kannkaku(int i) {
        kus.set_b_to_heikouna_memori_kannkaku(i);
    }

    public void a_to_heikouna_memori_iti_idou() {
        kus.a_to_heikouna_memori_iti_idou();
    }

    public void b_to_heikouna_memori_iti_idou() {
        kus.b_to_heikouna_memori_iti_idou();
    }



/*
一値分解する関数itti_bunkai();

public void itti_bunkai(){//（１）2点a,bを指定







}

（１）2点a,bを指定
（２）aを基点とするベクトルabが最初にぶつかる折線との交点cを求める。abと重なる折線は無視
（３）
cが既存の折線の柄の部分だった場合、その線で鏡映し、cをaとし、bを鏡映線の先の点として再帰的に。
cが点だった場合、すでに通過していた点なら、return;


cからベクトルacと一値性を持つベクトルを求める、






*/

    //--------------------------------------------
    public void test1() {//デバック等のテスト用

        System.out.println("ori_s.getsousuu()  " + ori_s.getsousuu());

    }

    //--------------------------------------------

    //メモ
    //icol=0 black
    //icol=1 red
    //icol=2 blue
    //icol=3 cyan
    //icol=4 orange
    //icol=5 mazenta
    //icol=6 green
    //icol=7 yellow
    //icol=8 new Color(210,0,255) //紫


}
