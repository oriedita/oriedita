package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami_editor.editor.databinding.CanvasModel;

public class MouseHandlerDrawCreaseSymmetric extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerDrawCreaseSymmetric(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_SYMMETRIC_12;
    }

    //12 12 12 12 12    mouseMode==12　;鏡映モード
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==12鏡映モード　でボタンを押したとき)時の作業----------------------------------------------------
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
                d.line_step[1].setB(d.line_step[2].getB());
            }
            if (d.line_step[1].getLength() < 0.00000001) {
                d.i_drawing_stage = 0;
                d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }
    }

    //マウス操作(mouseMode==12鏡映モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==12鏡映モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        LineSegment adds = new LineSegment();
        if (d.i_drawing_stage == 2) {
            d.i_drawing_stage = 0;
            d.app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            int old_sousuu = d.foldLineSet.getTotal();

            for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
                if (d.foldLineSet.get_select(i) == 2) {
                    adds.set(OritaCalc.findLineSymmetryLineSegment(d.foldLineSet.get(i), d.line_step[1]));
                    adds.setColor(d.foldLineSet.getColor(i));

                    d.foldLineSet.addLine(adds.getA(), adds.getB());
                    d.foldLineSet.setColor(d.foldLineSet.getTotal(), d.foldLineSet.getColor(i));
                }
            }

            int new_sousuu = d.foldLineSet.getTotal();

            d.foldLineSet.intersect_divide(1, old_sousuu, old_sousuu + 1, new_sousuu);

            d.foldLineSet.unselect_all();
            d.record();
            d.app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        }
    }
}
