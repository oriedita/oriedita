package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

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
        super.mouseReleased(p0);
        d.getLineStep().clear();
        d.unselect(selectionStart, p0);

        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));
            if (d.getFoldLineSet().closestLineSegmentDistance(p) < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.getFoldLineSet().closestLineSegmentSearch(p).setSelected(0);
            }
        }
    }
}
