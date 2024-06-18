package oriedita.editor.action;

import com.formdev.flatlaf.FlatLaf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.swing.CustomHSVPanel;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        Color backColor = CustomHSVPanel.showCustomColorDialog(frameProvider, "B_col", FlatLaf.isLafDark() ? Colors.FIGURE_BACK_DARK : Colors.FIGURE_BACK);

        if (backColor != null) {
            foldedFigureModel.setBackColor(backColor);
        }
    }
}
