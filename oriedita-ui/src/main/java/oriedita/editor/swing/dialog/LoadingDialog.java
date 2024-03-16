package oriedita.editor.swing.dialog;

import jico.Ico;
import jico.ImageReadException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Dialog shown when loading Oriedita
 */
public class LoadingDialog extends JDialog {
    public LoadingDialog() {
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setSize(new Dimension(300,200));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            BufferedImage img = Ico.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("oriedita.ico"))).get(0);

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(img, 0, 0, null);
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(img.getWidth(), img.getHeight());
                }
            };

            setContentPane(panel);

            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        } catch (ImageReadException | IOException e) {
            e.printStackTrace();
        }
    }
}
