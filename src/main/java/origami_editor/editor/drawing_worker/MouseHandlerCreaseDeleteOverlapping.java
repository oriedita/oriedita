package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseDeleteOverlapping extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerCreaseDeleteOverlapping(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_DELETE_OVERLAPPING_64;
    }

    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==64　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.i_drawing_stage = 1;

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) > d.selectionDistance) {
            d.i_drawing_stage = 0;
        }
        d.line_step[1].set(p, d.closest_point);
        d.line_step[1].setColor(LineColor.MAGENTA_5);
    }

    //マウス操作(mouseMode==64　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        mouseHandlerDrawCreaseRestricted.mouseDragged(p0);
    }


//65 65 65 65 65 65 65 65 65 65 65 65 65入力した線分に重複している折線やX交差している折線を削除する

    //マウス操作(mouseMode==64　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            d.closest_point.set(d.getClosestPoint(p));
            d.line_step[1].setA(d.closest_point);
            if (p.distance(d.closest_point) <= d.selectionDistance) {
                if (d.line_step[1].getLength() > 0.00000001) {

                    d.foldLineSet.deleteInsideLine(d.line_step[1], "l");//lは小文字のエル

                    d.record();

                }
            }
        }

    }
}
