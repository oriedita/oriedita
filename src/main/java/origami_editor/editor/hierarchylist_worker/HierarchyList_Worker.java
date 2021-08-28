package origami_editor.editor.hierarchylist_worker;

import origami_editor.editor.hierarchylist_worker.hierarchylist.HierarchyList;
import origami_editor.editor.hierarchylist_worker.hierarchylist.equivalence_condition.EquivalenceCondition;
import origami_editor.editor.hierarchylist_worker.subface.SubFace;
import origami_editor.editor.creasepattern_worker.CreasePattern_Worker;
import origami_editor.graphic2d.linesegment.LineSegment;
import origami_editor.graphic2d.oritaoekaki.OritaDrawing;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import origami_editor.graphic2d.point.Point;
import origami_editor.record.memo.Memo;
import origami_editor.record.string_op.StringOp;
import origami_editor.tools.camera.Camera;
import origami_editor.tools.pointset.PointSet;
import origami_editor.editor.App;
import origami_editor.editor.LineColor;
import origami_editor.sortingbox.SortingBox_int_double;
import origami_editor.sortingbox.int_double;
import origami_editor.graphic2d.polygon.Polygon;

//HierarchyList: Record and utilize what kind of vertical relationship the surface of the developed view before folding will be after folding.
public class HierarchyList_Worker {
    HierarchyList hierarchyList = new HierarchyList();
    int SubFaceTotal;//SubFaceの数
    int SubFace_valid_number;//SubFaceは全て調べなくても、Faceの上下関係は網羅できる。Faceの上下関係を網羅するのに必要なSubFaceの数が優先順位の何番目までかをさがす。
    int FaceIdCount_max;//各SubFaceの持つMenidsuuの最大値。すなわち、最も紙に重なりが多いところの枚数。
    //paint 用のint格納用VVVVVVVVVVVVVVVVVVVVVV
    boolean ip1_flipped = false; //0 is the mode to display the front side of the folding diagram. 1 is a mode to display the back side of the folding diagram.
    //  hierarchyList[][]は折る前の展開図のすべての面同士の上下関係を1つの表にまとめたものとして扱う
    //　hierarchyList[i][j]が1なら面iは面jの上側。0なら下側。
    //  hierarchyList[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
    //hierarchyList[i][j]が-100なら、面iとjは重なるところがない。

    SubFace[] s0;//SubFace obtained from SubFace_figure
    SubFace[] s;//s is s0 sorted in descending order of priority.
    int[] s0_no_yusenjyun;
    int[] yusenjyun_kara_s0id;

    boolean displayShadows = false; //Whether to display shadows. 0 is not displayed, 1 is displayed

    Camera camera = new Camera();

    App app;

    public double[] face_rating;
    public int[] i_face_rating;

    public SortingBox_int_double nbox = new SortingBox_int_double();//20180227　nboxにはmenのidがmen_ratingと組になって、men_ratingの小さい順に並べ替えられて入っている。

    public HierarchyList_Worker(App app0) {
        app = app0;
        reset();
    }

