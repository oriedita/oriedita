package origami.data.quadTree.collector;

import origami.data.quadTree.QuadTree.Node;

public abstract class RecursiveCollector implements QuadTreeCollector {

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
