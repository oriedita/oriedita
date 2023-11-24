package oriedita.editor.handler;

import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.util.List;

public abstract class BaseMouseHandlerPolygon extends BaseMouseHandler {
    boolean polygonCompleted = false;

    @Override
    public void mouseMoved(Point p0) {
//マウス操作(マウスを動かしたとき)を行う関数
        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();
            Point p = d.getCamera().TV2object(p0);

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) > p.distance(d.getLineStep().get(0).getA())) {
                closest_point = d.getLineStep().get(0).getA();
            }

            if (p.distance(closest_point) < d.getSelectionDistance()) {
                candidate.set(closest_point, closest_point);
            } else {
                candidate.set(p, p);
            }

            candidate.setColor(LineColor.MAGENTA_5);
            d.getLineCandidate().add(candidate);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        if (polygonCompleted) {
            polygonCompleted = false;
            d.getLineStep().clear();
        }

        LineSegment s = new LineSegment();
        s.setColor(LineColor.MAGENTA_5);
        Point p = d.getCamera().TV2object(p0);

        if (d.getLineStep().isEmpty()) {
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) > d.getSelectionDistance()) {
                closest_point = p;
            }
            s.set(closest_point, p);

        } else {//ここでi_egaki_dankai=0となることはない。
            s.set(d.getLineStep().get(d.getLineStep().size() - 1).getB(), p);
        }

        d.lineStepAdd(s);
    }

    @Override
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        d.getLineStep().get(d.getLineStep().size() - 1).setB(p);


        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) > p.distance(d.getLineStep().get(0).getA())) {
                closest_point = d.getLineStep().get(0).getA();
            }


            if (p.distance(closest_point) < d.getSelectionDistance()) {
                candidate.set(closest_point, closest_point);
            } else {
                candidate.set(p, p);
            }
            candidate.setColor(LineColor.MAGENTA_5);

            d.getLineCandidate().add(candidate);

            d.getLineStep().get(d.getLineStep().size() - 1).setB(candidate.getA());
        }
    }

    @Override
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.getSelectionDistance()) {
            closest_point = p;
        }

        d.getLineStep().get(d.getLineStep().size() - 1).setB(closest_point);

        if (d.getLineStep().size() >= 2) {
            if (p.distance(d.getLineStep().get(0).getA()) <= d.getSelectionDistance()) {
                d.getLineStep().get(d.getLineStep().size() - 1).setB(d.getLineStep().get(0).getA());
                //i_O_F_C=1;
                polygonCompleted = true;
            }
        }

        if (polygonCompleted) {
            List<LineSegment> lineStep = d.getLineStep();
            Polygon polygon = new Polygon(lineStep.size());
            int index = 1;
            for (LineSegment lineSegment : lineStep) {
                polygon.set(index++, lineSegment.getA());
            }

            //各動作モードで独自に行う作業は以下に条件分けして記述する
            if (getMouseMode() == MouseMode.SELECT_POLYGON_66) {
                d.getFoldLineSet().select_Takakukei(polygon, "select");
            }//66 66 66 66 66 多角形を入力し、それに全体が含まれる折線をselectする
            if (getMouseMode() == MouseMode.UNSELECT_POLYGON_67) {
                d.getFoldLineSet().select_Takakukei(polygon, "unselectAction");
            }//67 67 67 67 67 多角形を入力し、それに全体が含まれる折線を折線をunselectする
            //各動作モードで独自に行う作業はここまで
        }
    }
}
