package origami.folding.algorithm;

import java.util.*;

import origami.folding.HierarchyList;
import origami.folding.element.SubFace;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This class improves the implementation of the original SubFace priority
 * algorithm. I aim to balance speed and space, so I used linked list to
 * implement the observer pattern.
 */
public class SubFacePriority {

    public static final long mask = (1L << 32) - 1;

    // These are all 1-based
    private final int[] listHeads;
    private final int[] newInfoCount;
    private final boolean[] processed;
    private final ArrayList<List> lists = new ArrayList<>();
    private final ArrayList<Node> nodes = new ArrayList<>();

    public SubFacePriority(int totalFace, int totalSubFace) {
        listHeads = new int[totalFace + 1];
        newInfoCount = new int[totalSubFace + 1];
        processed = new boolean[totalSubFace + 1];
        lists.add(null);
        nodes.add(null);
    }

    public void addSubFace(SubFace s, int index, HierarchyList hierarchyList) {
        int count = s.getFaceIdCount();
        for (int i = 1; i < count; i++) {
            for (int j = i + 1; j <= count; j++) {
                int I = s.getFaceId(i), J = s.getFaceId(j);
                if (hierarchyList.get(I, J) == HierarchyList.EMPTY_N100) {
                    addObserver(I, J, index);
                }
            }
        }
    }

    public void processSubFace(SubFace s, int index, HierarchyList hierarchyList) {
        int count = s.getFaceIdCount();
        processed[index] = true;
        for (int i = 1; i < count; i++) {
            for (int j = i + 1; j <= count; j++) {
                int I = s.getFaceId(i), J = s.getFaceId(j);
                if (hierarchyList.get(I, J) == HierarchyList.EMPTY_N100) {
                    hierarchyList.set(I, J, HierarchyList.UNKNOWN_N50);
                    notify(I, J);
                }
            }
        }
    }

    /** high bits: max value, low bits: index */
    public long getMaxSubFace(SubFace[] subFaces) {
        long max = 0;
        int found = 0;
        for (int i = 1; i < newInfoCount.length; i++) {
            if (!processed[i] && (newInfoCount[i] > max
                    || newInfoCount[i] == max && subFaces[i].getFaceIdCount() > subFaces[found].getFaceIdCount())) {
                max = newInfoCount[i];
                found = i;
            }
        }
        return (max << 32) | found;
    }

    private void addObserver(int i, int j, int s) {
        getOrCreateList(i, j).addNode(s);
        newInfoCount[s]++;
    }

    private void notify(int i, int j) {
        int pos = getOrCreateList(i, j).head;
        while (pos != 0) {
            Node n = nodes.get(pos);
            newInfoCount[n.subFaceId]--;
            pos = n.next;
        }
    }

    private List getOrCreateList(int i, int j) {
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        int listHead = listHeads[i];
        List list = lists.get(listHead);
        while (list != null) {
            if (list.j == j) {
                return list;
            }
            list = lists.get(list.next);
        }
        listHeads[i] = lists.size();
        lists.add(list = new List(j));
        list.next = listHead;
        return list;
    }

    class List {
        public int j;
        public int head;
        public int next;

        public List(int j) {
            this.j = j;
        }

        public void addNode(int s) {
            if (head == 0) {
                head = nodes.size();
                nodes.add(new Node(s));
            } else {
                // The code here assumes that the same index is not added twice.
                int next = head;
                head = nodes.size();
                Node node = new Node(s);
                node.next = next;
                nodes.add(node);
            }
        }
    }

    class Node {
        public int subFaceId;
        public int next;

        public Node(int s) {
            this.subFaceId = s;
        }
    }
}
