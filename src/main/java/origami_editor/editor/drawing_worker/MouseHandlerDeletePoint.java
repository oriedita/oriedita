package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDeletePoint extends BaseMouseHandler {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DELETE_POINT_15;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）	public int tyouten_syuui_sennsuu(Ten p) {

        d.foldLineSet.del_V(p, d.selectionDistance, 0.000001);
        d.record();
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
