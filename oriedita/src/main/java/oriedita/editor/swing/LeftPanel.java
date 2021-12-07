package oriedita.editor.swing;

import com.formdev.flatlaf.FlatLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.tinylog.Logger;
import oriedita.editor.Colors;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FoldingService;
import oriedita.editor.service.HistoryState;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.UndoRedo;
import oriedita.editor.tools.StringOp;
import origami.crease_pattern.element.LineColor;
import origami.folding.FoldedFigure;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;

@Singleton
public class LeftPanel {
    private final MeasuresModel measuresModel;
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
    // private JButton inputDataButton;
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
    private JLabel gridXPlusLabel;
    private JLabel gridYPlusLabel;
    private JLabel gridXSqrtLabel;
    private JLabel gridYSqrtLabel;

    @Inject
    public LeftPanel(@Named("mainFrame") JFrame frame,
                     @Named("normal") HistoryState historyState,
                     MeasuresModel measuresModel,
                     ButtonService buttonService,
                     CreasePattern_Worker mainCreasePatternWorker,
                     ApplicationModel applicationModel,
                     FoldedFigureModel foldedFigureModel,
                     GridModel gridModel,
                     CanvasModel canvasModel,
                     FoldingService foldingService,
                     FoldedFiguresList foldedFiguresList) {
        this.measuresModel = measuresModel;

        applicationModel.addPropertyChangeListener(e -> setData(e, applicationModel));
        gridModel.addPropertyChangeListener(e -> setData(gridModel));
        foldedFigureModel.addPropertyChangeListener(e -> setData(foldedFigureModel));
        canvasModel.addPropertyChangeListener(e -> setData(e, canvasModel));
        historyState.addPropertyChangeListener(e -> setData(historyState));

        $$$setupUI$$$();

        setData(historyState);

        getData(gridModel);

        buttonService.registerButton(lineWidthDecreaseButton, "lineWidthDecreaseAction");
        buttonService.registerButton(lineWidthIncreaseButton, "lineWidthIncreaseAction");
        buttonService.registerButton(pointSizeDecreaseButton, "pointSizeDecreaseAction");
        buttonService.registerButton(pointSizeIncreaseButton, "pointSizeIncreaseAction");
        buttonService.registerButton(antiAliasToggleButton, "antiAliasToggleAction");
        buttonService.registerButton(lineStyleChangeButton, "lineStyleChangeAction");
        buttonService.registerButton(drawCreaseFreeButton, "drawCreaseFreeAction");
        buttonService.registerButton(drawCreaseRestrictedButton, "drawCreaseRestrictedAction");
        buttonService.registerButton(voronoiButton, "voronoiAction");
        buttonService.registerButton(makeFlatFoldableButton, "makeFlatFoldableAction");
        buttonService.registerButton(lengthenCreaseButton, "lengthenCreaseAction");
        buttonService.registerButton(lengthenCrease2Button, "lengthenCrease2Action");
        buttonService.registerButton(angleBisectorButton, "angleBisectorAction");
        buttonService.registerButton(rabbitEarButton, "rabbitEarAction");
        buttonService.registerButton(perpendicularDrawButton, "perpendicularDrawAction");
        buttonService.registerButton(symmetricDrawButton, "symmetricDrawAction");
        buttonService.registerButton(continuousSymmetricDrawButton, "continuousSymmetricDrawAction");
        buttonService.registerButton(parallelDrawButton, "parallelDrawAction");
        buttonService.registerButton(setParallelDrawWidthButton, "setParallelDrawWidthAction");
        buttonService.registerButton(foldableLineDrawButton, "foldableLineDrawAction");
        buttonService.registerButton(all_s_step_to_orisenButton, "all_s_step_to_orisenAction");
        buttonService.registerButton(fishBoneDrawButton, "fishBoneDrawAction");
        buttonService.registerButton(doubleSymmetricDrawButton, "doubleSymmetricDrawAction");
        buttonService.registerButton(senbun_b_nyuryokuButton, "senbun_b_nyuryokuAction");
        buttonService.registerButton(reflectButton, "reflectAction");
        buttonService.registerButton(selectButton, "selectAction");
        buttonService.registerButton(unselectButton, "unselectAction");
        buttonService.registerButton(selectAllButton, "selectAllAction");
        buttonService.registerButton(unselectAllButton, "unselectAllAction");
        buttonService.registerButton(moveButton, "moveAction");
        buttonService.registerButton(move2p2pButton, "move2p2pAction");
        buttonService.registerButton(copyButton, "copyAction");
        buttonService.registerButton(copy2p2pButton, "copy2p2pAction");
        buttonService.registerButton(deleteSelectedLineSegmentButton, "deleteSelectedLineSegmentAction");
        buttonService.registerButton(lineSegmentDeleteButton, "lineSegmentDeleteAction");
        buttonService.registerButton(edgeLineSegmentDeleteButton, "edgeLineSegmentDeleteAction");
        buttonService.registerButton(auxLiveLineSegmentDeleteButton, "auxLiveLineSegmentDeleteAction");
        buttonService.registerButton(trimBranchesButton, "trimBranchesAction");
        buttonService.registerButton(toMountainButton, "toMountainAction");
        buttonService.registerButton(toValleyButton, "toValleyAction");
        buttonService.registerButton(toEdgeButton, "toEdgeAction");
        buttonService.registerButton(toAuxButton, "toAuxAction");
        buttonService.registerButton(zen_yama_tani_henkanButton, "zen_yama_tani_henkanAction");
        buttonService.registerButton(senbun_henkan2Button, "senbun_henkan2Action");
        buttonService.registerButton(senbun_henkanButton, "senbun_henkanAction");
        buttonService.registerButton(in_L_col_changeButton, "in_L_col_changeAction");
        buttonService.registerButton(on_L_col_changeButton, "on_L_col_changeAction");
        buttonService.registerButton(v_addButton, "vertexAddAction");
        buttonService.registerButton(v_delButton, "vertexDeleteAction");
        buttonService.registerButton(v_del_ccButton, "v_del_ccAction");
        buttonService.registerButton(v_del_allButton, "v_del_allAction");
        buttonService.registerButton(v_del_all_ccButton, "v_del_all_ccAction");
        buttonService.registerButton(drawTwoColoredCpButton, "drawTwoColoredCpAction");
        buttonService.registerButton(suitei_01Button, "suitei_01Action");
        buttonService.registerButton(koteimen_siteiButton, "koteimen_siteiAction");
        buttonService.registerButton(suitei_02Button, "suitei_02Action");
        buttonService.registerButton(suitei_03Button, "suitei_03Action");
        buttonService.registerButton(coloredXRayDecreaseButton, "coloredXRayDecreaseAction");
        buttonService.registerButton(coloredXRayIncreaseButton, "coloredXRayIncreaseAction");
        buttonService.registerButton(colRedButton, "colRedAction");
        buttonService.registerButton(colBlueButton, "colBlueAction");
        buttonService.registerButton(colBlackButton, "colBlackAction");
        buttonService.registerButton(colCyanButton, "colCyanAction");
        buttonService.registerButton(lineSegmentDivisionSetButton, "lineSegmentDivisionSetAction");

        buttonService.registerButton(gridSizeDecreaseButton, "gridSizeDecreaseAction");
        buttonService.registerButton(gridSizeSetButton, "gridSizeSetAction");
        buttonService.registerButton(gridSizeIncreaseButton, "gridSizeIncreaseAction");
        buttonService.registerButton(gridColorButton, "gridColorAction");
        buttonService.registerButton(gridLineWidthDecreaseButton, "gridLineWidthDecreaseAction");
        buttonService.registerButton(gridLineWidthIncreaseButton, "gridLineWidthIncreaseAction");
        buttonService.registerButton(changeGridStateButton, "changeGridStateAction");
        buttonService.registerButton(moveIntervalGridVerticalButton, "moveIntervalGridVerticalAction");
        buttonService.registerButton(setIntervalGridSizeButton, "setIntervalGridSizeAction");
        buttonService.registerButton(moveIntervalGridHorizontal, "moveIntervalGridHorizontalAction");
        buttonService.registerButton(intervalGridColorButton, "intervalGridColorAction");
        buttonService.registerButton(setGridParametersButton, "setGridParametersAction");
        buttonService.registerButton(resetButton, "gridConfigureResetAction");

        buttonService.registerButton(undoRedo.getRedoButton(), "redoAction");
        buttonService.registerButton(undoRedo.getUndoButton(), "undoAction");

        buttonService.registerButton(correctCpBeforeFoldingCheckBox, "correctCpBeforeFoldingAction");
        buttonService.registerButton(selectPersistentCheckBox, "selectPersistentAction");
        buttonService.registerButton(coloredXRayCheckBox, "coloredXRayAction");

        buttonService.registerLabel(gridXPlusLabel, "labelPlus");
        buttonService.registerLabel(gridYPlusLabel, "labelPlus");
        buttonService.registerLabel(gridXSqrtLabel, "labelSqrt");
        buttonService.registerLabel(gridYSqrtLabel, "labelSqrt");


        undoRedo.addUndoActionListener(e -> {
            mainCreasePatternWorker.undo();
        });
        undoRedo.addRedoActionListener(e -> {
            mainCreasePatternWorker.redo();
        });
        lineWidthDecreaseButton.addActionListener(e -> applicationModel.decreaseLineWidth());
        lineWidthIncreaseButton.addActionListener(e -> applicationModel.increaseLineWidth());
        pointSizeDecreaseButton.addActionListener(e -> applicationModel.decreasePointSize());
        pointSizeIncreaseButton.addActionListener(e -> applicationModel.increasePointSize());
        antiAliasToggleButton.addActionListener(e -> applicationModel.toggleAntiAlias());
        lineStyleChangeButton.addActionListener(e -> applicationModel.advanceLineStyle());
        colRedButton.addActionListener(e -> canvasModel.setLineColor(LineColor.RED_1));
        colBlueButton.addActionListener(e -> canvasModel.setLineColor(LineColor.BLUE_2));
        colBlackButton.addActionListener(e -> canvasModel.setLineColor(LineColor.BLACK_0));
        colCyanButton.addActionListener(e -> canvasModel.setLineColor(LineColor.CYAN_3));
        drawCreaseFreeButton.addActionListener(e -> {
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            canvasModel.setMouseMode(MouseMode.DRAW_CREASE_FREE_1);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

            mainCreasePatternWorker.unselect_all();
        });
        drawCreaseRestrictedButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DRAW_CREASE_RESTRICTED_11);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_RESTRICTED_11);

            mainCreasePatternWorker.unselect_all();
        });
        voronoiButton.addActionListener(e -> {
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            canvasModel.setMouseMode(MouseMode.VORONOI_CREATE_62);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.VORONOI_CREATE_62);

            mainCreasePatternWorker.unselect_all();
        });
        makeFlatFoldableButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);

            mainCreasePatternWorker.unselect_all();
        });
        lengthenCreaseButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_5);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_5);

            mainCreasePatternWorker.unselect_all();
        });
        lengthenCrease2Button.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);

            mainCreasePatternWorker.unselect_all();
        });
        angleBisectorButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.SQUARE_BISECTOR_7);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.SQUARE_BISECTOR_7);

            mainCreasePatternWorker.unselect_all();
        });
        rabbitEarButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.INWARD_8);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.INWARD_8);

            mainCreasePatternWorker.unselect_all();
        });
        perpendicularDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.PERPENDICULAR_DRAW_9);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PERPENDICULAR_DRAW_9);

            mainCreasePatternWorker.unselect_all();
        });
        symmetricDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.SYMMETRIC_DRAW_10);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.SYMMETRIC_DRAW_10);

            mainCreasePatternWorker.unselect_all();
        });
        continuousSymmetricDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);

            mainCreasePatternWorker.unselect_all();
        });
        parallelDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_40);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_40);

            mainCreasePatternWorker.unselect_all();
        });
        setParallelDrawWidthButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_WIDTH_51);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_WIDTH_51);

            mainCreasePatternWorker.unselect_all();
        });
        foldableLineDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.FOLDABLE_LINE_DRAW_71);

            mainCreasePatternWorker.unselect_all();
        });
        all_s_step_to_orisenButton.addActionListener(e -> {
            Logger.info("i_egaki_dankai = " + mainCreasePatternWorker.getDrawingStage());
            Logger.info("i_kouho_dankai = " + mainCreasePatternWorker.getCandidateSize());

            mainCreasePatternWorker.all_s_step_to_orisen();
        });
        fishBoneDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.FISH_BONE_DRAW_33);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.FISH_BONE_DRAW_33);

            mainCreasePatternWorker.unselect_all();
            buttonService.Button_shared_operation();
        });
        doubleSymmetricDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);

            mainCreasePatternWorker.unselect_all();
            buttonService.Button_shared_operation();
        });
        lineSegmentDivisionSetButton.addActionListener(e -> {
            getData(applicationModel);

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);
        });
        lineSegmentDivisionTextField.addActionListener(e -> lineSegmentDivisionSetButton.doClick());
        senbun_b_nyuryokuButton.addActionListener(e -> {
            getData(applicationModel);

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            mainCreasePatternWorker.unselect_all();
        });
        selectButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19);
        });
        selectAllButton.addActionListener(e -> {
            mainCreasePatternWorker.select_all();
        });
        unselectButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_UNSELECT_20);
        });
        unselectAllButton.addActionListener(e -> {
            mainCreasePatternWorker.unselect_all();
        });
        moveButton.addActionListener(e -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MOVE_1);
            canvasModel.setMouseMode(MouseMode.CREASE_MOVE_21);
        });
        move2p2pButton.addActionListener(e -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MOVE4P_2);
            canvasModel.setMouseMode(MouseMode.CREASE_MOVE_4P_31);
        });
        copyButton.addActionListener(e -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.COPY_3);
            canvasModel.setMouseMode(MouseMode.CREASE_COPY_22);
        });
        copy2p2pButton.addActionListener(e -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.COPY4P_4);
            canvasModel.setMouseMode(MouseMode.CREASE_COPY_4P_32);
        });
        reflectButton.addActionListener(e -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MIRROR_5);
            canvasModel.setMouseMode(MouseMode.DRAW_CREASE_SYMMETRIC_12);
        });
        deleteSelectedLineSegmentButton.addActionListener(e -> {
            mainCreasePatternWorker.del_selected_senbun();
            mainCreasePatternWorker.record();
        });
        lineSegmentDeleteButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);

            mainCreasePatternWorker.unselect_all();
        });
        edgeLineSegmentDeleteButton.addActionListener(e -> {

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.BLACK_LINE_2);

            mainCreasePatternWorker.unselect_all();
        });
        auxLiveLineSegmentDeleteButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.AUX_LIVE_LINE_3);

            mainCreasePatternWorker.unselect_all();
        });
        trimBranchesButton.addActionListener(e -> {
            mainCreasePatternWorker.point_removal();
            mainCreasePatternWorker.overlapping_line_removal();
            mainCreasePatternWorker.branch_trim();
            mainCreasePatternWorker.organizeCircles();
            mainCreasePatternWorker.record();
            mainCreasePatternWorker.unselect_all();
        });
        toMountainButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MOUNTAIN_23);

            mainCreasePatternWorker.unselect_all();
        });
        toValleyButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_VALLEY_24);

            mainCreasePatternWorker.unselect_all();
        });
        toEdgeButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_EDGE_25);

            mainCreasePatternWorker.unselect_all();
        });
        toAuxButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_AUX_60);

            mainCreasePatternWorker.unselect_all();
        });
        zen_yama_tani_henkanButton.addActionListener(e -> {
            mainCreasePatternWorker.allMountainValleyChange();
            mainCreasePatternWorker.unselect_all();
        });
        senbun_henkan2Button.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_TOGGLE_MV_58);

            mainCreasePatternWorker.unselect_all();
        });
        senbun_henkanButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CHANGE_CREASE_TYPE_4);

            mainCreasePatternWorker.unselect_all();
        });
        in_L_col_changeButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MV_34);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_MAKE_MV_34);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.RED_1);
            }

            mainCreasePatternWorker.unselect_all();
        });
        on_L_col_changeButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASES_ALTERNATE_MV_36);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASES_ALTERNATE_MV_36);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.BLUE_2);
            }

            mainCreasePatternWorker.unselect_all();
        });
        v_addButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DRAW_POINT_14);

            mainCreasePatternWorker.unselect_all();
        });
        v_delButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DELETE_POINT_15);

            mainCreasePatternWorker.unselect_all();
        });
        v_del_ccButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.VERTEX_DELETE_ON_CREASE_41);

            mainCreasePatternWorker.unselect_all();
        });
        v_del_allButton.addActionListener(e -> {
            mainCreasePatternWorker.v_del_all();
            Logger.info("mainDrawingWorker.v_del_all()");
        });
        v_del_all_ccButton.addActionListener(e -> {
            mainCreasePatternWorker.v_del_all_cc();
            Logger.info("mainDrawingWorker.v_del_all_cc()");
        });
        correctCpBeforeFoldingCheckBox.addActionListener(e -> {
            applicationModel.setCorrectCpBeforeFolding(correctCpBeforeFoldingCheckBox.isSelected());
        });
        selectPersistentCheckBox.addActionListener(e -> {
            applicationModel.setSelectPersistent(selectPersistentCheckBox.isSelected());
        });
        drawTwoColoredCpButton.addActionListener(e -> {
            foldingService.createTwoColoredCp();
        });
        suitei_01Button.addActionListener(e -> {
            foldingService.fold(FoldedFigure.EstimationOrder.ORDER_1);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!applicationModel.getSelectPersistent()) {
                mainCreasePatternWorker.unselect_all();
            }
        });
        koteimen_siteiButton.addActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

            if (selectedFigure != null && selectedFigure.foldedFigure.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                canvasModel.setMouseMode(MouseMode.CHANGE_STANDARD_FACE_103);
            }
        });
        suitei_02Button.addActionListener(e -> {
            foldingService.fold(FoldedFigure.EstimationOrder.ORDER_2);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!applicationModel.getSelectPersistent()) {
                mainCreasePatternWorker.unselect_all();
            }
        });
        suitei_03Button.addActionListener(e -> {
            foldingService.fold(FoldedFigure.EstimationOrder.ORDER_3);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (!applicationModel.getSelectPersistent()) {
                mainCreasePatternWorker.unselect_all();
            }
        });
        coloredXRayCheckBox.addActionListener(e -> foldedFigureModel.setTransparencyColor(coloredXRayCheckBox.isSelected()));
        coloredXRayDecreaseButton.addActionListener(e -> foldedFigureModel.decreaseTransparency());
        coloredXRayIncreaseButton.addActionListener(e -> foldedFigureModel.increaseTransparency());

        gridSizeDecreaseButton.addActionListener(e -> {
            int gridSize = gridModel.getGridSize();

            gridSize = gridSize / 2;
            if (gridSize < 1) {
                gridSize = 1;
            }

            gridModel.setGridSize(gridSize);
        });
        gridSizeSetButton.addActionListener(e -> getData(gridModel));
        gridSizeTextField.addActionListener(e -> gridSizeSetButton.doClick());
        gridSizeIncreaseButton.addActionListener(e -> gridModel.setGridSize(gridModel.getGridSize() * 2));
        gridColorButton.addActionListener(e -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(frame, "Col", FlatLaf.isLafDark() ? Colors.GRID_LINE_DARK : Colors.GRID_LINE);
            if (color != null) {
                applicationModel.setGridColor(color);
            }
            //以上でやりたいことは書き終わり
        });
        gridLineWidthDecreaseButton.addActionListener(e -> applicationModel.decreaseGridLineWidth());
        gridLineWidthIncreaseButton.addActionListener(e -> applicationModel.increaseGridLineWidth());
        changeGridStateButton.addActionListener(e -> gridModel.advanceBaseState());
        moveIntervalGridVerticalButton.addActionListener(e -> gridModel.changeHorizontalScalePosition());
        setIntervalGridSizeButton.addActionListener(e -> getData(gridModel));
        intervalGridSizeTextField.addActionListener(e -> setIntervalGridSizeButton.doClick());
        moveIntervalGridHorizontal.addActionListener(e -> gridModel.changeVerticalScalePosition());
        intervalGridColorButton.addActionListener(e -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(frame, "Col", FlatLaf.isLafDark() ? Colors.GRID_SCALE_DARK : Colors.GRID_SCALE);
            if (color != null) {
                applicationModel.setGridScaleColor(color);
            }
        });
        setGridParametersButton.addActionListener(e -> getData(gridModel));
        gridAngleTextField.addActionListener(e -> setGridParametersButton.doClick());
        resetButton.addActionListener(e -> gridModel.reset());
    }

    private void setData(HistoryState historyState) {
        undoRedo.getUndoButton().setEnabled(historyState.canUndo());
        undoRedo.getRedoButton().setEnabled(historyState.canRedo());
    }


    public void getData(GridModel data) {
        data.setIntervalGridSize(StringOp.String2int(intervalGridSizeTextField.getText(), data.getIntervalGridSize()));
        data.setGridSize(StringOp.String2int(gridSizeTextField.getText(), data.getGridSize()));
        data.setGridXA(measuresModel.string2double(gridXATextField.getText(), data.getGridXA()));
        data.setGridXB(measuresModel.string2double(gridXBTextField.getText(), data.getGridXB()));
        data.setGridXC(measuresModel.string2double(gridXCTextField.getText(), data.getGridXC()));
        data.setGridYA(measuresModel.string2double(gridYATextField.getText(), data.getGridYA()));
        data.setGridYB(measuresModel.string2double(gridYBTextField.getText(), data.getGridYB()));
        data.setGridYC(measuresModel.string2double(gridYCTextField.getText(), data.getGridYC()));
        data.setGridAngle(measuresModel.string2double(gridAngleTextField.getText(), data.getGridAngle()));
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
        root.setLayout(new GridLayoutManager(22, 1, new Insets(1, 1, 1, 1), 1, 1));
        undoRedo = new UndoRedo();
        root.add(undoRedo.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), 1, 1, false, true));
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
        antiAliasToggleButton.setText("a_a");
        panel1.add(antiAliasToggleButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        toMountainButton.setForeground(new Color(-65536));
        toMountainButton.setIcon(new ImageIcon(getClass().getResource("/ppp/M_nisuru.png")));
        panel3.add(toMountainButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toValleyButton = new JButton();
        toValleyButton.setForeground(new Color(-16776961));
        toValleyButton.setIcon(new ImageIcon(getClass().getResource("/ppp/V_nisuru.png")));
        panel3.add(toValleyButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toEdgeButton = new JButton();
        toEdgeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/E_nisuru.png")));
        panel3.add(toEdgeButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toAuxButton = new JButton();
        toAuxButton.setForeground(new Color(-10172216));
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
        panel6.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
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
        v_del_allButton = new JButton();
        v_del_allButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all.png")));
        panel6.add(v_del_allButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_all_ccButton = new JButton();
        v_del_all_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all_cc.png")));
        panel6.add(v_del_all_ccButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel7, new GridConstraints(18, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        correctCpBeforeFoldingCheckBox = new JCheckBox();
        correctCpBeforeFoldingCheckBox.setText("Correct");
        panel7.add(correctCpBeforeFoldingCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectPersistentCheckBox = new JCheckBox();
        selectPersistentCheckBox.setText("Persist");
        selectPersistentCheckBox.setToolTipText("ckbox_select_nokosi");
        panel7.add(selectPersistentCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawTwoColoredCpButton = new JButton();
        drawTwoColoredCpButton.setIcon(new ImageIcon(getClass().getResource("/ppp/2syoku_tenkaizu.png")));
        panel7.add(drawTwoColoredCpButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel8, new GridConstraints(19, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_01Button = new JButton();
        suitei_01Button.setText("CP_rcg");
        panel8.add(suitei_01Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        koteimen_siteiButton = new JButton();
        koteimen_siteiButton.setText("S_face");
        panel8.add(koteimen_siteiButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel9, new GridConstraints(20, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_02Button = new JButton();
        suitei_02Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_02.png")));
        panel9.add(suitei_02Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_03Button = new JButton();
        suitei_03Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_03.png")));
        panel9.add(suitei_03Button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayCheckBox = new JCheckBox();
        coloredXRayCheckBox.setText("Colored");
        panel9.add(coloredXRayCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayDecreaseButton = new JButton();
        coloredXRayDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        panel9.add(coloredXRayDecreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayIncreaseButton = new JButton();
        coloredXRayIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        panel9.add(coloredXRayIncreaseButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel10, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        perpendicularDrawButton = new JButton();
        perpendicularDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suisen.png")));
        panel10.add(perpendicularDrawButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineSegmentDivisionTextField = new JTextField();
        lineSegmentDivisionTextField.setColumns(1);
        lineSegmentDivisionTextField.setText("2");
        panel10.add(lineSegmentDivisionTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), null, null, 0, false));
        lineSegmentDivisionSetButton = new JButton();
        lineSegmentDivisionSetButton.setText("Set");
        panel10.add(lineSegmentDivisionSetButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        senbun_b_nyuryokuButton = new JButton();
        senbun_b_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_b_nyuryoku.png")));
        panel10.add(senbun_b_nyuryokuButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        all_s_step_to_orisenButton = new JButton();
        all_s_step_to_orisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/all_s_step_to_orisen.png")));
        panel10.add(all_s_step_to_orisenButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        voronoiButton = new JButton();
        voronoiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Voronoi.png")));
        panel10.add(voronoiButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parallelDrawButton = new JButton();
        parallelDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen.png")));
        panel10.add(parallelDrawButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fishBoneDrawButton = new JButton();
        fishBoneDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sakananohone.png")));
        panel10.add(fishBoneDrawButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setParallelDrawWidthButton = new JButton();
        setParallelDrawWidthButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen_haba_sitei.png")));
        panel10.add(setParallelDrawWidthButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doubleSymmetricDrawButton = new JButton();
        doubleSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/fuku_orikaesi.png")));
        panel10.add(doubleSymmetricDrawButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        makeFlatFoldableButton = new JButton();
        makeFlatFoldableButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen.png")));
        panel10.add(makeFlatFoldableButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        continuousSymmetricDrawButton = new JButton();
        continuousSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/renzoku_orikaesi.png")));
        panel10.add(continuousSymmetricDrawButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        symmetricDrawButton = new JButton();
        symmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orikaesi.png")));
        panel10.add(symmetricDrawButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        angleBisectorButton = new JButton();
        angleBisectorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kaku_toubun.png")));
        angleBisectorButton.setText("");
        panel10.add(angleBisectorButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCrease2Button = new JButton();
        lengthenCrease2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou_2.png")));
        panel10.add(lengthenCrease2Button, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCreaseButton = new JButton();
        lengthenCreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou.png")));
        panel10.add(lengthenCreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseRestrictedButton = new JButton();
        drawCreaseRestrictedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku11.png")));
        panel10.add(drawCreaseRestrictedButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rabbitEarButton = new JButton();
        rabbitEarButton.setIcon(new ImageIcon(getClass().getResource("/ppp/naishin.png")));
        panel10.add(rabbitEarButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foldableLineDrawButton = new JButton();
        foldableLineDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen_and_kousitenkei_simple.png")));
        panel10.add(foldableLineDrawButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseFreeButton = new JButton();
        drawCreaseFreeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku.png")));
        panel10.add(drawCreaseFreeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel11, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectButton = new JButton();
        selectButton.setText("sel");
        panel11.add(selectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectAllButton = new JButton();
        selectAllButton.setText("s_al");
        panel11.add(selectAllButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveButton = new JButton();
        moveButton.setText("move");
        panel11.add(moveButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        move2p2pButton = new JButton();
        move2p2pButton.setText("mv_4p");
        panel11.add(move2p2pButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        reflectButton = new JButton();
        reflectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kyouei.png")));
        panel11.add(reflectButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectButton = new JButton();
        unselectButton.setText("unsel");
        panel11.add(unselectButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectAllButton = new JButton();
        unselectAllButton.setText("uns_al");
        panel11.add(unselectAllButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setText("copy");
        panel11.add(copyButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copy2p2pButton = new JButton();
        copy2p2pButton.setText("cp_4p");
        panel11.add(copy2p2pButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deleteSelectedLineSegmentButton = new JButton();
        deleteSelectedLineSegmentButton.setText("d_s_L");
        panel11.add(deleteSelectedLineSegmentButton, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel12, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeDecreaseButton = new JButton();
        gridSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei2.png")));
        panel12.add(gridSizeDecreaseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeTextField = new JTextField();
        gridSizeTextField.setColumns(2);
        panel12.add(gridSizeTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridSizeSetButton = new JButton();
        gridSizeSetButton.setText("S");
        panel12.add(gridSizeSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeIncreaseButton = new JButton();
        gridSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei.png")));
        panel12.add(gridSizeIncreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridColorButton = new JButton();
        gridColorButton.setText("Color");
        panel12.add(gridColorButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridLineWidthDecreaseButton = new JButton();
        gridLineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_sage.png")));
        panel12.add(gridLineWidthDecreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridLineWidthIncreaseButton = new JButton();
        gridLineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_age.png")));
        panel12.add(gridLineWidthIncreaseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        changeGridStateButton = new JButton();
        changeGridStateButton.setIcon(new ImageIcon(getClass().getResource("/ppp/i_kitei_jyoutai.png")));
        panel12.add(changeGridStateButton, new GridConstraints(1, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveIntervalGridVerticalButton = new JButton();
        moveIntervalGridVerticalButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_tate_idou.png")));
        panel12.add(moveIntervalGridVerticalButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridSizeTextField = new JTextField();
        intervalGridSizeTextField.setColumns(2);
        intervalGridSizeTextField.setText("8");
        panel12.add(intervalGridSizeTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        setIntervalGridSizeButton = new JButton();
        setIntervalGridSizeButton.setText("S");
        panel12.add(setIntervalGridSizeButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveIntervalGridHorizontal = new JButton();
        moveIntervalGridHorizontal.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_yoko_idou.png")));
        panel12.add(moveIntervalGridHorizontal, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridColorButton = new JButton();
        intervalGridColorButton.setText("Color");
        panel12.add(intervalGridColorButton, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel13, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineSegmentDeleteButton = new JButton();
        lineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_sakujyo.png")));
        panel13.add(lineSegmentDeleteButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        edgeLineSegmentDeleteButton = new JButton();
        edgeLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kuro_senbun_sakujyo.png")));
        panel13.add(edgeLineSegmentDeleteButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        auxLiveLineSegmentDeleteButton = new JButton();
        auxLiveLineSegmentDeleteButton.setForeground(new Color(-10172216));
        auxLiveLineSegmentDeleteButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun3_sakujyo.png")));
        panel13.add(auxLiveLineSegmentDeleteButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        trimBranchesButton = new JButton();
        trimBranchesButton.setIcon(new ImageIcon(getClass().getResource("/ppp/eda_kesi.png")));
        panel13.add(trimBranchesButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer5 = new Spacer();
        root.add(spacer5, new GridConstraints(21, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        root.add(spacer6, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel14, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridAngleTextField = new JTextField();
        gridAngleTextField.setColumns(3);
        gridAngleTextField.setHorizontalAlignment(11);
        gridAngleTextField.setText("90.0");
        panel14.add(gridAngleTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        setGridParametersButton = new JButton();
        setGridParametersButton.setText("S");
        panel14.add(setGridParametersButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        resetButton = new JButton();
        resetButton.setText("Reset");
        panel14.add(resetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel15, new GridConstraints(15, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXATextField = new JTextField();
        gridXATextField.setColumns(3);
        gridXATextField.setText("0.0");
        panel15.add(gridXATextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridXPlusLabel = new JLabel();
        gridXPlusLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        panel15.add(gridXPlusLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXBTextField = new JTextField();
        gridXBTextField.setColumns(3);
        gridXBTextField.setText("1.0");
        panel15.add(gridXBTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridXSqrtLabel = new JLabel();
        gridXSqrtLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        panel15.add(gridXSqrtLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXCTextField = new JTextField();
        gridXCTextField.setColumns(3);
        gridXCTextField.setText("1.0");
        panel15.add(gridXCTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridYATextField = new JTextField();
        gridYATextField.setColumns(3);
        gridYATextField.setText("0.0");
        panel15.add(gridYATextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridYPlusLabel = new JLabel();
        gridYPlusLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        panel15.add(gridYPlusLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridYBTextField = new JTextField();
        gridYBTextField.setColumns(3);
        gridYBTextField.setText("1.0");
        panel15.add(gridYBTextField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridYSqrtLabel = new JLabel();
        gridYSqrtLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        panel15.add(gridYSqrtLabel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridYCTextField = new JTextField();
        gridYCTextField.setColumns(3);
        gridYCTextField.setText("1.0");
        panel15.add(gridYCTextField, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
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
                default:
                    break;
            }
        }

        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode") || e.getPropertyName().equals("foldLineAdditionalInputMode")) {
            Color buttonBg = UIManager.getColor("Button.background");
            Color buttonFg = UIManager.getColor("Button.foreground");
            toMountainButton.setBackground(buttonBg);
            toMountainButton.setForeground(Colors.get(Color.red));
            toValleyButton.setBackground(buttonBg);
            toValleyButton.setForeground(Colors.get(Color.blue));
            toEdgeButton.setBackground(buttonBg);
            toEdgeButton.setForeground(Colors.get(Color.black));
            toAuxButton.setBackground(buttonBg);
            toAuxButton.setForeground(Colors.get(new Color(100, 200, 200)));
            senbun_henkan2Button.setBackground(buttonBg);
            senbun_henkan2Button.setForeground(buttonFg);
            auxLiveLineSegmentDeleteButton.setBackground(buttonBg);
            auxLiveLineSegmentDeleteButton.setForeground(Colors.get(new Color(100, 200, 200)));

            switch (data.getMouseMode()) {
                case CREASE_MAKE_MOUNTAIN_23:
                    toMountainButton.setBackground(Colors.get(Color.red));
                    toMountainButton.setForeground(Colors.get(Color.white));
                    break;
                case CREASE_MAKE_VALLEY_24:
                    toValleyButton.setBackground(Colors.get(Color.blue));
                    toValleyButton.setForeground(Colors.get(Color.white));
                    break;
                case CREASE_MAKE_EDGE_25:
                    toEdgeButton.setBackground(Colors.get(Color.black));
                    toEdgeButton.setForeground(Colors.get(Color.white));
                    break;
                case CREASE_MAKE_AUX_60:
                    toAuxButton.setBackground(Colors.get(new Color(100, 200, 200)));
                    toAuxButton.setForeground(Colors.get(Color.white));
                    break;
                case CREASE_TOGGLE_MV_58:
                    senbun_henkan2Button.setBackground(Colors.get(new Color(138, 43, 226)));
                    senbun_henkan2Button.setForeground(Colors.get(Color.white));
                    break;
                case LINE_SEGMENT_DELETE_3:
                    if (data.getFoldLineAdditionalInputMode() == FoldLineAdditionalInputMode.AUX_LIVE_LINE_3) {
                        auxLiveLineSegmentDeleteButton.setBackground(Colors.get(new Color(100, 200, 200)));
                        auxLiveLineSegmentDeleteButton.setForeground(Colors.get(Color.white));
                    }
                default:
                    break;
            }
        }

        if (e.getPropertyName() == null || e.getPropertyName().equals("selectionOperationMode")) {
            Border defaultBorder = (Border) UIManager.get("Button.border");
            moveButton.setBorder(defaultBorder);
            move2p2pButton.setBorder(defaultBorder);
            copyButton.setBorder(defaultBorder);
            copy2p2pButton.setBorder(defaultBorder);
            reflectButton.setBorder(defaultBorder);

            switch (data.getSelectionOperationMode()) {
                case MOVE_1:
                    moveButton.setBorder(new LineBorder(Color.green));
                    break;
                case MOVE4P_2:
                    move2p2pButton.setBorder(new LineBorder(Color.green));
                    break;
                case COPY_3:
                    copyButton.setBorder(new LineBorder(Color.green));
                    break;
                case COPY4P_4:
                    copy2p2pButton.setBorder(new LineBorder(Color.green));
                    break;
                case MIRROR_5:
                    reflectButton.setBorder(new LineBorder(Color.green));
                    break;
                default:
                    break;
            }
        }
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        coloredXRayCheckBox.setSelected(foldedFigureModel.isTransparencyColor());
    }
}
