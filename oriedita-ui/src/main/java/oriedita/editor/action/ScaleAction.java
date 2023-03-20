package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.AnimationDurations;
import oriedita.editor.Animations;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.service.AnimationService;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.scaleAction)
public class ScaleAction extends AbstractOrieditaAction{
    @Inject
    FoldedFigureModel foldedFigureModel;
    @Inject
    AnimationService animationService;
    @Inject
    public ScaleAction(){}

    @Override
    public void actionPerformed(ActionEvent e) {
        animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                foldedFigureModel::setScale,
                foldedFigureModel::getScale,
                1.0,
                AnimationDurations.SCALE_SPEED);
    }
}
