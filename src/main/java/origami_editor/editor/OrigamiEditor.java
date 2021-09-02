package origami_editor.editor;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class OrigamiEditor {
    public static void main(String[] argv) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            new App();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Rewrite location
        });
    }
}
