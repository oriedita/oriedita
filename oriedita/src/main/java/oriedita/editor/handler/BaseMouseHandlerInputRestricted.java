package oriedita.editor.handler;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

public abstract class BaseMouseHandlerInputRestricted extends BaseMouseHandler {
    protected Point candidatePoint;

    @Override
    public void mouseMoved(Point p0) {
        //マウスで選択できる候補点を表示する。近くに既成の点があるときはその点が候補点となる。近くに既成の点が無いときは候補点無しなので候補点の表示も無し。
        if (d.getGridInputAssist()) {
            candidatePoint = null;
            d.getLineCandidate().clear();
            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point p = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                candidate.set(closestPoint, closestPoint);
                candidate.setColor(d.getLineColor());
                this.candidatePoint = closestPoint;
                d.getLineCandidate().add(candidate);
            }
        }
    }
}
