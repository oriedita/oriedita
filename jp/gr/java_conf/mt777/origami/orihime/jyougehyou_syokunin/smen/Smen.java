package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen;

import jp.gr.java_conf.mt777.origami.orihime.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_jyunretu_hasseiki.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken.*;

public class Smen {//このクラスは展開図をを折り畳み推定してえられた針金図の面の重なり情報を
    //記録活用するために使う。Jyougehyou_Syokuninクラスの中でのみ使う
    int Menidsuu;//Smen(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。
    int[] Menid;//S面に含まれるMenのid番号を記録する。これが20ということは、
    //折った後の紙の重なりが最大の場所でも20-1=19面までということを意味する。//この制限は現在は無し20150309
    //Jyunretu_hasseiki jh = new Jyunretu_hasseiki();
    Jyuufuku_Jyunretu_hasseiki jh;// = new Jyuufuku_Jyunretu_hasseiki();


    int Jyunretu_count = 1;


    //Annaisyo ann = new Annaisyo();

    int[] Menid2uekara_kazoeta_iti;//面(Menid)の上から数えた位置を表す。
    int[] uekara_kazoeta_iti2Menid;//上から数えた位置の面を表す。

    App orihime_app;

    public Smen() {
        reset();
    }  //コンストラクタ

    public Smen(App app0) {
        orihime_app = app0;
        reset();
    }  //コンストラクタ


    private void reset() {
        Menidsuu = 0;
    }

    //--------------------------------------
    //Ketasuuと順列発生機の初期設定。忘れずにすること。
    public void setKetasuu(int Midsuu) {
        Menidsuu = Midsuu;

        int[] Men_id = new int[Menidsuu + 1];
        Menid = Men_id;

        int[] Menid_2_uekara_kazoeta_iti = new int[Menidsuu + 1];  //面(Menid)の上から数えた位置を表す。
        int[] uekara_kazoeta_iti_2_Menid = new int[Menidsuu + 1];  //上から数えた位置の面を表す。
        Menid2uekara_kazoeta_iti = Menid_2_uekara_kazoeta_iti;
        uekara_kazoeta_iti2Menid = uekara_kazoeta_iti_2_Menid;

        for (int i = 0; i <= Menidsuu; i++) {
            Menid[i] = 0;

            Menid2uekara_kazoeta_iti[i] = 0;
            uekara_kazoeta_iti2Menid[i] = 0;
        }
        if (Midsuu > 0) {
            //System.out.println("20150309@@@@@@@2222222222222222222@@@@@@@@@" );
            jh = new Jyuufuku_Jyunretu_hasseiki(Menidsuu);
            //System.out.println("20150309@@@@@@@333333333333333333@@@@@@@@@" );
            Jyunretu_1banme();

        }
    }

    //--------------------------------------
    public int getMenidsuu() {
        return Menidsuu;
    }

    //--------------------------------------
    public int getMenid(int i) {
        return Menid[i];
    }

//--------------------------------------

    public void setMenid(int i, int Mid) {
        Menid[i] = Mid;
    }

    // ここは　class Smen の中だよ。
 /*      //Smenの位置にMenが有るなら1、無いなら0を返す。
	int Men_no_ari_nasi(int id){
		for(int i=1;i<=Menidsuu;i++){
			if(id==Menid[i]){return 1;}
		}
		return 0;
	}
   */

    //--------------------------------------
    public int get_Jyunretu_count() {
        return Jyunretu_count;
    }

//--------------------------------------


    public void Jyunretu_1banme() {


        if (getMenidsuu() > 0) {//System.out.println("20150309@@@@@@@2222222222222222222@@@@@@@@@" );
            jh.Jyunretu_1banme();
            Jyunretu_count = 1;
        }//System.out.println("20150309@@@@@@@333333333333333333@@@@@@@@@" );

    } //順列の1番目にもどる。


