package origami_editor.editor;

import origami_editor.editor.canvas.MouseHandlerModifyCalculatedShape;
import origami_editor.editor.component.ColorIcon;
import origami_editor.editor.component.FoldedFigureResize;
import origami_editor.editor.component.FoldedFigureRotate;
import origami_editor.editor.component.UndoRedo;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami.folding.FoldedFigure;
import origami_editor.editor.drawing.FoldedFigure_Drawer;
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
    private JComboBox<FoldedFigure_Drawer> foldedFigureBox;

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
            FoldedFigure_Drawer selectedItem = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();
            if (selectedItem != null) {
                selectedItem.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

                TaskExecutor.executeTask("Folding Estimate", new FoldingEstimateTask(app));
            }
        });
        flipButton.addActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();
            if (selectedFigure != null) {
                foldedFigureModel.advanceState();

                if ((app.canvasModel.getMouseMode() == MouseMode.MODIFY_CALCULATED_SHAPE_101) && (selectedFigure.foldedFigure.ip4 == FoldedFigure.State.BOTH_2)) {
                    foldedFigureModel.setState(FoldedFigure.State.FRONT_0);
                }//Fold-up forecast map Added to avoid the mode that can not be moved when moving
            }
        });
        As100Button.addActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();
            if (selectedFigure != null && selectedFigure.foldedFigure.findAnotherOverlapValid) {
                selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

                TaskExecutor.executeTask("Folding Estimate Save 100", new FoldingEstimateSave100Task(app));
            }
        });
        goToFoldedFigureButton.addActionListener(e -> {
            int foldedCases_old = app.foldedFigureModel.getFoldedCases();
            int newFoldedCases = StringOp.String2int(goToFoldedFigureTextField.getText(), foldedCases_old);
            if (newFoldedCases < 1) {
                newFoldedCases = 1;
            }

            app.foldedFigureModel.setFoldedCases(newFoldedCases);

            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

            if (selectedFigure == null) {
                return;
            }

            selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            if (app.foldedFigureModel.getFoldedCases() < selectedFigure.foldedFigure.discovered_fold_cases) {
                selectedFigure.foldedFigure.estimationOrder = FoldedFigure.EstimationOrder.ORDER_51;    //i_suitei_meirei=51はoritatami_suiteiの最初の推定図用カメラの設定は素通りするための設定。推定図用カメラの設定を素通りしたら、i_suitei_meirei=5に変更される。
                //1例目の折り上がり予想はi_suitei_meirei=5を指定、2例目以降の折り上がり予想はi_suitei_meirei=6で実施される
            }

            TaskExecutor.executeTask("Folding Estimate Specific", new FoldingEstimateSpecificTask(app));

            app.repaintCanvas();
        });

        undoRedo.addUndoActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.undo();
                app.repaintCanvas();
            }
        });
        undoRedo.addRedoActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.redo();
                app.repaintCanvas();
            }
        });
        oriagari_sousaButton.addActionListener(e -> {
            app.canvasModel.setFoldedFigureOperationMode(MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode.MODE_1);
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.foldedFigure.setAllPointStateFalse();
                selectedFigure.record();
            }

            app.canvasModel.setMouseMode(MouseMode.MODIFY_CALCULATED_SHAPE_101);
        });
        oriagari_sousa_2Button.addActionListener(e -> {
            app.canvasModel.setFoldedFigureOperationMode(MouseHandlerModifyCalculatedShape.FoldedFigureOperationMode.MODE_2);
            FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

            if (selectedFigure != null) {
                selectedFigure.foldedFigure.setAllPointStateFalse();
                selectedFigure.record();
            }

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
        haltButton.addActionListener(e -> {
            TaskExecutor.stopTask();
            app.mainCreasePatternWorker.camvTask.cancel(true);
        });
        trashButton.addActionListener(e -> {
            if (app.foldedFiguresList.getSize() == 0) {
                return;
            }

            Object selectedItem = app.foldedFiguresList.getSelectedItem();

            if (selectedItem == null) {
                selectedItem = app.foldedFiguresList.getElementAt(0);
            }

            app.foldedFiguresList.removeElement(selectedItem);

            app.repaintCanvas();
        });
        resetButton.addActionListener(e -> {

            app.mainCreasePatternWorker.clearCreasePattern();
            app.creasePatternCameraModel.reset();
            app.foldedFiguresList.removeAllElements();

            app.Button_shared_operation();
            app.repaintCanvas();

            app.canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);

            app.mainCreasePatternWorker.record();
            app.mainCreasePatternWorker.auxRecord();
        });
        foldedFigureBox.setModel(app.foldedFiguresList);
        foldedFigureBox.setRenderer(new IndexCellRenderer());
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
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(anotherSolutionButton, gbc);
        As100Button = new JButton();
        As100Button.setText("AS100");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
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
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(oriagari_sousaButton, gbc);
        foldedFigureMoveButton = new JButton();
        foldedFigureMoveButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_idiu.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureMoveButton, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 13;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureRotate.$$$getRootComponent$$$(), gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 15;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureResize.$$$getRootComponent$$$(), gbc);
        foldedFigureAntiAliasButton = new JButton();
        foldedFigureAntiAliasButton.setText("a_a");
        gbc = new GridBagConstraints();
        gbc.gridx = 17;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(foldedFigureAntiAliasButton, gbc);
        shadowButton = new JButton();
        shadowButton.setText("S");
        gbc = new GridBagConstraints();
        gbc.gridx = 18;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(shadowButton, gbc);
        frontColorButton = new JButton();
        frontColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/F_color.png")));
        frontColorButton.setText("FC");
        gbc = new GridBagConstraints();
        gbc.gridx = 20;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(frontColorButton, gbc);
        backColorButton = new JButton();
        backColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/B_color.png")));
        backColorButton.setText("BC");
        gbc = new GridBagConstraints();
        gbc.gridx = 21;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(backColorButton, gbc);
        lineColorButton = new JButton();
        lineColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/L_color.png")));
        lineColorButton.setText("LC");
        gbc = new GridBagConstraints();
        gbc.gridx = 22;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(lineColorButton, gbc);
        haltButton = new JButton();
        haltButton.setIcon(new ImageIcon(getClass().getResource("/ppp/keisan_tyuusi.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 23;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(haltButton, gbc);
        trashButton = new JButton();
        trashButton.setIcon(new ImageIcon(getClass().getResource("/ppp/settei_syokika.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 24;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(trashButton, gbc);
        resetButton = new JButton();
        resetButton.setIcon(new ImageIcon(getClass().getResource("/ppp/zen_syokika.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 25;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(resetButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 26;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(foldedFigureBox, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 14;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 12;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 16;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 19;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer6, gbc);
        flipButton = new JButton();
        flipButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Button0b.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(flipButton, gbc);
        oriagari_sousa_2Button = new JButton();
        oriagari_sousa_2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_sousa_2.png")));
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(oriagari_sousa_2Button, gbc);
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
        foldedFigureBox = new JComboBox<>();
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

        boolean findAnotherOverlapValid = foldedFigureModel.isFindAnotherOverlapValid();
        anotherSolutionButton.setEnabled(findAnotherOverlapValid);
        As100Button.setEnabled(findAnotherOverlapValid);
        goToFoldedFigureButton.setEnabled(findAnotherOverlapValid);
    }

    public void getData(FoldedFigureModel foldedFigureModel) {
        foldedFigureModel.setScale(app.string2double(foldedFigureResize.getText(), foldedFigureModel.getScale()));
        foldedFigureModel.setRotation(app.string2double(foldedFigureRotate.getText(), foldedFigureModel.getRotation()));
        foldedFigureModel.setFoldedCases(StringOp.String2int(goToFoldedFigureTextField.getText(), foldedFigureModel.getFoldedCases()));
    }

    private static class IndexCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (index == -1) {
                if (list.getSelectedIndex() == -1) {
                    setText("");
                } else {
                    setText(Integer.toString(list.getSelectedIndex() + 1));
                }
            } else {
                setText(Integer.toString(index + 1));
            }

            return this;
        }
    }
}
