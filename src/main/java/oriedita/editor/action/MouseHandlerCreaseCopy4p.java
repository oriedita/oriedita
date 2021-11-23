package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;
import oriedita.editor.databinding.CanvasModel;

@Singleton
public class MouseHandlerCreaseCopy4p extends BaseMouseHandlerInputRestricted {
    private final CanvasModel canvasModel;

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_COPY_4P_32;
    }

    @Inject
    public MouseHandlerCreaseCopy4p(CreasePattern_Worker d, CanvasModel canvasModel) {
        this.d = d;
        this.canvasModel = canvasModel;
    }

    //マウス操作(mouseMode==32copy2p2p2p2p　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.lineStep.size() == 0) {    //第1段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.MAGENTA_5));
            }
            return;
        }

        if (d.lineStep.size() == 1) {    //第2段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) >= d.selectionDistance) {
                d.lineStep.clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.BLUE_2));
            }
            if (Epsilon.high.le0(OritaCalc.distance(d.lineStep.get(0).getA(), d.lineStep.get(1).getA()))) {
                d.lineStep.clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
            return;
        }


        if (d.lineStep.size() == 2) {    //第3段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);

            closestPoint.set(d.getClosestPoint(p));
            if (p.distance(closestPoint) >= d.selectionDistance) {
                d.lineStep.clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.CYAN_3));
            }
            return;
        }

        if (d.lineStep.size() == 3) {    //第4段階として、点を選択
            Point closestPoint = d.getClosestPoint(p);
            closestPoint.set(d.getClosestPoint(p));
            if (p.distance(closestPoint) >= d.selectionDistance) {
                d.lineStep.clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }
            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.ORANGE_4));
            }
            if (Epsilon.high.le0(OritaCalc.distance(d.lineStep.get(2).getA(), d.lineStep.get(3).getA()))) {
                d.lineStep.clear();
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
        if (d.lineStep.size() == 4) {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

            FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
            Save save = new SaveV1();
            d.foldLineSet.getMemoSelectOption(save, 2);
            ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
            ori_s_temp.move(d.lineStep.get(0).getA(), d.lineStep.get(1).getA(), d.lineStep.get(2).getA(), d.lineStep.get(3).getA());//全体を移動する

            int sousuu_old = d.foldLineSet.getTotal();
            Save save1 = new SaveV1();
            ori_s_temp.getSave(save1);
            d.foldLineSet.addSave(save1);
            int sousuu_new = d.foldLineSet.getTotal();
            d.foldLineSet.divideLineSegmentIntersections(1, sousuu_old, sousuu_old + 1, sousuu_new);

            d.record();
            canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);

            d.lineStep.clear();
        }
    }
}
