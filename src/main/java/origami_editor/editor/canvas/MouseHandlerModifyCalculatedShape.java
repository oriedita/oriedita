package origami_editor.editor.canvas;

import origami.crease_pattern.FoldingException;
import origami.crease_pattern.element.Point;
import origami_editor.editor.App;
import origami_editor.editor.MouseMode;
import origami_editor.editor.folded_figure.FoldedFigure;

public class MouseHandlerModifyCalculatedShape implements MouseModeHandler {
    private final App app;
    private final Point p_m_left_on = new Point();//Coordinates when the left mouse button is pressed
    private final Point move_previous_selection_point = new Point();//Coordinates of the selected point before moving
    private int i_nanini_near = 0;//Point p is close to the point in the development view = 1, close to the point in the folded view = 2, not close to either = 0
    private int i_closestPointId;
    private PointSelection i_point_selection = PointSelection.NONE_0;//Both cp_worker1 and cp_worker2 are not selected (situation i_point_selection = 0), cp_worker1 is selected and cp_worker2 is not selected (situation i_point_selection = 1), and the vertex is cp_worker2 selected (situation i_point_selection = 2).

    public MouseHandlerModifyCalculatedShape(App app) {
        this.app = app;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.MODIFY_CALCULATED_SHAPE_101;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
            foldedFigure_operation_mouse_on_1(p0);
        }
        if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
            foldedFigure_operation_mouse_on_2(p0);
        }
    }

    @Override
    public void mouseDragged(Point p0) {
        if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
            foldedFigure_operation_mouse_drag_1(p0);
        }
        if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
            foldedFigure_operation_mouse_drag_2(p0);
        }
    }

    @Override
    public void mouseReleased(Point p0) {
        if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
            foldedFigure_operation_mouse_off_1(p0);
        }
        if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
            foldedFigure_operation_mouse_off_2(p0);
        }

    }

    public void foldedFigure_operation_mouse_off_1(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        app.OZ.cp_worker2.setCamera(app.OZ.foldedFigureCamera);
        app.OZ.cp_worker2.setCam_front(app.OZ.foldedFigureFrontCamera);
        app.OZ.cp_worker2.setCam_rear(app.OZ.foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            app.OZ.displayStyle = app.OZ.display_flg_backup;//20180216

            app.OZ.cp_worker2.mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, app.OZ.ip4);
            if (p_m_left_on.distance(p) > 0.0000001) {
                app.OZ.record();
                app.OZ.estimationStep = FoldedFigure.EstimationStep.STEP_2;

                if (app.OZ.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
                }

                if (app.OZ.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                    app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;
                    try {
                        app.folding_estimated();
                    } catch (InterruptedException | FoldingException e) {
                        e.printStackTrace();
                    }
                }//オリジナル 20180124 これ以外だと、表示いったんもどるようでうざい
            }

            app.OZ.cp_worker1.setAllPointStateFalse();
            //折り上がり図でi_closestPointIdと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = new Point();
            ps.set(app.OZ.cp_worker2.getPoint(i_closestPointId));
            for (int i = 1; i <= app.OZ.cp_worker2.getPointsTotal(); i++) {
                if (ps.distance(app.OZ.cp_worker2.getPoint(i)) < 0.0000001) {
                    app.OZ.cp_worker1.setPointStateTrue(i);
                }
            }
        }
    }

    public void foldedFigure_operation_mouse_off_2(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        app.OZ.cp_worker2.setCamera(app.OZ.foldedFigureCamera);
        app.OZ.cp_worker2.setCam_front(app.OZ.foldedFigureFrontCamera);
        app.OZ.cp_worker2.setCam_rear(app.OZ.foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            app.OZ.cp_worker2.mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, app.OZ.ip4);
            if (p_m_left_on.distance(p) > 0.0000001) {
                app.OZ.record();
                app.OZ.estimationStep = FoldedFigure.EstimationStep.STEP_2;

                if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
                    app.OZ.displayStyle = app.OZ.display_flg_backup;//20180216
                }
                if (app.OZ.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
                }

                try {
                    app.OZ.folding_estimated_03();//20180216
                } catch (InterruptedException e) {
                    // Ignore
                }
            }

            app.OZ.cp_worker1.setAllPointStateFalse();
            //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = new Point();
            ps.set(app.OZ.cp_worker2.getPoint(i_closestPointId));
            for (int i = 1; i <= app.OZ.cp_worker2.getPointsTotal(); i++) {
                if (ps.distance(app.OZ.cp_worker2.getPoint(i)) < 0.0000001) {
                    app.OZ.cp_worker1.setPointStateTrue(i);
                }
            }
        }
    }

    public void foldedFigure_operation_mouse_on_1(Point p) {//Work when the left mouse button is pressed in the folding diagram operation Folding function
        p_m_left_on.set(new Point(p.getX(), p.getY()));

        app.OZ.cp_worker2.setCamera(app.OZ.foldedFigureCamera);
        app.OZ.cp_worker2.setCam_front(app.OZ.foldedFigureFrontCamera);
        app.OZ.cp_worker2.setCam_rear(app.OZ.foldedFigureRearCamera);

        //Store the number of the point closest to p in i_closestPointId. I_closestPointId = 0 if there are no close points
        i_nanini_near = 0;//Close to the point in the development view = 1, close to the point in the folded view = 2, not close to either = 0
        i_closestPointId = app.OZ.cp_worker1.closestPointId_with_camera(p);
        if (i_closestPointId != 0) {
            i_nanini_near = 1;
        }
        if (app.OZ.cp_worker2.closestPointId_with_camera(p, app.OZ.ip4) != 0) {
            if (app.OZ.cp_worker1.closest_point_distance_with_camera(p) > app.OZ.cp_worker2.closest_point_distance_with_camera(p, app.OZ.ip4)) {
                i_closestPointId = app.OZ.cp_worker2.closestPointId_with_camera(p, app.OZ.ip4);
                i_nanini_near = 2;
            }
        }//Store the number of the point closest to p in i_closestPointId

        move_previous_selection_point.set(app.OZ.cp_worker2.getPoint(i_closestPointId));

        System.out.println("i_nanini_tikai = " + i_nanini_near);

        if (i_nanini_near == 1) {
            //Decide i_point_selection
            i_point_selection = PointSelection.NONE_0;
            if (app.OZ.cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
            }
            if (app.OZ.cp_worker2.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_2;
            }
            //Decide i_point_selection so far


            switch (i_point_selection) {//Find the number of the point at the same position as i_closestPointId in the fold-up diagram, and mark the point with that number as selected with cp_worker1.
                case NONE_0:
                    app.OZ.setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(app.OZ.cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= app.OZ.cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(app.OZ.cp_worker2.getPoint(i)) < 0.0000001) {
                            app.OZ.cp_worker1.setPointStateTrue(i);
                        }
                    }
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                case WORKER_2:
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
            }
        }

        if (i_nanini_near == 2) {
            //Decide i_point_selection
            i_point_selection = PointSelection.NONE_0;
            if (app.OZ.cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
                if (app.OZ.cp_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = PointSelection.WORKER_2;
                }    //At the point specified on the origami diagram, one of the points that overlaps it is selected by cp_worker2. In short, the point displayed in green on the origami diagram is selected.
            }
            //Decide i_point_selection so far
            System.out.println("i_ten_sentaku = " + i_point_selection);

            switch (i_point_selection) {//Find the number of the point at the same position as i_closestPointId in the fold-up diagram, and mark the point with that number as selected with cp_worker1.
                case NONE_0:
                    app.OZ.setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(app.OZ.cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= app.OZ.cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(app.OZ.cp_worker2.getPoint(i)) < 0.0000001) {
                            app.OZ.cp_worker1.setPointStateTrue(i);
                        }
                    }
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
            }

            if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
                app.OZ.display_flg_backup = app.OZ.displayStyle;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                app.OZ.displayStyle = FoldedFigure.DisplayStyle.WIRE_2;            //20180216
            }
        }

        System.out.println("cp_worker1.get_ten_sentakusuu() = " + app.OZ.cp_worker1.getSelectedPointsNum());
        System.out.println("cp_worker2.get_ten_sentakusuu() = " + app.OZ.cp_worker2.getSelectedPointsNum());
    }

    public void foldedFigure_operation_mouse_drag_1(Point p) {//Work when dragging while holding down the left mouse button in the fold-up diagram operation
        app.OZ.cp_worker2.setCamera(app.OZ.foldedFigureCamera);
        app.OZ.cp_worker2.setCam_front(app.OZ.foldedFigureFrontCamera);
        app.OZ.cp_worker2.setCam_rear(app.OZ.foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            app.OZ.cp_worker2.mDragged_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, app.OZ.ip4);

            if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
                try {
                    app.OZ.folding_estimated_03();//20180216
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
    public void foldedFigure_operation_mouse_on_2(Point p) {//Work when the left mouse button is pressed in the fold-up diagram operation
        p_m_left_on.set(new Point(p.getX(), p.getY()));

        app.OZ.cp_worker2.setCamera(app.OZ.foldedFigureCamera);
        app.OZ.cp_worker2.setCam_front(app.OZ.foldedFigureFrontCamera);
        app.OZ.cp_worker2.setCam_rear(app.OZ.foldedFigureRearCamera);

        //i_closestPointIdにpに最も近い点の番号を格納。近い点がまったくない場合はi_mottomo_tikai_Tenid=0
        i_nanini_near = 0;//展開図の点に近い=1、折り上がり図の点に近い=2、どちらにも近くない=0
        i_closestPointId = app.OZ.cp_worker1.closestPointId_with_camera(p);
        if (i_closestPointId != 0) {
            i_nanini_near = 1;
        }
        if (app.OZ.cp_worker2.closestPointId_with_camera(p, app.OZ.ip4) != 0) {
            if (app.OZ.cp_worker1.closest_point_distance_with_camera(p) > app.OZ.cp_worker2.closest_point_distance_with_camera(p, app.OZ.ip4)) {
                i_closestPointId = app.OZ.cp_worker2.closestPointId_with_camera(p, app.OZ.ip4);
                i_nanini_near = 2;
            }
        }//i_mottomo_tikai_Tenidにpに最も近い点の番号を格納 ここまで

        move_previous_selection_point.set(app.OZ.cp_worker2.getPoint(i_closestPointId));

        System.out.println("i_nanini_tikai = " + i_nanini_near);

        if (i_nanini_near == 1) {

            //i_ten_sentakuを決める
            i_point_selection = PointSelection.NONE_0;
            if (app.OZ.cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
            }
            if (app.OZ.cp_worker2.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_2;
            }
            //i_ten_sentakuを決める  ここまで

            switch (i_point_selection) {//折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                case NONE_0:
                    app.OZ.setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(app.OZ.cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= app.OZ.cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(app.OZ.cp_worker2.getPoint(i)) < 0.0000001) {
                            app.OZ.cp_worker1.setPointStateTrue(i);
                        }
                    }
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                case WORKER_2:
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
            }
        }

        if (i_nanini_near == 2) {
            //i_ten_sentakuを決める
            i_point_selection = PointSelection.NONE_0;
            if (app.OZ.cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
                if (app.OZ.cp_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = PointSelection.WORKER_2;
                }    //折図上で指定した点で、そこに重なるいずれかの点がcp_worker2で選択されている。要するに折図上の緑表示されている点を選んだ状態
            }
            //i_ten_sentakuを決める  ここまで
            System.out.println("i_ten_sentaku = " + i_point_selection);

            switch (i_point_selection) {//折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                case NONE_0:
                    app.OZ.setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(app.OZ.cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= app.OZ.cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(app.OZ.cp_worker2.getPoint(i)) < 0.0000001) {
                            app.OZ.cp_worker1.setPointStateTrue(i);
                        }
                    }
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                    app.OZ.cp_worker2.changePointState(i_closestPointId);
                    break;
            }

            if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1) {
                app.OZ.display_flg_backup = app.OZ.displayStyle;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                app.OZ.displayStyle = FoldedFigure.DisplayStyle.WIRE_2;            //20180216
            }
        }

        System.out.println("cp_worker1.get_ten_sentakusuu() = " + app.OZ.cp_worker1.getSelectedPointsNum());
        System.out.println("cp_worker2.get_ten_sentakusuu() = " + app.OZ.cp_worker2.getSelectedPointsNum());
    }

    public void foldedFigure_operation_mouse_drag_2(Point p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        app.OZ.cp_worker2.setCamera(app.OZ.foldedFigureCamera);
        app.OZ.cp_worker2.setCam_front(app.OZ.foldedFigureFrontCamera);
        app.OZ.cp_worker2.setCam_rear(app.OZ.foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            app.OZ.cp_worker2.mDragged_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, app.OZ.ip4);

            if (app.canvasModel.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2) {
                try {
                    app.OZ.folding_estimated_03();//20180216
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
    }

    public enum FoldedFigureOperationMode {
        MODE_1,
        MODE_2,
    }

    public enum PointSelection {
        NONE_0,
        WORKER_1,
        WORKER_2,
    }
}
