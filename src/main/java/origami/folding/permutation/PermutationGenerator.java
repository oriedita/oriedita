package origami.folding.permutation;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This is the base class for different permutation generator implementations.
 */
public abstract class PermutationGenerator {

    /** Number of valid permutations found. */
    protected int count;

    /** Total number of digits. */
    protected final int numDigits;

    /** digits[i] gives the element at position i. */
    protected final int[] digits;

    /** map[i] gives the position of element i. */
    protected final int[] map;

    public PermutationGenerator(int numDigits) {
        this.numDigits = numDigits;
        this.digits = new int[numDigits + 1];
        this.map = new int[numDigits + 1];
    }

    public final int locate(int i) {
        return map[i];
    }

    public final int getCount() {
        return count;
    }

    public final int getPermutation(int digit) {
        return digits[digit];
    }

    /** Remember to reset at the end of initialization. */
    public abstract void initialize() throws InterruptedException;

    /** Reset to the first valid permutation. */
    public abstract void reset() throws InterruptedException;

    /** Clear all temporary guides. */
    public abstract void clearTempGuide();

    /** Returns the lowest digit that was changed in the process. */
    public abstract int next(int digit) throws InterruptedException;

    /** Add a constraint saying that "from" must appear before "to". */
    public abstract void addGuide(int from, int to);
}
