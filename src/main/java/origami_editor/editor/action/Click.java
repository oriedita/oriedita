package origami_editor.editor.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Click extends AbstractAction {
    private final AbstractButton button;

    public Click(AbstractButton button) {
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        button.doClick();
    }

    public AbstractButton getButton() {
        return button;
    }
}
