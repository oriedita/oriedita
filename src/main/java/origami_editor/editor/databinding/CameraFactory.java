package origami_editor.editor.databinding;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import origami_editor.tools.Camera;

@Component
public class CameraFactory {

    @Bean
    @Qualifier("creasePatternCamera")
    public Camera creasePatternCamera(CameraModel cameraModel) {
        Camera creasePatternCamera = new Camera();
        cameraModel.addPropertyChangeListener(e -> creasePatternCamera.setData(cameraModel));
        return creasePatternCamera;
    }
}
