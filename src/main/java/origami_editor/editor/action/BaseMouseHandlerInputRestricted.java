package origami_editor.editor.action;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

public abstract class BaseMouseHandlerInputRestricted extends BaseMouseHandler{
    @Override
    public void mouseMoved(Point p0) {
        //マウスで選択できる候補点を表示する。近くに既成の点があるときはその点が候補点となる。近くに既成の点が無いときは候補点無しなので候補点の表示も無し。
        if (d.gridInputAssist) {
            d.lineCandidate.clear();
            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.selectionDistance) {
                candidate.set(closestPoint, closestPoint);
                candidate.setColor(d.lineColor);

                d.lineCandidate.add(candidate);
            }
        }
    }
}
