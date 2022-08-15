package oriedita.editor.factory;

import dagger.Binds;
import dagger.Module;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.impl.CreasePattern_Worker_Impl;
import oriedita.editor.service.*;
import oriedita.editor.service.impl.*;

@Module
public abstract class ServiceFactory {
    @Binds
    abstract ResetService resetService(ResetServiceImpl resetService);

    @Binds
    abstract ApplicationModelPersistenceService applicationModelPersistenceService(ApplicationModelPersistenceServiceImpl applicationModelPersistenceService);

    @Binds
    abstract ButtonService buttonService(ButtonServiceImpl buttonService);

    @Binds
    abstract LookAndFeelService lookAndFeelService(LookAndFeelServiceImpl lookAndFeelService);

    @Binds
    abstract FileSaveService fileSaveService(FileSaveServiceImpl fileSaveService);

    @Binds
    abstract FoldedFigureCanvasSelectService foldedFigureCanvasSelectService(FoldedFigureCanvasSelectServiceImpl foldedFigureCanvasSelectService);

    @Binds
    abstract FoldingService foldingService(FoldingServiceImpl foldingService);

    @Binds
    abstract CreasePattern_Worker creasePattern_worker(CreasePattern_Worker_Impl creasePattern_worker);

    @Binds
    abstract TaskService taskService(TaskServiceImpl taskService);
}
