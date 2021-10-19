package origami_editor.editor;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.io.File;

public class OrigamiEditor {
    public static void main(String[] argv) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // Set global color filter
        FlatSVGIcon.ColorFilter colorFilter = FlatSVGIcon.ColorFilter.getInstance();
        colorFilter.add(Color.black, Color.black, Color.lightGray);

        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            App app = new App();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Rewrite location
            app.start();

            if (argv.length == 1) {
                // We got a file
                app.openFile(new File(argv[0]));
            }
        });
    }
}
