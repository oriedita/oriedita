package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin;

import jp.gr.java_conf.mt777.origami.orihime.*;
import jp.gr.java_conf.mt777.origami.orihime.tenkaizu_syokunin.*;
import jp.gr.java_conf.mt777.origami.dougu.pointstore.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.*;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import jp.gr.java_conf.mt777.kiroku.memo.*;
import jp.gr.java_conf.mt777.kiroku.moji_sousa.*;
import jp.gr.java_conf.mt777.origami.dougu.camera.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken.*;
import jp.gr.java_conf.mt777.zukei2d.oritaoekaki.*;
import jp.gr.java_conf.mt777.seiretu.narabebako.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;


public class HierarchyList_Worker {//HierarchyList: Record and utilize what kind of vertical relationship the surface of the developed view before folding will be after folding.
    HierarchyList hierarchyList = new HierarchyList();
    int SubFaceTotal;//SubFaceの数
    int SubFace_valid_number;//SubFaceは全て調べなくても、Faceの上下関係は網羅できる。Faceの上下関係を網羅するのに必要なSubFaceの数が優先順位の何番目までかをさがす。
    int FaceIdCount_max;//各SubFaceの持つMenidsuuの最大値。すなわち、最も紙に重なりが多いところの枚数。
    //paint 用のint格納用VVVVVVVVVVVVVVVVVVVVVV
    int ip1 = 0; //0は折り畳み図の表側を表示するモード。1は折り畳み図の裏側を表示するモード。
    //  hierarchyList[][]は折る前の展開図のすべての面同士の上下関係を1つの表にまとめたものとして扱う
    //　hierarchyList[i][j]が1なら面iは面jの上側。0なら下側。
    //  hierarchyList[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
    //hierarchyList[i][j]が-100なら、面iとjは重なるところがない。

    SubFace[] s0;//SubFace_figureから得られるSubFace
    SubFace[] s;//s is s0 sorted in descending order of priority.
    int[] s0_no_yusenjyun;
    int[] yusenjyun_kara_s0id;

    boolean displayShadows = false; //Whether to display shadows. 0 is not displayed, 1 is displayed

    Camera camera = new Camera();

    App orihime_app;

    public double[] face_rating;
    public int[] i_face_rating;

    public SortingBox_int_double nbox = new SortingBox_int_double();//20180227　nboxにはmenのidがmen_ratingと組になって、men_ratingの小さい順に並べ替えられて入っている。

    //-----------------------------------------------------------------
    public HierarchyList_Worker(App app0) {
        orihime_app = app0;
        reset();
    }

    //-----------------------------------------------------------------
    public void reset() {
        hierarchyList.reset();
        SubFaceTotal = 0;
        SubFace_valid_number = 0;
        ip1 = 0;
        FaceIdCount_max = 0;
        camera.reset();
    }


