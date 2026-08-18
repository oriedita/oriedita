package oriedita.editor.swing.toolsetting;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.common.converter.DoubleConverter;
import oriedita.editor.action.ActionType;
import oriedita.editor.databinding.FixPrecisionModel;
import oriedita.editor.handler.MouseHandlerSettingGroup;
import oriedita.editor.handler.UiFor;
import oriedita.editor.service.BindingService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.DraggableTextField;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.Insets;

@ApplicationScoped
@UiFor(MouseHandlerSettingGroup.FIX_PRECISION)
public class FixPrecisionUi implements MouseHandlerUi {
    private JPanel root;
    private JSlider fixPrecision_22_5_Slider;
    private JLabel fixPrecision_22_5_Label;
    private DraggableTextField fixPrecision_22_5_DraggableTextField;
    private JCheckBox fixPrecision_22_5_CheckBox;
    private JCheckBox fixPrecision_BP_CheckBox;
    private JCheckBox fixPrecision_BPLocal22_5_CheckBox;
    private JSlider fixPrecision_BPLocal22_5_Slider;
    private DraggableTextField fixPrecision_BPLocal22_5_DraggableTextField;
    private JLabel fixPrecision_BPLocal22_5_Label;
    private JLabel fixPrecision_Seperator;

    private final ButtonService buttonService;
    private final FixPrecisionModel fixPrecisionModel;
    private final BindingService bindingService;

    private double sliderValue_22_5;
    private double sliderValue_BP;
    private boolean updating_22_5;
    private boolean updating_BP;

    @Inject
    public FixPrecisionUi(ButtonService buttonService, FixPrecisionModel fixPrecisionModel, BindingService bindingService) {
        this.buttonService = buttonService;
        this.fixPrecisionModel = fixPrecisionModel;
        this.bindingService = bindingService;
    }

