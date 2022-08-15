package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javax.inject.Inject;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;
import oriedita.editor.Canvas;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.databinding.*;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.drawing.tools.Background_camera;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;

import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

@Singleton
public class TopPanel {
    private final MeasuresModel measuresModel;
    private JButton operationFrameSelectButton;
    private JPanel root;
    private JTextField ratioATextField;
    private JButton moveCreasePatternButton;
    private JButton creasePatternZoomOutButton;
    private JButton creasePatternZoomInButton;
    private JButton rotateAnticlockwiseButton;
    private JButton rotateClockwiseButton;
    private JButton senbun_yoke_henkanButton;
    private JButton lineSegmentInternalDivisionRatioSetButton;
    private JButton drawLineSegmentInternalDivisionRatioButton;
    private JTextField ratioBTextField;
    private JTextField ratioCTextField;
    private JTextField ratioDTextField;
    private JTextField ratioETextField;
    private JTextField ratioFTextField;
    private JTextField scaleFactorTextField;
    private JButton scaleFactorSetButton;
    private JTextField rotationTextField;
    private JButton rotationSetButton;
    private JButton transparentButton;
    private JButton backgroundTrimButton;
    private JButton readBackgroundButton;
    private JCheckBox backgroundToggleButton;
    private JButton backgroundSetPositionButton;
    private JCheckBox backgroundLockButton;
    private JCheckBox mouseSettingsCheckBox;
    private JLabel ratioLabel1;
    private JLabel ratioLabel2;
    private JLabel ratioLabel3;
    private JLabel ratioLabel4;
    private JLabel ratioLabel5;

