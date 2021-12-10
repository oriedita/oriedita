package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerSelectLineIntersecting extends BaseMouseHandlerLineSelect {
    @Inject
    public MouseHandlerSelectLineIntersecting() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.SELECT_LINE_INTERSECTING_68;
    }

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (Epsilon.high.gt0(selectionLine.determineLength())) {
            //やりたい動作はここに書く
            d.foldLineSet.select_lX(selectionLine, "select_lX");//lXは小文字のエルと大文字のエックス
        }
        super.mouseReleased(p0);
    }
}
