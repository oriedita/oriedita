package jp.gr.java_conf.mt777.origami.orihime.oriagari_zu;

//import java.awt.MouseInfo;
//import java.awt.PointerInfo;

import java.awt.*;
//import java.awt.BorderLayout;

//画像出力のため追加開始　20170107
//import java.io.*;
//画像出力のため追加終わり

import jp.gr.java_conf.mt777.origami.orihime.*;
import jp.gr.java_conf.mt777.origami.orihime.kihonshi_syokunin.*;
import jp.gr.java_conf.mt777.origami.orihime.tenkaizu_syokunin.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.*;

import jp.gr.java_conf.mt777.origami.dougu.camera.*;
import jp.gr.java_conf.mt777.origami.dougu.keijiban.*;
import jp.gr.java_conf.mt777.origami.dougu.senbunsyuugou.*;
import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.kiroku.moji_sousa.*;
import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.oritacalc.*;


// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------

//public class Oriagari_Zu extends Oriagari_Zu_00 {//Oriagari_Zuが基本となる折上がり予測アルゴリズム

public class Oriagari_Zu {

    App orihime_app;

    OritaCalc oc = new OritaCalc();
    Moji_sousa ms = new Moji_sousa(); //文字列操作用の関数を集めたクラス

    double r = 3.0;                   //基本枝構造の直線の両端の円の半径、枝と各種ポイントの近さの判定基準


    public double d_oriagarizu_syukusyaku_keisuu = 1.0;//折り上がり図の縮尺係数
    public double d_oriagarizu_kaiten_hosei = 0.0;//折り上がり図の回転表示角度の補正角度

    public Kihonshi_Syokunin ks2 = new Kihonshi_Syokunin(r);    //基本枝職人。ts2の持つ点集合をts3に渡す前に、
    //ts2の持つ点集合は棒が重なっていたりするかもしれないので、
    //いったんks2に渡して線分集合として整理する。

    public Tenkaizu_Syokunin ts1 = new Tenkaizu_Syokunin(r);    //展開図職人。入力された線分集合を最初に折って針金状の点集合の折り上がり図を作る
    public Tenkaizu_Syokunin ts2 = new Tenkaizu_Syokunin(r);    //展開図職人。ts1の作った針金状の点集合の折り上がり図を保持し、線分集合にするなどの働きをする。
    public Tenkaizu_Syokunin ts3 = new Tenkaizu_Syokunin(r);    //展開図職人。ts1の作った針金状の点集合を整理し。新たに面を認識するなどの働きを持つ。

    public Jyougehyou_Syokunin js;
    //public Jyougehyou_Syokunin js      = new Jyougehyou_Syokunin();
    //public Jyougehyou_Syokunin(ap ap0){ orihime_ap=ap0;    reset();	}    //コンストラクタ

    public Camera camera_of_oriagarizu = new Camera();
    public Camera camera_of_oriagari_omote = new Camera();
    public Camera camera_of_oriagari_ura = new Camera();
    public Camera camera_of_touka_omote = new Camera();
    public Camera camera_of_touka_ura = new Camera();

    public Color oriagarizu_F_color = new Color(255, 255, 50);//折り上がり図の表面の色
    public Color oriagarizu_B_color = new Color(233, 233, 233);//折り上がり図の裏面の色
    public Color oriagarizu_L_color = Color.black;//折り上がり図の線の色

    public int hyouji_flg_backup = 4;//表示様式hyouji_flgの一時的バックアップ用
    //int hyouji_flg_backup=4;//表示様式hyouji_flgの一時的バックアップ用
    public int hyouji_flg = 0;//折り上がり図の表示様式の指定。1なら展開図整理、2なら針金図。3なら透過図。4なら実際に折り紙を折った場合と同じ。
    public int i_suitei_meirei = 0;//折り畳み推定をどの段階まで行うかの指示
    public int i_suitei_dankai = 0;//折り畳み推定がどの段階までできたかの表示
    //public int i_suitei_jissi_umu=0;//int i_suitei_jissi_umuは、折り畳み推定の計算を実施したかどうかを表す。int i_suitei_jissi_umu=0なら実施しない。1なら実施した。


    //表示用の値を格納する変数
    public int ip1 = -1;//上下表職人の初期設定時に、折った後の表裏が同じ面が
    //隣接するという誤差があれが0を、無ければ1000を格納する変数。
    //ここでの初期値は(0か1000)以外の数ならなんでもいい。
    public int ip2 = -1;//上下表職人が折り畳み可能な重なり方を探した際に、
    //可能な重なり方がなければ0を、可能な重なり方があれば1000を格納する変数。
    //ここでの初期値は(0か1000)以外の数ならなんでもいい。
    //int ip3a=1;
    public int ip3 = 1;//ts1が折り畳みを行う際の基準面を指定するのに使う。

    public int ip4 = 0;//これは、ts1の最初に裏返しをするかどうかを指定する。0ならしない。1なら裏返す。

    public int ip5 = -1;    //上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
    //さらに別の紙の重なりをさがす時の最初のjs.susumu(Smensuu)の結果。
    //0なら新たにsusumu余地がなかった。0以外なら変化したSmenのidの最も小さい番号

    public int ip6 = -1;    //上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
    //さらに別の紙の重なりをさがす時の js.kanou_kasanari_sagasi()の結果。
    //0なら可能な重なりかたとなる状態は存在しない。
    //1000なら別の重なり方が見つかった。

    public int betu_sagasi_flg = 0;     //これは「別の重なりを探す」ことが有効の場合は１、無効の場合は０をとる。
    public int hakkenn_sita_kazu = 0;    //折り重なり方で、何通り発見したかを格納する。


    //public int i_AS_matome =100;//折畳み推定の別解をまとめて出す個数//20171217 ver3.030では使われていない。


    public int toukazu_toukado = 16;//透過図をカラー描画する際の透過度


    public int i_oriagari_sousa_mode = 1;//1=変形時は針金図になり、変形後に上下表を再計算する、旧来からのモード、2=変形時も折り上がり図のままで、基本的に変形後に上下表は再計算しないモード


    //public Keijiban keijiban =new Keijiban(this);
    public Keijiban keijiban;


    public boolean w_image_jikkoutyuu = false;//折畳みまとめ実行の。単一回のイメージ書き出しが実行中ならtureになる。
    public boolean matome_write_image_jikkoutyuu = false;//matome_write_imageが実行中ならtureになる。これは、複数の折りあがり形の予測の書き出しがかすれないように使う。20170613

    String fname_and_number;//まとめ書き出しに使う。


    //各種変数の定義
    String c = "";                //文字列処理用のクラスのインスタンス化
    public String text_kekka;                //結果表示用文字列のクラスのインスタンス化


    int i_fold_type = 0;//=0は通常の展開図の全折線を対象とした折り畳み推定、=1はselectされた折線を対象とした折り畳み推定、


    public int i_toukazu_color = 0;//透過図をカラーにするなら１、しないなら０


    // **************************************************
//コンストラクタ
    public Oriagari_Zu(App app0) {

        orihime_app = app0;

        js = new Jyougehyou_Syokunin(app0);
        keijiban = new Keijiban(app0);

        //カメラの設定 ------------------------------------------------------------------
        oriagari_camera_syokika();
        //カメラの設定はここまで----------------------------------------------------


        text_kekka = "";


    }

