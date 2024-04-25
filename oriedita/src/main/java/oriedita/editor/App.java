package oriedita.editor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jico.Ico;
import jico.ImageReadException;
import org.tinylog.Logger;
import oriedita.common.task.MultiStagedExecutor;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.FoldedFigure_Worker_Drawer;
import oriedita.editor.service.ActionRegistrationService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.LookAndFeelService;
import oriedita.editor.service.ResetService;
import oriedita.editor.swing.AppMenuBar;
import oriedita.editor.swing.Editor;
import oriedita.editor.swing.dialog.HelpDialog;
import oriedita.editor.tools.ResourceUtil;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

@ApplicationScoped
public class App {
    private final ApplicationModel applicationModel;
    private final CanvasModel canvasModel;
    private final FoldedFigureModel foldedFigureModel;
    private final FileModel fileModel;
    private final FoldedFiguresList foldedFiguresList;
    private final CreasePattern_Worker mainCreasePatternWorker;    // Basic branch craftsman. Accepts input from the mouse.
    private final Queue<Popup> popups = new ArrayDeque<>();
    private final ButtonService buttonService;
    private final LookAndFeelService lookAndFeelService;
    private final Editor editor;
    private final AppMenuBar appMenuBar;
    private final GridModel gridModel;
    private final BackgroundModel backgroundModel;
    private final AngleSystemModel angleSystemModel;
    private final CameraModel cameraModel;
    private final ResetService resetService;
    private final ActionRegistrationService actionRegistrationService;
    // ------------------------------------------------------------------------
    // Buffer screen settings VVVVVVVVVVVVVVVVVVVVVVVVV
    Canvas canvas;
    //各種変数の定義
    HelpDialog explanation;
    //画像出力するため20170107_oldと書かれた行をコメントアウトし、20170107_newの行を有効にした。
    //画像出力不要で元にもどすなら、20170107_oldと書かれた行を有効にし、20170107_newの行をコメントアウトにすればよい。（この変更はOrihime.javaの中だけに2箇所ある）
    // オフスクリーン
    FrameProvider frameProvider;

    @Inject
    public App(
            FrameProvider frameProvider,
            LookAndFeelService lookAndFeelService,
            ApplicationModel applicationModel,
            CanvasModel canvasModel,
            FoldedFigureModel foldedFigureModel,
            FileModel fileModel,
            FoldedFiguresList foldedFiguresList,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            Canvas canvas,
            HelpDialog explanation,
            ButtonService buttonService,
            Editor editor,
            AppMenuBar appMenuBar,
            GridModel gridModel,
            BackgroundModel backgroundModel,
            AngleSystemModel angleSystemModel,
            CameraModel cameraModel,
            ResetService resetService,
            ActionRegistrationService actionRegistrationService
    ) {
        this.frameProvider = frameProvider;
        this.lookAndFeelService = lookAndFeelService;
        this.applicationModel = applicationModel;
        this.canvasModel = canvasModel;
        this.foldedFigureModel = foldedFigureModel;
        this.fileModel = fileModel;
        this.foldedFiguresList = foldedFiguresList;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.canvas = canvas;
        this.explanation = explanation;
        this.buttonService = buttonService;
        this.editor = editor;
        this.appMenuBar = appMenuBar;
        this.gridModel = gridModel;
        this.backgroundModel = backgroundModel;
        this.angleSystemModel = angleSystemModel;
        this.cameraModel = cameraModel;
        this.resetService = resetService;
        this.actionRegistrationService = actionRegistrationService;
    }

    public static boolean isPointInScreen(Point pos) {
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            if (gd.getDefaultConfiguration().getBounds().contains(pos)) {
                return true;
            }
        }

