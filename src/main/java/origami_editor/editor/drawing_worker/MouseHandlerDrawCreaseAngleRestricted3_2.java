package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseAngleRestricted3_2 extends BaseMouseHandlerInputRestricted {
    double d_angle_system;

    public MouseHandlerDrawCreaseAngleRestricted3_2(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.lineStep.size() <= 1) {
            super.mouseMoved(p0);//近い既存点のみ表示
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {

        int honsuu;//Number of lines drawn around one endpoint
        if (d.id_angle_system != 0) {
            honsuu = d.id_angle_system * 2 - 1;
        } else {
            honsuu = 6;
        }


        double kakudo;
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if ((d.lineStep.size() == 0) || (d.lineStep.size() == 1)) {
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.fromNumber(d.lineStep.size() + 1)));
            }
        }

        if (d.lineStep.size() == 2) {
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)

            if (d.id_angle_system != 0) {
                d_angle_system = 180.0 / (double) d.id_angle_system;
            } else {
                d_angle_system = 180.0 / 4.0;
            }

            if (d.id_angle_system != 0) {
                LineSegment s_kiso = new LineSegment(d.lineStep.get(1).getA(), d.lineStep.get(0).getA());
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


            if (d.id_angle_system == 0) {
                double[] jk = new double[7];
                jk[0] = 0.0;
                jk[1] = d.d_restricted_angle_2;
                jk[2] = d.d_restricted_angle_1;
                jk[3] = d.d_restricted_angle_3;
                jk[4] = 360.0 - d.d_restricted_angle_2;
                jk[5] = 360.0 - d.d_restricted_angle_1;
                jk[6] = 360.0 - d.d_restricted_angle_3;

                LineSegment s_kiso = new LineSegment(d.lineStep.get(1).getA(), d.lineStep.get(0).getA());

                for (int i = 1; i <= 6; i++) {
                    kakudo = jk[i];
                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, kakudo, 100.0);

                    if (i == 1) {
                        s.setColor(LineColor.ORANGE_4);
                    }
                    if (i == 2) {
                        s.setColor(LineColor.GREEN_6);
                    }
                    if (i == 3) {
                        s.setColor(LineColor.PURPLE_8);
                    }
                    if (i == 4) {
                        s.setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        s.setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        s.setColor(LineColor.PURPLE_8);
                    }
                    d.lineStepAdd(s);
                }
            }

            return;
        }

        if (d.lineStep.size() == 2 + (honsuu)) {
            LineSegment closest_step_lineSegment = new LineSegment(100000.0, 100000.0, 100000.0, 100000.1); //マウス最寄のstep線分(線分追加のための準備をするための線分)。なお、ここで宣言する必要はないので、どこで宣言すべきか要検討20161113

            closest_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 3, 2 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, closest_step_lineSegment) >= d.selectionDistance) {
                d.lineStep.clear();
                return;
            }

            if (OritaCalc.distance_lineSegment(p, closest_step_lineSegment) < d.selectionDistance) {
                Point mokuhyou_point = new Point();
                mokuhyou_point.set(OritaCalc.findProjection(closest_step_lineSegment, p));

                LineSegment closestLineSegment = new LineSegment();
                closestLineSegment.set(d.getClosestLineSegment(p));
                if (OritaCalc.distance_lineSegment(p, closestLineSegment) < d.selectionDistance) {//最寄折線が近い場合
                    if (OritaCalc.parallel_judgement(closest_step_lineSegment, closestLineSegment, 0.000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//最寄折線が最寄step折線と平行の場合は除外
                        Point mokuhyou_point2 = new Point();
                        mokuhyou_point2.set(OritaCalc.findIntersection(closest_step_lineSegment, closestLineSegment));
                        if (p.distance(mokuhyou_point) * 2.0 > p.distance(mokuhyou_point2)) {
                            mokuhyou_point.set(mokuhyou_point2);
                        }

                    }

                }

                LineSegment add_sen = new LineSegment();
                add_sen.set(mokuhyou_point, d.lineStep.get(1).getA());
                add_sen.setColor(d.lineColor);
                d.addLineSegment(add_sen);
                d.record();
            }

            d.lineStep.clear();
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
