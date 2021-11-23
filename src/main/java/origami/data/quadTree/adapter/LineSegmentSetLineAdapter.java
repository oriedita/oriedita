package origami.data.quadTree.adapter;

import origami.crease_pattern.LineSegmentSet;
import origami.data.quadTree.QuadTreeItem;

/**
 * LineSegmentSetLineAdapter is a {@link LineSegmentSetAdapter} of which items
 * are the lines.
 * 
 * @author Mu-Tsun Tsai
 */
public class LineSegmentSetLineAdapter extends LineSegmentSetAdapter {

    public LineSegmentSetLineAdapter(LineSegmentSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumLineSegments();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        return QuadTreeAdapter.createItem(set.getA(index), set.getB(index));
    }
}
