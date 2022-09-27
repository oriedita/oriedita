package origami.data.symmetricMatrix;

/**
 * SparseSymmetricMatrix is for the largest of CPs, like Senbazuru. The idea of
 * it is to organize the matrix into blocks, and only store the blocks that
 * contains at least one non-zero element. In practice, for really large CPs,
 * about 90% of the blocks will be empty, so this implementation provides huge
 * memory saving comparing to {@link BitmapSymmetricMatrix} while getting and
 * setting are still O(1) operations, so it is only very slightly slower.
 *
 * @author Mu-Tsun Tsai
 */
public class SparseSymmetricMatrix extends SymmetricMatrix {

    /**
     * Size of each entry
     */
    private final int bits;

    /**
     * of the form 0b111..1 of length bits
     */
    private final int mask;

    private final int blockSize;
    private final int blockMax;
    private final int[] block;
    private int blockCount = 0;

    private final GiantArray data;

    SparseSymmetricMatrix(int size, int bits) {
        super(size);

        this.bits = bits;
        this.mask = (1 << bits) - 1;

        // Decide on block size
        int bSize = 1;
        while ((bSize * bSize) < size) bSize <<= 1;
        blockSize = bSize;

        blockMax = (size - 1) / blockSize + 1;
        block = new int[blockSize * blockMax * (blockMax + 1) / 2]; // 0-based

        data = new GiantArray(1 << 26); // About 1/4 GB per chunk
    }

    @Override
    public int get(int i, int j) {
        if (i > j) return get(j, i);
        int b = blockPosition(i, j);
        if (block[b] == 0) return 0;
        long pos = getBitPos(block[b] - 1, j);
        long intPos = pos >>> 5;
        int offset = (int) (pos & OFFSET_MASK);
        return (data.get(intPos) >>> offset) & mask;
    }

    @Override
    public void set(int i, int j, int value) {
        if (i > j) {
            set(j, i, value);
            return;
        }
        int b = blockPosition(i, j);
        if (block[b] == 0) block[b] = ++blockCount;
        long pos = getBitPos(block[b] - 1, j);
        long intPos = pos >>> 5;
        int offset = (int) (pos & OFFSET_MASK);
        data.set(intPos, data.get(intPos) & ~(mask << offset) | value << offset);
    }

    @Override
    public void replaceData(SymmetricMatrix other) {
        if (other instanceof SparseSymmetricMatrix && other.size == size) {
            SparseSymmetricMatrix that = (SparseSymmetricMatrix) other;
            System.arraycopy(that.block, 0, block, 0, block.length);
            blockCount = that.blockCount;
            data.replaceData(that.data);
        }
    }

    private long getBitPos(long blockPos, int j) {
        return (blockPos * blockSize + ((j - 1) % blockSize)) * bits;
    }

    private int blockPosition(long i, long j) {
        long tp = triangularPosition((i - 1) / blockSize + 1, (j - 1) / blockSize + 1, blockMax);
        return (int) (tp * blockSize + ((i - 1) % blockSize));
    }
}
