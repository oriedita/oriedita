package oriedita.editor.canvas.impl;

import org.tinylog.Logger;
import oriedita.editor.Colors;
import oriedita.editor.canvas.*;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.Grid;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.save.Save;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.task.CheckCAMVTask;
import origami.Epsilon;
import origami.crease_pattern.FlatFoldabilityViolation;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import origami.crease_pattern.element.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for holding the current creasepattern and drawing it.
 */
@Singleton
public class CreasePattern_Worker_Impl implements CreasePattern_Worker {
        // ------------
    final int check4ColorTransparencyIncrement = 10;
    private final LineSegmentSet lineSegmentSet = new LineSegmentSet();    //Instantiation of basic branch structure
    private final Camera creasePatternCamera;
    private final TaskExecutorService camvTaskExecutor;
    private final CanvasModel canvasModel;
    private final ApplicationModel applicationModel;
    private final GridModel gridModel;
    private final FoldedFigureModel foldedFigureModel;

    public final TextWorker textWorker;
    private final FileModel fileModel;
    public FoldLineSet foldLineSet = new FoldLineSet();    //Store polygonal lines
    public Grid grid = new Grid();
    public Polygon operationFrameBox = new Polygon(4);    //Instantiation of selection box (TV coordinates)
    public int pointSize = 1;
    public LineColor lineColor;//Line segment color
    public LineColor auxLineColor = LineColor.ORANGE_4;//Auxiliary line color
    public boolean gridInputAssist = false;//1 if you use the input assist function for fine grid display, 0 if you do not use it
    public Color customCircleColor;//Stores custom colors for circles and auxiliary hot lines
    HistoryState historyState;
    HistoryState auxHistoryState;
    public FoldLineAdditionalInputMode i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;//= 0 is polygonal line input = 1 is auxiliary line input mode (when inputting a line segment, these two). When deleting a line segment, the value becomes as follows. = 0 is the deletion of the polygonal line, = 1 is the deletion of the auxiliary picture line, = 2 is the deletion of the black line, = 3 is the deletion of the auxiliary live line, = 4 is the folding line, the auxiliary live line and the auxiliary picture line.
    public FoldLineSet auxLines = new FoldLineSet();    //Store auxiliary lines
    public int id_angle_system = 8;//180 / id_angle_system represents the angular system. For example, if id_angle_system = 3, 180/3 = 60 degrees, if id_angle_system = 5, 180/5 = 36 degrees
    public int foldLineDividingNumber = 1;
    public double d_restricted_angle_1;
    public double d_restricted_angle_2;
    public double d_restricted_angle_3;
    public int numPolygonCorners = 5;
    public double selectionDistance = 50.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Value for determining whether an input point is close to an existing point or line segment
    /**
     * Temporary line segments when drawing.
     */
    public List<LineSegment> lineStep = new ArrayList<>();
    /**
     * Temporary circles when drawing.
     */
    public List<Circle> circleStep = new ArrayList<>();
    /**
     * Candidate line segments.
     */
    public List<LineSegment> lineCandidate = new ArrayList<>();
    String text_cp_setumei;
    String s_title; //Used to hold the title that appears at the top of the frame
    public Camera camera = new Camera();
    public boolean check1 = false;//=0 check1を実施しない、1=実施する　　
    public boolean check2 = false;//=0 check2を実施しない、1=実施する　
    public boolean check3 = false;//=0 check3を実施しない、1=実施する　
    public boolean check4 = false;//=0 check4を実施しない、1=実施する　
    //---------------------------------
    int check4ColorTransparency = 100;
    //mouseMode==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
    public Point operationFrame_p1 = new Point();//TV座標
    public Point operationFrame_p2 = new Point();//TV座標
    public Point operationFrame_p3 = new Point();//TV座標
    public Point operationFrame_p4 = new Point();//TV座標
    Point p = new Point();
    // ****************************************************************************************************************************************
    // **************　Variable definition so far　****************************************************************************************************
    // ****************************************************************************************************************************************
    // ------------------------------------------------------------------------------------------------------------
    // Sub-operation mode for MouseMode.FOLDABLE_LINE_DRAW_71, either DRAW_CREASE_FREE_1, or VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38
    //--------------------------------------------
    public CanvasModel.SelectionOperationMode i_select_mode = CanvasModel.SelectionOperationMode.NORMAL_0;//=0は通常のセレクト操作
    private final SelectedTextModel textModel;

