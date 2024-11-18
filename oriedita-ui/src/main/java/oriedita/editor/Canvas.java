package oriedita.editor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.MouseWheelTarget;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Background_camera;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.MouseModeHandler;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FoldedFigureCanvasSelectService;
import oriedita.editor.swing.component.BulletinBoard;
import oriedita.editor.swing.component.TextEditingArea;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Rectangle;
import origami.folding.FoldedFigure;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Panel in the center of the main view.
 */
@ApplicationScoped
public class Canvas implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFiguresList foldedFiguresList;
    private final BackgroundModel backgroundModel;
    private final BulletinBoard bulletinBoard;
    private final ApplicationModel applicationModel;
    private final CameraModel creasePatternCameraModel;
    private final FoldedFigureModel foldedFigureModel;
    private final GridModel gridModel;
    private final Instance<MouseModeHandler> handlerList;
    private final AngleSystemModel angleSystemModel;
    private final FoldedFigureCanvasSelectService foldedFigureCanvasSelectService;
    private final CanvasModel canvasModel;
    private final TextWorker textWorker;
    private final SelectedTextModel textModel;
    private final AnimationService animationService;
    private MouseModeHandler activeMouseHandler;


    private TextEditingArea cpTextEditingArea;

    private final Set<Integer> pressedButtons = new HashSet<>(); // keeps track of which mouse buttons are pressed.
    private Point mouse_temp0 = new Point();//マウスの動作対応時に、一時的に使うTen

    private MouseMode mouseMode;

    private boolean mouseWheelMovesCreasePattern;

    private final Camera creasePatternCamera;

    private final Map<MouseMode, MouseModeHandler> mouseModeHandlers = new HashMap<>();

    private final FrameProvider frameProvider;

    private final ButtonService buttonService;

    private final CanvasUI canvasUI;

    public static String userWarningMessage = null;

    public static void setUserWarningMessage(String uwm) {
        Canvas.userWarningMessage = uwm;
    }

    public static void clearUserWarningMessage() {
        Canvas.userWarningMessage = null;
    }

    public CanvasUI getCanvasImpl() {
        return canvasUI;
    }

    @Inject
    public Canvas(@Named("creasePatternCamera") Camera creasePatternCamera,
                  FrameProvider frameProvider,
                  @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                  Instance<CanvasUI> canvasUIProvider,
                  FoldedFiguresList foldedFiguresList,
                  BackgroundModel backgroundModel,
                  BulletinBoard bulletinBoard,
                  ApplicationModel applicationModel,
                  CameraModel creasePatternCameraModel,
                  FoldedFigureModel foldedFigureModel,
                  GridModel gridModel,
                  @Any Instance<MouseModeHandler> handlerList,
                  AngleSystemModel angleSystemModel,
                  FoldedFigureCanvasSelectService foldedFigureCanvasSelectService,
                  @Any CanvasModel canvasModel,
                  TextWorker textWorker,
                  SelectedTextModel textModel,
                  AnimationService animationService,
                  ButtonService buttonService) {
        this.canvasUI = canvasUIProvider.get();
        this.creasePatternCamera = creasePatternCamera;
        this.frameProvider = frameProvider;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.foldedFiguresList = foldedFiguresList;
        this.backgroundModel = backgroundModel;
        this.bulletinBoard = bulletinBoard;
        this.applicationModel = applicationModel;
        this.creasePatternCameraModel = creasePatternCameraModel;
        this.foldedFigureModel = foldedFigureModel;
        this.gridModel = gridModel;
        this.handlerList = handlerList;
        this.angleSystemModel = angleSystemModel;
        this.foldedFigureCanvasSelectService = foldedFigureCanvasSelectService;
        this.canvasModel = canvasModel;
        this.textWorker = textWorker;
        this.textModel = textModel;
        this.animationService = animationService;
        this.buttonService = buttonService;
    }

    public void init() {
        canvasUI.init();
        canvasUI.setLayout(null);
        cpTextEditingArea = new TextEditingArea(textModel, textWorker, mainCreasePatternWorker,
                canvasModel, creasePatternCameraModel);
        cpTextEditingArea.setBounds(0, 0, 300, 100);
        cpTextEditingArea.setVisible(false);
        canvasUI.add(cpTextEditingArea);

        cpTextEditingArea.setupListeners();

        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));
        canvasModel.addPropertyChangeListener(e -> setData(e, canvasModel));
        backgroundModel.addPropertyChangeListener(e -> setData(e, backgroundModel));

        creasePatternCameraModel.addPropertyChangeListener(e -> canvasUI.repaint());
        foldedFigureModel.addPropertyChangeListener(e -> canvasUI.repaint());
        gridModel.addPropertyChangeListener(e -> canvasUI.repaint());
        angleSystemModel.addPropertyChangeListener(e -> canvasUI.repaint());
        bulletinBoard.addChangeListener(e -> canvasUI.repaint());

        foldedFiguresList.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {

            }

            @Override
            public void intervalRemoved(ListDataEvent e) {

            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                canvasUI.repaint();
            }
        });

        canvasUI.addMouseListener(this);
        canvasUI.addMouseMotionListener(this);
        canvasUI.addMouseWheelListener(this);

        var dim = canvasUI.getSize();

        Logger.info(" dim 001 :" + dim.width + " , " + dim.height);//多分削除可能

        for (MouseModeHandler handler : handlerList) {
            mouseModeHandlers.put(handler.getMouseMode(), handler);
        }
    }

    public void writeImageFile(File file) {
        canvasUI.writeImageFile(file);
    }

    public void updateBackgroundCamera() {
        canvasUI.updateBackgroundCamera();
    }

    public void drawBackground(Graphics2D g2h, Image imgh) {
        canvasUI.drawBackground(g2h, imgh);
    }

    public void createMenuItem(JPopupMenu popupMenu, String action, String text) {
        JMenuItem item = new JMenuItem();
        item.setActionCommand(action);
        popupMenu.add(item);
        item.setText(text);
    }

    // マウス操作(マウスが動いた時)を行う関数----------------------------------------------------
    public void mouseMoved(MouseEvent e) {
        //何もしない
        //  final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();//これは多分J2SE 5.0「Tiger」以降で作動するコード

        Point p = e2p(e);
        canvasUI.setMousePosition(p);

        mainCreasePatternWorker.setCamera(creasePatternCamera);

        if (mouseModeHandlers.containsKey(mouseMode)) {
            mouseModeHandlers.get(mouseMode).mouseMoved(p, e);
        }

        canvasUI.repaint();
    }

    //マウス操作(ボタンを押したとき)を行う関数----------------------------------------------------
    public void mousePressed(MouseEvent e) {
        Point p = e2p(e);
        canvasUI.requestFocus();

        int pressedButton = e.getButton();
        if (e.isMetaDown()) {
            pressedButton = MouseEvent.BUTTON2;
        }
        pressedButtons.add(pressedButton);

        if (mouseModeHandlers.containsKey(mouseMode)) {
            MouseModeHandler handler = mouseModeHandlers.get(mouseMode);
            if (handler.accepts(e, pressedButton)) {
                handler.mousePressed(p, e, pressedButton);
                setActiveMouseHandler(handler);
                mainCreasePatternWorker.setCamera(creasePatternCamera);
                canvasUI.repaint();
                return;
            }
        }

        //---------ボタンの種類による動作変更-----------------------------------------
        switch (pressedButton) {
            case MouseEvent.BUTTON2:
                Logger.info("中ボタンクリック");

                MouseWheelTarget target = foldedFigureCanvasSelectService.pointInCreasePatternOrFoldedFigure(p);

                Logger.info("i_cp_or_oriagari = " + target);

                FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

                switch (target) {
                    case CREASE_PATTERN_0: // 展開図移動。
                        creasePatternCamera.camera_position_specify_from_TV(p);
                        break;
                    case FOLDED_FRONT_1:
                        if (selectedFigure != null)
                            selectedFigure.getFoldedFigureFrontCamera().camera_position_specify_from_TV(p);
                        break;
                    case FOLDED_BACK_2:
                        if (selectedFigure != null)
                            selectedFigure.getFoldedFigureRearCamera().camera_position_specify_from_TV(p);
                        break;
                    case TRANSPARENT_FRONT_3:
                        if (selectedFigure != null)
                            selectedFigure.getTransparentFrontCamera().camera_position_specify_from_TV(p);
                        break;
                    case TRANSPARENT_BACK_4:
                        if (selectedFigure != null)
                            selectedFigure.getTransparentRearCamera().camera_position_specify_from_TV(p);
                        break;
                }

                mouse_temp0 = p;
                canvasUI.repaint();
                return;
            case MouseEvent.BUTTON3:
                mainCreasePatternWorker.setCamera(creasePatternCamera);
                activeMouseHandler.reset();
                MouseWheelTarget rightCLickTarget = foldedFigureCanvasSelectService.pointInCreasePatternOrFoldedFigure(p);

                switch (rightCLickTarget) {
                    case CREASE_PATTERN_0:
                        if (activeMouseHandler.getMouseMode() != MouseMode.LINE_SEGMENT_DELETE_3) {
                            mainCreasePatternWorker.setFoldLineAdditional(FoldLineAdditionalInputMode.BOTH_4);
                        }
                        mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3).mousePressed(p, e, pressedButton);
                        setActiveMouseHandler(mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3));
                        break;
                    case FOLDED_FRONT_1:
                    case FOLDED_BACK_2:
                    case TRANSPARENT_FRONT_3:
                    case TRANSPARENT_BACK_4:
                        JPopupMenu foldPopUp = new JPopupMenu();

                        createMenuItem(foldPopUp, "foldedFigureFlipAction", "Flip");
                        createMenuItem(foldPopUp, "scaleAction", "Scale");
                        createMenuItem(foldPopUp, "foldedFigureTrashAction", "Delete");
                        createMenuItem(foldPopUp, "duplicateFoldedModelAction", "Duplicate");
                        if (foldedFiguresList.getActiveItem().getFoldedFigure().estimationStep != FoldedFigure.EstimationStep.STEP_10) {
                            createMenuItem(foldPopUp, "suitei_02Action", "Wireframe");
                            createMenuItem(foldPopUp, "suitei_03Action", "X-ray");
                        }

                        buttonService.addDefaultListener(foldPopUp, false);
                        foldPopUp.show(this.canvasUI, e.getX(), e.getY());
                        break;
                    default:
                        break;
                }
                canvasUI.repaint();

        }

        mainCreasePatternWorker.setCamera(creasePatternCamera);

        canvasUI.repaint();
    }

    //マウス操作(ドラッグしたとき)を行う関数---------- Logger.info("A");------------------------------------------
    public void mouseDragged(MouseEvent e) {
        Point p = e2p(e);
        canvasUI.setMousePosition(p);

        Set<Integer> unhandledButtons = new HashSet<>(pressedButtons);
        if (mouseModeHandlers.containsKey(mouseMode)) {
            MouseModeHandler handler = mouseModeHandlers.get(mouseMode);
            for (int btn : pressedButtons) {
                if (handler.accepts(e, btn)) {
                    handler.mouseDragged(p, e);
                    setActiveMouseHandler(handler);
                    canvasUI.repaint();
                    unhandledButtons.remove(btn);
                }
            }
        }
        for (int btn : unhandledButtons) {

            switch (btn) {
                case MouseEvent.BUTTON1:
                    break;
                case MouseEvent.BUTTON2:
                    FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

                    switch (canvasModel.getMouseInCpOrFoldedFigure()) {
                        case CREASE_PATTERN_0: // 展開図移動。
                            creasePatternCamera.displayPositionMove(mouse_temp0.delta(p));
                            mainCreasePatternWorker.setCamera(creasePatternCamera);
                            cpTextEditingArea.update();
                            break;
                        case FOLDED_FRONT_1:
                            if (selectedFigure != null)
                                selectedFigure.getFoldedFigureFrontCamera().displayPositionMove(mouse_temp0.delta(p));
                            break;
                        case FOLDED_BACK_2:
                            if (selectedFigure != null)
                                selectedFigure.getFoldedFigureRearCamera().displayPositionMove(mouse_temp0.delta(p));
                            break;
                        case TRANSPARENT_FRONT_3:
                            if (selectedFigure != null)
                                selectedFigure.getTransparentFrontCamera().displayPositionMove(mouse_temp0.delta(p));
                            break;
                        case TRANSPARENT_BACK_4:
                            if (selectedFigure != null)
                                selectedFigure.getTransparentRearCamera().displayPositionMove(mouse_temp0.delta(p));
                            break;
                    }

                    mouse_temp0 = p;
                    canvasUI.repaint();
                    continue;

                case MouseEvent.BUTTON3:
                    mainCreasePatternWorker.setCamera(creasePatternCamera);
                    if (canvasModel.getMouseInCpOrFoldedFigure() == MouseWheelTarget.CREASE_PATTERN_0) {
                        mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3).mouseDragged(p, e);
                        setActiveMouseHandler(mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3));
                    }
            }

            mainCreasePatternWorker.setCamera(creasePatternCamera);

            canvasUI.repaint();
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
        Point p = e2p(e);

        if (!pressedButtons.contains(e.getButton())) {
            // When pressing this button the meta key was held down.
            pressedButtons.remove(MouseEvent.BUTTON2);
        } else {
            pressedButtons.remove(e.getButton());
        }

        mainCreasePatternWorker.setCamera(creasePatternCamera);
        if (mouseModeHandlers.containsKey(mouseMode)) {
            MouseModeHandler handler = mouseModeHandlers.get(mouseMode);
            if (handler.accepts(e, e.getButton())) {
                handler.mouseReleased(p, e);
                setActiveMouseHandler(handler);
                canvasUI.repaint();
                return;
            }

        }

        //---------ボタンの種類による動作変更-----------------------------------------
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                //
                break;
            case MouseEvent.BUTTON2:
                FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
                switch (canvasModel.getMouseInCpOrFoldedFigure()) {
                    case CREASE_PATTERN_0:
                        creasePatternCamera.displayPositionMove(mouse_temp0.delta(p));
                        mainCreasePatternWorker.setCamera(creasePatternCamera);
                        // Move all other objects along.
                        break;
                    case FOLDED_FRONT_1:
                        if (selectedFigure != null)
                            selectedFigure.getFoldedFigureFrontCamera().displayPositionMove(mouse_temp0.delta(p));
                        break;
                    case FOLDED_BACK_2:
                        if (selectedFigure != null)
                            selectedFigure.getFoldedFigureRearCamera().displayPositionMove(mouse_temp0.delta(p));
                        break;
                    case TRANSPARENT_FRONT_3:
                        if (selectedFigure != null)
                            selectedFigure.getTransparentFrontCamera().displayPositionMove(mouse_temp0.delta(p));
                        break;
                    case TRANSPARENT_BACK_4:
                        if (selectedFigure != null)
                            selectedFigure.getTransparentRearCamera().displayPositionMove(mouse_temp0.delta(p));
                        break;
                }

                mouse_temp0 = p;
                canvasUI.repaint();
                return;//
            case MouseEvent.BUTTON3:
                mainCreasePatternWorker.setCamera(creasePatternCamera);
                if (canvasModel.getMouseInCpOrFoldedFigure() == MouseWheelTarget.CREASE_PATTERN_0) {
                    //if(i_mouse_undo_redo_mode==1){i_mouse_undo_redo_mode=0;mainDrawingWorker.unselect_all();Button_kyoutuu_sagyou();mainDrawingWorker.modosi_i_orisen_hojyosen();return;}
                    mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3).mouseReleased(p, e);
                    setActiveMouseHandler(mouseModeHandlers.get(MouseMode.LINE_SEGMENT_DELETE_3));
                    canvasModel.restoreFoldLineAdditionalInputMode();
                    //線分削除モード。
                }
                canvasUI.repaint();//なんでここにrepaintがあるか検討した方がよいかも。20181208


                //----------------------------Logger.info("a");-----------------------
                //}  //20201010　コメントアウト


        }
        canvasUI.repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (mouseWheelMovesCreasePattern) {
            Point p = e2p(e);
            MouseWheelTarget target = foldedFigureCanvasSelectService.pointInCreasePatternOrFoldedFigure(p);

            double scrollDistance = applicationModel.isPreciseZoom() ? e.getPreciseWheelRotation() : e.getWheelRotation();
            if (target == MouseWheelTarget.CREASE_PATTERN_0) {

                animationService.animate(Animations.ZOOM_CP,
                        creasePatternCameraModel::setScale,
                        creasePatternCameraModel::getScale,
                        scale -> creasePatternCameraModel.getScaleForZoomBy(scrollDistance, applicationModel.getZoomSpeed(), scale),
                        AnimationDurations.ZOOM);

            } else {
                animationService.animate(Animations.ZOOM_FOLDED_MODEL,
                        foldedFigureModel::setScale,
                        foldedFigureModel::getScale,
                        scale -> foldedFigureModel.getScaleForZoomBy(scrollDistance, applicationModel.getZoomSpeed(), scale),
                        AnimationDurations.ZOOM);
            }

            canvasUI.setMousePosition(p);
            canvasUI.repaint();
        }
    }

    // -----------------------------------mmmmmmmmmmmmmm-------


    public void setData(ApplicationModel applicationModel) {
        canvasUI.setData(applicationModel);
        mouseWheelMovesCreasePattern = applicationModel.getMouseWheelMovesCreasePattern();
        Logger.debug("repainting");
        canvasUI.repaint();
    }

    public void setData(PropertyChangeEvent e, CanvasModel canvasModel) {

        if (Objects.equals(e.getPropertyName(), "mouseMode")) {
            if (activeMouseHandler != null) {
                activeMouseHandler.reset();
            }
        }
        mouseMode = canvasModel.getMouseMode();
        if (mouseModeHandlers.containsKey(mouseMode)) {
            setActiveMouseHandler(mouseModeHandlers.get(mouseMode));
        }

        canvasUI.repaint();
    }

    //=============================================================================
    //Method called when the mouse wheel rotates
    //=============================================================================
    public void mouse_object_position(Point p) {//この関数はmouseMoved等と違ってマウスイベントが起きても自動では認識されない
        canvasUI.setMousePosition(p);
    }

    public void setData(PropertyChangeEvent e, BackgroundModel backgroundModel) {
        if (e.getPropertyName() == null || e.getPropertyName().equals("backgroundPosition")) {
            background_set(backgroundModel.getBackgroundPosition());
        }

        var h_cam = canvasUI.getH_cam();

        h_cam.setLocked(backgroundModel.isLockBackground());

        if (backgroundModel.isLockBackground()) {
            h_cam.setCamera(creasePatternCamera);
            h_cam.h3_obj_and_h4_obj_calculation();
        }
    }

    //----------------------------------------------------------------------
    //Functions that perform mouse operations (move and button operations)------------------------------
    //----------------------------------------------------------------------
    // ------------------------------------------------------
    public void background_set(Rectangle position) {
        var h_cam = canvasUI.getH_cam();

        h_cam.set_h1(position.getP1());
        h_cam.set_h2(position.getP2());
        h_cam.set_h3(position.getP3());
        h_cam.set_h4(position.getP4());

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
        java.awt.Rectangle canvasBounds = canvasUI.getBounds();

        java.awt.Point canvasLocation = canvasUI.getLocationOnScreen();
        java.awt.Rectangle bounds = new java.awt.Rectangle(canvasLocation.x, canvasLocation.y, canvasBounds.width, canvasBounds.height);

        java.awt.Point currentLocation = frameProvider.get().getLocation();
        Dimension size = frameProvider.get().getSize();

        // Move all associated windows outside the bounds.
        Window[] windows = frameProvider.get().getOwnedWindows();
        java.util.Queue<java.awt.Point> locations = new LinkedList<>();
        frameProvider.get().setLocation(currentLocation.x, currentLocation.y + size.height);
        for (Window w : windows) {
            java.awt.Point loc = w.getLocation();
            locations.offer(loc);
            w.setLocation(loc.x, loc.y + size.height);
        }

        backgroundModel.setBackgroundImage(robot.createScreenCapture(bounds));

        // Move all associated windows back.
        frameProvider.get().setLocation(currentLocation);
        for (Window w : windows) {
            w.setLocation(Objects.requireNonNull(locations.poll()));
        }

        Logger.info("新背景カメラインスタンス化");
        canvasUI.setH_cam(new Background_camera());

        backgroundModel.setBackgroundPosition(new Rectangle(new Point(120.0, 120.0),
                new Point(120.0 + 10.0, 120.0),
                new Point(0, 0),
                new Point(10.0, 0)));

        //Set each condition for background display
        backgroundModel.setDisplayBackground(true);

        if (backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
            var h_cam = canvasUI.getH_cam();
            h_cam.setLocked(true);
            h_cam.setCamera(creasePatternCamera);
            h_cam.h3_obj_and_h4_obj_calculation();
        }

        canvasUI.repaint();
    }

    public void setActiveMouseHandler(MouseModeHandler activeMouseHandler) {
        this.activeMouseHandler = activeMouseHandler;
        canvasUI.setActiveMouseHandler(activeMouseHandler);
    }

    public Point e2p(MouseEvent e) {
        double offset = 0.0;
        if (applicationModel.getDisplayPointOffset()) {
            offset = creasePatternCamera.getCameraZoomX() * mainCreasePatternWorker.getSelectionDistance();
        }
        return new Point(e.getX() - (int) offset, e.getY() - (int) offset);
    }

    public void setHideOperationFrame(boolean hideOperationFrame) {
        canvasUI.setHideOperationFrame(hideOperationFrame);
    }

    public Background_camera getH_cam() {
        return canvasUI.getH_cam();
    }

    public void setH_cam(Background_camera h_cam) {
        canvasUI.setH_cam(h_cam);
    }

    public Camera getCreasePatternCamera() {
        return creasePatternCamera;
    }
}

