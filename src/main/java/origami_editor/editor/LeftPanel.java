package origami_editor.editor;

import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.GridModel;
import origami_editor.editor.databinding.HistoryStateModel;
import origami_editor.editor.drawing_worker.DrawingWorker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.record.memo.Memo;
import origami_editor.record.string_op.StringOp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;

public class LeftPanel extends JPanel {
    private final GridConfigureDialog gridConfigureDialog;
    private JPanel panel1;
    private JButton lineWidthDecreaseButton;
    private JButton lineWidthIncreaseButton;
    private JTextField lineSegmentDivisionTextField;
    private JCheckBox correctCpBeforeFoldingCheckBox;
    private UndoRedo undoRedo;
    private JButton pointSizeDecreaseButton;
    private JButton pointSizeIncreaseButton;
    private JButton antiAliasToggleButton;
    private JButton lineStyleChangeButton;
    private JButton drawCreaseFreeButton;
    private JButton drawCreaseRestrictedButton;
    private JButton voronoiButton;
    private JButton makeFlatFoldableButton;
    private JButton lengthenCreaseButton;
    private JButton lengthenCrease2Button;
    private JButton angleBisectorButton;
    private JButton inwardButton;
    private JButton perpendicularDrawButton;
    private JButton symmetricDrawButton;
    private JButton continuousSymmetricDrawButton;
    private JButton parallelDrawButton;
    private JButton setParallelDrawWidthButton;
    private JButton foldableLineDrawButton;
    private JButton all_s_step_to_orisenButton;
    private JButton fishBoneDrawButton;
    private JButton doubleSymmetricDrawButton;
    private JButton senbun_b_nyuryokuButton;
    private JButton reflectButton;
    private JButton selectButton;
    private JButton unselectButton;
    private JButton selectAllButton;
    private JButton unselectAllButton;
    private JButton moveButton;
    private JButton move2p2pButton;
    private JButton copyButton;
    private JButton copy2p2pButton;
    private JButton deleteSelectedLineSegmentButton;
    private JButton lineSegmentDeleteButton;
    private JButton edgeLineSegmentDeleteButton;
    private JButton auxLiveLineSegmentDeleteButton;
    private JButton trimBranchesButton;
    private JButton toMountainButton;
    private JButton toValleyButton;
    private JButton toEdgeButton;
    private JButton toAuxButton;
    private JButton zen_yama_tani_henkanButton;
    private JButton senbun_henkan2Button;
    private JButton senbun_henkanButton;
    private JButton in_L_col_changeButton;
    private JButton on_L_col_changeButton;
    private JButton v_addButton;
    private JButton v_delButton;
    private JButton v_del_ccButton;
    private JButton v_del_allButton;
    private JButton v_del_all_ccButton;
    private JButton inputDataButton;
    private JCheckBox selectPersistentCheckBox;
    private JButton drawTwoColoredCpButton;
    private JButton suitei_01Button;
    private JButton koteimen_siteiButton;
    private JButton suitei_02Button;
    private JButton suitei_03Button;
    private JCheckBox coloredXRayCheckBox;
    private JButton coloredXRayDecreaseButton;
    private JButton coloredXRayIncreaseButton;
    private JButton colRedButton;
    private JButton colBlueButton;
    private JButton colBlackButton;
    private JButton colCyanButton;
    private JButton lineSegmentDivisionSetButton;
    private JButton button1;

