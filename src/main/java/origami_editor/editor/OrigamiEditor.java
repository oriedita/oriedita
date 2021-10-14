package origami_editor.editor;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;

public class OrigamiEditor {
    public static void main(String[] argv) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            App app = new App();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Rewrite location

            if (argv.length == 1) {
                // We got a file
                app.openFile(new File(argv[0]));
            }
        });
    }
}
