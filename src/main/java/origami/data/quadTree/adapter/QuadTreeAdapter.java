package origami.data.quadTree.adapter;

import origami.data.quadTree.QuadTreeItem;

/**
 * Author: Mu-Tsun Tsai
 * 
 * QuadTreeAdapter interface adapts different types of collection to QuadTree.
 */
public interface QuadTreeAdapter {
    
    public int getCount();

    public QuadTreeItem getItem(int index);
}
