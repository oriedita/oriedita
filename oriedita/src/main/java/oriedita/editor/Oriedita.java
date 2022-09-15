package oriedita.editor;

import org.tinylog.Logger;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.factory.AppFactory;
import oriedita.editor.factory.DaggerAppFactory;
import oriedita.util.ResourceUtil;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class Oriedita {
    public static void main(String[] argv) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        loadOrieditaFont();

        AppFactory build = DaggerAppFactory.create();

        ResourceUtil.preloadBundles();

        loadUI(build);

        // Initialize look and feel service, this will bind to the applicationModel update the look and feel (must be done early).
        build.lookAndFeelService().init();
        // Restore the applicationModel, this should be done as early as possible.
        build.applicationModelPersistenceService().init();

        Logger.info("Setup in {} ms.", System.currentTimeMillis() - startTime);

        App app = build.app();

        SwingUtilities.invokeLater(() -> {
            build.lookAndFeelService().registerFlatLafSource();

            app.start();

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
            Logger.info("Startup in {} ms.", System.currentTimeMillis() - startTime);
        });
    }

    private static void loadUI(AppFactory build) throws InterruptedException {
        ExecutorService service = Executors.newWorkStealingPool();

        service.invokeAll(List.of(
                build::lookAndFeelService,
                build::applicationModelPersistenceService,
                build::leftPanel,
                build::rightPanel,
                build::topPanel,
                build::bottomPanel,
                build::app
        ), 10, TimeUnit.SECONDS);
    }

    private static void loadOrieditaFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Oriedita.class.getClassLoader().getResourceAsStream("Icons2.ttf"))));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }
}
