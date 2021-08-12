package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen;

import jp.gr.java_conf.mt777.origami.orihime.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_permutation_generator.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken.*;

public class Smen {//This class folds the development view and estimates the overlap information of the planes of the wire diagram.
    //Used to utilize records. Use only in the ClassTable class
    int FaceIdCount;//Smen(折り畳み推定してえられた針金図を細分割した面)で重なっているFace(折りたたむ前の展開図の面)の数。
    int[] FaceId;//S面に含まれるFaceのid番号を記録する。これが20ということは、
    //折った後の紙の重なりが最大の場所でも20-1=19面までということを意味する。//この制限は現在は無し20150309
    //Jyunretu_hasseiki jh = new Jyunretu_hasseiki();
    Overlapping_Permutation_generator jh;// = new Jyuufuku_Jyunretu_hasseiki();


    int Permutation_count = 1;

    int[] FaceId2fromTop_counted_position;//面(Menid)の上から数えた位置を表す。
    int[] fromTop_counted_position2FaceId;//上から数えた位置の面を表す。

    App orihime_app;

    public Smen() {
        reset();
    }  //コンストラクタ

    public Smen(App app0) {
        orihime_app = app0;
        reset();
    }  //コンストラクタ


    private void reset() {
        FaceIdCount = 0;
    }

    //--------------------------------------
    //Initial settings for Ketasuu and permutation generators. Don't forget.
    public void setNumDigits(int FIdCount) {
        FaceIdCount = FIdCount;

        FaceId = new int[FaceIdCount + 1];

        int[] FaceId_2_fromTop_counted_position = new int[FaceIdCount + 1];  //面(Menid)の上から数えた位置を表す。
        int[] fromTop_counted_position_2_FaceId = new int[FaceIdCount + 1];  //上から数えた位置の面を表す。
        FaceId2fromTop_counted_position = FaceId_2_fromTop_counted_position;
        fromTop_counted_position2FaceId = fromTop_counted_position_2_FaceId;

        for (int i = 0; i <= FaceIdCount; i++) {
            FaceId[i] = 0;

            FaceId2fromTop_counted_position[i] = 0;
            fromTop_counted_position2FaceId[i] = 0;
        }
        if (FIdCount > 0) {
            jh = new Overlapping_Permutation_generator(FaceIdCount);
            Permutation_first();

        }
    }

    //--------------------------------------
    public int getFaceIdCount() {
        return FaceIdCount;
    }

    //--------------------------------------
    public int getMenid(int i) {
        return FaceId[i];
    }

//--------------------------------------

