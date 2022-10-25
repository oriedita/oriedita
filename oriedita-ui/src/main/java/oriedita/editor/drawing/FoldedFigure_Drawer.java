package oriedita.editor.drawing;

import org.tinylog.Logger;
import oriedita.editor.Foldable;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.folded_figure.FoldedFigure_01;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.linesegmentset.GetBoundingBox;
import origami.folding.FoldedFigure;

import java.awt.Color;
import java.awt.Graphics;

public class FoldedFigure_Drawer implements Foldable {
    private final FoldedFigure_01 foldedFigure;
    private final FoldedFigure_Worker_Drawer foldedFigure_worker_drawer;
    private final WireFrame_Worker_Drawer wireFrame_worker_drawer1;
    private final WireFrame_Worker_Drawer wireFrame_worker_drawer2;
    private final FoldedFigureModel foldedFigureModel = new FoldedFigureModel();
    private final Camera foldedFigureCamera = new Camera();
    private final Camera foldedFigureFrontCamera = new Camera();//折り上がり
    private final Camera foldedFigureRearCamera = new Camera();
    private final Camera transparentFrontCamera = new Camera();
    private final Camera transparentRearCamera = new Camera();
    private origami.crease_pattern.element.Polygon boundingBox;
    private Color foldedFigure_F_color = new Color(255, 255, 50);//Folded surface color
    private Color foldedFigure_B_color = new Color(233, 233, 233);//The color of the back side of the folded figure
    private Color foldedFigure_L_color = Color.black;//Folded line color
    private double d_foldedFigure_scale_factor = 1.0;//Scale factor of folded view
    private double d_foldedFigure_rotation_correction = 0.0;//Correction angle of rotation display angle of folded view
    private boolean transparencyColor = false;//1 if the transparency is in color, 0 otherwise
    private int transparent_transparency = 16;//Transparency when drawing a transparent diagram in color

    /**
     * Standard face, -1 means try to find the face which contains 0,0 or select face 1. Can be updated between folds.
     */
    private int startingFaceId = -1;

    public FoldedFigure_Drawer(FoldedFigure_01 foldedFigure) {
        this.foldedFigure = foldedFigure;
        foldedFigure_worker_drawer = new FoldedFigure_Worker_Drawer(foldedFigure.foldedFigure_worker);
        wireFrame_worker_drawer1 = new WireFrame_Worker_Drawer(foldedFigure.wireFrame_worker1);
        wireFrame_worker_drawer2 = new WireFrame_Worker_Drawer(foldedFigure.wireFrame_worker2);

        //Camera settings ------------------------------------------------------------------
        foldedFigure_camera_initialize();
        //This is the end of the camera settings ----------------------------------------------------
    }

    public int getStartingFaceId() {
        return startingFaceId;
    }

    public void setStartingFaceId(int startingFaceId) {
        this.startingFaceId = startingFaceId;
    }

    public void foldedFigure_camera_initialize() {
        initializeCamera(foldedFigureCamera, 1.0);
        initializeCamera(foldedFigureFrontCamera, 1.0);
        initializeCamera(foldedFigureRearCamera, -1.0);
        initializeCamera(transparentFrontCamera, 1.0);
        initializeCamera(transparentRearCamera, -1.0);
    }

    private void initializeCamera(Camera cam, double mirror) {
        cam.setCameraPositionX(0.0);
        cam.setCameraPositionY(0.0);
        cam.setCameraAngle(0.0);
        cam.setCameraMirror(mirror);
        cam.setCameraZoomX(1.0);
        cam.setCameraZoomY(1.0);
        cam.setDisplayPositionX(350.0);
        cam.setDisplayPositionY(350.0);
    }

