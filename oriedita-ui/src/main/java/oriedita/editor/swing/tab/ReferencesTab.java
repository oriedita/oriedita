package oriedita.editor.swing.tab;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.Colors;
import oriedita.editor.action.ActionType;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.TextFieldTempPopupAdapter;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.DropdownToolButton;
import origami.crease_pattern.element.LineColor;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@ApplicationScoped
public class ReferencesTab {
    private final ButtonService buttonService;
    private final ApplicationModel applicationModel;
    private final CanvasModel canvasModel;
    private final MeasuresModel measuresModel;

    private JPanel root;
    private JButton circleButton;
    private DropdownToolButton restrictedCircleDropdown;
    private DropdownToolButton concentricCircleDropdown;
    private JButton a3pointCircleButton;
    private JButton tangentButton;
    private JButton invertButton;
    private JButton circleColorButton;
    private JButton circleColorToolButton;
    private JButton markerYellowBtn;
    private JButton markerOrangeBtn;
    private JButton markerToolButton;
    private JButton markerEraseButton;
    private JButton measureLength1Btn;
    private JButton measureLength2Btn;
    private JButton measureAngle1Btn;
    private JButton measureAngle2Btn;
    private JButton measureAngle3Btn;
    private JTextField measureLength1TextField;
    private JTextField measureLength2TextField;
    private JTextField measureAngle1TextField;
    private JTextField measureAngle2TextField;
    private JTextField measureAngle3TextField;
    private JButton textButton;

    @Inject
    public ReferencesTab(ButtonService buttonService,
                         ApplicationModel applicationModel,
                         CanvasModel canvasModel,
                         MeasuresModel measuresModel) {
        this.buttonService = buttonService;
        this.applicationModel = applicationModel;
        this.canvasModel = canvasModel;
        this.measuresModel = measuresModel;
    }

