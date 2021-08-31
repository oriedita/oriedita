package origami_editor.editor;

import origami_editor.editor.drawing_worker.Drawing_Worker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.graphic2d.oritacalc.OritaCalc;
import origami_editor.graphic2d.point.Point;
import origami_editor.tools.background_camera.Background_camera;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NorthPanel extends JPanel {
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
    private JTextField tenkaizu_syukusyouTextField;
    private JButton tenkaizu_syukusyouSetButton;
    private JTextField tenkaizu_kaitenTextField;
    private JButton tenkaizu_kaitenSetButton;
    private JButton toumeiButton;
    private JButton backgroundTrimButton;
    private JButton readBackgroundButton;
    private JButton backgroundToggleButton;
    private JButton backgroundSetPositionButton;
    private JButton backgroundLockButton;
    private JCheckBox mouseSettingsCheckBox;

    public JTextField getTenkaizu_kaitenTextField() {
        return tenkaizu_kaitenTextField;
    }

    public JButton getBackgroundToggleButton() {
        return backgroundToggleButton;
    }

    public JButton getBackgroundLockButton() {
        return backgroundLockButton;
    }

    public JCheckBox getMouseSettingsCheckBox() {
        return mouseSettingsCheckBox;
    }

    public NorthPanel(App app) {
        $$$setupUI$$$();
        tyouhoukei_selectButton.addActionListener(e -> {
            app.setHelp("qqq/tyouhoukei_select.png");
            app.foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            app.es1.setFoldLineAdditional(app.foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            app.i_mouse_modeA = MouseMode.OPERATION_FRAME_CREATE_61;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_FREE_1;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.canvas.repaint();
        });
        writeImageButton.addActionListener(e -> {
            app.setHelp("qqq/writeImage.png");
            if (app.i_mouse_modeA != MouseMode.OPERATION_FRAME_CREATE_61) {
                app.Button_shared_operation();
                app.es1.setDrawingStage(0);
            }//枠設定時(==61)には、その枠を消さないためにes1.set_i_egaki_dankaiを０にしないでおく　20180524
            app.i_mouseDragged_valid = false;
            app.i_mouseReleased_valid = false;

            app.writeImage();
            app.canvas.repaint();
        });
        mouseSettingsCheckBox.addActionListener(e -> {
            app.setHelp("qqq/ckbox_mouse_settei.png");
            app.canvas.repaint();
        });
        lineSegmentInternalDivisionRatioSetButton.addActionListener(e -> {
            app.setInternalDivisionRatio();

            app.setHelp("qqq/senbun_naibun_set.png");
            app.i_mouse_modeA = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);

            app.Button_shared_operation();
            app.canvas.repaint();
        });
        drawLineSegmentInternalDivisionRatioButton.addActionListener(e -> {
            app.setInternalDivisionRatio();
            app.setHelp("qqq/senbun_n_nyuryoku.png");
            app.i_mouse_modeA = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_RATIO_SET_28;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.canvas.repaint();
        });
        tenkaizu_idiuButton.addActionListener(e -> {
            app.setHelp("qqq/tenkaizu_idiu.png");

            app.i_mouse_modeA = MouseMode.MOVE_CREASE_PATTERN_2;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);

            app.canvas.repaint();
        });
        tenkaizu_syukusyouButton.addActionListener(e -> {
            app.setHelp("qqq/tenkaizu_syukusyou.png");

            double d_bairitu = 1.0 / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.scaleFactor = app.scaleFactor / Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.camera_of_orisen_input_diagram.multiplyCameraZoomX(d_bairitu);
            app.camera_of_orisen_input_diagram.multiplyCameraZoomY(d_bairitu);

//20180122追加
            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= app.OAZ.size() - 1; i_oz++) {
                OZi = app.OAZ.get(i_oz);

                Point t_o2tv = app.camera_of_orisen_input_diagram.object2TV(app.camera_of_orisen_input_diagram.getCameraPosition());

                OZi.d_foldedFigure_scale_factor = OZi.d_foldedFigure_scale_factor * d_bairitu;

                OZi.camera_of_foldedFigure.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_rear.multiplyCameraZoomY(d_bairitu);
                app.foldedFigureSizeTextField.setText(String.valueOf(OZi.d_foldedFigure_scale_factor));
                app.foldedFigureSizeTextField.setCaretPosition(0);
            }
//20180122追加　ここまで


            tenkaizu_syukusyouTextField.setText(String.valueOf(app.scaleFactor));
            tenkaizu_syukusyouTextField.setCaretPosition(0);
            app.canvas.repaint();
        });
        tenkaizu_syukusyouSetButton.addActionListener(e -> {
            double d_syukusyaku_keisuu_old = app.scaleFactor;
            app.scaleFactor = app.String2double(tenkaizu_syukusyouTextField.getText(), d_syukusyaku_keisuu_old);
            if (app.scaleFactor <= 0.0) {
                app.scaleFactor = d_syukusyaku_keisuu_old;
            }
            tenkaizu_syukusyouTextField.setText(String.valueOf(app.scaleFactor));
            if (app.scaleFactor != d_syukusyaku_keisuu_old) {
                app.camera_of_orisen_input_diagram.setCameraZoomX(app.scaleFactor);
                app.camera_of_orisen_input_diagram.setCameraZoomY(app.scaleFactor);

//20180225追加

                double d_bairitu = app.scaleFactor / d_syukusyaku_keisuu_old;


                FoldedFigure OZi;
                for (int i_oz = 1; i_oz <= app.OAZ.size() - 1; i_oz++) {
                    OZi = app.OAZ.get(i_oz);

                    Point t_o2tv = app.camera_of_orisen_input_diagram.object2TV(app.camera_of_orisen_input_diagram.getCameraPosition());

                    OZi.d_foldedFigure_scale_factor = OZi.d_foldedFigure_scale_factor * d_bairitu;


                    OZi.camera_of_foldedFigure.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_foldedFigure.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_foldedFigure.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_foldedFigure_front.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_foldedFigure_front.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_foldedFigure_rear.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_foldedFigure_rear.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_transparent_front.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_transparent_front.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_transparent_front.multiplyCameraZoomY(d_bairitu);

                    OZi.camera_of_transparent_rear.camera_ichi_sitei_from_TV(t_o2tv);
                    OZi.camera_of_transparent_rear.multiplyCameraZoomX(d_bairitu);
                    OZi.camera_of_transparent_rear.multiplyCameraZoomY(d_bairitu);

                    app.foldedFigureSizeTextField.setText(String.valueOf(app.OZ.d_foldedFigure_scale_factor));
                    app.foldedFigureSizeTextField.setCaretPosition(0);
                }
//20180225追加　ここまで


            }
            tenkaizu_syukusyouTextField.setText(String.valueOf(app.scaleFactor));
            tenkaizu_syukusyouTextField.setCaretPosition(0);
            app.canvas.repaint();

            app.setHelp("qqq/syukusyaku_keisuu_set.png");
            app.Button_shared_operation();
            app.canvas.repaint();
        });
        tenkaizu_kakudaiButton.addActionListener(e -> {
            app.setHelp("qqq/tenkaizu_kakudai.png");

            double d_bairitu = Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.scaleFactor = app.scaleFactor * Math.sqrt(Math.sqrt(Math.sqrt(2.0)));//  sqrt(sqrt(2))=1.1892
            app.camera_of_orisen_input_diagram.multiplyCameraZoomX(d_bairitu);
            app.camera_of_orisen_input_diagram.multiplyCameraZoomY(d_bairitu);


//20180122追加
            FoldedFigure OZi;
            for (int i_oz = 1; i_oz <= app.OAZ.size() - 1; i_oz++) {
                OZi = app.OAZ.get(i_oz);

                Point t_o2tv = app.camera_of_orisen_input_diagram.object2TV(app.camera_of_orisen_input_diagram.getCameraPosition());

                OZi.d_foldedFigure_scale_factor = OZi.d_foldedFigure_scale_factor * d_bairitu;


                OZi.camera_of_foldedFigure.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_foldedFigure_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_foldedFigure_rear.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_front.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_front.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_front.multiplyCameraZoomY(d_bairitu);

                OZi.camera_of_transparent_rear.camera_ichi_sitei_from_TV(t_o2tv);
                OZi.camera_of_transparent_rear.multiplyCameraZoomX(d_bairitu);
                OZi.camera_of_transparent_rear.multiplyCameraZoomY(d_bairitu);

                app.foldedFigureSizeTextField.setText(String.valueOf(app.OZ.d_foldedFigure_scale_factor));
                app.foldedFigureSizeTextField.setCaretPosition(0);
            }
//20180122追加　ここまで


            tenkaizu_syukusyouTextField.setText(String.valueOf(app.scaleFactor));
            tenkaizu_syukusyouTextField.setCaretPosition(0);
            app.canvas.repaint();
        });
        tenkaizu_p_kaitenButton.addActionListener(e -> {
            app.setHelp("qqq/tenkaizu_p_kaiten.png");

            app.rotationCorrection = OritaCalc.angle_between_m180_180(app.rotationCorrection + 11.25);
            app.camera_of_orisen_input_diagram.setCameraAngle(app.rotationCorrection);
            tenkaizu_kaitenTextField.setText(String.valueOf(app.rotationCorrection));
            tenkaizu_kaitenTextField.setCaretPosition(0);

            app.canvas.repaint();
        });
        tenkaizu_kaitenSetButton.addActionListener(e -> {
            double d_kaiten_hosei_old = app.rotationCorrection;
            app.rotationCorrection = OritaCalc.angle_between_m180_180(app.String2double(tenkaizu_kaitenTextField.getText(), d_kaiten_hosei_old));

            tenkaizu_kaitenTextField.setText(String.valueOf(app.rotationCorrection));

            if (app.rotationCorrection != d_kaiten_hosei_old) {
                app.camera_of_orisen_input_diagram.setCameraAngle(app.rotationCorrection);
            }

            tenkaizu_kaitenTextField.setText(String.valueOf(app.rotationCorrection));
            tenkaizu_kaitenTextField.setCaretPosition(0);
            app.canvas.repaint();


            app.setHelp("qqq/kaiten_hosei_set.png");
            app.Button_shared_operation();
            app.canvas.repaint();
        });
        tenkaizu_m_kaitenButton.addActionListener(e -> {
            app.setHelp("qqq/tenkaizu_m_kaiten.png");
            app.rotationCorrection = OritaCalc.angle_between_m180_180(app.rotationCorrection - 11.25);
            app.camera_of_orisen_input_diagram.setCameraAngle(app.rotationCorrection);
            tenkaizu_kaitenTextField.setText(String.valueOf(app.rotationCorrection));
            tenkaizu_kaitenTextField.setCaretPosition(0);
            app.canvas.repaint();
        });
        toumeiButton.addActionListener(e -> {

            app.setHelp("qqq/toumei.png");
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
            //int upperLeft_ix=115;
            //int upperLeft_iy=60;

            //右下端から、右下で描画用画面の見える限界位置へのベクトル
            //int lowerRight_ix=115;
            //int lowerRight_iy=40;

            //int i_dx=115;int i_dy=0;

            bounds = new Rectangle(bounds.x + canvasBounds.x,
                    bounds.y + canvasBounds.y,
                    canvasBounds.width - app.upperLeft_ix - app.lowerRight_ix,
                    canvasBounds.height - app.upperLeft_iy - app.lowerRight_iy);

            setVisible(false);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
            }//A line to make you wait only 100. Without this line, there is a risk of capturing Orihime itself when executed in a jar file. The ie of InterruptedException ie was initially e. 20181125
            app.imageT = robot.createScreenCapture(bounds);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }//A line to make you wait only 100. Without this line, there is a risk of capturing Orihime itself when executed in a jar file. The ie of InterruptedException ie was initially e. 20181125
            setVisible(true);

            app.img_background = app.imageT;
            OritaCalc.display("新背景カメラインスタンス化");
            app.h_cam = new Background_camera();//20181202

            double dvx = app.upperLeft_ix;
            double dvy = app.upperLeft_iy;

            app.background_set(new Point(120.0, 120.0),
                    new Point(120.0 + 10.0, 120.0),
                    new Point(dvx, dvy),
                    new Point(dvx + 10.0, dvy));


