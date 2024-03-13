package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.FoldingService;
import origami.Epsilon;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.element.Point;
import origami.folding.FoldedFigure;

import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.MODIFY_CALCULATED_SHAPE_101)
public class MouseHandlerModifyCalculatedShape implements MouseModeHandler {
    private final FoldingService foldingService;
    private final CanvasModel canvasModel;
    private final FoldedFiguresList foldedFiguresList;
    private Point p_m_left_on = new Point();//Coordinates when the left mouse button is pressed
    private Point move_previous_selection_point = new Point();//Coordinates of the selected point before moving
    private int i_nanini_near = 0;//Point p is close to the point in the development view = 1, close to the point in the folded view = 2, not close to either = 0
    private int i_closestPointId;
    private PointSelection i_point_selection = PointSelection.NONE_0;//Both wireFrame_worker1 and wireFrame_worker2 are not selected (situation i_point_selection = 0), wireFrame_worker1 is selected and wireFrame_worker2 is not selected (situation i_point_selection = 1), and the vertex is wireFrame_worker2 selected (situation i_point_selection = 2).
    private FoldedFigure_Drawer selectedFigure;

    @Inject
    public MouseHandlerModifyCalculatedShape(FoldingService foldingService, CanvasModel canvasModel, FoldedFiguresList foldedFiguresList) {
        this.foldingService = foldingService;
        this.canvasModel = canvasModel;
        this.foldedFiguresList = foldedFiguresList;
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        selectedFigure = foldedFiguresList.getActiveItem();

        if (selectedFigure == null) {
            return;
        }

        if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
            foldedFigure_operation_mouse_on_1(p0);
        }
        if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
            foldedFigure_operation_mouse_on_2(p0);
        }
    }

    @Override
    public void mouseDragged(Point p0) {
        selectedFigure = foldedFiguresList.getActiveItem();

        if (selectedFigure == null) {
            return;
        }

        if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
            foldedFigure_operation_mouse_drag_1(p0);
        }
        if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
            foldedFigure_operation_mouse_drag_2(p0);
        }
    }

    @Override
    public void mouseReleased(Point p0) {
        selectedFigure = foldedFiguresList.getActiveItem();

        if (selectedFigure == null) {
            return;
        }

        if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
            foldedFigure_operation_mouse_off_1(p0);
        }
        if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
            foldedFigure_operation_mouse_off_2(p0);
        }

    }

    private void foldedFigure_operation_mouse_off_1(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        selectedFigure.getWireFrame_worker_drawer2().setCamera(selectedFigure.getFoldedFigureCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_front(selectedFigure.getFoldedFigureFrontCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_rear(selectedFigure.getFoldedFigureRearCamera());

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            selectedFigure.getFoldedFigure().displayStyle = selectedFigure.getFoldedFigure().display_flg_backup;//20180216

            selectedFigure.getWireFrame_worker_drawer2().mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, selectedFigure.getFoldedFigure().ip4);
            if (p_m_left_on.distance(p) > Epsilon.UNKNOWN_1EN7) {
                selectedFigure.record();
                selectedFigure.getFoldedFigure().estimationStep = FoldedFigure.EstimationStep.STEP_2;

                if (selectedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
                }

                if (selectedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                    selectedFigure.getFoldedFigure().estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
                    try {
                        foldingService.folding_estimated(selectedFigure);
                    } catch (InterruptedException e) {
                        Logger.warn(e, "Folding got interrupted");
                    } catch (FoldingException e) {
                        Logger.error(e, "An error occurred during folding");
                    }
                }//オリジナル 20180124 これ以外だと、表示いったんもどるようでうざい
            }

            selectedFigure.getFoldedFigure().wireFrame_worker1.setAllPointStateFalse();
            //折り上がり図でi_closestPointIdと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);
            for (int i = 1; i <= selectedFigure.getFoldedFigure().wireFrame_worker2.getPointsTotal(); i++) {
                if (ps.distance(selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i)) < Epsilon.UNKNOWN_1EN7) {
                    selectedFigure.getFoldedFigure().wireFrame_worker1.setPointStateTrue(i);
                }
            }
        }
    }

    private void foldedFigure_operation_mouse_off_2(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        selectedFigure.getWireFrame_worker_drawer2().setCamera(selectedFigure.getFoldedFigureCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_front(selectedFigure.getFoldedFigureFrontCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_rear(selectedFigure.getFoldedFigureRearCamera());

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            selectedFigure.getWireFrame_worker_drawer2().mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, selectedFigure.getFoldedFigure().ip4);
            if (p_m_left_on.distance(p) > Epsilon.UNKNOWN_1EN7) {
                selectedFigure.record();
                selectedFigure.getFoldedFigure().estimationStep = FoldedFigure.EstimationStep.STEP_2;

                if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
                    selectedFigure.getFoldedFigure().displayStyle = selectedFigure.getFoldedFigure().display_flg_backup;//20180216
                }
                if (selectedFigure.getFoldedFigure().displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
                }

                try {
                    selectedFigure.getFoldedFigure().folding_estimated_03();//20180216
                    selectedFigure.getFoldedFigure_worker_drawer().calculateFromTopCountedPosition();
                } catch (InterruptedException e) {
                    // Ignore
                }
            }

            selectedFigure.getFoldedFigure().wireFrame_worker1.setAllPointStateFalse();
            //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);
            for (int i = 1; i <= selectedFigure.getFoldedFigure().wireFrame_worker2.getPointsTotal(); i++) {
                if (ps.distance(selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i)) < Epsilon.UNKNOWN_1EN7) {
                    selectedFigure.getFoldedFigure().wireFrame_worker1.setPointStateTrue(i);
                }
            }
        }
    }

    private void foldedFigure_operation_mouse_on_1(Point p) {//Work when the left mouse button is pressed in the folding diagram operation Folding function
        p_m_left_on = new Point(p.getX(), p.getY());

        selectedFigure.getWireFrame_worker_drawer2().setCamera(selectedFigure.getFoldedFigureCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_front(selectedFigure.getFoldedFigureFrontCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_rear(selectedFigure.getFoldedFigureRearCamera());

        //Store the number of the point closest to p in i_closestPointId. I_closestPointId = 0 if there are no close points
        i_nanini_near = 0;//Close to the point in the development view = 1, close to the point in the folded view = 2, not close to either = 0
        i_closestPointId = selectedFigure.getWireFrame_worker_drawer1().closestPointId_with_camera(p);
        if (i_closestPointId != 0) {
            i_nanini_near = 1;
        }
        if (selectedFigure.getWireFrame_worker_drawer2().closestPointId_with_camera(p, selectedFigure.getFoldedFigure().ip4) != 0) {
            if (selectedFigure.getWireFrame_worker_drawer1().closest_point_distance_with_camera(p) > selectedFigure.getWireFrame_worker_drawer2().closest_point_distance_with_camera(p, selectedFigure.getFoldedFigure().ip4)) {
                i_closestPointId = selectedFigure.getWireFrame_worker_drawer2().closestPointId_with_camera(p, selectedFigure.getFoldedFigure().ip4);
                i_nanini_near = 2;
            }
        }//Store the number of the point closest to p in i_closestPointId

        move_previous_selection_point = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);

        Logger.info("i_nanini_tikai = " + i_nanini_near);

        if (i_nanini_near == 1) {
            //Decide i_point_selection
            i_point_selection = PointSelection.NONE_0;
            if (selectedFigure.getFoldedFigure().wireFrame_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
            }
            if (selectedFigure.getFoldedFigure().wireFrame_worker2.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_2;
            }
            //Decide i_point_selection so far


            switch (i_point_selection) {//Find the number of the point at the same position as i_closestPointId in the fold-up diagram, and mark the point with that number as selected with wireFrame_worker1.
                case NONE_0:
                    selectedFigure.getFoldedFigure().setAllPointStateFalse();
                    Point ps = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);
                    for (int i = 1; i <= selectedFigure.getFoldedFigure().wireFrame_worker2.getPointsTotal(); i++) {
                        if (ps.distance(selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i)) < Epsilon.UNKNOWN_1EN7) {
                            selectedFigure.getFoldedFigure().wireFrame_worker1.setPointStateTrue(i);
                        }
                    }
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                case WORKER_2:
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
            }
        }

        if (i_nanini_near == 2) {
            //Decide i_point_selection
            i_point_selection = PointSelection.NONE_0;
            if (selectedFigure.getFoldedFigure().wireFrame_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
                if (selectedFigure.getFoldedFigure().wireFrame_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = PointSelection.WORKER_2;
                }    //At the point specified on the origami diagram, one of the points that overlaps it is selected by wireFrame_worker2. In short, the point displayed in green on the origami diagram is selected.
            }
            //Decide i_point_selection so far
            Logger.info("i_ten_sentaku = " + i_point_selection);

            switch (i_point_selection) {//Find the number of the point at the same position as i_closestPointId in the fold-up diagram, and mark the point with that number as selected with wireFrame_worker1.
                case NONE_0:
                    selectedFigure.getFoldedFigure().setAllPointStateFalse();
                    Point ps = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);
                    for (int i = 1; i <= selectedFigure.getFoldedFigure().wireFrame_worker2.getPointsTotal(); i++) {
                        if (ps.distance(selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i)) < Epsilon.UNKNOWN_1EN7) {
                            selectedFigure.getFoldedFigure().wireFrame_worker1.setPointStateTrue(i);
                        }
                    }
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
                default:
                    break;
            }

            if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
                selectedFigure.getFoldedFigure().display_flg_backup = selectedFigure.getFoldedFigure().displayStyle;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                selectedFigure.getFoldedFigure().displayStyle = FoldedFigure.DisplayStyle.WIRE_2;            //20180216
            }
        }

        Logger.info("wireFrame_worker1.get_ten_sentakusuu() = " + selectedFigure.getFoldedFigure().wireFrame_worker1.getSelectedPointsNum());
        Logger.info("wireFrame_worker2.get_ten_sentakusuu() = " + selectedFigure.getFoldedFigure().wireFrame_worker2.getSelectedPointsNum());
    }

    private void foldedFigure_operation_mouse_drag_1(Point p) {//Work when dragging while holding down the left mouse button in the fold-up diagram operation
        selectedFigure.getWireFrame_worker_drawer2().setCamera(selectedFigure.getFoldedFigureCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_front(selectedFigure.getFoldedFigureFrontCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_rear(selectedFigure.getFoldedFigureRearCamera());

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            selectedFigure.getWireFrame_worker_drawer2().mDragged_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, selectedFigure.getFoldedFigure().ip4);

            if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
                try {
                    selectedFigure.getFoldedFigure().folding_estimated_03();//20180216
                    selectedFigure.getFoldedFigure_worker_drawer().calculateFromTopCountedPosition();
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
    }


    //-------------------------------------------------------------------------------------------------------
    //  =================================================================================================================================
    //  ==========Deformation operation with the folded figure===========================================================================================================
    //-----------------------------------------------------------------------------------------------------uuuuuuu--
    private void foldedFigure_operation_mouse_on_2(Point p) {//Work when the left mouse button is pressed in the fold-up diagram operation
        p_m_left_on = p;

        selectedFigure.getWireFrame_worker_drawer2().setCamera(selectedFigure.getFoldedFigureCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_front(selectedFigure.getFoldedFigureFrontCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_rear(selectedFigure.getFoldedFigureRearCamera());

        //i_closestPointIdにpに最も近い点の番号を格納。近い点がまったくない場合はi_mottomo_tikai_Tenid=0
        i_nanini_near = 0;//展開図の点に近い=1、折り上がり図の点に近い=2、どちらにも近くない=0
        i_closestPointId = selectedFigure.getWireFrame_worker_drawer1().closestPointId_with_camera(p);
        if (i_closestPointId != 0) {
            i_nanini_near = 1;
        }
        if (selectedFigure.getWireFrame_worker_drawer2().closestPointId_with_camera(p, selectedFigure.getFoldedFigure().ip4) != 0) {
            if (selectedFigure.getWireFrame_worker_drawer1().closest_point_distance_with_camera(p) > selectedFigure.getWireFrame_worker_drawer2().closest_point_distance_with_camera(p, selectedFigure.getFoldedFigure().ip4)) {
                i_closestPointId = selectedFigure.getWireFrame_worker_drawer2().closestPointId_with_camera(p, selectedFigure.getFoldedFigure().ip4);
                i_nanini_near = 2;
            }
        }//i_mottomo_tikai_Tenidにpに最も近い点の番号を格納 ここまで

        move_previous_selection_point = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);

        Logger.info("i_nanini_tikai = " + i_nanini_near);

        if (i_nanini_near == 1) {

            //i_ten_sentakuを決める
            i_point_selection = PointSelection.NONE_0;
            if (selectedFigure.getFoldedFigure().wireFrame_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
            }
            if (selectedFigure.getFoldedFigure().wireFrame_worker2.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_2;
            }
            //i_ten_sentakuを決める  ここまで

            switch (i_point_selection) {//折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                case NONE_0:
                    selectedFigure.getFoldedFigure().setAllPointStateFalse();
                    Point ps = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);
                    for (int i = 1; i <= selectedFigure.getFoldedFigure().wireFrame_worker2.getPointsTotal(); i++) {
                        if (ps.distance(selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i)) < Epsilon.UNKNOWN_1EN7) {
                            selectedFigure.getFoldedFigure().wireFrame_worker1.setPointStateTrue(i);
                        }
                    }
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                case WORKER_2:
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
            }
        }

        if (i_nanini_near == 2) {
            //i_ten_sentakuを決める
            i_point_selection = PointSelection.NONE_0;
            if (selectedFigure.getFoldedFigure().wireFrame_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
                if (selectedFigure.getFoldedFigure().wireFrame_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = PointSelection.WORKER_2;
                }    //折図上で指定した点で、そこに重なるいずれかの点がcp_worker2で選択されている。要するに折図上の緑表示されている点を選んだ状態
            }
            //i_ten_sentakuを決める  ここまで
            Logger.info("i_ten_sentaku = " + i_point_selection);

            switch (i_point_selection) {//折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                case NONE_0:
                    selectedFigure.getFoldedFigure().setAllPointStateFalse();
                    Point ps = selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i_closestPointId);
                    for (int i = 1; i <= selectedFigure.getFoldedFigure().wireFrame_worker2.getPointsTotal(); i++) {
                        if (ps.distance(selectedFigure.getFoldedFigure().wireFrame_worker2.getPoint(i)) < Epsilon.UNKNOWN_1EN7) {
                            selectedFigure.getFoldedFigure().wireFrame_worker1.setPointStateTrue(i);
                        }
                    }
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                    selectedFigure.getFoldedFigure().wireFrame_worker2.changePointState(i_closestPointId);
                    break;
                default:
                    break;
            }

            if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
                selectedFigure.getFoldedFigure().display_flg_backup = selectedFigure.getFoldedFigure().displayStyle;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                selectedFigure.getFoldedFigure().displayStyle = FoldedFigure.DisplayStyle.WIRE_2;            //20180216
            }
        }

        Logger.info("wireFrame_worker1.get_ten_sentakusuu() = " + selectedFigure.getFoldedFigure().wireFrame_worker1.getSelectedPointsNum());
        Logger.info("wireFrame_worker2.get_ten_sentakusuu() = " + selectedFigure.getFoldedFigure().wireFrame_worker2.getSelectedPointsNum());
    }

    private void foldedFigure_operation_mouse_drag_2(Point p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        selectedFigure.getWireFrame_worker_drawer2().setCamera(selectedFigure.getFoldedFigureCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_front(selectedFigure.getFoldedFigureFrontCamera());
        selectedFigure.getWireFrame_worker_drawer2().setCam_rear(selectedFigure.getFoldedFigureRearCamera());

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            selectedFigure.getWireFrame_worker_drawer2().mDragged_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, selectedFigure.getFoldedFigure().ip4);

            if (canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
                try {
                    selectedFigure.getFoldedFigure().folding_estimated_03();//20180216
                    selectedFigure.getFoldedFigure_worker_drawer().calculateFromTopCountedPosition();
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
    }

    public enum PointSelection {
        NONE_0,
        WORKER_1,
        WORKER_2,
    }
}
