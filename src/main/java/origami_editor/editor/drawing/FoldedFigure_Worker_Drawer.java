package origami_editor.editor.drawing;

import origami.Epsilon;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.worker.FoldedFigure_Worker;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.folding.HierarchyList;
import origami_editor.editor.Colors;
import origami_editor.editor.canvas.DrawingUtil;
import origami_editor.editor.databinding.ApplicationModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.tools.Camera;
import origami.crease_pattern.element.Point;

import java.awt.*;

/**
 * Responsible for drawing a folded figure.
 */
public class FoldedFigure_Worker_Drawer {
    private final FoldedFigure_Worker worker;

    Camera camera = new Camera();

    Color F_color = new Color(255, 255, 50);//表面の色
    Color B_color = new Color(233, 233, 233);//裏面の色
    Color L_color = Color.black;//線の色

    boolean antiAlias = true;
    boolean displayShadows = false; //Whether to display shadows. 0 is not displayed, 1 is displayed
    static boolean displaySsi = false;
    private boolean displayNumbers = false;

    public FoldedFigure_Worker_Drawer(FoldedFigure_Worker worker) {
        this.worker = worker;
    }


    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);
    }

    public void reset() {
        camera.reset();
        worker.reset();
    }

    private int gx(double d) {
        return (int) d; //Front side display
    }

    private int gy(double d) {
        return (int) d;
    }

    public void draw_transparency_with_camera(Graphics g, WireFrame_Worker_Drawer orite, PointSet otta_Face_figure, PointSet subFace_figure, boolean transparencyColor, int transparency_toukado, int index) {
        Graphics2D g2 = (Graphics2D) g;

        origami.crease_pattern.element.Point t0 = new origami.crease_pattern.element.Point();
        origami.crease_pattern.element.Point t1 = new origami.crease_pattern.element.Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();

        //Preparing to draw a face
        int[] x = new int[100];
        int[] y = new int[100];

        //Find the proper darkness of the surface
        int col_hiku = 0;
        int colmax = 255;
        int colmin = 30;//colmax=255(真っ白)以下、colmin=0(真っ黒)以上
        //Menidsuu_max must be 1 or greater
        if (worker.FaceIdCount_max > 0) {
            col_hiku = (colmax - colmin) / worker.FaceIdCount_max;
        }

        if (transparencyColor) {
            //カラーの透過図
            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), transparency_toukado));

            //Draw a face
            for (int im = 1; im <= otta_Face_figure.getNumFaces(); im++) {
                for (int i = 1; i <= otta_Face_figure.getPointsCount(im) - 1; i++) {
                    t0.setX(otta_Face_figure.getPointX(otta_Face_figure.getPointId(im, i)));
                    t0.setY(otta_Face_figure.getPointY(otta_Face_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getX());
                    y[i] = gy(t1.getY());
                }

                t0.setX(otta_Face_figure.getPointX(otta_Face_figure.getPointId(im, otta_Face_figure.getPointsCount(im))));
                t0.setY(otta_Face_figure.getPointY(otta_Face_figure.getPointId(im, otta_Face_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getX());
                y[0] = gy(t1.getY());
                g.fillPolygon(x, y, otta_Face_figure.getPointsCount(im));
            }

            //Preparing to draw a line

            if (antiAlias) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//Anti-alias on
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//Anti-alias off
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }

            g.setColor(new Color(F_color.getRed(), F_color.getGreen(), F_color.getBlue(), 2 * transparency_toukado));
            //Draw a line
            for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.determineAX()), gy(s_tv.determineAY()), gx(s_tv.determineBX()), gy(s_tv.determineBY())); //直線
            }
        } else {//Black and white transparent view (old style)
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

                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, i)));
                    t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getX());
                    y[i] = gy(t1.getY());
                }

                t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getX());
                y[0] = gy(t1.getY());
                g.fillPolygon(x, y, subFace_figure.getPointsCount(im));
            }

            //Prepare the line
            g.setColor(Colors.get(Color.black));

            if (antiAlias) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//Anti-alias on
                BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//Anti-alias off
                BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
                g2.setStroke(BStroke);//Line thickness and shape of the end of the line
            }

            //Draw a line
            for (int ib = 1; ib <= subFace_figure.getNumLines(); ib++) {
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.determineAX()), gy(s_tv.determineAY()), gx(s_tv.determineBX()), gy(s_tv.determineBY())); //Straight line
            }
        }

        if (worker.errorPos != null && displaySsi) {
            g2.setColor(Colors.get(new Color(255, 0, 0, 75)));
            fillSubFace(g2, worker.errorPos.getA(), subFace_figure, camera);
            fillSubFace(g2, worker.errorPos.getB(), subFace_figure, camera);
            fillSubFace(g2, worker.errorPos.getC(), subFace_figure, camera);
            fillSubFace(g2, worker.errorPos.getD(), subFace_figure, camera);

            fillPolygon(g2, worker.errorPos.getA(), orite.get(), orite.camera);
            fillPolygon(g2, worker.errorPos.getB(), orite.get(), orite.camera);
            fillPolygon(g2, worker.errorPos.getC(), orite.get(), orite.camera);
            fillPolygon(g2, worker.errorPos.getD(), orite.get(), orite.camera);
        }
    }

    private void fillSubFace(Graphics2D g, int id, PointSet faces, Camera transform) {
        for (int i = 1; i <= worker.SubFaceTotal; i++) {
            if (worker.s[i].contains(id)) {
                fillPolygon(g, i, faces, transform);
            }
        }
    }

    private void fillPolygon(Graphics2D g, int id, PointSet faces, Camera transform) {
        origami.crease_pattern.element.Point t0 = new origami.crease_pattern.element.Point();
        origami.crease_pattern.element.Point t1 = new origami.crease_pattern.element.Point();

        int[] x = new int[faces.getPointsCount(id)+1];
        int[] y = new int[faces.getPointsCount(id)+1];

        for (int i = 1; i <= faces.getPointsCount(id) - 1; i++) {
            t0.setX(faces.getPointX(faces.getPointId(id, i)));
            t0.setY(faces.getPointY(faces.getPointId(id, i)));
            t1.set(transform.object2TV(t0));
            x[i] = (int)(t1.getX());
            y[i] = (int)(t1.getY());
        }

        t0.setX(faces.getPointX(faces.getPointId(id, faces.getPointsCount(id))));
        t0.setY(faces.getPointY(faces.getPointId(id, faces.getPointsCount(id))));
        t1.set(transform.object2TV(t0));
        x[0] = (int)(t1.getX());
        y[0] = (int)(t1.getY());

        g.fill(new java.awt.Polygon(x, y, faces.getPointsCount(id)));
    }

    public void calculateFromTopCountedPosition() {
        //if(hyouji_flg==5){//折紙表示---------------------------------------------------------------------------
        //SubFaces.set_FaceId2fromTop_counted_position stores the id number of the i-th surface counting from the top in all orders based on the current table above and below.
        for (int im = 1; im <= worker.SubFaceTotal; im++) { //Find the id of the specified face from the top from SubFace.
            worker.s0[im].set_FaceId2fromTop_counted_position(worker.hierarchyList);//s0[] is the SubFace itself obtained from SubFace_zu, and the hierarchyList is the upper and lower table hierarchyList.
        }
        //ここまでで、上下表の情報がSubFaceの各面に入った
    }

    public void draw_foldedFigure_with_camera(Graphics g, WireFrame_Worker orite, PointSet subFace_figure, int index) {
        Graphics2D g2 = (Graphics2D) g;
        boolean flipped = camera.determineIsCameraMirrored();

        origami.crease_pattern.element.Point t0 = new origami.crease_pattern.element.Point();
        origami.crease_pattern.element.Point t1 = new origami.crease_pattern.element.Point();
        LineSegment s_ob = new LineSegment();
        LineSegment s_tv = new LineSegment();

        //Draw a face
        int[] x = new int[100];
        int[] y = new int[100];

        double[] xd = new double[100];
        double[] yd = new double[100];

        //面を描く-----------------------------------------------------------------------------------------------------
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ

        int faceOrder;
        for (int im = 1; im <= worker.SubFaceTotal; im++) {//imは各SubFaceの番号
            if (worker.s0[im].getFaceIdCount() > 0) {//MenidsuuはSubFace(折り畳み推定してえられた針金図を細分割した面)で重なっているMen(折りたたむ前の展開図の面)の数。これが0なら、ドーナツ状の穴の面なので描画対象外
                //Determine the color of the im-th SubFace when drawing a fold-up diagram
                faceOrder = flipped ? worker.s0[im].getFaceIdCount() : 1;

                int i1 = worker.s0[im].fromTop_count_FaceId(faceOrder);
                int iFacePosition = orite.getIFacePosition(i1);
                if (iFacePosition % 2 == 1) {
                    g.setColor(F_color);
                }
                if (iFacePosition % 2 == 0) {
                    g.setColor(B_color);
                }

                if (flipped) {
                    if (iFacePosition % 2 == 0) {
                        g.setColor(F_color);
                    }
                    if (iFacePosition % 2 == 1) {
                        g.setColor(B_color);
                    }
                }

                //This is the end of deciding the color of SubFace when drawing a folded figure

                //Find the coordinates (on the PC display) of the vertices of the im-th SubFace polygon when drawing a fold-up diagram.

                for (int i = 1; i <= subFace_figure.getPointsCount(im) - 1; i++) {
                    t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, i)));
                    t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, i)));
                    t1.set(camera.object2TV(t0));
                    x[i] = gx(t1.getX());
                    y[i] = gy(t1.getY());
                }

                t0.setX(subFace_figure.getPointX(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t0.setY(subFace_figure.getPointY(subFace_figure.getPointId(im, subFace_figure.getPointsCount(im))));
                t1.set(camera.object2TV(t0));
                x[0] = gx(t1.getX());
                y[0] = gy(t1.getY());

                //This is the end of finding the coordinates (on the PC display) of the vertices of the im-th SubFace polygon when drawing a fold-up diagram.

                g2.fill(new java.awt.Polygon(x, y, subFace_figure.getPointsCount(im)));
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
                    origami.crease_pattern.element.Point b_begin = new origami.crease_pattern.element.Point(subFace_figure.getBeginX(lineId), subFace_figure.getBeginY(lineId));
                    origami.crease_pattern.element.Point b_end = new origami.crease_pattern.element.Point(subFace_figure.getEndX(lineId), subFace_figure.getEndY(lineId));
                    double b_length = b_begin.distance(b_end);

                    //棒と直交するベクトル
                    double o_btx = -(subFace_figure.getBeginY(lineId) - subFace_figure.getEndY(lineId)) * 10.0 / b_length;//棒と直交するxベクトル
                    double o_bty = (subFace_figure.getBeginX(lineId) - subFace_figure.getEndX(lineId)) * 10.0 / b_length;//棒と直交するyベクトル

                    //棒の中点
                    double o_bmx, o_bmy;
                    double t_bmx, t_bmy;

                    o_bmx = (subFace_figure.getBeginX(lineId) + subFace_figure.getEndX(lineId)) / 2.0;
                    o_bmy = (subFace_figure.getBeginY(lineId) + subFace_figure.getEndY(lineId)) / 2.0;

                    t0.setX(o_bmx);
                    t0.setY(o_bmy);
                    t1.set(camera.object2TV(t0));
                    t_bmx = t1.getX();
                    t_bmy = t1.getY();

                    //棒の中点を通る直交線上の点
                    double o_bmtx, o_bmty;
                    double t_bmtx, t_bmty;

                    //A point on the orthogonal line that passes through the midpoint of the bar
                    o_bmtx = o_bmx + o_btx;
                    o_bmty = o_bmy + o_bty;

                    if (subFace_figure.inside(new origami.crease_pattern.element.Point(o_bmx + Epsilon.UNKNOWN_001 * o_btx, o_bmy + Epsilon.UNKNOWN_001 * o_bty), im) != origami.crease_pattern.element.Polygon.Intersection.OUTSIDE) {//0=外部、　1=境界、　2=内部
                        t0.setX(o_bmtx);
                        t0.setY(o_bmty);
                        t1.set(camera.object2TV(t0));
                        t_bmtx = t1.getX();
                        t_bmty = t1.getY();

                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId));
                        t0.setY(subFace_figure.getBeginY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getX();
                        yd[0] = t1.getY();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId) + o_btx);
                        t0.setY(subFace_figure.getBeginY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getX();
                        yd[1] = t1.getY();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setX(subFace_figure.getEndX(lineId) + o_btx);
                        t0.setY(subFace_figure.getEndY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getX();
                        yd[2] = t1.getY();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setX(subFace_figure.getEndX(lineId));
                        t0.setY(subFace_figure.getEndY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getX();
                        yd[3] = t1.getY();
                        x[3] = (int) xd[3];
                        y[3] = (int) yd[3];

                        g2.setPaint(new GradientPaint((float) t_bmx, (float) t_bmy, new Color(0, 0, 0, 50), (float) t_bmtx, (float) t_bmty, new Color(0, 0, 0, 0)));

                        g2.fill(new java.awt.Polygon(x, y, 4));

                    }
                    //----------------------------------棒と直交するxベクトルの向きを変えて影を描画
                    o_btx = -o_btx;//棒と直交するxベクトル
                    o_bty = -o_bty;//棒と直交するyベクトル

                    //-----------------------------------------------
                    //棒の中点を通る直交線上の点
                    o_bmtx = o_bmx + o_btx;
                    o_bmty = o_bmy + o_bty;

                    if (subFace_figure.inside(new origami.crease_pattern.element.Point(o_bmx + Epsilon.UNKNOWN_001 * o_btx, o_bmy + Epsilon.UNKNOWN_001 * o_bty), im) != origami.crease_pattern.element.Polygon.Intersection.OUTSIDE) {//0=外部、　1=境界、　2=内部

                        t0.setX(o_bmtx);
                        t0.setY(o_bmty);
                        t1.set(camera.object2TV(t0));

                        //影の長方形

                        // ---------- [0] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId));
                        t0.setY(subFace_figure.getBeginY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[0] = t1.getX();
                        yd[0] = t1.getY();
                        x[0] = (int) xd[0];
                        y[0] = (int) yd[0];

                        // ---------- [1] ----------------
                        t0.setX(subFace_figure.getBeginX(lineId) + o_btx);
                        t0.setY(subFace_figure.getBeginY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[1] = t1.getX();
                        yd[1] = t1.getY();
                        x[1] = (int) xd[1];
                        y[1] = (int) yd[1];

                        // ---------- [2] ----------------
                        t0.setX(subFace_figure.getEndX(lineId) + o_btx);
                        t0.setY(subFace_figure.getEndY(lineId) + o_bty);
                        t1.set(camera.object2TV(t0));
                        xd[2] = t1.getX();
                        yd[2] = t1.getY();
                        x[2] = (int) xd[2];
                        y[2] = (int) yd[2];

                        // ---------- [3] ----------------
                        t0.setX(subFace_figure.getEndX(lineId));
                        t0.setY(subFace_figure.getEndY(lineId));
                        t1.set(camera.object2TV(t0));
                        xd[3] = t1.getX();
                        yd[3] = t1.getY();
                        x[3] = (int) xd[3];
                        y[3] = (int) yd[3];


                        //g2.setPaint( new GradientPaint( (float)t_bmx, (float)t_bmy, new Color(0,0,0,50),     (float)t_bmtx, (float)t_bmty,  new Color(0,0,0,0)  ));
                        g2.setPaint(new GradientPaint((float) xd[0], (float) yd[0], new Color(0, 0, 0, 50), (float) xd[1], (float) yd[1], new Color(0, 0, 0, 0)));
                        g2.fill(new java.awt.Polygon(x, y, 4));
                    }
                }
            }
        }//影をつけるは、ここで終わり

        //棒を描く-----------------------------------------------------------------------------------------


        if (antiAlias) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//アンチェイリアス　オン
            BasicStroke BStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オフ
            BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g2.setStroke(BStroke);//線の太さや線の末端の形状
        }

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
                s_ob.set(subFace_figure.getBeginX(ib), subFace_figure.getBeginY(ib), subFace_figure.getEndX(ib), subFace_figure.getEndY(ib));
                s_tv.set(camera.object2TV(s_ob));
                g.drawLine(gx(s_tv.determineAX()), gy(s_tv.determineAY()), gx(s_tv.determineBX()), gy(s_tv.determineBY())); //直線
            }
        }
    }

    //---------------------------------------------------------
    public void draw_cross_with_camera(Graphics g, boolean selected, int index) {
        //Draw the center of the camera with a cross
        Point point = camera.object2TV(camera.getCameraPosition());
        DrawingUtil.cross(g, point, 5.0, 2.0, LineColor.ORANGE_4);

        if (selected) {
            g.setColor(Colors.get(new Color(200, 50, 255, 90)));
            g.fillOval(gx(point.getX()) - 25, gy(point.getY()) - 25, 50, 50); //円
        }

        if (displayNumbers) {
            Font f = g.getFont();
            g.setFont(new Font(f.getName(), f.getStyle(), 50));
            g.setColor(Color.orange);
            g.drawString(String.valueOf(index), gx(point.getX()) + 25, gy(point.getY()) + 25);
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

    public static void setStaticData(ApplicationModel applicationModel) {
        displaySsi = applicationModel.getDisplaySelfIntersection(); 
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigureModel.setAntiAlias(antiAlias);
        foldedFigureModel.setDisplayShadows(displayShadows);
    }
}
