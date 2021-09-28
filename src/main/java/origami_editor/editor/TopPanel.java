package origami_editor.editor;

import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing_worker.FoldLineAdditionalInputMode;
import origami_editor.editor.folded_figure.FoldedFigure;
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

        operationFrameSelectButton.addActionListener(e -> {
            app.setHelp("tyouhoukei_select");

            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
            app.canvasModel.setMouseMode(MouseMode.OPERATION_FRAME_CREATE_61);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        mouseSettingsCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_mouse_settei");

            app.canvasModel.setMouseWheelMovesCreasePattern(mouseSettingsCheckBox.isSelected());
        });
        lineSegmentInternalDivisionRatioSetButton.addActionListener(e -> {
            app.setHelp("senbun_naibun_set");

            getData(app.internalDivisionRatioModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        drawLineSegmentInternalDivisionRatioButton.addActionListener(e -> {
            app.setHelp("senbun_n_nyuryoku");

            getData(app.internalDivisionRatioModel);

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        moveCreasePatternButton.addActionListener(e -> {
            app.setHelp("tenkaizu_idiu");

            app.canvasModel.setMouseMode(MouseMode.MOVE_CREASE_PATTERN_2);

            app.repaintCanvas();
        });
        FoldedFigureModel foldedFigureModel = app.foldedFigureModel;
        creasePatternZoomOutButton.addActionListener(e -> {
            app.setHelp("tenkaizu_syukusyou");

            app.creasePatternCameraModel.zoomOut();

            double magnification = 1.0 / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892

            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                OZi = app.foldedFigures.get(i_oz);

                Point t_o2tv = app.canvas.creasePatternCamera.object2TV(app.canvas.creasePatternCamera.getCameraPosition());

                OZi.scale(magnification, t_o2tv);
            }

            foldedFigureModel.zoomOut();
        });
        scaleFactorSetButton.addActionListener(e -> {
            double d_syukusyaku_keisuu_old = app.creasePatternCameraModel.getScale();

            app.creasePatternCameraModel.setScale(app.string2double(scaleFactorTextField.getText(), d_syukusyaku_keisuu_old));
            if (app.creasePatternCameraModel.getScale() != d_syukusyaku_keisuu_old) {
                double magnification = app.creasePatternCameraModel.getScale() / d_syukusyaku_keisuu_old;

                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                    OZi = app.foldedFigures.get(i_oz);

                    Point t_o2tv = app.canvas.creasePatternCamera.object2TV(app.canvas.creasePatternCamera.getCameraPosition());

                    OZi.scale(magnification, t_o2tv);
                }

                foldedFigureModel.setScale(foldedFigureModel.getScale() * magnification);
            }

            app.setHelp("syukusyaku_keisuu_set");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        creasePatternZoomInButton.addActionListener(e -> {
            app.setHelp("tenkaizu_kakudai");

            app.creasePatternCameraModel.zoomIn();

            double magnification = Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892

            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                OZi = app.foldedFigures.get(i_oz);

                Point t_o2tv = app.canvas.creasePatternCamera.object2TV(app.canvas.creasePatternCamera.getCameraPosition());

                OZi.scale(magnification, t_o2tv);
            }

            foldedFigureModel.zoomIn();
//20180122追加　ここまで
        });
        rotateAnticlockwiseButton.addActionListener(e -> {
            app.setHelp("tenkaizu_p_kaiten");

            app.creasePatternCameraModel.increaseRotation();
        });
        rotationSetButton.addActionListener(e -> {
            app.creasePatternCameraModel.setRotation(app.string2double(rotationTextField.getText(), app.creasePatternCameraModel.getRotation()));

            app.setHelp("kaiten_hosei_set");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        rotateClockwiseButton.addActionListener(e -> {
            app.setHelp("tenkaizu_m_kaiten");
            app.creasePatternCameraModel.decreaseRotation();
        });
        transparentButton.addActionListener(e -> {
            app.setHelp("toumei");

            app.createTransparentBackground();
        });
        backgroundTrimButton.addActionListener(e -> {
            app.setHelp("haikei_trim");


            app.offsc_background = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2_background = app.offsc_background.createGraphics();
            //背景表示
            if ((app.img_background != null) && app.backgroundModel.isDisplayBackground()) {
                int iw = app.img_background.getWidth(null);//イメージの幅を取得
                int ih = app.img_background.getHeight(null);//イメージの高さを取得

                app.h_cam.setBackgroundWidth(iw);
                app.h_cam.setBackgroundHeight(ih);

                app.drawBackground(g2_background, app.img_background);
            }


//枠設定時の背景を枠内のみ残してトリム 20181204
            if ((app.mouseMode == MouseMode.OPERATION_FRAME_CREATE_61) && (app.mainDrawingWorker.getDrawingStage() == 4)) {//枠線が表示されている状態
                int xmin = (int) app.mainDrawingWorker.operationFrameBox.getXMin();
                int xmax = (int) app.mainDrawingWorker.operationFrameBox.getXMax();
                int ymin = (int) app.mainDrawingWorker.operationFrameBox.getYMin();
                int ymax = (int) app.mainDrawingWorker.operationFrameBox.getYMax();

                app.img_background = app.offsc_background.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin);

                app.h_cam = new Background_camera();

                app.background_set(new Point(120.0, 120.0),
                        new Point(120.0 + 10.0, 120.0),
                        new Point(xmin, ymin),
                        new Point((double) xmin + 10.0, ymin));

                if (app.backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                    app.h_cam.setLocked(true);
                    app.h_cam.setCamera(app.canvas.creasePatternCamera);
                    app.h_cam.h3_obj_and_h4_obj_calculation();
                }
            }

            app.repaintCanvas();
        });
        readBackgroundButton.addActionListener(e -> {
            app.setHelp("haikei");

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.readImageFromFile();

            app.h_cam = new Background_camera();//20181202
            if (app.backgroundModel.isLockBackground()) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                app.h_cam.setLocked(app.backgroundModel.isLockBackground());
                app.h_cam.setCamera(app.canvas.creasePatternCamera);
                app.h_cam.h3_obj_and_h4_obj_calculation();
            }

            app.repaintCanvas();
        });
        backgroundToggleButton.addActionListener(e -> {
            app.setHelp("haikei_kirikae");

            app.backgroundModel.setDisplayBackground(!app.backgroundModel.isDisplayBackground());

            app.repaintCanvas();
        });
        backgroundSetPositionButton.addActionListener(e -> {
            app.setHelp("set_BG");

            app.canvasModel.setMouseMode(MouseMode.BACKGROUND_CHANGE_POSITION_26);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        backgroundLockButton.addActionListener(e -> {
            app.setHelp("haikei_Lock_on");

            app.backgroundModel.setLockBackground(!app.backgroundModel.isLockBackground());

            app.repaintCanvas();
        });
        senbun_yoke_henkanButton.addActionListener(e -> {
            app.setHelp("senbun_yoke_henkan");

            app.canvasModel.setMouseMode(MouseMode.CREASE_ADVANCE_TYPE_30);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
    }

    public void setData(PropertyChangeEvent e, CanvasModel canvasModel) {
        mouseSettingsCheckBox.setSelected(canvasModel.getMouseWheelMovesCreasePattern());

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
        data.setInternalDivisionRatioA(app.string2double(ratioATextField.getText(), data.getInternalDivisionRatioA()));
        data.setInternalDivisionRatioB(app.string2double(ratioBTextField.getText(), data.getInternalDivisionRatioB()));
        data.setInternalDivisionRatioC(app.string2double(ratioCTextField.getText(), data.getInternalDivisionRatioC()));
        data.setInternalDivisionRatioD(app.string2double(ratioDTextField.getText(), data.getInternalDivisionRatioD()));
        data.setInternalDivisionRatioE(app.string2double(ratioETextField.getText(), data.getInternalDivisionRatioE()));
        data.setInternalDivisionRatioF(app.string2double(ratioFTextField.getText(), data.getInternalDivisionRatioF()));
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
        root.setLayout(new GridBagLayout());
        operationFrameSelectButton = new JButton();
        operationFrameSelectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tyouhoukei_select.png")));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(operationFrameSelectButton, gbc);
        mouseSettingsCheckBox = new JCheckBox();
        mouseSettingsCheckBox.setIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_off.png")));
        mouseSettingsCheckBox.setSelected(false);
        mouseSettingsCheckBox.setSelectedIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_on.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(mouseSettingsCheckBox, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        ratioATextField = new JTextField();
        ratioATextField.setColumns(2);
        ratioATextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(ratioATextField, gbc);
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel2.add(label1, gbc);
        ratioBTextField = new JTextField();
        ratioBTextField.setColumns(2);
        ratioBTextField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(ratioBTextField, gbc);
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel2.add(label2, gbc);
        ratioCTextField = new JTextField();
        ratioCTextField.setColumns(2);
        ratioCTextField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(ratioCTextField, gbc);
        final JLabel label3 = new JLabel();
        label3.setIcon(new ImageIcon(getClass().getResource("/ppp/tenten.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel1.add(label3, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel3, gbc);
        ratioDTextField = new JTextField();
        ratioDTextField.setColumns(2);
        ratioDTextField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(ratioDTextField, gbc);
        final JLabel label4 = new JLabel();
        label4.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel3.add(label4, gbc);
        ratioETextField = new JTextField();
        ratioETextField.setColumns(2);
        ratioETextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(ratioETextField, gbc);
        final JLabel label5 = new JLabel();
        label5.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel3.add(label5, gbc);
        ratioFTextField = new JTextField();
        ratioFTextField.setColumns(2);
        ratioFTextField.setText("2.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(ratioFTextField, gbc);
        lineSegmentInternalDivisionRatioSetButton = new JButton();
        lineSegmentInternalDivisionRatioSetButton.setText("Set");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(lineSegmentInternalDivisionRatioSetButton, gbc);
        drawLineSegmentInternalDivisionRatioButton = new JButton();
        drawLineSegmentInternalDivisionRatioButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_n_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(drawLineSegmentInternalDivisionRatioButton, gbc);
        moveCreasePatternButton = new JButton();
        moveCreasePatternButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_idiu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(moveCreasePatternButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel4, gbc);
        creasePatternZoomOutButton = new JButton();
        creasePatternZoomOutButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_syukusyou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(creasePatternZoomOutButton, gbc);
        scaleFactorTextField = new JTextField();
        scaleFactorTextField.setColumns(2);
        scaleFactorTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(scaleFactorTextField, gbc);
        scaleFactorSetButton = new JButton();
        scaleFactorSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(scaleFactorSetButton, gbc);
        creasePatternZoomInButton = new JButton();
        creasePatternZoomInButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_kakudai.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(creasePatternZoomInButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel5, gbc);
        rotateAnticlockwiseButton = new JButton();
        rotateAnticlockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_p_kaiten.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotateAnticlockwiseButton, gbc);
        rotationTextField = new JTextField();
        rotationTextField.setColumns(2);
        rotationTextField.setEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotationTextField, gbc);
        rotationSetButton = new JButton();
        rotationSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotationSetButton, gbc);
        rotateClockwiseButton = new JButton();
        rotateClockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_m_kaiten.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotateClockwiseButton, gbc);
        transparentButton = new JButton();
        transparentButton.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(transparentButton, gbc);
        backgroundTrimButton = new JButton();
        backgroundTrimButton.setText("Tr");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(backgroundTrimButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel6, gbc);
        readBackgroundButton = new JButton();
        readBackgroundButton.setText("BG");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(readBackgroundButton, gbc);
        backgroundToggleButton = new JButton();
        backgroundToggleButton.setText("off");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(backgroundToggleButton, gbc);
        backgroundSetPositionButton = new JButton();
        backgroundSetPositionButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(backgroundSetPositionButton, gbc);
        backgroundLockButton = new JButton();
        backgroundLockButton.setText("L");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(backgroundLockButton, gbc);
        senbun_yoke_henkanButton = new JButton();
        senbun_yoke_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_yoke_henkan.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(senbun_yoke_henkanButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        root.add(spacer1, gbc);
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
