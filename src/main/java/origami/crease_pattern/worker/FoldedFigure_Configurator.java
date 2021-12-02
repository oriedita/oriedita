package origami.crease_pattern.worker;

import origami.Epsilon;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.crease_pattern.worker.FoldedFigure_Worker.HierarchyListStatus;
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
import origami.folding.constraint.CustomConstraint;
import origami.folding.element.SubFace;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class isolates those codes related to configuring {@link FoldedFigure_Worker}.
 * 
 * @author Mu-Tsun Tsai
 */
public class FoldedFigure_Configurator {
    
    private final FoldedFigure_Worker worker;
    private ListArray faceToSubFaceMap;
    private QuadTree qt;

    private PointSet otta_face_figure;
    private PointSet SubFace_figure;
    private WireFrame_Worker orite;
    private AdditionalEstimationAlgorithm AEA;

    public FoldedFigure_Configurator(FoldedFigure_Worker worker) {
        this.worker = worker;
    }

    public void setFaceFigure(PointSet figure) {
        otta_face_figure = figure;
    }

    public void setSubFaceFigure(PointSet figure) {
        SubFace_figure = figure;
    }

    public void setWireFrameWorker(WireFrame_Worker worker) {
        orite = worker;
    }

    public void SubFace_configure() throws InterruptedException {
        // Make an upper and lower table of faces (the faces in the unfolded view before folding).
        // This includes the point set of ts2 (which has information on the positional relationship of the faces after folding) and <-------------otta_Face_figure
        // Use the point set of ts3 (which has the information of SubFace whose surface is subdivided in the wire diagram). <-------------SubFace_figure
        // Also, use the information on the positional relationship of the surface when folded, which ts1 has.

        System.out.println("Smenの初期設定");
        worker.reset();
        worker.SubFaceTotal = SubFace_figure.getNumFaces();

        worker.s0 = new SubFace[worker.SubFaceTotal + 1];
        worker.s = new SubFace[worker.SubFaceTotal + 1];

        for (int i = 0; i < worker.SubFaceTotal + 1; i++) {
            worker.s0[i] = new SubFace(worker.bb);
            worker.s[i] = worker.s0[i];
        }

        //Record the faces contained in each SubFace.
        System.out.println("各Smenに含まれる面を記録するため、各Smenの内部点を登録");
        Point[] subFace_insidePoint = new Point[worker.SubFaceTotal + 1];  //<<<<<<<<<<<<<<<<<<<<<<<<<<<オブジェクトの配列を動的に指定
        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            subFace_insidePoint[i] = SubFace_figure.insidePoint_surface(i);
        }

        // Also used later in setupEquivalenceConditions
        qt = new QuadTree(new PointSetFaceAdapter(otta_face_figure), ExpandComparator.instance);

        System.out.println("各Smenに含まれる面を記録する");

        int faceTotal = otta_face_figure.getNumFaces();
        faceToSubFaceMap = new ListArray(faceTotal, faceTotal * 5);
        int[] s0addFaceId = new int[faceTotal + 1]; // SubFaceに追加する面を一時記録しておく

        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            int s0addFaceTotal = 0;

            for (int j : qt.collect(new PointCollector(subFace_insidePoint[i]))) {
                j++; // qt is 0-based
                if (otta_face_figure.inside(subFace_insidePoint[i], j) == Polygon.Intersection.INSIDE) {
                    s0addFaceId[++s0addFaceTotal] = j;
                }
            }

            worker.s0[i].setNumDigits(s0addFaceTotal);

            for (int j = 1; j <= s0addFaceTotal; j++) {
                worker.s0[i].setFaceId(j, s0addFaceId[j]);//ここで面番号jは小さい方が先に追加される。
                faceToSubFaceMap.add(s0addFaceId[j], i);
            }

