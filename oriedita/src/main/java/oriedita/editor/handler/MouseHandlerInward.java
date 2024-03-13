package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.INWARD_8)
public class MouseHandlerInward extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerInward() {
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 3) {
            //三角形の内心を求める	public Ten oc.center(Ten ta,Ten tb,Ten tc)
            Point center = OritaCalc.center(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA(), d.getLineStep().get(2).getA());

            LineSegment add_sen1 = new LineSegment(d.getLineStep().get(0).getA(), center, d.getLineColor());
            if (Epsilon.high.gt0(add_sen1.determineLength())) {
                d.addLineSegment(add_sen1);
            }
            LineSegment add_sen2 = new LineSegment(d.getLineStep().get(1).getA(), center, d.getLineColor());
            if (Epsilon.high.gt0(add_sen2.determineLength())) {
                d.addLineSegment(add_sen2);
            }
            LineSegment add_sen3 = new LineSegment(d.getLineStep().get(2).getA(), center, d.getLineColor());
            if (Epsilon.high.gt0(add_sen3.determineLength())) {
                d.addLineSegment(add_sen3);
            }
            d.record();

            d.getLineStep().clear();
        }
    }
}
