package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import origami.crease_pattern.element.LineColor;
import origami_editor.editor.component.ColorIcon;
import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.databinding.*;
import origami_editor.editor.canvas.FoldLineAdditionalInputMode;
import origami.folding.FoldedFigure;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.task.TaskExecutor;
import origami_editor.editor.task.TwoColoredTask;
import origami_editor.tools.StringOp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Arrays;

public class LeftPanel {
    private final App app;
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
    private JCheckBox correctCpBeforeFoldingCheckBox;
    private JCheckBox selectPersistentCheckBox;
    private JCheckBox coloredXRayCheckBox;
    private JButton resetButton;
    private JButton moveIntervalGridVerticalButton;
    private JTextField intervalGridSizeTextField;
    private JButton setIntervalGridSizeButton;
    private JButton moveIntervalGridHorizontal;
    private JButton intervalGridColorButton;
    private JButton gridLineWidthDecreaseButton;
    private JButton gridLineWidthIncreaseButton;
    private JButton changeGridStateButton;
    private JButton gridSizeDecreaseButton;
    private JTextField gridSizeTextField;
    private JButton gridSizeSetButton;
    private JButton gridSizeIncreaseButton;
    private JButton gridColorButton;
    private JTextField gridXATextField;
    private JTextField gridXBTextField;
    private JTextField gridXCTextField;
    private JTextField gridYATextField;
    private JTextField gridYBTextField;
    private JTextField gridYCTextField;
    private JTextField gridAngleTextField;
    private JButton setGridParametersButton;

