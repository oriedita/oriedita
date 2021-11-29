package oriedita.editor;

import com.formdev.flatlaf.FlatLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.factory.AppFactory;
import oriedita.editor.factory.DaggerAppFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class Oriedita {
    private static final Logger logger = LogManager.getLogger(Oriedita.class);

    public static void main(String[] argv) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        AppFactory build = DaggerAppFactory.create();

        // Initialize look and feel service, this will bind to the applicationModel update the look and feel (must be done early).
        build.lookAndFeelService().init();
        // Restore the applicationModel, this should be done as early as possible.
        build.applicationModelPersistenceService().init();

        SwingUtilities.invokeLater(() -> {
            FlatLaf.registerCustomDefaultsSource("oriedita.editor.themes");

            build.app().start();

            if (argv.length == 1) {
                // We got a file
                try {
                    build.fileSaveService().openFile(new File(argv[0]));
                } catch (FileReadingException e) {
                    logger.error("Error reading file", e);
                    JOptionPane.showMessageDialog(null, "An error occurred when reading this file", "Read Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            build.fileSaveService().initAutoSave();
        });
    }
}
