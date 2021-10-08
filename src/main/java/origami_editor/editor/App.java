package origami_editor.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.HierarchyList_Worker;
import origami_editor.editor.action.Click;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing_worker.*;
import origami_editor.editor.export.Cp;
import origami_editor.editor.export.Obj;
import origami_editor.editor.export.Orh;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.editor.folded_figure.FoldedFigure_01;
import origami_editor.editor.json.DefaultObjectMapper;
import origami_editor.editor.task.CheckCAMVTask;
import origami_editor.editor.task.FoldingEstimateTask;
import origami_editor.tools.ResourceUtil;
import origami_editor.tools.StringOp;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.nio.file.Path;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static origami_editor.tools.ResourceUtil.createImageIcon;
import static origami_editor.tools.ResourceUtil.getAppDir;

public class App extends JFrame implements ActionListener {
    public static final String CONFIG_JSON = "config.json";
    public final ApplicationModel applicationModel = new ApplicationModel();
    public final GridModel gridModel = new GridModel();
    public final CanvasModel canvasModel = new CanvasModel();
    public final FoldedFigureModel foldedFigureModel = new FoldedFigureModel();
    public final AngleSystemModel angleSystemModel = new AngleSystemModel();
    public final MeasuresModel measuresModel = new MeasuresModel();
    public final InternalDivisionRatioModel internalDivisionRatioModel = new InternalDivisionRatioModel();
    public final HistoryStateModel historyStateModel = new HistoryStateModel();
    public final BackgroundModel backgroundModel = new BackgroundModel();
    public final CameraModel creasePatternCameraModel = new CameraModel();
    public final FileModel fileModel = new FileModel();
    public final AtomicBoolean w_image_running = new AtomicBoolean(false); // Folding together execution. If a single image export is in progress, it will be true.
    public final DrawingWorker mainDrawingWorker = new DrawingWorker(this);    // Basic branch craftsman. Accepts input from the mouse.
    final Queue<Popup> popups = new ArrayDeque<>();
    public FoldedFigure temp_OZ;    //Folded figure
    public FoldedFigure OZ;    //Current Folded figure
    public LineSegmentSet lineSegmentsForFolding;//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(lineSegmentsForFolding)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
    public BulletinBoard bulletinBoard = new BulletinBoard();
    public MouseMode mouseMode = MouseMode.FOLDABLE_LINE_DRAW_71;//Defines the response to mouse movements. If it is 1, the line segment input mode. If it is 2, adjust the development view (move). If it is 101, operate the folded figure.
    // ------------------------------------------------------------------------
    public Point point_of_referencePlane_old = new Point(); //ten_of_kijyunmen_old.set(OZ.ts1.get_ten_of_kijyunmen_tv());//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加
    // Buffer screen settings VVVVVVVVVVVVVVVVVVVVVVVVV
    public Canvas canvas;
    public ArrayList<FoldedFigure> foldedFigures = new ArrayList<>(); //Instantiation of fold-up diagram
    public File exportFile;
    public File fname_and_number;//まとめ書き出しに使う。
    public double d_ap_check4 = 0.0;
    int foldedFigureIndex = 0;//Specify which number of foldedFigures Oriagari_Zu is the target of button operation or transformation operation
    Background_camera h_cam = new Background_camera();
    //各種変数の定義
    String frame_title_0;//フレームのタイトルの根本部分
    String frame_title;//フレームのタイトルの全体
    Image img_background;       //Image for background
    Point p_mouse_object_position = new Point();//マウスのオブジェクト座標上の位置
    Point p_mouse_TV_position = new Point();//マウスのTV座標上の位置
    HelpDialog explanation;
    boolean mouseDraggedValid = false;
    //ウィンドウ透明化用のパラメータ
    boolean mouseReleasedValid = false;//0 ignores mouse operation. 1 is valid for mouse operation. When an unexpected mouseDragged or mouseReleased occurs due to on-off of the file box, set it to 0 so that it will not be picked up. These are set to 1 valid when the mouse is clicked.
    //画像出力するため20170107_oldと書かれた行をコメントアウトし、20170107_newの行を有効にした。
    //画像出力不要で元にもどすなら、20170107_oldと書かれた行を有効にし、20170107_newの行をコメントアウトにすればよい。（この変更はOrihime.javaの中だけに2箇所ある）
    // オフスクリーン
    boolean flg61 = false;//Used when setting the frame 　20180524
    MouseWheelTarget i_cp_or_oriagari = MouseWheelTarget.CREASE_PATTERN_0;//0 if the target of the mouse wheel is a cp development view, 1 if it is a folded view (front), 2 if it is a folded view (back), 3 if it is a transparent view (front), 4 if it is a transparent view (back)
    Map<KeyStroke, AbstractButton> helpInputMap = new HashMap<>();
    ExecutorService pool;
    private Future<?> currentTask;

