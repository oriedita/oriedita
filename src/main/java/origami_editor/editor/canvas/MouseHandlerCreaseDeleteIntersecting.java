package origami_editor.editor.canvas;

import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseDeleteIntersecting extends BaseMouseHandlerLineSelect {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_DELETE_INTERSECTING_65;
    }

//----------------------------------------------------------------------------------------
//多角形を入力(既存頂点への引き寄せあるが既存頂点が遠い場合は引き寄せ無し)し、何らかの作業を行うセット
    //マウス操作(マウスを動かしたとき)を行う関数

    //マウス操作(mouseMode==65　でボタンを離したとき)を行う関数----------------------------------------------------
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
            d.foldLineSet.deleteInsideLine(d.lineStep.get(0), "lX");//lXは小文字のエルと大文字のエックス
            d.record();
        }

        d.lineStep.clear();
    }
}
