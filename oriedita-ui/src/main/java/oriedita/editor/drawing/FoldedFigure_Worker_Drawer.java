package oriedita.editor.drawing;

import oriedita.editor.Colors;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.FoldedFigure_Worker;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.folding.HierarchyList;
import origami.folding.constraint.CustomConstraint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * Responsible for drawing a folded figure.
 */
public class FoldedFigure_Worker_Drawer {
    private static boolean displaySsi = false;
    private final boolean showConstraints = true;
    private final FoldedFigure_Worker worker;
    private final Camera camera = new Camera();
    private Color F_color = new Color(255, 255, 50);//表面の色
    private Color B_color = new Color(233, 233, 233);//裏面の色
    private Color L_color = Color.black;//線の色
    private boolean antiAlias = true;
    private boolean displayShadows = false; //Whether to display shadows. 0 is not displayed, 1 is displayed
    private boolean displayNumbers = false;

    public FoldedFigure_Worker_Drawer(FoldedFigure_Worker worker) {
        this.worker = worker;
    }

    private static void drawLine(Graphics2D g2, Camera camera, PointSet subFace_figure, int ib) {

        LineSegment s_ob = new LineSegment(subFace_figure.getBeginX(ib),
                subFace_figure.getBeginY(ib),
                subFace_figure.getEndX(ib),
                subFace_figure.getEndY(ib));
        LineSegment s_tv = camera.object2TV(s_ob);

        Path2D.Double line = new Path2D.Double();
        line.moveTo(s_tv.determineAX(), s_tv.determineAY());
        line.lineTo(s_tv.determineBX(), s_tv.determineBY());

        g2.draw(line);
    }

