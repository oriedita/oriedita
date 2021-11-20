package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.canvas.MouseMode;

@Singleton
public class MouseHandlerUnused_10001 extends BaseMouseHandler {
    @Inject
    public MouseHandlerUnused_10001() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNUSED_10001;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //Work when operating the mouse (when the button is pressed with mouseMode == 10001)
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.selectionDistance) {
            d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.fromNumber(d.lineStep.size() + 1)));
        }
    }

    //マウス操作(mouseMode==10001　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==10001　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 3) {
            d.lineStep.clear();
        }
    }
}
