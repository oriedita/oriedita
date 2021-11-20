package origami_editor.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerUnused_6 extends BaseMouseHandler {
    @Inject
    public MouseHandlerUnused_6() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNUSED_6;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.selectionDistance) {
            d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.fromNumber(d.lineStep.size() + 1)));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 3) {
            d.lineStep.clear();
        }
    }
}