        return false;
    }

    private static void loadFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(App.class.getClassLoader().getResourceAsStream("Icons2.ttf"))));
        } catch (IOException | FontFormatException e) {
            Logger.error(e);
        }
    }

    public void start() throws InterruptedException, InvocationTargetException {
        loadFont();
        MultiStagedExecutor executor = new MultiStagedExecutor();
        executor.execute(actionRegistrationService::registerActionsInitial);
        executor.execute(canvas::init);
        editor.init(executor);
        executor.execute(appMenuBar::init);

        executor.nextStage();

        Logger.trace("Init stage finished");

        // ---
        // Bind model to ui
        backgroundModel.addPropertyChangeListener(editor.getTopPanel());
        applicationModel.addPropertyChangeListener(editor.getTopPanel());
        cameraModel.addPropertyChangeListener(editor.getTopPanel());
        // ---

        JFrame frame = frameProvider.get();
        frame.setTitle("Oriedita " + ResourceUtil.getVersionFromManifest());//Specify the title and execute the constructor

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

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(true);

        foldedFiguresList.removeAllElements();

        canvas.getCreasePatternCamera().setCameraPositionX(0.0);
        canvas.getCreasePatternCamera().setCameraPositionY(0.0);
        canvas.getCreasePatternCamera().setCameraAngle(0.0);
        canvas.getCreasePatternCamera().setCameraMirror(1.0);
        canvas.getCreasePatternCamera().setCameraZoomX(1.0);
        canvas.getCreasePatternCamera().setCameraZoomY(1.0);
        canvas.getCreasePatternCamera().setDisplayPositionX(350.0);
        canvas.getCreasePatternCamera().setDisplayPositionY(350.0);

        try {
            frame.setIconImages(Ico.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("oriedita.ico"))));
        } catch (IOException | ImageReadException | NullPointerException e) {
            Logger.error(e);
        }
        frame.setContentPane(editor.$$$getRootComponent$$$());
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, KeyEvent.CTRL_DOWN_MASK),
                "CTRLPress");
        frame.getRootPane().getActionMap().put("CTRLPress", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!canvasModel.getToggleLineColor()) {
                    canvasModel.setToggleLineColor(true);
                }
            }
        });
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, KeyEvent.ALT_DOWN_MASK),
                "ALTPress");
        frame.getRootPane().getActionMap().put("ALTPress", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (popups.isEmpty()) {
                    for (Map.Entry<KeyStroke, AbstractButton> entry : buttonService.getHelpInputMap().entrySet()) {
                        AbstractButton button = entry.getValue();
                        KeyStroke keyStroke = entry.getKey();

                        if (!button.isShowing()) continue;

                        Point locationOnScreen = button.getLocationOnScreen();
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
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true),
                "Release");
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true),
                "Release");
        frame.getRootPane().getActionMap().put("Release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Popup popup;
                while ((popup = popups.poll()) != null) {
                    popup.hide();
                }

                canvasModel.setToggleLineColor(false);
            }
        });

        frame.setJMenuBar(appMenuBar.getAppMenuBarUI());

        applicationModel.addPropertyChangeListener(e -> {
            for (int i = 0; i < foldedFiguresList.getSize(); i++) {
                FoldedFigure_Drawer item = foldedFiguresList.getElementAt(i);
                item.setData(applicationModel);
            }
            FoldedFigure_Worker_Drawer.setStaticData(applicationModel);
            setData(applicationModel);
        });


        applicationModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(e, applicationModel));
        gridModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setGridConfigurationData(gridModel));
        angleSystemModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(angleSystemModel));
        canvasModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setData(canvasModel));
        fileModel.addPropertyChangeListener(e -> mainCreasePatternWorker.setTitle(fileModel.determineFrameTitle()));

        foldedFigureModel.addPropertyChangeListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null) {
                selectedFigure.setData(foldedFigureModel);
            }
        });

        canvasModel.addPropertyChangeListener(e -> {
            if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
                Logger.info("mouseMode = " + canvasModel.getMouseMode().toReadableString());
            }
        });


        executor.execute(() -> {
            applicationModel.reload();

            fileModel.addPropertyChangeListener(e -> frame.setTitle(fileModel.determineFrameTitle()));

            fileModel.reset();

            mainCreasePatternWorker.setCamera(canvas.getCreasePatternCamera());

            mainCreasePatternWorker.record();
            mainCreasePatternWorker.auxRecord();
        });
        executor.execute(resetService::developmentView_initialization);
        executor.execute(() -> {
            buttonService.Button_shared_operation();
            buttonService.loadAllKeyStrokes();
        });
        executor.execute(lookAndFeelService::updateButtonIcons);

        executor.stopStage();


        Logger.trace("SetupExecutor finished");

        if (applicationModel.getWindowPosition() != null && isPointInScreen(applicationModel.getWindowPosition())) {
            frame.setLocation(applicationModel.getWindowPosition());
        } else {
            frame.setLocationRelativeTo(null);
        }

        frame.setExtendedState(applicationModel.getWindowState());
        Logger.trace("Main Frame packed");
        frame.pack();

        if (applicationModel.getWindowSize() != null) {
            frame.setSize(applicationModel.getWindowSize());
        }
    }

    public void afterStart() {
        JFrame frame = frameProvider.get();
        frame.setVisible(true);
        explanation.start(canvas.getCanvasImpl().getLocationOnScreen(), canvas.getCanvasImpl().getSize());

        explanation.setVisible(applicationModel.getHelpVisible());
        //focus back to here after creating dialog
        frame.requestFocus();
    }

    private void setData(ApplicationModel applicationModel) {
        editor.getBottomPanel().$$$getRootComponent$$$().setVisible(applicationModel.getDisplayBottomPanel());
        editor.getTopPanel().$$$getRootComponent$$$().setVisible(applicationModel.getDisplayTopPanel());
        editor.getRightPanel().$$$getRootComponent$$$().setVisible(applicationModel.getDisplayRightPanel());
        editor.getLeftPanel().$$$getRootComponent$$$().setVisible(applicationModel.getDisplayLeftPanel());
    }
}