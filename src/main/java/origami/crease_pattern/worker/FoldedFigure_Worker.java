package origami.crease_pattern.worker;

import origami.Epsilon;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.data.ListArray;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.adapter.PointSetFaceAdapter;
import origami.data.quadTree.adapter.PointSetLineAdapter;
import origami.data.quadTree.collector.LineSegmentCollector;
import origami.data.quadTree.collector.PointCollector;
import origami.data.quadTree.comparator.ExpandComparator;
import origami.folding.HierarchyList;
import origami.folding.algorithm.AdditionalEstimationAlgorithm;
import origami.folding.algorithm.SubFacePriority;
import origami.folding.algorithm.swapping.SubFaceSwappingAlgorithm;
import origami.folding.element.SubFace;
import origami.folding.util.EquivalenceCondition;
import origami.folding.util.IBulletinBoard;
import origami.folding.util.SortingBox;
import origami.folding.util.WeightedValue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


//HierarchyList: Record and utilize what kind of vertical relationship the surface of the developed view before folding will be after folding.

/**
 * Responsible for calculating the correct order of subfaces in a folded figure.
 */
public class FoldedFigure_Worker {
    public final HierarchyList hierarchyList = new HierarchyList();
    public double[] face_rating;
    public SortingBox<Integer> nbox = new SortingBox<>();//20180227 In the nbox, the id of men is paired with men_rating and sorted in ascending order of men_rating.
    public int SubFaceTotal;//SubFaceの数
    //paint 用のint格納用VVVVVVVVVVVVVVVVVVVVVV
    public EquivalenceCondition errorPos = null;
    int[] i_face_rating;
    //  hierarchyList[][]は折る前の展開図のすべての面同士の上下関係を1つの表にまとめたものとして扱う
    //　hierarchyList[i][j]が1なら面iは面jの上側。0なら下側。
    //  hierarchyList[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
    //hierarchyList[i][j]が-100なら、面iとjは重なるところがない。
    int SubFace_valid_number;//SubFaceは全て調べなくても、Faceの上下関係は網羅できる。Faceの上下関係を網羅するのに必要なSubFaceの数が優先順位の何番目までかをさがす。
    public int FaceIdCount_max;//各SubFaceの持つMenidsuuの最大値。すなわち、最も紙に重なりが多いところの枚数。
    //paint 用のint格納用VVVVVVVVVVVVVVVVVVVVVV
    public SubFace[] s0;//SubFace obtained from SubFace_figure
    public SubFace[] s;//s is s0 sorted in descending order of priority.
    IBulletinBoard bb;
    //　ここは  class Jyougehyou_Syokunin  の中です。
    //上下表の初期設定。展開図に1頂点から奇数の折線がでる誤りがある場合0を返す。それが無ければ1000を返す。
    //展開図に山谷折線の拡張による誤りがある場合2を返す。
    int makesuu0no_menno_amount = 0;//Number of faces that can be ranked without any other faces on top
    int makesuu1ijyouno_menno_amount = 0;//Number of faces that can only be ranked if there is one or more other faces on top
    SubFaceSwappingAlgorithm swapper;
    ListArray faceToSubFaceMap;
    QuadTree qt;
    private int top_face_id_ga_maketa_kazu_goukei_without_rated_face = 0;

    public FoldedFigure_Worker(IBulletinBoard bb0) {
        bb = bb0;
        reset();
    }

    public void reset() {
        hierarchyList.reset();
        SubFaceTotal = 0;
        SubFace_valid_number = 0;
        FaceIdCount_max = 0;
    }


    //　ここは  class Jyougehyou_Syokunin  の中です。


    //SubFaceの面の重なり状態を次の状態にする。
    //もし現在の面の重なり状態が、最後のものだったら0をreturnして、面の重なり状態は最初のものに戻る。
    //zzzzzzzz

    public int getSubFace_valid_number() {
        return SubFace_valid_number;
    }

