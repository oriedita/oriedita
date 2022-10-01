package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_ADVANCE_TYPE_30)
public class MouseHandlerCreaseAdvanceType extends BaseMouseHandler {
    LineSegment lineSegment;

    @Inject
    public MouseHandlerCreaseAdvanceType() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {    //マウス操作(mouseMode==4線_変換　でボタンを押したとき)時の作業
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        lineSegment = null;
        if (d.getFoldLineSet().closestLineSegmentDistance(p) < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
            lineSegment = d.getFoldLineSet().closestLineSegmentSearch(p);
            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(lineSegment, 0.01));
            lineSegment.setB(s01.getB());
        }
    }

    public void mouseDragged(Point p0) {//マウス操作(mouseMode==4線_変換　でドラッグしたとき)を行う関数
        if (lineSegment != null) {
            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(lineSegment, 100.0));
            lineSegment.setB(s01.getB());
            lineSegment = null;
        }

    }

    //マウス操作(mouseMode==30 除け_線_変換　でボタンを離したとき)を行う関数（背景に展開図がある場合用）
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        if (lineSegment != null) {
            LineSegment s01 = new LineSegment();
            s01.set(OritaCalc.lineSegment_double(lineSegment, 100.0));
            lineSegment.setB(s01.getB());

            LineColor ic_temp = lineSegment.getColor();
            int is_temp = lineSegment.getSelected();

            if ((ic_temp == LineColor.BLACK_0) && (is_temp == 0)) {
                lineSegment.setSelected(2);
            } else if ((ic_temp == LineColor.BLACK_0) && (is_temp == 2)) {
                lineSegment.setColor(LineColor.RED_1);
                lineSegment.setSelected(0);
            } else if ((ic_temp == LineColor.RED_1) && (is_temp == 0)) {
                lineSegment.setColor(LineColor.BLUE_2);
            } else if ((ic_temp == LineColor.BLUE_2) && (is_temp == 0)) {
                lineSegment.setColor(LineColor.BLACK_0);
            }

            d.record();
        }
    }
}
