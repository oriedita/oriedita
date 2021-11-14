package origami_editor.editor.service;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import origami_editor.editor.databinding.CameraModel;
import origami_editor.tools.Camera;

@Singleton
public class CameraFactory {
    @Produces
    @Singleton
    @Named("creasePatternCamera")
    public Camera creasePatternCamera(CameraModel cameraModel) {
        Camera creasePatternCamera = new Camera();
        cameraModel.addPropertyChangeListener(e -> creasePatternCamera.setData(cameraModel));
        return creasePatternCamera;
    }
}
