package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.dialog.OpenFrame;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.ad_fncAction)
public class AdFncAction extends AbstractOrieditaAction{
    OpenFrame openFrame;

    @Inject
    CanvasModel canvasModel;

    @Inject
    FrameProvider frameProvider;

    @Inject
    ButtonService buttonService;

    @Override
    public void actionPerformed(ActionEvent e) {
        openFrame = new OpenFrame("additionalFrame", frameProvider.get(), buttonService);

        openFrame.setData(null, canvasModel);
        openFrame.setLocationRelativeTo(frameProvider.get());
        openFrame.setVisible(true);
    }
}
