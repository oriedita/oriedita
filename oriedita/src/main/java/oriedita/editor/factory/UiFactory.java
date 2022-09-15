package oriedita.editor.factory;

import dagger.Module;
import dagger.Provides;
import oriedita.editor.Canvas;

@Module
public class UiFactory {
    @Provides
    Canvas.CanvasImpl canvasImplProvider(Canvas canvas) {
        return canvas.init();
    }
}
