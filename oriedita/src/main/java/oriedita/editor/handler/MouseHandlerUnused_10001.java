package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.UNUSED_10001)
public class MouseHandlerUnused_10001 extends BaseMouseHandler {
    @Inject
    public MouseHandlerUnused_10001() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //Work when operating the mouse (when the button is pressed with mouseMode == 10001)
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.fromNumber(d.getLineStep().size() + 1)));
        }
    }

    //マウス操作(mouseMode==10001　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==10001　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 3) {
            d.getLineStep().clear();
        }
    }
}
