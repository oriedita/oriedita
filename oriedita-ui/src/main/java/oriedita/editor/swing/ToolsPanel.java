package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.MouseModeAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.factory.RegexHighlightFactory;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.DropdownToolButton;
import oriedita.editor.tools.StringOp;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

@ApplicationScoped
public class ToolsPanel {
    private JTabbedPane tabbedPane1;
    private JPanel root;
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

    private final ButtonService buttonService;
    private final CanvasModel canvasModel;
    private final ApplicationModel applicationModel;
    private final CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public ToolsPanel(ButtonService buttonService,
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
    }

    private void setupNumberTextField(JTextField textField, String key) {
        textField.addActionListener(e -> {
            getData(applicationModel);
            Logger.info(textField.getWidth());
        });
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

    private void getData(ApplicationModel data) {
        data.setFoldLineDividingNumber(StringOp.String2int(lineDivisionsTextField.getText(), data.getFoldLineDividingNumber()));
        data.setNumPolygonCorners(StringOp.String2int(polygonTextField.getText(), data.getNumPolygonCorners()));
    }

    public void setData(ApplicationModel data) {
        lineDivisionsTextField.setText(String.valueOf(data.getFoldLineDividingNumber()));
        polygonTextField.setText(String.valueOf(data.getNumPolygonCorners()));
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
                move4pBtn
        })).forEach(button -> {
            if (button.getAction() instanceof MouseModeAction action) {
                button.setSelected(m == action.getMouseMode());
            }
        });
        updateSelectionTransformButtons();
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
        root.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        root.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel1);
        tabbedPane1 = new JTabbedPane();
        panel1.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Drawing", panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Draw");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel2.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawCreaseFreeBtn = new JButton();
        drawCreaseFreeBtn.setActionCommand("drawCreaseFreeAction");
        drawCreaseFreeBtn.setText("drawCreaseFree");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(drawCreaseFreeBtn, gbc);
        drawCreaseRestrictedBtn = new JButton();
        drawCreaseRestrictedBtn.setActionCommand("drawCreaseRestrictedAction");
        drawCreaseRestrictedBtn.setText("drawCreaseRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(drawCreaseRestrictedBtn, gbc);
        angleRestrictedToolsDropdown.setText("angleRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(angleRestrictedToolsDropdown, gbc);
        lengthenCreaseBtn = new JButton();
        lengthenCreaseBtn.setActionCommand("lengthenCreaseAction");
        lengthenCreaseBtn.setText("lengthenCrease");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(lengthenCreaseBtn, gbc);
        rabbitEarBtn = new JButton();
        rabbitEarBtn.setActionCommand("rabbitEarAction");
        rabbitEarBtn.setSelected(false);
        rabbitEarBtn.setText("rabbitEar");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(rabbitEarBtn, gbc);
        flatfoldVertexBtn = new JButton();
        flatfoldVertexBtn.setActionCommand("makeFlatFoldableAction");
        flatfoldVertexBtn.setText("flatfoldVertex");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(flatfoldVertexBtn, gbc);
        perpendicularDropdown.setText("perpendicular");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(perpendicularDropdown, gbc);
        mirroLineBtn = new JButton();
        mirroLineBtn.setActionCommand("symmetricDrawAction");
        mirroLineBtn.setText("mirrorLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(mirroLineBtn, gbc);
        angleBisectorBtn = new JButton();
        angleBisectorBtn.setActionCommand("angleBisectorAction");
        angleBisectorBtn.setText("angleBisector");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(angleBisectorBtn, gbc);
        fishBoneBtn = new JButton();
        fishBoneBtn.setActionCommand("fishBoneDrawAction");
        fishBoneBtn.setText("gridFill");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(fishBoneBtn, gbc);
        reflectOverLineBtn = new JButton();
        reflectOverLineBtn.setActionCommand("doubleSymmetricDrawAction");
        reflectOverLineBtn.setText("reflectOverLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(reflectOverLineBtn, gbc);
        reflectThroughLinesBtn = new JButton();
        reflectThroughLinesBtn.setActionCommand("continuousSymmetricDrawAction");
        reflectThroughLinesBtn.setText("reflectThroughLines");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(reflectThroughLinesBtn, gbc);
        axiomDropdown.setText("axioms");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(axiomDropdown, gbc);
        voronoiBtn = new JButton();
        voronoiBtn.setActionCommand("voronoiAction");
        voronoiBtn.setText("voronoi");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(voronoiBtn, gbc);
        applyBtn = new JButton();
        applyBtn.setActionCommand("all_s_step_to_orisenAction");
        applyBtn.setText("apply");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(applyBtn, gbc);
        equallyDividedLineBtn = new JButton();
        equallyDividedLineBtn.setActionCommand("senbun_b_nyuryokuAction");
        equallyDividedLineBtn.setText("equallyDividedLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(equallyDividedLineBtn, gbc);
        lineDivisionsTextField = new JTextField();
        lineDivisionsTextField.setText("2");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(lineDivisionsTextField, gbc);
        polygonBtn = new JButton();
        polygonBtn.setActionCommand("regularPolygonAction");
        polygonBtn.setText("polygon");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(polygonBtn, gbc);
        polygonTextField = new JTextField();
        polygonTextField.setText("3");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(polygonTextField, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Select");
        panel2.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 5), new Dimension(-1, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 7), new Dimension(-1, 7), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel2.add(panel5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addSelectionDropdown.setText("addSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(addSelectionDropdown, gbc);
        removeSelectionDropdown.setText("removeSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(removeSelectionDropdown, gbc);
        setSelectionDropdown.setText("setSelection");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(setSelectionDropdown, gbc);
        mirrorBtn = new JButton();
        mirrorBtn.setActionCommand("reflectAction");
        mirrorBtn.setText("mirror");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(mirrorBtn, gbc);
        copyLineBtn = new JButton();
        copyLineBtn.setActionCommand("copyAction");
        copyLineBtn.setText("copyLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(copyLineBtn, gbc);
        copy4pBtn = new JButton();
        copy4pBtn.setActionCommand("copy2p2pAction");
        copy4pBtn.setText("copy4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(copy4pBtn, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(panel6, gbc);
        moveLineBtn = new JButton();
        moveLineBtn.setActionCommand("moveAction");
        moveLineBtn.setText("moveLine");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(moveLineBtn, gbc);
        move4pBtn = new JButton();
        move4pBtn.setActionCommand("move2p2pAction");
        move4pBtn.setText("move4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(move4pBtn, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("References", panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Folding", panel8);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Grid", panel9);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Settings", panel10);
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
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
    }
}
