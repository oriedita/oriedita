package oriedita.editor.action;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1_0;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCreaseMove extends BaseMouseHandlerLineTransform {

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MOVE_21;
    }

    @Inject
    public MouseHandlerCreaseMove(CreasePattern_Worker d, CanvasModel canvasModel) {
        super(canvasModel);
        this.d = d;
    }

    //マウスリリース----------------------------------------------------
    public void mouseReleased(Point p0) {
        super.mouseReleased(p0);
        if (Epsilon.high.gt0(delta.distance(new Point(0,0)))) {
            //やりたい動作はここに書く

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            Save save = new SaveV1_0();
            d.foldLineSet.getMemoSelectOption(save, 2);
            ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
            d.foldLineSet.delSelectedLineSegmentFast();//セレクトされた折線を削除する。
            ori_s_temp.move(delta.getX(), delta.getY());//全体を移動する
            int total_old = d.foldLineSet.getTotal();
            Save save1 = new SaveV1_0();
            ori_s_temp.getSave(save1);
            d.foldLineSet.addSave(save1);
            int total_new = d.foldLineSet.getTotal();
            d.foldLineSet.divideLineSegmentWithNewLines(total_old, total_new);

            d.foldLineSet.unselect_all();
            d.record();

            canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        }
        lines = null;
    }
}
