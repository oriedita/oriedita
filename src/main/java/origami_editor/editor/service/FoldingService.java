package origami_editor.editor.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.folding.FoldedFigure;
import origami_editor.editor.Save;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.folded_figure.FoldedFigure_01;
import origami_editor.editor.task.FoldingEstimateTask;
import origami_editor.editor.task.TaskExecutor;
import origami_editor.editor.task.TwoColoredTask;
import origami_editor.tools.Camera;

import javax.swing.*;

@Component
public class FoldingService {
    private final BulletinBoard bulletinBoard;
    private final CanvasModel canvasModel;
    private final JFrame frame;
    private final Camera creasePatternCamera;
    private final ApplicationModel applicationModel;
    private final GridModel gridModel;
    private final FoldedFigureModel foldedFigureModel;
    private final FileModel fileModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFiguresList foldedFiguresList;
    private final InternalDivisionRatioModel internalDivisionRatioModel;
    private final AngleSystemModel angleSystemModel;
    private final HistoryStateModel historyStateModel;
    public LineSegmentSet lineSegmentsForFolding;//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(lineSegmentsForFolding)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。

    public FoldingService(BulletinBoard bulletinBoard,
                          CanvasModel canvasModel,
                          @Qualifier("mainFrame") JFrame frame,
                          @Qualifier("creasePatternCamera") Camera creasePatternCamera,
                          ApplicationModel applicationModel,
                          GridModel gridModel,
                          FoldedFigureModel foldedFigureModel,
                          FileModel fileModel,
                          CreasePattern_Worker mainCreasePatternWorker,
                          FoldedFiguresList foldedFiguresList,
                          InternalDivisionRatioModel internalDivisionRatioModel,
                          AngleSystemModel angleSystemModel,
                          HistoryStateModel historyStateModel) {
        this.bulletinBoard = bulletinBoard;
        this.canvasModel = canvasModel;
        this.frame = frame;
        this.creasePatternCamera = creasePatternCamera;
        this.applicationModel = applicationModel;
        this.gridModel = gridModel;
        this.foldedFigureModel = foldedFigureModel;
        this.fileModel = fileModel;

        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.foldedFiguresList = foldedFiguresList;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
        this.angleSystemModel = angleSystemModel;
        this.historyStateModel = historyStateModel;
    }

    public void folding_estimated(FoldedFigure_Drawer selectedFigure) throws InterruptedException, FoldingException {
        selectedFigure.folding_estimated(creasePatternCamera, lineSegmentsForFolding);
    }