            if (Thread.interrupted()) throw new InterruptedException();
        }
        for (CustomConstraint cc : worker.hierarchyList.getCustomConstraints()) {
            Map<Integer, Integer> subfaceIds = new HashMap<>();
            Set<Integer> allFaces = new HashSet<>(cc.getBottom());
            allFaces.addAll(cc.getTop());
            for (Integer faceId : allFaces) {
                if (subfaceIds.isEmpty()){
                    for (Integer subfaceId : faceToSubFaceMap.get(faceId)) {
                        if (worker.s[subfaceId].getFaceIdCount() == allFaces.size()) {
                            subfaceIds.put(subfaceId, 1);
                        }
                    }
                } else {
                    for (Integer subfaceId : faceToSubFaceMap.get(faceId)) {
                        if (subfaceIds.containsKey(subfaceId)) {
                            subfaceIds.put(subfaceId, subfaceIds.get(subfaceId)+1);
                        }
                    }
                }
            }
            int constraintSubfaceId = subfaceIds.entrySet().stream().filter((e) -> e.getValue() == allFaces.size()).findFirst().get().getKey();
            SubFace constraintSubface = worker.s[constraintSubfaceId];
            if (constraintSubface.hasCustomConstraint()) {
                CustomConstraint c2 = constraintSubface.getCustomConstraint();
                Set<Integer> newTop = new HashSet<>(c2.getTop());
                newTop.retainAll(cc.getTop());
                Set<Integer> newBottom = new HashSet<>(c2.getBottom());
                newBottom.addAll(cc.getBottom());
                cc = new CustomConstraint(cc.getFaceOrder(), newTop, newBottom, cc.getPos(), cc.getType());
            }
            constraintSubface.setCustomConstraint(cc);
        }
        worker.s1 = reduceSubFaceSet(worker.s0);

        //ここまでで、SubFaceTotal＝	SubFace_figure.getMensuu()のままかわりなし。
        System.out.println("各Smenに含まれる面の数の内で最大のものを求める");
        // Find the largest number of faces in each SubFace.
        worker.FaceIdCount_max = 0;
        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            if (worker.s0[i].getFaceIdCount() > worker.FaceIdCount_max) {
                worker.FaceIdCount_max = worker.s0[i].getFaceIdCount();
            }
        }
    }

     /**
     * If the faces of a SubFace A is a subset of the faces of a SubFace B, then A
     * cannot possibly contribute any new relations that B would not contribute, so
     * we don't need to process A at all. This method removes all SubFaces that are
     * subsets of other SubFaces. In some CPs, this even removes more than half of
     * the SubFaces.
     */
    private SubFace[] reduceSubFaceSet(SubFace[] s) throws InterruptedException {
        Map<Integer, List<Integer>> faceToSubFaceMap = new HashMap<>();
        s = s.clone();
        Arrays.sort(s, 1, s.length, Comparator.comparingInt(SubFace::getFaceIdCount).reversed());
        List<SubFace> reduced = new ArrayList<>();
        reduced.add(s[0]);
        for (int i = 1; i < s.length; i++) {
            if (s[i].getFaceIdCount() == 0) continue;
            if (s[i].hasCustomConstraint()) {
                reduced.add(s[i]);
                continue;
            }
            boolean isNotSubset = false;
            int faceId = s[i].getFaceId(1);
            if (!faceToSubFaceMap.containsKey(faceId)) isNotSubset = true;
            else {
                Set<Integer> superSets = new HashSet<>(faceToSubFaceMap.get(faceId));
                for (int f = 2; f <= s[i].getFaceIdCount() && superSets.size() > 0; f++) {
                    Iterator<Integer> it = superSets.iterator();
                    faceId = s[i].getFaceId(f);
                    while (it.hasNext()) {
                        SubFace sf = reduced.get(it.next());
                        if (!sf.contains(faceId)) it.remove();
                    }
                }
                isNotSubset = superSets.size() == 0;
            }
            if (isNotSubset) {
                int id = reduced.size();
                reduced.add(s[i]);
                for (int f = 1; f <= s[i].getFaceIdCount(); f++) {
                    faceId = s[i].getFaceId(f);
                    faceToSubFaceMap.computeIfAbsent(faceId, k -> new ArrayList<>()).add(id);
                }
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }
        return reduced.toArray(new SubFace[0]);
    }

    public HierarchyListStatus HierarchyList_configure() throws InterruptedException {
        worker.bb.write("           HierarchyList_configure   step1   start ");
        HierarchyListStatus result = setupHierarchyList();
        if (result != HierarchyListStatus.SUCCESSFUL_1000) {
            return result;
        }

        // First round of AEA; this will save both time and space later on
        int capacity = worker.FaceIdCount_max * worker.FaceIdCount_max;
        AEA = new AdditionalEstimationAlgorithm(worker.bb, worker.hierarchyList, worker.s1, capacity);
        result = AEA.run(0);
        if (result != HierarchyListStatus.SUCCESSFUL_1000) {
            worker.errorPos = AEA.errorPos;
            return result;
        }

        //----------------------------------------------
        worker.bb.rewrite(10, "           HierarchyList_configure   step2   start ");
        result = setupEquivalenceConditions();
        if (result != HierarchyListStatus.SUCCESSFUL_1000) {
            return result;
        }

        //----------------------------------------------
        worker.bb.write("           HierarchyList_configure   step3   start ");
        setupUEquivalenceConditions();

        faceToSubFaceMap = null;
        System.gc();

        worker.bb.write("           HierarchyList_configure   step4   start ");
        // Second round of AEA
        AEA.removeMode = true; // This time we turn on the remove mode.
        result = AEA.run(0);
        if (result != HierarchyListStatus.SUCCESSFUL_1000) {
            worker.errorPos = AEA.errorPos;
            return result;
        }
        AEA = null; // Now we can release the memory
        System.gc();
        
        // Here we can compare and see the huge difference before and after AEA
        System.out.print("３面が関与する突き抜け条件の数　＝　");
        System.out.println(worker.hierarchyList.getEquivalenceConditionTotal());
        System.out.print("４面が関与する突き抜け条件の数　＝　");
        System.out.println(worker.hierarchyList.getUEquivalenceConditionTotal());

        System.out.println("追加推定 終了し、上下表を保存------------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        //*************Saving the results of the first deductive reasoning**************************
        worker.hierarchyList.save();//Save the hierarchical relationship determined from the mountain fold and valley fold information.
        //************************************************************************
        worker.bb.rewrite(10, "           HierarchyList_configure   step5   start ");

        //s0に優先順位をつける(このときhierarchyListの-100のところが変るところがある)
        System.out.println("Smen(s0)に優先順位をつける");

        setupSubFacePriority();
        setupGuideMap();

        //SubFaceは優先順の何番目までやるかを決める

        System.out.print("Smen有効数は　");
        System.out.print(worker.SubFace_valid_number);
        System.out.print("／");
        System.out.println(worker.SubFaceTotal);
        System.out.println("上下表初期設定終了");
        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    private HierarchyListStatus setupHierarchyList() throws InterruptedException {
        worker.hierarchyList.setFacesTotal(otta_face_figure.getNumFaces());

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
                        worker.hierarchyList.set(faceId_min, faceId_max, HierarchyList.ABOVE_1);
                    } else {//The surface Mid_max has the same orientation as the reference surface (the surface faces up)
                        worker.hierarchyList.set(faceId_min, faceId_max, HierarchyList.BELOW_0);
                    }
                } else {//The blue line means valley fold
                    if (minPos % 2 == 1) {//面Mid_minは基準面と同じ向き(表面が上を向く)
                        worker.hierarchyList.set(faceId_min, faceId_max, HierarchyList.BELOW_0);
                    } else {//面Mid_maxは基準面と同じ向き(表面が上を向く)
                        worker.hierarchyList.set(faceId_min, faceId_max, HierarchyList.ABOVE_1);
                    }
                }
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }

        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    private HierarchyListStatus setupEquivalenceConditions() throws InterruptedException {
        System.out.println("等価条件を設定する   ");
        //等価条件を設定する。棒ibを境界として隣接する2つの面im1,im2が有る場合、折り畳み推定した場合に
        //棒ibの一部と重なる位置に有る面imは面im1と面im2に上下方向で挟まれることはない。このことから
        //gj[im1][im]=gj[im2][im]という等価条件が成り立つ。
        int faceId_min, faceId_max;
        for (int ib = 1; ib <= orite.getNumLines(); ib++) {
            faceId_min = orite.lineInFaceBorder_min_request(ib);
            faceId_max = orite.lineInFaceBorder_max_request(ib);
            if (faceId_min != faceId_max) {//展開図において、棒ibの両脇に面がある
                Point p = otta_face_figure.getBeginPointFromLineId(ib);
                Point q = otta_face_figure.getEndPointFromLineId(ib);
                // This qt here is the same instance as in SubFace_configure()
                for (int im : qt.collect(new LineSegmentCollector(p, q))) {
                    im++; // qt is 0-based
                    if ((im != faceId_min) && (im != faceId_max)) {
                        if (otta_face_figure.convex_inside(ib, im)) {
                            //下の２つのifは暫定的な処理。あとで置き換え予定
                            if (otta_face_figure.convex_inside(Epsilon.UNKNOWN_05, ib, im)) {
                                if (otta_face_figure.convex_inside(-Epsilon.UNKNOWN_05, ib, im)) {
                                    // We add the 3EC through AEA, so if it is consumed immediately, it will not be
                                    // actually added. This helps saves memory.
                                    if(!AEA.addEquivalenceCondition(im, faceId_min, faceId_max)) {
                                        // Error handling is also needed here
                                        worker.errorPos = AEA.errorPos;
                                        return HierarchyListStatus.CONTRADICTED_3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }

        System.out.print("３面が関与する突き抜け条件の数　＝　");
        System.out.println(worker.hierarchyList.getEquivalenceConditionTotal());

        qt = null; // no longer needed
        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    private void setupUEquivalenceConditions() throws InterruptedException {
         // Add equivalence condition. There are two adjacent faces im1 and im2 as the boundary of the bar ib,
        // Also, there are two adjacent faces im3 and im4 as the boundary of the bar jb, and when ib and jb are parallel and partially overlap, when folding is estimated.
        // The surface of the bar ib and the surface of the surface jb are not aligned with i, j, i, j or j, i, j, i. If this happens,
        // Since there is a mistake in the 3rd place from the beginning, find the number of digits in this 3rd place with SubFace and advance this digit by 1.

        QuadTree qt = new QuadTree(new PointSetLineAdapter(otta_face_figure));
        ExecutorService service = Executors.newWorkStealingPool();

        for (int ib = 1; ib <= orite.getNumLines() - 1; ib++) {
            final int ibf = ib;
            final int mi1 = orite.lineInFaceBorder_min_request(ibf);
            final int mi2 = orite.lineInFaceBorder_max_request(ibf);
            if (mi1 != mi2 && mi1 != 0) {
                service.execute(() -> {
                    for (int jb : qt.getPotentialCollision(ibf - 1)) { // qt is 0-based
                        if (Thread.interrupted()) break;
                        final int jbf = jb + 1; // qt is 0-based
                        int mj1 = orite.lineInFaceBorder_min_request(jbf);
                        int mj2 = orite.lineInFaceBorder_max_request(jbf);
                        if (mj1 != mj2 && mj1 != 0) {
                            if (otta_face_figure.parallel_overlap(ibf, jbf)) {
                                if (exist_identical_subFace(mi1, mi2, mj1, mj2)) {
                                    // For the moment AEA doesn't support parallel processing, so we cannot add 4EC
                                    // through it the same way we did with 3EC. Fortunately the total number of 4EC
                                    // is in general a lot less than 3EC, so this is not a problem.
                                    worker.hierarchyList.addUEquivalenceCondition(mi1, mi2, mj1, mj2);
                                }
                            }
                        }
                    }
                });
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }
        shutdownAndWait(service);

        System.out.print("４面が関与する突き抜け条件の数　＝　");
        System.out.println(worker.hierarchyList.getUEquivalenceConditionTotal());
    }

    private void setupSubFacePriority() throws InterruptedException {
        // Priority initialization
        int[] priorityMap = new int[worker.SubFaceTotal + 1];
        SubFacePriority SFP = new SubFacePriority(worker.hierarchyList.getFacesTotal(), worker.SubFaceTotal);
        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            SFP.addSubFace(worker.s0[i], i, worker.hierarchyList);
        }

        // Priority processing
        for (int i = 1; i <= worker.SubFaceTotal; i++) {// 優先度i番目のSubFaceIdをさがす。
            long result = SFP.getMaxSubFace(worker.s0);
            int i_yusen = (int) (result & SubFacePriority.mask);
            int max = (int) (result >>> 32);
            priorityMap[i] = i_yusen; // 優先度からs0のidを指定できるようにする
            if (max > 0) {
                worker.SubFace_valid_number++;
            }

            SFP.processSubFace(worker.s0[i_yusen], i_yusen, worker.hierarchyList);
            if (Thread.interrupted()) throw new InterruptedException();
        }

        // System.out.println("------------" );
        System.out.println("上下表職人内　Smensuu = " + worker.SubFaceTotal);
        System.out.println("上下表職人内　s0に優先順位をつける");
        System.out.println("上下表職人内　優先度からs0のid");

        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            worker.s[i] = worker.s0[priorityMap[i]];
        }
    }

    private void setupGuideMap() throws InterruptedException {
        // Make a guidebook for each valid SubFace.
        // Previously this is done for all SubFaces, which is unnecessary.
        System.out.println("Building guides for SubFace");
        ExecutorService service = Executors.newWorkStealingPool();
        for (int i = 1; i <= worker.SubFace_valid_number; i++) {
            final SubFace sf = worker.s[i];
            service.execute(() -> sf.setGuideMap(worker.hierarchyList));
        }
        shutdownAndWait(service);

        //優先順位を逆転させる。これが有効かどうかは不明wwwww
    }

    private void shutdownAndWait(ExecutorService service) throws InterruptedException {
        // Done adding tasks, shut down ExecutorService
        service.shutdown();
        try {
            while (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                // For really large CP, it could take longer time to finish. Just wait.
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                // This is not supposed to happen if each thread handles interruption correctly.
                throw new RuntimeException("HierarchyList_configure did not exit!");
            }
            throw e;
        }
    }

    //引数の４つの面を同時に含むSubFaceが1つ以上存在するなら１、しないなら０を返す。
    private boolean exist_identical_subFace(int im1, int im2, int im3, int im4) {
        for (int i : faceToSubFaceMap.get(im1)) {
            if (worker.s[i].contains(im1, im2, im3, im4)) {
                return true;
            }
        }
        return false;
    }
}