    //----------------------------------------------------------
    public void estimated_initialize() {
        text_kekka = "";
        ks2.reset();
        ts1.reset();
        ts2.reset();
        ts3.reset();
        js.reset();

        //oriagari_camera_syokika();		//20170615 実行しないようにした（折りあがり図の表示状況を変えないようにするため）
        //ts2.setCamera(camera_of_oriagarizu);	//20170615 実行しないようにした（折りあがり図の表示状況を変えないようにするため）
        //js.setCamera(camera_of_oriagarizu);	//20170615 実行しないようにした（折りあがり図の表示状況を変えないようにするため）

        hyouji_flg = 0;//折り上がり図の表示様式の指定。1なら展開図整理、2なら針金図。3なら透過図。5なら実際に折り紙を折った場合と同じ。
        i_suitei_meirei = 0;//折り畳み推定をどの段階まで行うかの指示
        i_suitei_dankai = 0;//折り畳み推定がどの段階までできたかの表示
        betu_sagasi_flg = 0;

        matome_write_image_jikkoutyuu = false; //複数の折りあがり形の予測の書き出しがが実行中ならtureになる。20170615


    }


    //----------------------------------------------------------
    public void oriagari_camera_syokika() {


        //camera_of_oriagarizu	;
        camera_of_oriagarizu.set_camera_ichi_x(0.0);
        camera_of_oriagarizu.set_camera_ichi_y(0.0);
        camera_of_oriagarizu.set_camera_kakudo(0.0);
        camera_of_oriagarizu.set_camera_kagami(1.0);
        camera_of_oriagarizu.set_camera_bairitsu_x(1.0);
        camera_of_oriagarizu.set_camera_bairitsu_y(1.0);
        camera_of_oriagarizu.set_hyouji_ichi_x(350.0);
        camera_of_oriagarizu.set_hyouji_ichi_y(350.0);


        //camera_of_oriagari_omote	;
        camera_of_oriagari_omote.set_camera_ichi_x(0.0);
        camera_of_oriagari_omote.set_camera_ichi_y(0.0);
        camera_of_oriagari_omote.set_camera_kakudo(0.0);
        camera_of_oriagari_omote.set_camera_kagami(1.0);
        camera_of_oriagari_omote.set_camera_bairitsu_x(1.0);
        camera_of_oriagari_omote.set_camera_bairitsu_y(1.0);
        camera_of_oriagari_omote.set_hyouji_ichi_x(350.0);
        camera_of_oriagari_omote.set_hyouji_ichi_y(350.0);

        //camera_of_oriagari_ura	;
        camera_of_oriagari_ura.set_camera_ichi_x(0.0);
        camera_of_oriagari_ura.set_camera_ichi_y(0.0);
        camera_of_oriagari_ura.set_camera_kakudo(0.0);
        camera_of_oriagari_ura.set_camera_kagami(-1.0);
        camera_of_oriagari_ura.set_camera_bairitsu_x(1.0);
        camera_of_oriagari_ura.set_camera_bairitsu_y(1.0);
        camera_of_oriagari_ura.set_hyouji_ichi_x(350.0);
        camera_of_oriagari_ura.set_hyouji_ichi_y(350.0);


        //camera_of_touka_omote	;
        camera_of_touka_omote.set_camera_ichi_x(0.0);
        camera_of_touka_omote.set_camera_ichi_y(0.0);
        camera_of_touka_omote.set_camera_kakudo(0.0);
        camera_of_touka_omote.set_camera_kagami(1.0);
        camera_of_touka_omote.set_camera_bairitsu_x(1.0);
        camera_of_touka_omote.set_camera_bairitsu_y(1.0);
        camera_of_touka_omote.set_hyouji_ichi_x(350.0);
        camera_of_touka_omote.set_hyouji_ichi_y(350.0);

        //camera_of_touka_ura	;
        camera_of_touka_ura.set_camera_ichi_x(0.0);
        camera_of_touka_ura.set_camera_ichi_y(0.0);
        camera_of_touka_ura.set_camera_kakudo(0.0);
        camera_of_touka_ura.set_camera_kagami(-1.0);
        camera_of_touka_ura.set_camera_bairitsu_x(1.0);
        camera_of_touka_ura.set_camera_bairitsu_y(1.0);
        camera_of_touka_ura.set_hyouji_ichi_x(350.0);
        camera_of_touka_ura.set_hyouji_ichi_y(350.0);


    }

    // ------------------------------------------------------------------------------------------
    public void oriagari_oekaki(Graphics bufferGraphics, int i_mejirusi_hyouji) {

        //hyouji_flg==2,ip4==0  omote
        //hyouji_flg==2,ip4==1	ura
        //hyouji_flg==2,ip4==2	omote & ura
        //hyouji_flg==2,ip4==3	omote & ura

        //hyouji_flg==3,ip4==0  omote
        //hyouji_flg==3,ip4==1	ura
        //hyouji_flg==3,ip4==2	omote & ura
        //hyouji_flg==3,ip4==3	omote & ura

        //hyouji_flg==5,ip4==0  omote
        //hyouji_flg==5,ip4==1	ura
        //hyouji_flg==5,ip4==2	omote & ura
        //hyouji_flg==5,ip4==3	omote & ura & omote2 & ura2


        //折り上がり図の表示はjsが行うので表示自体はts2にカメラをセットする必要はないが、その後、画面クリックをts2が判定したりするのでts2のカメラ更新は表示と同期して行う必要がある。
        ts2.setCamera(camera_of_oriagarizu);
        ts2.setCam_omote(camera_of_oriagari_omote);
        ts2.setCam_ura(camera_of_oriagari_ura);
        ts2.setCam_touka_omote(camera_of_touka_omote);
        ts2.setCam_touka_ura(camera_of_touka_ura);


        //針金図の表示
        //System.out.println("paint　+++++++++++++++++++++　針金図の表示");
        if (hyouji_flg == 2) {
            ts2.oekaki_with_camera(bufferGraphics, ip4);//折り上がり図の操作はこのts2の針金図を動かす。
        }

        //折りあがり図（表）の表示
        if (((ip4 == 0) || (ip4 == 2)) || (ip4 == 3)) {
            js.setCamera(camera_of_oriagari_omote);

            //透過図の表示
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            if (hyouji_flg == 3) {        // hyouji_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                js.oekaki_toukazu_with_camera(bufferGraphics, ts1, ts2.get(), ts3.get(), i_toukazu_color, toukazu_toukado);
            }

            //折り上がり図の表示************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (hyouji_flg == 5) {
                js.oekaki_oriagarizu_with_camera(bufferGraphics, ts1, ts2.get(), ts3.get());// hyouji_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //折り上がり図の動かし中心の十字表示
            //System.out.println("paint　+++++++++++++++++++++　折り上がり図の動かし中心の十字表示)");
            if (i_mejirusi_hyouji == 1) {
                js.oekaki_jyuuji_with_camera(bufferGraphics);
            }
        }

        //折りあがり図（裏）の表示
        if (((ip4 == 1) || (ip4 == 2)) || (ip4 == 3)) {
            camera_of_oriagari_ura.hyouji();
            js.setCamera(camera_of_oriagari_ura);

            //透過図の表示
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            if (hyouji_flg == 3) {        // hyouji_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                js.oekaki_toukazu_with_camera(bufferGraphics, ts1, ts2.get(), ts3.get(), i_toukazu_color, toukazu_toukado);
            }

            //折り上がり図の表示************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (hyouji_flg == 5) {
                js.oekaki_oriagarizu_with_camera(bufferGraphics, ts1, ts2.get(), ts3.get());// hyouji_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //折り上がり図の動かし中心の十字表示
            //System.out.println("paint　+++++++++++++++++++++　折り上がり図の動かし中心の十字表示)");
            if (i_mejirusi_hyouji == 1) {
                js.oekaki_jyuuji_with_camera(bufferGraphics);
            }
        }

        //透過図（折りあがり図表示時に追加する分）
        if ((ip4 == 3) && (hyouji_flg == 5)) {
            // ---------------------------------------------------------------------------------
            js.setCamera(camera_of_touka_omote);
            //透過図の表示
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            js.oekaki_toukazu_with_camera(bufferGraphics, ts1, ts2.get(), ts3.get(), i_toukazu_color, toukazu_toukado);

            //折り上がり図の動かし中心の十字表示
            //System.out.println("paint　+++++++++++++++++++++　折り上がり図の動かし中心の十字表示)");
            if (i_mejirusi_hyouji == 1) {
                js.oekaki_jyuuji_with_camera(bufferGraphics);
            }

            // ---------------------------------------------------------------------------------
            js.setCamera(camera_of_touka_ura);

            //透過図の表示
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            js.oekaki_toukazu_with_camera(bufferGraphics, ts1, ts2.get(), ts3.get(), i_toukazu_color, toukazu_toukado);

            //折り上がり図の動かし中心の十字表示
            //System.out.println("paint　+++++++++++++++++++++　折り上がり図の動かし中心の十字表示)");
            if (i_mejirusi_hyouji == 1) {
                js.oekaki_jyuuji_with_camera(bufferGraphics);
            }
            // ---------------------------------------------------------------------------------
        }


        //折り上がり図動かし時の針金図と展開図上の対応点の表示


        for (int i = 1; i <= ts1.getTensuu(); i++) {
            if (ts1.get_ten_sentaku(i) == 1) {
                ts1.oekaki_Ten_id_with_camera(bufferGraphics, i);
                //	ts2.oekaki_Ten_id_with_camera(bufferGraphics,i,ip4);
            }
        }


        for (int i = 1; i <= ts2.getTensuu(); i++) {
            if (ts2.get_ten_sentaku(i) == 1) {
                ts1.oekaki_Ten_id_with_camera_green(bufferGraphics, i);
                ts2.oekaki_Ten_id_with_camera(bufferGraphics, i, ip4);
            }
        }


    }


