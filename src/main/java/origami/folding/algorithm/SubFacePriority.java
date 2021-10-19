package origami.folding.algorithm;

import origami.folding.HierarchyList;
import origami.folding.element.SubFace;

import java.util.*;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This class improves the implementation of the original SubFace priority
 * algorithm.
 */
public class SubFacePriority {

    public static final long mask = (1L << 32) - 1;

    // These are all 1-based
    private final int[] newInfoCount;
    private final boolean[] processed;
    private final Map<Pair<Integer, Integer>, List<Integer>> observers;

    public SubFacePriority(int totalFace, int totalSubFace) {
        newInfoCount = new int[totalSubFace + 1];
        processed = new boolean[totalSubFace + 1];
        observers = new HashMap<>();
    }

    public void addSubFace(SubFace s, int index, HierarchyList hierarchyList) {
        int count = s.getFaceIdCount();
        for (int i = 1; i < count; i++) {
            for (int j = i + 1; j <= count; j++) {
                int I = s.getFaceId(i), J = s.getFaceId(j);
                if (hierarchyList.get(I, J) == HierarchyList.EMPTY_N100) {
                    observers.computeIfAbsent(new Pair<>(I, J), a -> new LinkedList<>()).add(index);

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
                    for (int subFaceId : observers.get(new Pair<>(I, J))) {
                        newInfoCount[subFaceId]--;
                    }
                }
            }
        }
    }

    /** high bits: max value, low bits: index */
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

    /**
     * HashMap Index
     * @param <T>
     * @param <U>
     */
    public static class Pair<T, U> {
        private final T i;
        private final U j;

        public Pair(T i, U j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(i, pair.i) && Objects.equals(j, pair.j);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }
    }
}
