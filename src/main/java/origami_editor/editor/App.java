package origami_editor.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import origami.crease_pattern.FoldingException;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.element.Point;
import origami_editor.editor.action.Click;
import origami_editor.editor.canvas.*;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.drawing.FoldedFigure_Worker_Drawer;
import origami.folding.FoldedFigure;
import origami_editor.editor.folded_figure.FoldedFigure_01;
import origami_editor.editor.json.DefaultObjectMapper;
import origami_editor.editor.service.FileSaveService;
import origami_editor.editor.task.FoldingEstimateTask;
import origami_editor.editor.task.TaskExecutor;
import origami_editor.tools.KeyStrokeUtil;
import origami_editor.tools.ResourceUtil;
import origami_editor.tools.StringOp;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static origami_editor.tools.ResourceUtil.getAppDir;

public class App {
    public static final String CONFIG_JSON = "config.json";
    public final ApplicationModel applicationModel;
    public final GridModel gridModel;
    public final CanvasModel canvasModel;
    public final FoldedFigureModel foldedFigureModel;
    public final AngleSystemModel angleSystemModel;
    public final MeasuresModel measuresModel;
    public final InternalDivisionRatioModel internalDivisionRatioModel;
    public final HistoryStateModel historyStateModel;
    public final BackgroundModel backgroundModel;
    public final CameraModel creasePatternCameraModel;
    public final FileModel fileModel;
    public final DefaultComboBoxModel<FoldedFigure_Drawer> foldedFiguresList;
    public final AtomicBoolean w_image_running = new AtomicBoolean(false); // Folding together execution. If a single image export is in progress, it will be true.
    public final CreasePattern_Worker mainCreasePatternWorker;    // Basic branch craftsman. Accepts input from the mouse.
    final Queue<Popup> popups = new ArrayDeque<>();
    private final MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate = new MouseHandlerVoronoiCreate();
    public  final FileSaveService fileSaveService;
    public LineSegmentSet lineSegmentsForFolding;//折畳み予測の最初に、ts1.Senbunsyuugou2Tensyuugou(lineSegmentsForFolding)として使う。　Ss0は、mainDrawingWorker.get_for_oritatami()かes1.get_for_select_oritatami()で得る。
    public BulletinBoard bulletinBoard = new BulletinBoard();
    // ------------------------------------------------------------------------
    // Buffer screen settings VVVVVVVVVVVVVVVVVVVVVVVVV
    public Canvas canvas;
    //各種変数の定義
    String frame_title_0;//フレームのタイトルの根本部分
    String frame_title;//フレームのタイトルの全体
    HelpDialog explanation;
    //画像出力するため20170107_oldと書かれた行をコメントアウトし、20170107_newの行を有効にした。
    //画像出力不要で元にもどすなら、20170107_oldと書かれた行を有効にし、20170107_newの行をコメントアウトにすればよい。（この変更はOrihime.javaの中だけに2箇所ある）
    // オフスクリーン
    Map<KeyStroke, AbstractButton> helpInputMap = new HashMap<>();
    JFrame frame;

    public App() {
        applicationModel = new ApplicationModel();
        gridModel = new GridModel();
        canvasModel = new CanvasModel();
        foldedFigureModel = new FoldedFigureModel();
        angleSystemModel = new AngleSystemModel();
        measuresModel = new MeasuresModel();
        internalDivisionRatioModel = new InternalDivisionRatioModel();
        historyStateModel = new HistoryStateModel();
        backgroundModel = new BackgroundModel();
        creasePatternCameraModel = new CameraModel();
        fileModel = new FileModel();
        mainCreasePatternWorker = new CreasePattern_Worker(this);
        foldedFiguresList = new DefaultComboBoxModel<>();

        canvas = new Canvas(this);

        fileSaveService = new FileSaveService(canvas, mainCreasePatternWorker, fileModel, applicationModel, historyStateModel, canvasModel, internalDivisionRatioModel, foldedFigureModel, gridModel, angleSystemModel, creasePatternCameraModel, foldedFiguresList);
    }

