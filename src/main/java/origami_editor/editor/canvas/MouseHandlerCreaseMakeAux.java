package origami_editor.editor.canvas;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerCreaseMakeAux extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseMakeAux() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_AUX_60;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==60 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        d.lineStep.clear();

        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {
            if (d.insideToAux(selectionStart, p0)) {
                d.record();
            }//この関数は不完全なのでまだ未公開20171126
        } else {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double closestLineSegmentDistance(Ten p)
                LineSegment closestLineSegment = d.foldLineSet.closestLineSegmentSearchReversedOrder(p);
                if (closestLineSegment.getColor().getNumber() < 3) {
                    LineSegment add_sen = new LineSegment();
                    add_sen.set(closestLineSegment);
                    add_sen.setColor(LineColor.CYAN_3);

                    d.foldLineSet.deleteLineSegment_vertex(closestLineSegment);
                    d.addLineSegment(add_sen);

                    d.organizeCircles();
                    d.record();
                }
            }
        }
    }
}
