package origami.folding;

import origami.folding.util.EquivalenceCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HierarchyList {//This class is used to record and utilize the hierarchical relationship of faces when folded.
    int facesTotal;             //Number of faces in the unfolded view before folding

    // hierarchyList[][] treats the hierarchical relationship between all the faces of the crease pattern before folding as one table.
    // If hierarchyList[i][j] is 1, surface i is above surface j. If it is 0, it is the lower side.
    // If hierarchyList[i][j] is -50, faces i and j overlap, but the hierarchical relationship is not determined.
    // If hierarchyList[i][j] is -100, then faces i and j do not overlap.
    HierarchyListCondition[][] hierarchyList;
    HierarchyListCondition[][] hierarchyList_copy;
    ArrayList<EquivalenceCondition> tL = new ArrayList<>();
    Queue<EquivalenceCondition> uL = new ConcurrentLinkedQueue<>();

    // This is tL grouped by `a`, to speed things up.
    Map<Integer, ArrayList<EquivalenceCondition>> tLMap = new HashMap<>();

    public HierarchyList() {
        reset();
    }
    //Furthermore, when a, b, c, and d coexist in a certain SubFace, a combination that can cause penetration at the boundary line

    public void reset() {
        tL.clear();
        tLMap.clear();
        uL.clear();
    }

    public void save() {
        for (int i = 1; i <= facesTotal; i++) {
            for (int j = 1; j <= facesTotal; j++) {
                hierarchyList_copy[i][j] = hierarchyList[i][j];
            }
        }
    }

    public void restore() {
        for (int i = 1; i <= facesTotal; i++) {
            for (int j = 1; j <= facesTotal; j++) {
                hierarchyList[i][j] = hierarchyList_copy[i][j];
            }
        }
    }

    public void set(int i, int j, HierarchyListCondition condition) {
        hierarchyList[i][j] = condition;
    }

    public HierarchyListCondition get(int i, int j) {
        return hierarchyList[i][j];
    }

    public int getFacesTotal() {
        return facesTotal;
    }

    public void setFacesTotal(int iM) {
        facesTotal = iM;

        hierarchyList = new HierarchyListCondition[facesTotal + 1][facesTotal + 1];
        hierarchyList_copy = new HierarchyListCondition[facesTotal + 1][facesTotal + 1];

        for (int i = 0; i <= facesTotal; i++) {
            for (int j = 0; j <= facesTotal; j++) {
                hierarchyList[i][j] = HierarchyListCondition.EMPTY_N100;
                hierarchyList_copy[i][j] = HierarchyListCondition.EMPTY_N100;
            }
        }
    }

    public int getEquivalenceConditionTotal() {
        return tL.size();
    }

    public Iterable<EquivalenceCondition> getEquivalenceConditions() {
        return tL;
    }

    public Iterable<EquivalenceCondition> getEquivalenceConditions(int a) {
		return tLMap.get(a);
	}

    // Add equivalence condition. When there are two adjacent faces im1 and im2 as the boundary of the bar ib, when the folding is estimated
    // The surface im located at the position where it overlaps a part of the bar ib is not sandwiched between the surface im1 and the surface im2 in the vertical direction. From this
    // The equivalent condition of gj [im1] [im] = gj [im2] [im] is satisfied.
    public void addEquivalenceCondition(int ai, int bi, int ci, int di) {
        ArrayList<EquivalenceCondition> tL_ai = tLMap.get(ai);
        if (tL_ai == null) {
            tL_ai = new ArrayList<>();
            tLMap.put(ai, tL_ai);
        }
        EquivalenceCondition ec = new EquivalenceCondition(ai, bi, ci, di);
        tL_ai.add(ec);
        tL.add(ec);
    }

    public int getUEquivalenceConditionTotal() {
        return uL.size();
    }

    public Iterable<EquivalenceCondition> getUEquivalenceConditions() {
        return uL;
    }

    // Add equivalence condition. There are two adjacent faces im1 and im2 as the boundary of the bar ib,
    // Also, there are two adjacent faces im3 and im4 as the boundary of the bar jb, and when ib and jb are parallel and partially overlap, when folding is estimated.
    // The surface of the bar ib and the surface of the surface jb are not aligned with i, j, i, j or j, i, j, i. If this happens,
    // Since there is a mistake in the 3rd place from the beginning, find out what digit this 3rd place is in SubFace and advance this digit by 1.
    public void addUEquivalenceCondition(int ai, int bi, int ci, int di) {
        uL.add(new EquivalenceCondition(ai, bi, ci, di));
    }

    public enum HierarchyListCondition {
        UNKNOWN_0,
        UNKNOWN_1,
        UNKNOWN_N50,
        EMPTY_N100,
        ;

        public boolean isEmpty() {
            return this == UNKNOWN_N50 || this == EMPTY_N100;
        }
    }
}
