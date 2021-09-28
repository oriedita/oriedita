package origami_editor.editor;

import origami.crease_pattern.element.LineColor;
import origami_editor.editor.component.ColorIcon;
import origami_editor.editor.databinding.AngleSystemModel;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.HistoryStateModel;
import origami_editor.editor.databinding.MeasuresModel;
import origami_editor.editor.drawing_worker.FoldLineAdditionalInputMode;
import origami_editor.tools.StringOp;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

public class RightPanel {
    private final App app;
    private final OpenFrame frame;
    private JCheckBox cAMVCheckBox;
    private JButton ck4_colorIncreaseButton;
    private JCheckBox ckTCheckBox;
    private JCheckBox ckOCheckBox;
    private JButton fxOButton;
    private JButton fxTButton;
    private JButton angleSystemAButton;
    private JButton ck4_colorDecreaseButton;
    private JButton angleSystemADecreaseButton;
    private JButton angleSystemAIncreaseButton;
    private JButton angleSystemBDecreaseButton;
    private JButton angleSystemBButton;
    private JButton angleSystemBIncreaseButton;
    private JTextField angleATextField;
    private JTextField angleCTextField;
    private JTextField angleBTextField;
    private JButton restrictedAngleABCSetButton;
    private JButton c_colButton;
    private JButton l1Button;
    private JLabel measuredLength1Label;
    private JLabel measuredLength2Label;
    private JButton ad_fncButton;
    private JTextField auxUndoTotalTextField;
    private JButton degButton;
    private JButton deg3Button;
    private JButton angleRestrictedButton;
    private JButton deg2Button;
    private JButton deg4Button;
    private JButton polygonSizeSetButton;
    private JTextField polygonSizeTextField;
    private JButton regularPolygonButton;
    private JButton circleDrawFreeButton;
    private JButton circleDrawButton;
    private JButton circleDrawSeparateButton;
    private JButton circleDrawConcentricButton;
    private JButton circleDrawConcentricSelectButton;
    private JButton circleDrawTwoConcentricButton;
    private JButton circleDrawTangentLineButton;
    private JButton circleDrawThreePointButton;
    private JButton circleDrawInvertedButton;
    private JButton sen_tokutyuu_color_henkouButton;
    private JButton h_undoButton;
    private JButton h_redoButton;
    private JButton h_senhaba_sageButton;
    private JButton h_senhaba_ageButton;
    private JButton h_senbun_nyuryokuButton;
    private JButton h_senbun_sakujyoButton;
    private JButton restrictedAngleSetDEFButton;
    private JTextField angleDTextField;
    private JTextField angleETextField;
    private JTextField angleFTextField;
    private JButton h_undoTotalSetButton;
    private JButton colOrangeButton;
    private JButton colYellowButton;
    private JButton l2Button;
    private JButton a1Button;
    private JButton a2Button;
    private JButton a3Button;
    private JLabel measuredAngle1Label;
    private JLabel measuredAngle2Label;
    private JLabel measuredAngle3Label;
    private JPanel root;