    @Inject
    public CreasePattern_Worker_Impl(@Named("creasePatternCamera") Camera creasePatternCamera,
                                @Named("normal") HistoryState normalHistoryState,
                                @Named("aux") HistoryState auxHistoryState,
                                @Named("auxlines") FoldLineSet auxLines,
                                @Named("foldlines") FoldLineSet foldLineSet,
                                @Named("camvExecutor") TaskExecutorService camvTaskExecutor,
                                CanvasModel canvasModel,
                                ApplicationModel applicationModel,
                                GridModel gridModel,
                                FoldedFigureModel foldedFigureModel,
                                FileModel fileModel,
                                AngleSystemModel angleSystemModel,
                                TextWorker textWorker,
                                SelectedTextModel textModel) {
        this.creasePatternCamera = creasePatternCamera;  //コンストラクタ
        this.historyState = normalHistoryState;
        this.auxHistoryState = auxHistoryState;
        this.camvTaskExecutor = camvTaskExecutor;
        this.canvasModel = canvasModel;
        this.applicationModel = applicationModel;
        this.gridModel = gridModel;
        this.foldedFigureModel = foldedFigureModel;
        this.fileModel = fileModel;
        this.textWorker = textWorker;
        this.textModel = textModel;

        this.auxLines = auxLines;
        this.foldLineSet = foldLineSet;

        if (applicationModel != null) applicationModel.addPropertyChangeListener(e -> setData(e, applicationModel));
        if (gridModel != null) gridModel.addPropertyChangeListener(e -> setGridConfigurationData(gridModel));
        if (angleSystemModel != null) angleSystemModel.addPropertyChangeListener(e -> setData(angleSystemModel));
        if (canvasModel != null) canvasModel.addPropertyChangeListener(e -> setData(canvasModel));
        if (fileModel != null) fileModel.addPropertyChangeListener(e -> setTitle(fileModel.determineFrameTitle()));

        lineColor = LineColor.BLACK_0;

        text_cp_setumei = "1/";
        s_title = "no title";

        reset();
    }

    @Override public void lineStepAdd(LineSegment s) {
        LineSegment s0 = s.clone();
        s0.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
        lineStep.add(s0);
    }

    public void setGridConfigurationData(GridModel gridModel) {
        grid.setGridConfigurationData(gridModel);
        text_cp_setumei = "1/" + grid.getGridSize();
        calculateDecisionWidth();
    }

    @Override public void clearCreasePattern() {
        foldLineSet.reset();
        auxLines.reset();
        initialize();

        camera.reset();
        lineStep.clear();
        circleStep.clear();
    }

    @Override public void reset() {
        foldLineSet.reset();
        auxLines.reset();

        historyState.reset();
        auxHistoryState.reset();

        camera.reset();
        lineStep.clear();
        circleStep.clear();
    }

    @Override public void initialize() {
        //Enter the paper square (start)
        foldLineSet.addLine(-200.0, -200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(-200.0, -200.0, 200.0, -200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, 200.0, -200.0, LineColor.BLACK_0);
        //Enter the paper square (end)
    }

    public void Memo_jyouhou_toridasi(Save memo1) {
        if (memo1.getCreasePatternCamera() != null) {
            creasePatternCamera.setCamera(memo1.getCreasePatternCamera());
        }

        if (memo1.getApplicationModel() != null) {
            applicationModel.set(memo1.getApplicationModel());
        }

        if (memo1.getCanvasModel() != null) {
            canvasModel.set(memo1.getCanvasModel());
        }

        if (memo1.getGridModel() != null) {
            gridModel.set(memo1.getGridModel());
        }

        if (memo1.getFoldedFigureModel() != null) {
            foldedFigureModel.setFrontColor(memo1.getFoldedFigureModel().getFrontColor());
            foldedFigureModel.setBackColor(memo1.getFoldedFigureModel().getBackColor());
            foldedFigureModel.setLineColor(memo1.getFoldedFigureModel().getLineColor());
        }

        textModel.reset();
    }

    public String setMemo_for_redo_undo(Save save) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        textWorker.setSave(save);
        textModel.setSelected(false);
        return foldLineSet.setSave(save);
    }

    public void setSave_for_reading(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<For reading data
        Memo_jyouhou_toridasi(memo1);
        foldLineSet.setSave(memo1);
        auxLines.setAuxSave(memo1);
        textWorker.setSave(memo1);
    }

