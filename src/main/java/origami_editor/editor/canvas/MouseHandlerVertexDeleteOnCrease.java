package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.Epsilon;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerVertexDeleteOnCrease extends BaseMouseHandler {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.VERTEX_DELETE_ON_CREASE_41;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        //点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の線分が出ているか（頂点とr以内に端点がある線分の数）	public int tyouten_syuui_sennsuu(Ten p) {

        d.foldLineSet.del_V_cc(p, d.selectionDistance, Epsilon.UNKNOWN_1EN6);

        d.record();
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
