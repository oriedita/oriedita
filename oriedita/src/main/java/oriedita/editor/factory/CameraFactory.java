package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

public class CameraFactory {
    @Produces
    @ApplicationScoped
    @Named("creasePatternCamera")
    public static Camera creasePatternCamera(CameraModel cameraModel) {
        Camera creasePatternCamera = new Camera();
        creasePatternCamera.setCameraPosition(new Point(350, 350));

        cameraModel.addPropertyChangeListener(e -> {
            creasePatternCamera.setCameraAngle(cameraModel.getRotation());
            creasePatternCamera.setCameraZoomX(cameraModel.getScale());
            creasePatternCamera.setCameraZoomY(cameraModel.getScale());
        });


        return creasePatternCamera;
    }

}