    public void start() {
        frame = new JFrame();
        fileSaveService.setOwner(frame);

        frame.setTitle("Origami Editor " + ResourceUtil.getVersionFromManifest());//Specify the title and execute the constructor
        frame_title_0 = frame.getTitle();
        frame_title = frame_title_0;//Store title in variable
        mainCreasePatternWorker.setTitle(frame_title);

        final ConsoleDialog consoleDialog;

        if (System.console() == null) {
            consoleDialog = new ConsoleDialog();
        } else {
            consoleDialog = null;
        }

        //--------------------------------------------------------------------------------------------------
        frame.addWindowListener(new WindowAdapter() {//ウィンドウの状態が変化したときの処理
            //終了ボタンを有効化
            public void windowClosing(WindowEvent evt) {
                closing();//Work to be done when pressing X at the right end of the upper side of the window
            }//終了ボタンを有効化 ここまで。
        });//Processing when the window state changes Up to here.

        frame.addWindowStateListener(new WindowAdapter() {
            public void windowStateChanged(WindowEvent eve) {
                applicationModel.setWindowState(eve.getNewState());
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                // Only update when not maximized.
                if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
                    applicationModel.setWindowPosition(frame.getLocation());
                }
            }

            @Override
            public void componentResized(ComponentEvent e) {
                if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
                    applicationModel.setWindowSize(frame.getSize());
                }
            }
        });

        //--------------------------------------------------------------------------------------------------
        frame.addWindowFocusListener(new WindowAdapter() {//オリヒメのメインウィンドウのフォーカスが変化したときの処理
            public void windowLostFocus(WindowEvent evt) {
                Popup popup;
                while ((popup = popups.poll()) != null) {
                    popup.hide();
                }
            }
        });//オリヒメのメインウィンドウのフォーカスが変化したときの処理 ここまで。

        frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(true);
        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                Popup popup;
                while ((popup = popups.poll()) != null) {
                    popup.hide();
                }

                canvasModel.setToggleLineColor(false);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && !canvasModel.getToggleLineColor()) {
                    canvasModel.setToggleLineColor(true);
                }

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

        foldedFiguresList.removeAllElements();

        Editor editor = new Editor(this, canvas);

        bulletinBoard.addChangeListener(e -> frame.repaint());

        canvas.creasePatternCamera.setCameraPositionX(0.0);
        canvas.creasePatternCamera.setCameraPositionY(0.0);
        canvas.creasePatternCamera.setCameraAngle(0.0);
        canvas.creasePatternCamera.setCameraMirror(1.0);
        canvas.creasePatternCamera.setCameraZoomX(1.0);
        canvas.creasePatternCamera.setCameraZoomY(1.0);
        canvas.creasePatternCamera.setDisplayPositionX(350.0);
        canvas.creasePatternCamera.setDisplayPositionY(350.0);

        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("fishbase.png")));
        frame.setContentPane(editor.$$$getRootComponent$$$());

        TopPanel topPanel = editor.getTopPanel();
        RightPanel rightPanel = editor.getRightPanel();
        BottomPanel bottomPanel = editor.getBottomPanel();
        LeftPanel leftPanel = editor.getLeftPanel();

        AppMenuBar appMenuBar = new AppMenuBar(this, applicationModel, fileSaveService);

        frame.setJMenuBar(appMenuBar);

        leftPanel.getData(gridModel);

        applicationModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(e, applicationModel));
        applicationModel.addPropertyChangeListener(e -> canvas.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> appMenuBar.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> topPanel.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> rightPanel.setData(applicationModel));
        applicationModel.addPropertyChangeListener(e -> leftPanel.setData(e, applicationModel));

        applicationModel.addPropertyChangeListener(e -> persistApplicationModel());

        applicationModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("laf")) {
                applyLookAndFeel(applicationModel.getLaf());
            }
        });

        applicationModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(applicationModel));

        applicationModel.addPropertyChangeListener(e -> {
            for (int i = 0; i < foldedFiguresList.getSize(); i++) {
                FoldedFigure_Drawer item = foldedFiguresList.getElementAt(i);
                item.setData(applicationModel);
            }
            FoldedFigure_Worker_Drawer.setStaticData(applicationModel);
        });

        applicationModel.reload();

        gridModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setGridConfigurationData(gridModel));
        gridModel.addPropertyChangeListener(e -> leftPanel.setData(gridModel));
        gridModel.addPropertyChangeListener(e -> repaintCanvas());

        angleSystemModel.addPropertyChangeListener(e -> rightPanel.setData(angleSystemModel));
        angleSystemModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(angleSystemModel));
        angleSystemModel.addPropertyChangeListener(e -> repaintCanvas());

        measuresModel.addPropertyChangeListener(e -> rightPanel.setData(measuresModel));

        internalDivisionRatioModel.addPropertyChangeListener(e -> topPanel.setData(internalDivisionRatioModel));
        internalDivisionRatioModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(internalDivisionRatioModel));

        foldedFiguresList.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {

            }

            @Override
            public void intervalRemoved(ListDataEvent e) {

            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                canvas.repaint();
            }
        });

        foldedFigureModel.addPropertyChangeListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.setData(foldedFigureModel);
            }
        });
        foldedFigureModel.addPropertyChangeListener(e -> bottomPanel.setData(foldedFigureModel));
        foldedFigureModel.addPropertyChangeListener(e -> repaintCanvas());
        foldedFigureModel.addPropertyChangeListener(e -> leftPanel.setData(foldedFigureModel));

        canvasModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(canvasModel));
        canvasModel.addPropertyChangeListener(e -> canvas.setData(canvasModel));
        canvasModel.addPropertyChangeListener(e -> topPanel.setData(e, canvasModel));
        canvasModel.addPropertyChangeListener(e -> rightPanel.setData(e, canvasModel));
        canvasModel.addPropertyChangeListener(e -> leftPanel.setData(e, canvasModel));
        canvasModel.addPropertyChangeListener(e -> bottomPanel.setData(e, canvasModel));

        canvasModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
                CanvasModel canvasModel = (CanvasModel) e.getSource();
                System.out.println("mouseMode = " + canvasModel.getMouseMode().toReadableString());
            }
        });

        historyStateModel.addPropertyChangeListener(e -> rightPanel.setData(historyStateModel));
        historyStateModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(historyStateModel));

        backgroundModel.addPropertyChangeListener(e -> topPanel.setData(backgroundModel));
        backgroundModel.addPropertyChangeListener(e -> canvas.setData(backgroundModel));

        creasePatternCameraModel.addPropertyChangeListener(e -> canvas.creasePatternCamera.setData(creasePatternCameraModel));
        creasePatternCameraModel.addPropertyChangeListener(e -> topPanel.setData(creasePatternCameraModel));
        creasePatternCameraModel.addPropertyChangeListener(e -> repaintCanvas());

        fileModel.addPropertyChangeListener(e -> setData(fileModel));

        fileModel.reset();

        fileSaveService.developmentView_initialization();

        Button_shared_operation();

        mainCreasePatternWorker.setCamera(canvas.creasePatternCamera);

        mainCreasePatternWorker.record();
        mainCreasePatternWorker.auxRecord();

        canvas.addMouseModeHandler(MouseHandlerDrawCreaseFree.class);
        canvas.addMouseModeHandler(MouseHandlerLineSegmentDelete.class);
        canvas.addMouseModeHandler(MouseHandlerSquareBisector.class);
        canvas.addMouseModeHandler(MouseHandlerFoldableLineDraw.class);
        canvas.addMouseModeHandler(MouseHandlerVertexMakeAngularlyFlatFoldable.class);
        mouseHandlerVoronoiCreate.setDrawingWorker(mainCreasePatternWorker);
        canvas.addMouseModeHandler(mouseHandlerVoronoiCreate);
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
        canvas.addMouseModeHandler(new MouseHandlerChangeStandardFace(this, mainCreasePatternWorker));

        updateButtonIcons(frame);
        frame.pack();

        frame.setMinimumSize(frame.getSize());


        if (applicationModel.getWindowPosition() != null) {
            frame.setLocation(applicationModel.getWindowPosition());
        } else {
            frame.setLocationRelativeTo(null);
        }
        if (applicationModel.getWindowSize() != null) {
            frame.setSize(applicationModel.getWindowSize());
        }

        frame.setExtendedState(applicationModel.getWindowState());
        frame.setVisible(true);


        explanation = new HelpDialog(frame, applicationModel::setHelpVisible, canvas.getLocationOnScreen(), canvas.getSize());
        explanation.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                applicationModel.setHelpVisible(false);
            }
        });

        if (consoleDialog != null) {
            consoleDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    applicationModel.setConsoleVisible(false);
                }
            });
            applicationModel.addPropertyChangeListener(e -> {
                if (e.getPropertyName() == null || e.getPropertyName().equals("consoleVisible")) {
                    consoleDialog.setVisible(applicationModel.getConsoleVisible());
                }
                frame.requestFocus();
            });
            consoleDialog.setVisible(applicationModel.getConsoleVisible());
        }
        applicationModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("helpVisible")) {
                explanation.setVisible(applicationModel.getHelpVisible());
            }
            frame.requestFocus();
        });
        explanation.setVisible(applicationModel.getHelpVisible());
        //focus back to here after creating dialog
        frame.requestFocus();
    }

    private void updateButtonIcons(Container container) {
        boolean isDark = FlatLaf.isLafDark();
        for (Component c : container.getComponents()) {
            if (c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                if (button.getIcon() instanceof ImageIcon) {
                    button.setIcon(determineIcon(isDark, (ImageIcon) button.getIcon()));
                }
            } else if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getIcon() instanceof ImageIcon) {
                    label.setIcon(determineIcon(isDark, (ImageIcon) label.getIcon()));
                }
            } else if (c instanceof Container) {
                updateButtonIcons((Container) c);
            }
        }
    }

    private ImageIcon determineIcon(boolean isDark, ImageIcon icon) {
        // TODO this works because the description is the filename of the image, this should be based on the name of the action.
        String uri = icon.getDescription();

        if (isDark) {
            uri = uri.replaceAll(".*ppp", "ppp_dark");
        } else {
            uri = uri.replaceAll(".*ppp_dark", "ppp");
        }

        URL resource = App.class.getClassLoader().getResource(uri);

        if (resource != null) {
            return new ImageIcon(resource);
        }

        return icon;
    }

    private static void updateUI2() {
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Component permanentFocusOwner = keyboardFocusManager.getPermanentFocusOwner();
        JSpinner spinner = (permanentFocusOwner != null)
                ? (JSpinner) SwingUtilities.getAncestorOfClass(JSpinner.class, permanentFocusOwner)
                : null;

        FlatLaf.updateUI();

        if (spinner != null && keyboardFocusManager.getPermanentFocusOwner() == null) {
            JComponent editor = spinner.getEditor();
            JTextField textField = (editor instanceof JSpinner.DefaultEditor)
                    ? ((JSpinner.DefaultEditor) editor).getTextField()
                    : null;
            if (textField != null)
                textField.requestFocusInWindow();
        }
    }

    public void restoreApplicationModel() {
        Path storage = getAppDir();
        File configFile = storage.resolve(CONFIG_JSON).toFile();

        if (!configFile.exists()) {
            applicationModel.reset();

            return;
        }

        ObjectMapper mapper = new DefaultObjectMapper();

        try {
            ApplicationModel loadedApplicationModel = mapper.readValue(configFile, ApplicationModel.class);

            applicationModel.set(loadedApplicationModel);
        } catch (IOException e) {
            // An application state is found, but it is not valid.
            JOptionPane.showMessageDialog(frame, "<html>Failed to load application state.<br/>Loading default application configuration.", "State load failed", JOptionPane.WARNING_MESSAGE);

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

        frame.setTitle(frame_title);
        mainCreasePatternWorker.setTitle(frame_title);
    }

    public void repaintCanvas() {
        canvas.repaint();
    }

    public FoldType getFoldType() {
        FoldType foldType;//= 0 Do nothing, = 1 Folding estimation for all fold lines in the normal development view, = 2 for fold estimation for selected fold lines, = 3 for changing the folding state
        int foldLineTotalForSelectFolding = mainCreasePatternWorker.getFoldLineTotalForSelectFolding();
        System.out.println("foldedFigures.size() = " + foldedFiguresList.getSize() + "    : foldedFigureIndex = " + foldedFiguresList.getIndexOf(foldedFiguresList.getSelectedItem()) + "    : mainDrawingWorker.get_orisensuu_for_select_oritatami() = " + foldLineTotalForSelectFolding);
        if (foldedFiguresList.getSize() == 0) {                        //折り上がり系図無し
                if (foldLineTotalForSelectFolding == 0) {        //折り線選択無し
                    foldType = FoldType.FOR_ALL_LINES_1;//全展開図で折畳み
                } else {        //折り線選択有り
                    foldType = FoldType.FOR_SELECTED_LINES_2;//選択された展開図で折畳み
                }
        } else {                        //折り上がり系図有り
            if (foldedFiguresList.getSelectedItem() == null) {                            //展開図指定
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
                //mainCreasePatternWorker.select_all();
                Point cpPivot = this.mainCreasePatternWorker.getCameraPosition();
                mainCreasePatternWorker.selectConnected(this.mainCreasePatternWorker.foldLineSet.closestPoint(cpPivot));
            }
            //
            if (applicationModel.getCorrectCpBeforeFolding()) {// Automatically correct strange parts (branch-shaped fold lines, etc.) in the crease pattern
                CreasePattern_Worker creasePatternWorker2 = new CreasePattern_Worker(this);    // Basic branch craftsman. Accepts input from the mouse.
                Save save = new Save();
                mainCreasePatternWorker.foldLineSet.getSaveForSelectFolding(save);
                creasePatternWorker2.setSave_for_reading(save);
                creasePatternWorker2.point_removal();
                creasePatternWorker2.overlapping_line_removal();
                creasePatternWorker2.branch_trim();
                creasePatternWorker2.organizeCircles();
                lineSegmentsForFolding = creasePatternWorker2.getForFolding();
            } else {
                lineSegmentsForFolding = mainCreasePatternWorker.getForSelectFolding();
            }

            //これより前のOZは古いOZ
            FoldedFigure_Drawer selectedFigure = folding_prepare();//OAZのアレイリストに、新しく折り上がり図をひとつ追加し、それを操作対象に指定し、foldedFigures(0)共通パラメータを引き継がせる。
            //これより後のOZは新しいOZに変わる

            TaskExecutor.executeTask("Folding Estimate", new FoldingEstimateTask(this, selectedFigure, estimationOrder));
        } else if (foldType == FoldType.CHANGING_FOLDED_3) {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.foldedFigure.estimationOrder = estimationOrder;
                selectedFigure.foldedFigure.estimationStep = FoldedFigure.EstimationStep.STEP_0;

                TaskExecutor.executeTask("Folding Estimate",new FoldingEstimateTask(this, selectedFigure, estimationOrder));
            }
        }
    }

    public FoldedFigure_Drawer folding_prepare() {//Add one new folding diagram to the foldedFigures array list, specify it as the operation target, and inherit the foldedFigures (0) common parameters.
        System.out.println(" oritatami_jyunbi 20180107");

        FoldedFigure_Drawer newFoldedFigure = new FoldedFigure_Drawer(new FoldedFigure_01(bulletinBoard));

        foldedFiguresList.addElement(newFoldedFigure);
        foldedFiguresList.setSelectedItem(newFoldedFigure);

        newFoldedFigure.getData(foldedFigureModel);

        return newFoldedFigure;
    }

    public void twoColorNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>２色塗りわけ展開図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To get 2-Colored crease pattern, select the target range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(frame, label);
    }

    public void foldingNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>新たに折り上がり図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To calculate new folded shape, select the target clease lines range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(frame, label);
    }

    public void closing() {
        if (!fileModel.isSaved()) {
            int option = JOptionPane.showConfirmDialog(frame, "Save crease pattern before exiting?", "Save", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            switch (option) {
                case JOptionPane.YES_OPTION:
                    canvas.mouseDraggedValid = false;
                    canvas.mouseReleasedValid = false;
                    fileSaveService.saveFile();

                    TaskExecutor.stopTask();
                    System.exit(0);
                case JOptionPane.NO_OPTION:
                    TaskExecutor.stopTask();
                    System.exit(0);
                case JOptionPane.CANCEL_OPTION:
                    break;
            }
        } else {
            TaskExecutor.stopTask();
            System.exit(0);
        }
    }

    public void Button_shared_operation() {
        mainCreasePatternWorker.setDrawingStage(0);
        mainCreasePatternWorker.resetCircleStep();
        mouseHandlerVoronoiCreate.voronoiLineSet.clear();
        canvas.mouseReleasedValid = false;
        canvas.mouseDraggedValid = false;
    }

    void setFoldedFigureIndex(int i) {//Processing when OZ is switched
        System.out.println("foldedFigureIndex = " + i);

        FoldedFigure_Drawer newSelectedItem = foldedFiguresList.getElementAt(i);
        foldedFiguresList.setSelectedItem(newSelectedItem);

        // Load data from this foldedFigure to the ui.
        newSelectedItem.getData(foldedFigureModel);
    }

    public Point e2p(MouseEvent e) {
        double offset = 0.0;
        if (applicationModel.getDisplayPointOffset()) {
            offset = canvas.creasePatternCamera.getCameraZoomX() * mainCreasePatternWorker.getSelectionDistance();
        }
        return new Point(e.getX() - (int) offset, e.getY() - (int) offset);
    }

    void readBackgroundImageFromFile() {
        FileDialog fd = new FileDialog(frame, "Select Image File.", FileDialog.LOAD);
        fd.setVisible(true);
        String img_background_fname = fd.getDirectory() + fd.getFile();
        try {
            if (fd.getFile() != null) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Image img_background = tk.getImage(img_background_fname);

                if (img_background != null) {
                    backgroundModel.setBackgroundImage(img_background);
                    backgroundModel.setDisplayBackground(true);
                    backgroundModel.setLockBackground(false);
                }
            }

        } catch (Exception e) {
        }
    }

    public void folding_estimated(FoldedFigure_Drawer selectedFigure) throws InterruptedException, FoldingException {
        selectedFigure.folding_estimated(canvas.creasePatternCamera, lineSegmentsForFolding);
    }

    public void setTooltip(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String tooltip = ResourceUtil.getBundleString("tooltip", key);
        String help = ResourceUtil.getBundleString("help", key);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);


        String tooltipText = "<html>";
        if (!StringOp.isEmpty(name)) {
            tooltipText += "<i>" + name + "</i><br/>";
        }
        if (!StringOp.isEmpty(tooltip)) {
            tooltipText += tooltip + "<br/>";
        }
        if (keyStroke != null) {
            tooltipText += "Hotkey: " + KeyStrokeUtil.toString(keyStroke) + "<br/>";
        }

        if (!tooltipText.equals("<html>")) {
            button.setToolTipText(tooltipText);
        }
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

        setTooltip(button, key);

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
        } else {
            KeyStrokeUtil.resetButton(button);

            addContextMenu(button, key, keyStroke);

            if (keyStroke != null) {
                helpInputMap.put(keyStroke, button);
                button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
            }
            button.getActionMap().put(key, new Click(button));
        }

        if (!StringOp.isEmpty(help)) {
            button.addActionListener(e -> {
                explanation.setExplanation(key);

                Button_shared_operation();
            });
        }
    }

    private void addContextMenu(AbstractButton button, String key, KeyStroke keyStroke) {
        JPopupMenu popup = new JPopupMenu();
        Action addKeybindAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyStroke currentKeyStroke = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys() != null && button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys().length > 0
                        ? button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys()[0]
                        : null;

                new SelectKeyStrokeDialog(frame, button, helpInputMap, currentKeyStroke, newKeyStroke -> {
                    if (newKeyStroke != null && helpInputMap.containsKey(newKeyStroke) && helpInputMap.get(newKeyStroke) != button) {
                        String conflictingButton = (String) helpInputMap.get(newKeyStroke).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(newKeyStroke);
                        JOptionPane.showMessageDialog(frame, "Conflicting KeyStroke! Conflicting with " + conflictingButton);
                        return false;
                    }

                    ResourceUtil.updateBundleKey("hotkey", key, newKeyStroke == null ? null : newKeyStroke.toString());

                    helpInputMap.remove(currentKeyStroke);
                    button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(currentKeyStroke);

                    if (newKeyStroke != null) {
                        helpInputMap.put(newKeyStroke, button);
                        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(newKeyStroke, key);
                        putValue(Action.NAME, "Change key stroke (Current: " + KeyStrokeUtil.toString(newKeyStroke) + ")");
                    } else {
                        putValue(Action.NAME, "Change key stroke");
                    }

                    setTooltip(button, key);

                    return true;
                });
            }
        };
        String actionName = "Change key stroke";
        if (keyStroke != null) {
            actionName += " (Current: " + KeyStrokeUtil.toString(keyStroke) + ")";
        }
        addKeybindAction.putValue(Action.NAME, actionName);
        popup.add(addKeybindAction);

        java.awt.Point point = new java.awt.Point();

        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();

                maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });
    }


    private void applyLookAndFeel(String lafClassName) {
        EventQueue.invokeLater(() -> {
            try {
                // clear custom default font before switching to other LaF
                Font defaultFont = null;
                if (UIManager.getLookAndFeel() instanceof FlatLaf) {
                    Font font = UIManager.getFont("defaultFont");
                    if (font != UIManager.getLookAndFeelDefaults().getFont("defaultFont"))
                        defaultFont = font;
                }
                UIManager.put("defaultFont", null);

                // change look and feel
                UIManager.setLookAndFeel(lafClassName);

                // restore custom default font when switched to other FlatLaf LaF
                if (defaultFont != null && UIManager.getLookAndFeel() instanceof FlatLaf)
                    UIManager.put("defaultFont", defaultFont);

                // update all components
                updateUI2();

                updateButtonIcons(frame);

                if (frame.getExtendedState() == Frame.NORMAL) {
                    // increase size of frame if necessary
                    int width = frame.getWidth();
                    int height = frame.getHeight();
                    Dimension prefSize = frame.getPreferredSize();
                    if (prefSize.width > width || prefSize.height > height)
                        frame.setSize(Math.max(prefSize.width, width), Math.max(prefSize.height, height));

                    // limit frame size to screen size
                    Rectangle screenBounds = frame.getGraphicsConfiguration().getBounds();
                    screenBounds = FlatUIUtils.subtractInsets(screenBounds, frame.getToolkit().getScreenInsets(frame.getGraphicsConfiguration()));
                    Dimension frameSize = frame.getSize();
                    if (frameSize.width > screenBounds.width || frameSize.height > screenBounds.height)
                        frame.setSize(Math.min(frameSize.width, screenBounds.width), Math.min(frameSize.height, screenBounds.height));

                    // move frame to left/top if necessary
                    if (frame.getX() + frame.getWidth() > screenBounds.x + screenBounds.width ||
                            frame.getY() + frame.getHeight() > screenBounds.y + screenBounds.height) {
                        frame.setLocation(Math.min(frame.getX(), screenBounds.x + screenBounds.width - frame.getWidth()),
                                Math.min(frame.getY(), screenBounds.y + screenBounds.height - frame.getHeight()));
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public enum FoldType {
        NOTHING_0,
        FOR_ALL_LINES_1,
        FOR_SELECTED_LINES_2,
        CHANGING_FOLDED_3,
    }
}
