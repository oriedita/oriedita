package origami.crease_pattern.worker;

import org.tinylog.Logger;
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
import origami.folding.util.IBulletinBoard;

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
    private int[] frequency;
    private QuadTree qt;

    private PointSet faceFigure;
    private PointSet subFaceFigure;
    private WireFrame_Worker wireFrame_worker;
    private AdditionalEstimationAlgorithm AEA;
    private IBulletinBoard bb;

    private boolean[] isReducedSubFace;

    public FoldedFigure_Configurator(IBulletinBoard bb, FoldedFigure_Worker worker) {
        this.bb = bb;
        this.worker = worker;
    }

    public void setFaceFigure(PointSet figure) {
        faceFigure = figure;
    }

    public void setSubFaceFigure(PointSet figure) {
        subFaceFigure = figure;
    }

    public void setWireFrameWorker(WireFrame_Worker worker) {
        wireFrame_worker = worker;
    }

    public void configureSubFaces(PointSet faceFigure, PointSet subFaceFigure) throws InterruptedException {
        setFaceFigure(faceFigure);
        setSubFaceFigure(subFaceFigure);
        configureSubfaces();
    }

    /**
     * Make an upper and lower table of faces (the faces in the unfolded view before folding).
     * This includes the point set of ts2 (which has information on the positional relationship of the faces after folding) and <-------------otta_Face_figure
     * Use the point set of ts3 (which has the information of SubFace whose surface is subdivided in the wire diagram). <-------------subFaceFigure
     * Also, use the information on the positional relationship of the surface when folded, which ts1 has.
     */
    public void configureSubfaces() throws InterruptedException {
        Logger.info("SubFaces initial setup");
        worker.reset();
        worker.SubFaceTotal = subFaceFigure.getNumFaces();

        worker.s0 = new SubFace[worker.SubFaceTotal + 1];
        worker.s = new SubFace[worker.SubFaceTotal + 1];

        for (int i = 0; i < worker.SubFaceTotal + 1; i++) {
            worker.s0[i] = new SubFace(bb);
            worker.s[i] = worker.s0[i];
        }

        //Record the faces contained in each SubFace.
        Logger.info("Register the interior points of each subFace to record the faces contained in each subFace");
        Point[] subFace_insidePoint = new Point[worker.SubFaceTotal + 1];
        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            subFace_insidePoint[i] = subFaceFigure.insidePoint_surface(i);
        }

        // Also used later in setupEquivalenceConditions
        qt = new QuadTree(new PointSetFaceAdapter(faceFigure), ExpandComparator.instance);

        Logger.info("Record the faces included in each subFace");

        ExecutorService service = Executors.newWorkStealingPool();
        int faceTotal = faceFigure.getNumFaces();
        frequency = new int[faceTotal + 1];

        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            final int iff = i;
            service.execute(() -> {
                int[] s0addFaceId = new int[faceTotal + 1]; // SubFaceに追加する面を一時記録しておく
                int s0addFaceTotal = 0;

                for (int j : qt.collect(new PointCollector(subFace_insidePoint[iff]))) {
                    if (faceFigure.inside(subFace_insidePoint[iff], j) == Polygon.Intersection.INSIDE) {
                        s0addFaceId[++s0addFaceTotal] = j;
                    }
                    if (Thread.interrupted()) return;
                }
                worker.s0[iff].setNumDigits(s0addFaceTotal);

                for (int j = 1; j <= s0addFaceTotal; j++) {
                    worker.s0[iff].setFaceId(j, s0addFaceId[j]);//ここで面番号jは小さい方が先に追加される。
                }
                synchronized (frequency) {
                    for (int j = 1; j <= s0addFaceTotal; j++) frequency[s0addFaceId[j]]++;
                }
            });
            if (Thread.interrupted()) throw new InterruptedException();
        }
        shutdownAndWait(service);
        Logger.info("Creating full SubFace faceId map");
        faceToSubFaceMap = new ListArray(faceTotal, faceTotal * 5);
        for (int i = 1; i < worker.s0.length; i++) {
            for (int j = 1; j <= worker.s0[i].getFaceIdCount(); j++) {
                faceToSubFaceMap.add(worker.s0[i].getFaceId(j), i);
            }
        }
        for (CustomConstraint cc : worker.hierarchyList.getCustomConstraints()) {
            SubFace constraintSubface = findContainingSubface(cc);
            switch (cc.getFaceOrder()) {
                case NORMAL:
                    if (constraintSubface.hasTopFaceConstraint()) {
                        CustomConstraint c2 = constraintSubface.getConstraintTopFace();
                        cc = mergeConstraints(cc, c2);
                    }
                    constraintSubface.setConstraintTopFace(cc);
                    break;
                case FLIPPED:
                    if (constraintSubface.hasBottomFaceConstraint()) {
                        CustomConstraint c2 = constraintSubface.getConstraintBottomFace();
                        cc = mergeConstraints(cc, c2);
                    }
                    constraintSubface.setConstraintBottomFace(cc);
                    break;
            }

        }
        Logger.info("Calculating reduced SubFace set");
        worker.s1 = reduceSubFaceSet(worker.s0);
        frequency = null;

        //ここまでで、SubFaceTotal＝	subFaceFigure.getMensuu()のままかわりなし。
        Logger.info("各Smenに含まれる面の数の内で最大のものを求める");
        // Find the largest number of faces in each SubFace.
        worker.FaceIdCount_max = 0;
        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            int count = worker.s0[i].getFaceIdCount();
            if (count > worker.FaceIdCount_max) {
                worker.FaceIdCount_max = count;
            }
        }

        Logger.info("Creating reduced SubFace faceId map");
        faceToSubFaceMap = new ListArray(faceTotal, faceTotal * 5);
        for (int i = 1; i < worker.s1.length; i++) {
            for (int j = 1; j <= worker.s1[i].getFaceIdCount(); j++) {
                faceToSubFaceMap.add(worker.s1[i].getFaceId(j), i);
            }
        }
    }

    private SubFace findContainingSubface(CustomConstraint cc) {
        Collection<Integer> allFaces = cc.getAll();
        Map<Integer, Integer> subfaceIds = new HashMap<>();
        for (int faceId : allFaces) {
            if (subfaceIds.isEmpty()){
                for (int subfaceId : faceToSubFaceMap.get(faceId)) {
                    if (worker.s[subfaceId].getFaceIdCount() == allFaces.size()) {
                        subfaceIds.put(subfaceId, 1);
                    }
                }
            } else {
                for (int subfaceId : faceToSubFaceMap.get(faceId)) {
                    if (subfaceIds.containsKey(subfaceId)) {
                        subfaceIds.put(subfaceId, subfaceIds.get(subfaceId)+1);
                    }
                }
            }
        }
        int constraintSubfaceId = subfaceIds.entrySet().stream().filter((e) -> e.getValue() == allFaces.size()).findFirst().get().getKey();
        return worker.s[constraintSubfaceId];
    }

    private CustomConstraint mergeConstraints(CustomConstraint cc, CustomConstraint c2) {
        Set<Integer> newTop = new HashSet<>(c2.getTop());
        newTop.retainAll(cc.getTop());
        Set<Integer> newBottom = new HashSet<>(c2.getBottom());
        newBottom.addAll(cc.getBottom());
        cc = new CustomConstraint(cc.getFaceOrder(), newTop, newBottom, cc.getPos(), cc.getType());
        return cc;
    }

    /**
     * If the faces of a SubFace A is a subset of the faces of a SubFace B, then A
     * cannot possibly contribute any new relations that B would not contribute, so
     * we don't need to process A at all. This method removes all SubFaces that are
     * subsets of other SubFaces. In some CPs, this even removes more than half of
     * the SubFaces.
     */
    private SubFace[] reduceSubFaceSet(SubFace[] s) throws InterruptedException {
        Map<Integer, List<Integer>> map = new HashMap<>();

        isReducedSubFace = new boolean[s.length];
        Map<SubFace, Integer> subFaceToId = new HashMap<>();
        for (int i = 1; i < s.length; i++) {
            subFaceToId.put(s[i], i);
        }

        s = s.clone();
        Arrays.sort(s, 1, s.length, Comparator.comparingInt(SubFace::getFaceIdCount).reversed());
        List<SubFace> reduced = new ArrayList<>();
        reduced.add(s[0]);
        for (int i = 1; i < s.length; i++) {
            int count = s[i].getFaceIdCount();
            if (count == 0) continue;
            if (s[i].hasCustomConstraint()) {
                reduced.add(s[i]);
                continue;
            }

            // First we sort face id by frequency
            Integer[] ids = new Integer[count + 1];
            for (int j = 1; j <= count; j++) ids[j] = s[i].getFaceId(j);
            Arrays.sort(ids, 1, count + 1, Comparator.comparingInt(id -> frequency[id]));

            boolean isNotSubset = false;
            if (!map.containsKey(ids[1])) isNotSubset = true;
            else {
                List<Integer> superSets = new LinkedList<>(map.get(ids[1]));
                for (int f = 2; f <= count && superSets.size() > 0; f++) {
                    Iterator<Integer> it = superSets.iterator();
                    while (it.hasNext()) {
                        SubFace sf = reduced.get(it.next());
                        if (!sf.contains(ids[f])) it.remove();
                    }
                }
                isNotSubset = superSets.size() == 0;
            }
            if (isNotSubset) {
                int id = reduced.size();
                reduced.add(s[i]);
                isReducedSubFace[subFaceToId.get(s[i])] = true;
                for (int f = 1; f <= count; f++) {
                    map.computeIfAbsent(ids[f], k -> new ArrayList<>()).add(id);
                }
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }
        return reduced.toArray(new SubFace[0]);
    }

    public HierarchyListStatus HierarchyList_configure(WireFrame_Worker orite) throws InterruptedException {
        setWireFrameWorker(orite);
        return HierarchyList_configure();
    }

    public HierarchyListStatus HierarchyList_configure() throws InterruptedException {
        bb.write("           HierarchyList_configure   step1   start ");
        HierarchyListStatus result = setupHierarchyList();
        if (result != HierarchyListStatus.SUCCESSFUL_1000) {
            return result;
        }

        // First round of AEA; this will save both time and space later on
        int capacity = worker.FaceIdCount_max * worker.FaceIdCount_max;
        AEA = new AdditionalEstimationAlgorithm(bb, worker.hierarchyList, worker.s1, capacity);
        result = AEA.run(0);
        if (result != HierarchyListStatus.SUCCESSFUL_1000) {
            worker.errorPos = AEA.errorPos;
            return result;
        }

        //----------------------------------------------
        bb.rewrite(10, "           HierarchyList_configure   step2   start ");
        result = setupEquivalenceConditions();
        if (result != HierarchyListStatus.SUCCESSFUL_1000) return result;

        //----------------------------------------------
        bb.write("           HierarchyList_configure   step3   start ");
        result = setupUEquivalenceConditions();
        if (result != HierarchyListStatus.SUCCESSFUL_1000) return result;

        faceToSubFaceMap = null;
        System.gc();

        bb.write("           HierarchyList_configure   step4   start ");
        // Second round of AEA
        AEA.removeMode = true; // This time we turn on the remove mode.
        result = AEA.run(0);
        if (result != HierarchyListStatus.SUCCESSFUL_1000) {
            worker.errorPos = AEA.errorPos;
            return result;
        }
        AEA = null; // Now we can release the memory
        System.gc();
        
        worker.hierarchyList.sortEquivalenceConditions();
        // Here we can compare and see the huge difference before and after AEA
        Logger.info("３面が関与する突き抜け条件の数　＝　{}", worker.hierarchyList.getEquivalenceConditionTotal());
        Logger.info("４面が関与する突き抜け条件の数　＝　{}", worker.hierarchyList.getUEquivalenceConditionTotal());

        Logger.info("追加推定 終了し、上下表を保存------------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");

        //*************Saving the results of the first deductive reasoning**************************
        worker.hierarchyList.save();//Save the hierarchical relationship determined from the mountain fold and valley fold information.
        //************************************************************************
        bb.rewrite(10, "           HierarchyList_configure   step5   start ");

        //s0に優先順位をつける(このときhierarchyListの-100のところが変るところがある)
        Logger.info("Smen(s0)に優先順位をつける");

        setupSubFacePriority();
        setupGuideMap();

        // If any SubFace failed to initialize, then the constraints are impossible.
        for (int i = 1; i <= worker.SubFace_valid_number; i++) {
            if (worker.s[i].getPermutationCount() == 0) {
                // TODO: we can add impossible constraint indication.
                return HierarchyListStatus.CONSTRAINT_5;
            }
        }

        //SubFaceは優先順の何番目までやるかを決める

        Logger.info("Smen有効数は　{} ／ {}", worker.SubFace_valid_number, worker.SubFaceTotal);
        Logger.info("上下表初期設定終了");
        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    private HierarchyListStatus setupHierarchyList() throws InterruptedException {
        worker.hierarchyList.setFacesTotal(faceFigure.getNumFaces());

        //Put the hierarchical relationship determined from the information of mountain folds and valley folds in the table above and below.
        Logger.info("山折り谷折りの情報から決定される上下関係を上下表に入れる");
        int faceId_min, faceId_max;
        for (int ib = 1; ib <= wireFrame_worker.getNumLines(); ib++) {
            faceId_min = wireFrame_worker.lineInFaceBorder_min_request(ib);
            faceId_max = wireFrame_worker.lineInFaceBorder_max_request(ib);
            if (faceId_min != faceId_max) {// In the developed view, there are faces on both sides of the rod ib.
                int minPos = wireFrame_worker.getIFacePosition(faceId_min);
                int maxPos = wireFrame_worker.getIFacePosition(faceId_max);
                if (minPos % 2 == maxPos % 2) {
                    return HierarchyListStatus.UNKNOWN_0;
                }
                if (faceFigure.getColor(ib) == LineColor.RED_1) {// Red line means mountain fold
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
        Logger.info("等価条件を設定する   ");
        ExecutorService service = Executors.newWorkStealingPool();
        worker.errorPos = null;

        //等価条件を設定する。棒ibを境界として隣接する2つの面im1,im2が有る場合、折り畳み推定した場合に
        //棒ibの一部と重なる位置に有る面imは面im1と面im2に上下方向で挟まれることはない。このことから
        //gj[im1][im]=gj[im2][im]という等価条件が成り立つ。
        for (int ib = 1; ib <= wireFrame_worker.getNumLines(); ib++) {
            final int ibf = ib;
            service.execute(() -> {
                int faceId_min = wireFrame_worker.lineInFaceBorder_min_request(ibf);
                int faceId_max = wireFrame_worker.lineInFaceBorder_max_request(ibf);
                if (faceId_min != faceId_max) {//展開図において、棒ibの両脇に面がある
                    Point p = faceFigure.getBeginPointFromLineId(ibf);
                    Point q = faceFigure.getEndPointFromLineId(ibf);
                    // This qt here is the same instance as in SubFace_configure()
                    for (int im : qt.collect(new LineSegmentCollector(p, q))) {
                        if ((im != faceId_min) && (im != faceId_max)) {
                            if (faceFigure.convex_inside(ibf, im)) {
                                // AEA cannot run in parallel
                                synchronized (AEA) {
                                    if (Thread.interrupted()) return;
                                    // We add the 3EC through AEA, so if it is consumed immediately, it will not be
                                    // actually added. This helps saves memory.
                                    if (!AEA.addEquivalenceCondition(im, faceId_min, faceId_max)) {
                                        // Error handling is also needed here
                                        worker.errorPos = AEA.errorPos;
                                        service.shutdownNow();
                                    }
                                }
                            }
                        }
                    }
                }
            });
            if (Thread.interrupted()) throw new InterruptedException();
        }
        shutdownAndWait(service);
        if (worker.errorPos != null) return HierarchyListStatus.CONTRADICTED_3;

        Logger.info("３面が関与する突き抜け条件の数　＝　{}", worker.hierarchyList.getEquivalenceConditionTotal());

        qt = null; // no longer needed
        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    private HierarchyListStatus setupUEquivalenceConditions() throws InterruptedException {
         // Add equivalence condition. There are two adjacent faces im1 and im2 as the boundary of the bar ib,
        // Also, there are two adjacent faces im3 and im4 as the boundary of the bar jb, and when ib and jb are parallel and partially overlap, when folding is estimated.
        // The surface of the bar ib and the surface of the surface jb are not aligned with i, j, i, j or j, i, j, i. If this happens,
        // Since there is a mistake in the 3rd place from the beginning, find the number of digits in this 3rd place with SubFace and advance this digit by 1.

        QuadTree qt = new QuadTree(new PointSetLineAdapter(faceFigure));
        ExecutorService service = Executors.newWorkStealingPool();
        worker.errorPos = null;

        for (int ib = 1; ib <= wireFrame_worker.getNumLines() - 1; ib++) {
            final int ibf = ib;
            final int mi1 = wireFrame_worker.lineInFaceBorder_min_request(ibf);
            final int mi2 = wireFrame_worker.lineInFaceBorder_max_request(ibf);
            if (mi1 != mi2 && mi1 != 0) {
                service.execute(() -> {
                    for (int jb : qt.getPotentialCollision(ibf)) {
                        if (Thread.interrupted()) break;
                        int mj1 = wireFrame_worker.lineInFaceBorder_min_request(jb);
                        int mj2 = wireFrame_worker.lineInFaceBorder_max_request(jb);
                        if (mj1 != mj2 && mj1 != 0) {
                            if (faceFigure.parallel_overlap(ibf, jb)) {
                                if (exist_identical_subFace(mi1, mi2, mj1, mj2)) {
                                    // AEA cannot run in parallel
                                    synchronized (AEA) {
                                        if (Thread.interrupted()) return;
                                        // We add the 4EC through AEA, so if it is consumed immediately, it will not be
                                        // actually added. This helps saves memory.
                                        if (!AEA.addUEquivalenceCondition(mi1, mi2, mj1, mj2)) {
                                            // Error handling is also needed here
                                            worker.errorPos = AEA.errorPos;
                                            service.shutdownNow();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
            if (Thread.interrupted()) throw new InterruptedException();
        }
        shutdownAndWait(service);
        if (worker.errorPos != null) return HierarchyListStatus.CONTRADICTED_4;

        Logger.info("４面が関与する突き抜け条件の数　＝　{}", worker.hierarchyList.getUEquivalenceConditionTotal());
        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    private void setupSubFacePriority() throws InterruptedException {
        // Priority initialization; it suffices to do just the reduced SubFaces
        int[] priorityMap = new int[worker.SubFaceTotal + 1];
        int reducedSubFaceTotal = worker.s1.length - 1;
        SubFacePriority SFP = new SubFacePriority(worker.hierarchyList.getFacesTotal(), reducedSubFaceTotal);
        for (int i = 1; i <= reducedSubFaceTotal; i++) {
            SFP.addSubFace(worker.s1[i], i, worker.hierarchyList);
        }

        // Priority processing
        for (int i = 1; i <= reducedSubFaceTotal; i++) {// 優先度i番目のSubFaceIdをさがす。
            long result = SFP.getMaxSubFace(worker.s1);
            int i_yusen = (int) (result & SubFacePriority.mask);
            int max = (int) (result >>> 32);
            priorityMap[i] = i_yusen; // 優先度からs0のidを指定できるようにする
            if (max > 0) {
                worker.SubFace_valid_number++;
            }

            SFP.processSubFace(worker.s1[i_yusen], i_yusen, worker.hierarchyList);
            if (Thread.interrupted()) throw new InterruptedException();
        }

        // Logger.info("------------" );
        Logger.info("上下表職人内　Smensuu = " + worker.SubFaceTotal);
        Logger.info("上下表職人内　s0に優先順位をつける");
        Logger.info("上下表職人内　優先度からs0のid");

        for (int i = 1; i <= reducedSubFaceTotal; i++) {
            worker.s[i] = worker.s1[priorityMap[i]];
        }

        // Fill the rest with non-reduced ones
        int j = 1;
        for (int i = reducedSubFaceTotal + 1; i <= worker.SubFaceTotal; i++) {
            while (isReducedSubFace[j]) j++;
            worker.s[i] = worker.s0[j];
            j++;
        }
        isReducedSubFace = null; // no longer needed
    }

    private void setupGuideMap() throws InterruptedException {
        // Make a guidebook for each valid SubFace.
        // Previously this is done for all SubFaces, which is unnecessary.
        Logger.info("Building guides for SubFace");
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
        // We could choose the im with the least frequency, but experiments showed that
        // it makes little difference.
        for (int i : faceToSubFaceMap.get(im1)) {
            // faceToSubFaceMap is now generated for s1
            if (worker.s1[i].contains(im1, im2, im3, im4)) return true;
        }
        return false;
    }
}
