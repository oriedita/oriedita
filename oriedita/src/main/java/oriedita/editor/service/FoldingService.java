package oriedita.editor.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.tinylog.Logger;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.folding.FoldedFigure;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.swing.component.BulletinBoard;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.folded_figure.FoldedFigure_01;
import oriedita.editor.task.FoldingEstimateTask;
import oriedita.editor.task.TaskExecutor;
import oriedita.editor.task.TwoColoredTask;
import oriedita.editor.drawing.tools.Camera;

import javax.inject.Singleton;
import javax.swing.*;

@Singleton
public class FoldingService {
    private final BulletinBoard bulletinBoard;
    private final CanvasModel canvasModel;
    private final JFrame frame;
    private final Camera creasePatternCamera;
    private final CreasePattern_Worker backupCreasePatternWorker;
    private final ApplicationModel applicationModel;
    private final FoldedFigureModel foldedFigureModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFiguresList foldedFiguresList;
    public LineSegmentSet lineSegmentsForFolding;//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(lineSegmentsForFolding)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。

    @Inject
    public FoldingService(BulletinBoard bulletinBoard,
                          CanvasModel canvasModel,
                          @Named("mainFrame") JFrame frame,
                          @Named("creasePatternCamera") Camera creasePatternCamera,
                          @Named("backupCreasePattern_Worker") CreasePattern_Worker backupCreasePatternWorker,
                          ApplicationModel applicationModel,
                          FoldedFigureModel foldedFigureModel,
                          CreasePattern_Worker mainCreasePatternWorker,
                          FoldedFiguresList foldedFiguresList) {
        this.bulletinBoard = bulletinBoard;
        this.canvasModel = canvasModel;
        this.frame = frame;
        this.creasePatternCamera = creasePatternCamera;
        this.backupCreasePatternWorker = backupCreasePatternWorker;
        this.applicationModel = applicationModel;
        this.foldedFigureModel = foldedFigureModel;

        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.foldedFiguresList = foldedFiguresList;
    }

    public void folding_estimated(FoldedFigure_Drawer selectedFigure) throws InterruptedException, FoldingException {
        selectedFigure.folding_estimated(creasePatternCamera, lineSegmentsForFolding);
    }

    public void fold(FoldedFigure.EstimationOrder estimationOrder) {
        fold(getFoldType(), estimationOrder);
    }

    public void fold(FoldType foldType, FoldedFigure.EstimationOrder estimationOrder) {
        if (foldType == FoldType.FOR_ALL_CONNECTED_LINES_1) {
            Point cameraPos = this.mainCreasePatternWorker.getCameraPosition();
            mainCreasePatternWorker.selectConnected(this.mainCreasePatternWorker.foldLineSet.closestPoint(cameraPos));
            // replace currently selected model if not using selection to fold
            foldedFiguresList.removeElement(foldedFiguresList.getSelectedItem());
        }

        if (applicationModel.getCorrectCpBeforeFolding()) {// Automatically correct strange parts (branch-shaped fold lines, etc.) in the crease pattern
            CreasePattern_Worker creasePatternWorker2 = backupCreasePatternWorker;
            Save save = new SaveV1();
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
        FoldedFigure_Drawer selectedFigure = initFoldedFigure();//OAZのアレイリストに、新しく折り上がり図をひとつ追加し、それを操作対象に指定し、foldedFigures(0)共通パラメータを引き継がせる。
        //これより後のOZは新しいOZに変わる

        FoldingEstimateTask foldingEstimateTask = new FoldingEstimateTask(creasePatternCamera, bulletinBoard, canvasModel);
        foldingEstimateTask.execute(lineSegmentsForFolding, selectedFigure, estimationOrder);
    }

    public FoldType getFoldType() {
        //= 0 Do nothing, = 1 Folding estimation for all fold lines in the normal development view, = 2 for fold estimation for selected fold lines, = 3 for changing the folding state
        int foldLineTotalForSelectFolding = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        Logger.info("foldedFigures.size() = " + foldedFiguresList.getSize() + "    : foldedFigureIndex = " + foldedFiguresList.getIndexOf(foldedFiguresList.getSelectedItem()) + "    : mainDrawingWorker.get_orisensuu_for_select_oritatami() = " + foldLineTotalForSelectFolding);
        if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
            return FoldType.FOR_ALL_CONNECTED_LINES_1;//全展開図で折畳み
        } else {        //折り線選択有り
            return FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
        }
    }

    public FoldedFigure_Drawer initFoldedFigure() {//Add one new folding diagram to the foldedFigures array list, specify it as the operation target, and inherit the foldedFigures (0) common parameters.
        Logger.info(" oritatami_jyunbi 20180107");

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

    public void foldAnother(FoldedFigure_Drawer selectedItem) {
        FoldingEstimateTask foldingEstimateTask = new FoldingEstimateTask(creasePatternCamera, bulletinBoard, canvasModel);
        foldingEstimateTask.execute(lineSegmentsForFolding, selectedItem, FoldedFigure.EstimationOrder.ORDER_6);
    }

    public enum FoldType {
        FOR_ALL_CONNECTED_LINES_1,
        FOR_SELECTED_LINES_2
    }
}
