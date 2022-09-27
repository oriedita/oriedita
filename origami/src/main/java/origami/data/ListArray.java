package origami.data;

import java.util.Iterator;

/**
 * This data structure is for "an array of lists". It supports adding elements
 * and iterating over the list of a given index.
 *
 * @author Mu-Tsun Tsai
 */
public class ListArray {

    // 1-based
    protected final int[] head;
    protected int[] next;
    protected int[] values;
    protected int[] sizes;
    private int capacity;
    private int size;

    public ListArray(int count, int capacity) {
        this.capacity = capacity;
        head = new int[count + 1];
        sizes = new int[count + 1];
        next = new int[capacity + 1];
        values = new int[capacity + 1];
    }

    public void add(int index, int value) {
        if (size == capacity) grow();
        int cursor = ++size;
        next[cursor] = head[index];
        head[index] = cursor;
        values[cursor] = value;
        sizes[index]++;
    }

    private void grow() {
        capacity *= 1.1;
        int[] newNext = new int[capacity + 1];
        int[] newValues = new int[capacity + 1];
        System.arraycopy(next, 1, newNext, 1, size);
        System.arraycopy(values, 1, newValues, 1, size);
        next = newNext;
        values = newValues;
    }

    public final Iterable<Integer> get(final int index) {
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
                return result;
            }
        };
    }

    public final int size(int index) {
        return sizes[index];
    }
}
