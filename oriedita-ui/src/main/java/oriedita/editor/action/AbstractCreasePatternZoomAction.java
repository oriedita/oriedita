package oriedita.editor.action;

import jakarta.inject.Inject;
import oriedita.editor.AnimationDurations;
import oriedita.editor.Animations;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.service.AnimationService;

public abstract class AbstractCreasePatternZoomAction extends AbstractOrieditaAction{
    @Inject
    CameraModel creasePatternCameraModel;

    @Inject
    AnimationService animationService;

    @Inject
    ApplicationModel applicationModel;

    protected void zoom(double value) {
        animationService.animate(Animations.ZOOM_CP, creasePatternCameraModel::setScale,
                creasePatternCameraModel::getScale, s -> creasePatternCameraModel.getScaleForZoomBy(value, applicationModel.getZoomSpeed(), s),
                AnimationDurations.ZOOM);
    }
}
