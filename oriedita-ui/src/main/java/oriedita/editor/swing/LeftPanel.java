package oriedita.editor.swing;

import com.formdev.flatlaf.FlatLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.Canvas;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.action.ActionHandler;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.DrawCreaseFreeAction;
import oriedita.editor.action.LineWidthDecreaseAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FoldingService;
import oriedita.editor.service.HistoryState;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.UndoRedo;
import oriedita.editor.tools.LookAndFeelUtil;
import oriedita.editor.tools.StringOp;
import origami.crease_pattern.element.LineColor;
import origami.folding.FoldedFigure;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;

@ApplicationScoped
public class LeftPanel {
    @Inject
    @ActionHandler(ActionType.lineWidthDecreaseAction)
    LineWidthDecreaseAction lineWidthDecreaseAction;
    private final FrameProvider frameProvider;
    private final HistoryState historyState;
    private final MeasuresModel measuresModel;
    private final ButtonService buttonService;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final ApplicationModel applicationModel;
    private final FoldedFigureModel foldedFigureModel;
    private final GridModel gridModel;
    private final CanvasModel canvasModel;
    private final DrawCreaseFreeAction drawCreaseFreeAction;
    private final FoldingService foldingService;
    private final FoldedFiguresList foldedFiguresList;
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
    private JButton trimBranchesButton;
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
    private JButton resetGridButton;
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
    private JCheckBox drawDiagonalGridlinesCheckBox;
    private JButton del_l_typeButton;
    private JComboBox<String> delTypeDropBox;
    private JButton replace_lineButton;
    private JComboBox<String> fromLineDropBox;
    private JComboBox<String> toLineDropBox;
    private JLabel replaceLabel;

    private HashMap<MouseMode, JButton> selectionTransformationToolLookup;

    @Inject
    public LeftPanel(FrameProvider frameProvider,
                     @Named("normal") HistoryState historyState,
                     MeasuresModel measuresModel,
                     ButtonService buttonService,
                     @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                     ApplicationModel applicationModel,
                     FoldedFigureModel foldedFigureModel,
                     GridModel gridModel,
                     CanvasModel canvasModel,
                     @ActionHandler(ActionType.drawCreaseFreeAction) DrawCreaseFreeAction drawCreaseFreeAction,
                     FoldingService foldingService,
                     FoldedFiguresList foldedFiguresList) {
        this.frameProvider = frameProvider;
        this.historyState = historyState;
        this.measuresModel = measuresModel;
        this.buttonService = buttonService;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.applicationModel = applicationModel;
        this.foldedFigureModel = foldedFigureModel;
        this.gridModel = gridModel;
        this.canvasModel = canvasModel;
        this.drawCreaseFreeAction = drawCreaseFreeAction;
        this.foldingService = foldingService;
        this.foldedFiguresList = foldedFiguresList;

        this.selectionTransformationToolLookup = new HashMap<MouseMode, JButton>();

        this.selectionTransformationToolLookup.put(null, deleteSelectedLineSegmentButton);
        this.selectionTransformationToolLookup.put(MouseMode.DRAW_CREASE_SYMMETRIC_12, reflectButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_MOVE_21, moveButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_COPY_22, copyButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_MOVE_4P_31, move2p2pButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_COPY_4P_32, copy2p2pButton);
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());

        applicationModel.addPropertyChangeListener(e -> setData(e, applicationModel));
        gridModel.addPropertyChangeListener(e -> setData(gridModel));
        foldedFigureModel.addPropertyChangeListener(e -> setData(foldedFigureModel));
        canvasModel.addPropertyChangeListener(e -> setData(e, canvasModel));
        mainCreasePatternWorker.addPropertyChangeListener(e -> setData(e, mainCreasePatternWorker));
        historyState.addPropertyChangeListener(e -> setData(historyState));

        setData(historyState);

        getData(gridModel);

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
        buttonService.registerButton(del_l_typeButton, "del_l_typeButton");
        buttonService.registerButton(trimBranchesButton, "trimBranchesAction");
        buttonService.registerButton(replace_lineButton, "replace_lineButton");
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
        buttonService.registerButton(resetGridButton, "gridConfigureResetAction");

        buttonService.registerButton(undoRedo.getRedoButton(), "redoAction");
        buttonService.registerButton(undoRedo.getUndoButton(), "undoAction");

        buttonService.registerButton(correctCpBeforeFoldingCheckBox, "correctCpBeforeFoldingAction");
        buttonService.registerButton(selectPersistentCheckBox, "selectPersistentAction");
        buttonService.registerButton(coloredXRayCheckBox, "coloredXRayAction");
        buttonService.registerButton(drawDiagonalGridlinesCheckBox, "drawDiagonalGridlinesAction");