    @Override public void setSave_for_reading_tuika(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<For reading data
        double addx, addy;

        FoldLineSet tempFoldLineSet = new FoldLineSet();    //追加された折線だけ取り出すために使う
        tempFoldLineSet.setSave(memo1);//追加された折線だけ取り出してori_s_tempを作る
        addx = foldLineSet.get_x_max() + 100.0 - tempFoldLineSet.get_x_min();
        addy = foldLineSet.get_y_max() - tempFoldLineSet.get_y_max();

        tempFoldLineSet.move(addx, addy);//全体を移動する

        int total_old = foldLineSet.getTotal();
        Save save = Save.createInstance();
        tempFoldLineSet.getSave(save);
        foldLineSet.addSave(save);
        int total_new = foldLineSet.getTotal();
        foldLineSet.divideLineSegmentWithNewLines(total_old, total_new);

        foldLineSet.unselect_all();
        record();
    }

    @Override public void setSaveForPaste(Save save1) {
        int total_old = foldLineSet.getTotal();
        foldLineSet.addSave(save1);
        int total_new = foldLineSet.getTotal();
        foldLineSet.divideLineSegmentWithNewLines(total_old, total_new);

        foldLineSet.unselect_all();
        record();
    }

    @Override public void setAuxMemo(Save memo1) {
        auxLines.setAuxSave(memo1);
    }

    @Override public void setCamera(Camera cam0) {
        camera.setCamera(cam0);

        calculateDecisionWidth();
    }

    public void setCustomCircleColor(Color c0) {
        customCircleColor = c0;
    }

    @Override public void allMountainValleyChange() {
        foldLineSet.allMountainValleyChange();
        checkIfNecessary();
    }

    @Override public void branch_trim() {
        foldLineSet.applyBranchTrim();
    }

    @Override public LineSegmentSet get() {
        Save save = Save.createInstance();
        foldLineSet.getSave(save);
        lineSegmentSet.setSave(save);
        return lineSegmentSet;
    }

    @Override public LineSegmentSet getForFolding() {
        Save save = Save.createInstance();
        foldLineSet.getMemo_for_folding(save);
        lineSegmentSet.setSave(save);
        return lineSegmentSet;
    }

    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    @Override public int getFoldLineTotalForSelectFolding() {
        return foldLineSet.getFoldLineTotalForSelectFolding();
    }

    @Override public LineSegmentSet getForSelectFolding() {//selectした折線で折り畳み推定をする。
        Save save = Save.createInstance();
        foldLineSet.getSaveForSelectFolding(save);
        LineSegmentSet ls = new LineSegmentSet();
        ls.setSave(save);
        return ls;
    }

    public void calculateDecisionWidth() {
        selectionDistance = grid.getGridWidth() / 4.0;
        if (camera.getCameraZoomX() * selectionDistance < 10.0) {
            selectionDistance = 10.0 / camera.getCameraZoomX();
        }
    }

    @Override public int getTotal() {
        return foldLineSet.getTotal();
    }

    public Save getSave(String title) {
        Save save_temp = Save.createInstance();
        foldLineSet.getSave(save_temp, title);

        saveAdditionalInformation(save_temp);
        return save_temp;
    }

    public Save h_getSave() {
        Save save = Save.createInstance();
        auxLines.h_getSave(save);
        return save;
    }

    @Override public Save getSave_for_export() {
        Save save = Save.createInstance();
        foldLineSet.getSave(save);
        auxLines.h_getSave(save);
        saveAdditionalInformation(save);

        return save;
    }

    @Override public Save getSave_for_export_with_applicationModel() {
        Save save = getSave_for_export();

        save.setApplicationModel(applicationModel);

        return save;
    }

    @Override public void saveAdditionalInformation(Save memo1) {
        Camera camera = new Camera();
        camera.setCamera(this.camera);
        memo1.setCreasePatternCamera(camera);

        textWorker.getSave(memo1);

        memo1.setCanvasModel(canvasModel);
        memo1.setGridModel(gridModel);

        memo1.setFoldedFigureModel(foldedFigureModel);
    }

    public void setColor(LineColor i) {
        lineColor = i;
    }

    @Override public void point_removal() {
        foldLineSet.removePoints();
    }

    @Override public void overlapping_line_removal() {
        foldLineSet.removeOverlappingLines();
    }

    @Override public String undo() {
        s_title = setMemo_for_redo_undo(historyState.undo());
        checkIfNecessary();
        return s_title;
    }

