package origami_editor.editor.drawing_worker;

import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;

public class MouseHandlerCreaseCopy4p extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerCreaseCopy4p(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_COPY_4P_32;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==32copy2p2p2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.i_drawing_stage == 0) {    //第1段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.MAGENTA_5);
            }
            return;
        }

        if (d.i_drawing_stage == 1) {    //第2段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) >= d.selectionDistance) {
                d.i_drawing_stage = 0;
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(d.closest_point) < d.selectionDistance) {

                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.fromNumber(d.i_drawing_stage));

            }
            if (OritaCalc.distance(d.line_step[1].getA(), d.line_step[2].getA()) < 0.00000001) {
                d.i_drawing_stage = 0;
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


        if (d.i_drawing_stage == 2) {    //第3段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) >= d.selectionDistance) {
                d.i_drawing_stage = 0;
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(d.closest_point) < d.selectionDistance) {

                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.fromNumber(d.i_drawing_stage));

            }
            return;
        }

        if (d.i_drawing_stage == 3) {    //第4段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) >= d.selectionDistance) {
                d.i_drawing_stage = 0;
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(d.closest_point) < d.selectionDistance) {

                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.fromNumber(d.i_drawing_stage));

            }
            if (OritaCalc.distance(d.line_step[3].getA(), d.line_step[4].getA()) < 0.00000001) {
                d.i_drawing_stage = 0;
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }
    }

    //マウス操作(mouseMode==32copy2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

//  ********************************************

    //マウス操作(mouseMode==32copy2p2pp　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 4) {
            d.i_drawing_stage = 0;
            d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            ori_s_temp.setSave(d.foldLineSet.getMemoSelectOption(2));//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s_temp.move(d.line_step[1].getA(), d.line_step[2].getA(), d.line_step[3].getA(), d.line_step[4].getA());//全体を移動する

            int sousuu_old = d.foldLineSet.getTotal();
            d.foldLineSet.addSave(ori_s_temp.getSave());
            int sousuu_new = d.foldLineSet.getTotal();
            d.foldLineSet.intersect_divide(1, sousuu_old, sousuu_old + 1, sousuu_new);

            d.record();
            d.app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        }
    }
}
