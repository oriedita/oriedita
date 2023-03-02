package oriedita.editor.swing;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputEnterKeyAdapter extends KeyAdapter {
    private final JTextField tf;

    public InputEnterKeyAdapter(JTextField tf){
        this.tf = tf;
    }

    @Override
    public void keyTyped(KeyEvent e){
        if(e.getKeyChar() == KeyEvent.VK_ENTER) {
            tf.transferFocus();
        }
    }
}
