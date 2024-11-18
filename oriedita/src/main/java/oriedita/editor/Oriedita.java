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
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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

    public void start(@Observes ContainerInitialized event) throws InterruptedException, InvocationTargetException {
        long startTime = System.currentTimeMillis();

        if (!event.getContainerId().equals(RegistrySingletonProvider.STATIC_INSTANCE)) {
            Logger.warn("Not starting Oriedita outside of normal mode");
            return;
        }

        System.setProperty("apple.laf.useScreenMenuBar", "true");

//        loadFont();

        // Initialize look and feel service, this will bind to the applicationModel update the look and feel (must be done early).
        lookAndFeelService.init();
        // Restore the applicationModel, this should be done as early as possible.
        applicationModelPersistenceService.init();

        Logger.info("Setup in {} ms.", System.currentTimeMillis() - startTime);

        lookAndFeelService.registerFlatLafSource();

        SwingUtilities.invokeAndWait(() -> {
            try {
                app.start();
                Logger.trace("App.start finished");
            } catch (InterruptedException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        LoadingDialogUtil.hide();

        app.afterStart();


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
    }

    public static void main(String[] argv) {
        LoadingDialogUtil.show();
        StartMain.main(argv);
    }
}
