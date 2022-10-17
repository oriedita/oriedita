package oriedita.editor.action2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.Canvas;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.drawing.tools.Background_camera;
import oriedita.editor.service.FileSaveService;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.readBackgroundAction)
public class ReadBackgroundAction extends AbstractOrieditaAction {
    @Inject
    FileSaveService fileSaveService;
    @Inject
    Canvas canvas;
    @Inject
    BackgroundModel backgroundModel;

    @Inject
    public ReadBackgroundAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean saved = fileSaveService.readBackgroundImageFromFile();

        if (!saved) return;

        canvas.setH_cam(new Background_camera());//20181202
        canvas.getH_cam().setLocked(backgroundModel.isLockBackground());

        int iw = backgroundModel.getBackgroundImage().getWidth(null);//イメージの幅を取得
        int ih = backgroundModel.getBackgroundImage().getHeight(null);//イメージの高さを取得

        canvas.getH_cam().setBackgroundWidth(iw);
        canvas.getH_cam().setBackgroundHeight(ih);

        if (backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
            canvas.getH_cam().setCamera(canvas.getCreasePatternCamera());
            canvas.getH_cam().h3_obj_and_h4_obj_calculation();
        }
    }
}
