package origami_editor.editor;

import origami.crease_pattern.OritaCalc;
import origami_editor.editor.databinding.ApplicationModel;
import origami_editor.editor.databinding.BackgroundModel;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.canvas.BaseMouseHandler;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.canvas.FoldLineAdditionalInputMode;
import origami_editor.editor.canvas.MouseModeHandler;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami.crease_pattern.element.Point;
import origami_editor.editor.export.Svg;
import origami_editor.editor.task.TaskExecutor;
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
import java.util.LinkedList;
import java.util.Map;

/**
 * Panel in the center of the main view.
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final CreasePattern_Worker es1;
    private final App app;

    MouseWheelTarget i_cp_or_oriagari = MouseWheelTarget.CREASE_PATTERN_0;//0 if the target of the mouse wheel is a cp development view, 1 if it is a folded view (front), 2 if it is a folded view (back), 3 if it is a transparent view (front), 4 if it is a transparent view (back)

    Point p_mouse_object_position = new Point();//マウスのオブジェクト座標上の位置
    Point p_mouse_TV_position = new Point();//マウスのTV座標上の位置

    Graphics bufferGraphics;
    BufferedImage offscreen;//20181205new

    Background_camera h_cam = new Background_camera();

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

        es1 = app0.mainCreasePatternWorker;

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
            instance.setDrawingWorker(app.mainCreasePatternWorker);
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
        app.mainCreasePatternWorker.setCamera(creasePatternCamera);

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
        Image backgroundImage = app.backgroundModel.getBackgroundImage();

        if ((backgroundImage != null) && app.backgroundModel.isDisplayBackground()) {
            int iw = backgroundImage.getWidth(this);//イメージの幅を取得
            int ih = backgroundImage.getHeight(this);//イメージの高さを取得

            h_cam.setBackgroundWidth(iw);
            h_cam.setBackgroundHeight(ih);

            drawBackground(g2, backgroundImage);
        }

        //格子表示

        //基準面の表示
        if (displayMarkings) {
            if (app.OZ.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                app.OZ.cp_worker1.drawing_referencePlane_with_camera(bufferGraphics);//ts1が折り畳みを行う際の基準面を表示するのに使う。
            }
        }

        double d_width = creasePatternCamera.getCameraZoomX() * app.mainCreasePatternWorker.getSelectionDistance();
        //Flashlight (dot) search range
        if (displayPointSpotlight) {
            g2.setColor(new Color(255, 240, 0, 30));
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(255, 240, 0, 230));
            g2.draw(new Ellipse2D.Double(p_mouse_TV_position.getX() - d_width, p_mouse_TV_position.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
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

            bufferGraphics.drawString(String.format("mouse= ( %.2f, %.2f )", p_mouse_object_position.getX(), p_mouse_object_position.getY()), 10, 10); //この表示内容はvoid kekka_syoriで決められる。

            bufferGraphics.drawString("L=" + app.mainCreasePatternWorker.getTotal(), 10, 25); //この表示内容はvoid kekka_syoriで決められる。

            //結果の文字表示
            bufferGraphics.drawString(app.OZ.text_result, 10, 40); //この表示内容はvoid kekka_syoriで決められる。

            if (displayGridInputAssist) {
                Point gridIndex = new Point(app.mainCreasePatternWorker.getGridPosition(p_mouse_TV_position));//20201024高密度入力がオンならばrepaint（画面更新）のたびにここで最寄り点を求めているので、描き職人で別途最寄り点を求めていることと二度手間になっている。

                double dx_ind = gridIndex.getX();
                double dy_ind = gridIndex.getY();
                int ix_ind = (int) Math.round(dx_ind);
                int iy_ind = (int) Math.round(dy_ind);
                bufferGraphics.drawString("(" + ix_ind + "," + iy_ind + ")", (int) p_mouse_TV_position.getX() + 25, (int) p_mouse_TV_position.getY() + 20); //この表示内容はvoid kekka_syoriで決められる。
            }

            if (TaskExecutor.isTaskRunning()) {
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
            g2.drawLine((int) (p_mouse_TV_position.getX()), (int) (p_mouse_TV_position.getY()),
                    (int) (p_mouse_TV_position.getX() + d_width), (int) (p_mouse_TV_position.getY() + d_width)); //直線
        }

        //描画したい内容はここまでAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

        // オフスクリーンイメージを実際に描画する。オフスクリーンの幅は最初は 0,0。
        g.drawImage(offscreen, 0, 0, this);

        if (app.OZ.summary_write_image_during_execution) {//Meaning during summary writing)
            writeImageFile(new File(app.fileModel.getExportImageFileName()), app);

            synchronized (app.w_image_running) {
                app.w_image_running.set(false);
                app.w_image_running.notify();
            }
        }

        if (flg_wi) {//For control when exporting with a frame 20180525
            flg_wi = false;
            writeImageFile(new File(app.fileModel.getExportImageFileName()), app);
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
            h_cam.setCamera(creasePatternCamera);
            h_cam.h3_and_h4_calculation();
            h_cam.parameter_calculation();
        }

        AffineTransform at = new AffineTransform();
        at.rotate(h_cam.getAngle() * Math.PI / 180.0, h_cam.getRotationX(), h_cam.getRotationY());
        g2h.setTransform(at);

        g2h.drawImage(imgh, h_cam.getX0(), h_cam.getY0(), h_cam.getX1(), h_cam.getY1(), this);

        at.rotate(-h_cam.getAngle() * Math.PI / 180.0, h_cam.getRotationX(), h_cam.getRotationY());
        g2h.setTransform(at);

    }

    // マウス操作(マウスが動いた時)を行う関数----------------------------------------------------
    public void mouseMoved(MouseEvent e) {
        //何もしない
        //  final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();//これは多分J2SE 5.0「Tiger」以降で作動するコード

        Point p = new Point(app.e2p(e));
        app.canvas.mouse_object_position(p);

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

                MouseWheelTarget target = pointInCreasePatternOrFoldedFigure(p, app);

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
            app.canvas.mouse_object_position(p);

            switch (btn) {
                case MouseEvent.BUTTON1:
                    break;
                case MouseEvent.BUTTON2:
                    switch (i_cp_or_oriagari) {
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
                    switch (i_cp_or_oriagari) {
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
                MouseWheelTarget target = pointInCreasePatternOrFoldedFigure(p, app);

                double scrollDistance = app.applicationModel.isPreciseZoom() ? e.getPreciseWheelRotation() : e.getWheelRotation();

                if (target == MouseWheelTarget.CREASE_PATTERN_0) {
                    app.creasePatternCameraModel.zoomBy(scrollDistance);
                } else {
                    app.foldedFigureModel.zoomBy(scrollDistance);
                }

                app.canvas.mouse_object_position(p_mouse_TV_position);
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
                Svg.exportFile(app.mainCreasePatternWorker.foldLineSet, creasePatternCamera, displayCpLines, lineWidth, intLineWidth, lineStyle, pointSize, app.foldedFigures, file);
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
                    int xMin = (int) app.mainCreasePatternWorker.operationFrameBox.getXMin();
                    int xMax = (int) app.mainCreasePatternWorker.operationFrameBox.getXMax();
                    int yMin = (int) app.mainCreasePatternWorker.operationFrameBox.getYMin();
                    int yMax = (int) app.mainCreasePatternWorker.operationFrameBox.getYMax();

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

    public MouseWheelTarget pointInCreasePatternOrFoldedFigure(Point p, App app) {//A function that determines which of the development and folding views the Ten obtained with the mouse points to.
        //20171216
        //hyouji_flg==2,ip4==0  omote
        //hyouji_flg==2,ip4==1	ura
        //hyouji_flg==2,ip4==2	omote & ura
        //hyouji_flg==2,ip4==3	omote & ura

        //hyouji_flg==3,ip4==0  omote
        //hyouji_flg==3,ip4==1	ura
        //hyouji_flg==3,ip4==2	omote & ura
        //hyouji_flg==3,ip4==3	omote & ura

        //hyouji_flg==5,ip4==0  omote
        //hyouji_flg==5,ip4==1	ura
        //hyouji_flg==5,ip4==2	omote & ura
        //hyouji_flg==5,ip4==3	omote & ura & omote2 & ura2

        //OZ_hyouji_mode=0;  nun
        //OZ_hyouji_mode=1;  omote
        //OZ_hyouji_mode=2;  ura
        //OZ_hyouji_mode=3;  omote & ura
        //OZ_hyouji_mode=4;  omote & ura & omote2 & ura2

        int tempFoldedFigureIndex = 0;
        MouseWheelTarget temp_i_cp_or_oriagari = MouseWheelTarget.CREASE_PATTERN_0;
        FoldedFigure OZi;
        for (int i = 1; i <= app.foldedFigures.size() - 1; i++) {
            OZi = app.foldedFigures.get(i);

            int OZ_display_mode = 0;//No fold-up diagram display
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 3;
            }//	omote & ura

            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 3;
            }//	omote & ura

            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 4;
            }//	omote & ura & omote2 & ura2

            if (OZi.cp_worker2.isInsideFront(p) > 0) {
                if (((OZ_display_mode == 1) || (OZ_display_mode == 3)) || (OZ_display_mode == 4)) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.FOLDED_FRONT_1;
                    tempFoldedFigureIndex = i;
                }
            }

            if (OZi.cp_worker2.isInsideRear(p) > 0) {
                if (((OZ_display_mode == 2) || (OZ_display_mode == 3)) || (OZ_display_mode == 4)) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.FOLDED_BACK_2;
                    tempFoldedFigureIndex = i;
                }
            }

            if (OZi.cp_worker2.isInsideTransparentFront(p) > 0) {
                if (OZ_display_mode == 4) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.TRANSPARENT_FRONT_3;
                    tempFoldedFigureIndex = i;
                }
            }

            if (OZi.cp_worker2.isInsideTransparentRear(p) > 0) {
                if (OZ_display_mode == 4) {
                    temp_i_cp_or_oriagari = MouseWheelTarget.TRANSPARENT_BACK_4;
                    tempFoldedFigureIndex = i;
                }
            }
        }
        i_cp_or_oriagari = temp_i_cp_or_oriagari;

        app.setFoldedFigureIndex(tempFoldedFigureIndex);

        return temp_i_cp_or_oriagari;
    }

    //=============================================================================
    //Method called when the mouse wheel rotates
    //=============================================================================
    public void mouse_object_position(Point p) {//この関数はmouseMoved等と違ってマウスイベントが起きても自動では認識されない
        p_mouse_TV_position.set(p.getX(), p.getY());

        p_mouse_object_position.set(creasePatternCamera.TV2object(p_mouse_TV_position));
    }

    public void setData(BackgroundModel backgroundModel) {
        if (backgroundModel.isLockBackground()) {
            h_cam.setLocked(backgroundModel.isLockBackground());
            h_cam.setCamera(creasePatternCamera);
            h_cam.h3_obj_and_h4_obj_calculation();
        } else {
            h_cam.setLocked(backgroundModel.isLockBackground());
        }
    }

    //----------------------------------------------------------------------
    //Functions that perform mouse operations (move and button operations)------------------------------
    //----------------------------------------------------------------------
    // ------------------------------------------------------
    public void background_set(Point t1, Point t2, Point t3, Point t4) {
        h_cam.set_h1(t1);
        h_cam.set_h2(t2);
        h_cam.set_h3(t3);
        h_cam.set_h4(t4);

        h_cam.parameter_calculation();
    }

    public void createTransparentBackground() {
        Robot robot;

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            ex.printStackTrace();
            return;
        }

        // Capture by specifying a range
        Rectangle canvasBounds = getBounds();

        java.awt.Point canvasLocation = getLocationOnScreen();
        Rectangle bounds = new Rectangle(canvasLocation.x, canvasLocation.y, canvasBounds.width, canvasBounds.height);

        java.awt.Point currentLocation = app.frame.getLocation();
        Dimension size = app.frame.getSize();

        // Move all associated windows outside the bounds.
        Window[] windows = app.frame.getOwnedWindows();
        java.util.Queue<java.awt.Point> locations = new LinkedList<>();
        app.frame.setLocation(currentLocation.x, currentLocation.y + size.height);
        for (Window w : windows) {
            java.awt.Point loc = w.getLocation();
            locations.offer(loc);
            w.setLocation(loc.x, loc.y + size.height);
        }

        app.backgroundModel.setBackgroundImage(robot.createScreenCapture(bounds));

        // Move all associated windows back.
        app.frame.setLocation(currentLocation);
        for (Window w : windows) {
            w.setLocation(locations.poll());
        }

        OritaCalc.display("新背景カメラインスタンス化");
        h_cam = new Background_camera();//20181202

        background_set(new Point(120.0, 120.0),
                new Point(120.0 + 10.0, 120.0),
                new Point(0, 0),
                new Point(10.0, 0));

        //Set each condition for background display
        app.backgroundModel.setDisplayBackground(true);

        if (app.backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
            h_cam.setLocked(true);
            h_cam.setCamera(creasePatternCamera);
            h_cam.h3_obj_and_h4_obj_calculation();
        }

        app.repaintCanvas();
    }

    public enum MouseWheelTarget {
        CREASE_PATTERN_0,
        FOLDED_FRONT_1,
        FOLDED_BACK_2,
        TRANSPARENT_FRONT_3,
        TRANSPARENT_BACK_4,
    }
}
