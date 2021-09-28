package origami_editor.editor.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Click extends AbstractAction {
    private final JButton button;

    public Click(JButton button) {
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        button.doClick();
    }

    public JButton getButton() {
        return button;
    }
}
