package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.Foldable;
import oriedita.editor.FrameProvider;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.folded_figure.FoldedFigure_01;
import oriedita.editor.service.FoldingService;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.swing.component.BulletinBoard;
import oriedita.editor.task.FoldingEstimateTask;
import oriedita.editor.task.TwoColoredTask;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.folding.FoldedFigure;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

@ApplicationScoped
public class FoldingServiceImpl implements FoldingService {
    private final BulletinBoard bulletinBoard;
    private final CanvasModel canvasModel;
    private final FrameProvider frame;
    private final Camera creasePatternCamera;
    @SuppressWarnings("unused")
    private final CreasePattern_Worker backupCreasePatternWorker;
    private final TaskExecutorService foldingExecutor;
    private final ApplicationModel applicationModel;
    private final FoldedFigureModel foldedFigureModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFiguresList foldedFiguresList;
    private LineSegmentSet lineSegmentsForFolding;//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(lineSegmentsForFolding)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
    private LineSegmentSet lastFold;

    @Inject
    public FoldingServiceImpl(BulletinBoard bulletinBoard,
                              CanvasModel canvasModel,
                              FrameProvider frame,
                              @Named("creasePatternCamera") Camera creasePatternCamera,
                              @Named("backupCreasePattern_Worker") CreasePattern_Worker backupCreasePatternWorker,
                              @Named("foldingExecutor") TaskExecutorService foldingExecutor,
                              ApplicationModel applicationModel,
                              FoldedFigureModel foldedFigureModel,
                              @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                              FoldedFiguresList foldedFiguresList) {
        this.bulletinBoard = bulletinBoard;
        this.canvasModel = canvasModel;
        this.frame = frame;
        this.creasePatternCamera = creasePatternCamera;
        this.backupCreasePatternWorker = backupCreasePatternWorker;
        this.foldingExecutor = foldingExecutor;
        this.applicationModel = applicationModel;
        this.foldedFigureModel = foldedFigureModel;

        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.foldedFiguresList = foldedFiguresList;
    }

    @Override
    public void folding_estimated(Foldable selectedFigure) throws InterruptedException, FoldingException {
        selectedFigure.folding_estimated(creasePatternCamera, lineSegmentsForFolding);
    }

    @Override
    public void fold(FoldedFigure.EstimationOrder estimationOrder) {
        fold(getFoldType(), estimationOrder);
    }

    public void fold(FoldType foldType, FoldedFigure.EstimationOrder estimationOrder) {
        if (foldType == FoldType.FOR_EXISTING_FOLDED_FIGURE_3) {
            assert foldedFiguresList.getSelectedItem() != null;

            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            mainCreasePatternWorker.getFoldLineSet().select(selectedFigure.getBoundingBox());
            LineSegmentSet reFold = mainCreasePatternWorker.getForSelectFolding();
            if (foldedFiguresList.getSelectedItem() != null && reFold.contentEquals(lastFold)) {
                Logger.info("CP didnt change, refolding using constraints and starting face");
                selectedFigure.getFoldedFigure().estimationOrder = estimationOrder;
                selectedFigure.getFoldedFigure().estimationStep = FoldedFigure.EstimationStep.STEP_0;
                foldingExecutor.executeTask(new FoldingEstimateTask(creasePatternCamera, bulletinBoard, canvasModel, lineSegmentsForFolding, selectedFigure, estimationOrder));
                return;
            }
            lastFold = reFold;
            // replace currently selected model if not using selection to fold
            foldedFiguresList.removeElement(foldedFiguresList.getSelectedItem());
        }

        if (foldType == FoldType.FOR_ALL_CONNECTED_LINES_1) {
            Point cameraPos = this.mainCreasePatternWorker.getCameraPosition();
            mainCreasePatternWorker.selectConnected(this.mainCreasePatternWorker.getFoldLineSet().closestPoint(cameraPos));
            LineSegmentSet newFold = mainCreasePatternWorker.getForSelectFolding();
            if (foldedFiguresList.getSelectedItem() != null && newFold.contentEquals(lastFold)) {
                Logger.info("CP didnt change, refolding using constraints and starting face");
                FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
                if (selectedFigure != null) {
                    selectedFigure.getFoldedFigure().estimationOrder = estimationOrder;
                    selectedFigure.getFoldedFigure().estimationStep = FoldedFigure.EstimationStep.STEP_0;
                    foldingExecutor.executeTask(new FoldingEstimateTask(creasePatternCamera, bulletinBoard, canvasModel, lineSegmentsForFolding, selectedFigure, estimationOrder));
                }
                return;
            }
            lastFold = newFold;
            // replace currently selected model if not using selection to fold
            foldedFiguresList.removeElement(foldedFiguresList.getSelectedItem());
        }

        lineSegmentsForFolding = mainCreasePatternWorker.getForSelectFolding();

        //これより前のOZは古いOZ
        Foldable selectedFigure = initFoldedFigure();//OAZのアレイリストに、新しく折り上がり図をひとつ追加し、それを操作対象に指定し、foldedFigures(0)共通パラメータを引き継がせる。
        //これより後のOZは新しいOZに変わる

        foldingExecutor.executeTask(new FoldingEstimateTask(creasePatternCamera, bulletinBoard, canvasModel, lineSegmentsForFolding, selectedFigure, estimationOrder));
    }

