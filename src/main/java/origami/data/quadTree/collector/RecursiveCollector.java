package origami.data.quadTree.collector;

import origami.data.quadTree.QuadTree.Node;
import origami.data.quadTree.adapter.QuadTreeAdapter;

public abstract class RecursiveCollector implements QuadTreeCollector {

    protected final QuadTreeAdapter adapter;

    public RecursiveCollector(QuadTreeAdapter adapter) {
        this.adapter = adapter;
    }

    public abstract boolean contains(Node node);

    @Override
    public final Node findInitial(Node node) {
        if (!contains(node)) {
            return null;
        }
        if (node.children[0] != null) {
            for (int j = 0; j < 4; j++) {
                Node n = findInitial(node.children[j]);
                if (n != null) {
                    return n;
                }
            }
        }
        return node;
    }
}
