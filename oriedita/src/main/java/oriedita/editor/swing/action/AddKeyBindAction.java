package oriedita.editor.swing.action;

import oriedita.editor.databinding.HotkeyModel;
import oriedita.editor.swing.dialog.SelectKeyStrokeDialog;
import oriedita.editor.tools.KeyStrokeUtil;
import oriedita.editor.tools.ResourceUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class AddKeyBindAction extends AbstractAction {
    private final JFrame owner;
    private final String key;
    private final HotkeyModel hotkeyModel;

    public AddKeyBindAction(JFrame owner, String key, HotkeyModel hotkeyModel) {
        this.owner = owner;
        this.key = key;

        this.hotkeyModel = hotkeyModel;

        hotkeyModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals(key)) {
                KeyStroke newKeyStroke = hotkeyModel.getHotkey(key);

                if (newKeyStroke != null) {
                    putValue(Action.NAME, "Change key stroke (Current: " + KeyStrokeUtil.toString(newKeyStroke) + ")");
                } else {
                    putValue(Action.NAME, "Change key stroke");
                }
            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        InputMap map = owner.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke stroke = null;
        for (KeyStroke keyStroke : map.keys()) {
            if (map.get(keyStroke).equals(key)) {
                stroke = keyStroke;
            }
        }
        KeyStroke currentKeyStroke = stroke;

        SelectKeyStrokeDialog selectKeyStrokeDialog = new SelectKeyStrokeDialog(owner, key, hotkeyModel, currentKeyStroke, newKeyStroke -> {
            if (newKeyStroke != null && hotkeyModel.getKey(newKeyStroke) != null && !Objects.equals(hotkeyModel.getKey(newKeyStroke), key)) {

                String conflictingButton = (String) owner.getRootPane()
                        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                        .get(newKeyStroke);
                JOptionPane.showMessageDialog(owner, "Conflicting KeyStroke! Conflicting with " + conflictingButton);
                return false;
            }

            hotkeyModel.setHotkey(key, newKeyStroke);

            return true;
        });

        selectKeyStrokeDialog.showDialog();
    }
}
