package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Camera;
import origami.crease_pattern.element.Point;

import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.MOVE_CREASE_PATTERN_2)
public class MouseHandlerMoveCreasePattern implements MouseModeHandler {
    private final Point mouse_temp0 = new Point();//マウスの動作対応時に、一時的に使うTen

    private final Camera creasePatternCamera;
    private final FoldedFiguresList foldedFiguresList;
    private final CreasePattern_Worker mainCreasePatternWorker;

    @Inject
    public MouseHandlerMoveCreasePattern(@Named("creasePatternCamera") Camera creasePatternCamera, FoldedFiguresList foldedFiguresList, @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker) {
        this.creasePatternCamera = creasePatternCamera;
        this.foldedFiguresList = foldedFiguresList;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        creasePatternCamera.camera_position_specify_from_TV(p0);
        mouse_temp0.set(p0);
    }

    @Override
    public void mouseDragged(Point p0) {
        creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
        mainCreasePatternWorker.setCamera(creasePatternCamera);

//20180225追加
        for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = foldedFiguresList.getElementAt(i_oz);

            OZi.getFoldedFigureCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getFoldedFigureFrontCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getFoldedFigureRearCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getTransparentFrontCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getTransparentRearCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        mouse_temp0.set(p0);
    }

    @Override
    public void mouseReleased(Point p0) {
        creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p0));
        mainCreasePatternWorker.setCamera(creasePatternCamera);


//20180225追加
        for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
            FoldedFigure_Drawer OZi = foldedFiguresList.getElementAt(i_oz);

            OZi.getFoldedFigureCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getFoldedFigureFrontCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getFoldedFigureRearCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getTransparentFrontCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
            OZi.getTransparentRearCamera().displayPositionMove(mouse_temp0.other_Point_position(p0));
        }
//20180225追加　ここまで

        mouse_temp0.set(p0);
    }
}
