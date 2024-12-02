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
@Handles(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13)
public class MouseHandlerDrawCreaseAngleRestricted extends BaseMouseHandler {
    private final AngleSystemModel angleSystemModel;
    double d_angle_system;

    @Inject
    public MouseHandlerDrawCreaseAngleRestricted(AngleSystemModel angleSystemModel) {
        this.angleSystemModel = angleSystemModel;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        // select a line
        if(d.getLineStep().isEmpty() && d.getClosestPoint(p).distance(p) > d.getSelectionDistance()){
            LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                closestLineSegment.setColor(LineColor.MAGENTA_5);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        //select 2 points
        if(d.getLineStep().isEmpty() || d.getLineStep().get(0).determineLength() <= 0.0){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
            }

            if(d.getLineStep().size() == 2){
                Point p1 = new Point(d.getLineStep().get(0).getA());
                Point p2 = new Point(d.getLineStep().get(1).getA());
                LineSegment selectedSegment = new LineSegment(p1, p2);
                selectedSegment.setColor(LineColor.MAGENTA_5);

                d.getLineStep().clear();
                d.lineStepAdd(selectedSegment);
            }
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        int honsuu;//1つの端点周りに描く線の本数
        if (angleSystemModel.getCurrentAngleSystemDivider() != 0) {
            honsuu = angleSystemModel.getCurrentAngleSystemDivider() * 2 - 1;
        } else {
            honsuu = 6;
        }

        if (d.getLineStep().size() == 1 && d.getLineStep().get(0).determineLength() > 0.0) {    //if(i_egaki_dankai==1){        //動作の準備として人間が選択するステップ数が終わった状態で実行
            boolean i_jyun;//i_jyunは線を描くとき順番に色を変えたいとき使う
            //線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）public oc.Senbun_kaiten(Senbun s0,double d) //    double d_angle_system;double angle;

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

                    angle = angle + d_angle_system;
                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0);
                    s.setColor(i_jyun ? LineColor.ORANGE_4 : LineColor.GREEN_6);
                    d.lineStepAdd(s);

                }

                s_kiso = new LineSegment(d.getLineStep().get(0).getB(), d.getLineStep().get(0).getA());
                angle = 0.0;
                i_jyun = false;
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = !i_jyun;
                    angle = angle + d_angle_system;
                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0);
                    s.setColor(i_jyun ? LineColor.ORANGE_4 : LineColor.GREEN_6);

                    d.lineStepAdd(s);
                }
            }
            if (angleSystemModel.getCurrentAngleSystemDivider() == 0) {
                double[] jk = angleSystemModel.getAngles();

                LineSegment s_kiso = new LineSegment(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB());
                double angle;
                i_jyun = false;
                for (int i = 0; i < 6; i++) {
                    i_jyun = !i_jyun;

                    angle = jk[i];
                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0);
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

                s_kiso = new LineSegment(d.getLineStep().get(0).getB(), d.getLineStep().get(0).getA());
                i_jyun = false;
                for (int i = 1; i <= 6; i++) {
                    i_jyun = !i_jyun;

                    angle = jk[i];
                    LineSegment s = OritaCalc.lineSegment_rotate(s_kiso, angle, 10.0);
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
        } else if (d.getLineStep().size() == 1 + (honsuu) + (honsuu)) {//19     //動作の準備としてソフトが返答するステップ数が終わった状態で実行
            int i_tikai_s_step_suu = 0;

            //line_step[2から10]までとs_step[11から19]まで
            LineSegment closestLineSegment = new LineSegment(d.getClosestLineStepSegment(p, 2, 1 + (honsuu)));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                i_tikai_s_step_suu++;
                d.lineStepAdd(closestLineSegment);
            }

            //line_step[2から10]までとs_step[11から19]まで
            closestLineSegment = new LineSegment(d.getClosestLineStepSegment(p, 1 + (honsuu) + 1, 1 + (honsuu) + (honsuu)));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) {
                i_tikai_s_step_suu++;
                d.lineStepAdd(closestLineSegment);    //line_step[i_egaki_dankai].setcolor(lineColor);
            }

            if (i_tikai_s_step_suu == 2) { //この段階でs_stepが[21]までうまってたら、line_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //=     1+ (honsuu) +(honsuu) +  2 ){i_egaki_dankai=0; //この段階でs_stepが[21]までうまってたら、line_step[20]とs_step[21]は共に加える折線なので、ここで処理を終えてしまう。
                //例外処理としてs_step[20]とs_step[21]が平行の場合、より近いほうをs_stepが[20]とし、s_stepを[20]としてリターン（この場合はまだ処理は終われない）。
                //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
                //0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する

                if (OritaCalc.isLineSegmentParallel(d.getLineStep().get(d.getLineStep().size() - 1 - 1), d.getLineStep().get(d.getLineStep().size() - 1)) != OritaCalc.ParallelJudgement.NOT_PARALLEL) {//ここは安全を見て閾値を0.1と大目にとっておこのがよさそう
                    d.getLineStep().clear();
                    return;
                }

                //line_step[20]とs_step[21]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                Point kousa_point = OritaCalc.findIntersection(d.getLineStep().get(1 + honsuu + honsuu), d.getLineStep().get(1 + honsuu + honsuu + 1));

                LineSegment add_sen = new LineSegment(kousa_point, d.getLineStep().get(1 + (honsuu) + (honsuu)).getA());
                add_sen.setColor(d.getLineColor());
                if (Epsilon.high.gt0(add_sen.determineLength())) {
                    d.addLineSegment(add_sen);
                }

                LineSegment add_sen2 = new LineSegment(kousa_point, d.getLineStep().get(1 + (honsuu) + (honsuu) + 1).getA());
                add_sen2.setColor(d.getLineColor());
                if (Epsilon.high.gt0(add_sen.determineLength())) {
                    d.addLineSegment(add_sen2);
                }
                d.record();
                d.getLineStep().clear();
            }

            d.getLineStep().clear();
        }
    }
}