    // -------------------------------------------------------
    public void set_syukusyaku(double d0) {
        d_oriagarizu_syukusyaku_keisuu = d0;
    }//折り上がり図の縮尺係数

    public double get_syukusyaku() {
        return d_oriagarizu_syukusyaku_keisuu;
    }//折り上がり図の縮尺係数

    public void set_kaiten(double d0) {
        d_oriagarizu_kaiten_hosei = d0;
    }//折り上がり図の回転表示角度の補正角度

    public double get_kaiten() {
        return d_oriagarizu_kaiten_hosei;
    }//折り上がり図の回転表示角度の補正角度

//mmmmmmm

    //---------------------------------------------------------

    //public Memo getMemo_for_svg_kakidasi(){
    //	return js.getMemo_for_svg_kakidasi() ;
    //}

    public Memo getMemo_for_svg_kakidasi() {

        Memo memo_temp = new Memo();

        //針金図のsvg
        //System.out.println("paint　+++++++++++++++++++++　針金図の表示");
        if (hyouji_flg == 2) {
            //ts2.oekaki_with_camera(bufferGraphics,ip4);//折り上がり図の操作はこのts2の針金図を動かす。

//System.out.println("getMemo_hariganezu_for_svg_kakidasi　針金図のsvg");
            memo_temp.addMemo(js.getMemo_hariganezu_for_svg_kakidasi(ts1, ts2.get(), ts3.get(), 0));//４番目の整数は０なら面の枠のみ、１なら面を塗る


        }

        //折りあがり図（表）のsvg
        if (((ip4 == 0) || (ip4 == 2)) || (ip4 == 3)) {
            js.setCamera(camera_of_oriagari_omote);

            //透過図のsvg
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            if (hyouji_flg == 3) {        // hyouji_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(js.getMemo_hariganezu_for_svg_kakidasi(ts1, ts2.get(), ts3.get(), 1));
            }

            //折り上がり図のsvg************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (hyouji_flg == 5) {
                //js.oekaki_oriagarizu_with_camera(bufferGraphics,ts1,ts2.get(),ts3.get());// hyouji_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                memo_temp.addMemo(js.getMemo_for_svg_with_camera(ts1, ts2.get(), ts3.get()));// hyouji_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。

            }
        }

        //折りあがり図（裏）のsvg
        if (((ip4 == 1) || (ip4 == 2)) || (ip4 == 3)) {

            js.setCamera(camera_of_oriagari_ura);

            //透過図のsvg
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            if (hyouji_flg == 3) {        // hyouji_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(js.getMemo_hariganezu_for_svg_kakidasi(ts1, ts2.get(), ts3.get(), 1));
            }

            //折り上がり図のsvg************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (hyouji_flg == 5) {
                //js.oekaki_oriagarizu_with_camera(bufferGraphics,ts1,ts2.get(),ts3.get());// hyouji_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                memo_temp.addMemo(js.getMemo_for_svg_with_camera(ts1, ts2.get(), ts3.get()));// hyouji_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。

            }


        }

        //透過図（折りあがり図表示時に追加する分）
        if ((ip4 == 3) && (hyouji_flg == 5)) {
            // ---------------------------------------------------------------------------------
            js.setCamera(camera_of_touka_omote);
            //透過図のsvg
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");

            //js.oekaki_toukazu_with_camera(bufferGraphics,ts1,ts2.get(),ts3.get());
            //js.getMemo_toukazu_with_camera(bufferGraphics,ts1,ts2.get(),ts3.get());


            // ---------------------------------------------------------------------------------
            js.setCamera(camera_of_touka_ura);

            //透過図のsvg
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");

            //js.oekaki_toukazu_with_camera(bufferGraphics,ts1,ts2.get(),ts3.get());
            //js.getMemo_toukazu_with_camera(bufferGraphics,ts1,ts2.get(),ts3.get());

            // ---------------------------------------------------------------------------------
        }
        return memo_temp;

    }


