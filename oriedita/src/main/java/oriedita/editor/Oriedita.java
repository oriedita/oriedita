package oriedita.editor;

import org.tinylog.Logger;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.factory.AppFactory;
import oriedita.editor.factory.DaggerAppFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Oriedita {

    public static void main(String[] argv) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Oriedita.class.getClassLoader().getResourceAsStream("Icons2.ttf"))));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        AppFactory build = DaggerAppFactory.create();

        // Initialize look and feel service, this will bind to the applicationModel update the look and feel (must be done early).
        build.lookAndFeelService().init();
        // Restore the applicationModel, this should be done as early as possible.
        build.applicationModelPersistenceService().init();

        SwingUtilities.invokeLater(() -> {
            build.lookAndFeelService().registerFlatLafSource();

            build.app().start();

            if (argv.length == 1) {
                // We got a file
                try {
                    build.fileSaveService().openFile(new File(argv[0]));
                } catch (FileReadingException e) {
                    Logger.error(e, "Error reading file");
                    JOptionPane.showMessageDialog(null, "An error occurred when reading this file", "Read Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            build.fileSaveService().initAutoSave();
        });
    }
}