    void folding_estimation_camera_configure(Camera creasePatternCamera) {
        d_foldedFigure_scale_factor = creasePatternCamera.getCameraZoomX();
        d_foldedFigure_rotation_correction = creasePatternCamera.getCameraAngle();

        foldedFigureModel.setScale(d_foldedFigure_scale_factor);
        foldedFigureModel.setRotation(d_foldedFigure_rotation_correction);

        Logger.info("wireFrame_worker1.ten_of_kijyunmen_ob     " + wireFrame_worker_drawer1.getStartingFacePointTV(startingFaceId).getX());

        Point p0 = new Point();
        Point p = new Point();

        p.set(wireFrame_worker_drawer1.getStartingFacePoint(startingFaceId));
        p0.set(creasePatternCamera.object2TV(p));

        double cameraPositionX = p.getX();
        double cameraPositionY = p.getY();
        double displayPositionX = p0.getX();
        double displayPositionY = p0.getY();

        foldedFigureCamera.setCamera(creasePatternCamera);
        foldedFigureCamera.setCameraPositionX(cameraPositionX);
        foldedFigureCamera.setCameraPositionY(cameraPositionY);
        foldedFigureCamera.setDisplayPositionX(displayPositionX + 20.0);
        foldedFigureCamera.setDisplayPositionY(displayPositionY + 20.0);

        foldedFigureFrontCamera.setCamera(creasePatternCamera);
        foldedFigureFrontCamera.setCameraPositionX(cameraPositionX);
        foldedFigureFrontCamera.setCameraPositionY(cameraPositionY);
        foldedFigureFrontCamera.setDisplayPositionX(displayPositionX + 20.0);
        foldedFigureFrontCamera.setDisplayPositionY(displayPositionY + 20.0);

        foldedFigureRearCamera.setCamera(creasePatternCamera);
        foldedFigureRearCamera.setCameraPositionX(cameraPositionX);
        foldedFigureRearCamera.setCameraPositionY(cameraPositionY);
        foldedFigureRearCamera.setDisplayPositionX(displayPositionX + 40.0);
        foldedFigureRearCamera.setDisplayPositionY(displayPositionY + 20.0);

        transparentFrontCamera.setCamera(creasePatternCamera);
        transparentFrontCamera.setCameraPositionX(cameraPositionX);
        transparentFrontCamera.setCameraPositionY(cameraPositionY);
        transparentFrontCamera.setDisplayPositionX(displayPositionX + 20.0);
        transparentFrontCamera.setDisplayPositionY(displayPositionY + 0.0);

        transparentRearCamera.setCamera(creasePatternCamera);
        transparentRearCamera.setCameraPositionX(cameraPositionX);
        transparentRearCamera.setCameraPositionY(cameraPositionY);
        transparentRearCamera.setDisplayPositionX(displayPositionX + 40.0);
        transparentRearCamera.setDisplayPositionY(displayPositionY + 0.0);

        double d_camera_mirror = foldedFigureRearCamera.getCameraMirror();
        foldedFigureRearCamera.setCameraMirror(d_camera_mirror * -1.0);
        transparentRearCamera.setCameraMirror(d_camera_mirror * -1.0);
    }

    public origami.crease_pattern.element.Polygon getBoundingBox() {
        return boundingBox;
    }

    public void folding_estimated(Camera creasePatternCamera, LineSegmentSet lineSegmentSet) throws InterruptedException, FoldingException {//折畳み予測の最初に、wireFrame_worker1.lineStore2pointStore(lineStore)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
        boolean i_camera_estimated = foldedFigure.estimationStep == FoldedFigure.EstimationStep.STEP_0
                && foldedFigure.estimationOrder.isAtMost(FoldedFigure.EstimationOrder.ORDER_5);
        boolean shouldCalculateFromTop = foldedFigure.estimationOrder.isAtLeast(FoldedFigure.EstimationOrder.ORDER_4);

        boundingBox = GetBoundingBox.getBoundingBox(lineSegmentSet);

        foldedFigure.folding_estimated(lineSegmentSet, startingFaceId);

        if (shouldCalculateFromTop) {
            foldedFigure_worker_drawer.calculateFromTopCountedPosition();
        }
        if (i_camera_estimated) {
            folding_estimation_camera_configure(creasePatternCamera);
        }
    }

    @Override
    public void setEstimationOrder(FoldedFigure.EstimationOrder estimationOrder) {
        foldedFigure.estimationOrder = estimationOrder;
    }

    @Override
    public void estimated_initialize() {
        foldedFigure.estimated_initialize();
    }

    @Override
    public String getTextResult() {
        return foldedFigure.text_result;
    }

    @Override
    public void setTextResult(String textResult) {
        foldedFigure.text_result = textResult;
    }

