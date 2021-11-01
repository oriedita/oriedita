package origami.data.listMatrix;

import java.util.*;

/**
 * Author: Mu-Tsun Tsai
 * 
 * The idea of PseudoListMatrix is to store, for each i, the values appear in
 * List[i][x] and List[x][i] for all x, instead of keeping the precise list of
 * List[i][j] for all i and j. Then when List[i][j] is requested, it returns the
 * intersection set of List[i][x] and List[x][j].
 * 
 * This ensures that if a value s is in List[i][j] then it will be returned by
 * get(i, j), but not necessarily the other way. Although whoever uses it still
 * need to check that the returned values are really in List[i][j], this class
 * is excellent in memory efficiency (it uses space O(n) instead of O(n^2))
 * while remains fast in iterating over the list.
 * 
 * Even better, in fact in our use case the returned list is exactly the actual
 * list (see the comments in AEA).
 */
public class PseudoListMatrix {

    private final Map<Integer, SortedSet<Integer>> mapI;
    private final Map<Integer, SortedSet<Integer>> mapJ;

    public PseudoListMatrix(int count) {
        mapI = new HashMap<Integer, SortedSet<Integer>>(count / 3);
        mapJ = new HashMap<Integer, SortedSet<Integer>>(count / 3);
    }

    public void add(int i, int j, int value) {
        mapI.computeIfAbsent(i, k -> new TreeSet<Integer>()).add(value);
        mapJ.computeIfAbsent(j, k -> new TreeSet<Integer>()).add(value);
    }

    public Iterable<Integer> get(int i, int j) {
        return () -> new Iterator<Integer>() {
            Iterator<Integer> Ii;
            Iterator<Integer> Ij;
            Integer next;
            {
                SortedSet<Integer> Si = mapI.get(i);
                SortedSet<Integer> Sj = mapJ.get(j);
                if (Si != null && Sj != null) {
                    Ii = Si.iterator();
                    Ij = Sj.iterator();
                    getNext();
                }
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Integer next() {
                int result = next;
                getNext();
                return result;
            }

            public void getNext() {
                next = null;
                if (Ii.hasNext() && Ij.hasNext()) {
                    int s = Ii.next();
                    int t = Ij.next();
                    while (next == null) {
                        if (s < t) {
                            if (!Ii.hasNext()) {
                                break;
                            }
                            s = Ii.next();
                        } else if (t < s) {
                            if (!Ij.hasNext()) {
                                break;
                            }
                            t = Ij.next();
                        } else {
                            next = s;
                        }
                    }
                }
            }
        };
    }
}