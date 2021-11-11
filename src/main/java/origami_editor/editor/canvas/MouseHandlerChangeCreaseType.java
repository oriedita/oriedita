package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerChangeCreaseType extends BaseMouseHandler {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CHANGE_CREASE_TYPE_4;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //4 4 4 4 4 444444444444444444444444444444444444444444444444444444444
    public void mousePressed(Point p0) {
    }//マウス操作(mouseMode==4線_変換　でボタンを押したとき)時の作業

    public void mouseDragged(Point p0) {
    }//マウス操作(mouseMode==4線_変換　でドラッグしたとき)を行う関数

    //マウス操作(mouseMode==4線_変換　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.foldLineSet.closestLineSegmentDistance(p) < d.selectionDistance) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            LineSegment minris = d.foldLineSet.closestLineSegmentSearch(p);
            LineColor ic_temp = minris.getColor();
            if (ic_temp.isFoldingLine()) {
                minris.setColor(ic_temp.advanceFolding());
                d.record();
            }
        }
    }
}