    public LeftPanel(App app) {
        CanvasModel canvasModel = app.canvasModel;

        $$$setupUI$$$();
        undoRedo.addUndoActionListener(e -> {
            app.setHelp("undo");

            app.setTitle(app.mainDrawingWorker.undo());
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        undoRedo.addRedoActionListener(e -> {
            app.setHelp("redo");

            app.setTitle(app.mainDrawingWorker.redo());
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        undoRedo.addSetUndoCountActionListener(e -> {
            app.setHelp("undo_syutoku");

            app.historyStateModel.setHistoryTotal(StringOp.String2int(undoRedo.getText(), app.historyStateModel.getHistoryTotal()));
        });
        lineWidthDecreaseButton.addActionListener(e -> {
            app.setHelp("senhaba_sage");

            canvasModel.decreaseLineWidth();
        });
        lineWidthIncreaseButton.addActionListener(e -> {
            app.setHelp("senhaba_age");

            canvasModel.increaseLineWidth();
        });
        pointSizeDecreaseButton.addActionListener(e -> {
            app.setHelp("tenhaba_sage");

            canvasModel.decreasePointSize();
        });
        pointSizeIncreaseButton.addActionListener(e -> {
            app.setHelp("tenhaba_age");

            canvasModel.increasePointSize();
        });
        antiAliasToggleButton.addActionListener(e -> {
            app.setHelp("anti_alias");

            canvasModel.toggleAntiAlias();
        });
        lineStyleChangeButton.addActionListener(e -> {
            app.setHelp("orisen_hyougen");

            app.Button_shared_operation();

            canvasModel.advanceLineStyle();
        });
        colRedButton.addActionListener(e -> {
            app.setHelp("ButtonCol_red");

            app.canvasModel.setLineColor(LineColor.RED_1);
        });
        colBlueButton.addActionListener(e -> {
            app.setHelp("ButtonCol_blue");

            app.canvasModel.setLineColor(LineColor.BLUE_2);
        });
        colBlackButton.addActionListener(e -> {
            app.setHelp("ButtonCol_black");

            app.canvasModel.setLineColor(LineColor.BLACK_0);
        });
        colCyanButton.addActionListener(e -> {
            app.setHelp("ButtonCol_cyan");

            app.canvasModel.setLineColor(LineColor.CYAN_3);
        });
        drawCreaseFreeButton.addActionListener(e -> {
            app.setHelp("senbun_nyuryoku");

            app.canvasModel.setFoldLineAdditionalInputMode(DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_FREE_1);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        drawCreaseRestrictedButton.addActionListener(e -> {
            app.setHelp("senbun_nyuryoku11");

            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_RESTRICTED_11);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_RESTRICTED_11);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        voronoiButton.addActionListener(e -> {
            app.setHelp("Voronoi");

            app.canvasModel.setFoldLineAdditionalInputMode(DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.VORONOI_CREATE_62);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.VORONOI_CREATE_62);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        makeFlatFoldableButton.addActionListener(e -> {
            app.setHelp("oritatami_kanousen");

            app.canvasModel.setMouseMode(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        lengthenCreaseButton.addActionListener(e -> {
            app.setHelp("senbun_entyou");

            app.canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_5);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_5);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        lengthenCrease2Button.addActionListener(e -> {
            app.setHelp("senbun_entyou_2");

            app.canvasModel.setMouseMode(MouseMode.CREASE_LENGTHEN_70);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_LENGTHEN_70);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        angleBisectorButton.addActionListener(e -> {
            app.setHelp("kaku_toubun");

            app.canvasModel.setMouseMode(MouseMode.SQUARE_BISECTOR_7);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.SQUARE_BISECTOR_7);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        inwardButton.addActionListener(e -> {
            app.setHelp("naishin");

            app.canvasModel.setMouseMode(MouseMode.INWARD_8);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.INWARD_8);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        perpendicularDrawButton.addActionListener(e -> {
            app.setHelp("suisen");

            app.canvasModel.setMouseMode(MouseMode.PERPENDICULAR_DRAW_9);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PERPENDICULAR_DRAW_9);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        symmetricDrawButton.addActionListener(e -> {
            app.setHelp("orikaesi");

            app.canvasModel.setMouseMode(MouseMode.SYMMETRIC_DRAW_10);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.SYMMETRIC_DRAW_10);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        continuousSymmetricDrawButton.addActionListener(e -> {
            app.setHelp("renzoku_orikaesi");

            app.canvasModel.setMouseMode(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        parallelDrawButton.addActionListener(e -> {
            app.setHelp("heikousen");

            app.canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_40);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_40);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        setParallelDrawWidthButton.addActionListener(e -> {
            app.setHelp("heikousen_haba_sitei");

            app.canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_WIDTH_51);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_WIDTH_51);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        foldableLineDrawButton.addActionListener(e -> {
            app.setHelp("oritatami_kanousen_and_kousitenkei_simple");

            app.canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.FOLDABLE_LINE_DRAW_71);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        all_s_step_to_orisenButton.addActionListener(e -> {
            System.out.println("i_egaki_dankai = " + app.mainDrawingWorker.i_drawing_stage);
            System.out.println("i_kouho_dankai = " + app.mainDrawingWorker.i_candidate_stage);

            app.setHelp("all_s_step_to_orisen");
            app.mainDrawingWorker.all_s_step_to_orisen();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        fishBoneDrawButton.addActionListener(e -> {
            app.setHelp("sakananohone");

            app.canvasModel.setMouseMode(MouseMode.FISH_BONE_DRAW_33);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.FISH_BONE_DRAW_33);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        doubleSymmetricDrawButton.addActionListener(e -> {
            app.setHelp("fuku_orikaesi");

            app.canvasModel.setMouseMode(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        lineSegmentDivisionSetButton.addActionListener(e -> {
            app.setHelp("senbun_bunkatu_set");

            getData(app.canvasModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_b_nyuryokuButton.addActionListener(e -> {
            app.setHelp("senbun_b_nyuryoku");

            getData(app.canvasModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        selectButton.addActionListener(e -> {
            app.setHelp("Select");

            app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        selectAllButton.addActionListener(e -> {
            app.setHelp("select_all");

            app.mainDrawingWorker.select_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        unselectButton.addActionListener(e -> {
            app.setHelp("unselect");

            app.canvasModel.setMouseMode(MouseMode.CREASE_UNSELECT_20);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        unselectAllButton.addActionListener(e -> {
            app.setHelp("unselect_all");

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        moveButton.addActionListener(e -> {
            app.setHelp("move");

            app.canvasModel.setSelectionOperationMode(App.SelectionOperationMode.MOVE_1);
            app.canvasModel.setMouseMode(MouseMode.CREASE_MOVE_21);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        move2p2pButton.addActionListener(e -> {
            app.setHelp("move_2p2p");

            app.canvasModel.setSelectionOperationMode(App.SelectionOperationMode.MOVE4P_2);
            app.canvasModel.setMouseMode(MouseMode.CREASE_MOVE_4P_31);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        copyButton.addActionListener(e -> {
            app.setHelp("copy_paste");

            app.canvasModel.setSelectionOperationMode(App.SelectionOperationMode.COPY_3);
            app.canvasModel.setMouseMode(MouseMode.CREASE_COPY_22);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        copy2p2pButton.addActionListener(e -> {
            app.setHelp("copy_paste_2p2p");

            app.canvasModel.setSelectionOperationMode(App.SelectionOperationMode.COPY4P_4);
            app.canvasModel.setMouseMode(MouseMode.CREASE_COPY_4P_32);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        reflectButton.addActionListener(e -> {
            app.setHelp("kyouei");

            app.canvasModel.setSelectionOperationMode(App.SelectionOperationMode.MIRROR_5);
            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_SYMMETRIC_12);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        deleteSelectedLineSegmentButton.addActionListener(e -> {
            app.setHelp("del_selected_senbun");

            app.mainDrawingWorker.del_selected_senbun();
            app.mainDrawingWorker.record();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        lineSegmentDeleteButton.addActionListener(e -> {
            app.setHelp("senbun_sakujyo");

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        edgeLineSegmentDeleteButton.addActionListener(e -> {
            app.setHelp("kuro_senbun_sakujyo");

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(DrawingWorker.FoldLineAdditionalInputMode.BLACK_LINE_2);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        auxLiveLineSegmentDeleteButton.addActionListener(e -> {
            app.setHelp("senbun3_sakujyo");

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(DrawingWorker.FoldLineAdditionalInputMode.AUX_LIVE_LINE_3);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        trimBranchesButton.addActionListener(e -> {
            app.setHelp("eda_kesi");
            app.mainDrawingWorker.point_removal();
            app.mainDrawingWorker.overlapping_line_removal();
            app.mainDrawingWorker.branch_trim(0.000001);
            app.mainDrawingWorker.organizeCircles();
            app.mainDrawingWorker.record();
            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        toMountainButton.addActionListener(e -> {
            app.setHelp("M_nisuru");

            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MOUNTAIN_23);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        toValleyButton.addActionListener(e -> {
            app.setHelp("V_nisuru");

            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_VALLEY_24);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        toEdgeButton.addActionListener(e -> {
            app.setHelp("E_nisuru");

            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_EDGE_25);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        toAuxButton.addActionListener(e -> {
            app.setHelp("HK_nisuru");

            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_AUX_60);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        zen_yama_tani_henkanButton.addActionListener(e -> {
            app.setHelp("zen_yama_tani_henkan");
            app.mainDrawingWorker.allMountainValleyChange();
            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_henkan2Button.addActionListener(e -> {
            app.setHelp("senbun_henkan2");

            app.canvasModel.setMouseMode(MouseMode.CREASE_TOGGLE_MV_58);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_henkanButton.addActionListener(e -> {
            app.setHelp("senbun_henkan");

            app.canvasModel.setMouseMode(MouseMode.CHANGE_CREASE_TYPE_4);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        in_L_col_changeButton.addActionListener(e -> {
            app.setHelp("in_L_col_change");

            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MV_34);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_MAKE_MV_34);

            if (app.canvasModel.getLineColor() == LineColor.BLACK_0) {
                app.canvasModel.setLineColor(LineColor.RED_1);
            }

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        on_L_col_changeButton.addActionListener(e -> {
            app.setHelp("on_L_col_change");

            app.canvasModel.setMouseMode(MouseMode.CREASES_ALTERNATE_MV_36);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASES_ALTERNATE_MV_36);

            if (app.canvasModel.getLineColor() == LineColor.BLACK_0) {
                app.canvasModel.setLineColor(LineColor.BLUE_2);
            }

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_addButton.addActionListener(e -> {
            app.setHelp("v_add");

            app.canvasModel.setMouseMode(MouseMode.DRAW_POINT_14);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_delButton.addActionListener(e -> {
            app.setHelp("v_del");

            app.canvasModel.setMouseMode(MouseMode.DELETE_POINT_15);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_del_ccButton.addActionListener(e -> {
            app.setHelp("v_del_cc");

            app.canvasModel.setMouseMode(MouseMode.VERTEX_DELETE_ON_CREASE_41);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_del_allButton.addActionListener(e -> {
            app.setHelp("v_del_all");
            //mouseMode=19;
            app.mainDrawingWorker.v_del_all();
            System.out.println("mainDrawingWorker.v_del_all()");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_del_all_ccButton.addActionListener(e -> {
            app.setHelp("v_del_all_cc");
            app.mainDrawingWorker.v_del_all_cc();
            System.out.println("mainDrawingWorker.v_del_all_cc()");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        inputDataButton.addActionListener(e -> {
            app.setHelp("yomi_tuika");

            app.Button_shared_operation();

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            Memo memo_temp;

            System.out.println("readFile2Memo() 開始");
            memo_temp = app.readFile2Memo();
            System.out.println("readFile2Memo() 終了");

            if (memo_temp.getLineCount() > 0) {
                app.mainDrawingWorker.setMemo_for_reading_tuika(memo_temp);
                app.mainDrawingWorker.record();
                app.repaintCanvas();
            }
        });
        correctCpBeforeFoldingCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_cp_kaizen_oritatami");

            app.canvasModel.setCorrectCpBeforeFolding(correctCpBeforeFoldingCheckBox.isSelected());

            app.repaintCanvas();
        });
        selectPersistentCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_select_nokosi");

            app.canvasModel.setSelectPersistent(selectPersistentCheckBox.isSelected());

            app.repaintCanvas();
        });
        drawTwoColoredCpButton.addActionListener(e -> {
            app.setHelp("2syoku_tenkaizu");

            app.Ss0 = app.mainDrawingWorker.getForSelectFolding();

            if (app.mainDrawingWorker.getFoldLineTotalForSelectFolding() == 0) {        //折り線選択無し
                app.twoColorNoSelectedPolygonalLineWarning();//Warning: There is no selected polygonal line


            } else if (app.mainDrawingWorker.getFoldLineTotalForSelectFolding() > 0) {
                app.folding_prepare();//ここでOZがOAZ(0)からOAZ(i)に切り替わる
                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;

                if (!app.subThreadRunning) {
                    app.subThreadRunning = true;
                    app.subThreadMode = SubThread.Mode.TWO_COLORED_4;
                    app.mks();//新しいスレッドを作る
                    app.sub.start();
                }
            }

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
        });
        suitei_01Button.addActionListener(e -> {
            app.setHelp("suitei_01");

            app.oritatame(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_1);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!app.canvasModel.isSelectPersistent()) {
                app.mainDrawingWorker.unselect_all();
            }

            app.Button_shared_operation();
        });
        koteimen_siteiButton.addActionListener(e -> {
            app.setHelp("koteimen_sitei");
            if (app.OZ.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                app.canvasModel.setMouseMode(MouseMode.CHANGE_STANDARD_FACE_103);
            }
            app.Button_shared_operation();
        });
        suitei_02Button.addActionListener(e -> {
            app.setHelp("suitei_02");

            app.oritatame(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_2);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!app.canvasModel.isSelectPersistent()) {
                app.mainDrawingWorker.unselect_all();
            }

            app.Button_shared_operation();
        });
        suitei_03Button.addActionListener(e -> {
            app.setHelp("suitei_03");

            app.oritatame(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_3);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (!app.canvasModel.isSelectPersistent()) {
                app.mainDrawingWorker.unselect_all();
            }
            app.Button_shared_operation();
        });
        coloredXRayCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_toukazu_color");

            app.foldedFigureModel.setTransparencyColor(coloredXRayCheckBox.isSelected());
        });
        coloredXRayDecreaseButton.addActionListener(e -> {
            app.setHelp("toukazu_color_sage");

            app.foldedFigureModel.decreaseTransparency();

            app.Button_shared_operation();
        });
        coloredXRayIncreaseButton.addActionListener(e -> {
            app.setHelp("toukazu_color_age");

            app.foldedFigureModel.increaseTransparency();

            app.Button_shared_operation();
        });
        gridConfigureDialog = new GridConfigureDialog(app, app.gridModel);
        gridConfigureDialog.pack();

        button1.addActionListener(e -> {
            gridConfigureDialog.setVisible(true);
            gridConfigureDialog.setLocationRelativeTo(button1);
        });
    }


    public void getGridConfigurationData(GridModel gridModel) {
        gridConfigureDialog.getData(gridModel);
    }

    public void setGridConfigurationData(GridModel gridModel) {
        gridConfigureDialog.setData(gridModel);
    }

    public JCheckBox getColoredXRayButton() {
        return coloredXRayCheckBox;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1.setLayout(new GridBagLayout());
        undoRedo = new UndoRedo();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(undoRedo.$$$getRootComponent$$$(), gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        lineWidthDecreaseButton = new JButton();
        lineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(lineWidthDecreaseButton, gbc);
        lineWidthIncreaseButton = new JButton();
        lineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(lineWidthIncreaseButton, gbc);
        pointSizeDecreaseButton = new JButton();
        pointSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(pointSizeDecreaseButton, gbc);
        pointSizeIncreaseButton = new JButton();
        pointSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(pointSizeIncreaseButton, gbc);
        antiAliasToggleButton = new JButton();
        antiAliasToggleButton.setMinimumSize(new Dimension(60, 30));
        antiAliasToggleButton.setText("a_a");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(antiAliasToggleButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel3, gbc);
        colRedButton = new JButton();
        colRedButton.setBackground(new Color(-6908266));
        colRedButton.setText("M");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colRedButton, gbc);
        colBlueButton = new JButton();
        colBlueButton.setBackground(new Color(-6908266));
        colBlueButton.setText("V");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colBlueButton, gbc);
        colBlackButton = new JButton();
        colBlackButton.setBackground(new Color(-6908266));
        colBlackButton.setText("E");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colBlackButton, gbc);
        colCyanButton = new JButton();
        colCyanButton.setBackground(new Color(-6908266));
        colCyanButton.setText("A");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colCyanButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel4, gbc);
        lineStyleChangeButton = new JButton();
        lineStyleChangeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orisen_hyougen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel4.add(lineStyleChangeButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel5, gbc);
        lineSegmentDeleteButton = new JButton();
        lineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(lineSegmentDeleteButton, gbc);
        edgeLineSegmentDeleteButton = new JButton();
        edgeLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kuro_senbun_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(edgeLineSegmentDeleteButton, gbc);
        auxLiveLineSegmentDeleteButton = new JButton();
        auxLiveLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun3_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(auxLiveLineSegmentDeleteButton, gbc);
        trimBranchesButton = new JButton();
        trimBranchesButton.setIcon(new ImageIcon(getClass().getResource("/ppp/eda_kesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(trimBranchesButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel6, gbc);
        toMountainButton = new JButton();
        toMountainButton.setIcon(new ImageIcon(getClass().getResource("/ppp/M_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(toMountainButton, gbc);
        toValleyButton = new JButton();
        toValleyButton.setIcon(new ImageIcon(getClass().getResource("/ppp/V_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(toValleyButton, gbc);
        toEdgeButton = new JButton();
        toEdgeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/E_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(toEdgeButton, gbc);
        toAuxButton = new JButton();
        toAuxButton.setIcon(new ImageIcon(getClass().getResource("/ppp/HK_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(toAuxButton, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel7, gbc);
        zen_yama_tani_henkanButton = new JButton();
        zen_yama_tani_henkanButton.setText("AC");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(zen_yama_tani_henkanButton, gbc);
        senbun_henkan2Button = new JButton();
        senbun_henkan2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(senbun_henkan2Button, gbc);
        senbun_henkanButton = new JButton();
        senbun_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(senbun_henkanButton, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel8, gbc);
        in_L_col_changeButton = new JButton();
        in_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/in_L_col_change.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(in_L_col_changeButton, gbc);
        on_L_col_changeButton = new JButton();
        on_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/on_L_col_change.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(on_L_col_changeButton, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 21;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel9, gbc);
        v_addButton = new JButton();
        v_addButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_add.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_addButton, gbc);
        v_delButton = new JButton();
        v_delButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_delButton, gbc);
        v_del_ccButton = new JButton();
        v_del_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_cc.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_del_ccButton, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 22;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel10, gbc);
        v_del_allButton = new JButton();
        v_del_allButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(v_del_allButton, gbc);
        v_del_all_ccButton = new JButton();
        v_del_all_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all_cc.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(v_del_all_ccButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 25;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer2, gbc);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 26;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel11, gbc);
        inputDataButton = new JButton();
        inputDataButton.setText("Op");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel11.add(inputDataButton, gbc);
        correctCpBeforeFoldingCheckBox = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel11.add(correctCpBeforeFoldingCheckBox, gbc);
        selectPersistentCheckBox = new JCheckBox();
        selectPersistentCheckBox.setToolTipText("ckbox_select_nokosi");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel11.add(selectPersistentCheckBox, gbc);
        drawTwoColoredCpButton = new JButton();
        drawTwoColoredCpButton.setIcon(new ImageIcon(getClass().getResource("/ppp/2syoku_tenkaizu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel11.add(drawTwoColoredCpButton, gbc);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 27;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel12, gbc);
        suitei_01Button = new JButton();
        suitei_01Button.setText("CP_rcg");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel12.add(suitei_01Button, gbc);
        koteimen_siteiButton = new JButton();
        koteimen_siteiButton.setText("S_face");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel12.add(koteimen_siteiButton, gbc);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 28;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel13, gbc);
        suitei_02Button = new JButton();
        suitei_02Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_02.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel13.add(suitei_02Button, gbc);
        suitei_03Button = new JButton();
        suitei_03Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_03.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel13.add(suitei_03Button, gbc);
        coloredXRayCheckBox = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel13.add(coloredXRayCheckBox, gbc);
        coloredXRayDecreaseButton = new JButton();
        coloredXRayDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel13.add(coloredXRayDecreaseButton, gbc);
        coloredXRayIncreaseButton = new JButton();
        coloredXRayIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel13.add(coloredXRayIncreaseButton, gbc);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel14, gbc);
        drawCreaseFreeButton = new JButton();
        drawCreaseFreeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(drawCreaseFreeButton, gbc);
        drawCreaseRestrictedButton = new JButton();
        drawCreaseRestrictedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku11.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(drawCreaseRestrictedButton, gbc);
        voronoiButton = new JButton();
        voronoiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Voronoi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(voronoiButton, gbc);
        makeFlatFoldableButton = new JButton();
        makeFlatFoldableButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(makeFlatFoldableButton, gbc);
        lengthenCreaseButton = new JButton();
        lengthenCreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(lengthenCreaseButton, gbc);
        lengthenCrease2Button = new JButton();
        lengthenCrease2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou_2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(lengthenCrease2Button, gbc);
        angleBisectorButton = new JButton();
        angleBisectorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kaku_toubun.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(angleBisectorButton, gbc);
        inwardButton = new JButton();
        inwardButton.setIcon(new ImageIcon(getClass().getResource("/ppp/naishin.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(inwardButton, gbc);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel15, gbc);
        perpendicularDrawButton = new JButton();
        perpendicularDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suisen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(perpendicularDrawButton, gbc);
        symmetricDrawButton = new JButton();
        symmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(symmetricDrawButton, gbc);
        parallelDrawButton = new JButton();
        parallelDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(parallelDrawButton, gbc);
        setParallelDrawWidthButton = new JButton();
        setParallelDrawWidthButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen_haba_sitei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(setParallelDrawWidthButton, gbc);
        all_s_step_to_orisenButton = new JButton();
        all_s_step_to_orisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/all_s_step_to_orisen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(all_s_step_to_orisenButton, gbc);
        fishBoneDrawButton = new JButton();
        fishBoneDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sakananohone.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(fishBoneDrawButton, gbc);
        lineSegmentDivisionTextField = new JTextField();
        lineSegmentDivisionTextField.setColumns(2);
        lineSegmentDivisionTextField.setText("2");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(lineSegmentDivisionTextField, gbc);
        lineSegmentDivisionSetButton = new JButton();
        lineSegmentDivisionSetButton.setText("Set");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(lineSegmentDivisionSetButton, gbc);
        continuousSymmetricDrawButton = new JButton();
        continuousSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/renzoku_orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(continuousSymmetricDrawButton, gbc);
        foldableLineDrawButton = new JButton();
        foldableLineDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen_and_kousitenkei_simple.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(foldableLineDrawButton, gbc);
        doubleSymmetricDrawButton = new JButton();
        doubleSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/fuku_orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(doubleSymmetricDrawButton, gbc);
        senbun_b_nyuryokuButton = new JButton();
        senbun_b_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_b_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(senbun_b_nyuryokuButton, gbc);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel16, gbc);
        selectButton = new JButton();
        selectButton.setBackground(new Color(-16711936));
        selectButton.setText("sel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(selectButton, gbc);
        selectAllButton = new JButton();
        selectAllButton.setBackground(new Color(-16711936));
        selectAllButton.setText("s_al");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(selectAllButton, gbc);
        unselectButton = new JButton();
        unselectButton.setBackground(new Color(-16711936));
        unselectButton.setText("unsel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(unselectButton, gbc);
        unselectAllButton = new JButton();
        unselectAllButton.setBackground(new Color(-16711936));
        unselectAllButton.setText("uns_al");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(unselectAllButton, gbc);
        moveButton = new JButton();
        moveButton.setBackground(new Color(-5579606));
        moveButton.setEnabled(true);
        moveButton.setText("move");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(moveButton, gbc);
        move2p2pButton = new JButton();
        move2p2pButton.setBackground(new Color(-5579606));
        move2p2pButton.setEnabled(true);
        move2p2pButton.setText("mv_4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(move2p2pButton, gbc);
        copyButton = new JButton();
        copyButton.setBackground(new Color(-5579606));
        copyButton.setEnabled(true);
        copyButton.setText("copy");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(copyButton, gbc);
        copy2p2pButton = new JButton();
        copy2p2pButton.setBackground(new Color(-5579606));
        copy2p2pButton.setEnabled(true);
        copy2p2pButton.setText("cp_4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(copy2p2pButton, gbc);
        reflectButton = new JButton();
        reflectButton.setBackground(new Color(-5579606));
        reflectButton.setEnabled(true);
        reflectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kyouei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(reflectButton, gbc);
        deleteSelectedLineSegmentButton = new JButton();
        deleteSelectedLineSegmentButton.setText("d_s_L");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(deleteSelectedLineSegmentButton, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer5, gbc);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 24;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel17, gbc);
        button1 = new JButton();
        button1.setText("Configure Grid");
        panel17.add(button1);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 29;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer6, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private void createUIComponents() {
        panel1 = this;
    }

    public void getData(CanvasModel data) {
        data.setFoldLineDividingNumber(StringOp.String2int(lineSegmentDivisionTextField.getText(), data.getFoldLineDividingNumber()));
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        lineSegmentDivisionTextField.setText(String.valueOf(data.getFoldLineDividingNumber()));

        if (e.getPropertyName() == null || e.getPropertyName().equals("lineColor")) {
            Color gray = new Color(150, 150, 150);

            colBlackButton.setBackground(gray);
            colRedButton.setBackground(gray);
            colBlueButton.setBackground(gray);
            colCyanButton.setBackground(gray);
            colBlackButton.setForeground(Color.black);
            colRedButton.setForeground(Color.black);
            colBlueButton.setForeground(Color.black);
            colCyanButton.setForeground(Color.black);

            switch (data.getLineColor()) {
                case BLACK_0:
                    colBlackButton.setBackground(Color.black);
                    colBlackButton.setForeground(Color.white);
                    break;
                case RED_1:
                    colRedButton.setBackground(Color.red);
                    colRedButton.setForeground(Color.black);
                    break;
                case BLUE_2:
                    colBlueButton.setBackground(Color.blue);
                    colBlueButton.setForeground(Color.black);
                    break;
                case CYAN_3:
                    colCyanButton.setBackground(Color.cyan);
                    colCyanButton.setForeground(Color.black);
            }
        }

        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
            toMountainButton.setForeground(Color.black);
            toValleyButton.setForeground(Color.black);
            toEdgeButton.setForeground(Color.black);
            toAuxButton.setForeground(Color.black); //HKとは補助活線のこと
            senbun_henkan2Button.setForeground(Color.black);

            toMountainButton.setBackground(Color.white);
            toValleyButton.setBackground(Color.white);
            toEdgeButton.setBackground(Color.white);
            toAuxButton.setBackground(Color.white);
            senbun_henkan2Button.setBackground(Color.white);

            switch (data.getMouseMode()) {
                case CREASE_MAKE_MOUNTAIN_23:
                    toMountainButton.setForeground(Color.black);
                    toMountainButton.setBackground(Color.red);
                    break;
                case CREASE_MAKE_VALLEY_24:
                    toValleyButton.setForeground(Color.black);
                    toValleyButton.setBackground(Color.blue);
                    break;
                case CREASE_MAKE_EDGE_25:
                    toEdgeButton.setForeground(Color.white);
                    toEdgeButton.setBackground(Color.black);
                    break;
                case CREASE_MAKE_AUX_60:
                    toAuxButton.setForeground(Color.black);
                    toAuxButton.setBackground(new Color(100, 200, 200));
                    break;
                case CREASE_TOGGLE_MV_58:
                    senbun_henkan2Button.setBackground(new Color(138, 43, 226));
                    break;
            }
        }

        if (e.getPropertyName() == null || e.getPropertyName().equals("selectionOperationMode")) {
            moveButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
            move2p2pButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
            copyButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
            copy2p2pButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));
            reflectButton.setBorder(new LineBorder(new Color(150, 150, 150), 1, false));

            switch (data.getSelectionOperationMode()) {
                case MOVE_1:
                    moveButton.setBorder(new LineBorder(Color.green, 3, false));
                    break;
                case MOVE4P_2:
                    move2p2pButton.setBorder(new LineBorder(Color.green, 3, false));
                    break;
                case COPY_3:
                    copyButton.setBorder(new LineBorder(Color.green, 3, false));
                    break;
                case COPY4P_4:
                    copy2p2pButton.setBorder(new LineBorder(Color.green, 3, false));
                    break;
                case MIRROR_5:
                    reflectButton.setBorder(new LineBorder(Color.green, 3, false));
                    break;
            }
        }
    }

    public void setData(HistoryStateModel historyStateModel) {
        undoRedo.setText(String.valueOf(historyStateModel.getHistoryTotal()));
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        coloredXRayCheckBox.setSelected(foldedFigureModel.isTransparencyColor());
    }
}
