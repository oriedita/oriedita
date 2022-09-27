package origami.folding.algorithm;

import origami.data.listMatrix.ListMatrix;
import origami.folding.HierarchyList;
import origami.folding.element.SubFace;

/**
 * This class improves the implementation of the original {@link SubFace}
 * priority algorithm.
 *
 * @author Mu-Tsun Tsai
 */
public class SubFacePriority {

    public static final long mask = (1L << 32) - 1;

    // These are all 1-based
    private final int[] newInfoCount;
    private final boolean[] processed;
    private final ListMatrix observers;

    public SubFacePriority(int totalFace, int totalSubFace) {
        newInfoCount = new int[totalSubFace + 1];
        processed = new boolean[totalSubFace + 1];
        observers = new ListMatrix(totalFace);
    }

    public void addSubFace(SubFace s, int index, HierarchyList hierarchyList) {
        int count = s.getFaceIdCount();
        if (s.hasCustomConstraint()) {
            newInfoCount[index]++;
        }
        for (int i = 1; i < count; i++) {
            for (int j = i + 1; j <= count; j++) {
                int I = s.getFaceId(i), J = s.getFaceId(j);
                if (hierarchyList.get(I, J) == HierarchyList.EMPTY_N100) {
                    observers.add(I, J, index);
                    newInfoCount[index]++;
                }
            }
        }
    }

    public void processSubFace(SubFace s, int index, HierarchyList hierarchyList) {
        int count = s.getFaceIdCount();
        processed[index] = true;
        for (int i = 1; i < count; i++) {
            for (int j = i + 1; j <= count; j++) {
                int I = s.getFaceId(i), J = s.getFaceId(j);
                if (hierarchyList.get(I, J) == HierarchyList.EMPTY_N100) {
                    hierarchyList.set(I, J, HierarchyList.UNKNOWN_N50);
                    for (int subFaceId : observers.get(I, J)) {
                        newInfoCount[subFaceId]--;
                    }
                }
            }
        }
    }

    /**
     * high bits: max value, low bits: index
     */
    public long getMaxSubFace(SubFace[] subFaces) {
        long max = 0;
        int found = 0;
        for (int i = 1; i < newInfoCount.length; i++) {
            if (!processed[i] && (newInfoCount[i] > max
                    || newInfoCount[i] == max && subFaces[i].getFaceIdCount() > subFaces[found].getFaceIdCount())) {
                max = newInfoCount[i];
                found = i;
            }
        }
        return (max << 32) | found;
    }
}
