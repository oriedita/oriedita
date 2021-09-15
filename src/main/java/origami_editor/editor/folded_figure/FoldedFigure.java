package origami_editor.editor.folded_figure;

import origami_editor.editor.App;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.crease_pattern.worker.CreasePattern_Worker;
import origami.crease_pattern.worker.HierarchyList_Worker;
import origami.crease_pattern.element.Point;
import origami_editor.record.Memo;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.tools.Camera;
import origami.crease_pattern.LineSegmentSet;

import java.awt.*;

public class FoldedFigure {
    private final Point p_m_left_on = new Point();//Coordinates when the left mouse button is pressed
    private final Point move_previous_selection_point = new Point();//Coordinates of the selected point before moving
    public double d_foldedFigure_scale_factor = 1.0;//Scale factor of folded view
    public double d_foldedFigure_rotation_correction = 0.0;//Correction angle of rotation display angle of folded view
    public HierarchyList_Worker ct_worker;
    // The point set of cp_worker2 may have overlapping bars, so
    // Pass it to bb_worker once and organize it as a line segment set.
    public Camera foldedFigureCamera = new Camera();
    public Camera foldedFigureFrontCamera = new Camera();//折り上がり
    public Camera foldedFigureRearCamera = new Camera();
    public Camera transparentFrontCamera = new Camera();
    public Camera transparentRearCamera = new Camera();
    private Color foldedFigure_F_color = new Color(255, 255, 50);//Folded surface color
    private Color foldedFigure_B_color = new Color(233, 233, 233);//The color of the back side of the folded figure
    private Color foldedFigure_L_color = Color.black;//Folded line color
    public DisplayStyle display_flg_backup = DisplayStyle.DEVELOPMENT_4;//For temporary backup of display format displayStyle
    public DisplayStyle displayStyle = DisplayStyle.NONE_0;//Designation of the display style of the folded figure. 1 is a crease pattern, 2 is a wire drawing. If it is 3, it is a transparent view. If it is 4, it is the same as when you actually fold the origami paper.
    public EstimationOrder estimationOrder = EstimationOrder.ORDER_0;//Instructions on how far to perform folding estimation
    public EstimationStep estimationStep = EstimationStep.STEP_0;//Display of how far the folding estimation has been completed
    //Variable to store the value for display
    public HierarchyList_Worker.HierarchyListStatus ip1_anotherOverlapValid = HierarchyList_Worker.HierarchyListStatus.UNKNOWN_N1;// At the time of initial setting of the upper and lower front craftsmen, the front and back sides are the same after folding
    // A variable that stores 0 if there is an error of being adjacent, and 1000 if there is no error.
    // The initial value here can be any number other than (0 or 1000).
    public int ip2_possibleOverlap = -1;// When the top and bottom craftsmen look for a foldable stacking method,
    // A variable that stores 0 if there is no possible overlap, and 1000 if there is a possible overlap.
    // The initial value here can be any number other than (0 or 1000).
    //int ip3a=1;
    public int ip3 = 1;// Used by cp_worker1 to specify the reference plane for folding.
    public State ip4 = State.FRONT_0;// This specifies whether to flip over at the beginning of cp_worker1. Do not set to 0. If it is 1, turn it over.
    public int ip5 = -1;    // After the top and bottom craftsmen once show the overlap of foldable paper,
    public int ip6 = -1;    // After the top and bottom craftsmen once show the overlap of foldable paper,
    public boolean findAnotherOverlapValid = false;     //This takes 1 if "find another overlap" is valid, and 0 if it is invalid.
    public int discovered_fold_cases = 0;    //折り重なり方で、何通り発見したかを格納する。
    public int transparent_transparency = 16;//Transparency when drawing a transparent diagram in color
    public int i_foldedFigure_operation_mode = 1;//1 = When deformed, it becomes a wire diagram, and after deformation, the upper and lower tables are recalculated, the old mode,2 = A mode in which the folded figure remains even when deformed, and the upper and lower tables are basically not recalculated after deformation.
    public BulletinBoard bulletinBoard;
    // The result of the first ct_worker.susumu (SubFaceTotal) when looking for yet another paper overlap. If it was
    // 0, there was no room for new susumu. If non-zero, the smallest number of changed SubFace ids
    public boolean summary_write_image_during_execution = false;//matome_write_imageが実行中ならtureになる。これは、複数の折りあがり形の予測の書き出しがかすれないように使う。20170613
    // The result of ct_worker.kanou_kasanari_sagasi () when looking for another paper overlap. If
    // 0, there is no possible overlapping state.
    // If it is 1000, another way of overlapping was found.
    public String text_result;                //Instantiation of result display string class
    public boolean transparencyColor = false;//1 if the transparency is in color, 0 otherwise
    App app;
    double r = 3.0;                   //Criteria for determining the radius of the circles at both ends of the straight line of the basic branch structure and the proximity of the branches to various points
    public WireFrame_Worker bb_worker = new WireFrame_Worker(r);    //Basic branch craftsman. Before passing the point set of cp_worker2 to cp_worker3,
    public CreasePattern_Worker cp_worker1 = new CreasePattern_Worker(r);    //Net craftsman. Fold the input line segment set first to make a fold-up diagram of the wire-shaped point set.
    public CreasePattern_Worker cp_worker2 = new CreasePattern_Worker(r);    //Net craftsman. It holds the folded-up view of the wire-shaped point set created by cp_worker1 and functions as a line segment set.
    public CreasePattern_Worker cp_worker3 = new CreasePattern_Worker(r);    //Net craftsman. Organize the wire-shaped point set created by cp_worker1. It has functions such as recognizing a new surface.
    private int i_nanini_near = 0;//Point p is close to the point in the development view = 1, close to the point in the folded view = 2, not close to either = 0
    private int i_closestPointId;
    private PointSelection i_point_selection = PointSelection.NONE_0;//Both cp_worker1 and cp_worker2 are not selected (situation i_point_selection = 0), cp_worker1 is selected and cp_worker2 is not selected (situation i_point_selection = 1), and the vertex is cp_worker2 selected (situation i_point_selection = 2).

