package origami_editor.editor.factory;

import dagger.Component;
import dagger.Provides;
import origami_editor.editor.App;
import origami_editor.editor.service.ApplicationModelPersistenceService;
import origami_editor.editor.service.FileSaveService;
import origami_editor.editor.service.ResetService;
import origami_editor.editor.service.ResetServiceImpl;

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
}
