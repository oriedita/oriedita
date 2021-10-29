package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import origami_editor.editor.exception.FileReadingException;
import origami_editor.editor.factory.AppFactory;
import origami_editor.editor.factory.DaggerAppFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class OrigamiEditor {
    public static void main(String[] argv) throws InterruptedException, InvocationTargetException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(OrigamiEditor.class.getClassLoader().getResourceAsStream("Icons2.ttf"))));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        AppFactory build = DaggerAppFactory.create();

        // Initialize look and feel service, this will bind to the applicationModel update the look and feel (must be done early).
        build.lookAndFeelService().init();
        // Restore the applicationModel, this should be done as early as possible.
        build.applicationModelPersistenceService().init();

        SwingUtilities.invokeLater(() -> {
            FlatLaf.registerCustomDefaultsSource("origami_editor.editor.themes");

            build.app().start();

            if (argv.length == 1) {
                // We got a file
                try {
                    build.fileSaveService().openFile(new File(argv[0]));
                } catch (FileReadingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