//背景表示の各条件を設定
            app.displayBackground = true;
            app.Button_background_kirikae.setBackground(Color.ORANGE);

            if (app.lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                app.h_cam.set_i_Lock_on(app.lockBackground);
                app.h_cam.setCamera(app.camera_of_orisen_input_diagram);
                app.h_cam.h3_obj_and_h4_obj_calculation();
            }

            app.canvas.repaint();
        });
        backgroundTrimButton.addActionListener(e -> {
            app.setHelp("qqq/haikei_trim.png");


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
            if ((app.i_mouse_modeA == MouseMode.OPERATION_FRAME_CREATE_61) && (app.es1.getDrawingStage() == 4)) {//枠線が表示されている状態
                int xmin = (int) app.es1.operationFrameBox.getXMin();
                int xmax = (int) app.es1.operationFrameBox.getXMax();
                int ymin = (int) app.es1.operationFrameBox.getYMin();
                int ymax = (int) app.es1.operationFrameBox.getYMax();

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
            app.setHelp("qqq/haikei.png");

            app.i_mouseDragged_valid = false;
            app.i_mouseReleased_valid = false;

            app.readImageFromFile();

            app.displayBackground = true;
            backgroundToggleButton.setBackground(Color.ORANGE);


            app.h_cam = new Background_camera();//20181202
            if (app.lockBackground) {//20181202  このifが無いとlock on のときに背景がうまく表示できない
                app.h_cam.set_i_Lock_on(app.lockBackground);
                app.h_cam.setCamera(app.camera_of_orisen_input_diagram);
                app.h_cam.h3_obj_and_h4_obj_calculation();
            }

            app.canvas.repaint();
        });
        backgroundToggleButton.addActionListener(e -> {
            app.setHelp("qqq/haikei_kirikae.png");

            app.displayBackground = !app.displayBackground;

            if (!app.displayBackground) {
                backgroundToggleButton.setBackground(Color.gray);
            } else {
                backgroundToggleButton.setBackground(Color.ORANGE);
            }

            app.canvas.repaint();
        });
        backgroundSetPositionButton.addActionListener(e -> {
            app.setHelp("qqq/set_BG.png");

            app.i_mouse_modeA = MouseMode.BACKGROUND_CHANGE_POSITION_26;
            app.Button_shared_operation();
            app.canvas.repaint();
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);
        });
        backgroundLockButton.addActionListener(e -> {
            app.setHelp("qqq/haikei_Lock_on.png");

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

            app.canvas.repaint();
        });
        senbun_yoke_henkanButton.addActionListener(e -> {
            app.setHelp("qqq/senbun_yoke_henkan.png");
            app.i_mouse_modeA = MouseMode.CREASE_ADVANCE_TYPE_30;
            System.out.println("i_mouse_modeA = " + app.i_mouse_modeA);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.canvas.repaint();
        });
    }

    public JTextField getTenkaizu_syukusyouTextField() {
        return tenkaizu_syukusyouTextField;
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
        rootPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        tyouhoukei_selectButton = new JButton();
        tyouhoukei_selectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tyouhoukei_select.png")));
        rootPanel.add(tyouhoukei_selectButton);
        writeImageButton = new JButton();
        writeImageButton.setText("Im_s");
        rootPanel.add(writeImageButton);
        mouseSettingsCheckBox = new JCheckBox();
        mouseSettingsCheckBox.setIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_off.png")));
        mouseSettingsCheckBox.setSelected(false);
        mouseSettingsCheckBox.setSelectedIcon(new ImageIcon(getClass().getResource("/ppp/ckbox_mouse_settei_on.png")));
        rootPanel.add(mouseSettingsCheckBox);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        rootPanel.add(panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        panel2.add(panel3);
        ratioATextField = new JTextField();
        ratioATextField.setColumns(2);
        ratioATextField.setText("1.0");
        panel3.add(ratioATextField);
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        panel3.add(label1);
        ratioBTextField = new JTextField();
        ratioBTextField.setColumns(2);
        ratioBTextField.setText("0.0");
        panel3.add(ratioBTextField);
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel3.add(label2);
        ratioCTextField = new JTextField();
        ratioCTextField.setColumns(2);
        ratioCTextField.setText("0.0");
        panel3.add(ratioCTextField);
        final JLabel label3 = new JLabel();
        label3.setIcon(new ImageIcon(getClass().getResource("/ppp/tenten.png")));
        panel2.add(label3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        panel2.add(panel4);
        ratioDTextField = new JTextField();
        ratioDTextField.setColumns(2);
        ratioDTextField.setText("0.0");
        panel4.add(ratioDTextField);
        final JLabel label4 = new JLabel();
        label4.setIcon(new ImageIcon(getClass().getResource("/ppp/plus.png")));
        panel4.add(label4);
        ratioETextField = new JTextField();
        ratioETextField.setColumns(2);
        ratioETextField.setText("1.0");
        panel4.add(ratioETextField);
        final JLabel label5 = new JLabel();
        label5.setIcon(new ImageIcon(getClass().getResource("/ppp/root.png")));
        panel4.add(label5);
        ratioFTextField = new JTextField();
        ratioFTextField.setColumns(2);
        ratioFTextField.setText("2.0");
        panel4.add(ratioFTextField);
        lineSegmentInternalDivisionRatioSetButton = new JButton();
        lineSegmentInternalDivisionRatioSetButton.setText("Set");
        panel2.add(lineSegmentInternalDivisionRatioSetButton);
        drawLineSegmentInternalDivisionRatioButton = new JButton();
        drawLineSegmentInternalDivisionRatioButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_n_nyuryoku.png")));
        panel2.add(drawLineSegmentInternalDivisionRatioButton);
        tenkaizu_idiuButton = new JButton();
        tenkaizu_idiuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_idiu.png")));
        rootPanel.add(tenkaizu_idiuButton);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        rootPanel.add(panel5);
        tenkaizu_syukusyouButton = new JButton();
        tenkaizu_syukusyouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_syukusyou.png")));
        panel5.add(tenkaizu_syukusyouButton);
        tenkaizu_syukusyouTextField = new JTextField();
        tenkaizu_syukusyouTextField.setText("1.0");
        panel5.add(tenkaizu_syukusyouTextField);
        tenkaizu_syukusyouSetButton = new JButton();
        tenkaizu_syukusyouSetButton.setText("S");
        panel5.add(tenkaizu_syukusyouSetButton);
        tenkaizu_kakudaiButton = new JButton();
        tenkaizu_kakudaiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_kakudai.png")));
        panel5.add(tenkaizu_kakudaiButton);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        rootPanel.add(panel6);
        tenkaizu_p_kaitenButton = new JButton();
        tenkaizu_p_kaitenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_p_kaiten.png")));
        panel6.add(tenkaizu_p_kaitenButton);
        tenkaizu_kaitenTextField = new JTextField();
        panel6.add(tenkaizu_kaitenTextField);
        tenkaizu_kaitenSetButton = new JButton();
        tenkaizu_kaitenSetButton.setText("S");
        panel6.add(tenkaizu_kaitenSetButton);
        tenkaizu_m_kaitenButton = new JButton();
        tenkaizu_m_kaitenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenkaizu_m_kaiten.png")));
        panel6.add(tenkaizu_m_kaitenButton);
        toumeiButton = new JButton();
        toumeiButton.setText("T");
        rootPanel.add(toumeiButton);
        backgroundTrimButton = new JButton();
        backgroundTrimButton.setText("Tr");
        rootPanel.add(backgroundTrimButton);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        rootPanel.add(panel7);
        readBackgroundButton = new JButton();
        readBackgroundButton.setText("BG");
        panel7.add(readBackgroundButton);
        backgroundToggleButton = new JButton();
        backgroundToggleButton.setText("off");
        panel7.add(backgroundToggleButton);
        backgroundSetPositionButton = new JButton();
        backgroundSetPositionButton.setText("S");
        panel7.add(backgroundSetPositionButton);
        backgroundLockButton = new JButton();
        backgroundLockButton.setText("L");
        panel7.add(backgroundLockButton);
        senbun_yoke_henkanButton = new JButton();
        senbun_yoke_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_yoke_henkan.png")));
        panel7.add(senbun_yoke_henkanButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