//-----------------------------------

    void oritatami_suitei_camera_settei(Camera camera_of_orisen_nyuuryokuzu, Senbunsyuugou Ss0) {


        d_oriagarizu_syukusyaku_keisuu = camera_of_orisen_nyuuryokuzu.get_camera_bairitsu_x();
        orihime_app.text29.setText(String.valueOf(d_oriagarizu_syukusyaku_keisuu));
        orihime_app.text29.setCaretPosition(0);

        d_oriagarizu_kaiten_hosei = camera_of_orisen_nyuuryokuzu.get_camera_kakudo();
        orihime_app.text30.setText(String.valueOf(d_oriagarizu_kaiten_hosei));
        orihime_app.text30.setCaretPosition(0);


        System.out.println("ts1.ten_of_kijyunmen_ob     " + ts1.ten_of_kijyunmen_ob.getx());
//Ten p =new Ten();p.set(ts1.ten_of_kijyunmen_ob);
//Ten p0 =new Ten();p0.set(camera_of_orisen_nyuuryokuzu.object2TV(p));

        Ten p0 = new Ten();
        Ten p = new Ten();


        p.set(ts1.ten_of_kijyunmen_ob);
        p0.set(camera_of_orisen_nyuuryokuzu.object2TV(p));


        double d_camera_ichi_x = p.getx();
        double d_camera_ichi_y = p.gety();
        double d_hyouji_ichi_x = p0.getx();
        double d_hyouji_ichi_y = p0.gety();
        //double d_hyouji_ichi_x=camera_of_orisen_nyuuryokuzu.get_hyouji_ichi_x();
        //double d_hyouji_ichi_y=camera_of_orisen_nyuuryokuzu.get_hyouji_ichi_y();


        camera_of_oriagarizu.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_oriagarizu.set_camera_ichi_x(d_camera_ichi_x);
        camera_of_oriagarizu.set_camera_ichi_y(d_camera_ichi_y);
        camera_of_oriagarizu.set_hyouji_ichi_x(d_hyouji_ichi_x + 20.0);
        camera_of_oriagarizu.set_hyouji_ichi_y(d_hyouji_ichi_y + 20.0);

        camera_of_oriagari_omote.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_oriagari_omote.set_camera_ichi_x(d_camera_ichi_x);
        camera_of_oriagari_omote.set_camera_ichi_y(d_camera_ichi_y);
        camera_of_oriagari_omote.set_hyouji_ichi_x(d_hyouji_ichi_x + 20.0);
        camera_of_oriagari_omote.set_hyouji_ichi_y(d_hyouji_ichi_y + 20.0);

        camera_of_oriagari_ura.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_oriagari_ura.set_camera_ichi_x(d_camera_ichi_x);
        camera_of_oriagari_ura.set_camera_ichi_y(d_camera_ichi_y);
        camera_of_oriagari_ura.set_hyouji_ichi_x(d_hyouji_ichi_x + 40.0);
        camera_of_oriagari_ura.set_hyouji_ichi_y(d_hyouji_ichi_y + 20.0);

        camera_of_touka_omote.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_touka_omote.set_camera_ichi_x(d_camera_ichi_x);
        camera_of_touka_omote.set_camera_ichi_y(d_camera_ichi_y);
        camera_of_touka_omote.set_hyouji_ichi_x(d_hyouji_ichi_x + 20.0);
        camera_of_touka_omote.set_hyouji_ichi_y(d_hyouji_ichi_y + 0.0);

        camera_of_touka_ura.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_touka_ura.set_camera_ichi_x(d_camera_ichi_x);
        camera_of_touka_ura.set_camera_ichi_y(d_camera_ichi_y);
        camera_of_touka_ura.set_hyouji_ichi_x(d_hyouji_ichi_x + 40.0);
        camera_of_touka_ura.set_hyouji_ichi_y(d_hyouji_ichi_y + 0.0);

        double d_camera_kagami = camera_of_oriagari_ura.get_camera_kagami();
        camera_of_oriagari_ura.set_camera_kagami(d_camera_kagami * -1.0);
        camera_of_touka_ura.set_camera_kagami(d_camera_kagami * -1.0);


    }

    //-----------------------------------
    public void oritatami_suitei(Camera camera_of_orisen_nyuuryokuzu, Senbunsyuugou Ss0) {//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(Ss0)として使う。　Ss0は、es1.get_for_oritatami()かes1.get_for_select_oritatami()で得る。


        int i_camera_settei = 0;


        //-------------------------------折り上がり図表示用カメラの設定

        if ((i_suitei_dankai == 0) && (i_suitei_meirei <= 5)) {
            i_camera_settei = 1;


        }

        if (i_suitei_meirei == 51) {
            i_suitei_meirei = 5;
        }


        //-------------------------------


        // suitei = estimated
        // dankai = step
        // meirei = order
        if ((i_suitei_dankai == 0) && (i_suitei_meirei == 1)) {
            estimated_initialize(); // estimated_initialize
            oritatami_suitei_01(Ss0);
            i_suitei_dankai = 1;
            hyouji_flg = 1;
        } else if ((i_suitei_dankai == 0) && (i_suitei_meirei == 2)) {
            estimated_initialize();
            oritatami_suitei_01(Ss0);
            i_suitei_dankai = 1;
            hyouji_flg = 1;
            oritatami_suitei_02();
            i_suitei_dankai = 2;
            hyouji_flg = 2;
        } else if ((i_suitei_dankai == 0) && (i_suitei_meirei == 3)) {
            estimated_initialize();
            oritatami_suitei_01(Ss0);
            i_suitei_dankai = 1;
            hyouji_flg = 1;
            oritatami_suitei_02();
            i_suitei_dankai = 2;
            hyouji_flg = 2;
            oritatami_suitei_03();
            i_suitei_dankai = 3;
            hyouji_flg = 3;
        } else if ((i_suitei_dankai == 0) && (i_suitei_meirei == 5)) {
            estimated_initialize();
            oritatami_suitei_01(Ss0);
            i_suitei_dankai = 1;
            hyouji_flg = 1;
            oritatami_suitei_02();
            i_suitei_dankai = 2;
            hyouji_flg = 2;
            oritatami_suitei_03();
            i_suitei_dankai = 3;
            hyouji_flg = 3;
            oritatami_suitei_04();
            i_suitei_dankai = 4;
            hyouji_flg = 4;
            oritatami_suitei_05();
            i_suitei_dankai = 5;
            hyouji_flg = 5;
            if ((hakkenn_sita_kazu == 0) && (betu_sagasi_flg == 0)) {
                i_suitei_dankai = 3;
                hyouji_flg = 3;
            }
        } else if ((i_suitei_dankai == 1) && (i_suitei_meirei == 1)) {
        } else if ((i_suitei_dankai == 1) && (i_suitei_meirei == 2)) {
            oritatami_suitei_02();
            i_suitei_dankai = 2;
            hyouji_flg = 2;
        } else if ((i_suitei_dankai == 1) && (i_suitei_meirei == 3)) {
            oritatami_suitei_02();
            i_suitei_dankai = 2;
            hyouji_flg = 2;
            oritatami_suitei_03();
            i_suitei_dankai = 3;
            hyouji_flg = 3;
        } else if ((i_suitei_dankai == 1) && (i_suitei_meirei == 5)) {
            oritatami_suitei_02();
            i_suitei_dankai = 2;
            hyouji_flg = 2;
            oritatami_suitei_03();
            i_suitei_dankai = 3;
            hyouji_flg = 3;
            oritatami_suitei_04();
            i_suitei_dankai = 4;
            hyouji_flg = 4;
            oritatami_suitei_05();
            i_suitei_dankai = 5;
            hyouji_flg = 5;
            if ((hakkenn_sita_kazu == 0) && (betu_sagasi_flg == 0)) {
                i_suitei_dankai = 3;
                hyouji_flg = 3;
            }
        } else if ((i_suitei_dankai == 2) && (i_suitei_meirei == 1)) {
        } else if ((i_suitei_dankai == 2) && (i_suitei_meirei == 2)) {
        } else if ((i_suitei_dankai == 2) && (i_suitei_meirei == 3)) {
            oritatami_suitei_03();
            i_suitei_dankai = 3;
            hyouji_flg = 3;
        } else if ((i_suitei_dankai == 2) && (i_suitei_meirei == 5)) {
            oritatami_suitei_03();
            i_suitei_dankai = 3;
            hyouji_flg = 3;
            oritatami_suitei_04();
            i_suitei_dankai = 4;
            hyouji_flg = 4;
            oritatami_suitei_05();
            i_suitei_dankai = 5;
            hyouji_flg = 5;
            if ((hakkenn_sita_kazu == 0) && (betu_sagasi_flg == 0)) {
                i_suitei_dankai = 3;
                hyouji_flg = 3;
            }
        } else if ((i_suitei_dankai == 3) && (i_suitei_meirei == 1)) {
        } else if ((i_suitei_dankai == 3) && (i_suitei_meirei == 2)) {
            hyouji_flg = 2;
        } else if ((i_suitei_dankai == 3) && (i_suitei_meirei == 3)) {
            hyouji_flg = 3;
        } else if ((i_suitei_dankai == 3) && (i_suitei_meirei == 5)) {
            oritatami_suitei_04();
            i_suitei_dankai = 4;
            hyouji_flg = 4;
            oritatami_suitei_05();
            i_suitei_dankai = 5;
            hyouji_flg = 5;
            if ((hakkenn_sita_kazu == 0) && (betu_sagasi_flg == 0)) {
                i_suitei_dankai = 3;
                hyouji_flg = 3;
            }
        } else if ((i_suitei_dankai == 5) && (i_suitei_meirei == 1)) {
        } else if ((i_suitei_dankai == 5) && (i_suitei_meirei == 2)) {
            hyouji_flg = 2;
        } else if ((i_suitei_dankai == 5) && (i_suitei_meirei == 3)) {
            hyouji_flg = 3;
        } else if ((i_suitei_dankai == 5) && (i_suitei_meirei == 5)) {
            hyouji_flg = 5;
        } else if ((i_suitei_dankai == 5) && (i_suitei_meirei == 6)) {
            oritatami_suitei_05();
            i_suitei_dankai = 5;
            hyouji_flg = 5;
        }


        if (i_camera_settei == 1) {
            oritatami_suitei_camera_settei(camera_of_orisen_nyuuryokuzu, Ss0);
        }


    }

    //--------------------------------------------------------------------------
    public void oritatami_suitei_2col(Camera camera_of_orisen_nyuuryokuzu, Senbunsyuugou Ss0) {//２色塗りわけ展開図

        //-------------------------------折り上がり図表示用カメラの設定

        //	if( (i_suitei_dankai==0)&&(i_suitei_meirei<=5) ){

        d_oriagarizu_syukusyaku_keisuu = camera_of_orisen_nyuuryokuzu.get_camera_bairitsu_x();
        orihime_app.text29.setText(String.valueOf(d_oriagarizu_syukusyaku_keisuu));
        orihime_app.text29.setCaretPosition(0);

        d_oriagarizu_kaiten_hosei = camera_of_orisen_nyuuryokuzu.get_camera_kakudo();
        orihime_app.text30.setText(String.valueOf(d_oriagarizu_kaiten_hosei));
        orihime_app.text30.setCaretPosition(0);

        double d_hyouji_ichi_x = camera_of_orisen_nyuuryokuzu.get_hyouji_ichi_x();
        double d_hyouji_ichi_y = camera_of_orisen_nyuuryokuzu.get_hyouji_ichi_y();

        camera_of_oriagarizu.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_oriagarizu.set_hyouji_ichi_x(d_hyouji_ichi_x + 20.0);
        camera_of_oriagarizu.set_hyouji_ichi_y(d_hyouji_ichi_y + 20.0);

        camera_of_oriagari_omote.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_oriagari_omote.set_hyouji_ichi_x(d_hyouji_ichi_x + 20.0);
        camera_of_oriagari_omote.set_hyouji_ichi_y(d_hyouji_ichi_y + 20.0);

        camera_of_oriagari_ura.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_oriagari_ura.set_hyouji_ichi_x(d_hyouji_ichi_x + 40.0);
        camera_of_oriagari_ura.set_hyouji_ichi_y(d_hyouji_ichi_y + 20.0);

        camera_of_touka_omote.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_touka_omote.set_hyouji_ichi_x(d_hyouji_ichi_x + 20.0);
        camera_of_touka_omote.set_hyouji_ichi_y(d_hyouji_ichi_y + 0.0);

        camera_of_touka_ura.set_camera(camera_of_orisen_nyuuryokuzu);
        camera_of_touka_ura.set_hyouji_ichi_x(d_hyouji_ichi_x + 40.0);
        camera_of_touka_ura.set_hyouji_ichi_y(d_hyouji_ichi_y + 0.0);

        double d_camera_kagami = camera_of_oriagari_ura.get_camera_kagami();
        camera_of_oriagari_ura.set_camera_kagami(d_camera_kagami * -1.0);
        camera_of_touka_ura.set_camera_kagami(d_camera_kagami * -1.0);
        //	}

        //-------------------------------
        estimated_initialize();
        oritatami_suitei_01(Ss0);
        i_suitei_dankai = 1;
        hyouji_flg = 1;
        oritatami_suitei_02col();
        i_suitei_dankai = 2;
        hyouji_flg = 2;
        oritatami_suitei_03();
        i_suitei_dankai = 3;
        hyouji_flg = 3;
        oritatami_suitei_04();
        i_suitei_dankai = 4;
        hyouji_flg = 4;
        oritatami_suitei_05();
        i_suitei_dankai = 5;
        hyouji_flg = 5;
        i_suitei_dankai = 10;
        //if((OZ.hakkenn_sita_kazu==0)&&(OZ.betu_sagasi_flg==0)){ OZ.i_suitei_dankai=3; OZ.hyouji_flg=3;}

        //return 1000;
    }
