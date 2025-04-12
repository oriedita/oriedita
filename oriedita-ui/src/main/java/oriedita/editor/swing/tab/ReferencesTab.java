package oriedita.editor.swing.tab;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.common.converter.DoubleConverter;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.InternalDivisionRatioModel;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.factory.RegexHighlightFactory;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.InputEnterKeyAdapter;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Locale;
import java.util.function.Consumer;

@ApplicationScoped
public class ReferencesTab {
    private final ButtonService buttonService;
    private final AngleSystemModel angleSystemModel;
    private final MeasuresModel measuresModel;
    private final InternalDivisionRatioModel internalDivisionRatioModel;

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

    @Inject
    public ReferencesTab(ButtonService buttonService,
                         AngleSystemModel angleSystemModel,
                         MeasuresModel measuresModel,
                         InternalDivisionRatioModel internalDivisionRatioModel) {
        this.buttonService = buttonService;
        this.angleSystemModel = angleSystemModel;
        this.measuresModel = measuresModel;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
    }

    public void init() {
        buttonService.addDefaultListener($$$getRootComponent$$$());
        angleSystemModel.addPropertyChangeListener(e -> setData(angleSystemModel));

        ActionListener customAnglesListener = e -> useCustomAnglesBtn.doClick();
        Consumer<FocusEvent> customAngleFocusLost = e -> {
            getData(angleSystemModel);
            angleSystemModel.setCurrentABC();
        };
        initTextField(angleATextField, customAnglesListener, customAngleFocusLost);
        initTextField(angleBTextField, customAnglesListener, customAngleFocusLost);
        initTextField(angleCTextField, customAnglesListener, customAngleFocusLost);

        internalDivisionRatioModel.bind(ratio1TextField, "internalDivisionRatioA", new DoubleConverter());
        internalDivisionRatioModel.bind(ratio2TextField, "internalDivisionRatioB", new DoubleConverter());
        internalDivisionRatioModel.bind(ratio3TextField, "internalDivisionRatioC", new DoubleConverter());
        internalDivisionRatioModel.bind(ratio4TextField, "internalDivisionRatioD", new DoubleConverter());
        internalDivisionRatioModel.bind(ratio5TextField, "internalDivisionRatioE", new DoubleConverter());
        internalDivisionRatioModel.bind(ratio6TextField, "internalDivisionRatioF", new DoubleConverter());

        buttonService.setIcon(ratioLabel1, "labelPlus");
        buttonService.setIcon(ratioLabel2, "labelSqrt");
        buttonService.setIcon(ratioLabel3, "labelPlus");
        buttonService.setIcon(ratioLabel4, "labelSqrt");
    }

