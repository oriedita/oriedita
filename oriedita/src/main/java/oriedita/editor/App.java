package oriedita.editor;

import com.formdev.flatlaf.FlatLaf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jico.Ico;
import jico.ImageReadException;
import org.tinylog.Logger;
import oriedita.editor.action.ActionService;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.LambdaAction;
import oriedita.editor.action.Oriagari_sousaAction;
import oriedita.editor.action.SuiteiAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.FoldedFigure_Worker_Drawer;
import oriedita.editor.handler.FoldedFigureOperationMode;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FoldingService;
import oriedita.editor.service.LookAndFeelService;
import oriedita.editor.service.ResetService;
import oriedita.editor.swing.AppMenuBar;
import oriedita.editor.swing.Editor;
import oriedita.editor.swing.dialog.HelpDialog;
import oriedita.editor.tools.ResourceUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.folding.FoldedFigure;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
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
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private final MeasuresModel measuresModel;
    private final ResetService resetService;
    private final FoldingService foldingService;
    private final ActionService actionService;
    private final AnimationService animationService;
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
            MeasuresModel measuresModel,
            ResetService resetService,
            ActionService actionService,
            FoldingService foldingService,
            AnimationService animationService
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
        this.actionService = actionService;
        this.foldingService = foldingService;
        this.animationService = animationService;
        this.measuresModel = measuresModel;
    }

    public static boolean isPointInScreen(Point pos) {
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            if (gd.getDefaultConfiguration().getBounds().contains(pos)) {
                return true;
            }
        }

        return false;
    }

    public void start() throws InterruptedException {
        ExecutorService initService = Executors.newWorkStealingPool();
        registerActionsInitial(actionService);
        canvas.init();
        editor.init(initService);

        initService.shutdown();

        if (!initService.awaitTermination(10L, TimeUnit.SECONDS)) {
            throw new RuntimeException("Could not start");
        }

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
            e.printStackTrace();
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

        appMenuBar.init();
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

        applicationModel.reload();

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

        fileModel.addPropertyChangeListener(e -> frame.setTitle(fileModel.determineFrameTitle()));

        fileModel.reset();
        resetService.developmentView_initialization();

        buttonService.Button_shared_operation();
        buttonService.loadAllKeyStrokes();

        mainCreasePatternWorker.setCamera(canvas.getCreasePatternCamera());

        mainCreasePatternWorker.record();
        mainCreasePatternWorker.auxRecord();

        lookAndFeelService.updateButtonIcons();


        if (applicationModel.getWindowPosition() != null && isPointInScreen(applicationModel.getWindowPosition())) {
            frame.setLocation(applicationModel.getWindowPosition());
        } else {
            frame.setLocationRelativeTo(null);
        }

        frame.setExtendedState(applicationModel.getWindowState());
        frame.pack();

        if (applicationModel.getWindowSize() != null) {
            frame.setSize(applicationModel.getWindowSize());
        }

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

    private void registerActionsInitial(ActionService actionService){
        // |---------------------------------------------------------------------------|
        // Categorized by sections in each panel (improvements towards such are appreciated)
        // |---------------------------------------------------------------------------|
        // --- Left Panel ---
        // - line actions
        actionService.registerAction(new LambdaAction(ActionType.colRedAction, () -> canvasModel.setLineColor(LineColor.RED_1)));
        actionService.registerAction(new LambdaAction(ActionType.colBlueAction, () -> canvasModel.setLineColor(LineColor.BLUE_2)));
        actionService.registerAction(new LambdaAction(ActionType.colBlackAction, () -> canvasModel.setLineColor(LineColor.BLACK_0)));
        actionService.registerAction(new LambdaAction(ActionType.colCyanAction, () -> canvasModel.setLineColor(LineColor.CYAN_3)));
        actionService.registerAction(new LambdaAction(ActionType.antiAliasToggleAction, applicationModel::toggleAntiAlias));
        actionService.registerAction(new LambdaAction(ActionType.lineWidthDecreaseAction, applicationModel::decreaseLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.lineWidthIncreaseAction, applicationModel::increaseLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.pointSizeDecreaseAction, applicationModel::decreasePointSize));
        actionService.registerAction(new LambdaAction(ActionType.pointSizeIncreaseAction, applicationModel::increasePointSize));

        // - draw actions
        actionService.registerAction(new LambdaAction(ActionType.lengthenCreaseAction, () -> {
            canvasModel.setMouseMode(MouseMode.LENGTHEN_CREASE_5);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LENGTHEN_CREASE_5);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.rabbitEarAction, () -> {
            canvasModel.setMouseMode(MouseMode.INWARD_8);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.INWARD_8);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.perpendicularDrawAction, () -> {
            canvasModel.setMouseMode(MouseMode.PERPENDICULAR_DRAW_9);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PERPENDICULAR_DRAW_9);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.symmetricDrawAction, () -> {
            canvasModel.setMouseMode(MouseMode.SYMMETRIC_DRAW_10);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.SYMMETRIC_DRAW_10);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.senbun_b_nyuryokuAction, () -> {
            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DIVISION_27);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_DIVISION_27);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.fishBoneDrawAction, () -> {
            canvasModel.setMouseMode(MouseMode.FISH_BONE_DRAW_33);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.FISH_BONE_DRAW_33);
            mainCreasePatternWorker.unselect_all(false);
            buttonService.Button_shared_operation();
        }));
        actionService.registerAction(new LambdaAction(ActionType.doubleSymmetricDrawAction, () -> {
            canvasModel.setMouseMode(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.DOUBLE_SYMMETRIC_DRAW_35);
            mainCreasePatternWorker.unselect_all(false);
            buttonService.Button_shared_operation();
        }));
        actionService.registerAction(new LambdaAction(ActionType.parallelDrawAction, () -> {
            canvasModel.setMouseMode(MouseMode.PARALLEL_DRAW_40);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.PARALLEL_DRAW_40);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.continuousSymmetricDrawAction, () -> {
            canvasModel.setMouseMode(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            mainCreasePatternWorker.unselect_all(false);
        }));

        // - select and transform actions
        actionService.registerAction(new LambdaAction(ActionType.selectAction, () -> canvasModel.setMouseMode(MouseMode.CREASE_SELECT_19)));
        actionService.registerAction(new LambdaAction(ActionType.unselectAction, () -> canvasModel.setMouseMode(MouseMode.CREASE_UNSELECT_20)));
        actionService.registerAction(new LambdaAction(ActionType.selectAllAction, mainCreasePatternWorker::select_all));
        actionService.registerAction(new LambdaAction(ActionType.unselectAllAction, mainCreasePatternWorker::unselect_all));
        actionService.registerAction(new LambdaAction(ActionType.reflectAction, () -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MIRROR_5);
            canvasModel.setMouseMode(MouseMode.DRAW_CREASE_SYMMETRIC_12);
        }));
        actionService.registerAction(new LambdaAction(ActionType.moveAction, () -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MOVE_1);
            canvasModel.setMouseMode(MouseMode.CREASE_MOVE_21);
        }));
        actionService.registerAction(new LambdaAction(ActionType.copyAction, () -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.COPY_3);
            canvasModel.setMouseMode(MouseMode.CREASE_COPY_22);
        }));
        actionService.registerAction(new LambdaAction(ActionType.move2p2pAction, () -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.MOVE4P_2);
            canvasModel.setMouseMode(MouseMode.CREASE_MOVE_4P_31);
        }));
        actionService.registerAction(new LambdaAction(ActionType.copy2p2pAction, () -> {
            canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.COPY4P_4);
            canvasModel.setMouseMode(MouseMode.CREASE_COPY_4P_32);
        }));
        actionService.registerAction(new LambdaAction(ActionType.deleteSelectedLineSegmentAction, () -> {
            mainCreasePatternWorker.del_selected_senbun();
            mainCreasePatternWorker.record();
        }));

        // - line edit actions
        actionService.registerAction(new LambdaAction(ActionType.v_del_allAction, mainCreasePatternWorker::v_del_all));
        actionService.registerAction(new LambdaAction(ActionType.v_del_all_ccAction, mainCreasePatternWorker::v_del_all_cc));
        actionService.registerAction(new LambdaAction(ActionType.zen_yama_tani_henkanAction, () -> {
            mainCreasePatternWorker.allMountainValleyChange();
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.senbun_henkanAction, () -> {
            canvasModel.setMouseMode(MouseMode.CHANGE_CREASE_TYPE_4);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.vertexAddAction, () -> {
            canvasModel.setMouseMode(MouseMode.DRAW_POINT_14);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.vertexDeleteAction, () -> {
            canvasModel.setMouseMode(MouseMode.DELETE_POINT_15);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.in_L_col_changeAction, () -> {
            canvasModel.setMouseMode(MouseMode.CREASE_MAKE_MV_34);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASE_MAKE_MV_34);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.RED_1);
            }

            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.on_L_col_changeAction, () -> {
            canvasModel.setMouseMode(MouseMode.CREASES_ALTERNATE_MV_36);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.CREASES_ALTERNATE_MV_36);

            if (canvasModel.getLineColor() == LineColor.BLACK_0) {
                canvasModel.setLineColor(LineColor.BLUE_2);
            }

            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.v_del_ccAction, () -> {
            canvasModel.setMouseMode(MouseMode.VERTEX_DELETE_ON_CREASE_41);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.senbun_henkan2Action, () -> {
            canvasModel.setMouseMode(MouseMode.CREASE_TOGGLE_MV_58);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.replace_lineAction, () -> {
            canvasModel.setMouseMode(MouseMode.REPLACE_LINE_TYPE_SELECT_72);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.del_l_typeAction, () -> {
            canvasModel.setMouseMode(MouseMode.DELETE_LINE_TYPE_SELECT_73);
            mainCreasePatternWorker.unselect_all(false);
        }));
        actionService.registerAction(new LambdaAction(ActionType.trimBranchesAction, () -> {
            mainCreasePatternWorker.point_removal();
            mainCreasePatternWorker.overlapping_line_removal();
            mainCreasePatternWorker.branch_trim();
            mainCreasePatternWorker.organizeCircles();
            mainCreasePatternWorker.record();
            mainCreasePatternWorker.unselect_all(false);
        }));

        // - grid actions
        actionService.registerAction(new LambdaAction(ActionType.gridSizeIncreaseAction, () -> gridModel.setGridSize(gridModel.getGridSize() * 2)));
        actionService.registerAction(new LambdaAction(ActionType.gridSizeDecreaseAction, () -> gridModel.setGridSize(Math.max(gridModel.getGridSize() / 2, 1))));
        actionService.registerAction(new LambdaAction(ActionType.changeGridStateAction, gridModel::advanceBaseState));
        actionService.registerAction(new LambdaAction(ActionType.gridLineWidthDecreaseAction, applicationModel::decreaseGridLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.gridLineWidthIncreaseAction, applicationModel::increaseGridLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.moveIntervalGridVerticalAction, gridModel::changeHorizontalScalePosition));
        actionService.registerAction(new LambdaAction(ActionType.moveIntervalGridHorizontalAction, gridModel::changeVerticalScalePosition));
        actionService.registerAction(new LambdaAction(ActionType.gridColorAction, () -> {
            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(frameProvider.get(), "Col", FlatLaf.isLafDark() ? Colors.GRID_LINE_DARK : Colors.GRID_LINE);
            if (color != null) {
                applicationModel.setGridColor(color);
            }
            //以上でやりたいことは書き終わり
        }));
        actionService.registerAction(new LambdaAction(ActionType.intervalGridColorAction, () -> {
            Color color = JColorChooser.showDialog(frameProvider.get(), "Col", FlatLaf.isLafDark() ? Colors.GRID_SCALE_DARK : Colors.GRID_SCALE);
            if (color != null) {
                applicationModel.setGridScaleColor(color);
            }
        }));

        // - other actions
        actionService.registerAction(new SuiteiAction(ActionType.suitei_01Action, FoldedFigure.EstimationOrder.ORDER_1));
        actionService.registerAction(new SuiteiAction(ActionType.suitei_02Action, FoldedFigure.EstimationOrder.ORDER_2));
        actionService.registerAction(new SuiteiAction(ActionType.suitei_03Action, FoldedFigure.EstimationOrder.ORDER_3));
        actionService.registerAction(new LambdaAction(ActionType.drawTwoColoredCpAction, foldingService::createTwoColoredCp));
        actionService.registerAction(new LambdaAction(ActionType.coloredXRayDecreaseAction, foldedFigureModel::decreaseTransparency));
        actionService.registerAction(new LambdaAction(ActionType.coloredXRayIncreaseAction, foldedFigureModel::increaseTransparency));
        actionService.registerAction(new LambdaAction(ActionType.koteimen_siteiAction, () -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null && selectedFigure.getFoldedFigure().displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                canvasModel.setMouseMode(MouseMode.CHANGE_STANDARD_FACE_103);
            }
        }));

        // |---------------------------------------------------------------------------|
        // --- Top Panel ---
        actionService.registerAction(new LambdaAction(ActionType.backgroundSetPositionAction, () -> canvasModel.setMouseMode(MouseMode.BACKGROUND_CHANGE_POSITION_26)));
        actionService.registerAction(new LambdaAction(ActionType.rotateClockwiseAction, cameraModel::decreaseRotation));
        actionService.registerAction(new LambdaAction(ActionType.rotateAnticlockwiseAction, cameraModel::increaseRotation));
        actionService.registerAction(new LambdaAction(ActionType.transparentAction, canvas::createTransparentBackground));

        // |---------------------------------------------------------------------------|
        // --- Right panel ---
        // - validation actions
        actionService.registerAction(new LambdaAction(ActionType.ck4_colorDecreaseAction, mainCreasePatternWorker::lightenCheck4Color));
        actionService.registerAction(new LambdaAction(ActionType.ck4_colorIncreaseAction, mainCreasePatternWorker::darkenCheck4Color));
        actionService.registerAction(new LambdaAction(ActionType.ckTAction, mainCreasePatternWorker::unselect_all));
        actionService.registerAction(new LambdaAction(ActionType.ckOAction, mainCreasePatternWorker::unselect_all));
        actionService.registerAction(new LambdaAction(ActionType.fxOAction, () -> {
            mainCreasePatternWorker.unselect_all();
            mainCreasePatternWorker.fix1();
            mainCreasePatternWorker.check1();
        }));
        actionService.registerAction(new LambdaAction(ActionType.fxTAction, () -> {
            mainCreasePatternWorker.unselect_all();
            mainCreasePatternWorker.fix2();
            mainCreasePatternWorker.check2();
        }));
        actionService.registerAction(new LambdaAction(ActionType.cAMVAction, () -> {
            mainCreasePatternWorker.unselect_all();
            buttonService.Button_shared_operation();
        }));

        // - angle system actions
        actionService.registerAction(new LambdaAction(ActionType.angleSystemADecreaseAction, angleSystemModel::decreaseAngleSystemA));
        actionService.registerAction(new LambdaAction(ActionType.angleSystemAAction, () -> angleSystemModel.setCurrentAngleSystemDivider(angleSystemModel.getAngleSystemADivider())));
        actionService.registerAction(new LambdaAction(ActionType.angleSystemAIncreaseAction, angleSystemModel::increaseAngleSystemA));
        actionService.registerAction(new LambdaAction(ActionType.angleSystemBDecreaseAction, angleSystemModel::decreaseAngleSystemB));
        actionService.registerAction(new LambdaAction(ActionType.angleSystemBAction, () -> angleSystemModel.setCurrentAngleSystemDivider(angleSystemModel.getAngleSystemBDivider())));
        actionService.registerAction(new LambdaAction(ActionType.angleSystemBIncreaseAction, angleSystemModel::increaseAngleSystemB));
        actionService.registerAction(new LambdaAction(ActionType.angleRestrictedAction, () -> {
            angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_5);
            canvasModel.setMouseMode(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37);
        }));

        // - angle restricted actions
        actionService.registerAction(new LambdaAction(ActionType.degAction, () -> {
            angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_1);
            canvasModel.setMouseMode(MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13);
        }));

        // - polygon actions
        actionService.registerAction(new LambdaAction(ActionType.regularPolygonAction, () -> {
            canvasModel.setMouseMode(MouseMode.POLYGON_SET_NO_CORNERS_29);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.POLYGON_SET_NO_CORNERS_29);
            mainCreasePatternWorker.unselect_all();
        }));

        // circle actions
        actionService.registerAction(new LambdaAction(ActionType.circleDrawFreeAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_FREE_47)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_42)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawThreePointAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_THREE_POINT_43)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawSeparateAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_SEPARATE_44)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawTangentLineAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_TANGENT_LINE_45)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawInvertedAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_INVERTED_46)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawConcentricAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_CONCENTRIC_48)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawConcentricSelectAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49)));
        actionService.registerAction(new LambdaAction(ActionType.circleDrawTwoConcentricAction, () -> canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50)));
        actionService.registerAction(new LambdaAction(ActionType.sen_tokutyuu_color_henkouAction, () -> {
            canvasModel.setMouseMode(MouseMode.CIRCLE_CHANGE_COLOR_59);
            mainCreasePatternWorker.unselect_all();
        }));

        // - other aux actions
        actionService.registerAction(new LambdaAction(ActionType.colOrangeAction, () -> canvasModel.setAuxLiveLineColor(LineColor.ORANGE_4)));
        actionService.registerAction(new LambdaAction(ActionType.colYellowAction, () -> canvasModel.setAuxLiveLineColor(LineColor.YELLOW_7)));
        actionService.registerAction(new LambdaAction(ActionType.h_senhaba_sageAction, applicationModel::decreaseAuxLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.h_senhaba_ageAction, applicationModel::increaseAuxLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.h_undoAction, mainCreasePatternWorker::auxUndo));
        actionService.registerAction(new LambdaAction(ActionType.h_redoAction, mainCreasePatternWorker::auxRedo));
        actionService.registerAction(new LambdaAction(ActionType.h_senbun_nyuryokuAction, () -> {
            canvasModel.setMouseMode(MouseMode.DRAW_CREASE_FREE_1);
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.AUX_LINE_1);
            mainCreasePatternWorker.unselect_all();
        }));
        actionService.registerAction(new LambdaAction(ActionType.h_senbun_sakujyoButton, () -> {
            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.AUX_LINE_1);
            mainCreasePatternWorker.unselect_all();
        }));

        // - lines & angles measuring actions
        actionService.registerAction(new LambdaAction(ActionType.l1Action, () -> {
            canvasModel.setMouseMode(MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53);
            mainCreasePatternWorker.unselect_all();
        }));
        actionService.registerAction(new LambdaAction(ActionType.l2Action, () -> {
            canvasModel.setMouseMode(MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54);
            mainCreasePatternWorker.unselect_all();
        }));
        actionService.registerAction(new LambdaAction(ActionType.a1Action, () -> {
            canvasModel.setMouseMode(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55);
            mainCreasePatternWorker.unselect_all();
        }));
        actionService.registerAction(new LambdaAction(ActionType.a2Action, () -> {
            canvasModel.setMouseMode(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56);
            mainCreasePatternWorker.unselect_all();
        }));
        actionService.registerAction(new LambdaAction(ActionType.a3Action, () -> {
            canvasModel.setMouseMode(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57);
            mainCreasePatternWorker.unselect_all();
        }));

        // - text
        actionService.registerAction(new LambdaAction(ActionType.textAction, () -> {
            canvasModel.setMouseMode(MouseMode.TEXT);
            mainCreasePatternWorker.unselect_all();
        }));

        // |---------------------------------------------------------------------------|
        // --- Bottom Panel ---
        // foldedFigure actions
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureMoveAction, () -> canvasModel.setMouseMode(MouseMode.MOVE_CALCULATED_SHAPE_102)));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureToggleAntiAliasAction, foldedFigureModel::toggleAntiAlias));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureToggleShadowAction, foldedFigureModel::toggleDisplayShadows));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureSizeIncreaseAction, () -> animationService.animate(Animations.ZOOM_FOLDED_MODEL,
            foldedFigureModel::setScale,
            foldedFigureModel::getScale,
            scale -> foldedFigureModel.getScaleForZoomBy(-1, applicationModel.getZoomSpeed(), scale),
            AnimationDurations.ZOOM)
        ));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureSizeDecreaseAction, () -> animationService.animate(Animations.ZOOM_FOLDED_MODEL,
            foldedFigureModel::setScale,
            foldedFigureModel::getScale,
            scale -> foldedFigureModel.getScaleForZoomBy(1, applicationModel.getZoomSpeed(), scale),
            AnimationDurations.ZOOM)
        ));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureRotateClockwiseAction, () -> {
            double rotation = foldedFigureModel.getState() == FoldedFigure.State.BACK_1 ? foldedFigureModel.getRotation() + 11.25 : foldedFigureModel.getRotation() - 11.25;
            foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(rotation));
        }));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureRotateAntiClockwiseAction, () -> {
            double rotation = foldedFigureModel.getState() != FoldedFigure.State.BACK_1 ? foldedFigureModel.getRotation() + 11.25 : foldedFigureModel.getRotation() - 11.25;
            foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(rotation));
        }));
        actionService.registerAction(new Oriagari_sousaAction(ActionType.oriagari_sousaAction, FoldedFigureOperationMode.MODE_1));
        actionService.registerAction(new Oriagari_sousaAction(ActionType.oriagari_sousa2Action, FoldedFigureOperationMode.MODE_2));

        // |---------------------------------------------------------------------------|
        // --- AppMenuBar ---
        actionService.registerAction(new LambdaAction(ActionType.toggleHelpAction, applicationModel::toggleHelpVisible));

        // --- OpenFrame ---
        actionService.registerAction(new LambdaAction(ActionType.o_F_checkAction, () -> canvasModel.setMouseMode(MouseMode.FLAT_FOLDABLE_CHECK_63)));
        actionService.registerAction(new LambdaAction(ActionType.del_lAction, () -> canvasModel.setMouseMode(MouseMode.CREASE_DELETE_OVERLAPPING_64)));
        actionService.registerAction(new LambdaAction(ActionType.del_l_XAction, () -> canvasModel.setMouseMode(MouseMode.CREASE_DELETE_INTERSECTING_65)));
        actionService.registerAction(new LambdaAction(ActionType.select_polygonAction, () -> canvasModel.setMouseMode(MouseMode.SELECT_POLYGON_66)));
        actionService.registerAction(new LambdaAction(ActionType.unselect_polygonAction, () -> canvasModel.setMouseMode(MouseMode.UNSELECT_POLYGON_67)));
        actionService.registerAction(new LambdaAction(ActionType.select_lXAction, () -> canvasModel.setMouseMode(MouseMode.SELECT_LINE_INTERSECTING_68)));
        actionService.registerAction(new LambdaAction(ActionType.unselect_lXAction, () -> canvasModel.setMouseMode(MouseMode.UNSELECT_LINE_INTERSECTING_69)));
    }
}