    public App() {
        setTitle("Origami Editor " + ResourceUtil.getVersionFromManifest());//Specify the title and execute the constructor
        frame_title_0 = getTitle();
        frame_title = frame_title_0;//Store title in variable
        mainDrawingWorker.setTitle(frame_title);

        //--------------------------------------------------------------------------------------------------
        addWindowListener(new WindowAdapter() {//ウィンドウの状態が変化したときの処理
            //終了ボタンを有効化
            public void windowClosing(WindowEvent evt) {
                System.out.println("windowClosing_20200928");
                closing();//Work to be done when pressing X at the right end of the upper side of the window
            }//終了ボタンを有効化 ここまで。

            public void windowOpened(WindowEvent eve) {
                System.out.println("windowOpened_20200928");
            }

            public void windowClosed(WindowEvent eve) {
                System.out.println("windowClosed_20200928");
            }

            public void windowIconified(WindowEvent eve) {
                System.out.println("windowIconified_20200928");
            }

            public void windowDeiconified(WindowEvent eve) {
                System.out.println("windowDeiconified_20200928");
            }

            public void windowActivated(WindowEvent eve) {
                System.out.println("windowActivated_20200928");
            }

            public void windowDeactivated(WindowEvent eve) {
                System.out.println("windowDeactivated_20200928");
            }

            public void windowStateChanged(WindowEvent eve) {
                System.out.println("windowStateChanged_20200928");
            }
        });//Processing when the window state changes Up to here.

        //--------------------------------------------------------------------------------------------------
        addWindowFocusListener(new WindowAdapter() {//オリヒメのメインウィンドウのフォーカスが変化したときの処理
            public void windowGainedFocus(WindowEvent evt) {
                System.out.println("windowGainedFocus_20200929");
            }

            public void windowLostFocus(WindowEvent evt) {
                System.out.println("windowLostFocus_20200929");
                Popup popup;
                while ((popup = popups.poll()) != null) {
                    popup.hide();
                }
            }
        });//オリヒメのメインウィンドウのフォーカスが変化したときの処理 ここまで。

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setFocusable(true);
        setFocusTraversalKeysEnabled(true);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                Popup popup;
                while ((popup = popups.poll()) != null) {
                    popup.hide();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown() && popups.isEmpty()) {
                    for (Map.Entry<KeyStroke, AbstractButton> entry : helpInputMap.entrySet()) {
                        AbstractButton button = entry.getValue();
                        KeyStroke keyStroke = entry.getKey();

                        if (!button.isShowing()) continue;

                        java.awt.Point locationOnScreen = button.getLocationOnScreen();
                        Dimension size = button.getSize();
                        JToolTip tooltip = new JToolTip();
                        tooltip.setTipText(keyStroke.toString().replaceAll("pressed ", ""));
                        Dimension tooltipSize = tooltip.getPreferredSize();
                        Popup myPopup = PopupFactory.getSharedInstance().getPopup(button, tooltip, locationOnScreen.x + size.width / 2 - tooltipSize.width / 2, locationOnScreen.y + size.height - 10);
                        myPopup.show();

                        popups.offer(myPopup);
                    }
                }
            }
        });

        foldedFigures.clear();
        addNewFoldedFigure();
        OZ = foldedFigures.get(0);//折りあがり図

        Editor editor = new Editor(this);

        canvas = editor.getCanvas();

        temp_OZ = new FoldedFigure(bulletinBoard);
        bulletinBoard.addChangeListener(e -> repaint());

        canvas.creasePatternCamera.setCameraPositionX(0.0);
        canvas.creasePatternCamera.setCameraPositionY(0.0);
        canvas.creasePatternCamera.setCameraAngle(0.0);
        canvas.creasePatternCamera.setCameraMirror(1.0);
        canvas.creasePatternCamera.setCameraZoomX(1.0);
        canvas.creasePatternCamera.setCameraZoomY(1.0);
        canvas.creasePatternCamera.setDisplayPositionX(350.0);
        canvas.creasePatternCamera.setDisplayPositionY(350.0);

        OZ.foldedFigure_camera_initialize();

        pool = Executors.newFixedThreadPool(1);

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("fishbase.png")));


        setContentPane(editor.$$$getRootComponent$$$());

        TopPanel topPanel = editor.getTopPanel();
        RightPanel rightPanel = editor.getRightPanel();
        BottomPanel bottomPanel = editor.getBottomPanel();
        LeftPanel leftPanel = editor.getLeftPanel();

        AppMenuBar appMenuBar = new AppMenuBar(this);

        setJMenuBar(appMenuBar);

        leftPanel.getGridConfigurationData(gridModel);

        applicationModel.addPropertyChangeListener(e -> mainDrawingWorker.setData(e, applicationModel));
        applicationModel.addPropertyChangeListener(e -> canvas.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> appMenuBar.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> topPanel.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> rightPanel.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> leftPanel.setData(e, applicationModel));

        applicationModel.addPropertyChangeListener(e -> persistApplicationModel());

        restoreApplicationModel();

        gridModel.addPropertyChangeListener(e -> mainDrawingWorker.setGridConfigurationData(gridModel));
        gridModel.addPropertyChangeListener(e -> leftPanel.setGridConfigurationData(gridModel));

        angleSystemModel.addPropertyChangeListener(e -> rightPanel.setData(angleSystemModel));
        angleSystemModel.addPropertyChangeListener(e -> mainDrawingWorker.setData(angleSystemModel));
        angleSystemModel.addPropertyChangeListener(e -> {
            switch (angleSystemModel.getAngleSystemInputType()) {
                case DEG_1:
                    canvasModel.setMouseMode(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13);
                    break;
                case DEG_2:
                    canvasModel.setMouseMode(MouseMode.ANGLE_SYSTEM_16);
                    break;
                case DEG_3:
                    canvasModel.setMouseMode(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17);
                    break;
                case DEG_4:
                    canvasModel.setMouseMode(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18);
                    break;
                case DEG_5:
                    canvasModel.setMouseMode(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37);
                    break;
            }
        });
        angleSystemModel.addPropertyChangeListener(e -> repaintCanvas());

        measuresModel.addPropertyChangeListener(e -> rightPanel.setData(measuresModel));

        internalDivisionRatioModel.addPropertyChangeListener(e -> topPanel.setData(internalDivisionRatioModel));
        internalDivisionRatioModel.addPropertyChangeListener(e -> mainDrawingWorker.setData(internalDivisionRatioModel));

        foldedFigureModel.addPropertyChangeListener(e -> OZ.setData(foldedFigureModel));
        foldedFigureModel.addPropertyChangeListener(e -> bottomPanel.setData(foldedFigureModel));
        foldedFigureModel.addPropertyChangeListener(e -> repaintCanvas());
        foldedFigureModel.addPropertyChangeListener(e -> leftPanel.setData(foldedFigureModel));

        canvasModel.addPropertyChangeListener(e -> mainDrawingWorker.setData(canvasModel));
        canvasModel.addPropertyChangeListener(e -> canvas.setData(canvasModel));
        canvasModel.addPropertyChangeListener(e -> topPanel.setData(e, canvasModel));
        canvasModel.addPropertyChangeListener(e -> rightPanel.setData(e, canvasModel));
        canvasModel.addPropertyChangeListener(e -> leftPanel.setData(e, canvasModel));
        canvasModel.addPropertyChangeListener(e -> bottomPanel.setData(e, canvasModel));

        canvasModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
                CanvasModel canvasModel = (CanvasModel) e.getSource();
                System.out.println("mouseMode = " + canvasModel.getMouseMode());

                mouseMode = canvasModel.getMouseMode();
            }
        });

        historyStateModel.addPropertyChangeListener(e -> leftPanel.setData(historyStateModel));
        historyStateModel.addPropertyChangeListener(e -> rightPanel.setData(historyStateModel));
        historyStateModel.addPropertyChangeListener(e -> mainDrawingWorker.setData(historyStateModel));

        backgroundModel.addPropertyChangeListener(e -> topPanel.setData(backgroundModel));
        backgroundModel.addPropertyChangeListener(e -> {
            if (backgroundModel.isLockBackground()) {
                h_cam.setLocked(backgroundModel.isLockBackground());
                h_cam.setCamera(canvas.creasePatternCamera);
                h_cam.h3_obj_and_h4_obj_calculation();
            } else {
                h_cam.setLocked(backgroundModel.isLockBackground());
            }
        });

        creasePatternCameraModel.addPropertyChangeListener(e -> canvas.creasePatternCamera.setData(creasePatternCameraModel));
        creasePatternCameraModel.addPropertyChangeListener(e -> topPanel.setData(creasePatternCameraModel));
        creasePatternCameraModel.addPropertyChangeListener(e -> repaintCanvas());

        fileModel.addPropertyChangeListener(e -> appMenuBar.setData(fileModel));
        fileModel.addPropertyChangeListener(e -> setData(fileModel));

        fileModel.reset();

        developmentView_initialization();

        configure_initialize_prediction();

        Button_shared_operation();

        mainDrawingWorker.setCamera(canvas.creasePatternCamera);

        mainDrawingWorker.record();
        mainDrawingWorker.auxRecord();

        pack();
        setLocationRelativeTo(null);//If you want to put the application window in the center of the screen, use the setLocationRelativeTo () method. If you pass null, it will always be in the center.
        setVisible(true);

        explanation = new HelpDialog(this, canvas.getLocationOnScreen(), canvas.getSize());

        applicationModel.addPropertyChangeListener(e -> {
            explanation.setVisible(applicationModel.getHelpVisible());
            requestFocus();
        });
        explanation.setVisible(applicationModel.getHelpVisible());
        //focus back to here after creating dialog
        requestFocus();

        canvas.addMouseModeHandler(MouseHandlerDrawCreaseFree.class);
        canvas.addMouseModeHandler(MouseHandlerLineSegmentDelete.class);
        canvas.addMouseModeHandler(MouseHandlerSquareBisector.class);
        canvas.addMouseModeHandler(MouseHandlerFoldableLineDraw.class);
        canvas.addMouseModeHandler(MouseHandlerVertexMakeAngularlyFlatFoldable.class);
        canvas.addMouseModeHandler(MouseHandlerVoronoiCreate.class);
        canvas.addMouseModeHandler(MouseHandlerLineSegmentRatioSet.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawThreePoint.class);
        canvas.addMouseModeHandler(MouseHandlerCreasesAlternateMV.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawConcentricTwoCircleSelect.class);
        canvas.addMouseModeHandler(MouseHandlerInward.class);
        canvas.addMouseModeHandler(MouseHandlerPolygonSetNoCorners.class);
        canvas.addMouseModeHandler(MouseHandlerDrawCreaseAngleRestricted5.class);
        canvas.addMouseModeHandler(MouseHandlerPerpendicularDraw.class);
        canvas.addMouseModeHandler(MouseHandlerSymmetricDraw.class);
        canvas.addMouseModeHandler(MouseHandlerParallelDraw.class);
        canvas.addMouseModeHandler(MouseHandlerContinuousSymmetricDraw.class);
        canvas.addMouseModeHandler(MouseHandlerDisplayLengthBetweenPoints1.class);
        canvas.addMouseModeHandler(MouseHandlerDisplayLengthBetweenPoints2.class);
        canvas.addMouseModeHandler(MouseHandlerDisplayAngleBetweenThreePoints1.class);
        canvas.addMouseModeHandler(MouseHandlerDisplayAngleBetweenThreePoints2.class);
        canvas.addMouseModeHandler(MouseHandlerDisplayAngleBetweenThreePoints3.class);
        canvas.addMouseModeHandler(MouseHandlerFoldableLineInput.class);
        canvas.addMouseModeHandler(MouseHandlerLineSegmentDivision.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDraw.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseMakeEdge.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseMakeAux.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseMakeValley.class);
        canvas.addMouseModeHandler(MouseHandlerOperationFrameCreate.class);
        canvas.addMouseModeHandler(MouseHandlerChangeCreaseType.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawFree.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawSeparate.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawConcentric.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawConcentricSelect.class);
        canvas.addMouseModeHandler(MouseHandlerParallelDrawWidth.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawTangentLine.class);
        canvas.addMouseModeHandler(MouseHandlerCircleDrawInverted.class);
        canvas.addMouseModeHandler(MouseHandlerDeletePoint.class);
        canvas.addMouseModeHandler(MouseHandlerVertexDeleteOnCrease.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseMakeMountain.class);
        canvas.addMouseModeHandler(MouseHandlerDrawPoint.class);
        canvas.addMouseModeHandler(MouseHandlerDrawCreaseAngleRestricted3_2.class);
        canvas.addMouseModeHandler(MouseHandlerDrawCreaseRestricted.class);
        canvas.addMouseModeHandler(MouseHandlerDrawCreaseAngleRestricted2.class);
        canvas.addMouseModeHandler(MouseHandlerAngleSystem.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseAdvanceType.class);
        canvas.addMouseModeHandler(MouseHandlerFishBoneDraw.class);
        canvas.addMouseModeHandler(MouseHandlerDoubleSymmetricDraw.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseMove4p.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseCopy4p.class);
        canvas.addMouseModeHandler(MouseHandlerDrawCreaseSymmetric.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseMakeMV.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseDeleteOverlapping.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseMove.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseCopy.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseSelect.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseUnselect.class);
        canvas.addMouseModeHandler(MouseHandlerCircleChangeColor.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseToggleMV.class);
        canvas.addMouseModeHandler(MouseHandlerUnused_6.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseDeleteIntersecting.class);
        canvas.addMouseModeHandler(MouseHandlerSelectPolygon.class);
        canvas.addMouseModeHandler(MouseHandlerUnselectPolygon.class);
        canvas.addMouseModeHandler(MouseHandlerSelectLineIntersecting.class);
        canvas.addMouseModeHandler(MouseHandlerUnselectLineIntersecting.class);
        canvas.addMouseModeHandler(MouseHandlerFlatFoldableCheck.class);
        canvas.addMouseModeHandler(MouseHandlerDrawCreaseAngleRestricted.class);
        canvas.addMouseModeHandler(MouseHandlerLengthenCreaseSameColor.class);
        canvas.addMouseModeHandler(MouseHandlerLengthenCrease.class);
        canvas.addMouseModeHandler(MouseHandlerUnused_10001.class);
        canvas.addMouseModeHandler(MouseHandlerUnused_10002.class);
        canvas.addMouseModeHandler(MouseHandlerBackgroundChangePosition.class);
        canvas.addMouseModeHandler(new MouseHandlerMoveCalculatedShape(this));
        canvas.addMouseModeHandler(new MouseHandlerModifyCalculatedShape(this));
        canvas.addMouseModeHandler(new MouseHandlerMoveCreasePattern(this));
        canvas.addMouseModeHandler(new MouseHandlerChangeStandardFace(this));

    }

    private void restoreApplicationModel() {
        Path storage = getAppDir();
        if (!storage.toFile().exists()) {
            applicationModel.reset();

            return;
        }

        ObjectMapper mapper = new DefaultObjectMapper();
        File configFile = storage.resolve(CONFIG_JSON).toFile();

        try {
            ApplicationModel loadedApplicationModel = mapper.readValue(configFile, ApplicationModel.class);

            applicationModel.set(loadedApplicationModel);
        } catch (IOException e) {
            // An application state is found, but it is not valid.
            JOptionPane.showMessageDialog(this, "<html>Failed to load application state.<br/>Loading default application configuration.", "State load failed", JOptionPane.WARNING_MESSAGE);

            if (!configFile.renameTo(storage.resolve(CONFIG_JSON + ".old").toFile())) {
                System.err.println("Not allowed to move config.json");
            }

            applicationModel.reset();
        }
    }

    private void persistApplicationModel() {
        Path storage = getAppDir();

        if (!storage.toFile().exists()) {
            if (!storage.toFile().mkdirs()) {
                System.err.println("Failed to create directory for application model");

                return;
            }
        }

        ObjectMapper mapper = new DefaultObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(storage.resolve(CONFIG_JSON).toFile(), applicationModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setData(FileModel fileModel) {
        if (fileModel.getSavedFileName() != null) {
            File file = new File(fileModel.getSavedFileName());

            frame_title = frame_title_0 + "        " + file.getName();
        } else {
            frame_title = frame_title_0 + "        " + "Unsaved";
        }

        if (!fileModel.isSaved()) {
            frame_title += "*";
        }

        setTitle(frame_title);
        mainDrawingWorker.setTitle(frame_title);
    }

    public void repaintCanvas() {
        canvas.repaint();
    }

    public FoldType getFoldType() {
        FoldType foldType;//= 0 Do nothing, = 1 Folding estimation for all fold lines in the normal development view, = 2 for fold estimation for selected fold lines, = 3 for changing the folding state
        int foldLineTotalForSelectFolding = mainDrawingWorker.getFoldLineTotalForSelectFolding();
        System.out.println("foldedFigures.size() = " + foldedFigures.size() + "    : foldedFigureIndex = " + foldedFigureIndex + "    : mainDrawingWorker.get_orisensuu_for_select_oritatami() = " + foldLineTotalForSelectFolding);
        if (foldedFigures.size() == 1) {                        //折り上がり系図無し
            if (foldedFigureIndex == 0) {                            //展開図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    foldType = FoldType.FOR_ALL_LINES_1;//全展開図で折畳み
                } else {        //折り線選択有り
                    foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            } else {                        //折り上がり系図指定
                foldType = FoldType.NOTHING_0;//有り得ない
            }
        } else {                        //折り上がり系図有り
            if (foldedFigureIndex == 0) {                            //展開図指定
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    foldType = FoldType.NOTHING_0;//何もしない
                } else {        //折り線選択有り
                    foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
            } else {                        //折り上がり系図指定
                if (foldLineTotalForSelectFolding == 0) {        //No fold line selection
                    foldType = FoldType.CHANGING_FOLDED_3;//Fold with the specified fold-up genealogy
                } else {        //With fold line selection
                    foldType = FoldType.FOR_SELECTED_LINES_2;//Fold in selected crease pattern
                }
            }
        }

        return foldType;
    }

    public void fold(FoldType foldType, FoldedFigure.EstimationOrder estimationOrder) {
        if (foldType == FoldType.NOTHING_0) {
            System.out.println(" oritatame 20180108");
        } else if ((foldType == FoldType.FOR_ALL_LINES_1) || (foldType == FoldType.FOR_SELECTED_LINES_2)) {
            if (foldType == FoldType.FOR_ALL_LINES_1) {
                mainDrawingWorker.select_all();
            }
            //
            if (applicationModel.getCorrectCpBeforeFolding()) {// Automatically correct strange parts (branch-shaped fold lines, etc.) in the crease pattern
                DrawingWorker drawingWorker2 = new DrawingWorker(this);    // Basic branch craftsman. Accepts input from the mouse.
                drawingWorker2.setSave_for_reading(mainDrawingWorker.foldLineSet.getSaveForSelectFolding());
                drawingWorker2.point_removal();
                drawingWorker2.overlapping_line_removal();
                drawingWorker2.branch_trim(0.000001);
                drawingWorker2.organizeCircles();
                lineSegmentsForFolding = drawingWorker2.getForFolding();
            } else {
                lineSegmentsForFolding = mainDrawingWorker.getForSelectFolding();
            }

            point_of_referencePlane_old.set(OZ.cp_worker1.get_point_of_referencePlane_tv());//20180222折り線選択状態で折り畳み推定をする際、以前に指定されていた基準面を引き継ぐために追加
            //これより前のOZは古いOZ
            folding_prepare();//OAZのアレイリストに、新しく折り上がり図をひとつ追加し、それを操作対象に指定し、foldedFigures(0)共通パラメータを引き継がせる。
            //これより後のOZは新しいOZに変わる

            OZ.estimationOrder = estimationOrder;

            executeTask(new FoldingEstimateTask(this));
        } else if (foldType == FoldType.CHANGING_FOLDED_3) {
            OZ.estimationOrder = estimationOrder;
            OZ.estimationStep = FoldedFigure.EstimationStep.STEP_0;

            executeTask(new FoldingEstimateTask(this));
        }
    }

    void folding_prepare() {//Add one new folding diagram to the foldedFigures array list, specify it as the operation target, and inherit the foldedFigures (0) common parameters.
        System.out.println(" oritatami_jyunbi 20180107");

        addNewFoldedFigure(); //OAZのアレイリストに、新しく折り上がり図をひとつ追加する。

        setFoldedFigureIndex(foldedFigures.size() - 1);//foldedFigureIndex=i;OZ = (Oriagari_Zu)foldedFigures.get(foldedFigureIndex); OZ(各操作の対象となる折上がり図）に、アレイリストに最新に追加された折上がり図を割り当てる)

        FoldedFigure orz = foldedFigures.get(0);//Assign foldedFigures (0) (folded figures that hold common parameters) to orz

        orz.getData(foldedFigureModel);
    }

    public void addNewFoldedFigure() {
        foldedFigures.add(new FoldedFigure_01(bulletinBoard));
    }

    public void twoColorNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>２色塗りわけ展開図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To get 2-Colored crease pattern, select the target range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(this, label);
    }

    public void foldingNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>新たに折り上がり図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To calculate new folded shape, select the target clease lines range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(this, label);
    }

    public void closing() {
        if (!fileModel.isSaved()) {
            int option = JOptionPane.showConfirmDialog(this, createImageIcon("ppp/owari.png"));

            switch (option) {
                case JOptionPane.YES_OPTION:
                    mouseDraggedValid = false;
                    mouseReleasedValid = false;
                    saveFile();

                    stopTask();
                    System.exit(0);
                case JOptionPane.NO_OPTION:
                    stopTask();
                    System.exit(0);
                case JOptionPane.CANCEL_OPTION:
                    break;
            }
        } else {
            stopTask();
            System.exit(0);
        }
    }

    // --------展開図の初期化-----------------------------
    void developmentView_initialization() {
        mainDrawingWorker.reset();
        mainDrawingWorker.initialize();

        //camera_of_orisen_nyuuryokuzu	の設定;
        canvas.creasePatternCamera.setCameraPositionX(0.0);
        canvas.creasePatternCamera.setCameraPositionY(0.0);
        canvas.creasePatternCamera.setCameraAngle(0.0);
        canvas.creasePatternCamera.setCameraMirror(1.0);
        canvas.creasePatternCamera.setCameraZoomX(1.0);
        canvas.creasePatternCamera.setCameraZoomY(1.0);
        canvas.creasePatternCamera.setDisplayPositionX(350.0);
        canvas.creasePatternCamera.setDisplayPositionY(350.0);

        mainDrawingWorker.setCamera(canvas.creasePatternCamera);
        OZ.cp_worker1.setCamera(canvas.creasePatternCamera);

        canvasModel.reset();
        internalDivisionRatioModel.reset();
        foldedFigureModel.reset();

        gridModel.reset();
        angleSystemModel.reset();
        creasePatternCameraModel.reset();
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void Button_shared_operation() {
        mainDrawingWorker.setDrawingStage(0);
        mainDrawingWorker.resetCircleStep();
        mainDrawingWorker.voronoiLineSet.reset();
    }

    public MouseWheelTarget pointInCreasePatternOrFoldedFigure(Point p) {//A function that determines which of the development and folding views the Ten obtained with the mouse points to.
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
        for (int i = 1; i <= foldedFigures.size() - 1; i++) {
            OZi = foldedFigures.get(i);

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

        setFoldedFigureIndex(tempFoldedFigureIndex);

        return temp_i_cp_or_oriagari;
    }

    void setFoldedFigureIndex(int i) {//Processing when OZ is switched
        System.out.println("foldedFigureIndex = " + foldedFigureIndex);
        foldedFigureIndex = i;
        OZ = foldedFigures.get(foldedFigureIndex);

        // Load data from this foldedFigure to the ui.
        OZ.getData(foldedFigureModel);
    }

    public Point e2p(MouseEvent e) {
        double offset = 0.0;
        if (applicationModel.getDisplayPointOffset()) {
            offset = canvas.creasePatternCamera.getCameraZoomX() * mainDrawingWorker.getSelectionDistance();
        }
        return new Point(e.getX() - (int) offset, e.getY() - (int) offset);
    }

    //=============================================================================
    //Method called when the mouse wheel rotates
    //=============================================================================
    public void mouse_object_position(Point p) {//この関数はmouseMoved等と違ってマウスイベントが起きても自動では認識されない
        p_mouse_TV_position.set(p.getX(), p.getY());

        p_mouse_object_position.set(canvas.creasePatternCamera.TV2object(p_mouse_TV_position));
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

    public void drawBackground(Graphics2D g2h, Image imgh) {//引数はカメラ設定、線幅、画面X幅、画面y高さ
        //背景画を、画像の左上はしを、ウィンドウの(0,0)に合わせて回転や拡大なしで表示した場合を基準状態とする。
        //背景画上の点h1を中心としてa倍拡大する。次に、h1を展開図上の点h3と重なるように背景画を平行移動する。
        //この状態の展開図を、h3を中心にb度回転したよう見えるように座標を回転させて貼り付けて、その後、座標の回転を元に戻すという関数。
        //引数は、Graphics2D g2h,Image imgh,Ten h1,Ten h2,Ten h3,Ten h4
        //h2,とh4も重なるようにする

        if (backgroundModel.isLockBackground()) {
            h_cam.setCamera(canvas.creasePatternCamera);
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

    void configure_initialize_prediction() {
        OZ.text_result = "";
        OZ.displayStyle = FoldedFigure.DisplayStyle.NONE_0;//折り上がり図の表示様式の指定。１なら実際に折り紙を折った場合と同じ。２なら透過図
        OZ.display_flg_backup = FoldedFigure.DisplayStyle.NONE_0;//表示様式hyouji_flgの一時的バックアップ用

        //表示用の値を格納する変数
        OZ.ip1_anotherOverlapValid = HierarchyList_Worker.HierarchyListStatus.UNKNOWN_N1;//上下表職人の初期設定時に、折った後の表裏が同じ面が
        //隣接するという誤差があれが0を、無ければ1000を格納する変数。
        //ここでの初期値は(0か1000)以外の数ならなんでもいい。
        OZ.ip2_possibleOverlap = -1;//上下表職人が折り畳み可能な重なり方を探した際に、
        //可能な重なり方がなければ0を、可能な重なり方があれば1000を格納する変数。
        //ここでの初期値は(0か1000)以外の数ならなんでもいい。
        OZ.ip3 = 1;//ts1が折り畳みを行う際の基準面を指定するのに使う。

        //ip4=0;//これは、ts1の最初に裏返しをするかどうかを指定する。0ならしない。1なら裏返す。//20170615 実行しないようにした（折りあがり図の表示状況を変えないようにするため）

        OZ.ip5 = -1;    //上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
        //さらに別の紙の重なりをさがす時の最初のjs.susumu(SubFaceTotal)の結果。
        //0なら新たにsusumu余地がなかった。0以外なら変化したSubFaceのidの最も小さい番号
        OZ.ip6 = -1;    //上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
        //さらに別の紙の重なりをさがす時の js.kanou_kasanari_sagasi()の結果。
        //0なら可能な重なりかたとなる状態は存在しない。
        //1000なら別の重なり方が見つかった。

        foldedFigureModel.setFindAnotherOverlapValid(false);

        OZ.discovered_fold_cases = 0;    //折り重なり方で、何通り発見したかを格納する。

        mouseDraggedValid = false;
        mouseReleasedValid = false;//0は、マウス操作を無視。1はマウス操作有効。ファイルボックスのon-offなどで、予期せぬmouseDraggedやmouseReleasedが発生したとき、それを拾わないように0に設定する。これらは、マウスがクリックされたときに、1有効指定にする。

        OZ.estimated_initialize();
        bulletinBoard.clear();
    }

    void readImageFromFile() {
        FileDialog fd = new FileDialog(this, "Select Image File.", FileDialog.LOAD);
        fd.setVisible(true);
        String img_background_fname = fd.getDirectory() + fd.getFile();
        try {
            if (fd.getFile() != null) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                img_background = tk.getImage(img_background_fname);

                if (img_background != null) {
                    backgroundModel.setDisplayBackground(true);
                    backgroundModel.setLockBackground(false);
                }
            }

        } catch (Exception e) {
        }
    }

    void writeImage() {
        exportFile = selectExportFile();

        if (exportFile == null) {
            return;
        }

        if (exportFile.getName().endsWith(".png") || exportFile.getName().endsWith(".jpg") || exportFile.getName().endsWith(".jpeg") || exportFile.getName().endsWith(".svg")) {
            flg61 = false;
            if ((mouseMode == MouseMode.OPERATION_FRAME_CREATE_61) && (mainDrawingWorker.getDrawingStage() == 4)) {
                flg61 = true;
                mainDrawingWorker.setDrawingStage(0);
            }

            canvas.flg_wi = true;
            repaintCanvas();//Necessary to not export the green border
        } else if (exportFile.getName().endsWith(".cp")) {
            Cp.exportFile(mainDrawingWorker.getSave_for_export(), exportFile);
        } else if (exportFile.getName().endsWith(".orh")) {
            Orh.exportFile(mainDrawingWorker.getSave_for_export_with_applicationModel(), exportFile);
        }
    }

    File selectOpenFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Open");

        fileChooser.setFileFilter(new FileNameExtensionFilter("Origami Editor (*.ori)", "ori"));

        fileChooser.showOpenDialog(this);

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile != null) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
            fileModel.setSavedFileName(selectedFile.getAbsolutePath());
            fileModel.setSaved(true);
            historyStateModel.reset();
        }

        return selectedFile;
    }

    File selectSaveFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Save As");

        fileChooser.setFileFilter(new FileNameExtensionFilter("Origami Editor (*.ori)", "ori"));
        fileChooser.setSelectedFile(new File("untitled.ori"));

        File selectedFile;
        int choice = JOptionPane.NO_OPTION;
        do {
            int saveChoice = fileChooser.showSaveDialog(this);

            if (saveChoice != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            selectedFile = fileChooser.getSelectedFile();

            if (selectedFile != null && !selectedFile.getName().endsWith(".ori")) {
                selectedFile = new File(selectedFile.getPath() + ".ori");
            }

            if (selectedFile != null && selectedFile.exists()) {
                choice = JOptionPane.showConfirmDialog(this, "<html>File already exists.<br/>Do you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        } while (selectedFile != null && selectedFile.exists() && choice != JOptionPane.YES_OPTION);

        if (selectedFile != null) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
            fileModel.setSavedFileName(selectedFile.getAbsolutePath());
            fileModel.setSaved(true);
        }

        return selectedFile;
    }

    File selectImportFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Import");

        fileChooser.setFileFilter(new FileNameExtensionFilter("All supported files", "cp", "orh", "ori"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CP / ORIPA (*.cp)", "cp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Orihime (*.orh)", "orh"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Origami Editor (*.ori)", "ori"));

        fileChooser.showOpenDialog(this);

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile != null) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
        }

        return selectedFile;
    }

    public File selectExportFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Export");

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.png)", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.jpg)", "jpg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.svg)", "svg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CP / ORIPA (*.cp)", "cp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Orihime (*.orh)", "orh"));
        fileChooser.setSelectedFile(new File("creasepattern.png"));

        File selectedFile;
        int choice = JOptionPane.NO_OPTION;
        do {
            int saveChoice = fileChooser.showSaveDialog(this);

            if (saveChoice != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists()) {
                choice = JOptionPane.showConfirmDialog(this, "<html>File already exists.<br/>Do you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        } while (selectedFile != null && selectedFile.exists() && choice == JOptionPane.NO_OPTION);

        if (selectedFile != null) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
        }

        return selectedFile;
    }

    Save readImportFile(File file) {
        if (file == null) {
            return null;
        }

        if (!file.exists()) {
            return null;
        }

        Save save = null;

        try {
            if (file.getName().endsWith(".ori")) {
                try {
                    ObjectMapper mapper = new DefaultObjectMapper();
                    return mapper.readValue(file, Save.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (file.getName().endsWith(".obj")) {
                save = Obj.importFile(file);
            }

            if (file.getName().endsWith(".cp")) {
                save = Cp.importFile(file);
            }

            if (file.getName().endsWith(".orh")) {
                save = Orh.importFile(file);
            }

        } catch (IOException e) {
            System.out.println(e);

            JOptionPane.showMessageDialog(this, "Opening of the saved file failed", "Opening failed", JOptionPane.ERROR_MESSAGE);

            fileModel.setSavedFileName(null);

            return new Save();
        }

        return save;
    }

    void saveFile() {
        if (fileModel.getSavedFileName() == null) {
            saveAsFile();

            return;
        }

        File file = new File(fileModel.getSavedFileName());

        Save save = mainDrawingWorker.getSave_for_export();
        save.setVersion(ResourceUtil.getVersionFromManifest());

        saveAndName2File(save, file);

        fileModel.setSaved(true);
    }

    void saveAsFile() {
        File file = selectSaveFile();

        if (file == null) {
            return;
        }

        Save save = mainDrawingWorker.getSave_for_export();
        save.setVersion(ResourceUtil.getVersionFromManifest());

        saveAndName2File(save, file);

        fileModel.setSaved(true);
    }

    void saveAndName2File(Save save, File fname) {
        try {
            ObjectMapper mapper = new DefaultObjectMapper();

            mapper.writeValue(fname, save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void folding_estimated() throws InterruptedException {
        OZ.folding_estimated(canvas.creasePatternCamera, lineSegmentsForFolding, point_of_referencePlane_old);
    }

    public void createTwoColorCreasePattern() throws InterruptedException {//Two-color crease pattern
        OZ.createTwoColorCreasePattern(canvas.creasePatternCamera, lineSegmentsForFolding, point_of_referencePlane_old);
    }

    public void stopTask() {
        if (currentTask != null && !currentTask.isDone()) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                    pool.shutdownNow();

                    if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                        System.err.println("Pool did not terminate!");
                    }
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }

            pool = Executors.newFixedThreadPool(1);
        }
    }

    void executeTask(Runnable runnable) {
        stopTask();

        currentTask = pool.submit(runnable);
    }

    public double string2double(String str0, double default_if_error) {
        String new_str0 = str0.trim();
        if (new_str0.equals("L1")) {
            str0 = String.valueOf(measuresModel.getMeasuredLength1());
        }
        if (new_str0.equals("L2")) {
            str0 = String.valueOf(measuresModel.getMeasuredLength2());
        }
        if (new_str0.equals("A1")) {
            str0 = String.valueOf(measuresModel.getMeasuredAngle1());
        }
        if (new_str0.equals("A2")) {
            str0 = String.valueOf(measuresModel.getMeasuredAngle2());
        }
        if (new_str0.equals("A3")) {
            str0 = String.valueOf(measuresModel.getMeasuredAngle3());
        }

        return StringOp.String2double(str0, default_if_error);
    }

    public void check4(double r) {
        d_ap_check4 = r;

        executeTask(new CheckCAMVTask(this));
    }

    public void openFile() {
        System.out.println("readFile2Memo() 開始");

        if (!fileModel.isSaved()) {
            int choice = JOptionPane.showConfirmDialog(this, "<html>Current file not saved.<br/>Do you want to save it?", "File not saved", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (choice == JOptionPane.CLOSED_OPTION || choice == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        File file = selectOpenFile();

        if (file == null) {
            return;
        }

        fileModel.setSaved(true);
        fileModel.setSavedFileName(file.getAbsolutePath());
        applicationModel.setDefaultDirectory(file.getParent());

        Save memo_temp = readImportFile(file);
        System.out.println("readFile2Memo() 終了");

        if (memo_temp != null) {
            //Initialization of development drawing started
            developmentView_initialization();
            //Deployment parameter initialization

            //Initialization of folding prediction map started
            OZ = temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
            foldedFigures.clear();
            addNewFoldedFigure();
            setFoldedFigureIndex(0);
            configure_initialize_prediction();

            mainDrawingWorker.setCamera(canvas.creasePatternCamera);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
            mainDrawingWorker.setSave_for_reading(memo_temp);
            mainDrawingWorker.record();
        }
    }

    public void importFile() {
        System.out.println("readFile2Memo() 開始");
        File importFile = selectImportFile();
        Save memo_temp = readImportFile(importFile);
        System.out.println("readFile2Memo() 終了");

        if (memo_temp != null) {
            fileModel.setSavedFileName(null);

            //Initialization of development drawing started
            developmentView_initialization();
            //Deployment parameter initialization

            //Initialization of folding prediction map started
            OZ = temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
            foldedFigures.clear();
            addNewFoldedFigure();
            setFoldedFigureIndex(0);
            configure_initialize_prediction();

            mainDrawingWorker.setCamera(canvas.creasePatternCamera);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
            mainDrawingWorker.setSave_for_reading(memo_temp);
            mainDrawingWorker.record();
        }
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
        Rectangle canvasBounds = canvas.getBounds();

        java.awt.Point canvasLocation = canvas.getLocationOnScreen();
        Rectangle bounds = new Rectangle(canvasLocation.x, canvasLocation.y, canvasBounds.width, canvasBounds.height);

        java.awt.Point currentLocation = getLocation();
        Dimension size = getSize();

        // Move all associated windows outside the bounds.
        Window[] windows = getOwnedWindows();
        java.util.Queue<java.awt.Point> locations = new LinkedList<>();
        setLocation(currentLocation.x, currentLocation.y + size.height);
        for (Window w : windows) {
            java.awt.Point loc = w.getLocation();
            locations.offer(loc);
            w.setLocation(loc.x, loc.y + size.height);
        }

        img_background = robot.createScreenCapture(bounds);

        // Move all associated windows back.
        setLocation(currentLocation);
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
        backgroundModel.setDisplayBackground(true);

        if (backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
            h_cam.setLocked(true);
            h_cam.setCamera(canvas.creasePatternCamera);
            h_cam.h3_obj_and_h4_obj_calculation();
        }

        repaintCanvas();
    }

    public void registerButton(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String tooltip = ResourceUtil.getBundleString("tooltip", key);
        String help = ResourceUtil.getBundleString("help", key);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);

        if (!StringOp.isEmpty(keyStrokeString) && keyStroke == null) {
            System.err.println("Keystroke for \"" + key + "\": \"" + keyStrokeString + "\" is invalid");
        }

        String tooltipText = "<html>";
        if (!StringOp.isEmpty(name)) {
            tooltipText += "<i>" + name + "</i><br/>";
        }
        if (!StringOp.isEmpty(tooltip)) {
            tooltipText += tooltip + "<br/>";
        }
        if (keyStroke != null) {
            tooltipText += "Hotkey: " + keyStrokeString + "<br/>";
        }

        if (!tooltipText.equals("<html>")) {
            button.setToolTipText(tooltipText);
        }

        if (button instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) button;

            if (!StringOp.isEmpty(name)) {
                int mnemonicIndex = name.indexOf('_');
                if (mnemonicIndex > -1) {
                    String formattedName = name.replaceAll("_", "");

                    menuItem.setText(formattedName);
                    menuItem.setMnemonic(formattedName.charAt(mnemonicIndex));
                    menuItem.setDisplayedMnemonicIndex(mnemonicIndex);
                } else {
                    menuItem.setText(name);
                }
            }

            if (keyStroke != null) {
                // Menu item can handle own accelerator (and shows a nice hint).
                menuItem.setAccelerator(keyStroke);
            }
        } else if (keyStroke != null) {
            helpInputMap.put(keyStroke, button);
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
            button.getActionMap().put(key, new Click(button));
        }

        if (!StringOp.isEmpty(help)) {
            button.addActionListener(e -> {
                explanation.setExplanation(key);

                Button_shared_operation();
            });
        }
    }

    public boolean isTaskRunning() {
        return currentTask != null && !currentTask.isDone();
    }

    public enum MouseWheelTarget {
        CREASE_PATTERN_0,
        FOLDED_FRONT_1,
        FOLDED_BACK_2,
        TRANSPARENT_FRONT_3,
        TRANSPARENT_BACK_4,
    }

    public enum FoldType {
        NOTHING_0,
        FOR_ALL_LINES_1,
        FOR_SELECTED_LINES_2,
        CHANGING_FOLDED_3,
    }
}
