package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_MAKE_EDGE_25)
public class MouseHandlerCreaseMakeEdge extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseMakeEdge() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    /**
     * マウス操作(mouseMode==25 でボタンを離したとき)を行う関数
     * <p>
     * The reason for doing {@link CreasePattern_Worker#fix2()} at the end of this
     * process is to correct the T-shaped disconnection that frequently occurs in
     * the combination of the original polygonal line and the polygonal line
     * converted from the auxiliary line.
     */
    public void mouseReleased(Point p0) {
        super.mouseReleased(p0);
        d.getLineStep().clear();

        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {
            if (d.insideToEdge(selectionStart, p0)) {
                d.fix2();
                d.record();
            }
        } else {
            Point p = new Point();
            p.set(d.getCamera().TV2object(p0));
            if (d.getFoldLineSet().closestLineSegmentDistance(p) < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.getFoldLineSet().closestLineSegmentSearch(p).setColor(LineColor.BLACK_0);
                d.fix2();
                d.record();
            }
        }
    }
}
