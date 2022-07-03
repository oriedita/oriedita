package oriedita.editor.factory;

import dagger.Component;
import oriedita.editor.App;
import oriedita.editor.service.*;

import javax.inject.Singleton;

/**
 * Main factory for the application.
 */
@Component(modules = {
        CameraFactory.class,
        FrameFactory.class,
        MouseHandlerFactory.class,
        BackupCreasePattern_WorkerFactory.class,
        HistoryStateFactory.class,
        TaskFactory.class,
        FoldLineSetFactory.class,
        ServiceFactory.class,
})
@Singleton
public interface AppFactory {
    App app();

    ApplicationModelPersistenceService applicationModelPersistenceService();

    FileSaveService fileSaveService();

    LookAndFeelService lookAndFeelService();
}