    public void fold(FoldType foldType, FoldedFigure.EstimationOrder estimationOrder) {
        if (foldType == FoldType.NOTHING_0) {
            System.out.println(" oritatame 20180108");
        } else if ((foldType == FoldType.FOR_ALL_LINES_1) || (foldType == FoldType.FOR_SELECTED_LINES_2)) {
            if (foldType == FoldType.FOR_ALL_LINES_1) {
                //d.select_all();
                Point cpPivot = this.mainCreasePatternWorker.getCameraPosition();
                mainCreasePatternWorker.selectConnected(this.mainCreasePatternWorker.foldLineSet.closestPoint(cpPivot));
            }
            //
            if (applicationModel.getCorrectCpBeforeFolding()) {// Automatically correct strange parts (branch-shaped fold lines, etc.) in the crease pattern
                CreasePattern_Worker creasePatternWorker2 = new CreasePattern_Worker(creasePatternCamera,
                        canvasModel,
                        applicationModel,
                        gridModel,
                        foldedFigureModel,
                        fileModel,
                        angleSystemModel,
                        internalDivisionRatioModel,
                        historyStateModel);    // Basic branch craftsman. Accepts input from the mouse.
                Save save = new Save();
                mainCreasePatternWorker.foldLineSet.getSaveForSelectFolding(save);
                creasePatternWorker2.setSave_for_reading(save);
                creasePatternWorker2.point_removal();
                creasePatternWorker2.overlapping_line_removal();
                creasePatternWorker2.branch_trim();
                creasePatternWorker2.organizeCircles();
                lineSegmentsForFolding = creasePatternWorker2.getForFolding();
            } else {
                lineSegmentsForFolding = mainCreasePatternWorker.getForSelectFolding();
            }

            //これより前のOZは古いOZ
            FoldedFigure_Drawer selectedFigure = folding_prepare();//OAZのアレイリストに、新しく折り上がり図をひとつ追加し、それを操作対象に指定し、foldedFigures(0)共通パラメータを引き継がせる。
            //これより後のOZは新しいOZに変わる

            TaskExecutor.executeTask("Folding Estimate", new FoldingEstimateTask(this, bulletinBoard, selectedFigure, estimationOrder, canvasModel));
        } else if (foldType == FoldType.CHANGING_FOLDED_3) {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.foldedFigure.estimationOrder = estimationOrder;
                selectedFigure.foldedFigure.estimationStep = FoldedFigure.EstimationStep.STEP_0;

                TaskExecutor.executeTask("Folding Estimate",new FoldingEstimateTask(this, bulletinBoard, selectedFigure, estimationOrder, canvasModel));
            }
        }
    }

    public FoldType getFoldType() {
        FoldType foldType;//= 0 Do nothing, = 1 Folding estimation for all fold lines in the normal development view, = 2 for fold estimation for selected fold lines, = 3 for changing the folding state
        int foldLineTotalForSelectFolding = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        System.out.println("foldedFigures.size() = " + foldedFiguresList.getSize() + "    : foldedFigureIndex = " + foldedFiguresList.getIndexOf(foldedFiguresList.getSelectedItem()) + "    : mainDrawingWorker.get_orisensuu_for_select_oritatami() = " + foldLineTotalForSelectFolding);
        if (foldedFiguresList.getSize() == 0) {                        //折り上がり系図無し
            if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                foldType = FoldType.FOR_ALL_LINES_1;//全展開図で折畳み
            } else {        //折り線選択有り
                foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
            }
        } else {                        //折り上がり系図有り
            if (foldedFiguresList.getSelectedItem() == null) {                            //展開図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    foldType = FoldType.NOTHING_0;//何もしない
                } else {        //折り線選択有り
                    foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            } else {                        //折り上がり系図指定
                if (foldLineTotalForSelectFolding == 0) {        //No fold line selection
                    foldType = FoldType.CHANGING_FOLDED_3;//Fold with the specified fold-up genealogy
                } else {        //With fold line selection
                    foldType = FoldType.FOR_SELECTED_LINES_2;//Fold in selected crease pattern
                }
            }
        }

        return foldType;
    }
    public FoldedFigure_Drawer folding_prepare() {//Add one new folding diagram to the foldedFigures array list, specify it as the operation target, and inherit the foldedFigures (0) common parameters.
        System.out.println(" oritatami_jyunbi 20180107");

        FoldedFigure_Drawer newFoldedFigure = new FoldedFigure_Drawer(new FoldedFigure_01(bulletinBoard));

        foldedFiguresList.addElement(newFoldedFigure);
        foldedFiguresList.setSelectedItem(newFoldedFigure);

        newFoldedFigure.getData(foldedFigureModel);

        return newFoldedFigure;
    }

    public void createTwoColoredCp() {
        lineSegmentsForFolding = mainCreasePatternWorker.getForSelectFolding();

        if (mainCreasePatternWorker.getFoldLineTotalForSelectFolding() == 0) {        //折り線選択無し
            twoColorNoSelectedPolygonalLineWarning();//Warning: There is no selected polygonal line


        } else if (mainCreasePatternWorker.getFoldLineTotalForSelectFolding() > 0) {
            TaskExecutor.executeTask("Two Colored CP", new TwoColoredTask(bulletinBoard, creasePatternCamera, this, canvasModel));
        }

        mainCreasePatternWorker.unselect_all();
    }

    public void twoColorNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>２色塗りわけ展開図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To get 2-Colored crease pattern, select the target range in advance (use the select button).<html>");
        // TODO fix owner
        JOptionPane.showMessageDialog(frame, label);
    }

    public enum FoldType {
        NOTHING_0,
        FOR_ALL_LINES_1,
        FOR_SELECTED_LINES_2,
        CHANGING_FOLDED_3,
    }
}
