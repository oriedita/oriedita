package oriedita.editor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.weld.bootstrap.api.helpers.RegistrySingletonProvider;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.tinylog.Logger;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.service.ApplicationModelPersistenceService;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.LookAndFeelService;
import oriedita.editor.swing.dialog.LoadingDialogUtil;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class Oriedita {
    @Inject
    LookAndFeelService lookAndFeelService;
    @Inject
    ApplicationModelPersistenceService applicationModelPersistenceService;

    @Inject
    FileSaveService fileSaveService;

    @Inject
    App app;

    @Inject
    @Parameters
    List<String> argv;

    public void start(@Observes ContainerInitialized event) {
        long startTime = System.currentTimeMillis();

        if (! event.getContainerId().equals(RegistrySingletonProvider.STATIC_INSTANCE)) {
            Logger.warn("Not starting Oriedita outside of normal mode");
            return;
        }

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        loadFont();

        // Initialize look and feel service, this will bind to the applicationModel update the look and feel (must be done early).
        lookAndFeelService.init();
        // Restore the applicationModel, this should be done as early as possible.
        applicationModelPersistenceService.init();

        Logger.info("Setup in {} ms.", System.currentTimeMillis() - startTime);

        SwingUtilities.invokeLater(() -> {
            lookAndFeelService.registerFlatLafSource();

            try {
                app.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            LoadingDialogUtil.hide();

            if (argv.size() == 1) {
                // We got a file
                try {
                    fileSaveService.openFile(new File(argv.get(0)));
                } catch (FileReadingException e) {
                    Logger.error(e, "Error reading file");
                    JOptionPane.showMessageDialog(null, "An error occurred when reading this file", "Read Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            fileSaveService.initAutoSave();
            Logger.info("Startup in {} ms.", System.currentTimeMillis() - startTime);
        });
    }

    private static void loadFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Oriedita.class.getClassLoader().getResourceAsStream("Icons2.ttf"))));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv) {
        LoadingDialogUtil.show();
        StartMain.main(argv);
    }
}