    @Inject
    public TopPanel(MeasuresModel measuresModel,
                    ButtonService buttonService,
                    CanvasModel canvasModel,
                    InternalDivisionRatioModel internalDivisionRatioModel,
                    BackgroundModel backgroundModel,
                    CreasePattern_Worker mainCreasePatternWorker,
                    FoldedFigureModel foldedFigureModel,
                    FileSaveService fileSaveService,
                    CameraModel creasePatternCameraModel,
                    FoldedFiguresList foldedFiguresList,
                    Canvas canvas,
                    ApplicationModel applicationModel) {
        this.measuresModel = measuresModel;

        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));
        internalDivisionRatioModel.addPropertyChangeListener(e -> setData(internalDivisionRatioModel));
        canvas.addPropertyChangeListener(e -> setData(e, canvasModel));
        backgroundModel.addPropertyChangeListener(e -> setData(backgroundModel));
        creasePatternCameraModel.addPropertyChangeListener(e -> setData(creasePatternCameraModel));

        $$$setupUI$$$();

        buttonService.registerButton(operationFrameSelectButton, "operationFrameSelectAction");
        buttonService.registerButton(moveCreasePatternButton, "moveCreasePatternAction");
        buttonService.registerButton(creasePatternZoomOutButton, "creasePatternZoomOutAction");
        buttonService.registerButton(creasePatternZoomInButton, "creasePatternZoomInAction");
        buttonService.registerButton(rotateAnticlockwiseButton, "rotateAnticlockwiseAction");
        buttonService.registerButton(rotateClockwiseButton, "rotateClockwiseAction");
        buttonService.registerButton(senbun_yoke_henkanButton, "senbun_yoke_henkanAction");
        buttonService.registerButton(lineSegmentInternalDivisionRatioSetButton, "lineSegmentInternalDivisionRatioSetAction");
        buttonService.registerButton(drawLineSegmentInternalDivisionRatioButton, "drawLineSegmentInternalDivisionRatioAction");
        buttonService.registerButton(scaleFactorSetButton, "scaleFactorSetAction");
        buttonService.registerButton(rotationSetButton, "rotationSetAction");
        buttonService.registerButton(transparentButton, "transparentAction");
        buttonService.registerButton(backgroundTrimButton, "backgroundTrimAction");
        buttonService.registerButton(readBackgroundButton, "readBackgroundAction");
        buttonService.registerButton(backgroundToggleButton, "backgroundToggleAction");
        buttonService.registerButton(backgroundSetPositionButton, "backgroundSetPositionAction");
        buttonService.registerButton(backgroundLockButton, "backgroundLockAction");
        buttonService.registerButton(mouseSettingsCheckBox, "mouseSettingsAction");

        buttonService.registerLabel(ratioLabel1, "labelPlus");
        buttonService.registerLabel(ratioLabel2, "labelSqrt");
        buttonService.registerLabel(ratioLabel3, "labelColon");
        buttonService.registerLabel(ratioLabel4, "labelPlus");
        buttonService.registerLabel(ratioLabel5, "labelSqrt");

        operationFrameSelectButton.addActionListener(e -> {
            canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            canvasModel.setMouseMode(MouseMode.OPERATION_FRAME_CREATE_61);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

            mainCreasePatternWorker.unselect_all();
        });
        mouseSettingsCheckBox.addActionListener(e -> applicationModel.setMouseWheelMovesCreasePattern(mouseSettingsCheckBox.isSelected()));
        lineSegmentInternalDivisionRatioSetButton.addActionListener(e -> {
            getData(internalDivisionRatioModel);

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);
        });
        drawLineSegmentInternalDivisionRatioButton.addActionListener(e -> {
            getData(internalDivisionRatioModel);

            canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
            canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);

            mainCreasePatternWorker.unselect_all();
        });
        moveCreasePatternButton.addActionListener(e -> canvasModel.setMouseMode(MouseMode.MOVE_CREASE_PATTERN_2));
        creasePatternZoomOutButton.addActionListener(e -> {
            creasePatternCameraModel.zoomOut();

            double magnification = 1.0 / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892

            FoldedFigure_Drawer OZi;
            for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
                OZi = foldedFiguresList.getElementAt(i_oz);

                Point t_o2tv = canvas.creasePatternCamera.object2TV(canvas.creasePatternCamera.getCameraPosition());

                OZi.scale(magnification, t_o2tv);
            }

            foldedFigureModel.zoomOut();
        });
        scaleFactorSetButton.addActionListener(e -> {
            double d_syukusyaku_keisuu_old = creasePatternCameraModel.getScale();

            creasePatternCameraModel.setScale(measuresModel.string2double(scaleFactorTextField.getText(), d_syukusyaku_keisuu_old));
            if (creasePatternCameraModel.getScale() != d_syukusyaku_keisuu_old) {
                double magnification = creasePatternCameraModel.getScale() / d_syukusyaku_keisuu_old;

                FoldedFigure_Drawer OZi;
                for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
                    OZi = foldedFiguresList.getElementAt(i_oz);

                    Point t_o2tv = canvas.creasePatternCamera.object2TV(canvas.creasePatternCamera.getCameraPosition());

                    OZi.scale(magnification, t_o2tv);
                }

                foldedFigureModel.setScale(foldedFigureModel.getScale() * magnification);
            }
        });
        scaleFactorTextField.addActionListener(e -> scaleFactorSetButton.doClick());
        creasePatternZoomInButton.addActionListener(e -> {
            creasePatternCameraModel.zoomIn();

            double magnification = Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892

            FoldedFigure_Drawer OZi;
            for (int i_oz = 0; i_oz < foldedFiguresList.getSize(); i_oz++) {
                OZi = foldedFiguresList.getElementAt(i_oz);

                Point t_o2tv = canvas.creasePatternCamera.object2TV(canvas.creasePatternCamera.getCameraPosition());

                OZi.scale(magnification, t_o2tv);
            }

            foldedFigureModel.zoomIn();
//20180122追加　ここまで
        });
        rotateAnticlockwiseButton.addActionListener(e -> creasePatternCameraModel.increaseRotation());
        rotationSetButton.addActionListener(e -> creasePatternCameraModel.setRotation(measuresModel.string2double(rotationTextField.getText(), creasePatternCameraModel.getRotation())));
        rotationTextField.addActionListener(e -> rotationSetButton.doClick());
        rotateClockwiseButton.addActionListener(e -> creasePatternCameraModel.decreaseRotation());
        transparentButton.addActionListener(e -> canvas.createTransparentBackground());
        backgroundTrimButton.addActionListener(e -> {
            BufferedImage offsc_background = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2_background = offsc_background.createGraphics();
            //背景表示
            Image backgroundImage = backgroundModel.getBackgroundImage();

            if ((backgroundImage != null) && backgroundModel.isDisplayBackground()) {
                int iw = backgroundImage.getWidth(null);//イメージの幅を取得
                int ih = backgroundImage.getHeight(null);//イメージの高さを取得

                canvas.h_cam.setBackgroundWidth(iw);
                canvas.h_cam.setBackgroundHeight(ih);

                canvas.drawBackground(g2_background, backgroundImage);
            }

//枠設定時の背景を枠内のみ残してトリム 20181204
            if ((canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61) && (mainCreasePatternWorker.getDrawingStage() == 4)) {//枠線が表示されている状態
                int xmin = (int) mainCreasePatternWorker.getOperationFrameBox().getXMin();
                int xmax = (int) mainCreasePatternWorker.getOperationFrameBox().getXMax();
                int ymin = (int) mainCreasePatternWorker.getOperationFrameBox().getYMin();
                int ymax = (int) mainCreasePatternWorker.getOperationFrameBox().getYMax();

                backgroundModel.setBackgroundImage(offsc_background.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin));

                canvas.h_cam = new Background_camera();

                backgroundModel.setBackgroundPosition(new Polygon(new Point(120.0, 120.0),
                        new Point(120.0 + 10.0, 120.0),
                        new Point(xmin, ymin),
                        new Point((double) xmin + 10.0, ymin)));

                if (backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                    canvas.h_cam.setLocked(true);
                    canvas.h_cam.setCamera(canvas.creasePatternCamera);
                    canvas.h_cam.h3_obj_and_h4_obj_calculation();
                }
            }
        });
        readBackgroundButton.addActionListener(e -> {
            boolean saved = fileSaveService.readBackgroundImageFromFile();

            if (!saved) return;

            canvas.h_cam = new Background_camera();//20181202
            canvas.h_cam.setLocked(backgroundModel.isLockBackground());

            int iw = backgroundModel.getBackgroundImage().getWidth(null);//イメージの幅を取得
            int ih = backgroundModel.getBackgroundImage().getHeight(null);//イメージの高さを取得

            canvas.h_cam.setBackgroundWidth(iw);
            canvas.h_cam.setBackgroundHeight(ih);

            if (backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                canvas.h_cam.setCamera(canvas.creasePatternCamera);
                canvas.h_cam.h3_obj_and_h4_obj_calculation();
            }
        });
        backgroundToggleButton.addActionListener(e -> {
            backgroundModel.setDisplayBackground(!backgroundModel.isDisplayBackground());
        });
        backgroundSetPositionButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.BACKGROUND_CHANGE_POSITION_26);
        });
        backgroundLockButton.addActionListener(e -> {
            backgroundModel.setLockBackground(!backgroundModel.isLockBackground());
        });
        senbun_yoke_henkanButton.addActionListener(e -> {
            canvasModel.setMouseMode(MouseMode.CREASE_ADVANCE_TYPE_30);

            mainCreasePatternWorker.unselect_all();
        });
    }

    public void setData(ApplicationModel applicationModel) {
        mouseSettingsCheckBox.setSelected(applicationModel.getMouseWheelMovesCreasePattern());
    }

    public void setData(PropertyChangeEvent e, CanvasModel canvasModel) {
        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
            MouseMode m = canvasModel.getMouseMode();

            backgroundSetPositionButton.setSelected(m == MouseMode.BACKGROUND_CHANGE_POSITION_26);
            senbun_yoke_henkanButton.setSelected(m == MouseMode.CREASE_ADVANCE_TYPE_30);
            moveCreasePatternButton.setSelected(m == MouseMode.MOVE_CREASE_PATTERN_2);
            drawLineSegmentInternalDivisionRatioButton.setSelected(m == MouseMode.LINE_SEGMENT_RATIO_SET_28);
            operationFrameSelectButton.setSelected(m == MouseMode.OPERATION_FRAME_CREATE_61);
        }
    }

    public void setData(InternalDivisionRatioModel data) {
        ratioATextField.setText(String.valueOf(data.getInternalDivisionRatioA()));
        ratioBTextField.setText(String.valueOf(data.getInternalDivisionRatioB()));
        ratioCTextField.setText(String.valueOf(data.getInternalDivisionRatioC()));
        ratioDTextField.setText(String.valueOf(data.getInternalDivisionRatioD()));
        ratioETextField.setText(String.valueOf(data.getInternalDivisionRatioE()));
        ratioFTextField.setText(String.valueOf(data.getInternalDivisionRatioF()));
    }

    public void getData(InternalDivisionRatioModel data) {
        data.setInternalDivisionRatioA(measuresModel.string2double(ratioATextField.getText(), data.getInternalDivisionRatioA()));
        data.setInternalDivisionRatioB(measuresModel.string2double(ratioBTextField.getText(), data.getInternalDivisionRatioB()));
        data.setInternalDivisionRatioC(measuresModel.string2double(ratioCTextField.getText(), data.getInternalDivisionRatioC()));
        data.setInternalDivisionRatioD(measuresModel.string2double(ratioDTextField.getText(), data.getInternalDivisionRatioD()));
        data.setInternalDivisionRatioE(measuresModel.string2double(ratioETextField.getText(), data.getInternalDivisionRatioE()));
        data.setInternalDivisionRatioF(measuresModel.string2double(ratioFTextField.getText(), data.getInternalDivisionRatioF()));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(1, 11, new Insets(1, 1, 1, 1), 1, 1));
        operationFrameSelectButton = new JButton();
        operationFrameSelectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tyouhoukei_select.png")));
        root.add(operationFrameSelectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mouseSettingsCheckBox = new JCheckBox();
        mouseSettingsCheckBox.setIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_off.png")));
        mouseSettingsCheckBox.setSelected(false);
        mouseSettingsCheckBox.setSelectedIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_on.png")));
        root.add(mouseSettingsCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioATextField = new JTextField();
        ratioATextField.setColumns(2);
        ratioATextField.setMinimumSize(new Dimension(60, 30));
        ratioATextField.setText("1.0");
        panel2.add(ratioATextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel1 = new JLabel();
        ratioLabel1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        ratioLabel1.setText("");
        panel2.add(ratioLabel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioBTextField = new JTextField();
        ratioBTextField.setColumns(2);
        ratioBTextField.setMinimumSize(new Dimension(60, 30));
        ratioBTextField.setText("0.0");
        panel2.add(ratioBTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel2 = new JLabel();
        ratioLabel2.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel2.add(ratioLabel2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioCTextField = new JTextField();
        ratioCTextField.setColumns(2);
        ratioCTextField.setMinimumSize(new Dimension(60, 30));
        ratioCTextField.setText("0.0");
        panel2.add(ratioCTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel3 = new JLabel();
        ratioLabel3.setIcon(new ImageIcon(getClass().getResource("/ppp/tenten.png")));
        panel1.add(ratioLabel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioDTextField = new JTextField();
        ratioDTextField.setColumns(2);
        ratioDTextField.setMinimumSize(new Dimension(60, 30));
        ratioDTextField.setText("0.0");
        panel3.add(ratioDTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel4 = new JLabel();
        ratioLabel4.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        panel3.add(ratioLabel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioETextField = new JTextField();
        ratioETextField.setColumns(2);
        ratioETextField.setMinimumSize(new Dimension(60, 30));
        ratioETextField.setText("1.0");
        panel3.add(ratioETextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        ratioLabel5 = new JLabel();
        ratioLabel5.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel3.add(ratioLabel5, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioFTextField = new JTextField();
        ratioFTextField.setColumns(2);
        ratioFTextField.setMinimumSize(new Dimension(60, 30));
        ratioFTextField.setText("2.0");
        panel3.add(ratioFTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        lineSegmentInternalDivisionRatioSetButton = new JButton();
        lineSegmentInternalDivisionRatioSetButton.setText("Set");
        panel1.add(lineSegmentInternalDivisionRatioSetButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        drawLineSegmentInternalDivisionRatioButton = new JButton();
        drawLineSegmentInternalDivisionRatioButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_n_nyuryoku.png")));
        panel1.add(drawLineSegmentInternalDivisionRatioButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveCreasePatternButton = new JButton();
        moveCreasePatternButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_idiu.png")));
        root.add(moveCreasePatternButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel4, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        creasePatternZoomOutButton = new JButton();
        creasePatternZoomOutButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_syukusyou.png")));
        panel4.add(creasePatternZoomOutButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scaleFactorTextField = new JTextField();
        scaleFactorTextField.setColumns(2);
        scaleFactorTextField.setText("1.0");
        panel4.add(scaleFactorTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        scaleFactorSetButton = new JButton();
        scaleFactorSetButton.setText("S");
        panel4.add(scaleFactorSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        creasePatternZoomInButton = new JButton();
        creasePatternZoomInButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_kakudai.png")));
        panel4.add(creasePatternZoomInButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel5, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        rotateAnticlockwiseButton = new JButton();
        rotateAnticlockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_p_kaiten.png")));
        panel5.add(rotateAnticlockwiseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rotationTextField = new JTextField();
        rotationTextField.setColumns(2);
        rotationTextField.setEnabled(true);
        panel5.add(rotationTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        rotationSetButton = new JButton();
        rotationSetButton.setText("S");
        panel5.add(rotationSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rotateClockwiseButton = new JButton();
        rotateClockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_m_kaiten.png")));
        panel5.add(rotateClockwiseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel6, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        readBackgroundButton = new JButton();
        readBackgroundButton.setText("Select");
        panel6.add(readBackgroundButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundToggleButton = new JCheckBox();
        backgroundToggleButton.setEnabled(false);
        backgroundToggleButton.setText("Show");
        panel6.add(backgroundToggleButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundLockButton = new JCheckBox();
        backgroundLockButton.setEnabled(false);
        backgroundLockButton.setText("Lock");
        panel6.add(backgroundLockButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(46, 21), null, 0, false));
        senbun_yoke_henkanButton = new JButton();
        senbun_yoke_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_yoke_henkan.png")));
        panel6.add(senbun_yoke_henkanButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundTrimButton = new JButton();
        backgroundTrimButton.setEnabled(false);
        backgroundTrimButton.setText("Trim");
        panel6.add(backgroundTrimButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        transparentButton = new JButton();
        transparentButton.setText("Transparent");
        panel6.add(transparentButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundSetPositionButton = new JButton();
        backgroundSetPositionButton.setText("S");
        panel6.add(backgroundSetPositionButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    public void setData(BackgroundModel backgroundModel) {
        backgroundToggleButton.setEnabled(backgroundModel.getBackgroundImage() != null);
        backgroundLockButton.setEnabled(backgroundModel.getBackgroundImage() != null);

        backgroundTrimButton.setEnabled(backgroundModel.getBackgroundImage() != null);

        backgroundToggleButton.setSelected(backgroundModel.isDisplayBackground());
        backgroundLockButton.setSelected(backgroundModel.isLockBackground());
    }

    public void setData(CameraModel creasePatternCameraModel) {
        rotationTextField.setText(String.valueOf(creasePatternCameraModel.getRotation()));
        rotationTextField.setCaretPosition(0);
        scaleFactorTextField.setText(String.valueOf(creasePatternCameraModel.getScale()));
        scaleFactorTextField.setCaretPosition(0);
    }
}
