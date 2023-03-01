package oriedita.editor.swing;

import com.formdev.flatlaf.FlatLaf;
import oriedita.editor.Colors;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.util.regex.Pattern;

public class OnlyIntAdapter implements DocumentListener {
    private final JTextField tf;

    public OnlyIntAdapter(JTextField tf) {
        this.tf = tf;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        keyPressed();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        keyPressed();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        keyPressed();
    }

    public void keyPressed() {
        if(!onlyInt(tf)){
            tf.setBackground(Colors.get(FlatLaf.isLafDark() ? Colors.INVALID_INPUT_DARK : Colors.INVALID_INPUT));
        }
        else {
            tf.setBackground(UIManager.getColor("TextField.background"));
        }
    }

    public boolean onlyInt(JTextField tf) {
        return Pattern.compile("^-?\\d+$").matcher(tf.getText()).matches();
    }
}
