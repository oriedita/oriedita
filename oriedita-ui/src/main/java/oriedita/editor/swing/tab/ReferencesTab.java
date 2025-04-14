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
import oriedita.editor.databinding.InternalDivisionRatioModel;
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
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Locale;

@ApplicationScoped
public class ReferencesTab {
    private final ButtonService buttonService;
    private final AngleSystemModel angleSystemModel;
    private final InternalDivisionRatioModel internalDivisionRatioModel;
    private final ApplicationModel applicationModel;
    private final CanvasModel canvasModel;
    private final MeasuresModel measuresModel;

    private JPanel root;
    private JButton angleSystemBtn;
    private JButton angleSystemIncreaseBtn;
    private JButton angleSystemDecreaseBtn;
    private JTextField angleATextField;
    private JTextField angleBTextField;
    private JTextField angleCTextField;
    private JButton useCustomAnglesBtn;
    private JButton angleRestricedBtn;
    private JButton offsetRestrictedBtn;
    private JButton convergingBtn;
    private JButton ratioBtn;
    private JLabel ratioLabel1;
    private JLabel ratioLabel2;
    private JLabel ratioLabel3;
    private JLabel ratioLabel4;
    private JTextField ratio1TextField;
    private JTextField ratio2TextField;
    private JTextField ratio3TextField;
    private JTextField ratio4TextField;
    private JTextField ratio5TextField;
    private JTextField ratio6TextField;
    private JButton circleButton;
    private DropdownToolButton restrictedCircleDropdown;
    private DropdownToolButton concentricCircleDropdown;
    private JButton a3pointCircleButton;
    private JButton tangentButton;
    private JButton invertButton;
    private JButton circleColorButton;
    private JButton circleColorToolButton;
    private JPanel angleDividerPanel;
    private JPanel customAnglePanel;
    private JButton markerYellowBtn;
    private JButton markerOrangeBtn;
    private JButton markerToolButton;
    private JButton markerRedoButton;
    private JButton markerUndoButton;
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
                         AngleSystemModel angleSystemModel,
                         InternalDivisionRatioModel internalDivisionRatioModel,
                         ApplicationModel applicationModel,
                         CanvasModel canvasModel,
                         MeasuresModel measuresModel) {
        this.buttonService = buttonService;
        this.angleSystemModel = angleSystemModel;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
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

        angleSystemModel.addPropertyChangeListener(e -> setData(angleSystemModel));
        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));
        canvasModel.addPropertyChangeListener(e -> setData(canvasModel));

        var customAngleFocusLost = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                angleSystemModel.setCurrentABC();
            }
        };
        angleSystemModel.bind(angleATextField, "angleA");
        angleSystemModel.bind(angleBTextField, "angleB");
        angleSystemModel.bind(angleCTextField, "angleC");
        angleATextField.addFocusListener(customAngleFocusLost);
        angleBTextField.addFocusListener(customAngleFocusLost);
        angleCTextField.addFocusListener(customAngleFocusLost);
        buttonService.registerTextField(angleATextField, ActionType.restrictedAngleABCSetAction.action());
        buttonService.registerTextField(angleBTextField, ActionType.restrictedAngleABCSetAction.action());
        buttonService.registerTextField(angleCTextField, ActionType.restrictedAngleABCSetAction.action());

        internalDivisionRatioModel.bind(ratio1TextField, "internalDivisionRatioA");
        internalDivisionRatioModel.bind(ratio2TextField, "internalDivisionRatioB");
        internalDivisionRatioModel.bind(ratio3TextField, "internalDivisionRatioC");
        internalDivisionRatioModel.bind(ratio4TextField, "internalDivisionRatioD");
        internalDivisionRatioModel.bind(ratio5TextField, "internalDivisionRatioE");
        internalDivisionRatioModel.bind(ratio6TextField, "internalDivisionRatioF");
        buttonService.registerTextField(ratio1TextField, ActionType.lineSegmentInternalDivisionRatioSetAction.action());
        buttonService.registerTextField(ratio2TextField, ActionType.lineSegmentInternalDivisionRatioSetAction.action());
        buttonService.registerTextField(ratio3TextField, ActionType.lineSegmentInternalDivisionRatioSetAction.action());
        buttonService.registerTextField(ratio4TextField, ActionType.lineSegmentInternalDivisionRatioSetAction.action());
        buttonService.registerTextField(ratio5TextField, ActionType.lineSegmentInternalDivisionRatioSetAction.action());
        buttonService.registerTextField(ratio6TextField, ActionType.lineSegmentInternalDivisionRatioSetAction.action());

        buttonService.setIcon(ratioLabel1, "labelPlus");
        buttonService.setIcon(ratioLabel2, "labelSqrt");
        buttonService.setIcon(ratioLabel3, "labelPlus");
        buttonService.setIcon(ratioLabel4, "labelSqrt");

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
        setData(angleSystemModel); // update colors
    }

    public void setData(AngleSystemModel angleSystemModel) {
        angleSystemBtn.setText(angleSystemModel.getAngleSystemADescription());
        angleDividerPanel.setBorder(new LineBorder(
                angleSystemModel.getCurrentAngleSystemDivider() == 0 ?
                        new Color(0, 0, 0, 0) :
                        Colors.get(Colors.SELECTED_ANGLE_SYSTEM), 2));
        customAnglePanel.setBorder(new LineBorder(
                angleSystemModel.getCurrentAngleSystemDivider() != 0 ?
                        new Color(0, 0, 0, 0) :
                        Colors.get(Colors.SELECTED_ANGLE_SYSTEM), 2));
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
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        angleDividerPanel = new JPanel();
        angleDividerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(angleDividerPanel, gbc);
        angleSystemBtn = new JButton();
        angleSystemBtn.setActionCommand("angleSystemAAction");
        angleSystemBtn.setText("180/8 = 22.5");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        angleDividerPanel.add(angleSystemBtn, gbc);
        angleSystemIncreaseBtn = new JButton();
        angleSystemIncreaseBtn.setActionCommand("angleSystemAIncreaseAction");
        angleSystemIncreaseBtn.setText("increase");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        angleDividerPanel.add(angleSystemIncreaseBtn, gbc);
        angleSystemDecreaseBtn = new JButton();
        angleSystemDecreaseBtn.setActionCommand("angleSystemADecreaseAction");
        angleSystemDecreaseBtn.setText("decrease");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        angleDividerPanel.add(angleSystemDecreaseBtn, gbc);
        customAnglePanel = new JPanel();
        customAnglePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        panel2.add(customAnglePanel, gbc);
        angleATextField = new JTextField();
        angleATextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        customAnglePanel.add(angleATextField, gbc);
        angleBTextField = new JTextField();
        angleBTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        customAnglePanel.add(angleBTextField, gbc);
        angleCTextField = new JTextField();
        angleCTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        customAnglePanel.add(angleCTextField, gbc);
        useCustomAnglesBtn = new JButton();
        useCustomAnglesBtn.setActionCommand("restrictedAngleABCSetAction");
        Font useCustomAnglesBtnFont = this.$$$getFont$$$(null, -1, -1, useCustomAnglesBtn.getFont());
        if (useCustomAnglesBtnFont != null) useCustomAnglesBtn.setFont(useCustomAnglesBtnFont);
        useCustomAnglesBtn.setHideActionText(false);
        useCustomAnglesBtn.setPreferredSize(new Dimension(78, 30));
        useCustomAnglesBtn.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        customAnglePanel.add(useCustomAnglesBtn, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        angleRestricedBtn = new JButton();
        angleRestricedBtn.setActionCommand("deg2Action");
        angleRestricedBtn.setText("angleRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleRestricedBtn, gbc);
        offsetRestrictedBtn = new JButton();
        offsetRestrictedBtn.setActionCommand("deg3Action");
        offsetRestrictedBtn.setText("offsetRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(offsetRestrictedBtn, gbc);
        convergingBtn = new JButton();
        convergingBtn.setActionCommand("deg1Action");
        convergingBtn.setText("converging");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(convergingBtn, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel4, gbc);
        ratio1TextField = new JTextField();
        ratio1TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(ratio1TextField, gbc);
        ratioLabel1 = new JLabel();
        ratioLabel1.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(ratioLabel1, gbc);
        ratio2TextField = new JTextField();
        ratio2TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(ratio2TextField, gbc);
        ratioLabel2 = new JLabel();
        ratioLabel2.setText("sqrt");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(ratioLabel2, gbc);
        ratio3TextField = new JTextField();
        ratio3TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(ratio3TextField, gbc);
        ratioBtn = new JButton();
        ratioBtn.setActionCommand("drawLineSegmentInternalDivisionRatioAction");
        ratioBtn.setText("ratio");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(ratioBtn, gbc);
        ratio4TextField = new JTextField();
        ratio4TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(ratio4TextField, gbc);
        ratioLabel3 = new JLabel();
        ratioLabel3.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(ratioLabel3, gbc);
        ratio5TextField = new JTextField();
        ratio5TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(ratio5TextField, gbc);
        ratioLabel4 = new JLabel();
        ratioLabel4.setText("sqrt");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(ratioLabel4, gbc);
        ratio6TextField = new JTextField();
        ratio6TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(ratio6TextField, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel4.add(spacer2, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Angles and References");
        panel1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 7), null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Circles");
        panel5.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        panel5.add(panel6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        circleButton = new JButton();
        circleButton.setActionCommand("circleDrawFreeAction");
        circleButton.setText("circle");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(circleButton, gbc);
        restrictedCircleDropdown = new DropdownToolButton();
        restrictedCircleDropdown.setText("restrictedCircle");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(restrictedCircleDropdown, gbc);
        concentricCircleDropdown = new DropdownToolButton();
        concentricCircleDropdown.setText("concentricCircle");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(concentricCircleDropdown, gbc);
        a3pointCircleButton = new JButton();
        a3pointCircleButton.setActionCommand("circleDrawThreePointAction");
        a3pointCircleButton.setText("3pointCircle");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(a3pointCircleButton, gbc);
        tangentButton = new JButton();
        tangentButton.setActionCommand("circleDrawTangentLineAction");
        tangentButton.setText("tangent");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(tangentButton, gbc);
        invertButton = new JButton();
        invertButton.setActionCommand("circleDrawInvertedAction");
        invertButton.setText("invert");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(invertButton, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel7, gbc);
        circleColorButton = new JButton();
        circleColorButton.setActionCommand("c_colAction");
        circleColorButton.setText("Aux Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(circleColorButton, gbc);
        circleColorToolButton = new JButton();
        circleColorToolButton.setActionCommand("sen_tokutyuu_color_henkouAction");
        circleColorToolButton.setText("circleColorTool");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(circleColorToolButton, gbc);
        final Spacer spacer4 = new Spacer();
        panel5.add(spacer4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 7), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Marker Line");
        panel8.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel8.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 7), null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        panel8.add(panel9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        markerYellowBtn = new JButton();
        markerYellowBtn.setActionCommand("colYellowAction");
        markerYellowBtn.setText("a1");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(markerYellowBtn, gbc);
        markerOrangeBtn = new JButton();
        markerOrangeBtn.setActionCommand("colOrangeAction");
        markerOrangeBtn.setText("a2");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(markerOrangeBtn, gbc);
        markerToolButton = new JButton();
        markerToolButton.setActionCommand("h_senbun_nyuryokuAction");
        markerToolButton.setText("markerTool");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(markerToolButton, gbc);
        markerRedoButton = new JButton();
        markerRedoButton.setActionCommand("h_redoAction");
        markerRedoButton.setText("markerRedo");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(markerRedoButton, gbc);
        markerUndoButton = new JButton();
        markerUndoButton.setActionCommand("h_undoAction");
        markerUndoButton.setText("markerUndo");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(markerUndoButton, gbc);
        markerEraseButton = new JButton();
        markerEraseButton.setActionCommand("h_senbun_sakujyoAction");
        markerEraseButton.setText("markerErase");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(markerEraseButton, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel10, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Measure");
        panel10.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel10.add(spacer6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 7), null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        panel10.add(panel11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        measureLength1Btn = new JButton();
        measureLength1Btn.setActionCommand("l1Action");
        measureLength1Btn.setText("L1=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        panel11.add(measureLength1Btn, gbc);
        measureLength1TextField = new JTextField();
        measureLength1TextField.setEditable(false);
        measureLength1TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureLength1TextField, gbc);
        measureLength2Btn = new JButton();
        measureLength2Btn.setActionCommand("l2Action");
        measureLength2Btn.setText("L2=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureLength2Btn, gbc);
        measureLength2TextField = new JTextField();
        measureLength2TextField.setEditable(false);
        measureLength2TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureLength2TextField, gbc);
        measureAngle1Btn = new JButton();
        measureAngle1Btn.setActionCommand("a1Action");
        measureAngle1Btn.setText("A1=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureAngle1Btn, gbc);
        measureAngle1TextField = new JTextField();
        measureAngle1TextField.setEditable(false);
        measureAngle1TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureAngle1TextField, gbc);
        measureAngle2Btn = new JButton();
        measureAngle2Btn.setActionCommand("a2Action");
        measureAngle2Btn.setText("A2=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureAngle2Btn, gbc);
        measureAngle2TextField = new JTextField();
        measureAngle2TextField.setEditable(false);
        measureAngle2TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureAngle2TextField, gbc);
        measureAngle3Btn = new JButton();
        measureAngle3Btn.setActionCommand("a3Action");
        measureAngle3Btn.setText("A3=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureAngle3Btn, gbc);
        measureAngle3TextField = new JTextField();
        measureAngle3TextField.setEditable(false);
        measureAngle3TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(measureAngle3TextField, gbc);
        textButton = new JButton();
        textButton.setActionCommand("textAction");
        textButton.setText("Text");
        panel10.add(textButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
