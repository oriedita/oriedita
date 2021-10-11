package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerSelectLineIntersecting extends BaseMouseHandlerLineSelect {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SELECT_LINE_INTERSECTING_68;
    }

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.lineStep.get(0).setA(p);

        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStep.get(0).setA(closestPoint);
        }
        if (d.lineStep.get(0).determineLength() > 0.00000001) {
            //やりたい動作はここに書く
            d.foldLineSet.select_lX(d.lineStep.get(0), "select_lX");//lXは小文字のエルと大文字のエックス
        }
    }
}
