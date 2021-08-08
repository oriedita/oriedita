package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin;

import jp.gr.java_conf.mt777.origami.orihime.*;
import jp.gr.java_conf.mt777.origami.orihime.tenkaizu_syokunin.*;
import jp.gr.java_conf.mt777.origami.dougu.tensyuugou.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.*;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Random;

import jp.gr.java_conf.mt777.kiroku.memo.*;
import jp.gr.java_conf.mt777.kiroku.moji_sousa.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;
import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken.*;
import jp.gr.java_conf.mt777.zukei2d.oritaoekaki.*;
import jp.gr.java_conf.mt777.seiretu.narabebako.*;


public class Jyougehyou_Syokunin {//上下表　折りたたむ前の展開図の面が折りたたんだあとでどのような上下関係になるかを記録し活用する
    //String c=new String();
    //int Mensuu;             //折りたたむ前の展開図の面の数
    Jyougehyou jg = new Jyougehyou();
    //Jyougehyou jg;// =new Jyougehyou();
    //Jyougehyou jg_syokiti =new Jyougehyou();//展開図のみから得られる上下関係を記録しておく。
    int Smensuu;//Smenの数
    int Smen_yuukou_suu;//Smenは全て調べなくても、Menの上下関係は網羅できる。Menの上下関係を網羅するのに必要なSmenの数が優先順位の何番目までかをさがす。
    int Menidsuu_max;//各Smenの持つMenidsuuの最大値。すなわち、最も紙に重なりが多いところの枚数。
    //paint 用のint格納用VVVVVVVVVVVVVVVVVVVVVV
    int ip1 = 0; //0は折り畳み図の表側を表示するモード。1は折り畳み図の裏側を表示するモード。
    // int ip1,ip2,ip3,ip4,ip5;
    int hyouji_yousiki = 0;//表示様式の指定
    //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    //  jg[][]は折る前の展開図のすべての面同士の上下関係を1つの表にまとめたものとして扱う
    //　jg[i][j]が1なら面iは面jの上側。0なら下側。
    //  jg[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
    //jg[i][j]が-100なら、面iとjは重なるところがない。

    Smen[] s0;//Smen_zuから得られるSmen
    Smen[] s;//sは優先度の高い順にs0を並べ替えたもの。
    int[] s0_no_yusenjyun;
    int[] yusenjyun_kara_s0id;

    int i_kage = 0; //影を表示するかどうか。0は表示しない、1は表示する

    Camera camera = new Camera();

    App orihime_app;
    Moji_sousa ms = new Moji_sousa();


    public double[] men_rating;
    public int[] i_men_rating;

    public Narabebako_int_double nbox = new Narabebako_int_double();//20180227　nboxにはmenのidがmen_ratingと組になって、men_ratingの小さい順に並べ替えられて入っている。


    //-----------------------------------------------------------------
    public Jyougehyou_Syokunin() {
        reset();
    }    //コンストラクタ

    //-----------------------------------------------------------------
    public Jyougehyou_Syokunin(App app0) {
        orihime_app = app0;
        reset();
    }    //コンストラクタ

    //-----------------------------------------------------------------
    public void reset() {
        jg.reset();//jg_syokiti.reset();
        Smensuu = 0;
        Smen_yuukou_suu = 0;
        ip1 = 0;
        Menidsuu_max = 0;
        camera.reset();
    }


    //--------
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
    }


    public int getSmensuu() {
        return Smensuu;
    }

    public int getSmen_yuukou_suu() {
        return Smen_yuukou_suu;
    }

    //　ここは  class Jyougehyou_Syokunin  の中です。
    //上下表の初期設定。展開図に1頂点から奇数の折線がでる誤りがある場合0を返す。それが無ければ1000を返す。
    //展開図に山谷折線の拡張による誤りがある場合2を返す。

    //----------------------------------------------------------------------
/*
	public int Jyougehyou_settei  (Tenkaizu_Syokunin orite,Tensyuugou otta_Men_zu,Tensyuugou Smen_zu){//js.Jyougehyou_settei(ts1,ts2.get(),ts3.get()){
         	Smen_settei           (                  orite,           otta_Men_zu,           Smen_zu);
         return Jyougehyou_settei_2   (                  orite,           otta_Men_zu,           Smen_zu);
       }
*/
    public void Smen_settei(Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu) {//js.Jyougehyou_settei(ts1,ts2.get(),ts3.get());
        //面(折りたたむ前の展開図の面のこと)の上下表を作る。
        //これにはts2の持つ点集合（折りたたんだあとの面の位置関係の情報を持つ）と  <-------------otta_Men_zu
        //ts3の持つ点集合（針金図で面を細分割したSmenの情報を持つ）を使う。 <-------------Smen_zu
        //また、ts1の持つ、折ったときの面の位置関係の情報も使う。


        System.out.println("Smenの初期設定");
        reset();
        Smensuu = Smen_zu.getMensuu();
        //4915                      System.out.print("Smensuu = "); System.out.println(Smensuu);System.exit(0);

        Smen[] s0_ori = new Smen[Smensuu + 1];
        Smen[] s_ori = new Smen[Smensuu + 1];
        s0 = s0_ori;
        s = s_ori;
        int[] s0yj = new int[Smensuu + 1];
        int[] yjs0 = new int[Smensuu + 1];
        s0_no_yusenjyun = s0yj;
        yusenjyun_kara_s0id = yjs0;

        //	Smensuu=Smen_zu.getMensuu();
        //int ireturn=1000;

        // Jyougehyou j_g =new Jyougehyou();
        // jg=j_g;
        //jg.setMensuu(otta_Men_zu.getMensuu());

        //4926                     System.out.print("Smensuu = "); System.out.println(Smensuu);System.exit(0);

        //
        for (int i = 0; i < Smensuu + 1; i++) {
            s0[i] = new Smen(orihime_app);
            s[i] = s0[i];
            s0_no_yusenjyun[i] = 0;
            yusenjyun_kara_s0id[i] = i;
        }
        //

        //各Smenに含まれる面を記録する。
        System.out.println("各Smenに含まれる面を記録するため、各Smenの内部点を登録");
        Ten[] Smen_naibuTen = new Ten[Smensuu + 1];  //<<<<<<<<<<<<<<<<<<<<<<<<<<<オブジェクトの配列を動的に指定
        for (int i = 1; i <= Smensuu; i++) {
            Smen_naibuTen[i] = Smen_zu.naibuTen_motome(i);
        }

        System.out.println("各Smenに含まれる面を記録する");
        otta_Men_zu.BouMenMaxMinZahyou();//tttttttttt

        int[] s0addMenid = new int[otta_Men_zu.getMensuu() + 1];  //Smenに追加する面を一時記録しておく

/*
		for(int i=1;i<=Smensuu;i++){
		       int s0addMenidsuu=0;

			for(int j=1;j<=jg.getMensuu();j++){
				//if(otta_Men_zu.naibu(Smen_zu.naibuTen_motome(i),j)==2){
				if(otta_Men_zu.kantan_naibu(Smen_naibuTen[i],j)==2){
					s0[i].addMenid(j)	;//ここで面番号jは小さい方が先に追加される。
				}
			}
                        s0[i].setKetasuu();
		}
*/

        for (int i = 1; i <= Smensuu; i++) {
            int s0addMenidsuu = 0;
            //System.out.println("現在処理中のSmenは、"+i+" / "+Smensuu );
            //for(int j=1;j<=jg.getMensuu();j++){

            for (int j = 1; j <= otta_Men_zu.getMensuu(); j++) {

                //System.out.print("現在処理中のSmenは、"+i+" / "+Smensuu +"     :::     "   +j+"/"+jg.getMensuu()+"-");
                if (otta_Men_zu.kantan_naibu(Smen_naibuTen[i], j) == 2) {
                    //System.out.println(j);
                    s0addMenidsuu = s0addMenidsuu + 1;
                    s0addMenid[s0addMenidsuu] = j;
                }

            }
            //if (s0addMenidsuu==0){
            //System.out.println("**1** 現在処理中のSmenは、"+i+" / "+Smensuu+"  :::  s0addMenidsuu= "+s0addMenidsuu+" ::: 面積= "+Smen_zu.menseki_motome(i));
            //}/////////20150308

            s0[i].setKetasuu(s0addMenidsuu);

            //System.out.println("**2**");
            for (int j = 1; j <= s0addMenidsuu; j++) {
                s0[i].setMenid(j, s0addMenid[j]);//ここで面番号jは小さい方が先に追加される。
            }
            //System.out.println("**3**");
        }

        //4956                     System.out.print("Smensuu = "); System.out.println(Smensuu);System.exit(0);
        //ここまでで、Smensuu＝	Smen_zu.getMensuu()のままかわりなし。
        System.out.println("各Smenに含まれる面の数の内で最大のものを求める");
        //各Smenに含まれる面の数の内で最大のものを求める。
        Menidsuu_max = 0;
        for (int i = 1; i <= Smensuu; i++) {
            if (s0[i].getMenidsuu() > Menidsuu_max) {
                Menidsuu_max = s0[i].getMenidsuu();
            }
        }
    }

