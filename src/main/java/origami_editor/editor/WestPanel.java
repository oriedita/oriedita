package origami_editor.editor;

import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.drawing_worker.Drawing_Worker;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.record.memo.Memo;
import origami_editor.record.string_op.StringOp;

import javax.swing.*;
import java.awt.*;

public class WestPanel extends JPanel {
    private JPanel panel1;
    private JButton lineWidthDecreaseButton;
    private JButton lineWidthIncreaseButton;
    private JTextField lineSegmentDivisionTextField;
    private JTextField gridSizeTextField;
    private JTextField gridXATextField;
    private JTextField gridAngleTextField;
    private JCheckBox ckbox_cp_kaizen_folding;
    private UndoRedo undoRedo;
    private JButton pointSizeDecreaseButton;
    private JButton pointSizeIncreaseButton;
    private JButton antiAliasToggleButton;
    private JButton lineStyleChangeButton;
    private JButton senbun_nyuryokuButton;
    private JButton senbun_nyuryoku11Button;
    private JButton voronoiButton;
    private JButton oritatami_kanousenButton;
    private JButton senbun_entyouButton;
    private JButton senbun_entyou_2Button;
    private JButton kaku_toubunButton;
    private JButton naishinButton;
    private JButton suisenButton;
    private JButton orikaesiButton;
    private JButton renzoku_orikaesiButton;
    private JButton heikousenButton;
    private JButton heikousen_haba_siteiButton;
    private JButton oritatami_kanousen_and_kousitenkei_simpleButton;
    private JButton all_s_step_to_orisenButton;
    private JButton sakananohoneButton;
    private JButton fuku_orikaesiButton;
    private JButton senbun_b_nyuryokuButton;
    private JButton reflectButton;
    private JButton selectButton;
    private JButton unselectButton;
    private JButton selectAllButton;
    private JButton unselectAllButton;
    private JButton moveButton;
    private JButton move2p2pButton;
    private JButton copyButton;
    private JButton copy2p2pButton;
    private JButton deleteSelectedLineSegmentButton;
    private JButton senbun_sakujyoButton;
    private JButton kuro_senbun_sakujyoButton;
    private JButton senbun3_sakujyoButton;
    private JButton eda_kesiButton;
    private JButton M_nisuruButton;
    private JButton V_nisuruButton;
    private JButton E_nisuruButton;
    private JButton HK_nisuruButton;
    private JButton zen_yama_tani_henkanButton;
    private JButton senbun_henkan2Button;
    private JButton senbun_henkanButton;
    private JButton in_L_col_changeButton;
    private JButton on_L_col_changeButton;
    private JButton v_addButton;
    private JButton v_delButton;
    private JButton v_del_ccButton;
    private JButton v_del_allButton;
    private JButton v_del_all_ccButton;
    private JButton gridSizeDecreaseButton;
    private JButton gridSizeIncreaseButton;
    private JButton gridSizeSetButton;
    private JButton gridColorButton;
    private JButton gridLineWidthDecreaseButton;
    private JButton gridLineWidthIncreaseButton;
    private JButton i_kitei_jyoutaiButton;
    private JButton memori_tate_idouButton;
    private JButton memori_kankaku_syutokuButton;
    private JTextField intervalGridSizeTextField;
    private JButton memori_yoko_idouButton;
    private JButton intervalGridColorButton;
    private JTextField gridXBTextField;
    private JTextField gridXCTextField;
    private JTextField gridYATextField;
    private JTextField gridYBTextField;
    private JTextField gridYCTextField;
    private JButton setGridParametersButton;
    private JButton inputDataButton;
    private JCheckBox ckbox_select_nokosi;
    private JButton drawTwoColoredCpButton;
    private JButton suitei_01Button;
    private JButton koteimen_siteiButton;
    private JButton suitei_02Button;
    private JButton suitei_03Button;
    private JCheckBox coloredXRayButton;
    private JButton coloredXRayDecreaseButton;
    private JButton coloredXRayIncreaseButton;
    private JButton colRedButton;
    private JButton colBlueButton;
    private JButton colBlackButton;
    private JButton colCyanButton;
    private JButton lineSegmentDivisionSetButton;

    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    public JCheckBox getColoredXRayButton() {
        return coloredXRayButton;
    }

    public JCheckBox getCkbox_cp_kaizen_folding() {
        return ckbox_cp_kaizen_folding;
    }

    public JCheckBox getCkbox_select_nokosi() {
        return ckbox_select_nokosi;
    }

    public JTextField getGridAngleTextField() {
        return gridAngleTextField;
    }

    public JTextField getGridXATextField() {
        return gridXATextField;
    }

    public JTextField getGridXBTextField() {
        return gridXBTextField;
    }

    public JTextField getGridXCTextField() {
        return gridXCTextField;
    }

    public JTextField getGridYATextField() {
        return gridYATextField;
    }

    public JTextField getGridYBTextField() {
        return gridYBTextField;
    }

    public JTextField getGridYCTextField() {
        return gridYCTextField;
    }

    public JTextField getIntervalGridSizeTextField() {
        return intervalGridSizeTextField;
    }

    public JTextField getGridSizeTextField() {
        return gridSizeTextField;
    }

    public JTextField getLineSegmentDivisionTextField() {
        return lineSegmentDivisionTextField;
    }

    public JButton getSenbun_henkan2Button() {
        return senbun_henkan2Button;
    }

