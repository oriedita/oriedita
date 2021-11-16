package origami_editor.editor;

import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami_editor.editor.drawing.FoldedFigure_Worker_Drawer;
import origami_editor.editor.service.*;
import origami_editor.tools.ResourceUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

@Singleton
public class App {
    private final ApplicationModel applicationModel;
    private final CanvasModel canvasModel;
    private final FoldedFigureModel foldedFigureModel;
    private final FileModel fileModel;
    private final FoldedFiguresList foldedFiguresList;
    private final CreasePattern_Worker mainCreasePatternWorker;    // Basic branch craftsman. Accepts input from the mouse.
    private final Queue<Popup> popups = new ArrayDeque<>();
    private final FileSaveService fileSaveService;
    private final ButtonService buttonService;
    private final LookAndFeelService lookAndFeelService;
    private final Editor editor;
    private final AppMenuBar appMenuBar;
    private final ResetService resetService;
    private final ConsoleDialog consoleDialog;
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

    @Inject
    public App(
            @Named("mainFrame") JFrame frame,
            LookAndFeelService lookAndFeelService,
            ApplicationModel applicationModel,
            CanvasModel canvasModel,
            FoldedFigureModel foldedFigureModel,
            FileModel fileModel,
            FoldedFiguresList foldedFiguresList,
            CreasePattern_Worker mainCreasePatternWorker,
            Canvas canvas,
            HelpDialog explanation,
            FileSaveService fileSaveService,
            ButtonService buttonService,
            Editor editor,
            AppMenuBar appMenuBar,
            ResetService resetService
    ) {
        this.frame = frame;
        this.lookAndFeelService = lookAndFeelService;
        this.applicationModel = applicationModel;
        this.canvasModel = canvasModel;
        this.foldedFigureModel = foldedFigureModel;
        this.fileModel = fileModel;
        this.foldedFiguresList = foldedFiguresList;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.canvas = canvas;
        this.explanation = explanation;
        this.fileSaveService = fileSaveService;
        this.buttonService = buttonService;
        this.editor = editor;
        this.appMenuBar = appMenuBar;
        this.resetService = resetService;

        frame.setTitle("Origami Editor " + ResourceUtil.getVersionFromManifest());//Specify the title and execute the constructor
        frame_title_0 = frame.getTitle();
        frame_title = frame_title_0;//Store title in variable
        mainCreasePatternWorker.setTitle(frame_title);

        consoleDialog = new ConsoleDialog();
    }

    public void start() {

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
        resetService.developmentView_initialization();

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
