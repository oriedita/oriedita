package oriedita.editor.swing;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TextFieldTempPopupAdapter extends MouseAdapter {
    private final JTextField tf;
    private final String popupText;


    public TextFieldTempPopupAdapter(JTextField tf, String popupText){
        this.tf = tf;
        this.popupText = popupText;
    }

    @Override
    public void mouseClicked(MouseEvent e){
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.tf.getText()), null);

        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        JPanel panel = new JPanel();
        panel.add(new JLabel(this.popupText));
        frame.add(panel);

        Point popupPt = this.tf.getLocationOnScreen();
        frame.setLocation(popupPt.x + (this.tf.getWidth() / 2) - 30, popupPt.y - this.tf.getHeight() - 5);
        frame.setVisible(true);
        frame.pack();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                frame.dispose();
            } catch (InterruptedException ex) { ex.printStackTrace(); }
        }).start();
    }
}
