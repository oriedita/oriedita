package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class OrigamiEditor {
    public static void main(String[] argv) throws InterruptedException, InvocationTargetException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        App app = new App();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Rewrite location

        SwingUtilities.invokeLater(() -> {
            app.restoreApplicationModel();

            FlatLaf.registerCustomDefaultsSource("origami_editor.editor.themes");

            try {
                UIManager.setLookAndFeel(app.applicationModel.getLaf());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            app.start();

            if (argv.length == 1) {
                // We got a file
                app.openFile(new File(argv[0]));
            }
        });
    }
}