    public void SubFace_configure(PointSet otta_Face_figure, PointSet SubFace_figure) throws InterruptedException {
        // Make an upper and lower table of faces (the faces in the unfolded view before folding).
        // This includes the point set of ts2 (which has information on the positional relationship of the faces after folding) and <-------------otta_Face_figure
        // Use the point set of ts3 (which has the information of SubFace whose surface is subdivided in the wire diagram). <-------------SubFace_figure
        // Also, use the information on the positional relationship of the surface when folded, which ts1 has.

        System.out.println("Smenの初期設定");
        reset();
        SubFaceTotal = SubFace_figure.getNumFaces();

        s0 = new SubFace[SubFaceTotal + 1];
        s = new SubFace[SubFaceTotal + 1];

        for (int i = 0; i < SubFaceTotal + 1; i++) {
            s0[i] = new SubFace(bb);
            s[i] = s0[i];
        }

        //Record the faces contained in each SubFace.
        System.out.println("各Smenに含まれる面を記録するため、各Smenの内部点を登録");
        Point[] subFace_insidePoint = new Point[SubFaceTotal + 1];  //<<<<<<<<<<<<<<<<<<<<<<<<<<<オブジェクトの配列を動的に指定
        for (int i = 1; i <= SubFaceTotal; i++) {
            subFace_insidePoint[i] = SubFace_figure.insidePoint_surface(i);
        }

        // Also used later in HierarchyList_configure
        qt = new QuadTree(new PointSetFaceAdapter(otta_Face_figure), ExpandComparator.instance);

        System.out.println("各Smenに含まれる面を記録する");

        int faceTotal = otta_Face_figure.getNumFaces();
        faceToSubFaceMap = new ListArray(faceTotal, faceTotal * 5);
        int[] s0addFaceId = new int[faceTotal + 1]; // SubFaceに追加する面を一時記録しておく

        for (int i = 1; i <= SubFaceTotal; i++) {
            int s0addFaceTotal = 0;

            for (int j : qt.collect(new PointCollector(subFace_insidePoint[i]))) {
                j++; // qt is 0-based
                if (otta_Face_figure.inside(subFace_insidePoint[i], j) == Polygon.Intersection.INSIDE) {
                    s0addFaceId[++s0addFaceTotal] = j;
                }
            }

            s0[i].setNumDigits(s0addFaceTotal);

            for (int j = 1; j <= s0addFaceTotal; j++) {
                s0[i].setFaceId(j, s0addFaceId[j]);//ここで面番号jは小さい方が先に追加される。
                faceToSubFaceMap.add(s0addFaceId[j], i);
            }

            if (Thread.interrupted()) throw new InterruptedException();
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

    public HierarchyListStatus HierarchyList_configure(WireFrame_Worker orite, PointSet otta_face_figure) throws InterruptedException {
        bb.write("           HierarchyList_configure   step1   start ");
        hierarchyList.setFacesTotal(otta_face_figure.getNumFaces());

        //Put the hierarchical relationship determined from the information of mountain folds and valley folds in the table above and below.
        System.out.println("山折り谷折りの情報から決定される上下関係を上下表に入れる");
        int faceId_min, faceId_max;
        for (int ib = 1; ib <= orite.getNumLines(); ib++) {
            faceId_min = orite.lineInFaceBorder_min_request(ib);
            faceId_max = orite.lineInFaceBorder_max_request(ib);
            if (faceId_min != faceId_max) {// In the developed view, there are faces on both sides of the rod ib.
                int minPos = orite.getIFacePosition(faceId_min);
                int maxPos = orite.getIFacePosition(faceId_max);
                if (minPos % 2 == maxPos % 2) {
                    return HierarchyListStatus.UNKNOWN_0;
                }
                if (otta_face_figure.getColor(ib) == LineColor.RED_1) {// Red line means mountain fold
                    if (minPos % 2 == 1) {// The surface Mid_min has the same orientation as the reference surface (the surface faces up)
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.ABOVE_1);
                    } else {//The surface Mid_max has the same orientation as the reference surface (the surface faces up)
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.BELOW_0);
                    }
                } else {//The blue line means valley fold
                    if (minPos % 2 == 1) {//面Mid_minは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.BELOW_0);
                    } else {//面Mid_maxは基準面と同じ向き(表面が上を向く)
                        hierarchyList.set(faceId_min, faceId_max, HierarchyList.ABOVE_1);
                    }
                }
            }
        }

        //----------------------------------------------
        bb.write("           HierarchyList_configure   step2   start ");
        System.out.println("等価条件を設定する   ");
        //等価条件を設定する。棒ibを境界として隣接する2つの面im1,im2が有る場合、折り畳み推定した場合に
        //棒ibの一部と重なる位置に有る面imは面im1と面im2に上下方向で挟まれることはない。このことから
        //gj[im1][im]=gj[im2][im]という等価条件が成り立つ。

        for (int ib = 1; ib <= orite.getNumLines(); ib++) {
            faceId_min = orite.lineInFaceBorder_min_request(ib);
            faceId_max = orite.lineInFaceBorder_max_request(ib);
            if (faceId_min != faceId_max) {//展開図において、棒ibの両脇に面がある
                Point p = otta_face_figure.getBeginPointFromLineId(ib);
                Point q = otta_face_figure.getEndPointFromLineId(ib);
                for (int im : qt.collect(new LineSegmentCollector(p, q))) {
                    im++; // qt is 0-based
                    if ((im != faceId_min) && (im != faceId_max)) {
                        if (otta_face_figure.convex_inside(ib, im)) {
                            //下の２つのifは暫定的な処理。あとで置き換え予定
                            if (otta_face_figure.convex_inside(Epsilon.UNKNOWN_05, ib, im)) {
                                if (otta_face_figure.convex_inside(-Epsilon.UNKNOWN_05, ib, im)) {
                                    hierarchyList.addEquivalenceCondition(im, faceId_min, im, faceId_max);
                                }
                            }
                        }
                    }
                }
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }
        System.out.print("３面が関与する突き抜け条件の数　＝　");
        System.out.println(hierarchyList.getEquivalenceConditionTotal());

        //----------------------------------------------
        bb.write("           HierarchyList_configure   step3   start ");
        // Add equivalence condition. There are two adjacent faces im1 and im2 as the boundary of the bar ib,
        // Also, there are two adjacent faces im3 and im4 as the boundary of the bar jb, and when ib and jb are parallel and partially overlap, when folding is estimated.
        // The surface of the bar ib and the surface of the surface jb are not aligned with i, j, i, j or j, i, j, i. If this happens,
        // Since there is a mistake in the 3rd place from the beginning, find the number of digits in this 3rd place with SubFace and advance this digit by 1.

        qt = new QuadTree(new PointSetLineAdapter(otta_face_figure));
        ExecutorService service = Executors.newWorkStealingPool();

        for (int ib = 1; ib <= orite.getNumLines() - 1; ib++) {
            final int ibf = ib;
            final QuadTree qtf = qt;
            final int mi1 = orite.lineInFaceBorder_min_request(ibf);
            final int mi2 = orite.lineInFaceBorder_max_request(ibf);
            if (mi1 != mi2 && mi1 != 0) {
                service.execute(() -> {
                    for (int jb : qtf.getPotentialCollision(ibf - 1)) { // qt is 0-based
                        if (Thread.interrupted()) break;
                        final int jbf = jb + 1; // qt is 0-based
                        int mj1 = orite.lineInFaceBorder_min_request(jbf);
                        int mj2 = orite.lineInFaceBorder_max_request(jbf);
                        if (mj1 != mj2 && mj1 != 0) {
                            if (otta_face_figure.parallel_overlap(ibf, jbf)) {
                                if (exist_identical_subFace(mi1, mi2, mj1, mj2)) {
                                    hierarchyList.addUEquivalenceCondition(mi1, mi2, mj1, mj2);
                                }
                            }
                        }
                    }
                });
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }

        // Done adding tasks, shut down ExecutorService
        service.shutdown();
        try {
            while (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                // For really large CP, it could take longer time to finish. Just wait.
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("HierarchyList_configure did not exit!");
            }
        }
        faceToSubFaceMap = null;
        service = null;
        qt = null;
        System.gc();

        System.out.print("４面が関与する突き抜け条件の数　＝　");
        System.out.println(hierarchyList.getUEquivalenceConditionTotal());

        bb.write("           HierarchyList_configure   step4   start ");
        //Additional estimation

        HierarchyListStatus additional = additional_estimation();
        if (additional != HierarchyListStatus.SUCCESSFUL_1000) {
            return additional;
        }
        System.gc();
        
        // Here we can compare and see the huge difference before and after AEA
        System.out.print("３面が関与する突き抜け条件の数　＝　");
        System.out.println(hierarchyList.getEquivalenceConditionTotal());
        System.out.print("４面が関与する突き抜け条件の数　＝　");
        System.out.println(hierarchyList.getUEquivalenceConditionTotal());

        System.out.println("追加推定 終了し、上下表を保存------------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        //*************Saving the results of the first deductive reasoning**************************
        hierarchyList.save();//Save the hierarchical relationship determined from the mountain fold and valley fold information.
        //************************************************************************
        bb.rewrite(10, "           HierarchyList_configure   step5   start ");

        //s0に優先順位をつける(このときhierarchyListの-100のところが変るところがある)
        System.out.println("Smen(s0)に優先順位をつける");
        //まず、他のSubFaceに丸ごと含まれているSubFaceを除外する


        // Priority initialization
        int[] priorityMap = new int[SubFaceTotal + 1];     //<<<<<<<<<<<<<<<臨時
        SubFacePriority SFP = new SubFacePriority(hierarchyList.getFacesTotal(), SubFaceTotal);
        for (int i = 1; i <= SubFaceTotal; i++) {
            SFP.addSubFace(s0[i], i, hierarchyList);
        }

        // Priority processing
        for (int i = 1; i <= SubFaceTotal; i++) {// 優先度i番目のSubFaceIdをさがす。
            long result = SFP.getMaxSubFace(s0);
            int i_yusen = (int) (result & SubFacePriority.mask);
            int max = (int) (result >>> 32);
            priorityMap[i] = i_yusen; // 優先度からs0のidを指定できるようにする
            if (max > 0) {
                SubFace_valid_number++;
            }

            SFP.processSubFace(s0[i_yusen], i_yusen, hierarchyList);
        }

        //System.out.println("------------" );
        System.out.println("上下表職人内　Smensuu = " + SubFaceTotal);
        System.out.println("上下表職人内　s0に優先順位をつける");
        System.out.println("上下表職人内　優先度からs0のid");

        for (int i = 1; i <= SubFaceTotal; i++) {
            s[i] = s0[priorityMap[i]];
        }

        // Make a guidebook for each valid SubFace.
        // Previously this is done for all SubFaces, which is unnecessary.
        System.out.println("Building guides for SubFace");
        service = Executors.newWorkStealingPool();
        for (int i = 1; i <= SubFace_valid_number; i++) {
            final SubFace sf = s[i];
            service.execute(() -> sf.setGuideMap(hierarchyList));
        }

        // Done adding tasks, shut down ExecutorService
        service.shutdown();
        try {
            while (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                // For really large CP, it could take longer time to finish. Just wait.
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("HierarchyList_configure did not exit!");
            }
        }

        //優先順位を逆転させる。これが有効かどうかは不明wwwww

        //SubFaceは優先順の何番目までやるかを決める
        service = null;
        SFP = null;
        System.gc();

        System.out.print("Smen有効数は　");
        System.out.print(SubFace_valid_number);
        System.out.print("／");
        System.out.println(SubFaceTotal);
        System.out.println("上下表初期設定終了");
        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    public HierarchyListStatus additional_estimation() throws InterruptedException {
        // We will infer relationships that can be further determined from the
        // information on mountain folds and valley folds.

        int capacity = FaceIdCount_max * FaceIdCount_max;
        AdditionalEstimationAlgorithm AEA = new AdditionalEstimationAlgorithm(bb, hierarchyList, s0, capacity);
        AEA.removeMode = true;
        HierarchyListStatus result = AEA.run(0);
        errorPos = AEA.errorPos;
        return result;
    }


    //引数の４つの面を同時に含むSubFaceが1つ以上存在するなら１、しないなら０を返す。
    private boolean exist_identical_subFace(int im1, int im2, int im3, int im4) {
        for (int i : faceToSubFaceMap.get(im1)) {
            if (s[i].contains(im1, im2, im3, im4)) {
                return true;
            }
        }
        return false;
    }

    public int next(int ss) throws InterruptedException {
        int isusumu;//When = 0, SubFace changes (image that digits change).
        int subfaceId;//SubFace id number that has changed
        isusumu = 0;
        //All SubFaces above ss + 1 are set to the initial values. An error occurs when the number of faces included in SubFace is 0.

        for (int i = ss + 1; i <= SubFace_valid_number; i++) {
            s[i].Permutation_first();
        }
        //The overlapping state of the surfaces is changed in order from the one with the largest id number of the SubFace.
        subfaceId = ss;
        for (int i = ss; i >= 1 && isusumu == 0; i--) {
            isusumu = s[i].next(s[i].getFaceIdCount());
            subfaceId = i;
        }
        if (isusumu == 0) {
            return 0;
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
    public int possible_overlapping_search(boolean swap) throws InterruptedException {      //This should not change the hierarchyList.
        bb.write("Initializing search...");
        bb.write(" ");
        bb.write(" ");
        bb.write(" ");
        int ms, Sid;

        AdditionalEstimationAlgorithm AEA = null;
        if (swap) {
            swapper = new SubFaceSwappingAlgorithm();

            // Create a smaller "realtime AEA" to assist the search. Since AEA is now a very
            // fast algorithm, we have the luxury of using it every step of the search to
            // infer more stacking relations from our current set of permutation choices,
            // and this will greatly speed up the permutation generating (because of the
            // temporary guide mechanism) of later SubFaces.
            AEA = new AdditionalEstimationAlgorithm(hierarchyList, s, SubFace_valid_number, 1000);
            AEA.initialize();
        }

        Sid = 1;//The initial value of Sid can be anything other than 0.
        while (Sid != 0) { //If Sid == 0, it means that even the smallest number of SubFace has been searched.

            ms = inconsistent_subFace_request(AEA);
            if (ms == 1000) {
                return 1000;
            }//There is no contradiction in all SubFaces.
            Sid = next(ms - 1);

            if (swap) swapper.process(s, SubFace_valid_number);

            if (Thread.interrupted()) throw new InterruptedException();
        }
        return 0;//There is no possible overlapping state
    }

    //-----------------------------------------------------------------------------------------------------------------
    //Search for SubFaces that fold inconsistently in ascending order of number. There is room for speeding up here as well.
    private int inconsistent_subFace_request(AdditionalEstimationAlgorithm AEA) throws InterruptedException { //hierarchyList changes.
        int kks;
        boolean swap = AEA != null;
        hierarchyList.restore();// <<<<<<<<<<<<<<<<<<<<<<<<<<<,,
        if (swap) AEA.restore();

        for (int ss = 1; ss <= SubFace_valid_number; ss++) { // <<<<<<<<<<<<<<高速化のため変更。070417
            if (swap) swapper.visit(s[ss]);

            int count = s[ss].getFaceIdCount(), pair = count * (count - 1) / 2;
            String msg = "Current SubFace( " + ss + " / ";
            if (swap) msg += swapper.getVisitedCount() + " / ";
            bb.rewrite(7, msg + SubFace_valid_number + " ) , face count = " + count + " , face pair = " + pair);
            bb.rewrite(8, "Search progress " + Permutation_count(ss));

            kks = s[ss].possible_overlapping_search(hierarchyList);
            if (kks == 0) {// kks == 0 means that there is no permutation that can overlap
                swapper.record(ss);
                if (ss > SubFace_valid_number / 2 || s[ss].swapCounter > 0) s[ss].swapCounter++;
                return ss;
            }

            s[ss].swapCounter = 0;
            if (swap) {
                // Enter the stacking information of the ss th SubFace in hierarchyList.
                s[ss].enterStackingOfSubFace(AEA);

                boolean se = swapper.shouldEstimate(ss); // side effect
                if (se && ss <= Math.sqrt(SubFace_valid_number)) {
                    // It is possible in theory that the following line returns a result other than
                    // success (even as we ran AEA in each step and the current permutation doesn't
                    // have any immediate contradiction, since something might still go wrong in the
                    // inference process), but we shall ignore that result here and keep going. The
                    // reason for this is that stopping at this point is costly in performance, and
                    // basically the contradiction will make a later SubFace unsolvable anyway. We
                    // will then count on that SubFace and the swapping algorithm to fix everything.
                    AEA.run(0);
                } else if (ss % (3 + ss * ss / 6400) == 0) {
                    // There's no need to execute run() even fastRun() in every step (that will be
                    // too slow), so we use the formula above to decide when to run it.
                    AEA.fastRun();
                }
            } else {
                s[ss].enterStackingOfSubFace(hierarchyList);
            }
        }

        // Solution found, perform final checking
        bb.rewrite(10, " ");
        bb.rewrite(9, "Possible solution found...");
        AEA = new AdditionalEstimationAlgorithm(hierarchyList, s, 1000); // we don't need much for this
        if (AEA.run(SubFace_valid_number) != HierarchyListStatus.SUCCESSFUL_1000) {
            bb.rewrite(9, " ");
            // This rarely happens, but typically it means the solution contradicts some of
            // the SubFace not counted as "valid" previously. In that case, adding it to the
            // valid set will solve the problem.
            if (AEA.errorIndex != 0) {
                // Add additional SubFace to the valid list and continue the search
                int v = ++SubFace_valid_number, e = AEA.errorIndex;
                System.out.println("Adding SubFace " + e + " to the valid set index " + v);
                SubFace temp = s[v];
                s[v] = s[e];
                s[e] = temp;

                // The new SubFace doesn't have guidebook yet.
                hierarchyList.restore();
                s[v].setGuideMap(hierarchyList);

                // record dead-end here since this SubFace is having a contradiction already
                swapper.record(v);
            }
            return SubFace_valid_number;
        }

        // Solution is confirmed
        return 1000;
    }

    public SortingBox<Integer> rating2() {
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
            nbox.container_i_smallest_first(new WeightedValue<>(i, face_rating[i]));
        }

        return nbox;
    }
    //Each of the following functions uses s0 [] as FaceStack 20180305

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


    public enum HierarchyListStatus {
        UNKNOWN_N1,
        UNKNOWN_0,
        UNKNOWN_1,
        CONTRADICTED_2,
        CONTRADICTED_3,
        CONTRADICTED_4,
        SUCCESSFUL_1000,
    }
}     