    public void init() {
        restrictedCircleDropdown.setActions(
                ActionType.circleDrawAction, ActionType.circleDrawSeparateAction
        );
        concentricCircleDropdown.setActions(
                ActionType.circleDrawConcentricAction, ActionType.circleDrawTwoConcentricAction, ActionType.circleDrawConcentricSelectAction
        );
        buttonService.addDefaultListener($$$getRootComponent$$$());

        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));
        canvasModel.addPropertyChangeListener(e -> setData(canvasModel));

        circleColorButton.addActionListener(e -> circleColorToolButton.doClick());

        measuresModel.bind(measureLength1TextField, "measuredLength1");
        measuresModel.bind(measureLength2TextField, "measuredLength2");
        measuresModel.bind(measureAngle1TextField, "measuredAngle1");
        measuresModel.bind(measureAngle2TextField, "measuredAngle2");
        measuresModel.bind(measureAngle3TextField, "measuredAngle3");
        measureLength1TextField.addMouseListener(new TextFieldTempPopupAdapter(measureLength1TextField, "Copied"));
        measureLength2TextField.addMouseListener(new TextFieldTempPopupAdapter(measureLength2TextField, "Copied"));
        measureAngle1TextField.addMouseListener(new TextFieldTempPopupAdapter(measureAngle1TextField, "Copied"));
        measureAngle2TextField.addMouseListener(new TextFieldTempPopupAdapter(measureAngle2TextField, "Copied"));
        measureAngle3TextField.addMouseListener(new TextFieldTempPopupAdapter(measureAngle3TextField, "Copied"));
    }

    private void setData(CanvasModel canvasModel) {
        markerYellowBtn.setBackground(
                canvasModel.calculateAuxColor() == LineColor.YELLOW_7 ?
                        Colors.get(Color.YELLOW) :
                        Color.GRAY
        );
        markerYellowBtn.setForeground(Color.BLACK);
        markerOrangeBtn.setBackground(
                canvasModel.calculateAuxColor() == LineColor.ORANGE_4 ?
                        Colors.get(Color.ORANGE) :
                        Color.GRAY
        );
        markerOrangeBtn.setForeground(Color.BLACK);
    }

    private void setData(ApplicationModel applicationModel) {
        circleColorButton.setIcon(new ColorIcon(applicationModel.getCircleCustomizedColor()));
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
        root.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.setMinimumSize(new Dimension(100, 759));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Circles");
        panel2.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel2.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        circleButton = new JButton();
        circleButton.setActionCommand("circleDrawFreeAction");
        circleButton.setText("circle");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(circleButton, gbc);
        restrictedCircleDropdown = new DropdownToolButton();
        restrictedCircleDropdown.setText("restrictedCircle");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(restrictedCircleDropdown, gbc);
        concentricCircleDropdown = new DropdownToolButton();
        concentricCircleDropdown.setText("concentricCircle");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(concentricCircleDropdown, gbc);
        a3pointCircleButton = new JButton();
        a3pointCircleButton.setActionCommand("circleDrawThreePointAction");
        a3pointCircleButton.setText("3pointCircle");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(a3pointCircleButton, gbc);
        tangentButton = new JButton();
        tangentButton.setActionCommand("circleDrawTangentLineAction");
        tangentButton.setText("tangent");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(tangentButton, gbc);
        invertButton = new JButton();
        invertButton.setActionCommand("circleDrawInvertedAction");
        invertButton.setText("invert");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(invertButton, gbc);
        circleColorToolButton = new JButton();
        circleColorToolButton.setActionCommand("sen_tokutyuu_color_henkouAction");
        circleColorToolButton.setText("circleColorTool");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(circleColorToolButton, gbc);
        circleColorButton = new JButton();
        circleColorButton.setActionCommand("c_colAction");
        circleColorButton.setText("Aux Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(circleColorButton, gbc);
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 7), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Marker Line");
        panel4.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 7), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel4.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        markerYellowBtn = new JButton();
        markerYellowBtn.setActionCommand("colYellowAction");
        markerYellowBtn.setText("a1");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(markerYellowBtn, gbc);
        markerOrangeBtn = new JButton();
        markerOrangeBtn.setActionCommand("colOrangeAction");
        markerOrangeBtn.setText("a2");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(markerOrangeBtn, gbc);
        markerEraseButton = new JButton();
        markerEraseButton.setActionCommand("h_senbun_sakujyoAction");
        markerEraseButton.setText("markerErase");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(markerEraseButton, gbc);
        markerToolButton = new JButton();
        markerToolButton.setActionCommand("h_senbun_nyuryokuAction");
        markerToolButton.setText("markerTool");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(markerToolButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Measure");
        panel6.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel6.add(spacer4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 7), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        panel6.add(panel7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        measureLength1Btn = new JButton();
        measureLength1Btn.setActionCommand("l1Action");
        measureLength1Btn.setText("L1=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel7.add(measureLength1Btn, gbc);
        measureLength1TextField = new JTextField();
        measureLength1TextField.setEditable(false);
        measureLength1TextField.setMinimumSize(new Dimension(-1, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureLength1TextField, gbc);
        measureLength2Btn = new JButton();
        measureLength2Btn.setActionCommand("l2Action");
        measureLength2Btn.setText("L2=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureLength2Btn, gbc);
        measureLength2TextField = new JTextField();
        measureLength2TextField.setEditable(false);
        measureLength2TextField.setMinimumSize(new Dimension(-1, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureLength2TextField, gbc);
        measureAngle1Btn = new JButton();
        measureAngle1Btn.setActionCommand("a1Action");
        measureAngle1Btn.setText("A1=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureAngle1Btn, gbc);
        measureAngle1TextField = new JTextField();
        measureAngle1TextField.setEditable(false);
        measureAngle1TextField.setMinimumSize(new Dimension(-1, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureAngle1TextField, gbc);
        measureAngle2Btn = new JButton();
        measureAngle2Btn.setActionCommand("a2Action");
        measureAngle2Btn.setText("A2=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureAngle2Btn, gbc);
        measureAngle2TextField = new JTextField();
        measureAngle2TextField.setEditable(false);
        measureAngle2TextField.setMinimumSize(new Dimension(-1, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureAngle2TextField, gbc);
        measureAngle3Btn = new JButton();
        measureAngle3Btn.setActionCommand("a3Action");
        measureAngle3Btn.setText("A3=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureAngle3Btn, gbc);
        measureAngle3TextField = new JTextField();
        measureAngle3TextField.setEditable(false);
        measureAngle3TextField.setMinimumSize(new Dimension(-1, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(measureAngle3TextField, gbc);
        textButton = new JButton();
        textButton.setActionCommand("textAction");
        textButton.setText("Text");
        panel6.add(textButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 25), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
