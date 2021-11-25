package origami.data.quadTree.adapter;

import java.util.List;

import origami.crease_pattern.element.LineSegment;

public class DivideAdapter extends LineSegmentListAdapter {

    private final int max;

    public DivideAdapter(List<LineSegment> lineSegments, int max) {
        super(lineSegments);
        this.max = max;
    }

    @Override
    public int getCount() {
        return max;
    }
}
