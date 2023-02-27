package oriedita.editor.swing;

import javax.swing.JTextField;
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
            tf.setBackground(new Color(255, 153, 153));
        }
        else {
            tf.setBackground(new Color(255, 255, 255));}
    }

    public boolean onlyDouble(JTextField tf) {
        return Pattern.compile("^-?\\d+(\\.\\d+)?$").matcher(tf.getText()).matches();
    }
}