    //--------------------------------------
    public int susumu() {
        return 0;
    }

/*
        //順列発生機を進め、面の重なり状態を次の状態にする。通常は0をreturnする。
	//もし現在の面の重なり状態が、最後のものだったら1をreturnして、面の重なり状態は最初のものに戻る。
	int susumu(Jyougehyou jg){
		return jh.susumu();
	}   //<<<<<<<<<<<<<<<<<<<ここは後で機能を強化して高速化したい。
*/

    //k桁目の順列発生機を進め、面の重なり状態を次の状態にする。通常は0をreturnする。
    //もし現在の面の重なり状態が、最後のものだったら1をreturnする。
    //この場合は面の重なり状態は最後のもののまま。
    public int susumu(int k) {
        Jyunretu_count = Jyunretu_count + 1;
        return jh.susumu(k);
    }   //<<<<<<<<<<<<<<<<<<<ここは後で機能を強化して高速化したい。
    // ここは　class Smen の中だよ。

    //現在の順列状態から開始して、可能な重なりかたとなる順列を探す
    public int kanou_kasanari_sagasi(Jyougehyou jg) {//これはjgを変えないはず。
        int mk, ijh;
        mk = 0;
        ijh = 1;//ijhの初期値は0以外ならなんでもいい。
        while (ijh != 0) { //ijh==0なら、桁の最後まできた。
            mk = mujyun_keta_motome(jg);

            //orihime_ap.keijiban.kakikae(8,"Smen_mujyun_keta_motome(jg) =  "+mk);
            if (mk == 1000) {
                return 1000;
            }//このSmenは、矛盾はない状態になっている。

            ijh = susumu(mk);

            //	orihime_ap.keijiban.kakikae(9,"Smen_kanou_kasanari_sagasi(jg) =  "+ijh);

            String s0 = "";
            for (int i = 1; i <= Menidsuu; i++) {
                s0 = s0 + " : " + getJyunretu(i);
            }
            orihime_app.keijiban.kakikae(10, "Smen_kanou_kasanari_sagasi(jg) =  " + s0);


            //try{Thread.sleep(1000);}catch (InterruptedException e){}// 1000ミリ秒待機する。


        }
        return 0;//可能な重なりかたとなる順列は存在しない
    }

    // ここは　class Smen の中だよ。


    /*    //このSmenで重なる面の重なり順序の総数
        int getJyunretusuu(){
		int ir;
		for(int i=1;i<=Menidsuu;i++){
			ir=ir*i;
		}
		return ir;
	}
     */

    // 現在の順列状態をもとに、上から数えてi番めの面のid番号を返す。

    private int get_uekara_kazoeta_itino_Menid(int i) {
        return Menid[getJyunretu(i)];
    }


    //現在の上下表をもとに、上から数えてi番めの面のid番号を格納する。
    public void set_Menid2uekara_kazoeta_iti(Jyougehyou jg) {
        for (int i = 1; i <= Menidsuu; i++) {
            Menid2uekara_kazoeta_iti[i] = 0;
            for (int j = 1; j <= Menidsuu; j++) {
                if (jg.get(Menid[i], Menid[j]) == 1) {
                    Menid2uekara_kazoeta_iti[i] = Menid2uekara_kazoeta_iti[i] + 1;
                }
            }
            Menid2uekara_kazoeta_iti[i] = Menidsuu - Menid2uekara_kazoeta_iti[i];
        }

        for (int iban = 1; iban <= Menidsuu; iban++) {
            for (int i = 1; i <= Menidsuu; i++) {
                if (Menid2uekara_kazoeta_iti[i] == iban) {
                    uekara_kazoeta_iti2Menid[iban] = i;
                }
            }
        }
    }

    //--------------
    public int uekara_kazoeta_Menid(int iban) {
        return Menid[uekara_kazoeta_iti2Menid[iban]];
    }

