package origami.data.quadTree.collector;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.adapter.QuadTreeAdapter;

public class EndPointCollector extends PointCollector {

    private final int min;

    public EndPointCollector(Point p, int min) {
        super(p);
        this.min = min;
    }

    @Override
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter) {
        return super.shouldCollect(cursor, adapter) && cursor > min;
    }
}
