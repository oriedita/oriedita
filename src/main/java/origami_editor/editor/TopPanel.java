package origami_editor.editor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import origami_editor.editor.databinding.*;
import origami_editor.editor.canvas.FoldLineAdditionalInputMode;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
import origami.crease_pattern.element.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

public class TopPanel {
    private final App app;
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
    private JButton backgroundToggleButton;
    private JButton backgroundSetPositionButton;
    private JButton backgroundLockButton;
    private JCheckBox mouseSettingsCheckBox;

    public TopPanel(App app) {
        this.app = app;
        $$$setupUI$$$();

        app.registerButton(operationFrameSelectButton, "operationFrameSelectAction");
        app.registerButton(moveCreasePatternButton, "moveCreasePatternAction");
        app.registerButton(creasePatternZoomOutButton, "creasePatternZoomOutAction");
        app.registerButton(creasePatternZoomInButton, "creasePatternZoomInAction");
        app.registerButton(rotateAnticlockwiseButton, "rotateAnticlockwiseAction");
        app.registerButton(rotateClockwiseButton, "rotateClockwiseAction");
        app.registerButton(senbun_yoke_henkanButton, "senbun_yoke_henkanAction");
        app.registerButton(lineSegmentInternalDivisionRatioSetButton, "lineSegmentInternalDivisionRatioSetAction");
        app.registerButton(drawLineSegmentInternalDivisionRatioButton, "drawLineSegmentInternalDivisionRatioAction");
        app.registerButton(scaleFactorSetButton, "scaleFactorSetAction");
        app.registerButton(rotationSetButton, "rotationSetAction");
        app.registerButton(transparentButton, "transparentAction");
        app.registerButton(backgroundTrimButton, "backgroundTrimAction");
        app.registerButton(readBackgroundButton, "readBackgroundAction");
        app.registerButton(backgroundToggleButton, "backgroundToggleAction");
        app.registerButton(backgroundSetPositionButton, "backgroundSetPositionAction");
        app.registerButton(backgroundLockButton, "backgroundLockAction");
        app.registerButton(mouseSettingsCheckBox, "mouseSettingsAction");

        operationFrameSelectButton.addActionListener(e -> {
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.OPERATION_FRAME_CREATE_61);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        mouseSettingsCheckBox.addActionListener(e -> app.applicationModel.setMouseWheelMovesCreasePattern(mouseSettingsCheckBox.isSelected()));
        lineSegmentInternalDivisionRatioSetButton.addActionListener(e -> {
            getData(app.internalDivisionRatioModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);

            app.repaintCanvas();
        });
        drawLineSegmentInternalDivisionRatioButton.addActionListener(e -> {
            getData(app.internalDivisionRatioModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
        });
        moveCreasePatternButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.MOVE_CREASE_PATTERN_2);

            app.repaintCanvas();
        });
        FoldedFigureModel foldedFigureModel = app.foldedFigureModel;
        creasePatternZoomOutButton.addActionListener(e -> {
            app.creasePatternCameraModel.zoomOut();

            double magnification = 1.0 / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892

            FoldedFigure_Drawer OZi;
            for (int i_oz = 0; i_oz < app.foldedFiguresList.getSize(); i_oz++) {
                OZi = app.foldedFiguresList.getElementAt(i_oz);

                Point t_o2tv = app.canvas.creasePatternCamera.object2TV(app.canvas.creasePatternCamera.getCameraPosition());

                OZi.scale(magnification, t_o2tv);
            }

            foldedFigureModel.zoomOut();
        });
        scaleFactorSetButton.addActionListener(e -> {
            double d_syukusyaku_keisuu_old = app.creasePatternCameraModel.getScale();

            app.creasePatternCameraModel.setScale(app.measuresModel.string2double(scaleFactorTextField.getText(), d_syukusyaku_keisuu_old));
            if (app.creasePatternCameraModel.getScale() != d_syukusyaku_keisuu_old) {
                double magnification = app.creasePatternCameraModel.getScale() / d_syukusyaku_keisuu_old;

                FoldedFigure_Drawer OZi;
                for (int i_oz = 0; i_oz < app.foldedFiguresList.getSize(); i_oz++) {
                    OZi = app.foldedFiguresList.getElementAt(i_oz);

                    Point t_o2tv = app.canvas.creasePatternCamera.object2TV(app.canvas.creasePatternCamera.getCameraPosition());

                    OZi.scale(magnification, t_o2tv);
                }

                foldedFigureModel.setScale(foldedFigureModel.getScale() * magnification);
            }

            app.repaintCanvas();
        });
        creasePatternZoomInButton.addActionListener(e -> {
            app.creasePatternCameraModel.zoomIn();

            double magnification = Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892

            FoldedFigure_Drawer OZi;
            for (int i_oz = 0; i_oz < app.foldedFiguresList.getSize(); i_oz++) {
                OZi = app.foldedFiguresList.getElementAt(i_oz);

                Point t_o2tv = app.canvas.creasePatternCamera.object2TV(app.canvas.creasePatternCamera.getCameraPosition());

                OZi.scale(magnification, t_o2tv);
            }

            foldedFigureModel.zoomIn();
//20180122追加　ここまで
        });
        rotateAnticlockwiseButton.addActionListener(e -> app.creasePatternCameraModel.increaseRotation());
        rotationSetButton.addActionListener(e -> {
            app.creasePatternCameraModel.setRotation(app.measuresModel.string2double(rotationTextField.getText(), app.creasePatternCameraModel.getRotation()));

            app.repaintCanvas();
        });
        rotateClockwiseButton.addActionListener(e -> app.creasePatternCameraModel.decreaseRotation());
        transparentButton.addActionListener(e -> app.canvas.createTransparentBackground());
        backgroundTrimButton.addActionListener(e -> {
            BufferedImage offsc_background = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2_background = offsc_background.createGraphics();
            //背景表示
            Image backgroundImage = app.backgroundModel.getBackgroundImage();

            if ((backgroundImage != null) && app.backgroundModel.isDisplayBackground()) {
                int iw = backgroundImage.getWidth(null);//イメージの幅を取得
                int ih = backgroundImage.getHeight(null);//イメージの高さを取得

                app.canvas.h_cam.setBackgroundWidth(iw);
                app.canvas.h_cam.setBackgroundHeight(ih);

                app.canvas.drawBackground(g2_background, backgroundImage);
            }


//枠設定時の背景を枠内のみ残してトリム 20181204
            if ((app.canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61) && (app.mainCreasePatternWorker.getDrawingStage() == 4)) {//枠線が表示されている状態
                int xmin = (int) app.mainCreasePatternWorker.operationFrameBox.getXMin();
                int xmax = (int) app.mainCreasePatternWorker.operationFrameBox.getXMax();
                int ymin = (int) app.mainCreasePatternWorker.operationFrameBox.getYMin();
                int ymax = (int) app.mainCreasePatternWorker.operationFrameBox.getYMax();

                app.backgroundModel.setBackgroundImage(offsc_background.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin));

                app.canvas.h_cam = new Background_camera();

                app.canvas.background_set(new Point(120.0, 120.0),
                        new Point(120.0 + 10.0, 120.0),
                        new Point(xmin, ymin),
                        new Point((double) xmin + 10.0, ymin));

                if (app.backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                    app.canvas.h_cam.setLocked(true);
                    app.canvas.h_cam.setCamera(app.canvas.creasePatternCamera);
                    app.canvas.h_cam.h3_obj_and_h4_obj_calculation();
                }
            }

            app.repaintCanvas();
        });
        readBackgroundButton.addActionListener(e -> {
            app.readBackgroundImageFromFile();

            app.canvas.h_cam = new Background_camera();//20181202
            if (app.backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                app.canvas.h_cam.setLocked(app.backgroundModel.isLockBackground());
                app.canvas.h_cam.setCamera(app.canvas.creasePatternCamera);
                app.canvas.h_cam.h3_obj_and_h4_obj_calculation();
            }

            app.repaintCanvas();
        });
        backgroundToggleButton.addActionListener(e -> {
            app.backgroundModel.setDisplayBackground(!app.backgroundModel.isDisplayBackground());

            app.repaintCanvas();
        });
        backgroundSetPositionButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.BACKGROUND_CHANGE_POSITION_26);

            app.repaintCanvas();
        });
        backgroundLockButton.addActionListener(e -> {
            app.backgroundModel.setLockBackground(!app.backgroundModel.isLockBackground());

            app.repaintCanvas();
        });
        senbun_yoke_henkanButton.addActionListener(e -> {
            app.canvasModel.setMouseMode(MouseMode.CREASE_ADVANCE_TYPE_30);

            app.mainCreasePatternWorker.unselect_all();
            app.repaintCanvas();
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
        data.setInternalDivisionRatioA(app.measuresModel.string2double(ratioATextField.getText(), data.getInternalDivisionRatioA()));
        data.setInternalDivisionRatioB(app.measuresModel.string2double(ratioBTextField.getText(), data.getInternalDivisionRatioB()));
        data.setInternalDivisionRatioC(app.measuresModel.string2double(ratioCTextField.getText(), data.getInternalDivisionRatioC()));
        data.setInternalDivisionRatioD(app.measuresModel.string2double(ratioDTextField.getText(), data.getInternalDivisionRatioD()));
        data.setInternalDivisionRatioE(app.measuresModel.string2double(ratioETextField.getText(), data.getInternalDivisionRatioE()));
        data.setInternalDivisionRatioF(app.measuresModel.string2double(ratioFTextField.getText(), data.getInternalDivisionRatioF()));
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
        root.setLayout(new GridLayoutManager(1, 13, new Insets(1, 1, 1, 1), 1, 1));
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
        root.add(panel1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioATextField = new JTextField();
        ratioATextField.setColumns(2);
        ratioATextField.setMinimumSize(new Dimension(60, 30));
        ratioATextField.setText("1.0");
        panel2.add(ratioATextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        panel2.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioBTextField = new JTextField();
        ratioBTextField.setColumns(2);
        ratioBTextField.setMinimumSize(new Dimension(60, 30));
        ratioBTextField.setText("0.0");
        panel2.add(ratioBTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel2.add(label2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioCTextField = new JTextField();
        ratioCTextField.setColumns(2);
        ratioCTextField.setMinimumSize(new Dimension(60, 30));
        ratioCTextField.setText("0.0");
        panel2.add(ratioCTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setIcon(new ImageIcon(getClass().getResource("/ppp/tenten.png")));
        panel1.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        panel1.add(panel3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioDTextField = new JTextField();
        ratioDTextField.setColumns(2);
        ratioDTextField.setMinimumSize(new Dimension(60, 30));
        ratioDTextField.setText("0.0");
        panel3.add(ratioDTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        panel3.add(label4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioETextField = new JTextField();
        ratioETextField.setColumns(2);
        ratioETextField.setMinimumSize(new Dimension(60, 30));
        ratioETextField.setText("1.0");
        panel3.add(ratioETextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel3.add(label5, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ratioFTextField = new JTextField();
        ratioFTextField.setColumns(2);
        ratioFTextField.setMinimumSize(new Dimension(60, 30));
        ratioFTextField.setText("2.0");
        panel3.add(ratioFTextField, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), new Dimension(30, -1), null, 0, false));
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
        panel4.add(scaleFactorTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        panel5.add(rotationTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rotationSetButton = new JButton();
        rotationSetButton.setText("S");
        panel5.add(rotationSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rotateClockwiseButton = new JButton();
        rotateClockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_m_kaiten.png")));
        panel5.add(rotateClockwiseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        transparentButton = new JButton();
        transparentButton.setText("T");
        root.add(transparentButton, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundTrimButton = new JButton();
        backgroundTrimButton.setText("Tr");
        root.add(backgroundTrimButton, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), 1, 1));
        root.add(panel6, new GridConstraints(0, 11, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        readBackgroundButton = new JButton();
        readBackgroundButton.setText("BG");
        panel6.add(readBackgroundButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundToggleButton = new JButton();
        backgroundToggleButton.setText("off");
        panel6.add(backgroundToggleButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundSetPositionButton = new JButton();
        backgroundSetPositionButton.setText("S");
        panel6.add(backgroundSetPositionButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backgroundLockButton = new JButton();
        backgroundLockButton.setText("L");
        panel6.add(backgroundLockButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        senbun_yoke_henkanButton = new JButton();
        senbun_yoke_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_yoke_henkan.png")));
        panel6.add(senbun_yoke_henkanButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(0, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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
        if (backgroundModel.isDisplayBackground()) {
            backgroundToggleButton.setBackground(Color.orange);
        } else {
            backgroundToggleButton.setBackground(Color.gray);
        }

        if (backgroundModel.isLockBackground()) {
            backgroundLockButton.setBackground(Color.orange);
        } else {
            backgroundLockButton.setBackground(Color.gray);
        }
    }

    public void setData(CameraModel creasePatternCameraModel) {
        rotationTextField.setText(String.valueOf(creasePatternCameraModel.getRotation()));
        rotationTextField.setCaretPosition(0);
        scaleFactorTextField.setText(String.valueOf(creasePatternCameraModel.getScale()));
        scaleFactorTextField.setCaretPosition(0);
    }
}
