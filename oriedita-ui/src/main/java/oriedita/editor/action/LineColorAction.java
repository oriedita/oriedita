package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.swing.CustomHSVPanel;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        Color lineColor = CustomHSVPanel.showCustomColorDialog(frameProvider, "L_col", Color.black);
        if (lineColor != null) {
            foldedFigureModel.setLineColor(lineColor);
        }
    }
}
