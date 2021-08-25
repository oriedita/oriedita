package jp.gr.java_conf.mt777.origami.orihime.oriagari_zu;

import java.awt.*;

import jp.gr.java_conf.mt777.origami.orihime.*;
import jp.gr.java_conf.mt777.origami.orihime.basicbranch_worker.*;
import jp.gr.java_conf.mt777.origami.orihime.tenkaizu_syokunin.*;
import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.*;

import jp.gr.java_conf.mt777.origami.dougu.camera.*;
import jp.gr.java_conf.mt777.origami.dougu.keijiban.*;
import jp.gr.java_conf.mt777.origami.dougu.linestore.*;
import jp.gr.java_conf.mt777.kiroku.memo.*;

import jp.gr.java_conf.mt777.graphic2d.point.Point;

public class FoldedFigure {
    App orihime_app;

    double r = 3.0;                   //Criteria for determining the radius of the circles at both ends of the straight line of the basic branch structure and the proximity of the branches to various points

    public double d_foldedFigure_scale_factor = 1.0;//Scale factor of folded view
    public double d_foldedFigure_rotation_correction = 0.0;//Correction angle of rotation display angle of folded view

    public WireFrame_Worker bb_worker = new WireFrame_Worker(r);    //Basic branch craftsman. Before passing the point set of cp_worker2 to cp_worker3,
    // The point set of cp_worker2 may have overlapping bars, so
    // Pass it to bb_worker once and organize it as a line segment set.

    public CreasePattern_Worker cp_worker1 = new CreasePattern_Worker(r);    //Net craftsman. Fold the input line segment set first to make a fold-up diagram of the wire-shaped point set.
    public CreasePattern_Worker cp_worker2 = new CreasePattern_Worker(r);    //Net craftsman. It holds the folded-up view of the wire-shaped point set created by cp_worker1 and functions as a line segment set.
    public CreasePattern_Worker cp_worker3 = new CreasePattern_Worker(r);    //Net craftsman. Organize the wire-shaped point set created by cp_worker1. It has functions such as recognizing a new surface.

    public HierarchyList_Worker ct_worker;

    public Camera camera_of_foldedFigure = new Camera();
    public Camera camera_of_foldedFigure_front = new Camera();//折り上がり
    public Camera camera_of_foldedFigure_rear = new Camera();
    public Camera camera_of_transparent_front = new Camera();
    public Camera camera_of_transparent_rear = new Camera();

    public Color foldedFigure_F_color = new Color(255, 255, 50);//Folded surface color
    public Color foldedFigure_B_color = new Color(233, 233, 233);//The color of the back side of the folded figure
    public Color foldedFigure_L_color = Color.black;//Folded line color

    public DisplayStyle display_flg_backup = DisplayStyle.DEVELOPMENT_4;//For temporary backup of display format display_flg
    public DisplayStyle display_flg = DisplayStyle.NONE_0;//Designation of the display style of the folded figure. 1 is a development drawing, 2 is a wire drawing. If it is 3, it is a transparent view. If it is 4, it is the same as when you actually fold the origami paper.
    public int i_estimated_order = 0;//Instructions on how far to perform folding estimation
    public int i_estimated_step = 0;//Display of how far the folding estimation has been completed

    //Variable to store the value for display
    public int ip1 = -1;// At the time of initial setting of the upper and lower front craftsmen, the front and back sides are the same after folding
    // A variable that stores 0 if there is an error of being adjacent, and 1000 if there is no error.
    // The initial value here can be any number other than (0 or 1000).
    public int ip2 = -1;// When the top and bottom craftsmen look for a foldable stacking method,
    // A variable that stores 0 if there is no possible overlap, and 1000 if there is a possible overlap.
    // The initial value here can be any number other than (0 or 1000).
    //int ip3a=1;
    public int ip3 = 1;// Used by cp_worker1 to specify the reference plane for folding.

    public State ip4 = State.FRONT_0;// This specifies whether to flip over at the beginning of cp_worker1. Do not set to 0. If it is 1, turn it over.

    public int ip5 = -1;    // After the top and bottom craftsmen once show the overlap of foldable paper,
    // The result of the first ct_worker.susumu (SubFaceTotal) when looking for yet another paper overlap. If it was
    // 0, there was no room for new susumu. If non-zero, the smallest number of changed SubFace ids

    public int ip6 = -1;    // After the top and bottom craftsmen once show the overlap of foldable paper,
    // The result of ct_worker.kanou_kasanari_sagasi () when looking for another paper overlap. If
    // 0, there is no possible overlapping state.
    // If it is 1000, another way of overlapping was found.

    public boolean findAnotherOverlapValid = false;     //This takes 1 if "find another overlap" is valid, and 0 if it is invalid.
    public int discovered_fold_cases = 0;    //折り重なり方で、何通り発見したかを格納する。

    public int transparent_transparency = 16;//Transparency when drawing a transparent diagram in color

    public int i_foldedFigure_operation_mode = 1;//1 = When deformed, it becomes a wire diagram, and after deformation, the upper and lower tables are recalculated, the old mode,2 = A mode in which the folded figure remains even when deformed, and the upper and lower tables are basically not recalculated after deformation.

    public BulletinBoard bulletinBoard;

    public boolean summary_write_image_during_execution = false;//matome_write_imageが実行中ならtureになる。これは、複数の折りあがり形の予測の書き出しがかすれないように使う。20170613

    public String text_result;                //Instantiation of result display string class

    public boolean transparencyColor = false;//1 if the transparency is in color, 0 otherwise