//-----------------------------------


    //-------------------------------bbbbbbb----
    public int oritatami_suitei_01(Senbunsyuugou Ss1) {
        System.out.println("＜＜＜＜＜oritatami_suitei_01;開始");
        keijiban.tuiki("<<<<oritatami_suitei_01;  start");
        //マウスの入力でes1の中に作った線分集合をts1に渡し、点集合(展開図に相当)にする
        // Pass the line segment set created in es1 to ts1 by mouse input and make it a point set (corresponding to the development view).
        ts1.Senbunsyuugou2Tensyuugou(Ss1);
        ip3 = ts1.set_kijyunmen_id(ip3);
        ip3 = ts1.set_kijyunmen_id(orihime_app.ten_of_kijyunmen_old);//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加

        return 1000;
    }


    //-----------------------------------
    public int oritatami_suitei_02() {
        System.out.println("＜＜＜＜＜oritatami_suitei_02;開始");
        keijiban.tuiki("<<<<oritatami_suitei_02;  start");
        //ts1が折りたたみを行い、できた針金図をts2に渡す。
        //ts1 folds and passes the resulting wire diagram to ts2.
        //ts2が折りあがった形を少しだけ変形したいような場合に働く。
        //It works when you want to slightly deform the folded shape of ts2.
        ts2.set(ts1.oritatami());
        orihime_app.keijiban.tuiki("<<<<oritatami_suitei_02; end");

        //ts2.Iti_sitei(0.0 , 0.0);点集合の平均位置を全点の重心にする。
        //  if(ip4==1){ ts2.uragaesi();}
        // ts2.set( ts2.oritatami())  ;//折り畳んだ針金図を、折り開きたい場合の操作
        //ここまでで針金図はできていて、ts2が持っている。これは、マウスで操作、変形できる。
        return 1000;
    }

    //-----------------------------------
    public int oritatami_suitei_02col() {//20171225　２色塗りわけをするための特別推定（折り畳み位置を推定しない）
        System.out.println("＜＜＜＜＜oritatami_suitei_02;開始");
        keijiban.tuiki("<<<<oritatami_suitei_02;  start");
        ts2.set(ts1.men_iti_motome());
        orihime_app.keijiban.tuiki("<<<<oritatami_suitei_02; end");
        return 1000;
    }

    //-----------------------------------
    public int oritatami_suitei_03() {
        System.out.println("＜＜＜＜＜oritatami_suitei_03;開始");
        keijiban.tuiki("<<<<oritatami_suitei_03;  start");
        //ts2は折る前の展開図の面を保持した点集合を持っている。
        //折りたたんだ場合の面の上下関係を推定するにはts2の持つ針金図に応じて面を
        //細分した（細分した面をSmenと言うことにする）点集合を使う。
        //このSmen面に分割した点集合はts3が持つようにする。
        //ts2の持つ点集合をts3に渡す前に、ts2の持つ点集合は棒が重なっていたりするかもしれないので、
        //いったんks2に渡して線分集合として整理する。
        // ts2 has a set of points that holds the faces of the unfolded view before folding.
        // To estimate the vertical relationship of the surface when folded, set the surface according to the wire diagram of ts2.
        // Use a set of subdivided points (let's call the subdivided surface Smen).
        // Let ts3 have the set of points divided into this Smen plane.
        // Before passing the point set of ts2 to ts3, the point set of ts2 may have overlapping bars, so
        // Pass it to ks2 and organize it as a set of line segments.
        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____基本枝職人ks2はts2から線分集合（針金図からできたもの）を受け取り、整理する。");
        ks2.set(ts2.getSenbunsyuugou());
        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____基本枝職人ks2がks2.bunkatu_seiri_for_Smen_hassei;実施。");
        ks2.bunkatu_seiri_for_Smen_hassei();//重なった線分や交差する線分折り畳み推定などで得られる針金図の整理
        //展開図職人ts3はks2から点集合（ts2の持つ針金図を整理したもの）を受け取り、Smenに分割する。
        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____展開図職人ts3はks2から整理された線分集合を受け取り、Smenに分割する。");
        System.out.println("　　　oritatami_suitei_03()のts3.Senbunsyuugou2Tensyuugou(ks2.get());実施");
        ts3.Senbunsyuugou2Tensyuugou(ks2.get());

        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____上下表職人jsは、展開図職人ts3から点集合を受け取り、Smenを設定する。");
        js.Smen_settei(ts1, ts2.get(), ts3.get());
        //ここまでで透過図つくろうと思えば作れる。透過図はSmen図に濃度をつけたもの。
        return 1000;
    }

    //-----------------------------------
    public int oritatami_suitei_04() {
        System.out.println("＜＜＜＜＜oritatami_suitei_04;開始");
        keijiban.tuiki("<<<<oritatami_suitei_04;  start");
        //面(折りたたむ前の展開図の面のこと)の上下表を作る。
        //これにはts2の持つ点集合（折りたたんだあとの面の位置関係の情報を持つ）と
        //ts3の持つ点集合（針金図で面を細分割したSmenの情報を持つ）を使う。
        //また、ts1の持つ、折ったときの面の位置関係の情報も使う。
        System.out.println("＜＜＜＜＜oritatami_suitei_04()_____上下表職人jsが面(折りたたむ前の展開図の面のこと)の上下表を作る。");

        ip1 = 0;
        betu_sagasi_flg = 0;
        ip1 = js.Jyougehyou_settei(ts1, ts2.get(), ts3.get());   //ip1=折った後の表裏が同じ面が隣接するという誤りがあれば0を、無ければ1000を格納する変数。
        if (ip1 == 1000) {
            ip1 = 1000;
            betu_sagasi_flg = 1;
        }
        hakkenn_sita_kazu = 0;
        System.out.println("＜＜＜＜＜oritatami_suitei_04()____終了");
        return 1000;
    }


    //-----------------------------------
    public int oritatami_suitei_05() {
        System.out.println("＜＜＜＜＜oritatami_suitei_05()_____上下表職人jsがjs.kanou_kasanari_sagasi()実施。");
        orihime_app.keijiban.tuiki("<<<<oritatami_suitei_05()  ___js.kanou_kasanari_sagasi()  start");

        if ((i_suitei_dankai == 4) || (i_suitei_dankai == 5)) {
            if (betu_sagasi_flg == 1) {

                ip2 = js.kanou_kasanari_sagasi();//ip2=上下表職人が折り畳み可能な重なり方を探した際に、可能な重なり方がなければ0を、可能な重なり方があれば1000を格納する変数。

                if (ip2 == 1000) {
                    hakkenn_sita_kazu = hakkenn_sita_kazu + 1;
                }

                ip5 = js.susumu(js.getSmen_yuukou_suu());//次の重なり探しの準備//ip5=0なら新たにsusumu余地がなかった。0以外なら変化したSmenのidの最も小さい番号
            }
        }
        orihime_app.keijiban.clear();

        text_kekka = "Number of found solutions = " + hakkenn_sita_kazu + "  ";

        betu_sagasi_flg = 0;
        if ((ip2 == 1000) && (ip5 > 0)) {
            betu_sagasi_flg = 1;
        }

        if (betu_sagasi_flg == 0) {
            text_kekka = text_kekka + " There is no other solution. ";
        }

        return 1000;
    }

