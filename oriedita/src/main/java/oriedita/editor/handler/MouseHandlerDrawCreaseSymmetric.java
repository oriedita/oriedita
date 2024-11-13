package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_SYMMETRIC_12)
public class MouseHandlerDrawCreaseSymmetric extends BaseMouseHandlerInputRestricted {
    private final CreasePattern_Worker d;
    private final CanvasModel canvasModel;

    @Inject
    public MouseHandlerDrawCreaseSymmetric(@Named("mainCreasePattern_Worker") CreasePattern_Worker d, CanvasModel canvasModel) {
        this.d = d;
        this.canvasModel = canvasModel;
    }

    //マウス操作(mouseMode==12鏡映モード　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        if (d.getLineStep().isEmpty()) {    //第1段階として、点を選択
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.MAGENTA_5));
            }
        } else if (d.getLineStep().size() == 1) {    //第2段階として、点を選択
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.fromNumber(d.getLineStep().size() + 1)));
                d.getLineStep().set(0, d.getLineStep().get(0).withB(d.getLineStep().get(1).getB()));
            } else {
                d.getLineStep().clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
                return;
            }

            if (Epsilon.high.le0(d.getLineStep().get(0).determineLength())) {
                d.getLineStep().clear();
                canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            }
        }
    }

    //マウス操作(mouseMode==12鏡映モード　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==12鏡映モード　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 2) {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。
            int old_sousuu = d.getFoldLineSet().getTotal();

            for (var s : d.getFoldLineSet().getLineSegmentsCollection()) {
                if (s.getSelected() == 2) {
                    LineSegment adds = OritaCalc.findLineSymmetryLineSegment(s, d.getLineStep().get(0));
                    adds.setColor(s.getColor());

                    d.getFoldLineSet().addLine(adds);
                }
            }

            int new_sousuu = d.getFoldLineSet().getTotal();

            d.getFoldLineSet().divideLineSegmentWithNewLines(old_sousuu, new_sousuu);

            d.record();
            d.unselect_all(false);
            d.getLineStep().clear();
        }
    }
}
