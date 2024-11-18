package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.Canvas;
import oriedita.editor.Colors;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.factory.RegexHighlightFactory;
import oriedita.editor.handler.PopupMenuAdapter;
import origami.crease_pattern.CustomLineTypes;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.HistoryState;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.UndoRedo;
import oriedita.editor.tools.LookAndFeelUtil;
import oriedita.editor.tools.StringOp;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;


@ApplicationScoped
public class LeftPanel {
    private final HistoryState historyState;
    private final MeasuresModel measuresModel;
    private final ButtonService buttonService;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final ApplicationModel applicationModel;
    private final FoldedFigureModel foldedFigureModel;
    private final GridModel gridModel;
    private final CanvasModel canvasModel;
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
    private JScrollPane scrollPane1;
    private JButton switchReplaceButton;

    private HashMap<MouseMode, JButton> selectionTransformationToolLookup;
    private boolean isReplaceHold;
    private boolean isDelTypeHold;

    public double convertAngle(double angle) {
        if (angle >= 0) return angle % 180;
        else return 180 + (angle % 180);
    }

    @Inject
    public LeftPanel(@Named("normal") HistoryState historyState,
                     MeasuresModel measuresModel,
                     ButtonService buttonService,
                     @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                     ApplicationModel applicationModel,
                     FoldedFigureModel foldedFigureModel,
                     GridModel gridModel,
                     CanvasModel canvasModel) {
        this.historyState = historyState;
        this.measuresModel = measuresModel;
        this.buttonService = buttonService;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.applicationModel = applicationModel;
        this.foldedFigureModel = foldedFigureModel;
        this.gridModel = gridModel;
        this.canvasModel = canvasModel;

        this.selectionTransformationToolLookup = new HashMap<>();

        $$$setupUI$$$();
        this.selectionTransformationToolLookup.put(null, deleteSelectedLineSegmentButton);
        this.selectionTransformationToolLookup.put(MouseMode.DRAW_CREASE_SYMMETRIC_12, reflectButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_MOVE_21, moveButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_COPY_22, copyButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_MOVE_4P_31, move2p2pButton);
        this.selectionTransformationToolLookup.put(MouseMode.CREASE_COPY_4P_32, copy2p2pButton);

        isReplaceHold = false;
        isDelTypeHold = false;
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());

        applicationModel.addPropertyChangeListener(e -> setData(e, applicationModel));
        gridModel.addPropertyChangeListener(e -> setData(gridModel));
        foldedFigureModel.addPropertyChangeListener(e -> setData(foldedFigureModel));
        canvasModel.addPropertyChangeListener(e -> setData(e, canvasModel));
        mainCreasePatternWorker.addPropertyChangeListener(this::setData);
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
        buttonService.registerButton(del_l_typeButton, "del_l_typeAction");
        buttonService.registerButton(trimBranchesButton, "trimBranchesAction");
        buttonService.registerButton(replace_lineButton, "replace_lineAction");
        buttonService.registerButton(switchReplaceButton, "switchReplaceAction");
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

        buttonService.registerButton(selectPersistentCheckBox, "selectPersistentAction");
        buttonService.registerButton(coloredXRayCheckBox, "coloredXRayAction");
        buttonService.registerButton(drawDiagonalGridlinesCheckBox, "drawDiagonalGridlinesAction");

        buttonService.registerTextField(gridAngleTextField, "setGridAngleAction");
        buttonService.registerTextField(gridXATextField, "setGridXAction");
        buttonService.registerTextField(gridXBTextField, "setGridXAction");
        buttonService.registerTextField(gridXCTextField, "setGridXAction");
        buttonService.registerTextField(gridYATextField, "setGridYAction");
        buttonService.registerTextField(gridYBTextField, "setGridYAction");
        buttonService.registerTextField(gridYCTextField, "setGridYAction");

        buttonService.setIcon(gridXPlusLabel, "labelPlus");
        buttonService.setIcon(gridYPlusLabel, "labelPlus");
        buttonService.setIcon(gridXSqrtLabel, "labelSqrt");
        buttonService.setIcon(gridYSqrtLabel, "labelSqrt");
        buttonService.setIcon(replaceLabel, "labelReplace");

        // Disable switchReplaceButton if "Any" or "M & V" is active
        switchReplaceButton.setEnabled(applicationModel.getCustomFromLineType() != CustomLineTypes.ANY &&
                applicationModel.getCustomFromLineType() != CustomLineTypes.MANDV);

