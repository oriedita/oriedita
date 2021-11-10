package origami.data;

import java.util.Iterator;

/**
 * This data structure is for "an array of stacks". In addition to the methods
 * of {@link ListArray}, it also supports flushing the stack of a given index,
 * and it will reuse the memory after flushing.
 * 
 * The idea is that chain index zero stands for "empty slots" that can be reused
 * when new entires are added.
 * 
 * @author Mu-Tsun Tsai
 */
public final class StackArray extends ListArray {

    // 1-based
    private final int[] tail;

    public StackArray(int count, int capacity) {
        super(count, capacity);
        tail = new int[count + 1];
    }

    public void add(int index, int value) {
        int cursor = head[0], oh = head[index];
        if (cursor != 0) {
            // Fill entry to a blank slot
            head[0] = next[cursor];
            next[cursor] = oh;
            head[index] = cursor;
            values[cursor] = value;
        } else {
            super.add(index, value);
        }
        if (oh == 0) tail[index] = cursor;
    }

    public void clear(int index) {
        if (head[0] == 0) {
            // If there's no empty slots, set the existing chain to be empty.
            head[0] = head[index];
        } else {
            // Otherwise chain the empty slots to the existing chain.
            next[tail[0]] = head[index];
        }
        tail[0] = tail[index];

        // Clear existing chain.
        head[index] = 0;
        tail[index] = 0;
    }

    /**
     * Iterate and pop at once. This is a lot more efficient than popping one by
     * one, but it's important that the new entires are not added to the stack
     * during the process.
     */
    public Iterable<Integer> flush(final int index) {
        return () -> new Iterator<Integer>() {
            int nextChild = head[index];

            @Override
            public boolean hasNext() {
                return nextChild != 0;
            }

            @Override
            public Integer next() {
                int result = values[nextChild];
                nextChild = next[nextChild];
                if (nextChild == 0) {
                    clear(index);
                }
                return result;
            }
        };
    }
}
