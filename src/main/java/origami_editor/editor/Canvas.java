package origami_editor.editor;

import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.drawing_worker.DrawingWorker;
import origami_editor.editor.drawing_worker.FoldLineAdditionalInputMode;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami_editor.record.Memo;
import origami_editor.tools.Camera;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Panel in the center of the main view.
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final DrawingWorker es1;
    private final App app;

    Graphics bufferGraphics;
    BufferedImage offscreen;//20181205new

    boolean flg_wi = false;//writeimage時につかう　1にするとpaintの関数の終了部にwriteimageするようにする。これは、paintの変更が書き出されるイメージに反映されないことを防ぐための工夫。20180528
    int btn = 0;//Stores which button in the center of the left and right is pressed. 1 =
    Point mouse_temp0 = new Point();//マウスの動作対応時に、一時的に使うTen

    boolean displayPointSpotlight;
    boolean displayPointOffset;
    boolean displayGridInputAssist;
    boolean displayComments;
    boolean displayCpLines;
    boolean displayAuxLines;
    boolean displayLiveAuxLines;
    boolean displayMarkings;
    boolean displayCreasePatternOnTop;
    boolean displayFoldingProgress;

    float auxLineWidth;
    float lineWidth;

    LineStyle lineStyle;

    MouseMode mouseMode;

    // Canvas width and height
    Dimension dim;
    private boolean antiAlias;
    private boolean mouseWheelMovesCreasePattern;

    public Camera creasePatternCamera = new Camera();

    public Canvas(App app0) {
        app = app0;

        offscreen = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_BGR);
        bufferGraphics = offscreen.createGraphics();    //20170107_new

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        es1 = app0.mainDrawingWorker;

        dim = getSize();
        System.out.println(" dim 001 :" + dim.width + " , " + dim.height);//多分削除可能
    }

    @Override
    public void paintComponent(Graphics g) {
        //「f」を付けることでfloat型の数値として記述することができる
        Graphics2D g2 = (Graphics2D) bufferGraphics;

        BasicStroke BStroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//線の太さや線の末端の形状

        //アンチエイリアス　オフ
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);//アンチエイリアス　オン

        g2.setBackground(Color.WHITE);    //この行は、画像をファイルに書き出そうとしてBufferedImageクラスを使う場合、デフォルトで背景が黒になるので、それを避けるための意味　20170107
        //画像をファイルに書き出さすことはやめて、、BufferedImageクラスを使わず、Imageクラスだけですむなら不要の行

        //別の重なりさがし　のボタンの色の指定。
        if (app.OZ.findAnotherOverlapValid) {
            app.Button_another_solution.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            app.Button_another_solution.setForeground(Color.black);

            app.Button_AS_matome.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            app.Button_AS_matome.setForeground(Color.black);

            app.Button_bangou_sitei_estimated_display.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            app.Button_bangou_sitei_estimated_display.setForeground(Color.black);
        } else {
            app.Button_another_solution.setBackground(new Color(201, 201, 201));
            app.Button_another_solution.setForeground(Color.gray);

            app.Button_AS_matome.setBackground(new Color(201, 201, 201));
            app.Button_AS_matome.setForeground(Color.gray);

            app.Button_bangou_sitei_estimated_display.setBackground(new Color(201, 201, 201));
            app.Button_bangou_sitei_estimated_display.setForeground(Color.gray);
        }

        // バッファー画面のクリア
        dim = getSize();
        bufferGraphics.clearRect(0, 0, dim.width, dim.height);

        bufferGraphics.setColor(Color.red);
        //描画したい内容は以下に書くことVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

        //カメラのセット
        app.mainDrawingWorker.setCamera(creasePatternCamera);

        FoldedFigure OZi;
        for (int i = 1; i <= app.foldedFigures.size() - 1; i++) {
            OZi = app.foldedFigures.get(i);
            OZi.cp_worker1.setCamera(creasePatternCamera);
        }

