package origami.data.quadTree;

import java.util.Iterator;

/**
 * The main goal of {@link QuadTree} is to return a list of items of potential
 * interest, and in order to keep the algorithm consistent, it returns the list
 * in order. Previously, this was done by using TreeSet, but that means each
 * query will create a new TreeSet object, and the memory relocation overhead
 * for each query operation is costly. This StaticMinHeap class allocates a
 * static memory space for each thread and reuse it for each query. It also uses
 * minimal heap instead of BST to achieve the ordering functionality, since in
 * our use case we'll only iterate over the item list once and heap can do that
 * in fewer operations than BST.
 *
 * @author Mu-Tsun Tsai
 */
public class StaticMinHeap implements Iterable<Integer> {

    private static final ThreadLocal<int[]> heapStatic = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[101];
        }
    };
    private static final ThreadLocal<Integer> capacity = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 100;
        }
    };

    private final int[] heap; // 1-based
    private int size = 1;

    public StaticMinHeap(int count) {
        int c = capacity.get();
        if (c < count) {
            while (c < count) c *= 1.5;
            heapStatic.set(new int[c + 1]);
            capacity.set(c);
        }
        heap = heapStatic.get();
    }

    public void add(int value) {
        heap[size++] = value;
        int cursor = size - 1, parent;
        while (cursor > 0 && heap[cursor] < heap[parent = cursor >>> 1]) {
            swap(cursor, parent);
            cursor = parent;
        }
    }

    private void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private int extract() {
        int result = heap[1];
        heap[1] = heap[--size];
        int cursor = 1, child;
        while ((child = cursor << 1) < size) {
            if (child + 1 < size && heap[child + 1] < heap[child]) {
                child++;
            }
            if (heap[child] < heap[cursor]) {
                swap(cursor, child);
                cursor = child;
            } else {
                break;
            }
        }
        return result;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return size > 1;
            }

            @Override
            public Integer next() {
                return extract();
            }
        };
    }
}
