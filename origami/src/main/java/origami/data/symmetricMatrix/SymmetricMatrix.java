package origami.data.symmetricMatrix;

/**
 * This is the base class of 1-based symmetric matrix.
 *
 * @author Mu-Tsun Tsai
 */
public abstract class SymmetricMatrix {

    protected static final int OFFSET_MASK = (1 << 5) - 1;

    public static SymmetricMatrix create(int size, int bits) {
        if (size > 40000) {
            return new SparseSymmetricMatrix(size, bits);
        } else if (size > 2000) {
            return new BitmapSymmetricMatrix(size, bits);
        } else {
            return new ArraySymmetricMatrix(size);
        }
    }

    protected final int size;

    protected SymmetricMatrix(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public abstract int get(int i, int j);

    public abstract void set(int i, int j, int value);

    public abstract void replaceData(SymmetricMatrix matrix);

    /**
     * For the triangular region 1 <= i <= j <= max, returns the 0-based position of
     * (i, j), in the ordering of (1, 1), ..., (1, max), (2, 2), ..., (2, max), (3,
     * 3), ..., ..., (max, max).
     */
    protected static long triangularPosition(long i, long j, int max) {
        // we use long here to prevent multiplication overflow
        return (2L * max + 2 - i) * (i - 1) / 2 + j - i;
    }
}