    @Override
    public FoldType getFoldType() {
        //= 0 Do nothing, = 1 Folding estimation for all fold lines in the normal development view, = 2 for fold estimation for selected fold lines, = 3 for changing the folding state
        int foldLineTotalForSelectFolding = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        Logger.info("foldedFigures.size() = " + foldedFiguresList.getSize() + "    : foldedFigureIndex = " + foldedFiguresList.getIndexOf(foldedFiguresList.getSelectedItem()) + "    : mainDrawingWorker.get_orisensuu_for_select_oritatami() = " + foldLineTotalForSelectFolding);

        if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
            if (foldedFiguresList.getSelectedItem() != null) {
                return FoldType.FOR_EXISTING_FOLDED_FIGURE_3;
            }
            return FoldType.FOR_ALL_CONNECTED_LINES_1;//全展開図で折畳み
        } else {        //折り線選択有り
            return FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
        }
    }

    @Override
    public FoldedFigure_Drawer initFoldedFigure() {//Add one new folding diagram to the foldedFigures array list, specify it as the operation target, and inherit the foldedFigures (0) common parameters.
        Logger.info(" oritatami_jyunbi 20180107");

        FoldedFigure_Drawer newFoldedFigure = new FoldedFigure_Drawer(new FoldedFigure_01(bulletinBoard));
        newFoldedFigure.setMoveWithCp(applicationModel.getMoveFoldedModelWithCp());
        foldedFiguresList.addElement(newFoldedFigure);
        foldedFiguresList.setSelectedItem(newFoldedFigure);

        newFoldedFigure.getData(foldedFigureModel);

        return newFoldedFigure;
    }

    @Override
    public void createTwoColoredCp() {
        lineSegmentsForFolding = mainCreasePatternWorker.getForSelectFolding();

        if (mainCreasePatternWorker.getFoldLineTotalForSelectFolding() == 0) {        //折り線選択無し
            twoColorNoSelectedPolygonalLineWarning();//Warning: There is no selected polygonal line


        } else if (mainCreasePatternWorker.getFoldLineTotalForSelectFolding() > 0) {
            foldingExecutor.executeTask(new TwoColoredTask(bulletinBoard, creasePatternCamera, this, canvasModel));
        }
        mainCreasePatternWorker.unselect_all(false);
    }

    public void twoColorNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>２色塗りわけ展開図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To get 2-Colored crease pattern, select the target range in advance (use the select button).<html>");
        // TODO fix owner
        JOptionPane.showMessageDialog(frame.get(), label);
    }

    @Override
    public void foldAnother(Foldable selectedItem) {
        foldingExecutor.executeTask(new FoldingEstimateTask(creasePatternCamera, bulletinBoard, canvasModel, lineSegmentsForFolding, selectedItem, FoldedFigure.EstimationOrder.ORDER_6));
    }

    @Override
    public LineSegmentSet getLineSegmentsForFolding() {
        return lineSegmentsForFolding;
    }

    @Override
    public void duplicate(FoldedFigure figureToDuplicate) { //Trox's stuffs
        FoldedFigure figure = figureToDuplicate;
        LineSegmentSet lines = figure.wireFrame_worker1.getLineStore();
        Foldable newFigure = initFoldedFigure();

        //What the fuck
        FoldedFigure.EstimationOrder order;
        switch (figure.displayStyle) {
            case NONE_0:
                order = FoldedFigure.EstimationOrder.ORDER_0;
                break;
            case DEVELOPMENT_1:
                order = FoldedFigure.EstimationOrder.ORDER_1;
                break;
            case WIRE_2:
                order = FoldedFigure.EstimationOrder.ORDER_2;
                break;
            case TRANSPARENT_3:
                order = FoldedFigure.EstimationOrder.ORDER_3;
                break;
            case DEVELOPMENT_4:
                order = FoldedFigure.EstimationOrder.ORDER_4;
                break;
            case PAPER_5:
                order = FoldedFigure.EstimationOrder.ORDER_5;
                break;
            default:
                order = FoldedFigure.EstimationOrder.ORDER_5;
        }
        foldingExecutor.executeTask(new FoldingEstimateTask(
                creasePatternCamera, bulletinBoard, canvasModel, lines, newFigure, order));
    }

}
