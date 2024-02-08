package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.swing.CustomColorChooserPanel;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
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

//        Color frontColor = JColorChooser.showDialog(frameProvider.get(), "F_col", Color.white);
//
//        if (frontColor != null) {
//            foldedFigureModel.setFrontColor(frontColor);
//        }

        JFrame frame = new JFrame();
        frame.setMinimumSize(new Dimension(700, 250));
        frame.setPreferredSize(new Dimension(700, 250));
        frame.setLocationRelativeTo(null);

        JPanel colorChooserPanel = new CustomColorChooserPanel(foldedFigureModel, foldedFigureModel.getFrontColor());
        frame.add(colorChooserPanel);

        frame.setVisible(true);
    }
}
