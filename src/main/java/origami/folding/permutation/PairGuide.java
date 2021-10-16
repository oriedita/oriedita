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

    private static final int mask = (1 << 16) - 1;

    private final int numDigits;

    /**
     * Each entry consists of faceId in lower bits and pointer to next entry in
     * upper bits.
     */
    private final ArrayList<Integer> entries;

    /**
     * guide[i] points to the head of the list of elements that must appear after i.
     * 
     * Orihime use int[50] for each digit, but I'm not sure if that's safe
     * (especially now with temporary guides), so I use linked list instead.
     */
    private final int[] guide;

    /**
     * goal is like the opposite of guide, where goal[i] specify the number of
     * elements that must appear before i.
     */
    private final short[] goal;

    /**
     * score[i] is the current progress element i made towards goal[i].
     */
    private final short[] score;

    // These are for adding temporary guide
    private boolean locked = false;
    private boolean added = false;
    private final short[] initGoal;
    private final int[] initGuide;
    private int initEntries = 0;

    // For longest path finding
    private boolean[] isSource;
    private int[] path;
    private int[] visited;

    public PairGuide(int numDigits) {
        this.numDigits = numDigits;
        this.score = new short[numDigits + 1];
        this.goal = new short[numDigits + 1];
        this.guide = new int[numDigits + 1];
        this.initGoal = new short[numDigits + 1];
        this.initGuide = new int[numDigits + 1];
        this.isSource = new boolean[numDigits + 1];
        this.visited = new int[numDigits + 1];
        this.path = new int[numDigits + 1];
        entries = new ArrayList<>();
        entries.add(null); // 1-based
    }

    public void reset() {
        for (int i = 1; i <= numDigits; i++) {
            score[i] = 0;
            if (added) {
                guide[i] = initGuide[i];
                goal[i] = initGoal[i];
            }
        }
        if (added) {
            entries.subList(initEntries, entries.size()).clear();
            added = false;
        }

    }

    public void confirm(int curDigit) {
        int pos = guide[curDigit];
        while (pos != 0) {
            int e = entries.get(pos);
            score[e & mask]++;
            pos = e >>> 16;
        }
    }

    public void retract(int curDigit) {
        int pos = guide[curDigit];
        while (pos != 0) {
            int e = entries.get(pos);
            score[e & mask]--;
            pos = e >>> 16;
        }
    }

    /** Lock the initial guide. */
    public int[] lock() {
        locked = true;
        initEntries = entries.size();
        for (int i = 1; i <= numDigits; i++) {
            initGoal[i] = goal[i];
            initGuide[i] = guide[i];
        }

        // Find the longest path
        int[] result = null;
        int max = 0;
        for (int i = 1; i <= numDigits; i++) {
            if (isSource[i]) {
                DFS(i, 1);
                if (path[0] > max) {
                    max = path[0];
                    result = path;
                    path = new int[numDigits + 1];
                }
            }
        }

        // Cleanup; these are no longer needed
        path = null;
        isSource = null;
        visited = null;
        return result;
    }

    private boolean DFS(int id, int depth) {
        // Memorization to speed up the search
        if (visited[id] > depth) {
            return false;
        }
        visited[id] = depth;

        // Perform search
        if (guide[id] == 0 && depth > path[0]) {
            path[0] = depth;
            path[depth] = id;
            return true;
        } else {
            int pos = guide[id];
            boolean found = false;
            while (pos != 0) {
                int e = entries.get(pos);
                if (DFS(e & mask, depth + 1)) {
                    found = true;
                }
                pos = e >>> 16;
            }
            if (found) {
                path[depth] = id;
            }
            return found;
        }
    }

    public boolean isNotReady(int curDigit) {
        return score[curDigit] < goal[curDigit];
    }

    public void add(int upperFaceIndex, int faceIndex) {
        int next = guide[upperFaceIndex];
        entries.add(faceIndex | (next << 16));
        guide[upperFaceIndex] = entries.size() - 1;
        goal[faceIndex]++;

        if (locked) { // This means this is a temporary addition.
            added = true;
            score[faceIndex]++; // To make retraction consistent.
        } else {
            isSource[upperFaceIndex] = true;
            isSource[faceIndex] = false;
        }
    }
}
