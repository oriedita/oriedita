package origami.folding.algorithm.italiano;

/**
 * Author: Mu-Tsun Tsai
 * 
 * RestorableItalianoAlgorithm allows the entire state of the algorithm to be
 * saved and restored.
 */
public class RestorableItalianoAlgorithm extends ItalianoAlgorithm {

    /** This is used only in the realtime AEA. */
    private int[][] backup;

    public RestorableItalianoAlgorithm(int size) {
        super(size);
    }

    public void save() {
        backup = new int[size + 1][size + 1];
        copy(matrix, backup);
    }

    public void restore() {
        copy(backup, matrix);
    }

    private void copy(int[][] src, int[][] target) {
        for (int i = 1; i <= size; i++) {
            System.arraycopy(src[i], 1, target[i], 1, size);
        }
    }
}
