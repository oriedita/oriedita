package origami_editor.editor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import origami_editor.editor.canvas.*;
import origami_editor.editor.component.BulletinBoard;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.drawing.FoldedFigure_Worker_Drawer;
import origami_editor.editor.service.*;
import origami_editor.tools.ResourceUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Queue;
import java.util.*;

@Singleton
public class App {
    private final LookAndFeelService lookAndFeelService;
    final ApplicationModel applicationModel;
    final GridModel gridModel;
    final CanvasModel canvasModel;
    final FoldedFigureModel foldedFigureModel;
    final AngleSystemModel angleSystemModel;
    final MeasuresModel measuresModel;
    final InternalDivisionRatioModel internalDivisionRatioModel;
    final HistoryStateModel historyStateModel;
    final BackgroundModel backgroundModel;
    final CameraModel creasePatternCameraModel;
    final FileModel fileModel;
    final FoldedFiguresList foldedFiguresList;
    final CreasePattern_Worker mainCreasePatternWorker;    // Basic branch craftsman. Accepts input from the mouse.
    final Queue<Popup> popups = new ArrayDeque<>();
    final FileSaveService fileSaveService;
    final ButtonService buttonService;
    final FoldingService foldingService;
    private final Editor editor;
    private final AppMenuBar appMenuBar;
    BulletinBoard bulletinBoard = new BulletinBoard();
    // ------------------------------------------------------------------------
    // Buffer screen settings VVVVVVVVVVVVVVVVVVVVVVVVV
    Canvas canvas;
    //各種変数の定義
    String frame_title_0;//フレームのタイトルの根本部分
    String frame_title;//フレームのタイトルの全体
    HelpDialog explanation;
    //画像出力するため20170107_oldと書かれた行をコメントアウトし、20170107_newの行を有効にした。
    //画像出力不要で元にもどすなら、20170107_oldと書かれた行を有効にし、20170107_newの行をコメントアウトにすればよい。（この変更はOrihime.javaの中だけに2箇所ある）
    // オフスクリーン
    JFrame frame;
    private final ConsoleDialog consoleDialog;

    @Inject
    public App(
            @Named("mainFrame") JFrame frame,
            LookAndFeelService lookAndFeelService,
            ApplicationModel applicationModel,
            GridModel gridModel,
            CanvasModel canvasModel,
            FoldedFigureModel foldedFigureModel,
            AngleSystemModel angleSystemModel,
            MeasuresModel measuresModel,
            InternalDivisionRatioModel internalDivisionRatioModel,
            ApplicationModelPersistenceService applicationModelPersistenceService,
            HistoryStateModel historyStateModel,
            BackgroundModel backgroundModel,
            CameraModel creasePatternCameraModel,
            FileModel fileModel,
            FoldedFiguresList foldedFiguresList,
            CreasePattern_Worker mainCreasePatternWorker,
            Canvas canvas,
            HelpDialog explanation,
            FileSaveService fileSaveService,
            ButtonService buttonService,
            FoldingService foldingService,
            Editor editor,
            AppMenuBar appMenuBar
    ) {
        this.frame = frame;
        this.lookAndFeelService = lookAndFeelService;
        this.applicationModel = applicationModel;
        this.gridModel = gridModel;
        this.canvasModel = canvasModel;
        this.foldedFigureModel = foldedFigureModel;
        this.angleSystemModel =angleSystemModel;
        this.measuresModel = measuresModel;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
        this.historyStateModel = historyStateModel;
        this.backgroundModel = backgroundModel;
        this.creasePatternCameraModel = creasePatternCameraModel;
        this.fileModel = fileModel;
        this.foldedFiguresList = foldedFiguresList;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.canvas = canvas;
        this.explanation = explanation;
        this.fileSaveService = fileSaveService;
        this.buttonService = buttonService;
        this.foldingService = foldingService;
        this.editor = editor;
        this.appMenuBar = appMenuBar;

        frame.setTitle("Origami Editor " + ResourceUtil.getVersionFromManifest());//Specify the title and execute the constructor
        frame_title_0 = frame.getTitle();
        frame_title = frame_title_0;//Store title in variable
        mainCreasePatternWorker.setTitle(frame_title);

        consoleDialog = new ConsoleDialog();
//        applicationModelPersistenceService.restoreApplicationModel();
    }

    public void start() {
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

        bulletinBoard.addChangeListener(e -> canvas.repaint());

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

        frame.setJMenuBar(appMenuBar);

        applicationModel.addPropertyChangeListener(e -> {
            for (int i = 0; i < foldedFiguresList.getSize(); i++) {
                FoldedFigure_Drawer item = foldedFiguresList.getElementAt(i);
                item.setData(applicationModel);
            }
            FoldedFigure_Worker_Drawer.setStaticData(applicationModel);
        });

        applicationModel.reload();

        foldedFigureModel.addPropertyChangeListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.setData(foldedFigureModel);
            }
        });

        canvasModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
                System.out.println("mouseMode = " + canvasModel.getMouseMode().toReadableString());
            }
        });

        fileModel.addPropertyChangeListener(e -> setData(fileModel));

        fileModel.reset();
        fileSaveService.developmentView_initialization();

        buttonService.Button_shared_operation();

        mainCreasePatternWorker.setCamera(canvas.creasePatternCamera);

        mainCreasePatternWorker.record();
        mainCreasePatternWorker.auxRecord();

        lookAndFeelService.updateButtonIcons();
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

        explanation.start(canvas.getLocationOnScreen(), canvas.getSize());
        explanation.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                applicationModel.setHelpVisible(false);
            }
        });

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
}
