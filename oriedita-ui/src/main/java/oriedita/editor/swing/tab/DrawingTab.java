package oriedita.editor.swing.tab;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.MouseModeAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.factory.RegexHighlightFactory;
import oriedita.editor.handler.PopupMenuAdapter;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.InputEnterKeyAdapter;
import oriedita.editor.swing.component.DropdownToolButton;
import oriedita.editor.swing.component.combobox.CustomTextComboBoxRenderer;
import oriedita.editor.tools.StringOp;
import origami.crease_pattern.CustomLineTypes;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

@ApplicationScoped
public class DrawingTab {
    private final ButtonService buttonService;
    private final CanvasModel canvasModel;
    private final ApplicationModel applicationModel;
    private final CreasePattern_Worker mainCreasePatternWorker;

    private JButton drawCreaseFreeBtn;
    private JButton drawCreaseRestrictedBtn;
    private DropdownToolButton angleRestrictedToolsDropdown;
    private JButton lengthenCreaseBtn;
    private JButton rabbitEarBtn;
    private JButton flatfoldVertexBtn;
    private DropdownToolButton perpendicularDropdown;
    private JButton mirroLineBtn;
    private JButton angleBisectorBtn;
    private JButton fishBoneBtn;
    private JButton reflectOverLineBtn;
    private JButton reflectThroughLinesBtn;
    private DropdownToolButton axiomDropdown;
    private JButton voronoiBtn;
    private JButton applyBtn;
    private JButton equallyDividedLineBtn;
    private JTextField lineDivisionsTextField;
    private JButton polygonBtn;
    private JTextField polygonTextField;
    private DropdownToolButton addSelectionDropdown;
    private DropdownToolButton removeSelectionDropdown;
    private DropdownToolButton setSelectionDropdown;
    private JButton mirrorBtn;
    private JButton copyLineBtn;
    private JButton copy4pBtn;
    private JButton moveLineBtn;
    private JButton move4pBtn;
    private JPanel root;
    private JButton eraseBtn;
    private JComboBox<CustomLineTypes> eraserTypeComboBox;
    private DropdownToolButton deleteOnLineDropdown;
    private DropdownToolButton removeVerticesDropdown;
    private JButton addVertexBtn;
    private JButton replaceBtn;
    private JComboBox<CustomLineTypes> replaceFromComboBox;
    private JComboBox<CustomLineTypes> replaceToComboBox;
    private JLabel replaceLabel;
    private JButton switchReplaceBtn;
    private JButton alternateIntersectedBtn;
    private JButton alternateIncludedBtn;
    private DropdownToolButton mvDropdown;

    @Inject
    public DrawingTab(ButtonService buttonService,
                      CanvasModel canvasModel,
                      ApplicationModel applicationModel,
                      @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker) {
        this.buttonService = buttonService;
        this.canvasModel = canvasModel;
        this.applicationModel = applicationModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());
        setData(applicationModel);
        setData(canvasModel);
        canvasModel.addPropertyChangeListener(event -> setData(canvasModel));
        applicationModel.addPropertyChangeListener(event -> setData(applicationModel));

