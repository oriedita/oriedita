package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_MOVE_4P_31)
public class MouseHandlerCreaseMove4p extends BaseMouseHandlerInputRestricted {
    private final CanvasModel canvasModel;

    @Inject
    public MouseHandlerCreaseMove4p(@Named("mainCreasePattern_Worker") CreasePattern_Worker d, CanvasModel canvasModel) {
        this.d = d;
        this.canvasModel = canvasModel;
    }

    //マウス操作(mouseMode==31move2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        Point closestPoint = d.getClosestPoint(p);

        switch (d.getLineStep().size()) {
            case 0: {    //第1段階として、点を選択
                if (p.distance(closestPoint) < d.getSelectionDistance()) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
                }
                break;
            }
            case 1: {    //第2段階として、点を選択
                if (p.distance(closestPoint) < d.getSelectionDistance()) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.BLUE_2));

                    if (Epsilon.high.le0(OritaCalc.distance(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA()))) {
                        d.getLineStep().clear();
                        canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                    }
                } else {
                    d.getLineStep().clear();
                    canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                    //点の選択が失敗した場合もi_select_mode=0にしないと、セレクトのつもりが動作モードがmove2p2pになったままになる
                }
                break;
            }
            case 2: {    //第3段階として、点を選択
                if (p.distance(closestPoint) < d.getSelectionDistance()) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.CYAN_3));
                } else {
                    d.getLineStep().clear();
                    canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                }
                break;
            }
            case 3: {    //第4段階として、点を選択
                if (p.distance(closestPoint) < d.getSelectionDistance()) {
                    d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.ORANGE_4));

                    if (Epsilon.high.le0(OritaCalc.distance(d.getLineStep().get(2).getA(), d.getLineStep().get(3).getA()))) {
                        d.getLineStep().clear();
                        canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                    }
                } else {
                    d.getLineStep().clear();
                    canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
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
        if (d.getLineStep().size() == 4) {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            Save save = SaveProvider.createInstance();
            d.getFoldLineSet().getMemoSelectOption(save, 2);
            ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
            d.getFoldLineSet().delSelectedLineSegmentFast();//セレクトされた折線を削除する。
            ori_s_temp.move(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA(), d.getLineStep().get(3).getA());//全体を移動する

            int sousuu_old = d.getFoldLineSet().getTotal();
            Save save1 = SaveProvider.createInstance();
            ori_s_temp.getSave(save1);
            d.getFoldLineSet().addSave(save1);
            int sousuu_new = d.getFoldLineSet().getTotal();
            d.getFoldLineSet().divideLineSegmentWithNewLines(sousuu_old, sousuu_new);

            d.unselect_all(false);
            d.record();
            d.getLineStep().clear();
        }
    }
}