    //現在の上下表をもとに、上から数えてi番めの面のid番号を返す。上下表は完成したものを使わないと結果がおかしくなる恐れ有り。
    private int get_uekara_kazoeta_itino_Menid(int iban, Jyougehyou jg) {
        set_Menid2uekara_kazoeta_iti(jg);
        return Menid[uekara_kazoeta_iti2Menid[iban]];
     
            /*  
		for(int i=1;i<=Menidsuu;i++){
		    Menid2uekara_kazoeta_iti[i] = 0;
		
		   for(int j=1;j<=Menidsuu;j++){
	              if(jg.get(Menid[i],Menid[j])==1){Menid2uekara_kazoeta_iti[i] = Menid2uekara_kazoeta_iti[i]+1;}
		   }	
                   Menid2uekara_kazoeta_iti[i]=Menidsuu-Menid2uekara_kazoeta_iti[i];
		}

                //
                for(int i=1;i<=Menidsuu;i++){
			if (Menid2uekara_kazoeta_iti[i]==iban){return Menid[i];}
		}



              return 0;//ここはエラーがなければ通ることは無い。
            */

    }

    //
    private int getJyunretu(int i) {
        return jh.getJyunretu(i);
    }

    //一番上の面からチェックしていって何桁目で折り重なりに矛盾が生じるかを求める。
    //この際jgは変化しない。なおここでは隣接面の境界線の突き抜け条件はチェックしていない。
    //このSmenでは折り重なりに矛盾がない場合は1000を返す。
    private int kasanari_mujyun_keta_motome(Jyougehyou jg) {
        for (int i = 1; i <= Menidsuu - 1; i++) {
            for (int j = i + 1; j <= Menidsuu; j++) {
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == 0) {
                    return i;
                }
            }
        }
        return 1000;
    }

    //面imの重なり順が上から何番目かを求める。このSmenにMenが含まれないときは0を返す。
    public int Menid2Jyunretuketa(int im) {
        for (int i = 1; i <= Menidsuu; i++) {
            if (Menid[getJyunretu(i)] == im) {
                return i;
            }
        }
        return 0;
    }


    // ここは　class Smen の中だよ。


    //一番上の面からチェックしていって何番目で隣接面の境界線の突き抜け条件に矛盾が生じるかを求める。
    //この際jgは変化しない。このSmenでは突き抜け条件に矛盾がない場合は1000を返す。
    private int tukinuke_mujyun_keta_motome(Touka_jyouken tj) {
        int mm, M1, M2; //折り畳み推定の際の等価条件の登録は　addTouka_jyouken(im,Mid_min,im,Mid_max);  による
        mm = Menid2Jyunretuketa(tj.geta());
        M1 = Menid2Jyunretuketa(tj.getb());
        M2 = Menid2Jyunretuketa(tj.getd());
        if (mm * M1 * M2 == 0) {
            return 1000;
        }
        //if ((mm-M1)*(mm-M2)<0) {if(mm!=Menidsuu-1) {  return mm;} }   //qqqqqqqqqqqqqq
        //if ((mm-M1)*(mm-M2)<0) {if(mm!=2) {  return mm;} }   //qqqqqqqqqqqqqq

        if ((mm - M1) * (mm - M2) < 0) {
            return mm;
        }
        return 1000;
    }

    //

    private int tukinuke_mujyun_keta_motome(Jyougehyou jg) {
        int ketaMim = 1000;
        int tmk = 1000;
        for (int i = 1; i <= jg.getTouka_jyoukensuu(); i++) {
            tmk = tukinuke_mujyun_keta_motome(jg.getTouka_jyouken(i));

            if (tmk <= ketaMim) {
                ketaMim = tmk;
            }
        }

        // if(ketaMim==Menidsuu-1) {  return 1000;}    <<<<<<<<テスト用

        return ketaMim;

    }


    //一番上の面からチェックしていって何番目で境界線の一部を共有する２面と２面の突き抜け条件に矛盾が生じるかを求める。
    //この際jgは変化しない。このSmenでは矛盾がない場合は1000を返す。
    private int u_tukinuke_mujyun_keta_motome(Touka_jyouken uj) {
        int mi1, mi2, mj1, mj2, itemp; //折り畳み推定の際の等価条件の登録は　u_addTouka_jyouken(im1,im2,im3,im4);  による
        mi1 = Menid2Jyunretuketa(uj.geta());
        mi2 = Menid2Jyunretuketa(uj.getb());
        if (mi2 < mi1) {
            itemp = mi1;
            mi1 = mi2;
            mi2 = itemp;
        }

        mj1 = Menid2Jyunretuketa(uj.getc());
        mj2 = Menid2Jyunretuketa(uj.getd());
        if (mj2 < mj1) {
            itemp = mj1;
            mj1 = mj2;
            mj2 = itemp;
        }

        if (mi1 * mi2 * mj1 * mj2 != 0) {
            if (((mi1 < mj1) && (mj1 < mi2)) && (mi2 < mj2)) {
                //System.out.print(mi1);
                //System.out.print(",");
                //System.out.print(mj1);
                //System.out.print(",aaa,");
                //System.out.print(mi2);
                //System.out.print(",");
                //System.out.println(mj2);
                return mi2;
            }
            if (((mj1 < mi1) && (mi1 < mj2)) && (mj2 < mi2)) {
                //System.out.print(mi1);
                //System.out.print(",");
                //System.out.print(mj1);
                //System.out.print(",bbb,");
                //System.out.print(mi2);
                //System.out.print(",");
                //System.out.println(mj2);
                return mj2;
            }
        }

        return 1000;
    }

    //

    private int u_tukinuke_mujyun_keta_motome(Jyougehyou jg) {
        int ketaMim = 1000;
        int tmk = 1000;
        for (int i = 1; i <= jg.get_uTouka_jyoukensuu(); i++) {
            tmk = u_tukinuke_mujyun_keta_motome(jg.get_uTouka_jyouken(i));

            if (tmk <= ketaMim) {
                ketaMim = tmk;
            }
        }

        // if(ketaMim==Menidsuu-1) {  return 1000;}    <<<<<<<<テスト用

        return ketaMim;


    }


    // ここは　class Smen の中だよ。


    //一番上の面からチェックしていって何番目で矛盾が生じるかを求める。
    //この際jgは変化しない。このSmenでは矛盾がない場合は1000を返す。
    private int mujyun_keta_motome(Jyougehyou jg) {
        int min1, min2, min3;
        min1 = kasanari_mujyun_keta_motome(jg);
        min2 = tukinuke_mujyun_keta_motome(jg);
        min3 = u_tukinuke_mujyun_keta_motome(jg);

        //min1,min2,min3の中で最小値をリターンする。
        if (min3 < min2) {
            min2 = min3;
        }
        if (min2 < min1) {
            min1 = min2;
        }

        return min1;

    }


    //上下表にSmenの面の重なりによる情報を入れる
    public void jg_ni_Smen_wo_nyuuryoku(Jyougehyou jg) {

        for (int i = 1; i <= Menidsuu; i++) {
            for (int j = 1; j <= i - 1; j++) {
                jg.set(Menid[getJyunretu(i)], Menid[getJyunretu(j)], 0);
            }

            for (int j = i + 1; j <= Menidsuu; j++) {
                jg.set(Menid[getJyunretu(i)], Menid[getJyunretu(j)], 1);
            }
        }
    }


    //上下表にSmenの面の重なりによる情報を入れる。これは初期の計算準備の際にSmenの有効数を求めるのに使う。
    public void jg_ni_Smen_no_tantoubasyo_wo_nyuuryoku(Jyougehyou jg) {

        for (int i = 1; i <= Menidsuu; i++) {
            for (int j = 1; j <= i - 1; j++) {
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == -100) {
                    jg.set(Menid[getJyunretu(i)], Menid[getJyunretu(j)], -50);
                }
            }

            for (int j = i + 1; j <= Menidsuu; j++) {
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == -100) {
                    jg.set(Menid[getJyunretu(i)], Menid[getJyunretu(j)], -50);
                }
            }
        }
    }

    //上下表にSmenによって何個の新情報が入るかを返す。
    public int sinki_jyouhou_suu(Jyougehyou jg) {
        int inew = 0;
        for (int i = 1; i <= Menidsuu; i++) {
            for (int j = 1; j <= i - 1; j++) {
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == -100) {
                    inew = inew + 1;
                }
            }

            for (int j = i + 1; j <= Menidsuu; j++) {
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == -100) {
                    inew = inew + 1;
                }
            }
        }
        //
        return inew;
    }
    //


    //  jg[][]は折る前の展開図のすべての面同士の上下関係を1つの表にまとめたものとして扱う
    //　jg[i][j]が1なら面iは面jの上側。0なら下側。
    //  jg[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
    //jg[i][j]が-100なら、面iとjは重なるところがない。


    public void setAnnaisyo(Jyougehyou jg) { //重複順列発生機の案内書をSmenで準備してやる。
        int[] ueMenid = new int[Menidsuu + 1];
        int[] ueMenidFlg = new int[Menidsuu + 1];//ueMenid[]が有効なら1、無効なら0

        for (int iMen = 1; iMen <= Menidsuu; iMen++) {
            int ueMenidsuu = 0;//ueMenid[]が、1からいくつまであるかを格納。

            //まず、上にある面のSmenでのid番号をueMenid[]に収集
            for (int i = 1; i <= Menidsuu; i++) {
                if (jg.get(Menid[i], Menid[iMen]) == 1) {
                    ueMenidsuu = ueMenidsuu + 1;
                    ueMenid[ueMenidsuu] = i;
                    ueMenidFlg[ueMenidsuu] = 1;
                }
            }

            //無効にするid番号のueMenidFlg[id]を0にする。
            for (int i = 1; i <= ueMenidsuu - 1; i++) {
                for (int j = i + 1; j <= ueMenidsuu; j++) {
                    if (jg.get(Menid[ueMenid[i]], Menid[ueMenid[j]]) == 1) {
                        ueMenidFlg[i] = 0;
                    }
                    if (jg.get(Menid[ueMenid[j]], Menid[ueMenid[i]]) == 1) {
                        ueMenidFlg[j] = 0;
                    }
                }
            }

            //案内書に格納
            for (int i = 1; i <= ueMenidsuu; i++) {
                if (ueMenidFlg[i] == 1) {
                    jh.addAnnai(iMen, ueMenid[i]);
                }
            }

        }

    }

    //-----------------------------------------------------------
    //上下表による、このSmenに含まれる面同士のペアの重なり分類が未定の統計をとる
    public int kasanari_bunryi_mitei(Jyougehyou jg) {
        int iret = 0;
        for (int i = 1; i <= Menidsuu - 1; i++) {
            for (int j = i + 1; j <= Menidsuu; j++) {
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == -100) {
                    iret = iret + 1;
                }//20171021本当は-50のつもりだったが現状は-100となっている
            }
        }
        return iret;
    }

    //-----------------------------------------------------------
    //上下表による、このSmenに含まれる面同士のペアの重なり分類が決定済みの統計をとる
    public int kasanari_bunryi_ketteizumi(Jyougehyou jg) {
        int iret = 0;
        for (int i = 1; i <= Menidsuu - 1; i++) {
            for (int j = i + 1; j <= Menidsuu; j++) {
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == 0) {
                    iret = iret + 1;
                }
                if (jg.get(Menid[getJyunretu(i)], Menid[getJyunretu(j)]) == 1) {
                    iret = iret + 1;
                }
            }
        }
        return iret;
    }


}
