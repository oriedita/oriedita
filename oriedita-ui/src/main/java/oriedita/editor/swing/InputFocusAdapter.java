package oriedita.editor.swing;

import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class InputFocusAdapter extends FocusAdapter {
    private final JTextField tf;

    public InputFocusAdapter(JTextField tf) {
        this.tf = tf;
    }

    @Override
    public void focusLost(FocusEvent e) {
        tf.setEditable(true);
    }
}
