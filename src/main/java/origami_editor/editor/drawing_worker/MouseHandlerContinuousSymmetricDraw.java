package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerContinuousSymmetricDraw extends BaseMouseHandler{
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;

    public MouseHandlerContinuousSymmetricDraw(DrawingWorker d) {
        super(d);
        mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        System.out.println("i_egaki_dankai=" + d.i_drawing_stage);

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));

        d.i_drawing_stage = d.i_drawing_stage + 1;
        if (p.distance(d.closest_point) < d.selectionDistance) {
            d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
            d.line_step[d.i_drawing_stage].setColor(d.lineColor);
        } else {
            d.line_step[d.i_drawing_stage].set(p, p);
            d.line_step[d.i_drawing_stage].setColor(d.lineColor);
        }

        System.out.println("i_egaki_dankai=" + d.i_drawing_stage);
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 2) {
            d.i_drawing_stage = 0;

            LineSegment add_lineSegment = new LineSegment();
            d.continuous_folding_new(d.line_step[1].getA(), d.line_step[2].getA());
            for (int i = 1; i <= d.i_drawing_stage; i++) {
                if (d.line_step[i].getLength() > 0.00000001) {
                    add_lineSegment.set(d.line_step[i].getA(), d.line_step[i].getB());//要注意　s_stepは表示上の都合でアクティヴが0以外に設定されているのでadd_senbunにうつしかえてる20170507
                    add_lineSegment.setColor(d.lineColor);
                    d.addLineSegment(add_lineSegment);
                }
            }
            d.record();

            d.i_drawing_stage = 0;
        }
    }
}