    //--------
    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);
    }


    public int getSubFaceTotal() {
        return SubFaceTotal;
    }

    public int getSubFace_valid_number() {
        return SubFace_valid_number;
    }

    //　ここは  class Jyougehyou_Syokunin  の中です。
    //上下表の初期設定。展開図に1頂点から奇数の折線がでる誤りがある場合0を返す。それが無ければ1000を返す。
    //展開図に山谷折線の拡張による誤りがある場合2を返す。

    //----------------------------------------------------------------------

    public void SubFace_configure(PointSet otta_Face_figure, PointSet SubFace_figure) {//js.Jyougehyou_settei(ts1,ts2.get(),ts3.get());
        // Make an upper and lower table of faces (the faces in the unfolded view before folding).
        // This includes the point set of ts2 (which has information on the positional relationship of the faces after folding) and <-------------otta_Face_figure
        // Use the point set of ts3 (which has the information of SubFace whose surface is subdivided in the wire diagram). <-------------SubFace_figure
        // Also, use the information on the positional relationship of the surface when folded, which ts1 has.

        System.out.println("Smenの初期設定");
        reset();
        SubFaceTotal = SubFace_figure.getFacesTotal();

        SubFace[] s0_ori = new SubFace[SubFaceTotal + 1];
        SubFace[] s_ori = new SubFace[SubFaceTotal + 1];
        s0 = s0_ori;
        s = s_ori;
        int[] s0yj = new int[SubFaceTotal + 1];
        int[] yjs0 = new int[SubFaceTotal + 1];
        s0_no_yusenjyun = s0yj;
        yusenjyun_kara_s0id = yjs0;

        for (int i = 0; i < SubFaceTotal + 1; i++) {
            s0[i] = new SubFace(orihime_app);
            s[i] = s0[i];
            s0_no_yusenjyun[i] = 0;
            yusenjyun_kara_s0id[i] = i;
        }

        //Record the faces contained in each SubFace.
        System.out.println("各Smenに含まれる面を記録するため、各Smenの内部点を登録");
        Point[] subFace_insidePoint = new Point[SubFaceTotal + 1];  //<<<<<<<<<<<<<<<<<<<<<<<<<<<オブジェクトの配列を動的に指定
        for (int i = 1; i <= SubFaceTotal; i++) {
            subFace_insidePoint[i] = SubFace_figure.insidePoint_surface(i);
        }

        System.out.println("各Smenに含まれる面を記録する");
        otta_Face_figure.BouMenMaxMinZahyou();//tttttttttt

        int[] s0addFaceId = new int[otta_Face_figure.getFacesTotal() + 1];  //SubFaceに追加する面を一時記録しておく

        for (int i = 1; i <= SubFaceTotal; i++) {
            int s0addFaceTotal = 0;

            for (int j = 1; j <= otta_Face_figure.getFacesTotal(); j++) {

                if (otta_Face_figure.simple_inside(subFace_insidePoint[i], j) == 2) {
                    s0addFaceTotal = s0addFaceTotal + 1;
                    s0addFaceId[s0addFaceTotal] = j;
                }

            }

            s0[i].setNumDigits(s0addFaceTotal);

            for (int j = 1; j <= s0addFaceTotal; j++) {
                s0[i].setFaceId(j, s0addFaceId[j]);//ここで面番号jは小さい方が先に追加される。
            }
        }

        //ここまでで、SubFaceTotal＝	SubFace_figure.getMensuu()のままかわりなし。
        System.out.println("各Smenに含まれる面の数の内で最大のものを求める");
        // Find the largest number of faces in each SubFace.
        FaceIdCount_max = 0;
        for (int i = 1; i <= SubFaceTotal; i++) {
            if (s0[i].getFaceIdCount() > FaceIdCount_max) {
                FaceIdCount_max = s0[i].getFaceIdCount();
            }
        }
    }

    //------------------------------------------------------
    public int ClassTable_configure(CreasePattern_Worker orite, PointSet otta_face_figure) {//js.Jyougehyou_settei(ts1,ts2.get(),ts3.get());
        orihime_app.bulletinBoard.write("           Jyougehyou_settei   step1   start ");
        int ireturn = 1000;
        hierarchyList.setFacesTotal(otta_face_figure.getFacesTotal());

        //Put the hierarchical relationship determined from the information of mountain folds and valley folds in the table above and below.
        System.out.println("山折り谷折りの情報から決定される上下関係を上下表に入れる");
        int Mid_min, Mid_max;
        for (int ib = 1; ib <= orite.getSticksTotal(); ib++) {
            Mid_min = orite.Stick_moti_FaceId_min_request(ib);
            Mid_max = orite.Stick_moti_FaceId_max_request(ib);
            if (Mid_min != Mid_max) {//展開図において、棒ibの両脇に面がある
                if (otta_face_figure.getColor(ib) == LineType.RED_1) {//赤い線で山折りを意味する
                    if (orite.getIFacePosition(Mid_min) % 2 == 1) {//面Mid_minは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(Mid_min, Mid_max, 1);
                        hierarchyList.set(Mid_max, Mid_min, 0);
                    }
                    if (orite.getIFacePosition(Mid_max) % 2 == 1) {//面Mid_maxは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(Mid_max, Mid_min, 1);
                        hierarchyList.set(Mid_min, Mid_max, 0);
                    }
                }
                if (otta_face_figure.getColor(ib) == LineType.BLUE_2) {//青い線で谷折りを意味する
                    if (orite.getIFacePosition(Mid_min) % 2 == 1) {//面Mid_minは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(Mid_min, Mid_max, 0);
                        hierarchyList.set(Mid_max, Mid_min, 1);
                    }
                    if (orite.getIFacePosition(Mid_max) % 2 == 1) {//面Mid_maxは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(Mid_max, Mid_min, 0);
                        hierarchyList.set(Mid_min, Mid_max, 1);
                    }
                }

                if ((orite.getIFacePosition(Mid_min) % 2 == 0) && (orite.getIFacePosition(Mid_max) % 2 == 0)) {
                    ireturn = 0;
                }
                if ((orite.getIFacePosition(Mid_min) % 2 == 1) && (orite.getIFacePosition(Mid_max) % 2 == 1)) {
                    ireturn = 0;
                }
            }
        }

        //----------------------------------------------
        orihime_app.bulletinBoard.write("           Jyougehyou_settei   step2   start ");
        System.out.println("等価条件を設定する   ");
        //等価条件を設定する。棒ibを境界として隣接する2つの面im1,im2が有る場合、折り畳み推定した場合に
        //棒ibの一部と重なる位置に有る面imは面im1と面im2に上下方向で挟まれることはない。このことから
        //gj[im1][im]=gj[im2][im]という等価条件が成り立つ。
        for (int ib = 1; ib <= orite.getSticksTotal(); ib++) {
            Mid_min = orite.Stick_moti_FaceId_min_request(ib);
            Mid_max = orite.Stick_moti_FaceId_max_request(ib);
            if (Mid_min != Mid_max) {//展開図において、棒ibの両脇に面がある
                for (int im = 1; im <= hierarchyList.getFacesTotal(); im++) {
                    if ((im != Mid_min) && (im != Mid_max)) {
                        if (otta_face_figure.simple_convex_inside(ib, im)) {
                            //下の２つのifは暫定的な処理。あとで置き換え予定
                            if (otta_face_figure.convex_inside(0.5, ib, im)) {
                                if (otta_face_figure.convex_inside(-0.5, ib, im)) {
                                    hierarchyList.addEquivalenceCondition(im, Mid_min, im, Mid_max);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.print("３面が関与する突き抜け条件の数　＝　");
        System.out.println(hierarchyList.getEquivalenceConditionTotal());
        orihime_app.bulletinBoard.write("           Jyougehyou_settei   step3   start ");
        //等価条件の追加。棒ibの境界として隣接する2つの面im1,im2が有り、
        //また棒jbの境界として隣接する2つの面im3,im4が有り、ibとjbが平行で、一部重なる場合、折り畳み推定した場合に
        //棒ibの面と面jbの面がi,j,i,j　または　j,i,j,i　と並ぶことはない。もしこれがおきたら、
        //最初から3番目で間違いが起きているので、この3番目のところがSubFaceで何桁目かを求めて、この桁を１進める。
        int mi1, mi2, mj1, mj2;

        for (int ib = 1; ib <= orite.getSticksTotal() - 1; ib++) {
            for (int jb = ib + 1; jb <= orite.getSticksTotal(); jb++) {
                if (otta_face_figure.parallel_overlap(ib, jb)) {
                    mi1 = orite.Stick_moti_FaceId_min_request(ib);
                    mi2 = orite.Stick_moti_FaceId_max_request(ib);
                    if (mi1 != mi2) {
                        mj1 = orite.Stick_moti_FaceId_min_request(jb);
                        mj2 = orite.Stick_moti_FaceId_max_request(jb);
                        if (mj1 != mj2) {
                            if (mi1 * mi2 * mj1 * mj2 != 0) {
                                if (onaji_subFace_ni_sonzai(mi1, mi2, mj1, mj2)) {
                                    hierarchyList.addUEquivalenceCondition(mi1, mi2, mj1, mj2);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.print("４面が関与する突き抜け条件の数　＝　");
        System.out.println(hierarchyList.getUEquivalenceConditionTotal());

        orihime_app.bulletinBoard.write("           Jyougehyou_settei   step4   start ");
        //Additional estimation
        int additional;

        additional = additional_estimation();
        if (additional != 1000) {
            return additional;
        }

        System.out.println("追加推定 終了し、上下表を保存------------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        //*************Saving the results of the first deductive reasoning**************************
        hierarchyList.save();//Save the hierarchical relationship determined from the mountain fold and valley fold information.
        //************************************************************************
        orihime_app.bulletinBoard.write("           Jyougehyou_settei   step5   start ");
        //Make a guidebook for each SubFace
        System.out.println("Smen毎に案内書を作る");
        for (int i = 1; i <= SubFaceTotal; i++) {
            s0[i].setGuideMap(hierarchyList);
        }

        //s0に優先順位をつける(このときhierarchyListの-100のところが変るところがある)
        System.out.println("Smen(s0)に優先順位をつける");
        //まず、他のSubFaceに丸ごと含まれているSubFaceを除外する

        int[] SubFace_no_dokujisei = new int[SubFaceTotal + 1];  //<<<<<<<<<<<<<<<SubFaceの独自性
        for (int i = 1; i <= SubFaceTotal; i++) {
            SubFace_no_dokujisei[i] = 1;
        }
        for (int i = 1; i <= SubFaceTotal; i++) {
            SubFace_no_dokujisei[i] = 1;
            for (int j = 1; j <= SubFaceTotal; j++) {
                if (SubFace_no_dokujisei[j] == 1) {

                    if (i != j) {//s0[j]がs0[i]を含むかをみる。
                        if (subFace_i_ga_j_ni_included(i, j)) {
                            SubFace_no_dokujisei[i] = 0;
                            break;
                        }
                    }
                }
            }
        }

        int[] i_yusendo_max = new int[SubFaceTotal + 1];     //<<<<<<<<<<<<<<<臨時

        for (int i = 1; i <= SubFaceTotal; i++) {//優先度i番目のSubFaceIdをさがす。
            int yusendo_max = -10000;//優先度i番目の優先度の値（大きいほうが優先度が高い）。
            int i_yusen = 0;

            for (int is0 = 1; is0 <= SubFaceTotal; is0++) { //SubFaceを１からSubFaceTotal番目までサーチ
                int Sy;//SubFaceId_yusendo(is0)+SubFace_no_dokujisei[is0] を格納
                if (s0_no_yusenjyun[is0] == 0) {//まだ優先順位がついていないSubFaceだけを扱う
                    Sy = subFaceId_priority(is0)/*+SubFace_no_dokujisei[is0]*/;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    if (yusendo_max < Sy) {
                        yusendo_max = Sy;
                        i_yusen = is0;// i_yusenがi番目の優先度を探している際の最も有力な候補の番号
                    }
                    if (yusendo_max == Sy) {
                        if (s0[i_yusen].getFaceIdCount() < s0[is0].getFaceIdCount()) {
                            i_yusen = is0;
                        }
                    }
                }
            }

            s0_no_yusenjyun[i_yusen] = i; //優先度i番目のSubFaceIdはi_yusen。
            i_yusendo_max[i_yusen] = yusendo_max;//優先度i番目の優先度の値（大きいほうが優先度が高い）。

            s0[i_yusen].hierarchyList_ni_subFace_no_manager_wo_input(hierarchyList); //hierarchyListの-100のところを変る。<<<<<<<<<<<<<<<<<<<<<<
        }

        //優先度からs0のidを指定できるようにする

        for (int i = 1; i <= SubFaceTotal; i++) {
            for (int is0 = 1; is0 <= SubFaceTotal; is0++) {
                if (i == s0_no_yusenjyun[is0]) {
                    yusenjyun_kara_s0id[i] = is0;
                }
            }
        }


        //System.out.println("------------" );
        System.out.println("上下表職人内　Smensuu = " + SubFaceTotal);
        System.out.println("上下表職人内　s0に優先順位をつける");
        for (int i = 1; i <= SubFaceTotal; i++) {
            System.out.println(s0_no_yusenjyun[i]);
        }
        System.out.println("上下表職人内　優先度からs0のid");
        for (int i = 1; i <= SubFaceTotal; i++) {
            System.out.println(yusenjyun_kara_s0id[i]);
        }


        for (int i = 1; i <= SubFaceTotal; i++) {
            //System.out.println(yusenjyun_kara_s0id[i]);
            if (i_yusendo_max[yusenjyun_kara_s0id[i]] != 0) {
                SubFace_valid_number = i;       //早いが変な結果になることあり。
//20191012 wwwww				SubFace_yuukou_suu=SubFaceTotal;//遅いが確実

            }
        }


//20191012 				SubFace_yuukou_suu=SubFaceTotal;//遅いが確実//20191012


        for (int i = 1; i <= SubFaceTotal; i++) {
            s[i] = s0[yusenjyun_kara_s0id[i]];
        }

        //優先順位を逆転させる。これが有効かどうかは不明wwwww

        //SubFaceは優先順の何番目までやるかを決める

        System.out.print("Smen有効数は　");
        System.out.print(SubFace_valid_number);
        System.out.print("／");
        System.out.println(SubFaceTotal);

        //hierarchyList[][]の重なりのある面の組み合わせの位置の値を-100から-50に変える。
        for (int k = 1; k <= SubFaceTotal; k++) {
            for (int i = 1; i <= s[k].getFaceIdCount() - 1; i++) {
                for (int j = i + 1; j <= s[k].getFaceIdCount(); j++) {
                    hierarchyList.set(i, j, -50);
                    hierarchyList.set(j, i, -50);
                }
            }
        }


        System.out.println("上下表初期設定終了");
        return ireturn;
    }

    //------------------------------------------------------------
    public int additional_estimation() {
        //We will infer relationships that can be further determined from the information on mountain folds and valley folds. 。

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

                for (int iS = 1; iS <= SubFaceTotal; iS++) {

                    int flg_a = 1;
                    while (flg_a >= 1) {
                        flg_a = 0;
                        for (int iM = 1; iM <= s0[iS].getFaceIdCount(); iM++) {//3面の比較で中間にくる面
                            int[] ueMenid = new int[s0[iS].getFaceIdCount() + 1];//S面に含まれるあるMenの上がわにあるid番号を記録する。これが20ということは、
                            int[] sitaMenid = new int[s0[iS].getFaceIdCount() + 1];//S面に含まれるあるMenの下がわにあるid番号を記録する。これが20ということは、
                            int ueMenid_max = 0;
                            int sitaMenid_max = 0;
                            Mid = s0[iS].getFaceId(iM);

                            // Thinking: Think about a certain side Mid of a certain SubFace.
                            // Other than this SubFace, it is assumed that surface A is above the surface Mid and surface B is below the surface Mid.
                            // Generally, in separate SubFace, surface A cannot be determined to be above surface B just because surface A is above surface Mid and surface B is below surface Mid.
                            // However, this is the point, but if there is a SubFace that includes surface A, surface Mid, and surface B together, even if you do not know the hierarchical relationship of that SubFace
                            // Surface A is above surface B. So, the information we get from SubFace in this operation is whether there are three sides together.
                            // There is no need for a hierarchical relationship within a SubFace.
                            // //
                            // The operation here is collecting the hierarchical relationship of a certain SubFace from the upper and lower tables.
                            for (int i = 1; i <= s0[iS].getFaceIdCount(); i++) {//Menid[iM]より上にある面。
                                if (iM != i) {
                                    if (hierarchyList.get(Mid, s0[iS].getFaceId(i)) == 0) {
                                        ueMenid_max = ueMenid_max + 1;
                                        ueMenid[ueMenid_max] = s0[iS].getFaceId(i);
                                    }
                                    if (hierarchyList.get(Mid, s0[iS].getFaceId(i)) == 1) {
                                        sitaMenid_max = sitaMenid_max + 1;
                                        sitaMenid[sitaMenid_max] = s0[iS].getFaceId(i);
                                    }
                                }
                            }

                            for (int iuM = 1; iuM <= ueMenid_max; iuM++) {//Menid[iM]より上にある面。
                                for (int isM = 1; isM <= sitaMenid_max; isM++) {//Menid[iM]より下にある面。

                                    if (hierarchyList.get(ueMenid[iuM], sitaMenid[isM]) == 0) {
                                        return 2;
                                    }//面の上下関係の拡張で矛盾発生。
                                    if (hierarchyList.get(sitaMenid[isM], ueMenid[iuM]) == 1) {
                                        return 2;
                                    }//面の上下関係の拡張で矛盾発生。

                                    if (hierarchyList.get(ueMenid[iuM], sitaMenid[isM]) < 0) {
                                        hierarchyList.set(ueMenid[iuM], sitaMenid[isM], 1);
                                        flg_a = flg_a + 1;
                                        flg_b = flg_b + 1;
                                        flg_c = flg_c + 1;
                                    }
                                    if (hierarchyList.get(sitaMenid[isM], ueMenid[iuM]) < 0) {
                                        hierarchyList.set(sitaMenid[isM], ueMenid[iuM], 0);
                                        flg_a = flg_a + 1;
                                        flg_b = flg_b + 1;
                                        flg_c = flg_c + 1;
                                    }
                                }
                            }
                        }
                    }


                }
            }

            //hierarchyList のreset適切に行われているか確認のこと

            //System.out.println ("３面が関与する突き抜け条件から追加推定   " );
            //(im,Mid_min,im,Mid_max);
            EquivalenceCondition tg;

            int flg_a = 1;
            while (flg_a >= 1) {
                flg_a = 0;
                for (int i = 1; i <= hierarchyList.getEquivalenceConditionTotal(); i++) {
                    tg = hierarchyList.getEquivalenceCondition(i);
                    if (hierarchyList.get(tg.getA(), tg.getB()) == 1) {
                        if (hierarchyList.get(tg.getA(), tg.getD()) == 0) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()) == 1) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getD()) < 0) {
                            hierarchyList.set(tg.getA(), tg.getD(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()) < 0) {
                            hierarchyList.set(tg.getD(), tg.getA(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    if (hierarchyList.get(tg.getA(), tg.getB()) == 0) {
                        if (hierarchyList.get(tg.getA(), tg.getD()) == 1) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()) == 0) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getD()) < 0) {
                            hierarchyList.set(tg.getA(), tg.getD(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()) < 0) {
                            hierarchyList.set(tg.getD(), tg.getA(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //
                    if (hierarchyList.get(tg.getA(), tg.getD()) == 1) {
                        if (hierarchyList.get(tg.getA(), tg.getB()) == 0) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()) == 1) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getB()) < 0) {
                            hierarchyList.set(tg.getA(), tg.getB(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()) < 0) {
                            hierarchyList.set(tg.getB(), tg.getA(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    if (hierarchyList.get(tg.getA(), tg.getD()) == 0) {
                        if (hierarchyList.get(tg.getA(), tg.getB()) == 1) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()) == 0) {
                            return 3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getB()) < 0) {
                            hierarchyList.set(tg.getA(), tg.getB(), 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()) < 0) {
                            hierarchyList.set(tg.getB(), tg.getA(), 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                }
            }

            flg_a = 1;
            while (flg_a >= 1) {
                flg_a = 0;
                for (int i = 1; i <= hierarchyList.getUEquivalenceConditionTotal(); i++) {
                    tg = hierarchyList.getUEquivalenceCondition(i);
                    int a, b, c, d;
                    a = tg.getA();
                    b = tg.getB();
                    c = tg.getC();
                    d = tg.getD();


                    //　a>b>c　だけならdの位置は決まらない


                    //　a>c && b>d なら a>d && b>c
                    //  a>d && b>c なら a>c && b>d
                    //　a<c && b<d なら a<d && b<c
                    //  a<d && b<c なら a<c && b<d


                    //　a>c>b　なら　a>d>b

                    //a>c && b>d なら a>d && b>c
                    if ((hierarchyList.get(a, c) == 1) && (hierarchyList.get(b, d) == 1)) {
                        if (hierarchyList.get(a, d) == 0) {
                            return 4;
                        }
                        if (hierarchyList.get(b, c) == 0) {
                            return 4;
                        }
                        if (hierarchyList.get(a, d) < 0) {
                            hierarchyList.set(a, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, c) < 0) {
                            hierarchyList.set(b, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, a) < 0) {
                            hierarchyList.set(d, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, b) < 0) {
                            hierarchyList.set(c, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //a>d && b>c なら a>c && b>d
                    if ((hierarchyList.get(a, d) == 1) && (hierarchyList.get(b, c) == 1)) {
                        if (hierarchyList.get(a, c) == 0) {
                            return 4;
                        }
                        if (hierarchyList.get(b, d) == 0) {
                            return 4;
                        }
                        if (hierarchyList.get(a, c) < 0) {
                            hierarchyList.set(a, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, d) < 0) {
                            hierarchyList.set(b, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, a) < 0) {
                            hierarchyList.set(c, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, b) < 0) {
                            hierarchyList.set(d, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }

                    //a<c && b<d なら a<d && b<c
                    if ((hierarchyList.get(a, c) == 0) && (hierarchyList.get(b, d) == 0)) {
                        if (hierarchyList.get(a, d) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(b, c) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(a, d) < 0) {
                            hierarchyList.set(a, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, c) < 0) {
                            hierarchyList.set(b, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, a) < 0) {
                            hierarchyList.set(d, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, b) < 0) {
                            hierarchyList.set(c, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //a<d && b<c なら a<c && b<d
                    if ((hierarchyList.get(a, d) == 0) && (hierarchyList.get(b, c) == 0)) {
                        if (hierarchyList.get(a, c) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(b, d) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(a, c) < 0) {
                            hierarchyList.set(a, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, d) < 0) {
                            hierarchyList.set(b, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, a) < 0) {
                            hierarchyList.set(c, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, b) < 0) {
                            hierarchyList.set(d, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }


                    //　a>c>b　なら　a>d>b
                    if ((hierarchyList.get(a, c) == 1) && (hierarchyList.get(c, b) == 1)) {
                        if (hierarchyList.get(d, a) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(b, d) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(a, d) < 0) {
                            hierarchyList.set(a, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, b) < 0) {
                            hierarchyList.set(d, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, a) < 0) {
                            hierarchyList.set(d, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, d) < 0) {
                            hierarchyList.set(b, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　a>d>b　なら　a>c>b
                    if ((hierarchyList.get(a, d) == 1) && (hierarchyList.get(d, b) == 1)) {
                        if (hierarchyList.get(c, a) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(b, c) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(a, c) < 0) {
                            hierarchyList.set(a, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, b) < 0) {
                            hierarchyList.set(c, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, a) < 0) {
                            hierarchyList.set(c, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, c) < 0) {
                            hierarchyList.set(b, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　b>c>a　なら　b>d>a
                    if ((hierarchyList.get(b, c) == 1) && (hierarchyList.get(c, a) == 1)) {
                        if (hierarchyList.get(d, b) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(a, d) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(b, d) < 0) {
                            hierarchyList.set(b, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, a) < 0) {
                            hierarchyList.set(d, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, b) < 0) {
                            hierarchyList.set(d, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(a, d) < 0) {
                            hierarchyList.set(a, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　b>d>a　なら　b>c>a
                    if ((hierarchyList.get(b, d) == 1) && (hierarchyList.get(d, a) == 1)) {
                        if (hierarchyList.get(c, b) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(a, c) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(b, c) < 0) {
                            hierarchyList.set(b, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, a) < 0) {
                            hierarchyList.set(c, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, b) < 0) {
                            hierarchyList.set(c, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(a, c) < 0) {
                            hierarchyList.set(a, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }


                    //　c>a>d　なら　c>b>d
                    if ((hierarchyList.get(c, a) == 1) && (hierarchyList.get(a, d) == 1)) {
                        if (hierarchyList.get(b, c) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(d, b) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(c, b) < 0) {
                            hierarchyList.set(c, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, d) < 0) {
                            hierarchyList.set(b, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, c) < 0) {
                            hierarchyList.set(b, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, b) < 0) {
                            hierarchyList.set(d, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　c>b>d　なら　c>a>d
                    if ((hierarchyList.get(c, b) == 1) && (hierarchyList.get(b, d) == 1)) {
                        if (hierarchyList.get(a, c) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(d, a) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(c, a) < 0) {
                            hierarchyList.set(c, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(a, d) < 0) {
                            hierarchyList.set(a, d, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(a, c) < 0) {
                            hierarchyList.set(a, c, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(d, a) < 0) {
                            hierarchyList.set(d, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　d>a>c　なら　d>b>c
                    if ((hierarchyList.get(d, a) == 1) && (hierarchyList.get(a, c) == 1)) {
                        if (hierarchyList.get(b, d) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(c, b) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(d, b) < 0) {
                            hierarchyList.set(d, b, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, c) < 0) {
                            hierarchyList.set(b, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(b, d) < 0) {
                            hierarchyList.set(b, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, b) < 0) {
                            hierarchyList.set(c, b, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                    //　d>b>c　なら　d>a>c
                    if ((hierarchyList.get(d, b) == 1) && (hierarchyList.get(b, c) == 1)) {
                        if (hierarchyList.get(a, d) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(c, a) == 1) {
                            return 4;
                        }
                        if (hierarchyList.get(d, a) < 0) {
                            hierarchyList.set(d, a, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(a, c) < 0) {
                            hierarchyList.set(a, c, 1);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(a, d) < 0) {
                            hierarchyList.set(a, d, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                        if (hierarchyList.get(c, a) < 0) {
                            hierarchyList.set(c, a, 0);
                            flg_a = flg_a + 1;
                            flg_c = flg_c + 1;
                        }
                    }
                }
            }

            //----------------

            System.out.print("推測された関係の数の合計 ＝ ");
            System.out.println(flg_c);

        }


        System.out.println("追加推定 終了------------------------＊＊＊＊ここまで20150310＊＊＊＊＊＊＊＊＊＊＊");

        return 1000;

    }

    //------------------------------------------

    //-----------------------------------------------------------------------------------------

    private boolean subFace_i_ga_j_ni_included(int s0i, int s0j) { //1 if included, 0 otherwise
        if (s0[s0i].getFaceIdCount() > s0[s0j].getFaceIdCount()) {
            return false;
        }


        for (int i = 1; i <= s0[s0i].getFaceIdCount(); i++) {
            for (int j = 1; j <= s0[s0j].getFaceIdCount(); j++) {
                if (s0[s0i].getFaceId(i) == s0[s0j].getFaceId(j)) {
                    break;
                }
                if (j == s0[s0j].getFaceIdCount()) {
                    return false;
                }
            }
        }

        return true;

    }

    //Find the value that indicates the priority of s0 (SubFace). The higher this value, the higher the priority (closer to the beginning of the array).
    int subFaceId_priority(int s0id) {
        //Find out how many new hierarchies will be registered if you put SubFace in the current top and bottom table.
        return s0[s0id].sinki_jyouhou_suu(hierarchyList);
    }


    //------------------------
    //引数の４つの面を同時に含むSubFaceが1つ以上存在するなら１、しないなら０を返す。
    private boolean onaji_subFace_ni_sonzai(int im1, int im2, int im3, int im4) {
        for (int i = 1; i <= SubFaceTotal; i++) {
            if (s[i].FaceId2PermutationDigit(im1) >= 1) {
                if (s[i].FaceId2PermutationDigit(im2) >= 1) {
                    if (s[i].FaceId2PermutationDigit(im3) >= 1) {
                        if (s[i].FaceId2PermutationDigit(im4) >= 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }


    //　ここは  class Jyougehyou_Syokunin  の中です。


    //SubFaceの面の重なり状態を次の状態にする。
    //もし現在の面の重なり状態が、最後のものだったら0をreturnして、面の重なり状態は最初のものに戻る。
    //zzzzzzzz

    public int next(int ss) {
        int isusumu;//=0の場合SubFaceが変わる（桁が変るようなイメージ）。
        int Sid;//変化が及んだSubFaceのid番号
        isusumu = 0;
        //ss+1番目以上のSubFaceはみな初期値にする。SubFaceに含まれる面数が0のときはエラーになる。

        for (int i = ss + 1; i <= SubFaceTotal; i++) {
            s[i].Permutation_first();
        }
        //The overlapping state of the surfaces is changed in order from the one with the largest id number of the SubFace.
        Sid = ss;
        for (int i = ss; i >= 1; i--) {
            if (isusumu == 0) {
                isusumu = s[i].next(s[i].getFaceIdCount());
                Sid = i;
            }

        }
        if (isusumu == 0) {
            Sid = 0;
        }

        return Sid;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    public String Permutation_count(int imax) {
        StringBuilder s0 = new StringBuilder();

        for (int ss = 1; ss <= imax; ss++) {
            s0.append(" : ").append(s[ss].get_Permutation_count());
        }
        return s0.toString();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    public int kanou_kasanari_sagasi_self(int ss) {
        //最終桁での処理
        if (ss == SubFace_valid_number) {

            if (s[ss].possible_overlapping_search(hierarchyList) == 1000) {//==0ということは、可能な重なりかたとなる順列は存在しない。　==1000　このSubFaceは、矛盾はない状態になっている。
                return 100;//折り畳み可能な順列組み合わせが見つかった。
            } else {
                return 0;
            }

        }


        //最終桁以外での処理
        if (s[ss].possible_overlapping_search(hierarchyList) == 1000) {//==0ということは、可能な重なりかたとなる順列は存在しない。　==1000　このSubFaceは、矛盾はない状態になっている。


            while (kanou_kasanari_sagasi_self(ss + 1) == 0) {//次の桁で可能な重なりかたとなる順列は存在しない
                if (s[ss].next() == 0) {
                    return 0;
                }//この桁では進めない（新たな順列は無い）

            }

            return 1000;//折り畳み可能な順列組み合わせが見つかった。

        }

        return 0;
    }


    //------------------------------------------------------------------------------------------------------

    //Start with the current permutation state and look for possible overlapping states. There is room for speeding up here.
    public int possible_overlapping_search() {      //This should not change the hierarchyList.
        orihime_app.bulletinBoard.write("_ _______");
        orihime_app.bulletinBoard.write("__ ______");
        orihime_app.bulletinBoard.write("___ _____");
        orihime_app.bulletinBoard.write("____ ____");
        int ms, Sid;

        Sid = 1;//The initial value of Sid can be anything other than 0.
        while (Sid != 0) { //If Sid == 0, it means that even the smallest number of SubFace has been searched.

            ms = inconsistent_subFace_request();
            if (ms == 1000) {
                return 1000;
            }//There is no contradiction in all SubFaces.
            Sid = next(ms - 1);
            orihime_app.bulletinBoard.rewrite(9, "susumu(" + ms + "-1 = )" + Sid);
        }
        return 0;//There is no possible overlapping state
    }

    //-----------------------------------------------------------------------------------------------------------------
    //Search for SubFaces that fold inconsistently in ascending order of number. There is room for speeding up here as well.
    private int inconsistent_subFace_request() { //hierarchyList changes.
        int kks;
        hierarchyList.restore();//<<<<<<<<<<<<<<<<<<<<<<<<<<<,,

        for (int ss = 1; ss <= SubFace_valid_number; ss++) {      //<<<<<<<<<<<<<<高速化のため変更。070417

            orihime_app.bulletinBoard.rewrite(7, "mujyun_Smen_motome( " + ss + ") , Menidsuu = " + s[ss].getFaceIdCount() + " , Men_pair_suu = " + s[ss].getFaceIdCount() * (s[ss].getFaceIdCount() - 1) / 2);
            orihime_app.bulletinBoard.rewrite(8, " kasanari_bunryi_mitei = " + s[ss].overlapping_classification_pending(hierarchyList));
            orihime_app.bulletinBoard.rewrite(9, " kasanari_bunryi_ketteizumi = " + s[ss].overlapping_classification_determined(hierarchyList));


            kks = s[ss].possible_overlapping_search(hierarchyList);
            orihime_app.bulletinBoard.rewrite(10, Permutation_count(ss));


            if (kks == 0) {//kks == 0 means that there is no permutation that can overlap
                return ss;
            }
            s[ss].hierarchyList_at_subFace_wo_input(hierarchyList);//Enter the top and bottom information of the ss th SubFace in hierarchyList.
        }

        if (additional_estimation() != 1000) {
            return SubFace_valid_number;
        }

        return 1000;
    }

// ---------------------------------------------------------------

    //　ここは  class Jyougehyou_Syokunin  の中です。
    //-----------------------------------------------------


    //図をかく際の数値変換用関数-----------------------------------------------------------------

    private int gx(double d) {
        return (int) d; //Front side display
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


    //---------------------------------------------------------

    public Memo getMemo_for_svg_with_camera(CreasePattern_Worker orite, PointSet subFace_figure) {//折り上がり図(hyouji_flg==5)
        int omote_ura = 0;
        if (camera.getCameraMirror() == 1.0) {
            omote_ura = 0;
        }//カメラの鏡設定が1なら、折り上がり図は表表示
        if (camera.getCameraMirror() == -1.0) {
            omote_ura = 1;
        }//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示

        Point t0 = new Point();
        Point t1 = new Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();

        ip1 = omote_ura;

        Memo memo_temp = new Memo();

        Point a = new Point();
        Point b = new Point();
        StringBuilder str_zahyou;
        String str_stroke = "black";
        String str_strokewidth = "1";


        //面を描く-----------------------------------------------------------------------------------------------------
        int[] x = new int[100];
        int[] y = new int[100];

        //SubFaceの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        for (int im = 1; im <= SubFaceTotal; im++) { //SubFaceから上からの指定した番目の面のidを求める。
            s0[im].set_FaceId2fromTop_counted_position(hierarchyList);//s0[]はSubFace_zuから得られるSubFaceそのもの、jgは上下表Jyougehyouのこと
        }
        //ここまでで、上下表の情報がSubFaceの各面に入った

        //面を描く
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
        int face_order;
        for (int im = 1; im <= SubFaceTotal; im++) {//imは各SubFaceの番号
            if (s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外

                //Determine the color of the imth SubFace when drawing a fold-up diagram
                face_order = 1;
                if (omote_ura == 1) {
                    face_order = s0[im].getFaceIdCount();
                }


                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 1) {
                    str_stroke = StringOp.toHtmlColor(F_color);
                }//g.setColor(F_color)
                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 0) {
                    str_stroke = StringOp.toHtmlColor(B_color);
                }//g.setColor(B_color)

                if (omote_ura == 1) {
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 0) {
                        str_stroke = "yellow";
                    }//g.setColor(F_color)
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 1) {
                        str_stroke = "gray";
                    }//g.setColor(B_color)
                }

                //折り上がり図を描くときのSubFaceの色を決めるのはここまで

                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める
                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, i)));
                    t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getX());
                    y[i] = gy(t1.getY());
                }

                t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getX());
                y[0] = gy(t1.getY());
                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求めるのはここまで

                //g2.fill(new Polygon(x,y,subFace_figure.getTenidsuu(im)));  //svg出力

                str_zahyou = new StringBuilder(x[0] + "," + y[0]);
                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    str_zahyou.append(" ").append(x[i]).append(",").append(y[i]);

                }

                memo_temp.addLine("<polygon points=\"" + str_zahyou + "\"" +
                        " style=\"" + "stroke:" + str_stroke + ";fill:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );
            }
        }
        //面を描く　ここまで-----------------------------------------------------------------------------------------


        //棒を描く-----------------------------------------------------------------------------------------

        str_stroke = StringOp.toHtmlColor(L_color);

        for (int ib = 1; ib <= subFace_figure.getSticksTotal(); ib++) {
            int Mid_min, Mid_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
            int Men_jyunban_min, Men_jyunban_max;//PC画面に表示したときSubFace(Mid_min) で見える面の番号がMen_jyunban_min、SubFace(Mid_max) で見える面の番号がMen_jyunban_max
            int oekaki_flg;

            oekaki_flg = 0;
            Mid_min = subFace_figure.Stick_moti_Menid_min_motome(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            Mid_max = subFace_figure.Stick_moti_Menid_max_motome(ib);

            if (s0[Mid_min].getFaceIdCount() == 0) {
                oekaki_flg = 1;
            }//menをもたない、ドーナツの穴状のSubFaceは境界の棒を描く
            else if (s0[Mid_max].getFaceIdCount() == 0) {
                oekaki_flg = 1;
            } else if (Mid_min == Mid_max) {
                oekaki_flg = 1;
            }//一本の棒の片面だけにSubFace有り
            else {
                Men_jyunban_min = 1;
                if (omote_ura == 1) {
                    Men_jyunban_min = s0[Mid_min].getFaceIdCount();
                }
                Men_jyunban_max = 1;
                if (omote_ura == 1) {
                    Men_jyunban_max = s0[Mid_max].getFaceIdCount();
                }
                if (s0[Mid_min].fromTop_count_FaceId(Men_jyunban_min) != s0[Mid_max].fromTop_count_FaceId(Men_jyunban_max)) {
                    oekaki_flg = 1;
                }//この棒で隣接するSubFaceの1番上の面は異なるので、この棒は描く。
            }

            if (oekaki_flg == 1) {//棒を描く。
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));

                a.set(s_tv.getA());
                b.set(s_tv.getB());

                BigDecimal b_ax = new BigDecimal(String.valueOf(a.getX()));
                BigDecimal b_ay = new BigDecimal(String.valueOf(a.getY()));
                BigDecimal b_bx = new BigDecimal(String.valueOf(b.getX()));
                BigDecimal b_by = new BigDecimal(String.valueOf(b.getY()));


                memo_temp.addLine("<line x1=\"" + b_ax.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " y1=\"" + b_ay.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " x2=\"" + b_bx.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " y2=\"" + b_by.setScale(2, RoundingMode.HALF_UP).doubleValue() + "\"" +
                        " style=\"" + "stroke:" + str_stroke + "\"" +
                        " stroke-width=\"" + str_strokewidth + "\"" + " />"
                );
            }
        }


        return memo_temp;
    }
    //---------------------------------------------------------


    //
    //---------------------------------------------------------
    public Memo getMemo_wirediagram_for_svg_export(CreasePattern_Worker orite, PointSet otta_Men_zu, int i_fill) {
        //System.out.println("getMemo_hariganezu_for_svg_kakidasi");
        int omote_ura = 0;
        if (camera.getCameraMirror() == 1.0) {
            omote_ura = 0;
        }//カメラの鏡設定が1なら、折り上がり図は表表示
        if (camera.getCameraMirror() == -1.0) {
            omote_ura = 1;
        }//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示

        Point t_ob = new Point();
        Point t_tv = new Point();

        ip1 = omote_ura;

        Memo memo_temp = new Memo();

        String str_stroke;
        str_stroke = "black";
        String str_strokewidth;
        str_strokewidth = "1";
        String str_fill;
        str_fill = "";

        rating2();


        //面を描く準備

        //BigDecimalのコンストラクタの引数は浮動小数点数型と文字列型どちらもok。引数が浮動小数点数型は誤差が発生。正確な値を扱うためには、引数は文字列型で指定。

        for (int i_nbox = 1; i_nbox <= otta_Men_zu.getFacesTotal(); i_nbox++) {
            int im;
            if (camera.getCameraMirror() == -1.0) {//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示
                im = nbox.backwards_get_int(i_nbox);
            } else {
                im = nbox.getInt(i_nbox);
            }

            StringBuilder text;//文字列処理用のクラスのインスタンス化

            text = new StringBuilder("M ");
            t_ob.setX(otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, 1)));
            t_ob.setY(otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, 1)));
            t_tv.set(camera.object2TV(t_ob));
            BigDecimal b_t_tv_x = new BigDecimal(String.valueOf(t_tv.getX()));
            BigDecimal b_t_tv_y = new BigDecimal(String.valueOf(t_tv.getY()));

            text.append(b_t_tv_x.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ").append(b_t_tv_y.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ");


            for (int i = 2; i <= otta_Men_zu.getPointsCount(im); i++) {
                text.append("L ");
                t_ob.setX(otta_Men_zu.getPointX(otta_Men_zu.getPointId(im, i)));
                t_ob.setY(otta_Men_zu.getPointY(otta_Men_zu.getPointId(im, i)));
                t_tv.set(camera.object2TV(t_ob));
                BigDecimal b_t_tv_x_i = new BigDecimal(String.valueOf(t_tv.getX()));
                BigDecimal b_t_tv_y_i = new BigDecimal(String.valueOf(t_tv.getY()));

                text.append(b_t_tv_x_i.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ").append(b_t_tv_y_i.setScale(2, RoundingMode.HALF_UP).doubleValue()).append(" ");
            }

            text.append("Z");

            if (i_fill == 0) {
                str_fill = "none";

            }

            if (i_fill == 1) {

                if (orite.getIFacePosition(im) % 2 == 1) {
                    str_fill = StringOp.toHtmlColor(F_color);
                }
                if (orite.getIFacePosition(im) % 2 == 0) {
                    str_fill = StringOp.toHtmlColor(B_color);
                }

                if (omote_ura == 1) {
                    if (orite.getIFacePosition(im) % 2 == 1) {
                        str_fill = StringOp.toHtmlColor(B_color);
                    }
                    if (orite.getIFacePosition(im) % 2 == 0) {
                        str_fill = StringOp.toHtmlColor(F_color);
                    }


                }


            }

            memo_temp.addLine("<path d=\"" + text + "\"" +
                    " style=\"" + "stroke:" + str_stroke + "\"" +
                    " stroke-width=\"" + str_strokewidth + "\"" +
                    //" fill=\"none\"" +" />"
                    " fill=\"" + str_fill + "\"" + " />"
            );


        }

        return memo_temp;

    }


    //---------------------------------------------------------
    public void draw_transparency_with_camera(Graphics g, PointSet otta_Face_figure, PointSet subFace_figure, boolean transparencyColor, int transparency_toukado) {
        Graphics2D g2 = (Graphics2D) g;

        Point t0 = new Point();
        Point t1 = new Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();

        //Preparing to draw a face
        int[] x = new int[100];
        int[] y = new int[100];

        //Find the proper darkness of the surface
        int col_hiku = 0;
        int colmax = 255;
        int colmin = 30;//colmax=255(真っ白)以下、colmin=0(真っ黒)以上
        //Menidsuu_max must be 1 or greater
        if (FaceIdCount_max > 0) {
            col_hiku = (colmax - colmin) / FaceIdCount_max;
        }

        if (transparencyColor) {//カラーの透過図

            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), transparency_toukado));

            //Draw a face
            for (int im = 1; im <= otta_Face_figure.getFacesTotal(); im++) {
                for (int i = 1; i <= otta_Face_figure.getPointsCount(im) - 1; i++) {
                    t0.setX(otta_Face_figure.getPointX(otta_Face_figure.getPointId(im, i)));
                    t0.setY(otta_Face_figure.getPointY(otta_Face_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getX());
                    y[i] = gy(t1.getY());
                }

                t0.setX(otta_Face_figure.getPointX(otta_Face_figure.getPointId(im, otta_Face_figure.getPointsCount(im))));
                t0.setY(otta_Face_figure.getPointY(otta_Face_figure.getPointId(im, otta_Face_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getX());
                y[0] = gy(t1.getY());
                g.fillPolygon(x, y, otta_Face_figure.getPointsCount(im));
            }

            //Preparing to draw a stick

            if (i_anti_alias == 1) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//Anti-alias on
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }
            if (i_anti_alias == 0) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//Anti-alias off
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }

            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), 2 * transparency_toukado));
            //Draw a stick
            for (int ib = 1; ib <= subFace_figure.getSticksTotal(); ib++) {
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //直線
            }
        } else {//Black and white transparent view (old style)
            int col_kosa;

            for (int im = 1; im <= subFace_figure.getFacesTotal(); im++) {
                col_kosa = colmax - col_hiku * (s0[im].getFaceIdCount());

                if (col_kosa > 255) {
                    col_kosa = 255;
                }

                if (col_kosa < 0) {
                    col_kosa = 0;
                }
                g.setColor(new Color(col_kosa, col_kosa, col_kosa));

                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, i)));
                    t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getX());
                    y[i] = gy(t1.getY());
                }

                t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getX());
                y[0] = gy(t1.getY());
                g.fillPolygon(x, y, subFace_figure.getPointsCount(im));
            }

            //Prepare the stick
            g.setColor(Color.black);

            if (i_anti_alias == 1) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//Anti-alias on
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }
            if (i_anti_alias == 0) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//Anti-alias off
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }


            //Draw a stick
            for (int ib = 1; ib <= subFace_figure.getSticksTotal(); ib++) {
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line
            }
        }
    }


    //---------------------------------------------------------

    public void draw_foldedFigure_with_camera(Graphics g, CreasePattern_Worker orite, PointSet subFace_figure) {
        Graphics2D g2 = (Graphics2D) g;
        int omote_ura = 0;
        if (camera.getCameraMirror() == 1.0) {
            omote_ura = 0;
        }//カメラの鏡設定が1なら、折り上がり図は表表示
        if (camera.getCameraMirror() == -1.0) {
            omote_ura = 1;
        }//カメラの鏡設定が-1(x軸の符号を反転)なら、折り上がり図は裏表示
        //System.out.println("上下表職人　oekaki_with_camera+++++++++++++++折紙表示　面を描く");
        Point t0 = new Point();
        Point t1 = new Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();
        ip1 = omote_ura;
        //  System.out.println(Smensuu);

        //面を描く
        int[] x = new int[100];
        int[] y = new int[100];

        double[] xd = new double[100];
        double[] yd = new double[100];


        //if(hyouji_flg==5){//折紙表示---------------------------------------------------------------------------

        //SubFaceの.set_Menid2uekara_kazoeta_itiは現在の上下表をもとに、上から数えてi番めの面のid番号を全ての順番につき格納する。
        for (int im = 1; im <= SubFaceTotal; im++) { //SubFaceから上からの指定した番目の面のidを求める。
            s0[im].set_FaceId2fromTop_counted_position(hierarchyList);//s0[]はSubFace_zuから得られるSubFaceそのもの、hierarchyListは上下表Jyougehyouのこと
        }
        //ここまでで、上下表の情報がSubFaceの各面に入った


        //面を描く-----------------------------------------------------------------------------------------------------
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ

        int Men_jyunban;
        for (int im = 1; im <= SubFaceTotal; im++) {//imは各SubFaceの番号
            if (s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外

                //折り上がり図を描くときのim番目のSubFaceの色を決める
                Men_jyunban = 1;
                if (omote_ura == 1) {
                    Men_jyunban = s0[im].getFaceIdCount();
                }

                //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==1){g.setColor(new Color(255,255,50));}
                //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==0){g.setColor(new Color(233,233,233));}
                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(Men_jyunban)) % 2 == 1) {
                    g.setColor(F_color);
                }
                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(Men_jyunban)) % 2 == 0) {
                    g.setColor(B_color);
                }


                if (omote_ura == 1) {
                    //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==0){g.setColor(new Color(255,255,50));}
                    //if(orite.getiMeniti(s0[im].uekara_kazoeta_Menid(Men_jyunban))%2==1){g.setColor(new Color(233,233,233));}

                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(Men_jyunban)) % 2 == 0) {
                        g.setColor(F_color);
                    }
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(Men_jyunban)) % 2 == 1) {
                        g.setColor(B_color);
                    }
                }


                //折り上がり図を描くときのSubFaceの色を決めるのはここまで

                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める

                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {

                    t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, i)));
                    t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getX());
                    y[i] = gy(t1.getY());
                    //x[i]=gx(subFace_figure.getTenx(subFace_figure.getTenid(im,i)));
                    //y[i]=gy(subFace_figure.getTeny(subFace_figure.getTenid(im,i)));
                }

                t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getX());
                y[0] = gy(t1.getY());
                //x[0]=gx(subFace_figure.getTenx(subFace_figure.getTenid(im,subFace_figure.getTenidsuu(im))));
                //y[0]=gy(subFace_figure.getTeny(subFace_figure.getTenid(im,subFace_figure.getTenidsuu(im))));

                //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求めるのはここまで

                g2.fill(new Polygon(x, y, subFace_figure.getPointsCount(im)));
            }
        }
        //面を描く　ここまで


        //影をつける ------------------------------------------------------------------------------------
        if (displayShadows) {
            for (int ib = 1; ib <= subFace_figure.getSticksTotal(); ib++) {
                int im = bou_no_bangou_kara_kagenoaru_subFace_no_bangou_wo_motomeru(ib, subFace_figure, omote_ura);//影をつけるSubFaceのid
                if (im != 0) {//影を描く。

                    //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める


                    //棒の座標   subFace_figure.getmaex(ib),subFace_figure.getmaey(ib)   -    subFace_figure.getatox(ib) , subFace_figure.getatoy(ib)
                    Point b_begin = new Point(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib));
                    Point b_end = new Point(subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                    double b_length = b_begin.distance(b_end);

                    //棒と直交するベクトル
                    double o_btx = -(subFace_figure.getBeginY(ib) - subFace_figure.getEndY(ib)) * 10.0 / b_length;//棒と直交するxベクトル
                    double o_bty = (subFace_figure.getBeginX(ib) - subFace_figure.getEndX(ib)) * 10.0 / b_length;//棒と直交するyベクトル

                    //棒の中点
                    double o_bmx, o_bmy;
                    double t_bmx, t_bmy;

                    o_bmx = (subFace_figure.getBeginX(ib) + subFace_figure.getEndX(ib)) / 2.0;
                    o_bmy = (subFace_figure.getBeginY(ib) + subFace_figure.getEndY(ib)) / 2.0;

                    t0.setX(o_bmx);
                    t0.setY(o_bmy);
                    t1.set(camera.object2TV(t0));
                    t_bmx = t1.getX();
                    t_bmy = t1.getY();

                    //棒の中点を通る直交線上の点
                    double o_bmtx, o_bmty;
                    double t_bmtx, t_bmty;


                    //棒の中点を通る直交線上の点
                    o_bmtx = o_bmx + o_btx;
                    o_bmty = o_bmy + o_bty;

                    if (subFace_figure.inside(new Point(o_bmx + 0.01 * o_btx, o_bmy + 0.01 * o_bty), im) != 0) {//0=外部、　1=境界、　2=内部


                        t0.setX(o_bmtx);
                        t0.setY(o_bmty);
                        t1.set(camera.object2TV(t0));
                        t_bmtx = t1.getX();
                        t_bmty = t1.getY();


                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setX(subFace_figure.getBeginX(ib));
                        t0.setY(subFace_figure.getBeginY(ib));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getX();
                        yd[0] = t1.getY();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setX(subFace_figure.getBeginX(ib) + o_btx);
                        t0.setY(subFace_figure.getBeginY(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getX();
                        yd[1] = t1.getY();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setX(subFace_figure.getEndX(ib) + o_btx);
                        t0.setY(subFace_figure.getEndY(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getX();
                        yd[2] = t1.getY();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setX(subFace_figure.getEndX(ib));
                        t0.setY(subFace_figure.getEndY(ib));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getX();
                        yd[3] = t1.getY();
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


                    if (subFace_figure.inside(new Point(o_bmx + 0.01 * o_btx, o_bmy + 0.01 * o_bty), im) != 0) {//0=外部、　1=境界、　2=内部

                        t0.setX(o_bmtx);
                        t0.setY(o_bmty);
                        t1.set(camera.object2TV(t0));


                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setX(subFace_figure.getBeginX(ib));
                        t0.setY(subFace_figure.getBeginY(ib));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getX();
                        yd[0] = t1.getY();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setX(subFace_figure.getBeginX(ib) + o_btx);
                        t0.setY(subFace_figure.getBeginY(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getX();
                        yd[1] = t1.getY();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setX(subFace_figure.getEndX(ib) + o_btx);
                        t0.setY(subFace_figure.getEndY(ib) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getX();
                        yd[2] = t1.getY();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setX(subFace_figure.getEndX(ib));
                        t0.setY(subFace_figure.getEndY(ib));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getX();
                        yd[3] = t1.getY();
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

        for (int ib = 1; ib <= subFace_figure.getSticksTotal(); ib++) {

            int Mid_min, Mid_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
            int Men_jyunban_min, Men_jyunban_max;//PC画面に表示したときSubFace(Mid_min) で見える面の番号がMen_jyunban_min、SubFace(Mid_max) で見える面の番号がMen_jyunban_max
            int oekaki_flg;

            oekaki_flg = 0;
            Mid_min = subFace_figure.Stick_moti_Menid_min_motome(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            Mid_max = subFace_figure.Stick_moti_Menid_max_motome(ib);

            if (s0[Mid_min].getFaceIdCount() == 0) {
                oekaki_flg = 1;
            }//menをもたない、ドーナツの穴状のSubFaceは境界の棒を描く
            else if (s0[Mid_max].getFaceIdCount() == 0) {
                oekaki_flg = 1;
            } else if (Mid_min == Mid_max) {
                oekaki_flg = 1;
            }//一本の棒の片面だけにSubFace有り
            else {
                Men_jyunban_min = 1;
                if (omote_ura == 1) {
                    Men_jyunban_min = s0[Mid_min].getFaceIdCount();
                }
                Men_jyunban_max = 1;
                if (omote_ura == 1) {
                    Men_jyunban_max = s0[Mid_max].getFaceIdCount();
                }
                if (s0[Mid_min].fromTop_count_FaceId(Men_jyunban_min) != s0[Mid_max].fromTop_count_FaceId(Men_jyunban_max)) {
                    oekaki_flg = 1;
                }//この棒で隣接するSubFaceの1番上の面は異なるので、この棒は描く。
            }

            if (oekaki_flg == 1) {//棒を描く。
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                //g2.draw(new Line2D.Double( gx(s_tv.getax()),gy(s_tv.getay()),gx(s_tv.getbx()),gy(s_tv.getby())));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //直線
                //g.drawLine( gx(subFace_figure.getmaex(ib)),gy(subFace_figure.getmaey(ib)),gx(subFace_figure.getatox(ib)),gy(subFace_figure.getatoy(ib))); //直線
            }


        }
    }


    //---------------------------------------------------------
    public void draw_cross_with_camera(Graphics g) {
        //System.out.println("折り上がり図の動かし中心の十字表示");
        //System.out.println("上下表職人　oekaki_jyuuji_with_camera+++++++++++++++折り上がり図の動かし中心の十字表示");
        OritaDrawing OO = new OritaDrawing();

        //camera中心を十字で描く
        OO.cross(g, camera.object2TV(camera.get_camera_position()), 5.0, 2.0, LineType.ORANGE_4);
    }


    //---------------------------------------------------------

    public void changeDisplayShadows() {
        displayShadows = !displayShadows;
    }


    //---------------------------------------------------------

    public int bou_no_bangou_kara_kagenoaru_subFace_no_bangou_wo_motomeru(int ib, PointSet subFace_figure, int omote_ura) {//棒の番号から、その棒の影が発生するSubFace の番号を求める。影が発生しない場合は0を返す。

        int i_return;

        int Mid_min, Mid_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
        int Men_jyunban_min, Men_jyunban_max;//PC画面に表示したときSubFace(Mid_min) で見える面の、そのSubFaceでの重なり順がMen_jyunban_min、SubFace(Mid_max) で見える面のそのSubFaceでの重なり順がMen_jyunban_max

        Mid_min = subFace_figure.Stick_moti_Menid_min_motome(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
        Mid_max = subFace_figure.Stick_moti_Menid_max_motome(ib);

        if (s0[Mid_min].getFaceIdCount() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のSubFaceとの境界の棒には影なし
        if (s0[Mid_max].getFaceIdCount() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のFaceStackとの境界の棒には影なし
        if (Mid_min == Mid_max) {
            return 0;
        }//一本の棒の片面だけにFaceStack有り

        Men_jyunban_min = 1;
        if (omote_ura == 1) {
            Men_jyunban_min = s0[Mid_min].getFaceIdCount();
        }
        Men_jyunban_max = 1;
        if (omote_ura == 1) {
            Men_jyunban_max = s0[Mid_max].getFaceIdCount();
        }

        int Mid_min_mieteru_men_id = s0[Mid_min].fromTop_count_FaceId(Men_jyunban_min);
        int Mid_max_mieteru_men_id = s0[Mid_max].fromTop_count_FaceId(Men_jyunban_max);
        if (Mid_min_mieteru_men_id == Mid_max_mieteru_men_id) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面が同じなので、棒自体が描かれず影もなし。


        //Jyougehyou
        //public int get(int i,int j){return hierarchyList[i][j];}
        //　hierarchyList[i][j]が1なら面iは面jの上側。0なら下側。
        //  hierarchyList[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
        //hierarchyList[i][j]が-100なら、面iとjは重なるところがない。
        if (hierarchyList.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == -50) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面の上下関係不明なので、影はなし
        if (hierarchyList.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == -100) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面の上下関係ない（重ならない）ので、影はなし

        i_return = Mid_min;
        if (hierarchyList.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == 1) {
            i_return = Mid_max;
        }

        if (omote_ura == 1) {
            if (i_return == Mid_min) {
                return Mid_max;
            } else {
                return Mid_min;
            }
        }

        return i_return;
    }

// -----------------------------


//-----------------------------------

    int makesuu0no_menno_kazu = 0;//上に他の面がない状態で順位付けできる面の数
    int makesuu1ijyouno_menno_kazu = 0;//上に他の面が1以上ある状態でないと順位付けできない面の数

    private void rating2() {
        int hierarchyListFacesTotal = hierarchyList.getFacesTotal();//面の総数を求める。
        face_rating = new double[hierarchyListFacesTotal + 1];

        i_face_rating = new int[hierarchyListFacesTotal + 1];


        makesuu0no_menno_kazu = 0;//上に他の面がない状態で順位付けできる面の数
        makesuu1ijyouno_menno_kazu = 0;//上に他の面が1以上ある状態でないと順位付けできない面の数


        for (int i = 0; i <= hierarchyListFacesTotal; i++) {
            i_face_rating[i] = 0;
        }

        // Find the topmost surface in order from 1 on the s surface (excluding the rated surface).
        // Find the number of faces (excluding the rated faces) on the s plane in order from 1 and find the total.
        // Find the surface with the smallest number of surfaces on that surface (excluding the surface with a rate) and give a rate
        for (int i = 1; i <= hierarchyListFacesTotal; i++) {
            int i_rate = 1 + hierarchyListFacesTotal - i;

            int top_men_id = get_top_face_id_without_rated_face();

            i_face_rating[top_men_id] = i_rate;
            face_rating[top_men_id] = i_rate;
        }

        System.out.println("上に他の面がない状態で順位付けできた面の数 = " + makesuu0no_menno_kazu);
        System.out.println("上に他の面が1以上ある状態で順位付けした面の数 = " + makesuu1ijyouno_menno_kazu);

        nbox.reset();
        for (int i = 1; i <= hierarchyList.getFacesTotal(); i++) {
            nbox.container_i_smallest_first(new int_double(i, face_rating[i]));
        }
    }
//------------------------------------------------------------
//以下の各関数ではFaceStackとしてs0[]を使う20180305


    private int top_face_id_ga_maketa_kazu_goukei_without_rated_face = 0;

    //------------------------------------------------------------
    private int get_top_face_id_without_rated_face() {
        int top_men_id = 0;
        top_face_id_ga_maketa_kazu_goukei_without_rated_face = hierarchyList.getFacesTotal() + 100;


        int hierarchyListFacesTotal = hierarchyList.getFacesTotal();//Find the total number of faces.

        int[] i_kentouzumi = new int[hierarchyListFacesTotal + 1];//検討済みの面IDは１にする
        for (int i = 0; i <= hierarchyListFacesTotal; i++) {
            i_kentouzumi[i] = 0;
        }

        for (int i = 1; i <= SubFaceTotal; i++) {
            int s_top_id = get_s_top_id_without_rated_men(i);//各s面の（レートがついた面は除く）一番上の面。s_top_id=0ならそのs面にはレートが未定の面はない


            if (s_top_id != 0) {
                if (i_kentouzumi[s_top_id] == 0) {
                    int mkg = get_maketa_kazu_goukei_without_rated_face(s_top_id);
                    if (mkg == 0) {
                        makesuu0no_menno_kazu = makesuu0no_menno_kazu + 1;
                        return s_top_id;
                    }//ここは、これでよいか要検討20180306
                    if (top_face_id_ga_maketa_kazu_goukei_without_rated_face > mkg) {
                        top_face_id_ga_maketa_kazu_goukei_without_rated_face = mkg;
                        top_men_id = s_top_id;
                    }
                }
            }

            i_kentouzumi[s_top_id] = 1;
        }

        //makesuu0no_menno_kazu=0;//上に他の面がない状態で順位付けできる面の数
        //makesuu1ijyouno_menno_kazu=0;//上に他の面が1以上ある状態でないと順位付けできない面の数
        if (top_face_id_ga_maketa_kazu_goukei_without_rated_face == 0) {
            makesuu0no_menno_kazu = makesuu0no_menno_kazu + 1;
        } else if (top_face_id_ga_maketa_kazu_goukei_without_rated_face > 0) {
            makesuu1ijyouno_menno_kazu = makesuu1ijyouno_menno_kazu + 1;
        }

        return top_men_id;
    }

//-----------------------------------

    private int get_s_top_id_without_rated_men(int ism) {//ismはs面のid
        int Mensuu = s0[ism].getFaceIdCount();//FaceStackでの面数//FaceStack s0[];//FaceStack_figureから得られるFaceStack
        for (int jyunban = 1; jyunban <= Mensuu; jyunban++) {
            int im = s0[ism].fromTop_count_FaceId(jyunban);
            if (i_face_rating[im] == 0) {
                return im;
            }
        }
        return 0;
    }

// -----------------------------

    private int get_maketa_kazu_goukei_without_rated_face(int men_id) {
        int i_make = 0;
        for (int ism = 1; ism <= SubFaceTotal; ism++) {
            i_make = i_make + get_subFace_de_maketa_kazu_without_rated_Face(ism, men_id);
            if (i_make >= top_face_id_ga_maketa_kazu_goukei_without_rated_face) {
                return i_make;
            }//20180306高速化のためこの１行を入れているが、本当に効果があるかは不明。この行だけコメントアウトしても正常には動くはず。

        }
        return i_make;
    }


    //-----------------------------------
    private int get_subFace_de_maketa_kazu_without_rated_Face(int ism, int men_id) {//ismはFaceStackのid
        int FaceCount = s0[ism].getFaceIdCount();//FaceStackでの面数//FaceStack s0[];//FaceStack_figureから得られるFaceStack
        int maketa_kazu = 0;

        for (int i = 1; i <= FaceCount; i++) {
            int im = s0[ism].fromTop_count_FaceId(i);
            if (im == men_id) {
                return maketa_kazu;
            }
            if (i_face_rating[im] == 0) {
                maketa_kazu = maketa_kazu + 1;
            }
        }
        return 0;
    }
}     



