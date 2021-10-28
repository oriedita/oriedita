package origami.folding.algorithm;

import origami.folding.element.SubFace;

/**
 * Author: Mu-Tsun Tsai
 * 
 * The original Orihime algorithm chooses an initial SubFace ordering to perform
 * the exhaustive search, but it is very commonly the case that the ordering is
 * not optimal, leading to a phenomenon where the search reaches the same
 * dead-end at a certain depth over and over.
 * 
 * Previously, I defined the notion of "bouncing" and performed swapping by
 * detecting the bouncing. However, for very large CPs, this strategy is still
 * too slow. In this class, I choose a much more eager strategy, and it proved
 * to be a lot more efficient especially for giant CPs.
 */
public class SwappingAlgorithm {

    private int high;

    // These two are for preventing cycling swapping over and over.
    private int repetition = 0;
    private int lastHigh;
    private int lastLow;

    /** Records a dead-end. */
    public void record(int value) {
        high = value;
    }

    /** Performs the swap. */
    public void process(SubFace[] s) {
        if (high == 0) return;

        int low = high / 2;
        if (high == lastHigh) low -= ++repetition;
        else repetition = 0;

        if (low < 1) return; // Swapping algorithm has reached its limit.

        // Perform swap
        System.out.println("swapper.swap(s, " + high + ", " + low + ");");
        swap(s, high, low);
        lastHigh = high;
        lastLow = low;
        high = 0;
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
        SubFace temp = s[high];
        for (int i = high; i > low; i--) {
            s[i] = s[i - 1];
            s[i].clearTempGuide();
        }
        s[low] = temp;
    }

    public void reverseSwap(SubFace[] s, int high, int low) {
        SubFace temp = s[low];
        for (int i = low; i < high; i++) {
            s[i] = s[i + 1];
        }
        s[high] = temp;
    }
}
