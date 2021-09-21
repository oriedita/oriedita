package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseAngleRestricted3_2 extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseAngleRestricted2 mouseHandlerDrawCreaseAngleRestricted2;

    public MouseHandlerDrawCreaseAngleRestricted3_2(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseAngleRestricted2 = new MouseHandlerDrawCreaseAngleRestricted2(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseAngleRestricted2.mouseMoved(p0);
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

        if ((d.i_drawing_stage == 0) || (d.i_drawing_stage == 1)) {
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.fromNumber(d.i_drawing_stage));
                if (d.i_drawing_stage == 0) {
                    return;
                }
            }
        }

        if (d.i_drawing_stage == 2) {
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d)

            if (d.id_angle_system != 0) {
                d.d_angle_system = 180.0 / (double) d.id_angle_system;
            } else {
                d.d_angle_system = 180.0 / 4.0;
            }

            if (d.id_angle_system != 0) {
                LineSegment s_kiso = new LineSegment(d.line_step[2].getA(), d.line_step[1].getA());
                kakudo = 0.0;

                int i_jyun;
                i_jyun = 0;//i_jyunは線を描くとき順番に色を変えたいとき使う
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    d.i_drawing_stage = d.i_drawing_stage + 1;
                    kakudo = kakudo + d.d_angle_system;
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 100.0));
                    if (i_jyun == 0) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
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

                LineSegment s_kiso = new LineSegment(d.line_step[2].getA(), d.line_step[1].getA());

                for (int i = 1; i <= 6; i++) {
                    d.i_drawing_stage = d.i_drawing_stage + 1;
                    kakudo = jk[i];
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 100.0));
                    if (i == 1) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 2) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 3) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 4) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                    if (i == 5) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 6) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                }
            }

            return;
        }

        if (d.i_drawing_stage == 2 + (honsuu)) {
            d.i_drawing_stage = 0;
            d.closest_step_lineSegment.set(d.get_moyori_step_lineSegment(p, 3, 2 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, d.closest_step_lineSegment) >= d.selectionDistance) {
                return;
            }

            if (OritaCalc.distance_lineSegment(p, d.closest_step_lineSegment) < d.selectionDistance) {
                Point mokuhyou_point = new Point();
                mokuhyou_point.set(OritaCalc.findProjection(d.closest_step_lineSegment, p));

                d.closest_lineSegment.set(d.getClosestLineSegment(p));
                if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {//最寄折線が近い場合
                    if (OritaCalc.parallel_judgement(d.closest_step_lineSegment, d.closest_lineSegment, 0.000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//最寄折線が最寄step折線と平行の場合は除外
                        Point mokuhyou_point2 = new Point();
                        mokuhyou_point2.set(OritaCalc.findIntersection(d.closest_step_lineSegment, d.closest_lineSegment));
                        if (p.distance(mokuhyou_point) * 2.0 > p.distance(mokuhyou_point2)) {
                            mokuhyou_point.set(mokuhyou_point2);
                        }

                    }

                }

                LineSegment add_sen = new LineSegment();
                add_sen.set(mokuhyou_point, d.line_step[2].getA());
                add_sen.setColor(d.lineColor);
                d.addLineSegment(add_sen);
                d.record();
            }
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
