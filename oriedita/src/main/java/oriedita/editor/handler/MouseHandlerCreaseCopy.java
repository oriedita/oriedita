package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_COPY_22)
public class MouseHandlerCreaseCopy extends BaseMouseHandlerLineTransform {

    @Inject
    public MouseHandlerCreaseCopy(@Named("mainCreasePattern_Worker") CreasePattern_Worker d, CanvasModel canvasModel, AngleSystemModel angleSystemModel) {
        super(canvasModel, angleSystemModel);
        this.d = d;
    }

    //マウスリリース----------------------------------------------------
    public void mouseReleased(Point p0) {
        super.mouseReleased(p0);

        if (Epsilon.high.gt0(delta.distance(new Point(0, 0)))) {
            //やりたい動作はここに書く

            FoldLineSet ori_s_temp = lines;
            ori_s_temp.move(delta.getX(), delta.getY());
            ori_s_temp.unselect_all();

            int sousuu_old = d.getFoldLineSet().getTotal();
            Save save1 = SaveProvider.createInstance();
            ori_s_temp.getSave(save1);
            d.getFoldLineSet().addSave(save1);
            int sousuu_new = d.getFoldLineSet().getTotal();
            d.getFoldLineSet().divideLineSegmentWithNewLines(sousuu_old, sousuu_new);

            d.unselect_all(false);
            d.record();
        }
        lines = null;
    }


}
