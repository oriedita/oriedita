package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCreaseDeleteIntersecting extends BaseMouseHandlerLineSelect {
    @Inject
    public MouseHandlerCreaseDeleteIntersecting(AngleSystemModel angleSystemModel) {
        super(angleSystemModel);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_DELETE_INTERSECTING_65;
    }

//----------------------------------------------------------------------------------------
//多角形を入力(既存頂点への引き寄せあるが既存頂点が遠い場合は引き寄せ無し)し、何らかの作業を行うセット
    //マウス操作(マウスを動かしたとき)を行う関数

    //マウス操作(mouseMode==65　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (Epsilon.high.gt0(selectionLine.determineLength())) {
            //やりたい動作はここに書く
            d.getFoldLineSet().deleteInsideLine(selectionLine, "lX");//lXは小文字のエルと大文字のエックス
            d.record();
        }
        super.mouseReleased(p0);
    }
}
