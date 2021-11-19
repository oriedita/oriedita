package origami_editor.editor.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SaveTypeDialog extends JDialog {
    private JPanel contentPane;
    private JButton creasePatternCpSavesButton;
    private JButton completeOriAlsoSavesButton;
    private JCheckBox rememberCheckBox;

    private String saveType = null;

    public SaveTypeDialog(Frame owner) {
        super(owner, "Save type", ModalityType.APPLICATION_MODAL);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(completeOriAlsoSavesButton);

        creasePatternCpSavesButton.addActionListener(e -> saveType = ".cp");
        completeOriAlsoSavesButton.addActionListener(e -> saveType = ".ori");

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }

    public static String showSaveTypeDialog(Frame owner) {
        SaveTypeDialog saveTypeDialog = new SaveTypeDialog(owner);
        saveTypeDialog.setVisible(true);

        String saveType = saveTypeDialog.saveType;

        saveTypeDialog.dispose();

        return saveType;
    }
}
