package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import origami.Epsilon;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.SELECT_LINE_INTERSECTING_68)
public class MouseHandlerSelectLineIntersecting extends BaseMouseHandlerLineSelect {
    @Inject
    public MouseHandlerSelectLineIntersecting(AngleSystemModel angleSystemModel) {
        super(angleSystemModel);
    }

    //マウス操作でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (Epsilon.high.gt0(selectionLine.determineLength())) {
            //やりたい動作はここに書く
            d.getFoldLineSet().select_lX(selectionLine, "select_lX");//lXは小文字のエルと大文字のエックス
        }
        super.mouseReleased(p0);
    }
}
