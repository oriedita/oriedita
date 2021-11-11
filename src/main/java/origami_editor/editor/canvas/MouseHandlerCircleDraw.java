package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerCircleDraw extends BaseMouseHandler {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_42;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==42 円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);

        d.circleStep.clear();
        d.lineStep.clear();
        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStepAdd(new LineSegment(p, closestPoint, LineColor.CYAN_3));
            d.circleStep.add(new Circle(closestPoint, 0.0, LineColor.CYAN_3));
        }
    }

    //マウス操作(mouseMode==42 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        if (d.lineStep.size() > 0) {
            d.lineStep.get(0).setA(p);
        }
        if (d.circleStep.size() > 0) {
            d.circleStep.get(0).setR(OritaCalc.distance(d.lineStep.get(0).getA(), d.lineStep.get(0).getB()));
        }
    }

    //マウス操作(mouseMode==42 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 1) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);
            d.lineStep.get(0).setA(closestPoint);
            if (p.distance(closestPoint) <= d.selectionDistance) {
                if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
                    d.addCircle(d.lineStep.get(0).determineBX(), d.lineStep.get(0).determineBY(), d.lineStep.get(0).determineLength(), LineColor.CYAN_3);
                    d.record();
                }
            }

            d.lineStep.clear();
            d.circleStep.clear();
        }
    }
}
