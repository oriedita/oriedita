package oriedita.editor.action;

import jakarta.inject.Inject;
import oriedita.editor.AnimationDurations;
import oriedita.editor.Animations;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.AnimationService;

public abstract class AbstractCreasePatternZoomAction extends AbstractOrieditaAction{
    @Inject
    FoldedFiguresList foldedFiguresList;
    @Inject
    FoldedFigureModel foldedFigureModel;
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

        if (applicationModel.getMoveFoldedModelWithCp()) {
            animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                    s -> {
                        foldedFigureModel.setScale(s);
                        // Move all other objects along.
                        for (FoldedFigure_Drawer foldedFigure_drawer : foldedFiguresList.getItems()) {
                            foldedFigure_drawer.setScale(foldedFigureModel.getScale());
                        }
                    },
                    foldedFigureModel::getScale,
                    scale -> foldedFigureModel.getScaleForZoomBy(value, applicationModel.getZoomSpeed(), scale),
                    AnimationDurations.ZOOM);
        }
    }
}
