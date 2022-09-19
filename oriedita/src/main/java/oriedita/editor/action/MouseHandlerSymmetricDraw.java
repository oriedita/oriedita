package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.SYMMETRIC_DRAW_10)
public class MouseHandlerSymmetricDraw extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerSymmetricDraw() {
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

//--------------------------------------------
//29 29 29 29 29 29 29 29  mouseMode==29正多角形入力	入力 29 29 29 29 29 29 29 29
    //動作概要　
    //mouseMode==1と線分分割以外は同じ　
    //

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 3) {
            //２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
            Point t_taisyou = new Point();
            t_taisyou.set(OritaCalc.findLineSymmetryPoint(d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA(), d.getLineStep().get(0).getA()));

            LineSegment add_sen = new LineSegment(d.getLineStep().get(1).getA(), t_taisyou);

            add_sen.set(d.extendToIntersectionPoint(add_sen));
            add_sen.setColor(d.getLineColor());
            if (Epsilon.high.gt0(add_sen.determineLength())) {
                d.addLineSegment(add_sen);
                d.record();
            }

            d.getLineStep().clear();
        }
    }
}
