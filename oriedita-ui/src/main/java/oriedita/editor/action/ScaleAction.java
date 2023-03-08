package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.databinding.FoldedFigureModel;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.scaleAction)
public class ScaleAction extends AbstractOrieditaAction{
    @Inject
    FoldedFigureModel foldedFigureModel;

    @Inject
    public ScaleAction(){}

    @Override
    public void actionPerformed(ActionEvent e) {
        foldedFigureModel.setScale(1);
    }
}
