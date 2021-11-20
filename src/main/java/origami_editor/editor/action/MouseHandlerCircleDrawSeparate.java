package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerCircleDrawSeparate extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawSeparate() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_SEPARATE_44;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==44 円 分離入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);

        if (d.lineStep.size() == 0) {
            d.circleStep.clear();
            if (p.distance(closest_point) > d.selectionDistance) {
                return;
            }

            d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.CYAN_3));
        } else if (d.lineStep.size() == 1) {
            if (p.distance(closest_point) > d.selectionDistance) {
                return;
            }

            d.lineStepAdd(new LineSegment(p, closest_point, LineColor.CYAN_3));

            d.circleStep.clear();
            d.circleStep.add(new Circle(d.lineStep.get(0).getA(), 0.0, LineColor.CYAN_3));
        }
    }

    //マウス操作(mouseMode==44 円 分離入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if (d.lineStep.size() == 2) {
            d.lineStep.get(1).setA(p);
            d.circleStep.get(0).setR(d.lineStep.get(0).determineLength());
        }
    }

    //マウス操作(mouseMode==44 円 分離入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 2) {
            d.circleStep.clear();

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closest_point = d.getClosestPoint(p);
            d.lineStep.get(1).setA(closest_point);
            if (p.distance(closest_point) <= d.selectionDistance) {
                if (Epsilon.high.gt0(d.lineStep.get(1).determineLength())) {
                    d.addLineSegment(d.lineStep.get(1));
                    d.addCircle(d.lineStep.get(0).getA(), d.lineStep.get(1).determineLength(), LineColor.CYAN_3);
                    d.record();
                }
            }

            d.lineStep.clear();
        }
    }
}
