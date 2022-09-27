package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerDrawCreaseAngleRestricted2 extends BaseMouseHandlerInputRestricted {
    private final AngleSystemModel angleSystemModel;

    @Inject
    public MouseHandlerDrawCreaseAngleRestricted2(AngleSystemModel angleSystemModel) {
        this.angleSystemModel = angleSystemModel;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getLineStep().size() <= 1) {
            super.mouseMoved(p0);//近い既存点のみ表示
        }
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {

        int honsuu;//1つの端点周りに描く線の本数
        if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
            honsuu = angleSystemModel.getCurrentAngleSystemDivider() * 2 - 1;
        } else {
            honsuu = 6;
        }

        int i_jyunnbi_step_suu = 2;//動作の準備として人間が選択するステップ数

        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        if (d.getLineStep().size() == 0) {    //第1段階として、点を選択
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.MAGENTA_5));
            }
        } else if (d.getLineStep().size() == 1) {    //第2段階として、点を選択
            Point closest_point = d.getClosestPoint(p);
            if (p.distance(closest_point) >= d.getSelectionDistance()) {
                d.getLineStep().clear();
            } else if (p.distance(closest_point) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closest_point, closest_point, LineColor.fromNumber(d.getLineStep().size() + 1)));
                d.getLineStep().get(0).setB(d.getLineStep().get(1).getB());
            }
        } else if (d.getLineStep().size() == i_jyunnbi_step_suu) {    //if(i_egaki_dankai==1){        //動作の準備として人間が選択するステップ数が終わった状態で実行
            boolean i_jyun;//i_jyunは線を描くとき順番に色を変えたいとき使う
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d) //    double d_angle_system;double angle;

            double d_angle_system;
            if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
                d_angle_system = 180.0 / (double) angleSystemModel.getCurrentAngleSystemDivider();
            } else {
                d_angle_system = 180.0 / 4.0;
            }

            if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
                LineSegment s_kiso = new LineSegment(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB());
                double angle = 0.0;
                i_jyun = false;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = !i_jyun;

                    LineSegment s = new LineSegment();
                    angle = angle + d_angle_system;
                    s.set(OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0));
                    s.setColor(i_jyun ? LineColor.ORANGE_4 : LineColor.GREEN_6);
                    d.lineStepAdd(s);
                }

                s_kiso.set(d.getLineStep().get(0).getB(), d.getLineStep().get(0).getA());
                angle = 0.0;
                i_jyun = false;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = !i_jyun;

                    LineSegment s = new LineSegment();
                    angle = angle + d_angle_system;
                    s.set(OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0));
                    s.setColor(i_jyun ? LineColor.ORANGE_4 : LineColor.GREEN_6);
                    d.lineStepAdd(s);
                }
            }
            if (angleSystemModel.getCurrentAngleSystemDivider() == 0) {
                double[] jk = angleSystemModel.getAngles();

                LineSegment s_kiso = new LineSegment(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB());
                double angle = 0.0;
                for (int i = 0; i < 6; i++) {
                    angle = jk[i];
                    LineSegment s = new LineSegment();
                    s.set(OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0));
                    if (i == 0) {
                        s.setColor(LineColor.GREEN_6);
                    }
                    if (i == 1) {
                        s.setColor(LineColor.PURPLE_8);
                    }
                    if (i == 2) {
                        s.setColor(LineColor.ORANGE_4);
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

                s_kiso.set(d.getLineStep().get(0).getB(), d.getLineStep().get(0).getA());
                for (int i = 0; i < 6; i++) {
                    angle = jk[i];
                    LineSegment s = new LineSegment();

                    s.set(OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0));

                    if (i == 0) {
                        s.setColor(LineColor.GREEN_6);
                    }
                    if (i == 1) {
                        s.setColor(LineColor.PURPLE_8);
                    }
                    if (i == 2) {
                        s.setColor(LineColor.ORANGE_4);
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
        } else if (d.getLineStep().size() == i_jyunnbi_step_suu + (honsuu) + (honsuu)) {//19     //動作の準備としてソフトが返答するステップ数が終わった状態で実行

            int i_tikai_s_step_suu = 0;

            LineSegment closestLineSegment = new LineSegment();
            //line_step[2から10]までとs_step[11から19]まで
            closestLineSegment.set(d.get_moyori_step_lineSegment(p, 3, 2 + (honsuu)));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                d.lineStepAdd(closestLineSegment);
            }

            //line_step[2から10]までとs_step[11から19]まで
            closestLineSegment.set(d.get_moyori_step_lineSegment(p, 2 + (honsuu) + 1, 2 + (honsuu) + (honsuu)));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                i_tikai_s_step_suu = i_tikai_s_step_suu + 1;
                d.lineStepAdd(closestLineSegment);
            }

            if (i_tikai_s_step_suu == 2) { //この段階でs_stepが[21]までうまってたら、line_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //=     1+ (honsuu) +(honsuu) +  2 ){i_egaki_dankai=0; //この段階でs_stepが[21]までうまってたら、line_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //例外処理としてs_step[20]とs_step[21]が平行の場合、より近いほうをs_stepが[20]とし、s_stepを[20]としてリターン（この場合はまだ処理は終われない）。
                //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
                //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する

                if (OritaCalc.isLineSegmentParallel(d.getLineStep().get(d.getLineStep().size() - 1 - 1), d.getLineStep().get(d.getLineStep().size() - 1), Epsilon.UNKNOWN_01) != OritaCalc.ParallelJudgement.NOT_PARALLEL) {//ここは安全を見て閾値を0.1と大目にとっておこのがよさそう
                    d.getLineStep().clear();
                    return;
                }


                //line_step[20]とs_step[21]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                Point kousa_point = new Point();
                kousa_point.set(OritaCalc.findIntersection(d.getLineStep().get(2 + (honsuu) + (honsuu)), d.getLineStep().get(2 + (honsuu) + (honsuu) + 1)));

                LineSegment add_sen = new LineSegment(kousa_point, d.getLineStep().get(2 + (honsuu) + (honsuu)).getA());
                add_sen.setColor(d.getLineColor());
                if (Epsilon.high.gt0(add_sen.determineLength())) {
                    d.addLineSegment(add_sen);
                }

                LineSegment add_sen2 = new LineSegment(kousa_point, d.getLineStep().get(2 + (honsuu) + (honsuu) + 1).getA());
                add_sen2.setColor(d.getLineColor());
                if (Epsilon.high.gt0(add_sen.determineLength())) {
                    d.addLineSegment(add_sen2);
                }
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