    public final FoldedFigureModel foldedFigureModel = new FoldedFigureModel();

    public FoldedFigure(App app0) {
        app = app0;

        ct_worker = new HierarchyList_Worker(app0.bulletinBoard);
        bulletinBoard = new BulletinBoard(app0);

        //Camera settings ------------------------------------------------------------------
        foldedFigure_camera_initialize();
        //This is the end of the camera settings ----------------------------------------------------

        text_result = "";
    }

    public void estimated_initialize() {
        text_result = "";
        bb_worker.reset();
        cp_worker1.reset();
        cp_worker2.reset();
        cp_worker3.reset();
        ct_worker.reset();

        displayStyle = DisplayStyle.NONE_0;
        estimationOrder = EstimationOrder.ORDER_0;//Instructions on how far to perform folding estimation
        estimationStep = EstimationStep.STEP_0;//Display of how far the folding estimation has been completed
        findAnotherOverlapValid = false;

        summary_write_image_during_execution = false; //If the export of multiple folded forecasts is in progress, it will be ture. 20170615
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

    public void foldUp_draw(Graphics bufferGraphics, boolean displayMark) {
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

        //Since ct_worker displays the folded figure, it is not necessary to set the camera in cp_worker2 for the display itself, but after that, cp_worker2 judges the screen click, so it is necessary to update the camera of cp_worker2 in synchronization with the display. ..
        cp_worker2.setCamera(foldedFigureCamera);
        cp_worker2.setCam_front(foldedFigureFrontCamera);
        cp_worker2.setCam_rear(foldedFigureRearCamera);
        cp_worker2.setCam_transparent_front(transparentFrontCamera);
        cp_worker2.setCam_transparent_rear(transparentRearCamera);

        //Wire diagram display
        if (displayStyle == DisplayStyle.WIRE_2) {
            cp_worker2.drawing_with_camera(bufferGraphics, ip4);//The operation of the fold-up diagram moves the wire diagram of this cp_worker2.
        }

        //Display of folded figure (table)
        if (((ip4 == State.FRONT_0) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {
            ct_worker.setCamera(foldedFigureFrontCamera);

            //Display of transparency
            if (displayStyle == DisplayStyle.TRANSPARENT_3) {        // displayStyle; Specify the display style of the folded figure. If it is 1, it is the same as when actually folding origami. If it is 2, it is a transparent view. If it is 3, it is a wire diagram.
                ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);
            }

            //Display of folded figure *************
            if (displayStyle == DisplayStyle.PAPER_5) {
                ct_worker.draw_foldedFigure_with_camera(bufferGraphics, cp_worker1, cp_worker3.get());// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }
        }

        //Display of folded figure (back)
        if (((ip4 == State.BACK_1) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {
            foldedFigureRearCamera.display();
            ct_worker.setCamera(foldedFigureRearCamera);

            //Display of transparency
            if (displayStyle == DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);
            }

            //Display of folded figure ************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (displayStyle == DisplayStyle.PAPER_5) {
                ct_worker.draw_foldedFigure_with_camera(bufferGraphics, cp_worker1, cp_worker3.get());// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }
        }

        //Transparency map (added when the folded map is displayed)
        if ((ip4 == State.TRANSPARENT_3) && (displayStyle == DisplayStyle.PAPER_5)) {
            // ---------------------------------------------------------------------------------
            ct_worker.setCamera(transparentFrontCamera);
            //Display of transparency
            ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }

            ct_worker.setCamera(transparentRearCamera);

            //Display of transparency
            ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);

            //Cross-shaped display at the center of movement of the folded figure
            if (displayMark) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }
        }

        //Display of corresponding points on the wire diagram and development diagram when moving the fold-up diagram

        for (int i = 1; i <= cp_worker1.getPointsTotal(); i++) {
            if (cp_worker1.getPointState(i)) {
                cp_worker1.drawing_pointId_with_camera(bufferGraphics, i);
            }
        }

        for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
            if (cp_worker2.getPointState(i)) {
                cp_worker1.drawing_pointId_with_camera_green(bufferGraphics, i);
                cp_worker2.drawing_pointId_with_camera(bufferGraphics, i, ip4);
            }
        }
    }