    public static void setStaticData(ApplicationModel applicationModel) {
        displaySsi = applicationModel.getDisplaySelfIntersection();
    }

    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);
    }

    public void reset() {
        camera.reset();
        worker.reset();
    }

    public void draw_transparency_with_camera(Graphics g, WireFrame_Worker_Drawer orite, PointSet otta_Face_figure, PointSet subFace_figure, boolean transparencyColor, int transparency_toukado) {
        Graphics2D g2 = (Graphics2D) g;

        if (transparencyColor) {
            //カラーの透過図
            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), transparency_toukado));

            //Draw a face
            for (int im = 1; im <= otta_Face_figure.getNumFaces(); im++) {
                fillFace(g2, camera, otta_Face_figure, im);
            }

            //Preparing to draw a line
            setAntiAlias(g2);

            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), 2 * transparency_toukado));
        } else {//Black and white transparent view (old style)
            //Find the proper darkness of the surface
            int col_hiku = 0;
            int colmax = 255;
            int colmin = 30;//colmax=255(真っ白)以下、colmin=0(真っ黒)以上
            //Menidsuu_max must be 1 or greater
            if (worker.FaceIdCount_max > 0) {
                col_hiku = (colmax - colmin) / worker.FaceIdCount_max;
            }

            int col_kosa;
            for (int im = 1; im <= subFace_figure.getNumFaces(); im++) {
                col_kosa = colmax - col_hiku * (worker.s0[im].getFaceIdCount());

                if (col_kosa > 255) {
                    col_kosa = 255;
                }

                if (col_kosa < 0) {
                    col_kosa = 0;
                }
                g.setColor(Colors.get(new Color(col_kosa, col_kosa, col_kosa)));

                fillFace(g2, camera, subFace_figure, im);
            }

            drawConstraints(g2);

            //Prepare the line
            g.setColor(Colors.get(Color.black));

            setAntiAlias(g2);
        }

        //Draw a line
        drawLines(g2, camera, subFace_figure);
    }

    public void drawSelfIntersectingSubFaces(Graphics g, WireFrame_Worker_Drawer orite, PointSet subFace_figure) {
        if (worker.errorPos != null && displaySsi) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Colors.get(new Color(255, 0, 0, 75)));
            fillSubFace(g2, camera, subFace_figure, worker.errorPos.getA());
            fillSubFace(g2, camera, subFace_figure, worker.errorPos.getB());
            fillSubFace(g2, camera, subFace_figure, worker.errorPos.getC());
            fillSubFace(g2, camera, subFace_figure, worker.errorPos.getD());

            fillFace(g2, orite.getCamera(), orite.get(), worker.errorPos.getA());
            fillFace(g2, orite.getCamera(), orite.get(), worker.errorPos.getB());
            fillFace(g2, orite.getCamera(), orite.get(), worker.errorPos.getC());
            fillFace(g2, orite.getCamera(), orite.get(), worker.errorPos.getD());
        }
    }

    private static void drawLines(Graphics2D g2, Camera camera, PointSet subFace_figure) {
        for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
            drawLine(g2, camera, subFace_figure, ib);
        }
    }

    private void fillSubFace(Graphics2D g, Camera transform, PointSet faces, int id) {
        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            if (worker.s[i].contains(id)) {
                fillFace(g, transform, faces, i);
            }
        }
    }

    private void fillFace(Graphics2D g, Camera transform, PointSet faces, int id) {

        Path2D.Double path = new Path2D.Double();

        Point t1 = transform.object2TV(faces.getPoint(faces.getPointId(id, 1)));
        path.moveTo(t1.getX(), t1.getY());

        for (int i = 2; i <= faces.getPointsCount(id); i++) {
            t1 = transform.object2TV(faces.getPoint(faces.getPointId(id, i)));
            path.lineTo(t1.getX(), t1.getY());
        }

        path.closePath();

        g.fill(path);
    }

    public void calculateFromTopCountedPosition() {
        //if(hyouji_flg==5){//折紙表示---------------------------------------------------------------------------
        //SubFaces.set_FaceId2fromTop_counted_position stores the id number of the i-th surface counting from the top in all orders based on the current table above and below.
        for (int im = 1; im <= worker.SubFaceTotal; im++) { //Find the id of the specified face from the top from SubFace.
            worker.s0[im].set_FaceId2fromTop_counted_position(worker.hierarchyList);//s0[] is the SubFace itself obtained from SubFace_zu, and the hierarchyList is the upper and lower table hierarchyList.
        }
        //ここまでで、上下表の情報がSubFaceの各面に入った
    }

    public void draw_foldedFigure_with_camera(Graphics g, WireFrame_Worker orite, PointSet subFace_figure) {
        Graphics2D g2 = (Graphics2D) g;
        boolean flipped = camera.determineIsCameraMirrored();

        //面を描く-----------------------------------------------------------------------------------------------------
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ

        int faceOrder;
        for (int im = 1; im <= worker.SubFaceTotal; im++) {//imは各SubFaceの番号
            if (worker.s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外
                //Determine the color of the im-th SubFace when drawing a fold-up diagram
                faceOrder = flipped ? worker.s0[im].getFaceIdCount() : 1;

                int i1 = worker.s0[im].fromTop_count_FaceId(faceOrder);
                int iFacePosition = orite.getIFacePosition(i1);

                if (flipped) {
                    if (iFacePosition % 2 == 0) {
                        g.setColor(F_color);
                    } else {
                        g.setColor(B_color);
                    }
                } else {
                    if (iFacePosition % 2 == 1) {
                        g.setColor(F_color);
                    } else {
                        g.setColor(B_color);
                    }
                }

                fillFace(g2, camera, subFace_figure, im);
            }
        }
        // Draw a surface so far


        //Add a shadow  ------------------------------------------------------------------------------------
        if (displayShadows) {
            for (int lineId = 1; lineId <= subFace_figure.getNumLines(); lineId++) {
                int im = line_no_bangou_kara_kagenoaru_subFace_no_bangou_wo_motomeru(lineId, subFace_figure, flipped);//影をつけるSubFaceのid
                if (im != 0) {//影を描く。
                    //折り上がり図を描くときのim番目のSubFaceの多角形の頂点の座標（PC表示上）を求める

                    //棒の座標   subFace_figure.getmaex(lineId),subFace_figure.getmaey(lineId)   -    subFace_figure.getatox(lineId) , subFace_figure.getatoy(lineId)
                    Point b_begin = new Point(subFace_figure.getBegin(lineId), subFace_figure.getBeginY(lineId));
                    Point b_end = new Point(subFace_figure.getEndX(lineId), subFace_figure.getEndY(lineId));
                    double b_length = b_begin.distance(b_end);

                    //棒と直交するベクトル
                    double o_btx = -(subFace_figure.getBeginY(lineId) - subFace_figure.getEndY(lineId)) * 10.0 / b_length;//棒と直交するxベクトル
                    double o_bty = (subFace_figure.getBeginX(lineId) - subFace_figure.getEndX(lineId)) * 10.0 / b_length;//棒と直交するyベクトル

                    //棒の中点
                    double o_bmx, o_bmy;
                    double t_bmx, t_bmy;

                    o_bmx = (subFace_figure.getBeginX(lineId) + subFace_figure.getEndX(lineId)) / 2.0;
                    o_bmy = (subFace_figure.getBeginY(lineId) + subFace_figure.getEndY(lineId)) / 2.0;

                    Point t0 = new Point(o_bmx, o_bmy);
                    Point t1 = camera.object2TV(t0);
                    t_bmx = t1.getX();
                    t_bmy = t1.getY();

                    //棒の中点を通る直交線上の点
                    double o_bmtx, o_bmty;
                    double t_bmtx, t_bmty;

                    //A point on the orthogonal line that passes through the midpoint of the bar
                    o_bmtx = o_bmx + o_btx;
                    o_bmty = o_bmy + o_bty;

                    if (subFace_figure.inside(new origami.crease_pattern.element.Point(o_bmx + Epsilon.UNKNOWN_001 * o_btx, o_bmy + Epsilon.UNKNOWN_001 * o_bty), im) != origami.crease_pattern.element.Polygon.Intersection.OUTSIDE) {//0=外部、　1=境界、　2=内部
                        t0 = new Point(o_bmtx, o_bmty);
                        t1 = camera.object2TV(t0);
                        t_bmtx = t1.getX();
                        t_bmty = t1.getY();

                        Path2D.Double path = new Path2D.Double();
                        //影の長方形

                        // ---------- [0] ----------------
                        t0 = new Point(subFace_figure.getBeginX(lineId), subFace_figure.getBeginY(lineId));
                        t1 = camera.object2TV(t0);

                        path.moveTo(t1.getX(), t1.getY());

                        // ---------- [1] ----------------
                        t0 = new Point(subFace_figure.getBeginX(lineId) + o_btx, subFace_figure.getBeginY(lineId) + o_bty);
                        t1 = camera.object2TV(t0);

                        path.lineTo(t1.getX(), t1.getY());

                        // ---------- [2] ----------------
                        t0 = new Point(subFace_figure.getEndX(lineId) + o_btx, subFace_figure.getEndY(lineId) + o_bty);
                        t1 = camera.object2TV(t0);

                        path.lineTo(t1.getX(), t1.getY());

                        // ---------- [3] ----------------
                        t0 = new Point(subFace_figure.getEndX(lineId), subFace_figure.getEndY(lineId));
                        t1 = camera.object2TV(t0);

                        path.lineTo(t1.getX(), t1.getY());
                        path.closePath();

                        g2.setPaint(new GradientPaint((float) t_bmx, (float) t_bmy, new Color(0, 0, 0, 50), (float) t_bmtx, (float) t_bmty, new Color(0, 0, 0, 0)));
                        g2.fill(path);
                    }
                    //----------------------------------棒と直交するxベクトルの向きを変えて影を描画
                    o_btx = -o_btx;//棒と直交するxベクトル
                    o_bty = -o_bty;//棒と直交するyベクトル

                    //-----------------------------------------------
                    //棒の中点を通る直交線上の点

                    if (subFace_figure.inside(new Point(o_bmx + Epsilon.UNKNOWN_001 * o_btx, o_bmy + Epsilon.UNKNOWN_001 * o_bty), im) != origami.crease_pattern.element.Polygon.Intersection.OUTSIDE) {//0=外部、　1=境界、　2=内部
                        Path2D.Double path = new Path2D.Double();
                        //影の長方形

                        // ---------- [0] ----------------
                        t0 = new Point(subFace_figure.getBeginX(lineId), subFace_figure.getBeginY(lineId));
                        t1 = camera.object2TV(t0);

                        double xd0 = t1.getX();
                        double yd0 = t1.getY();
                        path.moveTo(t1.getX(), t1.getY());

                        // ---------- [1] ----------------
                        t0 = new Point(subFace_figure.getBeginX(lineId) + o_btx, subFace_figure.getBeginY(lineId) + o_bty);
                        t1 = camera.object2TV(t0);
                        double xd1 = t1.getX();
                        double yd1 = t1.getY();
                        path.lineTo(t1.getX(), t1.getY());


                        // ---------- [2] ----------------
                        t0 = new Point(subFace_figure.getEndX(lineId) + o_btx, subFace_figure.getEndY(lineId) + o_bty);
                        t1 = camera.object2TV(t0);
                        path.lineTo(t1.getX(), t1.getY());

                        // ---------- [3] ----------------
                        t0 = new Point(subFace_figure.getEndX(lineId), subFace_figure.getEndY(lineId));
                        t1 = camera.object2TV(t0);
                        path.lineTo(t1.getX(), t1.getY());
                        path.closePath();

                        g2.setPaint(new GradientPaint((float) xd0, (float) yd0, new Color(0, 0, 0, 50), (float) xd1, (float) yd1, new Color(0, 0, 0, 0)));
                        g2.fill(path);
                    }
                }
            }
        }//影をつけるは、ここで終わり

        //棒を描く-----------------------------------------------------------------------------------------

        setAntiAlias(g2);
        g.setColor(L_color);//g.setColor(Colors.get(Color.black));

        for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {

            int Mid_min, Mid_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
            int faceOrderMin, faceOrderMax;//PC画面に表示したときSubFace(Mid_min) で見える面の番号がMen_jyunban_min、SubFace(Mid_max) で見える面の番号がMen_jyunban_max
            boolean drawing_flag;

            drawing_flag = false;
            Mid_min = subFace_figure.lineInFaceBorder_min_lookup(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
            Mid_max = subFace_figure.lineInFaceBorder_max_lookup(ib);

            if (worker.s0[Mid_min].getFaceIdCount() == 0) {
                drawing_flag = true;
            }//menをもたない、ドーナツの穴状のSubFaceは境界の棒を描く
            else if (worker.s0[Mid_max].getFaceIdCount() == 0) {
                drawing_flag = true;
            } else if (Mid_min == Mid_max) {
                drawing_flag = true;
            }//一本の棒の片面だけにSubFace有り
            else {
                faceOrderMin = 1;
                if (flipped) {
                    faceOrderMin = worker.s0[Mid_min].getFaceIdCount();
                }
                faceOrderMax = 1;
                if (flipped) {
                    faceOrderMax = worker.s0[Mid_max].getFaceIdCount();
                }
                if (worker.s0[Mid_min].fromTop_count_FaceId(faceOrderMin) != worker.s0[Mid_max].fromTop_count_FaceId(faceOrderMax)) {
                    drawing_flag = true;
                }//この棒で隣接するSubFaceの1番上の面は異なるので、この棒は描く。
            }

            if (drawing_flag) {//棒を描く。
                drawLine(g2, camera, subFace_figure, ib);
            }
        }

        drawConstraints(g2);
    }

    private void drawConstraints(Graphics2D g2) {
        if (!showConstraints) {
            return;
        }
        Color fill, border;

        for (CustomConstraint cc : worker.hierarchyList.getCustomConstraints()) {
            if (camera.determineIsCameraMirrored()) {
                if (cc.getFaceOrder() == CustomConstraint.FaceOrder.NORMAL) {
                    continue;
                }
            } else {
                if (cc.getFaceOrder() == CustomConstraint.FaceOrder.FLIPPED) {
                    continue;
                }
            }
            Point pos = camera.object2TV(cc.getPos());
            g2.setStroke(new BasicStroke(1));
            if (cc.getType() == CustomConstraint.Type.COLOR_BACK) {
                fill = Color.white;
                border = Color.black;
            } else {
                fill = Color.black;
                border = Color.white;
            }

            int x = (int) pos.getX();
            int y = (int) pos.getY();
            g2.setPaint(fill);
            g2.fillOval(x - 4, y - 4, 8, 8);
            g2.setColor(border);
            g2.drawOval(x - 4, y - 4, 8, 8);
        }
    }

    private void setAntiAlias(Graphics2D g2) {
        if (antiAlias) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//アンチェイリアス　オン
            BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
            BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        }
    }

    //---------------------------------------------------------
    public void draw_cross_with_camera(Graphics g, boolean selected, int index) {
        Graphics2D g2 = (Graphics2D) g;
        //Draw the center of the camera with a cross
        Point point = camera.object2TV(camera.getCameraPosition());
        DrawingUtil.cross(g, point, 5.0, 2.0, LineColor.ORANGE_4);

        if (selected) {
            g.setColor(Colors.get(new Color(200, 50, 255, 90)));
            Ellipse2D.Double ellipse = new Ellipse2D.Double(point.getX() - 25, point.getY() - 25, 50, 50);

            g2.fill(ellipse);
        }

        if (displayNumbers) {
            Font f = g.getFont();
            g.setFont(new Font(f.getName(), f.getStyle(), 50));
            g.setColor(Color.orange);

            g2.drawString(String.valueOf(index), (float) point.getX() + 25, (float) point.getY() + 25);
            g.setFont(f);
        }
    }

    public int line_no_bangou_kara_kagenoaru_subFace_no_bangou_wo_motomeru(int ib, PointSet subFace_figure, boolean flipped) {//From the number of the bar, find the number of the SubFace where the shadow of the bar is generated. Returns 0 if no shadows occur.
        int i_return;

        int faceId_min, faceId_max; //棒の両側のSubFaceの番号の小さいほうがMid_min,　大きいほうがMid_max
        int faceOrderMin, faceordermax;//PC画面に表示したときSubFace(faceId_min) で見える面の、そのSubFaceでの重なり順がMen_jyunban_min、SubFace(faceId_max) で見える面のそのSubFaceでの重なり順がMen_jyunban_max

        faceId_min = subFace_figure.lineInFaceBorder_min_lookup(ib);//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
        faceId_max = subFace_figure.lineInFaceBorder_max_lookup(ib);

        if (worker.s0[faceId_min].getFaceIdCount() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のSubFaceとの境界の棒には影なし
        if (worker.s0[faceId_max].getFaceIdCount() == 0) {
            return 0;
        }//menをもたない、ドーナツの穴状のFaceStackとの境界の棒には影なし
        if (faceId_min == faceId_max) {
            return 0;
        }//一本の棒の片面だけにFaceStack有り

        faceOrderMin = 1;
        if (flipped) {
            faceOrderMin = worker.s0[faceId_min].getFaceIdCount();
        }
        faceordermax = 1;
        if (flipped) {
            faceordermax = worker.s0[faceId_max].getFaceIdCount();
        }

        int Mid_min_mieteru_men_id = worker.s0[faceId_min].fromTop_count_FaceId(faceOrderMin);
        int Mid_max_mieteru_men_id = worker.s0[faceId_max].fromTop_count_FaceId(faceordermax);
        if (Mid_min_mieteru_men_id == Mid_max_mieteru_men_id) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面が同じなので、棒自体が描かれず影もなし。


        if (worker.hierarchyList.isEmpty(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id)) {
            return 0;
        }//この棒で隣接するFaceStackで見えてる面の上下関係不明なので、影はなし
        //この棒で隣接するFaceStackで見えてる面の上下関係ない（重ならない）ので、影はなし

        i_return = faceId_min;
        if (worker.hierarchyList.get(Mid_min_mieteru_men_id, Mid_max_mieteru_men_id) == HierarchyList.ABOVE_1) {
            i_return = faceId_max;
        }

        if (flipped) {
            if (i_return == faceId_min) {
                return faceId_max;
            } else {
                return faceId_min;
            }
        }

        return i_return;
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        F_color = foldedFigureModel.getFrontColor();
        B_color = foldedFigureModel.getBackColor();
        L_color = foldedFigureModel.getLineColor();

        antiAlias = foldedFigureModel.getAntiAlias();
        displayShadows = foldedFigureModel.getDisplayShadows();
    }

    public void setData(ApplicationModel applicationModel) {
        displayNumbers = applicationModel.getDisplayNumbers();
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigureModel.setAntiAlias(antiAlias);
        foldedFigureModel.setDisplayShadows(displayShadows);
    }
}
