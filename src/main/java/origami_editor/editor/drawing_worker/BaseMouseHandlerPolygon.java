package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami_editor.editor.MouseMode;

import java.util.List;

public abstract class BaseMouseHandlerPolygon extends BaseMouseHandler {
    boolean polygonCompleted = false;

    public BaseMouseHandlerPolygon(DrawingWorker d) {
        super(d);
    }

    @Override
    public void mouseMoved(Point p0) {
//マウス操作(マウスを動かしたとき)を行う関数
        if (d.gridInputAssist) {
            d.lineCandidate.clear();
            Point p = new Point();
            p.set(d.camera.TV2object(p0));

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) > p.distance(d.lineStep.get(0).getA())) {
                closest_point.set(d.lineStep.get(0).getA());
            }

            if (p.distance(closest_point) < d.selectionDistance) {
                candidate.set(closest_point, closest_point);
            } else {
                candidate.set(p, p);
            }

            candidate.setColor(LineColor.MAGENTA_5);
            d.lineCandidate.add(candidate);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        if (polygonCompleted) {
            polygonCompleted = false;
            d.lineStep.clear();
        }

        LineSegment s = new LineSegment();
        s.setColor(LineColor.MAGENTA_5);
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.lineStep.size() == 0) {
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) > d.selectionDistance) {
                closest_point.set(p);
            }
            s.set(closest_point, p);

        } else {//ここでi_egaki_dankai=0となることはない。
            s.set(d.lineStep.get(d.lineStep.size() - 1).getB(), p);
        }

        d.lineStepAdd(s);
    }

    @Override
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.lineStep.get(d.lineStep.size() - 1).setB(p);


        if (d.gridInputAssist) {
            d.lineCandidate.clear();

            LineSegment candidate = new LineSegment();
            candidate.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) > p.distance(d.lineStep.get(0).getA())) {
                closest_point.set(d.lineStep.get(0).getA());
            }


            if (p.distance(closest_point) < d.selectionDistance) {
                candidate.set(closest_point, closest_point);
            } else {
                candidate.set(p, p);
            }
            candidate.setColor(LineColor.MAGENTA_5);

            d.lineCandidate.add(candidate);

            d.lineStep.get(d.lineStep.size() - 1).setB(candidate.getA());
        }
    }

    @Override
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.selectionDistance) {
            closest_point.set(p);
        }

        d.lineStep.get(d.lineStep.size() - 1).setB(closest_point);

        if (d.lineStep.size() >= 2) {
            if (p.distance(d.lineStep.get(0).getA()) <= d.selectionDistance) {
                d.lineStep.get(d.lineStep.size() - 1).setB(d.lineStep.get(0).getA());
                //i_O_F_C=1;
                polygonCompleted = true;
            }
        }

        if (polygonCompleted) {
            List<LineSegment> lineStep = d.lineStep;
            Polygon polygon = new Polygon(lineStep.size());
            int index = 1;
            for (LineSegment lineSegment : lineStep) {
                polygon.set(index++, lineSegment.getA());
            }

            //各動作モードで独自に行う作業は以下に条件分けして記述する
            if (getMouseMode() == MouseMode.SELECT_POLYGON_66) {
                d.foldLineSet.select_Takakukei(polygon, "select");
            }//66 66 66 66 66 多角形を入力し、それに全体が含まれる折線をselectする
            if (getMouseMode() == MouseMode.UNSELECT_POLYGON_67) {
                d.foldLineSet.select_Takakukei(polygon, "unselect");
            }//67 67 67 67 67 多角形を入力し、それに全体が含まれる折線を折線をunselectする
            //各動作モードで独自に行う作業はここまで
        }
    }
}
