package origami_editor.editor;

import origami_editor.editor.component.ColorIcon;
import origami_editor.editor.component.FoldedFigureResize;
import origami_editor.editor.component.FoldedFigureRotate;
import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.tools.StringOp;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

public class BottomPanel extends JPanel {
    private final App app;
    private JButton foldButton;
    private JPanel panel1;
    private JButton anotherSolutionButton;
    private JButton flipButton;
    private JTextField goToFoldedFigureTextField;
    private FoldedFigureRotate foldedFigureRotate;
    private FoldedFigureResize foldedFigureResize;
    private JButton a_aButton;
    private JButton shadowButton;
    private JButton frontColorButton;
    private JButton backColorButton;
    private JButton lineColorButton;
    private JButton haltButton;
    private JButton trashButton;
    private JButton resetButton;
    private JButton oriagari_sousaButton;
    private JButton oriagari_sousa_2Button;
    private JButton As100Button;
    private JButton goToFoldedFigureButton;
    private UndoRedo undoRedo;
    private JButton foldedFigureMove;

    public BottomPanel(App app) {
        this.app = app;
        FoldedFigureModel foldedFigureModel = app.foldedFigureModel;

        $$$setupUI$$$();
        foldButton.addActionListener(e -> {
            app.setHelp("suitei_04");

            System.out.println("20180220 get_i_fold_type() = " + app.getFoldType());
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_5);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (!app.canvasModel.getSelectPersistent()) {
                app.mainDrawingWorker.unselect_all();
            }

            app.Button_shared_operation();
        });
        anotherSolutionButton.addActionListener(e -> {
            app.setHelp("Button3");

            app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            app.subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_0;//1 = Put together another solution for folding estimation. 0 = It is not a mode to put out different solutions of folding estimation at once. This variable is used to change the behavior of subthreads.
            if (!app.subThreadRunning) {
                app.subThreadRunning = true;
                app.makeSubThread();//新しいスレッドを作る
                app.sub.start();
            }
        });
        flipButton.addActionListener(e -> {
            app.setHelp("Button0b");

            foldedFigureModel.advanceState();

            if ((app.mouseMode == MouseMode.MODIFY_CALCULATED_SHAPE_101) && (app.OZ.ip4 == FoldedFigure.State.BOTH_2)) {
                foldedFigureModel.setState(FoldedFigure.State.FRONT_0);
            }//Fold-up forecast map Added to avoid the mode that can not be moved when moving
            app.Button_shared_operation();
        });
        As100Button.addActionListener(e -> {
            app.subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_SAVE_100_1;
            app.setHelp("AS_matome");
            if (app.OZ.findAnotherOverlapValid) {
                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

                if (!app.subThreadRunning) {
                    app.subThreadRunning = true;
                    app.makeSubThread();//新しいスレッドを作る
                    app.sub.start();
                }
            }
        });
        goToFoldedFigureButton.addActionListener(e -> {
            int foldedCases_old = app.foldedCases;
            app.foldedCases = StringOp.String2int(goToFoldedFigureTextField.getText(), foldedCases_old);
            if (app.foldedCases < 1) {
                app.foldedCases = 1;
            }

            app.text26.setText(String.valueOf(app.foldedCases));

            app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            if (app.foldedCases < app.OZ.discovered_fold_cases) {
                app.configure_initialize_prediction();//折り上がり予想の廃棄
                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_51;    //i_suitei_meirei=51はoritatami_suiteiの最初の推定図用カメラの設定は素通りするための設定。推定図用カメラの設定を素通りしたら、i_suitei_meirei=5に変更される。
                //1例目の折り上がり予想はi_suitei_meirei=5を指定、2例目以降の折り上がり予想はi_suitei_meirei=6で実施される
            }

            app.subThreadMode = SubThread.Mode.FOLDING_ESTIMATE_SPECIFIC_2;
            if (!app.subThreadRunning) {
                app.subThreadRunning = true;
                app.makeSubThread();//新しいスレッドを作る
                app.sub.start();
            }

            app.setHelp("bangou_sitei_suitei_hyouji");
            app.Button_shared_operation();
            app.repaintCanvas();
        });