    public RightPanel(App app, AngleSystemModel angleSystemModel) {
        this.app = app;

        $$$setupUI$$$();

        app.registerButton(ck4_colorIncreaseButton, "ck4_colorIncreaseAction");
        app.registerButton(fxOButton, "fxOAction");
        app.registerButton(fxTButton, "fxTAction");
        app.registerButton(angleSystemAButton, "angleSystemAAction");
        app.registerButton(ck4_colorDecreaseButton, "ck4_colorDecreaseAction");
        app.registerButton(angleSystemADecreaseButton, "angleSystemADecreaseAction");
        app.registerButton(angleSystemAIncreaseButton, "angleSystemAIncreaseAction");
        app.registerButton(angleSystemBDecreaseButton, "angleSystemBDecreaseAction");
        app.registerButton(angleSystemBButton, "angleSystemBAction");
        app.registerButton(angleSystemBIncreaseButton, "angleSystemBIncreaseAction");
        app.registerButton(restrictedAngleABCSetButton, "restrictedAngleABCSetAction");
        app.registerButton(c_colButton, "c_colAction");
        app.registerButton(l1Button, "l1Action");
        app.registerButton(ad_fncButton, "ad_fncAction");
        app.registerButton(degButton, "degAction");
        app.registerButton(deg3Button, "deg3Action");
        app.registerButton(angleRestrictedButton, "angleRestrictedAction");
        app.registerButton(deg2Button, "deg2Action");
        app.registerButton(deg4Button, "deg4Action");
        app.registerButton(polygonSizeSetButton, "polygonSizeSetAction");
        app.registerButton(regularPolygonButton, "regularPolygonAction");
        app.registerButton(circleDrawFreeButton, "circleDrawFreeAction");
        app.registerButton(circleDrawButton, "circleDrawAction");
        app.registerButton(circleDrawSeparateButton, "circleDrawSeparateAction");
        app.registerButton(circleDrawConcentricButton, "circleDrawConcentricAction");
        app.registerButton(circleDrawConcentricSelectButton, "circleDrawConcentricSelectAction");
        app.registerButton(circleDrawTwoConcentricButton, "circleDrawTwoConcentricAction");
        app.registerButton(circleDrawTangentLineButton, "circleDrawTangentLineAction");
        app.registerButton(circleDrawThreePointButton, "circleDrawThreePointAction");
        app.registerButton(circleDrawInvertedButton, "circleDrawInvertedAction");
        app.registerButton(sen_tokutyuu_color_henkouButton, "sen_tokutyuu_color_henkouAction");
        app.registerButton(h_undoButton, "h_undoAction");
        app.registerButton(h_redoButton, "h_redoAction");
        app.registerButton(h_senhaba_sageButton, "h_senhaba_sageAction");
        app.registerButton(h_senhaba_ageButton, "h_senhaba_ageAction");
        app.registerButton(h_senbun_nyuryokuButton, "h_senbun_nyuryokuAction");
        app.registerButton(h_senbun_sakujyoButton, "h_senbun_sakujyoAction");
        app.registerButton(restrictedAngleSetDEFButton, "restrictedAngleSetDEFAction");
        app.registerButton(h_undoTotalSetButton, "h_undoTotalSetAction");
        app.registerButton(colOrangeButton, "colOrangeAction");
        app.registerButton(colYellowButton, "colYellowAction");
        app.registerButton(l2Button, "l2Action");
        app.registerButton(a1Button, "a1Action");
        app.registerButton(a2Button, "a2Action");
        app.registerButton(a3Button, "a3Action");

        ckOCheckBox.addActionListener(e -> {
            app.setHelp("check1");
            app.mainDrawingWorker.unselect_all();

            if (ckOCheckBox.isSelected()) {
                app.mainDrawingWorker.check1(0.001, 0.5);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                app.mainDrawingWorker.set_i_check1(true);
            } else {
                app.mainDrawingWorker.set_i_check1(false);
            }
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        fxOButton.addActionListener(e -> {

            app.setHelp("fix1");
            app.mainDrawingWorker.unselect_all();
            app.mainDrawingWorker.fix1(0.001, 0.5);
            app.mainDrawingWorker.check1(0.001, 0.5);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        ckTCheckBox.addActionListener(e -> {
            app.setHelp("check2");
            app.mainDrawingWorker.unselect_all();

            if (ckTCheckBox.isSelected()) {
                app.mainDrawingWorker.check2(0.01, 0.5);//r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度
                app.mainDrawingWorker.setCheck2(true);
            } else {
                app.mainDrawingWorker.setCheck2(false);
            }
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        fxTButton.addActionListener(e -> {
            app.setHelp("fix2");
            app.mainDrawingWorker.unselect_all();
            app.mainDrawingWorker.fix2(0.001, 0.5);
            app.mainDrawingWorker.check2(0.001, 0.5);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        cAMVCheckBox.addActionListener(e -> {
            app.setHelp("check4");
            app.mainDrawingWorker.unselect_all();

            app.canvasModel.setCheck4Enabled(cAMVCheckBox.isSelected());

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        ck4_colorDecreaseButton.addActionListener(e -> {
            app.mainDrawingWorker.lightenCheck4Color();
            app.setHelp("ck4_color_sage");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        ck4_colorIncreaseButton.addActionListener(e -> {
            app.mainDrawingWorker.darkenCheck4Color();
            app.setHelp("ck4_color_age");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        angleSystemADecreaseButton.addActionListener(e -> {
            app.setHelp("kakudo_kei_a_tiisaku");

            angleSystemModel.decreaseAngleSystemA();

            app.Button_shared_operation();
        });

        angleSystemAButton.addActionListener(e -> {
            app.setHelp("kakudo_kei_a");

            angleSystemModel.setCurrentAngleSystemDivider(angleSystemModel.getAngleSystemADivider());

            app.Button_shared_operation();
        });
        angleSystemAIncreaseButton.addActionListener(e -> {
            app.setHelp("kakudo_kei_a_ookiku");

            angleSystemModel.increaseAngleSystemA();

            app.Button_shared_operation();
        });

        angleSystemBDecreaseButton.addActionListener(e -> {
            app.setHelp("kakudo_kei_b_tiisaku");

            angleSystemModel.decreaseAngleSystemB();

            app.Button_shared_operation();
        });

        angleSystemBButton.addActionListener(e -> {
            app.setHelp("kakudo_kei_b");

            angleSystemModel.setCurrentAngleSystemDivider(angleSystemModel.getAngleSystemBDivider());

            app.Button_shared_operation();
        });
        angleSystemBIncreaseButton.addActionListener(e -> {
            app.setHelp("kakudo_kei_b_ookiku");

            angleSystemModel.increaseAngleSystemB();

            app.Button_shared_operation();
        });
        restrictedAngleABCSetButton.addActionListener(e -> {
            app.setHelp("jiyuu_kaku_set_a");

            getData(angleSystemModel);

            angleSystemModel.setCurrentABC();

            app.Button_shared_operation();
        });

        restrictedAngleSetDEFButton.addActionListener(e -> {
            app.setHelp("jiyuu_kaku_set_b");

            getData(angleSystemModel);

            angleSystemModel.setCurrentDEF();

            app.Button_shared_operation();
        });
        degButton.addActionListener(e -> {
            app.setHelp("deg");

            angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_1);

            app.Button_shared_operation();
        });
        deg3Button.addActionListener(e -> {
            app.setHelp("deg3");

            angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_3);

            app.Button_shared_operation();
        });
        angleRestrictedButton.addActionListener(e -> {
            app.setHelp("senbun_nyuryoku37");

            angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_5);

            app.Button_shared_operation();
        });
        deg2Button.addActionListener(e -> {
            app.setHelp("deg2");

            angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_2);

            app.Button_shared_operation();
        });
        deg4Button.addActionListener(e -> {
            app.setHelp("deg4");

            angleSystemModel.setAngleSystemInputType(AngleSystemModel.AngleSystemInputType.DEG_4);

            app.Button_shared_operation();
        });
        polygonSizeSetButton.addActionListener(e -> {
            app.setHelp("kakusuu_set");

            app.canvasModel.setNumPolygonCorners(StringOp.String2int(polygonSizeTextField.getText(), app.canvasModel.getNumPolygonCorners()));
            app.canvasModel.setMouseMode(MouseMode.POLYGON_SET_NO_CORNERS_29);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        regularPolygonButton.addActionListener(e -> {
            app.setHelp("sei_takakukei");

            app.canvasModel.setNumPolygonCorners(StringOp.String2int(polygonSizeTextField.getText(), app.canvasModel.getNumPolygonCorners()));
            app.canvasModel.setMouseMode(MouseMode.POLYGON_SET_NO_CORNERS_29);
            app.canvasModel.setMouseModeAfterColorSelection(MouseMode.POLYGON_SET_NO_CORNERS_29);

            app.Button_shared_operation();
            app.repaintCanvas();
            app.mainDrawingWorker.unselect_all();
        });
        circleDrawFreeButton.addActionListener(e -> {
            app.setHelp("en_nyuryoku_free");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_FREE_47);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawButton.addActionListener(e -> {
            app.setHelp("en_nyuryoku");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_42);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawSeparateButton.addActionListener(e -> {
            app.setHelp("en_bunri_nyuryoku");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_SEPARATE_44);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawConcentricButton.addActionListener(e -> {
            app.setHelp("dousin_en_tuika_s");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_CONCENTRIC_48);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawConcentricSelectButton.addActionListener(e -> {
            app.setHelp("dousin_en_tuika_d");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawTwoConcentricButton.addActionListener(e -> {
            app.setHelp("en_en_dousin_en");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawTangentLineButton.addActionListener(e -> {
            app.setHelp("en_en_sessen");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_TANGENT_LINE_45);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawThreePointButton.addActionListener(e -> {
            app.setHelp("en_3ten_nyuryoku");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_THREE_POINT_43);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        circleDrawInvertedButton.addActionListener(e -> {
            app.setHelp("hanten");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_DRAW_INVERTED_46);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        c_colButton.addActionListener(e -> {
            app.setHelp("sen_tokutyuu_color");
            app.Button_shared_operation();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            //以下にやりたいことを書く

            Color color = JColorChooser.showDialog(null, "color", new Color(100, 200, 200));
            if (color != null) {
                app.canvasModel.setCircleCustomizedColor(color);
            }

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_CHANGE_COLOR_59);

            app.repaintCanvas();
        });
        sen_tokutyuu_color_henkouButton.addActionListener(e -> {
            app.setHelp("sen_tokutyuu_color_henkou");

            app.canvasModel.setMouseMode(MouseMode.CIRCLE_CHANGE_COLOR_59);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        h_undoButton.addActionListener(e -> {
            app.setHelp("undo");

            app.mainDrawingWorker.auxUndo();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        h_undoTotalSetButton.addActionListener(e -> {
            app.setHelp("h_undo_syutoku");

            app.historyStateModel.setAuxHistoryTotal(StringOp.String2int(auxUndoTotalTextField.getText(), app.historyStateModel.getAuxHistoryTotal()));
        });
        h_redoButton.addActionListener(e -> {
            app.setHelp("h_redo");

            app.mainDrawingWorker.auxRedo();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        h_senhaba_sageButton.addActionListener(e -> {
            app.setHelp("h_senhaba_sage");

            app.canvasModel.decreaseAuxLineWidth();

            app.Button_shared_operation();
        });
        h_senhaba_ageButton.addActionListener(e -> {
            app.setHelp("h_senhaba_age");

            app.canvasModel.increaseAuxLineWidth();

            app.Button_shared_operation();
        });
        colOrangeButton.addActionListener(e -> {
            app.setHelp("Button_Col_orange");

            app.canvasModel.setAuxLiveLineColor(LineColor.ORANGE_4);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        colYellowButton.addActionListener(e -> {
            app.setHelp("Button_Col_yellow");

            app.canvasModel.setAuxLiveLineColor(LineColor.YELLOW_7);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        h_senbun_nyuryokuButton.addActionListener(e -> {
            app.setHelp("h_senbun_nyuryoku");

            app.canvasModel.setMouseMode(MouseMode.DRAW_CREASE_FREE_1);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();

            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.AUX_LINE_1);
        });
        h_senbun_sakujyoButton.addActionListener(e -> {
            app.setHelp("h_senbun_sakujyo");

            app.canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_DELETE_3);
            app.canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.AUX_LINE_1);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        l1Button.addActionListener(e -> {
            app.setHelp("nagasa_sokutei_1");

            app.canvasModel.setMouseMode(MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        l2Button.addActionListener(e -> {
            app.setHelp("nagasa_sokutei_2");

            app.canvasModel.setMouseMode(MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        a1Button.addActionListener(e -> {
            app.setHelp("kakudo_sokutei_1");

            app.canvasModel.setMouseMode(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        a2Button.addActionListener(e -> {
            app.setHelp("kakudo_sokutei_2");

            app.canvasModel.setMouseMode(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        a3Button.addActionListener(e -> {
            app.setHelp("kakudo_sokutei_3");

            app.canvasModel.setMouseMode(MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57);

            app.mainDrawingWorker.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });

        frame = new OpenFrame("additionalFrame", app);

        ad_fncButton.addActionListener(e -> {
            app.setHelp("tuika_kinou");

            frame.setLocationRelativeTo(ad_fncButton);
            frame.setVisible(true);
        });
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel1, gbc);
        cAMVCheckBox = new JCheckBox();
        cAMVCheckBox.setText("cAMV");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(cAMVCheckBox, gbc);
        ck4_colorIncreaseButton = new JButton();
        ck4_colorIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(ck4_colorIncreaseButton, gbc);
        fxTButton = new JButton();
        fxTButton.setText("fxT");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(fxTButton, gbc);
        ckOCheckBox = new JCheckBox();
        ckOCheckBox.setText("ckO");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(ckOCheckBox, gbc);
        ckTCheckBox = new JCheckBox();
        ckTCheckBox.setText("ckT");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(ckTCheckBox, gbc);
        ck4_colorDecreaseButton = new JButton();
        ck4_colorDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(ck4_colorDecreaseButton, gbc);
        fxOButton = new JButton();
        fxOButton.setText("fxO");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(fxOButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel2, gbc);
        angleSystemAButton = new JButton();
        angleSystemAButton.setText("180/12=15.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(angleSystemAButton, gbc);
        angleSystemADecreaseButton = new JButton();
        angleSystemADecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tiisaku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(angleSystemADecreaseButton, gbc);
        angleSystemAIncreaseButton = new JButton();
        angleSystemAIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ookiku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(angleSystemAIncreaseButton, gbc);
        angleSystemBDecreaseButton = new JButton();
        angleSystemBDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tiisaku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(angleSystemBDecreaseButton, gbc);
        angleSystemBButton = new JButton();
        angleSystemBButton.setText("180/8=22.5");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(angleSystemBButton, gbc);
        angleSystemBIncreaseButton = new JButton();
        angleSystemBIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ookiku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(angleSystemBIncreaseButton, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer2, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel3, gbc);
        angleATextField = new JTextField();
        angleATextField.setText("40.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleATextField, gbc);
        angleCTextField = new JTextField();
        angleCTextField.setText("80.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleCTextField, gbc);
        angleBTextField = new JTextField();
        angleBTextField.setText("60.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleBTextField, gbc);
        restrictedAngleABCSetButton = new JButton();
        restrictedAngleABCSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(restrictedAngleABCSetButton, gbc);
        angleDTextField = new JTextField();
        angleDTextField.setText("30.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleDTextField, gbc);
        angleETextField = new JTextField();
        angleETextField.setText("50.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleETextField, gbc);
        angleFTextField = new JTextField();
        angleFTextField.setText("100.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(angleFTextField, gbc);
        restrictedAngleSetDEFButton = new JButton();
        restrictedAngleSetDEFButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(restrictedAngleSetDEFButton, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer3, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel4, gbc);
        degButton = new JButton();
        degButton.setIcon(new ImageIcon(getClass().getResource("/ppp/deg.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(degButton, gbc);
        deg3Button = new JButton();
        deg3Button.setIcon(new ImageIcon(getClass().getResource("/ppp/deg3.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(deg3Button, gbc);
        angleRestrictedButton = new JButton();
        angleRestrictedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku37.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(angleRestrictedButton, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer4, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel5, gbc);
        c_colButton = new JButton();
        c_colButton.setText("C_col");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(c_colButton, gbc);
        sen_tokutyuu_color_henkouButton = new JButton();
        sen_tokutyuu_color_henkouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sen_tokutyuu_color_henkou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(sen_tokutyuu_color_henkouButton, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer5, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel6, gbc);
        l1Button = new JButton();
        l1Button.setHorizontalAlignment(11);
        l1Button.setText("L1=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(l1Button, gbc);
        measuredLength1Label = new JLabel();
        measuredLength1Label.setBackground(new Color(-1));
        measuredLength1Label.setOpaque(true);
        measuredLength1Label.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(measuredLength1Label, gbc);
        l2Button = new JButton();
        l2Button.setHorizontalAlignment(11);
        l2Button.setText("L2=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(l2Button, gbc);
        measuredLength2Label = new JLabel();
        measuredLength2Label.setBackground(new Color(-1));
        measuredLength2Label.setOpaque(true);
        measuredLength2Label.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(measuredLength2Label, gbc);
        a3Button = new JButton();
        a3Button.setHorizontalAlignment(11);
        a3Button.setText("A3=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(a3Button, gbc);
        measuredAngle3Label = new JLabel();
        measuredAngle3Label.setBackground(new Color(-1));
        measuredAngle3Label.setOpaque(true);
        measuredAngle3Label.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(measuredAngle3Label, gbc);
        measuredAngle2Label = new JLabel();
        measuredAngle2Label.setBackground(new Color(-1));
        measuredAngle2Label.setOpaque(true);
        measuredAngle2Label.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(measuredAngle2Label, gbc);
        measuredAngle1Label = new JLabel();
        measuredAngle1Label.setBackground(new Color(-1));
        measuredAngle1Label.setOpaque(true);
        measuredAngle1Label.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(measuredAngle1Label, gbc);
        a2Button = new JButton();
        a2Button.setHorizontalAlignment(11);
        a2Button.setText("A2=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(a2Button, gbc);
        a1Button = new JButton();
        a1Button.setHorizontalAlignment(11);
        a1Button.setText("A1=");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(a1Button, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 21;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer6, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 22;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel7, gbc);
        ad_fncButton = new JButton();
        ad_fncButton.setText("ad_fnc");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(ad_fncButton, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        root.add(spacer8, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel8, gbc);
        circleDrawFreeButton = new JButton();
        circleDrawFreeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/en_nyuryoku_free.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(circleDrawFreeButton, gbc);
        circleDrawConcentricButton = new JButton();
        circleDrawConcentricButton.setIcon(new ImageIcon(getClass().getResource("/ppp/dousin_en_tuika_s.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(circleDrawConcentricButton, gbc);
        circleDrawSeparateButton = new JButton();
        circleDrawSeparateButton.setIcon(new ImageIcon(getClass().getResource("/ppp/en_bunri_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(circleDrawSeparateButton, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel9, gbc);
        deg2Button = new JButton();
        deg2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/deg2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(deg2Button, gbc);
        deg4Button = new JButton();
        deg4Button.setIcon(new ImageIcon(getClass().getResource("/ppp/deg4.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(deg4Button, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel10, gbc);
        polygonSizeTextField = new JTextField();
        polygonSizeTextField.setText("5");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(polygonSizeTextField, gbc);
        polygonSizeSetButton = new JButton();
        polygonSizeSetButton.setText("Set");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(polygonSizeSetButton, gbc);
        regularPolygonButton = new JButton();
        regularPolygonButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sei_takakukei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(regularPolygonButton, gbc);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel11, gbc);
        circleDrawButton = new JButton();
        circleDrawButton.setIcon(new ImageIcon(getClass().getResource("/ppp/en_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel11.add(circleDrawButton, gbc);
        circleDrawConcentricSelectButton = new JButton();
        circleDrawConcentricSelectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/dousin_en_tuika_d.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel11.add(circleDrawConcentricSelectButton, gbc);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel12, gbc);
        circleDrawTwoConcentricButton = new JButton();
        circleDrawTwoConcentricButton.setIcon(new ImageIcon(getClass().getResource("/ppp/en_en_dousin_en.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(circleDrawTwoConcentricButton, gbc);
        circleDrawTangentLineButton = new JButton();
        circleDrawTangentLineButton.setIcon(new ImageIcon(getClass().getResource("/ppp/en_en_sessen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(circleDrawTangentLineButton, gbc);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel13, gbc);
        circleDrawThreePointButton = new JButton();
        circleDrawThreePointButton.setIcon(new ImageIcon(getClass().getResource("/ppp/en_3ten_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel13.add(circleDrawThreePointButton, gbc);
        circleDrawInvertedButton = new JButton();
        circleDrawInvertedButton.setIcon(new ImageIcon(getClass().getResource("/ppp/hanten.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel13.add(circleDrawInvertedButton, gbc);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel14, gbc);
        h_undoButton = new JButton();
        h_undoButton.setIcon(new ImageIcon(getClass().getResource("/ppp/h_undo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel14.add(h_undoButton, gbc);
        auxUndoTotalTextField = new JTextField();
        auxUndoTotalTextField.setColumns(2);
        auxUndoTotalTextField.setText("50");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel14.add(auxUndoTotalTextField, gbc);
        h_undoTotalSetButton = new JButton();
        h_undoTotalSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel14.add(h_undoTotalSetButton, gbc);
        h_redoButton = new JButton();
        h_redoButton.setIcon(new ImageIcon(getClass().getResource("/ppp/h_redo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel14.add(h_redoButton, gbc);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel15, gbc);
        h_senhaba_sageButton = new JButton();
        h_senhaba_sageButton.setIcon(new ImageIcon(getClass().getResource("/ppp/h_senhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(h_senhaba_sageButton, gbc);
        h_senhaba_ageButton = new JButton();
        h_senhaba_ageButton.setIcon(new ImageIcon(getClass().getResource("/ppp/h_senhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(h_senhaba_ageButton, gbc);
        colOrangeButton = new JButton();
        colOrangeButton.setBackground(new Color(-6908266));
        colOrangeButton.setText("a1");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(colOrangeButton, gbc);
        colYellowButton = new JButton();
        colYellowButton.setBackground(new Color(-6908266));
        colYellowButton.setText("a2");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel15.add(colYellowButton, gbc);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(panel16, gbc);
        h_senbun_nyuryokuButton = new JButton();
        h_senbun_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/h_senbun_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel16.add(h_senbun_nyuryokuButton, gbc);
        h_senbun_sakujyoButton = new JButton();
        h_senbun_sakujyoButton.setIcon(new ImageIcon(getClass().getResource("/ppp/h_senbun_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel16.add(h_senbun_sakujyoButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    public void setData(AngleSystemModel data) {
        angleATextField.setText(String.valueOf(data.getAngleA()));
        angleCTextField.setText(String.valueOf(data.getAngleC()));
        angleBTextField.setText(String.valueOf(data.getAngleB()));
        angleDTextField.setText(String.valueOf(data.getAngleD()));
        angleETextField.setText(String.valueOf(data.getAngleE()));
        angleFTextField.setText(String.valueOf(data.getAngleF()));

        angleSystemAButton.setText(data.getAngleSystemADescription());
        angleSystemBButton.setText(data.getAngleSystemBDescription());
    }

    public void getData(AngleSystemModel data) {
        data.setAngleA(app.string2double(angleATextField.getText(), data.getAngleA()));
        data.setAngleB(app.string2double(angleBTextField.getText(), data.getAngleB()));
        data.setAngleC(app.string2double(angleCTextField.getText(), data.getAngleC()));
        data.setAngleD(app.string2double(angleDTextField.getText(), data.getAngleD()));
        data.setAngleE(app.string2double(angleETextField.getText(), data.getAngleE()));
        data.setAngleF(app.string2double(angleFTextField.getText(), data.getAngleF()));
    }

    public void setData(MeasuresModel data) {
        measuredLength1Label.setText(String.valueOf(data.getMeasuredLength1()));
        measuredLength2Label.setText(String.valueOf(data.getMeasuredLength2()));
        measuredAngle1Label.setText(String.valueOf(data.getMeasuredAngle1()));
        measuredAngle2Label.setText(String.valueOf(data.getMeasuredAngle2()));
        measuredAngle3Label.setText(String.valueOf(data.getMeasuredAngle3()));
    }

    public void getData(CanvasModel data) {
        data.setNumPolygonCorners(StringOp.String2int(polygonSizeTextField.getText(), data.getNumPolygonCorners()));
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        frame.setData(e, data);
        switch (data.getAuxLiveLineColor()) {
            case ORANGE_4:
                colOrangeButton.setBackground(Color.ORANGE);
                colYellowButton.setBackground(new Color(150, 150, 150));
                break;
            case YELLOW_7:
                colYellowButton.setBackground(Color.YELLOW);
                colOrangeButton.setBackground(new Color(150, 150, 150));
        }

        c_colButton.setIcon(new ColorIcon(data.getCircleCustomizedColor()));

        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode") || e.getPropertyName().equals("foldLineAdditionalInputMode")) {
            MouseMode m = data.getMouseMode();
            FoldLineAdditionalInputMode f = data.getFoldLineAdditionalInputMode();

            regularPolygonButton.setSelected(m == MouseMode.POLYGON_SET_NO_CORNERS_29);
            circleDrawFreeButton.setSelected(m == MouseMode.CIRCLE_DRAW_FREE_47);
            circleDrawButton.setSelected(m == MouseMode.CIRCLE_DRAW_42);
            circleDrawSeparateButton.setSelected(m == MouseMode.CIRCLE_DRAW_SEPARATE_44);
            circleDrawConcentricButton.setSelected(m == MouseMode.CIRCLE_DRAW_CONCENTRIC_48);
            circleDrawConcentricSelectButton.setSelected(m == MouseMode.CIRCLE_DRAW_CONCENTRIC_SELECT_49);
            circleDrawTwoConcentricButton.setSelected(m == MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50);
            circleDrawTangentLineButton.setSelected(m == MouseMode.CIRCLE_DRAW_TANGENT_LINE_45);
            circleDrawThreePointButton.setSelected(m == MouseMode.CIRCLE_DRAW_THREE_POINT_43);
            circleDrawInvertedButton.setSelected(m == MouseMode.CIRCLE_DRAW_INVERTED_46);
            sen_tokutyuu_color_henkouButton.setSelected(m == MouseMode.CIRCLE_CHANGE_COLOR_59);
            h_senbun_nyuryokuButton.setSelected(m == MouseMode.DRAW_CREASE_FREE_1 && f == FoldLineAdditionalInputMode.AUX_LINE_1);
            h_senbun_sakujyoButton.setSelected(m == MouseMode.LINE_SEGMENT_DELETE_3 && f == FoldLineAdditionalInputMode.AUX_LINE_1);
            l1Button.setSelected(m == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_1_53);
            l2Button.setSelected(m == MouseMode.DISPLAY_LENGTH_BETWEEN_POINTS_2_54);
            a1Button.setSelected(m == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_1_55);
            a2Button.setSelected(m == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_2_56);
            a3Button.setSelected(m == MouseMode.DISPLAY_ANGLE_BETWEEN_THREE_POINTS_3_57);
        }
    }

    public void setData(HistoryStateModel historyStateModel) {
        auxUndoTotalTextField.setText(String.valueOf(historyStateModel.getAuxHistoryTotal()));
    }
}
