package origami.crease_pattern.worker;

import origami.crease_pattern.PointSet;
import origami.folding.HierarchyList;
import origami.folding.algorithm.AdditionalEstimationAlgorithm;
import origami.folding.algorithm.swapping.SubFaceSwappingAlgorithm;
import origami.folding.element.SubFace;
import origami.folding.util.EquivalenceCondition;
import origami.folding.util.IBulletinBoard;
import origami.folding.util.SortingBox;


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
    SubFace[] s1;//Reduced SubFace list, for AEA processing
    public SubFace[] s;//s is s1 sorted in descending order of priority.
    IBulletinBoard bb;
    //　ここは  class Jyougehyou_Syokunin  の中です。
    //上下表の初期設定。展開図に1頂点から奇数の折線がでる誤りがある場合0を返す。それが無ければ1000を返す。
    //展開図に山谷折線の拡張による誤りがある場合2を返す。
    int makesuu0no_menno_amount = 0;//Number of faces that can be ranked without any other faces on top
    int makesuu1ijyouno_menno_amount = 0;//Number of faces that can only be ranked if there is one or more other faces on top
    private int top_face_id_ga_maketa_kazu_goukei_without_rated_face = 0;

    private SubFaceSwappingAlgorithm swapper;
    private final FoldedFigure_Configurator configurator;
    private boolean aeaMode;

    public FoldedFigure_Worker(IBulletinBoard bb0) {
        bb = bb0;
        configurator = new FoldedFigure_Configurator(this);
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
        configurator.setFaceFigure(otta_Face_figure);
        configurator.setSubFaceFigure(SubFace_figure);
        configurator.SubFace_configure();
    }

    public HierarchyListStatus HierarchyList_configure(WireFrame_Worker orite) throws InterruptedException {
        configurator.setWireFrameWorker(orite);
        return configurator.HierarchyList_configure();
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
        aeaMode = swap;
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
        if (aeaMode) AEA.restore();

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
            if (aeaMode) {
                // Enter the stacking information of the ss th SubFace in hierarchyList.
                s[ss].enterStackingOfSubFace(AEA);

                boolean success = true;
                boolean se = swapper.shouldEstimate(ss); // side effect
                if (se && ss <= Math.sqrt(SubFace_valid_number)) {
                    success = AEA.run(0) == HierarchyListStatus.SUCCESSFUL_1000;
                } else if (ss % (3 + ss * ss / 6400) == 0) {
                    // There's no need to execute run() even fastRun() in every step (that will be
                    // too slow), so we use the formula above to decide when to run it.
                    success = AEA.fastRun();
                }
                if (!success) {
                    /**
                     * For some CPs, realtime AEA could return a result other than success (even as
                     * we ran AEA in each step and the current permutation doesn't have any
                     * immediate contradiction, since something might still go wrong in the
                     * inference process), and in this case it is very difficult to make sense out
                     * of the inference error, and the error could even stay all the way until the
                     * final solution (which would then make the solution invalid). The best we can
                     * do is to disable realtime AEA if this happens.
                     */
                    System.out.println("Disable realtime AEA");
                    aeaMode = false;
                    hierarchyList.restore();
                    ss = 0; // restart the search
                }
            } else {
                s[ss].enterStackingOfSubFace(hierarchyList);
            }
        }

        // Solution found, perform final checking
        bb.rewrite(10, " ");
        bb.rewrite(9, "Possible solution found...");
        AEA = new AdditionalEstimationAlgorithm(hierarchyList, s1, 1000); // we don't need much for this
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
            nbox.addByWeight(i, face_rating[i]);
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



