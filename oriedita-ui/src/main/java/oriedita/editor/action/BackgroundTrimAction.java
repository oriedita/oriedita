package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.Canvas;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Background_camera;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

@ApplicationScoped
@ActionHandler(ActionType.backgroundTrimAction)
public class BackgroundTrimAction extends AbstractOrieditaAction{

    @Inject
    BackgroundModel backgroundModel;
    @Inject
    Canvas canvas;
    @Inject
    CanvasModel canvasModel;
    @Inject @Named("mainCreasePattern_Worker")
    CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public BackgroundTrimAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedImage offsc_background = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2_background = offsc_background.createGraphics();
        //背景表示
        Image backgroundImage = backgroundModel.getBackgroundImage();

        if ((backgroundImage != null) && backgroundModel.isDisplayBackground()) {
            int iw = backgroundImage.getWidth(null);//イメージの幅を取得
            int ih = backgroundImage.getHeight(null);//イメージの高さを取得

            canvas.getH_cam().setBackgroundWidth(iw);
            canvas.getH_cam().setBackgroundHeight(ih);

            canvas.updateBackgroundCamera();
            canvas.drawBackground(g2_background, backgroundImage);
        }

//枠設定時の背景を枠内のみ残してトリム 20181204
        if ((canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61) && (mainCreasePatternWorker.getDrawingStage() == 4)) {//枠線が表示されている状態
            int xmin = (int) mainCreasePatternWorker.getOperationFrameBox().getXMin();
            int xmax = (int) mainCreasePatternWorker.getOperationFrameBox().getXMax();
            int ymin = (int) mainCreasePatternWorker.getOperationFrameBox().getYMin();
            int ymax = (int) mainCreasePatternWorker.getOperationFrameBox().getYMax();

            backgroundModel.setBackgroundImage(offsc_background.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin));

            canvas.setH_cam(new Background_camera());

            backgroundModel.setBackgroundPosition(new Polygon(new Point(120.0, 120.0),
                    new Point(120.0 + 10.0, 120.0),
                    new Point(xmin, ymin),
                    new Point((double) xmin + 10.0, ymin)));

            if (backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                canvas.getH_cam().setLocked(true);
                canvas.getH_cam().setCamera(canvas.getCreasePatternCamera());
                canvas.getH_cam().h3_obj_and_h4_obj_calculation();
            }
        }
    }
}
