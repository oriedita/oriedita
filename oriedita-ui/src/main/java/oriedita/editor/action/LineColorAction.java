package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;

import javax.swing.JColorChooser;
import java.awt.Color;
import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.lineColorAction)
public class LineColorAction extends AbstractOrieditaAction {
    @Inject
    FrameProvider frameProvider;
    @Inject
    FoldedFigureModel foldedFigureModel;

    @Inject
    public LineColorAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //以下にやりたいことを書く

        Color lineColor = JColorChooser.showDialog(frameProvider.get(), "L_col", Color.white);
        if (lineColor != null) {
            foldedFigureModel.setLineColor(lineColor);
        }
    }
}
