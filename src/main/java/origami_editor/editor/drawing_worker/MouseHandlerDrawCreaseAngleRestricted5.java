package origami_editor.editor.drawing_worker;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerDrawCreaseAngleRestricted5 extends BaseMouseHandler{
    private final MouseHandlerPolygonSetNoCorners mouseHandlerPolygonSetNoCorners;

    public MouseHandlerDrawCreaseAngleRestricted5(DrawingWorker d) {
        super(d);
        this.mouseHandlerPolygonSetNoCorners = new MouseHandlerPolygonSetNoCorners(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37;
    }

    //37 37 37 37 37 37 37 37 37 37 37;角度規格化
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        mouseHandlerPolygonSetNoCorners.mouseMoved(p0);
    }//近い既存点のみ表示

    //マウス操作(mouseMode==37　でボタンを押したとき)時の作業-------//System.out.println("A");---------------------------------------------
    public void mousePressed(Point p0) {
        d.line_step[1].setActive(LineSegment.ActiveState.ACTIVE_B_2);
        d.i_drawing_stage = 1;

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) > d.selectionDistance) {
            d.i_drawing_stage = 0;
        }
        d.line_step[1].set(p, d.closest_point);
        d.line_step[1].setColor(d.lineColor);

        d.line_step[2].set(d.line_step[1]);//ここではs_step[2]は表示されない、計算用の線分
    }

    //マウス操作(mouseMode==37　でドラッグしたとき)を行う関数--------------//System.out.println("A");--------------------------------------
    public void mouseDragged(Point p0) {
        Point syuusei_point = new Point(syuusei_point_A_37(p0));
        d.line_step[1].setA(syuusei_point);

        if (d.gridInputAssist) {
            d.i_candidate_stage = 1;
            d.line_candidate[1].set(kouho_point_A_37(syuusei_point), kouho_point_A_37(syuusei_point));
            d.line_candidate[1].setColor(d.lineColor);
            d.line_step[1].setA(kouho_point_A_37(syuusei_point));
        }

    }

    //マウス操作(mouseMode==37　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.i_drawing_stage == 1) {
            d.i_drawing_stage = 0;
            Point syuusei_point = new Point(syuusei_point_A_37(p0));
            d.line_step[1].setA(kouho_point_A_37(syuusei_point));
            if (d.line_step[1].getLength() > 0.00000001) {
                d.addLineSegment(d.line_step[1]);
                d.record();
            }
        }
    }

    public Point syuusei_point_A_37(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Point syuusei_point = new Point();
        double d_rad = 0.0;
        d.line_step[2].setA(p);

        if (d.id_angle_system != 0) {
            d.d_angle_system = 180.0 / (double) d.id_angle_system;
            d_rad = (Math.PI / 180) * d.d_angle_system * (int) Math.round(OritaCalc.angle(d.line_step[2]) / d.d_angle_system);
        } else {
            double[] jk = new double[7];
            jk[0] = OritaCalc.angle(d.line_step[2]);//マウスで入力した線分がX軸となす角度
            jk[1] = d.d_restricted_angle_1 - 180.0;
            jk[2] = d.d_restricted_angle_2 - 180.0;
            jk[3] = d.d_restricted_angle_3 - 180.0;
            jk[4] = 360.0 - d.d_restricted_angle_1 - 180.0;
            jk[5] = 360.0 - d.d_restricted_angle_2 - 180.0;
            jk[6] = 360.0 - d.d_restricted_angle_3 - 180.0;

            double d_kakudo_sa_min = 1000.0;
            for (int i = 1; i <= 6; i++) {
                if (Math.min(OritaCalc.angle_between_0_360(jk[i] - jk[0]), OritaCalc.angle_between_0_360(jk[0] - jk[i])) < d_kakudo_sa_min) {
                    d_kakudo_sa_min = Math.min(OritaCalc.angle_between_0_360(jk[i] - jk[0]), OritaCalc.angle_between_0_360(jk[0] - jk[i]));
                    d_rad = (Math.PI / 180) * jk[i];
                }
            }
        }

        syuusei_point.set(OritaCalc.findProjection(d.line_step[2].getB(), new Point(d.line_step[2].getBX() + Math.cos(d_rad), d.line_step[2].getBY() + Math.sin(d_rad)), p));
        return syuusei_point;
    }

    // ---
    public Point kouho_point_A_37(Point syuusei_point) {
        d.closest_point.set(d.getClosestPoint(syuusei_point));
        double zure_kakudo = OritaCalc.angle(d.line_step[2].getB(), syuusei_point, d.line_step[2].getB(), d.closest_point);
        int zure_flg = 0;
        if ((0.00001 < zure_kakudo) && (zure_kakudo <= 359.99999)) {
            zure_flg = 1;
        }
        if ((zure_flg == 0) && (syuusei_point.distance(d.closest_point) <= d.selectionDistance)) {//最寄点が角度系にのっていて、修正点とも近い場合
            return d.closest_point;
        }
        return syuusei_point;
    }
}
