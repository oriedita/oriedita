package origami_editor.editor.canvas;

import javax.inject.Inject;
import javax.inject.Singleton;

import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

@Singleton
public class MouseHandlerAngleSystem extends BaseMouseHandlerInputRestricted {
    double d_angle_system;

    @Inject
    public MouseHandlerAngleSystem() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.ANGLE_SYSTEM_16;
    }

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.lineStep.size() <= 1) {
            super.mouseMoved(p0);//近い既存点のみ表示
        }
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

        if ((d.lineStep.size() == 0) || (d.lineStep.size() == 1)) {
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) < d.selectionDistance) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, LineColor.fromNumber(d.lineStep.size() + 1)));
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

                boolean i_jyun;
                i_jyun = false;//i_jyunは線を描くとき順番に色を変えたいとき使う
                for (int i = 1; i <= honsuu; i++) {
                    i_jyun = !i_jyun;

                    kakudo = kakudo + d_angle_system;
                    LineSegment e = OritaCalc.lineSegment_rotate(s_kiso, kakudo, 1.0);
                    d.lineStepAdd(e);
                    if (i_jyun) {
                        e.setColor(LineColor.ORANGE_4);
                    } else {
                        e.setColor(LineColor.GREEN_6);
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


                LineSegment s_kiso = new LineSegment(d.lineStep.get(1).getA(), d.lineStep.get(0).getA());

                for (int i = 1; i <= 6; i++) {
                    LineSegment s = new LineSegment();
                    kakudo = jk[i];
                    s.set(OritaCalc.lineSegment_rotate(s_kiso, kakudo, 1.0));
                    d.lineStepAdd(s);

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
                }
            }

            return;
        }


        if (d.lineStep.size() == 2 + (honsuu)) {
            LineSegment closestLineSegment = d.get_moyori_step_lineSegment(p, 3, 2 + honsuu);
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                LineSegment s = new LineSegment();
                s.set(closestLineSegment);
                s.setColor(LineColor.BLUE_2);
                d.lineStepAdd(s);
                return;
            }
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.selectionDistance) {
                d.lineStep.clear();
                return;
            }
        }


        if (d.lineStep.size() == 2 + (honsuu) + 1) {
            LineSegment closestLineSegment = d.getClosestLineSegment(p);
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) >= d.selectionDistance) {//最寄折線が遠かった場合
                d.lineStep.clear();
                return;
            }

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.selectionDistance) {
                LineSegment s = new LineSegment();
                s.set(closestLineSegment);
                s.setColor(LineColor.GREEN_6);
                d.lineStepAdd(s);
            }
        }

        if (d.lineStep.size() == 2 + (honsuu) + 1 + 1) {
            d.lineStep.clear();

            Point kousa_point = new Point();
            kousa_point.set(OritaCalc.findIntersection(d.lineStep.get(2 + (honsuu)), d.lineStep.get(2 + (honsuu) + 1)));
            LineSegment add_sen = new LineSegment(kousa_point, d.lineStep.get(0).getA(), d.lineColor);
            if (Epsilon.high.gt0(add_sen.determineLength())) {
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
