package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CHANGE_CREASE_TYPE_4)
public class MouseHandlerChangeCreaseType extends BaseMouseHandler {
    @Inject
    public MouseHandlerChangeCreaseType() {
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
        Point p = d.getCamera().TV2object(p0);

        if (d.getFoldLineSet().closestLineSegmentDistance(p) < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            LineSegment minris = d.getFoldLineSet().closestLineSegmentSearch(p);
            LineColor ic_temp = minris.getColor();
            if (ic_temp.isFoldingLine()) {
                d.getFoldLineSet().setColor(minris, ic_temp.advanceFolding());
                d.record();
            }
        }
    }
}
