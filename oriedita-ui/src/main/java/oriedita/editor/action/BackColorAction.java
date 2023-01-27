package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;

import javax.swing.JColorChooser;
import java.awt.Color;
import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.backColorAction)
public class BackColorAction extends AbstractOrieditaAction {
    @Inject
    FrameProvider frameProvider;
    @Inject
    FoldedFigureModel foldedFigureModel;

    @Inject
    public BackColorAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //以下にやりたいことを書く
        Color backColor = JColorChooser.showDialog(frameProvider.get(), "B_col", Color.white);

        if (backColor != null) {
            foldedFigureModel.setBackColor(backColor);
        }
    }
}
