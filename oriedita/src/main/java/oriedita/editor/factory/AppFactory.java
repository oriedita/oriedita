package oriedita.editor.factory;

import dagger.Component;
import oriedita.editor.App;
import oriedita.editor.service.*;

import javax.inject.Singleton;

/**
 * Main factory for the application.
 */
@Component(modules = {CameraFactory.class, FrameFactory.class, MouseHandlerFactory.class, BackupCreasePattern_WorkerFactory.class, HistoryStateFactory.class})
@Singleton
public interface AppFactory {
    App app();

    ApplicationModelPersistenceService applicationModelPersistenceService();

    FileSaveService fileSaveService();

    LookAndFeelService lookAndFeelService();

    HotkeyService hotkeyService();

    default void init() {
        // Initialize look and feel service, this will bind to the applicationModel update the look and feel (must be done early).
        lookAndFeelService().init();
        // Restore the applicationModel, this should be done as early as possible.
        applicationModelPersistenceService().init();
        hotkeyService().init();
    }

    default void start() {
        app().start();
    }

    default void afterStart() {
        fileSaveService().initAutoSave();
    }
}
