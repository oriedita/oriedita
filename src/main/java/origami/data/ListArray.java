package origami.data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This data structure is for "an array of lists". It supports adding elements
 * and iterating over the list of a given index.
 */
public class ListArray<T> {

    // 1-based
    private final int[] firstChild;
    private final ArrayList<Integer> next;
    private final ArrayList<T> values;

    public ListArray(int count) {
        firstChild = new int[count + 1];
        next = new ArrayList<Integer>(count * 2); // Should be a fair guess
        values = new ArrayList<T>(count * 2);
        next.add(null);
        values.add(null);
    }

    public final void add(int index, T value) {
        next.add(firstChild[index]);
        firstChild[index] = values.size();
        values.add(value);
    }

    public final Iterable<T> get(final int index) {
        return () -> new Iterator<T>() {
            int nextChild = firstChild[index];
            T nextValue = values.get(nextChild);

            @Override
            public boolean hasNext() {
                return nextValue != null;
            }

            @Override
            public T next() {
                T result = nextValue;
                nextChild = next.get(nextChild);
                nextValue = values.get(nextChild);
                return result;
            }
        };
    }
}
