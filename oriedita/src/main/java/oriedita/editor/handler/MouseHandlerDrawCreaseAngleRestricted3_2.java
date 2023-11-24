package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18)
public class MouseHandlerDrawCreaseAngleRestricted3_2 extends BaseMouseHandlerInputRestricted {
    private final AngleSystemModel angleSystemModel;
    double d_angle_system;

    @Inject
    public MouseHandlerDrawCreaseAngleRestricted3_2(AngleSystemModel angleSystemModel) {
        this.angleSystemModel = angleSystemModel;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getLineStep().size() <= 1) {
            super.mouseMoved(p0);//近い既存点のみ表示
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {

        int honsuu;//Number of lines drawn around one endpoint
        if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
            honsuu = angleSystemModel.getCurrentAngleSystemDivider() * 2 - 1;
        } else {
            honsuu = 6;
        }


        double kakudo;
        Point p = d.getCamera().TV2object(p0);

        if ((d.getLineStep().size() == 0) || (d.getLineStep().size() == 1)) {
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.fromNumber(d.getLineStep().size() + 1)));
            }
        }

        if (d.getLineStep().size() == 2) {
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)

            if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
                d_angle_system = 180.0 / (double) angleSystemModel.getCurrentAngleSystemDivider();
            } else {
                d_angle_system = 180.0 / 4.0;
            }

            if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
                LineSegment s_kiso = new LineSegment(d.getLineStep().get(1).getA(), d.getLineStep().get(0).getA());
                kakudo = 0.0;

                boolean i_jyun = false;//i_jyunは線を描くとき順番に色を変えたいとき使う
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = !i_jyun;
                    kakudo = kakudo + d_angle_system;

                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, kakudo, 100.0);
                    if (i_jyun) {
                        s.setColor(LineColor.ORANGE_4);
                    } else {
                        s.setColor(LineColor.GREEN_6);
                    }
                    d.lineStepAdd(s);
                }
            }


            if (angleSystemModel.getCurrentAngleSystemDivider() == 0) {
                double[] jk = angleSystemModel.getAngles();

                LineSegment s_kiso = new LineSegment(d.getLineStep().get(1).getA(), d.getLineStep().get(0).getA());

                for (int i = 0; i < 6; i++) {
                    kakudo = jk[i];
                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, kakudo, 100.0);

                    if (i == 0) {
                        s.setColor(LineColor.ORANGE_4);
                    }
                    if (i == 1) {
                        s.setColor(LineColor.GREEN_6);
                    }
                    if (i == 2) {
                        s.setColor(LineColor.PURPLE_8);
                    }
                    if (i == 3) {
                        s.setColor(LineColor.ORANGE_4);
                    }
                    if (i == 4) {
                        s.setColor(LineColor.GREEN_6);
                    }
                    if (i == 5) {
                        s.setColor(LineColor.PURPLE_8);
                    }
                    d.lineStepAdd(s);
                }
            }

            return;
        }

        if (d.getLineStep().size() == 2 + (honsuu)) {
            LineSegment closest_step_lineSegment = new LineSegment(100000.0, 100000.0, 100000.0, 100000.0 + Epsilon.UNKNOWN_01); //マウス最寄のstep線分(線分追加のための準備をするための線分)。なお、ここで宣言する必要はないので、どこで宣言すべきか要検討20161113

            closest_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 3, 2 + (honsuu)));
            if (OritaCalc.determineLineSegmentDistance(p, closest_step_lineSegment) >= d.getSelectionDistance()) {
                d.getLineStep().clear();
                return;
            }

            if (OritaCalc.determineLineSegmentDistance(p, closest_step_lineSegment) < d.getSelectionDistance()) {
                Point mokuhyou_point = OritaCalc.findProjection(closest_step_lineSegment, p);

                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.getClosestLineSegment(p));
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {//最寄折線が近い場合
                    if (OritaCalc.isLineSegmentParallel(closest_step_lineSegment, closestLineSegment, Epsilon.UNKNOWN_1EN6) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//最寄折線が最寄step折線と平行の場合は除外
                        Point mokuhyou_point2 = OritaCalc.findIntersection(closest_step_lineSegment, closestLineSegment);
                        if (p.distance(mokuhyou_point) * 2.0 > p.distance(mokuhyou_point2)) {
                            mokuhyou_point = mokuhyou_point2;
                        }

                    }

                }

                LineSegment add_sen = new LineSegment();
                add_sen.set(mokuhyou_point, d.getLineStep().get(1).getA());
                add_sen.setColor(d.getLineColor());
                d.addLineSegment(add_sen);
                d.record();
            }

            d.getLineStep().clear();
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