    @Override public String redo() {
        s_title = setMemo_for_redo_undo(historyState.redo());
        checkIfNecessary();
        return s_title;
    }

    @Override public void setTitle(String s_title0) {
        s_title = s_title0;
    }

    @Override public void record() {
        checkIfNecessary();

        if (!historyState.isEmpty()) {
            fileModel.setSaved(false);
        }

        historyState.record(getSave(s_title));
    }

    @Override public void auxUndo() {
        setAuxMemo(auxHistoryState.undo());
    }

    @Override public void auxRedo() {
        setAuxMemo(auxHistoryState.redo());
    }

    @Override public void auxRecord() {
        auxHistoryState.record(h_getSave());
    }

    //------------------------------------------------------------------------------
    //Drawing the basic branch
    //------------------------------------------------------------------------------
    @Override public void drawWithCamera(Graphics g, boolean displayComments, boolean displayCpLines, boolean displayAuxLines, boolean displayAuxLiveLines, float lineWidth, LineStyle lineStyle, float f_h_WireframeLineWidth, int p0x_max, int p0y_max, boolean i_mejirusi_display, boolean hideOperationFrame) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        Graphics2D g2 = (Graphics2D) g;

        //Drawing grid lines
        grid.draw(g, camera, p0x_max, p0y_max, gridInputAssist);

        BasicStroke BStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//Line thickness and shape of the end of the line

