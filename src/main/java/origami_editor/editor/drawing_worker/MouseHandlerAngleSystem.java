package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerAngleSystem extends BaseMouseHandler{
    double d_angle_system;
    private final MouseHandlerDrawCreaseAngleRestricted2 mouseHandlerDrawCreaseAngleRestricted2;

    public MouseHandlerAngleSystem(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseAngleRestricted2 = new MouseHandlerDrawCreaseAngleRestricted2(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.ANGLE_SYSTEM_16;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerDrawCreaseAngleRestricted2.mouseMoved(p0);
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {

        int honsuu;//1つの端点周りに描く線の本数
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
                d.i_drawing_stage++;
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
                d_angle_system = 180.0 / (double) d.id_angle_system;
            } else {
                d_angle_system = 180.0 / 4.0;
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
                    kakudo = kakudo + d_angle_system;
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 1.0));
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
                    d.i_drawing_stage++;
                    kakudo = jk[i];
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 1.0));
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
            d.closest_lineSegment.set(d.get_moyori_step_lineSegment(p, 3, 2 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_lineSegment);//line_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                d.line_step[d.i_drawing_stage].setColor(LineColor.BLUE_2);
                return;
            }
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) >= d.selectionDistance) {
                d.i_drawing_stage = 0;
                return;
            }
        }


        if (d.i_drawing_stage == 2 + (honsuu) + 1) {

            d.closest_lineSegment.set(d.getClosestLineSegment(p));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) >= d.selectionDistance) {//最寄折線が遠かった場合
                d.i_drawing_stage = 0;
                return;
            }

            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_lineSegment);//line_step[i_egaki_dankai].setcolor(i_egaki_dankai);
                d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                //return;
            }
        }

        if (d.i_drawing_stage == 2 + (honsuu) + 1 + 1) {
            d.i_drawing_stage = 0;

            //line_step[12]とs_step[13]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
//			Ten kousa_ten =new Ten(); kousa_ten.set(oc.kouten_motome(line_step[12],line_step[13]));
            Point kousa_point = new Point();
            kousa_point.set(OritaCalc.findIntersection(d.line_step[2 + (honsuu) + 1], d.line_step[2 + (honsuu) + 1 + 1]));
            LineSegment add_sen = new LineSegment(kousa_point, d.line_step[2].getA(), d.lineColor);
            if (add_sen.getLength() > 0.00000001) {
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
