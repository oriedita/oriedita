package origami.data.symmetricMatrix;

/**
 * ArraySymmetricMatrix is the naive implementation of SymmetricMatrix using
 * plain 2D int array. It also double stores the value, despite of the fact that
 * the matrix is supposed to be symmetric. The benefit of it is that it is very
 * fast, but only works for smaller size as it uses vast amount of memory.
 */
public class ArraySymmetricMatrix extends SymmetricMatrix {

    private final int[][] matrix;

    protected ArraySymmetricMatrix(int size) {
        super(size);
        this.matrix = new int[size + 1][size + 1];
    }

    @Override
    public int get(int i, int j) {
        return matrix[i][j];
    }

    @Override
    public void set(int i, int j, int value) {
        matrix[i][j] = value;
        matrix[j][i] = value;
    }

    @Override
    public void replaceData(SymmetricMatrix other) {
        if (other instanceof ArraySymmetricMatrix && other.size == size) {
            ArraySymmetricMatrix that = (ArraySymmetricMatrix) other;
            for (int i = 1; i <= size; i++) {
                System.arraycopy(that.matrix[i], 1, matrix[i], 1, size);
            }
        }
    }
}
