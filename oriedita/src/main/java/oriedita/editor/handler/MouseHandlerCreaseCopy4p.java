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
@Handles(MouseMode.CREASE_COPY_4P_32)
public class MouseHandlerCreaseCopy4p extends BaseMouseHandlerInputRestricted {
    private final CanvasModel canvasModel;

    @Inject
    public MouseHandlerCreaseCopy4p(@Named("mainCreasePattern_Worker") CreasePattern_Worker d, CanvasModel canvasModel) {
        this.d = d;
        this.canvasModel = canvasModel;
    }

    //マウス操作(mouseMode==32copy2p2p2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        if (d.getLineStep().size() == 0) {    //第1段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
            }
            return;
        }

        if (d.getLineStep().size() == 1) {    //第2段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) >= d.getSelectionDistance()) {
                d.getLineStep().clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.BLUE_2));
            }
            if (Epsilon.high.le0(OritaCalc.distance(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA()))) {
                d.getLineStep().clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


        if (d.getLineStep().size() == 2) {    //第3段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) >= d.getSelectionDistance()) {
                d.getLineStep().clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.CYAN_3));
            }
            return;
        }

        if (d.getLineStep().size() == 3) {    //第4段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) >= d.getSelectionDistance()) {
                d.getLineStep().clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.ORANGE_4));
            }
            if (Epsilon.high.le0(OritaCalc.distance(d.getLineStep().get(2).getA(), d.getLineStep().get(3).getA()))) {
                d.getLineStep().clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }
    }

    //マウス操作(mouseMode==32copy2p2p　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

//  ********************************************

    //マウス操作(mouseMode==32copy2p2pp　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 4) {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            Save save = SaveProvider.createInstance();
            d.getFoldLineSet().getMemoSelectOption(save, 2);
            ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s_temp.move(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA(), d.getLineStep().get(3).getA());//全体を移動する

            ori_s_temp.unselect_all();
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
