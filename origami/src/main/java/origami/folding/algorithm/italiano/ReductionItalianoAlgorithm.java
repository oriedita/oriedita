package origami.folding.algorithm.italiano;

import java.util.*;

/**
 * One interesting thing about Italiano algorithm is that it can also be used to
 * efficiently find transitive reduction, since it keeps a spanning tree for
 * each source node. These trees together form a structure that is pretty close
 * to transitive reduction, and with just a bit more work we can obtain the
 * actual reduction.
 * 
 * @author Mu-Tsun Tsai
 */
public class ReductionItalianoAlgorithm extends TraceableItalianoAlgorithm {

    public ReductionItalianoAlgorithm(int size) {
        super(size);
    }

    /**
     * Returns all edges in the transitive reduction, in the form of upper bits ->
     * lower bits.
     */
    public Iterable<Integer> getReduction() {
        // Collect all edges in spanning trees
        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (int i = 1; i <= size; i++) {
            DFS(i, i, map);
        }

        List<Integer> results = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Set<Integer> set = map.getOrDefault(i, null);
            if (set == null) continue;

            // Find transitive reduction
            List<Integer> list = new ArrayList<>(set);
            for (int j = 0; j < list.size(); j++) {
                for (int k = 0; k < list.size(); k++) {
                    if (j != k && matrix[list.get(j)][list.get(k)] != 0) {
                        set.remove(list.get(j));
                        break;
                    }
                }
            }

            // Collect result
            for (int j : set) results.add((j << 16) | i);
        }
        return results;
    }

    private void DFS(int source, int cursor, Map<Integer, Set<Integer>> map) {
        int child = matrix[source][cursor] >>> 17;
        while (child != 0) {
            map.computeIfAbsent(child, k -> new HashSet<>()).add(cursor);
            DFS(source, child, map);
            child = matrix[source][child] & mask;
        }
    }
}
