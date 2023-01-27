package oriedita.editor.factory;

import dagger.Module;
import dagger.Provides;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.canvas.impl.CreasePattern_Worker_Impl;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.TaskExecutorService;
import origami.crease_pattern.FoldLineSet;

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
                                                                  @Named("auxlines") FoldLineSet auxLines,
                                                                  @Named("foldlines") FoldLineSet foldLineSet,
                                                                  @Named("camvExecutor") TaskExecutorService camvTaskExecutor,
                                                                  CanvasModel canvasModel,
                                                                  ApplicationModel applicationModel,
                                                                  GridModel gridModel,
                                                                  FoldedFigureModel foldedFigureModel,
                                                                  FileModel fileModel,
                                                                  AngleSystemModel angleSystemModel,
                                                                  TextWorker textWorker,
                                                                  SelectedTextModel textModel) {
        return new CreasePattern_Worker_Impl(creasePatternCamera, historyState, auxHistoryState, auxLines, foldLineSet, camvTaskExecutor, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, angleSystemModel, textWorker, textModel);
    }
}