        undoRedo.addUndoActionListener(e -> mainCreasePatternWorker.undo());
        undoRedo.addRedoActionListener(e -> mainCreasePatternWorker.redo());
        lineSegmentDivisionSetButton.addActionListener(e -> {
            getData(applicationModel);

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);
        });
        lineSegmentDivisionTextField.addActionListener(e -> lineSegmentDivisionSetButton.doClick());
        lineSegmentDivisionTextField.getDocument().addDocumentListener(RegexHighlightFactory.intRegexAdapter(lineSegmentDivisionTextField));
        lineSegmentDivisionTextField.addKeyListener(new InputEnterKeyAdapter(lineSegmentDivisionTextField));
        lineSegmentDivisionTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                getData(applicationModel);
            }
        });
        senbun_b_nyuryokuButton.addActionListener(e -> getData(applicationModel));
        delTypeDropBox.addActionListener(e -> applicationModel.setDelLineType(CustomLineTypes.from(delTypeDropBox.getSelectedIndex() - 1)));
        delTypeDropBox.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                int index = delTypeDropBox.getSelectedIndex();
                int itemCount = delTypeDropBox.getItemCount();
                if (e.getWheelRotation() > 0) {
                    delTypeDropBox.setSelectedIndex((index + 1) % itemCount);
                } else if (e.getWheelRotation() < 0) {
                    delTypeDropBox.setSelectedIndex(index != 0 ? (index - 1) % itemCount : itemCount - 1);
                }

                e.consume();
                delTypeDropBox.showPopup();
            }
        });
        delTypeDropBox.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                del_l_typeButton.doClick();
            }
        });
        fromLineDropBox.addActionListener(e -> {
            applicationModel.setCustomFromLineType(CustomLineTypes.from(fromLineDropBox.getSelectedIndex() - 1));

            // Disable switchReplaceButton if "Any" or "M & V" is active
            switchReplaceButton.setEnabled(applicationModel.getCustomFromLineType() != CustomLineTypes.ANY &&
                    applicationModel.getCustomFromLineType() != CustomLineTypes.MANDV);
        });
        fromLineDropBox.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int index = fromLineDropBox.getSelectedIndex();
                int itemCount = fromLineDropBox.getItemCount();
                if (e.getWheelRotation() > 0) {
                    fromLineDropBox.setSelectedIndex((index + 1) % itemCount);
                } else if (e.getWheelRotation() < 0) {
                    fromLineDropBox.setSelectedIndex(index != 0 ? (index - 1) % itemCount : itemCount - 1);
                }

                e.consume();
                fromLineDropBox.showPopup();
            }
        });
        fromLineDropBox.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                replace_lineButton.doClick();
            }
        });
        toLineDropBox.addActionListener(e -> {
            int index = toLineDropBox.getSelectedIndex();
            if (index == CustomLineTypes.EGDE.getNumber()) {
                applicationModel.setCustomToLineType(CustomLineTypes.from(index));
            } else {
                applicationModel.setCustomToLineType(CustomLineTypes.from(index + 1));
            }
        });
        toLineDropBox.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int index = toLineDropBox.getSelectedIndex();
                int itemCount = toLineDropBox.getItemCount();
                if (e.getWheelRotation() > 0) {
                    toLineDropBox.setSelectedIndex((index + 1) % itemCount);
                } else if (e.getWheelRotation() < 0) {
                    toLineDropBox.setSelectedIndex(index != 0 ? (index - 1) % itemCount : itemCount - 1);
                }

                e.consume();
                toLineDropBox.showPopup();
            }
        });
        toLineDropBox.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                replace_lineButton.doClick();
            }
        });
        selectPersistentCheckBox.addActionListener(e -> applicationModel.setSelectPersistent(selectPersistentCheckBox.isSelected()));
        coloredXRayCheckBox.addActionListener(e -> foldedFigureModel.setTransparencyColor(coloredXRayCheckBox.isSelected()));

        gridSizeSetButton.addActionListener(e -> getData(gridModel));
        gridSizeTextField.addActionListener(e -> gridSizeSetButton.doClick());
        gridSizeTextField.getDocument().addDocumentListener(RegexHighlightFactory.intRegexAdapter(gridSizeTextField));
        gridSizeTextField.addKeyListener(new InputEnterKeyAdapter(gridSizeTextField));
        gridSizeTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridSize(StringOp.String2int(gridSizeTextField.getText(), gridModel.getGridSize()));
            }
        });
        setIntervalGridSizeButton.addActionListener(e -> getData(gridModel));
        intervalGridSizeTextField.addActionListener(e -> setIntervalGridSizeButton.doClick());
        intervalGridSizeTextField.getDocument().addDocumentListener(RegexHighlightFactory.intRegexAdapter(intervalGridSizeTextField));
        intervalGridSizeTextField.addKeyListener(new InputEnterKeyAdapter(intervalGridSizeTextField));
        intervalGridSizeTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setIntervalGridSize(StringOp.String2int(intervalGridSizeTextField.getText(), gridModel.getIntervalGridSize()));
            }
        });
        gridXATextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(gridXATextField));
        gridXATextField.addKeyListener(new InputEnterKeyAdapter(gridXATextField));
        gridXATextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridXA(measuresModel.string2double(gridXATextField.getText(), gridModel.getGridXA()));
            }
        });
        gridXBTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(gridXBTextField));
        gridXBTextField.addKeyListener(new InputEnterKeyAdapter(gridXBTextField));
        gridXBTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridXB(measuresModel.string2double(gridXBTextField.getText(), gridModel.getGridXB()));
            }
        });
        gridXCTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(gridXCTextField));
        gridXCTextField.addKeyListener(new InputEnterKeyAdapter(gridXCTextField));
        gridXCTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridXC(measuresModel.string2double(gridXCTextField.getText(), gridModel.getGridXC()));
            }
        });
        gridYATextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(gridYATextField));
        gridYATextField.addKeyListener(new InputEnterKeyAdapter(gridYATextField));
        gridYATextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridYA(measuresModel.string2double(gridYATextField.getText(), gridModel.getGridYA()));
            }
        });
        gridYBTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(gridYBTextField));
        gridYBTextField.addKeyListener(new InputEnterKeyAdapter(gridYBTextField));
        gridYBTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridYB(measuresModel.string2double(gridYBTextField.getText(), gridModel.getGridYB()));
            }
        });
        gridYCTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(gridYCTextField));
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
        gridAngleTextField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(gridAngleTextField));
        gridAngleTextField.addKeyListener(new InputEnterKeyAdapter(gridAngleTextField));
        gridAngleTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                gridModel.setGridAngle(convertAngle(measuresModel.string2double(gridAngleTextField.getText(), gridModel.getGridAngle())));
            }
        });
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

        gridModel.setGridAngle(convertAngle(measuresModel.string2double(gridAngleTextField.getText(), gridModel.getGridAngle())));

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
        createUIComponents();
        root = new JPanel();
        root.setLayout(new GridLayoutManager(1, 1, new Insets(1, 1, 1, 1), 1, 1));
        scrollPane1.setHorizontalScrollBarPolicy(31);
        scrollPane1.setVerticalScrollBarPolicy(21);
        root.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(23, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel1);
        undoRedo = new UndoRedo();
        panel1.add(undoRedo.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), 1, 1, false, true));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineWidthDecreaseButton = new JButton();
        lineWidthDecreaseButton.setActionCommand("lineWidthDecreaseAction");
        lineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_sage.png")));
        panel2.add(lineWidthDecreaseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineWidthIncreaseButton = new JButton();
        lineWidthIncreaseButton.setActionCommand("lineWidthIncreaseAction");
        lineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_age.png")));
        panel2.add(lineWidthIncreaseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pointSizeDecreaseButton = new JButton();
        pointSizeDecreaseButton.setActionCommand("pointSizeDecreaseAction");
        pointSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_sage.png")));
        panel2.add(pointSizeDecreaseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pointSizeIncreaseButton = new JButton();
        pointSizeIncreaseButton.setActionCommand("pointSizeIncreaseAction");
        pointSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_age.png")));
        panel2.add(pointSizeIncreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        antiAliasToggleButton = new JButton();
        antiAliasToggleButton.setActionCommand("antiAliasToggleAction");
        antiAliasToggleButton.setText("a_a");
        panel2.add(antiAliasToggleButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineStyleChangeButton = new JButton();
        lineStyleChangeButton.setActionCommand("lineStyleChangeAction");
        lineStyleChangeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orisen_hyougen.png")));
        panel2.add(lineStyleChangeButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        panel1.add(panel3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colRedButton = new JButton();
        colRedButton.setActionCommand("colRedAction");
        colRedButton.setBackground(new Color(-6908266));
        colRedButton.setText("M");
        panel3.add(colRedButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colBlueButton = new JButton();
        colBlueButton.setActionCommand("colBlueAction");
        colBlueButton.setBackground(new Color(-6908266));
        colBlueButton.setText("V");
        panel3.add(colBlueButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colBlackButton = new JButton();
        colBlackButton.setActionCommand("colBlackAction");
        colBlackButton.setBackground(new Color(-6908266));
        colBlackButton.setText("E");
        panel3.add(colBlackButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colCyanButton = new JButton();
        colCyanButton.setActionCommand("colCyanAction");
        colCyanButton.setBackground(new Color(-6908266));
        colCyanButton.setText("A");
        panel3.add(colCyanButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), 1, 1, true, false));
        panel1.add(panel4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        perpendicularDrawButton = new JButton();
        perpendicularDrawButton.setActionCommand("perpendicularDrawAction");
        perpendicularDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suisen.png")));
        panel4.add(perpendicularDrawButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineSegmentDivisionTextField = new JTextField();
        lineSegmentDivisionTextField.setColumns(1);
        lineSegmentDivisionTextField.setText("2");
        panel4.add(lineSegmentDivisionTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), null, null, 0, false));
        lineSegmentDivisionSetButton = new JButton();
        lineSegmentDivisionSetButton.setActionCommand("lineSegmentDivisionSetAction");
        lineSegmentDivisionSetButton.setText("Set");
        panel4.add(lineSegmentDivisionSetButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        senbun_b_nyuryokuButton = new JButton();
        senbun_b_nyuryokuButton.setActionCommand("senbun_b_nyuryokuAction");
        senbun_b_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_b_nyuryoku.png")));
        panel4.add(senbun_b_nyuryokuButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        all_s_step_to_orisenButton = new JButton();
        all_s_step_to_orisenButton.setActionCommand("all_s_step_to_orisenAction");
        all_s_step_to_orisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/all_s_step_to_orisen.png")));
        panel4.add(all_s_step_to_orisenButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        voronoiButton = new JButton();
        voronoiButton.setActionCommand("voronoiAction");
        voronoiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Voronoi.png")));
        panel4.add(voronoiButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parallelDrawButton = new JButton();
        parallelDrawButton.setActionCommand("parallelDrawAction");
        parallelDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen.png")));
        panel4.add(parallelDrawButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fishBoneDrawButton = new JButton();
        fishBoneDrawButton.setActionCommand("fishBoneDrawAction");
        fishBoneDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sakananohone.png")));
        panel4.add(fishBoneDrawButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setParallelDrawWidthButton = new JButton();
        setParallelDrawWidthButton.setActionCommand("setParallelDrawWidthAction");
        setParallelDrawWidthButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen_haba_sitei.png")));
        panel4.add(setParallelDrawWidthButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doubleSymmetricDrawButton = new JButton();
        doubleSymmetricDrawButton.setActionCommand("doubleSymmetricDrawAction");
        doubleSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/fuku_orikaesi.png")));
        panel4.add(doubleSymmetricDrawButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        makeFlatFoldableButton = new JButton();
        makeFlatFoldableButton.setActionCommand("makeFlatFoldableAction");
        makeFlatFoldableButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen.png")));
        panel4.add(makeFlatFoldableButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        continuousSymmetricDrawButton = new JButton();
        continuousSymmetricDrawButton.setActionCommand("continuousSymmetricDrawAction");
        continuousSymmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/renzoku_orikaesi.png")));
        panel4.add(continuousSymmetricDrawButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        symmetricDrawButton = new JButton();
        symmetricDrawButton.setActionCommand("symmetricDrawAction");
        symmetricDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orikaesi.png")));
        panel4.add(symmetricDrawButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        angleBisectorButton = new JButton();
        angleBisectorButton.setActionCommand("angleBisectorAction");
        angleBisectorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kaku_toubun.png")));
        angleBisectorButton.setText("");
        panel4.add(angleBisectorButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCrease2Button = new JButton();
        lengthenCrease2Button.setActionCommand("lengthenCrease2Action");
        lengthenCrease2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou_2.png")));
        panel4.add(lengthenCrease2Button, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lengthenCreaseButton = new JButton();
        lengthenCreaseButton.setActionCommand("lengthenCreaseAction");
        lengthenCreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou.png")));
        panel4.add(lengthenCreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseRestrictedButton = new JButton();
        drawCreaseRestrictedButton.setActionCommand("drawCreaseRestrictedAction");
        drawCreaseRestrictedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku11.png")));
        panel4.add(drawCreaseRestrictedButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rabbitEarButton = new JButton();
        rabbitEarButton.setActionCommand("rabbitEarAction");
        rabbitEarButton.setIcon(new ImageIcon(getClass().getResource("/ppp/naishin.png")));
        panel4.add(rabbitEarButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foldableLineDrawButton = new JButton();
        foldableLineDrawButton.setActionCommand("foldableLineAction");
        foldableLineDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen_and_kousitenkei_simple.png")));
        panel4.add(foldableLineDrawButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawCreaseFreeButton = new JButton();
        drawCreaseFreeButton.setActionCommand("drawCreaseFreeAction");
        drawCreaseFreeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku.png")));
        drawCreaseFreeButton.setText("");
        panel4.add(drawCreaseFreeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectButton = new JButton();
        selectButton.setActionCommand("selectAction");
        selectButton.setText("sel");
        panel5.add(selectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectAllButton = new JButton();
        selectAllButton.setActionCommand("selectAllAction");
        selectAllButton.setText("s_al");
        panel5.add(selectAllButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveButton = new JButton();
        moveButton.setActionCommand("moveAction");
        moveButton.setText("move");
        panel5.add(moveButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        move2p2pButton = new JButton();
        move2p2pButton.setActionCommand("move2p2pAction");
        move2p2pButton.setText("mv_4p");
        panel5.add(move2p2pButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        reflectButton = new JButton();
        reflectButton.setActionCommand("reflectAction");
        reflectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kyouei.png")));
        panel5.add(reflectButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectButton = new JButton();
        unselectButton.setActionCommand("unselectAction");
        unselectButton.setText("unsel");
        panel5.add(unselectButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        unselectAllButton = new JButton();
        unselectAllButton.setActionCommand("unselectAllAction");
        unselectAllButton.setText("uns_al");
        panel5.add(unselectAllButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setActionCommand("copyAction");
        copyButton.setText("copy");
        panel5.add(copyButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copy2p2pButton = new JButton();
        copy2p2pButton.setActionCommand("copy2p2pAction");
        copy2p2pButton.setText("cp_4p");
        panel5.add(copy2p2pButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deleteSelectedLineSegmentButton = new JButton();
        deleteSelectedLineSegmentButton.setActionCommand("d_s_LAction");
        deleteSelectedLineSegmentButton.setText("d_s_L");
        panel5.add(deleteSelectedLineSegmentButton, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel6, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        trimBranchesButton = new JButton();
        trimBranchesButton.setActionCommand("trimBranchesAction");
        trimBranchesButton.setIcon(new ImageIcon(getClass().getResource("/ppp/eda_kesi.png")));
        panel6.add(trimBranchesButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        del_l_typeButton = new JButton();
        del_l_typeButton.setText("");
        panel6.add(del_l_typeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delTypeDropBox = new JComboBox<>();
        final DefaultComboBoxModel<String> defaultComboBoxModel1 = new DefaultComboBoxModel<>();
        defaultComboBoxModel1.addElement("Any");
        defaultComboBoxModel1.addElement("E");
        defaultComboBoxModel1.addElement("M & V");
        defaultComboBoxModel1.addElement("M");
        defaultComboBoxModel1.addElement("V");
        defaultComboBoxModel1.addElement("A");
        delTypeDropBox.setModel(defaultComboBoxModel1);
        panel6.add(delTypeDropBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 1, 1, true, false));
        panel1.add(panel7, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        zen_yama_tani_henkanButton = new JButton();
        zen_yama_tani_henkanButton.setActionCommand("zen_yama_tani_henkanAction");
        zen_yama_tani_henkanButton.setText("AC");
        panel7.add(zen_yama_tani_henkanButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(0, -1), new Dimension(0, -1), null, 0, false));
        senbun_henkan2Button = new JButton();
        senbun_henkan2Button.setActionCommand("senbun_henkan2Action");
        senbun_henkan2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan2.png")));
        panel7.add(senbun_henkan2Button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        senbun_henkanButton = new JButton();
        senbun_henkanButton.setActionCommand("senbun_henkanAction");
        senbun_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan.png")));
        panel7.add(senbun_henkanButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel8, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        replace_lineButton = new JButton();
        replace_lineButton.setText("Button");
        panel8.add(replace_lineButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromLineDropBox = new JComboBox<>();
        final DefaultComboBoxModel<String> defaultComboBoxModel2 = new DefaultComboBoxModel<>();
        defaultComboBoxModel2.addElement("Any");
        defaultComboBoxModel2.addElement("E");
        defaultComboBoxModel2.addElement("M & V");
        defaultComboBoxModel2.addElement("M");
        defaultComboBoxModel2.addElement("V");
        defaultComboBoxModel2.addElement("A");
        fromLineDropBox.setModel(defaultComboBoxModel2);
        panel8.add(fromLineDropBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        replaceLabel = new JLabel();
        replaceLabel.setText("");
        panel8.add(replaceLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toLineDropBox = new JComboBox<>();
        final DefaultComboBoxModel<String> defaultComboBoxModel3 = new DefaultComboBoxModel<>();
        defaultComboBoxModel3.addElement("E");
        defaultComboBoxModel3.addElement("M");
        defaultComboBoxModel3.addElement("V");
        defaultComboBoxModel3.addElement("A");
        toLineDropBox.setModel(defaultComboBoxModel3);
        panel8.add(toLineDropBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        switchReplaceButton = new JButton();
        switchReplaceButton.setText("⇆");
        panel8.add(switchReplaceButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        in_L_col_changeButton = new JButton();
        in_L_col_changeButton.setActionCommand("in_L_col_changeAction");
        in_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/in_L_col_change.png")));
        panel1.add(in_L_col_changeButton, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel9, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        on_L_col_changeButton = new JButton();
        on_L_col_changeButton.setActionCommand("on_L_col_changeButton");
        on_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/on_L_col_change.png")));
        panel9.add(on_L_col_changeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel10, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_addButton = new JButton();
        v_addButton.setActionCommand("v_addAction");
        v_addButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_add.png")));
        panel10.add(v_addButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_delButton = new JButton();
        v_delButton.setActionCommand("v_delAction");
        v_delButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del.png")));
        panel10.add(v_delButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_ccButton = new JButton();
        v_del_ccButton.setActionCommand("v_del_ccAction");
        v_del_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_cc.png")));
        panel10.add(v_del_ccButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_allButton = new JButton();
        v_del_allButton.setActionCommand("v_del_allAction");
        v_del_allButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all.png")));
        panel10.add(v_del_allButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        v_del_all_ccButton = new JButton();
        v_del_all_ccButton.setActionCommand("v_del_all_ccAction");
        v_del_all_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all_cc.png")));
        panel10.add(v_del_all_ccButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel11, new GridConstraints(15, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeDecreaseButton = new JButton();
        gridSizeDecreaseButton.setActionCommand("gridSizeDecreaseAction");
        gridSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei2.png")));
        panel11.add(gridSizeDecreaseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeTextField = new JTextField();
        gridSizeTextField.setColumns(2);
        panel11.add(gridSizeTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridSizeSetButton = new JButton();
        gridSizeSetButton.setActionCommand("gridSizeSetAction");
        gridSizeSetButton.setText("S");
        panel11.add(gridSizeSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridSizeIncreaseButton = new JButton();
        gridSizeIncreaseButton.setActionCommand("gridSizeIncreaseAction");
        gridSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei.png")));
        panel11.add(gridSizeIncreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridColorButton = new JButton();
        gridColorButton.setActionCommand("gridColorAction");
        gridColorButton.setText("Color");
        panel11.add(gridColorButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridLineWidthDecreaseButton = new JButton();
        gridLineWidthDecreaseButton.setActionCommand("gridLineWidthDecreaseAction");
        gridLineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_sage.png")));
        panel11.add(gridLineWidthDecreaseButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridLineWidthIncreaseButton = new JButton();
        gridLineWidthIncreaseButton.setActionCommand("gridLineWidthIncreaseAction");
        gridLineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_age.png")));
        panel11.add(gridLineWidthIncreaseButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        changeGridStateButton = new JButton();
        changeGridStateButton.setActionCommand("changeGridStateAction");
        changeGridStateButton.setIcon(new ImageIcon(getClass().getResource("/ppp/i_kitei_jyoutai.png")));
        panel11.add(changeGridStateButton, new GridConstraints(1, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveIntervalGridVerticalButton = new JButton();
        moveIntervalGridVerticalButton.setActionCommand("moveIntervalGridVerticalAction");
        moveIntervalGridVerticalButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_tate_idou.png")));
        panel11.add(moveIntervalGridVerticalButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridSizeTextField = new JTextField();
        intervalGridSizeTextField.setColumns(2);
        intervalGridSizeTextField.setText("8");
        panel11.add(intervalGridSizeTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        setIntervalGridSizeButton = new JButton();
        setIntervalGridSizeButton.setActionCommand("setIntervalGridSizeAction");
        setIntervalGridSizeButton.setText("S");
        panel11.add(setIntervalGridSizeButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveIntervalGridHorizontal = new JButton();
        moveIntervalGridHorizontal.setActionCommand("moveIntervalGridHorizontalAction");
        moveIntervalGridHorizontal.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_yoko_idou.png")));
        panel11.add(moveIntervalGridHorizontal, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        intervalGridColorButton = new JButton();
        intervalGridColorButton.setActionCommand("intervalGridColorAction");
        intervalGridColorButton.setText("Color");
        panel11.add(intervalGridColorButton, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel12, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXATextField = new JTextField();
        gridXATextField.setColumns(3);
        gridXATextField.setText("0.0");
        panel12.add(gridXATextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridXPlusLabel = new JLabel();
        gridXPlusLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        panel12.add(gridXPlusLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXBTextField = new JTextField();
        gridXBTextField.setColumns(3);
        gridXBTextField.setText("1.0");
        panel12.add(gridXBTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridXSqrtLabel = new JLabel();
        gridXSqrtLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        panel12.add(gridXSqrtLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridXCTextField = new JTextField();
        gridXCTextField.setColumns(3);
        gridXCTextField.setText("1.0");
        panel12.add(gridXCTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridYATextField = new JTextField();
        gridYATextField.setColumns(3);
        gridYATextField.setText("0.0");
        panel12.add(gridYATextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridYPlusLabel = new JLabel();
        gridYPlusLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        panel12.add(gridYPlusLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridYBTextField = new JTextField();
        gridYBTextField.setColumns(3);
        gridYBTextField.setText("1.0");
        panel12.add(gridYBTextField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        gridYSqrtLabel = new JLabel();
        gridYSqrtLabel.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        panel12.add(gridYSqrtLabel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridYCTextField = new JTextField();
        gridYCTextField.setColumns(3);
        gridYCTextField.setText("1.0");
        panel12.add(gridYCTextField, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel13, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gridAngleTextField = new JTextField();
        gridAngleTextField.setColumns(3);
        gridAngleTextField.setHorizontalAlignment(11);
        gridAngleTextField.setText("90.0");
        panel13.add(gridAngleTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        setGridParametersButton = new JButton();
        setGridParametersButton.setActionCommand("setGridParametersAction");
        setGridParametersButton.setText("S");
        panel13.add(setGridParametersButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        resetGridButton = new JButton();
        resetGridButton.setActionCommand("resetGridAction");
        resetGridButton.setText("Reset");
        panel13.add(resetGridButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        drawDiagonalGridlinesCheckBox = new JCheckBox();
        drawDiagonalGridlinesCheckBox.setText("Draw diagonal gridlines");
        drawDiagonalGridlinesCheckBox.setToolTipText("Draw diagonal gridlines");
        panel1.add(drawDiagonalGridlinesCheckBox, new GridConstraints(18, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(19, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel14, new GridConstraints(20, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectPersistentCheckBox = new JCheckBox();
        selectPersistentCheckBox.setText("Persist");
        selectPersistentCheckBox.setToolTipText("ckbox_select_nokosi");
        panel14.add(selectPersistentCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawTwoColoredCpButton = new JButton();
        drawTwoColoredCpButton.setActionCommand("drawTwoColoredCpAction");
        drawTwoColoredCpButton.setIcon(new ImageIcon(getClass().getResource("/ppp/2syoku_tenkaizu.png")));
        panel14.add(drawTwoColoredCpButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 1, 1, true, false));
        panel1.add(panel15, new GridConstraints(21, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_01Button = new JButton();
        suitei_01Button.setActionCommand("suitei_01Action");
        suitei_01Button.setText("CP_rcg");
        panel15.add(suitei_01Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        koteimen_siteiButton = new JButton();
        koteimen_siteiButton.setActionCommand("koteimen_siteiAction");
        koteimen_siteiButton.setText("S_face");
        panel15.add(koteimen_siteiButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel16, new GridConstraints(22, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_02Button = new JButton();
        suitei_02Button.setActionCommand("suitei_02Action");
        suitei_02Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_02.png")));
        panel16.add(suitei_02Button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        suitei_03Button = new JButton();
        suitei_03Button.setActionCommand("suitei_03Action");
        suitei_03Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_03.png")));
        panel16.add(suitei_03Button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayCheckBox = new JCheckBox();
        coloredXRayCheckBox.setText("Colored");
        panel16.add(coloredXRayCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayDecreaseButton = new JButton();
        coloredXRayDecreaseButton.setActionCommand("coloredXRayDecreaseAction");
        coloredXRayDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        panel16.add(coloredXRayDecreaseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coloredXRayIncreaseButton = new JButton();
        coloredXRayIncreaseButton.setActionCommand("coloredXRayIncreaseAction");
        coloredXRayIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        panel16.add(coloredXRayIncreaseButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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

        delTypeDropBox.setSelectedIndex(applicationModel.getDelLineType().getNumber() + 1);
        fromLineDropBox.setSelectedIndex(applicationModel.getCustomFromLineType().getNumber() + 1);

        selectPersistentCheckBox.setSelected(applicationModel.getSelectPersistent());

        // -------- CONTEXT FOR THE BELOW LOGIC --------
        // - The Replace-to dropbox items are Edge, Mountain, Valley, Aux in that order
        // - Dropbox starts at index 0 for first item
        // - CustomLineType enum has Any, Edge, M&V, Mountain, Valley, Aux in that order, starting from -1
        // -------- LOGIC --------
        // - If Edge is selected, update the applicationModel using normal index
        // - If Mountain, Valley, or Aux is selected, update the applicationModel with index - 1 (because those
        // 3 options in CustomLineType are after M&V, therefore each has an extra value increment)
        // - If somehow Any is selected (from manual edit in applicationModel), set its value in applicationModel
        // to CustomLineType.EDGE, and set dropdown's selected index to 0, corresponding to Edge
        switch (applicationModel.getCustomToLineType()) {
            case EGDE:
                toLineDropBox.setSelectedIndex(CustomLineTypes.EGDE.getNumber());
                break;
            case MOUNTAIN:
            case VALLEY:
            case AUX:
                toLineDropBox.setSelectedIndex(applicationModel.getCustomToLineType().getNumber() - 1);
                break;
            case ANY: // Impossible in general case (redundancy)
                applicationModel.setCustomToLineType(CustomLineTypes.EGDE);
                toLineDropBox.setSelectedIndex(CustomLineTypes.EGDE.getNumber());
                break;
            default:
                break;
        }
        // ------ END LOGIC ------

        if (e.getPropertyName() == null || e.getPropertyName().equals("laf")) {
            // The look and feel is not set yet, so it must be read from the applicationModel
            boolean isDark = LookAndFeelUtil.determineLafDark(data.getLaf());

            for (JButton button : Arrays.asList(colBlackButton, colRedButton, colBlueButton, colCyanButton)) {
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
            LineBorder highlight;

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

        Color gray = Colors.get(new Color(150, 150, 150));

        colBlackButton.setBackground(gray);
        colRedButton.setBackground(gray);
        colBlueButton.setBackground(gray);
        colCyanButton.setBackground(gray);
        colBlackButton.setForeground(Colors.get(Color.black));
        colRedButton.setForeground(Colors.get(Color.black));
        colBlueButton.setForeground(Colors.get(Color.black));
        colCyanButton.setForeground(Colors.get(Color.black));

        switch (canvasModel.getMouseMode()) {
            case REPLACE_LINE_TYPE_SELECT_72:
                if (switchReplaceButton.isEnabled()) {
                    if ((canvasModel.getToggleLineColor() && !isReplaceHold) || (!canvasModel.getToggleLineColor() && isReplaceHold)) {
                        CustomLineTypes temp = applicationModel.getCustomFromLineType();

                        applicationModel.setCustomFromLineType(applicationModel.getCustomToLineType());
                        applicationModel.setCustomToLineType(temp);

                        isReplaceHold = !isReplaceHold;
                    }
                }
                break;
            case DELETE_LINE_TYPE_SELECT_73:
                if ((canvasModel.getToggleLineColor() && !isDelTypeHold) || (!canvasModel.getToggleLineColor() && isDelTypeHold)) {
                    if (applicationModel.getDelLineType() == CustomLineTypes.MOUNTAIN) {
                        applicationModel.setDelLineType(CustomLineTypes.VALLEY);
                        isDelTypeHold = !isDelTypeHold;
                    } else if (applicationModel.getDelLineType() == CustomLineTypes.VALLEY) {
                        applicationModel.setDelLineType(CustomLineTypes.MOUNTAIN);
                        isDelTypeHold = !isDelTypeHold;
                    }
                }
                break;
            case AXIOM_5:
            case AXIOM_7:
            case DRAW_CREASE_FREE_1:
            case LENGTHEN_CREASE_5:
            case SQUARE_BISECTOR_7:
            case INWARD_8:
            case PERPENDICULAR_DRAW_9:
            case SYMMETRIC_DRAW_10:
            case DRAW_CREASE_RESTRICTED_11:
            case DRAW_CREASE_ANGLE_RESTRICTED_13:
            case ANGLE_SYSTEM_16:
            case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
            case LINE_SEGMENT_DIVISION_27:
            case LINE_SEGMENT_RATIO_SET_28:
            case POLYGON_SET_NO_CORNERS_29:
            case FISH_BONE_DRAW_33:
            case CREASE_MAKE_MV_34:
            case DOUBLE_SYMMETRIC_DRAW_35:
            case CREASES_ALTERNATE_MV_36:
            case DRAW_CREASE_ANGLE_RESTRICTED_5_37:
            case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
            case FOLDABLE_LINE_INPUT_39:
            case PARALLEL_DRAW_40:
            case CIRCLE_DRAW_TANGENT_LINE_45:
            case PARALLEL_DRAW_WIDTH_51:
            case CONTINUOUS_SYMMETRIC_DRAW_52:
            case VORONOI_CREATE_62:
            case FOLDABLE_LINE_DRAW_71:
                if (canvasModel.getMouseMode() == MouseMode.DRAW_CREASE_FREE_1 && data.getFoldLineAdditionalInputMode() == FoldLineAdditionalInputMode.AUX_LINE_1) {
                    break;
                }

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
                break;
            default:
                break;
        }
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        coloredXRayCheckBox.setSelected(foldedFigureModel.isTransparencyColor());
    }

    public void setData(PropertyChangeEvent e) {
        Logger.info(e.toString());
        if ("isSelectionEmpty".equals(e.getPropertyName())) {
            refreshButtons();
        }
    }

    private void createUIComponents() {
        scrollPane1 = new JScrollPane();
        scrollPane1.getVerticalScrollBar().setUnitIncrement(16);
    }
}
