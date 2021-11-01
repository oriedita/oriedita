package origami_editor.editor.canvas;

import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.Save;
import origami_editor.editor.databinding.CanvasModel;

public class MouseHandlerCreaseMove4p extends BaseMouseHandlerInputRestricted {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_MOVE_4P_31;
    }

    //マウス操作(mouseMode==31move2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Point closestPoint = d.getClosestPoint(p);

        switch (d.lineStep.size()) {
            case 0: {    //第1段階として、点を選択
                if (p.distance(closestPoint) < d.selectionDistance) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
                }
                break;
            }
            case 1: {    //第2段階として、点を選択
                if (p.distance(closestPoint) < d.selectionDistance) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.BLUE_2));

                    if (Epsilon.high.le0(OritaCalc.distance(d.lineStep.get(0).getA(), d.lineStep.get(1).getA()))) {
                        d.lineStep.clear();
                        d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                    }
                } else {
                    d.lineStep.clear();
                    d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                    //点の選択が失敗した場合もi_select_mode=0にしないと、セレクトのつもりが動作モードがmove2p2pになったままになる
                }
                break;
            }
            case 2: {    //第3段階として、点を選択
                if (p.distance(closestPoint) < d.selectionDistance) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.CYAN_3));
                } else {
                    d.lineStep.clear();
                    d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                }
                break;
            }
            case 3: {    //第4段階として、点を選択
                if (p.distance(closestPoint) < d.selectionDistance) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.ORANGE_4));

                    if (Epsilon.high.le0(OritaCalc.distance(d.lineStep.get(2).getA(), d.lineStep.get(3).getA()))) {
                        d.lineStep.clear();
                        d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                    }
                } else {
                    d.lineStep.clear();
                    d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                }

                break;
            }
        }
    }

    //マウス操作(mouseMode==31move2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

//  ********************************************


//--------------------------------------------
//32 32 32 32 32 32 32 32  mouseMode==32copy2p2p	入力 32 32 32 32 32 32 32 32

//動作概要　
//mouseMode==1と線分分割以外は同じ　
//

    //マウス操作(mouseMode==31move2p2p　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 4) {
            d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            Save save = new Save();
            d.foldLineSet.getMemoSelectOption(save, 2);
            ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
            d.foldLineSet.delSelectedLineSegmentFast();//セレクトされた折線を削除する。
            ori_s_temp.move(d.lineStep.get(0).getA(), d.lineStep.get(1).getA(), d.lineStep.get(2).getA(), d.lineStep.get(3).getA());//全体を移動する

            int sousuu_old = d.foldLineSet.getTotal();
            Save save1 = new Save();
            ori_s_temp.getSave(save1);
            d.foldLineSet.addSave(save1);
            int sousuu_new = d.foldLineSet.getTotal();
            d.foldLineSet.divideLineSegmentIntersections(1, sousuu_old, sousuu_old + 1, sousuu_new);

            d.foldLineSet.unselect_all();
            d.record();

            d.app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);

            d.lineStep.clear();
        }
    }
}
