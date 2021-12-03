package origami.folding.algorithm.italiano;

/**
 * RestorableItalianoAlgorithm allows the entire state of the algorithm to be
 * saved and restored.
 * 
 * @author Mu-Tsun Tsai
 */
public class RestorableItalianoAlgorithm extends ItalianoAlgorithm {

    /**
     * We do not declare this as final, since it's not always the case that we would
     * need the save and restore feature. We initialize it only when we need it.
     */
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