    private void initTextField(JTextField textField, ActionListener l, Consumer<FocusEvent> focusLost) {
        textField.addActionListener(l);
        textField.getDocument().addDocumentListener(RegexHighlightFactory.doubleRegexAdapter(textField));
        textField.addKeyListener(new InputEnterKeyAdapter(textField));
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                focusLost.accept(e);
            }
        });
    }

    public void getData(AngleSystemModel data) {
        data.setAngleA(measuresModel.string2double(angleATextField.getText(), data.getAngleA()));
        data.setAngleB(measuresModel.string2double(angleBTextField.getText(), data.getAngleB()));
        data.setAngleC(measuresModel.string2double(angleCTextField.getText(), data.getAngleC()));
    }

    public void setData(AngleSystemModel angleSystemModel) {
        angleATextField.setText(String.valueOf(angleSystemModel.getAngleA()));
        angleBTextField.setText(String.valueOf(angleSystemModel.getAngleB()));
        angleCTextField.setText(String.valueOf(angleSystemModel.getAngleC()));

        angleSystemBtn.setText(angleSystemModel.getAngleSystemADescription());
    }

    public void getData(InternalDivisionRatioModel data) {
        //data.setInternalDivisionRatioA(measuresModel.string2double(ratio1TextField.getText(), data.getInternalDivisionRatioA()));
        data.setInternalDivisionRatioB(measuresModel.string2double(ratio2TextField.getText(), data.getInternalDivisionRatioB()));
        data.setInternalDivisionRatioC(measuresModel.string2double(ratio3TextField.getText(), data.getInternalDivisionRatioC()));
        data.setInternalDivisionRatioD(measuresModel.string2double(ratio4TextField.getText(), data.getInternalDivisionRatioD()));
        data.setInternalDivisionRatioE(measuresModel.string2double(ratio5TextField.getText(), data.getInternalDivisionRatioE()));
        data.setInternalDivisionRatioF(measuresModel.string2double(ratio6TextField.getText(), data.getInternalDivisionRatioF()));
    }

    public void setData(InternalDivisionRatioModel data) {
        //ratio1TextField.setText(String.valueOf(data.getInternalDivisionRatioA()));
        ratio2TextField.setText(String.valueOf(data.getInternalDivisionRatioB()));
        ratio3TextField.setText(String.valueOf(data.getInternalDivisionRatioC()));
        ratio4TextField.setText(String.valueOf(data.getInternalDivisionRatioD()));
        ratio5TextField.setText(String.valueOf(data.getInternalDivisionRatioE()));
        ratio6TextField.setText(String.valueOf(data.getInternalDivisionRatioF()));
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
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        angleSystemBtn = new JButton();
        angleSystemBtn.setActionCommand("angleSystemAAction");
        angleSystemBtn.setText("180/8 = 22.5");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(angleSystemBtn, gbc);
        angleSystemIncreaseBtn = new JButton();
        angleSystemIncreaseBtn.setActionCommand("angleSystemAIncreaseAction");
        angleSystemIncreaseBtn.setText("increase");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleSystemIncreaseBtn, gbc);
        angleSystemDecreaseBtn = new JButton();
        angleSystemDecreaseBtn.setActionCommand("angleSystemADecreaseAction");
        angleSystemDecreaseBtn.setText("decrease");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleSystemDecreaseBtn, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        panel2.add(panel4, gbc);
        angleATextField = new JTextField();
        angleATextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(angleATextField, gbc);
        angleBTextField = new JTextField();
        angleBTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(angleBTextField, gbc);
        angleCTextField = new JTextField();
        angleCTextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(angleCTextField, gbc);
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
        panel4.add(useCustomAnglesBtn, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel5, gbc);
        angleRestricedBtn = new JButton();
        angleRestricedBtn.setActionCommand("deg2Action");
        angleRestricedBtn.setText("angleRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(angleRestricedBtn, gbc);
        offsetRestrictedBtn = new JButton();
        offsetRestrictedBtn.setActionCommand("deg3Action");
        offsetRestrictedBtn.setText("offsetRestricted");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(offsetRestrictedBtn, gbc);
        convergingBtn = new JButton();
        convergingBtn.setActionCommand("deg1Action");
        convergingBtn.setText("converging");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(convergingBtn, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel6, gbc);
        ratio1TextField = new JTextField();
        ratio1TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(ratio1TextField, gbc);
        ratioLabel1 = new JLabel();
        ratioLabel1.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(ratioLabel1, gbc);
        ratio2TextField = new JTextField();
        ratio2TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(ratio2TextField, gbc);
        ratioLabel2 = new JLabel();
        ratioLabel2.setText("sqrt");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(ratioLabel2, gbc);
        ratio3TextField = new JTextField();
        ratio3TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(ratio3TextField, gbc);
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
        panel6.add(ratioBtn, gbc);
        ratio4TextField = new JTextField();
        ratio4TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(ratio4TextField, gbc);
        ratioLabel3 = new JLabel();
        ratioLabel3.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(ratioLabel3, gbc);
        ratio5TextField = new JTextField();
        ratio5TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(ratio5TextField, gbc);
        ratioLabel4 = new JLabel();
        ratioLabel4.setText("sqrt");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(ratioLabel4, gbc);
        ratio6TextField = new JTextField();
        ratio6TextField.setMinimumSize(new Dimension(40, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(ratio6TextField, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel6.add(spacer2, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Angles and References");
        panel1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 7), null, null, 0, false));
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
