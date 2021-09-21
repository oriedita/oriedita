package origami_editor.editor.drawing_worker;

import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.crease_pattern.element.*;
import origami_editor.editor.App;
import origami_editor.editor.LineStyle;
import origami_editor.editor.MouseMode;
import origami_editor.editor.Save;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing_worker.drawing_worker_toolbox.Drawing_Worker_Toolbox;
import origami_editor.editor.undo_box.HistoryState;
import origami_editor.graphic2d.grid.Grid;
import origami_editor.graphic2d.oritaoekaki.OritaDrawing;
import origami_editor.sortingbox.SortingBox;
import origami_editor.sortingbox.WeightedValue;
import origami_editor.tools.Camera;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class DrawingWorker {
    // ------------
    final int check4ColorTransparencyIncrement = 10;
    private final LineSegmentSet lineSegmentSet = new LineSegmentSet();    //Instantiation of basic branch structure
    public FoldLineSet foldLineSet = new FoldLineSet();    //Store polygonal lines
    public FoldLineSet voronoiLineSet = new FoldLineSet();    //Store Voronoi diagram lines
    public Grid grid = new Grid();
    public int i_drawing_stage;//Stores information about the stage of the procedure for drawing a polygonal line
    public int i_candidate_stage;//Stores information about which candidate for the procedure to draw a polygonal line
    public Polygon operationFrameBox = new Polygon(4);    //Instantiation of selection box (TV coordinates)
    public boolean i_O_F_C = false;//Input status of a line segment representing the outer circumference when checking the outer circumference. 0 is input not completed, 1 is input completed (line segment is a closed polygon)
    int pointSize = 1;
    LineColor lineColor;//Line segment color
    LineColor auxLineColor = LineColor.ORANGE_4;//Auxiliary line color
    boolean gridInputAssist = false;//1 if you use the input assist function for fine grid display, 0 if you do not use it
    Color customCircleColor;//Stores custom colors for circles and auxiliary hot lines
    HistoryState historyState = new HistoryState();
    HistoryState auxHistoryState = new HistoryState();
    Point closest_point = new Point(100000.0, 100000.0); //マウス最寄の点。get_moyori_ten(Ten p)で求める。
    LineSegment closest_lineSegment = new LineSegment(100000.0, 100000.0, 100000.0, 100000.1); //マウス最寄の線分
    LineSegment closest_step_lineSegment = new LineSegment(100000.0, 100000.0, 100000.0, 100000.1); //マウス最寄のstep線分(線分追加のための準備をするための線分)。なお、ここで宣言する必要はないので、どこで宣言すべきか要検討20161113
    Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse
    FoldLineAdditionalInputMode i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;//= 0 is polygonal line input = 1 is auxiliary line input mode (when inputting a line segment, these two). When deleting a line segment, the value becomes as follows. = 0 is the deletion of the polygonal line, = 1 is the deletion of the auxiliary picture line, = 2 is the deletion of the black line, = 3 is the deletion of the auxiliary live line, = 4 is the folding line, the auxiliary live line and the auxiliary picture line.
    FoldLineSet auxLines = new FoldLineSet();    //Store auxiliary lines
    Drawing_Worker_Toolbox e_s_dougubako = new Drawing_Worker_Toolbox(foldLineSet);
    int id_angle_system = 8;//180 / id_angle_system represents the angular system. For example, if id_angle_system = 3, 180/3 = 60 degrees, if id_angle_system = 5, 180/5 = 36 degrees
    double d_angle_system;//d_angle_system=180.0/(double)id_angle_system
    double angle;
    int foldLineDividingNumber = 1;
    double internalDivisionRatio_s;
    double internalDivisionRatio_t;
    double d_restricted_angle_1;
    double d_restricted_angle_2;
    double d_restricted_angle_3;
    int numPolygonCorners = 5;
    double selectionDistance = 50.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Value for determining whether an input point is close to an existing point or line segment
    int i_circle_drawing_stage;//Stores information about which stage of the circle drawing procedure
    LineSegment[] line_step = new LineSegment[1024];//Used for temporary display when drawing. Do not actually use line_step [0], but use it from line_step [1].
    Circle[] circle_step = new Circle[1024];//Used for temporary display when drawing. circle_step [0] is not actually used, but is used from circle_step [1].
    LineSegment[] line_candidate = new LineSegment[16];//Used for displaying selection candidates when drawing. line_candidate [0] is not actually used, it is used from line_candidate [1].
    Circle[] circle_candidate = new Circle[16];//Used for displaying selection candidates when drawing. circle_candidate [0] is not actually used, it is used from circle_candidate [1].
    String text_cp_setumei;
    String s_title; //Used to hold the title that appears at the top of the frame
    Camera camera = new Camera();
    boolean check1 = false;//=0 check1を実施しない、1=実施する　　
    boolean check2 = false;//=0 check2を実施しない、1=実施する　
    boolean check3 = false;//=0 check3を実施しない、1=実施する　
    boolean check4 = false;//=0 check4を実施しない、1=実施する　
    //---------------------------------
    int check4ColorTransparency = 100;
    App app;
    LineColor icol_temp = LineColor.BLACK_0;//Used for temporary memory of color specification
    //mouseMode==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
    Point operationFrame_p1 = new Point();//TV座標
    Point operationFrame_p2 = new Point();//TV座標
    Point operationFrame_p3 = new Point();//TV座標
    Point operationFrame_p4 = new Point();//TV座標
    OperationFrameMode operationFrameMode = OperationFrameMode.NONE_0;// = 1 Create a new selection box. = 2 Move points. 3 Move the sides. 4 Move the selection box.
    Point p = new Point();
    ArrayList<LineSegment> lineSegment_voronoi_onePoint = new ArrayList<>(); //Line segment around one point in Voronoi diagram
    // ****************************************************************************************************************************************
    // **************　Variable definition so far　****************************************************************************************************
    // ****************************************************************************************************************************************
    // ------------------------------------------------------------------------------------------------------------
    int i_mouse_modeA_62_point_overlapping;//Newly added p does not overlap with previously added Point = 0, overlaps = 1
    SortingBox<LineSegment> entyou_kouho_nbox = new SortingBox<>();
    // Sub-operation mode for MouseMode.FOLDABLE_LINE_DRAW_71, either DRAW_CREASE_FREE_1, or VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38
    MouseMode operationModeFor_FOLDABLE_LINE_DRAW_71 = MouseMode.UNUSED_0;
    boolean operationModeChangeable = false;//Operation mode changeable. 0 is impossible, 1 is possible.
    Point moyori_point_memo = new Point();
    Point p19_1 = new Point();
    //--------------------------------------------
    CanvasModel.SelectionOperationMode i_select_mode = CanvasModel.SelectionOperationMode.NORMAL_0;//=0は通常のセレクト操作
    //30 30 30 30 30 30 30 30 30 30 30 30 除け_線_変換
    LineSegment lineSegment_ADVANCE_CREASE_TYPE_30;
    enum FourPointStep {
        STEP_0,
        STEP_1,
        STEP_2,
    }
    FourPointStep i_step_for_move_4p = FourPointStep.STEP_0;
    //39 39 39 39 39 39 39    mouseMode==39　;折り畳み可能線入力  qqqqqqqqq
    FourPointStep i_step_for_copy_4p = FourPointStep.STEP_0;//i_step_for_copy_4p=2の場合は、step線が1本だけになっていて、次の操作で入力折線が確定する状態
    boolean i_takakukei_kansei = false;//多角形が完成したら1、未完成なら0

    public DrawingWorker(double r0, App app0) {  //コンストラクタ
        app = app0;

        lineColor = LineColor.BLACK_0;

        for (int i = 0; i <= 1024 - 1; i++) {
            line_step[i] = new LineSegment();
        }
        for (int i = 0; i <= 1024 - 1; i++) {
            circle_step[i] = new Circle();
        }

        for (int i = 0; i <= 16 - 1; i++) {
            line_candidate[i] = new LineSegment();
        }
        for (int i = 0; i <= 16 - 1; i++) {
            circle_candidate[i] = new Circle();
        }

        text_cp_setumei = "1/";
        s_title = "no title";

        reset();
    }

    public void setGridConfigurationData(GridModel gridModel) {
        grid.setGridConfigurationData(gridModel);
        text_cp_setumei = "1/" + grid.getGridSize();
        calculateDecisionWidth();

        app.repaintCanvas();
    }

    public void reset() {
        pointSize = 1;
        foldLineSet.reset();
        auxLines.reset();

        camera.reset();
        i_drawing_stage = 0;
        i_circle_drawing_stage = 0;
    }

    public void initialize() {
        //Enter the paper square (start)
        foldLineSet.addLine(-200.0, -200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(-200.0, -200.0, 200.0, -200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, 200.0, -200.0, LineColor.BLACK_0);
        //Enter the paper square (end)
    }

    public void Memo_jyouhou_toridasi(Save memo1) {
        app.canvas.creasePatternCamera.setCamera(memo1.getCreasePatternCamera());

        app.canvasModel.set(memo1.getCanvasModel());

        app.gridModel.set(memo1.getGridModel());

        app.foldedFigureModel.setFrontColor(memo1.getFoldedFigureModel().getFrontColor());
        app.foldedFigureModel.setBackColor(memo1.getFoldedFigureModel().getBackColor());
        app.foldedFigureModel.setLineColor(memo1.getFoldedFigureModel().getLineColor());
    }

    public String setMemo_for_redo_undo(Save save) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        return foldLineSet.setSave(save);
    }

    public void setSave_for_reading(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<For reading data
        Memo_jyouhou_toridasi(memo1);
        foldLineSet.setSave(memo1);
        auxLines.setAuxSave(memo1);
    }

    public void setSave_for_reading_tuika(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<For reading data
        double addx, addy;

        FoldLineSet tempFoldLineSet = new FoldLineSet();    //追加された折線だけ取り出すために使う
        tempFoldLineSet.setSave(memo1);//追加された折線だけ取り出してori_s_tempを作る
        addx = foldLineSet.get_x_max() + 100.0 - tempFoldLineSet.get_x_min();
        addy = foldLineSet.get_y_max() - tempFoldLineSet.get_y_max();

        tempFoldLineSet.move(addx, addy);//全体を移動する

        int total_old = foldLineSet.getTotal();
        foldLineSet.addSave(tempFoldLineSet.getSave());
        int total_new = foldLineSet.getTotal();
        foldLineSet.intersect_divide(1, total_old, total_old + 1, total_new);

        foldLineSet.unselect_all();
        record();
    }

    public void setAuxMemo(Save memo1) {
        auxLines.setAuxSave(memo1);
    }

    public void setCamera(Camera cam0) {
        camera.setCamera(cam0);

        calculateDecisionWidth();
    }

    public void setCustomCircleColor(Color c0) {
        customCircleColor = c0;
    }

    public void allMountainValleyChange() {
        foldLineSet.allMountainValleyChange();
    }

    public void branch_trim(double r) {
        foldLineSet.branch_trim(r);
    }

    public LineSegmentSet get() {
        lineSegmentSet.setSave(foldLineSet.getSave());
        return lineSegmentSet;
    }

    public LineSegmentSet getForFolding() {
        lineSegmentSet.setSave(foldLineSet.getMemo_for_folding());
        return lineSegmentSet;
    }

    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    public int getFoldLineTotalForSelectFolding() {
        return foldLineSet.getFoldLineTotalForSelectFolding();
    }

    public LineSegmentSet getForSelectFolding() {//selectした折線で折り畳み推定をする。
        lineSegmentSet.setSave(foldLineSet.getSaveForSelectFolding());
        return lineSegmentSet;
    }

    //--------------------------------------------
    public void setPointSize(int i0) {
        pointSize = i0;
    }

    public void calculateDecisionWidth() {
        selectionDistance = grid.getGridWidth() / 4.0;
        if (camera.getCameraZoomX() * selectionDistance < 10.0) {
            selectionDistance = 10.0 / camera.getCameraZoomX();
        }
    }

    public int getTotal() {
        return foldLineSet.getTotal();
    }

    public Save getSave(String title) {
        Save save_temp = new Save();
        save_temp.set(foldLineSet.getSave(title));

        saveAdditionalInformation(save_temp);
        return save_temp;
    }

    public Save h_getSave() {
        return auxLines.h_getSave();
    }

    public Save getSave_for_export() {
        Save save = new Save();
        save.set(foldLineSet.getSave());
        save.add(auxLines.h_getSave());
        saveAdditionalInformation(save);

        return save;
    }

    public void saveAdditionalInformation(Save memo1) {
        Camera camera = new Camera();
        camera.setCamera(this.camera);
        memo1.setCreasePatternCamera(camera);

        memo1.setCanvasModel(app.canvasModel);
        memo1.setGridModel(app.gridModel);

        memo1.setFoldedFigureModel(app.foldedFigureModel);
    }

    public void setColor(LineColor i) {
        lineColor = i;
    }

    public void point_removal() {
        foldLineSet.point_removal();
    }

    public void overlapping_line_removal() {
        foldLineSet.overlapping_line_removal();
    }

    public String undo() {
        s_title = setMemo_for_redo_undo(historyState.undo());

        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

        return s_title;
    }

    public String redo() {
        s_title = setMemo_for_redo_undo(historyState.redo());

        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

        return s_title;
    }

    public void setTitle(String s_title0) {
        s_title = s_title0;
    }

    public void record() {
        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

        historyState.record(getSave(s_title));
    }

    public void auxUndo() {
        setAuxMemo(auxHistoryState.undo());
    }

    public void auxRedo() {
        setAuxMemo(auxHistoryState.redo());
    }

    public void auxRecord() {
        auxHistoryState.record(h_getSave());
    }

    //--------------------------------------------------------------------------------------
    //Mouse operation----------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------
    //Drawing the basic branch
    //------------------------------------------------------------------------------
    public void drawWithCamera(Graphics g, boolean displayComments, boolean displayCpLines, boolean displayAuxLines, boolean displayAuxLiveLines, float lineWidth, LineStyle lineStyle, float f_h_WireframeLineWidth, int p0x_max, int p0y_max, boolean i_mejirusi_display) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        Graphics2D g2 = (Graphics2D) g;

        LineSegment s_tv = new LineSegment();
        Point a = new Point();
        Point b = new Point();

        // ------------------------------------------------------

        //Drawing grid lines
        grid.draw(g, camera, p0x_max, p0y_max, gridInputAssist);

        BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//Line thickness and shape of the end of the line

        //Drawing auxiliary strokes (non-interfering with polygonal lines)
        if (displayAuxLiveLines) {
            g2.setStroke(new BasicStroke(f_h_WireframeLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//Line thickness and shape of the end of the line
            for (int i = 1; i <= auxLines.getTotal(); i++) {
                g_setColor(g, auxLines.getColor(i));

                s_tv.set(camera.object2TV(auxLines.get(i)));
                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

                if (lineWidth < 2.0f) {//Draw a square at the vertex
                    g.setColor(Color.black);
                    int i_width = pointSize;
                    g.fillRect((int) a.getX() - i_width, (int) a.getY() - i_width, 2 * i_width + 1, 2 * i_width + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                    g.fillRect((int) b.getX() - i_width, (int) b.getY() - i_width, 2 * i_width + 1, 2 * i_width + 1); //正方形を描く
                }

                if (lineWidth >= 2.0f) {//  Thick line
                    g2.setStroke(new BasicStroke(1.0f + f_h_WireframeLineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

                    if (pointSize != 0) {
                        double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
                    }

                    g2.setStroke(new BasicStroke(f_h_WireframeLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

                }
            }
        }

        //check結果の表示

        g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

        //Check1Senbには0番目からsize()-1番目までデータが入っている
        if (check1) {
            for (LineSegment s_temp : foldLineSet.getCheck1LineSegments()) {
                OritaDrawing.pointingAt1(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }

        if (check2) {
            for (LineSegment s_temp : foldLineSet.getCheck2LineSegments()) {
                OritaDrawing.pointingAt2(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }

        g2.setStroke(new BasicStroke(25.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定


        //Check4Senbには0番目からsize()-1番目までデータが入っている
        //System.out.println("foldLineSet.check4_size() = "+foldLineSet.check4_size());
        if (check4) {
            for (LineSegment s_temp : foldLineSet.getCheck4LineSegments()) {
                OritaDrawing.pointingAt4(g, camera.object2TV(s_temp), check4ColorTransparency);
            }
        }


        //Check3Senbには0番目からsize()-1番目までデータが入っている
        if (check3) {
            for (LineSegment s_temp : foldLineSet.getCheck3LineSegments()) {
                OritaDrawing.pointingAt3(g, camera.object2TV(s_temp), 7.0, 3.0, 1);
            }
        }

        //Draw the center of the camera with a cross
        if (i_mejirusi_display) {
            OritaDrawing.cross(g, camera.object2TV(camera.getCameraPosition()), 5.0, 2.0, LineColor.CYAN_3);
        }

        //円を描く　
        if (displayAuxLines) {
            for (Circle e_temp : foldLineSet.getCircles()) {
                double d_width;

                a.set(camera.object2TV(e_temp.getCenter()));//この場合のaは描画座標系での円の中心の位置

                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状


                if (e_temp.getCustomized() == 0) {
                    g_setColor(g, e_temp.getColor());
                } else if (e_temp.getCustomized() == 1) {
                    g.setColor(e_temp.getCustomizedColor());
                }


                //円周の描画
                d_width = e_temp.getRadius() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }


        //円の中心の描画
        if (displayAuxLines) {
            for (Circle e_temp : foldLineSet.getCircles()) {
                double d_width;

                a.set(camera.object2TV(e_temp.getCenter()));//この場合のaは描画座標系での円の中心の位置

                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                g.setColor(new Color(0, 255, 255, 255));

                //円の中心の描画
                if (lineWidth < 2.0f) {//中心の黒い正方形を描く
                    g.setColor(Color.black);
                    int i_width = pointSize;
                    g.fillRect((int) a.getX() - i_width, (int) a.getY() - i_width, 2 * i_width + 1, 2 * i_width + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                }

                if (lineWidth >= 2.0f) {//  太線指定時の中心を示す黒い小円を描く
                    g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                    if (pointSize != 0) {
                        d_width = (double) lineWidth / 2.0 + (double) pointSize;


                        g.setColor(Color.white);
                        g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                        g.setColor(Color.black);
                        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
                    }
                }
            }
        }

        //selectの描画
        g2.setStroke(new BasicStroke(lineWidth * 2.0f + 2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            if (foldLineSet.get_select(i) == 2) {
                g.setColor(Color.green);

                s_tv.set(camera.object2TV(foldLineSet.get(i)));

                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            }
        }

        //展開図の描画 補助活線のみ
        if (displayAuxLines) {
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                if (foldLineSet.getColor(i) == LineColor.CYAN_3) {

                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

                    if (foldLineSet.getLineCustomized(i) == 0) {
                        g_setColor(g, foldLineSet.getColor(i));
                    } else if (foldLineSet.getLineCustomized(i) == 1) {
                        g.setColor(foldLineSet.getLineCustomizedColor(i));
                    }

                    s_tv.set(camera.object2TV(foldLineSet.get(i)));
                    a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                    b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                    g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

                    if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
                        g.setColor(Color.black);
                        int i_width = pointSize;
                        g.fillRect((int) a.getX() - i_width, (int) a.getY() - i_width, 2 * i_width + 1, 2 * i_width + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                        g.fillRect((int) b.getX() - i_width, (int) b.getY() - i_width, 2 * i_width + 1, 2 * i_width + 1); //正方形を描く
                    }

                    if (lineWidth >= 2.0f) {//  太線
                        g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (pointSize != 0) {
                            double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));


                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
                        }
                    }
                }
            }

        }

        //展開図の描画  補助活線以外の折線
        if (displayCpLines) {

            g.setColor(Color.black);

            float[] dash_M1 = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
            float[] dash_M2 = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
            float[] dash_V = {8.0f, 8.0f};//破線

            g.setColor(Color.black);
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineSegment s = foldLineSet.get(i);
                if (s.getColor() != LineColor.CYAN_3) {
                    switch (lineStyle) {
                        case COLOR:
                            g_setColor(g, s.getColor());
                            g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                            break;
                        case COLOR_AND_SHAPE:
                            g_setColor(g, s.getColor());
                            if (s.getColor() == LineColor.BLACK_0) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            }//基本指定A　　線の太さや線の末端の形状
                            if (s.getColor() == LineColor.RED_1) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                            }//一点鎖線//線の太さや線の末端の形状
                            if (s.getColor() == LineColor.BLUE_2) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                            }//破線//線の太さや線の末端の形状
                            break;
                        case BLACK_ONE_DOT:
                            if (s.getColor() == LineColor.BLACK_0) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            }//基本指定A　　線の太さや線の末端の形状
                            if (s.getColor() == LineColor.RED_1) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                            }//一点鎖線//線の太さや線の末端の形状
                            if (s.getColor() == LineColor.BLUE_2) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                            }//破線//線の太さや線の末端の形状
                            break;
                        case BLACK_TWO_DOT:
                            if (s.getColor() == LineColor.BLACK_0) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            }//基本指定A　　線の太さや線の末端の形状
                            if (s.getColor() == LineColor.RED_1) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M2, 0.0f));
                            }//二点鎖線//線の太さや線の末端の形状
                            if (s.getColor() == LineColor.BLUE_2) {
                                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                            }//破線//線の太さや線の末端の形状
                            break;
                    }

                    s_tv.set(camera.object2TV(s));
                    a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                    b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                    g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

                    if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
                        g.setColor(Color.black);
                        int i_width = pointSize;
                        g.fillRect((int) a.getX() - i_width, (int) a.getY() - i_width, 2 * i_width + 1, 2 * i_width + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
                        g.fillRect((int) b.getX() - i_width, (int) b.getY() - i_width, 2 * i_width + 1, 2 * i_width + 1); //正方形を描く
                    }


                    if (lineWidth >= 2.0f) {//  太線
                        g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
                        if (pointSize != 0) {
                            double d_width = (double) lineWidth / 2.0 + (double) pointSize;


                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                            g.setColor(Color.white);
                            g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                            g.setColor(Color.black);
                            g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
                        }

                    }
                }
            }
        }

        //mouseMode==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
        if (app.mouseMode == MouseMode.OPERATION_FRAME_CREATE_61) {
            Point p1 = new Point();
            p1.set(camera.TV2object(operationFrame_p1));
            Point p2 = new Point();
            p2.set(camera.TV2object(operationFrame_p2));
            Point p3 = new Point();
            p3.set(camera.TV2object(operationFrame_p3));
            Point p4 = new Point();
            p4.set(camera.TV2object(operationFrame_p4));

            line_step[1].set(p1, p2); //縦線
            line_step[2].set(p2, p3); //横線
            line_step[3].set(p3, p4); //縦線
            line_step[4].set(p4, p1); //横線

            line_step[1].setColor(LineColor.GREEN_6);
            line_step[2].setColor(LineColor.GREEN_6);
            line_step[3].setColor(LineColor.GREEN_6);
            line_step[4].setColor(LineColor.GREEN_6);
        }

        //線分入力時の一時的なs_step線分を描く　

        if ((app.mouseMode == MouseMode.OPERATION_FRAME_CREATE_61) && (i_drawing_stage != 4)) {
        } else {
            for (int i = 1; i <= i_drawing_stage; i++) {
                g_setColor(g, line_step[i].getColor());
                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

                s_tv.set(camera.object2TV(line_step[i]));
                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため


                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
                int i_width_nyuiiryokuji = 3;
                if (gridInputAssist) {
                    i_width_nyuiiryokuji = 2;
                }

                if (line_step[i].getActive() == LineSegment.ActiveState.ACTIVE_A_1) {
                    g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                }
                if (line_step[i].getActive() == LineSegment.ActiveState.ACTIVE_B_2) {
                    g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                }
                if (line_step[i].getActive() == LineSegment.ActiveState.ACTIVE_BOTH_3) {
                    g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                    g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                }
            }
        }
        //候補入力時の候補を描く//System.out.println("_");
        g2.setStroke(new BasicStroke(lineWidth + 0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A


        for (int i = 1; i <= i_candidate_stage; i++) {
            g_setColor(g, line_candidate[i].getColor());

            s_tv.set(camera.object2TV(line_candidate[i]));
            a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
            b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            int i_width = pointSize + 5;

            if (line_candidate[i].getActive() == LineSegment.ActiveState.ACTIVE_A_1) {
                g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線
            }
            if (line_candidate[i].getActive() == LineSegment.ActiveState.ACTIVE_B_2) {
                g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
            }
            if (line_candidate[i].getActive() == LineSegment.ActiveState.ACTIVE_BOTH_3) {
                g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線

                g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
            }
        }

        g.setColor(Color.black);

        //円入力時の一時的な線分を描く　
        for (int i = 1; i <= i_circle_drawing_stage; i++) {
            g_setColor(g, circle_step[i].getColor());
            a.set(camera.object2TV(circle_step[i].getCenter()));//この場合のs_tvは描画座標系での円の中心の位置
            a.set(a.getX() + 0.000001, a.getY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            double d_width = circle_step[i].getRadius() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。

            g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
        }

        g.setColor(Color.black);

        if (displayComments) {
            g.drawString(text_cp_setumei, 10, 55);
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------
    public void g_setColor(Graphics g, LineColor i) {
        switch (i) {
            case BLACK_0:
                g.setColor(Color.black);
                break;
            case RED_1:
                g.setColor(Color.red);
                break;
            case BLUE_2:
                g.setColor(Color.blue);
                break;
            case CYAN_3:
                g.setColor(new Color(100, 200, 200));
                break;
            case ORANGE_4:
                g.setColor(Color.orange);
                break;
            case MAGENTA_5:
                g.setColor(Color.magenta);
                break;
            case GREEN_6:
                g.setColor(Color.green);
                break;
            case YELLOW_7:
                g.setColor(Color.yellow);
                break;
            case PURPLE_8:
                g.setColor(new Color(210, 0, 255));
                break;
        }
    }


    //動作モデル00a--------------------------------------------------------------------------------------------------------
    //マウスクリック（マウスの近くの既成点を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（マウスの近くの既成点を選択）してから目的の処理をする雛形セット

    public void set_i_circle_drawing_stage(int i) {
        i_circle_drawing_stage = i;
    }

    public void set_id_angle_system(int i) {
        id_angle_system = i;
    }

    // ------------------------------------
    public void setGridInputAssist(boolean i) {
        gridInputAssist = i;

        if (!gridInputAssist) {
            line_candidate[1].deactivate();
        }
    }

    public void addCircle(Circle e0) {
        addCircle(e0.getX(), e0.getY(), e0.getRadius(), e0.getColor());
    }


    //動作モデル00b--------------------------------------------------------------------------------------------------------
    //マウスクリック（近くの既成点かマウス位置を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（近くの既成点かマウス位置を選択）してから目的の処理をする雛形セット

    public void addCircle(Point t0, double dr, LineColor ic) {
        addCircle(t0.getX(), t0.getY(), dr, ic);
    }

    public void addCircle(double dx, double dy, double dr, LineColor ic) {
        foldLineSet.addCircle(dx, dy, dr, ic);

        int imin = 0;
        int imax = foldLineSet.numCircles() - 2;
        int jmin = foldLineSet.numCircles() - 1;
        int jmax = foldLineSet.numCircles() - 1;

        foldLineSet.circle_circle_intersection(imin, imax, jmin, jmax);
        foldLineSet.lineSegment_circle_intersection(1, foldLineSet.getTotal(), jmin, jmax);

    }

    //--------------------------
    public void addLineSegment_auxiliary(LineSegment s0) {
        auxLines.addLine(s0);
    }

    //--------------------------------------------
    public void addLineSegment(LineSegment s0) {//0 = No change, 1 = Color change only, 2 = Line segment added
        foldLineSet.addLine(s0);//Just add the information of s0 to the end of senbun of foldLineSet
        int total_old = foldLineSet.getTotal();
        foldLineSet.lineSegment_circle_intersection(foldLineSet.getTotal(), foldLineSet.getTotal(), 0, foldLineSet.numCircles() - 1);

        foldLineSet.intersect_divide(1, total_old - 1, total_old, total_old);
    }


//--------------------------------------------
//28 28 28 28 28 28 28 28  mouseMode==28線分内分入力
    //動作概要
    //mouseMode==1と線分内分以外は同じ

    public Point getClosestPoint(Point t0) {
        // When dividing paper 1/1 Only the end point of the folding line is the reference point. The grid point never becomes the reference point.
        // When dividing paper from 1/2 to 1/512 The end point of the polygonal line and the grid point in the paper frame (-200.0, -200.0 _ 200.0, 200.0) are the reference points.
        Point t1 = new Point(); //End point of the polygonal line
        Point t3 = new Point(); //Center of circle

        t1.set(foldLineSet.closestPoint(t0)); // foldLineSet.closestPoint returns (100000.0,100000.0) if there is no close point

        t3.set(foldLineSet.closestCenter(t0)); // foldLineSet.closestCenter returns (100000.0,100000.0) if there is no close point

        if (t0.distanceSquared(t1) > t0.distanceSquared(t3)) {
            t1.set(t3);
        }

        if (grid.getBaseState() == Grid.State.HIDDEN) {
            return t1;
        }

        if (t0.distanceSquared(t1) > t0.distanceSquared(grid.closestGridPoint(t0))) {
            return grid.closestGridPoint(t0);
        }

        return t1;
    }

    //------------------------------
    public LineSegment getClosestLineSegment(Point t0) {
        return foldLineSet.closestLineSegment(t0);
    }

    //------------------------------------------------------
    public LineSegment get_moyori_step_lineSegment(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;//Senbun s1 =new Senbun(100000.0,100000.0,100000.0,100000.1);
        for (int i = imin; i <= imax; i++) {
            double sk = OritaCalc.distance_lineSegment(t0, line_step[i]);
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }

        return line_step[minrid];
    }


//1 1 1 1 1 1 01 01 01 01 01 11111111111 mouseMode==1線分入力 111111111111111111111111111111111
    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    //------------------------------
    public Circle getClosestCircleMidpoint(Point t0) {
        return foldLineSet.closestCircleMidpoint(t0);
    }

    public void set_s_step_iactive(LineSegment.ActiveState ia) {
        for (int i = 0; i < 1024; i++) {
            line_step[i].setActive(ia);
        }
    }

    //動作モデル001--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_001(Point p0, LineColor i_c) {//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点が候補点となる。近くに既成の点が無いときは候補点無しなので候補点の表示も無し。
        if (gridInputAssist) {
            line_candidate[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            i_candidate_stage = 0;
            p.set(camera.TV2object(p0));
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < selectionDistance) {
                i_candidate_stage = 1;
                line_candidate[1].set(closest_point, closest_point);
                line_candidate[1].setColor(i_c);
            }
        }
    }

    //動作モデル002--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_002(Point p0, LineColor i_c) {//Display candidate points that can be selected with the mouse. If there is an established point nearby, that point is the candidate point, and if not, the mouse position itself is the candidate point.
        if (gridInputAssist) {
            line_candidate[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));

            if (p.distance(closest_point) < selectionDistance) {
                line_candidate[1].set(closest_point, closest_point);
            } else {
                line_candidate[1].set(p, p);
            }

            line_candidate[1].setColor(i_c);
        }
    }

    //動作モデル003--------------------------------------------------------------------------------------------------------
    //マウス操作(マウスを動かしたとき)を行う関数
    public void mMoved_m_003(Point p0, LineColor i_c) {//マウスで選択できる候補点を表示する。常にマウスの位置自身が候補点となる。
        if (gridInputAssist) {
            //line_candidate[1].setiactive(3);
            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            line_candidate[1].set(p, p);

            line_candidate[1].setColor(i_c);
        }
    }

    //マウスを動かしたとき----------------------------------------------
    public void mMoved_m_00a(Point p0, LineColor i_c) {
        mMoved_m_001(p0, i_c);
    }//近い既存点のみ表示

    //マウスクリック----------------------------------------------------
    public void mPressed_m_00a(Point p0, LineColor i_c) {
        i_drawing_stage = 1;
        line_step[1].setActive(LineSegment.ActiveState.ACTIVE_B_2);
        //Ten p =new Ten();
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > selectionDistance) {
            i_drawing_stage = 0;
        }
        line_step[1].set(p, closest_point);
        line_step[1].setColor(i_c);
    }

    //マウスドラッグ---------------------------------------------------
    public void mDragged_m_00a(Point p0, LineColor i_c) {  //近い既存点のみ表示

        p.set(camera.TV2object(p0));
        line_step[1].setA(p);

        if (gridInputAssist) {
            i_candidate_stage = 0;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) < selectionDistance) {
                i_candidate_stage = 1;
                line_candidate[1].set(closest_point, closest_point);
                line_candidate[1].setColor(i_c);
                line_step[1].setA(line_candidate[1].getA());
            }
        }
    }

// ------------------------------------------

    //マウスを動かしたとき----------------------------------------------
    public void mMoved_m_00b(Point p0, LineColor i_c) {
        mMoved_m_002(p0, i_c);
    }//近くの既成点かマウス位置表示

    //マウスクリック----------------------------------------------------
    public void mPressed_m_00b(Point p0, LineColor i_c) {
        i_drawing_stage = 1;
        line_step[1].setActive(LineSegment.ActiveState.ACTIVE_B_2);
        p.set(camera.TV2object(p0));
        line_step[1].set(p, p);

        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) < selectionDistance) {
            line_step[1].set(p, closest_point);
        }

        line_step[1].setColor(i_c);
    }

    //マウスドラッグ---------------------------------------------------
    public void mDragged_m_00b(Point p0, LineColor i_c) {  //近くの既成点かマウス位置表示

        p.set(camera.TV2object(p0));
        line_step[1].setA(p);

        if (gridInputAssist) {
            closest_point.set(getClosestPoint(p));
            i_candidate_stage = 1;
            if (p.distance(closest_point) < selectionDistance) {
                line_candidate[1].set(closest_point, closest_point);
            } else {
                line_candidate[1].set(p, p);
            }
            line_candidate[1].setColor(i_c);
            line_step[1].setA(line_candidate[1].getA());
        }
    }

    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える
    public Point getGridPosition(Point p0) {
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        return new Point(grid.getPosition(closest_point));
    }

    // ------------------------------------------------------------------------------------------------------------
    int s_step_no_1_top_continue_no_point_no_number() {//line_step [i] returns the number of Point (length 0) from the beginning. Returns 0 if there are no dots
        int r_i = 0;
        int i_add = 1;
        for (int i = 1; i <= i_drawing_stage; i++) {
            if (line_step[i].getLength() > 0.00000001) {
                i_add = 0;
            }
            r_i = r_i + i_add;
        }
        return r_i;
    }

    public void voronoi_02_01(int tyuusinn_ten_bangou, LineSegment add_lineSegment) {
        //i_egaki_dankai番目のボロノイ頂点は　　line_step[i_egaki_dankai].geta()　　　

        //Organize the line segments to be added
        StraightLine add_straightLine = new StraightLine(add_lineSegment);

        int i_saisyo = lineSegment_voronoi_onePoint.size() - 1;
        for (int i = i_saisyo; i >= 0; i--) {
            //Organize existing line segments
            LineSegment existing_lineSegment = new LineSegment();
            existing_lineSegment.set(lineSegment_voronoi_onePoint.get(i));
            StraightLine existing_straightLine = new StraightLine(existing_lineSegment);

            //Fight the line segment to be added with the existing line segment

            OritaCalc.ParallelJudgement parallel = OritaCalc.parallel_judgement(add_straightLine, existing_straightLine, 0.0001);//0 = not parallel, 1 = parallel and 2 straight lines do not match, 2 = parallel and 2 straight lines match

            if (parallel == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//When the line segment to be added and the existing line segment are non-parallel
                Point intersection = new Point();
                intersection.set(OritaCalc.findIntersection(add_straightLine, existing_straightLine));

                if ((add_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) <= 0) &&
                        (add_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getB()) <= 0)) {
                    lineSegment_voronoi_onePoint.remove(i);
                } else if ((add_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) == 1) &&
                        (add_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getB()) == -1)) {
                    existing_lineSegment.set(existing_lineSegment.getA(), intersection);
                    if (existing_lineSegment.getLength() < 0.0000001) {
                        lineSegment_voronoi_onePoint.remove(i);
                    } else {
                        lineSegment_voronoi_onePoint.set(i, existing_lineSegment);
                    }
                } else if ((add_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) == -1) &&
                        (add_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getB()) == 1)) {
                    existing_lineSegment.set(intersection, existing_lineSegment.getB());
                    if (existing_lineSegment.getLength() < 0.0000001) {
                        lineSegment_voronoi_onePoint.remove(i);
                    } else {
                        lineSegment_voronoi_onePoint.set(i, existing_lineSegment);
                    }
                }

                if ((existing_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) <= 0) &&
                        (existing_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getB()) <= 0)) {
                    return;
                } else if ((existing_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) == 1) &&
                        (existing_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getB()) == -1)) {
                    add_lineSegment.set(add_lineSegment.getA(), intersection);
                    if (add_lineSegment.getLength() < 0.0000001) {
                        return;
                    }
                } else if ((existing_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) == -1) &&
                        (existing_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getB()) == 1)) {
                    add_lineSegment.set(intersection, add_lineSegment.getB());
                    if (add_lineSegment.getLength() < 0.0000001) {
                        return;
                    }
                }


            } else if (parallel == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines do not match
                if (add_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), existing_lineSegment.getA()) == -1) {
                    lineSegment_voronoi_onePoint.remove(i);
                } else if (existing_straightLine.sameSide(line_step[tyuusinn_ten_bangou].getA(), add_lineSegment.getA()) == -1) {
                    return;
                }
            } else if (parallel == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines match
                return;
            }
        }

        lineSegment_voronoi_onePoint.add(add_lineSegment);
    }

    public void Senb_boro_1p_motome(int center_point_count) {//It can be used when line_step contains only Voronoi mother points. Get Senb_boro_1p as a set of Voronoi line segments around center_point_count
        //i_egaki_dankai Obtain an array list of Voronoi line segments surrounding the third Voronoi vertex. // i_egaki_dankai The third Voronoi apex is line_step [i_egaki_dankai] .geta ()
        lineSegment_voronoi_onePoint.clear();

        for (int i_e_d = 1; i_e_d <= i_drawing_stage; i_e_d++) {
            if (i_e_d != center_point_count) {
                //Find the line segment to add
                LineSegment add_lineSegment = new LineSegment();

                add_lineSegment.set(OritaCalc.bisection(line_step[i_e_d].getA(), line_step[center_point_count].getA(), 1000.0));

                System.out.println("center_point_count= " + center_point_count + " ,i_e_d= " + i_e_d);

                if (i_e_d < center_point_count) {
                    add_lineSegment.setVoronoiA(i_e_d);
                    add_lineSegment.setVoronoiB(center_point_count);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                } else {
                    add_lineSegment.setVoronoiA(center_point_count);
                    add_lineSegment.setVoronoiB(i_e_d);//Record the two Voronoi vertices of the Voronoi line segment in iactive and color
                }
                voronoi_02_01(center_point_count, add_lineSegment);
            }
        }
    }

    public void voronoi_02() {//i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点を取り囲むボロノイ線分のアレイリストを得る。
        Senb_boro_1p_motome(i_drawing_stage);

        //20181109ここでori_v.の既存のボロノイ線分の整理が必要

        //ori_vの線分を最初に全て非選択にする
        voronoiLineSet.unselect_all();

        //
        LineSegment s_begin = new LineSegment();
        LineSegment s_end = new LineSegment();

        for (int ia = 0; ia < lineSegment_voronoi_onePoint.size() - 1; ia++) {
            for (int ib = ia + 1; ib < lineSegment_voronoi_onePoint.size(); ib++) {

                s_begin.set(lineSegment_voronoi_onePoint.get(ia));
                s_end.set(lineSegment_voronoi_onePoint.get(ib));

                StraightLine t_begin = new StraightLine(s_begin);

                int i_begin = s_begin.getVoronoiA();//In this case, voronoiA contains the number of the existing Voronoi mother point when the Voronoi line segment is added.
                int i_end = s_end.getVoronoiA();//In this case, voronoiA contains the number of the existing Voronoi mother point when the Voronoi line segment is added.

                if (i_begin > i_end) {
                    int i_temp = i_begin;
                    i_begin = i_end;
                    i_end = i_temp;
                }

                //The surrounding Voronoi line segment created by adding a new Voronoi matrix is being sought. The polygon of this Voronoi line segment is called a new cell.
                // Before adding a new cell to voronoiLineSet, process so that there is no existing line segment of voronoiLineSet that is inside the new cell.

                //20181109ここでori_v.の既存のボロノイ線分(iactive()が必ずicolorより小さくなっている)を探す
                for (int j = 1; j <= voronoiLineSet.getTotal(); j++) {
                    LineSegment s_kizon = new LineSegment();
                    s_kizon.set(voronoiLineSet.get(j));

                    int i_kizon_syou = s_kizon.getVoronoiA();
                    int i_kizon_dai = s_kizon.getVoronoiB();

                    if (i_kizon_syou > i_kizon_dai) {
                        i_kizon_dai = s_kizon.getVoronoiA();
                        i_kizon_syou = s_kizon.getVoronoiB();
                    }

                    if (i_kizon_syou == i_begin) {
                        if (i_kizon_dai == i_end) {

//20181110ここポイント
//
//	-1		0		1
//-1 	何もせず	何もせず	交点まで縮小
// 0	何もせず	有り得ない	削除
// 1	交点まで縮小	削除		削除
//

                            Point kouten = new Point();
                            kouten.set(OritaCalc.findIntersection(s_begin, s_kizon));

                            if ((t_begin.sameSide(line_step[i_drawing_stage].getA(), s_kizon.getA()) >= 0) &&
                                    (t_begin.sameSide(line_step[i_drawing_stage].getA(), s_kizon.getB()) >= 0)) {
                                voronoiLineSet.select(j);
                            }

                            if ((t_begin.sameSide(line_step[i_drawing_stage].getA(), s_kizon.getA()) == -1) &&
                                    (t_begin.sameSide(line_step[i_drawing_stage].getA(), s_kizon.getB()) == 1)) {
                                voronoiLineSet.set(j, s_kizon.getA(), kouten);
                            }

                            if ((t_begin.sameSide(line_step[i_drawing_stage].getA(), s_kizon.getA()) == 1) &&
                                    (t_begin.sameSide(line_step[i_drawing_stage].getA(), s_kizon.getB()) == -1)) {
                                voronoiLineSet.set(j, kouten, s_kizon.getB());
                            }
                        }
                    }
                }
            }
        }

        //選択状態のものを削除
        voronoiLineSet.delSelectedLineSegmentFast();

        voronoiLineSet.del_V_all(); //この行はいらないかも

        //Add the line segment of Senb_boro_1p to the end of senbun of voronoiLineSet
        for (LineSegment lineSegment : lineSegment_voronoi_onePoint) {
            LineSegment add_S = new LineSegment();
            add_S.set(lineSegment);
            voronoiLineSet.addLine(lineSegment);
        }
    }

    //マウス操作(マウスを動かしたとき)を行う関数    //System.out.println("_");
    public void mMoved_A_05or70(Point p0) {
        mMoved_m_003(p0, lineColor);
    }//常にマウスの位置のみが候補点

    //マウス操作(ボタンを押したとき)時の作業
    public void mPressed_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        i_candidate_stage = 0;

        if (i_drawing_stage == 0) {
            entyou_kouho_nbox.reset();
            i_drawing_stage = 1;

            line_step[1].set(p, p);
            line_step[1].setColor(LineColor.MAGENTA_5);//マゼンタ
            return;
        }

        if (i_drawing_stage >= 2) {

            i_drawing_stage = i_drawing_stage + 1;
            line_step[i_drawing_stage].set(p, p);
            line_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);//マゼンタ
            return;
        }

    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mDragged_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        if (i_drawing_stage == 1) {
            line_step[i_drawing_stage].setB(p);
        }
        if (i_drawing_stage > 1) {
            line_step[i_drawing_stage].set(p, p);
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mReleased_A_05or70(Point p0) {
        p.set(camera.TV2object(p0));
        closest_lineSegment.set(getClosestLineSegment(p));


        if (i_drawing_stage == 1) {

            line_step[1].setB(p);


            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineSegment s = foldLineSet.get(i);
                LineSegment.Intersection i_lineSegment_intersection_decision = OritaCalc.line_intersect_decide(s, line_step[1], 0.0001, 0.0001);
                boolean i_jikkou = i_lineSegment_intersection_decision == LineSegment.Intersection.INTERSECTS_1;

                if (i_jikkou) {
                    WeightedValue<LineSegment> i_d = new WeightedValue<>(s, OritaCalc.distance(line_step[1].getA(), OritaCalc.findIntersection(s, line_step[1])));
                    entyou_kouho_nbox.container_i_smallest_first(i_d);
                }


            }
            if ((entyou_kouho_nbox.getTotal() == 0) && (line_step[1].getLength() <= 0.000001)) {//延長する候補になる折線を選ぶために描いた線分s_step[1]が点状のときの処理
                if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < selectionDistance) {
                    WeightedValue<LineSegment> i_d = new WeightedValue<>(foldLineSet.closestLineSegmentSearch(p), 1.0);//entyou_kouho_nboxに1本の情報しか入らないのでdoubleの部分はどうでもよいので適当に1.0にした。
                    entyou_kouho_nbox.container_i_smallest_first(i_d);

                    line_step[1].setB(OritaCalc.findLineSymmetryPoint(closest_lineSegment.getA(), closest_lineSegment.getB(), p));

                    line_step[1].set(//line_step[1]を短くして、表示時に目立たない様にする。
                            OritaCalc.point_double(OritaCalc.midPoint(line_step[1].getA(), line_step[1].getB()), line_step[1].getA(), 0.00001 / line_step[1].getLength())
                            ,
                            OritaCalc.point_double(OritaCalc.midPoint(line_step[1].getA(), line_step[1].getB()), line_step[1].getB(), 0.00001 / line_step[1].getLength())
                    );

                }

            }

            System.out.println(" entyou_kouho_nbox.getsousuu() = " + entyou_kouho_nbox.getTotal());


            if (entyou_kouho_nbox.getTotal() == 0) {
                i_drawing_stage = 0;
                return;
            }
            if (entyou_kouho_nbox.getTotal() >= 0) {

                i_drawing_stage = 1 + entyou_kouho_nbox.getTotal();

                for (int i = 2; i <= i_drawing_stage; i++) {
                    line_step[i].set(entyou_kouho_nbox.getValue(i - 1));
                    line_step[i].setColor(LineColor.GREEN_6);//グリーン
                }
                return;
            }
            return;
        }


        if (i_drawing_stage >= 3) {
            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) >= selectionDistance) {
                i_drawing_stage = 0;
                return;
            }

            if (OritaCalc.distance_lineSegment(p, closest_lineSegment) < selectionDistance) {


                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがあるかどうかを判断する。
                boolean i_senbun_entyou_mode = false;// i_senbun_entyou_mode=0なら最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない。1ならある。
                for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                    if (OritaCalc.line_intersect_decide(entyou_kouho_nbox.getValue(i), closest_lineSegment, 0.000001, 0.000001) == LineSegment.Intersection.PARALLEL_EQUAL_31) {//線分が同じならoc.senbun_kousa_hantei==31
                        i_senbun_entyou_mode = true;
                    }
                }


                LineSegment addLineSegment = new LineSegment();
                //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがない場合
                if (!i_senbun_entyou_mode) {
                    int sousuu_old = foldLineSet.getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        //最初に選んだ線分と2番目に選んだ線分が平行でない場合
                        if (OritaCalc.parallel_judgement(entyou_kouho_nbox.getValue(i), closest_lineSegment, 0.000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) { //２つの線分が平行かどうかを判定する関数。oc.heikou_hantei(Tyokusen t1,Tyokusen t2)//0=平行でない
                            //line_step[1]とs_step[2]の交点はoc.kouten_motome(Senbun s1,Senbun s2)で求める//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
                            Point kousa_point = new Point();
                            kousa_point.set(OritaCalc.findIntersection(entyou_kouho_nbox.getValue(i), closest_lineSegment));
                            //addLineSegment =new Senbun(kousa_ten,foldLineSet.get(entyou_kouho_nbox.get_int(i)).get_tikai_hasi(kousa_ten));
                            addLineSegment.setA(kousa_point);
                            addLineSegment.setB(entyou_kouho_nbox.getValue(i).getClosestEndpoint(kousa_point));


                            if (addLineSegment.getLength() > 0.00000001) {
                                if (app.mouseMode == MouseMode.LENGTHEN_CREASE_5) {
                                    addLineSegment.setColor(lineColor);
                                }
                                if (app.mouseMode == MouseMode.CREASE_LENGTHEN_70) {
                                    addLineSegment.setColor(entyou_kouho_nbox.getValue(i).getColor());
                                }

                                //addsenbun(addLineSegment);
                                foldLineSet.addLine(addLineSegment);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                            }
                        }
                    }
                    foldLineSet.lineSegment_circle_intersection(sousuu_old, foldLineSet.getTotal(), 0, foldLineSet.numCircles() - 1);//(3)
                    foldLineSet.intersect_divide(1, sousuu_old, sousuu_old + 1, foldLineSet.getTotal());//(4)


                } else {
                    //最初に選んだ延長候補線分群中に2番目に選んだ線分と等しいものがある場合

                    int sousuu_old = foldLineSet.getTotal();//(1)
                    for (int i = 1; i <= entyou_kouho_nbox.getTotal(); i++) {
                        LineSegment moto_no_sen = new LineSegment();
                        moto_no_sen.set(entyou_kouho_nbox.getValue(i));
                        Point p_point = new Point();
                        p_point.set(OritaCalc.findIntersection(moto_no_sen, line_step[1]));

                        if (p_point.distance(moto_no_sen.getA()) < p_point.distance(moto_no_sen.getB())) {
                            moto_no_sen.a_b_swap();
                        }
                        addLineSegment.set(extendToIntersectionPoint_2(moto_no_sen));


                        if (addLineSegment.getLength() > 0.00000001) {
                            if (app.mouseMode == MouseMode.LENGTHEN_CREASE_5) {
                                addLineSegment.setColor(lineColor);
                            }
                            if (app.mouseMode == MouseMode.CREASE_LENGTHEN_70) {
                                addLineSegment.setColor(entyou_kouho_nbox.getValue(i).getColor());
                            }

                            foldLineSet.addLine(addLineSegment);//ori_sのsenbunの最後にs0の情報をを加えるだけ//(2)
                        }

                    }
                    foldLineSet.lineSegment_circle_intersection(sousuu_old, foldLineSet.getTotal(), 0, foldLineSet.numCircles() - 1);//(3)
                    foldLineSet.intersect_divide(1, sousuu_old, sousuu_old + 1, foldLineSet.getTotal());//(4)
                }

                record();

                i_drawing_stage = 0;
            }
        }


    }

    public void continuous_folding_new(Point a, Point b) {//An improved version of continuous folding.
        app.repaint();

        //ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする。
        //補助活線は無視する
        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする


        //「再帰関数における、種の発芽」交点がない場合「種」が成長せずリターン。

        e_s_dougubako.lengthenUntilIntersectionCalculateDisregardIncludedLineSegment_new(a, b);//一番近い交差点を見つけて各種情報を記録
        if (e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.NONE_0) {
            return;
        }

        //「再帰関数における、種の成長」交点が見つかった場合、交点まで伸びる線分をs_step[i_egaki_dankai]に追加
        //if(e_s_dougubako.get_kousaten_made_nobasi_orisen_fukumu_flg(a,b)==3){return;}
        i_drawing_stage = i_drawing_stage + 1;
        if (i_drawing_stage > 100) {
            return;
        }//念のためにs_stepの上限を100に設定した

        line_step[i_drawing_stage].set(e_s_dougubako.getLengthenUntilIntersectionLineSegment_new());//要注意　es1でうっかりs_stepにset.(senbun)やるとアクティヴでないので表示が小さくなる20170507
        line_step[i_drawing_stage].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

        System.out.println("20201129 saiki repaint ");

        //「再帰関数における、種の生成」求めた最も近い交点から次のベクトル（＝次の再帰関数に渡す「種」）を発生する。最も近い交点が折線とＸ字型に交差している点か頂点かで、種のでき方が異なる。

        //最も近い交点が折線とＸ字型の場合無条件に種を生成し、散布。
        if (e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.INTERSECT_X_1) {
            LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
            kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

            Point new_a = new Point();
            new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
            Point new_b = new Point();
            new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            continuous_folding_new(new_a, new_b);//種の散布
            return;
        }

        //最も近い交点が頂点（折線端末）の場合、頂点に集まる折線の数で条件分けして、種を生成し散布、
        if ((e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.INTERSECT_T_A_21)
                || (e_s_dougubako.getLengthenUntilIntersectionFlg_new(a, b) == StraightLine.Intersection.INTERSECT_T_B_22)) {//System.out.println("20201129 21 or 22");

            StraightLine tyoku1 = new StraightLine(a, b);
            StraightLine.Intersection intersection;

            SortingBox<LineSegment> t_m_s_nbox = new SortingBox<>();

            t_m_s_nbox.set(foldLineSet.get_SortingBox_of_vertex_b_surrounding_foldLine(e_s_dougubako.getLengthenUntilIntersectionLineSegment_new().getA(), e_s_dougubako.getLengthenUntilIntersectionLineSegment_new().getB()));

            if (t_m_s_nbox.getTotal() == 2) {
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                    kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                    Point new_a = new Point();
                    new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                    Point new_b = new Point();
                    new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                    continuous_folding_new(new_a, new_b);//種の散布
                    return;
                }
                return;
            }


            if (t_m_s_nbox.getTotal() == 3) {
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(2));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(3));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(e_s_dougubako.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = new Point();
                        new_a.set(e_s_dougubako.getLengthenUntilIntersectionPoint_new());//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                    }
                }
            }
        }
    }

    public void mPressed_A_box_select(Point p0) {
        p19_1.set(p0);

        i_drawing_stage = 0;

        p.set(camera.TV2object(p0));

        line_step[1].set(p, p);
        line_step[1].setColor(LineColor.MAGENTA_5);
        line_step[2].set(p, p);
        line_step[2].setColor(LineColor.MAGENTA_5);
        line_step[3].set(p, p);
        line_step[3].setColor(LineColor.MAGENTA_5);
        line_step[4].set(p, p);
        line_step[4].setColor(LineColor.MAGENTA_5);
    }

    public void mDragged_A_box_select(Point p0) {
        Point p19_2 = new Point(p19_1.getX(), p0.getY());
        Point p19_4 = new Point(p0.getX(), p19_1.getY());

        Point p19_a = new Point(camera.TV2object(p19_1));
        Point p19_b = new Point(camera.TV2object(p19_2));
        Point p19_c = new Point(camera.TV2object(p0));
        Point p19_d = new Point(camera.TV2object(p19_4));

        line_step[1].set(p19_a, p19_b);
        line_step[2].set(p19_b, p19_c);
        line_step[3].set(p19_c, p19_d);
        line_step[4].set(p19_d, p19_a);

        i_drawing_stage = 4;//line_step[4]まで描画するために、この行が必要
    }

    public int getDrawingStage() {
        return i_drawing_stage;
    }

    public void setDrawingStage(int i) {
        i_drawing_stage = i;
    }

    public void select_all() {
        foldLineSet.select_all();
    }

    public void unselect_all() {
        foldLineSet.unselect_all();
    }

    public void select(Point p0a, Point p0b) {
        Point p0_a = new Point(p0a.getX(), p0a.getY());
        Point p0_b = new Point(p0a.getX(), p0b.getY());
        Point p0_c = new Point(p0b.getX(), p0b.getY());
        Point p0_d = new Point(p0b.getX(), p0a.getY());

        Point p_a = new Point(camera.TV2object(p0_a));
        Point p_b = new Point(camera.TV2object(p0_b));
        Point p_c = new Point(camera.TV2object(p0_c));
        Point p_d = new Point(camera.TV2object(p0_d));

        foldLineSet.select(p_a, p_b, p_c, p_d);
    }

    public void unselect(Point p0a, Point p0b) {
        Point p0_a = new Point(p0a.getX(), p0a.getY());
        Point p0_b = new Point(p0a.getX(), p0b.getY());
        Point p0_c = new Point(p0b.getX(), p0b.getY());
        Point p0_d = new Point(p0b.getX(), p0a.getY());
        Point p_a = new Point(camera.TV2object(p0_a));
        Point p_b = new Point(camera.TV2object(p0_b));
        Point p_c = new Point(camera.TV2object(p0_c));
        Point p_d = new Point(camera.TV2object(p0_d));

        foldLineSet.unselect(p_a, p_b, p_c, p_d);
    }

    public boolean deleteInside_foldingLine(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));

        return foldLineSet.deleteInside_foldingLine(p_a, p_b, p_c, p_d);
    }

    public boolean deleteInside_edge(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.deleteInside_edge(p_a, p_b, p_c, p_d);
    }

    public boolean deleteInside_aux(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.deleteInside_aux(p_a, p_b, p_c, p_d);
    }

    public boolean change_property_in_4kakukei(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.change_property_in_4kakukei(p_a, p_b, p_c, p_d, customCircleColor);
    }

    public boolean deleteInside(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return auxLines.deleteInside(p_a, p_b, p_c, p_d);
    }

    public int MV_change(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.MV_change(p_a, p_b, p_c, p_d);
    }

    public LineSegment extendToIntersectionPoint(LineSegment s0) {//Extend s0 from point a to b, until it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_ten_kyori = kousa_point.distance(add_sen.getA());


        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_kousa_flg;
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            i_kousa_flg = tyoku1.lineSegment_intersect_reverse_detail(foldLineSet.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            if (i_kousa_flg.isIntersecting()) {
                kousa_point.set(OritaCalc.findIntersection(tyoku1, foldLineSet.get(i)));
                if (kousa_point.distance(add_sen.getA()) > 0.00001) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                            add_sen.set(add_sen.getA(), kousa_point);
                        }
                    }
                }
            }
        }
        return add_sen;
    }

    public LineSegment extendToIntersectionPoint_2(LineSegment s0) {//Extend s0 from point b in the opposite direction of a to the point where it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
        LineSegment add_sen = new LineSegment();
        add_sen.set(s0);

        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_point_distance = kousa_point.distance(add_sen.getA());

        StraightLine tyoku1 = new StraightLine(add_sen.getA(), add_sen.getB());
        StraightLine.Intersection i_intersection_flg;//元の線分を直線としたものと、他の線分の交差状態
        LineSegment.Intersection i_lineSegment_intersection_flg;//元の線分と、他の線分の交差状態

        System.out.println("AAAAA_");
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(foldLineSet.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

            //i_lineSegment_intersection_flg=oc.senbun_kousa_hantei_amai( add_sen,foldLineSet.get(i),0.00001,0.00001);//20180408なぜかこの行の様にadd_senを使うと、i_senbun_kousa_flgがおかしくなる
            i_lineSegment_intersection_flg = OritaCalc.line_intersect_decide_sweet(s0, foldLineSet.get(i), 0.00001, 0.00001);//20180408なぜかこの行の様にs0のままだと、i_senbun_kousa_flgがおかしくならない。
            if (i_intersection_flg.isIntersecting()) {
                if (!i_lineSegment_intersection_flg.isEndpointIntersection()) {
                    //System.out.println("i_intersection_flg = "+i_intersection_flg  +      " ; i_lineSegment_intersection_flg = "+i_lineSegment_intersection_flg);
                    kousa_point.set(OritaCalc.findIntersection(tyoku1, foldLineSet.get(i)));
                    if (kousa_point.distance(add_sen.getA()) > 0.00001) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                //i_kouten_ari_nasi=1;
                                kousa_point_distance = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }
                }
            }

            if (i_intersection_flg == StraightLine.Intersection.INCLUDED_3) {
                if (i_lineSegment_intersection_flg != LineSegment.Intersection.PARALLEL_EQUAL_31) {

                    System.out.println("i_intersection_flg = " + i_intersection_flg + " ; i_lineSegment_intersection_flg = " + i_lineSegment_intersection_flg);


                    kousa_point.set(foldLineSet.get(i).getA());
                    if (kousa_point.distance(add_sen.getA()) > 0.00001) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                kousa_point_distance = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }

                    kousa_point.set(foldLineSet.get(i).getB());
                    if (kousa_point.distance(add_sen.getA()) > 0.00001) {
                        if (kousa_point.distance(add_sen.getA()) < kousa_point_distance) {
                            double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                            if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                                kousa_point_distance = kousa_point.distance(add_sen.getA());
                                add_sen.set(add_sen.getA(), kousa_point);
                            }
                        }
                    }
                }
            }
        }

        add_sen.set(s0.getB(), add_sen.getB());
        return add_sen;
    }

    public int kouten_ari_nasi(LineSegment s0) {//If s0 is extended from the point a to the b direction and intersects with another polygonal line, 0 is returned if it is not 1. The intersecting line segments at the a store have no intersection with this function.
        LineSegment add_line = new LineSegment();
        add_line.set(s0);
        Point intersection_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120

        StraightLine tyoku1 = new StraightLine(add_line.getA(), add_line.getB());
        StraightLine.Intersection i_intersection_flg;
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            i_intersection_flg = tyoku1.lineSegment_intersect_reverse_detail(foldLineSet.get(i));//0 = This straight line does not intersect a given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.

            if (i_intersection_flg.isIntersecting()) {
                intersection_point.set(OritaCalc.findIntersection(tyoku1, foldLineSet.get(i)));
                if (intersection_point.distance(add_line.getA()) > 0.00001) {
                    double d_kakudo = OritaCalc.angle(add_line.getA(), add_line.getB(), add_line.getA(), intersection_point);
                    if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                        return 1;

                    }

                }
            }
        }
        return 0;
    }

    //-------------------------
    public void del_selected_senbun() {
        foldLineSet.delSelectedLineSegmentFast();
    }

    public void mMoved_takakukei_and_sagyou(Point p0) {
        //マウス操作(マウスを動かしたとき)を行う関数
        if (gridInputAssist) {
            line_candidate[1].setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
            p.set(camera.TV2object(p0));
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) > p.distance(line_step[1].getA())) {
                closest_point.set(line_step[1].getA());
            }

            if (p.distance(closest_point) < selectionDistance) {
                line_candidate[1].set(closest_point, closest_point);
            } else {
                line_candidate[1].set(p, p);
            }

            line_candidate[1].setColor(LineColor.MAGENTA_5);
            //return;
        }

    }

    //マウス操作(ボタンを押したとき)時の作業----------------------------------------------------
    public void mPressed_takakukei_and_sagyou(Point p0) {
//i_egaki_dankai==0なのはこの操作ボタンを押した直後の段階か、多角形が完成して、その後ボタンを押した後
        if (i_takakukei_kansei) {
            i_takakukei_kansei = false;
            i_drawing_stage = 0;
        }

        i_drawing_stage = i_drawing_stage + 1;
        line_step[i_drawing_stage].setColor(LineColor.MAGENTA_5);
        p.set(camera.TV2object(p0));

        if (i_drawing_stage == 1) {
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) > selectionDistance) {
                closest_point.set(p);
            }
            line_step[i_drawing_stage].set(closest_point, p);

        } else {//ここでi_egaki_dankai=0となることはない。
            line_step[i_drawing_stage].set(line_step[i_drawing_stage - 1].getB(), p);
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数----------------------------------------------------

    public void mDragged_takakukei_and_sagyou(Point p0) {
        //if(i_takakukei_kansei==0)//ここにくるときは必ずi_takakukei_kansei==0なのでif分は無意味

        p.set(camera.TV2object(p0));
        line_step[i_drawing_stage].setB(p);


        if (gridInputAssist) {
            i_candidate_stage = 1;
            closest_point.set(getClosestPoint(p));
            if (p.distance(closest_point) > p.distance(line_step[1].getA())) {
                closest_point.set(line_step[1].getA());
            }


            if (p.distance(closest_point) < selectionDistance) {
                line_candidate[1].set(closest_point, closest_point);
            } else {
                line_candidate[1].set(p, p);
            }

            line_step[i_drawing_stage].setB(line_candidate[1].getA());
        }
    }

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public void mReleased_takakukei_and_sagyou(Point p0, int i_mode) {
        p.set(camera.TV2object(p0));
        closest_point.set(getClosestPoint(p));
        if (p.distance(closest_point) > selectionDistance) {
            closest_point.set(p);
        }

        line_step[i_drawing_stage].setB(closest_point);


        if (i_drawing_stage >= 2) {
            if (p.distance(line_step[1].getA()) <= selectionDistance) {
                line_step[i_drawing_stage].setB(line_step[1].getA());
                //i_O_F_C=1;
                i_takakukei_kansei = true;
            }
        }

        if (i_takakukei_kansei) {
            Polygon Taka = new Polygon(i_drawing_stage);
            for (int i = 1; i <= i_drawing_stage; i++) {
                Taka.set(i, line_step[i].getA());
            }

            //各動作モードで独自に行う作業は以下に条件分けして記述する
            if (i_mode == 66) {
                foldLineSet.select_Takakukei(Taka, "select");
            }//66 66 66 66 66 多角形を入力し、それに全体が含まれる折線をselectする
            if (i_mode == 67) {
                foldLineSet.select_Takakukei(Taka, "unselect");
            }//67 67 67 67 67 多角形を入力し、それに全体が含まれる折線を折線をunselectする
            //各動作モードで独自に行う作業はここまで
        }
    }


//20201024高密度入力がオンならばapのrepaint（画面更新）のたびにTen kus_sisuu=new Ten(mainDrawingWorker.get_moyori_ten_sisuu(p_mouse_TV_iti));で最寄り点を求めているので、この描き職人内で別途最寄り点を求めていることは二度手間になっている。

    public void v_del_all() {
        int sousuu_old = foldLineSet.getTotal();
        foldLineSet.del_V_all();
        if (sousuu_old != foldLineSet.getTotal()) {
            record();
        }
    }

    public void v_del_all_cc() {
        int sousuu_old = foldLineSet.getTotal();
        foldLineSet.del_V_all_cc();
        if (sousuu_old != foldLineSet.getTotal()) {
            record();
        }
    }

    public void all_s_step_to_orisen() {//20181014

        LineSegment add_sen = new LineSegment();
        for (int i = 1; i <= i_drawing_stage; i++) {

            if (line_step[i].getLength() > 0.00000001) {
                add_sen.set(line_step[i]);
                add_sen.setColor(lineColor);
                addLineSegment(add_sen);
            } else {

                addCircle(line_step[i].getAX(), line_step[i].getAY(), 5.0, LineColor.CYAN_3);
            }
        }
        record();
    }

    public boolean insideToMountain(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.insideToMountain(p_a, p_b, p_c, p_d);
    }

    public boolean insideToValley(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.insideToValley(p_a, p_b, p_c, p_d);
    }

    public boolean insideToEdge(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.insideToEdge(p_a, p_b, p_c, p_d);
    }

    public boolean insideToAux(Point p0a, Point p0b) {
        Point p0_a = new Point();
        Point p0_b = new Point();
        Point p0_c = new Point();
        Point p0_d = new Point();
        Point p_a = new Point();
        Point p_b = new Point();
        Point p_c = new Point();
        Point p_d = new Point();
        p0_a.set(p0a.getX(), p0a.getY());
        p0_b.set(p0a.getX(), p0b.getY());
        p0_c.set(p0b.getX(), p0b.getY());
        p0_d.set(p0b.getX(), p0a.getY());
        p_a.set(camera.TV2object(p0_a));
        p_b.set(camera.TV2object(p0_b));
        p_c.set(camera.TV2object(p0_c));
        p_d.set(camera.TV2object(p0_d));
        return foldLineSet.insideToAux(p_a, p_b, p_c, p_d);
    }

    public LineSegment get_s_step(int i) {
        return line_step[i];
    }

    public void setFoldLineDividingNumber(int i) {
        foldLineDividingNumber = i;
        if (foldLineDividingNumber < 1) {
            foldLineDividingNumber = 1;
        }
    }

    public void set_d_restricted_angle(double d_1, double d_2, double d_3) {
        d_restricted_angle_1 = d_1;
        d_restricted_angle_2 = d_2;
        d_restricted_angle_3 = d_3;
    }

    public void setNumPolygonCorners(int i) {
        numPolygonCorners = i;
        if (numPolygonCorners < 3) {
            foldLineDividingNumber = 3;
        }
    }

    public void setFoldLineAdditional(FoldLineAdditionalInputMode i) {
        i_foldLine_additional = i;
    }


    public void check1(double r_hitosii, double parallel_decision) {
        foldLineSet.check1(r_hitosii, parallel_decision);
    }//In foldLineSet, check and set the funny fold line to the selected state.

    public void fix1(double r_hitosii, double heikou_hantei) {
        while (foldLineSet.fix1(r_hitosii, heikou_hantei)) {
        }
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

    }

    public void set_i_check1(boolean i) {
        check1 = i;
    }

    public void check2(double r_hitosii, double heikou_hantei) {
        foldLineSet.check2(r_hitosii, heikou_hantei);
    }

    public void fix2(double r_hitosii, double heikou_hantei) {
        while (foldLineSet.fix2(r_hitosii, heikou_hantei)) {
        }
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        if (check1) {
            check1(0.001, 0.5);
        }
        if (check2) {
            check2(0.01, 0.5);
        }
        if (check3) {
            check3(0.0001);
        }
        if (check4) {
            check4(0.0001);
        }

    }

    public void setCheck2(boolean i) {
        check2 = i;
    }

    public void check3(double r) {
        foldLineSet.check3(r);
    }

    public void check4(double r) {
        app.check4(r);
    }

    public void ap_check4(double r) {
        foldLineSet.check4(r);
    }

    public void setCheck3(boolean i) {
        check3 = i;
    }

    public void setCheck4(boolean i) {
        check4 = i;
    }

    public void lightenCheck4Color() {
        check4ColorTransparency = check4ColorTransparency - check4ColorTransparencyIncrement;
        if (check4ColorTransparency < 50) {
            check4ColorTransparency = check4ColorTransparency + check4ColorTransparencyIncrement;
        }
    }

    public void darkenCheck4Color() {
        check4ColorTransparency = check4ColorTransparency + check4ColorTransparencyIncrement;
        if (check4ColorTransparency > 250) {
            check4ColorTransparency = check4ColorTransparency - check4ColorTransparencyIncrement;
        }
    }

    public void setAuxLineColor(LineColor i) {
        auxLineColor = i;
    }

    public void setUndoTotal(int i) {
        historyState.setUndoTotal(i);
    }

    public void setAuxUndoTotal(int i) {
        auxHistoryState.setUndoTotal(i);
    }

    public void organizeCircles() {//Organize all circles.
        foldLineSet.organizeCircles();
    }

    public void add_hanten(Circle e0, Circle eh) {
        //e0の円周が(x,y)を通るとき
        if (Math.abs(OritaCalc.distance(e0.getCenter(), eh.getCenter()) - e0.getRadius()) < 0.0000001) {
            LineSegment s_add = new LineSegment();
            s_add.set(eh.turnAround_CircleToLineSegment(e0));
            //s_add.setcolor(3);
            addLineSegment(s_add);
            record();
            return;
        }

        //e0の円周が(x,y)を通らないとき。e0の円周の外部に(x,y)がくるとき//e0の円周の内部に(x,y)がくるとき
        Circle e_add = new Circle();
        e_add.set(eh.turnAround(e0));
        addCircle(e_add);
        record();
    }

    public void add_hanten(LineSegment s0, Circle eh) {
        StraightLine ty = new StraightLine(s0);
        //s0上に(x,y)がくるとき
        if (ty.calculateDistance(eh.getCenter()) < 0.0000001) {
            return;
        }

        //s0が(x,y)を通らないとき。
        Circle e_add = new Circle();
        e_add.set(eh.turnAround_LineSegmentToCircle(s0));
        addCircle(e_add);
        record();
    }

    public double getSelectionDistance() {
        return selectionDistance;
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        setGridInputAssist(data.getDisplayGridInputAssist());
        setPointSize(data.getPointSize());
        setColor(data.getLineColor());
        setAuxLineColor(data.getAuxLiveLineColor());
        setFoldLineAdditional(data.getFoldLineAdditionalInputMode());
        i_select_mode = data.getSelectionOperationMode();
        setFoldLineDividingNumber(data.getFoldLineDividingNumber());
        setNumPolygonCorners(data.getNumPolygonCorners());
        setCheck4(data.getCheck4Enabled());
        setCustomCircleColor(data.getCircleCustomizedColor());

        if (e.getPropertyName() == null || e.getPropertyName().equals("check4Enabled")) {
            if (data.getCheck4Enabled()) {
                check4(0.0001);
            }
        }
    }

    public void setData(AngleSystemModel angleSystemModel) {
        set_id_angle_system(angleSystemModel.getCurrentAngleSystemDivider());
        set_d_restricted_angle(angleSystemModel.getCurrentAngleA(), angleSystemModel.getCurrentAngleB(), angleSystemModel.getCurrentAngleC());

        unselect_all();
    }

    public void setData(InternalDivisionRatioModel data) {
        internalDivisionRatio_s = data.getInternalDivisionRatioS();
        internalDivisionRatio_t = data.getInternalDivisionRatioT();
    }

    public void setData(HistoryStateModel historyStateModel) {
        setUndoTotal(historyStateModel.getHistoryTotal());
        setAuxUndoTotal(historyStateModel.getHistoryTotal());
    }

    public enum OperationFrameMode {
        NONE_0,
        CREATE_1,
        MOVE_POINTS_2,
        MOVE_SIDES_3,
        MOVE_BOX_4,
    }
}
