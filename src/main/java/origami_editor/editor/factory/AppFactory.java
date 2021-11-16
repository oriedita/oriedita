package origami_editor.editor.factory;

import dagger.Component;
import origami_editor.editor.App;
import origami_editor.editor.service.*;

import javax.inject.Singleton;

/**
 * Main factory for the application.
 */
@Component(modules = {CameraFactory.class, FrameFactory.class, MouseHandlerFactory.class, BackupCreasePattern_WorkerFactory.class})
@Singleton
public interface AppFactory {
    App app();

    ApplicationModelPersistenceService applicationModelPersistenceService();

    FileSaveService fileSaveService();

    LookAndFeelService lookAndFeelService();
}
