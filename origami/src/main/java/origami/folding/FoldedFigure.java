package origami.folding;

import org.tinylog.Logger;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.worker.FoldedFigure_Configurator;
import origami.crease_pattern.worker.FoldedFigure_Worker;
import origami.crease_pattern.worker.LineSegmentSetWorker;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.folding.util.IBulletinBoard;

public class FoldedFigure {
    public FoldedFigure_Worker foldedFigure_worker;
    public FoldedFigure_Configurator foldedFigure_configurator;
    // The point set of wireFrame_worker2 may have overlapping bars, so
    // Pass it to bb_worker once and organize it as a line segment set.

    public DisplayStyle display_flg_backup = DisplayStyle.DEVELOPMENT_4;//For temporary backup of display format displayStyle
    public DisplayStyle displayStyle = DisplayStyle.NONE_0;//Designation of the display style of the folded figure. 1 is a crease pattern, 2 is a wire drawing. If it is 3, it is a transparent view. If it is 4, it is the same as when you actually fold the origami paper.
    public EstimationOrder estimationOrder = EstimationOrder.ORDER_0;//Instructions on how far to perform folding estimation
    public EstimationStep estimationStep = EstimationStep.STEP_0;//Display of how far the folding estimation has been completed
    //Variable to store the value for display
    public FoldedFigure_Worker.HierarchyListStatus ip1_anotherOverlapValid = FoldedFigure_Worker.HierarchyListStatus.UNKNOWN_N1;// At the time of initial setting of the upper and lower front craftsmen, the front and back sides are the same after folding
    // A variable that stores 0 if there is an error of being adjacent, and 1000 if there is no error.
    // The initial value here can be any number other than (0 or 1000).
    public int ip2_possibleOverlap = -1;// When the top and bottom craftsmen look for a foldable stacking method,
    // A variable that stores 0 if there is no possible overlap, and 1000 if there is a possible overlap.
    // The initial value here can be any number other than (0 or 1000).
    //int ip3a=1;
    public int ip3 = 1;// Used by wireFrame_worker1 to specify the reference plane for folding.
    public State ip4 = State.FRONT_0;// This specifies whether to flip over at the beginning of wireFrame_worker1. Do not set to 0. If it is 1, turn it over.
    public int ip5 = -1;    // After the top and bottom craftsmen once show the overlap of foldable paper,
    public int ip6 = -1;    // After the top and bottom craftsmen once show the overlap of foldable paper,
    public boolean findAnotherOverlapValid = false;     //This takes 1 if "find another overlap" is valid, and 0 if it is invalid.
    public int discovered_fold_cases = 0;    //折り重なり方で、何通り発見したかを格納する。
    public IBulletinBoard bulletinBoard;
    // The result of the first foldedFigure_worker.susumu (SubFaceTotal) when looking for yet another paper overlap. If it was
    // 0, there was no room for new susumu. If non-zero, the smallest number of changed SubFace ids
    public boolean summary_write_image_during_execution = false;//matome_write_imageが実行中ならtureになる。これは、複数の折りあがり形の予測の書き出しがかすれないように使う。20170613
    // The result of foldedFigure_worker.kanou_kasanari_sagasi () when looking for another paper overlap. If
    // 0, there is no possible overlapping state.
    // If it is 1000, another way of overlapping was found.
    public String text_result;                //Instantiation of result display string class
    public LineSegmentSetWorker bb_worker = new LineSegmentSetWorker();    //Basic branch craftsman. Before passing the point set of wireFrame_worker2 to wireFrame_worker3,
    double r = 3.0;                   //Criteria for determining the radius of the circles at both ends of the straight line of the basic branch structure and the proximity of the branches to various points
    public WireFrame_Worker wireFrame_worker1 = new WireFrame_Worker(r);    //Net craftsman. Fold the input line segment set first to make a fold-up diagram of the wire-shaped point set.
    public WireFrame_Worker wireFrame_worker2 = new WireFrame_Worker(r);    //Net craftsman. It holds the folded-up view of the wire-shaped point set created by wireFrame_worker1 and functions as a line segment set.
    public WireFrame_Worker wireFrame_worker3 = new WireFrame_Worker(r);    //Net craftsman. Organize the wire-shaped point set created by wireFrame_worker1. It has functions such as recognizing a new surface.

