package origami.data.symmetricMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a data structure for extremely large (that is, the indices may exceed
 * the limit of int type) and dynamic integer array.
 *
 * @author Mu-Tsun Tsai
 */
public class GiantArray {

    private final List<int[]> chunks = new ArrayList<>();
    private final int chunkSize;

    public GiantArray(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int get(long index) {
        int[] chunk = getOrCreateChunk((int) (index / chunkSize));
        return chunk[(int) (index % chunkSize)];
    }

    public void set(long index, int value) {
        int[] chunk = getOrCreateChunk((int) (index / chunkSize));
        chunk[(int) (index % chunkSize)] = value;
    }

    private int[] getOrCreateChunk(int chunkPos) {
        while (chunks.size() <= chunkPos) {
            chunks.add(new int[chunkSize]);
        }
        return chunks.get(chunkPos);
    }

    public void replaceData(GiantArray that) {
        // Make the chunk size equal
        int thatChunks = that.chunks.size();
        if (chunks.size() > thatChunks) {
            chunks.subList(thatChunks, chunks.size()).clear();
            System.gc(); // Since the array is so large, it's good to force GC here.
        }
        while (chunks.size() < thatChunks) {
            chunks.add(new int[chunkSize]);
        }

        // Copy chunk data
        for (int i = 0; i < thatChunks; i++) {
            System.arraycopy(that.chunks.get(i), 0, chunks.get(i), 0, chunkSize);
        }
    }
}
