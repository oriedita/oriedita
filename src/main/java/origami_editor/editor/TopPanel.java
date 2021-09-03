package origami_editor.editor;

import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.InternalDivisionRatioModel;
import origami_editor.editor.drawing_worker.DrawingWorker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.graphic2d.oritacalc.OritaCalc;
import origami_editor.graphic2d.point.Point;
import origami_editor.tools.background_camera.Background_camera;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TopPanel extends JPanel {
    private final App app;
    private JButton tyouhoukei_selectButton;
    private JPanel rootPanel;
    private JTextField ratioATextField;
    private JButton writeImageButton;
    private JButton tenkaizu_idiuButton;
    private JButton tenkaizu_syukusyouButton;
    private JButton tenkaizu_kakudaiButton;
    private JButton tenkaizu_p_kaitenButton;
    private JButton tenkaizu_m_kaitenButton;
    private JButton senbun_yoke_henkanButton;
    private JButton lineSegmentInternalDivisionRatioSetButton;
    private JButton drawLineSegmentInternalDivisionRatioButton;
    private JTextField ratioBTextField;
    private JTextField ratioCTextField;
    private JTextField ratioDTextField;
    private JTextField ratioETextField;
    private JTextField ratioFTextField;
    private JTextField scaleFactorTextField;
    private JButton tenkaizu_syukusyouSetButton;
    private JTextField rotationTextField;
    private JButton tenkaizu_kaitenSetButton;
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
        tyouhoukei_selectButton.addActionListener(e -> {
            app.setHelp("tyouhoukei_select");
            app.foldLineAdditionalInputMode = DrawingWorker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            app.mainDrawingWorker.setFoldLineAdditional(app.foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            app.mouseMode = MouseMode.OPERATION_FRAME_CREATE_61;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_FREE_1;
            System.out.println("mouseMode = " + app.mouseMode);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        writeImageButton.addActionListener(e -> {
            app.setHelp("writeImage");
            if (app.mouseMode != MouseMode.OPERATION_FRAME_CREATE_61) {
                app.Button_shared_operation();
                app.mainDrawingWorker.setDrawingStage(0);
            }//枠設定時(==61)には、その枠を消さないためにes1.set_i_egaki_dankaiを０にしないでおく　20180524
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.writeImage();
            app.repaintCanvas();
        });
        mouseSettingsCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_mouse_settei");

            app.canvasModel.setMouseWheelMovesCreasePattern(mouseSettingsCheckBox.isSelected());

            app.updateCanvas();
        });
        lineSegmentInternalDivisionRatioSetButton.addActionListener(e -> {
            app.setHelp("senbun_naibun_set");

            getData(app.internalDivisionRatioModel);
            app.updateInternalDivisionRatio();

            app.mouseMode = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            System.out.println("mouseMode = " + app.mouseMode);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        drawLineSegmentInternalDivisionRatioButton.addActionListener(e -> {
            app.setHelp("senbun_n_nyuryoku");

            getData(app.internalDivisionRatioModel);
            app.updateInternalDivisionRatio();

            app.mouseMode = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            System.out.println("mouseMode = " + app.mouseMode);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        tenkaizu_idiuButton.addActionListener(e -> {
            app.setHelp("tenkaizu_idiu");

            app.mouseMode = MouseMode.MOVE_CREASE_PATTERN_2;
            System.out.println("mouseMode = " + app.mouseMode);

            app.repaintCanvas();
        });
        tenkaizu_syukusyouButton.addActionListener(e -> {
            app.setHelp("tenkaizu_syukusyou");

            double d_bairitu = 1.0 / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.scaleFactor = app.scaleFactor / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.camera_of_orisen_input_diagram.multiplyCameraZoomX(d_bairitu);
            app.camera_of_orisen_input_diagram.multiplyCameraZoomY(d_bairitu);

//20180122追加
            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                OZi = app.foldedFigures.get(i_oz);

                Point t_o2tv = app.camera_of_orisen_input_diagram.object2TV(app.camera_of_orisen_input_diagram.getCameraPosition());

                OZi.scale(d_bairitu, t_o2tv);
            }

            app.foldedFigureModel.setScale(app.foldedFigureModel.getScale() * d_bairitu);
            app.updateFoldedFigure();
//20180122追加　ここまで


            scaleFactorTextField.setText(String.valueOf(app.scaleFactor));
            scaleFactorTextField.setCaretPosition(0);
            app.repaintCanvas();
        });
        tenkaizu_syukusyouSetButton.addActionListener(e -> {
            double d_syukusyaku_keisuu_old = app.scaleFactor;
            app.scaleFactor = app.String2double(scaleFactorTextField.getText(), d_syukusyaku_keisuu_old);
            if (app.scaleFactor <= 0.0) {
                app.scaleFactor = d_syukusyaku_keisuu_old;
            }
            scaleFactorTextField.setText(String.valueOf(app.scaleFactor));
            if (app.scaleFactor != d_syukusyaku_keisuu_old) {
                app.camera_of_orisen_input_diagram.setCameraZoomX(app.scaleFactor);
                app.camera_of_orisen_input_diagram.setCameraZoomY(app.scaleFactor);

//20180225追加

                double d_bairitu = app.scaleFactor / d_syukusyaku_keisuu_old;

                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                    OZi = app.foldedFigures.get(i_oz);

                    Point t_o2tv = app.camera_of_orisen_input_diagram.object2TV(app.camera_of_orisen_input_diagram.getCameraPosition());

                    OZi.scale(d_bairitu, t_o2tv);
                }

                app.foldedFigureModel.setScale(app.foldedFigureModel.getScale() * d_bairitu);

                app.updateFoldedFigure();
            }
            scaleFactorTextField.setText(String.valueOf(app.scaleFactor));
            scaleFactorTextField.setCaretPosition(0);
            app.repaintCanvas();

            app.setHelp("syukusyaku_keisuu_set");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        tenkaizu_kakudaiButton.addActionListener(e -> {
            app.setHelp("tenkaizu_kakudai");

            double d_bairitu = Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.scaleFactor = app.scaleFactor * Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.camera_of_orisen_input_diagram.multiplyCameraZoomX(d_bairitu);
            app.camera_of_orisen_input_diagram.multiplyCameraZoomY(d_bairitu);


//20180122追加
            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= app.foldedFigures.size() - 1; i_oz++) {
                OZi = app.foldedFigures.get(i_oz);

                Point t_o2tv = app.camera_of_orisen_input_diagram.object2TV(app.camera_of_orisen_input_diagram.getCameraPosition());

                OZi.scale(d_bairitu, t_o2tv);
            }

            app.foldedFigureModel.setScale(app.foldedFigureModel.getScale() * d_bairitu);
            app.updateFoldedFigure();
//20180122追加　ここまで

            scaleFactorTextField.setText(String.valueOf(app.scaleFactor));
            scaleFactorTextField.setCaretPosition(0);
            app.repaintCanvas();
        });
        tenkaizu_p_kaitenButton.addActionListener(e -> {
            app.setHelp("tenkaizu_p_kaiten");

            app.rotationCorrection = OritaCalc.angle_between_m180_180(app.rotationCorrection + 11.25);
            app.camera_of_orisen_input_diagram.setCameraAngle(app.rotationCorrection);
            rotationTextField.setText(String.valueOf(app.rotationCorrection));
            rotationTextField.setCaretPosition(0);

            app.repaintCanvas();
        });
        tenkaizu_kaitenSetButton.addActionListener(e -> {
            double d_kaiten_hosei_old = app.rotationCorrection;
            app.rotationCorrection = OritaCalc.angle_between_m180_180(app.String2double(rotationTextField.getText(), d_kaiten_hosei_old));

            rotationTextField.setText(String.valueOf(app.rotationCorrection));

            if (app.rotationCorrection != d_kaiten_hosei_old) {
                app.camera_of_orisen_input_diagram.setCameraAngle(app.rotationCorrection);
            }

            rotationTextField.setText(String.valueOf(app.rotationCorrection));
            rotationTextField.setCaretPosition(0);
            app.repaintCanvas();


            app.setHelp("kaiten_hosei_set");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        tenkaizu_m_kaitenButton.addActionListener(e -> {
            app.setHelp("tenkaizu_m_kaiten");
            app.rotationCorrection = OritaCalc.angle_between_m180_180(app.rotationCorrection - 11.25);
            app.camera_of_orisen_input_diagram.setCameraAngle(app.rotationCorrection);
            rotationTextField.setText(String.valueOf(app.rotationCorrection));
            rotationTextField.setCaretPosition(0);
            app.repaintCanvas();
        });
        transparentButton.addActionListener(e -> {

            app.setHelp("toumei");
            Robot robot;

            try {
                robot = new Robot();
            } catch (AWTException ex) {
                ex.printStackTrace();
                return;
            }

            // 範囲を指定してキャプチャ

            Rectangle bounds = getBounds();
            Rectangle canvasBounds = app.canvas.getBounds();
            Insets insets = getInsets();
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("bounds.x=" + bounds.x + "   :bounds.y=" + bounds.y + "    :bounds.width=" + bounds.width + "   :bounds.height=" + bounds.height);
            System.out.println("insets.top=" + insets.top + "   :insets.left=" + insets.left + "    :insets.right=" + insets.right + "   :insets.bottom=" + insets.bottom);


            //左上端から、左上で描画用画面の見える限界位置へのベクトル
            //int upperLeftX=115;
            //int upperLeftY=60;

            //右下端から、右下で描画用画面の見える限界位置へのベクトル
            //int lowerRightX=115;
            //int lowerRightY=40;

            //int i_dx=115;int i_dy=0;

            bounds = new Rectangle(bounds.x + canvasBounds.x,
                    bounds.y + canvasBounds.y,
                    canvasBounds.width - app.upperLeftX - app.lowerRightX,
                    canvasBounds.height - app.upperLeftY - app.lowerRightY);

            app.setVisible(false);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
            }//A line to make you wait only 100. Without this line, there is a risk of capturing Orihime itself when executed in a jar file. The ie of InterruptedException ie was initially e. 20181125
            app.imageT = robot.createScreenCapture(bounds);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }//A line to make you wait only 100. Without this line, there is a risk of capturing Orihime itself when executed in a jar file. The ie of InterruptedException ie was initially e. 20181125
            app.setVisible(true);

            app.img_background = app.imageT;
            OritaCalc.display("新背景カメラインスタンス化");
            app.h_cam = new Background_camera();//20181202

            double dvx = app.upperLeftX;
            double dvy = app.upperLeftY;

            app.background_set(new Point(120.0, 120.0),
                    new Point(120.0 + 10.0, 120.0),
                    new Point(dvx, dvy),
                    new Point(dvx + 10.0, dvy));


//背景表示の各条件を設定
            app.displayBackground = true;
            app.backgroundToggleButton.setBackground(Color.ORANGE);

            if (app.lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                app.h_cam.set_i_Lock_on(app.lockBackground);
                app.h_cam.setCamera(app.camera_of_orisen_input_diagram);
                app.h_cam.h3_obj_and_h4_obj_calculation();
            }

            app.repaintCanvas();
        });
        backgroundTrimButton.addActionListener(e -> {
            app.setHelp("haikei_trim");


            app.offsc_background = new BufferedImage(2000, 1100, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2_background = app.offsc_background.createGraphics();
            //背景表示
            if ((app.img_background != null) && app.displayBackground) {
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

                if (app.lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                    app.h_cam.set_i_Lock_on(app.lockBackground);
                    app.h_cam.setCamera(app.camera_of_orisen_input_diagram);
                    app.h_cam.h3_obj_and_h4_obj_calculation();
                }
            }
        });
        readBackgroundButton.addActionListener(e -> {
            app.setHelp("haikei");

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.readImageFromFile();

            app.displayBackground = true;
            backgroundToggleButton.setBackground(Color.ORANGE);


            app.h_cam = new Background_camera();//20181202
            if (app.lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                app.h_cam.set_i_Lock_on(app.lockBackground);
                app.h_cam.setCamera(app.camera_of_orisen_input_diagram);
                app.h_cam.h3_obj_and_h4_obj_calculation();
            }

            app.repaintCanvas();
        });
        backgroundToggleButton.addActionListener(e -> {
            app.setHelp("haikei_kirikae");

            app.displayBackground = !app.displayBackground;

            if (!app.displayBackground) {
                backgroundToggleButton.setBackground(Color.gray);
            } else {
                backgroundToggleButton.setBackground(Color.ORANGE);
            }

            app.repaintCanvas();
        });
        backgroundSetPositionButton.addActionListener(e -> {
            app.setHelp("set_BG");

            app.mouseMode = MouseMode.BACKGROUND_CHANGE_POSITION_26;
            app.Button_shared_operation();
            app.repaintCanvas();
            System.out.println("mouseMode = " + app.mouseMode);
        });
        backgroundLockButton.addActionListener(e -> {
            app.setHelp("haikei_Lock_on");

            app.lockBackground_ori = !app.lockBackground_ori;
            app.lockBackground = app.lockBackground_ori;

            if (app.lockBackground) {
                backgroundLockButton.setBackground(Color.ORANGE);

                app.h_cam.set_i_Lock_on(app.lockBackground);
                app.h_cam.setCamera(app.camera_of_orisen_input_diagram);
                app.h_cam.h3_obj_and_h4_obj_calculation();
            } else {
                backgroundLockButton.setBackground(Color.gray);

                app.h_cam.set_i_Lock_on(app.lockBackground);
            }

            app.repaintCanvas();
        });
        senbun_yoke_henkanButton.addActionListener(e -> {
            app.setHelp("senbun_yoke_henkan");
            app.mouseMode = MouseMode.CREASE_ADVANCE_TYPE_30;
            System.out.println("mouseMode = " + app.mouseMode);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
    }

    public void setData(CanvasModel canvasModel) {
        mouseSettingsCheckBox.setSelected(canvasModel.getMouseWheelMovesCreasePattern());
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
        data.setInternalDivisionRatioA(app.String2double(ratioATextField.getText(), data.getInternalDivisionRatioA()));
        data.setInternalDivisionRatioB(app.String2double(ratioBTextField.getText(), data.getInternalDivisionRatioB()));
        data.setInternalDivisionRatioC(app.String2double(ratioCTextField.getText(), data.getInternalDivisionRatioC()));
        data.setInternalDivisionRatioD(app.String2double(ratioDTextField.getText(), data.getInternalDivisionRatioD()));
        data.setInternalDivisionRatioE(app.String2double(ratioETextField.getText(), data.getInternalDivisionRatioE()));
        data.setInternalDivisionRatioF(app.String2double(ratioFTextField.getText(), data.getInternalDivisionRatioF()));
    }

    public JTextField getRotationTextField() {
        return rotationTextField;
    }

    public JButton getBackgroundToggleButton() {
        return backgroundToggleButton;
    }

    public JButton getBackgroundLockButton() {
        return backgroundLockButton;
    }

    public JTextField getScaleFactorTextField() {
        return scaleFactorTextField;
    }

    public JTextField getRatioATextField() {
        return ratioATextField;
    }

    public JTextField getRatioBTextField() {
        return ratioBTextField;
    }

    public JTextField getRatioCTextField() {
        return ratioCTextField;
    }

    public JTextField getRatioDTextField() {
        return ratioDTextField;
    }

    public JTextField getRatioETextField() {
        return ratioETextField;
    }

    public JTextField getRatioFTextField() {
        return ratioFTextField;
    }

    private void createUIComponents() {
        rootPanel = this;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel.setLayout(new GridBagLayout());
        tyouhoukei_selectButton = new JButton();
        tyouhoukei_selectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tyouhoukei_select.png")));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(tyouhoukei_selectButton, gbc);
        writeImageButton = new JButton();
        writeImageButton.setText("Im_s");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(writeImageButton, gbc);
        mouseSettingsCheckBox = new JCheckBox();
        mouseSettingsCheckBox.setIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_off.png")));
        mouseSettingsCheckBox.setSelected(false);
        mouseSettingsCheckBox.setSelectedIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_on.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(mouseSettingsCheckBox, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(panel1, gbc);
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
        tenkaizu_idiuButton = new JButton();
        tenkaizu_idiuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_idiu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(tenkaizu_idiuButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(panel4, gbc);
        tenkaizu_syukusyouButton = new JButton();
        tenkaizu_syukusyouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_syukusyou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(tenkaizu_syukusyouButton, gbc);
        scaleFactorTextField = new JTextField();
        scaleFactorTextField.setColumns(2);
        scaleFactorTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(scaleFactorTextField, gbc);
        tenkaizu_syukusyouSetButton = new JButton();
        tenkaizu_syukusyouSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(tenkaizu_syukusyouSetButton, gbc);
        tenkaizu_kakudaiButton = new JButton();
        tenkaizu_kakudaiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_kakudai.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(tenkaizu_kakudaiButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(panel5, gbc);
        tenkaizu_p_kaitenButton = new JButton();
        tenkaizu_p_kaitenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_p_kaiten.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(tenkaizu_p_kaitenButton, gbc);
        rotationTextField = new JTextField();
        rotationTextField.setColumns(2);
        rotationTextField.setEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(rotationTextField, gbc);
        tenkaizu_kaitenSetButton = new JButton();
        tenkaizu_kaitenSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(tenkaizu_kaitenSetButton, gbc);
        tenkaizu_m_kaitenButton = new JButton();
        tenkaizu_m_kaitenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_m_kaiten.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(tenkaizu_m_kaitenButton, gbc);
        transparentButton = new JButton();
        transparentButton.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(transparentButton, gbc);
        backgroundTrimButton = new JButton();
        backgroundTrimButton.setText("Tr");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(backgroundTrimButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(panel6, gbc);
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
        gbc.gridx = 10;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rootPanel.add(spacer1, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
