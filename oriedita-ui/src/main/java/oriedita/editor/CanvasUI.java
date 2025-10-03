package oriedita.editor;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Background_camera;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.DrawingSettings;
import oriedita.editor.handler.MouseModeHandler;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.swing.component.BulletinBoard;
import origami.crease_pattern.element.Point;
import origami.folding.FoldedFigure;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Dependent // This bean is not proxyable (because JPanel)
public class CanvasUI extends JPanel {
    private final Camera creasePatternCamera;
    private final TaskExecutorService foldingExecutor;
    private final BackgroundModel backgroundModel;
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final AnimationService animationService;
    private final ApplicationModel applicationModel;
    private final BulletinBoard bulletinBoard;
    private final FoldedFiguresList foldedFiguresList;
    private final FoldedFigureModel foldedFigureModel;
    private final GridModel gridModel;

    private MouseModeHandler activeMouseHandler;

    /**
     * Position of the cursor relative to the center of the crease pattern.
     */
    private Point mousePosition = new Point();//マウスのオブジェクト座標上の位置
    /**
     * Position of the cursor on the canvas
     */
    private Point mousePositionOnCanvas = new Point();//マウスのTV座標上の位置
    private boolean hideOperationFrame = false;
    private boolean antiAlias;

    private boolean displayPointSpotlight;
    private boolean displayPointOffset;
    private boolean displayComments;
    private boolean displayCpLines;
    private boolean displayAuxLines;
    private boolean displayLiveAuxLines;
    private Background_camera h_cam = new Background_camera();
    // Canvas width and height
    private Dimension dim;
    private LineStyle lineStyle;
    private float lineWidth;
    private float auxLineWidth;

    @Inject
    public CanvasUI(
            @Named("creasePatternCamera") Camera creasePatternCamera,
            @Named("foldingExecutor") TaskExecutorService foldingExecutor,
            BackgroundModel backgroundModel,
            CanvasModel canvasModel,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            AnimationService animationService,
            ApplicationModel applicationModel,
            BulletinBoard bulletinBoard,
            FoldedFigureModel foldedFigureModel,
            FoldedFiguresList foldedFiguresList,
            GridModel gridModel) {
        this.creasePatternCamera = creasePatternCamera;
        this.foldingExecutor = foldingExecutor;
        this.backgroundModel = backgroundModel;
        this.canvasModel = canvasModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.animationService = animationService;
        this.applicationModel = applicationModel;
        this.bulletinBoard = bulletinBoard;
        this.foldedFigureModel = foldedFigureModel;
        this.foldedFiguresList = foldedFiguresList;
        this.gridModel = gridModel;
    }