    public FoldedFigure(App app0) {
        orihime_app = app0;

        ct_worker = new HierarchyList_Worker(app0);
        bulletinBoard = new BulletinBoard(app0);

        //Camera settings ------------------------------------------------------------------
        foldedFigure_camera_initialize();
        //This is the end of the camera settings ----------------------------------------------------

        text_result = "";
    }

    //----------------------------------------------------------
    public void estimated_initialize() {
        text_result = "";
        bb_worker.reset();
        cp_worker1.reset();
        cp_worker2.reset();
        cp_worker3.reset();
        ct_worker.reset();

        display_flg = DisplayStyle.NONE_0;
        i_estimated_order = 0;//Instructions on how far to perform folding estimation
        i_estimated_step = 0;//Display of how far the folding estimation has been completed
        findAnotherOverlapValid = false;

        summary_write_image_during_execution = false; //If the export of multiple folded forecasts is in progress, it will be ture. 20170615
    }

    public void foldedFigure_camera_initialize() {
        //camera_of_oriagarizu	;
        camera_of_foldedFigure.setCameraPositionX(0.0);
        camera_of_foldedFigure.setCameraPositionY(0.0);
        camera_of_foldedFigure.setCameraAngle(0.0);
        camera_of_foldedFigure.setCameraMirror(1.0);
        camera_of_foldedFigure.setCameraZoomX(1.0);
        camera_of_foldedFigure.setCameraZoomY(1.0);
        camera_of_foldedFigure.setDisplayPositionX(350.0);
        camera_of_foldedFigure.setDisplayPositionY(350.0);


        //camera_of_oriagari_omote	;
        camera_of_foldedFigure_front.setCameraPositionX(0.0);
        camera_of_foldedFigure_front.setCameraPositionY(0.0);
        camera_of_foldedFigure_front.setCameraAngle(0.0);
        camera_of_foldedFigure_front.setCameraMirror(1.0);
        camera_of_foldedFigure_front.setCameraZoomX(1.0);
        camera_of_foldedFigure_front.setCameraZoomY(1.0);
        camera_of_foldedFigure_front.setDisplayPositionX(350.0);
        camera_of_foldedFigure_front.setDisplayPositionY(350.0);

        //camera_of_oriagari_ura	;
        camera_of_foldedFigure_rear.setCameraPositionX(0.0);
        camera_of_foldedFigure_rear.setCameraPositionY(0.0);
        camera_of_foldedFigure_rear.setCameraAngle(0.0);
        camera_of_foldedFigure_rear.setCameraMirror(-1.0);
        camera_of_foldedFigure_rear.setCameraZoomX(1.0);
        camera_of_foldedFigure_rear.setCameraZoomY(1.0);
        camera_of_foldedFigure_rear.setDisplayPositionX(350.0);
        camera_of_foldedFigure_rear.setDisplayPositionY(350.0);


        //camera_of_touka_omote	;
        camera_of_transparent_front.setCameraPositionX(0.0);
        camera_of_transparent_front.setCameraPositionY(0.0);
        camera_of_transparent_front.setCameraAngle(0.0);
        camera_of_transparent_front.setCameraMirror(1.0);
        camera_of_transparent_front.setCameraZoomX(1.0);
        camera_of_transparent_front.setCameraZoomY(1.0);
        camera_of_transparent_front.setDisplayPositionX(350.0);
        camera_of_transparent_front.setDisplayPositionY(350.0);

        //camera_of_touka_ura	;
        camera_of_transparent_rear.setCameraPositionX(0.0);
        camera_of_transparent_rear.setCameraPositionY(0.0);
        camera_of_transparent_rear.setCameraAngle(0.0);
        camera_of_transparent_rear.setCameraMirror(-1.0);
        camera_of_transparent_rear.setCameraZoomX(1.0);
        camera_of_transparent_rear.setCameraZoomY(1.0);
        camera_of_transparent_rear.setDisplayPositionX(350.0);
        camera_of_transparent_rear.setDisplayPositionY(350.0);
    }

