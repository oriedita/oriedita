package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.canvas.impl.CreasePattern_Worker_Impl;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.TaskExecutorService;
import origami.crease_pattern.FoldLineSet;

@ApplicationScoped
public class BackupCreasePattern_WorkerFactory {
    @Produces
    @ApplicationScoped
    @Named("backupCreasePattern_Worker")
    public static CreasePattern_Worker backupCreasePattern_Worker(@Named("creasePatternCamera") Camera creasePatternCamera,
                                                                  @Named("normal") HistoryState historyState,
                                                                  @Named("aux") HistoryState auxHistoryState,
                                                                  @Named("backup_auxlines") FoldLineSet auxLines,
                                                                  @Named("backup_foldlines") FoldLineSet foldLineSet,
                                                                  @Named("camvExecutor") TaskExecutorService camvTaskExecutor,
                                                                  CanvasModel canvasModel,
                                                                  ApplicationModel applicationModel,
                                                                  GridModel gridModel,
                                                                  FoldedFigureModel foldedFigureModel,
                                                                  FileModel fileModel,
                                                                  TextWorker textWorker,
                                                                  SelectedTextModel textModel) {
        return new CreasePattern_Worker_Impl(creasePatternCamera, historyState, auxHistoryState, auxLines, foldLineSet, camvTaskExecutor, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, textWorker, textModel);
    }

    @Produces
    @ApplicationScoped
    @Named("mainCreasePattern_Worker")
    public static CreasePattern_Worker mainCreasePattern_Worker(@Named("creasePatternCamera") Camera creasePatternCamera,
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
                                                                TextWorker textWorker,
                                                                SelectedTextModel textModel) {

        return new CreasePattern_Worker_Impl(creasePatternCamera, historyState, auxHistoryState, auxLines, foldLineSet, camvTaskExecutor, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, textWorker, textModel);
    }
}