//VVVVVVVVVVVVVVV以下のts2へのカメラセットはOriagari_zuのoekakiで実施しているので以下の5行はなくてもいいはず　20180225
        app.OZ.cp_worker2.setCamera(app.OZ.foldedFigureCamera);
        app.OZ.cp_worker2.setCam_front(app.OZ.foldedFigureFrontCamera);
        app.OZ.cp_worker2.setCam_rear(app.OZ.foldedFigureRearCamera);
        app.OZ.cp_worker2.setCam_transparent_front(app.OZ.transparentFrontCamera);
        app.OZ.cp_worker2.setCam_transparent_rear(app.OZ.transparentRearCamera);
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

        //System.out.println("paint　+++++++++++++++++++++　背景表示");
        //背景表示
        if ((app.img_background != null) && app.backgroundModel.isDisplayBackground()) {
            int iw = app.img_background.getWidth(this);//イメージの幅を取得
            int ih = app.img_background.getHeight(this);//イメージの高さを取得

            app.h_cam.setBackgroundWidth(iw);
            app.h_cam.setBackgroundHeight(ih);

            drawBackground(g2, app.img_background);
        }

        //格子表示

        //基準面の表示
        if (displayMarkings) {
            if (app.OZ.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                app.OZ.cp_worker1.drawing_referencePlane_with_camera(bufferGraphics);//ts1が折り畳みを行う際の基準面を表示するのに使う。
            }
        }

        double d_width = creasePatternCamera.getCameraZoomX() * app.mainDrawingWorker.getSelectionDistance();
        //Flashlight (dot) search range
        if (displayPointSpotlight) {
            g2.setColor(new Color(255, 240, 0, 30));
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(255, 240, 0, 230));
            g2.draw(new Ellipse2D.Double(app.p_mouse_TV_position.getX() - d_width, app.p_mouse_TV_position.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
        }

        //Luminous flux of flashlight, etc.
        if (displayPointSpotlight && displayPointOffset) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(255, 240, 0, 170));
        }

        //展開図表示
        es1.drawWithCamera(bufferGraphics, displayComments, displayCpLines, displayAuxLines, displayLiveAuxLines, lineWidth, lineStyle, auxLineWidth, dim.width, dim.height, displayMarkings);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ,展開図動かし中心の十字の目印の表示

        if (displayComments) {
            //展開図情報の文字表示
            bufferGraphics.setColor(Color.black);

            bufferGraphics.drawString(String.format("mouse= ( %.2f, %.2f )", app.p_mouse_object_position.getX(), app.p_mouse_object_position.getY()), 10, 10); //この表示内容はvoid kekka_syoriで決められる。

            bufferGraphics.drawString("L=" + app.mainDrawingWorker.getTotal(), 10, 25); //この表示内容はvoid kekka_syoriで決められる。

            //結果の文字表示
            bufferGraphics.drawString(app.OZ.text_result, 10, 40); //この表示内容はvoid kekka_syoriで決められる。

            if (displayGridInputAssist) {
                Point gridIndex = new Point(app.mainDrawingWorker.getGridPosition(app.p_mouse_TV_position));//20201024高密度入力がオンならばrepaint（画面更新）のたびにここで最寄り点を求めているので、描き職人で別途最寄り点を求めていることと二度手間になっている。

                double dx_ind = gridIndex.getX();
                double dy_ind = gridIndex.getY();
                int ix_ind = (int) Math.round(dx_ind);
                int iy_ind = (int) Math.round(dy_ind);
                bufferGraphics.drawString("(" + ix_ind + "," + iy_ind + ")", (int) app.p_mouse_TV_position.getX() + 25, (int) app.p_mouse_TV_position.getY() + 20); //この表示内容はvoid kekka_syoriで決められる。
            }

            if (app.subThreadRunning) {
                bufferGraphics.setColor(Color.red);

                bufferGraphics.drawString("Under Calculation. If you want to cancel calculation, uncheck [check A + MV]on right side and press the brake button (bicycle brake icon) on lower side.", 10, 69); //この表示内容はvoid kekka_syoriで決められる。
                bufferGraphics.drawString("計算中。　なお、計算を取り消し通常状態に戻りたいなら、右辺の[check A+MV]のチェックをはずし、ブレーキボタン（下辺の、自転車のブレーキのアイコン）を押す。 ", 10, 83); //この表示内容はvoid kekka_syoriで決められる。
            }

            app.bulletinBoard.draw(bufferGraphics);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        }


        //折り上がりの各種お絵かき
        for (int i = 1; i <= app.foldedFigures.size() - 1; i++) {
            OZi = app.foldedFigures.get(i);
            OZi.foldUp_draw(bufferGraphics, displayMarkings);
        }

        //展開図を折り上がり図の上に描くために、展開図を再表示する
        if (displayCreasePatternOnTop) {
            es1.drawWithCamera(bufferGraphics, displayComments, displayCpLines, displayAuxLines, displayLiveAuxLines, lineWidth, lineStyle, auxLineWidth, dim.width, dim.height, displayMarkings);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ
        }

        //アンチェイリアス
        //アンチェイリアス　オフ
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オン

        //Central indicator
        if (displayPointOffset) {
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(Color.black);
            g2.drawLine((int) (app.p_mouse_TV_position.getX()), (int) (app.p_mouse_TV_position.getY()),
                    (int) (app.p_mouse_TV_position.getX() + d_width), (int) (app.p_mouse_TV_position.getY() + d_width)); //直線
        }

        //描画したい内容はここまでAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

        // オフスクリーンイメージを実際に描画する。オフスクリーンの幅は最初は 0,0。
        g.drawImage(offscreen, 0, 0, this);

        if (app.OZ.summary_write_image_during_execution) {//Meaning during summary writing)
            writeImageFile(app.fname_and_number, app);

            synchronized (app.w_image_running) {
                app.w_image_running.set(false);
                app.w_image_running.notify();
            }
        }

        if (flg_wi) {//For control when exporting with a frame 20180525
            flg_wi = false;
            writeImageFile(app.fname_wi, app);
        }
        if (app.flg61) {
            app.flg61 = false;
            es1.setDrawingStage(4);
        }
    }

    public void drawBackground(Graphics2D g2h, Image imgh) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        //背景画を、画像の左上はしを、ウィンドウの(0,0)に合わせて回転や拡大なしで表示した場合を基準状態とする。
        //背景画上の点h1を中心としてa倍拡大する。次に、h1を展開図上の点h3と重なるように背景画を平行移動する。
        //この状態の展開図を、h3を中心にb度回転したよう見えるように座標を回転させて貼り付けて、その後、座標の回転を元に戻すという関数。
        //引数は、Graphics2D g2h,Image imgh,Ten h1,Ten h2,Ten h3,Ten h4
        //h2,とh4も重なるようにする
        //

        //最初に
        if (app.backgroundModel.isLockBackground()) {
            app.h_cam.setCamera(creasePatternCamera);
            app.h_cam.h3_and_h4_calculation();
            app.h_cam.parameter_calculation();
        }

        AffineTransform at = new AffineTransform();
        at.rotate(app.h_cam.getAngle() * Math.PI / 180.0, app.h_cam.getRotationX(), app.h_cam.getRotationY());
        g2h.setTransform(at);


        g2h.drawImage(imgh, app.h_cam.getX0(), app.h_cam.getY0(), app.h_cam.getX1(), app.h_cam.getY1(), this);

        //g2h.drawImage(imgh,kaisi_x,kaisi_y,this);//hx0,hy0,は描画開始位置

        at.rotate(-app.h_cam.getAngle() * Math.PI / 180.0, app.h_cam.getRotationX(), app.h_cam.getRotationY());
        g2h.setTransform(at);

    }

    // マウス操作(マウスが動いた時)を行う関数----------------------------------------------------
    public void mouseMoved(MouseEvent e) {
        //何もしない
        //  final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();//これは多分J2SE 5.0「Tiger」以降で作動するコード

        Point p = new Point(app.e2p(e));
        app.mouse_object_position(p);
        switch (mouseMode) {
            case UNUSED_0:
                break;
            case DRAW_CREASE_FREE_1:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_01(p);
                break;
            case LENGTHEN_CREASE_5:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_05(p);
                break;
            case SQUARE_BISECTOR_7:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_07(p);
                break;
            case INWARD_8:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_08(p);
                break;
            case PERPENDICULAR_DRAW_9:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_09(p);
                break;
            case SYMMETRIC_DRAW_10:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_10(p);
                break;
            case DRAW_CREASE_RESTRICTED_11:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_11(p);
                break;
            case DRAW_CREASE_SYMMETRIC_12:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_12(p);
                break;
            case ANGLE_SYSTEM_16:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_16(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_2_17:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_17(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_18(p);
                break;
            case CREASE_MOVE_21:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_21(p);
                break;
            case CREASE_COPY_22:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_22(p);
                break;
            case LINE_SEGMENT_DIVISION_27:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_27(p);
                break;
            case LINE_SEGMENT_RATIO_SET_28:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_28(p);
                break;
            case POLYGON_SET_NO_CORNERS_29:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_29(p);
                break;
            case CREASE_MOVE_4P_31:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_31(p);
                break;
            case CREASE_COPY_4P_32:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_32(p);
                break;
            case FISH_BONE_DRAW_33:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_33(p);
                break;
            case CREASE_MAKE_MV_34:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_34(p);
                break;
            case DOUBLE_SYMMETRIC_DRAW_35:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_35(p);
                break;
            case CREASES_ALTERNATE_MV_36:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_36(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_3_37:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_37(p);
                break;
            case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_38(p);
                break;
            case FOLDABLE_LINE_INPUT_39:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_39(p);
                break;
            case PARALLEL_DRAW_40:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_40(p);
                break;
            case CONTINUOUS_SYMMETRIC_DRAW_52:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_52(p);
                break;
            case DISPLAY_LENGTH_BETWEEN_POINTS_1_53:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_53(p);
                break;
            case DISPLAY_LENGTH_BETWEEN_POINTS_2_54:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_54(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_55(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_56(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_57(p);
                break;
            case OPERATION_FRAME_CREATE_61:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_61(p);
                break;
            case VORONOI_CREATE_62:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_62(p);
                break;
            case FLAT_FOLDABLE_CHECK_63:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_63(p);
                break;
            case CREASE_DELETE_OVERLAPPING_64:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_64(p);
                break;
            case CREASE_DELETE_INTERSECTING_65:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_65(p);
                break;
            case SELECT_POLYGON_66:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_66(p);
                break;
            case UNSELECT_POLYGON_67:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_67(p);
                break;
            case SELECT_LINE_INTERSECTING_68:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_68(p);
                break;
            case UNSELECT_LINE_INTERSECTING_69:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_69(p);
                break;
            case CREASE_LENGTHEN_70:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_70(p);
                break;
            case FOLDABLE_LINE_DRAW_71:
                es1.setCamera(creasePatternCamera);
                es1.mMoved_A_71(p);
                break;
            default:
                break;
        }

        repaint();
    }

    //マウス操作(ボタンを押したとき)を行う関数----------------------------------------------------
    public void mousePressed(MouseEvent e) {
        Point p = new Point(app.e2p(e));

        app.mouseDraggedValid = true;
        app.mouseReleasedValid = true;

        btn = e.getButton();

        //---------ボタンの種類による動作変更-----------------------------------------
        switch (btn) {
            case MouseEvent.BUTTON1:
                if (e.getClickCount() == 3) {
                    System.out.println("3_Click");//("トリプルクリック"
                    if (mouseMode == MouseMode.CREASE_SELECT_19) {
                        if (app.ckbox_add_frame_SelectAnd3click_isSelected) {
                            switch (app.canvasModel.getSelectionOperationMode()) {
                                case MOVE_1:
                                    app.canvasModel.setMouseMode(MouseMode.CREASE_MOVE_21);
                                    break;
                                case MOVE4P_2:
                                    app.canvasModel.setMouseMode(MouseMode.CREASE_MOVE_4P_31);
                                    break;
                                case COPY_3:
                                    app.canvasModel.setMouseMode(MouseMode.CREASE_COPY_22);
                                    break;
                                case COPY4P_4:
                                    app.canvasModel.setMouseMode(MouseMode.CREASE_COPY_4P_32);
                                    break;
                                case MIRROR_5:
                                    app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_SYMMETRIC_12);
                                    break;
                            }
                        }
                    }
                }
                break;
            case MouseEvent.BUTTON2:
                System.out.println("中ボタンクリック");

                App.MouseWheelTarget target = app.pointInCreasePatternOrFoldedFigure(p);

                System.out.println("i_cp_or_oriagari = " + target);

                switch (target) {
                    case CREASE_PATTERN_0: // 展開図移動。
                        creasePatternCamera.camera_position_specify_from_TV(p);
                        break;
                    case FOLDED_FRONT_1:
                        app.OZ.foldedFigureFrontCamera.camera_position_specify_from_TV(p);
                        break;
                    case FOLDED_BACK_2:
                        app.OZ.foldedFigureRearCamera.camera_position_specify_from_TV(p);
                        break;
                    case TRANSPARENT_FRONT_3:
                        app.OZ.transparentFrontCamera.camera_position_specify_from_TV(p);
                        break;
                    case TRANSPARENT_BACK_4:
                        app.OZ.transparentRearCamera.camera_position_specify_from_TV(p);
                        break;
                }

                mouse_temp0.set(p);
                repaint();
                return;
            case MouseEvent.BUTTON3: //右ボタンクリック
                if (mouseMode == MouseMode.VORONOI_CREATE_62) {//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                } else {
                    app.i_mouse_right_button_on = true;

                    //線分削除モード。
                    es1.setCamera(creasePatternCamera);
                    es1.mPressed_A_03(p);

                    app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.BOTH_4);
                }
                repaint();

                return;
        }
        //-----------------------------System.out.println("a");----------------------

        //}  //20201010　コメントアウト


        switch (mouseMode) {
            case UNUSED_0:
                break;
            case DRAW_CREASE_FREE_1:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_01(p);
                break;
            case MOVE_CREASE_PATTERN_2:                                        //2 展開図移動。
                creasePatternCamera.camera_position_specify_from_TV(p);
                mouse_temp0.set(p);
                break;
            case LINE_SEGMENT_DELETE_3:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_03(p);
                break;
            case CHANGE_CREASE_TYPE_4:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_04(p);
                break;
            case LENGTHEN_CREASE_5:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_05(p);
                break;
            case UNUSED_6:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_06(p);
                break;
            case SQUARE_BISECTOR_7:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_07(p);
                break;
            case INWARD_8:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_08(p);
                break;
            case PERPENDICULAR_DRAW_9:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_09(p);
                break;
            case SYMMETRIC_DRAW_10:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_10(p);
                break;
            case DRAW_CREASE_RESTRICTED_11:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_11(p);
                break;
            case DRAW_CREASE_SYMMETRIC_12:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_12(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_13:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_13(p);
                break;
            case DRAW_POINT_14:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_14(p);
                break;
            case DELETE_POINT_15:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_15(p);
                break;
            case ANGLE_SYSTEM_16:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_16(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_2_17:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_17(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_18(p);
                break;
            case CREASE_SELECT_19:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_19(p);
                break;
            case CREASE_UNSELECT_20:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_20(p);
                break;
            case CREASE_MOVE_21:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_21(p);
                break;
            case CREASE_COPY_22:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_22(p);
                break;
            case CREASE_MAKE_MOUNTAIN_23:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_23(p);
                break;
            case CREASE_MAKE_VALLEY_24:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_24(p);
                break;
            case CREASE_MAKE_EDGE_25:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_25(p);
                break;
            case BACKGROUND_CHANGE_POSITION_26:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_26(p);
                break;
            case LINE_SEGMENT_DIVISION_27:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_27(p);
                break;
            case LINE_SEGMENT_RATIO_SET_28:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_28(p);
                break;
            case POLYGON_SET_NO_CORNERS_29:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_29(p);
                break;
            case CREASE_ADVANCE_TYPE_30:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_30(p);
                break;
            case CREASE_MOVE_4P_31:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_31(p);
                break;
            case CREASE_COPY_4P_32:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_32(p);
                break;
            case FISH_BONE_DRAW_33:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_33(p);
                break;
            case CREASE_MAKE_MV_34:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_34(p);
                break;
            case DOUBLE_SYMMETRIC_DRAW_35:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_35(p);
                break;
            case CREASES_ALTERNATE_MV_36:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_36(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_3_37:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_37(p);
                break;
            case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_38(p);
                break;
            case FOLDABLE_LINE_INPUT_39:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_39(p);
                break;
            case PARALLEL_DRAW_40:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_40(p);
                break;
            case VERTEX_DELETE_ON_CREASE_41:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_41(p);
                break;
            case CIRCLE_DRAW_42:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_42(p);
                break;
            case CIRCLE_DRAW_THREE_POINT_43:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_43(p);
                break;
            case CIRCLE_DRAW_SEPARATE_44:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_44(p);
                break;
            case CIRCLE_DRAW_TANGENT_LINE_45:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_45(p);
                break;
            case CIRCLE_DRAW_INVERTED_46:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_46(p);
                break;
            case CIRCLE_DRAW_FREE_47:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_47(p);
                break;
            case CIRCLE_DRAW_CONCENTRIC_48:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_48(p);
                break;
            case CIRCLE_DRAW_CONCENTRIC_SELECT_49:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_49(p);
                break;
            case CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_50(p);
                break;
            case PARALLEL_DRAW_WIDTH_51:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_51(p);
                break;
            case CONTINUOUS_SYMMETRIC_DRAW_52:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_52(p);
                break;
            case DISPLAY_LENGTH_BETWEEN_POINTS_1_53:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_53(p);
                break;
            case DISPLAY_LENGTH_BETWEEN_POINTS_2_54:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_54(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_55(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_56(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_57(p);
                break;
            case CREASE_TOGGLE_MV_58:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_58(p);
                break;
            case CIRCLE_CHANGE_COLOR_59:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_59(p);
                break;
            case CREASE_MAKE_AUX_60:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_60(p);
                break;
            case OPERATION_FRAME_CREATE_61:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_61(p);
                break;
            case VORONOI_CREATE_62:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_62(p);
                break;
            case FLAT_FOLDABLE_CHECK_63:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_63(p);
                break;
            case CREASE_DELETE_OVERLAPPING_64:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_64(p);
                break;
            case CREASE_DELETE_INTERSECTING_65:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_65(p);
                break;
            case SELECT_POLYGON_66:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_66(p);
                break;
            case UNSELECT_POLYGON_67:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_67(p);
                break;
            case SELECT_LINE_INTERSECTING_68:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_68(p);
                break;
            case UNSELECT_LINE_INTERSECTING_69:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_69(p);
                break;
            case CREASE_LENGTHEN_70:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_70(p);
                break;
            case FOLDABLE_LINE_DRAW_71:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_71(p);
                break;
            case UNUSED_10001:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_10001(p);
                break;
            case UNUSED_10002:
                es1.setCamera(creasePatternCamera);
                es1.mPressed_A_10002(p);
                break;
            case MODIFY_CALCULATED_SHAPE_101:         //折り上がり図操作
                app.OZ.foldedFigure_operation_mouse_on(p);
                break;
            case MOVE_CALCULATED_SHAPE_102: //折り上がり図移動
                app.OZ.foldedFigureCamera.camera_position_specify_from_TV(p);
                app.OZ.foldedFigureFrontCamera.camera_position_specify_from_TV(p);
                app.OZ.foldedFigureRearCamera.camera_position_specify_from_TV(p);

                app.OZ.transparentFrontCamera.camera_position_specify_from_TV(p);
                app.OZ.transparentRearCamera.camera_position_specify_from_TV(p);

                mouse_temp0.set(p);
                break;
            case CHANGE_STANDARD_FACE_103:
                //ts1.set_kijyunmen_id(p);
                break;
        }

        repaint();

        //add_frame_to_Front();
    }

    //マウス操作(ドラッグしたとき)を行う関数---------- System.out.println("A");------------------------------------------
    public void mouseDragged(MouseEvent e) {

        if (app.mouseDraggedValid) {
            Point p = new Point(app.e2p(e));
            app.mouse_object_position(p);

            //if (ckbox_mouse_settei.isSelected()){  //20201010　コメントアウト
            //---------ボタンの種類による動作変更-----------------------------------------
            switch (btn) {
                case MouseEvent.BUTTON1:
                    break;
                case MouseEvent.BUTTON2:
                    switch (app.i_cp_or_oriagari) {
                        case CREASE_PATTERN_0: // 展開図移動。
                            creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            es1.setCamera(creasePatternCamera);
                            break;
                        case FOLDED_FRONT_1:
                            app.OZ.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case FOLDED_BACK_2:
                            app.OZ.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_FRONT_3:
                            app.OZ.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_BACK_4:
                            app.OZ.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                    }

                    mouse_temp0.set(p);
                    repaint();
                    return;

                case MouseEvent.BUTTON3:
                    if (mouseMode == MouseMode.VORONOI_CREATE_62) {//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                    } else {
                        if (app.i_mouse_undo_redo_mode) {
                            return;
                        }//undo,redoモード。
                        es1.setCamera(creasePatternCamera);
                        es1.mDragged_A_03(p);//線分削除モード。
                    }
                    repaint();
                    return;
            }
            //}  //20201010　コメントアウト


            switch (mouseMode) {
                case UNUSED_0:
                    break;
                case DRAW_CREASE_FREE_1:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_01(p);
                    break;
                case MOVE_CREASE_PATTERN_2:
                    creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    es1.setCamera(creasePatternCamera);

//20180225追加
                    FoldedFigure OZi;
                    for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                        OZi = app.foldedFigures.get(i_oz);

                        OZi.foldedFigureCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    }
//20180225追加　ここまで

                    mouse_temp0.set(p);
                    break;
                case LINE_SEGMENT_DELETE_3:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_03(p);
                    break;
                case CHANGE_CREASE_TYPE_4:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_04(p);
                    break;
                case LENGTHEN_CREASE_5:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_05(p);
                    break;
                case UNUSED_6:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_06(p);
                    break;
                case SQUARE_BISECTOR_7:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_07(p);
                    break;
                case INWARD_8:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_08(p);
                    break;
                case PERPENDICULAR_DRAW_9:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_09(p);
                    break;
                case SYMMETRIC_DRAW_10:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_10(p);
                    break;
                case DRAW_CREASE_RESTRICTED_11:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_11(p);
                    break;
                case DRAW_CREASE_SYMMETRIC_12:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_12(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_13:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_13(p);
                    break;
                case DRAW_POINT_14:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_14(p);
                    break;
                case DELETE_POINT_15:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_15(p);
                    break;
                case ANGLE_SYSTEM_16:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_16(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_2_17:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_17(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_18(p);
                    break;
                case CREASE_SELECT_19:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_19(p);
                    break;
                case CREASE_UNSELECT_20:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_20(p);
                    break;
                case CREASE_MOVE_21:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_21(p);
                    break;
                case CREASE_COPY_22:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_22(p);
                    break;
                case CREASE_MAKE_MOUNTAIN_23:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_23(p);
                    break;
                case CREASE_MAKE_VALLEY_24:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_24(p);
                    break;
                case CREASE_MAKE_EDGE_25:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_25(p);
                    break;
                case BACKGROUND_CHANGE_POSITION_26:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_26(p);
                    break;
                case LINE_SEGMENT_DIVISION_27:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_27(p);
                    break;
                case LINE_SEGMENT_RATIO_SET_28:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_28(p);
                    break;
                case POLYGON_SET_NO_CORNERS_29:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_29(p);
                    break;
                case CREASE_ADVANCE_TYPE_30:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_30(p);
                    break;
                case CREASE_MOVE_4P_31:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_31(p);
                    break;
                case CREASE_COPY_4P_32:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_32(p);
                    break;
                case FISH_BONE_DRAW_33:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_33(p);
                    break;
                case CREASE_MAKE_MV_34:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_34(p);
                    break;
                case DOUBLE_SYMMETRIC_DRAW_35:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_35(p);
                    break;
                case CREASES_ALTERNATE_MV_36:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_36(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_37:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_37(p);
                    break;
                case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_38(p);
                    break;
                case FOLDABLE_LINE_INPUT_39:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_39(p);
                    break;
                case PARALLEL_DRAW_40:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_40(p);
                    break;
                case VERTEX_DELETE_ON_CREASE_41:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_41(p);
                    break;
                case CIRCLE_DRAW_42:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_42(p);
                    break;
                case CIRCLE_DRAW_THREE_POINT_43:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_43(p);
                    break;
                case CIRCLE_DRAW_SEPARATE_44:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_44(p);
                    break;
                case CIRCLE_DRAW_TANGENT_LINE_45:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_45(p);
                    break;
                case CIRCLE_DRAW_INVERTED_46:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_46(p);
                    break;
                case CIRCLE_DRAW_FREE_47:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_47(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_48:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_48(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_SELECT_49:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_49(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_50(p);
                    break;
                case PARALLEL_DRAW_WIDTH_51:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_51(p);
                    break;
                case CONTINUOUS_SYMMETRIC_DRAW_52:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_52(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_1_53:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_53(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_2_54:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_54(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_55(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_56(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_57(p);
                    break;
                case CREASE_TOGGLE_MV_58:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_58(p);
                    break;
                case CIRCLE_CHANGE_COLOR_59:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_59(p);
                    break;
                case CREASE_MAKE_AUX_60:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_60(p);
                    break;
                case OPERATION_FRAME_CREATE_61:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_61(p);
                    break;
                case VORONOI_CREATE_62:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_62(p);
                    break;
                case FLAT_FOLDABLE_CHECK_63:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_63(p);
                    break;
                case CREASE_DELETE_OVERLAPPING_64:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_64(p);
                    break;
                case CREASE_DELETE_INTERSECTING_65:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_65(p);
                    break;
                case SELECT_POLYGON_66:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_66(p);
                    break;
                case UNSELECT_POLYGON_67:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_67(p);
                    break;
                case SELECT_LINE_INTERSECTING_68:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_68(p);
                    break;
                case UNSELECT_LINE_INTERSECTING_69:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_69(p);
                    break;
                case CREASE_LENGTHEN_70:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_70(p);
                    break;
                case FOLDABLE_LINE_DRAW_71:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_71(p);
                    break;
                case UNUSED_10001:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_10001(p);
                    break;
                case UNUSED_10002:
                    es1.setCamera(creasePatternCamera);
                    es1.mDragged_A_10002(p);
                    break;
                case MODIFY_CALCULATED_SHAPE_101:
                    app.OZ.foldedFigure_operation_mouse_drag(p);
                    break;
                case MOVE_CALCULATED_SHAPE_102:
                    app.OZ.foldedFigureCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));

                    app.OZ.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));

                    mouse_temp0.set(p);//mouse_temp0は一時的に使うTen、mouse_temp0.tano_Ten_iti(p)はmouse_temp0から見たpの位置

                    break;
                case CHANGE_STANDARD_FACE_103:
                    break;
            }

            repaint();
        }
    }

    //マウス操作(ボタンをクリックしたとき)を行う関数----------------------------------------------------
    public void mouseClicked(MouseEvent e) {
        //何もしない
    }

    //マウス操作(カーソルが有効領域内に入ったとき)を行う関数----------------------------------------------------
    public void mouseEntered(MouseEvent e) {
        //何もしない
    }

    //マウス操作(カーソルが有効領域外に出たとき)を行う関数----------------------------------------------------
    public void mouseExited(MouseEvent e) {
        //何もしない
    }

    //マウス操作(ボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(MouseEvent e) {
        if (app.mouseReleasedValid) {
            Point p = new Point(app.e2p(e));


            //---------ボタンの種類による動作変更-----------------------------------------
            switch (btn) {
                case MouseEvent.BUTTON1:
                    //
                    break;
                case MouseEvent.BUTTON2:
                    switch (app.i_cp_or_oriagari) {
                        case CREASE_PATTERN_0:
                            creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            es1.setCamera(creasePatternCamera);
                            break;
                        case FOLDED_FRONT_1:
                            app.OZ.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case FOLDED_BACK_2:
                            app.OZ.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_FRONT_3:
                            app.OZ.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_BACK_4:
                            app.OZ.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                    }

                    mouse_temp0.set(p);
                    repaint();
                    app.mouseDraggedValid = false;
                    app.mouseReleasedValid = false;
                    return;//
                case MouseEvent.BUTTON3:
                    //System.out.println("右ボタンクリック");
                    if (mouseMode == MouseMode.VORONOI_CREATE_62) {
                        repaint();//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                    } else {

                        app.i_mouse_right_button_on = false;

                        //if(i_mouse_undo_redo_mode==1){i_mouse_undo_redo_mode=0;mainDrawingWorker.unselect_all();Button_kyoutuu_sagyou();mainDrawingWorker.modosi_i_orisen_hojyosen();return;}
                        if (app.i_mouse_undo_redo_mode) {
                            app.i_mouse_undo_redo_mode = false;
                            return;
                        } //undo,redoモード。
                        es1.setCamera(creasePatternCamera);
                        es1.mReleased_A_03(p);
                        repaint();//なんでここにrepaintがあるか検討した方がよいかも。20181208
                        app.canvasModel.restoreFoldLineAdditionalInputMode();
                        app.mouseDraggedValid = false;
                        app.mouseReleasedValid = false;
                        //線分削除モード。
                    }
                    return;
            }
            //----------------------------System.out.println("a");-----------------------
            //}  //20201010　コメントアウト


            switch (mouseMode) {
                case UNUSED_0:
                    break;
                case DRAW_CREASE_FREE_1:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_01(p);
                    break;
                case MOVE_CREASE_PATTERN_2:
                    creasePatternCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    es1.setCamera(creasePatternCamera);


//20180225追加
                    FoldedFigure OZi;
                    for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                        OZi = app.foldedFigures.get(i_oz);

                        OZi.foldedFigureCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    }
//20180225追加　ここまで

                    mouse_temp0.set(p);
                    break;
                case LINE_SEGMENT_DELETE_3:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_03(p);
                    break;
                case CHANGE_CREASE_TYPE_4:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_04(p);
                    break;
                case LENGTHEN_CREASE_5:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_05(p);
                    break;
                case UNUSED_6:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_06(p);
                    break;
                case SQUARE_BISECTOR_7:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_07(p);
                    break;
                case INWARD_8:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_08(p);
                    break;
                case PERPENDICULAR_DRAW_9:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_09(p);
                    break;
                case SYMMETRIC_DRAW_10:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_10(p);
                    break;
                case DRAW_CREASE_RESTRICTED_11:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_11(p);
                    break;
                case DRAW_CREASE_SYMMETRIC_12:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_12(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_13:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_13(p);
                    break;
                case DRAW_POINT_14:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_14(p);
                    break;
                case DELETE_POINT_15:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_15(p);
                    break;
                case ANGLE_SYSTEM_16:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_16(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_2_17:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_17(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_18(p);
                    break;
                case CREASE_SELECT_19:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_19(p);
                    break;
                case CREASE_UNSELECT_20:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_20(p);
                    break;
                case CREASE_MOVE_21:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_21(p);
                    break;
                case CREASE_COPY_22:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_22(p);
                    break;
                case CREASE_MAKE_MOUNTAIN_23:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_23(p);
                    break;
                case CREASE_MAKE_VALLEY_24:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_24(p);
                    break;
                case CREASE_MAKE_EDGE_25:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_25(p);
                    break;
                case BACKGROUND_CHANGE_POSITION_26:
                    es1.setCamera(creasePatternCamera);

                    if (es1.mReleased_A_26(p) == 4) {
                        app.Button_shared_operation();
                        LineSegment s_1 = new LineSegment();
                        s_1.set(es1.get_s_step(1));
                        LineSegment s_2 = new LineSegment();
                        s_2.set(es1.get_s_step(2));
                        LineSegment s_3 = new LineSegment();
                        s_3.set(es1.get_s_step(3));
                        LineSegment s_4 = new LineSegment();
                        s_4.set(es1.get_s_step(4));

                        app.backgroundModel.setLockBackground(false);

                        app.background_set(creasePatternCamera.object2TV(s_1.getA()),
                                creasePatternCamera.object2TV(s_2.getA()),
                                creasePatternCamera.object2TV(s_3.getA()),
                                creasePatternCamera.object2TV(s_4.getA()));
                    }
                    break;
                case LINE_SEGMENT_DIVISION_27:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_27(p);
                    break;
                case LINE_SEGMENT_RATIO_SET_28:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_28(p);
                    break;
                case POLYGON_SET_NO_CORNERS_29:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_29(p);
                    break;
                case CREASE_ADVANCE_TYPE_30:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_30(p);
                    break;
                case CREASE_MOVE_4P_31:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_31(p);
                    break;
                case CREASE_COPY_4P_32:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_32(p);
                    break;
                case FISH_BONE_DRAW_33:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_33(p);
                    break;
                case CREASE_MAKE_MV_34:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_34(p);
                    break;
                case DOUBLE_SYMMETRIC_DRAW_35:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_35(p);
                    break;
                case CREASES_ALTERNATE_MV_36:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_36(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_37:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_37(p);
                    break;
                case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_38(p);
                    break;
                case FOLDABLE_LINE_INPUT_39:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_39(p);
                    break;
                case PARALLEL_DRAW_40:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_40(p);
                    break;
                case VERTEX_DELETE_ON_CREASE_41:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_41(p);
                    break;
                case CIRCLE_DRAW_42:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_42(p);
                    break;
                case CIRCLE_DRAW_THREE_POINT_43:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_43(p);
                    break;
                case CIRCLE_DRAW_SEPARATE_44:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_44(p);
                    break;
                case CIRCLE_DRAW_TANGENT_LINE_45:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_45(p);
                    break;
                case CIRCLE_DRAW_INVERTED_46:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_46(p);
                    break;
                case CIRCLE_DRAW_FREE_47:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_47(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_48:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_48(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_SELECT_49:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_49(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_50(p);
                    break;
                case PARALLEL_DRAW_WIDTH_51:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_51(p);
                    break;
                case CONTINUOUS_SYMMETRIC_DRAW_52:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_52(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_1_53:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_53(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_2_54:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_54(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_55(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_56(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_57(p);
                    break;
                case CREASE_TOGGLE_MV_58:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_58(p);
                    break;
                case CIRCLE_CHANGE_COLOR_59:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_59(p);
                    break;
                case CREASE_MAKE_AUX_60:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_60(p);
                    break;
                case OPERATION_FRAME_CREATE_61:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_61(p);
                    break;
                case VORONOI_CREATE_62:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_62(p);
                    break;
                case FLAT_FOLDABLE_CHECK_63:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_63(p);
                    break;
                case CREASE_DELETE_OVERLAPPING_64:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_64(p);
                    break;
                case CREASE_DELETE_INTERSECTING_65:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_65(p);
                    break;
                case SELECT_POLYGON_66:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_66(p);
                    break;
                case UNSELECT_POLYGON_67:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_67(p);
                    break;
                case SELECT_LINE_INTERSECTING_68:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_68(p);
                    break;
                case UNSELECT_LINE_INTERSECTING_69:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_69(p);
                    break;
                case CREASE_LENGTHEN_70:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_70(p);
                    break;
                case FOLDABLE_LINE_DRAW_71:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_71(p);
                    break;
                case UNUSED_10001:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_10001(p);
                    break;
                case UNUSED_10002:
                    es1.setCamera(creasePatternCamera);
                    es1.mReleased_A_10002(p);
                    break;
                case MODIFY_CALCULATED_SHAPE_101:         //折り上がり図操作
                    app.OZ.foldedFigure_operation_mouse_off(p);
                    break;
                case MOVE_CALCULATED_SHAPE_102:
                    app.OZ.foldedFigureCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.foldedFigureFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.foldedFigureRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));

                    app.OZ.transparentFrontCamera.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.transparentRearCamera.displayPositionMove(mouse_temp0.other_Point_position(p));

                    mouse_temp0.set(p);

                    break;
                case CHANGE_STANDARD_FACE_103: //基準面指定
                    int new_referencePlane_id;
                    int old_referencePlane_id;
                    old_referencePlane_id = app.OZ.cp_worker1.getReferencePlaneId();

                    new_referencePlane_id = app.OZ.cp_worker1.setReferencePlaneId(p);
                    System.out.println("kijyunmen_id = " + new_referencePlane_id);
                    if (app.OZ.ct_worker.face_rating != null) {//20180227追加
                        System.out.println(
                                "OZ.js.nbox.get_jyunjyo = " + app.OZ.ct_worker.nbox.getSequence(new_referencePlane_id) + " , rating = " +
                                        app.OZ.ct_worker.nbox.getWeight(app.OZ.ct_worker.nbox.getSequence(new_referencePlane_id))

                        );

                    }
                    if ((new_referencePlane_id != old_referencePlane_id) && (app.OZ.estimationStep != FoldedFigure.EstimationStep.STEP_0)) {
                        app.OZ.estimationStep = FoldedFigure.EstimationStep.STEP_1;
                    }
                    break;
            }

            repaint();
        }

        app.mouseDraggedValid = false;
        app.mouseReleasedValid = false;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (mouseWheelMovesCreasePattern) {
            //	ホイールでundo,redo
            if ((e.isShiftDown()) || (app.i_mouse_right_button_on)) {
                app.i_mouse_undo_redo_mode = true;
                es1.unselect_all();
                app.Button_shared_operation();
                app.canvasModel.restoreFoldLineAdditionalInputMode();
                if (e.getWheelRotation() == -1) {
                    app.setTitle(es1.redo());
                    repaint();
                } else {
                    app.setTitle(es1.undo());
                    repaint();
                }
            }

            //	ホイールで拡大縮小
            if ((!e.isShiftDown()) && (!app.i_mouse_right_button_on)) {

                Point p = new Point(app.e2p(e));
                App.MouseWheelTarget target = app.pointInCreasePatternOrFoldedFigure(p);
                if (target == App.MouseWheelTarget.CREASE_PATTERN_0) {
                    if (e.getWheelRotation() == -1) {
                        app.creasePatternCameraModel.zoomIn();
                    } else {
                        app.creasePatternCameraModel.zoomOut();
                    }
                } else {
                    if (e.getWheelRotation() == -1) {
                        app.foldedFigureModel.zoomIn();
                    } else {
                        app.foldedFigureModel.zoomOut();
                    }
                }

                app.mouse_object_position(app.p_mouse_TV_position);
                repaint();
            }
        }
    }

    // -----------------------------------mmmmmmmmmmmmmm-------
    void writeImageFile(String fname, App app) {//i=1　png, 2=jpg
        if (fname != null) {
            String formatName;

            if (fname.endsWith("svg")) {
                Memo memo1;
                memo1 = app.mainDrawingWorker.getMemo_for_svg_export_with_camera(displayComments, displayCpLines, displayAuxLines, displayLiveAuxLines, lineWidth, lineStyle, auxLineWidth, dim.width, dim.height, displayMarkings);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ,展開図動かし中心の十字の目印の表示

                Memo memo2 = new Memo();

                //各折り上がりのmemo
                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                    OZi = app.foldedFigures.get(i_oz);

                    memo2.addMemo(OZi.getMemo_for_svg_export());
                }

                app.memoAndName2File(FileFormatConverter.orihime2svg(memo1, memo2), fname);
                return;
            } else if (fname.endsWith("png")) {
                formatName = "png";
            } else if (fname.endsWith("jpg")) {
                formatName = "jpg";
            } else {
                fname = fname + ".png";
                formatName = "png";
            }

            dim = getSize();

            //	ファイル保存

            try {
                if (app.flg61) { //枠設定時の枠内のみ書き出し 20180524
                    int xMin = (int) app.mainDrawingWorker.operationFrameBox.getXMin();
                    int xMax = (int) app.mainDrawingWorker.operationFrameBox.getXMax();
                    int yMin = (int) app.mainDrawingWorker.operationFrameBox.getYMin();
                    int yMax = (int) app.mainDrawingWorker.operationFrameBox.getYMax();

                    ImageIO.write(offscreen.getSubimage(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1), formatName, new File(fname));

                } else {//Full export without frame
                    System.out.println("2018-529_");
                    ImageIO.write(offscreen.getSubimage(0, 0, dim.width, dim.height), formatName, new File(fname));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("終わりました");
        }
    }

    public void setData(CanvasModel canvasModel) {
        displayPointSpotlight = canvasModel.getDisplayPointSpotlight();
        displayPointOffset = canvasModel.getDisplayPointOffset();
        displayGridInputAssist = canvasModel.getDisplayGridInputAssist();
        displayComments = canvasModel.getDisplayComments();
        displayCpLines = canvasModel.getDisplayCpLines();
        displayAuxLines = canvasModel.getDisplayAuxLines();
        displayLiveAuxLines = canvasModel.getDisplayLiveAuxLines();

        displayMarkings = canvasModel.getDisplayMarkings();
        displayCreasePatternOnTop = canvasModel.getDisplayCreasePatternOnTop();
        displayFoldingProgress = canvasModel.getDisplayFoldingProgress();

        lineStyle = canvasModel.getLineStyle();
        antiAlias = canvasModel.getAntiAlias();

        mouseWheelMovesCreasePattern = canvasModel.getMouseWheelMovesCreasePattern();

        lineWidth = canvasModel.getCalculatedLineWidth();
        auxLineWidth = canvasModel.getCalculatedAuxLineWidth();

        mouseMode = canvasModel.getMouseMode();

        repaint();
    }
}
