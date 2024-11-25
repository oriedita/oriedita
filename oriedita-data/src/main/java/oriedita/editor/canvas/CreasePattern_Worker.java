package oriedita.editor.canvas;

import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.Grid;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.CustomLineTypes;
import oriedita.editor.save.Save;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public interface CreasePattern_Worker {
    void lineStepAdd(LineSegment s);

    void clearCreasePattern();

    void reset();

    void initialize();

    void setSave_for_reading_tuika(Save memo1);

    void setSaveForPaste(Save save1);

    void setAuxMemo(Save memo1);

    void setCamera(Camera cam0);

    void setSave_for_reading(Save memo);

    void allMountainValleyChange();

    void branch_trim();

    LineSegmentSet get();

    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    int getFoldLineTotalForSelectFolding();

    LineSegmentSet getForSelectFolding();

    int getTotal();

    Save getSave_for_export();

    Save getSave_for_export_with_applicationModel();

    void saveAdditionalInformation(Save memo1);

    void point_removal();

    void overlapping_line_removal();

    String undo();

    String redo();

    void setTitle(String s_title0);

    void record();

    void auxUndo();

    void auxRedo();

    void auxRecord();

    //------------------------------------------------------------------------------
    //Drawing the basic branch
    //------------------------------------------------------------------------------
    void drawWithCamera(Graphics g, boolean displayComments, boolean displayCpLines, boolean displayAuxLines, boolean displayAuxLiveLines, float lineWidth, LineStyle lineStyle, float f_h_WireframeLineWidth, int p0x_max, int p0y_max, boolean i_mejirusi_display, boolean hideOperationFrame);

    void resetCircleStep();

    // ------------------------------------
    void setGridInputAssist(boolean i);

    void addCircle(Circle e0);

    void addCircle(Point t0, double dr, LineColor ic);

    void addCircle(double dx, double dy, double dr, LineColor ic);

    FoldLineSet getAuxFoldLineSet();

    void addLineSegment_auxiliary(LineSegment s0);

    void addLineSegment(LineSegment s0);

    Point getClosestPoint(Point t0);

    //------------------------------
    LineSegment getClosestLineSegment(Point t0);

    //------------------------------------------------------
    LineSegment getClosestLineStepSegment(Point t0, int imin, int imax);

    //------------------------------
    Circle getClosestCircleMidpoint(Point t0);

    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える
    Point getGridPosition(Point p0);

    void resetLineStep(int i);

    int getCandidateSize();

    void refreshIsSelectionEmpty();

    void setIsSelectionEmpty(boolean isSelectionEmpty);

    boolean getIsSelectionEmpty();

    void select_all();

    void unselect_all();

    void unselect_all(boolean ignorePersistent);

    void select(Point p0a, Point p0b);

    void unselect(Point p0a, Point p0b);

    boolean deleteInside_foldingLine(Point p0a, Point p0b);

    boolean deleteInside_edge(Point p0a, Point p0b);

    boolean deleteInside_aux(Point p0a, Point p0b);

    boolean deleteInside_text(Point p1, Point p2);

    boolean change_property_in_4kakukei(Point p0a, Point p0b);

    boolean deleteInside(Point p0a, Point p0b);

    int MV_change(Point p0a, Point p0b);

    LineSegment extendToIntersectionPoint(LineSegment s0);

    //-------------------------
    void del_selected_senbun();

    void v_del_all();

    void v_del_all_cc();

    void addPreviewLinesToCp();

    boolean insideToMountain(Point p0a, Point p0b);

    boolean insideToValley(Point p0a, Point p0b);

    boolean insideToEdge(Point p0a, Point p0b);

    boolean insideToAux(Point p0a, Point p0b);

    boolean insideToReplaceType(Point p0a, Point p0b, CustomLineTypes from, CustomLineTypes to);

    boolean insideToDeleteType(Point p0a, Point p0b, CustomLineTypes del);

    void setFoldLineDividingNumber(int i);

    void setNumPolygonCorners(int i);

    void setFoldLineAdditional(FoldLineAdditionalInputMode i);

    void check1()//In foldLineSet, check and set the funny fold line to the selected state.
    ;

    void fix1();

    void set_i_check1(boolean i);

    void check2();

    void fix2();

    void setCheck2(boolean i);

    void check3();

    void check4();

    void setCheck4(boolean i);

    void lightenCheck4Color();

    void darkenCheck4Color();

    void setAuxLineColor(LineColor i);

    void organizeCircles();

    double getSelectionDistance();

    void setData(PropertyChangeEvent e, ApplicationModel data);

    void setData(CanvasModel data);

    void setData(AngleSystemModel angleSystemModel);

    Point getCameraPosition();

    void selectConnected(Point p);

    java.util.List<LineSegment> getLineStep();

    GeneralPath getLinePath();

    Camera getCamera();

    boolean getGridInputAssist();

    LineColor getLineColor();

    List<LineSegment> getLineCandidate();

    FoldLineSet getFoldLineSet();

    int getPointSize();

    List<Circle> getCircleStep();

    Grid getGrid();

    FoldLineAdditionalInputMode getI_foldLine_additional();

    LineColor getAuxLineColor();

    FoldLineSet getAuxLines();

    boolean isCheck1();

    boolean isCheck2();

    boolean isCheck3();

    boolean isCheck4();

    OperationFrame getOperationFrame();

    int getNumPolygonCorners();

    Color getCustomCircleColor();

    CanvasModel.SelectionOperationMode getI_select_mode();

    int getFoldLineDividingNumber();

    TextWorker getTextWorker();

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void setGridConfigurationData(GridModel gridModel);

    //30 30 30 30 30 30 30 30 30 30 30 30 除け_線_変換
    enum FourPointStep {
        STEP_0,
        STEP_1,
        STEP_2,
    }

    enum OperationFrameMode {
        NONE_0,
        CREATE_1,
        MOVE_POINTS_2,
        MOVE_SIDES_3,
        MOVE_BOX_4,
    }
}