    public WestPanel(App app) {
        $$$setupUI$$$();
        undoRedo.addUndoActionListener(e -> {
            app.setHelp("qqq/undo.png");

            app.setTitle(app.es1.undo());
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        undoRedo.addRedoActionListener(e -> {
            app.setHelp("qqq/redo.png");

            app.setTitle(app.es1.redo());
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        undoRedo.addSetUndoCountActionListener(e -> {
            app.setHelp("qqq/undo_syutoku.png");
            int i_undo_suu_old = app.i_undo_suu;
            app.i_undo_suu = StringOp.String2int(undoRedo.getText(), i_undo_suu_old);
            if (app.i_undo_suu < 0) {
                app.i_undo_suu = 0;
            }
            undoRedo.setText(String.valueOf(app.i_undo_suu));
            app.es1.set_Ubox_undo_suu(app.i_undo_suu);
        });
        lineWidthDecreaseButton.addActionListener(e -> {
            app.displayLineWidth = app.displayLineWidth - 2;
            if (app.displayLineWidth < 1) {
                app.displayLineWidth = 1;
            }
            app.setHelp("qqq/senhaba_sage.png");
            app.repaintCanvas();
        });
        lineWidthIncreaseButton.addActionListener(e -> {
            app.displayLineWidth = app.displayLineWidth + 2;
            app.setHelp("qqq/senhaba_age.png");
            app.repaintCanvas();
        });
        pointSizeDecreaseButton.addActionListener(e -> {
            app.setHelp("qqq/tenhaba_sage.png");

            app.pointSize = app.pointSize - 1;
            if (app.pointSize < 0) {
                app.pointSize = 0;
            }
            app.es1.setPointSize(app.pointSize);

            app.repaintCanvas();
        });
        pointSizeIncreaseButton.addActionListener(e -> {
            app.setHelp("qqq/tenhaba_age.png");

            app.pointSize = app.pointSize + 1;
            app.es1.setPointSize(app.pointSize);

            app.repaintCanvas();
        });
        antiAliasToggleButton.addActionListener(e -> {
            app.antiAlias = !app.antiAlias;

            app.setHelp("qqq/anti_alias.png");

            app.repaintCanvas();
        });
        lineStyleChangeButton.addActionListener(e -> {
            app.Button_shared_operation();
            app.lineStyle = app.lineStyle.advance();

            app.setHelp("qqq/orisen_hyougen.png");

            app.repaintCanvas();
        });
        colRedButton.addActionListener(e -> {
            app.setHelp("qqq/ButtonCol_red.png");
            app.buttonColorReset();
            colRedButton.setForeground(Color.black);
            colRedButton.setBackground(Color.red);
            app.currentLineColor = LineColor.RED_1;
            app.es1.setColor(app.currentLineColor);

            app.repaintCanvas();
        });
        colBlueButton.addActionListener(e -> {

            app.setHelp("qqq/ButtonCol_blue.png");
            app.buttonColorReset();
            colBlueButton.setForeground(Color.black);
            colBlueButton.setBackground(Color.blue);
            app.currentLineColor = LineColor.BLUE_2;
            app.es1.setColor(app.currentLineColor);

            app.repaintCanvas();
        });
        colBlackButton.addActionListener(e -> {
            app.setHelp("qqq/ButtonCol_black.png");

            app.buttonColorReset();
            colBlackButton.setForeground(Color.white);
            colBlackButton.setBackground(Color.black);
            app.currentLineColor = LineColor.BLACK_0;
            app.es1.setColor(app.currentLineColor);

            app.repaintCanvas();
        });
        colCyanButton.addActionListener(e -> {
            app.setHelp("qqq/ButtonCol_cyan.png");

            app.buttonColorReset();
            colCyanButton.setForeground(Color.black);
            colCyanButton.setBackground(Color.cyan);
            app.currentLineColor = LineColor.CYAN_3;
            app.es1.setColor(app.currentLineColor);

            app.repaintCanvas();
        });
        senbun_nyuryokuButton.addActionListener(e -> {
            app.setHelp("qqq/senbun_nyuryoku.png");
            app.foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            app.es1.setFoldLineAdditional(app.foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            app.mouseMode = MouseMode.DRAW_CREASE_FREE_1;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_FREE_1;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_nyuryoku11Button.addActionListener(e -> {
            app.setHelp("qqq/senbun_nyuryoku11.png");
            app.mouseMode = MouseMode.DRAW_CREASE_RESTRICTED_11;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DRAW_CREASE_RESTRICTED_11;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        voronoiButton.addActionListener(e -> {
            app.setHelp("qqq/Voronoi.png");
            app.foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            app.es1.setFoldLineAdditional(app.foldLineAdditionalInputMode);//このボタンと機能は補助絵線共通に使っているのでi_orisen_hojyosenの指定がいる
            app.mouseMode = MouseMode.VORONOI_CREATE_62;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.VORONOI_CREATE_62;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        oritatami_kanousenButton.addActionListener(e -> {
            app.setHelp("qqq/oritatami_kanousen.png");

            app.mouseMode = MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_entyouButton.addActionListener(e -> {
            app.setHelp("qqq/senbun_entyou.png");

            app.mouseMode = MouseMode.LENGTHEN_CREASE_5;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LENGTHEN_CREASE_5;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_entyou_2Button.addActionListener(e -> {
            app.setHelp("qqq/senbun_entyou_2.png");

            app.mouseMode = MouseMode.CREASE_LENGTHEN_70;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LENGTHEN_CREASE_5;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        kaku_toubunButton.addActionListener(e -> {
            app.setHelp("qqq/kaku_toubun.png");

            app.mouseMode = MouseMode.SQUARE_BISECTOR_7;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.SQUARE_BISECTOR_7;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        naishinButton.addActionListener(e -> {
            app.setHelp("qqq/naishin.png");

            app.mouseMode = MouseMode.INWARD_8;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.INWARD_8;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        suisenButton.addActionListener(e -> {
            app.setHelp("qqq/suisen.png");

            app.mouseMode = MouseMode.PERPENDICULAR_DRAW_9;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.PERPENDICULAR_DRAW_9;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        orikaesiButton.addActionListener(e -> {
            app.setHelp("qqq/orikaesi.png");

            app.mouseMode = MouseMode.SYMMETRIC_DRAW_10;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.SYMMETRIC_DRAW_10;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        renzoku_orikaesiButton.addActionListener(e -> {
            app.setHelp("qqq/renzoku_orikaesi.png");

            app.mouseMode = MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        heikousenButton.addActionListener(e -> {
            app.setHelp("qqq/heikousen.png");
            app.mouseMode = MouseMode.PARALLEL_DRAW_40;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.PARALLEL_DRAW_40;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        heikousen_haba_siteiButton.addActionListener(e -> {
            app.setHelp("qqq/heikousen_haba_sitei.png");
            app.mouseMode = MouseMode.PARALLEL_DRAW_WIDTH_51;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.PARALLEL_DRAW_WIDTH_51;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        oritatami_kanousen_and_kousitenkei_simpleButton.addActionListener(e -> {
            app.setHelp("qqq/oritatami_kanousen_and_kousitenkei_simple.png");

            app.mouseMode = MouseMode.FOLDABLE_LINE_DRAW_71;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.FOLDABLE_LINE_DRAW_71;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        all_s_step_to_orisenButton.addActionListener(e -> {
            System.out.println("i_egaki_dankai = " + app.es1.i_drawing_stage);
            System.out.println("i_kouho_dankai = " + app.es1.i_candidate_stage);

            app.setHelp("qqq/all_s_step_to_orisen.png");
            app.es1.all_s_step_to_orisen();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        sakananohoneButton.addActionListener(e -> {
            app.setHelp("qqq/sakananohone.png");

            app.mouseMode = MouseMode.FISH_BONE_DRAW_33;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.FISH_BONE_DRAW_33;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        fuku_orikaesiButton.addActionListener(e -> {
            app.setHelp("qqq/fuku_orikaesi.png");

            app.mouseMode = MouseMode.DOUBLE_SYMMETRIC_DRAW_35;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.DOUBLE_SYMMETRIC_DRAW_35;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        lineSegmentDivisionSetButton.addActionListener(e -> {

            int i_orisen_bunkatu_suu_old = app.foldLineDividingNumber;
            app.foldLineDividingNumber = StringOp.String2int(lineSegmentDivisionTextField.getText(), i_orisen_bunkatu_suu_old);
            if (app.foldLineDividingNumber < 1) {
                app.foldLineDividingNumber = 1;
            }
            lineSegmentDivisionTextField.setText(String.valueOf(app.foldLineDividingNumber));
            app.es1.setFoldLineDividingNumber(app.foldLineDividingNumber);

            app.setHelp("qqq/senbun_bunkatu_set.png");
            app.mouseMode = MouseMode.LINE_SEGMENT_DIVISION_27;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_DIVISION_27;
            System.out.println("mouseMode = " + app.mouseMode);

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_b_nyuryokuButton.addActionListener(e -> {

            int i_orisen_bunkatu_suu_old = app.foldLineDividingNumber;
            app.foldLineDividingNumber = StringOp.String2int(lineSegmentDivisionTextField.getText(), i_orisen_bunkatu_suu_old);
            if (app.foldLineDividingNumber < 1) {
                app.foldLineDividingNumber = 1;
            }
            lineSegmentDivisionTextField.setText(String.valueOf(app.foldLineDividingNumber));
            app.es1.setFoldLineDividingNumber(app.foldLineDividingNumber);

            app.setHelp("qqq/senbun_b_nyuryoku.png");
            app.mouseMode = MouseMode.LINE_SEGMENT_DIVISION_27;
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.LINE_SEGMENT_DIVISION_27;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        selectButton.addActionListener(e -> {
            app.setHelp("qqq/Select.png");

            app.mouseMode = MouseMode.CREASE_SELECT_19;
            System.out.println("mouseMode = " + app.mouseMode);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        selectAllButton.addActionListener(e -> {

            app.setHelp("qqq/select_all.png");
            app.es1.select_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        unselectButton.addActionListener(e -> {
            app.setHelp("qqq/unselect.png");

            app.mouseMode = MouseMode.CREASE_UNSELECT_20;
            System.out.println("mouseMode = " + app.mouseMode);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        unselectAllButton.addActionListener(e -> {

            app.setHelp("qqq/unselect_all.png");
            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        moveButton.addActionListener(e -> {
            app.setHelp("qqq/move.png");
            app.selectionOperationMode = App.SelectionOperationMode.MOVE_1;
            app.Button_sel_mou_wakukae();

            app.mouseMode = MouseMode.CREASE_MOVE_21;
            app.Button_shared_operation();
            app.repaintCanvas();
            System.out.println("mouseMode = " + app.mouseMode);
        });
        move2p2pButton.addActionListener(e -> {
            app.setHelp("qqq/move_2p2p.png");
            app.selectionOperationMode = App.SelectionOperationMode.MOVE4P_2;
            app.Button_sel_mou_wakukae();


            app.mouseMode = MouseMode.CREASE_MOVE_4P_31;
            System.out.println("mouseMode = " + app.mouseMode);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        copyButton.addActionListener(e -> {
            app.setHelp("qqq/copy_paste.png");
            app.selectionOperationMode = App.SelectionOperationMode.COPY_3;
            app.Button_sel_mou_wakukae();


            app.mouseMode = MouseMode.CREASE_COPY_22;
            System.out.println("mouseMode = " + app.mouseMode);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        copy2p2pButton.addActionListener(e -> {
            app.setHelp("qqq/copy_paste_2p2p.png");
            app.selectionOperationMode = App.SelectionOperationMode.COPY4P_4;
            app.Button_sel_mou_wakukae();


            app.mouseMode = MouseMode.CREASE_COPY_4P_32;
            System.out.println("mouseMode = " + app.mouseMode);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        reflectButton.addActionListener(e -> {
            app.setHelp("qqq/kyouei.png");
            app.selectionOperationMode = App.SelectionOperationMode.MIRROR_5;
            app.Button_sel_mou_wakukae();

            app.mouseMode = MouseMode.DRAW_CREASE_SYMMETRIC_12;
            System.out.println("mouseMode = " + app.mouseMode);
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        deleteSelectedLineSegmentButton.addActionListener(e -> {
            app.setHelp("qqq/del_selected_senbun.png");
            app.es1.del_selected_senbun();
            app.es1.record();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_sakujyoButton.addActionListener(e -> {
            app.setHelp("qqq/senbun_sakujyo.png");
            app.mouseMode = MouseMode.LINE_SEGMENT_DELETE_3;
            System.out.println("mouseMode = " + app.mouseMode);


            app.foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.POLY_LINE_0;//=0は折線入力　=1は補助線入力モード
            app.es1.setFoldLineAdditional(app.foldLineAdditionalInputMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        kuro_senbun_sakujyoButton.addActionListener(e -> {
            app.setHelp("qqq/kuro_senbun_sakujyo.png");
            app.mouseMode = MouseMode.LINE_SEGMENT_DELETE_3;
            System.out.println("mouseMode = " + app.mouseMode);


            app.foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.BLACK_LINE_2;//= 2 is the black polygonal line deletion mode
            app.es1.setFoldLineAdditional(app.foldLineAdditionalInputMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun3_sakujyoButton.addActionListener(e -> {
            app.setHelp("qqq/senbun3_sakujyo.png");
            app.mouseMode = MouseMode.LINE_SEGMENT_DELETE_3;
            System.out.println("mouseMode = " + app.mouseMode);


            app.foldLineAdditionalInputMode = Drawing_Worker.FoldLineAdditionalInputMode.AUX_LIVE_LINE_3;//= 0 is polygonal line input = 1 is auxiliary line input mode = 3 is for auxiliary live line only
            app.es1.setFoldLineAdditional(app.foldLineAdditionalInputMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        eda_kesiButton.addActionListener(e -> {
            app.setHelp("qqq/eda_kesi.png");
            app.es1.point_removal();
            app.es1.overlapping_line_removal();
            app.es1.branch_trim(0.000001);
            app.es1.circle_organize();
            app.es1.record();
            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        M_nisuruButton.addActionListener(e -> {
            app.setHelp("qqq/M_nisuru.png");
            app.buttonReset();
            M_nisuruButton.setForeground(Color.black);
            M_nisuruButton.setBackground(Color.red);
            //currentLineColor=1;es1.setcolor(currentLineColor);
            app.mouseMode = MouseMode.CREASE_MAKE_MOUNTAIN_23;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        V_nisuruButton.addActionListener(e -> {
            app.setHelp("qqq/V_nisuru.png");
            app.buttonReset();
            V_nisuruButton.setForeground(Color.black);
            V_nisuruButton.setBackground(Color.blue);
            //currentLineColor=1;es1.setcolor(currentLineColor);
            app.mouseMode = MouseMode.CREASE_MAKE_VALLEY_24;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        E_nisuruButton.addActionListener(e -> {
            app.setHelp("qqq/E_nisuru.png");
            app.buttonReset();
            E_nisuruButton.setForeground(Color.white);
            E_nisuruButton.setBackground(Color.black);
            //currentLineColor=1;es1.setcolor(currentLineColor);
            app.mouseMode = MouseMode.CREASE_MAKE_EDGE_25;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        HK_nisuruButton.addActionListener(e -> {
            app.setHelp("qqq/HK_nisuru.png");
            app.buttonReset();
            HK_nisuruButton.setForeground(Color.white);
            HK_nisuruButton.setBackground(new Color(100, 200, 200));
            //currentLineColor=1;es1.setcolor(currentLineColor);
            app.mouseMode = MouseMode.CREASE_MAKE_AUX_60;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        zen_yama_tani_henkanButton.addActionListener(e -> {
            app.setHelp("qqq/zen_yama_tani_henkan.png");
            app.es1.allMountainValleyChange();
            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_henkan2Button.addActionListener(e -> {
            app.setHelp("qqq/senbun_henkan2.png");
            app.buttonReset();
            senbun_henkan2Button.setBackground(new Color(138, 43, 226));

            app.mouseMode = MouseMode.CREASE_TOGGLE_MV_58;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        senbun_henkanButton.addActionListener(e -> {
            app.setHelp("qqq/senbun_henkan.png");
            app.buttonReset();

            app.mouseMode = MouseMode.CHANGE_CREASE_TYPE_4;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        in_L_col_changeButton.addActionListener(e -> {
            app.setHelp("qqq/in_L_col_change.png");

            app.mouseMode = MouseMode.CREASE_MAKE_MV_34;
            System.out.println("mouseMode = " + app.mouseMode);
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.CREASE_MAKE_MV_34;

            if (app.currentLineColor == LineColor.BLACK_0) {
                app.currentLineColor = LineColor.RED_1;
                app.es1.setColor(app.currentLineColor);                                        //最初の折線の色を指定する。0は黒、1は赤、2は青。
                app.buttonColorReset();
                colRedButton.setForeground(Color.black);
                colRedButton.setBackground(Color.red);    //折線のボタンの色設定
            }

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        on_L_col_changeButton.addActionListener(e -> {
            app.setHelp("qqq/on_L_col_change.png");
            app.mouseMode = MouseMode.CREASES_ALTERNATE_MV_36;
            System.out.println("mouseMode = " + app.mouseMode);
            app.iro_sitei_ato_ni_jissisuru_sagyou_bangou = MouseMode.CREASES_ALTERNATE_MV_36;


            if (app.currentLineColor == LineColor.BLACK_0) {
                app.currentLineColor = LineColor.BLUE_2;
                app.es1.setColor(app.currentLineColor);                                        //最初の折線の色を指定する。0は黒、1は赤、2は青。
                app.buttonColorReset();
                colBlueButton.setForeground(Color.black);
                colBlueButton.setBackground(Color.blue);    //折線のボタンの色設定
            }

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_addButton.addActionListener(e -> {
            app.setHelp("qqq/v_add.png");
            app.mouseMode = MouseMode.DRAW_POINT_14;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_delButton.addActionListener(e -> {
            app.setHelp("qqq/v_del.png");

            app.mouseMode = MouseMode.DELETE_POINT_15;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_del_ccButton.addActionListener(e -> {
            app.setHelp("qqq/v_del_cc.png");

            app.mouseMode = MouseMode.VERTEX_DELETE_ON_CREASE_41;
            System.out.println("mouseMode = " + app.mouseMode);

            app.es1.unselect_all();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_del_allButton.addActionListener(e -> {
            app.setHelp("qqq/v_del_all.png");
            //mouseMode=19;
            app.es1.v_del_all();
            System.out.println("es1.v_del_all()");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        v_del_all_ccButton.addActionListener(e -> {
            app.setHelp("qqq/v_del_all_cc.png");
            app.es1.v_del_all_cc();
            System.out.println("es1.v_del_all_cc()");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        gridSizeDecreaseButton.addActionListener(e -> {
            app.setHelp("qqq/kitei2.png");

            app.gridSize = app.gridSize / 2;
            if (app.gridSize < 1) {
                app.gridSize = 1;
            }

            if (app.gridSize < -0) {
                app.gridSize = -1;
            }

            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)

            gridSizeTextField.setText(String.valueOf(app.gridSize));
            app.es1.setGridSize(app.gridSize);
            app.repaintCanvas();
        });
        gridSizeSetButton.addActionListener(e -> {
            app.setHelp("qqq/syutoku.png");
            app.setGridSize();
        });
        gridSizeIncreaseButton.addActionListener(e -> {
            app.setHelp("qqq/kitei.png");

            app.gridSize = app.gridSize * 2;

            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeIncreaseButton.setForeground(Color.black);
                gridSizeIncreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            //ボタンの色変え
            if (app.gridSize >= 1) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(Color.white);
            }
            if (app.gridSize == 0) {
                gridSizeDecreaseButton.setForeground(Color.black);
                gridSizeDecreaseButton.setBackground(new Color(0, 200, 200));
            }
            //ボタンの色変え(ここまで)
            gridSizeTextField.setText(String.valueOf(app.gridSize));
            app.es1.setGridSize(app.gridSize);
            app.repaintCanvas();
        });
        gridColorButton.addActionListener(e -> {
            app.setHelp("qqq/kousi_color.png");
            //Button_kyoutuu_sagyou();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", new Color(230, 230, 230));
            if (color != null) {
                app.kus.setGridColor(color);
            }
            //以上でやりたいことは書き終わり

            app.repaintCanvas();
        });
        gridLineWidthDecreaseButton.addActionListener(e -> {
            app.kus.decreaseGridLineWidth();
            app.setHelp("qqq/kousi_senhaba_sage.png");
            app.repaintCanvas();
        });
        gridLineWidthIncreaseButton.addActionListener(e -> {
            app.kus.increaseGridLineWidth();
            app.setHelp("qqq/kousi_senhaba_age.png");
            app.repaintCanvas();
        });
        i_kitei_jyoutaiButton.addActionListener(e -> {
            app.setHelp("qqq/i_kitei_jyoutai.png");

            app.es1.setBaseState(app.es1.getBaseState().advance());
            app.repaintCanvas();
        });
        memori_tate_idouButton.addActionListener(e -> {
            app.setHelp("qqq/memori_tate_idou.png");
            app.es1.a_to_parallel_scale_position_change();

            app.repaintCanvas();
        });
        memori_kankaku_syutokuButton.addActionListener(e -> {
            app.setHelp("qqq/memori_kankaku_syutoku.png");
            int scale_interval_old = app.scale_interval;
            app.scale_interval = StringOp.String2int(intervalGridSizeTextField.getText(), scale_interval_old);
            if (app.scale_interval < 0) {
                app.scale_interval = 1;
            }
            intervalGridSizeTextField.setText(String.valueOf(app.scale_interval));
            app.es1.set_a_to_parallel_scale_interval(app.scale_interval);
            app.es1.set_b_to_parallel_scale_interval(app.scale_interval);
        });
        memori_yoko_idouButton.addActionListener(e -> {
            app.setHelp("qqq/memori_yoko_idou.png");

            app.es1.b_to_parallel_scale_position_change();
        });
        intervalGridColorButton.addActionListener(e -> {
            app.setHelp("qqq/kousi_memori_color.png");
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;


            //以下にやりたいことを書く
            Color color = JColorChooser.showDialog(null, "Col", new Color(180, 200, 180));
            if (color != null) {
                app.kus.setGridScaleColor(color);
            }
            //以上でやりたいことは書き終わり

            app.repaintCanvas();
        });
        setGridParametersButton.addActionListener(e -> {
            app.setHelp("qqq/kousi_syutoku.png");
            app.setGrid();
            app.repaintCanvas();
        });
        inputDataButton.addActionListener(e -> {
            app.setHelp("qqq/yomi_tuika.png");

            app.Button_shared_operation();

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            Memo memo_temp;

            System.out.println("readFile2Memo() 開始");
            memo_temp = app.readFile2Memo();
            System.out.println("readFile2Memo() 終了");

            if (memo_temp.getLineCount() > 0) {
                app.es1.setMemo_for_reading_tuika(memo_temp);
                app.es1.record();
                app.repaintCanvas();
            }
        });
        ckbox_cp_kaizen_folding.addActionListener(e -> {
            app.setHelp("qqq/ckbox_cp_kaizen_oritatami.png");

            app.repaintCanvas();
        });
        ckbox_select_nokosi.addActionListener(e -> {
            app.setHelp("qqq/ckbox_select_nokosi.png");

            app.repaintCanvas();
        });
        drawTwoColoredCpButton.addActionListener(e -> {
            app.setHelp("qqq/2syoku_tenkaizu.png");

            app.Ss0 = app.es1.getForSelectFolding();

            if (app.es1.getFoldLineTotalForSelectFolding() == 0) {        //折り線選択無し
                app.noSelectedPolygonalLineWarning();//Warning: There is no selected polygonal line


            } else if (app.es1.getFoldLineTotalForSelectFolding() > 0) {
                app.folding_prepare();//ここでOZがOAZ(0)からOAZ(i)に切り替わる
                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_5;

                if (!app.subThreadRunning) {
                    app.subThreadRunning = true;
                    app.subThreadMode = SubThread.Mode.TWO_COLORED_4;
                    app.mks();//新しいスレッドを作る
                    app.sub.start();
                }
            }

            app.es1.unselect_all();
            app.Button_shared_operation();
        });
        suitei_01Button.addActionListener(e -> {
            app.setHelp("qqq/suitei_01.png");

            app.oritatame(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_1);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (ckbox_select_nokosi.isSelected()) {
            } else {
                app.es1.unselect_all();
            }

            app.Button_shared_operation();
        });
        koteimen_siteiButton.addActionListener(e -> {
            app.setHelp("qqq/koteimen_sitei.png");
            if (app.OZ.displayStyle != FoldedFigure.DisplayStyle.NONE_0) {
                app.mouseMode = MouseMode.CHANGE_STANDARD_FACE_103;
                System.out.println("mouseMode = " + app.mouseMode);
            }
            app.Button_shared_operation();
        });
        suitei_02Button.addActionListener(e -> {
            app.setHelp("qqq/suitei_02.png");

            app.oritatame(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_2);//引数の意味は(i_fold_type , i_suitei_meirei);
            if (!ckbox_select_nokosi.isSelected()) {
                app.es1.unselect_all();
            }

            app.Button_shared_operation();
        });
        suitei_03Button.addActionListener(e -> {
            app.setHelp("qqq/suitei_03.png");

            app.oritatame(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_3);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (ckbox_select_nokosi.isSelected()) {
            } else {
                app.es1.unselect_all();
            }
            app.Button_shared_operation();
        });
        coloredXRayButton.addActionListener(e -> {
            app.setHelp("qqq/ckbox_toukazu_color.png");
            if (coloredXRayButton.isSelected()) {
                app.OZ.transparencyColor = true;
                System.out.println("coloredXRayButton.isSelected()");
            }//カラーの透過図
            else {
                app.OZ.transparencyColor = false;
                System.out.println("coloredXRayButton.is not Selected()");
            }
            app.repaintCanvas();
        });
        coloredXRayDecreaseButton.addActionListener(e -> {
            app.OZ.decreaseTransparency();
            app.setHelp("qqq/toukazu_color_sage.png");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        coloredXRayIncreaseButton.addActionListener(e -> {
            app.OZ.increaseTransparency();
            app.setHelp("qqq/toukazu_color_age.png");
            app.Button_shared_operation();
            app.repaintCanvas();
        });
    }

    public JButton getGridSizeDecreaseButton() {
        return gridSizeDecreaseButton;
    }

    public JButton getGridSizeIncreaseButton() {
        return gridSizeIncreaseButton;
    }

    public JButton getM_nisuruButton() {
        return M_nisuruButton;
    }

    public JButton getV_nisuruButton() {
        return V_nisuruButton;
    }

    public JButton getE_nisuruButton() {
        return E_nisuruButton;
    }

    public JButton getHK_nisuruButton() {
        return HK_nisuruButton;
    }

    public JButton getReflectButton() {
        return reflectButton;
    }

    public JButton getMoveButton() {
        return moveButton;
    }

    public JButton getMove2p2pButton() {
        return move2p2pButton;
    }

    public JButton getCopyButton() {
        return copyButton;
    }

    public JButton getCopy2p2pButton() {
        return copy2p2pButton;
    }

    public JButton getColRedButton() {
        return colRedButton;
    }

    public JButton getColBlueButton() {
        return colBlueButton;
    }

    public JButton getColBlackButton() {
        return colBlackButton;
    }

    public JButton getColCyanButton() {
        return colCyanButton;
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
        panel1.setLayout(new GridBagLayout());
        undoRedo = new UndoRedo();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(undoRedo.$$$getRootComponent$$$(), gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        lineWidthDecreaseButton = new JButton();
        lineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(lineWidthDecreaseButton, gbc);
        lineWidthIncreaseButton = new JButton();
        lineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(lineWidthIncreaseButton, gbc);
        pointSizeDecreaseButton = new JButton();
        pointSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(pointSizeDecreaseButton, gbc);
        pointSizeIncreaseButton = new JButton();
        pointSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/tenhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(pointSizeIncreaseButton, gbc);
        antiAliasToggleButton = new JButton();
        antiAliasToggleButton.setMinimumSize(new Dimension(60, 30));
        antiAliasToggleButton.setText("a_a");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(antiAliasToggleButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel3, gbc);
        colRedButton = new JButton();
        colRedButton.setBackground(new Color(-6908266));
        colRedButton.setText("M");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colRedButton, gbc);
        colBlueButton = new JButton();
        colBlueButton.setBackground(new Color(-6908266));
        colBlueButton.setText("V");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colBlueButton, gbc);
        colBlackButton = new JButton();
        colBlackButton.setBackground(new Color(-6908266));
        colBlackButton.setText("E");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colBlackButton, gbc);
        colCyanButton = new JButton();
        colCyanButton.setBackground(new Color(-6908266));
        colCyanButton.setText("A");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(colCyanButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel4, gbc);
        lineStyleChangeButton = new JButton();
        lineStyleChangeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orisen_hyougen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel4.add(lineStyleChangeButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel5, gbc);
        senbun_sakujyoButton = new JButton();
        senbun_sakujyoButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(senbun_sakujyoButton, gbc);
        kuro_senbun_sakujyoButton = new JButton();
        kuro_senbun_sakujyoButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kuro_senbun_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(kuro_senbun_sakujyoButton, gbc);
        senbun3_sakujyoButton = new JButton();
        senbun3_sakujyoButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun3_sakujyo.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(senbun3_sakujyoButton, gbc);
        eda_kesiButton = new JButton();
        eda_kesiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/eda_kesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(eda_kesiButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel6, gbc);
        M_nisuruButton = new JButton();
        M_nisuruButton.setIcon(new ImageIcon(getClass().getResource("/ppp/M_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(M_nisuruButton, gbc);
        V_nisuruButton = new JButton();
        V_nisuruButton.setIcon(new ImageIcon(getClass().getResource("/ppp/V_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(V_nisuruButton, gbc);
        E_nisuruButton = new JButton();
        E_nisuruButton.setIcon(new ImageIcon(getClass().getResource("/ppp/E_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(E_nisuruButton, gbc);
        HK_nisuruButton = new JButton();
        HK_nisuruButton.setIcon(new ImageIcon(getClass().getResource("/ppp/HK_nisuru.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(HK_nisuruButton, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel7, gbc);
        zen_yama_tani_henkanButton = new JButton();
        zen_yama_tani_henkanButton.setText("AC");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(zen_yama_tani_henkanButton, gbc);
        senbun_henkan2Button = new JButton();
        senbun_henkan2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(senbun_henkan2Button, gbc);
        senbun_henkanButton = new JButton();
        senbun_henkanButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_henkan.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(senbun_henkanButton, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel8, gbc);
        in_L_col_changeButton = new JButton();
        in_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/in_L_col_change.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(in_L_col_changeButton, gbc);
        on_L_col_changeButton = new JButton();
        on_L_col_changeButton.setIcon(new ImageIcon(getClass().getResource("/ppp/on_L_col_change.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(on_L_col_changeButton, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 21;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel9, gbc);
        v_addButton = new JButton();
        v_addButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_add.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_addButton, gbc);
        v_delButton = new JButton();
        v_delButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_delButton, gbc);
        v_del_ccButton = new JButton();
        v_del_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_cc.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(v_del_ccButton, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 22;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel10, gbc);
        v_del_allButton = new JButton();
        v_del_allButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(v_del_allButton, gbc);
        v_del_all_ccButton = new JButton();
        v_del_all_ccButton.setIcon(new ImageIcon(getClass().getResource("/ppp/v_del_all_cc.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(v_del_all_ccButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer1, gbc);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 24;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel11, gbc);
        gridSizeDecreaseButton = new JButton();
        gridSizeDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(gridSizeDecreaseButton, gbc);
        gridSizeTextField = new JTextField();
        gridSizeTextField.setColumns(2);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(gridSizeTextField, gbc);
        gridSizeSetButton = new JButton();
        gridSizeSetButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(gridSizeSetButton, gbc);
        gridSizeIncreaseButton = new JButton();
        gridSizeIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kitei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(gridSizeIncreaseButton, gbc);
        gridColorButton = new JButton();
        gridColorButton.setText("C");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(gridColorButton, gbc);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 25;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel12, gbc);
        gridLineWidthDecreaseButton = new JButton();
        gridLineWidthDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(gridLineWidthDecreaseButton, gbc);
        gridLineWidthIncreaseButton = new JButton();
        gridLineWidthIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kousi_senhaba_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(gridLineWidthIncreaseButton, gbc);
        i_kitei_jyoutaiButton = new JButton();
        i_kitei_jyoutaiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/i_kitei_jyoutai.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(i_kitei_jyoutaiButton, gbc);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 26;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel13, gbc);
        memori_tate_idouButton = new JButton();
        memori_tate_idouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_tate_idou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel13.add(memori_tate_idouButton, gbc);
        intervalGridSizeTextField = new JTextField();
        intervalGridSizeTextField.setText("8");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel13.add(intervalGridSizeTextField, gbc);
        memori_kankaku_syutokuButton = new JButton();
        memori_kankaku_syutokuButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel13.add(memori_kankaku_syutokuButton, gbc);
        memori_yoko_idouButton = new JButton();
        memori_yoko_idouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/memori_yoko_idou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel13.add(memori_yoko_idouButton, gbc);
        intervalGridColorButton = new JButton();
        intervalGridColorButton.setText("C");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel13.add(intervalGridColorButton, gbc);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 27;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel14, gbc);
        gridXATextField = new JTextField();
        gridXATextField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel14.add(gridXATextField, gbc);
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel14.add(label1, gbc);
        gridXBTextField = new JTextField();
        gridXBTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel14.add(gridXBTextField, gbc);
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel14.add(label2, gbc);
        gridXCTextField = new JTextField();
        gridXCTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel14.add(gridXCTextField, gbc);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 28;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel15, gbc);
        gridYATextField = new JTextField();
        gridYATextField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel15.add(gridYATextField, gbc);
        final JLabel label3 = new JLabel();
        label3.setIcon(new ImageIcon(getClass().getResource("/ppp/plus_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel15.add(label3, gbc);
        gridYBTextField = new JTextField();
        gridYBTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel15.add(gridYBTextField, gbc);
        final JLabel label4 = new JLabel();
        label4.setIcon(new ImageIcon(getClass().getResource("/ppp/root_min.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel15.add(label4, gbc);
        gridYCTextField = new JTextField();
        gridYCTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel15.add(gridYCTextField, gbc);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 29;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel16, gbc);
        gridAngleTextField = new JTextField();
        gridAngleTextField.setHorizontalAlignment(11);
        gridAngleTextField.setText("90.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(gridAngleTextField, gbc);
        setGridParametersButton = new JButton();
        setGridParametersButton.setText("Set");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel16.add(setGridParametersButton, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 30;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer2, gbc);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 31;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel17, gbc);
        inputDataButton = new JButton();
        inputDataButton.setText("Op");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel17.add(inputDataButton, gbc);
        ckbox_cp_kaizen_folding = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel17.add(ckbox_cp_kaizen_folding, gbc);
        ckbox_select_nokosi = new JCheckBox();
        ckbox_select_nokosi.setToolTipText("ckbox_select_nokosi");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel17.add(ckbox_select_nokosi, gbc);
        drawTwoColoredCpButton = new JButton();
        drawTwoColoredCpButton.setIcon(new ImageIcon(getClass().getResource("/ppp/2syoku_tenkaizu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel17.add(drawTwoColoredCpButton, gbc);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 32;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel18, gbc);
        suitei_01Button = new JButton();
        suitei_01Button.setText("CP_rcg");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel18.add(suitei_01Button, gbc);
        koteimen_siteiButton = new JButton();
        koteimen_siteiButton.setText("S_face");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel18.add(koteimen_siteiButton, gbc);
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 33;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel19, gbc);
        suitei_02Button = new JButton();
        suitei_02Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_02.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel19.add(suitei_02Button, gbc);
        suitei_03Button = new JButton();
        suitei_03Button.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_03.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel19.add(suitei_03Button, gbc);
        coloredXRayButton = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel19.add(coloredXRayButton, gbc);
        coloredXRayDecreaseButton = new JButton();
        coloredXRayDecreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_sage.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel19.add(coloredXRayDecreaseButton, gbc);
        coloredXRayIncreaseButton = new JButton();
        coloredXRayIncreaseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/ck4_color_age.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        panel19.add(coloredXRayIncreaseButton, gbc);
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel20, gbc);
        senbun_nyuryokuButton = new JButton();
        senbun_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(senbun_nyuryokuButton, gbc);
        senbun_nyuryoku11Button = new JButton();
        senbun_nyuryoku11Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_nyuryoku11.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(senbun_nyuryoku11Button, gbc);
        voronoiButton = new JButton();
        voronoiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Voronoi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(voronoiButton, gbc);
        oritatami_kanousenButton = new JButton();
        oritatami_kanousenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(oritatami_kanousenButton, gbc);
        senbun_entyouButton = new JButton();
        senbun_entyouButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(senbun_entyouButton, gbc);
        senbun_entyou_2Button = new JButton();
        senbun_entyou_2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_entyou_2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(senbun_entyou_2Button, gbc);
        kaku_toubunButton = new JButton();
        kaku_toubunButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kaku_toubun.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(kaku_toubunButton, gbc);
        naishinButton = new JButton();
        naishinButton.setIcon(new ImageIcon(getClass().getResource("/ppp/naishin.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel20.add(naishinButton, gbc);
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel21, gbc);
        suisenButton = new JButton();
        suisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suisen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(suisenButton, gbc);
        orikaesiButton = new JButton();
        orikaesiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(orikaesiButton, gbc);
        heikousenButton = new JButton();
        heikousenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(heikousenButton, gbc);
        heikousen_haba_siteiButton = new JButton();
        heikousen_haba_siteiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/heikousen_haba_sitei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(heikousen_haba_siteiButton, gbc);
        all_s_step_to_orisenButton = new JButton();
        all_s_step_to_orisenButton.setIcon(new ImageIcon(getClass().getResource("/ppp/all_s_step_to_orisen.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(all_s_step_to_orisenButton, gbc);
        sakananohoneButton = new JButton();
        sakananohoneButton.setIcon(new ImageIcon(getClass().getResource("/ppp/sakananohone.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(sakananohoneButton, gbc);
        lineSegmentDivisionTextField = new JTextField();
        lineSegmentDivisionTextField.setColumns(2);
        lineSegmentDivisionTextField.setText("2");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(lineSegmentDivisionTextField, gbc);
        lineSegmentDivisionSetButton = new JButton();
        lineSegmentDivisionSetButton.setText("Set");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(lineSegmentDivisionSetButton, gbc);
        renzoku_orikaesiButton = new JButton();
        renzoku_orikaesiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/renzoku_orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(renzoku_orikaesiButton, gbc);
        oritatami_kanousen_and_kousitenkei_simpleButton = new JButton();
        oritatami_kanousen_and_kousitenkei_simpleButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oritatami_kanousen_and_kousitenkei_simple.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(oritatami_kanousen_and_kousitenkei_simpleButton, gbc);
        fuku_orikaesiButton = new JButton();
        fuku_orikaesiButton.setIcon(new ImageIcon(getClass().getResource("/ppp/fuku_orikaesi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(fuku_orikaesiButton, gbc);
        senbun_b_nyuryokuButton = new JButton();
        senbun_b_nyuryokuButton.setIcon(new ImageIcon(getClass().getResource("/ppp/senbun_b_nyuryoku.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel21.add(senbun_b_nyuryokuButton, gbc);
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel22, gbc);
        selectButton = new JButton();
        selectButton.setBackground(new Color(-16711936));
        selectButton.setText("sel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(selectButton, gbc);
        selectAllButton = new JButton();
        selectAllButton.setBackground(new Color(-16711936));
        selectAllButton.setText("s_al");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(selectAllButton, gbc);
        unselectButton = new JButton();
        unselectButton.setBackground(new Color(-16711936));
        unselectButton.setText("unsel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(unselectButton, gbc);
        unselectAllButton = new JButton();
        unselectAllButton.setBackground(new Color(-16711936));
        unselectAllButton.setText("uns_al");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(unselectAllButton, gbc);
        moveButton = new JButton();
        moveButton.setBackground(new Color(-5579606));
        moveButton.setEnabled(true);
        moveButton.setText("move");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(moveButton, gbc);
        move2p2pButton = new JButton();
        move2p2pButton.setBackground(new Color(-5579606));
        move2p2pButton.setEnabled(true);
        move2p2pButton.setText("mv_4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(move2p2pButton, gbc);
        copyButton = new JButton();
        copyButton.setBackground(new Color(-5579606));
        copyButton.setEnabled(true);
        copyButton.setText("copy");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(copyButton, gbc);
        copy2p2pButton = new JButton();
        copy2p2pButton.setBackground(new Color(-5579606));
        copy2p2pButton.setEnabled(true);
        copy2p2pButton.setText("cp_4p");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(copy2p2pButton, gbc);
        reflectButton = new JButton();
        reflectButton.setBackground(new Color(-5579606));
        reflectButton.setEnabled(true);
        reflectButton.setIcon(new ImageIcon(getClass().getResource("/ppp/kyouei.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(reflectButton, gbc);
        deleteSelectedLineSegmentButton = new JButton();
        deleteSelectedLineSegmentButton.setText("d_s_L");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel22.add(deleteSelectedLineSegmentButton, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer5, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private void createUIComponents() {
        panel1 = this;
    }
}
