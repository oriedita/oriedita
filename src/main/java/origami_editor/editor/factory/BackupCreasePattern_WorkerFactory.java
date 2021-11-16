package origami_editor.editor.factory;

import dagger.Module;
import dagger.Provides;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.tools.Camera;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class BackupCreasePattern_WorkerFactory {
    @Provides
    @Singleton
    @Named("backupCreasePattern_Worker")
    public static CreasePattern_Worker backupCreasePattern_Worker(@Named("creasePatternCamera") Camera creasePatternCamera,
                                                                  CanvasModel canvasModel,
                                                                  ApplicationModel applicationModel,
                                                                  GridModel gridModel,
                                                                  FoldedFigureModel foldedFigureModel,
                                                                  FileModel fileModel,
                                                                  AngleSystemModel angleSystemModel,
                                                                  InternalDivisionRatioModel internalDivisionRatioModel) {
        return new CreasePattern_Worker(creasePatternCamera, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, angleSystemModel, internalDivisionRatioModel);
    }
}