        buttonService.registerLabel(gridXPlusLabel, "labelPlus");
        buttonService.registerLabel(gridYPlusLabel, "labelPlus");
        buttonService.registerLabel(gridXSqrtLabel, "labelSqrt");
        buttonService.registerLabel(gridYSqrtLabel, "labelSqrt");
        buttonService.registerLabel(replaceLabel, "labelReplace");


        undoRedo.addUndoActionListener(e -> mainCreasePatternWorker.undo());
        undoRedo.addRedoActionListener(e -> mainCreasePatternWorker.redo());
        colRedButton.addActionListener(e -> canvasModel.setLineColor(LineColor.RED_1));
        colBlueButton.addActionListener(e -> canvasModel.setLineColor(LineColor.BLUE_2));
        colBlackButton.addActionListener(e -> canvasModel.setLineColor(LineColor.BLACK_0));
        colCyanButton.addActionListener(e -> canvasModel.setLineColor(LineColor.CYAN_3));
//        drawCreaseFreeButton.setAction(drawCreaseFreeAction);
        lengthenCreaseButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_5);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_5);

            mainCreasePatternWorker.unselect_all(false);
        });
        lengthenCrease2Button.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);

            mainCreasePatternWorker.unselect_all(false);
        });
        rabbitEarButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.INWARD_8);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.INWARD_8);

            mainCreasePatternWorker.unselect_all(false);
        });
        perpendicularDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.PERPENDICULAR_DRAW_9);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PERPENDICULAR_DRAW_9);

            mainCreasePatternWorker.unselect_all(false);
        });
        symmetricDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.SYMMETRIC_DRAW_10);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.SYMMETRIC_DRAW_10);

            mainCreasePatternWorker.unselect_all(false);
        });
        continuousSymmetricDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);

            mainCreasePatternWorker.unselect_all(false);
        });
        parallelDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_40);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_40);

            mainCreasePatternWorker.unselect_all(false);
        });
        setParallelDrawWidthButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_WIDTH_51);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_WIDTH_51);

            mainCreasePatternWorker.unselect_all(false);
        });
        foldableLineDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.FOLDABLE_LINE_DRAW_71);

            mainCreasePatternWorker.unselect_all(false);
        });
        fishBoneDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.FISH_BONE_DRAW_33);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.FISH_BONE_DRAW_33);

            mainCreasePatternWorker.unselect_all(false);
            buttonService.Button_shared_operation();
        });
        doubleSymmetricDrawButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);

            mainCreasePatternWorker.unselect_all(false);
            buttonService.Button_shared_operation();
        });
        lineSegmentDivisionSetButton.addActionListener(e -> {
            getData(applicationModel);

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);
        });
        lineSegmentDivisionTextField.addActionListener(e -> lineSegmentDivisionSetButton.doClick());
        lineSegmentDivisionTextField.getDocument().addDocumentListener(new OnlyIntAdapter(lineSegmentDivisionTextField));
        lineSegmentDivisionTextField.addKeyListener(new InputEnterKeyAdapter(lineSegmentDivisionTextField));
        lineSegmentDivisionTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                getData(applicationModel);
            }
        });
        senbun_b_nyuryokuButton.addActionListener(e -> {
            getData(applicationModel);

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);

            mainCreasePatternWorker.unselect_all(false);
        });
        selectButton.addActionListener(e -> canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19));
        selectAllButton.addActionListener(e -> mainCreasePatternWorker.select_all());
        unselectButton.addActionListener(e -> canvasModel.setMouseMode(MouseMode.CREASE_UNSELECT_20));
        unselectAllButton.addActionListener(e -> mainCreasePatternWorker.unselect_all());
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
        del_l_typeButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DELETE_LINE_TYPE_SELECT_73);
            mainCreasePatternWorker.unselect_all(false);
        });
        delTypeDropBox.addActionListener(e -> {
            applicationModel.setDelLineType(delTypeDropBox.getSelectedIndex() - 1);
            delTypeDropBox.setSelectedIndex(applicationModel.getDelLineType() + 1);
        });
        trimBranchesButton.addActionListener(e -> {
            mainCreasePatternWorker.point_removal();
            mainCreasePatternWorker.overlapping_line_removal();
            mainCreasePatternWorker.branch_trim();
            mainCreasePatternWorker.organizeCircles();
            mainCreasePatternWorker.record();
            mainCreasePatternWorker.unselect_all(false);
        });
        replace_lineButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.REPLACE_LINE_TYPE_SELECT_72);
            mainCreasePatternWorker.unselect_all(false);
        });
        fromLineDropBox.addActionListener(e -> {
            applicationModel.setCustomFromLineType(fromLineDropBox.getSelectedIndex() - 1);
            fromLineDropBox.setSelectedIndex(applicationModel.getCustomFromLineType() + 1);
        });
        toLineDropBox.addActionListener(e -> {
            applicationModel.setCustomToLineType(toLineDropBox.getSelectedIndex());
            toLineDropBox.setSelectedIndex(applicationModel.getCustomToLineType());
        });
        zen_yama_tani_henkanButton.addActionListener(e -> {
            mainCreasePatternWorker.allMountainValleyChange();
            mainCreasePatternWorker.unselect_all(false);
        });
        senbun_henkan2Button.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_TOGGLE_MV_58);

            mainCreasePatternWorker.unselect_all(false);
        });
        senbun_henkanButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CHANGE_CREASE_TYPE_4);

            mainCreasePatternWorker.unselect_all(false);
        });
        in_L_col_changeButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MV_34);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_MAKE_MV_34);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.RED_1);
            }

            mainCreasePatternWorker.unselect_all(false);
        });
        on_L_col_changeButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASES_ALTERNATE_MV_36);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASES_ALTERNATE_MV_36);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.BLUE_2);
            }

            mainCreasePatternWorker.unselect_all(false);
        });
        v_addButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DRAW_POINT_14);

            mainCreasePatternWorker.unselect_all(false);
        });
        v_delButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.DELETE_POINT_15);

            mainCreasePatternWorker.unselect_all(false);
        });
        v_del_ccButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.VERTEX_DELETE_ON_CREASE_41);

            mainCreasePatternWorker.unselect_all(false);
        });
        v_del_allButton.addActionListener(e -> {
            mainCreasePatternWorker.v_del_all();
            Logger.info("mainDrawingWorker.v_del_all()");
        });
        v_del_all_ccButton.addActionListener(e -> {
            mainCreasePatternWorker.v_del_all_cc();
            Logger.info("mainDrawingWorker.v_del_all_cc()");
        });
        correctCpBeforeFoldingCheckBox.addActionListener(e -> applicationModel.setCorrectCpBeforeFolding(correctCpBeforeFoldingCheckBox.isSelected()));
        selectPersistentCheckBox.addActionListener(e -> applicationModel.setSelectPersistent(selectPersistentCheckBox.isSelected()));
        drawTwoColoredCpButton.addActionListener(e -> foldingService.createTwoColoredCp());
        suitei_01Button.addActionListener(e -> {
            foldingService.fold(FoldedFigure.EstimationOrder.ORDER_1);//引数の意味は(i_fold_type , i_suitei_meirei);
            mainCreasePatternWorker.unselect_all(false);
        });
        koteimen_siteiButton.addActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null && selectedFigure.getFoldedFigure().displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                canvasModel.setMouseMode(MouseMode.CHANGE_STANDARD_FACE_103);
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
        gridSizeTextField.getDocument().addDocumentListener(new OnlyIntAdapter(gridSizeTextField));
        gridSizeTextField.addKeyListener(new InputEnterKeyAdapter(gridSizeTextField));
        gridSizeTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridSize(StringOp.String2int(gridSizeTextField.getText(), gridModel.getGridSize()));
            }
        });
        gridSizeIncreaseButton.addActionListener(e -> gridModel.setGridSize(gridModel.getGridSize() * 2));
        gridColorButton.addActionListener(e -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(frameProvider.get(), "Col", FlatLaf.isLafDark() ? Colors.GRID_LINE_DARK : Colors.GRID_LINE);
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
        intervalGridSizeTextField.getDocument().addDocumentListener(new OnlyIntAdapter(intervalGridSizeTextField));
        intervalGridSizeTextField.addKeyListener(new InputEnterKeyAdapter(intervalGridSizeTextField));
        intervalGridSizeTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setIntervalGridSize(StringOp.String2int(intervalGridSizeTextField.getText(), gridModel.getIntervalGridSize()));
            }
        });
        moveIntervalGridHorizontal.addActionListener(e -> gridModel.changeVerticalScalePosition());
        intervalGridColorButton.addActionListener(e -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(frameProvider.get(), "Col", FlatLaf.isLafDark() ? Colors.GRID_SCALE_DARK : Colors.GRID_SCALE);
            if (color != null) {
                applicationModel.setGridScaleColor(color);
            }
        });
        gridXATextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(gridXATextField));
        gridXATextField.addKeyListener(new InputEnterKeyAdapter(gridXATextField));
        gridXATextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridXA(measuresModel.string2double(gridXATextField.getText(), gridModel.getGridXA()));
            }
        });
        gridXBTextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(gridXBTextField));
        gridXBTextField.addKeyListener(new InputEnterKeyAdapter(gridXBTextField));
        gridXBTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridXB(measuresModel.string2double(gridXBTextField.getText(), gridModel.getGridXB()));
            }
        });
        gridXCTextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(gridXCTextField));
        gridXCTextField.addKeyListener(new InputEnterKeyAdapter(gridXCTextField));
        gridXCTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridXC(measuresModel.string2double(gridXCTextField.getText(), gridModel.getGridXC()));
            }
        });
        gridYATextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(gridYATextField));
        gridYATextField.addKeyListener(new InputEnterKeyAdapter(gridYATextField));
        gridYATextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridYA(measuresModel.string2double(gridYATextField.getText(), gridModel.getGridYA()));
            }
        });
        gridYBTextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(gridYBTextField));
        gridYBTextField.addKeyListener(new InputEnterKeyAdapter(gridYBTextField));
        gridYBTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridYB(measuresModel.string2double(gridYBTextField.getText(), gridModel.getGridYB()));
            }
        });
        gridYCTextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(gridYCTextField));
        gridYCTextField.addKeyListener(new InputEnterKeyAdapter(gridYCTextField));
        gridYCTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridYC(measuresModel.string2double(gridYCTextField.getText(), gridModel.getGridYC()));
            }
        });
        setGridParametersButton.addActionListener(e -> {
            getData(gridModel);
            // Update the view if the grid angle got reset
            setData(gridModel);
        });
        gridAngleTextField.addActionListener(e -> setGridParametersButton.doClick());
        gridAngleTextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(gridAngleTextField));
        gridAngleTextField.addKeyListener(new InputEnterKeyAdapter(gridAngleTextField));
        gridAngleTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridAngle(measuresModel.string2double(gridAngleTextField.getText(), gridModel.getGridAngle()));
            }
        });
        resetGridButton.addActionListener(e -> gridModel.reset());
        drawDiagonalGridlinesCheckBox.addActionListener(e -> gridModel.setDrawDiagonalGridlines(drawDiagonalGridlinesCheckBox.isSelected()));
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        root.setLayout(new GridLayoutManager(23, 1, new Insets(1, 1, 1, 1), 1, 1));
        undoRedo = new UndoRedo();
        root.add(undoRedo.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), 1, 1, false, true));
        root.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineWidthDecreaseButton = new JButton();
        lineWidthDecreaseButton.setActionCommand("lineWidthDecreaseAction");
        lineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_sage.png")));
        panel1.add(lineWidthDecreaseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineWidthIncreaseButton = new JButton();
        lineWidthIncreaseButton.setActionCommand("lineWidthIncreaseAction");
        lineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_age.png")));
        panel1.add(lineWidthIncreaseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pointSizeDecreaseButton = new JButton();
        pointSizeDecreaseButton.setActionCommand("pointSizeDecreaseAction");
        pointSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_sage.png")));
        panel1.add(pointSizeDecreaseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pointSizeIncreaseButton = new JButton();
        pointSizeIncreaseButton.setActionCommand("pointSizeIncreaseAction");
        pointSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_age.png")));
        panel1.add(pointSizeIncreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        antiAliasToggleButton = new JButton();
        antiAliasToggleButton.setActionCommand("antiAliasToggleAction");
        antiAliasToggleButton.setText("a_a");
        panel1.add(antiAliasToggleButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineStyleChangeButton = new JButton();
        lineStyleChangeButton.setActionCommand("lineStyleChangeAction");
        lineStyleChangeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orisen_hyougen.png")));
        panel1.add(lineStyleChangeButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colRedButton = new JButton();
        colRedButton.setActionCommand("colRedAction");
        colRedButton.setBackground(new Color(-6908266));
        colRedButton.setText("M");
        panel2.add(colRedButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colBlueButton = new JButton();
        colBlueButton.setActionCommand("colBlueAction");
        colBlueButton.setBackground(new Color(-6908266));
        colBlueButton.setText("V");
        panel2.add(colBlueButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colBlackButton = new JButton();
        colBlackButton.setActionCommand("colBlackAction");
        colBlackButton.setBackground(new Color(-6908266));
        colBlackButton.setText("E");
        panel2.add(colBlackButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colCyanButton = new JButton();
        colCyanButton.setActionCommand("colCyanAction");
        colCyanButton.setBackground(new Color(-6908266));
        colCyanButton.setText("A");
        panel2.add(colCyanButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel3, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        replace_lineButton = new JButton();
        replace_lineButton.setText("Button");
        panel3.add(replace_lineButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromLineDropBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Any");
        defaultComboBoxModel1.addElement("E");
        defaultComboBoxModel1.addElement("M & V");
        defaultComboBoxModel1.addElement("M");
        defaultComboBoxModel1.addElement("V");
        defaultComboBoxModel1.addElement("A");
        fromLineDropBox.setModel(defaultComboBoxModel1);
        panel3.add(fromLineDropBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        replaceLabel = new JLabel();
        replaceLabel.setText("");
        panel3.add(replaceLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toLineDropBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("E");
        defaultComboBoxModel2.addElement("M");
        defaultComboBoxModel2.addElement("V");
        defaultComboBoxModel2.addElement("A");
        toLineDropBox.setModel(defaultComboBoxModel2);
        panel3.add(toLineDropBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel4, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        zen_yama_tani_henkanButton = new JButton();
        zen_yama_tani_henkanButton.setActionCommand("zen_yama_tani_henkanAction");
        zen_yama_tani_henkanButton.setText("AC");
        panel4.add(zen_yama_tani_henkanButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(0, -1), new Dimension(0, -1), null, 0, false));
        senbun_henkan2Button = new JButton();
        senbun_henkan2Button.setActionCommand("senbun_henkan2Action");
        senbun_henkan2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan2.png")));
        senbun_henkan2Button.setText("");
        panel4.add(senbun_henkan2Button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        senbun_henkanButton = new JButton();
        senbun_henkanButton.setActionCommand("senbun_henkanAction");
        senbun_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan.png")));
        panel4.add(senbun_henkanButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel5, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        in_L_col_changeButton = new JButton();
        in_L_col_changeButton.setActionCommand("in_L_col_changeAction");
        in_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/in_L_col_change.png")));
        panel5.add(in_L_col_changeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        on_L_col_changeButton = new JButton();
        on_L_col_changeButton.setActionCommand("on_L_col_changeButton");
        on_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/on_L_col_change.png")));
        panel5.add(on_L_col_changeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel6, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_addButton = new JButton();
        v_addButton.setActionCommand("v_addAction");
        v_addButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_add.png")));
        panel6.add(v_addButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_delButton = new JButton();
        v_delButton.setActionCommand("v_delAction");
        v_delButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del.png")));
        panel6.add(v_delButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_ccButton = new JButton();
        v_del_ccButton.setActionCommand("v_del_ccAction");
        v_del_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_cc.png")));
        panel6.add(v_del_ccButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_allButton = new JButton();
        v_del_allButton.setActionCommand("v_del_allAction");
        v_del_allButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all.png")));
        panel6.add(v_del_allButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_all_ccButton = new JButton();
        v_del_all_ccButton.setActionCommand("v_del_all_ccAction");
        v_del_all_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all_cc.png")));
        panel6.add(v_del_all_ccButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel7, new GridConstraints(19, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        correctCpBeforeFoldingCheckBox = new JCheckBox();
        correctCpBeforeFoldingCheckBox.setText("Correct");
        panel7.add(correctCpBeforeFoldingCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectPersistentCheckBox = new JCheckBox();
        selectPersistentCheckBox.setText("Persist");
        selectPersistentCheckBox.setToolTipText("ckbox_select_nokosi");
        panel7.add(selectPersistentCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawTwoColoredCpButton = new JButton();
        drawTwoColoredCpButton.setActionCommand("drawTwoColoredCpAction");
        drawTwoColoredCpButton.setIcon(new ImageIcon(getClass().getResource("/ppp/2syoku_tenkaizu.png")));
        panel7.add(drawTwoColoredCpButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel8, new GridConstraints(20, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_01Button = new JButton();
        suitei_01Button.setActionCommand("suitei_01Action");
        suitei_01Button.setText("CP_rcg");
        panel8.add(suitei_01Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        koteimen_siteiButton = new JButton();
        koteimen_siteiButton.setActionCommand("koteimen_siteiAction");
        koteimen_siteiButton.setText("S_face");
        panel8.add(koteimen_siteiButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel9, new GridConstraints(21, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_02Button = new JButton();
        suitei_02Button.setActionCommand("suitei_02Action");
        suitei_02Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_02.png")));
        panel9.add(suitei_02Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_03Button = new JButton();
        suitei_03Button.setActionCommand("suitei_03Action");
        suitei_03Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_03.png")));
        panel9.add(suitei_03Button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayCheckBox = new JCheckBox();
        coloredXRayCheckBox.setText("Colored");
        panel9.add(coloredXRayCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayDecreaseButton = new JButton();
        coloredXRayDecreaseButton.setActionCommand("coloredXRayDecreaseAction");
        coloredXRayDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        panel9.add(coloredXRayDecreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayIncreaseButton = new JButton();
        coloredXRayIncreaseButton.setActionCommand("coloredXRayIncreaseAction");
        coloredXRayIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        panel9.add(coloredXRayIncreaseButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        root.add(panel10, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        perpendicularDrawButton = new JButton();
        perpendicularDrawButton.setActionCommand("perpendicularDrawAction");
        perpendicularDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suisen.png")));
        panel10.add(perpendicularDrawButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineSegmentDivisionTextField = new JTextField();
        lineSegmentDivisionTextField.setColumns(1);
        lineSegmentDivisionTextField.setText("2");
        panel10.add(lineSegmentDivisionTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), null, null, 0, false));
        lineSegmentDivisionSetButton = new JButton();
        lineSegmentDivisionSetButton.setActionCommand("lineSegmentDivisionSetAction");
        lineSegmentDivisionSetButton.setText("Set");
        panel10.add(lineSegmentDivisionSetButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        senbun_b_nyuryokuButton = new JButton();
        senbun_b_nyuryokuButton.setActionCommand("senbun_b_nyuryokuAction");
        senbun_b_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_b_nyuryoku.png")));
        panel10.add(senbun_b_nyuryokuButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        all_s_step_to_orisenButton = new JButton();
        all_s_step_to_orisenButton.setActionCommand("all_s_step_to_orisenAction");
        all_s_step_to_orisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/all_s_step_to_orisen.png")));
        panel10.add(all_s_step_to_orisenButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        voronoiButton = new JButton();
        voronoiButton.setActionCommand("voronoiAction");
        voronoiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Voronoi.png")));
        panel10.add(voronoiButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parallelDrawButton = new JButton();
        parallelDrawButton.setActionCommand("parallelDrawAction");
        parallelDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen.png")));
        panel10.add(parallelDrawButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fishBoneDrawButton = new JButton();
        fishBoneDrawButton.setActionCommand("fishBoneDrawAction");
        fishBoneDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sakananohone.png")));
        panel10.add(fishBoneDrawButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setParallelDrawWidthButton = new JButton();
        setParallelDrawWidthButton.setActionCommand("setParallelDrawWidthAction");
        setParallelDrawWidthButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen_haba_sitei.png")));
        panel10.add(setParallelDrawWidthButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doubleSymmetricDrawButton = new JButton();
        doubleSymmetricDrawButton.setActionCommand("doubleSymmetricDrawAction");
        doubleSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/fuku_orikaesi.png")));
        panel10.add(doubleSymmetricDrawButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        makeFlatFoldableButton = new JButton();
        makeFlatFoldableButton.setActionCommand("makeFlatFoldableAction");
        makeFlatFoldableButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen.png")));
        panel10.add(makeFlatFoldableButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        continuousSymmetricDrawButton = new JButton();
        continuousSymmetricDrawButton.setActionCommand("continuousSymmetricDrawAction");
        continuousSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/renzoku_orikaesi.png")));
        panel10.add(continuousSymmetricDrawButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        symmetricDrawButton = new JButton();
        symmetricDrawButton.setActionCommand("symmetricDrawAction");
        symmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orikaesi.png")));
        panel10.add(symmetricDrawButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        angleBisectorButton = new JButton();
        angleBisectorButton.setActionCommand("angleBisectorAction");
        angleBisectorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kaku_toubun.png")));
        angleBisectorButton.setText("");
        panel10.add(angleBisectorButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCrease2Button = new JButton();
        lengthenCrease2Button.setActionCommand("lengthenCrease2Action");
        lengthenCrease2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou_2.png")));
        panel10.add(lengthenCrease2Button, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCreaseButton = new JButton();
        lengthenCreaseButton.setActionCommand("lengthenCreaseAction");
        lengthenCreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou.png")));
        panel10.add(lengthenCreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseRestrictedButton = new JButton();
        drawCreaseRestrictedButton.setActionCommand("drawCreaseRestrictedAction");
        drawCreaseRestrictedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku11.png")));
        panel10.add(drawCreaseRestrictedButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rabbitEarButton = new JButton();
        rabbitEarButton.setActionCommand("rabbitEarAction");
        rabbitEarButton.setIcon(new ImageIcon(getClass().getResource("/ppp/naishin.png")));
        panel10.add(rabbitEarButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foldableLineDrawButton = new JButton();
        foldableLineDrawButton.setActionCommand("foldableLineAction");
        foldableLineDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen_and_kousitenkei_simple.png")));
        panel10.add(foldableLineDrawButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseFreeButton = new JButton();
        drawCreaseFreeButton.setActionCommand("drawCreaseFreeAction");
        drawCreaseFreeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku.png")));
        drawCreaseFreeButton.setText("");
        panel10.add(drawCreaseFreeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel11, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectButton = new JButton();
        selectButton.setActionCommand("selectAction");
        selectButton.setText("sel");
        panel11.add(selectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectAllButton = new JButton();
        selectAllButton.setActionCommand("selectAllAction");
        selectAllButton.setText("s_al");
        panel11.add(selectAllButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveButton = new JButton();
        moveButton.setActionCommand("moveAction");
        moveButton.setText("move");
        panel11.add(moveButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        move2p2pButton = new JButton();
        move2p2pButton.setActionCommand("move2p2pAction");
        move2p2pButton.setText("mv_4p");
        panel11.add(move2p2pButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        reflectButton = new JButton();
        reflectButton.setActionCommand("reflectAction");
        reflectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kyouei.png")));
        panel11.add(reflectButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectButton = new JButton();
        unselectButton.setActionCommand("unselectAction");
        unselectButton.setText("unsel");
        panel11.add(unselectButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectAllButton = new JButton();
        unselectAllButton.setActionCommand("unselectAllAction");
        unselectAllButton.setText("uns_al");
        panel11.add(unselectAllButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setActionCommand("copyAction");
        copyButton.setText("copy");
        panel11.add(copyButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copy2p2pButton = new JButton();
        copy2p2pButton.setActionCommand("copy2p2pAction");
        copy2p2pButton.setText("cp_4p");
        panel11.add(copy2p2pButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deleteSelectedLineSegmentButton = new JButton();
        deleteSelectedLineSegmentButton.setActionCommand("d_s_LAction");
        deleteSelectedLineSegmentButton.setText("d_s_L");
        panel11.add(deleteSelectedLineSegmentButton, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel12, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeDecreaseButton = new JButton();
        gridSizeDecreaseButton.setActionCommand("gridSizeDecreaseAction");
        gridSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei2.png")));
        panel12.add(gridSizeDecreaseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeTextField = new JTextField();
        gridSizeTextField.setColumns(2);
        panel12.add(gridSizeTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridSizeSetButton = new JButton();
        gridSizeSetButton.setActionCommand("gridSizeSetAction");
        gridSizeSetButton.setText("S");
        panel12.add(gridSizeSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeIncreaseButton = new JButton();
        gridSizeIncreaseButton.setActionCommand("gridSizeIncreaseAction");
        gridSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei.png")));
        panel12.add(gridSizeIncreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridColorButton = new JButton();
        gridColorButton.setActionCommand("gridColorAction");
        gridColorButton.setText("Color");
        panel12.add(gridColorButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridLineWidthDecreaseButton = new JButton();
        gridLineWidthDecreaseButton.setActionCommand("gridLineWidthDecreaseAction");
        gridLineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_sage.png")));
        panel12.add(gridLineWidthDecreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridLineWidthIncreaseButton = new JButton();
        gridLineWidthIncreaseButton.setActionCommand("gridLineWidthIncreaseAction");
        gridLineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_age.png")));
        panel12.add(gridLineWidthIncreaseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        changeGridStateButton = new JButton();
        changeGridStateButton.setActionCommand("changeGridStateAction");
        changeGridStateButton.setIcon(new ImageIcon(getClass().getResource("/ppp/i_kitei_jyoutai.png")));
        panel12.add(changeGridStateButton, new GridConstraints(1, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveIntervalGridVerticalButton = new JButton();
        moveIntervalGridVerticalButton.setActionCommand("moveIntervalGridVerticalAction");
        moveIntervalGridVerticalButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_tate_idou.png")));
        panel12.add(moveIntervalGridVerticalButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridSizeTextField = new JTextField();
        intervalGridSizeTextField.setColumns(2);
        intervalGridSizeTextField.setText("8");
        panel12.add(intervalGridSizeTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        setIntervalGridSizeButton = new JButton();
        setIntervalGridSizeButton.setActionCommand("setIntervalGridSizeAction");
        setIntervalGridSizeButton.setText("S");
        panel12.add(setIntervalGridSizeButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveIntervalGridHorizontal = new JButton();
        moveIntervalGridHorizontal.setActionCommand("moveIntervalGridHorizontalAction");
        moveIntervalGridHorizontal.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_yoko_idou.png")));
        panel12.add(moveIntervalGridHorizontal, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridColorButton = new JButton();
        intervalGridColorButton.setActionCommand("intervalGridColorAction");
        intervalGridColorButton.setText("Color");
        panel12.add(intervalGridColorButton, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel13, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        trimBranchesButton = new JButton();
        trimBranchesButton.setActionCommand("trimBranchesAction");
        trimBranchesButton.setIcon(new ImageIcon(getClass().getResource("/ppp/eda_kesi.png")));
        panel13.add(trimBranchesButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        del_l_typeButton = new JButton();
        del_l_typeButton.setText("");
        panel13.add(del_l_typeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delTypeDropBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Any");
        defaultComboBoxModel3.addElement("Edge");
        defaultComboBoxModel3.addElement("M & V");
        defaultComboBoxModel3.addElement("Mountain");
        defaultComboBoxModel3.addElement("Valley");
        defaultComboBoxModel3.addElement("Aux");
        delTypeDropBox.setModel(defaultComboBoxModel3);
        panel13.add(delTypeDropBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(18, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer5 = new Spacer();
        root.add(spacer5, new GridConstraints(22, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        setGridParametersButton.setActionCommand("setGridParametersAction");
        setGridParametersButton.setText("S");
        panel14.add(setGridParametersButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        resetGridButton = new JButton();
        resetGridButton.setActionCommand("resetGridAction");
        resetGridButton.setText("Reset");
        panel14.add(resetGridButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        drawDiagonalGridlinesCheckBox = new JCheckBox();
        drawDiagonalGridlinesCheckBox.setText("Draw diagonal gridlines");
        drawDiagonalGridlinesCheckBox.setToolTipText("Draw diagonal gridlines");
        root.add(drawDiagonalGridlinesCheckBox, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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

        delTypeDropBox.setSelectedIndex(applicationModel.getDelLineType() + 1);
        fromLineDropBox.setSelectedIndex(applicationModel.getCustomFromLineType() + 1);
        toLineDropBox.setSelectedIndex(applicationModel.getCustomToLineType());

        if (e.getPropertyName() == null || e.getPropertyName().equals("laf")) {
            // The look and feel is not set yet, so it must be read from the applicationModel
            boolean isDark = LookAndFeelUtil.determineLafDark(data.getLaf());

            for (JButton button : Arrays.asList(colBlackButton, colRedButton, colBlueButton, colCyanButton, senbun_henkan2Button)) {
                button.setForeground(Colors.restore(button.getForeground(), isDark));
                button.setBackground(Colors.restore(button.getBackground(), isDark));
            }
        }
    }

    private void refreshButtons() {
        refreshSelectionButtons();
    }

    private void resetSelectionButtons() {
        Logger.info("Resetting Selection Buttons to default");

        Border defaultBorder = (Border) UIManager.get("Button.border");

        for (JButton sttButton : selectionTransformationToolLookup.values()) {
            sttButton.setBorder(defaultBorder);
        }

        Canvas.clearUserWarningMessage();
    }

    private void refreshSelectionButtons() {
        resetSelectionButtons();

        boolean nonEmptySelection = !mainCreasePatternWorker.getIsSelectionEmpty();

        for (JButton sttButton : selectionTransformationToolLookup.values()) {
            sttButton.setEnabled(nonEmptySelection);
        }

        MouseMode currentMouseMode = this.canvasModel.getMouseMode();

        if (selectionTransformationToolLookup.containsKey(currentMouseMode)) {

            JButton sttButton = selectionTransformationToolLookup.get(currentMouseMode);
            LineBorder highlight = null;

            if (!nonEmptySelection) {
                highlight = new LineBorder(Color.yellow);
                Logger.info("Highlight for selection tools has been set to yellow");
                Canvas.setUserWarningMessage("Selection Transformation Tools depend on crease(s) being selected in advance");
                sttButton.setBorder(highlight);
            }
        }
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {

        if ("mouseMode".equals(e.getPropertyName())) {
            refreshButtons();
        }

        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode") || e.getPropertyName().equals("foldLineAdditionalInputMode")) {
            MouseMode m = data.getMouseMode();
            FoldLineAdditionalInputMode f = data.getFoldLineAdditionalInputMode();

            drawCreaseFreeButton.setSelected(m == MouseMode.DRAW_CREASE_FREE_1);
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
            voronoiButton.setSelected(m == MouseMode.VORONOI_CREATE_62);
            lengthenCrease2Button.setSelected(m == MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            foldableLineDrawButton.setSelected(m == MouseMode.FOLDABLE_LINE_DRAW_71);
            koteimen_siteiButton.setSelected(m == MouseMode.CHANGE_STANDARD_FACE_103);
            del_l_typeButton.setSelected(m == MouseMode.DELETE_LINE_TYPE_SELECT_73);
            replace_lineButton.setSelected(m == MouseMode.REPLACE_LINE_TYPE_SELECT_72);
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
            senbun_henkan2Button.setBackground(buttonBg);
            senbun_henkan2Button.setForeground(buttonFg);

            switch (data.getMouseMode()) {
                case CREASE_TOGGLE_MV_58:
                    senbun_henkan2Button.setBackground(Colors.get(new Color(138, 43, 226)));
                    senbun_henkan2Button.setForeground(Colors.get(Color.white));
                    break;
                default:
                    break;
            }
        }
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        coloredXRayCheckBox.setSelected(foldedFigureModel.isTransparencyColor());
    }

    public void setData(PropertyChangeEvent e, CreasePattern_Worker mainCreasePatternWorker) {
        Logger.info(e.toString());
        if ("isSelectionEmpty".equals(e.getPropertyName())) {
            refreshButtons();
        }
    }
}