    @Override
    public void init() {
        double sliderScale = 1000.0;
        // Connect to UI and fixPrecisionModel
        sliderValue_22_5 = fixPrecision_22_5_Slider.getValue() / sliderScale;
        sliderValue_BP = fixPrecision_BPLocal22_5_Slider.getValue() / sliderScale;
        buttonService.addDefaultListener($$$getRootComponent$$$());
        bindingService.addBinding(fixPrecisionModel, "precision_22_5", fixPrecision_22_5_DraggableTextField, new DoubleConverter("0.0##"));
        buttonService.registerTextField(fixPrecision_22_5_DraggableTextField, ActionType.setFixPrecisionAction.action());
        bindingService.addBinding(fixPrecisionModel, "precision_BPLocal22_5", fixPrecision_BPLocal22_5_DraggableTextField, new DoubleConverter("0.0##"));
        buttonService.registerTextField(fixPrecision_BPLocal22_5_DraggableTextField, ActionType.setFixPrecisionAction.action());
        buttonService.registerButton(fixPrecision_BP_CheckBox, ActionType.setFixPrecisionAction.action());
        buttonService.registerButton(fixPrecision_22_5_CheckBox, ActionType.setFixPrecisionAction.action());
        buttonService.registerButton(fixPrecision_BPLocal22_5_CheckBox, ActionType.setFixPrecisionAction.action());
        buttonService.registerSlider(fixPrecision_22_5_Slider, ActionType.setFixPrecisionAction.action());
        buttonService.registerSlider(fixPrecision_BPLocal22_5_Slider, ActionType.setFixPrecisionAction.action());

        // Init
        fixPrecision_22_5_DraggableTextField.setText(String.valueOf(sliderValue_22_5));
        fixPrecision_BPLocal22_5_DraggableTextField.setText(String.valueOf(sliderValue_BP));
        fixPrecision_22_5_CheckBox.setSelected(true);
        fixPrecision_BP_CheckBox.setSelected(true);
        fixPrecision_BPLocal22_5_CheckBox.setSelected(false);

        fixPrecision_BPLocal22_5_Label.setEnabled(false);
        fixPrecision_BPLocal22_5_Slider.setEnabled(false);
        fixPrecision_BPLocal22_5_DraggableTextField.setEnabled(false);

        // Add listener for 22.5° check box
        fixPrecision_22_5_CheckBox.addActionListener(e -> {
            fixPrecision_22_5_Slider.setEnabled(fixPrecision_22_5_CheckBox.isSelected());
            fixPrecision_22_5_DraggableTextField.setEnabled(fixPrecision_22_5_CheckBox.isSelected());
            fixPrecision_22_5_Label.setEnabled(fixPrecision_22_5_CheckBox.isSelected());
            fixPrecisionModel.setUse_22_5(fixPrecision_22_5_CheckBox.isSelected());
        });

        //Add listener for BP check box
        fixPrecision_BP_CheckBox.addActionListener(e -> {
            if (fixPrecision_BP_CheckBox.isSelected() && fixPrecision_BPLocal22_5_CheckBox.isSelected()) {
                fixPrecision_BPLocal22_5_Slider.setEnabled(true);
                fixPrecision_BPLocal22_5_DraggableTextField.setEnabled(true);
                fixPrecision_BPLocal22_5_Label.setEnabled(true);
            } else {
                fixPrecision_BPLocal22_5_Slider.setEnabled(false);
                fixPrecision_BPLocal22_5_DraggableTextField.setEnabled(false);
                fixPrecision_BPLocal22_5_Label.setEnabled(false);
            }

            fixPrecision_BPLocal22_5_CheckBox.setEnabled(fixPrecision_BP_CheckBox.isSelected());
            fixPrecisionModel.setUse_BP(fixPrecision_BP_CheckBox.isSelected());
        });

        // Add listener for local 22.5° within BP check box
        fixPrecision_BPLocal22_5_CheckBox.addActionListener(e -> {
            fixPrecision_BPLocal22_5_Slider.setEnabled(fixPrecision_BPLocal22_5_CheckBox.isSelected());
            fixPrecision_BPLocal22_5_DraggableTextField.setEnabled(fixPrecision_BPLocal22_5_CheckBox.isSelected());
            fixPrecision_BPLocal22_5_Label.setEnabled(fixPrecision_BPLocal22_5_CheckBox.isSelected());
            fixPrecisionModel.setUse_BPLocal22_5(fixPrecision_BPLocal22_5_CheckBox.isSelected());
        });

        // Add listener to 22.5° precision slider
        fixPrecision_22_5_Slider.addChangeListener(l -> {
            if (!updating_22_5) {
                updating_22_5 = true;
                sliderValue_22_5 = fixPrecision_22_5_Slider.getValue() / sliderScale;
                fixPrecisionModel.setPrecision_22_5(sliderValue_22_5);
                updating_22_5 = false;
            }
        });

        // Add listener to local 22.5° within BP slider
        fixPrecision_BPLocal22_5_Slider.addChangeListener(l -> {
            if (!updating_BP) {
                updating_BP = true;
                sliderValue_BP = fixPrecision_BPLocal22_5_Slider.getValue() / sliderScale;
                fixPrecisionModel.setPrecision_BPLocal22_5(sliderValue_BP);
                updating_BP = false;
            }
        });

        // Add listener to the 22.5° precision text field
        fixPrecision_22_5_DraggableTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            private void onChange() {
                if (!updating_22_5 && fixPrecision_22_5_DraggableTextField.isEnabled()) {
                    updating_22_5 = true;
                    try {
                        sliderValue_22_5 = Double.parseDouble(fixPrecision_22_5_DraggableTextField.getText());
                        fixPrecision_22_5_Slider.setValue((int) (sliderValue_22_5 * sliderScale));
                        fixPrecisionModel.setPrecision_22_5(sliderValue_22_5);
                    } catch (RuntimeException e) {
                        Logger.info(e);
                    }
                    updating_22_5 = false;
                }
            }
        });
        fixPrecision_22_5_DraggableTextField.addRawListener((d, fine) -> {
            if (!updating_22_5 && d != 0 && fixPrecision_22_5_DraggableTextField.isEnabled()) {
                updating_22_5 = true;
                sliderValue_22_5 += fine ? (double) (d) / (sliderScale * 10) : (double) (d) / (sliderScale * 2);
                if (sliderValue_22_5 < 0)
                    sliderValue_22_5 = 0;
                fixPrecision_22_5_Slider.setValue((int) (sliderValue_22_5 * sliderScale));

                DoubleConverter df = new DoubleConverter("0.0##");
                String value = df.convert(sliderValue_22_5);
                fixPrecision_22_5_DraggableTextField.setText(String.valueOf(value));
                updating_22_5 = false;
            }
        });

        // Add listener to the 22.5° precision text field
        fixPrecision_BPLocal22_5_DraggableTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            private void onChange() {
                if (!updating_BP && fixPrecision_BPLocal22_5_DraggableTextField.isEnabled()) {
                    updating_BP = true;
                    try {
                        sliderValue_BP = Double.parseDouble(fixPrecision_BPLocal22_5_DraggableTextField.getText());
                        fixPrecision_BPLocal22_5_Slider.setValue((int) (sliderValue_BP * sliderScale));
                        fixPrecisionModel.setPrecision_BPLocal22_5(sliderValue_BP);
                    } catch (RuntimeException e) {
                        Logger.info(e);
                    }
                    updating_BP = false;
                }
            }
        });
        fixPrecision_BPLocal22_5_DraggableTextField.addRawListener((d, fine) -> {
            if (!updating_BP && d != 0 && fixPrecision_BPLocal22_5_DraggableTextField.isEnabled()) {
                updating_BP = true;
                sliderValue_BP += fine ? (double) (d) / (sliderScale * 10) : (double) (d) / (sliderScale * 2);
                if (sliderValue_BP < 0)
                    sliderValue_BP = 0;
                fixPrecision_BPLocal22_5_Slider.setValue((int) (sliderValue_BP * sliderScale));

                DoubleConverter df = new DoubleConverter("0.0##");
                String value = df.convert(sliderValue_BP);
                fixPrecision_BPLocal22_5_DraggableTextField.setText(String.valueOf(value));
                updating_BP = false;
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
        root = new JPanel();
        root.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.setOpaque(false);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setOpaque(false);
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fixPrecision_22_5_CheckBox = new JCheckBox();
        fixPrecision_22_5_CheckBox.setLabel("22.5°");
        fixPrecision_22_5_CheckBox.setText("22.5°");
        panel1.add(fixPrecision_22_5_CheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(90, 22), null, 0, false));
        fixPrecision_BP_CheckBox = new JCheckBox();
        fixPrecision_BP_CheckBox.setText("Box pleated");
        panel1.add(fixPrecision_BP_CheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(90, 22), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setOpaque(false);
        panel1.add(panel2, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(3, 8, 3, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fixPrecision_BPLocal22_5_Label = new JLabel();
        fixPrecision_BPLocal22_5_Label.setText("Precision");
        panel3.add(fixPrecision_BPLocal22_5_Label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fixPrecision_BPLocal22_5_Slider = new JSlider();
        panel3.add(fixPrecision_BPLocal22_5_Slider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fixPrecision_BPLocal22_5_DraggableTextField = new DraggableTextField();
        panel3.add(fixPrecision_BPLocal22_5_DraggableTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        fixPrecision_BPLocal22_5_CheckBox = new JCheckBox();
        fixPrecision_BPLocal22_5_CheckBox.setText("Local 22.5°");
        panel2.add(fixPrecision_BPLocal22_5_CheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, 22), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setOpaque(false);
        panel1.add(panel4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 3, new Insets(3, 8, 3, 3), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fixPrecision_22_5_Slider = new JSlider();
        fixPrecision_22_5_Slider.setMajorTickSpacing(0);
        fixPrecision_22_5_Slider.setMaximum(100);
        fixPrecision_22_5_Slider.setMinimum(0);
        panel5.add(fixPrecision_22_5_Slider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fixPrecision_22_5_DraggableTextField = new DraggableTextField();
        panel5.add(fixPrecision_22_5_DraggableTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        fixPrecision_22_5_Label = new JLabel();
        fixPrecision_22_5_Label.setText("Precision");
        panel5.add(fixPrecision_22_5_Label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(80, -1), null, 0, false));
        fixPrecision_Seperator = new JLabel();
        fixPrecision_Seperator.setText(" ―――――――――――――――――――――――――――――――――――――――――――");
        panel1.add(fixPrecision_Seperator, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
