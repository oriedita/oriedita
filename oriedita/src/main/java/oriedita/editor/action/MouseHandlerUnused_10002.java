package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.UNUSED_10002)
public class MouseHandlerUnused_10002 extends BaseMouseHandler {
    @Inject
    public MouseHandlerUnused_10002() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==10002　でボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        LineSegment closestLineSegment = new LineSegment();
        closestLineSegment.set(d.getClosestLineSegment(p));
        if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
            closestLineSegment.setColor(LineColor.GREEN_6);
            d.lineStepAdd(closestLineSegment);
        }
    }

    //マウス操作(mouseMode==10002　でドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(mouseMode==10002　でボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 3) {
            d.getLineStep().clear();
        }
    }
}
