package origami.folding.permutation;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This is a much more efficient permutation generator than the original
 * implementation by Mr.Meguro. It uses the classical digit swapping idea to
 * reduce half of the work searching for next available element. It is also
 * equipped with an improved PairGuide to help skipping vast amount of
 * permutations that won't work.
 * 
 * The idea behind ChainPermutationGenerator is that it locks the ordering of
 * the longest chain found in the given PairGuide. This reduces as much as l!
 * iterations, where l is the length of the chain.
 */
public class ChainPermutationGenerator extends PermutationGenerator {

    // swapHistory[i] == j >= i means we swap index i with index j in step i;
    // swapHistory[i] == i - 1 means we're not done yet.
    private final int[] swapHistory;

    private final PairGuide pairGuide;

    // An initial permutation, where the lock sequence return by PairGuide is
    // located at the end.
    private final int[] initPermutation;

    // If an element is in the lock sequence.
    private final boolean[] isLocked;

    private int lockCount;
    private int lockRemain;

    public ChainPermutationGenerator(int numDigits) {
        super(numDigits);
        this.initPermutation = new int[numDigits + 1];
        this.isLocked = new boolean[numDigits + 1];
        this.swapHistory = new int[numDigits + 1];
        this.pairGuide = new PairGuide(numDigits);
    }

    public void reset() {
        count = 0;
        lockRemain = lockCount;
        for (int i = 1; i <= numDigits; i++) {
            digits[i] = initPermutation[i];
            map[i] = i;
            swapHistory[i] = i - 1;
        }
        pairGuide.reset();
        next(0);
    }

    public int next(int digit) {
        int curIndex = 1;

        // swapHistory[1] == 0 means the generator has just reset, and we don't need to
        // do anything. Otherwise we need to retract to the requested digit.
        if (swapHistory[1] != 0) {
            curIndex = numDigits;

            // It is possible that temp guides are added, so that the last element is no
            // longer a leaf node, and we must retract it to makes things consistent.
            pairGuide.retract(digits[curIndex]);

            do {
                swapHistory[curIndex] = curIndex - 1;
                retract(--curIndex);
            } while (curIndex > digit);
        }

        while (curIndex < numDigits) {
            int swapIndex = swapHistory[curIndex];
            int curDigit = 0;

            // Find the next available element.
            do {
                swapIndex++;
                if (swapIndex > numDigits - lockRemain + 1) {
                    break;
                }
                curDigit = digits[swapIndex];
            } while (pairGuide.isNotReady(curDigit));

            // If the current digit has no available element, retract.
            if (swapIndex > numDigits - lockRemain + 1) {
                swapHistory[curIndex] = curIndex - 1;
                if (--curIndex == 0) {
                    return 0;
                }
                retract(curIndex);
                if (curIndex < digit) {
                    digit = curIndex;
                }
                continue;
            }

            // Make the swap.
            if (swapIndex != curIndex) {
                digits[swapIndex] = digits[curIndex];
                digits[curIndex] = curDigit;
            }
            swapHistory[curIndex] = swapIndex;
            map[curDigit] = curIndex;
            if (isLocked[curDigit]) {
                lockRemain--;
            }
            pairGuide.confirm(curDigit);

            curIndex++;
        }

        // Fill the last element into the map. There's no need to confirm the last
        // element, because it is definitely a leaf node.
        map[digits[numDigits]] = numDigits;

        count++;
        return digit;
    }

    @Override
    public void clearTempGuide() {
        pairGuide.clearTempGuide(count != 0);
    }

    @Override
    public void addGuide(int upperFaceIndex, int faceIndex) {
        pairGuide.add(upperFaceIndex, faceIndex);
    }

    public void initialize() {
        // Determine locked elements.
        int[] lock = pairGuide.lock();
        if (lock != null) {
            lockCount = lock[0];
            for (int i = 1; i <= lockCount; i++) {
                isLocked[lock[i]] = true;
            }

            // Prepare initial permutation.
            int i, j = 1;
            for (i = 1; i <= numDigits - lockCount; i++) {
                while (isLocked[j]) {
                    j++;
                }
                initPermutation[i] = j;
                j++;
            }
            for (i = 1; i <= lockCount; i++) {
                initPermutation[i + numDigits - lockCount] = lock[i];
            }

            // When generating permutations, the last locked element behaves the same as
            // normal elements.
            isLocked[lock[lockCount]] = false;
        } else {
            for (int i = 1; i <= numDigits; i++) {
                initPermutation[i] = i;
            }
        }

        reset();
    }

    private void retract(int index) {
        int swapIndex = swapHistory[index];
        int curDigit = digits[index];
        if (swapIndex != index) {
            digits[index] = digits[swapIndex];
            digits[swapIndex] = curDigit;
        }
        map[curDigit] = 0;
        if (isLocked[curDigit]) {
            lockRemain++;
        }
        pairGuide.retract(curDigit);
    }
}
