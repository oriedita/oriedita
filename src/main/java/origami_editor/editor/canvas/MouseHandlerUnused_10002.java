package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Component
public class MouseHandlerUnused_10002 extends BaseMouseHandler {
    @Override
    public MouseMode getMouseMode() {
        return MouseMode.UNUSED_10002;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==10002　でボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        LineSegment closestLineSegment = new LineSegment();
        closestLineSegment.set(d.getClosestLineSegment(p));
        if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
            closestLineSegment.setColor(LineColor.GREEN_6);
            d.lineStepAdd(closestLineSegment);
        }
    }

    //マウス操作(mouseMode==10002　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==10002　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 3) {
            d.lineStep.clear();
        }
    }
}
