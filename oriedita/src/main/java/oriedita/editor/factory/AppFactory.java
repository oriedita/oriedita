package oriedita.editor.factory;

import dagger.Component;
import oriedita.editor.App;
import oriedita.editor.service.*;
import oriedita.editor.swing.BottomPanel;
import oriedita.editor.swing.LeftPanel;
import oriedita.editor.swing.RightPanel;
import oriedita.editor.swing.TopPanel;

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
        FoldFileFactory.class,
})
@Singleton
public interface AppFactory {
    LeftPanel leftPanel();
    RightPanel rightPanel();
    BottomPanel bottomPanel();
    TopPanel topPanel();

    App app();

    ApplicationModelPersistenceService applicationModelPersistenceService();

    FileSaveService fileSaveService();

    LookAndFeelService lookAndFeelService();
}
