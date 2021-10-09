package origami.folding.permutation;

import java.util.ArrayList;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This class improves the efficiency of the original GuideMap class. There are
 * two differences:
 * 
 * 1. It uses goal checking mechanism to quickly determine if a digit is ready.
 * 2. It allows assigning temporary guide to vastly reduce runtime.
 * 
 * There was also this idea called "TripleGuide" which supposedly will replace
 * the functionality of penetration_inconsistent_digits_request(), but it turned
 * out that the cost of confirming and retracting in TripleGuide is too high, in
 * some cases even brings down the performance.
 */
public class PairGuide {

    private final int numDigits;

    /**
     * guide[i] contains a list of elements that must appear after i.
     * 
     * Orihime use int[50] for each digit, but I'm not sure if that's safe
     * (especially now with temporary guides), so I use ArrayList instead.
     */
    private final ArrayList<Integer>[] guide;

    /**
     * goal is like the opposite of guide, where goal[i] specify the number of
     * elements that must appear before i.
     */
    private final int[] goal;

    /**
     * score[i] is the current progress element i made towards goal[i].
     */
    private final int[] score;

    // These are for adding temporary guide
    private boolean locked = false;
    private boolean added = false;
    private final int[] initGoal;
    private final int[] initGuide;

    @SuppressWarnings("unchecked")
    public PairGuide(int numDigits) {
        this.numDigits = numDigits;
        this.score = new int[numDigits + 1];
        this.goal = new int[numDigits + 1];
        this.guide = new ArrayList[numDigits + 1];
        this.initGoal = new int[numDigits + 1];
        this.initGuide = new int[numDigits + 1];
        for (int i = 1; i <= numDigits; i++) {
            guide[i] = new ArrayList<>();
        }
    }

    public void reset() {
        for (int i = 1; i <= numDigits; i++) {
            score[i] = 0;
            if (added) {
                guide[i].subList(initGuide[i], guide[i].size()).clear();
                goal[i] = initGoal[i];
            }
        }
        added = false;
    }

    public void confirm(int curDigit) {
        for (int i : guide[curDigit]) {
            score[i]++;
        }
    }

    public void retract(int curDigit) {
        for (int i : guide[curDigit]) {
            score[i]--;
        }
    }

    /** Lock the initial guide. */
    public void lock() {
        locked = true;
        for (int i = 1; i <= numDigits; i++) {
            initGoal[i] = goal[i];
            initGuide[i] = guide[i].size();
        }
    }

    public boolean isNotReady(int curDigit) {
        return score[curDigit] < goal[curDigit];
    }

    public void add(int faceIndex, int upperFaceIndex) {
        guide[upperFaceIndex].add(faceIndex);
        goal[faceIndex]++;

        if (locked) { // This means this is a temporary addition.
            added = true;
            score[faceIndex]++; // To make retraction consistent.
        }
    }
}
