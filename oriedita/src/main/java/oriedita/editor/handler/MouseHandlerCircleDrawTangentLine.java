package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_TANGENT_LINE_45)
public class MouseHandlerCircleDrawTangentLine extends BaseMouseHandler {
    Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse

    @Inject
    public MouseHandlerCircleDrawTangentLine() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        closest_circumference.set(d.getClosestCircleMidpoint(p));

        if (d.getCircleStep().size() == 0 || d.getCircleStep().size() == 1) {
            d.getLineStep().clear();
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            Circle stepCircle = new Circle();
            stepCircle.set(closest_circumference);
            stepCircle.setColor(LineColor.GREEN_6);

            d.getCircleStep().add(stepCircle);
        } else if (d.getLineStep().size() > 1) {//			i_egaki_dankai=0;i_circle_drawing_stage=1;
            LineSegment closest_step_lineSegment = new LineSegment();
            closest_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 1, d.getLineStep().size()));

            if (OritaCalc.determineLineSegmentDistance(p, closest_step_lineSegment) > d.getSelectionDistance()) {
                return;
            }

            d.getLineStep().clear();

            d.lineStepAdd(closest_step_lineSegment);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 2)) {
            Circle firstCircle = d.getCircleStep().get(0);
            Circle secondCircle = d.getCircleStep().get(1);

            Point c1 = firstCircle.determineCenter();
            Point c2 = secondCircle.determineCenter();

            double x1 = firstCircle.getX();
            double y1 = firstCircle.getY();
            double r1 = firstCircle.getR();
            double x2 = secondCircle.getX();
            double y2 = secondCircle.getY();
            double r2 = secondCircle.getR();
            //0,0,r,        xp,yp,R
            double xp = x2 - x1;
            double yp = y2 - y1;

            if (c1.distance(c2) < Epsilon.UNKNOWN_1EN6) {
                d.getCircleStep().clear();
                return;
            }//接線0本の場合

            if ((xp * xp + yp * yp) < (r1 - r2) * (r1 - r2)) {
                d.getCircleStep().clear();
                return;
            }//接線0本の場合

            if (Math.abs((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2)) < Epsilon.UNKNOWN_1EN7) {//外接線1本の場合
                Point kouten = OritaCalc.internalDivisionRatio(c1, c2, -r1, r2);
                StraightLine ty = new StraightLine(c1, kouten);
                ty.orthogonalize(kouten);

                d.lineStepAdd(OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(new Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0), ty));
            } else if (((r1 - r2) * (r1 - r2) < (xp * xp + yp * yp)) && ((xp * xp + yp * yp) < (r1 + r2) * (r1 + r2))) {//外接線2本の場合
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線

                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.orthogonalize(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.orthogonalize(new Point(xr2, yr2));

                d.lineStepAdd(new LineSegment(new Point(xr1, yr1), OritaCalc.findProjection(t1, new Point(x2, y2)), LineColor.PURPLE_8));
                d.lineStepAdd(new LineSegment(new Point(xr2, yr2), OritaCalc.findProjection(t2, new Point(x2, y2)), LineColor.PURPLE_8));
            } else if (Math.abs((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2)) < Epsilon.UNKNOWN_1EN7) {//外接線2本と内接線1本の場合
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線

                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.orthogonalize(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.orthogonalize(new Point(xr2, yr2));

                d.lineStepAdd(new LineSegment(new Point(xr1, yr1), OritaCalc.findProjection(t1, new Point(x2, y2)), LineColor.PURPLE_8));
                d.lineStepAdd(new LineSegment(new Point(xr2, yr2), OritaCalc.findProjection(t2, new Point(x2, y2)), LineColor.PURPLE_8));

                Point kouten = OritaCalc.internalDivisionRatio(c1, c2, r1, r2);
                StraightLine ty = new StraightLine(c1, kouten);
                ty.orthogonalize(kouten);
                LineSegment s = OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(new Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0), ty);
                s.setColor(LineColor.PURPLE_8);
                d.lineStepAdd(s);
            } else if ((r1 + r2) * (r1 + r2) < (xp * xp + yp * yp)) {//外接線2本と内接線2本の場合
                //             ---------------------------------------------------------------
                //                                     -------------------------------------
                //                 -------               -------------   -------   -------       -------------
                double xq1 = r1 * (xp * (r1 - r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq1 = r1 * (yp * (r1 - r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq2 = r1 * (xp * (r1 - r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double yq2 = r1 * (yp * (r1 - r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 - r2) * (r1 - r2))) / (xp * xp + yp * yp);//共通外接線
                double xq3 = r1 * (xp * (r1 + r2) + yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double yq3 = r1 * (yp * (r1 + r2) - xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double xq4 = r1 * (xp * (r1 + r2) - yp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線
                double yq4 = r1 * (yp * (r1 + r2) + xp * Math.sqrt((xp * xp + yp * yp) - (r1 + r2) * (r1 + r2))) / (xp * xp + yp * yp);//共通内接線


                double xr1 = xq1 + x1;
                double yr1 = yq1 + y1;
                double xr2 = xq2 + x1;
                double yr2 = yq2 + y1;
                double xr3 = xq3 + x1;
                double yr3 = yq3 + y1;
                double xr4 = xq4 + x1;
                double yr4 = yq4 + y1;

                StraightLine t1 = new StraightLine(x1, y1, xr1, yr1);
                t1.orthogonalize(new Point(xr1, yr1));
                StraightLine t2 = new StraightLine(x1, y1, xr2, yr2);
                t2.orthogonalize(new Point(xr2, yr2));
                StraightLine t3 = new StraightLine(x1, y1, xr3, yr3);
                t3.orthogonalize(new Point(xr3, yr3));
                StraightLine t4 = new StraightLine(x1, y1, xr4, yr4);
                t4.orthogonalize(new Point(xr4, yr4));

                d.lineStepAdd(new LineSegment(new Point(xr1, yr1), OritaCalc.findProjection(t1, new Point(x2, y2)), LineColor.PURPLE_8));
                d.lineStepAdd(new LineSegment(new Point(xr2, yr2), OritaCalc.findProjection(t2, new Point(x2, y2)), LineColor.PURPLE_8));
                d.lineStepAdd(new LineSegment(new Point(xr3, yr3), OritaCalc.findProjection(t3, new Point(x2, y2)), LineColor.PURPLE_8));
                d.lineStepAdd(new LineSegment(new Point(xr4, yr4), OritaCalc.findProjection(t4, new Point(x2, y2)), LineColor.PURPLE_8));
            }
        }

        if (d.getLineStep().size() == 1) {
            d.getCircleStep().clear();

            LineSegment s = d.getLineStep().get(0);
            s.setColor(d.getLineColor());
            d.addLineSegment(s);

            d.getLineStep().clear();
            d.record();
        }
    }
}