        undoRedo.addUndoActionListener(e -> {
            app.setHelp("undo");

            app.OZ.undo();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        undoRedo.addRedoActionListener(e -> {
            app.setHelp("redo");

            app.OZ.redo();
            app.Button_shared_operation();
            app.repaintCanvas();
        });
        undoRedo.addSetUndoCountActionListener(e -> {
            app.setHelp("undo_syutoku");

            app.foldedFigureModel.setHistoryTotal(StringOp.String2int(undoRedo.getText(), app.foldedFigureModel.getHistoryTotal()));
        });
        oriagari_sousaButton.addActionListener(e -> {
            app.setHelp("oriagari_sousa");
            app.OZ.i_foldedFigure_operation_mode = 1;
            app.OZ.setAllPointStateFalse();
            app.OZ.record();

            app.canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);

            app.Button_shared_operation();
        });
        oriagari_sousa_2Button.addActionListener(e -> {
            app.setHelp("oriagari_sousa_2");
            app.OZ.i_foldedFigure_operation_mode = 2;
            app.OZ.setAllPointStateFalse();
            app.OZ.record();

            app.canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);

            app.Button_shared_operation();
        });
        foldedFigureMove.addActionListener(e -> {
            app.setHelp("oriagari_idiu");

            app.canvasModel.setMouseMode(MouseMode.MOVE_CALCULATED_SHAPE_102);

            app.Button_shared_operation();
        });
        a_aButton.addActionListener(e -> {
            app.Button_shared_operation();
            app.setHelp("a_a");

            foldedFigureModel.toggleAntiAlias();
        });
        shadowButton.addActionListener(e -> {
            app.Button_shared_operation();
            app.setHelp("kage");

            foldedFigureModel.toggleDisplayShadows();
        });
        frontColorButton.addActionListener(e -> {
            app.setHelp("F_color");
            app.Button_shared_operation();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く

            Color frontColor = JColorChooser.showDialog(app, "F_col", Color.white);

            if (frontColor != null) {
                foldedFigureModel.setFrontColor(frontColor);
            }
        });
        backColorButton.addActionListener(e -> {
            app.setHelp("B_color");
            app.Button_shared_operation();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く
            Color backColor = JColorChooser.showDialog(null, "B_col", Color.white);

            if (backColor != null) {
                foldedFigureModel.setBackColor(backColor);
            }
        });
        lineColorButton.addActionListener(e -> {
            app.setHelp("L_color");
            app.Button_shared_operation();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く

            Color lineColor = JColorChooser.showDialog(null, "L_col", Color.white);
            if (lineColor != null) {
                foldedFigureModel.setLineColor(lineColor);
            }
        });
        haltButton.addActionListener(e -> {
            app.setHelp("keisan_tyuusi");

            if (app.subThreadRunning) {
                app.halt();
            }

            app.Button_shared_operation();
        });
        trashButton.addActionListener(e -> {
            app.setHelp("settei_syokika");

            if (app.foldedFigureIndex == 0) {
                return;
            }
            app.OZ = app.temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
            if (app.foldedFigureIndex == app.foldedFigures.size() - 1) {
                app.foldedFigures.remove(app.foldedFigureIndex);
                app.setFoldedFigureIndex(app.foldedFigures.size() - 1);
            }
            if (app.foldedFigureIndex < app.foldedFigures.size() - 1) {
                app.foldedFigures.remove(app.foldedFigureIndex);
                app.setFoldedFigureIndex(app.foldedFigureIndex);
            }

            app.Button_shared_operation();
            app.repaintCanvas();
        });
        resetButton.addActionListener(e -> {
            app.setHelp("zen_syokika");

            //展開図の初期化　開始
            //settei_syokika_cp();//展開図パラメータの初期化
            app.developmentView_initialization();
            //展開図の初期化　終了
            //
            //折畳予測図のの初期化　開始
            app.OZ = app.temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
            app.foldedFigures.clear();
            app.addNewFoldedFigure();
            app.setFoldedFigureIndex(0);
            app.configure_initialize_prediction();
            //折畳予測図のの初期化　終了

            app.Button_shared_operation();
            app.repaintCanvas();

            app.canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);

            app.mainDrawingWorker.record();
            app.mainDrawingWorker.auxRecord();
        });
    }

    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    public JTextField getGoToFoldedFigureTextField() {
        return goToFoldedFigureTextField;
    }

    public JButton getGoToFoldedFigureButton() {
        return goToFoldedFigureButton;
    }

    public JButton getAs100Button() {
        return As100Button;
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
        foldButton = new JButton();
        foldButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_04.png")));
        foldButton.setText("Fold");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldButton, gbc);
        anotherSolutionButton = new JButton();
        anotherSolutionButton.setText("a_s");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(anotherSolutionButton, gbc);
        flipButton = new JButton();
        flipButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Button0b.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(flipButton, gbc);
        As100Button = new JButton();
        As100Button.setText("AS100");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(As100Button, gbc);
        goToFoldedFigureTextField = new JTextField();
        goToFoldedFigureTextField.setColumns(2);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(goToFoldedFigureTextField, gbc);
        goToFoldedFigureButton = new JButton();
        goToFoldedFigureButton.setText("Go");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(goToFoldedFigureButton, gbc);
        undoRedo = new UndoRedo();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(undoRedo.$$$getRootComponent$$$(), gbc);
        oriagari_sousaButton = new JButton();
        oriagari_sousaButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_sousa.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(oriagari_sousaButton, gbc);
        oriagari_sousa_2Button = new JButton();
        oriagari_sousa_2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_sousa_2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(oriagari_sousa_2Button, gbc);
        foldedFigureMove = new JButton();
        foldedFigureMove.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_idiu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureMove, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureRotate.$$$getRootComponent$$$(), gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureResize.$$$getRootComponent$$$(), gbc);
        a_aButton = new JButton();
        a_aButton.setText("a_a");
        gbc = new GridBagConstraints();
        gbc.gridx = 12;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(a_aButton, gbc);
        shadowButton = new JButton();
        shadowButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 13;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(shadowButton, gbc);
        frontColorButton = new JButton();
        frontColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/F_color.png")));
        frontColorButton.setText("FC");
        gbc = new GridBagConstraints();
        gbc.gridx = 14;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(frontColorButton, gbc);
        backColorButton = new JButton();
        backColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/B_color.png")));
        backColorButton.setText("BC");
        gbc = new GridBagConstraints();
        gbc.gridx = 15;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(backColorButton, gbc);
        lineColorButton = new JButton();
        lineColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/L_color.png")));
        lineColorButton.setText("LC");
        gbc = new GridBagConstraints();
        gbc.gridx = 16;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(lineColorButton, gbc);
        haltButton = new JButton();
        haltButton.setIcon(new ImageIcon(getClass().getResource("/ppp/keisan_tyuusi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 17;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(haltButton, gbc);
        trashButton = new JButton();
        trashButton.setIcon(new ImageIcon(getClass().getResource("/ppp/settei_syokika.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 18;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(trashButton, gbc);
        resetButton = new JButton();
        resetButton.setIcon(new ImageIcon(getClass().getResource("/ppp/zen_syokika.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 19;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(resetButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 20;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    public JButton getAnotherSolutionButton() {
        return anotherSolutionButton;
    }

    private void createUIComponents() {
        panel1 = this;
        foldedFigureResize = new FoldedFigureResize(app);
        foldedFigureRotate = new FoldedFigureRotate(app);
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode")) {
            MouseMode m = data.getMouseMode();

            foldedFigureMove.setSelected(m == MouseMode.MOVE_CALCULATED_SHAPE_102);
            oriagari_sousaButton.setSelected(app.OZ.i_foldedFigure_operation_mode == 1 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
            oriagari_sousa_2Button.setSelected(app.OZ.i_foldedFigure_operation_mode == 2 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
        }
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        foldedFigureResize.setText(String.valueOf(foldedFigureModel.getScale()));
        foldedFigureRotate.setText(String.valueOf(foldedFigureModel.getRotation()));

        frontColorButton.setIcon(new ColorIcon(foldedFigureModel.getFrontColor()));
        backColorButton.setIcon(new ColorIcon(foldedFigureModel.getBackColor()));
        lineColorButton.setIcon(new ColorIcon(foldedFigureModel.getLineColor()));

        undoRedo.setText(String.valueOf(foldedFigureModel.getHistoryTotal()));
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigureModel.setScale(app.string2double(foldedFigureResize.getText(), foldedFigureModel.getScale()));
        foldedFigureModel.setRotation(app.string2double(foldedFigureRotate.getText(), foldedFigureModel.getRotation()));
        foldedFigureModel.setHistoryTotal(StringOp.String2int(undoRedo.getText(), foldedFigureModel.getHistoryTotal()));
    }
}