    public void createTwoColorCreasePattern(Camera camera_of_foldLine_diagram, LineSegmentSet Ss0) throws InterruptedException {//Two-color crease pattern
        //Folded view display camera settings

        d_foldedFigure_scale_factor = camera_of_foldLine_diagram.getCameraZoomX();
        d_foldedFigure_rotation_correction = camera_of_foldLine_diagram.getCameraAngle();

        foldedFigureModel.setScale(d_foldedFigure_scale_factor);
        foldedFigureModel.setRotation(d_foldedFigure_rotation_correction);

        double d_display_position_x = camera_of_foldLine_diagram.getDisplayPositionX();
        double d_display_position_y = camera_of_foldLine_diagram.getDisplayPositionY();

        foldedFigureCamera.setCamera(camera_of_foldLine_diagram);
        foldedFigureCamera.setDisplayPositionX(d_display_position_x + 20.0);
        foldedFigureCamera.setDisplayPositionY(d_display_position_y + 20.0);

        foldedFigureFrontCamera.setCamera(camera_of_foldLine_diagram);
        foldedFigureFrontCamera.setDisplayPositionX(d_display_position_x + 20.0);
        foldedFigureFrontCamera.setDisplayPositionY(d_display_position_y + 20.0);

        foldedFigureRearCamera.setCamera(camera_of_foldLine_diagram);
        foldedFigureRearCamera.setDisplayPositionX(d_display_position_x + 40.0);
        foldedFigureRearCamera.setDisplayPositionY(d_display_position_y + 20.0);

        transparentFrontCamera.setCamera(camera_of_foldLine_diagram);
        transparentFrontCamera.setDisplayPositionX(d_display_position_x + 20.0);
        transparentFrontCamera.setDisplayPositionY(d_display_position_y + 0.0);

        transparentRearCamera.setCamera(camera_of_foldLine_diagram);
        transparentRearCamera.setDisplayPositionX(d_display_position_x + 40.0);
        transparentRearCamera.setDisplayPositionY(d_display_position_y + 0.0);

        double d_camera_mirror = foldedFigureRearCamera.getCameraMirror();
        foldedFigureRearCamera.setCameraMirror(d_camera_mirror * -1.0);
        transparentRearCamera.setCameraMirror(d_camera_mirror * -1.0);

        foldedFigure.createTwoColorCreasePattern(Ss0, startingFaceId);

        foldedFigure_worker_drawer.calculateFromTopCountedPosition();
    }

    public void foldUp_draw(Graphics bufferGraphics, boolean displayMark, int index, boolean selected) {
        //displayStyle==2,ip4==0  front
        //displayStyle==2,ip4==1	rear
        //displayStyle==2,ip4==2	front & rear
        //displayStyle==2,ip4==3	front & rear

        //displayStyle==3,ip4==0  front
        //displayStyle==3,ip4==1	rear
        //displayStyle==3,ip4==2	front & rear
        //displayStyle==3,ip4==3	front & rear

        //displayStyle==5,ip4==0  front
        //displayStyle==5,ip4==1	rear
        //displayStyle==5,ip4==2	front & rear
        //displayStyle==5,ip4==3	front & rear & front2 & rear2

        //Since foldedFigure_worker displays the folded figure, it is not necessary to set the camera in wireFrame_worker2 for the display itself, but after that, wireFrame_worker2 judges the screen click, so it is necessary to update the camera of wireFrame_worker2 in synchronization with the display. ..
        wireFrame_worker_drawer2.setCamera(foldedFigureCamera);
        wireFrame_worker_drawer2.setCam_front(foldedFigureFrontCamera);
        wireFrame_worker_drawer2.setCam_rear(foldedFigureRearCamera);
        wireFrame_worker_drawer2.setCam_transparent_front(transparentFrontCamera);
        wireFrame_worker_drawer2.setCam_transparent_rear(transparentRearCamera);

        //Wire diagram display
        if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) {
            wireFrame_worker_drawer2.drawing_with_camera(bufferGraphics, foldedFigure.ip4);//The operation of the fold-up diagram moves the wire diagram of this wireFrame_worker2.
        }