//int oritatami_suitei_06(){return 1000;}


    public void toukazu_color_sage() {
        toukazu_toukado = toukazu_toukado / 2;
        if (toukazu_toukado < 1) {
            toukazu_toukado = 1;
        }
    }


    public void toukazu_color_age() {
        toukazu_toukado = toukazu_toukado * 2;
        if (toukazu_toukado > 64) {
            toukazu_toukado = 64;
        }
    }    //20180819バグ修正　透過度の最大値がこれまで128で、プログラムで線の描画時に２倍するとく、256となり、透過度の上限255オーバーで、オリヒメ自体が
    //フリーズした。これは、128を127の変えることでもフリーズはなくなるが、透過度は２の倍数にしておかないと、2分の一にしたとき値がずれるかもしれないので、透過度の最大値は64としておくことにする。


    private Ten p_m_left_on = new Ten();//マウスの左ボタンを押したときの座標
    private int i_nanini_tikai = 0;//Ten pが展開図の点に近い=1、折り上がり図の点に近い=2、どちらにも近くない=0
    private int i_mottomo_tikai_Tenid;
    private int i_ten_sentaku = 0;//ts1、ts2ともに未選択（状況　i_ten_sentaku=0）,ts1選択済でts2未選択（状況　i_ten_sentaku=1）,その頂点がts2選択済（状況　i_ten_sentaku=2）
    private Ten ugokasu_maeno_sentaku_ten = new Ten();//動かす前の選択した点の座標


    //-----------------------------------------------------------------------------------------------------uuuuuuu--
    public void oriagari_sousa_mouse_on(Ten p) {//折り上がり図操作でマウスの左ボタンを押したときの作業
        if (i_oriagari_sousa_mode == 1) {
            oriagari_sousa_mouse_on_1(p);
        }
        if (i_oriagari_sousa_mode == 2) {
            oriagari_sousa_mouse_on_2(p);
        }
    }

    public void oriagari_sousa_mouse_drag(Ten p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        if (i_oriagari_sousa_mode == 1) {
            oriagari_sousa_mouse_drag_1(p);
        }
        if (i_oriagari_sousa_mode == 2) {
            oriagari_sousa_mouse_drag_2(p);
        }
    }


    public void oriagari_sousa_mouse_off(Ten p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        if (i_oriagari_sousa_mode == 1) {
            oriagari_sousa_mouse_off_1(p);
        }
        if (i_oriagari_sousa_mode == 2) {
            oriagari_sousa_mouse_off_2(p);
        }
    }


    //  =================================================================================================================================
    //-----------------------------------------------------------------------------------------------------uuuuuuu--
    public void oriagari_sousa_mouse_on_1(Ten p) {//折り上がり図操作でマウスの左ボタンを押したときの作業   折りずらし機能

        p_m_left_on.set(new Ten(p.getx(), p.gety()));

        ts2.setCamera(camera_of_oriagarizu);
        ts2.setCam_omote(camera_of_oriagari_omote);
        ts2.setCam_ura(camera_of_oriagari_ura);

        //i_mottomo_tikai_Tenidにpに最も近い点の番号を格納。近い点がまったくない場合はi_mottomo_tikai_Tenid=0
        i_nanini_tikai = 0;//展開図の点に近い=1、折り上がり図の点に近い=2、どちらにも近くない=0
        i_mottomo_tikai_Tenid = ts1.mottomo_tikai_Tenid_with_camera(p);
        if (i_mottomo_tikai_Tenid != 0) {
            i_nanini_tikai = 1;
        }
        if (ts2.mottomo_tikai_Tenid_with_camera(p, ip4) != 0) {
            if (ts1.mottomo_tikai_Ten_kyori_with_camera(p) > ts2.mottomo_tikai_Ten_kyori_with_camera(p, ip4)) {
                i_mottomo_tikai_Tenid = ts2.mottomo_tikai_Tenid_with_camera(p, ip4);
                i_nanini_tikai = 2;
            }
        }//i_mottomo_tikai_Tenidにpに最も近い点の番号を格納 ここまで

        ugokasu_maeno_sentaku_ten.set(ts2.getTen(i_mottomo_tikai_Tenid));


        System.out.println("i_nanini_tikai = " + i_nanini_tikai);

        if (i_nanini_tikai == 1) {

            //i_ten_sentakuを決める
            i_ten_sentaku = 0;
            if (ts1.get_ten_sentaku(i_mottomo_tikai_Tenid) == 1) {
                i_ten_sentaku = 1;
            }
            if (ts2.get_ten_sentaku(i_mottomo_tikai_Tenid) == 1) {
                i_ten_sentaku = 2;
            }
            //i_ten_sentakuを決める  ここまで


            if (i_ten_sentaku == 0) {
                set_all_ten_sentaku_0();
                //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、ts1でその番号の点を選択済みにする
                Ten ps = new Ten();
                ps.set(ts2.getTen(i_mottomo_tikai_Tenid));
                for (int i = 1; i <= ts2.getTensuu(); i++) {
                    if (ps.kyori(ts2.getTen(i)) < 0.0000001) {
                        ts1.set_ten_sentaku_1(i);
                    }
                }
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 1) {
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 2) {
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            }


        }

        if (i_nanini_tikai == 2) {

            //i_ten_sentakuを決める
            i_ten_sentaku = 0;
            if (ts1.get_ten_sentaku(i_mottomo_tikai_Tenid) == 1) {
                i_ten_sentaku = 1;
                if (ts2.get_ten_sentakusuu() > 0) {
                    i_ten_sentaku = 2;
                }    //折図上で指定した点で、そこに重なるいずれかの点がts2で選択されている。要するに折図上の緑表示されている点を選んだ状態
            }
            //i_ten_sentakuを決める  ここまで
            System.out.println("i_ten_sentaku = " + i_ten_sentaku);

            if (i_ten_sentaku == 0) {
                set_all_ten_sentaku_0();

                //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、ts1でその番号の点を選択済みにする
                Ten ps = new Ten();
                ps.set(ts2.getTen(i_mottomo_tikai_Tenid));
                for (int i = 1; i <= ts2.getTensuu(); i++) {
                    if (ps.kyori(ts2.getTen(i)) < 0.0000001) {
                        ts1.set_ten_sentaku_1(i);
                    }
                }
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 1) {
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 2) {
                //ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            }


            if (i_oriagari_sousa_mode == 1) {

                hyouji_flg_backup = hyouji_flg;   //20180216  //hyouji_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                hyouji_flg = 2;            //20180216
            }


        }


        System.out.println("ts1.get_ten_sentakusuu() = " + ts1.get_ten_sentakusuu());
        System.out.println("ts2.get_ten_sentakusuu() = " + ts2.get_ten_sentakusuu());


    }

    //-------------
    public void oriagari_sousa_mouse_drag_1(Ten p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業

        ts2.setCamera(camera_of_oriagarizu);
        ts2.setCam_omote(camera_of_oriagari_omote);
        ts2.setCam_ura(camera_of_oriagari_ura);

        if (i_nanini_tikai == 1) {
        }

        if (i_nanini_tikai == 2) {
            ts2.mDragged_sentakuten_ugokasi_with_camera(ugokasu_maeno_sentaku_ten, p_m_left_on, p, ip4);


            if (i_oriagari_sousa_mode == 2) {
                oritatami_suitei_03();//20180216
            }
        }

    }

    //-------------
    public void oriagari_sousa_mouse_off_1(Ten p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        ts2.setCamera(camera_of_oriagarizu);
        ts2.setCam_omote(camera_of_oriagari_omote);
        ts2.setCam_ura(camera_of_oriagari_ura);

        if (i_nanini_tikai == 1) {
        }

        if (i_nanini_tikai == 2) {

            hyouji_flg = hyouji_flg_backup;//20180216

            ts2.mReleased_sentakuten_ugokasi_with_camera(ugokasu_maeno_sentaku_ten, p_m_left_on, p, ip4);
            if (p_m_left_on.kyori(p) > 0.0000001) {
                kiroku();
                i_suitei_dankai = 2;

                if (hyouji_flg == 2) {
                }

                if (hyouji_flg == 5) {
                    i_suitei_meirei = 5;
                    orihime_app.oritatami_suitei();
                }//オリジナル 20180124 これ以外だと、表示いったんもどるようでうざい
            }


            ts1.set_all_ten_sentaku_0();
            //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、ts1でその番号の点を選択済みにする
            Ten ps = new Ten();
            ps.set(ts2.getTen(i_mottomo_tikai_Tenid));
            for (int i = 1; i <= ts2.getTensuu(); i++) {
                if (ps.kyori(ts2.getTen(i)) < 0.0000001) {
                    ts1.set_ten_sentaku_1(i);
                }
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
//  =================================================================================================================================
//  ==========折り上がり図のまま変形操作===========================================================================================================
    //-----------------------------------------------------------------------------------------------------uuuuuuu--
    public void oriagari_sousa_mouse_on_2(Ten p) {//折り上がり図操作でマウスの左ボタンを押したときの作業

        p_m_left_on.set(new Ten(p.getx(), p.gety()));

        ts2.setCamera(camera_of_oriagarizu);
        ts2.setCam_omote(camera_of_oriagari_omote);
        ts2.setCam_ura(camera_of_oriagari_ura);

        //i_mottomo_tikai_Tenidにpに最も近い点の番号を格納。近い点がまったくない場合はi_mottomo_tikai_Tenid=0
        i_nanini_tikai = 0;//展開図の点に近い=1、折り上がり図の点に近い=2、どちらにも近くない=0
        i_mottomo_tikai_Tenid = ts1.mottomo_tikai_Tenid_with_camera(p);
        if (i_mottomo_tikai_Tenid != 0) {
            i_nanini_tikai = 1;
        }
        if (ts2.mottomo_tikai_Tenid_with_camera(p, ip4) != 0) {
            if (ts1.mottomo_tikai_Ten_kyori_with_camera(p) > ts2.mottomo_tikai_Ten_kyori_with_camera(p, ip4)) {
                i_mottomo_tikai_Tenid = ts2.mottomo_tikai_Tenid_with_camera(p, ip4);
                i_nanini_tikai = 2;
            }
        }//i_mottomo_tikai_Tenidにpに最も近い点の番号を格納 ここまで

        ugokasu_maeno_sentaku_ten.set(ts2.getTen(i_mottomo_tikai_Tenid));


        System.out.println("i_nanini_tikai = " + i_nanini_tikai);

        if (i_nanini_tikai == 1) {

            //i_ten_sentakuを決める
            i_ten_sentaku = 0;
            if (ts1.get_ten_sentaku(i_mottomo_tikai_Tenid) == 1) {
                i_ten_sentaku = 1;
            }
            if (ts2.get_ten_sentaku(i_mottomo_tikai_Tenid) == 1) {
                i_ten_sentaku = 2;
            }
            //i_ten_sentakuを決める  ここまで


            if (i_ten_sentaku == 0) {
                set_all_ten_sentaku_0();
                //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、ts1でその番号の点を選択済みにする
                Ten ps = new Ten();
                ps.set(ts2.getTen(i_mottomo_tikai_Tenid));
                for (int i = 1; i <= ts2.getTensuu(); i++) {
                    if (ps.kyori(ts2.getTen(i)) < 0.0000001) {
                        ts1.set_ten_sentaku_1(i);
                    }
                }
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 1) {
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 2) {
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            }


        }

        if (i_nanini_tikai == 2) {

            //i_ten_sentakuを決める
            i_ten_sentaku = 0;
            if (ts1.get_ten_sentaku(i_mottomo_tikai_Tenid) == 1) {
                i_ten_sentaku = 1;
                if (ts2.get_ten_sentakusuu() > 0) {
                    i_ten_sentaku = 2;
                }    //折図上で指定した点で、そこに重なるいずれかの点がts2で選択されている。要するに折図上の緑表示されている点を選んだ状態
            }
            //i_ten_sentakuを決める  ここまで
            System.out.println("i_ten_sentaku = " + i_ten_sentaku);

            if (i_ten_sentaku == 0) {
                set_all_ten_sentaku_0();

                //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、ts1でその番号の点を選択済みにする
                Ten ps = new Ten();
                ps.set(ts2.getTen(i_mottomo_tikai_Tenid));
                for (int i = 1; i <= ts2.getTensuu(); i++) {
                    if (ps.kyori(ts2.getTen(i)) < 0.0000001) {
                        ts1.set_ten_sentaku_1(i);
                    }
                }
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 1) {
                ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            } else if (i_ten_sentaku == 2) {
                //ts2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            }


            if (i_oriagari_sousa_mode == 1) {

                hyouji_flg_backup = hyouji_flg;   //20180216  //hyouji_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                hyouji_flg = 2;            //20180216
            }


        }


        System.out.println("ts1.get_ten_sentakusuu() = " + ts1.get_ten_sentakusuu());
        System.out.println("ts2.get_ten_sentakusuu() = " + ts2.get_ten_sentakusuu());


    }

    //-------------
    public void oriagari_sousa_mouse_drag_2(Ten p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業

        ts2.setCamera(camera_of_oriagarizu);
        ts2.setCam_omote(camera_of_oriagari_omote);
        ts2.setCam_ura(camera_of_oriagari_ura);

        if (i_nanini_tikai == 1) {
        }

        if (i_nanini_tikai == 2) {
            ts2.mDragged_sentakuten_ugokasi_with_camera(ugokasu_maeno_sentaku_ten, p_m_left_on, p, ip4);


            if (i_oriagari_sousa_mode == 2) {
                oritatami_suitei_03();//20180216
            }
        }

    }

    //-------------
    public void oriagari_sousa_mouse_off_2(Ten p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        ts2.setCamera(camera_of_oriagarizu);
        ts2.setCam_omote(camera_of_oriagari_omote);
        ts2.setCam_ura(camera_of_oriagari_ura);

        if (i_nanini_tikai == 1) {
        }

        if (i_nanini_tikai == 2) {
            ts2.mReleased_sentakuten_ugokasi_with_camera(ugokasu_maeno_sentaku_ten, p_m_left_on, p, ip4);
            if (p_m_left_on.kyori(p) > 0.0000001) {
                kiroku();
                //if(ts2.get_ten_sentakusuu()!=0){
                i_suitei_dankai = 2;


                if (i_oriagari_sousa_mode == 1) {
                    hyouji_flg = hyouji_flg_backup;//20180216
                }
                if (hyouji_flg == 2) {
                }


//if(i_oriagari_sousa_mode==1){
                //if(hyouji_flg==5){i_suitei_meirei=5;orihime_ap.oritatami_suitei();}//オリジナル 20180124 これ以外だと、表示いったんもどるようでうざい
//}
//if(i_oriagari_sousa_mode==2){
                oritatami_suitei_03();//20180216
//}


            }
            //ts2. set_all_ten_sentaku_0();

            //}
            ts1.set_all_ten_sentaku_0();
            //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、ts1でその番号の点を選択済みにする
            Ten ps = new Ten();
            ps.set(ts2.getTen(i_mottomo_tikai_Tenid));
            for (int i = 1; i <= ts2.getTensuu(); i++) {
                if (ps.kyori(ts2.getTen(i)) < 0.0000001) {
                    ts1.set_ten_sentaku_1(i);
                }
            }


        }
    }


    //-------------------------------------------------------------------------------------------------------
//  =================================================================================================================================


    public void kiroku() {
        ts2.kiroku();
    }

    public void redo() {
        ts2.redo();
        oritatami_suitei_03();
    }


    public void undo() {
        ts2.undo();
        oritatami_suitei_03();
    }


    //--------------------
    public void set_all_ten_sentaku_0() {
        ts1.set_all_ten_sentaku_0();
        ts2.set_all_ten_sentaku_0();
    }


    //--------------------


}


