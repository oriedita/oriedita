package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;
import oriedita.editor.databinding.CanvasModel;

@Singleton
public class MouseHandlerCreaseCopy extends BaseMouseHandlerLineSelect {
    private final CanvasModel canvasModel;

    @Inject
    public MouseHandlerCreaseCopy(CreasePattern_Worker d, CanvasModel canvasModel) {
        this.d = d;
        this.canvasModel = canvasModel;
    }
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_COPY_22;
    }

    //マウスリリース----------------------------------------------------
    public void mouseReleased(Point p0) {
        canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStep.get(0).setA(closestPoint);
        }
        if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
            //やりたい動作はここに書く

            double addx = -d.lineStep.get(0).determineBX() + d.lineStep.get(0).determineAX();
            double addy = -d.lineStep.get(0).determineBY() + d.lineStep.get(0).determineAY();

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            Save save = new SaveV1();
            d.foldLineSet.getMemoSelectOption(save, 2);
            ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s_temp.move(addx, addy);//全体を移動する

            int sousuu_old = d.foldLineSet.getTotal();
            Save save1 = new SaveV1();
            ori_s_temp.getSave(save1);
            d.foldLineSet.addSave(save1);
            int sousuu_new = d.foldLineSet.getTotal();
            d.foldLineSet.divideLineSegmentIntersections(1, sousuu_old, sousuu_old + 1, sousuu_new);

            d.foldLineSet.unselect_all();
            d.record();

            canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        }
        d.lineStep.clear();
    }
}