// --------------------------------------------------------------------------------------------------------------------------
/*
	public int getMenidsuu_max(){
		int M_id_max;M_id_max=0;
		for(int i=1;i<=Smen_zu.getMensuu();i++){
			if(s0[i].getMenidsuu()>M_id_max){M_id_max = s0[i].getMenidsuu();}
		}
		return M_id_max;
	}
*/

    //------------------------------------------------------
    public int Jyougehyou_settei(Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu) {//js.Jyougehyou_settei(ts1,ts2.get(),ts3.get());
        orihime_app.keijiban.tuiki("           Jyougehyou_settei   step1   start ");
        int ireturn = 1000;
        jg.setMensuu(otta_Men_zu.getMensuu());

        //山折り谷折りの情報から決定される上下関係を上下表に入れる
        System.out.println("山折り谷折りの情報から決定される上下関係を上下表に入れる");
        int Mid_min, Mid_max;
        for (int ib = 1; ib <= orite.getBousuu(); ib++) {
            Mid_min = orite.Bou_moti_Menid_min_motome(ib);
            Mid_max = orite.Bou_moti_Menid_max_motome(ib);
            if (Mid_min != Mid_max) {//展開図において、棒ibの両脇に面がある
                //if(orite.getcolor(ib)==1){//赤い線で山折りを意味する
                if (otta_Men_zu.getcolor(ib) == 1) {//赤い線で山折りを意味する
                    if (orite.getiMeniti(Mid_min) % 2 == 1) {//面Mid_minは基準面と同じ向き(表面が上を向く)
                        jg.set(Mid_min, Mid_max, 1);
                        jg.set(Mid_max, Mid_min, 0);
                    }
                    if (orite.getiMeniti(Mid_max) % 2 == 1) {//面Mid_maxは基準面と同じ向き(表面が上を向く)
                        jg.set(Mid_max, Mid_min, 1);
                        jg.set(Mid_min, Mid_max, 0);
                    }
                }
                //if(orite.getcolor(ib)==2){//青い線で谷折りを意味する
                if (otta_Men_zu.getcolor(ib) == 2) {//青い線で谷折りを意味する
                    if (orite.getiMeniti(Mid_min) % 2 == 1) {//面Mid_minは基準面と同じ向き(表面が上を向く)
                        jg.set(Mid_min, Mid_max, 0);
                        jg.set(Mid_max, Mid_min, 1);
                    }
                    if (orite.getiMeniti(Mid_max) % 2 == 1) {//面Mid_maxは基準面と同じ向き(表面が上を向く)
                        jg.set(Mid_max, Mid_min, 0);
                        jg.set(Mid_min, Mid_max, 1);
                    }
                }

                if ((orite.getiMeniti(Mid_min) % 2 == 0) && (orite.getiMeniti(Mid_max) % 2 == 0)) {
                    ireturn = 0;
                }
                if ((orite.getiMeniti(Mid_min) % 2 == 1) && (orite.getiMeniti(Mid_max) % 2 == 1)) {
                    ireturn = 0;
                }
            }
        }

        //----------------------------------------------
        orihime_app.keijiban.tuiki("           Jyougehyou_settei   step2   start ");
        System.out.println("等価条件を設定する   ");
        //等価条件を設定する。棒ibを境界として隣接する2つの面im1,im2が有る場合、折り畳み推定した場合に
        //棒ibの一部と重なる位置に有る面imは面im1と面im2に上下方向で挟まれることはない。このことから
        //gj[im1][im]=gj[im2][im]という等価条件が成り立つ。
        for (int ib = 1; ib <= orite.getBousuu(); ib++) {
            Mid_min = orite.Bou_moti_Menid_min_motome(ib);
            Mid_max = orite.Bou_moti_Menid_max_motome(ib);
            if (Mid_min != Mid_max) {//展開図において、棒ibの両脇に面がある
                for (int im = 1; im <= jg.getMensuu(); im++) {
                    if ((im != Mid_min) && (im != Mid_max)) {
                        if (otta_Men_zu.kantan_totu_naibu(ib, im) == 1) {
                            //下の２つのifは暫定的な処理。あとで置き換え予定
                            if (otta_Men_zu.totu_naibu(0.5, ib, im) == 1) {
                                if (otta_Men_zu.totu_naibu(-0.5, ib, im) == 1) {
                                    jg.addTouka_jyouken(im, Mid_min, im, Mid_max);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.print("３面が関与する突き抜け条件の数　＝　");
        System.out.println(jg.getTouka_jyoukensuu());
        orihime_app.keijiban.tuiki("           Jyougehyou_settei   step3   start ");
        //等価条件の追加。棒ibの境界として隣接する2つの面im1,im2が有り、
        //また棒jbの境界として隣接する2つの面im3,im4が有り、ibとjbが平行で、一部重なる場合、折り畳み推定した場合に
        //棒ibの面と面jbの面がi,j,i,j　または　j,i,j,i　と並ぶことはない。もしこれがおきたら、
        //最初から3番目で間違いが起きているので、この3番目のところがSmenで何桁目かを求めて、この桁を１進める。
        int mi1, mi2, mj1, mj2;

        for (int ib = 1; ib <= orite.getBousuu() - 1; ib++) {
            for (int jb = ib + 1; jb <= orite.getBousuu(); jb++) {
                if (otta_Men_zu.heikou_kasanai(ib, jb) == 1) {
                    mi1 = orite.Bou_moti_Menid_min_motome(ib);
                    mi2 = orite.Bou_moti_Menid_max_motome(ib);
                    if (mi1 != mi2) {
                        mj1 = orite.Bou_moti_Menid_min_motome(jb);
                        mj2 = orite.Bou_moti_Menid_max_motome(jb);
                        if (mj1 != mj2) {
                            if (mi1 * mi2 * mj1 * mj2 != 0) {
                                if (onaji_Smen_ni_sonzai(mi1, mi2, mj1, mj2) == 1) {
                                    jg.add_uTouka_jyouken(mi1, mi2, mj1, mj2);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.print("４面が関与する突き抜け条件の数　＝　");
        System.out.println(jg.get_uTouka_jyoukensuu());
        //5154                      System.out.print("Smensuu = "); System.out.println(Smensuu);System.exit(0);

        orihime_app.keijiban.tuiki("           Jyougehyou_settei   step4   start ");
        //追加推定
        int ituika;

        ituika = tuika_suitei();
        if (ituika != 1000) {
            return ituika;
        }

        System.out.println("追加推定 終了し、上下表を保存------------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        //*************最初に行う演繹的推論の結果の保存**************************
        jg.jg_hozon();//山折り谷折りの情報から決定される上下関係を保存しておく。
        //************************************************************************
        orihime_app.keijiban.tuiki("           Jyougehyou_settei   step5   start ");
//orihime_ap.keijiban.tuiki("00000           ");
        //Smen毎に案内書を作る
        System.out.println("Smen毎に案内書を作る");
        for (int i = 1; i <= Smensuu; i++) {
            //System.out.print("Smen");System.out.print(i);System.out.println("にて。");
            s0[i].setAnnaisyo(jg);
        }

        //s0に優先順位をつける(このときjgの-100のところが変るところがある)
        System.out.println("Smen(s0)に優先順位をつける");
        //まず、他のSmenに丸ごと含まれているSmenを除外する

        int[] Smen_no_dokujisei = new int[Smensuu + 1];  //<<<<<<<<<<<<<<<Smenの独自性
        for (int i = 1; i <= Smensuu; i++) {
            Smen_no_dokujisei[i] = 1;
        }
        for (int i = 1; i <= Smensuu; i++) {
            Smen_no_dokujisei[i] = 1;
            for (int j = 1; j <= Smensuu; j++) {
                if (Smen_no_dokujisei[j] == 1) {

                    if (i != j) {//s0[j]がs0[i]を含むかをみる。
                        if (Smen_i_ga_j_ni_fukumareru(i, j) == 1) {
                            Smen_no_dokujisei[i] = 0;
                            break;
                        }
                    }
                }
            }
        }


/*
		for(int i=1;i<=Smensuu;i++){
			System.out.print("Smen[s0]の独立性　" );
			System.out.print(Smen_no_dokujisei[i]);
			System.out.print("　:　");
            		for(int j=1;j<=s0[i].getMenidsuu();j++){
				System.out.print(s0[i].getMenid(j));System.out.print(",");
			}
			System.out.println("");
		}
*/
//
        int[] i_yusendo_max = new int[Smensuu + 1];     //<<<<<<<<<<<<<<<臨時
        //5115                     System.out.print("Smensuu = "); System.out.println(Smensuu);System.exit(0);

        for (int i = 1; i <= Smensuu; i++) {//優先度i番目のSmenidをさがす。
            int yusendo_max = -10000;//優先度i番目の優先度の値（大きいほうが優先度が高い）。
            int i_yusen = 0;

            for (int is0 = 1; is0 <= Smensuu; is0++) { //Smenを１からSmensuu番目までサーチ
                int Sy;//Smenid_yusendo(is0)+Smen_no_dokujisei[is0] を格納
                if (s0_no_yusenjyun[is0] == 0) {//まだ優先順位がついていないSmenだけを扱う
                    Sy = Smenid_yusendo(is0)/*+Smen_no_dokujisei[is0]*/;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    if (yusendo_max < Sy) {
                        yusendo_max = Sy;
                        i_yusen = is0;// i_yusenがi番目の優先度を探している際の最も有力な候補の番号
                    }
                    if (yusendo_max == Sy) {
                        if (s0[i_yusen].getMenidsuu() < s0[is0].getMenidsuu()) {
                            yusendo_max = Sy;
                            i_yusen = is0;
                        }
                    }
                }
            }

            s0_no_yusenjyun[i_yusen] = i; //優先度i番目のSmenidはi_yusen。
            i_yusendo_max[i_yusen] = yusendo_max;//優先度i番目の優先度の値（大きいほうが優先度が高い）。
/*
			if(yusendo_max>0){
				System.out.print("優先度決め　");System.out.print(i);System.out.print("番目の優先度 ");
				System.out.println(yusendo_max);
			}
*/

            s0[i_yusen].jg_ni_Smen_no_tantoubasyo_wo_nyuuryoku(jg); //jgの-100のところを変る。<<<<<<<<<<<<<<<<<<<<<<
        }

        //優先度からs0のidを指定できるようにする

        for (int i = 1; i <= Smensuu; i++) {
            for (int is0 = 1; is0 <= Smensuu; is0++) {
                if (i == s0_no_yusenjyun[is0]) {
                    yusenjyun_kara_s0id[i] = is0;
                }
            }
        }


        //System.out.println("------------" );
        System.out.println("上下表職人内　Smensuu = " + Smensuu);
        System.out.println("上下表職人内　s0に優先順位をつける");
        for (int i = 1; i <= Smensuu; i++) {
            System.out.println(s0_no_yusenjyun[i]);
        }
        System.out.println("上下表職人内　優先度からs0のid");
        for (int i = 1; i <= Smensuu; i++) {
            System.out.println(yusenjyun_kara_s0id[i]);
        }


        for (int i = 1; i <= Smensuu; i++) {
            //System.out.println(yusenjyun_kara_s0id[i]);
            if (i_yusendo_max[yusenjyun_kara_s0id[i]] != 0) {
                Smen_yuukou_suu = i;       //早いが変な結果になることあり。
//20191012 wwwww				Smen_yuukou_suu=Smensuu;//遅いが確実

                //System.out.print("第");
                //System.out.print(i);
                //System.out.print("位(未定ペア数＝");
                //System.out.print(i_yusendo_max[yusenjyun_kara_s0id[i]]);

                //System.out.print("　||　");
            }
        }


//20191012 				Smen_yuukou_suu=Smensuu;//遅いが確実//20191012


        //System.out.println();
        //

        for (int i = 1; i <= Smensuu; i++) {
            s[i] = s0[yusenjyun_kara_s0id[i]];
        }

        //優先順位を逆転させる。これが有効かどうかは不明wwwww
        //for(int i=1;i<=Smen_yuukou_suu;i++){s[Smen_yuukou_suu-i+1]=s0[yusenjyun_kara_s0id[i]];}

/*
		for(int i=1;i<=Smensuu;i++){
                       if(i_yusendo_max[yusenjyun_kara_s0id[i]]!=0){
				System.out.print("第");
				System.out.print(i);
				System.out.print("位(未定ペア数＝");
				System.out.print(i_yusendo_max[yusenjyun_kara_s0id[i]]);
				System.out.print("、面数＝");
				System.out.print(s[i].getMenidsuu());
				System.out.print("　||　");
			}
		}

		System.out.println();
*/

        //Smenは優先順の何番目までやるかを決める
/*
		for(int i=1;i<=Smensuu;i++){
	  		if(s[i].getMenidsuu()>=1){
				Smen_yuukou_suu=i; ;
			}
		}
		Smen_yuukou_suu=Smensuu;   //<<<<<<<<<<<<<<<<<<<<<<<
*/
        System.out.print("Smen有効数は　");
        System.out.print(Smen_yuukou_suu);
        System.out.print("／");
        System.out.println(Smensuu);


        //jg.jg_hozon();//山折り谷折りの情報から決定される上下関係を保存しておく。


        //jg[][]の重なりのある面の組み合わせの位置の値を-100から-50に変える。
        for (int k = 1; k <= Smensuu; k++) {
            for (int i = 1; i <= s[k].getMenidsuu() - 1; i++) {
                for (int j = i + 1; j <= s[k].getMenidsuu(); j++) {
                    jg.set(i, j, -50);
                    jg.set(j, i, -50);
                }
            }
        }


        System.out.println("上下表初期設定終了");


//		jg.jg_hozon();//山折り谷折りの情報から決定される上下関係を保存しておく。

        //System.out.print("面数は1　");  System.out.println(s[1].getMenidsuu());
        return ireturn;
    }

    //------------------------------------------------------------
    public int tuika_suitei() {
        //山折り谷折りの情報から更に決定できる関係を推測していく。

        int Mid;//3面の比較で中間にくる面
        int flg_c = 1;
        System.out.println("追加推定開始---------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        while (flg_c >= 1) {
            flg_c = 0;
            System.out.println("追加推定------------------------");
            //System.out.println("山折り谷折りの情報から追加推定   " );

            int flg_b = 1;
            while (flg_b >= 1) {
                flg_b = 0;

                for (int iS = 1; iS <= Smensuu; iS++) {

                    int flg_a = 1;
                    while (flg_a >= 1) {
                        flg_a = 0;
                        for (int iM = 1; iM <= s0[iS].getMenidsuu(); iM++) {//3面の比較で中間にくる面
                            int[] ueMenid = new int[s0[iS].getMenidsuu() + 1];//S面に含まれるあるMenの上がわにあるid番号を記録する。これが20ということは、
                            int[] sitaMenid = new int[s0[iS].getMenidsuu() + 1];//S面に含まれるあるMenの下がわにあるid番号を記録する。これが20ということは、
                            int ueMenid_max = 0;
                            int sitaMenid_max = 0;
                            Mid = s0[iS].getMenid(iM);

                            //考え方：あるSmenのある面Midについて考える。
                            //このSmen以外で面Aは面Midの上側、面Bは面Midの下側と決まっていたとする。
                            //一般に別々のSmenで、面Aは面Midの上側で、面Bは面Midの下側だったからと言って、面Aは面Bの上側とは決定できない。
                            //しかし、ここがポイントだが、面A、面Mid、面Bがいっしょに含まれているSmenがあるなら、そのSmenの上下関係がわかっていなくても
                            //面Aは面Bの上側となる。だから、この操作ではあるSmenから得る情報は３つの面がいっしょにあるかということである。
                            //あるSmen内の上下関係は必要ない。
                            //
                            //ここの操作はあるSmenの上下関係を上下表から採集している。
                            for (int i = 1; i <= s0[iS].getMenidsuu(); i++) {//Menid[iM]より上にある面。
                                if (iM != i) {
                                    if (jg.get(Mid, s0[iS].getMenid(i)) == 0) {
                                        ueMenid_max = ueMenid_max + 1;
                                        ueMenid[ueMenid_max] = s0[iS].getMenid(i);
                                    }
                                    if (jg.get(Mid, s0[iS].getMenid(i)) == 1) {
                                        sitaMenid_max = sitaMenid_max + 1;
                                        sitaMenid[sitaMenid_max] = s0[iS].getMenid(i);
                                    }
                                }
                            }

                            //  System.out.print("VVVVVVVVVVVueMenid_max:sitaMenid_max = ");System.out.print(ueMenid_max);
                            //  System.out.print(":");System.out.println(sitaMenid_max);
                            //
                            for (int iuM = 1; iuM <= ueMenid_max; iuM++) {//Menid[iM]より上にある面。
                                for (int isM = 1; isM <= sitaMenid_max; isM++) {//Menid[iM]より下にある面。

                                    //  System.out.print(ueMenid[iuM]);System.out.print("<-上:下->");System.out.println(sitaMenid[isM]);


                                    if (jg.get(ueMenid[iuM], sitaMenid[isM]) == 0) {
                                        return 2;
                                    }//面の上下関係の拡張で矛盾発生。
                                    if (jg.get(sitaMenid[isM], ueMenid[iuM]) == 1) {
                                        return 2;
                                    }//面の上下関係の拡張で矛盾発生。

                                    if (jg.get(ueMenid[iuM], sitaMenid[isM]) < 0) {
                                        jg.set(ueMenid[iuM], sitaMenid[isM], 1);
                                        flg_a = flg_a + 1;
                                        flg_b = flg_b + 1;
                                        flg_c = flg_c + 1;
                                    }
                                    if (jg.get(sitaMenid[isM], ueMenid[iuM]) < 0) {
                                        jg.set(sitaMenid[isM], ueMenid[iuM], 0);
                                        flg_a = flg_a + 1;
                                        flg_b = flg_b + 1;
                                        flg_c = flg_c + 1;
                                    }
                                    //   System.out.print("AAAAAAAAAAAflg_a:");System.out.println(flg_a);
                                }
                            }
                        }
                    }


                }

                //System.out.print("推測された関係の数 ＝ ");System.out.println(flg_b);
            }

            //
            //jg.jg_hozon();//山折り谷折りの情報から決定される上下関係を保存しておく。


            //jg のreset適切に行われているか確認のこと

            //System.out.println ("３面が関与する突き抜け条件から追加推定   " );
            //jg.addTouka_jyouken(im,Mid_min,im,Mid_max)qqqqqqqqqqqq
            //(im,Mid_min,im,Mid_max);
            Touka_jyouken tg = new Touka_jyouken();

            int flg_a = 1;
            while (flg_a >= 1) {
                flg_a = 0;
                for (int i = 1; i <= jg.getTouka_jyoukensuu(); i++) {
                    tg = jg.getTouka_jyouken(i);
                    //if(onaji_Smen_ni_sonzai(tg.geta(),tg.getb(),tg.getd())==1) {
                    if (jg.get(tg.geta(), tg.getb()) == 1) {
                        if (jg.get(tg.geta(), tg.getd()) == 0) {
                            return 3;
                        }
                        if (jg.get(tg.getd(), tg.geta()) == 1) {
                            return 3;
                        }
                        if (jg.get(tg.geta(), tg.getd()) < 0) {
                            jg.set(tg.geta(), tg.getd(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(tg.getd(), tg.geta()) < 0) {
                            jg.set(tg.getd(), tg.geta(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    if (jg.get(tg.geta(), tg.getb()) == 0) {
                        if (jg.get(tg.geta(), tg.getd()) == 1) {
                            return 3;
                        }
                        if (jg.get(tg.getd(), tg.geta()) == 0) {
                            return 3;
                        }
                        if (jg.get(tg.geta(), tg.getd()) < 0) {
                            jg.set(tg.geta(), tg.getd(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(tg.getd(), tg.geta()) < 0) {
                            jg.set(tg.getd(), tg.geta(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //
                    if (jg.get(tg.geta(), tg.getd()) == 1) {
                        if (jg.get(tg.geta(), tg.getb()) == 0) {
                            return 3;
                        }
                        if (jg.get(tg.getb(), tg.geta()) == 1) {
                            return 3;
                        }
                        if (jg.get(tg.geta(), tg.getb()) < 0) {
                            jg.set(tg.geta(), tg.getb(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(tg.getb(), tg.geta()) < 0) {
                            jg.set(tg.getb(), tg.geta(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    if (jg.get(tg.geta(), tg.getd()) == 0) {
                        if (jg.get(tg.geta(), tg.getb()) == 1) {
                            return 3;
                        }
                        if (jg.get(tg.getb(), tg.geta()) == 0) {
                            return 3;
                        }
                        if (jg.get(tg.geta(), tg.getb()) < 0) {
                            jg.set(tg.geta(), tg.getb(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(tg.getb(), tg.geta()) < 0) {
                            jg.set(tg.getb(), tg.geta(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    // }
                }
                //System.out.print("推測された関係の数は ＝ ");System.out.println(flg_a);
            }

            //----------------

//	System.out.println ("４面が関与する突き抜け条件から追加推定   " );
            //jg.addTouka_jyouken(im,Mid_min,im,Mid_max)qqqqqqqqqqqq
            //(im,Mid_min,im,Mid_max);
            //Touka_jyouken tg = new Touka_jyouken();

            flg_a = 1;
            while (flg_a >= 1) {
                flg_a = 0;
                for (int i = 1; i <= jg.get_uTouka_jyoukensuu(); i++) {
                    tg = jg.get_uTouka_jyouken(i);
                    int a, b, c, d;
                    a = tg.geta();
                    b = tg.getb();
                    c = tg.getc();
                    d = tg.getd();


                    //　a>b>c　だけならdの位置は決まらない


                    //　a>c && b>d なら a>d && b>c
                    //  a>d && b>c なら a>c && b>d
                    //　a<c && b<d なら a<d && b<c
                    //  a<d && b<c なら a<c && b<d


                    //　a>c>b　なら　a>d>b

                    //a>c && b>d なら a>d && b>c
                    if ((jg.get(a, c) == 1) && (jg.get(b, d) == 1)) {
                        if (jg.get(a, d) == 0) {
                            return 4;
                        }
                        if (jg.get(b, c) == 0) {
                            return 4;
                        }
                        if (jg.get(a, d) < 0) {
                            jg.set(a, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, c) < 0) {
                            jg.set(b, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, a) < 0) {
                            jg.set(d, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, b) < 0) {
                            jg.set(c, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //a>d && b>c なら a>c && b>d
                    if ((jg.get(a, d) == 1) && (jg.get(b, c) == 1)) {
                        if (jg.get(a, c) == 0) {
                            return 4;
                        }
                        if (jg.get(b, d) == 0) {
                            return 4;
                        }
                        if (jg.get(a, c) < 0) {
                            jg.set(a, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, d) < 0) {
                            jg.set(b, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, a) < 0) {
                            jg.set(c, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, b) < 0) {
                            jg.set(d, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }

                    //a<c && b<d なら a<d && b<c
                    if ((jg.get(a, c) == 0) && (jg.get(b, d) == 0)) {
                        if (jg.get(a, d) == 1) {
                            return 4;
                        }
                        if (jg.get(b, c) == 1) {
                            return 4;
                        }
                        if (jg.get(a, d) < 0) {
                            jg.set(a, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, c) < 0) {
                            jg.set(b, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, a) < 0) {
                            jg.set(d, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, b) < 0) {
                            jg.set(c, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //a<d && b<c なら a<c && b<d
                    if ((jg.get(a, d) == 0) && (jg.get(b, c) == 0)) {
                        if (jg.get(a, c) == 1) {
                            return 4;
                        }
                        if (jg.get(b, d) == 1) {
                            return 4;
                        }
                        if (jg.get(a, c) < 0) {
                            jg.set(a, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, d) < 0) {
                            jg.set(b, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, a) < 0) {
                            jg.set(c, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, b) < 0) {
                            jg.set(d, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }


                    //　a>c>b　なら　a>d>b
                    if ((jg.get(a, c) == 1) && (jg.get(c, b) == 1)) {
                        if (jg.get(d, a) == 1) {
                            return 4;
                        }
                        if (jg.get(b, d) == 1) {
                            return 4;
                        }
                        if (jg.get(a, d) < 0) {
                            jg.set(a, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, b) < 0) {
                            jg.set(d, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, a) < 0) {
                            jg.set(d, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, d) < 0) {
                            jg.set(b, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　a>d>b　なら　a>c>b
                    if ((jg.get(a, d) == 1) && (jg.get(d, b) == 1)) {
                        if (jg.get(c, a) == 1) {
                            return 4;
                        }
                        if (jg.get(b, c) == 1) {
                            return 4;
                        }
                        if (jg.get(a, c) < 0) {
                            jg.set(a, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, b) < 0) {
                            jg.set(c, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, a) < 0) {
                            jg.set(c, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, c) < 0) {
                            jg.set(b, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　b>c>a　なら　b>d>a
                    if ((jg.get(b, c) == 1) && (jg.get(c, a) == 1)) {
                        if (jg.get(d, b) == 1) {
                            return 4;
                        }
                        if (jg.get(a, d) == 1) {
                            return 4;
                        }
                        if (jg.get(b, d) < 0) {
                            jg.set(b, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, a) < 0) {
                            jg.set(d, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, b) < 0) {
                            jg.set(d, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(a, d) < 0) {
                            jg.set(a, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　b>d>a　なら　b>c>a
                    if ((jg.get(b, d) == 1) && (jg.get(d, a) == 1)) {
                        if (jg.get(c, b) == 1) {
                            return 4;
                        }
                        if (jg.get(a, c) == 1) {
                            return 4;
                        }
                        if (jg.get(b, c) < 0) {
                            jg.set(b, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, a) < 0) {
                            jg.set(c, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, b) < 0) {
                            jg.set(c, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(a, c) < 0) {
                            jg.set(a, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }


                    //　c>a>d　なら　c>b>d
                    if ((jg.get(c, a) == 1) && (jg.get(a, d) == 1)) {
                        if (jg.get(b, c) == 1) {
                            return 4;
                        }
                        if (jg.get(d, b) == 1) {
                            return 4;
                        }
                        if (jg.get(c, b) < 0) {
                            jg.set(c, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, d) < 0) {
                            jg.set(b, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, c) < 0) {
                            jg.set(b, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, b) < 0) {
                            jg.set(d, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　c>b>d　なら　c>a>d
                    if ((jg.get(c, b) == 1) && (jg.get(b, d) == 1)) {
                        if (jg.get(a, c) == 1) {
                            return 4;
                        }
                        if (jg.get(d, a) == 1) {
                            return 4;
                        }
                        if (jg.get(c, a) < 0) {
                            jg.set(c, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(a, d) < 0) {
                            jg.set(a, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(a, c) < 0) {
                            jg.set(a, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(d, a) < 0) {
                            jg.set(d, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　d>a>c　なら　d>b>c
                    if ((jg.get(d, a) == 1) && (jg.get(a, c) == 1)) {
                        if (jg.get(b, d) == 1) {
                            return 4;
                        }
                        if (jg.get(c, b) == 1) {
                            return 4;
                        }
                        if (jg.get(d, b) < 0) {
                            jg.set(d, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, c) < 0) {
                            jg.set(b, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(b, d) < 0) {
                            jg.set(b, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, b) < 0) {
                            jg.set(c, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　d>b>c　なら　d>a>c
                    if ((jg.get(d, b) == 1) && (jg.get(b, c) == 1)) {
                        if (jg.get(a, d) == 1) {
                            return 4;
                        }
                        if (jg.get(c, a) == 1) {
                            return 4;
                        }
                        if (jg.get(d, a) < 0) {
                            jg.set(d, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(a, c) < 0) {
                            jg.set(a, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(a, d) < 0) {
                            jg.set(a, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (jg.get(c, a) < 0) {
                            jg.set(c, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }


                    // }
                }
                //System.out.print("推測された関係の数は ＝ ");System.out.println(flg_a);
            }

            //----------------

            System.out.print("推測された関係の数の合計 ＝ ");
            System.out.println(flg_c);

        }


        System.out.println("追加推定 終了------------------------＊＊＊＊ここまで20150310＊＊＊＊＊＊＊＊＊＊＊");

        return 1000;

    }

    //------------------------------------------

    private int tuika_suitei_2() {
        //山折り谷折りの情報から更に決定できる関係を推測していく。

        int Mid;//3面の比較で中間にくる面
        int flg_c = 1;
        System.out.println("追加推定開始---------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        while (flg_c >= 1) {
            flg_c = 0;
            System.out.println("追加推定------------------------");
            //System.out.println("山折り谷折りの情報から追加推定   " );

            int flg_b = 1;
            while (flg_b >= 1) {
                flg_b = 0;

                for (int iS = 1; iS <= Smensuu; iS++) {

                    int flg_a = 1;
                    while (flg_a >= 1) {
                        flg_a = 0;
                        for (int iM = 1; iM <= s0[iS].getMenidsuu(); iM++) {//3面の比較で中間にくる面
                            int[] ueMenid = new int[s0[iS].getMenidsuu() + 1];//S面に含まれるあるMenの上がわにあるid番号を記録する。これが20ということは、
                            int[] sitaMenid = new int[s0[iS].getMenidsuu() + 1];//S面に含まれるあるMenの下がわにあるid番号を記録する。これが20ということは、
                            int ueMenid_max = 0;
                            int sitaMenid_max = 0;
                            Mid = s0[iS].getMenid(iM);

                            //考え方：あるSmenのある面Midについて考える。
                            //このSmen以外で面Aは面Midの上側、面Bは面Midの下側と決まっていたとする。
                            //一般に別々のSmenで、面Aは面Midの上側で、面Bは面Midの下側だったからと言って、面Aは面Bの上側とは決定できない。
                            //しかし、ここがポイントだが、面A、面Mid、面Bがいっしょに含まれているSmenがあるなら、そのSmenの上下関係がわかっていなくても
                            //面Aは面Bの上側となる。だから、この操作ではあるSmenから得る情報は３つの面がいっしょにあるかということである。
                            //あるSmen内の上下関係は必要ない。
                            //
                            //ここの操作はあるSmenの上下関係を上下表から採集している。
                            for (int i = 1; i <= s0[iS].getMenidsuu(); i++) {//Menid[iM]より上にある面。
                                if (iM != i) {
                                    if (jg.get(Mid, s0[iS].getMenid(i)) == 0) {
                                        ueMenid_max = ueMenid_max + 1;
                                        ueMenid[ueMenid_max] = s0[iS].getMenid(i);
                                    }
                                    if (jg.get(Mid, s0[iS].getMenid(i)) == 1) {
                                        sitaMenid_max = sitaMenid_max + 1;
                                        sitaMenid[sitaMenid_max] = s0[iS].getMenid(i);
                                    }
                                }
                            }

                            //  System.out.print("VVVVVVVVVVVueMenid_max:sitaMenid_max = ");System.out.print(ueMenid_max);
                            //  System.out.print(":");System.out.println(sitaMenid_max);
                            //
                            for (int iuM = 1; iuM <= ueMenid_max; iuM++) {//Menid[iM]より上にある面。
                                for (int isM = 1; isM <= sitaMenid_max; isM++) {//Menid[iM]より下にある面。

                                    /**/      //  System.out.print(ueMenid[iuM]);System.out.print("<-上:下->");System.out.println(sitaMenid[isM]);


                                    if (jg.get(ueMenid[iuM], sitaMenid[isM]) == 0) {
                                        return 2;
                                    }//面の上下関係の拡張で矛盾発生。
                                    if (jg.get(sitaMenid[isM], ueMenid[iuM]) == 1) {
                                        return 2;
                                    }//面の上下関係の拡張で矛盾発生。

                                    if (jg.get(ueMenid[iuM], sitaMenid[isM]) < 0) {
                                        jg.set(ueMenid[iuM], sitaMenid[isM], 1);
                                        flg_a = flg_a + 1;
                                        flg_b = flg_b + 1;
                                        flg_c = flg_c + 1;
                                    }
                                    if (jg.get(sitaMenid[isM], ueMenid[iuM]) < 0) {
                                        jg.set(sitaMenid[isM], ueMenid[iuM], 0);
                                        flg_a = flg_a + 1;
                                        flg_b = flg_b + 1;
                                        flg_c = flg_c + 1;
                                    }
                                    //   System.out.print("AAAAAAAAAAAflg_a:");System.out.println(flg_a);
                                }
                            }
                        }
                    }
                }


            }

            //System.out.print("推測された関係の数 ＝ ");System.out.println(flg_b);
        }


        System.out.println("追加推定 終了------------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        return 1000;

    }


    //-----------------------------------------------------------------------------------------

    private int Smen_i_ga_j_ni_fukumareru(int s0i, int s0j) { //含まれるなら１、ないなら０
        if (s0[s0i].getMenidsuu() > s0[s0j].getMenidsuu()) {
            return 0;
        }


        for (int i = 1; i <= s0[s0i].getMenidsuu(); i++) {
            for (int j = 1; j <= s0[s0j].getMenidsuu(); j++) {
                if (s0[s0i].getMenid(i) == s0[s0j].getMenid(j)) {
                    break;
                }
                if (j == s0[s0j].getMenidsuu()) {
                    return 0;
                }
            }
        }

        return 1;

    }

    //s0(Smen)の優先度を示す値を求める。この値が高いほうが優先度が上（配列の先頭に近い。）
    int Smenid_yusendo(int s0id) {
        //return s0[s0id].getMenidsuu();

        //現在の上下表にSmenを入れたら何個の上下関係が新たに登録されるかを調べる。
        return s0[s0id].sinki_jyouhou_suu(jg);


    }

    //-----------
    //------------------------
    //引数の３つの面を同時に含むSmenが1つ以上存在するなら１、しないなら０を返す。
    private int onaji_Smen_ni_sonzai(int im1, int im2, int im3) {
        for (int i = 1; i <= Smensuu; i++) {
            if (s[i].Menid2Jyunretuketa(im1) >= 1) {
                if (s[i].Menid2Jyunretuketa(im2) >= 1) {
                    if (s[i].Menid2Jyunretuketa(im3) >= 1) {

                        return 1;
                    }
                }
            }

        }
        return 0;

    }


    //------------------------
    //引数の４つの面を同時に含むSmenが1つ以上存在するなら１、しないなら０を返す。
    private int onaji_Smen_ni_sonzai(int im1, int im2, int im3, int im4) {
        for (int i = 1; i <= Smensuu; i++) {
            if (s[i].Menid2Jyunretuketa(im1) >= 1) {
                if (s[i].Menid2Jyunretuketa(im2) >= 1) {
                    if (s[i].Menid2Jyunretuketa(im3) >= 1) {
                        if (s[i].Menid2Jyunretuketa(im4) >= 1) {
                            return 1;
                        }
                    }
                }
            }
        }
        return 0;

    }


    //　ここは  class Jyougehyou_Syokunin  の中です。


    //Smenの面の重なり状態を次の状態にする。
    //もし現在の面の重なり状態が、最後のものだったら0をreturnして、面の重なり状態は最初のものに戻る。
    //zzzzzzzz

    public int susumu(int ss) {
        int isusumu;//=0の場合Smenが変わる（桁が変るようなイメージ）。
        int Sid;//変化が及んだSmenのid番号
        isusumu = 0;
        //ss+1番目以上のSmenはみな初期値にする。Smenに含まれる面数が0のときはエラーになる。
//System.out.println("    js.susumu 001      ss+1番目以上のSmenはみな初期値にする。");

        for (int i = ss + 1; i <= Smensuu; i++) {

//System.out.println("    js.susumu i= "+i+"        Smensuu= "+Smensuu);

            s[i].Jyunretu_1banme();
        }
//System.out.println("    js.susumu 002      ss+1番目以上のSmenはみな初期値にする。");
        //Smenのid番号の大きいものから小さいものの順に面の重なり状態を変えていく。
        Sid = ss;
        //isusumu =s[ss].susumu(s);
        for (int i = ss; i >= 1; i--) {
            if (isusumu == 0) {
                isusumu = s[i].susumu(s[i].getMenidsuu());
                Sid = i;
            }

        }
        if (isusumu == 0) {
            Sid = 0;
        }

        return Sid;
    }


    /*
public int yuukouk_Smen_susumu(){//yuukoなSmen数だけ桁が変わる
		int isusumu;//=0の場合Smenが変わる（桁が変るようなイメージ）。
		int Sid;//変化が及んだSmenのid番号
		isusumu =0;
int ss; ss=getSmen_yuukou_suu();
	//ss+1番目以上のSmenはみな初期値にする。有効数以上は使わないので、何もしないことにする。
//System.out.println("    js.susumu 001      ss+1番目以上のSmenはみな初期値にする。");

		//for(int i=ss+1;i<=Smensuu;i++){

//System.out.println("    js.susumu i= "+i+"        Smensuu= "+Smensuu);

		 // s[i].Jyunretu_1banme();
		//}
//System.out.println("    js.susumu 002      ss+1番目以上のSmenはみな初期値にする。");
		  //Smenのid番号の大きいものから小さいものの順に面の重なり状態を変えていく。
                Sid=ss;
		//isusumu =s[ss].susumu(s);
		for(int i=ss;i>=1;i--){
			if(isusumu == 0){isusumu=s[i].susumu(s[i].getMenidsuu());Sid=i;}

		}
		if (isusumu==0){Sid=0;}

		return Sid;
	}


*/
//---------------------------------------------------------------------------------------------------------------------------------------------
    public String Jyunretu_count() {
        String s0 = "";

        for (int ss = 1; ss <= Smen_yuukou_suu; ss++) {
            s0 = s0 + " : " + s[ss].get_Jyunretu_count();
        }
        return s0;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    public String Jyunretu_count(int imax) {
        String s0 = "";

        for (int ss = 1; ss <= imax; ss++) {
            s0 = s0 + " : " + s[ss].get_Jyunretu_count();
        }
        return s0;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    public int kanou_kasanari_sagasi_new() {

//System.out.println(" Jyougehyou_syokunin          kanou_kasanari_sagasi()   ");
//System.out.print("Smen有効数/Smensuu　" );System.out.print(Smen_yuukou_suu );System.out.print("／");System.out.println(Smensuu );
        if (Smen_yuukou_suu == 0) {
            return 1000;
        }//Smen_yuukou_suu==0ということは、上下表に新たな情報を加えるSmenがないということ。すなわち、上下表がすべて埋まっているということを示すため、これ以上の重なり推定は不要。


        return kanou_kasanari_sagasi_self(1);
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    public int kanou_kasanari_sagasi_self(int ss) {
        //orihime_ap.keijiban.tuiki("ss = "+ss  +"  :  Smen_yuukou_suu = "+Smen_yuukou_suu );
        //最終桁での処理
        if (ss == Smen_yuukou_suu) {

            if (s[ss].kanou_kasanari_sagasi(jg) == 1000) {//==0ということは、可能な重なりかたとなる順列は存在しない。　==1000　このSmenは、矛盾はない状態になっている。
                return 100;//折り畳み可能な順列組み合わせが見つかった。
            } else {
                return 0;
            }

        }


        //最終桁以外での処理
        if (s[ss].kanou_kasanari_sagasi(jg) == 1000) {//==0ということは、可能な重なりかたとなる順列は存在しない。　==1000　このSmenは、矛盾はない状態になっている。


            while (kanou_kasanari_sagasi_self(ss + 1) == 0) {//次の桁で可能な重なりかたとなる順列は存在しない
                if (s[ss].susumu() == 0) {
                    return 0;
                }//この桁では進めない（新たな順列は無い）

            }

            return 1000;//折り畳み可能な順列組み合わせが見つかった。

        }

        return 0;


        //for(int ss=1;ss<=Smen_yuukou_suu;ss++){      //<<<<<<<<<<<<<<高速化のため変更。070417
        //        s[ss].jg_ni_Smen_wo_nyuuryoku(jg);//ss番目のSmenの上下情報をjgに入力する。
        //  }
// return 100;

    }


    public int susumu_new() {
        return 0;
    }
//------------------------------------------------------------------------------------------------------

    //現在の順列状態から開始して、可能な重なりかたとなる状態を探す。なお、ここは高速化の余地がある。
    public int kanou_kasanari_sagasi() {      //これはjgを変えないはず。
        orihime_app.keijiban.tuiki("_ _______");
        orihime_app.keijiban.tuiki("__ ______");
        orihime_app.keijiban.tuiki("___ _____");
        orihime_app.keijiban.tuiki("____ ____");
        int ms, Sid;
        ms = 0;

        //System.out.print("面数は2　");  System.out.println(s[1].getMenidsuu());

        Sid = 1;//Sidの初期値は0以外ならなんでもいい。
        while (Sid != 0) { //Sid==0なら、Smenの最も番号の小さいものまで調べ尽くしたという意味。

            ms = mujyun_Smen_motome();
            if (ms == 1000) {
                return 1000;
            }//全てのSmenで、矛盾はない。
            Sid = susumu(ms - 1);
            orihime_app.keijiban.kakikae(9, "susumu(" + ms + "-1 = )" + Sid);
        }
        return 0;//可能な重なりかたとなる状態は存在しない
    }

    //-----------------------------------------------------------------------------------------------------------------
    //折り重なり方が矛盾するSmenを番号の小さい順からさがす。  なお、ここも高速化の余地がある。
    private int mujyun_Smen_motome() { //jgは変化する。

        //orihime_ap.keijiban.kakikae(9,"mujyun_Smen_motome()");
        int kks;
        jg.jg_fukugen();//<<<<<<<<<<<<<<<<<<<<<<<<<<<,,

        for (int ss = 1; ss <= Smen_yuukou_suu; ss++) {      //<<<<<<<<<<<<<<高速化のため変更。070417

            orihime_app.keijiban.kakikae(7, "mujyun_Smen_motome( " + ss + ") , Menidsuu = " + s[ss].getMenidsuu() + " , Men_pair_suu = " + s[ss].getMenidsuu() * (s[ss].getMenidsuu() - 1) / 2);
            orihime_app.keijiban.kakikae(8, " kasanari_bunryi_mitei = " + s[ss].kasanari_bunryi_mitei(jg));
            orihime_app.keijiban.kakikae(9, " kasanari_bunryi_ketteizumi = " + s[ss].kasanari_bunryi_ketteizumi(jg));


            kks = s[ss].kanou_kasanari_sagasi(jg);
            orihime_app.keijiban.kakikae(10, Jyunretu_count(ss));


            if (kks == 0) {//kks==0ということは、可能な重なりかたとなる順列は存在しない

                return ss;
            }
            s[ss].jg_ni_Smen_wo_nyuuryoku(jg);//ss番目のSmenの上下情報をjgに入力する。


        }

        if (tuika_suitei() != 1000) {
            return Smen_yuukou_suu;
        } //ttttttttttt

        return 1000;
    }

// ---------------------------------------------------------------

    //　ここは  class Jyougehyou_Syokunin  の中です。
    //-----------------------------------------------------
    public void set_hyouji_yousiki(int i) {
        hyouji_yousiki = i;
    }


    //図をかく際の数値変換用関数-----------------------------------------------------------------

    private int gx(double d) {
        //if(ip1==1){return (int)(-d+2.0*700.0);} //裏側表示   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        //if(ip1==1){return (int)(-d);} //裏側表示   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // if(ip1==0){return (int)d;} //表側表示
        return (int) d; //表側表示
    }

    private int gy(double d) {
        return (int) d;
    }

    //---------------------------------------------------------
    //---------------------------------------------------------


    Color F_color = new Color(255, 255, 50);//表面の色
    Color B_color = new Color(233, 233, 233);//裏面の色
    Color L_color = Color.black;//線の色

    public void set_F_color(Color color0) {
        F_color = color0;
    }

    public void set_B_color(Color color0) {
        B_color = color0;
    }

    public void set_L_color(Color color0) {
        L_color = color0;
    }

    public Color get_F_color() {
        return F_color;
    }

    public Color get_B_color() {
        return B_color;
    }

    public Color get_L_color() {
        return L_color;
    }


    int i_anti_alias = 1;
    double senhaba_for_anti_alias = 1.2;

    //---------------------------------------------------------


    public void set_i_anti_alias(int i) {
        i_anti_alias = i;
    }

    public void change_i_anti_alias() {

        i_anti_alias = i_anti_alias + 1;
        if (i_anti_alias >= 2) {
            i_anti_alias = 0;
        }

        if (i_anti_alias == 0) {
            senhaba_for_anti_alias = 1.0;
        }
        if (i_anti_alias == 1) {
            senhaba_for_anti_alias = 1.2;
        }

    }


    public void oekaki(Graphics g, Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu, int omote_ura, int hyouji_flg) {


        //hyouji_flgは折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図
        String text = "";//文字列処理用のクラスのインスタンス化
        ip1 = omote_ura;
        //  System.out.println(Smensuu);
        for (int im = 1; im <= Smensuu; im++) { //Smenから上からの指定した番目の面のidを求める。
            s0[im].set_Menid2uekara_kazoeta_iti(jg);//Smenの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        }

        //面を描く
        int[] x = new int[100];
        int[] y = new int[100];

        if (hyouji_flg == 4) {//折紙表示---------------------------------------------------------------------------
            //面を描く
            int Men_jyunban;
            for (int im = 1; im <= Smensuu; im++) {
                Men_jyunban = 1;
                if (omote_ura == 1) {
                    Men_jyunban = s0[im].getMenidsuu();
                }

                if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                    g.setColor(new Color(255, 255, 50));
                }
                if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                    g.setColor(new Color(233, 233, 233));
                }

                if (omote_ura == 1) {
                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                        g.setColor(new Color(255, 255, 50));
                    }
                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                        g.setColor(new Color(233, 233, 233));
                    }
                }

                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {
                    x[i] = gx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                    y[i] = gy(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                }

                x[0] = gx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                y[0] = gy(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));

                g.fillPolygon(x, y, Smen_zu.getTenidsuu(im));
            }

            g.setColor(Color.black);

            //棒を描く
            int Mid_min, Mid_max;
            int Men_jyunban_min, Men_jyunban_max;
            for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {
                Mid_min = Smen_zu.Bou_moti_Menid_min_motome(ib);
                Mid_max = Smen_zu.Bou_moti_Menid_max_motome(ib);
                if (Mid_min == Mid_max) {//この棒は境界線なのでこの棒は描く。
                    g.drawLine(gx(Smen_zu.getmaex(ib)), gy(Smen_zu.getmaey(ib)), gx(Smen_zu.getatox(ib)), gy(Smen_zu.getatoy(ib))); //直線
                }

                Men_jyunban_min = 1;
                if (omote_ura == 1) {
                    Men_jyunban_min = s0[Mid_min].getMenidsuu();
                }
                Men_jyunban_max = 1;
                if (omote_ura == 1) {
                    Men_jyunban_max = s0[Mid_max].getMenidsuu();
                }

                if (
                        (Mid_min != Mid_max)
                                &&
                                (s0[Mid_min].uekara_kazoeta_Menid(Men_jyunban_min) !=
                                        s0[Mid_max].uekara_kazoeta_Menid(Men_jyunban_max))
                ) {//この棒で隣接するSmenの1番上の面は異なるので、この棒は描く。
                    g.drawLine(gx(Smen_zu.getmaex(ib)), gy(Smen_zu.getmaey(ib)), gx(Smen_zu.getatox(ib)), gy(Smen_zu.getatoy(ib))); //直線
                }
            }
        }

        if (hyouji_flg == -3) {//透過表示--------------------------------------------------------------------------------
            int col_hiku = 10;
            int colmax = 250;
            int col_kosa;
            System.out.println("Smen透過表示");
            //面を描く
            for (int im = 1; im <= Smen_zu.getMensuu(); im++) {
                col_kosa = colmax - col_hiku * (s0[im].getMenidsuu() - 1);
                if (col_kosa < 0) {
                    col_kosa = 0;
                }
                g.setColor(new Color(col_kosa, col_kosa, col_kosa));

                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {
                    x[i] = gx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                    y[i] = gy(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                }

                x[0] = gx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                y[0] = gy(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));

                g.fillPolygon(x, y, Smen_zu.getTenidsuu(im));
            }

            g.setColor(Color.black);

            //棒を描く
            int Mid_min, Mid_max;
            int Men_jyunban_min, Men_jyunban_max;
            for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {
                g.drawLine(gx(Smen_zu.getmaex(ib)), gy(Smen_zu.getmaey(ib)), gx(Smen_zu.getatox(ib)), gy(Smen_zu.getatoy(ib))); //直線
            }
        }
/*
	        if(hyouji_flg==2){  //針金表示--------------------------------------------------------------------------------------------------
			int  col_hiku=10;int colmax=250;
			int col_kosa;
			//面を描く
			g.setColor(Color.black);

			//棒を描く
			for(int ib=1;ib<=otta_Men_zu.getBousuu();ib++){
                         	g.drawLine( gx(otta_Men_zu.getmaex(ib)),gy(otta_Men_zu.getmaey(ib)),gx(otta_Men_zu.getatox(ib)),gy(otta_Men_zu.getatoy(ib))); //直線
			}
		}



	        if(hyouji_flg==4){  //Smen表示--------------------------------------------------------------------------------------------------
			//面を描く
 			System.out.println("Smensuu="+Smensuu);

			for (int im=1; im<=Smensuu; im++ ){

				g.setColor(Color.black);
				for (int i=1; i<=Smen_zu.getTenidsuu(im)-1; i++ ){
					x[i]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,i)));
					y[i]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,i)));
				}

				x[0]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
				y[0]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));

				g.setColor(Color.red)  ;g.fillPolygon(x,y,Smen_zu.getTenidsuu(im));
				g.setColor(Color.black);g.drawPolygon(x,y,Smen_zu.getTenidsuu(im));
			}

		}
*/
        //表示様式１Smenを描く
        if (hyouji_yousiki == 1) {
            for (int iu = 1; iu <= Smen_yuukou_suu; iu++) {
                int im;
                im = yusenjyun_kara_s0id[iu]; //Smenの優先順位
                int col_kosa;
                col_kosa = 255 - 20 * iu;
                if (col_kosa < 0) {
                    col_kosa = 0;
                }
                g.setColor(new Color(col_kosa, 0, 0));

                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {
                    x[i] = gx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                    y[i] = gy(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                }

                x[0] = gx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                y[0] = gy(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));

                g.fillPolygon(x, y, Smen_zu.getTenidsuu(im));
            }
        }
    }


    //---------------------------------------------------------

    public Memo getMemo_for_svg_kakidasi() {
        Memo memo_temp = new Memo();

        //memo_temp.set(ori_s.getMemo());
        //	memo_temp.addMemo(hoj_s.h_getMemo());
        //	Memo_jyouhou_tuika(memo_temp);


        return memo_temp;
    }


    //---------------------------------------------------------

    public Memo getMemo_for_svg_with_camera(Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu) {//折り上がり図(hyouji_flg==5)
        int omote_ura = 0;
        if (camera.get_camera_kagami() == 1.0) {
            omote_ura = 0;
        }//カメラの鏡設定が1なら、折り上がり図は表表示
        if (camera.get_camera_kagami() == -1.0) {
            omote_ura = 1;
        }//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示

        Ten t0 = new Ten();
        Ten t1 = new Ten();
        Senbun s_ob = new Senbun();
        Senbun s_tv = new Senbun();

        ip1 = omote_ura;
        //  System.out.println(Smensuu);

        Memo memo_temp = new Memo();

        Ten a = new Ten();
        Ten b = new Ten();
        String str = "";
        String str_zahyou = "";
        String str_stroke = "";
        str_stroke = "black";
        String str_strokewidth = "";
        str_strokewidth = "1";


        //面を描く-----------------------------------------------------------------------------------------------------
        int[] x = new int[100];
        int[] y = new int[100];
        double[] xd = new double[100];
        double[] yd = new double[100];

        //Smenの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        for (int im = 1; im <= Smensuu; im++) { //Smenから上からの指定した番目の面のidを求める。
            s0[im].set_Menid2uekara_kazoeta_iti(jg);//s0[]はSmen_zuから得られるSmenそのもの、jgは上下表Jyougehyouのこと
        }
        //ここまでで、上下表の情報がSmenの各面に入った

        //面を描く
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
        int Men_jyunban;
        for (int im = 1; im <= Smensuu; im++) {//imは各Smenの番号
            if (s0[im].getMenidsuu() > 0) {//MenidsuuはSmen(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外

                //折り上がり図を描くときのim番目のSmenの色を決める
                Men_jyunban = 1;
                if (omote_ura == 1) {
                    Men_jyunban = s0[im].getMenidsuu();
                }


                if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                    str_stroke = ms.toHtmlColor(F_color);
                }//g.setColor(F_color)
                if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                    str_stroke = ms.toHtmlColor(B_color);
                }//g.setColor(B_color)


                //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==1){str_stroke ="yellow";}//g.setColor(F_color)
                //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==0){str_stroke ="gray";}//g.setColor(B_color)


                if (omote_ura == 1) {
                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                        str_stroke = "yellow";
                    }//g.setColor(F_color)
                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                        str_stroke = "gray";
                    }//g.setColor(B_color)
                }

                //折り上がり図を描くときのSmenの色を決めるのはここまで

                //折り上がり図を描くときのim番目のSmenの多角形の頂点の座標（PC表示上）を求める
                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {
                    t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                    t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getx());
                    y[i] = gy(t1.gety());
                }

                t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getx());
                y[0] = gy(t1.gety());
                //折り上がり図を描くときのim番目のSmenの多角形の頂点の座標（PC表示上）を求めるのはここまで

                //g2.fill(new Polygon(x,y,Smen_zu.getTenidsuu(im)));  //svg出力

                str_zahyou = x[0] + "," + y[0];
                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {
                    str_zahyou = str_zahyou + " " + x[i] + "," + y[i];

                }

/*
				memo_temp.addGyou(    "<polygon points=\"" + str_zahyou + "\"" +
							    " fill=\"" + str_stroke	 + "\"" +
							  " stroke=\"" + str_stroke	 + "\"" +
						    " stroke-width=\"" + str_strokewidth + "\"" +" />"
													);
*/

                memo_temp.addGyou("<polygon points=\"" + str_zahyou + "\"" +
                        " style=\"" + "stroke:" + str_stroke + ";fill:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );


//"stroke:#ff0000;fill:#ff0000"
                //<polygon points="40,380 120,320 200,380" fill="red" stroke="red" stroke-width="1" />


            }
        }
        //面を描く　ここまで-----------------------------------------------------------------------------------------


        //棒を描く-----------------------------------------------------------------------------------------
/*
		if(i_anti_alias==1){
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);//アンチェイリアス　オン
			BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);g2.setStroke(BStroke);//線の太さや線の末端の形状
		}
		if(i_anti_alias==0){
	  		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
			BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);g2.setStroke(BStroke);//線の太さや線の末端の形状
		}
*/
        //g.setColor(L_color);//折り上がり図を描くときの棒の色を決める

        //str_stroke = "black";
        str_stroke = ms.toHtmlColor(L_color);

        //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　棒を描く");
        for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {

            int Mid_min, Mid_max; //棒の両側のSmenの番号の小さいほうがMid_min,　大きいほうがMid_max
            int Men_jyunban_min, Men_jyunban_max;//PC画面に表示したときSmen(Mid_min) で見える面の番号がMen_jyunban_min、Smen(Mid_max) で見える面の番号がMen_jyunban_max
            int oekaki_flg;

            oekaki_flg = 0;
            Mid_min = Smen_zu.Bou_moti_Menid_min_motome(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            Mid_max = Smen_zu.Bou_moti_Menid_max_motome(ib);

            if (s0[Mid_min].getMenidsuu() == 0) {
                oekaki_flg = 1;
            }//menをもたない、ドーナツの穴状のSmenは境界の棒を描く
            else if (s0[Mid_max].getMenidsuu() == 0) {
                oekaki_flg = 1;
            } else if (Mid_min == Mid_max) {
                oekaki_flg = 1;
            }//一本の棒の片面だけにSmen有り
            else {
                Men_jyunban_min = 1;
                if (omote_ura == 1) {
                    Men_jyunban_min = s0[Mid_min].getMenidsuu();
                }
                Men_jyunban_max = 1;
                if (omote_ura == 1) {
                    Men_jyunban_max = s0[Mid_max].getMenidsuu();
                }
                if (s0[Mid_min].uekara_kazoeta_Menid(Men_jyunban_min) != s0[Mid_max].uekara_kazoeta_Menid(Men_jyunban_max)) {
                    oekaki_flg = 1;
                }//この棒で隣接するSmenの1番上の面は異なるので、この棒は描く。
            }

            if (oekaki_flg == 1) {//棒を描く。
                s_ob.set(Smen_zu.getmaex(ib), Smen_zu.getmaey(ib), Smen_zu.getatox(ib), Smen_zu.getatoy(ib));
                s_tv.set(camera.object2TV(s_ob));
                //g.drawLine( gx(s_tv.getax()),gy(s_tv.getay()),gx(s_tv.getbx()),gy(s_tv.getby())); //svg に直線を出力

                a.set(s_tv.geta());
                b.set(s_tv.getb());//a.set(s_tv.getax()+0.000001,s_tv.getay()+0.000001); b.set(s_tv.getbx()+0.000001,s_tv.getby()+0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                BigDecimal b_ax = new BigDecimal(String.valueOf(a.getx()));
                BigDecimal b_ay = new BigDecimal(String.valueOf(a.gety()));
                BigDecimal b_bx = new BigDecimal(String.valueOf(b.getx()));
                BigDecimal b_by = new BigDecimal(String.valueOf(b.gety()));


                memo_temp.addGyou("<line x1=\"" + b_ax.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "\"" +
                        " y1=\"" + b_ay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "\"" +
                        " x2=\"" + b_bx.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "\"" +
                        " y2=\"" + b_by.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "\"" +
                        " style=\"" + "stroke:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );


/*

					memo_temp.addGyou(    "<line x1=\"" + str.valueOf(a.getx()) + "\"" +
							      " y1=\"" + str.valueOf(a.gety()) + "\"" +
							      " x2=\"" + str.valueOf(b.getx()) + "\"" +
							      " y2=\"" + str.valueOf(b.gety()) + "\"" +
							//  " stroke=\"" + str_stroke	 + "\"" +
						      " style=\"" + "stroke:"+str_stroke + "\"" +
						    " stroke-width=\"" + str_strokewidth + "\"" +" />"
													);
*/


            }
        }


        return memo_temp;
    }
    //---------------------------------------------------------

    public void oekaki_with_camera(Graphics g, Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu, int omote_ura, int hyouji_flg) {


        //hyouji_flgは折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図
        OritaOekaki OO = new OritaOekaki();
        Ten t0 = new Ten();
        Ten t1 = new Ten();
        Senbun s_ob = new Senbun();
        Senbun s_tv = new Senbun();
        String text = "";//文字列処理用のクラスのインスタンス化
        ip1 = omote_ura;
        //  System.out.println(Smensuu);

        //面を描く
        int[] x = new int[100];
        int[] y = new int[100];


        if (hyouji_flg == 5) {//折紙表示---------------------------------------------------------------------------

            for (int im = 1; im <= Smensuu; im++) { //Smenから上からの指定した番目の面のidを求める。
                s0[im].set_Menid2uekara_kazoeta_iti(jg);
            }


            //面を描く
//System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　面を描く");
            int Men_jyunban;
            for (int im = 1; im <= Smensuu; im++) {
                if (s0[im].getMenidsuu() > 0) {
                    Men_jyunban = 1;
                    if (omote_ura == 1) {
                        Men_jyunban = s0[im].getMenidsuu();
                    }

                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                        g.setColor(new Color(255, 255, 50));
                    }
                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                        g.setColor(new Color(233, 233, 233));
                    }

                    if (omote_ura == 1) {
                        if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                            g.setColor(new Color(255, 255, 50));
                        }
                        if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                            g.setColor(new Color(233, 233, 233));
                        }
                    }

                    for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {

                        t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                        t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                        t1.set(camera.object2TV(t0));
                        x[i] = gx(t1.getx());
                        y[i] = gy(t1.gety());
                        //x[i]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,i)));
                        //y[i]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,i)));
                    }

                    t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                    t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                    t1.set(camera.object2TV(t0));
                    x[0] = gx(t1.getx());
                    y[0] = gy(t1.gety());
                    //x[0]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
                    //y[0]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));

                    g.fillPolygon(x, y, Smen_zu.getTenidsuu(im));
                }
            }

            g.setColor(Color.black);
//System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　棒を描く");
            //棒を描く
            int Mid_min, Mid_max;
            int Men_jyunban_min, Men_jyunban_max;
            int oekaki_flg;
            for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {
                oekaki_flg = 0;
                Mid_min = Smen_zu.Bou_moti_Menid_min_motome(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
                Mid_max = Smen_zu.Bou_moti_Menid_max_motome(ib);

                if (s0[Mid_min].getMenidsuu() == 0) {
                    oekaki_flg = 1;
                }//menをもたない、ドーナツの穴状のSmenは境界の棒を描く
                else if (s0[Mid_max].getMenidsuu() == 0) {
                    oekaki_flg = 1;
                } else if (Mid_min == Mid_max) {
                    oekaki_flg = 1;
                }//一本の棒の片面だけにSmen有り
                else {
                    Men_jyunban_min = 1;
                    if (omote_ura == 1) {
                        Men_jyunban_min = s0[Mid_min].getMenidsuu();
                    }
                    Men_jyunban_max = 1;
                    if (omote_ura == 1) {
                        Men_jyunban_max = s0[Mid_max].getMenidsuu();
                    }
                    if (s0[Mid_min].uekara_kazoeta_Menid(Men_jyunban_min) != s0[Mid_max].uekara_kazoeta_Menid(Men_jyunban_max)) {
                        oekaki_flg = 1;
                    }//この棒で隣接するSmenの1番上の面は異なるので、この棒は描く。
                }

                if (oekaki_flg == 1) {//棒を描く。
                    s_ob.set(Smen_zu.getmaex(ib), Smen_zu.getmaey(ib), Smen_zu.getatox(ib), Smen_zu.getatoy(ib));
                    s_tv.set(camera.object2TV(s_ob));
                    g.drawLine(gx(s_tv.getax()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
                    //g.drawLine( gx(Smen_zu.getmaex(ib)),gy(Smen_zu.getmaey(ib)),gx(Smen_zu.getatox(ib)),gy(Smen_zu.getatoy(ib))); //直線
                }


            }
            //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　終了");
        }


//System.out.println("上下表職人　oekaki_with_camera+++++++++++++++hyouji_yousiki = "+hyouji_yousiki);

        //表示様式１Smenを描く
        if (hyouji_yousiki == 1) {
            for (int iu = 1; iu <= Smen_yuukou_suu; iu++) {
                int im;
                im = yusenjyun_kara_s0id[iu]; //Smenの優先順位
                int col_kosa;
                col_kosa = 255 - 20 * iu;
                if (col_kosa < 0) {
                    col_kosa = 0;
                }
                g.setColor(new Color(col_kosa, 0, 0));

                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {

                    t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                    t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getx());
                    y[i] = gy(t1.gety());
                    //x[i]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,i)));
                    //y[i]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,i)));
                    //x[i]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,i)));
                    //y[i]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,i)));
                }

                t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getx());
                y[0] = gy(t1.gety());
                //x[0]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
                //y[0]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
                //x[0]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
                //y[0]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));

                g.fillPolygon(x, y, Smen_zu.getTenidsuu(im));
            }
        }


        //camera中心を十字で描く
        OO.jyuuji(g, camera.object2TV(camera.get_camera_ichi()), 5.0, 2.0, 4);


    }


    //
    //---------------------------------------------------------
    public Memo getMemo_hariganezu_for_svg_kakidasi(Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu, int i_fill) {
        //System.out.println("getMemo_hariganezu_for_svg_kakidasi");
        int omote_ura = 0;
        if (camera.get_camera_kagami() == 1.0) {
            omote_ura = 0;
        }//カメラの鏡設定が1なら、折り上がり図は表表示
        if (camera.get_camera_kagami() == -1.0) {
            omote_ura = 1;
        }//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示

        Ten t_ob = new Ten();
        Ten t_tv = new Ten();
        Senbun s_ob = new Senbun();
        Senbun s_tv = new Senbun();

        ip1 = omote_ura;
        //  System.out.println(Smensuu);

        Memo memo_temp = new Memo();

        Ten a = new Ten();
        Ten b = new Ten();
        String str = "";
        String str_zahyou = "";
        String str_stroke = "";
        str_stroke = "black";
        String str_strokewidth = "";
        str_strokewidth = "1";
        String str_fill = "";
        str_fill = "";

        //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++透過表示");


        rating2();


        //面を描く準備
        int[] x = new int[100];
        int[] y = new int[100];

        //BigDecimalのコンストラクタの引数は浮動小数点数型と文字列型どちらもok。引数が浮動小数点数型は誤差が発生。正確な値を扱うためには、引数は文字列型で指定。

        for (int i_nbox = 1; i_nbox <= otta_Men_zu.getMensuu(); i_nbox++) {
            int im = 0;
            if (camera.get_camera_kagami() == -1.0) {//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示
                im = nbox.usirokara_get_int(i_nbox);
            } else {
                im = nbox.get_int(i_nbox);
            }

            String text = "";//文字列処理用のクラスのインスタンス化

            text = "M ";
            t_ob.setx(otta_Men_zu.getTenx(otta_Men_zu.getTenid(im, 1)));
            t_ob.sety(otta_Men_zu.getTeny(otta_Men_zu.getTenid(im, 1)));
            t_tv.set(camera.object2TV(t_ob));
            BigDecimal b_t_tv_x = new BigDecimal(String.valueOf(t_tv.getx()));
            BigDecimal b_t_tv_y = new BigDecimal(String.valueOf(t_tv.gety()));

            text = text + b_t_tv_x.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " " +
                    b_t_tv_y.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " ";


            for (int i = 2; i <= otta_Men_zu.getTenidsuu(im); i++) {
                text = text + "L ";
                t_ob.setx(otta_Men_zu.getTenx(otta_Men_zu.getTenid(im, i)));
                t_ob.sety(otta_Men_zu.getTeny(otta_Men_zu.getTenid(im, i)));
                t_tv.set(camera.object2TV(t_ob));
                BigDecimal b_t_tv_x_i = new BigDecimal(String.valueOf(t_tv.getx()));
                BigDecimal b_t_tv_y_i = new BigDecimal(String.valueOf(t_tv.gety()));

                text = text + b_t_tv_x_i.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " " +
                        b_t_tv_y_i.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " ";
            }

            text = text + "Z";


//<polygon points="250 50 300 100 250 100" stroke="black" fill="none"/>
//<path d="M 100 100 L 300 100 L 200 300 z" fill="red" stroke="blue" stroke-width="3" />
/*
					memo_temp.addGyou(    "<polygon points=\"" + str.valueOf( b_ax.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
							           " y1=\"" + str.valueOf( b_ay.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
							           " x2=\"" + str.valueOf( b_bx.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
							           " y2=\"" + str.valueOf( b_by.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()) + "\"" +
						      " style=\"" + "stroke:"+str_stroke + "\"" +
						    " stroke-width=\"" + str_strokewidth + "\"" +" />"

							);
*/

            if (i_fill == 0) {
                str_fill = "none";

            }

            if (i_fill == 1) {

                if (orite.getiMeniti(im) % 2 == 1) {
                    str_fill = ms.toHtmlColor(F_color);
                }
                if (orite.getiMeniti(im) % 2 == 0) {
                    str_fill = ms.toHtmlColor(B_color);
                }

                if (omote_ura == 1) {
                    if (orite.getiMeniti(im) % 2 == 1) {
                        str_fill = ms.toHtmlColor(B_color);
                    }
                    if (orite.getiMeniti(im) % 2 == 0) {
                        str_fill = ms.toHtmlColor(F_color);
                    }


                }


            }

            memo_temp.addGyou("<path d=\"" + text + "\"" +
                    " style=\"" + "stroke:" + str_stroke + "\"" +
                    " stroke-width=\"" + str_strokewidth + "\"" +
                    //" fill=\"none\"" +" />"
                    " fill=\"" + str_fill + "\"" + " />"
            );


        }

/*

//カラーの透過図
//toukazu_toukado=col_hiku;
		g.setColor(new Color(100,100, 100,50));

		//面を描く
		for (int im=1; im<=otta_Men_zu.getMensuu(); im++ ){
			for (int i=1; i<=otta_Men_zu.getTenidsuu(im)-1; i++ ){
				t0.setx(otta_Men_zu.getTenx(otta_Men_zu.getTenid(im,i)));
				t0.sety(otta_Men_zu.getTeny(otta_Men_zu.getTenid(im,i)));
				t1.set(camera.object2TV(t0));
				x[i]=gx(t1.getx());
				y[i]=gy(t1.gety());
			}

				t0.setx(otta_Men_zu.getTenx(otta_Men_zu.getTenid(im,otta_Men_zu.getTenidsuu(im))));
				t0.sety(otta_Men_zu.getTeny(otta_Men_zu.getTenid(im,otta_Men_zu.getTenidsuu(im))));
				t1.set(camera.object2TV(t0));
				x[0]=gx(t1.getx());
				y[0]=gy(t1.gety());
			g.fillPolygon(x,y,otta_Men_zu.getTenidsuu(im));
			//g.drawPolygon(x,y,otta_Men_zu.getTenidsuu(im));
		}
*/


        return memo_temp;

    }


    //---------------------------------------------------------
    public void oekaki_toukazu_with_camera(Graphics g, Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu, int i_toukazu_color, int toukazu_toukado) {
        Graphics2D g2 = (Graphics2D) g;

        //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++透過表示");
        OritaOekaki OO = new OritaOekaki();
        Ten t0 = new Ten();
        Ten t1 = new Ten();
        Senbun s_ob = new Senbun();
        Senbun s_tv = new Senbun();
        String text = "";//文字列処理用のクラスのインスタンス化

        //面を描く準備
        int[] x = new int[100];
        int[] y = new int[100];

        //面の適切な濃さを求める
        int col_hiku = 0;
        int colmax = 255;
        int colmin = 30;//colmax=255(真っ白)以下、colmin=0(真っ黒)以上
        //Menidsuu_maxは1以上であること
        if (Menidsuu_max > 0) {
            col_hiku = (colmax - colmin) / Menidsuu_max;
        }
        //if(Menidsuu_max<=0){System.out.println("上下表職人　oekaki_toukazu_with_camera!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    Menidsuu_max<=0  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");}


//if(orihime_ap.ckbox_toukazu_color.isSelected()){//カラーの透過図
//if(orihime_ap.OZ.i_toukazu_color==1){//カラーの透過図
        if (i_toukazu_color == 1) {//カラーの透過図

//toukazu_toukado=col_hiku;
            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), toukazu_toukado));

            //面を描く
            for (int im = 1; im <= otta_Men_zu.getMensuu(); im++) {
                for (int i = 1; i <= otta_Men_zu.getTenidsuu(im) - 1; i++) {
                    t0.setx(otta_Men_zu.getTenx(otta_Men_zu.getTenid(im, i)));
                    t0.sety(otta_Men_zu.getTeny(otta_Men_zu.getTenid(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getx());
                    y[i] = gy(t1.gety());
                }

                t0.setx(otta_Men_zu.getTenx(otta_Men_zu.getTenid(im, otta_Men_zu.getTenidsuu(im))));
                t0.sety(otta_Men_zu.getTeny(otta_Men_zu.getTenid(im, otta_Men_zu.getTenidsuu(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getx());
                y[0] = gy(t1.gety());
                g.fillPolygon(x, y, otta_Men_zu.getTenidsuu(im));
                //g.drawPolygon(x,y,otta_Men_zu.getTenidsuu(im));
            }

            //棒を描く準備
            //g.setColor(Color.black);


            if (i_anti_alias == 1) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//アンチェイリアス　オン
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//線の太さや線の末端の形状
            }
            if (i_anti_alias == 0) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//線の太さや線の末端の形状
            }

            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), 2 * toukazu_toukado));
            //棒を描く
            int Mid_min, Mid_max;
            int Men_jyunban_min, Men_jyunban_max;
            for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {
                s_ob.set(Smen_zu.getmaex(ib), Smen_zu.getmaey(ib), Smen_zu.getatox(ib), Smen_zu.getatoy(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.getax()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
            }


        } else {//白黒の透過図（旧式）



/*
		int col_hiku=0;int colmax=255;int colmin=30;//colmax=255(真っ白)以下、colmin=0(真っ黒)以上
		//Menidsuu_maxは1以上であること
		if(Menidsuu_max>0){col_hiku=(colmax-colmin)/Menidsuu_max;}
		//if(Menidsuu_max<=0){System.out.println("上下表職人　oekaki_toukazu_with_camera!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    Menidsuu_max<=0  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");}
*/
            int col_kosa;


            for (int im = 1; im <= Smen_zu.getMensuu(); im++) {

                col_kosa = colmax - col_hiku * (s0[im].getMenidsuu());


                if (col_kosa > 255) {
                    col_kosa = 255;
                }

                if (col_kosa < 0) {
                    col_kosa = 0;
                }
                //System.out.println("col_kosa="+col_kosa);
                g.setColor(new Color(col_kosa, col_kosa, col_kosa));

                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {
                    t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                    t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getx());
                    y[i] = gy(t1.gety());
                }

                t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getx());
                y[0] = gy(t1.gety());
                g.fillPolygon(x, y, Smen_zu.getTenidsuu(im));
            }

            //棒を描く準備
            g.setColor(Color.black);


            if (i_anti_alias == 1) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//アンチェイリアス　オン
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//線の太さや線の末端の形状
            }
            if (i_anti_alias == 0) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//線の太さや線の末端の形状
            }


            //棒を描く
            int Mid_min, Mid_max;
            int Men_jyunban_min, Men_jyunban_max;
            for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {
                s_ob.set(Smen_zu.getmaex(ib), Smen_zu.getmaey(ib), Smen_zu.getatox(ib), Smen_zu.getatoy(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.getax()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
            }

        }

    }


    //---------------------------------------------------------

    public void oekaki_oriagarizu_with_camera(Graphics g, Tenkaizu_Syokunin orite, Tensyuugou otta_Men_zu, Tensyuugou Smen_zu) {
        Graphics2D g2 = (Graphics2D) g;
        int omote_ura = 0;
        if (camera.get_camera_kagami() == 1.0) {
            omote_ura = 0;
        }//カメラの鏡設定が1なら、折り上がり図は表表示
        if (camera.get_camera_kagami() == -1.0) {
            omote_ura = 1;
        }//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示
        //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　面を描く");
        OritaOekaki OO = new OritaOekaki();
        Ten t0 = new Ten();
        Ten t1 = new Ten();
        Senbun s_ob = new Senbun();
        Senbun s_tv = new Senbun();
        String text = "";//文字列処理用のクラスのインスタンス化
        ip1 = omote_ura;
        //  System.out.println(Smensuu);

        //面を描く
        int[] x = new int[100];
        int[] y = new int[100];

        double[] xd = new double[100];
        double[] yd = new double[100];


        //if(hyouji_flg==5){//折紙表示---------------------------------------------------------------------------

        //Smenの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        for (int im = 1; im <= Smensuu; im++) { //Smenから上からの指定した番目の面のidを求める。
            s0[im].set_Menid2uekara_kazoeta_iti(jg);//s0[]はSmen_zuから得られるSmenそのもの、jgは上下表Jyougehyouのこと
        }
        //ここまでで、上下表の情報がSmenの各面に入った


        //面を描く-----------------------------------------------------------------------------------------------------
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ

        int Men_jyunban;
        for (int im = 1; im <= Smensuu; im++) {//imは各Smenの番号
            if (s0[im].getMenidsuu() > 0) {//MenidsuuはSmen(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外

                //折り上がり図を描くときのim番目のSmenの色を決める
                Men_jyunban = 1;
                if (omote_ura == 1) {
                    Men_jyunban = s0[im].getMenidsuu();
                }

                //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==1){g.setColor(new Color(255,255,50));}
                //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==0){g.setColor(new Color(233,233,233));}
                if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                    g.setColor(F_color);
                }
                if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                    g.setColor(B_color);
                }


                if (omote_ura == 1) {
                    //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==0){g.setColor(new Color(255,255,50));}
                    //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==1){g.setColor(new Color(233,233,233));}

                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 0) {
                        g.setColor(F_color);
                    }
                    if (orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban)) % 2 == 1) {
                        g.setColor(B_color);
                    }
                }


                //折り上がり図を描くときのSmenの色を決めるのはここまで

                //折り上がり図を描くときのim番目のSmenの多角形の頂点の座標（PC表示上）を求める

                for (int i = 1; i <= Smen_zu.getTenidsuu(im) - 1; i++) {

                    t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, i)));
                    t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getx());
                    y[i] = gy(t1.gety());
                    //x[i]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,i)));
                    //y[i]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,i)));
                }

                t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im, Smen_zu.getTenidsuu(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getx());
                y[0] = gy(t1.gety());
                //x[0]=gx(Smen_zu.getTenx(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
                //y[0]=gy(Smen_zu.getTeny(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));

                //折り上がり図を描くときのim番目のSmenの多角形の頂点の座標（PC表示上）を求めるのはここまで

                g2.fill(new Polygon(x, y, Smen_zu.getTenidsuu(im)));
                //g.fillPolygon(x,y,Smen_zu.getTenidsuu(im));
            }
        }
        //面を描く　ここまで

        //g.setColor(new Color(0,0,0));		//黒
        //g.setColor(new Color(255,255,255));	//白


        //影をつける ------------------------------------------------------------------------------------
        if (i_kage == 1) {

            for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {
                int im = bou_no_bangou_kara_kagenoaru_Smen_no_bangou_wo_motomeru(ib, Smen_zu, omote_ura);//影をつけるSmenのid
                if (im != 0) {//影を描く。

                    //折り上がり図を描くときのim番目のSmenの多角形の頂点の座標（PC表示上）を求める
		   /*
				for (int i=1; i<=Smen_zu.getTenidsuu(im)-1; i++ ){

					t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im,i)));
					t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im,i)));
					t1.set(camera.object2TV(t0));
					x[i]=gx(t1.getx());
					y[i]=gy(t1.gety());
				}

				t0.setx(Smen_zu.getTenx(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
				t0.sety(Smen_zu.getTeny(Smen_zu.getTenid(im,Smen_zu.getTenidsuu(im))));
				t1.set(camera.object2TV(t0));
				x[0]=gx(t1.getx());
				y[0]=gy(t1.gety());

				//折り上がり図を描くときのim番目のSmenの多角形の頂点の座標（PC表示上）を求めるのはここまで
*/


                    //棒の座標   Smen_zu.getmaex(ib),Smen_zu.getmaey(ib)   -    Smen_zu.getatox(ib) , Smen_zu.getatoy(ib)
                    Ten b_mae = new Ten(Smen_zu.getmaex(ib), Smen_zu.getmaey(ib));
                    Ten b_ato = new Ten(Smen_zu.getatox(ib), Smen_zu.getatoy(ib));
                    double b_nagasa = b_mae.kyori(b_ato);

                    //棒と直交するベクトル
                    double o_btx = -(Smen_zu.getmaey(ib) - Smen_zu.getatoy(ib)) * 10.0 / b_nagasa;//棒と直交するxベクトル
                    double o_bty = (Smen_zu.getmaex(ib) - Smen_zu.getatox(ib)) * 10.0 / b_nagasa;//棒と直交するyベクトル

                    //棒の中点
                    double o_bmx, o_bmy;
                    double t_bmx, t_bmy;

                    o_bmx = (Smen_zu.getmaex(ib) + Smen_zu.getatox(ib)) / 2.0;
                    o_bmy = (Smen_zu.getmaey(ib) + Smen_zu.getatoy(ib)) / 2.0;

                    t0.setx(o_bmx);
                    t0.sety(o_bmy);
                    t1.set(camera.object2TV(t0));
                    t_bmx = t1.getx();
                    t_bmy = t1.gety();

                    //棒の中点を通る直交線上の点
                    double o_bmtx, o_bmty;
                    double t_bmtx, t_bmty;


                    //棒の中点を通る直交線上の点
                    o_bmtx = o_bmx + o_btx;
                    o_bmty = o_bmy + o_bty;

                    if (Smen_zu.naibu(new Ten(o_bmx + 0.01 * o_btx, o_bmy + 0.01 * o_bty), im) != 0) {//0=外部、　1=境界、　2=内部


                        t0.setx(o_bmtx);
                        t0.sety(o_bmty);
                        t1.set(camera.object2TV(t0));
                        t_bmtx = t1.getx();
                        t_bmty = t1.gety();


                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setx(Smen_zu.getmaex(ib));
                        t0.sety(Smen_zu.getmaey(ib));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getx();
                        yd[0] = t1.gety();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setx(Smen_zu.getmaex(ib) + o_btx);
                        t0.sety(Smen_zu.getmaey(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getx();
                        yd[1] = t1.gety();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setx(Smen_zu.getatox(ib) + o_btx);
                        t0.sety(Smen_zu.getatoy(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getx();
                        yd[2] = t1.gety();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setx(Smen_zu.getatox(ib));
                        t0.sety(Smen_zu.getatoy(ib));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getx();
                        yd[3] = t1.gety();
                        x[3] = (int) xd[3];
                        y[3] = (int) yd[3];

                        g2.setPaint(new GradientPaint((float) t_bmx, (float) t_bmy, new Color(0, 0, 0, 50), (float) t_bmtx, (float) t_bmty, new Color(0, 0, 0, 0)));
                        //g2.setPaint( new GradientPaint( (float)xd[0], (float)yd[0], new Color(0,0,0,50),     (float)xd[1], (float)yd[1],  new Color(0,0,0,0)  ));


                        g2.fill(new Polygon(x, y, 4));

                    }


                    //----------------------------------棒と直交するxベクトルの向きを変えて影を描画
                    o_btx = -o_btx;//棒と直交するxベクトル
                    o_bty = -o_bty;//棒と直交するyベクトル


                    //-----------------------------------------------
                    //棒の中点を通る直交線上の点
                    o_bmtx = o_bmx + o_btx;
                    o_bmty = o_bmy + o_bty;


                    if (Smen_zu.naibu(new Ten(o_bmx + 0.01 * o_btx, o_bmy + 0.01 * o_bty), im) != 0) {//0=外部、　1=境界、　2=内部

                        t0.setx(o_bmtx);
                        t0.sety(o_bmty);
                        t1.set(camera.object2TV(t0));
                        t_bmtx = t1.getx();
                        t_bmty = t1.gety();


                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setx(Smen_zu.getmaex(ib));
                        t0.sety(Smen_zu.getmaey(ib));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getx();
                        yd[0] = t1.gety();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setx(Smen_zu.getmaex(ib) + o_btx);
                        t0.sety(Smen_zu.getmaey(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getx();
                        yd[1] = t1.gety();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setx(Smen_zu.getatox(ib) + o_btx);
                        t0.sety(Smen_zu.getatoy(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getx();
                        yd[2] = t1.gety();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setx(Smen_zu.getatox(ib));
                        t0.sety(Smen_zu.getatoy(ib));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getx();
                        yd[3] = t1.gety();
                        x[3] = (int) xd[3];
                        y[3] = (int) yd[3];


                        //g2.setPaint( new GradientPaint( (float)t_bmx, (float)t_bmy, new Color(0,0,0,50),     (float)t_bmtx, (float)t_bmty,  new Color(0,0,0,0)  ));
                        g2.setPaint(new GradientPaint((float) xd[0], (float) yd[0], new Color(0, 0, 0, 50), (float) xd[1], (float) yd[1], new Color(0, 0, 0, 0)));
                        g2.fill(new Polygon(x, y, 4));
                    }

//-------------------------------------------------------


                }
            }
        }//影をつけるは、ここで終わり

        //棒を描く-----------------------------------------------------------------------------------------


        if (i_anti_alias == 1) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//アンチェイリアス　オン
            BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        }
        if (i_anti_alias == 0) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
            BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        }

        g.setColor(L_color);//g.setColor(Color.black);


        //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　棒を描く");


        for (int ib = 1; ib <= Smen_zu.getBousuu(); ib++) {

            int Mid_min, Mid_max; //棒の両側のSmenの番号の小さいほうがMid_min,　大きいほうがMid_max
            int Men_jyunban_min, Men_jyunban_max;//PC画面に表示したときSmen(Mid_min) で見える面の番号がMen_jyunban_min、Smen(Mid_max) で見える面の番号がMen_jyunban_max
            int oekaki_flg;

            oekaki_flg = 0;
            Mid_min = Smen_zu.Bou_moti_Menid_min_motome(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            Mid_max = Smen_zu.Bou_moti_Menid_max_motome(ib);

            if (s0[Mid_min].getMenidsuu() == 0) {
                oekaki_flg = 1;
            }//menをもたない、ドーナツの穴状のSmenは境界の棒を描く
            else if (s0[Mid_max].getMenidsuu() == 0) {
                oekaki_flg = 1;
            } else if (Mid_min == Mid_max) {
                oekaki_flg = 1;
            }//一本の棒の片面だけにSmen有り
            else {
                Men_jyunban_min = 1;
                if (omote_ura == 1) {
                    Men_jyunban_min = s0[Mid_min].getMenidsuu();
                }
                Men_jyunban_max = 1;
                if (omote_ura == 1) {
                    Men_jyunban_max = s0[Mid_max].getMenidsuu();
                }
                if (s0[Mid_min].uekara_kazoeta_Menid(Men_jyunban_min) != s0[Mid_max].uekara_kazoeta_Menid(Men_jyunban_max)) {
                    oekaki_flg = 1;
                }//この棒で隣接するSmenの1番上の面は異なるので、この棒は描く。
            }

            if (oekaki_flg == 1) {//棒を描く。
                s_ob.set(Smen_zu.getmaex(ib), Smen_zu.getmaey(ib), Smen_zu.getatox(ib), Smen_zu.getatoy(ib));
                s_tv.set(camera.object2TV(s_ob));
                //g2.draw(new Line2D.Double( gx(s_tv.getax()),gy(s_tv.getay()),gx(s_tv.getbx()),gy(s_tv.getby())));
                g.drawLine(gx(s_tv.getax()), gy(s_tv.getay()), gx(s_tv.getbx()), gy(s_tv.getby())); //直線
                //g.drawLine( gx(Smen_zu.getmaex(ib)),gy(Smen_zu.getmaey(ib)),gx(Smen_zu.getatox(ib)),gy(Smen_zu.getatoy(ib))); //直線
            }


        }

        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ


        //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　終了");
        //}


        //camera中心を十字で描く
        //OO.jyuuji(g,camera.object2TV(camera.get_camera_ichi()), 5.0 , 2.0 , 4);
    }


    //---------------------------------------------------------
    public void oekaki_jyuuji_with_camera(Graphics g) {
        //System.out.println("折り上がり図の動かし中心の十字表示");
        //System.out.println("上下表職人　oekaki_jyuuji_with_camera+++++++++++++++折り上がり図の動かし中心の十字表示");
        OritaOekaki OO = new OritaOekaki();

        //camera中心を十字で描く
        OO.jyuuji(g, camera.object2TV(camera.get_camera_ichi()), 5.0, 2.0, 4);
    }


    //---------------------------------------------------------

    public void change_i_kage() {

        i_kage = i_kage + 1;
        if (i_kage >= 2) {
            i_kage = 0;
        }

    }


    //---------------------------------------------------------

    public int bou_no_bangou_kara_kagenoaru_Smen_no_bangou_wo_motomeru(int ib, Tensyuugou Smen_zu, int omote_ura) {//棒の番号から、その棒の影が発生するSmen の番号を求める。影が発生しない場合は0を返す。

        int i_return = 0;

        int Mid_min, Mid_max; //棒の両側のSmenの番号の小さいほうがMid_min,　大きいほうがMid_max
        int Men_jyunban_min, Men_jyunban_max;//PC画面に表示したときSmen(Mid_min) で見える面の、そのSmenでの重なり順がMen_jyunban_min、Smen(Mid_max) で見える面のそのSmenでの重なり順がMen_jyunban_max

        Mid_min = Smen_zu.Bou_moti_Menid_min_motome(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
        Mid_max = Smen_zu.Bou_moti_Menid_max_motome(ib);

        if (s0[Mid_min].getMenidsuu() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のSmenとの境界の棒には影なし
        if (s0[Mid_max].getMenidsuu() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のSmenとの境界の棒には影なし
        if (Mid_min == Mid_max) {
            return 0;
        }//一本の棒の片面だけにSmen有り

        Men_jyunban_min = 1;
        if (omote_ura == 1) {
            Men_jyunban_min = s0[Mid_min].getMenidsuu();
        }
        Men_jyunban_max = 1;
        if (omote_ura == 1) {
            Men_jyunban_max = s0[Mid_max].getMenidsuu();
        }

        int Mid_min_mieteru_men_id = s0[Mid_min].uekara_kazoeta_Menid(Men_jyunban_min);
        int Mid_max_mieteru_men_id = s0[Mid_max].uekara_kazoeta_Menid(Men_jyunban_max);
        if (Mid_min_mieteru_men_id == Mid_max_mieteru_men_id) {
            return 0;
        }//この棒で隣接するSmenで見えてる面が同じなので、棒自体が描かれず影もなし。


        //Jyougehyou
        //public int get(int i,int j){return jg[i][j];}
        //　jg[i][j]が1なら面iは面jの上側。0なら下側。
        //  jg[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
        //jg[i][j]が-100なら、面iとjは重なるところがない。
        if (jg.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == -50) {
            return 0;
        }//この棒で隣接するSmenで見えてる面の上下関係不明なので、影はなし
        if (jg.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == -100) {
            return 0;
        }//この棒で隣接するSmenで見えてる面の上下関係ない（重ならない）ので、影はなし

        i_return = Mid_min;
        if (jg.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == 1) {
            i_return = Mid_max;
        }

        if (omote_ura == 1) {
            if (i_return == Mid_min) {
                return Mid_max;
            } else if (i_return == Mid_max) {
                return Mid_min;
            }
        }

        return i_return;
    }

// -----------------------------

    private void rating() {
        int jgms = jg.getMensuu();
        Random rand = new Random();
        double dr = 0.0;
        double[] men_r = new double[jgms + 1];
        men_rating = men_r;

        for (int im = 1; im <= jgms; im++) {
            men_rating[im] = 10000.0;
        }

/*
		//------------------------------------------------------------------------------
		double katen=100.0;//加点
		for (int ism=1; ism<=Smensuu; ism++ ){
			int Mensuu=	s0[ism].getMenidsuu();//Smenでの面数
			for (int jyunban=1; jyunban<=Mensuu; jyunban++ ){
				int im=	s0[ism].uekara_kazoeta_Menid(jyunban);
				men_rating[im]=men_rating[im]+(double)(Mensuu-2*jyunban+1)*katen;
			}
		}
		//------------------------------------------------------------------------------
*/


        for (int j = 1; j <= 100; j++) {

            //------------------------------------------------------------

            for (int i = 1; i <= 10; i++) {
                for (int ia = 1; ia <= jg.getMensuu(); ia++) {
                    for (int ib = 1; ib <= jg.getMensuu(); ib++) {
                        //ia=rand.nextInt(jgms)+1 ;
                        //ib=rand.nextInt(jgms)+1 ;
                        if (ia != ib) {
                            if (jg.get(ia, ib) == 1) {//wikiのイロレーティングの将棋クラブ24参照
                                dr = 12.0 + (men_rating[ib] - men_rating[ia]) * 0.04;
                                if (dr < 1.0) {
                                    dr = 1.0;
                                }
                                if (dr > 31.0) {
                                    dr = 31.0;
                                }
                                men_rating[ia] = men_rating[ia] + dr;
                                men_rating[ib] = men_rating[ib] - dr;
                            } else if (jg.get(ia, ib) == 0) {
                                dr = 12.0 + (men_rating[ia] - men_rating[ib]) * 0.04;
                                if (dr < 1.0) {
                                    dr = 1.0;
                                }
                                if (dr > 31.0) {
                                    dr = 31.0;
                                }
                                men_rating[ia] = men_rating[ia] - dr;
                                men_rating[ib] = men_rating[ib] + dr;
                            }
                        }
                    }
                }
            }

/*			//------------------------------------------------------------

			for (int i=1; i<=10*jgms*jgms; i++ ){
				int ia;int ib;
			//for (int iaa=1; iaa<=jg.getMensuu(); iaa++ ){


				//for (int ibb=1; ibb<=jg.getMensuu(); ibb++ ){
					ia=rand.nextInt(jgms)+1 ;
					ib=rand.nextInt(jgms)+1 ;

					if(ia != ib){
						if(jg.get(ia,ib)==1){//wikiのイロレーティングの将棋クラブ24参照
							dr=12.0+( men_rating[ib]-men_rating[ia]   )*0.04;
							if(dr<1.0){dr=1.0;}
							if(dr>31.0){dr=31.0;}
							men_rating[ia]=men_rating[ia]+dr;
							men_rating[ib]=men_rating[ib]-dr;
						}else if(jg.get(ia,ib)==0){
							dr=12.0+( men_rating[ia]-men_rating[ib]   )*0.04;
							if(dr<1.0){dr=1.0;}
							if(dr>31.0){dr=31.0;}
							men_rating[ia]=men_rating[ia]-dr;
							men_rating[ib]=men_rating[ib]+dr;
						}


					}
				//}

			//}
			}
*/
            //------------------------------------------------------------


        }


        //System.out.println("jg.getMensuu() = "+jg.getMensuu());

        nbox.reset();
        for (int i = 1; i <= jg.getMensuu(); i++) {
            nbox.ire_i_tiisaijyun(new int_double(i, men_rating[i]));
        }
        //System.out.println("jg.getMensuu() = "+jg.getMensuu());
        //for (int i=1; i<=10; i++ ){nbox.ire_i_tiisaijyun(new int_double( 1  , 3.0 ));	}
    }


//-----------------------------------

    int makesuu0no_menno_kazu = 0;//上に他の面がない状態で順位付けできる面の数
    int makesuu1ijyouno_menno_kazu = 0;//上に他の面が1以上ある状態でないと順位付けできない面の数

    private void rating2() {
        int jgms = jg.getMensuu();//面の総数を求める。
//System.out.println("*********  rating2()   *************");
        double[] men_r = new double[jgms + 1];
        men_rating = men_r;

        int[] i_m_r = new int[jgms + 1];//各面の整数レーティング（rating2()で使う））
        i_men_rating = i_m_r;


        makesuu0no_menno_kazu = 0;//上に他の面がない状態で順位付けできる面の数
        makesuu1ijyouno_menno_kazu = 0;//上に他の面が1以上ある状態でないと順位付けできない面の数


        for (int i = 0; i <= jgms; i++) {
            i_men_rating[i] = 0;
        }


//s面の１から順に一番上にある面を求める（レートがついた面は除く）。
//s面の１から順にその面の上にある面数（レートがついた面は除く）を求め、総計を求める
//その面の上のある面数（レートがついた面は除く）が最小の面を求めレートをつける


        for (int i = 1; i <= jgms; i++) {
            int i_rate = 1 + jgms - i;


            int top_men_id = get_top_men_id_without_rated_men();

//System.out.println("*********  rating2()   rating = "+i_rate+"   top_men_id = "+top_men_id);

            i_men_rating[top_men_id] = i_rate;
            men_rating[top_men_id] = i_rate;
        }

//System.out.println("*********  rating2()   end *************");


        System.out.println("上に他の面がない状態で順位付けできた面の数 = " + makesuu0no_menno_kazu);
        System.out.println("上に他の面が1以上ある状態で順位付けした面の数 = " + makesuu1ijyouno_menno_kazu);











/*

		Random rand = new Random();
		double dr=0.0;
		double men_r[]= new double[jgms+1]; ;
		men_rating=men_r;

		for (int im=1; im<=jgms; im++ ){ men_rating[im]=10000.0;}
*/
/*
		//------------------------------------------------------------------------------
		double katen=100.0;//加点
		for (int ism=1; ism<=Smensuu; ism++ ){
			int Mensuu=	s0[ism].getMenidsuu();//Smenでの面数
			for (int jyunban=1; jyunban<=Mensuu; jyunban++ ){
				int im=	s0[ism].uekara_kazoeta_Menid(jyunban);
				men_rating[im]=men_rating[im]+(double)(Mensuu-2*jyunban+1)*katen;
			}
		}
		//------------------------------------------------------------------------------
*/

/*
		for (int j=1; j<=100; j++ ){

			//------------------------------------------------------------

			for (int i=1; i<=10; i++ ){
				for (int ia=1; ia<=jg.getMensuu(); ia++ ){
					for (int ib=1; ib<=jg.getMensuu(); ib++ ){
						//ia=rand.nextInt(jgms)+1 ;
						//ib=rand.nextInt(jgms)+1 ;
						if(ia != ib){
							if(jg.get(ia,ib)==1){//wikiのイロレーティングの将棋クラブ24参照
								dr=12.0+( men_rating[ib]-men_rating[ia]   )*0.04;
								if(dr<1.0){dr=1.0;}
								if(dr>31.0){dr=31.0;}
								men_rating[ia]=men_rating[ia]+dr;
								men_rating[ib]=men_rating[ib]-dr;
							}else if(jg.get(ia,ib)==0){
								dr=12.0+( men_rating[ia]-men_rating[ib]   )*0.04;
								if(dr<1.0){dr=1.0;}
								if(dr>31.0){dr=31.0;}
								men_rating[ia]=men_rating[ia]-dr;
								men_rating[ib]=men_rating[ib]+dr;
							}
						}
					}
				}
			}
*/
/*			//------------------------------------------------------------

			for (int i=1; i<=10*jgms*jgms; i++ ){
				int ia;int ib;
			//for (int iaa=1; iaa<=jg.getMensuu(); iaa++ ){


				//for (int ibb=1; ibb<=jg.getMensuu(); ibb++ ){
					ia=rand.nextInt(jgms)+1 ;
					ib=rand.nextInt(jgms)+1 ;

					if(ia != ib){
						if(jg.get(ia,ib)==1){//wikiのイロレーティングの将棋クラブ24参照
							dr=12.0+( men_rating[ib]-men_rating[ia]   )*0.04;
							if(dr<1.0){dr=1.0;}
							if(dr>31.0){dr=31.0;}
							men_rating[ia]=men_rating[ia]+dr;
							men_rating[ib]=men_rating[ib]-dr;
						}else if(jg.get(ia,ib)==0){
							dr=12.0+( men_rating[ia]-men_rating[ib]   )*0.04;
							if(dr<1.0){dr=1.0;}
							if(dr>31.0){dr=31.0;}
							men_rating[ia]=men_rating[ia]-dr;
							men_rating[ib]=men_rating[ib]+dr;
						}


					}
				//}

			//}
			}
*/
        //------------------------------------------------------------
/*

		}


*/
        //System.out.println("jg.getMensuu() = "+jg.getMensuu());

        nbox.reset();
        for (int i = 1; i <= jg.getMensuu(); i++) {
            nbox.ire_i_tiisaijyun(new int_double(i, men_rating[i]));
        }
        //System.out.println("jg.getMensuu() = "+jg.getMensuu());
        //for (int i=1; i<=10; i++ ){nbox.ire_i_tiisaijyun(new int_double( 1  , 3.0 ));	}

    }
//------------------------------------------------------------
//以下の各関数ではSmenとしてs0[]を使う20180305


    private int top_men_id_ga_maketa_kazu_goukei_without_rated_men = 0;

    //------------------------------------------------------------
    private int get_top_men_id_without_rated_men() {
//System.out.println("20180307*********   get_top_men_id_without_rated_men()   *************");
        int top_men_id = 0;
        top_men_id_ga_maketa_kazu_goukei_without_rated_men = jg.getMensuu() + 100;


        int jgms = jg.getMensuu();//面の総数を求める。

        int[] i_kentouzumi = new int[jgms + 1];//検討済みの面IDは１にする
        for (int i = 0; i <= jgms; i++) {
            i_kentouzumi[i] = 0;
        }

        for (int i = 1; i <= Smensuu; i++) {
            int s_top_id = get_s_top_id_without_rated_men(i);//各s面の（レートがついた面は除く）一番上の面。s_top_id=0ならそのs面にはレートが未定の面はない
            //System.out.println("*********   get_top_men_id_without_rated_men()   Smen ID = "+I+"  s_top_id = "+s_top_id+  "   負け数 = "+get_maketa_kazu_goukei_without_rated_men(s_top_id));


            if (s_top_id != 0) {
                if (i_kentouzumi[s_top_id] == 0) {
                    //int makesuu=get_maketa_kazu_goukei_without_rated_men(s_top_id);
                    //System.out.println("*********     Smen ID = "+i+"  s_top_id = "+s_top_id+  "   負け数 = "+get_maketa_kazu_goukei_without_rated_men(s_top_id));


                    int mkg = get_maketa_kazu_goukei_without_rated_men(s_top_id);
                    if (mkg == 0) {
                        makesuu0no_menno_kazu = makesuu0no_menno_kazu + 1;
                        return s_top_id;
                    }//ここは、これでよいか要検討20180306
                    //if(top_men_id_ga_maketa_kazu_goukei_without_rated_men>get_maketa_kazu_goukei_without_rated_men(s_top_id)){
                    if (top_men_id_ga_maketa_kazu_goukei_without_rated_men > mkg) {
                        //if()
                        //top_men_id_ga_maketa_kazu_goukei_without_rated_men=get_maketa_kazu_goukei_without_rated_men(s_top_id);
                        top_men_id_ga_maketa_kazu_goukei_without_rated_men = mkg;
                        top_men_id = s_top_id;
                    }
                }
            }

            i_kentouzumi[s_top_id] = 1;
        }

//System.out.println("*********   get_top_men_id_without_rated_men()   end *************");
        //makesuu0no_menno_kazu=0;//上に他の面がない状態で順位付けできる面の数
        //makesuu1ijyouno_menno_kazu=0;//上に他の面が1以上ある状態でないと順位付けできない面の数
        if (top_men_id_ga_maketa_kazu_goukei_without_rated_men == 0) {
            makesuu0no_menno_kazu = makesuu0no_menno_kazu + 1;
        } else if (top_men_id_ga_maketa_kazu_goukei_without_rated_men > 0) {
            makesuu1ijyouno_menno_kazu = makesuu1ijyouno_menno_kazu + 1;
        }


//System.out.println("*上に他の面がない状態で順位付けできた面の数 = "+makesuu0no_menno_kazu);
//System.out.println("*上に他の面が1以上ある状態で順位付けした面の数 = "+makesuu1ijyouno_menno_kazu);


        return top_men_id;
    }

//-----------------------------------

    private int get_s_top_id_without_rated_men(int ism) {//ismはs面のid
//System.out.println("*********   get_s_top_id_without_rated_men   *************");
        int Mensuu = s0[ism].getMenidsuu();//Smenでの面数//Smen s0[];//Smen_zuから得られるSmen
        for (int jyunban = 1; jyunban <= Mensuu; jyunban++) {
            int im = s0[ism].uekara_kazoeta_Menid(jyunban);
            if (i_men_rating[im] == 0) {
                return im;
            }
        }
        return 0;
    }

// -----------------------------

    private int get_maketa_kazu_goukei_without_rated_men(int men_id) {
//System.out.println("*********   get_maketa_kazu_goukei_without_rated_men   *************");
        int i_make = 0;
        for (int ism = 1; ism <= Smensuu; ism++) {
            i_make = i_make + get_smen_de_maketa_kazu_without_rated_men(ism, men_id);
            if (i_make >= top_men_id_ga_maketa_kazu_goukei_without_rated_men) {
                return i_make;
            }//20180306高速化のためこの１行を入れているが、本当に効果があるかは不明。この行だけコメントアウトしても正常には動くはず。

        }
//System.out.println("*********   get_maketa_kazu_goukei_without_rated_men   end *************");
        return i_make;
    }


    //-----------------------------------
    private int get_smen_de_maketa_kazu_without_rated_men(int ism, int men_id) {//ismはsmenのid
//System.out.println("*********   get_smen_de_maketa_kazu_without_rated_men   *************");
        int Mensuu = s0[ism].getMenidsuu();//Smenでの面数//Smen s0[];//Smen_zuから得られるSmen
        int maketa_kazu = 0;
//System.out.println("*********   get_smen_de_maketa_kazu_without_rated_men   ********Mensuu = "+Mensuu);
//System.out.println("*********   get_smen_de_maketa_kazu_without_rated_men   ********men_id = "+men_id);

        for (int i = 1; i <= Mensuu; i++) {
            int im = s0[ism].uekara_kazoeta_Menid(i);
            if (im == men_id) {
                return maketa_kazu;
            }
            if (i_men_rating[im] == 0) {
                maketa_kazu = maketa_kazu + 1;
            }
        }
        return 0;
    }


//-----------------------------------


}     



