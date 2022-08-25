package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerUnselectLineIntersecting extends BaseMouseHandlerLineSelect {
    @Inject
    public MouseHandlerUnselectLineIntersecting(AngleSystemModel angleSystemModel) {
        super(angleSystemModel);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNSELECT_LINE_INTERSECTING_69;
    }

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (Epsilon.high.gt0(selectionLine.determineLength())) {
            //やりたい動作はここに書く
            d.getFoldLineSet().select_lX(selectionLine, "unselect_lX");//lXは小文字のエルと大文字のエックス
        }
        super.mouseReleased(p0);
    }
}