        //Drawing auxiliary strokes (non-interfering with polygonal lines)
        if (displayAuxLiveLines) {
            g2.setStroke(new BasicStroke(f_h_WireframeLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//Line thickness and shape of the end of the line
            for (int i = 1; i <= auxLines.getTotal(); i++) {
                LineSegment as = auxLines.get(i);
                DrawingUtil.drawAuxLiveLine(g, as, camera, lineWidth, pointSize, f_h_WireframeLineWidth);
            }
        }

        //check結果の表示

        g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

        //Check1Senbには0番目からsize()-1番目までデータが入っている
        if (check1) {
            for (LineSegment s_temp : foldLineSet.getCheck1LineSegments()) {
                DrawingUtil.pointingAt1(g, camera.object2TV(s_temp));
            }
        }

        if (check2) {
            for (LineSegment s_temp : foldLineSet.getCheck2LineSegments()) {
                DrawingUtil.pointingAt2(g, camera.object2TV(s_temp));
            }
        }

        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定


        //Check4Senbには0番目からsize()-1番目までデータが入っている
        //Logger.info("foldLineSet.check4_size() = "+foldLineSet.check4_size());
        if (check4) {
            for (FlatFoldabilityViolation violation : foldLineSet.getViolations()) {
                DrawingUtil.drawViolation(g2, camera.object2TV(violation.getPoint()), violation,
                        check4ColorTransparency, applicationModel.getAdvancedCheck4Display());
            }

            if (displayComments) {

                if (camvTaskExecutor.isTaskRunning()) {
                    g.setColor(Colors.get(Color.orange));
                    g.drawString("... cAMV Errors", p0x_max - 100, 10);
                } else {
                    int numErrors = foldLineSet.getViolations().size();
                    if (numErrors == 0) {
                        g.setColor(Colors.get(Color.green));
                    } else {
                        g.setColor(Colors.get(Color.red));
                    }

                    g.drawString(numErrors + " cAMV Errors", p0x_max - 100, 10);
                }
            }
        }


        //Check3Senbには0番目からsize()-1番目までデータが入っている
        if (check3) {
            for (LineSegment s_temp : foldLineSet.getCheck3LineSegments()) {
                DrawingUtil.pointingAt3(g, camera.object2TV(s_temp));
            }
        }

        //Draw the center of the camera with a cross
        if (i_mejirusi_display) {
            DrawingUtil.cross(g, camera.object2TV(camera.getCameraPosition()), 5.0, 2.0, LineColor.CYAN_3);
        }

        //円を描く　
        if (displayAuxLines) {
            for (Circle circle : foldLineSet.getCircles()) {
                DrawingUtil.drawCircle(g, circle, camera, lineWidth, pointSize);
            }
        }

        //selectの描画
        g2.setStroke(new BasicStroke(lineWidth * 2.0f + 2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            LineSegment s = foldLineSet.get(i);
            if (s.getSelected() == 2) {
                DrawingUtil.drawSelectLine(g, s, camera);
            }
        }

        //展開図の描画 補助活線のみ
        if (displayAuxLines) {
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineSegment s = foldLineSet.get(i);
                if (s.getColor() == LineColor.CYAN_3) {

                    DrawingUtil.drawAuxLine(g, s, camera, lineWidth, pointSize);
                }
            }
        }

        //展開図の描画  補助活線以外の折線
        if (displayCpLines) {
            g.setColor(Colors.get(Color.black));
            for (int i = 1; i <= foldLineSet.getTotal(); i++) {
                LineSegment s = foldLineSet.get(i);
                if (s.getColor() != LineColor.CYAN_3) {
                    DrawingUtil.drawCpLine(g, s, camera, lineStyle, lineWidth, pointSize, p0x_max, p0y_max);
                }
            }
        }

        //mouseMode==61//長方形内選択（paintの選択に似せた選択機能）の時に使う
        if (!hideOperationFrame && canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61 && lineStep.size() == 4) {
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

        if (!hideOperationFrame && ((canvasModel.getMouseMode() != MouseMode.OPERATION_FRAME_CREATE_61) || (lineStep.size() == 4))) {
            for (LineSegment s : lineStep) {
                DrawingUtil.drawLineStep(g, s, camera, lineWidth, gridInputAssist);
            }
        }
        //候補入力時の候補を描く//Logger.info("_");
        g2.setStroke(new BasicStroke(lineWidth + 0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A

        for (LineSegment s : lineCandidate) {
            DrawingUtil.drawLineCandidate(g, s, camera, pointSize);
        }

        g.setColor(Colors.get(Color.black));

        for (Circle c : circleStep) {
            DrawingUtil.drawCircleStep(g, c, camera);
        }

        g.setColor(Colors.get(Color.black));

        if (displayComments) {
            g.drawString(text_cp_setumei, 10, 55);
            textWorker.draw(g2, camera);
        }
    }

    //--------------------------------------------------------------------------------------
    //Mouse operation----------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------

    @Override public void resetCircleStep() {
        circleStep.clear();
    }


    //動作モデル00a--------------------------------------------------------------------------------------------------------
    //マウスクリック（マウスの近くの既成点を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（マウスの近くの既成点を選択）してから目的の処理をする雛形セット

    public void set_id_angle_system(int i) {
        id_angle_system = i;
    }

    // ------------------------------------
    @Override public void setGridInputAssist(boolean i) {
        gridInputAssist = i;

        if (!gridInputAssist) {
            for (LineSegment candidate : lineCandidate) {
                candidate.deactivate();
            }
        }
    }

    @Override public void addCircle(Circle e0) {
        addCircle(e0.getX(), e0.getY(), e0.getR(), e0.getColor());
    }

    @Override public void addCircle(Point t0, double dr, LineColor ic) {
        addCircle(t0.getX(), t0.getY(), dr, ic);
    }


    //動作モデル00b--------------------------------------------------------------------------------------------------------
    //マウスクリック（近くの既成点かマウス位置を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（近くの既成点かマウス位置を選択）してから目的の処理をする雛形セット

    @Override public void addCircle(double dx, double dy, double dr, LineColor ic) {
        foldLineSet.addCircle(dx, dy, dr, ic);

        int imin = 0;
        int imax = foldLineSet.numCircles() - 2;
        int jmin = foldLineSet.numCircles() - 1;
        int jmax = foldLineSet.numCircles() - 1;

        foldLineSet.applyCircleCircleIntersection(imin, imax, jmin, jmax);
        foldLineSet.applyLineSegmentCircleIntersection(1, foldLineSet.getTotal(), jmin, jmax);

    }

    @Override public FoldLineSet getAuxFoldLineSet() {
        return auxLines;
    }

    @Override public void addLineSegment_auxiliary(LineSegment s0) {
        auxLines.addLine(s0);
    }

    @Override public void addLineSegment(LineSegment s0) {//0 = No change, 1 = Color change only, 2 = Line segment added
        foldLineSet.addLine(s0);//Just add the information of s0 to the end of senbun of foldLineSet
        int total_old = foldLineSet.getTotal();
        foldLineSet.applyLineSegmentCircleIntersection(foldLineSet.getTotal(), foldLineSet.getTotal(), 0, foldLineSet.numCircles() - 1);

        foldLineSet.divideLineSegmentWithNewLines(total_old - 1, total_old);
    }

    @Override public Point getClosestPoint(Point t0) {
        // When dividing paper 1/1 Only the end point of the folding line is the reference point. The grid point never becomes the reference point.
        // When dividing paper from 1/2 to 1/512 The end point of the polygonal line and the grid point in the paper frame (-200.0, -200.0 _ 200.0, 200.0) are the reference points.
        Point t1 = new Point(); //End point of the polygonal line
        Point t3 = new Point(); //Center of circle

        t1.set(foldLineSet.closestPoint(t0)); // foldLineSet.closestPoint returns (100000.0,100000.0) if there is no close point

        t3.set(foldLineSet.closestCenter(t0)); // foldLineSet.closestCenter returns (100000.0,100000.0) if there is no close point

        if (t0.distanceSquared(t1) > t0.distanceSquared(t3)) {
            t1.set(t3);
        }

        if (grid.getBaseState() == GridModel.State.HIDDEN) {
            return t1;
        }

        if (t0.distanceSquared(t1) > t0.distanceSquared(grid.closestGridPoint(t0))) {
            return grid.closestGridPoint(t0);
        }

        return t1;
    }

    //------------------------------
    @Override public LineSegment getClosestLineSegment(Point t0) {
        return foldLineSet.getClosestLineSegment(t0);
    }

    //------------------------------------------------------
    @Override public LineSegment get_moyori_step_lineSegment(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;
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
    @Override public Circle getClosestCircleMidpoint(Point t0) {
        return foldLineSet.closestCircleMidpoint(t0);
    }


    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える
    @Override public Point getGridPosition(Point p0) {
        p.set(camera.TV2object(p0));
        Point closestPoint = getClosestPoint(p);
        return new Point(grid.getPosition(closestPoint));
    }

    @Override public int getDrawingStage() {
        return lineStep.size();
    }

    /**
     * Used when OperationFrame is temporarily hidden.
     *
     * @param i New number of steps.
     */
    @Override public void setDrawingStage(int i) {
        lineStep.clear();

        for (int j = 0; j < i; j++) {
            lineStepAdd(new LineSegment());
        }
    }

    @Override public int getCandidateSize() {
        return lineCandidate.size();
    }

    @Override public void select_all() {
        foldLineSet.select_all();
    }

    @Override public void unselect_all() {
        foldLineSet.unselect_all();
    }

    @Override public void select(Point p0a, Point p0b) {
        foldLineSet.select(createBox(p0a, p0b));
    }

    @Override public void unselect(Point p0a, Point p0b) {
        foldLineSet.unselect(createBox(p0a, p0b));
    }

    @Override public boolean deleteInside_foldingLine(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_foldingLine(createBox(p0a, p0b));
    }

    @Override public boolean deleteInside_edge(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_edge(createBox(p0a, p0b));
    }

    @Override public boolean deleteInside_aux(Point p0a, Point p0b) {
        return foldLineSet.deleteInside_aux(createBox(p0a, p0b));
    }

    @Override public boolean deleteInside_text(Point p1, Point p2) {
        if (textWorker.deleteInsideRectangle(p1, p2, camera)) {
            textModel.markDirty();
            return true;
        }
        return false;
    }

    @Override public boolean change_property_in_4kakukei(Point p0a, Point p0b) {
        return foldLineSet.change_property_in_4kakukei(createBox(p0a, p0b), customCircleColor);
    }

    @Override public boolean deleteInside(Point p0a, Point p0b) {
        return auxLines.deleteInside(createBox(p0a, p0b));
    }

    @Override public int MV_change(Point p0a, Point p0b) {
        return foldLineSet.MV_change(createBox(p0a, p0b));
    }

    @Override public LineSegment extendToIntersectionPoint(LineSegment s0) {//Extend s0 from point a to b, until it intersects another polygonal line. Returns a new line // Returns the same line if it does not intersect another polygonal line
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
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
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

    //-------------------------
    @Override public void del_selected_senbun() {
        foldLineSet.delSelectedLineSegmentFast();
    }

    @Override public void v_del_all() {
        try {
            int sousuu_old = foldLineSet.getTotal();
            foldLineSet.del_V_all();
            if (sousuu_old != foldLineSet.getTotal()) {
                record();
            }
        } catch (InterruptedException e) {
            Logger.info("v_del_all aborted");
        }
    }

//20201024高密度入力がオンならばapのrepaint（画面更新）のたびにTen kus_sisuu=new Ten(mainDrawingWorker.get_moyori_ten_sisuu(p_mouse_TV_iti));で最寄り点を求めているので、この描き職人内で別途最寄り点を求めていることは二度手間になっている。

    @Override public void v_del_all_cc() {
        try {
            int sousuu_old = foldLineSet.getTotal();
            foldLineSet.del_V_all_cc();
            if (sousuu_old != foldLineSet.getTotal()) {
                record();
            }
        } catch (InterruptedException e) {
            Logger.info("v_del_all_cc aborted");
        }
    }

    @Override public void all_s_step_to_orisen() {//20181014

        LineSegment add_sen = new LineSegment();
        for (LineSegment s : lineStep) {
            if (Epsilon.high.gt0(s.determineLength())) {
                add_sen.set(s);
                add_sen.setColor(lineColor);
                addLineSegment(add_sen);
            } else {
                addCircle(s.determineAX(), s.determineAY(), 5.0, LineColor.CYAN_3);
            }
        }
        record();
    }

    @Override public boolean insideToMountain(Point p0a, Point p0b) {
        return foldLineSet.insideToMountain(createBox(p0a, p0b));
    }

    @Override public boolean insideToValley(Point p0a, Point p0b) {
        return foldLineSet.insideToValley(createBox(p0a, p0b));
    }

    @Override public boolean insideToEdge(Point p0a, Point p0b) {
        return foldLineSet.insideToEdge(createBox(p0a, p0b));
    }

    @Override public boolean insideToAux(Point p0a, Point p0b) {
        return foldLineSet.insideToAux(createBox(p0a, p0b));
    }

    @Override public void setFoldLineDividingNumber(int i) {
        foldLineDividingNumber = i;
        if (foldLineDividingNumber < 1) {
            foldLineDividingNumber = 1;
        }
    }

    @Override public void set_d_restricted_angle(double d_1, double d_2, double d_3) {
        d_restricted_angle_1 = d_1;
        d_restricted_angle_2 = d_2;
        d_restricted_angle_3 = d_3;
    }

    @Override public void setNumPolygonCorners(int i) {
        numPolygonCorners = i;
        if (numPolygonCorners < 3) {
            foldLineDividingNumber = 3;
        }
    }

    @Override public void setFoldLineAdditional(FoldLineAdditionalInputMode i) {
        i_foldLine_additional = i;
    }

    @Override public void check1() {
        foldLineSet.check1();
    }//In foldLineSet, check and set the funny fold line to the selected state.

    @Override public void fix1() {
        while (true) {
            if (!foldLineSet.fix1()) break;
        }
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        checkIfNecessary();
    }

    @Override public void set_i_check1(boolean i) {
        check1 = i;
    }

    @Override public void check2() {
        foldLineSet.check2();
    }

    @Override public void fix2() {
        foldLineSet.fix2();
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        checkIfNecessary();
    }

    private void checkIfNecessary() {
        if (check1) check1();
        if (check2) check2();
        if (check3) check3();
        if (check4) check4();
    }

    @Override public void setCheck2(boolean i) {
        check2 = i;
    }

    @Override public void check3() {
        foldLineSet.check3();
    }

    @Override public void check4() {
        camvTaskExecutor.executeTask(new CheckCAMVTask(foldLineSet, canvasModel));
    }

    @Override public void setCheck3(boolean i) {
        check3 = i;
    }

    @Override public void setCheck4(boolean i) {
        check4 = i;
    }

    @Override public void lightenCheck4Color() {
        check4ColorTransparency = check4ColorTransparency - check4ColorTransparencyIncrement;
        if (check4ColorTransparency < 50) {
            check4ColorTransparency = check4ColorTransparency + check4ColorTransparencyIncrement;
        }
    }

    @Override public void darkenCheck4Color() {
        check4ColorTransparency = check4ColorTransparency + check4ColorTransparencyIncrement;
        if (check4ColorTransparency > 250) {
            check4ColorTransparency = check4ColorTransparency - check4ColorTransparencyIncrement;
        }
    }

    @Override public void setAuxLineColor(LineColor i) {
        auxLineColor = i;
    }

    @Override public void organizeCircles() {//Organize all circles.
        foldLineSet.organizeCircles();
    }

    @Override public double getSelectionDistance() {
        return selectionDistance;
    }

    @Override public void setData(PropertyChangeEvent e, ApplicationModel data) {
        setGridInputAssist(data.getDisplayGridInputAssist());
        pointSize = data.getPointSize();

        setFoldLineDividingNumber(data.getFoldLineDividingNumber());
        setNumPolygonCorners(data.getNumPolygonCorners());
        setCheck4(data.getCheck4Enabled());
        setCustomCircleColor(data.getCircleCustomizedColor());

        if (e.getPropertyName() == null || e.getPropertyName().equals("check4Enabled")) {
            if (data.getCheck4Enabled()) {
                check4();
            } else if (camvTaskExecutor.isTaskRunning()) {
                camvTaskExecutor.stopTask();
            }
        }

        grid.setData(data);
    }

    @Override public void setData(CanvasModel data) {
        setColor(data.calculateLineColor());
        setAuxLineColor(data.getAuxLiveLineColor());
        setFoldLineAdditional(data.getFoldLineAdditionalInputMode());
        i_select_mode = data.getSelectionOperationMode();
    }

    @Override public void setData(AngleSystemModel angleSystemModel) {
        set_id_angle_system(angleSystemModel.getCurrentAngleSystemDivider());
        set_d_restricted_angle(angleSystemModel.getCurrentAngleA(), angleSystemModel.getCurrentAngleB(), angleSystemModel.getCurrentAngleC());

        unselect_all();
    }

    @Override public Point getCameraPosition() {
        return this.camera.getCameraPosition();
    }

    @Override public void selectConnected(Point p) {
        this.foldLineSet.selectProbablyConnected(p);
    }

    @Override
    public List<LineSegment> getLineStep() {
        return lineStep;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public boolean getGridInputAssist() {
        return gridInputAssist;
    }

    private origami.crease_pattern.element.Polygon createBox(Point p0a, Point p0b) {
        Point p_a = camera.TV2object(new Point(p0a.getX(), p0a.getY()));
        Point p_b = camera.TV2object(new Point(p0a.getX(), p0b.getY()));
        Point p_c = camera.TV2object(new Point(p0b.getX(), p0b.getY()));
        Point p_d = camera.TV2object(new Point(p0b.getX(), p0a.getY()));

        return new Polygon(p_a, p_b, p_c, p_d);
    }

    @Override
    public LineColor getLineColor() {
        return lineColor;
    }

    @Override
    public List<LineSegment> getLineCandidate() {
        return lineCandidate;
    }

    @Override
    public FoldLineSet getFoldLineSet() {
        return foldLineSet;
    }

    @Override
    public int getPointSize() {
        return pointSize;
    }

    @Override
    public double[] getAngles() {
        return new double[] {
                d_restricted_angle_1,
                d_restricted_angle_2,
                d_restricted_angle_3,
                360 - d_restricted_angle_1,
                360 - d_restricted_angle_2,
                360 - d_restricted_angle_3
        };
    }

    @Override
    public int getId_angle_system() {
        return id_angle_system;
    }

    @Override
    public List<Circle> getCircleStep() {
        return circleStep;
    }

    @Override
    public Grid getGrid() {
        return grid;
    }

    @Override
    public FoldLineAdditionalInputMode getI_foldLine_additional() {
        return i_foldLine_additional;
    }

    @Override
    public LineColor getAuxLineColor() {
        return auxLineColor;
    }

    @Override
    public FoldLineSet getAuxLines() {
        return auxLines;
    }

    @Override
    public Polygon getOperationFrameBox() {
        return operationFrameBox;
    }

    @Override
    public boolean isCheck1() {
        return check1;
    }

    @Override
    public boolean isCheck2() {
        return check2;
    }

    @Override
    public boolean isCheck3() {
        return check3;
    }

    @Override
    public boolean isCheck4() {
        return check4;
    }

    @Override
    public Point getOperationFrame_p1() {
        return operationFrame_p1;
    }

    @Override
    public Point getOperationFrame_p2() {
        return operationFrame_p2;
    }

    @Override
    public Point getOperationFrame_p3() {
        return operationFrame_p3;
    }

    @Override
    public Point getOperationFrame_p4() {
        return operationFrame_p4;
    }

    @Override
    public int getNumPolygonCorners() {
        return numPolygonCorners;
    }

    @Override
    public Color getCustomCircleColor() {
        return customCircleColor;
    }

    @Override
    public CanvasModel.SelectionOperationMode getI_select_mode() {
        return i_select_mode;
    }

    @Override
    public int getFoldLineDividingNumber() {
        return foldLineDividingNumber;
    }

    @Override
    public TextWorker getTextWorker() {
        return textWorker;
    }
}
