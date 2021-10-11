package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerContinuousSymmetricDraw extends BaseMouseHandlerInputRestricted {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        System.out.println("i_egaki_dankai=" + d.lineStep.size());

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);

        if (p.distance(closest_point) < d.selectionDistance) {
            d.lineStepAdd(new LineSegment(closest_point, closest_point, d.lineColor));
        } else {
            d.lineStepAdd(new LineSegment(p, p, d.lineColor));
        }

        System.out.println("i_egaki_dankai=" + d.lineStep.size());
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 2) {
            d.continuous_folding_new(d.lineStep.get(0).getA(), d.lineStep.get(1).getA());

            d.record();

            d.lineStep.clear();
        }
    }
}
