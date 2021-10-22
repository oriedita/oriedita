package origami_editor.editor;

import origami_editor.editor.component.ColorIcon;
import origami_editor.editor.component.FoldedFigureResize;
import origami_editor.editor.component.FoldedFigureRotate;
import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.canvas.MouseHandlerModifyCalculatedShape;
import origami_editor.editor.folded_figure.FoldedFigure;
import origami_editor.editor.task.FoldingEstimateSave100Task;
import origami_editor.editor.task.FoldingEstimateSpecificTask;
import origami_editor.editor.task.FoldingEstimateTask;
import origami_editor.editor.task.TaskExecutor;
import origami_editor.tools.StringOp;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

public class BottomPanel extends JPanel {
    private final App app;
    private JPanel panel1;
    private JTextField goToFoldedFigureTextField;
    private FoldedFigureRotate foldedFigureRotate;
    private FoldedFigureResize foldedFigureResize;
    private JButton foldButton;
    private JButton anotherSolutionButton;
    private JButton flipButton;
    private JButton foldedFigureAntiAliasButton;
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
    private JButton foldedFigureMoveButton;
    private UndoRedo undoRedo;

    public BottomPanel(App app) {
        this.app = app;
        $$$setupUI$$$();

        app.registerButton(foldButton, "foldAction");
        app.registerButton(anotherSolutionButton, "anotherSolutionAction");
        app.registerButton(flipButton, "foldedFigureFlipAction");
        app.registerButton(foldedFigureAntiAliasButton, "foldedFigureToggleAntiAliasAction");
        app.registerButton(shadowButton, "foldedFigureToggleShadowAction");
        app.registerButton(frontColorButton, "foldedFigureFrontColorAction");
        app.registerButton(backColorButton, "foldedFigureBackColorAction");
        app.registerButton(lineColorButton, "foldedFigureLineColorAction");
        app.registerButton(haltButton, "haltAction");
        app.registerButton(trashButton, "foldedFigureTrashAction");
        app.registerButton(resetButton, "resetAction");
        app.registerButton(oriagari_sousaButton, "oriagari_sousaAction");
        app.registerButton(oriagari_sousa_2Button, "oriagari_sousa_2Action");
        app.registerButton(As100Button, "As100Action");
        app.registerButton(goToFoldedFigureButton, "goToFoldedFigureAction");
        app.registerButton(foldedFigureMoveButton, "foldedFigureMoveAction");

        app.registerButton(undoRedo.getUndoButton(), "foldedFigureUndoAction");
        app.registerButton(undoRedo.getRedoButton(), "foldedFigureRedoAction");

        FoldedFigureModel foldedFigureModel = app.foldedFigureModel;

        foldButton.addActionListener(e -> {
            System.out.println("20180220 get_i_fold_type() = " + app.getFoldType());
            app.fold(app.getFoldType(), FoldedFigure.EstimationOrder.ORDER_5);//引数の意味は(i_fold_type , i_suitei_meirei);

            if (!app.applicationModel.getSelectPersistent()) {
                app.mainCreasePatternWorker.unselect_all();
            }
        });
        anotherSolutionButton.addActionListener(e -> {
            app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            TaskExecutor.executeTask(new FoldingEstimateTask(app));
        });
        flipButton.addActionListener(e -> {
            foldedFigureModel.advanceState();

            if ((app.mouseMode == MouseMode.MODIFY_CALCULATED_SHAPE_101) && (app.OZ.ip4 == FoldedFigure.State.BOTH_2)) {
                foldedFigureModel.setState(FoldedFigure.State.FRONT_0);
            }//Fold-up forecast map Added to avoid the mode that can not be moved when moving
        });
        As100Button.addActionListener(e -> {
            if (app.OZ.findAnotherOverlapValid) {
                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

                TaskExecutor.executeTask(new FoldingEstimateSave100Task(app));
            }
        });
        goToFoldedFigureButton.addActionListener(e -> {
            int foldedCases_old = app.foldedFigureModel.getFoldedCases();
            int newFoldedCases = StringOp.String2int(goToFoldedFigureTextField.getText(), foldedCases_old);
            if (newFoldedCases < 1) {
                newFoldedCases = 1;
            }

            app.foldedFigureModel.setFoldedCases(newFoldedCases);

            app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            if (app.foldedFigureModel.getFoldedCases() < app.OZ.discovered_fold_cases) {
                app.configure_initialize_prediction();//折り上がり予想の廃棄
                app.OZ.estimationOrder = FoldedFigure.EstimationOrder.ORDER_51;    //i_suitei_meirei=51はoritatami_suiteiの最初の推定図用カメラの設定は素通りするための設定。推定図用カメラの設定を素通りしたら、i_suitei_meirei=5に変更される。
                //1例目の折り上がり予想はi_suitei_meirei=5を指定、2例目以降の折り上がり予想はi_suitei_meirei=6で実施される
            }

            TaskExecutor.executeTask(new FoldingEstimateSpecificTask(app));

            app.repaintCanvas();
        });

        undoRedo.addUndoActionListener(e -> {
            app.OZ.undo();
            app.repaintCanvas();
        });
        undoRedo.addRedoActionListener(e -> {
            app.OZ.redo();
            app.repaintCanvas();
        });
        oriagari_sousaButton.addActionListener(e -> {
            app.canvasModel.setFoldedFigureOperationMode(MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode.MODE_1);
            app.OZ.setAllPointStateFalse();
            app.OZ.record();

            app.canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);
        });
        oriagari_sousa_2Button.addActionListener(e -> {
            app.canvasModel.setFoldedFigureOperationMode(MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode.MODE_2);
            app.OZ.setAllPointStateFalse();
            app.OZ.record();

            app.canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);
        });
        foldedFigureMoveButton.addActionListener(e -> app.canvasModel.setMouseMode(MouseMode.MOVE_CALCULATED_SHAPE_102));
        foldedFigureAntiAliasButton.addActionListener(e -> foldedFigureModel.toggleAntiAlias());
        shadowButton.addActionListener(e -> foldedFigureModel.toggleDisplayShadows());
        frontColorButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く

            Color frontColor = JColorChooser.showDialog(null, "F_col", Color.white);

            if (frontColor != null) {
                foldedFigureModel.setFrontColor(frontColor);
            }
        });
        backColorButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く
            Color backColor = JColorChooser.showDialog(null, "B_col", Color.white);

            if (backColor != null) {
                foldedFigureModel.setBackColor(backColor);
            }
        });
        lineColorButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            //以下にやりたいことを書く

            Color lineColor = JColorChooser.showDialog(null, "L_col", Color.white);
            if (lineColor != null) {
                foldedFigureModel.setLineColor(lineColor);
            }
        });
        haltButton.addActionListener(e -> TaskExecutor.stopTask());
        trashButton.addActionListener(e -> {
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

            app.repaintCanvas();
        });
        resetButton.addActionListener(e -> {

            app.mainCreasePatternWorker.clearCreasePattern();
            app.creasePatternCameraModel.reset();

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

            app.mainCreasePatternWorker.record();
            app.mainCreasePatternWorker.auxRecord();
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
        foldedFigureMoveButton = new JButton();
        foldedFigureMoveButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_idiu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureMoveButton, gbc);
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
        foldedFigureAntiAliasButton = new JButton();
        foldedFigureAntiAliasButton.setText("a_a");
        gbc = new GridBagConstraints();
        gbc.gridx = 12;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureAntiAliasButton, gbc);
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

    private void createUIComponents() {
        panel1 = this;
        foldedFigureResize = new FoldedFigureResize(app);
        foldedFigureRotate = new FoldedFigureRotate(app);
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode") || e.getPropertyName().equals("i_foldedFigure_operation_mode")) {
            MouseMode m = data.getMouseMode();

            foldedFigureMoveButton.setSelected(m == MouseMode.MOVE_CALCULATED_SHAPE_102);
            oriagari_sousaButton.setSelected(data.getFoldedFigureOperationMode() == MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode.MODE_1 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
            oriagari_sousa_2Button.setSelected(data.getFoldedFigureOperationMode() == MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode.MODE_2 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
        }
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        foldedFigureResize.setText(String.valueOf(foldedFigureModel.getScale()));
        foldedFigureRotate.setText(String.valueOf(foldedFigureModel.getRotation()));

        frontColorButton.setIcon(new ColorIcon(foldedFigureModel.getFrontColor()));
        backColorButton.setIcon(new ColorIcon(foldedFigureModel.getBackColor()));
        lineColorButton.setIcon(new ColorIcon(foldedFigureModel.getLineColor()));

        goToFoldedFigureTextField.setText(String.valueOf(foldedFigureModel.getFoldedCases()));

        if (foldedFigureModel.isFindAnotherOverlapValid()) {
            anotherSolutionButton.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            anotherSolutionButton.setForeground(Color.black);

            As100Button.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            As100Button.setForeground(Color.black);

            goToFoldedFigureButton.setBackground(new Color(200, 200, 200));//これがないとForegroundが直ぐに反映されない。仕様なのか？
            goToFoldedFigureButton.setForeground(Color.black);
        } else {
            anotherSolutionButton.setBackground(new Color(201, 201, 201));
            anotherSolutionButton.setForeground(Color.gray);

            As100Button.setBackground(new Color(201, 201, 201));
            As100Button.setForeground(Color.gray);

            goToFoldedFigureButton.setBackground(new Color(201, 201, 201));
            goToFoldedFigureButton.setForeground(Color.gray);
        }
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigureModel.setScale(app.string2double(foldedFigureResize.getText(), foldedFigureModel.getScale()));
        foldedFigureModel.setRotation(app.string2double(foldedFigureRotate.getText(), foldedFigureModel.getRotation()));
        foldedFigureModel.setFoldedCases(StringOp.String2int(goToFoldedFigureTextField.getText(), foldedFigureModel.getFoldedCases()));
    }
}
