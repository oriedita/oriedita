package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.Canvas;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.FoldingService;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.service.TaskService;
import oriedita.editor.task.FoldingEstimateSave100Task;
import oriedita.editor.task.FoldingEstimateSpecificTask;

@ApplicationScoped
public class TaskServiceImpl implements TaskService {
    private final TaskExecutorService foldingExecutor;
    private final CanvasModel canvasModel;
    private final Canvas canvas;
    private final FileSaveService fileSaveService;
    private final FoldingService foldingService;
    private final FoldedFiguresList foldedFiguresList;
    private final FoldedFigureModel foldedFigureModel;

    @Inject
    public TaskServiceImpl(
            @Named("foldingExecutor") TaskExecutorService foldingExecutor,
            CanvasModel canvasModel,
            Canvas canvas,
            FileSaveService fileSaveService,
            FoldingService foldingService,
            FoldedFiguresList foldedFiguresList,
            FoldedFigureModel foldedFigureModel
    ) {

        this.foldingExecutor = foldingExecutor;
        this.canvasModel = canvasModel;
        this.canvas = canvas;
        this.fileSaveService = fileSaveService;
        this.foldingService = foldingService;
        this.foldedFiguresList = foldedFiguresList;
        this.foldedFigureModel = foldedFigureModel;
    }

    public void executeFoldingEstimateSave100Task() {
        foldingExecutor.executeTask(new FoldingEstimateSave100Task(canvas, canvasModel, foldingService, fileSaveService, foldedFiguresList));
    }

    public void executeFoldingEstimateSpecificTask() {
        foldingExecutor.executeTask(new FoldingEstimateSpecificTask(foldedFigureModel, foldingService, canvasModel, foldedFiguresList));
    }
}
