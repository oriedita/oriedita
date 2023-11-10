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
import oriedita.editor.factory.ActionFactory;
import oriedita.editor.handler.FoldedFigureOperationMode;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;
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
import java.util.HashMap;
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
    private final FileSaveService fileSaveService;
    private final ActionFactory actionFactory;
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
            AnimationService animationService,
            FileSaveService fileSaveService,
            ActionFactory actionFactory
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
        this.fileSaveService = fileSaveService;
        this.actionFactory = actionFactory;
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
        // setMouseMode actions
        Map<ActionType, MouseMode> mouseModeActions = new HashMap<>() {{
            put(ActionType.selectAction, MouseMode.CREASE_SELECT_19);
            put(ActionType.unselectAction, MouseMode.CREASE_UNSELECT_20);
            put(ActionType.moveCreasePatternAction, MouseMode.MOVE_CREASE_PATTERN_2);
            put(ActionType.backgroundSetPositionAction, MouseMode.BACKGROUND_CHANGE_POSITION_26);
            put(ActionType.circleDrawAction, MouseMode.CIRCLE_DRAW_42);
            put(ActionType.circleDrawThreePointAction, MouseMode.CIRCLE_DRAW_THREE_POINT_43);
            put(ActionType.circleDrawSeparateAction, MouseMode.CIRCLE_DRAW_SEPARATE_44);
            put(ActionType.circleDrawTangentLineAction, MouseMode.CIRCLE_DRAW_TANGENT_LINE_45);
            put(ActionType.circleDrawInvertedAction, MouseMode.CIRCLE_DRAW_INVERTED_46);
            put(ActionType.circleDrawFreeAction, MouseMode.CIRCLE_DRAW_FREE_47);
            put(ActionType.circleDrawConcentricAction, MouseMode.CIRCLE_DRAW_CONCENTRIC_48);
            put(ActionType.circleDrawConcentricSelectAction, MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49);
            put(ActionType.circleDrawTwoConcentricAction, MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50);
            put(ActionType.foldedFigureMoveAction, MouseMode.MOVE_CALCULATED_SHAPE_102);
            put(ActionType.o_F_checkAction, MouseMode.FLAT_FOLDABLE_CHECK_63);
            put(ActionType.del_lAction, MouseMode.CREASE_DELETE_OVERLAPPING_64);
            put(ActionType.del_l_XAction, MouseMode.CREASE_DELETE_INTERSECTING_65);
            put(ActionType.select_polygonAction, MouseMode.SELECT_POLYGON_66);
            put(ActionType.unselect_polygonAction, MouseMode.UNSELECT_POLYGON_67);
            put(ActionType.select_lXAction, MouseMode.SELECT_LINE_INTERSECTING_68);
            put(ActionType.unselect_lXAction, MouseMode.UNSELECT_LINE_INTERSECTING_69);
        }};

        for (Map.Entry<ActionType, MouseMode> entry : mouseModeActions.entrySet()){
            actionService.registerAction(actionFactory.setMouseModeAction(entry.getKey(), entry.getValue()));
        }

        // setMouseModeWithUnselect actions
        Map<ActionType, MouseMode> mouseModeWithUnselectActions = new HashMap<>() {{
            put(ActionType.senbun_henkanAction, MouseMode.CHANGE_CREASE_TYPE_4);
            put(ActionType.vertexAddAction, MouseMode.DRAW_POINT_14);
            put(ActionType.vertexDeleteAction, MouseMode.DELETE_POINT_15);
            put(ActionType.toMountainAction, MouseMode.CREASE_MAKE_MOUNTAIN_23);
            put(ActionType.toValleyAction, MouseMode.CREASE_MAKE_VALLEY_24);
            put(ActionType.toEdgeAction, MouseMode.CREASE_MAKE_EDGE_25);
            put(ActionType.toAuxAction, MouseMode.CREASE_MAKE_AUX_60);
            put(ActionType.v_del_ccAction, MouseMode.VERTEX_DELETE_ON_CREASE_41);
            put(ActionType.senbun_henkan2Action, MouseMode.CREASE_TOGGLE_MV_58);
            put(ActionType.replace_lineAction, MouseMode.REPLACE_LINE_TYPE_SELECT_72);
            put(ActionType.del_l_typeAction, MouseMode.DELETE_LINE_TYPE_SELECT_73);
            put(ActionType.senbun_yoke_henkanAction, MouseMode.CREASE_ADVANCE_TYPE_30);
            put(ActionType.sen_tokutyuu_color_henkouAction, MouseMode.CIRCLE_CHANGE_COLOR_59);
            put(ActionType.l1Action, MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53);
            put(ActionType.l2Action, MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54);
            put(ActionType.a1Action, MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55);
            put(ActionType.a2Action, MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56);
            put(ActionType.a3Action, MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57);
            put(ActionType.textAction, MouseMode.TEXT);
        }};

        for (Map.Entry<ActionType, MouseMode> entry : mouseModeWithUnselectActions.entrySet()){
            actionService.registerAction(actionFactory.setMouseModeWithUnselectAction(entry.getKey(), entry.getValue()));
        }

        // setMouseModeWithAfterColorAndUnselect actions
        Map<ActionType, MouseMode> mouseModeWithAfterColorAndUnselectActions = new HashMap<>() {{
            put(ActionType.lengthenCreaseAction, MouseMode.LENGTHEN_CREASE_5);
            put(ActionType.angleBisectorAction, MouseMode.SQUARE_BISECTOR_7);
            put(ActionType.rabbitEarAction, MouseMode.INWARD_8);
            put(ActionType.perpendicularDrawAction, MouseMode.PERPENDICULAR_DRAW_9);
            put(ActionType.symmetricDrawAction, MouseMode.SYMMETRIC_DRAW_10);
            put(ActionType.drawCreaseRestrictedAction, MouseMode.DRAW_CREASE_RESTRICTED_11);
            put(ActionType.senbun_b_nyuryokuAction, MouseMode.LINE_SEGMENT_DIVISION_27);
            put(ActionType.makeFlatFoldableAction, MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38);
            put(ActionType.parallelDrawAction, MouseMode.PARALLEL_DRAW_40);
            put(ActionType.setParallelDrawWidthAction, MouseMode.PARALLEL_DRAW_WIDTH_51);
            put(ActionType.continuousSymmetricDrawAction, MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52);
            put(ActionType.lengthenCrease2Action, MouseMode.LENGTHEN_CREASE_SAME_COLOR_70);
            put(ActionType.foldableLineDrawAction, MouseMode.FOLDABLE_LINE_DRAW_71);
            put(ActionType.regularPolygonAction, MouseMode.POLYGON_SET_NO_CORNERS_29);
        }};

        for (Map.Entry<ActionType, MouseMode> entry : mouseModeWithAfterColorAndUnselectActions.entrySet()){
            actionService.registerAction(actionFactory.setMouseModeWithAfterColorAndUnselectAction(entry.getKey(), entry.getValue()));
        }

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
        actionService.registerAction(new LambdaAction(ActionType.lineStyleChangeAction, applicationModel::advanceLineStyle));

        // - draw actions
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

        // - select and transform actions
        actionService.registerAction(new LambdaAction(ActionType.selectAllAction, mainCreasePatternWorker::select_all));
        actionService.registerAction(new LambdaAction(ActionType.unselectAllAction, mainCreasePatternWorker::unselect_all));
        actionService.registerAction(actionFactory.selectionOperationAction(ActionType.moveAction, CanvasModel.SelectionOperationMode.MOVE_1, MouseMode.CREASE_MOVE_21));
        actionService.registerAction(actionFactory.selectionOperationAction(ActionType.move2p2pAction, CanvasModel.SelectionOperationMode.MOVE4P_2, MouseMode.CREASE_MOVE_4P_31));
        actionService.registerAction(actionFactory.selectionOperationAction(ActionType.copyAction, CanvasModel.SelectionOperationMode.COPY_3, MouseMode.CREASE_COPY_22));
        actionService.registerAction(actionFactory.selectionOperationAction(ActionType.copy2p2pAction, CanvasModel.SelectionOperationMode.COPY4P_4, MouseMode.CREASE_COPY_4P_32));
        actionService.registerAction(actionFactory.selectionOperationAction(ActionType.reflectAction, CanvasModel.SelectionOperationMode.MIRROR_5, MouseMode.DRAW_CREASE_SYMMETRIC_12));
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
        actionService.registerAction(actionFactory.setMouseModeLineTypeDeleteAction(ActionType.lineSegmentDeleteAction, MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.POLY_LINE_0));
        actionService.registerAction(actionFactory.setMouseModeLineTypeDeleteAction(ActionType.edgeLineSegmentDeleteAction, MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.BLACK_LINE_2));
        actionService.registerAction(actionFactory.setMouseModeLineTypeDeleteAction(ActionType.auxLiveLineSegmentDeleteAction, MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.AUX_LIVE_LINE_3));
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
        actionService.registerAction(actionFactory.suiteiAction(ActionType.suitei_01Action, FoldedFigure.EstimationOrder.ORDER_1));
        actionService.registerAction(actionFactory.suiteiAction(ActionType.suitei_02Action, FoldedFigure.EstimationOrder.ORDER_2));
        actionService.registerAction(actionFactory.suiteiAction(ActionType.suitei_03Action, FoldedFigure.EstimationOrder.ORDER_3));
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
        // - transform CP(crease pattern) actions
        actionService.registerAction(new LambdaAction(ActionType.rotateClockwiseAction, cameraModel::decreaseRotation));
        actionService.registerAction(new LambdaAction(ActionType.rotateAnticlockwiseAction, cameraModel::increaseRotation));

        // - background actions
        actionService.registerAction(new LambdaAction(ActionType.transparentAction, canvas::createTransparentBackground));
        actionService.registerAction(new LambdaAction(ActionType.backgroundLockAction, () -> backgroundModel.setLockBackground(!backgroundModel.isLockBackground())));
        actionService.registerAction(new LambdaAction(ActionType.backgroundToggleAction, () -> backgroundModel.setDisplayBackground(!backgroundModel.isDisplayBackground())));

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
        actionService.registerAction(actionFactory.degAction(ActionType.deg1Action, MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_13, AngleSystemModel.AngleSystemInputType.DEG_1));
        actionService.registerAction(actionFactory.degAction(ActionType.deg2Action, MouseMode.ANGLE_SYSTEM_16, AngleSystemModel.AngleSystemInputType.DEG_2));
        actionService.registerAction(actionFactory.degAction(ActionType.deg3Action, MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_2_17, AngleSystemModel.AngleSystemInputType.DEG_3));
        actionService.registerAction(actionFactory.degAction(ActionType.deg4Action, MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_3_18, AngleSystemModel.AngleSystemInputType.DEG_4));
        actionService.registerAction(actionFactory.degAction(ActionType.angleRestrictedAction, MouseMode.DRAW_CREASE_ANGLE_RESTRICTED_5_37, AngleSystemModel.AngleSystemInputType.DEG_5));

        // - other aux actions
        actionService.registerAction(new LambdaAction(ActionType.colOrangeAction, () -> canvasModel.setAuxLiveLineColor(LineColor.ORANGE_4)));
        actionService.registerAction(new LambdaAction(ActionType.colYellowAction, () -> canvasModel.setAuxLiveLineColor(LineColor.YELLOW_7)));
        actionService.registerAction(new LambdaAction(ActionType.h_senhaba_sageAction, applicationModel::decreaseAuxLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.h_senhaba_ageAction, applicationModel::increaseAuxLineWidth));
        actionService.registerAction(new LambdaAction(ActionType.h_undoAction, mainCreasePatternWorker::auxUndo));
        actionService.registerAction(new LambdaAction(ActionType.h_redoAction, mainCreasePatternWorker::auxRedo));
        actionService.registerAction(actionFactory.setMouseModeLineTypeDeleteAction(ActionType.h_senbun_nyuryokuAction, MouseMode.DRAW_CREASE_FREE_1, FoldLineAdditionalInputMode.AUX_LINE_1));
        actionService.registerAction(actionFactory.setMouseModeLineTypeDeleteAction(ActionType.h_senbun_nyuryokuAction, MouseMode.LINE_SEGMENT_DELETE_3, FoldLineAdditionalInputMode.AUX_LINE_1));

        // |---------------------------------------------------------------------------|
        // --- Bottom Panel ---
        // foldedFigure actions
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureToggleAntiAliasAction, foldedFigureModel::toggleAntiAlias));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureToggleShadowAction, foldedFigureModel::toggleDisplayShadows));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureSizeIncreaseAction, () -> {
            animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                    foldedFigureModel::setScale,
                    foldedFigureModel::getScale,
                    scale -> foldedFigureModel.getScaleForZoomBy(-1, applicationModel.getZoomSpeed(), scale),
                    AnimationDurations.ZOOM);
        }));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureSizeDecreaseAction, () -> {
            animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                    foldedFigureModel::setScale,
                    foldedFigureModel::getScale,
                    scale -> foldedFigureModel.getScaleForZoomBy(1, applicationModel.getZoomSpeed(), scale),
                    AnimationDurations.ZOOM);
        }));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureRotateClockwiseAction, () -> {
            double rotation = foldedFigureModel.getState() == FoldedFigure.State.BACK_1 ? foldedFigureModel.getRotation() + 11.25 : foldedFigureModel.getRotation() - 11.25;
            foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(rotation));
        }));
        actionService.registerAction(new LambdaAction(ActionType.foldedFigureRotateAntiClockwiseAction, () -> {
            double rotation = foldedFigureModel.getState() != FoldedFigure.State.BACK_1 ? foldedFigureModel.getRotation() + 11.25 : foldedFigureModel.getRotation() - 11.25;
            foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(rotation));
        }));
        actionService.registerAction(actionFactory.oriagari_sousaAction(ActionType.oriagari_sousaAction, FoldedFigureOperationMode.MODE_1));
        actionService.registerAction(actionFactory.oriagari_sousaAction(ActionType.oriagari_sousa_2Action, FoldedFigureOperationMode.MODE_2));

        // |---------------------------------------------------------------------------|
        // --- AppMenuBar ---
        actionService.registerAction(new LambdaAction(ActionType.IMPORT, fileSaveService::importFile));
        actionService.registerAction(new LambdaAction(ActionType.toggleHelpAction, applicationModel::toggleHelpVisible));

        // --- others ---
        actionService.registerAction(new LambdaAction(ActionType.scaleAction, () -> {animationService.animate(Animations.ZOOM_FOLDED_MODEL,
            foldedFigureModel::setScale,
            foldedFigureModel::getScale,
            1.0,
            AnimationDurations.SCALE_SPEED);
        }));
        actionService.registerAction(new LambdaAction(ActionType.selectAnd3ClickAction, () -> {
            canvasModel.setCkbox_add_frame_SelectAnd3click_isSelected(canvasModel.isCkbox_add_frame_SelectAnd3click_isSelected());
        }));
    }
}