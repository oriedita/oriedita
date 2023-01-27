package origami.data.quadTree.collector;

import origami.data.quadTree.QuadTree.Node;
import origami.data.quadTree.adapter.QuadTreeAdapter;

/**
 * QuadTreeCollector interface describes the behavior of a particular type of
 * collection process.
 *
 * @author Mu-Tsun Tsai
 */
public interface QuadTreeCollector {

    /**
     * Gives the starting node of the collection process.
     */
    public Node findInitial(Node root);

    /**
     * Whether this process should also collect all descendant nodes.
     */
    public boolean shouldGoDown();

    /**
     * Whether the given cursor position should be collected.
     */
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter);
}
