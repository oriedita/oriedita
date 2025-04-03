package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.MouseModeAction;
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

    private final ButtonService buttonService;
    private final CanvasModel canvasModel;
    private final ApplicationModel applicationModel;

    @Inject
    public ToolsPanel(ButtonService buttonService,
                      CanvasModel canvasModel,
                      ApplicationModel applicationModel) {
        this.buttonService = buttonService;
        this.canvasModel = canvasModel;
        this.applicationModel = applicationModel;
    }


    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());
        setData(applicationModel);
        canvasModel.addPropertyChangeListener(event -> setData(canvasModel));
        applicationModel.addPropertyChangeListener(event -> setData(applicationModel));

        lineDivisionsTextField.addActionListener(e -> {
            getData(applicationModel);
            Logger.info(lineDivisionsTextField.getWidth());
        });
        lineDivisionsTextField.getDocument().addDocumentListener(RegexHighlightFactory.intRegexAdapter(lineDivisionsTextField));
        lineDivisionsTextField.addKeyListener(new InputEnterKeyAdapter(lineDivisionsTextField));
        lineDivisionsTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                getData(applicationModel);
            }
        });

        polygonTextField.addActionListener(e -> {
            getData(applicationModel);
            Logger.info(polygonTextField.getWidth());
        });
        polygonTextField.getDocument().addDocumentListener(RegexHighlightFactory.intRegexAdapter(polygonTextField));
        polygonTextField.addKeyListener(new InputEnterKeyAdapter(polygonTextField));
        polygonTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                getData(applicationModel);
            }
        });
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
                polygonBtn
        })).forEach(button -> {
            if (button.getAction() instanceof MouseModeAction action) {
                button.setSelected(m == action.getMouseMode());
            }
        });


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
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Drawing", panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Draw");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel2.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(polygonTextField, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("References", panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Folding", panel6);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Grid", panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Settings", panel8);
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
    }
}