    public void foldUp_draw(Graphics bufferGraphics, boolean i_mark_display) {
        //display_flg==2,ip4==0  front
        //display_flg==2,ip4==1	rear
        //display_flg==2,ip4==2	front & rear
        //display_flg==2,ip4==3	front & rear

        //display_flg==3,ip4==0  front
        //display_flg==3,ip4==1	rear
        //display_flg==3,ip4==2	front & rear
        //display_flg==3,ip4==3	front & rear

        //display_flg==5,ip4==0  front
        //display_flg==5,ip4==1	rear
        //display_flg==5,ip4==2	front & rear
        //display_flg==5,ip4==3	front & rear & front2 & rear2

        //Since ct_worker displays the folded figure, it is not necessary to set the camera in cp_worker2 for the display itself, but after that, cp_worker2 judges the screen click, so it is necessary to update the camera of cp_worker2 in synchronization with the display. ..
        cp_worker2.setCamera(camera_of_foldedFigure);
        cp_worker2.setCam_front(camera_of_foldedFigure_front);
        cp_worker2.setCam_rear(camera_of_foldedFigure_rear);
        cp_worker2.setCam_transparent_front(camera_of_transparent_front);
        cp_worker2.setCam_transparent_rear(camera_of_transparent_rear);


        //Wire diagram display
        if (display_flg == DisplayStyle.WIRE_2) {
            cp_worker2.drawing_with_camera(bufferGraphics, ip4);//The operation of the fold-up diagram moves the wire diagram of this cp_worker2.
        }

        //Display of folded figure (table)
        if (((ip4 == State.FRONT_0) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {
            ct_worker.setCamera(camera_of_foldedFigure_front);

            //透過図の表示
            if (display_flg == DisplayStyle.TRANSPARENT_3) {        // display_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);
            }

            //折り上がり図の表示************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (display_flg == DisplayStyle.PAPER_5) {
                ct_worker.draw_foldedFigure_with_camera(bufferGraphics, cp_worker1, cp_worker3.get());// display_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //Cross-shaped display at the center of movement of the folded figure
            if (i_mark_display) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }
        }

        //Display of folded figure (back)
        if (((ip4 == State.BACK_1) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {
            camera_of_foldedFigure_rear.display();
            ct_worker.setCamera(camera_of_foldedFigure_rear);

            //Display of transparency
            if (display_flg == DisplayStyle.TRANSPARENT_3) {        // display_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);
            }

            //Display of folded figure ************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (display_flg == DisplayStyle.PAPER_5) {
                ct_worker.draw_foldedFigure_with_camera(bufferGraphics, cp_worker1, cp_worker3.get());// display_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }

            //Cross-shaped display at the center of movement of the folded figure
            //System.out.println("paint　+++++++++++++++++++++　折り上がり図の動かし中心の十字表示)");
            if (i_mark_display) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }
        }

        //透過図（折りあがり図表示時に追加する分）
        if ((ip4 == State.TRANSPARENT_3) && (display_flg == DisplayStyle.PAPER_5)) {
            // ---------------------------------------------------------------------------------
            ct_worker.setCamera(camera_of_transparent_front);
            //透過図の表示
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);

            //折り上がり図の動かし中心の十字表示
            //System.out.println("paint　+++++++++++++++++++++　折り上がり図の動かし中心の十字表示)");
            if (i_mark_display) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }

            // ---------------------------------------------------------------------------------
            ct_worker.setCamera(camera_of_transparent_rear);

            //透過図の表示
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            ct_worker.draw_transparency_with_camera(bufferGraphics, cp_worker2.get(), cp_worker3.get(), transparencyColor, transparent_transparency);

            //折り上がり図の動かし中心の十字表示
            //System.out.println("paint　+++++++++++++++++++++　折り上がり図の動かし中心の十字表示)");
            if (i_mark_display) {
                ct_worker.draw_cross_with_camera(bufferGraphics);
            }
            // ---------------------------------------------------------------------------------
        }


        //折り上がり図動かし時の針金図と展開図上の対応点の表示


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

        //針金図のsvg
        if (display_flg == DisplayStyle.WIRE_2) {
            memo_temp.addMemo(ct_worker.getMemo_wirediagram_for_svg_export(cp_worker1, cp_worker2.get(), 0));//４番目の整数は０なら面の枠のみ、１なら面を塗る
        }

        //折りあがり図（表）のsvg
        if (((ip4 == State.FRONT_0) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {
            ct_worker.setCamera(camera_of_foldedFigure_front);

            //透過図のsvg
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            if (display_flg == DisplayStyle.TRANSPARENT_3) {        // display_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(ct_worker.getMemo_wirediagram_for_svg_export(cp_worker1, cp_worker2.get(), 1));
            }

            //折り上がり図のsvg************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (display_flg == DisplayStyle.PAPER_5) {
                //ct_worker.oekaki_oriagarizu_with_camera(bufferGraphics,cp_worker1,cp_worker2.get(),cp_worker3.get());// display_flg; Specify the display style of the folded figure. If it is 5, it is the same as when you actually fold the origami paper. If it is 3, it is a transparent view. If it is 2, it is a wire diagram.
                memo_temp.addMemo(ct_worker.getMemo_for_svg_with_camera(cp_worker1, cp_worker3.get()));// display_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。

            }
        }

        //折りあがり図（裏）のsvg
        if (((ip4 == State.BACK_1) || (ip4 == State.BOTH_2)) || (ip4 == State.TRANSPARENT_3)) {

            ct_worker.setCamera(camera_of_foldedFigure_rear);

            //透過図のsvg
            //System.out.println("paint　+++++++++++++++++++++　透過図の表示");
            if (display_flg == DisplayStyle.TRANSPARENT_3) {        // display_flg;折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図。3なら針金図。
                memo_temp.addMemo(ct_worker.getMemo_wirediagram_for_svg_export(cp_worker1, cp_worker2.get(), 1));
            }

            //折り上がり図のsvg************* //System.out.println("paint　+++++++++++++++++++++　折り上がり図の表示");
            if (display_flg == DisplayStyle.PAPER_5) {
                memo_temp.addMemo(ct_worker.getMemo_for_svg_with_camera(cp_worker1, cp_worker3.get()));// display_flg;折り上がり図の表示様式の指定。5なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
            }
        }

        //透過図（折りあがり図表示時に追加する分）
        if ((ip4 == State.TRANSPARENT_3) && (display_flg == DisplayStyle.PAPER_5)) {
            // ---------------------------------------------------------------------------------
            ct_worker.setCamera(camera_of_transparent_front);
            //透過図のsvg
            ct_worker.setCamera(camera_of_transparent_rear);

            //透過図のsvg
        }
        return memo_temp;

    }

    void oritatami_suitei_camera_configure(Camera camera_of_orisen_nyuuryokuzu, LineSegmentSet Ss0) {
        d_foldedFigure_scale_factor = camera_of_orisen_nyuuryokuzu.getCameraZoomX();
        orihime_app.text29.setText(String.valueOf(d_foldedFigure_scale_factor));
        orihime_app.text29.setCaretPosition(0);

        d_foldedFigure_rotation_correction = camera_of_orisen_nyuuryokuzu.getCameraAngle();
        orihime_app.text30.setText(String.valueOf(d_foldedFigure_rotation_correction));
        orihime_app.text30.setCaretPosition(0);

        System.out.println("cp_worker1.ten_of_kijyunmen_ob     " + cp_worker1.point_of_referencePlane_ob.getX());

        Point p0 = new Point();
        Point p = new Point();

        p.set(cp_worker1.point_of_referencePlane_ob);
        p0.set(camera_of_orisen_nyuuryokuzu.object2TV(p));

        double d_camera_position_x = p.getX();
        double d_camera_position_y = p.getY();
        double d_display_position_x = p0.getX();
        double d_display_position_y = p0.getY();

        camera_of_foldedFigure.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_foldedFigure.setCameraPositionX(d_camera_position_x);
        camera_of_foldedFigure.setCameraPositionY(d_camera_position_y);
        camera_of_foldedFigure.setDisplayPositionX(d_display_position_x + 20.0);
        camera_of_foldedFigure.setDisplayPositionY(d_display_position_y + 20.0);

        camera_of_foldedFigure_front.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_foldedFigure_front.setCameraPositionX(d_camera_position_x);
        camera_of_foldedFigure_front.setCameraPositionY(d_camera_position_y);
        camera_of_foldedFigure_front.setDisplayPositionX(d_display_position_x + 20.0);
        camera_of_foldedFigure_front.setDisplayPositionY(d_display_position_y + 20.0);

        camera_of_foldedFigure_rear.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_foldedFigure_rear.setCameraPositionX(d_camera_position_x);
        camera_of_foldedFigure_rear.setCameraPositionY(d_camera_position_y);
        camera_of_foldedFigure_rear.setDisplayPositionX(d_display_position_x + 40.0);
        camera_of_foldedFigure_rear.setDisplayPositionY(d_display_position_y + 20.0);

        camera_of_transparent_front.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_transparent_front.setCameraPositionX(d_camera_position_x);
        camera_of_transparent_front.setCameraPositionY(d_camera_position_y);
        camera_of_transparent_front.setDisplayPositionX(d_display_position_x + 20.0);
        camera_of_transparent_front.setDisplayPositionY(d_display_position_y + 0.0);

        camera_of_transparent_rear.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_transparent_rear.setCameraPositionX(d_camera_position_x);
        camera_of_transparent_rear.setCameraPositionY(d_camera_position_y);
        camera_of_transparent_rear.setDisplayPositionX(d_display_position_x + 40.0);
        camera_of_transparent_rear.setDisplayPositionY(d_display_position_y + 0.0);

        double d_camera_mirror = camera_of_foldedFigure_rear.getCameraMirror();
        camera_of_foldedFigure_rear.setCameraMirror(d_camera_mirror * -1.0);
        camera_of_transparent_rear.setCameraMirror(d_camera_mirror * -1.0);
    }

    public void folding_estimated(Camera camera_of_orisen_nyuuryokuzu, LineSegmentSet lineSegmentSet) {//折畳み予測の最初に、cp_worker1.lineStore2pointStore(lineStore)として使う。　Ss0は、es1.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
        int i_camera_estimated = 0;

        //-------------------------------折り上がり図表示用カメラの設定

        if ((i_estimated_step == 0) && (i_estimated_order <= 5)) {
            i_camera_estimated = 1;


        }

        if (i_estimated_order == 51) {
            i_estimated_order = 5;
        }
        //-------------------------------
        // suitei = estimated
        // dankai = step
        // meirei = order
        if ((i_estimated_step == 0) && (i_estimated_order == 1)) {
            estimated_initialize(); // estimated_initialize
            folding_estimated_01(lineSegmentSet);
            i_estimated_step = 1;
            display_flg = DisplayStyle.DEVELOPMENT_1;
        } else if ((i_estimated_step == 0) && (i_estimated_order == 2)) {
            estimated_initialize();
            folding_estimated_01(lineSegmentSet);
            i_estimated_step = 1;
            display_flg = DisplayStyle.DEVELOPMENT_1;
            folding_estimated_02();
            i_estimated_step = 2;
            display_flg = DisplayStyle.WIRE_2;
        } else if ((i_estimated_step == 0) && (i_estimated_order == 3)) {
            estimated_initialize();
            folding_estimated_01(lineSegmentSet);
            i_estimated_step = 1;
            display_flg = DisplayStyle.DEVELOPMENT_1;
            folding_estimated_02();
            i_estimated_step = 2;
            display_flg = DisplayStyle.WIRE_2;
            folding_estimated_03();
            i_estimated_step = 3;
            display_flg = DisplayStyle.TRANSPARENT_3;
        } else if ((i_estimated_step == 0) && (i_estimated_order == 5)) {
            estimated_initialize();
            folding_estimated_01(lineSegmentSet);
            i_estimated_step = 1;
            display_flg = DisplayStyle.DEVELOPMENT_1;
            folding_estimated_02();
            i_estimated_step = 2;
            display_flg = DisplayStyle.WIRE_2;
            folding_estimated_03();
            i_estimated_step = 3;
            display_flg = DisplayStyle.TRANSPARENT_3;
            folding_estimated_04();
            i_estimated_step = 4;
            display_flg = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            i_estimated_step = 5;
            display_flg = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && (!findAnotherOverlapValid)) {
                i_estimated_step = 3;
                display_flg = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((i_estimated_step == 1) && (i_estimated_order == 1)) {
        } else if ((i_estimated_step == 1) && (i_estimated_order == 2)) {
            folding_estimated_02();
            i_estimated_step = 2;
            display_flg = DisplayStyle.WIRE_2;
        } else if ((i_estimated_step == 1) && (i_estimated_order == 3)) {
            folding_estimated_02();
            i_estimated_step = 2;
            display_flg = DisplayStyle.WIRE_2;
            folding_estimated_03();
            i_estimated_step = 3;
            display_flg = DisplayStyle.TRANSPARENT_3;
        } else if ((i_estimated_step == 1) && (i_estimated_order == 5)) {
            folding_estimated_02();
            i_estimated_step = 2;
            display_flg = DisplayStyle.WIRE_2;
            folding_estimated_03();
            i_estimated_step = 3;
            display_flg = DisplayStyle.TRANSPARENT_3;
            folding_estimated_04();
            i_estimated_step = 4;
            display_flg = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            i_estimated_step = 5;
            display_flg = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && (!findAnotherOverlapValid)) {
                i_estimated_step = 3;
                display_flg = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((i_estimated_step == 2) && (i_estimated_order == 1)) {
        } else if ((i_estimated_step == 2) && (i_estimated_order == 2)) {
        } else if ((i_estimated_step == 2) && (i_estimated_order == 3)) {
            folding_estimated_03();
            i_estimated_step = 3;
            display_flg = DisplayStyle.TRANSPARENT_3;
        } else if ((i_estimated_step == 2) && (i_estimated_order == 5)) {
            folding_estimated_03();
            i_estimated_step = 3;
            display_flg = DisplayStyle.TRANSPARENT_3;
            folding_estimated_04();
            i_estimated_step = 4;
            display_flg = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            i_estimated_step = 5;
            display_flg = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && !findAnotherOverlapValid) {
                i_estimated_step = 3;
                display_flg = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((i_estimated_step == 3) && (i_estimated_order == 1)) {
        } else if ((i_estimated_step == 3) && (i_estimated_order == 2)) {
            display_flg = DisplayStyle.WIRE_2;
        } else if ((i_estimated_step == 3) && (i_estimated_order == 3)) {
            display_flg = DisplayStyle.TRANSPARENT_3;
        } else if ((i_estimated_step == 3) && (i_estimated_order == 5)) {
            folding_estimated_04();
            i_estimated_step = 4;
            display_flg = DisplayStyle.DEVELOPMENT_4;
            folding_estimated_05();
            i_estimated_step = 5;
            display_flg = DisplayStyle.PAPER_5;
            if ((discovered_fold_cases == 0) && !findAnotherOverlapValid) {
                i_estimated_step = 3;
                display_flg = DisplayStyle.TRANSPARENT_3;
            }
        } else if ((i_estimated_step == 5) && (i_estimated_order == 1)) {
        } else if ((i_estimated_step == 5) && (i_estimated_order == 2)) {
            display_flg = DisplayStyle.WIRE_2;
        } else if ((i_estimated_step == 5) && (i_estimated_order == 3)) {
            display_flg = DisplayStyle.TRANSPARENT_3;
        } else if ((i_estimated_step == 5) && (i_estimated_order == 5)) {
            display_flg = DisplayStyle.PAPER_5;
        } else if ((i_estimated_step == 5) && (i_estimated_order == 6)) {
            folding_estimated_05();
            i_estimated_step = 5;
            display_flg = DisplayStyle.PAPER_5;
        }

        if (i_camera_estimated == 1) {
            oritatami_suitei_camera_configure(camera_of_orisen_nyuuryokuzu, lineSegmentSet);
        }
    }

    public void folding_settings_two_color(Camera camera_of_orisen_nyuuryokuzu, LineSegmentSet Ss0) {//Two-color development drawing
        //-------------------------------折り上がり図表示用カメラの設定

        d_foldedFigure_scale_factor = camera_of_orisen_nyuuryokuzu.getCameraZoomX();
        orihime_app.text29.setText(String.valueOf(d_foldedFigure_scale_factor));
        orihime_app.text29.setCaretPosition(0);

        d_foldedFigure_rotation_correction = camera_of_orisen_nyuuryokuzu.getCameraAngle();
        orihime_app.text30.setText(String.valueOf(d_foldedFigure_rotation_correction));
        orihime_app.text30.setCaretPosition(0);

        double d_display_position_x = camera_of_orisen_nyuuryokuzu.getDisplayPositionX();
        double d_display_position_y = camera_of_orisen_nyuuryokuzu.getDisplayPositionY();

        camera_of_foldedFigure.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_foldedFigure.setDisplayPositionX(d_display_position_x + 20.0);
        camera_of_foldedFigure.setDisplayPositionY(d_display_position_y + 20.0);

        camera_of_foldedFigure_front.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_foldedFigure_front.setDisplayPositionX(d_display_position_x + 20.0);
        camera_of_foldedFigure_front.setDisplayPositionY(d_display_position_y + 20.0);

        camera_of_foldedFigure_rear.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_foldedFigure_rear.setDisplayPositionX(d_display_position_x + 40.0);
        camera_of_foldedFigure_rear.setDisplayPositionY(d_display_position_y + 20.0);

        camera_of_transparent_front.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_transparent_front.setDisplayPositionX(d_display_position_x + 20.0);
        camera_of_transparent_front.setDisplayPositionY(d_display_position_y + 0.0);

        camera_of_transparent_rear.setCamera(camera_of_orisen_nyuuryokuzu);
        camera_of_transparent_rear.setDisplayPositionX(d_display_position_x + 40.0);
        camera_of_transparent_rear.setDisplayPositionY(d_display_position_y + 0.0);

        double d_camera_mirror = camera_of_foldedFigure_rear.getCameraMirror();
        camera_of_foldedFigure_rear.setCameraMirror(d_camera_mirror * -1.0);
        camera_of_transparent_rear.setCameraMirror(d_camera_mirror * -1.0);

        estimated_initialize();
        folding_estimated_01(Ss0);
        i_estimated_step = 1;
        display_flg = DisplayStyle.DEVELOPMENT_1;
        oritatami_suitei_02col();
        i_estimated_step = 2;
        display_flg = DisplayStyle.WIRE_2;
        folding_estimated_03();
        i_estimated_step = 3;
        display_flg = DisplayStyle.TRANSPARENT_3;
        folding_estimated_04();
        i_estimated_step = 4;
        display_flg = DisplayStyle.DEVELOPMENT_4;
        folding_estimated_05();
        i_estimated_step = 5;
        display_flg = DisplayStyle.PAPER_5;
        i_estimated_step = 10;
    }

    public int folding_estimated_01(LineSegmentSet lineSegmentSet) {
        System.out.println("＜＜＜＜＜oritatami_suitei_01;開始");
        bulletinBoard.write("<<<<oritatami_suitei_01;  start");
        // Pass the line segment set created in es1 to cp_worker1 by mouse input and make it a point set (corresponding to the development view).
        cp_worker1.lineStore2pointStore(lineSegmentSet);
        ip3 = cp_worker1.setReferencePlaneId(ip3);
        ip3 = cp_worker1.setReferencePlaneId(orihime_app.point_of_referencePlane_old);//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加

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
        orihime_app.bulletinBoard.write("<<<<oritatami_suitei_02; end");

        //cp_worker2.Iti_sitei(0.0 , 0.0);点集合の平均位置を全点の重心にする。
        //  if(ip4==1){ cp_worker2.uragaesi();}
        // cp_worker2.set( cp_worker2.oritatami())  ;//折り畳んだ針金図を、折り開きたい場合の操作
        //ここまでで針金図はできていて、cp_worker2が持っている。これは、マウスで操作、変形できる。
        return 1000;
    }

    public int oritatami_suitei_02col() {//20171225　２色塗りわけをするための特別推定（折り畳み位置を推定しない）
        System.out.println("＜＜＜＜＜oritatami_suitei_02;開始");
        bulletinBoard.write("<<<<oritatami_suitei_02;  start");
        cp_worker2.set(cp_worker1.surface_position_request());
        orihime_app.bulletinBoard.write("<<<<oritatami_suitei_02; end");
        return 1000;
    }

    public int folding_estimated_03() {
        System.out.println("＜＜＜＜＜oritatami_suitei_03;開始");
        bulletinBoard.write("<<<<oritatami_suitei_03;  start");
        //cp_worker2は折る前の展開図の面を保持した点集合を持っている。
        //折りたたんだ場合の面の上下関係を推定するにはcp_worker2の持つ針金図に応じて面を
        //細分した（細分した面をSubFaceと言うことにする）点集合を使う。
        //このSubFace面に分割した点集合はcp_worker3が持つようにする。
        //cp_worker2の持つ点集合をcp_worker3に渡す前に、cp_worker2の持つ点集合は棒が重なっていたりするかもしれないので、
        //いったんbb_workerに渡して線分集合として整理する。
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
        //The development drawing craftsman cp_worker3 receives a point set (arranged wire diagram of cp_worker2) from bb_worker and divides it into SubFace.
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

        ip1 = 0;
        findAnotherOverlapValid = false;
        ip1 = ct_worker.HierarchyList_configure(cp_worker1, cp_worker2.get());   //ip1 = A variable that stores 0 if there is an error that the front and back sides are adjacent after folding, and 1000 if there is no error.
        if (ip1 == 1000) {
            findAnotherOverlapValid = true;
        }
        discovered_fold_cases = 0;
        System.out.println("＜＜＜＜＜oritatami_suitei_04()____終了");
        return 1000;
    }


    public int folding_estimated_05() {
        System.out.println("＜＜＜＜＜oritatami_suitei_05()_____上下表職人ct_workerがct_worker.kanou_kasanari_sagasi()実施。");
        orihime_app.bulletinBoard.write("<<<<oritatami_suitei_05()  ___ct_worker.kanou_kasanari_sagasi()  start");

        if ((i_estimated_step == 4) || (i_estimated_step == 5)) {
            if (findAnotherOverlapValid) {

                ip2 = ct_worker.possible_overlapping_search();//ip2=上下表職人が折り畳み可能な重なり方を探した際に、可能な重なり方がなければ0を、可能な重なり方があれば1000を格納する変数。

                if (ip2 == 1000) {
                    discovered_fold_cases = discovered_fold_cases + 1;
                }

                ip5 = ct_worker.next(ct_worker.getSubFace_valid_number());//Preparation for the next overlap // If ip5 = 0, there was no room for new susumu. If non-zero, the smallest number of changed SubFace ids
            }
        }
        orihime_app.bulletinBoard.clear();

        text_result = "Number of found solutions = " + discovered_fold_cases + "  ";

        findAnotherOverlapValid = false;
        if ((ip2 == 1000) && (ip5 > 0)) {
            findAnotherOverlapValid = true;
        }

        if (!findAnotherOverlapValid) {
            text_result = text_result + " There is no other solution. ";
        }

        return 1000;
    }

    public void toukazu_color_sage() {
        transparent_transparency = transparent_transparency / 2;
        if (transparent_transparency < 1) {
            transparent_transparency = 1;
        }
    }


    public void toukazu_color_age() {
        transparent_transparency = transparent_transparency * 2;
        if (transparent_transparency > 64) {
            transparent_transparency = 64;
        }
    }    //20180819バグ修正　透過度の最大値がこれまで128で、プログラムで線の描画時に２倍するとく、256となり、透過度の上限255オーバーで、オリヒメ自体が
    //フリーズした。これは、128を127の変えることでもフリーズはなくなるが、透過度は２の倍数にしておかないと、2分の一にしたとき値がずれるかもしれないので、透過度の最大値は64としておくことにする。


    private Point p_m_left_on = new Point();//Coordinates when the left mouse button is pressed
    private int i_nanini_near = 0;//Point p is close to the point in the development view = 1, close to the point in the folded view = 2, not close to either = 0
    private int i_closestPointId;
    private int i_point_selection = 0;//Both cp_worker1 and cp_worker2 are not selected (situation i_point_selection = 0), cp_worker1 is selected and cp_worker2 is not selected (situation i_point_selection = 1), and the vertex is cp_worker2 selected (situation i_point_selection = 2).
    private Point move_previous_selection_point = new Point();//動かす前の選択した点の座標

    public void foldedFigure_operation_mouse_on(Point p) {//Work when the left mouse button is pressed in the fold-up diagram operation
        if (i_foldedFigure_operation_mode == 1) {
            foldedFigure_operation_mouse_on_1(p);
        }
        if (i_foldedFigure_operation_mode == 2) {
            foldedFigure_operation_mouse_on_2(p);
        }
    }

    public void foldedFigure_operation_mouse_drag(Point p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        if (i_foldedFigure_operation_mode == 1) {
            foldedFigure_operation_mouse_drag_1(p);
        }
        if (i_foldedFigure_operation_mode == 2) {
            foldedFigure_operation_mouse_drag_2(p);
        }
    }

    public void foldedFigure_operation_mouse_off(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        if (i_foldedFigure_operation_mode == 1) {
            foldedFigure_operation_mouse_off_1(p);
        }
        if (i_foldedFigure_operation_mode == 2) {
            foldedFigure_operation_mouse_off_2(p);
        }
    }

    public void foldedFigure_operation_mouse_on_1(Point p) {//Work when the left mouse button is pressed in the folding diagram operation Folding function
        p_m_left_on.set(new Point(p.getX(), p.getY()));

        cp_worker2.setCamera(camera_of_foldedFigure);
        cp_worker2.setCam_front(camera_of_foldedFigure_front);
        cp_worker2.setCam_rear(camera_of_foldedFigure_rear);

        //i_closestPointIdにpに最も近い点の番号を格納。近い点がまったくない場合はi_closestPointId=0
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
        }//i_closestPointIdにpに最も近い点の番号を格納 ここまで

        move_previous_selection_point.set(cp_worker2.getPoint(i_closestPointId));


        System.out.println("i_nanini_tikai = " + i_nanini_near);

        if (i_nanini_near == 1) {
            //i_ten_sentakuを決める
            i_point_selection = 0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = 1;
            }
            if (cp_worker2.getPointState(i_closestPointId)) {
                i_point_selection = 2;
            }
            //i_ten_sentakuを決める  ここまで


            if (i_point_selection == 0) {
                setAllPointState0();
                //折り上がり図でi_closestPointIdと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                Point ps = new Point();
                ps.set(cp_worker2.getPoint(i_closestPointId));
                for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                    if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                        cp_worker1.setPointState1(i);
                    }
                }
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 1) {
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 2) {
                cp_worker2.changePointState(i_closestPointId);
            }
        }

        if (i_nanini_near == 2) {
            //i_ten_sentakuを決める
            i_point_selection = 0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = 1;
                if (cp_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = 2;
                }    //折図上で指定した点で、そこに重なるいずれかの点がcp_worker2で選択されている。要するに折図上の緑表示されている点を選んだ状態
            }
            //i_ten_sentakuを決める  ここまで
            System.out.println("i_ten_sentaku = " + i_point_selection);

            if (i_point_selection == 0) {
                setAllPointState0();

                //折り上がり図でi_closestPointIdと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                Point ps = new Point();
                ps.set(cp_worker2.getPoint(i_closestPointId));
                for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                    if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                        cp_worker1.setPointState1(i);
                    }
                }
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 1) {
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 2) {
            }

            if (i_foldedFigure_operation_mode == 1) {
                display_flg_backup = display_flg;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                display_flg = DisplayStyle.WIRE_2;            //20180216
            }
        }

        System.out.println("cp_worker1.get_ten_sentakusuu() = " + cp_worker1.getSelectedPointsNum());
        System.out.println("cp_worker2.get_ten_sentakusuu() = " + cp_worker2.getSelectedPointsNum());
    }

    public void foldedFigure_operation_mouse_drag_1(Point p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        cp_worker2.setCamera(camera_of_foldedFigure);
        cp_worker2.setCam_front(camera_of_foldedFigure_front);
        cp_worker2.setCam_rear(camera_of_foldedFigure_rear);

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
        cp_worker2.setCamera(camera_of_foldedFigure);
        cp_worker2.setCam_front(camera_of_foldedFigure_front);
        cp_worker2.setCam_rear(camera_of_foldedFigure_rear);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            display_flg = display_flg_backup;//20180216

            cp_worker2.mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, ip4);
            if (p_m_left_on.distance(p) > 0.0000001) {
                record();
                i_estimated_step = 2;

                if (display_flg == DisplayStyle.WIRE_2) {
                }

                if (display_flg == DisplayStyle.PAPER_5) {
                    i_estimated_order = 5;
                    orihime_app.folding_estimated();
                }//オリジナル 20180124 これ以外だと、表示いったんもどるようでうざい
            }

            cp_worker1.setAllPointState0();
            //折り上がり図でi_closestPointIdと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = new Point();
            ps.set(cp_worker2.getPoint(i_closestPointId));
            for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                    cp_worker1.setPointState1(i);
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

        cp_worker2.setCamera(camera_of_foldedFigure);
        cp_worker2.setCam_front(camera_of_foldedFigure_front);
        cp_worker2.setCam_rear(camera_of_foldedFigure_rear);

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
            i_point_selection = 0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = 1;
            }
            if (cp_worker2.getPointState(i_closestPointId)) {
                i_point_selection = 2;
            }
            //i_ten_sentakuを決める  ここまで

            if (i_point_selection == 0) {
                setAllPointState0();
                //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                Point ps = new Point();
                ps.set(cp_worker2.getPoint(i_closestPointId));
                for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                    if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                        cp_worker1.setPointState1(i);
                    }
                }
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 1) {
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 2) {
                cp_worker2.changePointState(i_closestPointId);
            }
        }

        if (i_nanini_near == 2) {
            //i_ten_sentakuを決める
            i_point_selection = 0;
            if (cp_worker1.getPointState(i_closestPointId)) {
                i_point_selection = 1;
                if (cp_worker2.getSelectedPointsNum() > 0) {
                    i_point_selection = 2;
                }    //折図上で指定した点で、そこに重なるいずれかの点がcp_worker2で選択されている。要するに折図上の緑表示されている点を選んだ状態
            }
            //i_ten_sentakuを決める  ここまで
            System.out.println("i_ten_sentaku = " + i_point_selection);

            if (i_point_selection == 0) {
                setAllPointState0();

                //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
                Point ps = new Point();
                ps.set(cp_worker2.getPoint(i_closestPointId));
                for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                    if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                        cp_worker1.setPointState1(i);
                    }
                }
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 1) {
                cp_worker2.changePointState(i_closestPointId);
            } else if (i_point_selection == 2) {
                //cp_worker2.change_ten_sentaku(i_mottomo_tikai_Tenid);
            }

            if (i_foldedFigure_operation_mode == 1) {
                display_flg_backup = display_flg;   //20180216  //display_flgは、折り上がり図の表示様式の指定。4なら実際に折り紙を折った場合と同じ。3なら透過図。2なら針金図。
                display_flg = DisplayStyle.WIRE_2;            //20180216
            }
        }

        System.out.println("cp_worker1.get_ten_sentakusuu() = " + cp_worker1.getSelectedPointsNum());
        System.out.println("cp_worker2.get_ten_sentakusuu() = " + cp_worker2.getSelectedPointsNum());
    }

    //-------------
    public void foldedFigure_operation_mouse_drag_2(Point p) {//折り上がり図操作でマウスの左ボタンを押したままドラッグしたときの作業
        cp_worker2.setCamera(camera_of_foldedFigure);
        cp_worker2.setCam_front(camera_of_foldedFigure_front);
        cp_worker2.setCam_rear(camera_of_foldedFigure_rear);

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
    public void foldedFigure_operation_mouse_off_2(Point p) {//折り上がり図操作でマウスの左ボタンを離したときの作業
        cp_worker2.setCamera(camera_of_foldedFigure);
        cp_worker2.setCam_front(camera_of_foldedFigure_front);
        cp_worker2.setCam_rear(camera_of_foldedFigure_rear);

        if (i_nanini_near == 1) {
        }

        if (i_nanini_near == 2) {
            cp_worker2.mReleased_selectedPoint_move_with_camera(move_previous_selection_point, p_m_left_on, p, ip4);
            if (p_m_left_on.distance(p) > 0.0000001) {
                record();
                i_estimated_step = 2;

                if (i_foldedFigure_operation_mode == 1) {
                    display_flg = display_flg_backup;//20180216
                }
                if (display_flg == DisplayStyle.WIRE_2) {
                }

                folding_estimated_03();//20180216
            }

            cp_worker1.setAllPointState0();
            //折り上がり図でi_mottomo_tikai_Tenidと同じ位置の点の番号を求め、cp_worker1でその番号の点を選択済みにする
            Point ps = new Point();
            ps.set(cp_worker2.getPoint(i_closestPointId));
            for (int i = 1; i <= cp_worker2.getPointsTotal(); i++) {
                if (ps.distance(cp_worker2.getPoint(i)) < 0.0000001) {
                    cp_worker1.setPointState1(i);
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

    public void setAllPointState0() {
        cp_worker1.setAllPointState0();
        cp_worker2.setAllPointState0();
    }

    public enum State {
        FRONT_0,
        BACK_1,
        BOTH_2,
        TRANSPARENT_3,
        ;

        public State advance() {
            return State.values()[(ordinal() + 1) % State.values().length];
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
