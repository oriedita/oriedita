package origami_editor.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import origami_editor.editor.canvas.*;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.drawing.FoldedFigure_Worker_Drawer;
import origami_editor.editor.json.DefaultObjectMapper;
import origami_editor.editor.service.ButtonService;
import origami_editor.editor.service.FileSaveService;
import origami_editor.editor.service.FoldingService;
import origami_editor.tools.Camera;
import origami_editor.tools.ResourceUtil;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

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
    public final CreasePattern_Worker mainCreasePatternWorker;    // Basic branch craftsman. Accepts input from the mouse.
    final Queue<Popup> popups = new ArrayDeque<>();
    private final MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate = new MouseHandlerVoronoiCreate();
    public  final FileSaveService fileSaveService;
    public final ButtonService buttonService;
    public final FoldingService foldingService;
    private AppMenuBar appMenuBar;
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
        foldedFiguresList = new DefaultComboBoxModel<>();

        Camera creasePatternCamera = new Camera();

        mainCreasePatternWorker = new CreasePattern_Worker(creasePatternCamera, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel);

        canvas = new Canvas(creasePatternCamera, mainCreasePatternWorker, foldedFiguresList, backgroundModel, bulletinBoard, fileModel, applicationModel, creasePatternCameraModel, foldedFigureModel, canvasModel);
        explanation = new HelpDialog(applicationModel::setHelpVisible);

        fileSaveService = new FileSaveService(canvas, mainCreasePatternWorker, fileModel, applicationModel, historyStateModel, canvasModel, internalDivisionRatioModel, foldedFigureModel, gridModel, angleSystemModel, creasePatternCameraModel, foldedFiguresList, mouseHandlerVoronoiCreate, backgroundModel);
        buttonService = new ButtonService(explanation, mainCreasePatternWorker, canvas);
        foldingService = new FoldingService(bulletinBoard, canvas, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, mainCreasePatternWorker, foldedFiguresList);
        buttonService.setMouseHandlerVoronoiCreate(mouseHandlerVoronoiCreate);
    }

    public void start() {
        frame = new JFrame();
        fileSaveService.setOwner(frame);
        canvas.setFrame(frame);

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
                appMenuBar.closing();//Work to be done when pressing X at the right end of the upper side of the window
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
                    for (Map.Entry<KeyStroke, AbstractButton> entry : buttonService.helpInputMap.entrySet()) {
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

        RightPanel rightPanel = new RightPanel(angleSystemModel, buttonService, measuresModel, mainCreasePatternWorker, canvasModel, applicationModel, historyStateModel);
        BottomPanel bottomPanel = new BottomPanel(buttonService, measuresModel, canvasModel, foldedFigureModel, creasePatternCameraModel, mainCreasePatternWorker, foldingService, applicationModel, foldedFiguresList, fileModel, fileSaveService, canvas, bulletinBoard);
        TopPanel topPanel = new TopPanel(measuresModel, buttonService, canvasModel, internalDivisionRatioModel, backgroundModel, mainCreasePatternWorker, foldedFigureModel, fileSaveService, creasePatternCameraModel, foldedFiguresList, canvas, applicationModel);
        LeftPanel leftPanel = new LeftPanel(measuresModel, buttonService, mainCreasePatternWorker, applicationModel, foldedFigureModel, gridModel, canvasModel, foldingService, foldedFiguresList);


        Editor editor = new Editor(canvas, rightPanel, bottomPanel, topPanel, leftPanel);
        editor.setOwner(frame);

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

        appMenuBar = new AppMenuBar(applicationModel, fileSaveService, buttonService, canvasModel, fileModel, mainCreasePatternWorker, foldedFigureModel, foldedFiguresList);

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

        buttonService.Button_shared_operation();

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
        canvas.addMouseModeHandler(new MouseHandlerContinuousSymmetricDraw(mainCreasePatternWorker, canvas));
        canvas.addMouseModeHandler(new MouseHandlerDisplayLengthBetweenPoints1(mainCreasePatternWorker, measuresModel));
        canvas.addMouseModeHandler(new MouseHandlerDisplayLengthBetweenPoints2(mainCreasePatternWorker, measuresModel));
        canvas.addMouseModeHandler(new MouseHandlerDisplayAngleBetweenThreePoints1(mainCreasePatternWorker, measuresModel));
        canvas.addMouseModeHandler(new MouseHandlerDisplayAngleBetweenThreePoints2(mainCreasePatternWorker, measuresModel));
        canvas.addMouseModeHandler(new MouseHandlerDisplayAngleBetweenThreePoints3(mainCreasePatternWorker, measuresModel));
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
        canvas.addMouseModeHandler(new MouseHandlerCreaseMove4p(mainCreasePatternWorker, canvasModel));
        canvas.addMouseModeHandler(new MouseHandlerCreaseCopy4p(mainCreasePatternWorker, canvasModel));
        canvas.addMouseModeHandler(new MouseHandlerDrawCreaseSymmetric(mainCreasePatternWorker, canvasModel));
        canvas.addMouseModeHandler(MouseHandlerCreaseMakeMV.class);
        canvas.addMouseModeHandler(MouseHandlerCreaseDeleteOverlapping.class);
        canvas.addMouseModeHandler(new MouseHandlerCreaseMove(mainCreasePatternWorker, canvasModel));
        canvas.addMouseModeHandler(new MouseHandlerCreaseCopy(mainCreasePatternWorker, canvasModel));
        canvas.addMouseModeHandler(new MouseHandlerCreaseSelect(mainCreasePatternWorker, canvasModel));
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
        canvas.addMouseModeHandler(new MouseHandlerBackgroundChangePosition(buttonService, backgroundModel, canvas));
        canvas.addMouseModeHandler(new MouseHandlerMoveCalculatedShape(foldedFiguresList, canvas));
        canvas.addMouseModeHandler(new MouseHandlerModifyCalculatedShape(foldingService, canvasModel, foldedFiguresList));
        canvas.addMouseModeHandler(new MouseHandlerAddFoldingConstraints());
        canvas.addMouseModeHandler(new MouseHandlerMoveCreasePattern(canvas, foldedFiguresList, mainCreasePatternWorker));
        canvas.addMouseModeHandler(new MouseHandlerChangeStandardFace(foldedFiguresList, mainCreasePatternWorker));

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


        explanation.setOwner(frame);
        explanation.start(canvas.getLocationOnScreen(), canvas.getSize());
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

    public void foldingNoSelectedPolygonalLineWarning() {
        JLabel label = new JLabel(
                "<html>新たに折り上がり図を描くためには、あらかじめ対象範囲を選択してください（selectボタンを使う）。<br>" +
                        "To calculate new folded shape, select the target clease lines range in advance (use the select button).<html>");
        JOptionPane.showMessageDialog(frame, label);
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

}
