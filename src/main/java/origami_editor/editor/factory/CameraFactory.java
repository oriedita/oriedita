package origami_editor.editor.factory;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.canvas.MouseModeHandler;
import origami_editor.editor.databinding.*;
import origami_editor.tools.Camera;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;

@Module
public abstract class CameraFactory {
    @Provides
    @Singleton
    @Named("creasePatternCamera")
    public static Camera creasePatternCamera(CameraModel cameraModel) {
        Camera creasePatternCamera = new Camera();
        cameraModel.addPropertyChangeListener(e -> creasePatternCamera.setData(cameraModel));
        return creasePatternCamera;
    }

}
