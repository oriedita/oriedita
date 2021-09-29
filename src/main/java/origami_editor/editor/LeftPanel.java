package origami_editor.editor;

import origami.crease_pattern.element.LineColor;
import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.GridModel;
import origami_editor.editor.databinding.HistoryStateModel;
import origami_editor.editor.drawing_worker.FoldLineAdditionalInputMode;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.editor.task.TwoColoredTask;
import origami_editor.tools.StringOp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;

public class LeftPanel {
    private final GridConfigureDialog gridConfigureDialog;
    private JPanel root;

    private JTextField lineSegmentDivisionTextField;
    private UndoRedo undoRedo;
    private JButton lineWidthDecreaseButton;
    private JButton lineWidthIncreaseButton;
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
    private JButton rabbitEarButton;
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
    private JButton drawTwoColoredCpButton;
    private JButton suitei_01Button;
    private JButton koteimen_siteiButton;
    private JButton suitei_02Button;
    private JButton suitei_03Button;
    private JButton coloredXRayDecreaseButton;
    private JButton coloredXRayIncreaseButton;
    private JButton colRedButton;
    private JButton colBlueButton;
    private JButton colBlackButton;
    private JButton colCyanButton;
    private JButton lineSegmentDivisionSetButton;
    private JButton button1;
    private JCheckBox correctCpBeforeFoldingCheckBox;
    private JCheckBox selectPersistentCheckBox;
    private JCheckBox coloredXRayCheckBox;