    public LeftPanel(App app) {
        this.app = app;
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

        app.registerButton(gridSizeDecreaseButton, "gridSizeDecreaseAction");
        app.registerButton(gridSizeSetButton, "gridSizeSetAction");
        app.registerButton(gridSizeIncreaseButton, "gridSizeIncreaseAction");
        app.registerButton(gridColorButton, "gridColorAction");
        app.registerButton(gridLineWidthDecreaseButton, "gridLineWidthDecreaseAction");
        app.registerButton(gridLineWidthIncreaseButton, "gridLineWidthIncreaseAction");
        app.registerButton(changeGridStateButton, "changeGridStateAction");
        app.registerButton(moveIntervalGridVerticalButton, "moveIntervalGridVerticalAction");
        app.registerButton(setIntervalGridSizeButton, "setIntervalGridSizeAction");
        app.registerButton(moveIntervalGridHorizontal, "moveIntervalGridHorizontalAction");
        app.registerButton(intervalGridColorButton, "intervalGridColorAction");
        app.registerButton(setGridParametersButton, "setGridParametersAction");
        app.registerButton(resetButton, "gridConfigureResetAction");

        app.registerButton(undoRedo.getRedoButton(), "redoAction");
        app.registerButton(undoRedo.getUndoButton(), "undoAction");

        app.registerButton(correctCpBeforeFoldingCheckBox, "correctCpBeforeFoldingAction");
        app.registerButton(selectPersistentCheckBox, "selectPersistentAction");
        app.registerButton(coloredXRayCheckBox, "coloredXRayAction");


        undoRedo.addUndoActionListener(e -> {
            app.mainCreasePatternWorker.undo();
            app.repaintCanvas();
        });
        undoRedo.addRedoActionListener(e -> {
            app.mainCreasePatternWorker.redo();
            app.repaintCanvas();
        });
        lineWidthDecreaseButton.addActionListener(e -> app.applicationModel.decreaseLineWidth());
        lineWidthIncreaseButton.addActionListener(e -> app.applicationModel.increaseLineWidth());
        pointSizeDecreaseButton.addActionListener(e -> app.applicationModel.decreasePointSize());
        pointSizeIncreaseButton.addActionListener(e -> app.applicationModel.increasePointSize());
        antiAliasToggleButton.addActionListener(e -> app.applicationModel.toggleAntiAlias());
        lineStyleChangeButton.addActionListener(e -> app.applicationModel.advanceLineStyle());
        colRedButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.RED_1));
        colBlueButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.BLUE_2));
        colBlackButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.BLACK_0));
        colCyanButton.addActionListener(e -> app.canvasModel.setLineColor(LineColor.CYAN_3));
        drawCreaseFreeButton.addActionListener(e -> {
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_FREE_1);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        drawCreaseRestrictedButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_RESTRICTED_11);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_RESTRICTED_11);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        voronoiButton.addActionListener(e -> {
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.VORONOI_CREATE_62);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.VORONOI_CREATE_62);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        makeFlatFoldableButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        lengthenCreaseButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_5);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_5);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        lengthenCrease2Button.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        angleBisectorButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.SQUARE_BISECTOR_7);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.SQUARE_BISECTOR_7);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        rabbitEarButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.INWARD_8);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.INWARD_8);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        perpendicularDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.PERPENDICULAR_DRAW_9);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PERPENDICULAR_DRAW_9);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        symmetricDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.SYMMETRIC_DRAW_10);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.SYMMETRIC_DRAW_10);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        continuousSymmetricDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        parallelDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_40);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_40);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        setParallelDrawWidthButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_WIDTH_51);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_WIDTH_51);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        foldableLineDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.FOLDABLE_LINE_DRAW_71);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        all_s_step_to_orisenButton.addActionListener(e -> {
            System.out.println("i_egaki_dankai = " + app.mainCreasePatternWorker.getDrawingStage());
            System.out.println("i_kouho_dankai = " + app.mainCreasePatternWorker.getCandidateSize());

            app.mainCreasePatternWorker.all_s_step_to_orisen();
            app.repaintCanvas();
        });
        fishBoneDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.FISH_BONE_DRAW_33);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.FISH_BONE_DRAW_33);

            app.mainCreasePatternWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        doubleSymmetricDrawButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);

            app.mainCreasePatternWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        lineSegmentDivisionSetButton.addActionListener(e -> {
            getData(app.applicationModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            app.repaintCanvas();
        });
        senbun_b_nyuryokuButton.addActionListener(e -> {
            getData(app.applicationModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        selectButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);

            app.repaintCanvas();
        });
        selectAllButton.addActionListener(e -> {
            app.mainCreasePatternWorker.select_all();
            app.repaintCanvas();
        });
        unselectButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_UNSELECT_20);

            app.repaintCanvas();
        });
        unselectAllButton.addActionListener(e -> {
            app.mainCreasePatternWorker.unselect_all();
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
            app.mainCreasePatternWorker.del_selected_senbun();
            app.mainCreasePatternWorker.record();
            app.repaintCanvas();
        });
        lineSegmentDeleteButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        edgeLineSegmentDeleteButton.addActionListener(e -> {

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.BLACK_LINE_2);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        auxLiveLineSegmentDeleteButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.AUX_LIVE_LINE_3);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        trimBranchesButton.addActionListener(e -> {
            app.mainCreasePatternWorker.point_removal();
            app.mainCreasePatternWorker.overlapping_line_removal();
            app.mainCreasePatternWorker.branch_trim();
            app.mainCreasePatternWorker.organizeCircles();
            app.mainCreasePatternWorker.record();
            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        toMountainButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MOUNTAIN_23);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        toValleyButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_VALLEY_24);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        toEdgeButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_EDGE_25);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        toAuxButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_AUX_60);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        zen_yama_tani_henkanButton.addActionListener(e -> {
            app.mainCreasePatternWorker.allMountainValleyChange();
            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        senbun_henkan2Button.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_TOGGLE_MV_58);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        senbun_henkanButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CHANGE_CREASE_TYPE_4);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        in_L_col_changeButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MV_34);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_MAKE_MV_34);

            if (app.canvasModel.getLineColor() == LineColor.BLACK_0) {
                app.canvasModel.setLineColor(LineColor.RED_1);
            }

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        on_L_col_changeButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASES_ALTERNATE_MV_36);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASES_ALTERNATE_MV_36);

            if (app.canvasModel.getLineColor() == LineColor.BLACK_0) {
                app.canvasModel.setLineColor(LineColor.BLUE_2);
            }

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        v_addButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DRAW_POINT_14);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        v_delButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.DELETE_POINT_15);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        v_del_ccButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.VERTEX_DELETE_ON_CREASE_41);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        v_del_allButton.addActionListener(e -> {
            app.mainCreasePatternWorker.v_del_all();
            System.out.println("mainDrawingWorker.v_del_all()");
            app.repaintCanvas();
        });
        v_del_all_ccButton.addActionListener(e -> {
            app.mainCreasePatternWorker.v_del_all_cc();
            System.out.println("mainDrawingWorker.v_del_all_cc()");
            app.repaintCanvas();
        });
        correctCpBeforeFoldingCheckBox.addActionListener(e -> {
            app.applicationModel.setCorrectCpBeforeFolding(correctCpBeforeFoldingCheckBox.isSelected());

            app.repaintCanvas();
        });
        selectPersistentCheckBox.addActionListener(e -> {
            app.applicationModel.setSelectPersistent(selectPersistentCheckBox.isSelected());

            app.repaintCanvas();
        });
        drawTwoColoredCpButton.addActionListener(e -> {
            app.lineSegmentsForFolding = app.mainCreasePatternWorker.getForSelectFolding();

            if (app.mainCreasePatternWorker.getFoldLineTotalForSelectFolding() == 0) {        //折り線選択無し
                app.twoColorNoSelectedPolygonalLineWarning();//Warning: There is no selected polygonal line


            } else if (app.mainCreasePatternWorker.getFoldLineTotalForSelectFolding() > 0) {
                TaskExecutor.executeTask("Two Colored CP", new TwoColoredTask(app));
            }

            app.mainCreasePatternWorker.unselect_all();
        });
        suitei_01Button.addActionListener(e -> {
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_1);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!app.applicationModel.getSelectPersistent()) {
                app.mainCreasePatternWorker.unselect_all();
            }
        });
        koteimen_siteiButton.addActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

            if (selectedFigure != null && selectedFigure.foldedFigure.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                app.canvasModel.setMouseMode(MouseMode.CHANGE_STANDARD_FACE_103);
            }
        });
        suitei_02Button.addActionListener(e -> {
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_2);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!app.applicationModel.getSelectPersistent()) {
                app.mainCreasePatternWorker.unselect_all();
            }
        });
        suitei_03Button.addActionListener(e -> {
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_3);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (!app.applicationModel.getSelectPersistent()) {
                app.mainCreasePatternWorker.unselect_all();
            }
        });
        coloredXRayCheckBox.addActionListener(e -> app.foldedFigureModel.setTransparencyColor(coloredXRayCheckBox.isSelected()));
        coloredXRayDecreaseButton.addActionListener(e -> app.foldedFigureModel.decreaseTransparency());
        coloredXRayIncreaseButton.addActionListener(e -> app.foldedFigureModel.increaseTransparency());

        GridModel gridModel = app.gridModel;
        gridSizeDecreaseButton.addActionListener(e -> {
            int gridSize = gridModel.getGridSize();

            gridSize = gridSize / 2;
            if (gridSize < 1) {
                gridSize = 1;
            }

            gridModel.setGridSize(gridSize);
        });
        gridSizeSetButton.addActionListener(e -> getData(gridModel));
        gridSizeIncreaseButton.addActionListener(e -> gridModel.setGridSize(gridModel.getGridSize() * 2));
        gridColorButton.addActionListener(e -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", FlatLaf.isLafDark() ? Colors.GRID_LINE_DARK : Colors.GRID_LINE);
            if (color != null) {
                app.applicationModel.setGridColor(color);
            }
            //以上でやりたいことは書き終わり
        });
        gridLineWidthDecreaseButton.addActionListener(e -> app.applicationModel.decreaseGridLineWidth());
        gridLineWidthIncreaseButton.addActionListener(e -> app.applicationModel.increaseGridLineWidth());
        changeGridStateButton.addActionListener(e -> gridModel.advanceBaseState());
        moveIntervalGridVerticalButton.addActionListener(e -> gridModel.changeHorizontalScalePosition());
        setIntervalGridSizeButton.addActionListener(e -> getData(gridModel));
        moveIntervalGridHorizontal.addActionListener(e -> gridModel.changeVerticalScalePosition());
        intervalGridColorButton.addActionListener(e -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", FlatLaf.isLafDark() ? Colors.GRID_SCALE_DARK : Colors.GRID_SCALE);
            if (color != null) {
                app.applicationModel.setGridScaleColor(color);
            }
        });
        setGridParametersButton.addActionListener(e -> getData(gridModel));
        resetButton.addActionListener(e -> gridModel.reset());
    }


    public void getData(GridModel data) {
        data.setIntervalGridSize(StringOp.String2int(intervalGridSizeTextField.getText(), data.getIntervalGridSize()));
        data.setGridSize(StringOp.String2int(gridSizeTextField.getText(), data.getGridSize()));
        data.setGridXA(app.string2double(gridXATextField.getText(), data.getGridXA()));
        data.setGridXB(app.string2double(gridXBTextField.getText(), data.getGridXB()));
        data.setGridXC(app.string2double(gridXCTextField.getText(), data.getGridXC()));
        data.setGridYA(app.string2double(gridYATextField.getText(), data.getGridYA()));
        data.setGridYB(app.string2double(gridYBTextField.getText(), data.getGridYB()));
        data.setGridYC(app.string2double(gridYCTextField.getText(), data.getGridYC()));
        data.setGridAngle(app.string2double(gridAngleTextField.getText(), data.getGridAngle()));
    }

    public void setData(GridModel data) {
        intervalGridSizeTextField.setText(String.valueOf(data.getIntervalGridSize()));
        gridSizeTextField.setText(String.valueOf(data.getGridSize()));
        gridXATextField.setText(String.valueOf(data.getGridXA()));
        gridXBTextField.setText(String.valueOf(data.getGridXB()));
        gridXCTextField.setText(String.valueOf(data.getGridXC()));
        gridYATextField.setText(String.valueOf(data.getGridYA()));
        gridYBTextField.setText(String.valueOf(data.getGridYB()));
        gridYCTextField.setText(String.valueOf(data.getGridYC()));
        gridAngleTextField.setText(String.valueOf(data.getGridAngle()));

        gridSizeDecreaseButton.setEnabled(data.getGridSize() != 1);
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
        root.setLayout(new GridLayoutManager(21, 1, new Insets(1, 1, 1, 1), 1, 1));
        undoRedo = new UndoRedo();
        root.add(undoRedo.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), 1, 1, true, true));
        root.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineWidthDecreaseButton = new JButton();
        lineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_sage.png")));
        panel1.add(lineWidthDecreaseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineWidthIncreaseButton = new JButton();
        lineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_age.png")));
        panel1.add(lineWidthIncreaseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pointSizeDecreaseButton = new JButton();
        pointSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_sage.png")));
        panel1.add(pointSizeDecreaseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pointSizeIncreaseButton = new JButton();
        pointSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_age.png")));
        panel1.add(pointSizeIncreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        antiAliasToggleButton = new JButton();
        antiAliasToggleButton.setMinimumSize(new Dimension(60, 30));
        antiAliasToggleButton.setText("a_a");
        panel1.add(antiAliasToggleButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(0, 0), new Dimension(0, 0), null, 0, false));
        lineStyleChangeButton = new JButton();
        lineStyleChangeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orisen_hyougen.png")));
        panel1.add(lineStyleChangeButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colRedButton = new JButton();
        colRedButton.setBackground(new Color(-6908266));
        colRedButton.setText("M");
        panel2.add(colRedButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colBlueButton = new JButton();
        colBlueButton.setBackground(new Color(-6908266));
        colBlueButton.setText("V");
        panel2.add(colBlueButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colBlackButton = new JButton();
        colBlackButton.setBackground(new Color(-6908266));
        colBlackButton.setText("E");
        panel2.add(colBlackButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colCyanButton = new JButton();
        colCyanButton.setBackground(new Color(-6908266));
        colCyanButton.setText("A");
        panel2.add(colCyanButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel3, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toMountainButton = new JButton();
        toMountainButton.setIcon(new ImageIcon(getClass().getResource("/ppp/M_nisuru.png")));
        panel3.add(toMountainButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toValleyButton = new JButton();
        toValleyButton.setIcon(new ImageIcon(getClass().getResource("/ppp/V_nisuru.png")));
        panel3.add(toValleyButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toEdgeButton = new JButton();
        toEdgeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/E_nisuru.png")));
        panel3.add(toEdgeButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toAuxButton = new JButton();
        toAuxButton.setIcon(new ImageIcon(getClass().getResource("/ppp/HK_nisuru.png")));
        panel3.add(toAuxButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel4, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        zen_yama_tani_henkanButton = new JButton();
        zen_yama_tani_henkanButton.setText("AC");
        panel4.add(zen_yama_tani_henkanButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(0, -1), new Dimension(0, -1), null, 0, false));
        senbun_henkan2Button = new JButton();
        senbun_henkan2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan2.png")));
        panel4.add(senbun_henkan2Button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        senbun_henkanButton = new JButton();
        senbun_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan.png")));
        panel4.add(senbun_henkanButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel5, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        in_L_col_changeButton = new JButton();
        in_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/in_L_col_change.png")));
        panel5.add(in_L_col_changeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        on_L_col_changeButton = new JButton();
        on_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/on_L_col_change.png")));
        panel5.add(on_L_col_changeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel6, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_addButton = new JButton();
        v_addButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_add.png")));
        panel6.add(v_addButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_delButton = new JButton();
        v_delButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del.png")));
        panel6.add(v_delButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_ccButton = new JButton();
        v_del_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_cc.png")));
        panel6.add(v_del_ccButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel7, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_allButton = new JButton();
        v_del_allButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all.png")));
        panel7.add(v_del_allButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_all_ccButton = new JButton();
        v_del_all_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all_cc.png")));
        panel7.add(v_del_all_ccButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel8, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        correctCpBeforeFoldingCheckBox = new JCheckBox();
        correctCpBeforeFoldingCheckBox.setText("Correct");
        panel8.add(correctCpBeforeFoldingCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectPersistentCheckBox = new JCheckBox();
        selectPersistentCheckBox.setText("Persist");
        selectPersistentCheckBox.setToolTipText("ckbox_select_nokosi");
        panel8.add(selectPersistentCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawTwoColoredCpButton = new JButton();
        drawTwoColoredCpButton.setIcon(new ImageIcon(getClass().getResource("/ppp/2syoku_tenkaizu.png")));
        panel8.add(drawTwoColoredCpButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel9, new GridConstraints(18, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_01Button = new JButton();
        suitei_01Button.setText("CP_rcg");
        panel9.add(suitei_01Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        koteimen_siteiButton = new JButton();
        koteimen_siteiButton.setText("S_face");
        panel9.add(koteimen_siteiButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel10, new GridConstraints(19, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_02Button = new JButton();
        suitei_02Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_02.png")));
        panel10.add(suitei_02Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_03Button = new JButton();
        suitei_03Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_03.png")));
        panel10.add(suitei_03Button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayCheckBox = new JCheckBox();
        coloredXRayCheckBox.setText("Colored");
        panel10.add(coloredXRayCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayDecreaseButton = new JButton();
        coloredXRayDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        panel10.add(coloredXRayDecreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayIncreaseButton = new JButton();
        coloredXRayIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        panel10.add(coloredXRayIncreaseButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel11, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        perpendicularDrawButton = new JButton();
        perpendicularDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suisen.png")));
        panel11.add(perpendicularDrawButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineSegmentDivisionTextField = new JTextField();
        lineSegmentDivisionTextField.setText("2");
        panel11.add(lineSegmentDivisionTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        lineSegmentDivisionSetButton = new JButton();
        lineSegmentDivisionSetButton.setText("Set");
        panel11.add(lineSegmentDivisionSetButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        senbun_b_nyuryokuButton = new JButton();
        senbun_b_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_b_nyuryoku.png")));
        panel11.add(senbun_b_nyuryokuButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        all_s_step_to_orisenButton = new JButton();
        all_s_step_to_orisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/all_s_step_to_orisen.png")));
        panel11.add(all_s_step_to_orisenButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        voronoiButton = new JButton();
        voronoiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Voronoi.png")));
        panel11.add(voronoiButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parallelDrawButton = new JButton();
        parallelDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen.png")));
        panel11.add(parallelDrawButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fishBoneDrawButton = new JButton();
        fishBoneDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sakananohone.png")));
        panel11.add(fishBoneDrawButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setParallelDrawWidthButton = new JButton();
        setParallelDrawWidthButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen_haba_sitei.png")));
        panel11.add(setParallelDrawWidthButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doubleSymmetricDrawButton = new JButton();
        doubleSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/fuku_orikaesi.png")));
        panel11.add(doubleSymmetricDrawButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        makeFlatFoldableButton = new JButton();
        makeFlatFoldableButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen.png")));
        panel11.add(makeFlatFoldableButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        continuousSymmetricDrawButton = new JButton();
        continuousSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/renzoku_orikaesi.png")));
        panel11.add(continuousSymmetricDrawButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        symmetricDrawButton = new JButton();
        symmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orikaesi.png")));
        panel11.add(symmetricDrawButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        angleBisectorButton = new JButton();
        angleBisectorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kaku_toubun.png")));
        angleBisectorButton.setText("");
        panel11.add(angleBisectorButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCrease2Button = new JButton();
        lengthenCrease2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou_2.png")));
        panel11.add(lengthenCrease2Button, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCreaseButton = new JButton();
        lengthenCreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou.png")));
        panel11.add(lengthenCreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseRestrictedButton = new JButton();
        drawCreaseRestrictedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku11.png")));
        panel11.add(drawCreaseRestrictedButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rabbitEarButton = new JButton();
        rabbitEarButton.setIcon(new ImageIcon(getClass().getResource("/ppp/naishin.png")));
        panel11.add(rabbitEarButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foldableLineDrawButton = new JButton();
        foldableLineDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen_and_kousitenkei_simple.png")));
        panel11.add(foldableLineDrawButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseFreeButton = new JButton();
        drawCreaseFreeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku.png")));
        panel11.add(drawCreaseFreeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), 1, 1, true, true));
        root.add(panel12, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectButton = new JButton();
        selectButton.setBackground(new Color(-16711936));
        selectButton.setText("sel");
        panel12.add(selectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectAllButton = new JButton();
        selectAllButton.setBackground(new Color(-16711936));
        selectAllButton.setText("s_al");
        panel12.add(selectAllButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectButton = new JButton();
        unselectButton.setBackground(new Color(-16711936));
        unselectButton.setText("unsel");
        panel12.add(unselectButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectAllButton = new JButton();
        unselectAllButton.setBackground(new Color(-16711936));
        unselectAllButton.setText("uns_al");
        panel12.add(unselectAllButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveButton = new JButton();
        moveButton.setBackground(new Color(-5579606));
        moveButton.setEnabled(true);
        moveButton.setText("move");
        panel12.add(moveButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        move2p2pButton = new JButton();
        move2p2pButton.setBackground(new Color(-5579606));
        move2p2pButton.setEnabled(true);
        move2p2pButton.setText("mv_4p");
        panel12.add(move2p2pButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setBackground(new Color(-5579606));
        copyButton.setEnabled(true);
        copyButton.setText("copy");
        panel12.add(copyButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copy2p2pButton = new JButton();
        copy2p2pButton.setBackground(new Color(-5579606));
        copy2p2pButton.setEnabled(true);
        copy2p2pButton.setText("cp_4p");
        panel12.add(copy2p2pButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        reflectButton = new JButton();
        reflectButton.setBackground(new Color(-5579606));
        reflectButton.setEnabled(true);
        reflectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kyouei.png")));
        panel12.add(reflectButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deleteSelectedLineSegmentButton = new JButton();
        deleteSelectedLineSegmentButton.setText("d_s_L");
        panel12.add(deleteSelectedLineSegmentButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel13, new GridConstraints(15, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeDecreaseButton = new JButton();
        gridSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei2.png")));
        panel13.add(gridSizeDecreaseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeTextField = new JTextField();
        gridSizeTextField.setColumns(2);
        panel13.add(gridSizeTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        gridSizeSetButton = new JButton();
        gridSizeSetButton.setText("S");
        panel13.add(gridSizeSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        gridSizeIncreaseButton = new JButton();
        gridSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei.png")));
        panel13.add(gridSizeIncreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridColorButton = new JButton();
        gridColorButton.setText("Color");
        panel13.add(gridColorButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        gridLineWidthDecreaseButton = new JButton();
        gridLineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_sage.png")));
        panel13.add(gridLineWidthDecreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridLineWidthIncreaseButton = new JButton();
        gridLineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_age.png")));
        panel13.add(gridLineWidthIncreaseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        changeGridStateButton = new JButton();
        changeGridStateButton.setIcon(new ImageIcon(getClass().getResource("/ppp/i_kitei_jyoutai.png")));
        panel13.add(changeGridStateButton, new GridConstraints(1, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveIntervalGridVerticalButton = new JButton();
        moveIntervalGridVerticalButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_tate_idou.png")));
        panel13.add(moveIntervalGridVerticalButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridSizeTextField = new JTextField();
        intervalGridSizeTextField.setColumns(2);
        intervalGridSizeTextField.setText("8");
        panel13.add(intervalGridSizeTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        setIntervalGridSizeButton = new JButton();
        setIntervalGridSizeButton.setText("S");
        panel13.add(setIntervalGridSizeButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        moveIntervalGridHorizontal = new JButton();
        moveIntervalGridHorizontal.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_yoko_idou.png")));
        panel13.add(moveIntervalGridHorizontal, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridColorButton = new JButton();
        intervalGridColorButton.setText("Color");
        panel13.add(intervalGridColorButton, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        gridXATextField = new JTextField();
        gridXATextField.setColumns(3);
        gridXATextField.setText("0.0");
        panel13.add(gridXATextField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        panel13.add(label1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXBTextField = new JTextField();
        gridXBTextField.setColumns(3);
        gridXBTextField.setText("1.0");
        panel13.add(gridXBTextField, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        panel13.add(label2, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXCTextField = new JTextField();
        gridXCTextField.setColumns(3);
        gridXCTextField.setText("1.0");
        panel13.add(gridXCTextField, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        gridYATextField = new JTextField();
        gridYATextField.setColumns(3);
        gridYATextField.setText("0.0");
        panel13.add(gridYATextField, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        gridYBTextField = new JTextField();
        gridYBTextField.setColumns(3);
        gridYBTextField.setText("1.0");
        panel13.add(gridYBTextField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        gridYCTextField = new JTextField();
        gridYCTextField.setColumns(3);
        gridYCTextField.setText("1.0");
        panel13.add(gridYCTextField, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        panel13.add(label3, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        panel13.add(label4, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridAngleTextField = new JTextField();
        gridAngleTextField.setColumns(4);
        gridAngleTextField.setHorizontalAlignment(11);
        gridAngleTextField.setText("90.0");
        panel13.add(gridAngleTextField, new GridConstraints(5, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        resetButton = new JButton();
        resetButton.setText("Reset");
        panel13.add(resetButton, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 0), new Dimension(30, -1), null, 0, false));
        setGridParametersButton = new JButton();
        setGridParametersButton.setText("S");
        panel13.add(setGridParametersButton, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel14, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineSegmentDeleteButton = new JButton();
        lineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_sakujyo.png")));
        panel14.add(lineSegmentDeleteButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        edgeLineSegmentDeleteButton = new JButton();
        edgeLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kuro_senbun_sakujyo.png")));
        panel14.add(edgeLineSegmentDeleteButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        auxLiveLineSegmentDeleteButton = new JButton();
        auxLiveLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun3_sakujyo.png")));
        panel14.add(auxLiveLineSegmentDeleteButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        trimBranchesButton = new JButton();
        trimBranchesButton.setIcon(new ImageIcon(getClass().getResource("/ppp/eda_kesi.png")));
        panel14.add(trimBranchesButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer5 = new Spacer();
        root.add(spacer5, new GridConstraints(20, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        root.add(spacer6, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    public void getData(ApplicationModel data) {
        data.setFoldLineDividingNumber(StringOp.String2int(lineSegmentDivisionTextField.getText(), data.getFoldLineDividingNumber()));
    }

    public void setData(PropertyChangeEvent e, ApplicationModel data) {
        lineSegmentDivisionTextField.setText(String.valueOf(data.getFoldLineDividingNumber()));

        gridLineWidthDecreaseButton.setEnabled(data.getGridLineWidth() != 1);

        gridColorButton.setIcon(new ColorIcon(data.getGridColor()));
        intervalGridColorButton.setIcon(new ColorIcon(data.getGridScaleColor()));

        if (e.getPropertyName() == null || e.getPropertyName().equals("laf")) {
            // The look and feel is not set yet, so it must be read from the applicationModel
            boolean isDark = data.determineLafDark();

            for (JButton button : Arrays.asList(colBlackButton, colRedButton, colBlueButton, colCyanButton, toMountainButton, toValleyButton, toAuxButton, toEdgeButton, senbun_henkan2Button)) {
                button.setForeground(Colors.restore(button.getForeground(), isDark));
                button.setBackground(Colors.restore(button.getBackground(), isDark));
            }
        }
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
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

        if (e.getPropertyName() == null || e.getPropertyName().equals("lineColor") || e.getPropertyName().equals("toggleLineColor")) {
            Color gray = Colors.get(new Color(150, 150, 150));

            colBlackButton.setBackground(gray);
            colRedButton.setBackground(gray);
            colBlueButton.setBackground(gray);
            colCyanButton.setBackground(gray);
            colBlackButton.setForeground(Colors.get(Color.black));
            colRedButton.setForeground(Colors.get(Color.black));
            colBlueButton.setForeground(Colors.get(Color.black));
            colCyanButton.setForeground(Colors.get(Color.black));

            switch (data.calculateLineColor()) {
                case BLACK_0:
                    colBlackButton.setBackground(Colors.get(Color.black));
                    colBlackButton.setForeground(Colors.get(Color.white));
                    break;
                case RED_1:
                    colRedButton.setBackground(Colors.get(Color.red));
                    colRedButton.setForeground(Colors.get(Color.black));
                    break;
                case BLUE_2:
                    colBlueButton.setBackground(Colors.get(Color.blue));
                    colBlueButton.setForeground(Colors.get(Color.black));
                    break;
                case CYAN_3:
                    colCyanButton.setBackground(Colors.get(Color.cyan));
                    colCyanButton.setForeground(Colors.get(Color.black));
            }
        }

        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
            toMountainButton.setBackground(null);
            toValleyButton.setBackground(null);
            toEdgeButton.setBackground(null);
            toAuxButton.setBackground(null);
            senbun_henkan2Button.setBackground(null);

            switch (data.getMouseMode()) {
                case CREASE_MAKE_MOUNTAIN_23:
                    toMountainButton.setBackground(Colors.get(Color.red));
                    break;
                case CREASE_MAKE_VALLEY_24:
                    toValleyButton.setBackground(Colors.get(Color.blue));
                    break;
                case CREASE_MAKE_EDGE_25:
                    toEdgeButton.setBackground(Colors.get(Color.black));
                    break;
                case CREASE_MAKE_AUX_60:
                    toAuxButton.setBackground(Colors.get(new Color(100, 200, 200)));
                    break;
                case CREASE_TOGGLE_MV_58:
                    senbun_henkan2Button.setBackground(Colors.get(new Color(138, 43, 226)));
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

    public void setData(FoldedFigureModel foldedFigureModel) {
        coloredXRayCheckBox.setSelected(foldedFigureModel.isTransparencyColor());
    }
}