        //Display of folded figure (table)
        if (((foldedFigure.ip4 == FoldedFigure.State.FRONT_0) || (foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            foldedFigure_worker_drawer.setCamera(foldedFigureFrontCamera);

            //Display of transparency
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle; Specify the display style of the folded figure. If it is 1, it is the same as when actually folding origami. If it is 2, it is a transparent view. If it is 3, it is a wire diagram.
                foldedFigure_worker_drawer.draw_transparency_with_camera(bufferGraphics, wireFrame_worker_drawer1, foldedFigure.wireFrame_worker2.get(), foldedFigure.wireFrame_worker3.get(), transparencyColor, transparent_transparency);
            }

            //Display of folded figure *************
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                foldedFigure_worker_drawer.draw_foldedFigure_with_camera(bufferGraphics, foldedFigure.wireFrame_worker1, foldedFigure.wireFrame_worker3.get());// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                foldedFigure_worker_drawer.draw_cross_with_camera(bufferGraphics, selected, index);
            }
        }

        //Display of folded figure (back)
        if (((foldedFigure.ip4 == FoldedFigure.State.BACK_1) || (foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) || (foldedFigure.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
            foldedFigure_worker_drawer.setCamera(foldedFigureRearCamera);

            //Display of transparency
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                foldedFigure_worker_drawer.draw_transparency_with_camera(bufferGraphics, wireFrame_worker_drawer1, foldedFigure.wireFrame_worker2.get(), foldedFigure.wireFrame_worker3.get(), transparencyColor, transparent_transparency);
            }

            //Display of folded figure ************* //Logger.info("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) {
                foldedFigure_worker_drawer.draw_foldedFigure_with_camera(bufferGraphics, foldedFigure.wireFrame_worker1, foldedFigure.wireFrame_worker3.get());// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                foldedFigure_worker_drawer.draw_cross_with_camera(bufferGraphics, selected, index);
            }
        }

        //Transparency map (added when the folded map is displayed)
        if ((foldedFigure.ip4 == FoldedFigure.State.TRANSPARENT_3) && (foldedFigure.displayStyle == FoldedFigure.DisplayStyle.PAPER_5)) {
            // ---------------------------------------------------------------------------------
            foldedFigure_worker_drawer.setCamera(transparentFrontCamera);
            //Display of transparency
            foldedFigure_worker_drawer.draw_transparency_with_camera(bufferGraphics, wireFrame_worker_drawer1, foldedFigure.wireFrame_worker2.get(), foldedFigure.wireFrame_worker3.get(), transparencyColor, transparent_transparency);

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                foldedFigure_worker_drawer.draw_cross_with_camera(bufferGraphics, selected, index);
            }

            foldedFigure_worker_drawer.setCamera(transparentRearCamera);

