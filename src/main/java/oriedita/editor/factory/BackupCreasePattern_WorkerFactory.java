package oriedita.editor.factory;

import dagger.Module;
import dagger.Provides;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.HistoryState;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class BackupCreasePattern_WorkerFactory {
    @Provides
    @Singleton
    @Named("backupCreasePattern_Worker")
    public static CreasePattern_Worker backupCreasePattern_Worker(@Named("creasePatternCamera") Camera creasePatternCamera,
                                                                  @Named("normal") HistoryState historyState,
                                                                  @Named("aux") HistoryState auxHistoryState,
                                                                  CanvasModel canvasModel,
                                                                  ApplicationModel applicationModel,
                                                                  GridModel gridModel,
                                                                  FoldedFigureModel foldedFigureModel,
                                                                  FileModel fileModel,
                                                                  AngleSystemModel angleSystemModel,
                                                                  InternalDivisionRatioModel internalDivisionRatioModel) {
        return new CreasePattern_Worker(creasePatternCamera, historyState, auxHistoryState, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, angleSystemModel, internalDivisionRatioModel);
    }
}
