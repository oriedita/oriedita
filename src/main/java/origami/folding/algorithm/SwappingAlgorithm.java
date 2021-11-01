package origami.folding.algorithm;

import java.util.*;

import origami.folding.element.SubFace;

/**
 * Author: Mu-Tsun Tsai
 * 
 * The original Orihime algorithm chooses an initial SubFace ordering to perform
 * the exhaustive search, but it is very commonly the case that the ordering is
 * not optimal, leading to a phenomenon where the search reaches the same
 * dead-end at a certain depth over and over. The idea of swapping algorithm is
 * to swap the order of the SubFace reaching a dead-end to a earlier position,
 * and doing so generally improves ths searching performance.
 * 
 * One problem that may arises with the swapping algorithm is looping, where two
 * or more SubFaces swap in a loop (and possibly reset each other during the
 * process). In order to prevent this, the swapping algorithm now implements a
 * hash table recording the visited SubFace sequence. If the same sequence
 * reappears and swapping is again requested, it will introduce unvisited
 * SubFaces to the game to spice things up. This has proven to be quite
 * effective in breaking the loop.
 */
public class SwappingAlgorithm {

    private int high;
    private int lastLow;
    private int repetition;
    private int hash;

    // For preventing cycling swapping over and over.
    private final Set<Integer> history = new HashSet<>();
    private final Set<Integer> visited = new HashSet<>();

    /** Records a dead-end. */
    public void record(int value) {
        high = value;
    }

    /** Performs the swap. */
    public void process(SubFace[] s, int max) {
        if (high == 0) return;

        hash = getHash(s, high);
        if (history.contains(hash)) {
            // Introduce unvisited SubFaces to the game.
            int reverseResult = reverseSwap(s, 1, max); // side effect on hash
            if (reverseResult == high) return; // Let's hope that this never happen, or we're out of tricks.
            else high = reverseResult;
        }
        history.add(hash);

        // Perform swap
        int low = high / 2;
        swap(s, high, low);
        lastLow = low;
        high = 0;

		// To further improve performance
		reverseSwapCore(s, low, max, s[low].swapCounter - 1);
	}

    private int getHash(SubFace[] s, int high) {
        int[] ids = new int[high];
        for (int i = 0; i < high; i++) {
            ids[i] = s[i + 1].id;
        }
        return Arrays.hashCode(ids);
    }

    public boolean shouldEstimate(int s) {
        if (lastLow == 0) return true;
        if (s == lastLow) {
            lastLow = 0;
            return true;
        }
        return false;
    }

    public void swap(SubFace[] s, int high, int low) {
        System.out.println("swapper.swap(s, " + high + ", " + low + ");");
        SubFace temp = s[high];
        for (int i = high; i > low; i--) {
            s[i] = s[i - 1];
            s[i].clearTempGuide();
        }
        s[low] = temp;
    }

    public int reverseSwap(SubFace[] s, int index, int max) {
        return reverseSwapCore(s, index, max, ++repetition);
    }

    private int reverseSwapCore(SubFace[] s, int index, int max, int r) {
        int result = index;
        for (int i = index + 1; i <= max && r > 0; i++) {
            if (!visited.contains(s[i].id)) {
                visited.add(s[i].id);
                swap(s, i, index);
                hash = getHash(s, ++result);
                r--;
            }
        }
        return result;
    }

    public void visit(SubFace s) {
        visited.add(s.id);
    }

    public int getVisitedCount() {
        return visited.size();
    }
}