            //Display of transparency
            foldedFigure_worker_drawer.draw_transparency_with_camera(bufferGraphics, wireFrame_worker_drawer1, foldedFigure.wireFrame_worker2.get(), foldedFigure.wireFrame_worker3.get(), transparencyColor, transparent_transparency);

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                foldedFigure_worker_drawer.draw_cross_with_camera(bufferGraphics, selected, index);
            }
        }

        //Display of corresponding points on the wire diagram and development diagram when moving the fold-up diagram

        for (int i = 1; i <= foldedFigure.wireFrame_worker1.getPointsTotal(); i++) {
            if (foldedFigure.wireFrame_worker1.getPointState(i)) {
                wireFrame_worker_drawer1.drawing_pointId_with_camera(bufferGraphics, i);
            }
        }

        for (int i = 1; i <= foldedFigure.wireFrame_worker2.getPointsTotal(); i++) {
            if (foldedFigure.wireFrame_worker2.getPointState(i)) {
                wireFrame_worker_drawer1.drawing_pointId_with_camera_green(bufferGraphics, i);
                wireFrame_worker_drawer2.drawing_pointId_with_camera(bufferGraphics, i, foldedFigure.ip4);
            }
        }
    }

    public void record() {
        wireFrame_worker_drawer2.record();
    }

    public void redo() {
        wireFrame_worker_drawer2.redo();
        try {
            foldedFigure.folding_estimated_03();
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    public void undo() {
        wireFrame_worker_drawer2.undo();
        try {
            foldedFigure.folding_estimated_03();
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    public void setData(ApplicationModel applicationModel) {
        foldedFigure_worker_drawer.setData(applicationModel);
    }

    public void scale(double magnification, Point t_o2tv) {
        d_foldedFigure_scale_factor = d_foldedFigure_scale_factor * magnification;

        if (t_o2tv != null) {
            foldedFigureCamera.camera_position_specify_from_TV(t_o2tv);
            foldedFigureFrontCamera.camera_position_specify_from_TV(t_o2tv);
            foldedFigureRearCamera.camera_position_specify_from_TV(t_o2tv);
            transparentFrontCamera.camera_position_specify_from_TV(t_o2tv);
            transparentRearCamera.camera_position_specify_from_TV(t_o2tv);
        }

        foldedFigureCamera.multiplyCameraZoomX(magnification);
        foldedFigureCamera.multiplyCameraZoomY(magnification);

        foldedFigureFrontCamera.multiplyCameraZoomX(magnification);
        foldedFigureFrontCamera.multiplyCameraZoomY(magnification);

        foldedFigureRearCamera.multiplyCameraZoomX(magnification);
        foldedFigureRearCamera.multiplyCameraZoomY(magnification);

        transparentFrontCamera.multiplyCameraZoomX(magnification);
        transparentFrontCamera.multiplyCameraZoomY(magnification);

        transparentRearCamera.multiplyCameraZoomX(magnification);
        transparentRearCamera.multiplyCameraZoomY(magnification);
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        transparencyColor = foldedFigureModel.isTransparencyColor();
        transparent_transparency = foldedFigureModel.getTransparentTransparency();

        foldedFigure.setData(foldedFigureModel);
        foldedFigure_worker_drawer.setData(foldedFigureModel);
        foldedFigure_F_color = foldedFigureModel.getFrontColor();
        foldedFigure_B_color = foldedFigureModel.getBackColor();
        foldedFigure_L_color = foldedFigureModel.getLineColor();
        d_foldedFigure_rotation_correction = foldedFigureModel.getRotation();

        setScale(foldedFigureModel.getScale());

        // Update rotation
        foldedFigureCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        foldedFigureFrontCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        foldedFigureRearCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        transparentFrontCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        transparentRearCamera.setCameraAngle(d_foldedFigure_rotation_correction);
    }

    public void setScale(double scale) {
        d_foldedFigure_scale_factor = scale;
        foldedFigureCamera.setCameraZoomX(d_foldedFigure_scale_factor);
        foldedFigureCamera.setCameraZoomY(d_foldedFigure_scale_factor);
        foldedFigureFrontCamera.setCameraZoomX(d_foldedFigure_scale_factor);
        foldedFigureFrontCamera.setCameraZoomY(d_foldedFigure_scale_factor);
        foldedFigureRearCamera.setCameraZoomX(d_foldedFigure_scale_factor);
        foldedFigureRearCamera.setCameraZoomY(d_foldedFigure_scale_factor);
        transparentFrontCamera.setCameraZoomX(d_foldedFigure_scale_factor);
        transparentFrontCamera.setCameraZoomY(d_foldedFigure_scale_factor);
        transparentRearCamera.setCameraZoomX(d_foldedFigure_scale_factor);
        transparentRearCamera.setCameraZoomY(d_foldedFigure_scale_factor);
    }


    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigure.getData(foldedFigureModel);
        foldedFigure_worker_drawer.getData(foldedFigureModel);
        foldedFigureModel.setFrontColor(foldedFigure_F_color);
        foldedFigureModel.setBackColor(foldedFigure_B_color);
        foldedFigureModel.setLineColor(foldedFigure_L_color);
        foldedFigureModel.setRotation(d_foldedFigure_rotation_correction);
        foldedFigureModel.setScale(d_foldedFigure_scale_factor);
    }

    public FoldedFigure_01 getFoldedFigure() {
        return foldedFigure;
    }

    public FoldedFigure_Worker_Drawer getFoldedFigure_worker_drawer() {
        return foldedFigure_worker_drawer;
    }

    public WireFrame_Worker_Drawer getWireFrame_worker_drawer1() {
        return wireFrame_worker_drawer1;
    }

    public WireFrame_Worker_Drawer getWireFrame_worker_drawer2() {
        return wireFrame_worker_drawer2;
    }

    public FoldedFigureModel getFoldedFigureModel() {
        return foldedFigureModel;
    }

    public Camera getFoldedFigureCamera() {
        return foldedFigureCamera;
    }

    public Camera getFoldedFigureFrontCamera() {
        return foldedFigureFrontCamera;
    }

    public Camera getFoldedFigureRearCamera() {
        return foldedFigureRearCamera;
    }

    public Camera getTransparentFrontCamera() {
        return transparentFrontCamera;
    }

    public Camera getTransparentRearCamera() {
        return transparentRearCamera;
    }
}
