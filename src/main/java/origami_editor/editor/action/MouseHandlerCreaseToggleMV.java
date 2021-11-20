package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerCreaseToggleMV extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseToggleMV() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_TOGGLE_MV_58;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==58線_変換　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {//ここの処理の終わりに fix2();　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        d.lineStep.clear();

        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {//
            if (d.MV_change(selectionStart, p0) != 0) {
                d.fix2();
                d.record();
            }
        }

        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {//
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                LineSegment s = d.foldLineSet.closestLineSegmentSearch(p);
                LineColor ic_temp = s.getColor();
                if (ic_temp == LineColor.RED_1) {
                    s.setColor(LineColor.BLUE_2);
                } else if (ic_temp == LineColor.BLUE_2) {
                    s.setColor(LineColor.RED_1);
                }

                d.fix2();
                d.record();
            }

        }
    }
}