    public LeftPanel(App app) {
        CanvasModel canvasModel = app.canvasModel;

        $$$setupUI$$$();

        app.registerButton(lineWidthDecreaseButton, "lineWidthDecreaseAction");
        app.registerButton(lineWidthIncreaseButton, "lineWidthIncreaseAction");
        app.registerButton(pointSizeDecreaseButton, "pointSizeDecreaseAction");
        app.registerButton(pointSizeIncreaseButton, "pointSizeIncreaseAction");
        app.registerButton(antiAliasToggleButton, "antiAliasToggleAction");
        app.registerButton(lineStyleChangeButton, "lineStyleChangeAction");
        app.registerButton(drawCreaseFreeButton, "drawCreaseFreeAction");
        app.registerButton(drawCreaseRestrictedButton, "drawCreaseRestrictedAction");
        app.registerButton(voronoiButton, "voronoiAction");
        app.registerButton(makeFlatFoldableButton, "makeFlatFoldableAction");
        app.registerButton(lengthenCreaseButton, "lengthenCreaseAction");
        app.registerButton(lengthenCrease2Button, "lengthenCrease2Action");
        app.registerButton(angleBisectorButton, "angleBisectorAction");
        app.registerButton(rabbitEarButton, "rabbitEarAction");
        app.registerButton(perpendicularDrawButton, "perpendicularDrawAction");
        app.registerButton(symmetricDrawButton, "symmetricDrawAction");
        app.registerButton(continuousSymmetricDrawButton, "continuousSymmetricDrawAction");
        app.registerButton(parallelDrawButton, "parallelDrawAction");
        app.registerButton(setParallelDrawWidthButton, "setParallelDrawWidthAction");
        app.registerButton(foldableLineDrawButton, "foldableLineDrawAction");
        app.registerButton(all_s_step_to_orisenButton, "all_s_step_to_orisenAction");
        app.registerButton(fishBoneDrawButton, "fishBoneDrawAction");
        app.registerButton(doubleSymmetricDrawButton, "doubleSymmetricDrawAction");
        app.registerButton(senbun_b_nyuryokuButton, "senbun_b_nyuryokuAction");
        app.registerButton(reflectButton, "reflectAction");
        app.registerButton(selectButton, "selectAction");
        app.registerButton(unselectButton, "unselectAction");
        app.registerButton(selectAllButton, "selectAllAction");
        app.registerButton(unselectAllButton, "unselectAllAction");
        app.registerButton(moveButton, "moveAction");
        app.registerButton(move2p2pButton, "move2p2pAction");
        app.registerButton(copyButton, "copyAction");
        app.registerButton(copy2p2pButton, "copy2p2pAction");
        app.registerButton(deleteSelectedLineSegmentButton, "deleteSelectedLineSegmentAction");
        app.registerButton(lineSegmentDeleteButton, "lineSegmentDeleteAction");
        app.registerButton(edgeLineSegmentDeleteButton, "edgeLineSegmentDeleteAction");
        app.registerButton(auxLiveLineSegmentDeleteButton, "auxLiveLineSegmentDeleteAction");
        app.registerButton(trimBranchesButton, "trimBranchesAction");
        app.registerButton(toMountainButton, "toMountainAction");
        app.registerButton(toValleyButton, "toValleyAction");
        app.registerButton(toEdgeButton, "toEdgeAction");
        app.registerButton(toAuxButton, "toAuxAction");
        app.registerButton(zen_yama_tani_henkanButton, "zen_yama_tani_henkanAction");
        app.registerButton(senbun_henkan2Button, "senbun_henkan2Action");
        app.registerButton(senbun_henkanButton, "senbun_henkanAction");
        app.registerButton(in_L_col_changeButton, "in_L_col_changeAction");
        app.registerButton(on_L_col_changeButton, "on_L_col_changeAction");
        app.registerButton(v_addButton, "vertexAddAction");
        app.registerButton(v_delButton, "vertexDeleteAction");
        app.registerButton(v_del_ccButton, "v_del_ccAction");
        app.registerButton(v_del_allButton, "v_del_allAction");
        app.registerButton(v_del_all_ccButton, "v_del_all_ccAction");
        app.registerButton(inputDataButton, "inputDataAction");
        app.registerButton(drawTwoColoredCpButton, "drawTwoColoredCpAction");
        app.registerButton(suitei_01Button, "suitei_01Action");
        app.registerButton(koteimen_siteiButton, "koteimen_siteiAction");
        app.registerButton(suitei_02Button, "suitei_02Action");
        app.registerButton(suitei_03Button, "suitei_03Action");
        app.registerButton(coloredXRayDecreaseButton, "coloredXRayDecreaseAction");
        app.registerButton(coloredXRayIncreaseButton, "coloredXRayIncreaseAction");
        app.registerButton(colRedButton, "colRedAction");
        app.registerButton(colBlueButton, "colBlueAction");
        app.registerButton(colBlackButton, "colBlackAction");
        app.registerButton(colCyanButton, "colCyanAction");
        app.registerButton(lineSegmentDivisionSetButton, "lineSegmentDivisionSetAction");
        app.registerButton(button1, "gridConfigureAction");

        app.registerButton(undoRedo.getRedoButton(), "redoAction");
        app.registerButton(undoRedo.getUndoButton(), "undoAction");
        app.registerButton(undoRedo.getSetUndoCountButton(), "setUndoCountAction");

        app.registerButton(correctCpBeforeFoldingCheckBox, "correctCpBeforeFoldingAction");
        app.registerButton(selectPersistentCheckBox, "selectPersistentAction");
        app.registerButton(coloredXRayCheckBox, "coloredXRayAction");


        undoRedo.addUndoActionListener(e -> {
            app.setTitle(app.mainDrawingWorker.undo());
            app.repaintCanvas();
        });
        undoRedo.addRedoActionListener(e -> {
            app.setTitle(app.mainDrawingWorker.redo());
            app.repaintCanvas();
        });
        undoRedo.addSetUndoCountActionListener(e -> app.historyStateModel.setHistoryTotal(StringOp.String2int(undoRedo.getText(), app.historyStateModel.getHistoryTotal())));
        lineWidthDecreaseButton.addActionListener(e -> canvasModel.decreaseLineWidth());
        lineWidthIncreaseButton.addActionListener(e -> canvasModel.increaseLineWidth());
        pointSizeDecreaseButton.addActionListener(e -> canvasModel.decreasePointSize());
        pointSizeIncreaseButton.addActionListener(e -> canvasModel.increasePointSize());
        antiAliasToggleButton.addActionListener(e -> canvasModel.toggleAntiAlias());
        lineStyleChangeButton.addActionListener(e -> canvasModel.advanceLineStyle());
        colRedButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.RED_1));
        colBlueButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.BLUE_2));
        colBlackButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.BLACK_0));
        colCyanButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.CYAN_3));
        drawCreaseFreeButton.addActionListener(e -> {
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_FREE_1);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        drawCreaseRestrictedButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_RESTRICTED_11);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_RESTRICTED_11);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        voronoiButton.addActionListener(e -> {
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.VORONOI_CREATE_62);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.VORONOI_CREATE_62);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        makeFlatFoldableButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        lengthenCreaseButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_5);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_5);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        lengthenCrease2Button.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        angleBisectorButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.SQUARE_BISECTOR_7);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.SQUARE_BISECTOR_7);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        rabbitEarButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.INWARD_8);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.INWARD_8);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        perpendicularDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.PERPENDICULAR_DRAW_9);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PERPENDICULAR_DRAW_9);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        symmetricDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.SYMMETRIC_DRAW_10);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.SYMMETRIC_DRAW_10);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        continuousSymmetricDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        parallelDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_40);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_40);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        setParallelDrawWidthButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_WIDTH_51);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_WIDTH_51);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        foldableLineDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.FOLDABLE_LINE_DRAW_71);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        all_s_step_to_orisenButton.addActionListener(e -> {
            System.out.println("i_egaki_dankai = " + app.mainDrawingWorker.getDrawingStage());
            System.out.println("i_kouho_dankai = " + app.mainDrawingWorker.getCandidateSize());

            app.mainDrawingWorker.all_s_step_to_orisen();
            app.repaintCanvas();
        });
        fishBoneDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.FISH_BONE_DRAW_33);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.FISH_BONE_DRAW_33);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        doubleSymmetricDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        lineSegmentDivisionSetButton.addActionListener(e -> {
            getData(app.canvasModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            app.repaintCanvas();
        });
        senbun_b_nyuryokuButton.addActionListener(e -> {
            getData(app.canvasModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        selectButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);

            app.repaintCanvas();
        });
        selectAllButton.addActionListener(e -> {
            app.mainDrawingWorker.select_all();
            app.repaintCanvas();
        });
        unselectButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_UNSELECT_20);

            app.repaintCanvas();
        });
        unselectAllButton.addActionListener(e -> {
            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        moveButton.addActionListener(e -> {
            app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MOVE_1);
            app.canvasModel.setMouseMode(MouseMode.CREASE_MOVE_21);

            app.repaintCanvas();
        });
        move2p2pButton.addActionListener(e -> {
            app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MOVE4P_2);
            app.canvasModel.setMouseMode(MouseMode.CREASE_MOVE_4P_31);

            app.repaintCanvas();
        });
        copyButton.addActionListener(e -> {
            app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.COPY_3);
            app.canvasModel.setMouseMode(MouseMode.CREASE_COPY_22);

            app.repaintCanvas();
        });
        copy2p2pButton.addActionListener(e -> {
            app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.COPY4P_4);
            app.canvasModel.setMouseMode(MouseMode.CREASE_COPY_4P_32);

            app.repaintCanvas();
        });
        reflectButton.addActionListener(e -> {
            app.canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MIRROR_5);
            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_SYMMETRIC_12);

            app.repaintCanvas();
        });
        deleteSelectedLineSegmentButton.addActionListener(e -> {
            app.mainDrawingWorker.del_selected_senbun();
            app.mainDrawingWorker.record();
            app.repaintCanvas();
        });
        lineSegmentDeleteButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        edgeLineSegmentDeleteButton.addActionListener(e -> {

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.BLACK_LINE_2);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        auxLiveLineSegmentDeleteButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.AUX_LIVE_LINE_3);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        trimBranchesButton.addActionListener(e -> {
            app.mainDrawingWorker.point_removal();
            app.mainDrawingWorker.overlapping_line_removal();
            app.mainDrawingWorker.branch_trim(0.000001);
            app.mainDrawingWorker.organizeCircles();
            app.mainDrawingWorker.record();
            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        toMountainButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MOUNTAIN_23);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        toValleyButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_VALLEY_24);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        toEdgeButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_EDGE_25);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        toAuxButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_AUX_60);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        zen_yama_tani_henkanButton.addActionListener(e -> {
            app.mainDrawingWorker.allMountainValleyChange();
            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        senbun_henkan2Button.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_TOGGLE_MV_58);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        senbun_henkanButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CHANGE_CREASE_TYPE_4);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        in_L_col_changeButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MV_34);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_MAKE_MV_34);

            if (app.canvasModel.getLineColor() == LineColor.BLACK_0) {
                app.canvasModel.setLineColor(LineColor.RED_1);
            }

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        on_L_col_changeButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASES_ALTERNATE_MV_36);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASES_ALTERNATE_MV_36);

            if (app.canvasModel.getLineColor() == LineColor.BLACK_0) {
                app.canvasModel.setLineColor(LineColor.BLUE_2);
            }

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        v_addButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DRAW_POINT_14);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        v_delButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DELETE_POINT_15);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        v_del_ccButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.VERTEX_DELETE_ON_CREASE_41);

            app.mainDrawingWorker.unselect_all();
            app.repaintCanvas();
        });
        v_del_allButton.addActionListener(e -> {
            app.mainDrawingWorker.v_del_all();
            System.out.println("mainDrawingWorker.v_del_all()");
            app.repaintCanvas();
        });
        v_del_all_ccButton.addActionListener(e -> {
            app.mainDrawingWorker.v_del_all_cc();
            System.out.println("mainDrawingWorker.v_del_all_cc()");
            app.repaintCanvas();
        });
        inputDataButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            System.out.println("readFile2Memo() 開始");
            File file = app.selectImportFile();
            Save save = app.readImportFile(file);
            System.out.println("readFile2Memo() 終了");

            if (save != null) {
                app.mainDrawingWorker.setSave_for_reading_tuika(save);
                app.mainDrawingWorker.record();
                app.repaintCanvas();
            }
        });
        correctCpBeforeFoldingCheckBox.addActionListener(e -> {
            app.canvasModel.setCorrectCpBeforeFolding(correctCpBeforeFoldingCheckBox.isSelected());

            app.repaintCanvas();
        });
        selectPersistentCheckBox.addActionListener(e -> {
            app.canvasModel.setSelectPersistent(selectPersistentCheckBox.isSelected());

            app.repaintCanvas();
        });
        drawTwoColoredCpButton.addActionListener(e -> {
            app.Ss0 = app.mainDrawingWorker.getForSelectFolding();

            if (app.mainDrawingWorker.getFoldLineTotalForSelectFolding() == 0) {        //折り線選択無し
                app.twoColorNoSelectedPolygonalLineWarning();//Warning: There is no selected polygonal line


            } else if (app.mainDrawingWorker.getFoldLineTotalForSelectFolding() > 0) {
                app.folding_prepare();//ここでOZがOAZ(0)からOAZ(i)に切り替わる
                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;

                app.executeTask(new TwoColoredTask(app));
            }

            app.mainDrawingWorker.unselect_all();
        });
        suitei_01Button.addActionListener(e -> {
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_1);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!app.canvasModel.getSelectPersistent()) {
                app.mainDrawingWorker.unselect_all();
            }
        });
        koteimen_siteiButton.addActionListener(e -> {
            if (app.OZ.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                app.canvasModel.setMouseMode(MouseMode.CHANGE_STANDARD_FACE_103);
            }
        });
        suitei_02Button.addActionListener(e -> {
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_2);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!app.canvasModel.getSelectPersistent()) {
                app.mainDrawingWorker.unselect_all();
            }
        });
        suitei_03Button.addActionListener(e -> {
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_3);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (!app.canvasModel.getSelectPersistent()) {
                app.mainDrawingWorker.unselect_all();
            }
        });
        coloredXRayCheckBox.addActionListener(e -> app.foldedFigureModel.setTransparencyColor(coloredXRayCheckBox.isSelected()));
        coloredXRayDecreaseButton.addActionListener(e -> app.foldedFigureModel.decreaseTransparency());
        coloredXRayIncreaseButton.addActionListener(e -> app.foldedFigureModel.increaseTransparency());
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridBagLayout());
        undoRedo = new UndoRedo();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(undoRedo.$$$getRootComponent$$$(), gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel1, gbc);
        lineWidthDecreaseButton = new JButton();
        lineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(lineWidthDecreaseButton, gbc);
        lineWidthIncreaseButton = new JButton();
        lineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(lineWidthIncreaseButton, gbc);
        pointSizeDecreaseButton = new JButton();
        pointSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(pointSizeDecreaseButton, gbc);
        pointSizeIncreaseButton = new JButton();
        pointSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(pointSizeIncreaseButton, gbc);
        antiAliasToggleButton = new JButton();
        antiAliasToggleButton.setMinimumSize(new Dimension(60, 30));
        antiAliasToggleButton.setText("a_a");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(antiAliasToggleButton, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel2, gbc);
        colRedButton = new JButton();
        colRedButton.setBackground(new Color(-6908266));
        colRedButton.setText("M");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(colRedButton, gbc);
        colBlueButton = new JButton();
        colBlueButton.setBackground(new Color(-6908266));
        colBlueButton.setText("V");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(colBlueButton, gbc);
        colBlackButton = new JButton();
        colBlackButton.setBackground(new Color(-6908266));
        colBlackButton.setText("E");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(colBlackButton, gbc);
        colCyanButton = new JButton();
        colCyanButton.setBackground(new Color(-6908266));
        colCyanButton.setText("A");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(colCyanButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel3, gbc);
        lineStyleChangeButton = new JButton();
        lineStyleChangeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orisen_hyougen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel3.add(lineStyleChangeButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel4, gbc);
        lineSegmentDeleteButton = new JButton();
        lineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(lineSegmentDeleteButton, gbc);
        edgeLineSegmentDeleteButton = new JButton();
        edgeLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kuro_senbun_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(edgeLineSegmentDeleteButton, gbc);
        auxLiveLineSegmentDeleteButton = new JButton();
        auxLiveLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun3_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(auxLiveLineSegmentDeleteButton, gbc);
        trimBranchesButton = new JButton();
        trimBranchesButton.setIcon(new ImageIcon(getClass().getResource("/ppp/eda_kesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(trimBranchesButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel5, gbc);
        toMountainButton = new JButton();
        toMountainButton.setIcon(new ImageIcon(getClass().getResource("/ppp/M_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(toMountainButton, gbc);
        toValleyButton = new JButton();
        toValleyButton.setIcon(new ImageIcon(getClass().getResource("/ppp/V_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(toValleyButton, gbc);
        toEdgeButton = new JButton();
        toEdgeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/E_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(toEdgeButton, gbc);
        toAuxButton = new JButton();
        toAuxButton.setIcon(new ImageIcon(getClass().getResource("/ppp/HK_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(toAuxButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel6, gbc);
        zen_yama_tani_henkanButton = new JButton();
        zen_yama_tani_henkanButton.setText("AC");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(zen_yama_tani_henkanButton, gbc);
        senbun_henkan2Button = new JButton();
        senbun_henkan2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(senbun_henkan2Button, gbc);
        senbun_henkanButton = new JButton();
        senbun_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(senbun_henkanButton, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel7, gbc);
        in_L_col_changeButton = new JButton();
        in_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/in_L_col_change.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(in_L_col_changeButton, gbc);
        on_L_col_changeButton = new JButton();
        on_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/on_L_col_change.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(on_L_col_changeButton, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 21;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel8, gbc);
        v_addButton = new JButton();
        v_addButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_add.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(v_addButton, gbc);
        v_delButton = new JButton();
        v_delButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(v_delButton, gbc);
        v_del_ccButton = new JButton();
        v_del_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_cc.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(v_del_ccButton, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 22;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel9, gbc);
        v_del_allButton = new JButton();
        v_del_allButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_del_allButton, gbc);
        v_del_all_ccButton = new JButton();
        v_del_all_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all_cc.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_del_all_ccButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 25;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer2, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 26;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel10, gbc);
        inputDataButton = new JButton();
        inputDataButton.setText("Op");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel10.add(inputDataButton, gbc);
        correctCpBeforeFoldingCheckBox = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel10.add(correctCpBeforeFoldingCheckBox, gbc);
        selectPersistentCheckBox = new JCheckBox();
        selectPersistentCheckBox.setToolTipText("ckbox_select_nokosi");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel10.add(selectPersistentCheckBox, gbc);
        drawTwoColoredCpButton = new JButton();
        drawTwoColoredCpButton.setIcon(new ImageIcon(getClass().getResource("/ppp/2syoku_tenkaizu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel10.add(drawTwoColoredCpButton, gbc);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 27;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel11, gbc);
        suitei_01Button = new JButton();
        suitei_01Button.setText("CP_rcg");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel11.add(suitei_01Button, gbc);
        koteimen_siteiButton = new JButton();
        koteimen_siteiButton.setText("S_face");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel11.add(koteimen_siteiButton, gbc);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 28;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel12, gbc);
        suitei_02Button = new JButton();
        suitei_02Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_02.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel12.add(suitei_02Button, gbc);
        suitei_03Button = new JButton();
        suitei_03Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_03.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel12.add(suitei_03Button, gbc);
        coloredXRayCheckBox = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel12.add(coloredXRayCheckBox, gbc);
        coloredXRayDecreaseButton = new JButton();
        coloredXRayDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel12.add(coloredXRayDecreaseButton, gbc);
        coloredXRayIncreaseButton = new JButton();
        coloredXRayIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel12.add(coloredXRayIncreaseButton, gbc);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel13, gbc);
        drawCreaseFreeButton = new JButton();
        drawCreaseFreeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(drawCreaseFreeButton, gbc);
        drawCreaseRestrictedButton = new JButton();
        drawCreaseRestrictedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku11.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(drawCreaseRestrictedButton, gbc);
        voronoiButton = new JButton();
        voronoiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Voronoi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(voronoiButton, gbc);
        makeFlatFoldableButton = new JButton();
        makeFlatFoldableButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(makeFlatFoldableButton, gbc);
        lengthenCreaseButton = new JButton();
        lengthenCreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(lengthenCreaseButton, gbc);
        lengthenCrease2Button = new JButton();
        lengthenCrease2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou_2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(lengthenCrease2Button, gbc);
        angleBisectorButton = new JButton();
        angleBisectorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kaku_toubun.png")));
        angleBisectorButton.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(angleBisectorButton, gbc);
        rabbitEarButton = new JButton();
        rabbitEarButton.setIcon(new ImageIcon(getClass().getResource("/ppp/naishin.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel13.add(rabbitEarButton, gbc);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel14, gbc);
        perpendicularDrawButton = new JButton();
        perpendicularDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suisen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(perpendicularDrawButton, gbc);
        symmetricDrawButton = new JButton();
        symmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(symmetricDrawButton, gbc);
        parallelDrawButton = new JButton();
        parallelDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(parallelDrawButton, gbc);
        setParallelDrawWidthButton = new JButton();
        setParallelDrawWidthButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen_haba_sitei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(setParallelDrawWidthButton, gbc);
        all_s_step_to_orisenButton = new JButton();
        all_s_step_to_orisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/all_s_step_to_orisen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(all_s_step_to_orisenButton, gbc);
        fishBoneDrawButton = new JButton();
        fishBoneDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sakananohone.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(fishBoneDrawButton, gbc);
        lineSegmentDivisionTextField = new JTextField();
        lineSegmentDivisionTextField.setColumns(2);
        lineSegmentDivisionTextField.setText("2");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(lineSegmentDivisionTextField, gbc);
        lineSegmentDivisionSetButton = new JButton();
        lineSegmentDivisionSetButton.setText("Set");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(lineSegmentDivisionSetButton, gbc);
        continuousSymmetricDrawButton = new JButton();
        continuousSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/renzoku_orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(continuousSymmetricDrawButton, gbc);
        foldableLineDrawButton = new JButton();
        foldableLineDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen_and_kousitenkei_simple.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(foldableLineDrawButton, gbc);
        doubleSymmetricDrawButton = new JButton();
        doubleSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/fuku_orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(doubleSymmetricDrawButton, gbc);
        senbun_b_nyuryokuButton = new JButton();
        senbun_b_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_b_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel14.add(senbun_b_nyuryokuButton, gbc);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel15, gbc);
        selectButton = new JButton();
        selectButton.setBackground(new Color(-16711936));
        selectButton.setText("sel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(selectButton, gbc);
        selectAllButton = new JButton();
        selectAllButton.setBackground(new Color(-16711936));
        selectAllButton.setText("s_al");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(selectAllButton, gbc);
        unselectButton = new JButton();
        unselectButton.setBackground(new Color(-16711936));
        unselectButton.setText("unsel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(unselectButton, gbc);
        unselectAllButton = new JButton();
        unselectAllButton.setBackground(new Color(-16711936));
        unselectAllButton.setText("uns_al");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(unselectAllButton, gbc);
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
        panel15.add(moveButton, gbc);
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
        panel15.add(move2p2pButton, gbc);
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
        panel15.add(copyButton, gbc);
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
        panel15.add(copy2p2pButton, gbc);
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
        panel15.add(reflectButton, gbc);
        deleteSelectedLineSegmentButton = new JButton();
        deleteSelectedLineSegmentButton.setText("d_s_L");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(deleteSelectedLineSegmentButton, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer5, gbc);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 24;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel16, gbc);
        button1 = new JButton();
        button1.setText("Configure Grid");
        panel16.add(button1);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 29;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer6, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    public void getData(CanvasModel data) {
        data.setFoldLineDividingNumber(StringOp.String2int(lineSegmentDivisionTextField.getText(), data.getFoldLineDividingNumber()));
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        lineSegmentDivisionTextField.setText(String.valueOf(data.getFoldLineDividingNumber()));

        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode") || e.getPropertyName().equals("foldLineAdditionalInputMode")) {
            MouseMode m = data.getMouseMode();
            FoldLineAdditionalInputMode f = data.getFoldLineAdditionalInputMode();

            drawCreaseFreeButton.setSelected(m == MouseMode.DRAW_CREASE_FREE_1);
            lineSegmentDeleteButton.setSelected(m == MouseMode.LINE_SEGMENT_DELETE_3 && f == FoldLineAdditionalInputMode.POLY_LINE_0);
            edgeLineSegmentDeleteButton.setSelected(m == MouseMode.LINE_SEGMENT_DELETE_3 && f == FoldLineAdditionalInputMode.BLACK_LINE_2);
            auxLiveLineSegmentDeleteButton.setSelected(m == MouseMode.LINE_SEGMENT_DELETE_3 && f == FoldLineAdditionalInputMode.AUX_LIVE_LINE_3);
            senbun_henkanButton.setSelected(m == MouseMode.CHANGE_CREASE_TYPE_4);
            lengthenCreaseButton.setSelected(m == MouseMode.LENGTHEN_CREASE_5);
            angleBisectorButton.setSelected(m == MouseMode.SQUARE_BISECTOR_7);
            rabbitEarButton.setSelected(m == MouseMode.INWARD_8);
            perpendicularDrawButton.setSelected(m == MouseMode.PERPENDICULAR_DRAW_9);
            symmetricDrawButton.setSelected(m == MouseMode.SYMMETRIC_DRAW_10);
            drawCreaseRestrictedButton.setSelected(m == MouseMode.DRAW_CREASE_RESTRICTED_11);
            reflectButton.setSelected(m == MouseMode.DRAW_CREASE_SYMMETRIC_12);
            v_addButton.setSelected(m == MouseMode.DRAW_POINT_14);
            v_delButton.setSelected(m == MouseMode.DELETE_POINT_15);
            selectButton.setSelected(m == MouseMode.CREASE_SELECT_19);
            unselectButton.setSelected(m == MouseMode.CREASE_UNSELECT_20);
            moveButton.setSelected(m == MouseMode.CREASE_MOVE_21);
            copyButton.setSelected(m == MouseMode.CREASE_COPY_22);
            toMountainButton.setSelected(m == MouseMode.CREASE_MAKE_MOUNTAIN_23);
            toValleyButton.setSelected(m == MouseMode.CREASE_MAKE_VALLEY_24);
            toEdgeButton.setSelected(m == MouseMode.CREASE_MAKE_EDGE_25);
            senbun_b_nyuryokuButton.setSelected(m == MouseMode.LINE_SEGMENT_DIVISION_27);
            move2p2pButton.setSelected(m == MouseMode.CREASE_MOVE_4P_31);
            copy2p2pButton.setSelected(m == MouseMode.CREASE_COPY_4P_32);
            fishBoneDrawButton.setSelected(m == MouseMode.FISH_BONE_DRAW_33);
            in_L_col_changeButton.setSelected(m == MouseMode.CREASE_MAKE_MV_34);
            doubleSymmetricDrawButton.setSelected(m == MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            on_L_col_changeButton.setSelected(m == MouseMode.CREASES_ALTERNATE_MV_36);
            makeFlatFoldableButton.setSelected(m == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
            parallelDrawButton.setSelected(m == MouseMode.PARALLEL_DRAW_40);
            v_del_ccButton.setSelected(m == MouseMode.VERTEX_DELETE_ON_CREASE_41);
            setParallelDrawWidthButton.setSelected(m == MouseMode.PARALLEL_DRAW_WIDTH_51);
            continuousSymmetricDrawButton.setSelected(m == MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            senbun_henkan2Button.setSelected(m == MouseMode.CREASE_TOGGLE_MV_58);
            toAuxButton.setSelected(m == MouseMode.CREASE_MAKE_AUX_60);
            voronoiButton.setSelected(m == MouseMode.VORONOI_CREATE_62);
            lengthenCrease2Button.setSelected(m == MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            foldableLineDrawButton.setSelected(m == MouseMode.FOLDABLE_LINE_DRAW_71);
            koteimen_siteiButton.setSelected(m == MouseMode.CHANGE_STANDARD_FACE_103);
        }

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
