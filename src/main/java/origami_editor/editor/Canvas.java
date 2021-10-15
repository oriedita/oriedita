package origami_editor.editor;

import origami_editor.editor.databinding.ApplicationModel;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.drawing_worker.BaseMouseHandler;
import origami_editor.editor.drawing_worker.DrawingWorker;
import origami_editor.editor.drawing_worker.FoldLineAdditionalInputMode;
import origami_editor.editor.drawing_worker.MouseModeHandler;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami.crease_pattern.element.Point;
import origami_editor.editor.export.Svg;
import origami_editor.tools.Camera;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
    public Point mouse_temp0 = new Point();//マウスの動作対応時に、一時的に使うTen

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
    private int intLineWidth;
    private int pointSize;

    Map<MouseMode, MouseModeHandler> mouseModeHandlers = new HashMap<>();

    boolean i_mouse_undo_redo_mode = false;//1 for undo and redo mode with mouse

    public Canvas(App app0) {
        app = app0;

        onResize();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        es1 = app0.mainDrawingWorker;

        System.out.println(" dim 001 :" + dim.width + " , " + dim.height);//多分削除可能

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }
        });
    }

    public void onResize() {
        dim = getSize();
        if (dim.width == 0) {
            // Set a default size if the canvas is not yet loaded.
            dim = new Dimension(2000, 1000);
        }
        offscreen = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_BGR);
        bufferGraphics = offscreen.createGraphics();

        repaint();
    }

    public void addMouseModeHandler(MouseModeHandler handler) {
        mouseModeHandlers.put(handler.getMouseMode(), handler);
    }

    public void addMouseModeHandler(Class<? extends BaseMouseHandler> handler) {
        try {
            BaseMouseHandler instance = handler.getDeclaredConstructor().newInstance();
            instance.setDrawingWorker(app.mainDrawingWorker);
            mouseModeHandlers.put(instance.getMouseMode(), instance);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
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


        // バッファー画面のクリア
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

            if (app.isTaskRunning()) {
                bufferGraphics.setColor(Color.red);

                bufferGraphics.drawString("Under Calculation. If you want to cancel calculation, uncheck [check A + MV]on right side and press the brake button (bicycle brake icon) on lower side.", 10, 69); //この表示内容はvoid kekka_syoriで決められる。
                bufferGraphics.drawString("計算中。　なお、計算を取り消し通常状態に戻りたいなら、右辺の[check A+MV]のチェックをはずし、ブレーキボタン（下辺の、自転車のブレーキのアイコン）を押す。 ", 10, 83); //この表示内容はvoid kekka_syoriで決められる。
            }

            app.bulletinBoard.draw(bufferGraphics);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        }


        //折り上がりの各種お絵かき
        for (int i = 1; i <= app.foldedFigures.size() - 1; i++) {
            boolean selected = app.foldedFigureIndex == i;

            OZi = app.foldedFigures.get(i);
            OZi.foldUp_draw(bufferGraphics, displayMarkings, selected);
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
            writeImageFile(app.exportFile, app);
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

        es1.setCamera(creasePatternCamera);

        if (mouseModeHandlers.containsKey(mouseMode)) {
            mouseModeHandlers.get(mouseMode).mouseMoved(p);
        }

        repaint();
    }

    //マウス操作(ボタンを押したとき)を行う関数----------------------------------------------------
    public void mousePressed(MouseEvent e) {
        Point p = new Point(app.e2p(e));

        app.mouseDraggedValid = true;
        app.mouseReleasedValid = true;

        btn = e.getButton();

        if (e.isMetaDown()) {
            btn = MouseEvent.BUTTON2;
        }

        //---------ボタンの種類による動作変更-----------------------------------------
        switch (btn) {
            case MouseEvent.BUTTON1:
                if (e.getClickCount() == 3 && mouseMode == MouseMode.CREASE_SELECT_19 && app.canvasModel.isCkbox_add_frame_SelectAnd3click_isSelected()) {
                    System.out.println("3_Click");//("トリプルクリック"

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
                    //線分削除モード。
                    es1.setCamera(creasePatternCamera);
                    mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3).mousePressed(p);

                    app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.BOTH_4);
                }
                repaint();

                return;
        }

        es1.setCamera(creasePatternCamera);

        if (mouseModeHandlers.containsKey(mouseMode)) {
            mouseModeHandlers.get(mouseMode).mousePressed(p);
        }

        repaint();
    }

    //マウス操作(ドラッグしたとき)を行う関数---------- System.out.println("A");------------------------------------------
    public void mouseDragged(MouseEvent e) {

        if (app.mouseDraggedValid) {
            Point p = new Point(app.e2p(e));
            app.mouse_object_position(p);

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
                        if (i_mouse_undo_redo_mode) {
                            return;
                        }//undo,redoモード。
                        es1.setCamera(creasePatternCamera);
                        mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3).mouseDragged(p);
                    }
                    repaint();
                    return;
            }

            es1.setCamera(creasePatternCamera);

            if (mouseModeHandlers.containsKey(mouseMode)) {
                mouseModeHandlers.get(mouseMode).mouseDragged(p);
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
                        //if(i_mouse_undo_redo_mode==1){i_mouse_undo_redo_mode=0;mainDrawingWorker.unselect_all();Button_kyoutuu_sagyou();mainDrawingWorker.modosi_i_orisen_hojyosen();return;}
                        if (i_mouse_undo_redo_mode) {
                            i_mouse_undo_redo_mode = false;
                            return;
                        } //undo,redoモード。
                        es1.setCamera(creasePatternCamera);
                        mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3).mouseReleased(p);
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

            es1.setCamera(creasePatternCamera);
            if (mouseModeHandlers.containsKey(mouseMode)) {
                mouseModeHandlers.get(mouseMode).mouseReleased(p);
            }

            repaint();
        }

        app.mouseDraggedValid = false;
        app.mouseReleasedValid = false;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (mouseWheelMovesCreasePattern) {
            //	ホイールでundo,redo
            if ((e.isShiftDown())) {
                i_mouse_undo_redo_mode = true;
                es1.unselect_all();
                app.Button_shared_operation();
                app.canvasModel.restoreFoldLineAdditionalInputMode();
                if (e.getWheelRotation() < 0) {
                    es1.redo();
                    repaint();
                } else {
                    es1.undo();
                    repaint();
                }
            } else {
                Point p = new Point(app.e2p(e));
                App.MouseWheelTarget target = app.pointInCreasePatternOrFoldedFigure(p);
                if (target == App.MouseWheelTarget.CREASE_PATTERN_0) {
                    if (e.getWheelRotation() < 0) {
                        app.creasePatternCameraModel.zoomIn();
                    } else {
                        app.creasePatternCameraModel.zoomOut();
                    }
                } else {
                    if (e.getWheelRotation() < 0) {
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
    void writeImageFile(File file, App app) {//i=1　png, 2=jpg
        if (file != null) {
            String fname = file.getName();

            String formatName;

            if (fname.endsWith("svg")) {
                Svg.exportFile(app.mainDrawingWorker.foldLineSet, creasePatternCamera, displayCpLines, lineWidth, intLineWidth, lineStyle, pointSize, app.foldedFigures, file);
                return;
            } else if (fname.endsWith("png")) {
                formatName = "png";
            } else if (fname.endsWith("jpg")) {
                formatName = "jpg";
            } else {
                file = new File(fname + ".png");
                formatName = "png";
            }

            //	ファイル保存

            try {
                if (app.flg61) { //枠設定時の枠内のみ書き出し 20180524
                    int xMin = (int) app.mainDrawingWorker.operationFrameBox.getXMin();
                    int xMax = (int) app.mainDrawingWorker.operationFrameBox.getXMax();
                    int yMin = (int) app.mainDrawingWorker.operationFrameBox.getYMin();
                    int yMax = (int) app.mainDrawingWorker.operationFrameBox.getYMax();

                    ImageIO.write(offscreen.getSubimage(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1), formatName, file);

                } else {//Full export without frame
                    System.out.println("2018-529_");
                    ImageIO.write(offscreen.getSubimage(0, 0, dim.width, dim.height), formatName, file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("終わりました");
        }
    }

    public void setData(ApplicationModel applicationModel) {
        displayPointSpotlight = applicationModel.getDisplayPointSpotlight();
        displayPointOffset = applicationModel.getDisplayPointOffset();
        displayGridInputAssist = applicationModel.getDisplayGridInputAssist();
        displayComments = applicationModel.getDisplayComments();
        displayCpLines = applicationModel.getDisplayCpLines();
        displayAuxLines = applicationModel.getDisplayAuxLines();
        displayLiveAuxLines = applicationModel.getDisplayLiveAuxLines();

        displayMarkings = applicationModel.getDisplayMarkings();
        displayCreasePatternOnTop = applicationModel.getDisplayCreasePatternOnTop();
        displayFoldingProgress = applicationModel.getDisplayFoldingProgress();

        lineStyle = applicationModel.getLineStyle();
        antiAlias = applicationModel.getAntiAlias();

        mouseWheelMovesCreasePattern = applicationModel.getMouseWheelMovesCreasePattern();

        intLineWidth = applicationModel.getLineWidth();
        lineWidth = applicationModel.determineCalculatedLineWidth();
        auxLineWidth = applicationModel.determineCalculatedAuxLineWidth();

        pointSize = applicationModel.getPointSize();

        repaint();
    }

    public void setData(CanvasModel canvasModel) {
        mouseMode = canvasModel.getMouseMode();

        repaint();
    }
}