    public Memo getMemo_for_svg_export() {
        Memo memo_temp = new Memo();

        //Wire diagram svg
        if (displayStyle == DisplayStyle.WIRE_2) {
            memo_temp.addMemo(ct_worker.getMemo_wirediagram_for_svg_export(cp_worker1, cp_worker2.get(), false));//If the fourth integer is 0, only the frame of the face is painted, and if it is 1, the face is painted.
        }

        //Folded figure (table) svg
        if (((ip4 == State.FRONT_0) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {
            ct_worker.setCamera(foldedFigureFrontCamera);

            //透過図のsvg
            if (displayStyle == DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(ct_worker.getMemo_wirediagram_for_svg_export(cp_worker1, cp_worker2.get(), true));
            }

            //折り上がり図のsvg*************
            if (displayStyle == DisplayStyle.PAPER_5) {
                //ct_worker.oekaki_oriagarizu_with_camera(bufferGraphics,cp_worker1,cp_worker2.get(),cp_worker3.get());// displayStyle; Specify the display style of the folded figure. If it is 5, it is the same as when you actually fold the origami paper. If it is 3, it is a transparent view. If it is 2, it is a wire diagram.
                memo_temp.addMemo(ct_worker.getMemo_for_svg_with_camera(cp_worker1, cp_worker3.get()));// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。

            }
        }

        //折りあがり図（裏）のsvg
        if (((ip4 == State.BACK_1) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {

            ct_worker.setCamera(foldedFigureRearCamera);

            //透過図のsvg
            if (displayStyle == DisplayStyle.TRANSPARENT_3) {        // displayStyle;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(ct_worker.getMemo_wirediagram_for_svg_export(cp_worker1, cp_worker2.get(), true));
            }

            //折り上がり図のsvg*************
            if (displayStyle == DisplayStyle.PAPER_5) {
                memo_temp.addMemo(ct_worker.getMemo_for_svg_with_camera(cp_worker1, cp_worker3.get()));// displayStyle;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }

        //透過図（折りあがり図表示時に追加する分）
        if ((ip4 == State.TRANSPARENT_3) && (displayStyle == DisplayStyle.PAPER_5)) {
            // ---------------------------------------------------------------------------------
            ct_worker.setCamera(transparentFrontCamera);
            //透過図のsvg
            ct_worker.setCamera(transparentRearCamera);

            //透過図のsvg
        }
        return memo_temp;

    }

    void folding_estimation_camera_configure(Camera creasePatternCamera, LineSegmentSet Ss0) {
        d_foldedFigure_scale_factor = creasePatternCamera.getCameraZoomX();
        d_foldedFigure_rotation_correction = creasePatternCamera.getCameraAngle();

        app.foldedFigureModel.setScale(d_foldedFigure_scale_factor);
        app.foldedFigureModel.setRotation(d_foldedFigure_rotation_correction);

        System.out.println("cp_worker1.ten_of_kijyunmen_ob     " + cp_worker1.point_of_referencePlane_ob.getX());

        Point p0 = new Point();
        Point p = new Point();

        p.set(cp_worker1.point_of_referencePlane_ob);
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

    public void folding_estimated(Camera creasePatternCamera, LineSegmentSet lineSegmentSet) {//折畳み予測の最初に、cp_worker1.lineStore2pointStore(lineStore)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
        boolean i_camera_estimated = (estimationStep == EstimationStep.STEP_0) && (estimationOrder.isBelowOrEqual5());

        //Folded view display camera settings

        if (estimationOrder == EstimationOrder.ORDER_51) {
            estimationOrder = EstimationOrder.ORDER_5;
        }
        //-------------------------------
        // suitei = estimated
        // dankai = step
        // meirei = order
        if ((estimationStep == EstimationStep.STEP_0) && (estimationOrder == EstimationOrder.ORDER_1)) {
            estimated_initialize(); // estimated_initialize
            folding_estimated_01(lineSegmentSet);
            estimationStep = EstimationStep.STEP_1;
            displayStyle = DisplayStyle.DEVELOPMENT_1;
        } else if ((estimationStep == EstimationStep.STEP_0) && (estimationOrder == EstimationOrder.ORDER_2)) {
            estimated_initialize();
            folding_estimated_01(lineSegmentSet);
            estimationStep = EstimationStep.STEP_1;
            displayStyle = DisplayStyle.DEVELOPMENT_1;
            folding_estimated_02();
            estimationStep = EstimationStep.STEP_2;
            displayStyle = DisplayStyle.WIRE_2;
        } else if ((estimationStep == EstimationStep.STEP_0) && (estimationOrder == EstimationOrder.ORDER_3)) {
            estimated_initialize();
            folding_estimated_01(lineSegmentSet);
            estimationStep = EstimationStep.STEP_1;
            displayStyle = DisplayStyle.DEVELOPMENT_1;
            folding_estimated_02();
            estimationStep = EstimationStep.STEP_2;
            displayStyle = DisplayStyle.WIRE_2;
            folding_estimated_03();
            estimationStep = EstimationStep.STEP_3;
            displayStyle = DisplayStyle.TRANSPARENT_3;
        } else if ((estimationStep == EstimationStep.STEP_0) && (estimationOrder == EstimationOrder.ORDER_5)) {
            estimated_initialize();
            folding_estimated_01(lineSegmentSet);
            estimationStep = EstimationStep.STEP_1;
            displayStyle = DisplayStyle.DEVELOPMENT_1;
            folding_estimated_02();
            estimationStep = EstimationStep.STEP_2;
            displayStyle = DisplayStyle.WIRE_2;
            folding_estimated_03();
            estimationStep = EstimationStep.STEP_3;
            displayStyle = DisplayStyle.TRANSPARENT_3;
            folding_estimated_04();
            estimationStep = EstimationStep.STEP_4;
            displayStyle = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            estimationStep = EstimationStep.STEP_5;
            displayStyle = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && (!findAnotherOverlapValid)) {
                estimationStep = EstimationStep.STEP_3;
                displayStyle = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((estimationStep == EstimationStep.STEP_1) && (estimationOrder == EstimationOrder.ORDER_1)) {
        } else if ((estimationStep == EstimationStep.STEP_1) && (estimationOrder == EstimationOrder.ORDER_2)) {
            folding_estimated_02();
            estimationStep = EstimationStep.STEP_2;
            displayStyle = DisplayStyle.WIRE_2;
        } else if ((estimationStep == EstimationStep.STEP_1) && (estimationOrder == EstimationOrder.ORDER_3)) {
            folding_estimated_02();
            estimationStep = EstimationStep.STEP_2;
            displayStyle = DisplayStyle.WIRE_2;
            folding_estimated_03();
            estimationStep = EstimationStep.STEP_3;
            displayStyle = DisplayStyle.TRANSPARENT_3;
        } else if ((estimationStep == EstimationStep.STEP_1) && (estimationOrder == EstimationOrder.ORDER_5)) {
            folding_estimated_02();
            estimationStep = EstimationStep.STEP_2;
            displayStyle = DisplayStyle.WIRE_2;
            folding_estimated_03();
            estimationStep = EstimationStep.STEP_3;
            displayStyle = DisplayStyle.TRANSPARENT_3;
            folding_estimated_04();
            estimationStep = EstimationStep.STEP_4;
            displayStyle = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            estimationStep = EstimationStep.STEP_5;
            displayStyle = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && (!findAnotherOverlapValid)) {
                estimationStep = EstimationStep.STEP_3;
                displayStyle = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((estimationStep == EstimationStep.STEP_2) && (estimationOrder == EstimationOrder.ORDER_1)) {
        } else if ((estimationStep == EstimationStep.STEP_2) && (estimationOrder == EstimationOrder.ORDER_2)) {
        } else if ((estimationStep == EstimationStep.STEP_2) && (estimationOrder == EstimationOrder.ORDER_3)) {
            folding_estimated_03();
            estimationStep = EstimationStep.STEP_3;
            displayStyle = DisplayStyle.TRANSPARENT_3;
        } else if ((estimationStep == EstimationStep.STEP_2) && (estimationOrder == EstimationOrder.ORDER_5)) {
            folding_estimated_03();
            estimationStep = EstimationStep.STEP_3;
            displayStyle = DisplayStyle.TRANSPARENT_3;
            folding_estimated_04();
            estimationStep = EstimationStep.STEP_4;
            displayStyle = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            estimationStep = EstimationStep.STEP_5;
            displayStyle = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && !findAnotherOverlapValid) {
                estimationStep = EstimationStep.STEP_3;
                displayStyle = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((estimationStep == EstimationStep.STEP_3) && (estimationOrder == EstimationOrder.ORDER_1)) {
        } else if ((estimationStep == EstimationStep.STEP_3) && (estimationOrder == EstimationOrder.ORDER_2)) {
            displayStyle = DisplayStyle.WIRE_2;
        } else if ((estimationStep == EstimationStep.STEP_3) && (estimationOrder == EstimationOrder.ORDER_3)) {
            displayStyle = DisplayStyle.TRANSPARENT_3;
        } else if ((estimationStep == EstimationStep.STEP_3) && (estimationOrder == EstimationOrder.ORDER_5)) {
            folding_estimated_04();
            estimationStep = EstimationStep.STEP_4;
            displayStyle = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            estimationStep = EstimationStep.STEP_5;
            displayStyle = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && !findAnotherOverlapValid) {
                estimationStep = EstimationStep.STEP_3;
                displayStyle = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((estimationStep == EstimationStep.STEP_5) && (estimationOrder == EstimationOrder.ORDER_1)) {
        } else if ((estimationStep == EstimationStep.STEP_5) && (estimationOrder == EstimationOrder.ORDER_2)) {
            displayStyle = DisplayStyle.WIRE_2;
        } else if ((estimationStep == EstimationStep.STEP_5) && (estimationOrder == EstimationOrder.ORDER_3)) {
            displayStyle = DisplayStyle.TRANSPARENT_3;
        } else if ((estimationStep == EstimationStep.STEP_5) && (estimationOrder == EstimationOrder.ORDER_5)) {
            displayStyle = DisplayStyle.PAPER_5;
        } else if ((estimationStep == EstimationStep.STEP_5) && (estimationOrder == EstimationOrder.ORDER_6)) {
            folding_estimated_05();
            estimationStep = EstimationStep.STEP_5;
            displayStyle = DisplayStyle.PAPER_5;
        }

        if (i_camera_estimated) {
            folding_estimation_camera_configure(creasePatternCamera, lineSegmentSet);
        }
    }

    public void createTwoColorCreasePattern(Camera camera_of_foldLine_diagram, LineSegmentSet Ss0) {//Two-color crease pattern
        //Folded view display camera settings

        d_foldedFigure_scale_factor = camera_of_foldLine_diagram.getCameraZoomX();
        d_foldedFigure_rotation_correction = camera_of_foldLine_diagram.getCameraAngle();

        app.foldedFigureModel.setScale(d_foldedFigure_scale_factor);
        app.foldedFigureModel.setRotation(d_foldedFigure_rotation_correction);

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

        estimated_initialize();
        folding_estimated_01(Ss0);
        estimationStep = EstimationStep.STEP_1;
        displayStyle = DisplayStyle.DEVELOPMENT_1;
        folding_estimated_02col();
        estimationStep = EstimationStep.STEP_2;
        displayStyle = DisplayStyle.WIRE_2;
        folding_estimated_03();
        estimationStep = EstimationStep.STEP_3;
        displayStyle = DisplayStyle.TRANSPARENT_3;
        folding_estimated_04();
        estimationStep = EstimationStep.STEP_4;
        displayStyle = DisplayStyle.DEVELOPMENT_4;
        folding_estimated_05();
        estimationStep = EstimationStep.STEP_5;
        displayStyle = DisplayStyle.PAPER_5;
        estimationStep = EstimationStep.STEP_10;
    }

    public int folding_estimated_01(LineSegmentSet lineSegmentSet) {
        System.out.println("＜＜＜＜＜oritatami_suitei_01;開始");
        bulletinBoard.write("<<<<oritatami_suitei_01;  start");
        // Pass the line segment set created in mainDrawingWorker to cp_worker1 by mouse input and make it a point set (corresponding to the development view).
        cp_worker1.lineStore2pointStore(lineSegmentSet);
        ip3 = cp_worker1.setReferencePlaneId(ip3);
        ip3 = cp_worker1.setReferencePlaneId(app.point_of_referencePlane_old);//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加

        return 1000;
    }

    public int folding_estimated_02() {
        System.out.println("＜＜＜＜＜oritatami_suitei_02;開始");
        bulletinBoard.write("<<<<oritatami_suitei_02;  start");
        //cp_worker1が折りたたみを行い、できた針金図をcp_worker2に渡す。
        //cp_worker1 folds and passes the resulting wire diagram to cp_worker2.
        //cp_worker2が折りあがった形を少しだけ変形したいような場合に働く。
        //It works when you want to slightly deform the folded shape of cp_worker2.
        cp_worker2.set(cp_worker1.folding());
        app.bulletinBoard.write("<<<<oritatami_suitei_02; end");

        //cp_worker2.Iti_sitei(0.0 , 0.0);点集合の平均位置を全点の重心にする。
        //  if(ip4==1){ cp_worker2.uragaesi();}
        // cp_worker2.set( cp_worker2.oritatami())  ;//折り畳んだ針金図を、折り開きたい場合の操作
        //ここまでで針金図はできていて、cp_worker2が持っている。これは、マウスで操作、変形できる。
        return 1000;
    }

    public int folding_estimated_02col() {//20171225　２色塗りわけをするための特別推定（折り畳み位置を推定しない）
        System.out.println("＜＜＜＜＜oritatami_suitei_02;開始");
        bulletinBoard.write("<<<<oritatami_suitei_02;  start");
        cp_worker2.set(cp_worker1.surface_position_request());
        app.bulletinBoard.write("<<<<oritatami_suitei_02; end");
        return 1000;
    }

    public int folding_estimated_03() {
        System.out.println("＜＜＜＜＜oritatami_suitei_03;開始");
        bulletinBoard.write("<<<<oritatami_suitei_03;  start");
        // cp_worker2 has a set of points that holds the faces of the unfolded view before folding.
        // To estimate the vertical relationship of the surface when folded, set the surface according to the wire diagram of cp_worker2.
        // Use a set of subdivided points (let's call the subdivided surface SubFace).
        // Let cp_worker3 have the set of points divided into this SubFace plane.
        // Before passing the point set of cp_worker2 to cp_worker3, the point set of cp_worker2 may have overlapping bars, so
        // Pass it to bb_worker and organize it as a set of line segments.
        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____基本枝職人bb_workerはcp_worker2から線分集合（針金図からできたもの）を受け取り、整理する。");
        bb_worker.set(cp_worker2.getLineStore());
        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____基本枝職人bb_workerがbb_worker.bunkatu_seiri_for_Smen_hassei;実施。");
        bb_worker.split_arrangement_for_SubFace_generation();//Arrangement of wire diagrams obtained by estimating the folding of overlapping line segments and intersecting line segments
        //The crease pattern craftsman cp_worker3 receives a point set (arranged wire diagram of cp_worker2) from bb_worker and divides it into SubFace.
        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____展開図職人cp_worker3はbb_workerから整理された線分集合を受け取り、Smenに分割する。");
        System.out.println("　　　oritatami_suitei_03()のcp_worker3.Senbunsyuugou2Tensyuugou(bb_worker.get());実施");
        cp_worker3.lineStore2pointStore(bb_worker.get());

        System.out.println("＜＜＜＜＜oritatami_suitei_03()_____上下表職人ct_workerは、展開図職人cp_worker3から点集合を受け取り、Smenを設定する。");
        ct_worker.SubFace_configure(cp_worker2.get(), cp_worker3.get());
        //If you want to make a transparent map up to this point, you can. The transmission diagram is a SubFace diagram with density added.
        return 1000;
    }

    public int folding_estimated_04() {
        System.out.println("＜＜＜＜＜oritatami_suitei_04;開始");
        bulletinBoard.write("<<<<oritatami_suitei_04;  start");
        //Make an upper and lower table of faces (faces in the unfolded view before folding).
        // This includes the point set of cp_worker2 (which has information on the positional relationship of the faces after folding).
        // Use the point set of cp_worker3 (which has the information of SubFace whose surface is subdivided in the wire diagram).
        // Also, use the information on the positional relationship of the surface when folded, which cp_worker1 has.
        System.out.println("＜＜＜＜＜oritatami_suitei_04()_____上下表職人ct_workerが面(折りたたむ前の展開図の面のこと)の上下表を作る。");

        ip1_anotherOverlapValid = HierarchyList_Worker.HierarchyListStatus.UNKNOWN_0;
        findAnotherOverlapValid = false;
        ip1_anotherOverlapValid = ct_worker.HierarchyList_configure(cp_worker1, cp_worker2.get());   //ip1_anotherOverlapValid = A variable that stores 0 if there is an error that the front and back sides are adjacent after folding, and 1000 if there is no error.
        if (ip1_anotherOverlapValid == HierarchyList_Worker.HierarchyListStatus.UNKNOWN_1000) {
            findAnotherOverlapValid = true;
        }
        discovered_fold_cases = 0;
        System.out.println("＜＜＜＜＜oritatami_suitei_04()____終了");
        return 1000;
    }

    public int folding_estimated_05() {
        System.out.println("＜＜＜＜＜oritatami_suitei_05()_____上下表職人ct_workerがct_worker.kanou_kasanari_sagasi()実施。");
        app.bulletinBoard.write("<<<<oritatami_suitei_05()  ___ct_worker.kanou_kasanari_sagasi()  start");

        if ((estimationStep == EstimationStep.STEP_4) || (estimationStep == EstimationStep.STEP_5)) {
            if (findAnotherOverlapValid) {

                ip2_possibleOverlap = ct_worker.possible_overlapping_search();//ip2_possibleOverlap = A variable that stores 0 if there is no possible overlap and 1000 if there is a possible overlap when the upper and lower table craftsmen search for a foldable overlap.

                if (ip2_possibleOverlap == 1000) {
                    discovered_fold_cases = discovered_fold_cases + 1;
                }

                ip5 = ct_worker.next(ct_worker.getSubFace_valid_number());// Preparation for the next overlap // If ip5 = 0, there was no room for new susumu. If non-zero, the smallest number of changed SubFace ids
            }
        }
        app.bulletinBoard.clear();

        text_result = "Number of found solutions = " + discovered_fold_cases + "  ";
        findAnotherOverlapValid = (ip2_possibleOverlap == 1000) && (ip5 > 0);

        if (!findAnotherOverlapValid) {
            text_result = text_result + " There is no other solution. ";
        }

        return 1000;
    }

    public void foldedFigure_operation_mouse_on(Point p) {//Work when the left mouse button is pressed in the fold-up diagram operation
        if (i_foldedFigure_operation_mode == 1) {
            foldedFigure_operation_mouse_on_1(p);
        }
        if (i_foldedFigure_operation_mode == 2) {
            foldedFigure_operation_mouse_on_2(p);
        }
    }

    public void foldedFigure_operation_mouse_drag(Point p) {//Work when dragging while holding down the left mouse button in the fold-up diagram operation
        if (i_foldedFigure_operation_mode == 1) {
            foldedFigure_operation_mouse_drag_1(p);
        }
        if (i_foldedFigure_operation_mode == 2) {
            foldedFigure_operation_mouse_drag_2(p);
        }
    }

    public void foldedFigure_operation_mouse_off(Point p) {//Work when the left mouse button is released during the folding diagram operation
        if (i_foldedFigure_operation_mode == 1) {
            foldedFigure_operation_mouse_off_1(p);
        }
        if (i_foldedFigure_operation_mode == 2) {
            foldedFigure_operation_mouse_off_2(p);
        }
    }

    public void foldedFigure_operation_mouse_on_1(Point p) {//Work when the left mouse button is pressed in the folding diagram operation Folding function
        p_m_left_on.set(new Point(p.getX(), p.getY()));

        cp_worker2.setCamera(foldedFigureCamera);
        cp_worker2.setCam_front(foldedFigureFrontCamera);
        cp_worker2.setCam_rear(foldedFigureRearCamera);

        //Store the number of the point closest to p in i_closestPointId. I_closestPointId = 0 if there are no close points
        i_nanini_near = 0;//Close to the point in the development view = 1, close to the point in the folded view = 2, not close to either = 0
        i_closestPointId = cp_worker1.closestPointId_with_camera(p);
        if (i_closestPointId != 0) {
            i_nanini_near = 1;
        }
        if (cp_worker2.closestPointId_with_camera(p, ip4) != 0) {
            if (cp_worker1.closest_point_distance_with_camera(p) > cp_worker2.closest_point_distance_with_camera(p, ip4)) {
                i_closestPointId = cp_worker2.closestPointId_with_camera(p, ip4);
                i_nanini_near = 2;
            }
        }//Store the number of the point closest to p in i_closestPointId

        move_previous_selection_point.set(cp_worker2.getPoint(i_closestPointId));

        System.out.println("i_nanini_tikai = " + i_nanini_near);

        if (i_nanini_near == 1) {
            //Decide i_point_selection
            i_point_selection = PointSelection.NONE_0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
            }
            if (cp_worker2.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_2;
            }
            //Decide i_point_selection so far


            switch (i_point_selection) {//Find the number of the point at the same position as i_closestPointId in the fold-up diagram, and mark the point with that number as selected with cp_worker1.
                case NONE_0:
                    setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                            cp_worker1.setPointStateTrue(i);
                        }
                    }
                    cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                case WORKER_2:
                    cp_worker2.changePointState(i_closestPointId);
                    break;
            }
        }

        if (i_nanini_near == 2) {
            //Decide i_point_selection
            i_point_selection = PointSelection.NONE_0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
                if (cp_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = PointSelection.WORKER_2;
                }    //At the point specified on the origami diagram, one of the points that overlaps it is selected by cp_worker2. In short, the point displayed in green on the origami diagram is selected.
            }
            //Decide i_point_selection so far
            System.out.println("i_ten_sentaku = " + i_point_selection);

            switch (i_point_selection) {//Find the number of the point at the same position as i_closestPointId in the fold-up diagram, and mark the point with that number as selected with cp_worker1.
                case NONE_0:
                    setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                            cp_worker1.setPointStateTrue(i);
                        }
                    }
                    cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                    cp_worker2.changePointState(i_closestPointId);
                    break;
            }

            if (i_foldedFigure_operation_mode == 1) {
                display_flg_backup = displayStyle;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                displayStyle = DisplayStyle.WIRE_2;            //20180216
            }
        }

        System.out.println("cp_worker1.get_ten_sentakusuu() = " + cp_worker1.getSelectedPointsNum());
        System.out.println("cp_worker2.get_ten_sentakusuu() = " + cp_worker2.getSelectedPointsNum());
    }

    public void foldedFigure_operation_mouse_drag_1(Point p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        cp_worker2.setCamera(foldedFigureCamera);
        cp_worker2.setCam_front(foldedFigureFrontCamera);
        cp_worker2.setCam_rear(foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            cp_worker2.mDragged_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, ip4);

            if (i_foldedFigure_operation_mode == 2) {
                folding_estimated_03();//20180216
            }
        }
    }

    //-------------
    public void foldedFigure_operation_mouse_off_1(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        cp_worker2.setCamera(foldedFigureCamera);
        cp_worker2.setCam_front(foldedFigureFrontCamera);
        cp_worker2.setCam_rear(foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            displayStyle = display_flg_backup;//20180216

            cp_worker2.mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, ip4);
            if (p_m_left_on.distance(p) > 0.0000001) {
                record();
                estimationStep = EstimationStep.STEP_2;

                if (displayStyle == DisplayStyle.WIRE_2) {
                }

                if (displayStyle == DisplayStyle.PAPER_5) {
                    estimationOrder = EstimationOrder.ORDER_5;
                    app.folding_estimated();
                }//オリジナル 20180124 これ以外だと、表示いったんもどるようでうざい
            }

            cp_worker1.setAllPointStateFalse();
            //折り上がり図でi_closestPointIdと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = new Point();
            ps.set(cp_worker2.getPoint(i_closestPointId));
            for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                    cp_worker1.setPointStateTrue(i);
                }
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
    //  =================================================================================================================================
    //  ==========折り上がり図のまま変形操作===========================================================================================================
    //-----------------------------------------------------------------------------------------------------uuuuuuu--
    public void foldedFigure_operation_mouse_on_2(Point p) {//Work when the left mouse button is pressed in the fold-up diagram operation
        p_m_left_on.set(new Point(p.getX(), p.getY()));

        cp_worker2.setCamera(foldedFigureCamera);
        cp_worker2.setCam_front(foldedFigureFrontCamera);
        cp_worker2.setCam_rear(foldedFigureRearCamera);

        //i_closestPointIdにpに最も近い点の番号を格納。近い点がまったくない場合はi_mottomo_tikai_Tenid=0
        i_nanini_near = 0;//展開図の点に近い=1、折り上がり図の点に近い=2、どちらにも近くない=0
        i_closestPointId = cp_worker1.closestPointId_with_camera(p);
        if (i_closestPointId != 0) {
            i_nanini_near = 1;
        }
        if (cp_worker2.closestPointId_with_camera(p, ip4) != 0) {
            if (cp_worker1.closest_point_distance_with_camera(p) > cp_worker2.closest_point_distance_with_camera(p, ip4)) {
                i_closestPointId = cp_worker2.closestPointId_with_camera(p, ip4);
                i_nanini_near = 2;
            }
        }//i_mottomo_tikai_Tenidにpに最も近い点の番号を格納 ここまで

        move_previous_selection_point.set(cp_worker2.getPoint(i_closestPointId));

        System.out.println("i_nanini_tikai = " + i_nanini_near);

        if (i_nanini_near == 1) {

            //i_ten_sentakuを決める
            i_point_selection = PointSelection.NONE_0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
            }
            if (cp_worker2.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_2;
            }
            //i_ten_sentakuを決める  ここまで

            switch (i_point_selection) {//折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                case NONE_0:
                    setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                            cp_worker1.setPointStateTrue(i);
                        }
                    }
                    cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                case WORKER_2:
                    cp_worker2.changePointState(i_closestPointId);
                    break;
            }
        }

        if (i_nanini_near == 2) {
            //i_ten_sentakuを決める
            i_point_selection = PointSelection.NONE_0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = PointSelection.WORKER_1;
                if (cp_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = PointSelection.WORKER_2;
                }    //折図上で指定した点で、そこに重なるいずれかの点がcp_worker2で選択されている。要するに折図上の緑表示されている点を選んだ状態
            }
            //i_ten_sentakuを決める  ここまで
            System.out.println("i_ten_sentaku = " + i_point_selection);

            switch (i_point_selection) {//折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                case NONE_0:
                    setAllPointStateFalse();
                    Point ps = new Point();
                    ps.set(cp_worker2.getPoint(i_closestPointId));
                    for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                        if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                            cp_worker1.setPointStateTrue(i);
                        }
                    }
                    cp_worker2.changePointState(i_closestPointId);
                    break;
                case WORKER_1:
                    cp_worker2.changePointState(i_closestPointId);
                    break;
            }

