package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerCircleDrawFree extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawFree() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_FREE_47;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==47 円入力(フリー　)　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.circleStep.clear();
        d.lineStep.clear();

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) > d.selectionDistance) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.CYAN_3));

            Circle stepCircle = new Circle();
            stepCircle.set(p, 0.0, LineColor.CYAN_3);
            d.circleStep.add(stepCircle);
        } else {
            d.lineStepAdd(new LineSegment(p, closestPoint, LineColor.CYAN_3));

            Circle stepCircle = new Circle();
            stepCircle.set(closestPoint, 0.0, LineColor.CYAN_3);
            d.circleStep.add(stepCircle);
        }
    }

    //マウス操作(mouseMode==47 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);
        d.circleStep.get(0).setR(OritaCalc.distance(d.lineStep.get(0).getA(), d.lineStep.get(0).getB()));
    }

    //マウス操作(mouseMode==47 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 1) {

            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) <= d.selectionDistance) {
                d.lineStep.get(0).setA(closestPoint);
            } else {
                d.lineStep.get(0).setA(p);
            }

            if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
                d.addCircle(d.lineStep.get(0).determineBX(), d.lineStep.get(0).determineBY(), d.lineStep.get(0).determineLength(), LineColor.CYAN_3);
                d.record();
            }

            d.circleStep.clear();
            d.lineStep.clear();
        }
    }
}
