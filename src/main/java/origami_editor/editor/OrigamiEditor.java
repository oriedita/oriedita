package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import origami_editor.editor.exception.FileReadingException;
import origami_editor.editor.factory.AppFactory;
import origami_editor.editor.factory.DaggerAppFactory;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class OrigamiEditor {
    public static void main(String[] argv) throws InterruptedException, InvocationTargetException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        AppFactory build = DaggerAppFactory.create();

        build.applicationModelPersistenceService().restoreApplicationModel();

        // Create app before starting ui thread.
        // This also initializes the ui.
        App app = build.app();

        SwingUtilities.invokeLater(() -> {
            FlatLaf.registerCustomDefaultsSource("origami_editor.editor.themes");

            app.start();

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
