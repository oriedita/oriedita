package oriedita.editor.swing.toolsetting;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.handler.MouseHandlerSettingGroup;
import oriedita.editor.handler.UiFor;
import oriedita.editor.service.BindingService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.combobox.CustomLineTypeComboBoxRenderer;
import origami.crease_pattern.CustomLineTypes;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@ApplicationScoped
@UiFor(MouseHandlerSettingGroup.SWITCH_COLOR)
public class SetLineColorUi implements MouseHandlerUi {
    private final CanvasModel canvasModel;
    private JPanel root;
    private JComboBox<CustomLineTypes> replaceFromComboBox;
    private JLabel replaceLabel;
    private JComboBox<CustomLineTypes> replaceToComboBox;
    private JButton switchReplaceBtn;

    private final ButtonService buttonService;
    private final BindingService bindingService;

    @Inject
    public SetLineColorUi(
            CanvasModel canvasModel, ButtonService buttonService, BindingService bindingService
    ) {
        this.canvasModel = canvasModel;
        this.buttonService = buttonService;
        this.bindingService = bindingService;
    }

    @Override
    public void init() {
        buttonService.addDefaultListener(root);

        canvasModel.addPropertyChangeListener(e -> updateSwitchBtn(canvasModel));

        replaceFromComboBox.setModel(new DefaultComboBoxModel<>(CustomLineTypes.values()));
        replaceFromComboBox.setRenderer(new CustomLineTypeComboBoxRenderer());
        replaceToComboBox.setModel(new DefaultComboBoxModel<>(new CustomLineTypes[]{
                CustomLineTypes.EDGE, CustomLineTypes.MOUNTAIN, CustomLineTypes.VALLEY, CustomLineTypes.AUX
        }));
        replaceToComboBox.setRenderer(new CustomLineTypeComboBoxRenderer());

        bindingService.addBinding(canvasModel, "customFromLineType", replaceFromComboBox);
        bindingService.addBinding(canvasModel, "customToLineType", replaceToComboBox);

        buttonService.setIcon(replaceLabel, "labelReplace");
    }

    private void updateSwitchBtn(CanvasModel canvasModel) {
        var switchEnabled = canvasModel.getCustomFromLineType() != CustomLineTypes.ANY
                && canvasModel.getCustomFromLineType() != CustomLineTypes.MANDV;
        switchReplaceBtn.setEnabled(switchEnabled);
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
        panel1.setLayout(new GridBagLayout());
        panel1.setOpaque(false);
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        replaceFromComboBox = new JComboBox();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(replaceFromComboBox, gbc);
        replaceLabel = new JLabel();
        replaceLabel.setHorizontalAlignment(0);
        replaceLabel.setText("->");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 10;
        gbc.ipady = 5;
        panel1.add(replaceLabel, gbc);
        replaceToComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(replaceToComboBox, gbc);
        switchReplaceBtn = new JButton();
        switchReplaceBtn.setActionCommand("switchReplaceAction");
        switchReplaceBtn.setText("⇆");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(switchReplaceBtn, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
