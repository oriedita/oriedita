package origami.data.symmetricMatrix;

/**
 * This is the base class of 1-based symmetric matrix.
 * 
 * @author Mu-Tsun Tsai
 */
public abstract class SymmetricMatrix {

    public static SymmetricMatrix create(int size, int bits) {
        if (size < 100) {
            return new ArraySymmetricMatrix(size);
        } else {
            return new BitmapSymmetricMatrix(size, bits);
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
}
