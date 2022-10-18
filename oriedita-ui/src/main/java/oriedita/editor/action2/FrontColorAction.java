package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.frontColorAction)
public class FrontColorAction extends AbstractOrieditaAction{
    @Inject
    FrameProvider frameProvider;
    @Inject
    FoldedFigureModel foldedFigureModel;
    @Inject
    public FrontColorAction() {

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        //以下にやりたいことを書く

        Color frontColor = JColorChooser.showDialog(frameProvider.get(), "F_col", Color.white);

        if (frontColor != null) {
            foldedFigureModel.setFrontColor(frontColor);
        }
    }
}
