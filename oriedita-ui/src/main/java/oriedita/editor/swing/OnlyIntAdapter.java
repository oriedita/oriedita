package oriedita.editor.swing;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class OnlyIntAdapter extends KeyAdapter {
    private JTextField tf;

    public OnlyIntAdapter(JTextField tf) {
        this.tf = tf;
    }

    public void keyPressed(KeyEvent e) {
        tf.setEditable(onlyInt(e));
    }

    public boolean onlyInt(KeyEvent e) {
        return (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == KeyEvent.VK_BACK_SPACE;
    }
}
