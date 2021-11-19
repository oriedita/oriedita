package origami.folding;

import origami.data.symmetricMatrix.SymmetricMatrix;
import origami.folding.util.EquivalenceCondition;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HierarchyList {//This class is used to record and utilize the hierarchical relationship of faces when folded.

    public static final int BELOW_0 = 1;
    public static final int ABOVE_1 = 3;
    public static final int UNKNOWN_N50 = 2;
    public static final int EMPTY_N100 = 0;

    int facesTotal;             //Number of faces in the unfolded view before folding

    // hierarchyList[][] treats the hierarchical relationship between all the faces of the crease pattern before folding as one table.
    // If hierarchyList[i][j] is 1, surface i is above surface j. If it is 0, it is the lower side.
    // If hierarchyList[i][j] is -50, faces i and j overlap, but the hierarchical relationship is not determined.
    // If hierarchyList[i][j] is -100, then faces i and j do not overlap.
    SymmetricMatrix hierarchyList;
    SymmetricMatrix hierarchyList_copy;
    List<EquivalenceCondition> tL = new LinkedList<>(); // We need LinkedList here for fast removal
    Queue<EquivalenceCondition> uL = new ConcurrentLinkedQueue<>();

    public HierarchyList() {
        reset();
    }
    //Furthermore, when a, b, c, and d coexist in a certain SubFace, a combination that can cause penetration at the boundary line

    public void reset() {
        tL.clear();
        uL.clear();
    }

    public void save() {
        hierarchyList_copy.replaceData(hierarchyList);
    }

    public void restore() {
        hierarchyList.replaceData(hierarchyList_copy);
    }

    public void set(int i, int j, int value) {
        if (j < i) {
            value = (4 - value) % 4;
        }
        hierarchyList.set(i, j, value);
    }

    public int get(int i, int j) {
        int value = hierarchyList.get(i, j);
        if (j < i) {
            value = (4 - value) % 4;
        }
        return value;
    }

    public int getFacesTotal() {
        return facesTotal;
    }

    public void setFacesTotal(int iM) {
        facesTotal = iM;

        hierarchyList = SymmetricMatrix.create(facesTotal, 2);
        hierarchyList_copy = SymmetricMatrix.create(facesTotal, 2);
    }

    public int getEquivalenceConditionTotal() {
        return tL.size();
    }

    public Iterable<EquivalenceCondition> getEquivalenceConditions() {
        return tL;
    }

    // Add equivalence condition. When there are two adjacent faces im1 and im2 as the boundary of the bar ib, when the folding is estimated
    // The surface im located at the position where it overlaps a part of the bar ib is not sandwiched between the surface im1 and the surface im2 in the vertical direction. From this
    // The equivalent condition of gj [im1] [im] = gj [im2] [im] is satisfied.
    public void addEquivalenceCondition(EquivalenceCondition ec) {
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
    public void addUEquivalenceCondition(EquivalenceCondition ec) {
        uL.add(ec);
    }

    public boolean isEmpty(int i, int j) {
        int value = hierarchyList.get(i, j);
        return value == EMPTY_N100 || value == UNKNOWN_N50;
    }
}
