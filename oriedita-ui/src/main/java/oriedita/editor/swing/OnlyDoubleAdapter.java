package oriedita.editor.swing;

import com.formdev.flatlaf.FlatLaf;
import oriedita.editor.Colors;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.util.regex.Pattern;

public class OnlyDoubleAdapter implements DocumentListener {
    private final JTextField tf;

    public OnlyDoubleAdapter(JTextField tf) {
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
        if(!onlyDouble(tf)){
            tf.setBackground(Colors.get(FlatLaf.isLafDark() ? Colors.INVALID_INPUT_DARK : Colors.INVALID_INPUT));
        }
        else {
            tf.setBackground(UIManager.getColor("TextField.background"));
        }
    }

    public boolean onlyDouble(JTextField tf) {
        return Pattern.compile("^-?\\d+(\\.\\d+)?$").matcher(tf.getText()).matches();
    }
}
