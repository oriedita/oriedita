package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseAngleRestricted2 extends BaseMouseHandler{
    private final MouseHandlerDrawCreaseRestricted mouseHandlerDrawCreaseRestricted;

    public MouseHandlerDrawCreaseAngleRestricted2(DrawingWorker d) {
        super(d);
        mouseHandlerDrawCreaseRestricted = new MouseHandlerDrawCreaseRestricted(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.i_drawing_stage <= 1) {
            mouseHandlerDrawCreaseRestricted.mouseMoved(p0);//近い既存点のみ表示
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {

        int honsuu = 0;//1つの端点周りに描く線の本数
        if (d.id_angle_system != 0) {
            honsuu = d.id_angle_system * 2 - 1;
        } else {
            honsuu = 6;
        }

        int i_jyunnbi_step_suu = 2;//動作の準備として人間が選択するステップ数

        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        if (d.i_drawing_stage == 0) {    //第1段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.MAGENTA_5);
            }
            return;
        }

        if (d.i_drawing_stage == 1) {    //第2段階として、点を選択
            d.closest_point.set(d.getClosestPoint(p));
            if (p.distance(d.closest_point) >= d.selectionDistance) {
                d.i_drawing_stage = 0;
                return;
            }
            if (p.distance(d.closest_point) < d.selectionDistance) {

                d.i_drawing_stage = d.i_drawing_stage + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_point, d.closest_point);
                d.line_step[d.i_drawing_stage].setColor(LineColor.fromNumber(d.i_drawing_stage));
                d.line_step[1].setB(d.line_step[2].getB());


            }

        }

        if (d.i_drawing_stage == i_jyunnbi_step_suu) {    //if(i_egaki_dankai==1){        //動作の準備として人間が選択するステップ数が終わった状態で実行
            int i_jyun;//i_jyunは線を描くとき順番に色を変えたいとき使う
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d) //    double d_angle_system;double angle;

            if (d.id_angle_system != 0) {
                d.d_angle_system = 180.0 / (double) d.id_angle_system;
            } else {
                d.d_angle_system = 180.0 / 4.0;
            }

            if (d.id_angle_system != 0) {

                LineSegment s_kiso = new LineSegment(d.line_step[1].getA(), d.line_step[1].getB());
                d.angle = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    d.i_drawing_stage = d.i_drawing_stage + 1;
                    d.angle = d.angle + d.d_angle_system;
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, d.angle, 10.0));
                    if (i_jyun == 0) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i_jyun == 1) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.ORANGE_4);
                    }
                }

                s_kiso.set(d.line_step[1].getB(), d.line_step[1].getA());
                d.angle = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    d.i_drawing_stage = d.i_drawing_stage + 1;
                    d.angle = d.angle + d.d_angle_system;
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, d.angle, 10.0));
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

                LineSegment s_kiso = new LineSegment(d.line_step[1].getA(), d.line_step[1].getB());
                d.angle = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    d.i_drawing_stage = d.i_drawing_stage + 1;
                    d.angle = jk[i];
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, d.angle, 10.0));
                    if (i == 1) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 2) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 3) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.ORANGE_4);
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

                s_kiso.set(d.line_step[1].getB(), d.line_step[1].getA());
                d.angle = 0.0;
                i_jyun = 0;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = i_jyun + 1;
                    if (i_jyun == 2) {
                        i_jyun = 0;
                    }
                    d.i_drawing_stage = d.i_drawing_stage + 1;
                    d.angle = jk[i];
                    d.line_step[d.i_drawing_stage].set(OritaCalc.lineSegment_rotate(s_kiso, d.angle, 10.0));
                    if (i == 1) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.GREEN_6);
                    }
                    if (i == 2) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.PURPLE_8);
                    }
                    if (i == 3) {
                        d.line_step[d.i_drawing_stage].setColor(LineColor.ORANGE_4);
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


        if (d.i_drawing_stage == i_jyunnbi_step_suu + (honsuu) + (honsuu)) {//19     //動作の準備としてソフトが返答するステップ数が終わった状態で実行

            int i_tikai_s_step_suu = 0;

            //line_step[2から10]までとs_step[11から19]まで
            d.closest_lineSegment.set(d.get_moyori_step_lineSegment(p, 3, 2 + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_lineSegment);    //line_step[i_egaki_dankai].setcolor(2);//line_step[20]にinput
            }

            //line_step[2から10]までとs_step[11から19]まで
            d.closest_lineSegment.set(d.get_moyori_step_lineSegment(p, 2 + (honsuu) + 1, 2 + (honsuu) + (honsuu)));
            if (OritaCalc.distance_lineSegment(p, d.closest_lineSegment) < d.selectionDistance) {
                d.i_drawing_stage = d.i_drawing_stage + 1;
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                d.line_step[d.i_drawing_stage].set(d.closest_lineSegment);    //line_step[i_egaki_dankai].setcolor(lineColor);
            }

            if (i_tikai_s_step_suu == 2) { //この段階でs_stepが[21]までうまってたら、line_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //=     1+ (honsuu) +(honsuu) +  2 ){i_egaki_dankai=0; //この段階でs_stepが[21]までうまってたら、line_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //例外処理としてs_step[20]とs_step[21]が平行の場合、より近いほうをs_stepが[20]とし、s_stepを[20]としてリターン（この場合はまだ処理は終われない）。
                //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
                //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する

                if (OritaCalc.parallel_judgement(d.line_step[d.i_drawing_stage - 1], d.line_step[d.i_drawing_stage], 0.1) != OritaCalc.ParallelJudgement.NOT_PARALLEL) {//ここは安全を見て閾値を0.1と大目にとっておこのがよさそう
                    d.i_drawing_stage = 0;
                    return;
                }

                d.i_drawing_stage = 0;

                //line_step[20]とs_step[21]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                Point kousa_point = new Point();
                kousa_point.set(OritaCalc.findIntersection(d.line_step[2 + (honsuu) + (honsuu) + 1], d.line_step[2 + (honsuu) + (honsuu) + 1 + 1]));

                LineSegment add_sen = new LineSegment(kousa_point, d.line_step[2 + (honsuu) + (honsuu) + 1].getA());
                add_sen.setColor(d.lineColor);
                if (add_sen.getLength() > 0.00000001) {
                    d.addLineSegment(add_sen);
                }

                LineSegment add_sen2 = new LineSegment(kousa_point, d.line_step[2 + (honsuu) + (honsuu) + 2].getA());
                add_sen2.setColor(d.lineColor);
                if (add_sen.getLength() > 0.00000001) {
                    d.addLineSegment(add_sen2);
                }
                d.record();
            }

            d.i_drawing_stage = 0;
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