    public void reset() {
        hierarchyList.reset();
        SubFaceTotal = 0;
        SubFace_valid_number = 0;
        ip1_flipped = false;
        FaceIdCount_max = 0;
        camera.reset();
    }

    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);
    }

    public int getSubFace_valid_number() {
        return SubFace_valid_number;
    }

    //　ここは  class Jyougehyou_Syokunin  の中です。
    //上下表の初期設定。展開図に1頂点から奇数の折線がでる誤りがある場合0を返す。それが無ければ1000を返す。
    //展開図に山谷折線の拡張による誤りがある場合2を返す。

    public void SubFace_configure(PointSet otta_Face_figure, PointSet SubFace_figure) {//js.Jyougehyou_settei(ts1,ts2.get(),ts3.get());
        // Make an upper and lower table of faces (the faces in the unfolded view before folding).
        // This includes the point set of ts2 (which has information on the positional relationship of the faces after folding) and <-------------otta_Face_figure
        // Use the point set of ts3 (which has the information of SubFace whose surface is subdivided in the wire diagram). <-------------SubFace_figure
        // Also, use the information on the positional relationship of the surface when folded, which ts1 has.

        System.out.println("Smenの初期設定");
        reset();
        SubFaceTotal = SubFace_figure.getNumFaces();

        s0 = new SubFace[SubFaceTotal + 1];
        s = new SubFace[SubFaceTotal + 1];
        s0_no_yusenjyun = new int[SubFaceTotal + 1];
        yusenjyun_kara_s0id = new int[SubFaceTotal + 1];

        for (int i = 0; i < SubFaceTotal + 1; i++) {
            s0[i] = new SubFace(app);
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
        otta_Face_figure.LineFaceMaxMinCoordinate();//tttttttttt

        int[] s0addFaceId = new int[otta_Face_figure.getNumFaces() + 1];  //SubFaceに追加する面を一時記録しておく

        for (int i = 1; i <= SubFaceTotal; i++) {
            int s0addFaceTotal = 0;

            for (int j = 1; j <= otta_Face_figure.getNumFaces(); j++) {

                if (otta_Face_figure.simple_inside(subFace_insidePoint[i], j) == Polygon.Intersection.INSIDE) {
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

    public enum HierarchyListStatus {
        UNKNOWN_N1,
        UNKNOWN_0,
        UNKNOWN_1,
        UNKNOWN_2,
        UNKNOWN_3,
        UNKNOWN_4,
        UNKNOWN_1000,
    }

    public HierarchyListStatus HierarchyList_configure(CreasePattern_Worker orite, PointSet otta_face_figure) {
        app.bulletinBoard.write("           Jyougehyou_settei   step1   start ");
        HierarchyListStatus ireturn = HierarchyListStatus.UNKNOWN_1000;
        hierarchyList.setFacesTotal(otta_face_figure.getNumFaces());

        //Put the hierarchical relationship determined from the information of mountain folds and valley folds in the table above and below.
        System.out.println("山折り谷折りの情報から決定される上下関係を上下表に入れる");
        int faceId_min, faceId_max;
        for (int ib = 1; ib <= orite.getNumLines(); ib++) {
            faceId_min = orite.lineInFaceBorder_min_request(ib);
            faceId_max = orite.lineInFaceBorder_max_request(ib);
            if (faceId_min != faceId_max) {//In the developed view, there are faces on both sides of the rod ib.
                if (otta_face_figure.getColor(ib) == LineColor.RED_1) {//Red line means mountain fold
                    if (orite.getIFacePosition(faceId_min) % 2 == 1) {//The surface Mid_min has the same orientation as the reference surface (the surface faces up)
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                        hierarchyList.set(faceId_max, faceId_min, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                    }
                    if (orite.getIFacePosition(faceId_max) % 2 == 1) {//The surface Mid_max has the same orientation as the reference surface (the surface faces up)
                        hierarchyList.set(faceId_max, faceId_min, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                    }
                }
                if (otta_face_figure.getColor(ib) == LineColor.BLUE_2) {//The blue line means valley fold
                    if (orite.getIFacePosition(faceId_min) % 2 == 1) {//面Mid_minは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                        hierarchyList.set(faceId_max, faceId_min, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                    }
                    if (orite.getIFacePosition(faceId_max) % 2 == 1) {//面Mid_maxは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(faceId_max, faceId_min, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                    }
                }

                if ((orite.getIFacePosition(faceId_min) % 2 == 0) && (orite.getIFacePosition(faceId_max) % 2 == 0)) {
                    ireturn = HierarchyListStatus.UNKNOWN_0;
                }
                if ((orite.getIFacePosition(faceId_min) % 2 == 1) && (orite.getIFacePosition(faceId_max) % 2 == 1)) {
                    ireturn = HierarchyListStatus.UNKNOWN_0;
                }
            }
        }

        //----------------------------------------------
        app.bulletinBoard.write("           Jyougehyou_settei   step2   start ");
        System.out.println("等価条件を設定する   ");
        //等価条件を設定する。棒ibを境界として隣接する2つの面im1,im2が有る場合、折り畳み推定した場合に
        //棒ibの一部と重なる位置に有る面imは面im1と面im2に上下方向で挟まれることはない。このことから
        //gj[im1][im]=gj[im2][im]という等価条件が成り立つ。
        for (int ib = 1; ib <= orite.getNumLines(); ib++) {
            faceId_min = orite.lineInFaceBorder_min_request(ib);
            faceId_max = orite.lineInFaceBorder_max_request(ib);
            if (faceId_min != faceId_max) {//展開図において、棒ibの両脇に面がある
                for (int im = 1; im <= hierarchyList.getFacesTotal(); im++) {
                    if ((im != faceId_min) && (im != faceId_max)) {
                        if (otta_face_figure.simple_convex_inside(ib, im)) {
                            //下の２つのifは暫定的な処理。あとで置き換え予定
                            if (otta_face_figure.convex_inside(0.5, ib, im)) {
                                if (otta_face_figure.convex_inside(-0.5, ib, im)) {
                                    hierarchyList.addEquivalenceCondition(im, faceId_min, im, faceId_max);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.print("３面が関与する突き抜け条件の数　＝　");
        System.out.println(hierarchyList.getEquivalenceConditionTotal());
        app.bulletinBoard.write("           Jyougehyou_settei   step3   start ");
        // Add equivalence condition. There are two adjacent faces im1 and im2 as the boundary of the bar ib,
        // Also, there are two adjacent faces im3 and im4 as the boundary of the bar jb, and when ib and jb are parallel and partially overlap, when folding is estimated.
        // The surface of the bar ib and the surface of the surface jb are not aligned with i, j, i, j or j, i, j, i. If this happens,
        // Since there is a mistake in the 3rd place from the beginning, find the number of digits in this 3rd place with SubFace and advance this digit by 1.
        int mi1, mi2, mj1, mj2;

        for (int ib = 1; ib <= orite.getNumLines() - 1; ib++) {
            for (int jb = ib + 1; jb <= orite.getNumLines(); jb++) {
                if (otta_face_figure.parallel_overlap(ib, jb)) {
                    mi1 = orite.lineInFaceBorder_min_request(ib);
                    mi2 = orite.lineInFaceBorder_max_request(ib);
                    if (mi1 != mi2) {
                        mj1 = orite.lineInFaceBorder_min_request(jb);
                        mj2 = orite.lineInFaceBorder_max_request(jb);
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

        app.bulletinBoard.write("           Jyougehyou_settei   step4   start ");
        //Additional estimation

        HierarchyListStatus additional = additional_estimation();
        if (additional != HierarchyListStatus.UNKNOWN_1000) {
            return additional;
        }

        System.out.println("追加推定 終了し、上下表を保存------------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        //*************Saving the results of the first deductive reasoning**************************
        hierarchyList.save();//Save the hierarchical relationship determined from the mountain fold and valley fold information.
        //************************************************************************
        app.bulletinBoard.write("           Jyougehyou_settei   step5   start ");
        //Make a guidebook for each SubFace
        System.out.println("Smen毎に案内書を作る");
        for (int i = 1; i <= SubFaceTotal; i++) {
            s0[i].setGuideMap(hierarchyList);
        }

        //s0に優先順位をつける(このときhierarchyListの-100のところが変るところがある)
        System.out.println("Smen(s0)に優先順位をつける");
        //まず、他のSubFaceに丸ごと含まれているSubFaceを除外する

        int[] uniquenessOfSubFace = new int[SubFaceTotal + 1];  //<<<<<<<<<<<<<<<SubFaceの独自性
        for (int i = 1; i <= SubFaceTotal; i++) {
            uniquenessOfSubFace[i] = 1;
        }
        for (int i = 1; i <= SubFaceTotal; i++) {
            uniquenessOfSubFace[i] = 1;
            for (int j = 1; j <= SubFaceTotal; j++) {
                if (uniquenessOfSubFace[j] == 1) {

                    if (i != j) {//s0[j]がs0[i]を含むかをみる。
                        if (subFace_i_ga_j_ni_included(i, j)) {
                            uniquenessOfSubFace[i] = 0;
                            break;
                        }
                    }
                }
            }
        }

        int[] i_priority_max = new int[SubFaceTotal + 1];     //<<<<<<<<<<<<<<<臨時

        for (int i = 1; i <= SubFaceTotal; i++) {//優先度i番目のSubFaceIdをさがす。
            int priority_max = -10000;//優先度i番目の優先度の値（大きいほうが優先度が高い）。
            int i_yusen = 0;

            for (int is0 = 1; is0 <= SubFaceTotal; is0++) { //SubFaceを１からSubFaceTotal番目までサーチ
                int Sy;//SubFaceId_yusendo(is0)+uniquenessOfSubFace[is0] を格納
                if (s0_no_yusenjyun[is0] == 0) {//まだ優先順位がついていないSubFaceだけを扱う
                    Sy = subFaceId_priority(is0)/*+uniquenessOfSubFace[is0]*/;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    if (priority_max < Sy) {
                        priority_max = Sy;
                        i_yusen = is0;//The number of the most promising candidates when i_yusen is looking for the i-th priority
                    }
                    if (priority_max == Sy) {
                        if (s0[i_yusen].getFaceIdCount() < s0[is0].getFaceIdCount()) {
                            i_yusen = is0;
                        }
                    }
                }
            }

            s0_no_yusenjyun[i_yusen] = i; //優先度i番目のSubFaceIdはi_yusen。
            i_priority_max[i_yusen] = priority_max;//優先度i番目の優先度の値（大きいほうが優先度が高い）。

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
            if (i_priority_max[yusenjyun_kara_s0id[i]] != 0) {
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

        //Change the value of the position of the combination of overlapping faces of hierarchyList [] [] from -100 to -50.
        for (int k = 1; k <= SubFaceTotal; k++) {
            for (int i = 1; i <= s[k].getFaceIdCount() - 1; i++) {
                for (int j = i + 1; j <= s[k].getFaceIdCount(); j++) {
                    hierarchyList.set(i, j, HierarchyList.HierarchyListCondition.UNKNOWN_N50);
                    hierarchyList.set(j, i, HierarchyList.HierarchyListCondition.UNKNOWN_N50);
                }
            }
        }


        System.out.println("上下表初期設定終了");
        return ireturn;
    }

    //------------------------------------------------------------
    public HierarchyListStatus additional_estimation() {
        //We will infer relationships that can be further determined from the information on mountain folds and valley folds. 。

        int Mid;//The side that comes in the middle when comparing the three sides
        int flg_c = 1;
        System.out.println("追加推定開始---------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        while (flg_c >= 1) {
            flg_c = 0;
            System.out.println("追加推定------------------------");

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
                                    if (hierarchyList.get(Mid, s0[iS].getFaceId(i)) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                                        ueMenid_max = ueMenid_max + 1;
                                        ueMenid[ueMenid_max] = s0[iS].getFaceId(i);
                                    }
                                    if (hierarchyList.get(Mid, s0[iS].getFaceId(i)) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                                        sitaMenid_max = sitaMenid_max + 1;
                                        sitaMenid[sitaMenid_max] = s0[iS].getFaceId(i);
                                    }
                                }
                            }

                            for (int iuM = 1; iuM <= ueMenid_max; iuM++) {//Menid[iM]より上にある面。
                                for (int isM = 1; isM <= sitaMenid_max; isM++) {//Menid[iM]より下にある面。

                                    if (hierarchyList.get(ueMenid[iuM], sitaMenid[isM]) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                                        return HierarchyListStatus.UNKNOWN_2;
                                    }//面の上下関係の拡張で矛盾発生。
                                    if (hierarchyList.get(sitaMenid[isM], ueMenid[iuM]) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                                        return HierarchyListStatus.UNKNOWN_2;
                                    }//面の上下関係の拡張で矛盾発生。

                                    if (hierarchyList.get(ueMenid[iuM], sitaMenid[isM]).isEmpty()) {
                                        hierarchyList.set(ueMenid[iuM], sitaMenid[isM], HierarchyList.HierarchyListCondition.UNKNOWN_1);
                                        flg_a++;
                                        flg_b++;
                                        flg_c++;
                                    }
                                    if (hierarchyList.get(sitaMenid[isM], ueMenid[iuM]).isEmpty()) {
                                        hierarchyList.set(sitaMenid[isM], ueMenid[iuM], HierarchyList.HierarchyListCondition.UNKNOWN_0);
                                        flg_a++;
                                        flg_b++;
                                        flg_c++;
                                    }
                                }
                            }
                        }
                    }


                }
            }

            //Reset hierarchyList Make sure that it is done properly

            EquivalenceCondition tg;

            int flg_a = 1;
            while (flg_a >= 1) {
                flg_a = 0;
                for (int i = 1; i <= hierarchyList.getEquivalenceConditionTotal(); i++) {
                    tg = hierarchyList.getEquivalenceCondition(i);
                    if (hierarchyList.get(tg.getA(), tg.getB()) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                        if (hierarchyList.get(tg.getA(), tg.getD()) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getD()).isEmpty()) {
                            hierarchyList.set(tg.getA(), tg.getD(), HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()).isEmpty()) {
                            hierarchyList.set(tg.getD(), tg.getA(), HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    if (hierarchyList.get(tg.getA(), tg.getB()) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                        if (hierarchyList.get(tg.getA(), tg.getD()) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getD()).isEmpty()) {
                            hierarchyList.set(tg.getA(), tg.getD(), HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(tg.getD(), tg.getA()).isEmpty()) {
                            hierarchyList.set(tg.getD(), tg.getA(), HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //
                    if (hierarchyList.get(tg.getA(), tg.getD()) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                        if (hierarchyList.get(tg.getA(), tg.getB()) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getB()).isEmpty()) {
                            hierarchyList.set(tg.getA(), tg.getB(), HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()).isEmpty()) {
                            hierarchyList.set(tg.getB(), tg.getA(), HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    if (hierarchyList.get(tg.getA(), tg.getD()) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                        if (hierarchyList.get(tg.getA(), tg.getB()) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_3;
                        }
                        if (hierarchyList.get(tg.getA(), tg.getB()).isEmpty()) {
                            hierarchyList.set(tg.getA(), tg.getB(), HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(tg.getB(), tg.getA()).isEmpty()) {
                            hierarchyList.set(tg.getB(), tg.getA(), HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
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


                    // If only a> b> c, the position of d cannot be determined


                    // If a> c && b> d, then a> d && b> c
                    // If a> d && b> c then a> c && b> d
                    // If a <c && b <d, then a <d && b <c
                    // If a <d && b <c then a <c && b <d


                    // If a> c> b, then a> d> b

                    // a> c && b> d then a> d && b> c
                    if ((hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, d).isEmpty()) {
                            hierarchyList.set(a, d, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, c).isEmpty()) {
                            hierarchyList.set(b, c, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, a).isEmpty()) {
                            hierarchyList.set(d, a, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, b).isEmpty()) {
                            hierarchyList.set(c, b, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //a>d && b>c なら a>c && b>d
                    if ((hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, c).isEmpty()) {
                            hierarchyList.set(a, c, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, d).isEmpty()) {
                            hierarchyList.set(b, d, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, a).isEmpty()) {
                            hierarchyList.set(c, a, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, b).isEmpty()) {
                            hierarchyList.set(d, b, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }

                    //a<c && b<d なら a<d && b<c
                    if ((hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_0) && (hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_0)) {
                        if (hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, d).isEmpty()) {
                            hierarchyList.set(a, d, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, c).isEmpty()) {
                            hierarchyList.set(b, c, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, a).isEmpty()) {
                            hierarchyList.set(d, a, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, b).isEmpty()) {
                            hierarchyList.set(c, b, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //a<d && b<c なら a<c && b<d
                    if ((hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_0) && (hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_0)) {
                        if (hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, c).isEmpty()) {
                            hierarchyList.set(a, c, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, d).isEmpty()) {
                            hierarchyList.set(b, d, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, a).isEmpty()) {
                            hierarchyList.set(c, a, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, b).isEmpty()) {
                            hierarchyList.set(d, b, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                    }


                    //　a>c>b　なら　a>d>b
                    if ((hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(c, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(d, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, d).isEmpty()) {
                            hierarchyList.set(a, d, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, b).isEmpty()) {
                            hierarchyList.set(d, b, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, a).isEmpty()) {
                            hierarchyList.set(d, a, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, d).isEmpty()) {
                            hierarchyList.set(b, d, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //　a>d>b　なら　a>c>b
                    if ((hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(d, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(c, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, c).isEmpty()) {
                            hierarchyList.set(a, c, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, b).isEmpty()) {
                            hierarchyList.set(c, b, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, a).isEmpty()) {
                            hierarchyList.set(c, a, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, c).isEmpty()) {
                            hierarchyList.set(b, c, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //　b>c>a　なら　b>d>a
                    if ((hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(c, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(d, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, d).isEmpty()) {
                            hierarchyList.set(b, d, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, a).isEmpty()) {
                            hierarchyList.set(d, a, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, b).isEmpty()) {
                            hierarchyList.set(d, b, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(a, d).isEmpty()) {
                            hierarchyList.set(a, d, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //　b>d>a　なら　b>c>a
                    if ((hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(d, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(c, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(b, c).isEmpty()) {
                            hierarchyList.set(b, c, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, a).isEmpty()) {
                            hierarchyList.set(c, a, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, b).isEmpty()) {
                            hierarchyList.set(c, b, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(a, c).isEmpty()) {
                            hierarchyList.set(a, c, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }


                    //　c>a>d　なら　c>b>d
                    if ((hierarchyList.get(c, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(d, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(c, b).isEmpty()) {
                            hierarchyList.set(c, b, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, d).isEmpty()) {
                            hierarchyList.set(b, d, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, c).isEmpty()) {
                            hierarchyList.set(b, c, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, b).isEmpty()) {
                            hierarchyList.set(d, b, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //　c>b>d　なら　c>a>d
                    if ((hierarchyList.get(c, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(d, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(c, a).isEmpty()) {
                            hierarchyList.set(c, a, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(a, d).isEmpty()) {
                            hierarchyList.set(a, d, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(a, c).isEmpty()) {
                            hierarchyList.set(a, c, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(d, a).isEmpty()) {
                            hierarchyList.set(d, a, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //　d>a>c　なら　d>b>c
                    if ((hierarchyList.get(d, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(a, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(b, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(c, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(d, b).isEmpty()) {
                            hierarchyList.set(d, b, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, c).isEmpty()) {
                            hierarchyList.set(b, c, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(b, d).isEmpty()) {
                            hierarchyList.set(b, d, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, b).isEmpty()) {
                            hierarchyList.set(c, b, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                    //　d>b>c　なら　d>a>c
                    if ((hierarchyList.get(d, b) == HierarchyList.HierarchyListCondition.UNKNOWN_1) && (hierarchyList.get(b, c) == HierarchyList.HierarchyListCondition.UNKNOWN_1)) {
                        if (hierarchyList.get(a, d) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(c, a) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                            return HierarchyListStatus.UNKNOWN_4;
                        }
                        if (hierarchyList.get(d, a).isEmpty()) {
                            hierarchyList.set(d, a, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(a, c).isEmpty()) {
                            hierarchyList.set(a, c, HierarchyList.HierarchyListCondition.UNKNOWN_1);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(a, d).isEmpty()) {
                            hierarchyList.set(a, d, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                        if (hierarchyList.get(c, a).isEmpty()) {
                            hierarchyList.set(c, a, HierarchyList.HierarchyListCondition.UNKNOWN_0);
                            flg_a++;
                            flg_c++;
                        }
                    }
                }
            }

            //----------------

            System.out.print("推測された関係の数の合計 ＝ ");
            System.out.println(flg_c);

        }


        System.out.println("追加推定 終了------------------------＊＊＊＊ここまで20150310＊＊＊＊＊＊＊＊＊＊＊");

        return HierarchyListStatus.UNKNOWN_1000;
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
        int isusumu;//When = 0, SubFace changes (image that digits change).
        int subfaceId;//SubFace id number that has changed
        isusumu = 0;
        //All SubFaces above ss + 1 are set to the initial values. An error occurs when the number of faces included in SubFace is 0.

        for (int i = ss + 1; i <= SubFaceTotal; i++) {
            s[i].Permutation_first();
        }
        //The overlapping state of the surfaces is changed in order from the one with the largest id number of the SubFace.
        subfaceId = ss;
        for (int i = ss; i >= 1; i--) {
            if (isusumu == 0) {
                isusumu = s[i].next(s[i].getFaceIdCount());
                subfaceId = i;
            }

        }
        if (isusumu == 0) {
            subfaceId = 0;
        }

        return subfaceId;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    public String Permutation_count(int imax) {
        StringBuilder s0 = new StringBuilder();

        for (int ss = 1; ss <= imax; ss++) {
            s0.append(" : ").append(s[ss].get_Permutation_count());
        }
        return s0.toString();
    }

    //Start with the current permutation state and look for possible overlapping states. There is room for speeding up here.
    public int possible_overlapping_search() {      //This should not change the hierarchyList.
        app.bulletinBoard.write("_ _______");
        app.bulletinBoard.write("__ ______");
        app.bulletinBoard.write("___ _____");
        app.bulletinBoard.write("____ ____");
        int ms, Sid;

        Sid = 1;//The initial value of Sid can be anything other than 0.
        while (Sid != 0) { //If Sid == 0, it means that even the smallest number of SubFace has been searched.

            ms = inconsistent_subFace_request();
            if (ms == 1000) {
                return 1000;
            }//There is no contradiction in all SubFaces.
            Sid = next(ms - 1);
            app.bulletinBoard.rewrite(9, "susumu(" + ms + "-1 = )" + Sid);
        }
        return 0;//There is no possible overlapping state
    }

    //-----------------------------------------------------------------------------------------------------------------
    //Search for SubFaces that fold inconsistently in ascending order of number. There is room for speeding up here as well.
    private int inconsistent_subFace_request() { //hierarchyList changes.
        int kks;
        hierarchyList.restore();//<<<<<<<<<<<<<<<<<<<<<<<<<<<,,

        for (int ss = 1; ss <= SubFace_valid_number; ss++) {      //<<<<<<<<<<<<<<高速化のため変更。070417

            app.bulletinBoard.rewrite(7, "mujyun_Smen_motome( " + ss + ") , Menidsuu = " + s[ss].getFaceIdCount() + " , Men_pair_suu = " + s[ss].getFaceIdCount() * (s[ss].getFaceIdCount() - 1) / 2);
            app.bulletinBoard.rewrite(8, " kasanari_bunryi_mitei = " + s[ss].overlapping_classification_pending(hierarchyList));
            app.bulletinBoard.rewrite(9, " kasanari_bunryi_ketteizumi = " + s[ss].overlapping_classification_determined(hierarchyList));


            kks = s[ss].possible_overlapping_search(hierarchyList);
            app.bulletinBoard.rewrite(10, Permutation_count(ss));


            if (kks == 0) {//kks == 0 means that there is no permutation that can overlap
                return ss;
            }
            s[ss].hierarchyList_at_subFace_wo_input(hierarchyList);//Enter the top and bottom information of the ss th SubFace in hierarchyList.
        }

        if (additional_estimation() != HierarchyListStatus.UNKNOWN_1000) {
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


    boolean antiAlias = true;
    double lineWidthForAntiAlias = 1.2;

    //---------------------------------------------------------

    public void toggleAntiAlias() {

        antiAlias = !antiAlias;

        if (antiAlias) {
            lineWidthForAntiAlias = 1.2;
        } else {
            lineWidthForAntiAlias = 1.0;
        }

    }

    //---------------------------------------------------------

    public Memo getMemo_for_svg_with_camera(CreasePattern_Worker orite, PointSet subFace_figure) {//折り上がり図(hyouji_flg==5)
        boolean front_back = camera.isCameraMirrored();

        Point t0 = new Point();
        Point t1 = new Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();

        ip1_flipped = front_back;

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
        int face_order;
        for (int im = 1; im <= SubFaceTotal; im++) {//imは各SubFaceの番号
            if (s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外

                //Determine the color of the imth SubFace when drawing a fold-up diagram
                face_order = 1;
                if (front_back) {
                    face_order = s0[im].getFaceIdCount();
                }


                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 1) {
                    str_stroke = StringOp.toHtmlColor(F_color);
                }//g.setColor(F_color)
                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(face_order)) % 2 == 0) {
                    str_stroke = StringOp.toHtmlColor(B_color);
                }//g.setColor(B_color)

                if (front_back) {
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

        for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
            int faceId_min, faceId_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
            int faceOrderMin, faceOrderMax;//PC画面に表示したときSubFace(faceId_min) で見える面の番号がMen_jyunban_min、SubFace(faceId_max) で見える面の番号がMen_jyunban_max
            boolean drawing_flg;

            drawing_flg = false;
            faceId_min = subFace_figure.lineInFaceBorder_min_lookup(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            faceId_max = subFace_figure.lineInFaceBorder_max_lookup(ib);

            if (s0[faceId_min].getFaceIdCount() == 0) {
                drawing_flg = true;
            }//menをもたない、ドーナツの穴状のSubFaceは境界の棒を描く
            else if (s0[faceId_max].getFaceIdCount() == 0) {
                drawing_flg = true;
            } else if (faceId_min == faceId_max) {
                drawing_flg = true;
            }//一本の棒の片面だけにSubFace有り
            else {
                faceOrderMin = 1;
                if (front_back) {
                    faceOrderMin = s0[faceId_min].getFaceIdCount();
                }
                faceOrderMax = 1;
                if (front_back) {
                    faceOrderMax = s0[faceId_max].getFaceIdCount();
                }
                if (s0[faceId_min].fromTop_count_FaceId(faceOrderMin) != s0[faceId_max].fromTop_count_FaceId(faceOrderMax)) {
                    drawing_flg = true;
                }//この棒で隣接するSubFaceの1番上の面は異なるので、この棒は描く。
            }

            if (drawing_flg) {//棒を描く。
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
    public Memo getMemo_wirediagram_for_svg_export(CreasePattern_Worker orite, PointSet otta_Men_zu, boolean i_fill) {
        boolean flipped = camera.isCameraMirrored();

        Point t_ob = new Point();
        Point t_tv = new Point();

        ip1_flipped = flipped;

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

        for (int i_nbox = 1; i_nbox <= otta_Men_zu.getNumFaces(); i_nbox++) {
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

            if (!i_fill) {
                str_fill = "none";

            } else {

                if (orite.getIFacePosition(im) % 2 == 1) {
                    str_fill = StringOp.toHtmlColor(F_color);
                }
                if (orite.getIFacePosition(im) % 2 == 0) {
                    str_fill = StringOp.toHtmlColor(B_color);
                }

                if (flipped) {
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
            for (int im = 1; im <= otta_Face_figure.getNumFaces(); im++) {
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

            //Preparing to draw a line

            if (antiAlias) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//Anti-alias on
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//Anti-alias off
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }

            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), 2 * transparency_toukado));
            //Draw a line
            for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //直線
            }
        } else {//Black and white transparent view (old style)
            int col_kosa;

            for (int im = 1; im <= subFace_figure.getNumFaces(); im++) {
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

            //Prepare the line
            g.setColor(Color.black);

            if (antiAlias) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//Anti-alias on
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//Anti-alias off
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }

            //Draw a line
            for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //Straight line
            }
        }
    }


    //---------------------------------------------------------

    public void draw_foldedFigure_with_camera(Graphics g, CreasePattern_Worker orite, PointSet subFace_figure) {
        Graphics2D g2 = (Graphics2D) g;
        boolean flipped = camera.isCameraMirrored();

        Point t0 = new Point();
        Point t1 = new Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();
        ip1_flipped = flipped;

        //Draw a face
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

        int faceOrder;
        for (int im = 1; im <= SubFaceTotal; im++) {//imは各SubFaceの番号
            if (s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外
                //Determine the color of the im-th SubFace when drawing a fold-up diagram
                faceOrder = 1;
                if (flipped) {
                    faceOrder = s0[im].getFaceIdCount();
                }

                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(faceOrder)) % 2 == 1) {
                    g.setColor(F_color);
                }
                if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(faceOrder)) % 2 == 0) {
                    g.setColor(B_color);
                }

                if (flipped) {
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(faceOrder)) % 2 == 0) {
                        g.setColor(F_color);
                    }
                    if (orite.getIFacePosition(s0[im].fromTop_count_FaceId(faceOrder)) % 2 == 1) {
                        g.setColor(B_color);
                    }
                }

                //This is the end of deciding the color of SubFace when drawing a folded figure

                //Find the coordinates (on the PC display) of the vertices of the im-th SubFace polygon when drawing a fold-up diagram.

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

                //This is the end of finding the coordinates (on the PC display) of the vertices of the im-th SubFace polygon when drawing a fold-up diagram.

                g2.fill(new java.awt.Polygon(x, y, subFace_figure.getPointsCount(im)));
            }
        }
        // Draw a surface so far


        //Add a shadow  ------------------------------------------------------------------------------------
        if (displayShadows) {
            for (int lineId = 1; lineId <= subFace_figure.getNumLines(); lineId++) {
                int im = line_no_bangou_kara_kagenoaru_subFace_no_bangou_wo_motomeru(lineId, subFace_figure, flipped);//影をつけるSubFaceのid
                if (im != 0) {//影を描く。
                    //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める

                    //棒の座標   subFace_figure.getmaex(lineId),subFace_figure.getmaey(lineId)   -    subFace_figure.getatox(lineId) , subFace_figure.getatoy(lineId)
                    Point b_begin = new Point(subFace_figure.getBeginX(lineId), subFace_figure.getBeginY(lineId));
                    Point b_end = new Point(subFace_figure.getEndX(lineId), subFace_figure.getEndY(lineId));
                    double b_length = b_begin.distance(b_end);

                    //棒と直交するベクトル
                    double o_btx = -(subFace_figure.getBeginY(lineId) - subFace_figure.getEndY(lineId)) * 10.0 / b_length;//棒と直交するxベクトル
                    double o_bty = (subFace_figure.getBeginX(lineId) - subFace_figure.getEndX(lineId)) * 10.0 / b_length;//棒と直交するyベクトル

                    //棒の中点
                    double o_bmx, o_bmy;
                    double t_bmx, t_bmy;

                    o_bmx = (subFace_figure.getBeginX(lineId) + subFace_figure.getEndX(lineId)) / 2.0;
                    o_bmy = (subFace_figure.getBeginY(lineId) + subFace_figure.getEndY(lineId)) / 2.0;

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

                    if (subFace_figure.inside(new Point(o_bmx + 0.01 * o_btx, o_bmy + 0.01 * o_bty), im) != Polygon.Intersection.OUTSIDE) {//0=外部、　1=境界、　2=内部
                        t0.setX(o_bmtx);
                        t0.setY(o_bmty);
                        t1.set(camera.object2TV(t0));
                        t_bmtx = t1.getX();
                        t_bmty = t1.getY();

                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId));
                        t0.setY(subFace_figure.getBeginY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getX();
                        yd[0] = t1.getY();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId) + o_btx);
                        t0.setY(subFace_figure.getBeginY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getX();
                        yd[1] = t1.getY();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setX(subFace_figure.getEndX(lineId) + o_btx);
                        t0.setY(subFace_figure.getEndY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getX();
                        yd[2] = t1.getY();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setX(subFace_figure.getEndX(lineId));
                        t0.setY(subFace_figure.getEndY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getX();
                        yd[3] = t1.getY();
                        x[3] = (int) xd[3];
                        y[3] = (int) yd[3];

                        g2.setPaint(new GradientPaint((float) t_bmx, (float) t_bmy, new Color(0, 0, 0, 50), (float) t_bmtx, (float) t_bmty, new Color(0, 0, 0, 0)));

                        g2.fill(new java.awt.Polygon(x, y, 4));

                    }
                    //----------------------------------棒と直交するxベクトルの向きを変えて影を描画
                    o_btx = -o_btx;//棒と直交するxベクトル
                    o_bty = -o_bty;//棒と直交するyベクトル

                    //-----------------------------------------------
                    //棒の中点を通る直交線上の点
                    o_bmtx = o_bmx + o_btx;
                    o_bmty = o_bmy + o_bty;

                    if (subFace_figure.inside(new Point(o_bmx + 0.01 * o_btx, o_bmy + 0.01 * o_bty), im) != Polygon.Intersection.OUTSIDE) {//0=外部、　1=境界、　2=内部

                        t0.setX(o_bmtx);
                        t0.setY(o_bmty);
                        t1.set(camera.object2TV(t0));

                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId));
                        t0.setY(subFace_figure.getBeginY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getX();
                        yd[0] = t1.getY();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId) + o_btx);
                        t0.setY(subFace_figure.getBeginY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getX();
                        yd[1] = t1.getY();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setX(subFace_figure.getEndX(lineId) + o_btx);
                        t0.setY(subFace_figure.getEndY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getX();
                        yd[2] = t1.getY();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setX(subFace_figure.getEndX(lineId));
                        t0.setY(subFace_figure.getEndY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getX();
                        yd[3] = t1.getY();
                        x[3] = (int) xd[3];
                        y[3] = (int) yd[3];


                        //g2.setPaint( new GradientPaint( (float)t_bmx, (float)t_bmy, new Color(0,0,0,50),     (float)t_bmtx, (float)t_bmty,  new Color(0,0,0,0)  ));
                        g2.setPaint(new GradientPaint((float) xd[0], (float) yd[0], new Color(0, 0, 0, 50), (float) xd[1], (float) yd[1], new Color(0, 0, 0, 0)));
                        g2.fill(new java.awt.Polygon(x, y, 4));
                    }
                }
            }
        }//影をつけるは、ここで終わり

        //棒を描く-----------------------------------------------------------------------------------------


        if (antiAlias) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//アンチェイリアス　オン
            BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
            BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        }

        g.setColor(L_color);//g.setColor(Color.black);

        for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {

            int Mid_min, Mid_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
            int faceOrderMin, faceOrderMax;//PC画面に表示したときSubFace(Mid_min) で見える面の番号がMen_jyunban_min、SubFace(Mid_max) で見える面の番号がMen_jyunban_max
            boolean drawing_flag;

            drawing_flag = false;
            Mid_min = subFace_figure.lineInFaceBorder_min_lookup(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            Mid_max = subFace_figure.lineInFaceBorder_max_lookup(ib);

            if (s0[Mid_min].getFaceIdCount() == 0) {
                drawing_flag = true;
            }//menをもたない、ドーナツの穴状のSubFaceは境界の棒を描く
            else if (s0[Mid_max].getFaceIdCount() == 0) {
                drawing_flag = true;
            } else if (Mid_min == Mid_max) {
                drawing_flag = true;
            }//一本の棒の片面だけにSubFace有り
            else {
                faceOrderMin = 1;
                if (flipped) {
                    faceOrderMin = s0[Mid_min].getFaceIdCount();
                }
                faceOrderMax = 1;
                if (flipped) {
                    faceOrderMax = s0[Mid_max].getFaceIdCount();
                }
                if (s0[Mid_min].fromTop_count_FaceId(faceOrderMin) != s0[Mid_max].fromTop_count_FaceId(faceOrderMax)) {
                    drawing_flag = true;
                }//この棒で隣接するSubFaceの1番上の面は異なるので、この棒は描く。
            }

            if (drawing_flag) {//棒を描く。
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.getAX()), gy(s_tv.getAY()), gx(s_tv.getBX()), gy(s_tv.getBY())); //直線
            }
        }
    }


    //---------------------------------------------------------
    public void draw_cross_with_camera(Graphics g) {
        //Draw the center of the camera with a cross
        OritaDrawing.cross(g, camera.object2TV(camera.getCameraPosition()), 5.0, 2.0, LineColor.ORANGE_4);
    }

    public void toggleDisplayShadows() {
        displayShadows = !displayShadows;
    }

    public int line_no_bangou_kara_kagenoaru_subFace_no_bangou_wo_motomeru(int ib, PointSet subFace_figure, boolean flipped) {//棒の番号から、その棒の影が発生するSubFace の番号を求める。影が発生しない場合は0を返す。
        int i_return;

        int faceId_min, faceId_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
        int faceOrderMin, faceordermax;//PC画面に表示したときSubFace(faceId_min) で見える面の、そのSubFaceでの重なり順がMen_jyunban_min、SubFace(faceId_max) で見える面のそのSubFaceでの重なり順がMen_jyunban_max

        faceId_min = subFace_figure.lineInFaceBorder_min_lookup(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
        faceId_max = subFace_figure.lineInFaceBorder_max_lookup(ib);

        if (s0[faceId_min].getFaceIdCount() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のSubFaceとの境界の棒には影なし
        if (s0[faceId_max].getFaceIdCount() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のFaceStackとの境界の棒には影なし
        if (faceId_min == faceId_max) {
            return 0;
        }//一本の棒の片面だけにFaceStack有り

        faceOrderMin = 1;
        if (flipped) {
            faceOrderMin = s0[faceId_min].getFaceIdCount();
        }
        faceordermax = 1;
        if (flipped) {
            faceordermax = s0[faceId_max].getFaceIdCount();
        }

        int Mid_min_mieteru_men_id = s0[faceId_min].fromTop_count_FaceId(faceOrderMin);
        int Mid_max_mieteru_men_id = s0[faceId_max].fromTop_count_FaceId(faceordermax);
        if (Mid_min_mieteru_men_id == Mid_max_mieteru_men_id) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面が同じなので、棒自体が描かれず影もなし。


        //Jyougehyou
        //public int get(int i,int j){return hierarchyList[i][j];}
        //　hierarchyList[i][j]が1なら面iは面jの上側。0なら下側。
        //  hierarchyList[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
        //hierarchyList[i][j]が-100なら、面iとjは重なるところがない。
        if (hierarchyList.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == HierarchyList.HierarchyListCondition.UNKNOWN_N50) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面の上下関係不明なので、影はなし
        if (hierarchyList.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == HierarchyList.HierarchyListCondition.EMPTY_N100) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面の上下関係ない（重ならない）ので、影はなし

        i_return = faceId_min;
        if (hierarchyList.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
            i_return = faceId_max;
        }

        if (flipped) {
            if (i_return == faceId_min) {
                return faceId_max;
            } else {
                return faceId_min;
            }
        }

        return i_return;
    }

    int makesuu0no_menno_amount = 0;//Number of faces that can be ranked without any other faces on top
    int makesuu1ijyouno_menno_amount = 0;//Number of faces that can only be ranked if there is one or more other faces on top

    private void rating2() {
        int hierarchyListFacesTotal = hierarchyList.getFacesTotal();//面の総数を求める。
        face_rating = new double[hierarchyListFacesTotal + 1];

        i_face_rating = new int[hierarchyListFacesTotal + 1];


        makesuu0no_menno_amount = 0;//Number of faces that can be ranked without any other faces on top
        makesuu1ijyouno_menno_amount = 0;//Number of faces that can only be ranked if there is one or more other faces on top


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

        System.out.println("上に他の面がない状態で順位付けできた面の数 = " + makesuu0no_menno_amount);
        System.out.println("上に他の面が1以上ある状態で順位付けした面の数 = " + makesuu1ijyouno_menno_amount);

        nbox.reset();
        for (int i = 1; i <= hierarchyList.getFacesTotal(); i++) {
            nbox.container_i_smallest_first(new int_double(i, face_rating[i]));
        }
    }
    //Each of the following functions uses s0 [] as FaceStack 20180305

    private int top_face_id_ga_maketa_kazu_goukei_without_rated_face = 0;

    private int get_top_face_id_without_rated_face() {
        int top_men_id = 0;
        top_face_id_ga_maketa_kazu_goukei_without_rated_face = hierarchyList.getFacesTotal() + 100;

        int hierarchyListFacesTotal = hierarchyList.getFacesTotal();//Find the total number of faces.

        boolean[] i_kentouzumi = new boolean[hierarchyListFacesTotal + 1];//検討済みの面IDは１にする
        for (int i = 0; i <= hierarchyListFacesTotal; i++) {
            i_kentouzumi[i] = false;
        }

        for (int i = 1; i <= SubFaceTotal; i++) {
            int s_top_id = get_s_top_id_without_rated_face(i);//各s面の（レートがついた面は除く）一番上の面。s_top_id=0ならそのs面にはレートが未定の面はない

            if (s_top_id != 0) {
                if (!i_kentouzumi[s_top_id]) {
                    int mkg = get_maketa_kazu_goukei_without_rated_face(s_top_id);
                    if (mkg == 0) {
                        makesuu0no_menno_amount++;
                        return s_top_id;
                    }//ここは、これでよいか要検討20180306
                    if (top_face_id_ga_maketa_kazu_goukei_without_rated_face > mkg) {
                        top_face_id_ga_maketa_kazu_goukei_without_rated_face = mkg;
                        top_men_id = s_top_id;
                    }
                }
            }

            i_kentouzumi[s_top_id] = true;
        }

        //makesuu0no_menno_amount=0;//上に他の面がない状態で順位付けできる面の数
        //makesuu1ijyouno_menno_amount=0;//上に他の面が1以上ある状態でないと順位付けできない面の数
        if (top_face_id_ga_maketa_kazu_goukei_without_rated_face == 0) {
            makesuu0no_menno_amount++;
        } else if (top_face_id_ga_maketa_kazu_goukei_without_rated_face > 0) {
            makesuu1ijyouno_menno_amount = makesuu1ijyouno_menno_amount + 1;
        }

        return top_men_id;
    }

    private int get_s_top_id_without_rated_face(int ism) {//ismはs面のid
        int Mensuu = s0[ism].getFaceIdCount();//FaceStackでの面数//FaceStack s0[];//FaceStack_figureから得られるFaceStack
        for (int jyunban = 1; jyunban <= Mensuu; jyunban++) {
            int im = s0[ism].fromTop_count_FaceId(jyunban);
            if (i_face_rating[im] == 0) {
                return im;
            }
        }
        return 0;
    }

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

    private int get_subFace_de_maketa_kazu_without_rated_Face(int ism, int men_id) {//ismはFaceStackのid
        int FaceCount = s0[ism].getFaceIdCount();//FaceStackでの面数//FaceStack s0[];//FaceStack_figureから得られるFaceStack
        int maketa_kazu = 0;

        for (int i = 1; i <= FaceCount; i++) {
            int im = s0[ism].fromTop_count_FaceId(i);
            if (im == men_id) {
                return maketa_kazu;
            }
            if (i_face_rating[im] == 0) {
                maketa_kazu++;
            }
        }
        return 0;
    }
}     