    public void setFaceId(int i, int Mid) {
        FaceId[i] = Mid;
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
    public int get_Permutation_count() {
        return Permutation_count;
    }

//--------------------------------------


    public void Permutation_first() {


        if (getFaceIdCount() > 0) {//System.out.println("20150309@@@@@@@2222222222222222222@@@@@@@@@" );
            jh.Permutation_first();
            Permutation_count = 1;
        }//System.out.println("20150309@@@@@@@333333333333333333@@@@@@@@@" );

    } //順列の1番目にもどる。


    //--------------------------------------
    public int next() {
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
    public int next(int k) {
        Permutation_count = Permutation_count + 1;
        return jh.next(k);
    }   //<<<<<<<<<<<<<<<<<<<ここは後で機能を強化して高速化したい。
    // ここは　class Smen の中だよ。

    //現在の順列状態から開始して、可能な重なりかたとなる順列を探す
    public int possible_overlapping_search(ClassTable jg) {//これはjgを変えないはず。
        int mk, ijh;
        mk = 0;
        ijh = 1;//ijhの初期値は0以外ならなんでもいい。
        while (ijh != 0) { //ijh==0なら、桁の最後まできた。
            mk = mujyun_keta_motome(jg);

            //orihime_ap.keijiban.kakikae(8,"Smen_mujyun_keta_motome(jg) =  "+mk);
            if (mk == 1000) {
                return 1000;
            }//このSmenは、矛盾はない状態になっている。

            ijh = next(mk);

            //	orihime_ap.keijiban.kakikae(9,"Smen_kanou_kasanari_sagasi(jg) =  "+ijh);

            String s0 = "";
            for (int i = 1; i <= FaceIdCount; i++) {
                s0 = s0 + " : " + getPermutation(i);
            }
            orihime_app.bulletinBoard.rewrite(10, "Smen_kanou_kasanari_sagasi(jg) =  " + s0);


            //try{Thread.sleep(1000);}catch (InterruptedException e){}// 1000ミリ秒待機する。


        }
        return 0;//可能な重なりかたとなる順列は存在しない
    }

    // ここは　class Smen の中だよ。


    // 現在の順列状態をもとに、上から数えてi番めの面のid番号を返す。

    private int get_uekara_kazoeta_itino_Menid(int i) {
        return FaceId[getPermutation(i)];
    }


    //Based on the current upper and lower tables, the id number of the i-th surface counting from the top is stored.
    public void set_FaceId2fromTop_counted_position(ClassTable jg) {
        for (int i = 1; i <= FaceIdCount; i++) {
            FaceId2fromTop_counted_position[i] = 0;
            for (int j = 1; j <= FaceIdCount; j++) {
                if (jg.get(FaceId[i], FaceId[j]) == 1) {
                    FaceId2fromTop_counted_position[i] = FaceId2fromTop_counted_position[i] + 1;
                }
            }
            FaceId2fromTop_counted_position[i] = FaceIdCount - FaceId2fromTop_counted_position[i];
        }

        for (int iban = 1; iban <= FaceIdCount; iban++) {
            for (int i = 1; i <= FaceIdCount; i++) {
                if (FaceId2fromTop_counted_position[i] == iban) {
                    fromTop_counted_position2FaceId[iban] = i;
                }
            }
        }
    }

    //--------------
    public int uekara_kazoeta_Menid(int iban) {
        return FaceId[fromTop_counted_position2FaceId[iban]];
    }

    //現在の上下表をもとに、上から数えてi番めの面のid番号を返す。上下表は完成したものを使わないと結果がおかしくなる恐れ有り。
    private int get_uekara_kazoeta_itino_Menid(int iban, ClassTable jg) {
        set_FaceId2fromTop_counted_position(jg);
        return FaceId[fromTop_counted_position2FaceId[iban]];
    }

    //
    private int getPermutation(int i) {
        return jh.getPermutation(i);
    }

    // Check from the top side to find out at what digit the folds are inconsistent.
    // At this time, jg does not change. Here, the penetration condition of the boundary line of the adjacent surface is not checked.
    // This Smen returns 1000 if there is no contradiction in the folds.
    private int overlapping_inconsistent_digits_request(ClassTable jg) {
        for (int i = 1; i <= FaceIdCount - 1; i++) {
            for (int j = i + 1; j <= FaceIdCount; j++) {
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == 0) {
                    return i;
                }
            }
        }
        return 1000;
    }

    //Find the number from the top in the stacking order of the surface im. Returns 0 if this Smen does not contain Men.
    public int FaceId2PermutationDigit(int im) {
        for (int i = 1; i <= FaceIdCount; i++) {
            if (FaceId[getPermutation(i)] == im) {
                return i;
            }
        }
        return 0;
    }


    // ここは　class Smen の中だよ。


    // Check from the top surface to find out at what number the boundary line penetration condition of the adjacent surface is inconsistent.
    // At this time, jg does not change. This Smen returns 1000 if there is no contradiction in the penetration conditions.
    private int penetration_inconsistent_digits_request(EquivalenceCondition tj) {
        int mm, M1, M2; //折り畳み推定の際の等価条件の登録は　addTouka_jyouken(im,Mid_min,im,Mid_max);  による
        mm = FaceId2PermutationDigit(tj.getA());
        M1 = FaceId2PermutationDigit(tj.getB());
        M2 = FaceId2PermutationDigit(tj.getD());
        if (mm * M1 * M2 == 0) {
            return 1000;
        }

        if ((mm - M1) * (mm - M2) < 0) {
            return mm;
        }
        return 1000;
    }

    //

    private int penetration_inconsistent_digits_request(ClassTable jg) {
        int ketaMim = 1000;
        int tmk = 1000;
        for (int i = 1; i <= jg.getEquivalenceConditionTotal(); i++) {
            tmk = penetration_inconsistent_digits_request(jg.getEquivalenceCondition(i));

            if (tmk <= ketaMim) {
                ketaMim = tmk;
            }
        }

        return ketaMim;

    }


    // Check from the top surface to find out at what number the two surfaces that share a part of the boundary line and the penetration conditions of the two surfaces are inconsistent.
    // At this time, jg does not change. This Smen returns 1000 if there is no contradiction.
    private int u_penetration_inconsistent_digits_request(EquivalenceCondition uj) {
        int mi1, mi2, mj1, mj2, itemp; //折り畳み推定の際の等価条件の登録は　u_addTouka_jyouken(im1,im2,im3,im4);  による
        mi1 = FaceId2PermutationDigit(uj.getA());
        mi2 = FaceId2PermutationDigit(uj.getB());
        if (mi2 < mi1) {
            itemp = mi1;
            mi1 = mi2;
            mi2 = itemp;
        }

        mj1 = FaceId2PermutationDigit(uj.getC());
        mj2 = FaceId2PermutationDigit(uj.getD());
        if (mj2 < mj1) {
            itemp = mj1;
            mj1 = mj2;
            mj2 = itemp;
        }

        if (mi1 * mi2 * mj1 * mj2 != 0) {
            if (((mi1 < mj1) && (mj1 < mi2)) && (mi2 < mj2)) {
                return mi2;
            }
            if (((mj1 < mi1) && (mi1 < mj2)) && (mj2 < mi2)) {
                return mj2;
            }
        }

        return 1000;
    }

    //

    private int u_penetration_inconsistent_digits_request(ClassTable jg) {
        int ketaMim = 1000;
        int tmk = 1000;
        for (int i = 1; i <= jg.getUEquivalenceConditionTotal(); i++) {
            tmk = u_penetration_inconsistent_digits_request(jg.getUEquivalenceCondition(i));

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
    private int mujyun_keta_motome(ClassTable jg) {
        int min1, min2, min3;
        min1 = overlapping_inconsistent_digits_request(jg);
        min2 = penetration_inconsistent_digits_request(jg);
        min3 = u_penetration_inconsistent_digits_request(jg);

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
    public void jg_ni_Smen_wo_nyuuryoku(ClassTable jg) {

        for (int i = 1; i <= FaceIdCount; i++) {
            for (int j = 1; j <= i - 1; j++) {
                jg.set(FaceId[getPermutation(i)], FaceId[getPermutation(j)], 0);
            }

            for (int j = i + 1; j <= FaceIdCount; j++) {
                jg.set(FaceId[getPermutation(i)], FaceId[getPermutation(j)], 1);
            }
        }
    }


    //上下表にSmenの面の重なりによる情報を入れる。これは初期の計算準備の際にSmenの有効数を求めるのに使う。
    public void jg_ni_Smen_no_tantoubasyo_wo_nyuuryoku(ClassTable jg) {

        for (int i = 1; i <= FaceIdCount; i++) {
            for (int j = 1; j <= i - 1; j++) {
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == -100) {
                    jg.set(FaceId[getPermutation(i)], FaceId[getPermutation(j)], -50);
                }
            }

            for (int j = i + 1; j <= FaceIdCount; j++) {
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == -100) {
                    jg.set(FaceId[getPermutation(i)], FaceId[getPermutation(j)], -50);
                }
            }
        }
    }

    //上下表にSmenによって何個の新情報が入るかを返す。
    //Returns how many new information Smen will put in the top and bottom tables.
    public int sinki_jyouhou_suu(ClassTable jg) {
        int inew = 0;
        for (int i = 1; i <= FaceIdCount; i++) {
            for (int j = 1; j <= i - 1; j++) {
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == -100) {
                    inew = inew + 1;
                }
            }

            for (int j = i + 1; j <= FaceIdCount; j++) {
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == -100) {
                    inew = inew + 1;
                }
            }
        }
        //
        return inew;
    }
    //