    public void init() {
        onResize();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = new Point(e.getPoint().getX(), e.getPoint().getY());
                canvasModel.setMousePosition(p);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = new Point(e.getPoint().getX(), e.getPoint().getY());
                canvasModel.setMousePosition(p);
            }
        });
    }

    public void writeImageFile(File file) {

        if (file != null) {
            String fname = file.getName();

            String formatName;

            if (fname.endsWith("png")) {
                formatName = "png";
            } else if (fname.endsWith("jpg")) {
                formatName = "jpg";
            } else {
                file = new File(fname + ".png");
                formatName = "png";
            }

            //	ファイル保存

            try {
                BufferedImage myImage = getGraphicsConfiguration().createCompatibleImage(getSize().width, getSize().height);
                Graphics g = myImage.getGraphics();

                setHideOperationFrame(true);
                paintComponent(g);
                setHideOperationFrame(false);

                if (canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61 && mainCreasePatternWorker.getLineStep().size() == 4) { //枠設定時の枠内のみ書き出し 20180524
                    int xMin = (int) mainCreasePatternWorker.getOperationFrame().getPolygon().getXMin();
                    int xMax = (int) mainCreasePatternWorker.getOperationFrame().getPolygon().getXMax();
                    int yMin = (int) mainCreasePatternWorker.getOperationFrame().getPolygon().getYMin();
                    int yMax = (int) mainCreasePatternWorker.getOperationFrame().getPolygon().getYMax();

                    ImageIO.write(myImage.getSubimage(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1), formatName, file);

                } else {//Full export without frame
                    Logger.info("2018-529_");

                    ImageIO.write(myImage, formatName, file);
                }
            } catch (IOException e) {
                Logger.error(e, "Writing image file failed");
            }

            Logger.info("終わりました");
        }
    }

    public void paintComponent(Graphics bufferGraphics) {
        boolean displayMarkings = applicationModel.getDisplayMarkings();
        //「f」を付けることでfloat型の数値として記述することができる
        Graphics2D g2 = (Graphics2D) bufferGraphics;
        animationService.update();

        BasicStroke BStroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2.setStroke(BStroke);//線の太さや線の末端の形状

        //アンチエイリアス　オフ
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);//アンチエイリアス　オン

        g2.setBackground(Colors.get(Color.WHITE));    //この行は、画像をファイルに書き出そうとしてBufferedImageクラスを使う場合、デフォルトで背景が黒になるので、それを避けるための意味　20170107
        //画像をファイルに書き出さすことはやめて、、BufferedImageクラスを使わず、Imageクラスだけですむなら不要の行

        //別の重なりさがし　のボタンの色の指定。


        // バッファー画面のクリア
        bufferGraphics.clearRect(0, 0, dim.width, dim.height);

        bufferGraphics.setColor(Colors.get(Color.black));
        //描画したい内容は以下に書くことVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

        //カメラのセット
        mainCreasePatternWorker.setCamera(creasePatternCamera);
        mainCreasePatternWorker.drawGrid(bufferGraphics, dim.width, dim.height);

        FoldedFigure_Drawer[] foldedFigureDrawers = foldedFiguresList.getItems();
        for (FoldedFigure_Drawer d : foldedFigureDrawers) {
            d.setParentCamera(creasePatternCamera);
        }

        for (FoldedFigure_Drawer d : foldedFigureDrawers) {
            d.drawSelfIntersectingSubFaces(bufferGraphics);
        }

        for (FoldedFigure_Drawer d : foldedFigureDrawers) {
            d.getWireFrameWorkerDrawer_flat().setCamera(creasePatternCamera);
        }

        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

        if (selectedFigure != null) {
//VVVVVVVVVVVVVVV以下のts2へのカメラセットはOriagari_zuのoekakiで実施しているので以下の5行はなくてもいいはず　20180225
            selectedFigure.getWireFrameWorkerDrawer_folded().setCamera(selectedFigure.getFoldedFigureCamera());
            selectedFigure.getWireFrameWorkerDrawer_folded().setCam_front(selectedFigure.getFoldedFigureFrontCamera());
            selectedFigure.getWireFrameWorkerDrawer_folded().setCam_rear(selectedFigure.getFoldedFigureRearCamera());
            selectedFigure.getWireFrameWorkerDrawer_folded().setCam_transparent_front(selectedFigure.getTransparentFrontCamera());
            selectedFigure.getWireFrameWorkerDrawer_folded().setCam_transparent_rear(selectedFigure.getTransparentRearCamera());
            selectedFigure.getData(foldedFigureModel);
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        }
        //Logger.info("paint　+++++++++++++++++++++　背景表示");
        //背景表示
        Image backgroundImage = backgroundModel.getBackgroundImage();
        updateBackgroundCamera();
        if ((backgroundImage != null) && backgroundModel.isDisplayBackground()) {
            int iw = backgroundImage.getWidth(this);//イメージの幅を取得
            int ih = backgroundImage.getHeight(this);//イメージの高さを取得

            h_cam.setBackgroundWidth(iw);
            h_cam.setBackgroundHeight(ih);

            drawBackground(g2, backgroundImage);
        }

        //格子表示

        //基準面の表示
        if (displayMarkings && selectedFigure != null) {
            if (selectedFigure.getFoldedFigure().displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                selectedFigure.getWireFrameWorkerDrawer_flat().drawStartingFaceWithCamera(bufferGraphics, selectedFigure.getStartingFaceId());//ts1が折り畳みを行う際の基準面を表示するのに使う。
            }
        }

        double d_width = creasePatternCamera.getCameraZoomX() * mainCreasePatternWorker.getSelectionDistance();

        //展開図表示
        mainCreasePatternWorker.drawWithCamera(bufferGraphics,
                displayComments, displayCpLines, displayAuxLines, displayLiveAuxLines,
                applicationModel.getDisplayCpText(), lineWidth, lineStyle, auxLineWidth,
                dim.width, dim.height, displayMarkings, hideOperationFrame);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ,展開図動かし中心の十字の目印の表示
        DrawingSettings settings = new DrawingSettings(
                lineWidth, auxLineWidth, applicationModel.getPointSize(), lineStyle,
                dim.height, dim.width,
                applicationModel.getRoundedEnds(),
                applicationModel.getDisplayComments(),
                applicationModel.getDisplayCurrentStep());
        if (activeMouseHandler != null) {
            activeMouseHandler.drawPreview(g2, creasePatternCamera, settings);
        }
        int topY = canvasModel.getToolSettingsPanelHeight();
        if (displayComments) {
            //展開図情報の文字表示
            bufferGraphics.setColor(Colors.get(Color.black));

            if (gridModel.getBaseState() == GridModel.State.HIDDEN){
                bufferGraphics.drawString(String.format("mouse= ( %.2f, %.2f ) ", mousePosition.getX(), mousePosition.getY()),
                        10, getHeight()- 10); //この表示内容はvoid kekka_syoriで決められる。
            } else {
                Point gridIndex = new Point(mainCreasePatternWorker.getGridPosition(mousePositionOnCanvas));//20201024高密度入力がオンならばrepaint（画面更新）のたびにここで最寄り点を求めているので、描き職人で別途最寄り点を求めていることと二度手間になっている。

                double dx_ind = gridIndex.getX();
                double dy_ind = gridIndex.getY();
                int ix_ind = (int) Math.round(dx_ind);
                int iy_ind = (int) Math.round(dy_ind);
                bufferGraphics.drawString(String.format("mouse=( %.2f, %.2f ), grid=(%d, %d) ",
                                mousePosition.getX(), mousePosition.getY(),
                                ix_ind, iy_ind),
                        10, getHeight()- 10); //この表示内容はvoid kekka_syoriで決められる。
            }

            bufferGraphics.drawString("L=" + mainCreasePatternWorker.getTotal(), 10, getHeight() - 25); //この表示内容はvoid kekka_syoriで決められる。

            if (selectedFigure != null) {
                //結果の文字表示
                bufferGraphics.drawString(selectedFigure.getFoldedFigure().text_result, 10, topY + 20); //この表示内容はvoid kekka_syoriで決められる。
            }

            if (foldingExecutor.isTaskRunning()) {
                bufferGraphics.setColor(Colors.get(Color.red));

                bufferGraphics.drawString(foldingExecutor.getTaskName() + " Under Calculation. If you want to cancel calculation, uncheck [check A + MV]on right side and press the brake button (bicycle brake icon) on lower side.",
                        10, topY + 39); //この表示内容はvoid kekka_syoriで決められる。
                bufferGraphics.drawString("計算中。　なお、計算を取り消し通常状態に戻りたいなら、右辺の[check A+MV]のチェックをはずし、ブレーキボタン（下辺の、自転車のブレーキのアイコン）を押す。 ",
                        10, topY + 53); //この表示内容はvoid kekka_syoriで決められる。
            }


            bulletinBoard.draw(bufferGraphics);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        }

        if (applicationModel.getDisplayWarnings()){
            if (canvasModel.getWarningMessage() != null) {
                bufferGraphics.setColor(Colors.get(Color.yellow));
                bufferGraphics.drawString(canvasModel.getWarningMessage(), 10, topY + 67);
            }
        }


        //折り上がりの各種お絵かき
        int ffDrawerIndex = 0;
        for (FoldedFigure_Drawer d : foldedFigureDrawers) {
            d.foldUp_draw(bufferGraphics, displayMarkings, ffDrawerIndex + 1, d == foldedFiguresList.getSelectedItem());
            ffDrawerIndex++;
        }

        //展開図を折り上がり図の上に描くために、展開図を再表示する
        if (applicationModel.getDisplayCreasePatternOnTop()) {
            mainCreasePatternWorker.drawWithCamera(bufferGraphics, displayComments, displayCpLines,
                    displayAuxLines, displayLiveAuxLines, applicationModel.getDisplayCpText(),
                    lineWidth, lineStyle, auxLineWidth, dim.width, dim.height,
                    displayMarkings, hideOperationFrame);//渡す情報はカメラ設定、線幅、画面X幅、画面y高さ
        }

        //アンチェイリアス
        //アンチェイリアス　オフ
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);//アンチェイリアス　オン

        //Flashlight (dot) search range
        if (displayPointSpotlight) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(Colors.get(new Color(255, 240, 0, 230)));
            g2.draw(new Ellipse2D.Double(mousePositionOnCanvas.getX() - d_width, mousePositionOnCanvas.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
        }

        //Central indicator
        if (displayPointOffset) {
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(Colors.get(Color.black));
            g2.drawLine((int) (mousePositionOnCanvas.getX()), (int) (mousePositionOnCanvas.getY()),
                    (int) (mousePositionOnCanvas.getX() + d_width), (int) (mousePositionOnCanvas.getY() + d_width)); //直線
        }

        if (animationService.isAnimating()) {
            SwingUtilities.invokeLater(canvasModel::markDirty);
        }
    }

    public void setHideOperationFrame(boolean hideOperationFrame) {
        this.hideOperationFrame = hideOperationFrame;
    }

    public void updateBackgroundCamera() {
        if (backgroundModel.isLockBackground()) {
            h_cam.setCamera(creasePatternCamera);
            h_cam.h3_and_h4_calculation();
            h_cam.parameter_calculation();
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


        g2h.rotate(h_cam.getAngle() * Math.PI / 180.0, h_cam.getRotationX(), h_cam.getRotationY());

        g2h.drawImage(imgh, h_cam.getX0(), h_cam.getY0(), h_cam.getX1(), h_cam.getY1(), this);

        g2h.rotate(-h_cam.getAngle() * Math.PI / 180.0, h_cam.getRotationX(), h_cam.getRotationY());


    }

    public void setData(ApplicationModel applicationModel) {
        antiAlias = applicationModel.getAntiAlias();
        lineWidth = applicationModel.determineCalculatedLineWidth();
        auxLineWidth = applicationModel.determineCalculatedAuxLineWidth();
        lineStyle = applicationModel.getLineStyle();
        displayPointSpotlight = applicationModel.getDisplayPointSpotlight();
        displayPointOffset = applicationModel.getDisplayPointOffset();
        displayComments = applicationModel.getDisplayComments();
        displayCpLines = applicationModel.getDisplayCpLines();
        displayAuxLines = applicationModel.getDisplayAuxLines();
        displayLiveAuxLines = applicationModel.getDisplayLiveAuxLines();
    }

    public void setActiveMouseHandler(MouseModeHandler activeMouseHandler) {
        this.activeMouseHandler = activeMouseHandler;
    }

    public Background_camera getH_cam() {
        return h_cam;
    }

    public void setH_cam(Background_camera hCam) {
        this.h_cam = hCam;
    }

    public void setMousePosition(Point point) {
        mousePositionOnCanvas = (point);
        mousePosition = (creasePatternCamera.TV2object(point));
    }

    public void onResize() {
        dim = getSize();
        if (dim.width <= 0 || dim.height <= 0) {
            // Set a default size if the canvas is not yet loaded.
            dim = new Dimension(2000, 1000);
        }

        repaint();
    }
}
