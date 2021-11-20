package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.element.Point;
import origami_editor.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerCreaseUnselect extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseUnselect() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_UNSELECT_20;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==20 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        d.lineStep.clear();
        d.unselect(selectionStart, p0);

        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.foldLineSet.closestLineSegmentSearch(p).setSelected(0);
            }
        }
    }
}
