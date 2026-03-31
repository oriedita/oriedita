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
@UiFor(MouseHandlerSettingGroup.FIX_PRECISION_SELECT)
public class FixPrecisionUi implements MouseHandlerUi {
    private JPanel root;
    private JSlider fixPrecision22_5Slider;
    private JLabel fixPrecision22_5Label;
    private DraggableTextField fixPrecision22_5DraggableTextField;
    private JCheckBox fixPrecision22_5CheckBox;
    private JCheckBox fixPrecisionBPCheckBox;

    private final ButtonService buttonService;
    private final FixPrecisionModel fixPrecisionModel;
    private final BindingService bindingService;

    private double trueValue;
    private boolean updating;

    @Inject
    public FixPrecisionUi(ButtonService buttonService, FixPrecisionModel fixPrecisionModel, BindingService bindingService) {
        this.buttonService = buttonService;
        this.fixPrecisionModel = fixPrecisionModel;
        this.bindingService = bindingService;
    }

    @Override
    public void init() {
        double sliderScale = 1000.0;
        trueValue = fixPrecision22_5Slider.getValue() / sliderScale;
        buttonService.addDefaultListener($$$getRootComponent$$$());
        bindingService.addBinding(fixPrecisionModel, "fixPrecision", fixPrecision22_5DraggableTextField, new DoubleConverter("0.0##"));
        buttonService.registerTextField(fixPrecision22_5DraggableTextField, ActionType.setFixPrecisionAction.action());
        buttonService.registerButton(fixPrecisionBPCheckBox, ActionType.setFixPrecisionAction.action());
        buttonService.registerButton(fixPrecision22_5CheckBox, ActionType.setFixPrecisionAction.action());
        buttonService.registerSlider(fixPrecision22_5Slider, ActionType.setFixPrecisionAction.action());
        fixPrecision22_5DraggableTextField.setText(String.valueOf(trueValue));
        fixPrecision22_5CheckBox.setSelected(true);
        fixPrecisionBPCheckBox.setSelected(true);

        fixPrecision22_5CheckBox.addActionListener(e -> {
            fixPrecision22_5Slider.setEnabled(fixPrecision22_5CheckBox.isSelected());
            fixPrecision22_5DraggableTextField.setEnabled(fixPrecision22_5CheckBox.isSelected());
            fixPrecision22_5Label.setEnabled(fixPrecision22_5CheckBox.isSelected());
            fixPrecisionModel.setFixPrecisionUse22_5(fixPrecision22_5CheckBox.isSelected());
        });

        fixPrecisionBPCheckBox.addActionListener(e -> fixPrecisionModel.setFixPrecisionUseBP(fixPrecisionBPCheckBox.isSelected()));

        fixPrecision22_5Slider.addChangeListener(l -> {
            if (!updating) {
                updating = true;
                trueValue = fixPrecision22_5Slider.getValue() / sliderScale;
                fixPrecisionModel.setFixPrecision(trueValue);
                updating = false;
            }
        });

        fixPrecision22_5DraggableTextField.getDocument().addDocumentListener(new DocumentListener() {
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
                if (!updating) {
                    updating = true;
                    try {
                        trueValue = Double.parseDouble(fixPrecision22_5DraggableTextField.getText());
                        fixPrecision22_5Slider.setValue((int) (trueValue * sliderScale));
                        fixPrecisionModel.setFixPrecision(trueValue);
                    } catch (RuntimeException e) {
                        Logger.info(e);
                    }
                    updating = false;
                }
            }
        });
        fixPrecision22_5DraggableTextField.addRawListener((d, fine) -> {
            if (!updating && d != 0) {
                updating = true;
                trueValue += fine ? (double) (d) / (sliderScale * 10) : (double) (d) / (sliderScale * 2);
                if (trueValue < 0)
                    trueValue = 0;
                fixPrecision22_5Slider.setValue((int) (trueValue * sliderScale));

                DoubleConverter df = new DoubleConverter("0.0##");
                String value = df.convert(trueValue);
                fixPrecision22_5DraggableTextField.setText(String.valueOf(value));
                updating = false;
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
        root.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        root.setOpaque(false);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setOpaque(false);
        root.add(panel1, new GridConstraints(0, 0, 2, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fixPrecision22_5CheckBox = new JCheckBox();
        fixPrecision22_5CheckBox.setEnabled(true);
        fixPrecision22_5CheckBox.setSelected(true);
        fixPrecision22_5CheckBox.setText("22.5°");
        panel1.add(fixPrecision22_5CheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fixPrecisionBPCheckBox = new JCheckBox();
        fixPrecisionBPCheckBox.setText("Boxpleated");
        panel1.add(fixPrecisionBPCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(3, 3, 3, 3), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fixPrecision22_5Slider = new JSlider();
        fixPrecision22_5Slider.setMajorTickSpacing(0);
        fixPrecision22_5Slider.setMaximum(100);
        fixPrecision22_5Slider.setMinimum(1);
        panel2.add(fixPrecision22_5Slider, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fixPrecision22_5DraggableTextField = new DraggableTextField();
        panel2.add(fixPrecision22_5DraggableTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, -1), null, 0, false));
        fixPrecision22_5Label = new JLabel();
        fixPrecision22_5Label.setText("Precision");
        panel2.add(fixPrecision22_5Label, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(5, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
