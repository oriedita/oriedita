package origami.folding.algorithm.swapping;

import java.util.*;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This is the base class for swapping algorithms.
 * 
 * One problem that may arises with the swapping algorithm is looping, where two
 * or more elements swap in a loop (and possibly reset each other during the
 * process). In order to prevent this, the swapping algorithm now implements a
 * hash table recording the visited element sequence. If the same sequence
 * reappears and swapping is again requested, it will introduce unvisited
 * elements to the game to spice things up. This has proven to be quite
 * effective in breaking the loop.
 */
public class SwappingAlgorithm<T> {

    private int high;
    private int hash;

    // For preventing cycling swapping over and over.
    private final Set<Integer> history = new HashSet<>();
    private final Set<T> visited = new HashSet<>();

    /** Records a dead-end. */
    public void record(int index) {
        high = index;
    }

    /** Performs the swap. */
    public final void process(T[] s, int max) {
        if (high < 2) return;

        hash = getHash(s, high);
        if (history.contains(hash)) {
            // Introduce unvisited element to the game.
            int reverseResult = reverseSwap(s, 1, high, max, 1); // side effect on hash
            if (reverseResult == high) return; // Let's hope that this never happen, or we're out of tricks.
            else high = reverseResult;
        }
        history.add(hash);

        // Perform swap
        int low = high / 2;
        swap(s, high, low);
        high = 0;

        onAfterProcess(s, low, max);
    }

    private int getHash(T[] s, int high) {
        Object[] obj = new Object[high];
        for (int i = 0; i < high; i++) {
            obj[i] = s[i + 1];
        }
        return Arrays.hashCode(obj);
    }

    public void swap(T[] s, int high, int low) {
        onBeforeSwap(high, low);
        T temp = s[high];
        for (int i = high; i > low; i--) {
            s[i] = s[i - 1];
            onSwapOver(s[i]);
        }
        s[low] = temp;
    }

    protected int reverseSwap(T[] s, int index, int high, int max, int r) {
        for (int i = index + 1; i <= max && r > 0; i++) {
            if (!visited.contains(s[i])) {
                visited.add(s[i]);
                swap(s, i, index);
                hash = getHash(s, ++high);
                r--;
            }
        }
        return high;
    }

    public void visit(T s) {
        visited.add(s);
    }

    public int getVisitedCount() {
        return visited.size();
    }

    /////////////////////////////////////////////////////
    // Hooks
    /////////////////////////////////////////////////////

    protected void onAfterProcess(T[] s, int low, int max) {
        // Default behavior is doing nothing.
    }

    protected void onBeforeSwap(int high, int low) {
        // Default behavior is doing nothing.
    }

    protected void onSwapOver(T s) {
        // Default behavior is doing nothing.
    }
}
