package origami.data.quadTree.collector;

import java.util.ArrayList;

import origami.data.quadTree.QuadTree.Node;
import origami.data.quadTree.adapter.QuadTreeAdapter;

public class CollisionCollector implements QuadTreeCollector {

    private final int i;
    private final int min;
    private final ArrayList<Node> map;

    public CollisionCollector(int i, int min, ArrayList<Node> map) {
        this.i = i;
        this.min = min;
        this.map = map;
    }

    @Override
    public Node findInitial(Node root) {
        return map.get(i);
    }

    @Override
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter) {
        return cursor > min;
    }

    @Override
    public boolean shouldGoDown() {
        return true;
    }

}