            if (i_foldedFigure_operation_mode == 1) {
                display_flg_backup = displayStyle;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                displayStyle = DisplayStyle.WIRE_2;            //20180216
            }
        }

        System.out.println("cp_worker1.get_ten_sentakusuu() = " + cp_worker1.getSelectedPointsNum());
        System.out.println("cp_worker2.get_ten_sentakusuu() = " + cp_worker2.getSelectedPointsNum());
    }

    public void foldedFigure_operation_mouse_drag_2(Point p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        cp_worker2.setCamera(foldedFigureCamera);
        cp_worker2.setCam_front(foldedFigureFrontCamera);
        cp_worker2.setCam_rear(foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            cp_worker2.mDragged_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, ip4);

            if (i_foldedFigure_operation_mode == 2) {
                folding_estimated_03();//20180216
            }
        }
    }

    public void foldedFigure_operation_mouse_off_2(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        cp_worker2.setCamera(foldedFigureCamera);
        cp_worker2.setCam_front(foldedFigureFrontCamera);
        cp_worker2.setCam_rear(foldedFigureRearCamera);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            cp_worker2.mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, ip4);
            if (p_m_left_on.distance(p) > 0.0000001) {
                record();
                estimationStep = EstimationStep.STEP_2;

                if (i_foldedFigure_operation_mode == 1) {
                    displayStyle = display_flg_backup;//20180216
                }
                if (displayStyle == DisplayStyle.WIRE_2) {
                }

                folding_estimated_03();//20180216
            }

            cp_worker1.setAllPointStateFalse();
            //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = new Point();
            ps.set(cp_worker2.getPoint(i_closestPointId));
            for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                    cp_worker1.setPointStateTrue(i);
                }
            }
        }
    }

    public void record() {
        cp_worker2.record();
    }

    public void redo() {
        cp_worker2.redo();
        folding_estimated_03();
    }

    public void undo() {
        cp_worker2.undo();
        folding_estimated_03();
    }

    public void setAllPointStateFalse() {
        cp_worker1.setAllPointStateFalse();
        cp_worker2.setAllPointStateFalse();
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        ct_worker.setData(foldedFigureModel);
        foldedFigure_F_color = foldedFigureModel.getFrontColor();
        foldedFigure_B_color = foldedFigureModel.getBackColor();
        foldedFigure_L_color = foldedFigureModel.getLineColor();
        d_foldedFigure_scale_factor = foldedFigureModel.getScale();
        d_foldedFigure_rotation_correction = foldedFigureModel.getRotation();
        ip4 = foldedFigureModel.getState();

        cp_worker2.setUndoBoxUndoTotal(foldedFigureModel.getHistoryTotal());

        transparencyColor = foldedFigureModel.isTransparencyColor();
        transparent_transparency = foldedFigureModel.getTransparentTransparency();

        // Update scale
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

        // Update rotation
        foldedFigureCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        foldedFigureFrontCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        foldedFigureRearCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        transparentFrontCamera.setCameraAngle(d_foldedFigure_rotation_correction);
        transparentRearCamera.setCameraAngle(d_foldedFigure_rotation_correction);
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        ct_worker.getData(foldedFigureModel);
        foldedFigureModel.setFrontColor(foldedFigure_F_color);
        foldedFigureModel.setBackColor(foldedFigure_B_color);
        foldedFigureModel.setLineColor(foldedFigure_L_color);
        foldedFigureModel.setRotation(d_foldedFigure_rotation_correction);
        foldedFigureModel.setScale(d_foldedFigure_scale_factor);
        foldedFigureModel.setState(ip4);
    }

    public void scale(double magnification) {
        scale(magnification, null);
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

    public enum PointSelection {
        NONE_0,
        WORKER_1,
        WORKER_2,
    }

    public enum EstimationOrder {
        ORDER_0,
        ORDER_1,
        ORDER_2,
        ORDER_3,
        ORDER_4,
        ORDER_5,
        ORDER_6,
        ORDER_51,
        ;

        public boolean isBelowOrEqual5() {
            switch (this) {
                case ORDER_0:
                case ORDER_1:
                case ORDER_2:
                case ORDER_3:
                case ORDER_4:
                case ORDER_5:
                    return true;
                default:
                    return false;
            }
        }
    }

    public enum EstimationStep {
        STEP_0,
        STEP_1,
        STEP_2,
        STEP_3,
        STEP_4,
        STEP_5,
        STEP_6,
        STEP_7,
        STEP_8,
        STEP_9,
        STEP_10,
    }

    public enum State {
        FRONT_0,
        BACK_1,
        BOTH_2,
        TRANSPARENT_3,
        ;

        public State advance() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    public enum DisplayStyle {
        NONE_0,
        DEVELOPMENT_1,
        WIRE_2,
        TRANSPARENT_3,
        DEVELOPMENT_4,
        PAPER_5,
    }
}
