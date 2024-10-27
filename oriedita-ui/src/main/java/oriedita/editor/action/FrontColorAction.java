package oriedita.editor.action;

import com.formdev.flatlaf.FlatLaf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;

import javax.swing.JColorChooser;
import java.awt.Color;
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
        Color frontColor = JColorChooser.showDialog(frameProvider.get(), "F_col", FlatLaf.isLafDark() ? Colors.FIGURE_FRONT_DARK : Colors.FIGURE_FRONT);

        if (frontColor != null) {
            foldedFigureModel.setFrontColor(frontColor);
        }
    }
}
