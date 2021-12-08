package oriedita.editor.action;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCreaseCopy extends MouseHandlerLineTransform {

    @Inject
    public MouseHandlerCreaseCopy(CreasePattern_Worker d, CanvasModel canvasModel) {
        super(canvasModel);
        this.d = d;
    }
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_COPY_22;
    }

    //マウスリリース----------------------------------------------------
    public void mouseReleased(Point p0) {
        super.mouseReleased(p0);

        if (Epsilon.high.gt0(new Point(addx, addy).distance(new Point(0,0)))) {
            //やりたい動作はここに書く

            FoldLineSet ori_s_temp = lines;
            ori_s_temp.move(addx, addy);

            int sousuu_old = d.foldLineSet.getTotal();
            Save save1 = new SaveV1();
            ori_s_temp.getSave(save1);
            d.foldLineSet.addSave(save1);
            int sousuu_new = d.foldLineSet.getTotal();
            d.foldLineSet.divideLineSegmentWithNewLines(sousuu_old, sousuu_new);

            d.foldLineSet.unselect_all();
            d.record();

            canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        }
        lines = null;
    }


}
