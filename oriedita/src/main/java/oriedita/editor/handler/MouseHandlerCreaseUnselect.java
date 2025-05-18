package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_UNSELECT_20)
public class MouseHandlerCreaseUnselect extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseUnselect() {
    }

    //マウス操作(mouseMode==20 select　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        super.mouseReleased(p0);
        d.getLineStep().clear();

        int beforeSelectNum = d.getFoldLineTotalForSelectFolding();

        d.unselect(selectionStart, p0);
        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = d.getCamera().TV2object(p0);
            if (d.getFoldLineSet().closestLineSegmentDistance(p) < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.getFoldLineSet().closestLineSegmentSearch(p).setSelected(0);
            }
        }

        int afterSelectNum = d.getFoldLineTotalForSelectFolding();

        if(beforeSelectNum != afterSelectNum) d.record();
    }
}
