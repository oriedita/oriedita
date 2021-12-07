package oriedita.editor.action;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCreaseMakeEdge extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseMakeEdge() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_EDGE_25;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    /**
     * マウス操作(mouseMode==25 でボタンを離したとき)を行う関数
     * 
     * The reason for doing {@link CreasePattern_Worker#fix2()} at the end of this
     * process is to correct the T-shaped disconnection that frequently occurs in
     * the combination of the original polygonal line and the polygonal line
     * converted from the auxiliary line.
     */
    public void mouseReleased(Point p0) {
        super.mouseReleased(p0);
        d.lineStep.clear();

        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {
            if (d.insideToEdge(selectionStart, p0)) {
                d.fix2();
                d.record();
            }
        } else {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.foldLineSet.closestLineSegmentSearch(p).setColor(LineColor.BLACK_0);
                d.fix2();
                d.record();
            }
        }
    }
}
