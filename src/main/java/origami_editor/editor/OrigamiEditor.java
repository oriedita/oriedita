package origami_editor.editor;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class OrigamiEditor {
    public static void main(String[] argv) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            App frame = new App();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Rewrite location

//            frame.setSize(1200, 700);
            frame.pack();
            frame.setLocationRelativeTo(null);//If you want to put the application window in the center of the screen, use the setLocationRelativeTo () method. If you pass null, it will always be in the center.
            frame.setVisible(true);
        });
    }
}
