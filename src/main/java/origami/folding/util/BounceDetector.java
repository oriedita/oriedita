package origami.folding.util;

import java.util.*;

import origami.folding.element.SubFace;

/**
 * Author: Mu-Tsun Tsai
 * 
 * The original Orihime algorithm chooses an initial SubFace ordering to perform
 * the exhaustive search, but it is very commonly the case that the ordering is
 * not optimal, leading to a phenomenon I called "bouncing", where the search
 * hits a dead-end at a certain depth (H) and have to return to another certain
 * depth (L) repeatedly. This suggests that SubFace H is more "relevant" to
 * those SubFaces before L, so swapping H and L typically leads to a much faster
 * search.
 * 
 * This BounceDetector class detects the bouncing phenomenon based on some
 * sensitivity constants and makes the swap when it detects one. A direct
 * swapping turns out to be the best; I've tried rotation but it's not really
 * helping.
 * 
 * Although this technique in general greatly shortens the runtime for any
 * model, in some cases it takes a while to detect some less obvious or deeply
 * buried bouncing. To improve those will take a greater understanding of the
 * Orihime algorithm.
 */
public class BounceDetector {

    private static final int threshold = 3;
    private static final int target = 6;

    private final Map<Integer, Integer> high = new HashMap<>();
    private final Map<Integer, Integer> low = new HashMap<>();
    private final List<Integer> highMatch = new ArrayList<>();
    private final List<Integer> lowMatch = new ArrayList<>();

    // To prevent back-and-forth swapping
    private int lastH = 0, lastL = 0;

    public BounceDetector() {
        reset();
    }

    /** Records a high end. */
    public void recordHigh(int value) {
        if (add(value, high) == threshold) {
            highMatch.add(value);
        }
    }

    /** Records a low end. */
    public void recordLow(int value) {
        if (add(value, low) == threshold) {
            lowMatch.add(value);
        }
    }

    /** Performs the swap if bouncing is detected. */
    public void checkAndSwap(SubFace[] s) {
        // Previously it was required that the difference is greater than 1,
        // but it turns out that it is not necessary.
        int max = 0;
        int H = 0, L = 0;

        // Find the best matching
        for (int h : highMatch) {
            for (int l : lowMatch) {
                int diff = h - l;
                int score = Math.min(high.get(h), low.get(l)) / threshold + diff;
                if (score > target && diff > max && (h != lastH || l != lastL)) {
                    H = h;
                    L = l;
                    max = diff;
                }
            }
        }

        if (H != 0) {
            // Perform swap
            System.out.println("Swap " + H + " with " + L);
            SubFace temp = s[H];
            s[H] = s[L];
            s[L] = temp;

            // Record
            lastH = H;
            lastL = L;

            // Finally, reset all data
            reset();
        }
    }

    /** Resets all data. */
    private void reset() {
        high.clear();
        low.clear();
        highMatch.clear();
        lowMatch.clear();
    }

    /** Adds a value into a given record and returns the current count. */
    private int add(int value, Map<Integer, Integer> record) {
        int count = 1;
        if (record.containsKey(value)) {
            count += record.get(value);
        }
        record.put(value, count);
        return count;
    }
}
