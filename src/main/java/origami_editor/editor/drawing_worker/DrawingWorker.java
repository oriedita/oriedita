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
import origami_editor.tools.Camera;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawingWorker {
    // ------------
    final int check4ColorTransparencyIncrement = 10;
    private final LineSegmentSet lineSegmentSet = new LineSegmentSet();    //Instantiation of basic branch structure
    public FoldLineSet foldLineSet = new FoldLineSet();    //Store polygonal lines
    public FoldLineSet voronoiLineSet = new FoldLineSet();    //Store Voronoi diagram lines
    public Grid grid = new Grid();
    public Polygon operationFrameBox = new Polygon(4);    //Instantiation of selection box (TV coordinates)
    int pointSize = 1;
    LineColor lineColor;//Line segment color
    LineColor auxLineColor = LineColor.ORANGE_4;//Auxiliary line color
    boolean gridInputAssist = false;//1 if you use the input assist function for fine grid display, 0 if you do not use it
    Color customCircleColor;//Stores custom colors for circles and auxiliary hot lines
    HistoryState historyState = new HistoryState();
    HistoryState auxHistoryState = new HistoryState();
    FoldLineAdditionalInputMode i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;//= 0 is polygonal line input = 1 is auxiliary line input mode (when inputting a line segment, these two). When deleting a line segment, the value becomes as follows. = 0 is the deletion of the polygonal line, = 1 is the deletion of the auxiliary picture line, = 2 is the deletion of the black line, = 3 is the deletion of the auxiliary live line, = 4 is the folding line, the auxiliary live line and the auxiliary picture line.
    FoldLineSet auxLines = new FoldLineSet();    //Store auxiliary lines
    Drawing_Worker_Toolbox e_s_dougubako = new Drawing_Worker_Toolbox(foldLineSet);
    int id_angle_system = 8;//180 / id_angle_system represents the angular system. For example, if id_angle_system = 3, 180/3 = 60 degrees, if id_angle_system = 5, 180/5 = 36 degrees
    double angle;
    int foldLineDividingNumber = 1;
    double internalDivisionRatio_s;
    double internalDivisionRatio_t;
    double d_restricted_angle_1;
    double d_restricted_angle_2;
    double d_restricted_angle_3;
    int numPolygonCorners = 5;
    double selectionDistance = 50.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Value for determining whether an input point is close to an existing point or line segment
    /**
     * Temporary line segments when drawing.
     */
    List<LineSegment> lineStep = new ArrayList<>();
    /**
     * Temporary circles when drawing.
     */
    List<Circle> circleStep = new ArrayList<>();
    /**
     * Candidate line segments.
     */
    List<LineSegment> lineCandidate = new ArrayList<>();
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
    //mouseMode==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
    Point operationFrame_p1 = new Point();//TV座標
    Point operationFrame_p2 = new Point();//TV座標
    Point operationFrame_p3 = new Point();//TV座標
    Point operationFrame_p4 = new Point();//TV座標
    Point p = new Point();
    ArrayList<LineSegment> lineSegment_voronoi_onePoint = new ArrayList<>(); //Line segment around one point in Voronoi diagram
    // ****************************************************************************************************************************************
    // **************　Variable definition so far　****************************************************************************************************
    // ****************************************************************************************************************************************
    // ------------------------------------------------------------------------------------------------------------
    // Sub-operation mode for MouseMode.FOLDABLE_LINE_DRAW_71, either DRAW_CREASE_FREE_1, or VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38
    //--------------------------------------------
    CanvasModel.SelectionOperationMode i_select_mode = CanvasModel.SelectionOperationMode.NORMAL_0;//=0は通常のセレクト操作

    public DrawingWorker(App app0) {  //コンストラクタ
        app = app0;

        lineColor = LineColor.BLACK_0;

        text_cp_setumei = "1/";
        s_title = "no title";

        reset();
    }

    public void lineStepAdd(LineSegment s) {
        LineSegment s0 = s.clone();
        s0.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
        lineStep.add(s0);
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
        lineStep.clear();
        circleStep.clear();
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
        foldLineSet.divideLineSegmentIntersections(1, total_old, total_old + 1, total_new);

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
        foldLineSet.applyBranchTrim(r);
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
        foldLineSet.removePoints();
    }

    public void overlapping_line_removal() {
        foldLineSet.removeOverlappingLines();
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

        // Only mark saved false after the first record.
        if (!historyState.isEmpty()) {
            app.fileModel.setSaved(false);
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
                LineSegment as = auxLines.get(i);
                g_setColor(g, as.getColor());

                s_tv.set(camera.object2TV(as));
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
            LineSegment s = foldLineSet.get(i);
            if (s.getSelected() == 2) {
                g.setColor(Color.green);

                s_tv.set(camera.object2TV(s));

                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            }
        }

        //展開図の描画 補助活線のみ
        if (displayAuxLines) {
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineSegment s = foldLineSet.get(i);
                if (s.getColor() == LineColor.CYAN_3) {

                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

                    if (s.getCustomized() == 0) {
                        g_setColor(g, s.getColor());
                    } else if (s.getCustomized() == 1) {
                        g.setColor(s.getCustomizedColor());
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
        if (app.mouseMode == MouseMode.OPERATION_FRAME_CREATE_61 && lineStep.size() == 4) {
            Point p1 = new Point();
            p1.set(camera.TV2object(operationFrame_p1));
            Point p2 = new Point();
            p2.set(camera.TV2object(operationFrame_p2));
            Point p3 = new Point();
            p3.set(camera.TV2object(operationFrame_p3));
            Point p4 = new Point();
            p4.set(camera.TV2object(operationFrame_p4));

            lineStep.get(0).set(p1, p2, LineColor.GREEN_6);
            lineStep.get(1).set(p2, p3, LineColor.GREEN_6);
            lineStep.get(2).set(p3, p4, LineColor.GREEN_6);
            lineStep.get(3).set(p4, p1, LineColor.GREEN_6);
        }

        //線分入力時の一時的なs_step線分を描く　

        if ((app.mouseMode != MouseMode.OPERATION_FRAME_CREATE_61) || (lineStep.size() == 4)) {
            for (LineSegment s : lineStep) {
                g_setColor(g, s.getColor());
                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

                s_tv.set(camera.object2TV(s));
                a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
                b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため


                g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
                int i_width_nyuiiryokuji = 3;
                if (gridInputAssist) {
                    i_width_nyuiiryokuji = 2;
                }

                switch (s.getActive()) {
                    case ACTIVE_A_1:
                        g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                        break;
                    case ACTIVE_B_2:
                        g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                        break;
                    case ACTIVE_BOTH_3:
                        g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                        g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                        break;
                }
            }
        }
        //候補入力時の候補を描く//System.out.println("_");
        g2.setStroke(new BasicStroke(lineWidth + 0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A

        for (LineSegment s : lineCandidate) {
            g_setColor(g, s.getColor());

            s_tv.set(camera.object2TV(s));
            a.set(s_tv.getAX() + 0.000001, s_tv.getAY() + 0.000001);
            b.set(s_tv.getBX() + 0.000001, s_tv.getBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
            int i_width = pointSize + 5;

            switch (s.getActive()) {
                case ACTIVE_A_1:
                    g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                    g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線
                    break;
                case ACTIVE_B_2:
                    g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                    g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                    break;
                case ACTIVE_BOTH_3:
                    g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                    g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線

                    g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                    g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                    break;
            }
        }

        g.setColor(Color.black);

        for (Circle c : circleStep) {
            g_setColor(g, c.getColor());
            a.set(camera.object2TV(c.getCenter()));//この場合のs_tvは描画座標系での円の中心の位置
            a.set(a.getX() + 0.000001, a.getY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

            double d_width = c.getRadius() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。

            g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
        }

        g.setColor(Color.black);

        if (displayComments) {
            g.drawString(text_cp_setumei, 10, 55);
        }
    }

    //--------------------------------------------------------------------------------------
    //Mouse operation----------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------

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

    public void resetCircleStep() {
        circleStep.clear();
    }


    //動作モデル00a--------------------------------------------------------------------------------------------------------
    //マウスクリック（マウスの近くの既成点を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（マウスの近くの既成点を選択）してから目的の処理をする雛形セット

    public void set_id_angle_system(int i) {
        id_angle_system = i;
    }

    // ------------------------------------
    public void setGridInputAssist(boolean i) {
        gridInputAssist = i;

        if (!gridInputAssist) {
            for (LineSegment candidate : lineCandidate) {
                candidate.deactivate();
            }
        }
    }

    public void addCircle(Circle e0) {
        addCircle(e0.getX(), e0.getY(), e0.getRadius(), e0.getColor());
    }

    public void addCircle(Point t0, double dr, LineColor ic) {
        addCircle(t0.getX(), t0.getY(), dr, ic);
    }


    //動作モデル00b--------------------------------------------------------------------------------------------------------
    //マウスクリック（近くの既成点かマウス位置を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（近くの既成点かマウス位置を選択）してから目的の処理をする雛形セット

    public void addCircle(double dx, double dy, double dr, LineColor ic) {
        foldLineSet.addCircle(dx, dy, dr, ic);

        int imin = 0;
        int imax = foldLineSet.numCircles() - 2;
        int jmin = foldLineSet.numCircles() - 1;
        int jmax = foldLineSet.numCircles() - 1;

        foldLineSet.applyCircleCircleIntersection(imin, imax, jmin, jmax);
        foldLineSet.applyLineSegmentCircleIntersection(1, foldLineSet.getTotal(), jmin, jmax);

    }

    public void addLineSegment_auxiliary(LineSegment s0) {
        auxLines.addLine(s0);
    }

    public void addLineSegment(LineSegment s0) {//0 = No change, 1 = Color change only, 2 = Line segment added
        foldLineSet.addLine(s0);//Just add the information of s0 to the end of senbun of foldLineSet
        int total_old = foldLineSet.getTotal();
        foldLineSet.applyLineSegmentCircleIntersection(foldLineSet.getTotal(), foldLineSet.getTotal(), 0, foldLineSet.numCircles() - 1);

        foldLineSet.divideLineSegmentIntersections(1, total_old - 1, total_old, total_old);
    }

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
        return foldLineSet.getClosestLineSegment(t0);
    }

    //------------------------------------------------------
    public LineSegment get_moyori_step_lineSegment(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;//Senbun s1 =new Senbun(100000.0,100000.0,100000.0,100000.1);
        for (int i = imin; i <= imax; i++) {
            double sk = OritaCalc.determineLineSegmentDistance(t0, lineStep.get(i - 1));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか

        }

        return lineStep.get(minrid - 1);
    }

    //------------------------------
    public Circle getClosestCircleMidpoint(Point t0) {
        return foldLineSet.closestCircleMidpoint(t0);
    }


    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える
    public Point getGridPosition(Point p0) {
        p.set(camera.TV2object(p0));
        Point closestPoint = getClosestPoint(p);
        return new Point(grid.getPosition(closestPoint));
    }

    public void voronoi_02_01(int center_point_count, LineSegment add_lineSegment) {
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

            OritaCalc.ParallelJudgement parallel = OritaCalc.isLineSegmentParallel(add_straightLine, existing_straightLine, 0.0001);//0 = not parallel, 1 = parallel and 2 straight lines do not match, 2 = parallel and 2 straight lines match

            if (parallel == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//When the line segment to be added and the existing line segment are non-parallel
                Point intersection = new Point();
                intersection.set(OritaCalc.findIntersection(add_straightLine, existing_straightLine));

                if ((add_straightLine.sameSide(lineStep.get(center_point_count).getA(), existing_lineSegment.getA()) <= 0) &&
                        (add_straightLine.sameSide(lineStep.get(center_point_count).getA(), existing_lineSegment.getB()) <= 0)) {
                    lineSegment_voronoi_onePoint.remove(i);
                } else if ((add_straightLine.sameSide(lineStep.get(center_point_count).getA(), existing_lineSegment.getA()) == 1) &&
                        (add_straightLine.sameSide(lineStep.get(center_point_count).getA(), existing_lineSegment.getB()) == -1)) {
                    existing_lineSegment.set(existing_lineSegment.getA(), intersection);
                    if (existing_lineSegment.getLength() < 0.0000001) {
                        lineSegment_voronoi_onePoint.remove(i);
                    } else {
                        lineSegment_voronoi_onePoint.set(i, existing_lineSegment);
                    }
                } else if ((add_straightLine.sameSide(lineStep.get(center_point_count).getA(), existing_lineSegment.getA()) == -1) &&
                        (add_straightLine.sameSide(lineStep.get(center_point_count).getA(), existing_lineSegment.getB()) == 1)) {
                    existing_lineSegment.set(intersection, existing_lineSegment.getB());
                    if (existing_lineSegment.getLength() < 0.0000001) {
                        lineSegment_voronoi_onePoint.remove(i);
                    } else {
                        lineSegment_voronoi_onePoint.set(i, existing_lineSegment);
                    }
                }

                if ((existing_straightLine.sameSide(lineStep.get(center_point_count).getA(), add_lineSegment.getA()) <= 0) &&
                        (existing_straightLine.sameSide(lineStep.get(center_point_count).getA(), add_lineSegment.getB()) <= 0)) {
                    return;
                } else if ((existing_straightLine.sameSide(lineStep.get(center_point_count).getA(), add_lineSegment.getA()) == 1) &&
                        (existing_straightLine.sameSide(lineStep.get(center_point_count).getA(), add_lineSegment.getB()) == -1)) {
                    add_lineSegment.set(add_lineSegment.getA(), intersection);
                    if (add_lineSegment.getLength() < 0.0000001) {
                        return;
                    }
                } else if ((existing_straightLine.sameSide(lineStep.get(center_point_count).getA(), add_lineSegment.getA()) == -1) &&
                        (existing_straightLine.sameSide(lineStep.get(center_point_count).getA(), add_lineSegment.getB()) == 1)) {
                    add_lineSegment.set(intersection, add_lineSegment.getB());
                    if (add_lineSegment.getLength() < 0.0000001) {
                        return;
                    }
                }


            } else if (parallel == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//When the line segment to be added and the existing line segment are parallel and the two straight lines do not match
                if (add_straightLine.sameSide(lineStep.get(center_point_count).getA(), existing_lineSegment.getA()) == -1) {
                    lineSegment_voronoi_onePoint.remove(i);
                } else if (existing_straightLine.sameSide(lineStep.get(center_point_count).getA(), add_lineSegment.getA()) == -1) {
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

        for (int i_e_d = 0; i_e_d < lineStep.size(); i_e_d++) {
            if (i_e_d != center_point_count) {
                //Find the line segment to add
                LineSegment add_lineSegment = new LineSegment();

                add_lineSegment.set(OritaCalc.bisection(lineStep.get(i_e_d).getA(), lineStep.get(center_point_count).getA(), 1000.0));

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

    public void voronoi_02() {
        //i=1からi_egaki_dankaiまでのs_step[i]と、i_egaki_dankai-1までのボロノイ図からi_egaki_dankaiのボロノイ図を作成

        //i_egaki_dankai番目のボロノイ頂点を取り囲むボロノイ線分のアレイリストを得る。
        Senb_boro_1p_motome(lineStep.size() - 1);

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

                            if ((t_begin.sameSide(lineStep.get(lineStep.size() - 1).getA(), s_kizon.getA()) >= 0) &&
                                    (t_begin.sameSide(lineStep.get(lineStep.size() - 1).getA(), s_kizon.getB()) >= 0)) {
                                voronoiLineSet.select(j);
                            }

                            if ((t_begin.sameSide(lineStep.get(lineStep.size() - 1).getA(), s_kizon.getA()) == -1) &&
                                    (t_begin.sameSide(lineStep.get(lineStep.size() - 1).getA(), s_kizon.getB()) == 1)) {
                                voronoiLineSet.set(j, s_kizon.getA(), kouten);
                            }

                            if ((t_begin.sameSide(lineStep.get(lineStep.size() - 1).getA(), s_kizon.getA()) == 1) &&
                                    (t_begin.sameSide(lineStep.get(lineStep.size() - 1).getA(), s_kizon.getB()) == -1)) {
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

        LineSegment s = new LineSegment();
        s.set(e_s_dougubako.getLengthenUntilIntersectionLineSegment_new());
        lineStepAdd(s);
        s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

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

    public int getDrawingStage() {
        return lineStep.size();
    }

    /**
     * Used when OperationFrame is temporarily hidden.
     *
     * @param i New number of steps.
     */
    public void setDrawingStage(int i) {
        lineStep.clear();

        for (int j = 0; j < i; j++) {
            lineStepAdd(new LineSegment());
        }
    }

    public int getCandidateSize() {
        return lineCandidate.size();
    }

    public void select_all() {
        foldLineSet.select_all();
    }

    public void unselect_all() {
        foldLineSet.unselect_all();
    }

    public void select(Point p0a, Point p0b) {
        foldLineSet.select(createBox(p0a, p0b));
    }

    public void unselect(Point p0a, Point p0b) {
        foldLineSet.unselect(createBox(p0a, p0b));
    }

    public boolean deleteInside_foldingLine(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_foldingLine(createBox(p0a, p0b));
    }

    public boolean deleteInside_edge(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_edge(createBox(p0a, p0b));
    }

    public boolean deleteInside_aux(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_aux(createBox(p0a, p0b));
    }

    public boolean change_property_in_4kakukei(Point p0a, Point p0b) {
        return foldLineSet.change_property_in_4kakukei(createBox(p0a, p0b), customCircleColor);
    }

    public boolean deleteInside(Point p0a, Point p0b) {
        return auxLines.deleteInside(createBox(p0a, p0b));
    }

    public int MV_change(Point p0a, Point p0b) {
        return foldLineSet.MV_change(createBox(p0a, p0b));
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
            i_lineSegment_intersection_flg = OritaCalc.determineLineSegmentIntersectionSweet(s0, foldLineSet.get(i), 0.00001, 0.00001);//20180408なぜかこの行の様にs0のままだと、i_senbun_kousa_flgがおかしくならない。
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

    public void v_del_all() {
        int sousuu_old = foldLineSet.getTotal();
        foldLineSet.del_V_all();
        if (sousuu_old != foldLineSet.getTotal()) {
            record();
        }
    }

//20201024高密度入力がオンならばapのrepaint（画面更新）のたびにTen kus_sisuu=new Ten(mainDrawingWorker.get_moyori_ten_sisuu(p_mouse_TV_iti));で最寄り点を求めているので、この描き職人内で別途最寄り点を求めていることは二度手間になっている。

    public void v_del_all_cc() {
        int sousuu_old = foldLineSet.getTotal();
        foldLineSet.del_V_all_cc();
        if (sousuu_old != foldLineSet.getTotal()) {
            record();
        }
    }

    public void all_s_step_to_orisen() {//20181014

        LineSegment add_sen = new LineSegment();
        for (LineSegment s : lineStep) {
            if (s.getLength() > 0.00000001) {
                add_sen.set(s);
                add_sen.setColor(lineColor);
                addLineSegment(add_sen);
            } else {
                addCircle(s.getAX(), s.getAY(), 5.0, LineColor.CYAN_3);
            }
        }
        record();
    }

    public boolean insideToMountain(Point p0a, Point p0b) {
        return foldLineSet.insideToMountain(createBox(p0a, p0b));
    }

    public boolean insideToValley(Point p0a, Point p0b) {
        return foldLineSet.insideToValley(createBox(p0a, p0b));
    }

    private Polygon createBox(Point p0a, Point p0b) {
        Point p_a = camera.TV2object(new Point(p0a.getX(), p0a.getY()));
        Point p_b = camera.TV2object(new Point(p0a.getX(), p0b.getY()));
        Point p_c = camera.TV2object(new Point(p0b.getX(), p0b.getY()));
        Point p_d = camera.TV2object(new Point(p0b.getX(), p0a.getY()));

        return new Polygon(p_a, p_b, p_c, p_d);
    }

    public boolean insideToEdge(Point p0a, Point p0b) {
        return foldLineSet.insideToEdge(createBox(p0a, p0b));
    }

    public boolean insideToAux(Point p0a, Point p0b) {
        return foldLineSet.insideToAux(createBox(p0a, p0b));
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
        while (true) {
            if (!foldLineSet.fix1(r_hitosii, heikou_hantei)) break;
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
        while (true) {
            if (!foldLineSet.fix2(r_hitosii, heikou_hantei)) break;
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

    public void ap_check4(double r) throws InterruptedException {
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

    //30 30 30 30 30 30 30 30 30 30 30 30 除け_線_変換
    enum FourPointStep {
        STEP_0,
        STEP_1,
        STEP_2,
    }

    public enum OperationFrameMode {
        NONE_0,
        CREATE_1,
        MOVE_POINTS_2,
        MOVE_SIDES_3,
        MOVE_BOX_4,
    }
}
