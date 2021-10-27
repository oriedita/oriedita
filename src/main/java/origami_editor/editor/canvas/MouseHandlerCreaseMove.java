package origami_editor.editor.canvas;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;

public class MouseHandlerCreaseMove extends BaseMouseHandlerLineSelect {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MOVE_21;
    }

    //マウスリリース----------------------------------------------------
    public void mouseReleased(Point p0) {

        d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStep.get(0).setA(closestPoint);
        }
        if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
            //やりたい動作はここに書く

            double addx, addy;
            addx = -d.lineStep.get(0).determineBX() + d.lineStep.get(0).determineAX();
            addy = -d.lineStep.get(0).determineBY() + d.lineStep.get(0).determineAY();

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setSave(d.foldLineSet.getMemoSelectOption(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            d.foldLineSet.delSelectedLineSegmentFast();//セレクトされた折線を削除する。
            ori_s_temp.move(addx, addy);//全体を移動する

            int total_old = d.foldLineSet.getTotal();
            d.foldLineSet.addSave(ori_s_temp.getSave());
            int total_new = d.foldLineSet.getTotal();
            d.foldLineSet.divideLineSegmentIntersections(1, total_old, total_old + 1, total_new);

            d.foldLineSet.unselect_all();
            d.record();

            d.app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        }

        d.lineStep.clear();
    }
}
