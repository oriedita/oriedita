package origami_editor.editor;

import origami_editor.editor.drawing_worker.DrawingWorker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.graphic2d.linesegment.LineSegment;
import origami_editor.graphic2d.point.Point;
import origami_editor.record.memo.Memo;

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

    // Applet width and height
    Dimension dim;
    private boolean antiAlias;
    private boolean mouseWheelMovesCreasePattern;

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
        app.mainDrawingWorker.setCamera(app.camera_of_orisen_input_diagram);

        FoldedFigure OZi;
        for (int i = 1; i <= app.foldedFigures.size() - 1; i++) {
            OZi = app.foldedFigures.get(i);
            OZi.cp_worker1.setCamera(app.camera_of_orisen_input_diagram);
        }

//VVVVVVVVVVVVVVV以下のts2へのカメラセットはOriagari_zuのoekakiで実施しているので以下の5行はなくてもいいはず　20180225
        app.OZ.cp_worker2.setCamera(app.OZ.camera_of_foldedFigure);
        app.OZ.cp_worker2.setCam_front(app.OZ.camera_of_foldedFigure_front);
        app.OZ.cp_worker2.setCam_rear(app.OZ.camera_of_foldedFigure_rear);
        app.OZ.cp_worker2.setCam_transparent_front(app.OZ.camera_of_transparent_front);
        app.OZ.cp_worker2.setCam_transparent_rear(app.OZ.camera_of_transparent_rear);
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

        //System.out.println("paint　+++++++++++++++++++++　背景表示");
        //背景表示
        if ((app.img_background != null) && app.displayBackground) {
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
                //	ts1.setCamera(camera_of_orisen_nyuuryokuzu);
                app.OZ.cp_worker1.drawing_referencePlane_with_camera(bufferGraphics);//ts1が折り畳みを行う際の基準面を表示するのに使う。
            }
        }

        double d_width = app.camera_of_orisen_input_diagram.getCameraZoomX() * app.mainDrawingWorker.getSelectionDistance();
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

            app.w_image_running = false;
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
        if (app.lockBackground) {
            app.h_cam.setCamera(app.camera_of_orisen_input_diagram);
            app.h_cam.h3_and_h4_calculation();
            app.h_cam.parameter_calculation();
        }

        AffineTransform at = new AffineTransform();
        at.rotate(app.h_cam.getAngle() * Math.PI / 180.0, app.h_cam.get_cx(), app.h_cam.get_cy());
        g2h.setTransform(at);


        g2h.drawImage(imgh, app.h_cam.get_x0(), app.h_cam.get_y0(), app.h_cam.get_x1(), app.h_cam.get_y1(), this);

        //g2h.drawImage(imgh,kaisi_x,kaisi_y,this);//hx0,hy0,は描画開始位置

        at.rotate(-app.h_cam.getAngle() * Math.PI / 180.0, app.h_cam.get_cx(), app.h_cam.get_cy());
        g2h.setTransform(at);

    }

    // マウス操作(マウスが動いた時)を行う関数----------------------------------------------------
    public void mouseMoved(MouseEvent e) {
        //何もしない
        //  final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();//これは多分J2SE 5.0「Tiger」以降で作動するコード

        Point p = new Point(app.e2p(e));
        app.mouse_object_position(p);
        if (app.mouseMode == MouseMode.UNUSED_0) {
        } else if (app.mouseMode == MouseMode.DRAW_CREASE_FREE_1) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_01(p);
        }   //1 線分入力モード（フリー）
        //else if(app.mouseMode==2)  {		}						       //2 展開図移動。
        //else if(app.mouseMode==3)  { mainDrawingWorker.setCamera(camera_of_orisen_nyuuryokuzu);mainDrawingWorker.mMoved_A_03(p);}//線分削除モード。
        //else if(app.mouseMode==4)  { mainDrawingWorker.setCamera(camera_of_orisen_nyuuryokuzu);mainDrawingWorker.mMoved_A_04(p);}//senbun_henkan 黒赤青
        else if (app.mouseMode == MouseMode.LENGTHEN_CREASE_5) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_05(p);
        }//線分延長モード。
        //else if(app.mouseMode==6)  { mainDrawingWorker.setCamera(camera_of_orisen_nyuuryokuzu);mainDrawingWorker.mMoved_A_06(p);}//2点から等距離線分モード。
        else if (app.mouseMode == MouseMode.SQUARE_BISECTOR_7) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_07(p);
        }//角二等分線モード。
        else if (app.mouseMode == MouseMode.INWARD_8) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_08(p);
        }//内心モード。
        else if (app.mouseMode == MouseMode.PERPENDICULAR_DRAW_9) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_09(p);
        }//垂線おろしモード。
        else if (app.mouseMode == MouseMode.SYMMETRIC_DRAW_10) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_10(p);
        }//折り返しモード。
        else if (app.mouseMode == MouseMode.DRAW_CREASE_RESTRICTED_11) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_11(p);
        }//線分入力モード。(制限)
        else if (app.mouseMode == MouseMode.DRAW_CREASE_SYMMETRIC_12) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_12(p);
        }//鏡映モード。
        //else if(app.mouseMode==13) { mainDrawingWorker.setCamera(camera_of_orisen_nyuuryokuzu);mainDrawingWorker.mMoved_A_13(p);}//角度系モード（１番目）。//線分指定、交点まで
        //else if(app.mouseMode==14) { mainDrawingWorker.setCamera(camera_of_orisen_nyuuryokuzu);mainDrawingWorker.mMoved_A_14(p);}//点追加モード。
        //else if(app.mouseMode==15) { mainDrawingWorker.setCamera(camera_of_orisen_nyuuryokuzu);mainDrawingWorker.mMoved_A_15(p);}//点削除モード。
        else if (app.mouseMode == MouseMode.ANGLE_SYSTEM_16) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_16(p);
        }//角度系モード（４番目）。2点指定し、線分まで
        else if (app.mouseMode == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_17(p);
        }//角度系モード（２番目）。//2点指定、交点まで
        else if (app.mouseMode == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_18(p);
        }//角度系モード（５番目）。2点指定、自由末端
        else if (app.mouseMode == MouseMode.CREASE_MOVE_21) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_21(p);
        }//move　に使う
        else if (app.mouseMode == MouseMode.CREASE_COPY_22) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_22(p);
        }//copy_paste　に使う
        else if (app.mouseMode == MouseMode.LINE_SEGMENT_DIVISION_27) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_27(p);
        }//線分分割入力　に使う
        else if (app.mouseMode == MouseMode.LINE_SEGMENT_RATIO_SET_28) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_28(p);
        }//線分内分入力　に使う
        else if (app.mouseMode == MouseMode.POLYGON_SET_NO_CORNERS_29) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_29(p);
        }//正多角形入力　に使う
        else if (app.mouseMode == MouseMode.CREASE_MOVE_4P_31) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_31(p);
        }//move 2p2p　に使う
        else if (app.mouseMode == MouseMode.CREASE_COPY_4P_32) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_32(p);
        }//copy 2p2p　　に使う
        else if (app.mouseMode == MouseMode.FISH_BONE_DRAW_33) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_33(p);
        }//魚の骨　に使う
        else if (app.mouseMode == MouseMode.CREASE_MAKE_MV_34) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_34(p);
        }//準備としてだけ使う線分に重複している折線を順に山谷にするの　に使う
        else if (app.mouseMode == MouseMode.DOUBLE_SYMMETRIC_DRAW_35) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_35(p);
        }//複折り返し　入力した線分に接触している折線を折り返し　に使う
        else if (app.mouseMode == MouseMode.CREASES_ALTERNATE_MV_36) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_36(p);
        }//準備としてだけ使う線分にX交差している折線を順に山谷にするの　に使う
        else if (app.mouseMode == MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_37) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_37(p);
        }//角度系モード（３番目）。角度規格化線分入力モード。角度規格化折線入力　に使う
        else if (app.mouseMode == MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_38(p);
        }//折り畳み可能線追加
        else if (app.mouseMode == MouseMode.FOLDABLE_LINE_INPUT_39) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_39(p);
        }//折り畳み可能線+格子点系入力
        else if (app.mouseMode == MouseMode.PARALLEL_DRAW_40) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_40(p);
        }//平行線入力
        else if (app.mouseMode == MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_52(p);
        }//連続折り返しモード　に使う
        else if (app.mouseMode == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_53(p);
        }//長さ測定１　に使う
        else if (app.mouseMode == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_54(p);
        }//長さ測定２　に使う
        else if (app.mouseMode == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_55(p);
        }//角度測定１　に使う
        else if (app.mouseMode == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_56(p);
        }//角度測定２　に使う
        else if (app.mouseMode == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_57(p);
        }//角度測定３　に使う
        else if (app.mouseMode == MouseMode.OPERATION_FRAME_CREATE_61) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_61(p);
        }//長方形内選択（paintの選択に似せた選択機能）に使う
        else if (app.mouseMode == MouseMode.VORONOI_CREATE_62) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_62(p);
        }//ボロノイ図　に使う
        else if (app.mouseMode == MouseMode.FLAT_FOLDABLE_CHECK_63) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_63(p);
        }//外周部折り畳みチェックに使う
        else if (app.mouseMode == MouseMode.CREASE_DELETE_OVERLAPPING_64) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_64(p);
        }//線内削除　に使う
        else if (app.mouseMode == MouseMode.CREASE_DELETE_INTERSECTING_65) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_65(p);
        }//lX　直線で折線削除に使う
        else if (app.mouseMode == MouseMode.SELECT_POLYGON_66) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_66(p);
        }//選択＿多角形　に使う
        else if (app.mouseMode == MouseMode.UNSELECT_POLYGON_67) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_67(p);
        }//非選択＿多角形　に使う
        else if (app.mouseMode == MouseMode.SELECT_LINE_INTERSECTING_68) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_68(p);
        }//選択＿ｌX　に使う
        else if (app.mouseMode == MouseMode.UNSELECT_LINE_INTERSECTING_69) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_69(p);
        }//非選択＿ｌX　　に使う
        else if (app.mouseMode == MouseMode.CREASE_LENGTHEN_70) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_70(p);
        }//線分延長モード(延長する元の折線のクリックだけで実行されるタイプ)　に使う
        else if (app.mouseMode == MouseMode.FOLDABLE_LINE_DRAW_71) {
            es1.setCamera(app.camera_of_orisen_input_diagram);
            es1.mMoved_A_71(p);
        }//複数の線分延長モード　に使う
        else {
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
                    if (app.mouseMode == MouseMode.CREASE_SELECT_19) {
                        if (app.ckbox_add_frame_SelectAnd3click_isSelected) {
                            switch (app.selectionOperationMode) {
                                case MOVE_1:
                                    app.mouseMode = MouseMode.CREASE_MOVE_21;
                                    break;
                                case MOVE4P_2:
                                    app.mouseMode = MouseMode.CREASE_MOVE_4P_31;
                                    break;
                                case COPY_3:
                                    app.mouseMode = MouseMode.CREASE_COPY_22;
                                    break;
                                case COPY4P_4:
                                    app.mouseMode = MouseMode.CREASE_COPY_4P_32;
                                    break;
                                case MIRROR_5:
                                    app.mouseMode = MouseMode.DRAW_CREASE_SYMMETRIC_12;
                                    break;
                            }

                            System.out.println("mouseMode=" + app.mouseMode);
                        }
                    }
                }
                break;
            case MouseEvent.BUTTON2:
                System.out.println("中ボタンクリック");

                app.pointInCpOrFoldedFigure(p);

                System.out.println("i_cp_or_oriagari = " + app.i_cp_or_oriagari);

                switch (app.i_cp_or_oriagari) {
                    case CREASEPATTERN_0: // 展開図移動。
                        app.camera_of_orisen_input_diagram.camera_ichi_sitei_from_TV(p);
                        break;
                    case FOLDED_FRONT_1:
                        app.OZ.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(p);
                        break;
                    case FOLDED_BACK_2:
                        app.OZ.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(p);
                        break;
                    case TRANSPARENT_FRONT_3:
                        app.OZ.camera_of_transparent_front.camera_ichi_sitei_from_TV(p);
                        break;
                    case TRANSPARENT_BACK_4:
                        app.OZ.camera_of_transparent_rear.camera_ichi_sitei_from_TV(p);
                        break;
                }

                mouse_temp0.set(p);
                repaint();
                return;
            case MouseEvent.BUTTON3: //右ボタンクリック
                if (app.mouseMode == MouseMode.VORONOI_CREATE_62) {//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                } else {
                    app.i_mouse_right_button_on = true;

                    //線分削除モード。
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mPressed_A_03(p);

                    app.foldLineAdditionalInputMode = DrawingWorker.FoldLineAdditionalInputMode.BOTH_4;//= 0 is polygonal line input = 1 is auxiliary line input mode, 4 is for both
                    es1.setFoldLineAdditional(app.foldLineAdditionalInputMode);

                }
                repaint();

                return;
        }
        //-----------------------------System.out.println("a");----------------------

        //}  //20201010　コメントアウト


        switch (app.mouseMode) {
            case UNUSED_0:
                break;
            case DRAW_CREASE_FREE_1:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_01(p);
                break;
            case MOVE_CREASE_PATTERN_2:                                        //2 展開図移動。
                app.camera_of_orisen_input_diagram.camera_ichi_sitei_from_TV(p);
                mouse_temp0.set(p);
                break;
            case LINE_SEGMENT_DELETE_3:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_03(p);
                break;
            case CHANGE_CREASE_TYPE_4:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_04(p);
                break;
            case LENGTHEN_CREASE_5:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_05(p);
                break;
            case UNUSED_6:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_06(p);
                break;
            case SQUARE_BISECTOR_7:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_07(p);
                break;
            case INWARD_8:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_08(p);
                break;
            case PERPENDICULAR_DRAW_9:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_09(p);
                break;
            case SYMMETRIC_DRAW_10:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_10(p);
                break;
            case DRAW_CREASE_RESTRICTED_11:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_11(p);
                break;
            case DRAW_CREASE_SYMMETRIC_12:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_12(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_13:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_13(p);
                break;
            case DRAW_POINT_14:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_14(p);
                break;
            case DELETE_POINT_15:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_15(p);
                break;
            case ANGLE_SYSTEM_16:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_16(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_2_17:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_17(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_18(p);
                break;
            case CREASE_SELECT_19:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_19(p);
                break;
            case CREASE_UNSELECT_20:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_20(p);
                break;
            case CREASE_MOVE_21:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_21(p);
                break;
            case CREASE_COPY_22:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_22(p);
                break;
            case CREASE_MAKE_MOUNTAIN_23:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_23(p);
                break;
            case CREASE_MAKE_VALLEY_24:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_24(p);
                break;
            case CREASE_MAKE_EDGE_25:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_25(p);
                break;
            case BACKGROUND_CHANGE_POSITION_26:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_26(p);
                break;
            case LINE_SEGMENT_DIVISION_27:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_27(p);
                break;
            case LINE_SEGMENT_RATIO_SET_28:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_28(p);
                break;
            case POLYGON_SET_NO_CORNERS_29:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_29(p);
                break;
            case CREASE_ADVANCE_TYPE_30:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_30(p);
                break;
            case CREASE_MOVE_4P_31:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_31(p);
                break;
            case CREASE_COPY_4P_32:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_32(p);
                break;
            case FISH_BONE_DRAW_33:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_33(p);
                break;
            case CREASE_MAKE_MV_34:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_34(p);
                break;
            case DOUBLE_SYMMETRIC_DRAW_35:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_35(p);
                break;
            case CREASES_ALTERNATE_MV_36:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_36(p);
                break;
            case DRAW_CREASE_ANGLE_RESTRICTED_3_37:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_37(p);
                break;
            case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_38(p);
                break;
            case FOLDABLE_LINE_INPUT_39:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_39(p);
                break;
            case PARALLEL_DRAW_40:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_40(p);
                break;
            case VERTEX_DELETE_ON_CREASE_41:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_41(p);
                break;
            case CIRCLE_DRAW_42:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_42(p);
                break;
            case CIRCLE_DRAW_THREE_POINT_43:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_43(p);
                break;
            case CIRCLE_DRAW_SEPARATE_44:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_44(p);
                break;
            case CIRCLE_DRAW_TANGENT_LINE_45:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_45(p);
                break;
            case CIRCLE_DRAW_INVERTED_46:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_46(p);
                break;
            case CIRCLE_DRAW_FREE_47:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_47(p);
                break;
            case CIRCLE_DRAW_CONCENTRIC_48:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_48(p);
                break;
            case CIRCLE_DRAW_CONCENTRIC_SELECT_49:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_49(p);
                break;
            case CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_50(p);
                break;
            case PARALLEL_DRAW_WIDTH_51:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_51(p);
                break;
            case CONTINUOUS_SYMMETRIC_DRAW_52:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_52(p);
                break;
            case DISPLAY_LENGTH_BETWEEN_POINTS_1_53:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_53(p);
                break;
            case DISPLAY_LENGTH_BETWEEN_POINTS_2_54:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_54(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_55(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_56(p);
                break;
            case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_57(p);
                break;
            case CREASE_TOGGLE_MV_58:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_58(p);
                break;
            case CIRCLE_CHANGE_COLOR_59:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_59(p);
                break;
            case CREASE_MAKE_AUX_60:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_60(p);
                break;
            case OPERATION_FRAME_CREATE_61:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_61(p);
                break;
            case VORONOI_CREATE_62:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_62(p);
                break;
            case FLAT_FOLDABLE_CHECK_63:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_63(p);
                break;
            case CREASE_DELETE_OVERLAPPING_64:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_64(p);
                break;
            case CREASE_DELETE_INTERSECTING_65:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_65(p);
                break;
            case SELECT_POLYGON_66:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_66(p);
                break;
            case UNSELECT_POLYGON_67:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_67(p);
                break;
            case SELECT_LINE_INTERSECTING_68:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_68(p);
                break;
            case UNSELECT_LINE_INTERSECTING_69:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_69(p);
                break;
            case CREASE_LENGTHEN_70:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_70(p);
                break;
            case FOLDABLE_LINE_DRAW_71:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_71(p);
                break;
            case UNUSED_10001:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_10001(p);
                break;
            case UNUSED_10002:
                es1.setCamera(app.camera_of_orisen_input_diagram);
                es1.mPressed_A_10002(p);
                break;
            case MODIFY_CALCULATED_SHAPE_101:         //折り上がり図操作
                app.OZ.foldedFigure_operation_mouse_on(p);
                break;
            case MOVE_CALCULATED_SHAPE_102: //折り上がり図移動
                app.OZ.camera_of_foldedFigure.camera_ichi_sitei_from_TV(p);
                app.OZ.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(p);
                app.OZ.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(p);

                app.OZ.camera_of_transparent_front.camera_ichi_sitei_from_TV(p);
                app.OZ.camera_of_transparent_rear.camera_ichi_sitei_from_TV(p);

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
                        case CREASEPATTERN_0: // 展開図移動。
                            app.camera_of_orisen_input_diagram.displayPositionMove(mouse_temp0.other_Point_position(p));
                            es1.setCamera(app.camera_of_orisen_input_diagram);
                            break;
                        case FOLDED_FRONT_1:
                            app.OZ.camera_of_foldedFigure_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case FOLDED_BACK_2:
                            app.OZ.camera_of_foldedFigure_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_FRONT_3:
                            app.OZ.camera_of_transparent_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_BACK_4:
                            app.OZ.camera_of_transparent_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                    }

                    mouse_temp0.set(p);
                    repaint();
                    return;

                case MouseEvent.BUTTON3:
                    if (app.mouseMode == MouseMode.VORONOI_CREATE_62) {//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                    } else {
                        if (app.i_mouse_undo_redo_mode) {
                            return;
                        }//undo,redoモード。
                        es1.setCamera(app.camera_of_orisen_input_diagram);
                        es1.mDragged_A_03(p);//線分削除モード。
                    }
                    repaint();
                    return;
            }
            //}  //20201010　コメントアウト


            switch (app.mouseMode) {
                case UNUSED_0:
                    break;
                case DRAW_CREASE_FREE_1:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_01(p);
                    break;
                case MOVE_CREASE_PATTERN_2:
                    app.camera_of_orisen_input_diagram.displayPositionMove(mouse_temp0.other_Point_position(p));
                    es1.setCamera(app.camera_of_orisen_input_diagram);

//20180225追加
                    FoldedFigure OZi;
                    for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                        OZi = app.foldedFigures.get(i_oz);

                        OZi.camera_of_foldedFigure.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_foldedFigure_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_foldedFigure_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_transparent_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_transparent_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                    }
//20180225追加　ここまで

                    mouse_temp0.set(p);
                    break;
                case LINE_SEGMENT_DELETE_3:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_03(p);
                    break;
                case CHANGE_CREASE_TYPE_4:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_04(p);
                    break;
                case LENGTHEN_CREASE_5:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_05(p);
                    break;
                case UNUSED_6:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_06(p);
                    break;
                case SQUARE_BISECTOR_7:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_07(p);
                    break;
                case INWARD_8:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_08(p);
                    break;
                case PERPENDICULAR_DRAW_9:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_09(p);
                    break;
                case SYMMETRIC_DRAW_10:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_10(p);
                    break;
                case DRAW_CREASE_RESTRICTED_11:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_11(p);
                    break;
                case DRAW_CREASE_SYMMETRIC_12:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_12(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_13:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_13(p);
                    break;
                case DRAW_POINT_14:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_14(p);
                    break;
                case DELETE_POINT_15:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_15(p);
                    break;
                case ANGLE_SYSTEM_16:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_16(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_2_17:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_17(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_18(p);
                    break;
                case CREASE_SELECT_19:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_19(p);
                    break;
                case CREASE_UNSELECT_20:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_20(p);
                    break;
                case CREASE_MOVE_21:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_21(p);
                    break;
                case CREASE_COPY_22:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_22(p);
                    break;
                case CREASE_MAKE_MOUNTAIN_23:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_23(p);
                    break;
                case CREASE_MAKE_VALLEY_24:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_24(p);
                    break;
                case CREASE_MAKE_EDGE_25:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_25(p);
                    break;
                case BACKGROUND_CHANGE_POSITION_26:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_26(p);
                    break;
                case LINE_SEGMENT_DIVISION_27:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_27(p);
                    break;
                case LINE_SEGMENT_RATIO_SET_28:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_28(p);
                    break;
                case POLYGON_SET_NO_CORNERS_29:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_29(p);
                    break;
                case CREASE_ADVANCE_TYPE_30:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_30(p);
                    break;
                case CREASE_MOVE_4P_31:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_31(p);
                    break;
                case CREASE_COPY_4P_32:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_32(p);
                    break;
                case FISH_BONE_DRAW_33:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_33(p);
                    break;
                case CREASE_MAKE_MV_34:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_34(p);
                    break;
                case DOUBLE_SYMMETRIC_DRAW_35:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_35(p);
                    break;
                case CREASES_ALTERNATE_MV_36:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_36(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_37:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_37(p);
                    break;
                case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_38(p);
                    break;
                case FOLDABLE_LINE_INPUT_39:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_39(p);
                    break;
                case PARALLEL_DRAW_40:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_40(p);
                    break;
                case VERTEX_DELETE_ON_CREASE_41:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_41(p);
                    break;
                case CIRCLE_DRAW_42:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_42(p);
                    break;
                case CIRCLE_DRAW_THREE_POINT_43:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_43(p);
                    break;
                case CIRCLE_DRAW_SEPARATE_44:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_44(p);
                    break;
                case CIRCLE_DRAW_TANGENT_LINE_45:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_45(p);
                    break;
                case CIRCLE_DRAW_INVERTED_46:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_46(p);
                    break;
                case CIRCLE_DRAW_FREE_47:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_47(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_48:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_48(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_SELECT_49:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_49(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_50(p);
                    break;
                case PARALLEL_DRAW_WIDTH_51:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_51(p);
                    break;
                case CONTINUOUS_SYMMETRIC_DRAW_52:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_52(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_1_53:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_53(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_2_54:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_54(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_55(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_56(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_57(p);
                    break;
                case CREASE_TOGGLE_MV_58:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_58(p);
                    break;
                case CIRCLE_CHANGE_COLOR_59:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_59(p);
                    break;
                case CREASE_MAKE_AUX_60:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_60(p);
                    break;
                case OPERATION_FRAME_CREATE_61:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_61(p);
                    break;
                case VORONOI_CREATE_62:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_62(p);
                    break;
                case FLAT_FOLDABLE_CHECK_63:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_63(p);
                    break;
                case CREASE_DELETE_OVERLAPPING_64:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_64(p);
                    break;
                case CREASE_DELETE_INTERSECTING_65:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_65(p);
                    break;
                case SELECT_POLYGON_66:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_66(p);
                    break;
                case UNSELECT_POLYGON_67:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_67(p);
                    break;
                case SELECT_LINE_INTERSECTING_68:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_68(p);
                    break;
                case UNSELECT_LINE_INTERSECTING_69:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_69(p);
                    break;
                case CREASE_LENGTHEN_70:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_70(p);
                    break;
                case FOLDABLE_LINE_DRAW_71:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_71(p);
                    break;
                case UNUSED_10001:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_10001(p);
                    break;
                case UNUSED_10002:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mDragged_A_10002(p);
                    break;
                case MODIFY_CALCULATED_SHAPE_101:
                    app.OZ.foldedFigure_operation_mouse_drag(p);
                    break;
                case MOVE_CALCULATED_SHAPE_102:
                    app.OZ.camera_of_foldedFigure.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.camera_of_foldedFigure_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.camera_of_foldedFigure_rear.displayPositionMove(mouse_temp0.other_Point_position(p));

                    app.OZ.camera_of_transparent_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.camera_of_transparent_rear.displayPositionMove(mouse_temp0.other_Point_position(p));

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
                        case CREASEPATTERN_0:
                            app.camera_of_orisen_input_diagram.displayPositionMove(mouse_temp0.other_Point_position(p));
                            es1.setCamera(app.camera_of_orisen_input_diagram);
                            break;
                        case FOLDED_FRONT_1:
                            app.OZ.camera_of_foldedFigure_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case FOLDED_BACK_2:
                            app.OZ.camera_of_foldedFigure_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_FRONT_3:
                            app.OZ.camera_of_transparent_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                        case TRANSPARENT_BACK_4:
                            app.OZ.camera_of_transparent_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                            break;
                    }

                    mouse_temp0.set(p);
                    repaint();
                    app.mouseDraggedValid = false;
                    app.mouseReleasedValid = false;
                    return;//
                case MouseEvent.BUTTON3:
                    //System.out.println("右ボタンクリック");
                    if (app.mouseMode == MouseMode.VORONOI_CREATE_62) {
                        repaint();//ボロノイ図入力時は、入力途中のボロノイ母点が消えないように、右クリックに反応させない。20181208
                    } else {

                        app.i_mouse_right_button_on = false;

                        //if(i_mouse_undo_redo_mode==1){i_mouse_undo_redo_mode=0;mainDrawingWorker.unselect_all();Button_kyoutuu_sagyou();mainDrawingWorker.modosi_i_orisen_hojyosen();return;}
                        if (app.i_mouse_undo_redo_mode) {
                            app.i_mouse_undo_redo_mode = false;
                            return;
                        } //undo,redoモード。
                        es1.setCamera(app.camera_of_orisen_input_diagram);
                        es1.mReleased_A_03(p);
                        repaint();//なんでここにrepaintがあるか検討した方がよいかも。20181208
                        es1.modosi_foldLineAdditional();
                        app.mouseDraggedValid = false;
                        app.mouseReleasedValid = false;
                        //線分削除モード。
                    }
                    return;
            }
            //----------------------------System.out.println("a");-----------------------
            //}  //20201010　コメントアウト


            switch (app.mouseMode) {
                case UNUSED_0:
                    break;
                case DRAW_CREASE_FREE_1:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_01(p);
                    break;
                case MOVE_CREASE_PATTERN_2:
                    app.camera_of_orisen_input_diagram.displayPositionMove(mouse_temp0.other_Point_position(p));
                    es1.setCamera(app.camera_of_orisen_input_diagram);


//20180225追加
                    FoldedFigure OZi;
                    for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                        OZi = app.foldedFigures.get(i_oz);

                        //Ten t_o2tv =new Ten();
                        //t_o2tv =camera_of_orisen_nyuuryokuzu.object2TV(camera_of_orisen_nyuuryokuzu.get_camera_ichi());

//OZi.d_oriagarizu_syukusyaku_keisuu=OZi.d_oriagarizu_syukusyaku_keisuu*d_bairitu;


                        OZi.camera_of_foldedFigure.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_foldedFigure_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_foldedFigure_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_transparent_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                        OZi.camera_of_transparent_rear.displayPositionMove(mouse_temp0.other_Point_position(p));
                    }
//20180225追加　ここまで

                    mouse_temp0.set(p);
                    break;
                case LINE_SEGMENT_DELETE_3:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_03(p);
                    break;
                case CHANGE_CREASE_TYPE_4:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_04(p);
                    break;
                case LENGTHEN_CREASE_5:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_05(p);
                    break;
                case UNUSED_6:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_06(p);
                    break;
                case SQUARE_BISECTOR_7:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_07(p);
                    break;
                case INWARD_8:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_08(p);
                    break;
                case PERPENDICULAR_DRAW_9:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_09(p);
                    break;
                case SYMMETRIC_DRAW_10:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_10(p);
                    break;
                case DRAW_CREASE_RESTRICTED_11:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_11(p);
                    break;
                case DRAW_CREASE_SYMMETRIC_12:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_12(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_13:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_13(p);
                    break;
                case DRAW_POINT_14:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_14(p);
                    break;
                case DELETE_POINT_15:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_15(p);
                    break;
                case ANGLE_SYSTEM_16:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_16(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_2_17:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_17(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_18:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_18(p);
                    break;
                case CREASE_SELECT_19:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_19(p);
                    break;
                case CREASE_UNSELECT_20:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_20(p);
                    break;
                case CREASE_MOVE_21:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_21(p);
                    break;
                case CREASE_COPY_22:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_22(p);
                    break;
                case CREASE_MAKE_MOUNTAIN_23:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_23(p);
                    break;
                case CREASE_MAKE_VALLEY_24:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_24(p);
                    break;
                case CREASE_MAKE_EDGE_25:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_25(p);
                    break;
                case BACKGROUND_CHANGE_POSITION_26:
                    es1.setCamera(app.camera_of_orisen_input_diagram);

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

                        app.lockBackground = false;
                        app.backgroundLockButton.setBackground(Color.gray);

                        app.background_set(app.camera_of_orisen_input_diagram.object2TV(s_1.getA()),
                                app.camera_of_orisen_input_diagram.object2TV(s_2.getA()),
                                app.camera_of_orisen_input_diagram.object2TV(s_3.getA()),
                                app.camera_of_orisen_input_diagram.object2TV(s_4.getA()));
                    }
                    break;
                case LINE_SEGMENT_DIVISION_27:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_27(p);
                    break;
                case LINE_SEGMENT_RATIO_SET_28:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_28(p);
                    break;
                case POLYGON_SET_NO_CORNERS_29:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_29(p);
                    break;
                case CREASE_ADVANCE_TYPE_30:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_30(p);
                    break;
                case CREASE_MOVE_4P_31:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_31(p);
                    break;
                case CREASE_COPY_4P_32:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_32(p);
                    break;
                case FISH_BONE_DRAW_33:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_33(p);
                    break;
                case CREASE_MAKE_MV_34:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_34(p);
                    break;
                case DOUBLE_SYMMETRIC_DRAW_35:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_35(p);
                    break;
                case CREASES_ALTERNATE_MV_36:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_36(p);
                    break;
                case DRAW_CREASE_ANGLE_RESTRICTED_3_37:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_37(p);
                    break;
                case VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_38(p);
                    break;
                case FOLDABLE_LINE_INPUT_39:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_39(p);
                    break;
                case PARALLEL_DRAW_40:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_40(p);
                    break;
                case VERTEX_DELETE_ON_CREASE_41:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_41(p);
                    break;
                case CIRCLE_DRAW_42:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_42(p);
                    break;
                case CIRCLE_DRAW_THREE_POINT_43:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_43(p);
                    break;
                case CIRCLE_DRAW_SEPARATE_44:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_44(p);
                    break;
                case CIRCLE_DRAW_TANGENT_LINE_45:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_45(p);
                    break;
                case CIRCLE_DRAW_INVERTED_46:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_46(p);
                    break;
                case CIRCLE_DRAW_FREE_47:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_47(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_48:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_48(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_SELECT_49:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_49(p);
                    break;
                case CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_50(p);
                    break;
                case PARALLEL_DRAW_WIDTH_51:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_51(p);
                    break;
                case CONTINUOUS_SYMMETRIC_DRAW_52:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_52(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_1_53:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_53(p);
                    break;
                case DISPLAY_LENGTH_BETWEEN_POINTS_2_54:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_54(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_55(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_56(p);
                    break;
                case DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_57(p);
                    break;
                case CREASE_TOGGLE_MV_58:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_58(p);
                    break;
                case CIRCLE_CHANGE_COLOR_59:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_59(p);
                    break;
                case CREASE_MAKE_AUX_60:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_60(p);
                    break;
                case OPERATION_FRAME_CREATE_61:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_61(p);
                    break;
                case VORONOI_CREATE_62:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_62(p);
                    break;
                case FLAT_FOLDABLE_CHECK_63:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_63(p);
                    break;
                case CREASE_DELETE_OVERLAPPING_64:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_64(p);
                    break;
                case CREASE_DELETE_INTERSECTING_65:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_65(p);
                    break;
                case SELECT_POLYGON_66:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_66(p);
                    break;
                case UNSELECT_POLYGON_67:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_67(p);
                    break;
                case SELECT_LINE_INTERSECTING_68:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_68(p);
                    break;
                case UNSELECT_LINE_INTERSECTING_69:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_69(p);
                    break;
                case CREASE_LENGTHEN_70:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_70(p);
                    break;
                case FOLDABLE_LINE_DRAW_71:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_71(p);
                    break;
                case UNUSED_10001:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_10001(p);
                    break;
                case UNUSED_10002:
                    es1.setCamera(app.camera_of_orisen_input_diagram);
                    es1.mReleased_A_10002(p);
                    break;
                case MODIFY_CALCULATED_SHAPE_101:         //折り上がり図操作
                    app.OZ.foldedFigure_operation_mouse_off(p);
                    break;
                case MOVE_CALCULATED_SHAPE_102:
                    app.OZ.camera_of_foldedFigure.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.camera_of_foldedFigure_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.camera_of_foldedFigure_rear.displayPositionMove(mouse_temp0.other_Point_position(p));

                    app.OZ.camera_of_transparent_front.displayPositionMove(mouse_temp0.other_Point_position(p));
                    app.OZ.camera_of_transparent_rear.displayPositionMove(mouse_temp0.other_Point_position(p));

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
                                        app.OZ.ct_worker.nbox.getDouble(app.OZ.ct_worker.nbox.getSequence(new_referencePlane_id))

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
                es1.modosi_foldLineAdditional();
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
                app.pointInCpOrFoldedFigure(p);
                double root_root_root_2 = Math.sqrt(Math.sqrt(Math.sqrt(2.0)));
                if (app.i_cp_or_oriagari == App.MouseWheelTarget.CREASEPATTERN_0) {
                    if (e.getWheelRotation() == -1) {
                        app.scaleFactor = app.scaleFactor * root_root_root_2;//  sqrt(sqrt(2))=1.1892
                    } else {
                        app.scaleFactor = app.scaleFactor / root_root_root_2;//  sqrt(sqrt(2))=1.1892
                    }
                    app.camera_of_orisen_input_diagram.setCameraZoomX(app.scaleFactor);
                    app.camera_of_orisen_input_diagram.setCameraZoomY(app.scaleFactor);
                    app.scaleFactorTextField.setText(String.valueOf(app.scaleFactor));
                    app.scaleFactorTextField.setCaretPosition(0);
                    // ---------------------------------------------------------------------
                } else {
                    if (e.getWheelRotation() == -1) {
                        app.foldedFigureConfiguration.setScale(app.foldedFigureConfiguration.getScale() * root_root_root_2);
                    } else {
                        app.foldedFigureConfiguration.setScale(app.foldedFigureConfiguration.getScale() / root_root_root_2);
                    }

                    app.updateFoldedFigure();
                }
                // ---------------------------------------------------------------------

                app.mouse_object_position(app.p_mouse_TV_position);
                repaint();
            }
        }
    }

    // -----------------------------------mmmmmmmmmmmmmm-------
    void writeImageFile(String fname, App app) {//i=1　png, 2=jpg
        if (fname != null) {
            int i;

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
                i = 1;
            } else if (fname.endsWith("jpg")) {
                i = 2;
            } else {
                fname = fname + ".png";
                i = 1;
            }

            dim = getSize();

            //	ファイル保存

            try {
                if (app.flg61) { //枠設定時の枠内のみ書き出し 20180524
                    int xmin = (int) app.mainDrawingWorker.operationFrameBox.getXMin();
                    int xmax = (int) app.mainDrawingWorker.operationFrameBox.getXMax();
                    int ymin = (int) app.mainDrawingWorker.operationFrameBox.getYMin();
                    int ymax = (int) app.mainDrawingWorker.operationFrameBox.getYMax();

                    if (i == 1) {
                        ImageIO.write(offscreen.getSubimage(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1), "png", new File(fname));
                    }
                    if (i == 2) {
                        ImageIO.write(offscreen.getSubimage(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1), "jpg", new File(fname));
                    }

                } else {//枠無しの場合の全体書き出し
                    System.out.println("2018-529_");
                    if (i == 1) {
                        ImageIO.write(offscreen.getSubimage(app.upperLeftX, app.upperLeftY, dim.width - app.lowerRightX - app.upperLeftX, dim.height - app.lowerRightY - app.upperLeftY), "png", new File(fname));
                    }
                    if (i == 2) {
                        ImageIO.write(offscreen.getSubimage(app.upperLeftX, app.upperLeftY, dim.width - app.lowerRightX - app.upperLeftX, dim.height - app.lowerRightY - app.upperLeftY), "jpg", new File(fname));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("終わりました");
        }
    }

    public void setData(CanvasConfiguration canvasConfiguration) {
        displayPointSpotlight = canvasConfiguration.getDisplayPointSpotlight();
        displayPointOffset = canvasConfiguration.getDisplayPointOffset();
        displayGridInputAssist = canvasConfiguration.getDisplayGridInputAssist();
        displayComments = canvasConfiguration.getDisplayComments();
        displayCpLines = canvasConfiguration.getDisplayCpLines();
        displayAuxLines = canvasConfiguration.getDisplayAuxLines();
        displayLiveAuxLines = canvasConfiguration.getDisplayLiveAuxLines();

        displayMarkings = canvasConfiguration.getDisplayMarkings();
        displayCreasePatternOnTop = canvasConfiguration.getDisplayCreasePatternOnTop();
        displayFoldingProgress = canvasConfiguration.getDisplayFoldingProgress();

        lineStyle = canvasConfiguration.getLineStyle();
        antiAlias = canvasConfiguration.getAntiAlias();

        mouseWheelMovesCreasePattern = canvasConfiguration.getMouseWheelMovesCreasePattern();

        lineWidth = canvasConfiguration.getCalculatedLineWidth();
        auxLineWidth = canvasConfiguration.getCalculatedAuxLineWidth();

        repaint();
    }
}