    // jg [] [] treats the hierarchical relationship between all the faces of the development drawing before folding as one table.
    // If jg [i] [j] is 1, surface i is above surface j. If it is 0, it is the lower side.
    // If jg [i] [j] is -50, faces i and j overlap, but the hierarchical relationship is not determined.
    // If jg [i] [j] is -100, then faces i and j do not overlap.

    public void setGuideMap(ClassTable jg) { //I will prepare a guidebook for the permutations with repeat generator in Smen.
        int[] ueFaceId = new int[FaceIdCount + 1];
        int[] ueFaceIdFlg = new int[FaceIdCount + 1];//1 if ueFaceId [] is enabled, 0 if disabled

        for (int iMen = 1; iMen <= FaceIdCount; iMen++) {
            int ueFaceIdCount = 0;//Stores how many ueFaceId [] are from 1.

            //First, collect the Smen id number of the upper surface in ueFaceId []
            for (int i = 1; i <= FaceIdCount; i++) {
                if (jg.get(FaceId[i], FaceId[iMen]) == 1) {
                    ueFaceIdCount = ueFaceIdCount + 1;
                    ueFaceId[ueFaceIdCount] = i;
                    ueFaceIdFlg[ueFaceIdCount] = 1;
                }
            }

            //Set ueFaceIdFlg [id] of the id number to be invalid to 0.
            for (int i = 1; i <= ueFaceIdCount - 1; i++) {
                for (int j = i + 1; j <= ueFaceIdCount; j++) {
                    if (jg.get(FaceId[ueFaceId[i]], FaceId[ueFaceId[j]]) == 1) {
                        ueFaceIdFlg[i] = 0;
                    }
                    if (jg.get(FaceId[ueFaceId[j]], FaceId[ueFaceId[i]]) == 1) {
                        ueFaceIdFlg[j] = 0;
                    }
                }
            }

            //Store in guidebook
            for (int i = 1; i <= ueFaceIdCount; i++) {
                if (ueFaceIdFlg[i] == 1) {
                    jh.addGuide(iMen, ueFaceId[i]);
                }
            }

        }

    }

    //-----------------------------------------------------------
    //According to the table above and below, the overlapping classification of pairs of faces included in this Smen is undecided.
    public int overlapping_classification_pending(ClassTable jg) {
        int iret = 0;
        for (int i = 1; i <= FaceIdCount - 1; i++) {
            for (int j = i + 1; j <= FaceIdCount; j++) {
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == -100) {
                    iret = iret + 1;
                }//20171021本当は-50のつもりだったが現状は-100となっている
            }
        }
        return iret;
    }

    //-----------------------------------------------------------
    // According to the upper and lower tables, the overlapping classification of the pairs of faces included in this Smen is determined.
    public int overlapping_classification_determined(ClassTable jg) {
        int iret = 0;
        for (int i = 1; i <= FaceIdCount - 1; i++) {
            for (int j = i + 1; j <= FaceIdCount; j++) {
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == 0) {
                    iret = iret + 1;
                }
                if (jg.get(FaceId[getPermutation(i)], FaceId[getPermutation(j)]) == 1) {
                    iret = iret + 1;
                }
            }
        }
        return iret;
    }


}
