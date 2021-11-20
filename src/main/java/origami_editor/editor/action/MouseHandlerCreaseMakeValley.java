package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerCreaseMakeValley extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseMakeValley() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MAKE_VALLEY_24;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==24 でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {//ここの処理の終わりに fix2();　をするのは、元から折線だったものと、補助線から変換した折線との組合せで頻発するT字型不接続を修正するため
        d.lineStep.clear();

        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {
            if (d.insideToValley(selectionStart, p0)) {
                d.fix2();
                d.record();
            }
        }
        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                d.foldLineSet.closestLineSegmentSearch(p).setColor(LineColor.BLUE_2);
                d.fix2();
                d.record();
            }
        }
    }
}
