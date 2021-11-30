package origami.data.symmetricMatrix;

/**
 * BitmapSymmetricMatrix is the advanced implementation of SymmetricMatrix. It
 * store the data as bitmap to save memory for large matrix. It is only very
 * slightly slower than ArraySymmetricMatrix (reading and writing still take
 * only O(1) steps), and way more memory efficient.
 */
public class BitmapSymmetricMatrix extends SymmetricMatrix {

    /** Size of each entry */
    private final int bits;

    private final int length;

    /** of the form 0b111..1 of length bits */
    private final int mask;

    private final int[] data;

    protected BitmapSymmetricMatrix(int size, int bits) {
        super(size);
        if (bits > 32) {
            throw new IllegalArgumentException("bits must be <= 32");
        }

        // Our use case now no longer needs arbitrary bits size, so that entries are
        // never stored across two ints, but we add this line just to be safe.
        while (32 % bits != 0) bits++;

        this.bits = bits;
        this.mask = (1 << bits) - 1;
        double s = size;
        this.length = (int) Math.ceil(s * (s + 1) * bits / 64);
        this.data = new int[length];
    }

    @Override
    public int get(int i, int j) {
        if (j < i) {
            return get(j, i);
        }
        long pos = triangularPosition(i, j, size) * bits;
        int index = (int) (pos >>> 5);
        int offset = (int) (pos & OFFSET_MASK);
        int value = (data[index] >>> offset) & mask;
        return value;
    }

    @Override
    public void set(int i, int j, int value) {
        if (j < i) {
            set(j, i, value);
        } else {
            value = value & mask;
            long pos = triangularPosition(i, j, size) * bits;
            int index = (int) (pos >>> 5);
            int offset = (int) (pos & OFFSET_MASK);
            data[index] = data[index] & ~(mask << offset) | value << offset;
        }
    }

    @Override
    public void replaceData(SymmetricMatrix other) {
        if (other instanceof BitmapSymmetricMatrix && other.size == size) {
            BitmapSymmetricMatrix that = (BitmapSymmetricMatrix) other;
            System.arraycopy(that.data, 0, data, 0, length);
        }
    }
}