        setupNumberTextField(lineDivisionsTextField, "senbun_b_nyuryokuAction");
        setupNumberTextField(polygonTextField, "regularPolygonAction");
        mainCreasePatternWorker.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("isSelectionEmpty")) {
                updateSelectionTransformButtons();
            }
        });

        eraserTypeComboBox.addActionListener(e ->
                getData(applicationModel));
        replaceToComboBox.addActionListener(e -> getData(applicationModel));
        replaceFromComboBox.addActionListener(e -> getData(applicationModel));
        setupComboBox(eraserTypeComboBox, eraseBtn);
        setupComboBox(replaceFromComboBox, replaceBtn);
        setupComboBox(replaceToComboBox, replaceBtn);
        buttonService.setIcon(replaceLabel, "labelReplace");
    }

    private void setupNumberTextField(JTextField textField, String key) {
        textField.addActionListener(e -> getData(applicationModel));
        textField.getDocument().addDocumentListener(RegexHighlightFactory.intRegexAdapter(textField));
        textField.addKeyListener(new InputEnterKeyAdapter(textField));
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                getData(applicationModel);
            }
        });
        buttonService.registerTextField(textField, key);
    }

    private void setupComboBox(JComboBox<?> comboBox, JButton toolButton) {
        comboBox.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                toolButton.doClick();
            }
        });
        comboBox.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int index = comboBox.getSelectedIndex();
                int itemCount = comboBox.getItemCount();
                if (e.getWheelRotation() > 0) {
                    comboBox.setSelectedIndex((index + 1) % itemCount);
                } else if (e.getWheelRotation() < 0) {
                    comboBox.setSelectedIndex(index != 0 ? (index - 1) % itemCount : itemCount - 1);
                }

                e.consume();
                comboBox.showPopup();
            }
        });
    }

    private void getData(ApplicationModel data) {
        data.setFoldLineDividingNumber(StringOp.String2int(lineDivisionsTextField.getText(), data.getFoldLineDividingNumber()));
        data.setNumPolygonCorners(StringOp.String2int(polygonTextField.getText(), data.getNumPolygonCorners()));

        data.setDelLineType((CustomLineTypes) eraserTypeComboBox.getSelectedItem());
        data.setCustomFromLineType((CustomLineTypes) replaceFromComboBox.getSelectedItem());
        data.setCustomToLineType((CustomLineTypes) replaceToComboBox.getSelectedItem());
    }

    public void setData(ApplicationModel data) {
        lineDivisionsTextField.setText(String.valueOf(data.getFoldLineDividingNumber()));
        polygonTextField.setText(String.valueOf(data.getNumPolygonCorners()));

        if (data.getDelLineType() != eraserTypeComboBox.getSelectedItem()) {
            eraserTypeComboBox.setSelectedItem(data.getDelLineType());
        }
        if (data.getCustomToLineType() != replaceToComboBox.getSelectedItem()) {
            replaceToComboBox.setSelectedItem(data.getCustomToLineType());
        }
        if (data.getCustomFromLineType() != replaceFromComboBox.getSelectedItem()) {
            replaceFromComboBox.setSelectedItem(data.getCustomFromLineType());
        }

        updateSwitchBtn(data, canvasModel);
    }

    private void updateSwitchBtn(ApplicationModel applicationModel, CanvasModel canvasModel) {
        var switchEnabled = canvasModel.getMouseMode() == MouseMode.REPLACE_LINE_TYPE_SELECT_72
                && applicationModel.getCustomFromLineType() != CustomLineTypes.ANY
                && applicationModel.getCustomFromLineType() != CustomLineTypes.MANDV;
        switchReplaceBtn.setEnabled(switchEnabled);
    }

    public void setData(CanvasModel data) {
        MouseMode m = data.getMouseMode();

        Arrays.stream((new JButton[]{
                drawCreaseFreeBtn,
                drawCreaseRestrictedBtn,
                angleRestrictedToolsDropdown,
                lengthenCreaseBtn,
                rabbitEarBtn,
                flatfoldVertexBtn,
                perpendicularDropdown,
                mirroLineBtn,
                angleBisectorBtn,
                fishBoneBtn,
                reflectOverLineBtn,
                reflectThroughLinesBtn,
                axiomDropdown,
                voronoiBtn,
                applyBtn,
                equallyDividedLineBtn,
                polygonBtn,
                addSelectionDropdown,
                removeSelectionDropdown,
                setSelectionDropdown,
                mirrorBtn,
                copyLineBtn,
                copy4pBtn,
                moveLineBtn,
                move4pBtn,
                eraseBtn,
                deleteOnLineDropdown,
                addVertexBtn,
                removeVerticesDropdown,
                replaceBtn,
                switchReplaceBtn,
                alternateIntersectedBtn,
                alternateIncludedBtn,
                mvDropdown
        })).forEach(button -> {
            if (button.getAction() instanceof MouseModeAction action) {
                button.setSelected(m == action.getMouseMode());
            } else {
                button.setSelected(false);
            }

            // The new action of the button is only set after the action is executed, so at this point the button
            // still has the old action and therefore won't be selected in the first if statement.
            if (button instanceof DropdownToolButton dtb && dtb.wasDropdownItemJustSelected()) {
                button.setSelected(true);
            }
        });
        updateSelectionTransformButtons();
        updateSwitchBtn(applicationModel, canvasModel);
    }

    private void updateSelectionTransformButtons() {
        Border defaultBorder = (Border) UIManager.get("Button.border");
        String warningMessage = "";
        var transformBtns = (new JButton[]{
                mirrorBtn,
                copyLineBtn,
                copy4pBtn,
                moveLineBtn,
                move4pBtn
        });
        for (JButton button : transformBtns) {
            button.setBorder(defaultBorder);
            if (mainCreasePatternWorker.getIsSelectionEmpty()) {
                button.setEnabled(false);
                if (button.isSelected()) {
                    var highlight = new LineBorder(Color.yellow);
                    button.setBorder(highlight);
                    warningMessage = "Selection Transformation Tools depend on crease(s) being selected in advance";
                }
            } else {
                button.setEnabled(true);
            }
        }
        canvasModel.setWarningMessage(warningMessage);
    }

    private void createUIComponents() {
        angleRestrictedToolsDropdown = new DropdownToolButton();
        angleRestrictedToolsDropdown.setActions(
                ActionType.deg2Action, ActionType.deg3Action, ActionType.deg1Action
        );
        perpendicularDropdown = new DropdownToolButton();
        perpendicularDropdown.setActions(
                ActionType.perpendicularDrawAction, ActionType.parallelDrawAction
        );
        axiomDropdown = new DropdownToolButton();
        axiomDropdown.setActions(
                ActionType.axiom5Action, ActionType.axiom7Action
        );
        addSelectionDropdown = new DropdownToolButton();
        addSelectionDropdown.setActions(
                ActionType.selectAction, ActionType.select_lXAction, ActionType.select_polygonAction, ActionType.selectLassoAction
        );
        removeSelectionDropdown = new DropdownToolButton();
        removeSelectionDropdown.setActions(
                ActionType.unselectAction, ActionType.unselect_lXAction, ActionType.unselect_polygonAction, ActionType.unselectLassoAction
        );
        setSelectionDropdown = new DropdownToolButton();
        setSelectionDropdown.setActions(
                ActionType.unselectAllAction, ActionType.selectAllAction
        );

        eraserTypeComboBox = new JComboBox<>();
        eraserTypeComboBox.setModel(new DefaultComboBoxModel<>(CustomLineTypes.values()));
        eraserTypeComboBox.setRenderer(new CustomTextComboBoxRenderer<>(l -> switch (l) {
            case ANY -> "Any";
            case EGDE -> "E";
            case MANDV -> "M & V";
            case MOUNTAIN -> "M";
            case VALLEY -> "V";
            case AUX -> "A";
        }));

        deleteOnLineDropdown = new DropdownToolButton();
        deleteOnLineDropdown.setActions(
                ActionType.del_lAction, ActionType.del_l_XAction, ActionType.trimBranchesAction
        );

        removeVerticesDropdown = new DropdownToolButton();
        removeVerticesDropdown.setActions(
                ActionType.v_del_allAction, ActionType.v_del_all_ccAction, ActionType.v_del_ccAction
        );

        replaceFromComboBox = new JComboBox<>();
        replaceFromComboBox.setModel(new DefaultComboBoxModel<>(CustomLineTypes.values()));
        replaceFromComboBox.setRenderer(new CustomTextComboBoxRenderer<>(l -> switch (l) {
            case ANY -> "Any";
            case EGDE -> "E";
            case MANDV -> "M & V";
            case MOUNTAIN -> "M";
            case VALLEY -> "V";
            case AUX -> "A";
        }));

        replaceToComboBox = new JComboBox<>();
        replaceToComboBox.setModel(new DefaultComboBoxModel<>(new CustomLineTypes[]{
                CustomLineTypes.EGDE, CustomLineTypes.MOUNTAIN, CustomLineTypes.VALLEY, CustomLineTypes.AUX
        }));
        replaceToComboBox.setRenderer(new CustomTextComboBoxRenderer<>(l -> switch (l) {
            case ANY -> "Any (error)";
            case EGDE -> "E";
            case MANDV -> "M & V (error)";
            case MOUNTAIN -> "M";
            case VALLEY -> "V";
            case AUX -> "A";
        }));

        mvDropdown = new DropdownToolButton();
        mvDropdown.setActions(
                ActionType.senbun_henkan2Action,
                ActionType.senbun_henkanAction,
                ActionType.zen_yama_tani_henkanAction
        );
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
        createUIComponents();
        root = new JPanel();
        root.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Draw");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel1.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawCreaseFreeBtn = new JButton();
        drawCreaseFreeBtn.setActionCommand("drawCreaseFreeAction");
        drawCreaseFreeBtn.setText("drawCreaseFree");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(drawCreaseFreeBtn, gbc);
        drawCreaseRestrictedBtn = new JButton();
        drawCreaseRestrictedBtn.setActionCommand("drawCreaseRestrictedAction");
        drawCreaseRestrictedBtn.setText("drawCreaseRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(drawCreaseRestrictedBtn, gbc);
        angleRestrictedToolsDropdown.setText("angleRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleRestrictedToolsDropdown, gbc);
        lengthenCreaseBtn = new JButton();
        lengthenCreaseBtn.setActionCommand("lengthenCreaseAction");
        lengthenCreaseBtn.setText("lengthenCrease");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(lengthenCreaseBtn, gbc);
        rabbitEarBtn = new JButton();
        rabbitEarBtn.setActionCommand("rabbitEarAction");
        rabbitEarBtn.setSelected(false);
        rabbitEarBtn.setText("rabbitEar");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(rabbitEarBtn, gbc);
        flatfoldVertexBtn = new JButton();
        flatfoldVertexBtn.setActionCommand("makeFlatFoldableAction");
        flatfoldVertexBtn.setText("flatfoldVertex");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(flatfoldVertexBtn, gbc);
        perpendicularDropdown.setText("perpendicular");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(perpendicularDropdown, gbc);
        mirroLineBtn = new JButton();
        mirroLineBtn.setActionCommand("symmetricDrawAction");
        mirroLineBtn.setText("mirrorLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(mirroLineBtn, gbc);
        angleBisectorBtn = new JButton();
        angleBisectorBtn.setActionCommand("angleBisectorAction");
        angleBisectorBtn.setText("angleBisector");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleBisectorBtn, gbc);
        fishBoneBtn = new JButton();
        fishBoneBtn.setActionCommand("fishBoneDrawAction");
        fishBoneBtn.setText("gridFill");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(fishBoneBtn, gbc);
        reflectOverLineBtn = new JButton();
        reflectOverLineBtn.setActionCommand("doubleSymmetricDrawAction");
        reflectOverLineBtn.setText("reflectOverLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(reflectOverLineBtn, gbc);
        reflectThroughLinesBtn = new JButton();
        reflectThroughLinesBtn.setActionCommand("continuousSymmetricDrawAction");
        reflectThroughLinesBtn.setText("reflectThroughLines");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(reflectThroughLinesBtn, gbc);
        axiomDropdown.setText("axioms");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(axiomDropdown, gbc);
        voronoiBtn = new JButton();
        voronoiBtn.setActionCommand("voronoiAction");
        voronoiBtn.setText("voronoi");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(voronoiBtn, gbc);
        applyBtn = new JButton();
        applyBtn.setActionCommand("all_s_step_to_orisenAction");
        applyBtn.setText("apply");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(applyBtn, gbc);
        equallyDividedLineBtn = new JButton();
        equallyDividedLineBtn.setActionCommand("senbun_b_nyuryokuAction");
        equallyDividedLineBtn.setText("equallyDividedLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(equallyDividedLineBtn, gbc);
        lineDivisionsTextField = new JTextField();
        lineDivisionsTextField.setText("2");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(lineDivisionsTextField, gbc);
        polygonBtn = new JButton();
        polygonBtn.setActionCommand("regularPolygonAction");
        polygonBtn.setText("polygon");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(polygonBtn, gbc);
        polygonTextField = new JTextField();
        polygonTextField.setText("3");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(polygonTextField, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Select");
        panel1.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), new Dimension(-1, 7), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel1.add(panel4, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addSelectionDropdown.setText("addSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(addSelectionDropdown, gbc);
        removeSelectionDropdown.setText("removeSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(removeSelectionDropdown, gbc);
        setSelectionDropdown.setText("setSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(setSelectionDropdown, gbc);
        mirrorBtn = new JButton();
        mirrorBtn.setActionCommand("reflectAction");
        mirrorBtn.setText("mirror");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(mirrorBtn, gbc);
        copyLineBtn = new JButton();
        copyLineBtn.setActionCommand("copyAction");
        copyLineBtn.setText("copyLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(copyLineBtn, gbc);
        copy4pBtn = new JButton();
        copy4pBtn.setActionCommand("copy2p2pAction");
        copy4pBtn.setText("copy4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(copy4pBtn, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(panel5, gbc);
        moveLineBtn = new JButton();
        moveLineBtn.setActionCommand("moveAction");
        moveLineBtn.setText("moveLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(moveLineBtn, gbc);
        move4pBtn = new JButton();
        move4pBtn.setActionCommand("move2p2pAction");
        move4pBtn.setText("move4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(move4pBtn, gbc);
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Edit");
        panel1.add(label3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        panel1.add(panel6, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel7, gbc);
        eraseBtn = new JButton();
        eraseBtn.setActionCommand("del_l_typeAction");
        eraseBtn.setText("erase");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(eraseBtn, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(eraserTypeComboBox, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel8, gbc);
        deleteOnLineDropdown.setText("deleteOnLine");
        deleteOnLineDropdown.setToolTipText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(deleteOnLineDropdown, gbc);
        removeVerticesDropdown.setText("removeVertices");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(removeVerticesDropdown, gbc);
        addVertexBtn = new JButton();
        addVertexBtn.setActionCommand("vertexAddAction");
        addVertexBtn.setText("addVertex");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(addVertexBtn, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel9, gbc);
        replaceBtn = new JButton();
        replaceBtn.setActionCommand("replace_lineAction");
        replaceBtn.setText("replace");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(replaceBtn, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(replaceFromComboBox, gbc);
        replaceLabel = new JLabel();
        replaceLabel.setText("->");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(replaceLabel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(replaceToComboBox, gbc);
        switchReplaceBtn = new JButton();
        switchReplaceBtn.setActionCommand("switchReplaceAction");
        switchReplaceBtn.setText("⇆");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(switchReplaceBtn, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel10, gbc);
        alternateIntersectedBtn = new JButton();
        alternateIntersectedBtn.setActionCommand("on_L_col_changeAction");
        alternateIntersectedBtn.setText("alternateIntersected");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel10.add(alternateIntersectedBtn, gbc);
        alternateIncludedBtn = new JButton();
        alternateIncludedBtn.setActionCommand("in_L_col_changeAction");
        alternateIncludedBtn.setText("alternateIncluded");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel10.add(alternateIncludedBtn, gbc);
        mvDropdown.setText("invertMv");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel10.add(mvDropdown, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
