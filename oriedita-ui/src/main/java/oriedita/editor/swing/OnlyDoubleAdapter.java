package oriedita.editor.swing;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class OnlyDoubleAdapter extends KeyAdapter {
    private JTextField tf;

    public OnlyDoubleAdapter(JTextField tf) {
        this.tf = tf;
    }

    public void keyPressed(KeyEvent e) {
        tf.setEditable(onlyDouble(e, tf));
    }

    public boolean onlyDouble(KeyEvent e, JTextField tf) {
        return (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == KeyEvent.VK_BACK_SPACE || (e.getKeyChar() == '.' && !tf.getText().contains("."));
    }
}