    public FoldedFigure(IBulletinBoard bb) {
        foldedFigure_worker = new FoldedFigure_Worker(bb);
        foldedFigure_configurator = new FoldedFigure_Configurator(bb, foldedFigure_worker);
        bulletinBoard = bb;

        text_result = "";
    }

    public void estimated_initialize() {
        text_result = "";
        bb_worker.reset();
        wireFrame_worker1.reset();
        wireFrame_worker2.reset();
        wireFrame_worker3.reset();
        foldedFigure_worker.reset();

        displayStyle = DisplayStyle.NONE_0;
        estimationOrder = EstimationOrder.ORDER_0;//Instructions on how far to perform folding estimation
        estimationStep = EstimationStep.STEP_0;//Display of how far the folding estimation has been completed
        findAnotherOverlapValid = false;

        summary_write_image_during_execution = false; //If the export of multiple folded forecasts is in progress, it will be ture. 20170615
    }

    public void folding_estimated(LineSegmentSet lineSegmentSet, int startingFaceId) throws InterruptedException, FoldingException {//折畳み予測の最初に、wireFrame_worker1.lineStore2pointStore(lineStore)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
        //Folded view display camera settings

        EstimationOrder order = estimationOrder; // The latter will be reset during initialization.
        if (order == EstimationOrder.ORDER_51) {
            order = EstimationOrder.ORDER_5;
        }

        if (estimationStep == EstimationStep.STEP_0 && order.isAtLeast(EstimationOrder.ORDER_1)) {
            estimated_initialize(); // estimated_initialize
            folding_estimated_01(lineSegmentSet, startingFaceId);
            estimationStep = EstimationStep.STEP_1;
            displayStyle = DisplayStyle.DEVELOPMENT_1;
        }
        if (estimationStep == EstimationStep.STEP_1 && order.isAtLeast(EstimationOrder.ORDER_2)) {
            folding_estimated_02();
            estimationStep = EstimationStep.STEP_2;
            displayStyle = DisplayStyle.WIRE_2;
        }
        if (estimationStep == EstimationStep.STEP_2 && order.isAtLeast(EstimationOrder.ORDER_3)) {
            folding_estimated_03();
            estimationStep = EstimationStep.STEP_3;
            displayStyle = DisplayStyle.TRANSPARENT_3;
        }
        if (estimationStep == EstimationStep.STEP_3 && order.isAtLeast(EstimationOrder.ORDER_4)) {
            folding_estimated_04();
            estimationStep = EstimationStep.STEP_4;
            displayStyle = DisplayStyle.DEVELOPMENT_4;
        }
        if (estimationStep == EstimationStep.STEP_4 && order.isAtLeast(EstimationOrder.ORDER_5)) {
            folding_estimated_05();
            estimationStep = EstimationStep.STEP_5;
            displayStyle = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && !findAnotherOverlapValid) {
                estimationStep = EstimationStep.STEP_3;
                displayStyle = DisplayStyle.TRANSPARENT_3;
            }
        }
        if (estimationStep == EstimationStep.STEP_5 && order == EstimationOrder.ORDER_6) {
            folding_estimated_05();
            displayStyle = DisplayStyle.PAPER_5;
        }

    }

    public void createTwoColorCreasePattern(LineSegmentSet Ss0, int startingFaceId) throws InterruptedException {//Two-color crease pattern
        //Folded view display camera settings
        estimated_initialize();
        folding_estimated_01(Ss0, startingFaceId);
        estimationStep = EstimationStep.STEP_1;
        displayStyle = DisplayStyle.DEVELOPMENT_1;
        folding_estimated_02col();
        estimationStep = EstimationStep.STEP_2;
        displayStyle = DisplayStyle.WIRE_2;
        folding_estimated_03();
        estimationStep = EstimationStep.STEP_3;
        displayStyle = DisplayStyle.TRANSPARENT_3;
        folding_estimated_04();
        estimationStep = EstimationStep.STEP_4;
        displayStyle = DisplayStyle.DEVELOPMENT_4;
        folding_estimated_05();
        estimationStep = EstimationStep.STEP_5;
        displayStyle = DisplayStyle.PAPER_5;
        estimationStep = EstimationStep.STEP_10;
    }

    public int folding_estimated_01(LineSegmentSet lineSegmentSet, int startingFaceId) throws InterruptedException {
        Logger.info("Folding stage: 01");
        bulletinBoard.write("<<<<folding_estimated_01;  start");
        // Pass the line segment set created in mainDrawingWorker to wireFrame_worker1 by mouse input and make it a point set (corresponding to the development view).
        wireFrame_worker1.setLineSegmentSet(lineSegmentSet);
        ip3 = wireFrame_worker1.setStartingFaceId(startingFaceId);//20180222 Added to take over the previously specified reference plane when performing folding estimation with the fold line selected.

        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        return 1000;
    }

    public int folding_estimated_02() throws InterruptedException, FoldingException {
        Logger.info("Folding stage: 02");
        bulletinBoard.write("<<<<folding_estimated_02;  start");
        //cp_worker1が折りたたみを行い、できた針金図をcp_worker2に渡す。
        //wireFrame_worker1 folds and passes the resulting wire diagram to wireFrame_worker2.
        //cp_worker2が折りあがった形を少しだけ変形したいような場合に働く。
        //It works when you want to slightly deform the folded shape of wireFrame_worker2.
        wireFrame_worker2.set(wireFrame_worker1.folding());

        if (Thread.interrupted()) throw new InterruptedException();

        //wireFrame_worker2.Iti_sitei(0.0 , 0.0);点集合の平均位置を全点の重心にする。
        //  if(ip4==1){ wireFrame_worker2.uragaesi();}
        // wireFrame_worker2.set( wireFrame_worker2.oritatami())  ;//折り畳んだ針金図を、折り開きたい場合の操作
        //ここまでで針金図はできていて、cp_worker2が持っている。これは、マウスで操作、変形できる。
        return 1000;
    }

    public int folding_estimated_02col() throws InterruptedException {// 20171225　２色塗りわけをするための特別推定（折り畳み位置を推定しない）
        Logger.info("Two color stage: 02");
        bulletinBoard.write("<<<<folding_estimated_02;  start");
        wireFrame_worker2.set(wireFrame_worker1.getFacePositions());
        bulletinBoard.write("<<<<folding_estimated_02; end");

        if (Thread.interrupted()) throw new InterruptedException();

        return 1000;
    }

    public int folding_estimated_03() throws InterruptedException {
        Logger.info("Folding stage: 03");
        bulletinBoard.write("<<<<folding_estimated_03;  start");
        // wireFrame_worker2 has a set of points that holds the faces of the unfolded view before folding.
        // To estimate the vertical relationship of the surface when folded, set the surface according to the wire diagram of wireFrame_worker2.
        // Use a set of subdivided points (let's call the subdivided surface SubFace).
        // Let wireFrame_worker3 have the set of points divided into this SubFace plane.
        // Before passing the point set of wireFrame_worker2 to wireFrame_worker3, the point set of wireFrame_worker2 may have overlapping bars, so
        // Pass it to bb_worker and organize it as a set of line segments.
        Logger.info("＜＜＜＜＜folding_estimated_03()_____基本枝職人bb_workerはcp_worker2 Receives a set of line segments (made from wire diagrams) from and organizes them.");
        bb_worker.set(wireFrame_worker2.getLineStore());
        Logger.info("＜＜＜＜＜folding_estimated_03()_____基本枝職人bb_workerがbb_worker.bunkatu_seiri_for_Smen_hassei;実施。");
        //Arrangement of wire diagrams obtained by estimating the folding of overlapping line segments and intersecting line segments
        bb_worker.split_arrangement_for_SubFace_generation();
        //The crease pattern craftsman wireFrame_worker3 receives a point set (arranged wire diagram of wireFrame_worker2) from bb_worker and divides it into SubFace.
        Logger.info("＜＜＜＜＜folding_estimated_03()_____展開図職人cp_worker3はbb_worker Takes a streamlined set of line segments from and splits them into subfaces.");
        Logger.info("　　　folding_estimated_03()のcp_worker3.Senbunsyuugou2Tensyuugou(bb_worker.get());実施");
        wireFrame_worker3.setLineSegmentSet(bb_worker.get());

        Logger.info("＜＜＜＜＜folding_estimated_03()_____上下表職人ct_workerは、展開図職人cp_worker3Receive a set of points from and set subface.");
        foldedFigure_configurator.configureSubFaces(wireFrame_worker2.get(), wireFrame_worker3.get());
        //If you want to make a transparent map up to this point, you can. The transmission diagram is a SubFace diagram with density added.

        if (Thread.interrupted()) throw new InterruptedException();

        return 1000;
    }

    public int folding_estimated_04() throws InterruptedException {
        Logger.info("＜＜＜＜＜folding_estimated_04;開始");
        bulletinBoard.write("<<<<folding_estimated_04;  start");
        //Make an upper and lower table of faces (faces in the unfolded view before folding).
        // This includes the point set of wireFrame_worker2 (which has information on the positional relationship of the faces after folding).
        // Use the point set of wireFrame_worker3 (which has the information of SubFace whose surface is subdivided in the wire diagram).
        // Also, use the information on the positional relationship of the surface when folded, which wireFrame_worker1 has.
        Logger.info("＜＜＜＜＜folding_estimated_04()_____上下表職人ct_workerMake a top-bottom table of the surface (the surface of the developed view before folding).");

        ip1_anotherOverlapValid = FoldedFigure_Worker.HierarchyListStatus.UNKNOWN_0;
        findAnotherOverlapValid = false;
        ip1_anotherOverlapValid = foldedFigure_configurator.HierarchyList_configure(wireFrame_worker1);   //ip1_anotherOverlapValid = A variable that stores 0 if there is an error that the front and back sides are adjacent after folding, and 1000 if there is no error.
        if (ip1_anotherOverlapValid == FoldedFigure_Worker.HierarchyListStatus.SUCCESSFUL_1000) {
            findAnotherOverlapValid = true;
        }
        discovered_fold_cases = 0;
        Logger.info("＜＜＜＜＜oritatami_suitei_04()____終了");

        if (Thread.interrupted()) throw new InterruptedException();

        return 1000;
    }

    public int folding_estimated_05() throws InterruptedException {
        Logger.info("＜＜＜＜＜folding_estimated_05()_____上下表職人ct_workerがct_worker.possible_overlapping_search()実施。");
        bulletinBoard.write("<<<<folding_estimated_05()  ___ct_worker.possible_overlapping_search()  start");

        if ((estimationStep == EstimationStep.STEP_4) || (estimationStep == EstimationStep.STEP_5)) {
            if (findAnotherOverlapValid) {

                ip2_possibleOverlap = foldedFigure_worker.possible_overlapping_search(discovered_fold_cases == 0);//ip2_possibleOverlap = A variable that stores 0 if there is no possible overlap and 1000 if there is a possible overlap when the upper and lower table craftsmen search for a foldable overlap.

                if (ip2_possibleOverlap == 1000) {
                    discovered_fold_cases = discovered_fold_cases + 1;
                }

                ip5 = foldedFigure_worker.next(foldedFigure_worker.getSubFace_valid_number());// Preparation for the next overlap // If ip5 = 0, there was no room for new susumu. If non-zero, the smallest number of changed SubFace ids

                findAnotherOverlapValid = (ip2_possibleOverlap == 1000) && (ip5 > 0);
            }
        }
        bulletinBoard.clear();

        text_result = "Number of found solutions = " + discovered_fold_cases + "  ";

        if (!findAnotherOverlapValid) {
            text_result = text_result + " There is no other solution. ";
        }

        if (Thread.interrupted()) throw new InterruptedException();

        return 1000;
    }


    public void setAllPointStateFalse() {
        wireFrame_worker1.setAllPointStateFalse();
        wireFrame_worker2.setAllPointStateFalse();
    }

    public enum EstimationOrder {
        ORDER_0(0),
        ORDER_1(1),
        ORDER_2(2),
        ORDER_3(3),
        ORDER_4(4),
        ORDER_5(5),
        ORDER_6(6),
        ORDER_51(51),
        ;

        private final int value;

        EstimationOrder(int value) {
            this.value = value;
        }

        public boolean isAtMost(EstimationOrder that) {
            return this.value <= that.value;
        }

        public boolean isAtLeast(EstimationOrder that) {
            return this.value >= that.value;
        }
    }

    public enum EstimationStep {
        STEP_0,
        STEP_1,
        STEP_2,
        STEP_3,
        STEP_4,
        STEP_5,
        STEP_6,
        STEP_7,
        STEP_8,
        STEP_9,
        STEP_10,
    }

    public enum State {
        FRONT_0,
        BACK_1,
        BOTH_2,
        TRANSPARENT_3,
        ;

        public State advance() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    public enum DisplayStyle {
        NONE_0,
        DEVELOPMENT_1,
        WIRE_2,
        TRANSPARENT_3,
        DEVELOPMENT_4,
        PAPER_5,
    }
}
