package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.factory.RegexHighlightFactory;
import oriedita.editor.handler.FoldedFigureOperationMode;
import oriedita.editor.handler.PopupMenuAdapter;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.TaskService;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.FoldedFigureResize;
import oriedita.editor.swing.component.FoldedFigureRotate;
import oriedita.editor.swing.component.UndoRedo;
import oriedita.editor.tools.StringOp;
import origami.folding.FoldedFigure;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

@ApplicationScoped
public class BottomPanel {
    private final ButtonService buttonService;
    private final MeasuresModel measuresModel;
    @SuppressWarnings("unused")
    private final CanvasModel canvasModel;
    private final ApplicationModel applicationModel;
    private final FoldedFigureModel foldedFigureModel;
    private final FoldedFiguresList foldedFiguresList;
    private final TaskService taskService;
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
    private JButton constraintButton;
    private final AnimationService animationService;

    @Inject
    public BottomPanel(
            ButtonService buttonService,
            MeasuresModel measuresModel,
            CanvasModel canvasModel,
            FoldedFigureModel foldedFigureModel,
            ApplicationModel applicationModel,
            FoldedFiguresList foldedFiguresList,
            TaskService taskService,
            AnimationService animationService) {
        this.buttonService = buttonService;
        this.measuresModel = measuresModel;
        this.canvasModel = canvasModel;
        this.applicationModel = applicationModel;
        this.foldedFigureModel = foldedFigureModel;
        this.animationService = animationService;
        this.foldedFiguresList = foldedFiguresList;
        this.taskService = taskService;


        foldedFigureModel.addPropertyChangeListener(e -> setData(foldedFigureModel));
        canvasModel.addPropertyChangeListener(e -> setData(e, canvasModel));

        $$$setupUI$$$();
    }

    public void init() {
        foldedFigureResize.init();
        foldedFigureRotate.init();

        buttonService.addDefaultListener($$$getRootComponent$$$());

        buttonService.registerButton(foldedFigureAntiAliasButton, "foldedFigureToggleAntiAliasAction");
        buttonService.registerButton(shadowButton, "foldedFigureToggleShadowAction");
        buttonService.registerButton(oriagari_sousa_2Button, "oriagari_sousa_2Action");
        buttonService.registerButton(goToFoldedFigureButton, "goToFoldedFigureAction");
        buttonService.registerButton(foldedFigureMoveButton, "foldedFigureMoveAction");
        buttonService.registerButton(constraintButton, "addColorConstraintAction");

        buttonService.registerButton(undoRedo.getUndoButton(), "foldedFigureUndoAction");
        buttonService.registerButton(undoRedo.getRedoButton(), "foldedFigureRedoAction");

        goToFoldedFigureButton.addActionListener(e -> {
            int foldedCases_old = foldedFigureModel.getFoldedCases();
            int newFoldedCases = StringOp.String2int(goToFoldedFigureTextField.getText(), foldedCases_old);
            if (newFoldedCases < 1) {
                newFoldedCases = 1;
            }

            foldedFigureModel.setFoldedCases(newFoldedCases);

            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure == null) {
                return;
            }

            selectedFigure.getFoldedFigure().estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            if (foldedFigureModel.getFoldedCases() < selectedFigure.getFoldedFigure().discovered_fold_cases) {
                selectedFigure.getFoldedFigure().estimationOrder = FoldedFigure.EstimationOrder.ORDER_51;    //i_suitei_meirei=51はoritatami_suiteiの最初の推定図用カメラの設定は素通りするための設定。推定図用カメラの設定を素通りしたら、i_suitei_meirei=5に変更される。
                //1例目の折り上がり予想はi_suitei_meirei=5を指定、2例目以降の折り上がり予想はi_suitei_meirei=6で実施される
            }

            taskService.executeFoldingEstimateSpecificTask();
        });
        goToFoldedFigureTextField.addActionListener(e -> goToFoldedFigureButton.doClick());
        goToFoldedFigureTextField.getDocument().addDocumentListener(RegexHighlightFactory.intRegexAdapter(goToFoldedFigureTextField));
        goToFoldedFigureTextField.addKeyListener(new InputEnterKeyAdapter(goToFoldedFigureTextField));

        undoRedo.addUndoActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null) {
                selectedFigure.undo();
            }
        });
        undoRedo.addRedoActionListener(e -> {
            FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();

            if (selectedFigure != null) {
                selectedFigure.redo();
            }
        });

        foldedFigureBox.setModel(foldedFiguresList);
        foldedFigureBox.setRenderer(new IndexCellRenderer());
        foldedFigureBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!applicationModel.getDisplayNumbers()) {
                    applicationModel.setDisplayNumbers(true);
                }
            }
        });
        foldedFigureBox.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (applicationModel.getDisplayNumbers()) {
                    applicationModel.setDisplayNumbers(false);
                }
            }
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
        panel1.setLayout(new GridLayoutManager(1, 29, new Insets(1, 1, 1, 1), 1, 1));
        foldButton = new JButton();
        foldButton.setActionCommand("foldAction");
        foldButton.setIcon(new ImageIcon(getClass().getResource("/ppp/suitei_04.png")));
        foldButton.setText("Fold");
        panel1.add(foldButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        undoRedo = new UndoRedo();
        panel1.add(undoRedo.$$$getRootComponent$$$(), new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        oriagari_sousaButton = new JButton();
        oriagari_sousaButton.setActionCommand("oriagari_sousaAction");
        oriagari_sousaButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_sousa.png")));
        panel1.add(oriagari_sousaButton, new GridConstraints(0, 11, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        foldedFigureMoveButton = new JButton();
        foldedFigureMoveButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_idiu.png")));
        panel1.add(foldedFigureMoveButton, new GridConstraints(0, 13, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(foldedFigureRotate.$$$getRootComponent$$$(), new GridConstraints(0, 15, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(foldedFigureResize.$$$getRootComponent$$$(), new GridConstraints(0, 17, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        foldedFigureAntiAliasButton = new JButton();
        foldedFigureAntiAliasButton.setActionCommand("foldedFigureAntiAliasAction");
        foldedFigureAntiAliasButton.setText("a_a");
        panel1.add(foldedFigureAntiAliasButton, new GridConstraints(0, 19, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        shadowButton = new JButton();
        shadowButton.setActionCommand("shadowAction");
        shadowButton.setText("S");
        panel1.add(shadowButton, new GridConstraints(0, 20, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        frontColorButton = new JButton();
        frontColorButton.setActionCommand("frontColorAction");
        frontColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/F_color.png")));
        frontColorButton.setText("FC");
        panel1.add(frontColorButton, new GridConstraints(0, 21, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backColorButton = new JButton();
        backColorButton.setActionCommand("backColorAction");
        backColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/B_color.png")));
        backColorButton.setText("BC");
        panel1.add(backColorButton, new GridConstraints(0, 22, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lineColorButton = new JButton();
        lineColorButton.setActionCommand("lineColorAction");
        lineColorButton.setIcon(new ImageIcon(getClass().getResource("/ppp/L_color.png")));
        lineColorButton.setText("LC");
        panel1.add(lineColorButton, new GridConstraints(0, 23, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        haltButton = new JButton();
        haltButton.setActionCommand("haltAction");
        haltButton.setIcon(new ImageIcon(getClass().getResource("/ppp/keisan_tyuusi.png")));
        haltButton.setMargin(new Insets(0, 10, 0, 10));
        panel1.add(haltButton, new GridConstraints(0, 25, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        trashButton = new JButton();
        trashButton.setActionCommand("foldedFigureTrashAction");
        trashButton.setIcon(new ImageIcon(getClass().getResource("/ppp/settei_syokika.png")));
        trashButton.setMargin(new Insets(0, 10, 0, 10));
        panel1.add(trashButton, new GridConstraints(0, 26, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        resetButton = new JButton();
        resetButton.setActionCommand("resetAction");
        resetButton.setIcon(new ImageIcon(getClass().getResource("/ppp/zen_syokika.png")));
        resetButton.setMargin(new Insets(0, 10, 0, 10));
        panel1.add(resetButton, new GridConstraints(0, 27, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(foldedFigureBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        oriagari_sousa_2Button = new JButton();
        oriagari_sousa_2Button.setActionCommand("oriagari_sousa_2Action");
        oriagari_sousa_2Button.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_sousa_2.png")));
        panel1.add(oriagari_sousa_2Button, new GridConstraints(0, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        anotherSolutionButton = new JButton();
        anotherSolutionButton.setActionCommand("anotherSolutionAction");
        anotherSolutionButton.setText("a_s");
        panel1.add(anotherSolutionButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        As100Button = new JButton();
        As100Button.setActionCommand("as100Action");
        As100Button.setText("AS100");
        panel1.add(As100Button, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        goToFoldedFigureTextField = new JTextField();
        goToFoldedFigureTextField.setColumns(2);
        panel1.add(goToFoldedFigureTextField, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        goToFoldedFigureButton = new JButton();
        goToFoldedFigureButton.setActionCommand("goToFoldedFigureAction");
        goToFoldedFigureButton.setText("Go");
        panel1.add(goToFoldedFigureButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        flipButton = new JButton();
        flipButton.setActionCommand("foldedFigureFlipAction");
        flipButton.setIcon(new ImageIcon(getClass().getResource("/ppp/Button0b.png")));
        panel1.add(flipButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 28, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 16, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 14, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel1.add(spacer6, new GridConstraints(0, 18, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel1.add(spacer7, new GridConstraints(0, 24, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), null, null, 0, false));
        constraintButton = new JButton();
        constraintButton.setActionCommand("constraintAction");
        constraintButton.setText("C");
        constraintButton.setToolTipText("Add Constraints");
        panel1.add(constraintButton, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private void createUIComponents() {
        panel1 = new JPanel();
        foldedFigureResize = new FoldedFigureResize(buttonService, foldedFigureModel, measuresModel, animationService);
        foldedFigureRotate = new FoldedFigureRotate(buttonService, foldedFigureModel, measuresModel);
        foldedFigureBox = new JComboBox<>();
    }

    public void setData(PropertyChangeEvent e, CanvasModel data) {
        if (e.getPropertyName() == null || e.getPropertyName().equals("mouseMode") || e.getPropertyName().equals("i_foldedFigure_operation_mode")) {
            MouseMode m = data.getMouseMode();

            foldedFigureMoveButton.setSelected(m == MouseMode.MOVE_CALCULATED_SHAPE_102);
            oriagari_sousaButton.setSelected(data.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_1 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
            oriagari_sousa_2Button.setSelected(data.getFoldedFigureOperationMode() == FoldedFigureOperationMode.MODE_2 && m == MouseMode.MODIFY_CALCULATED_SHAPE_101);
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
        foldedFigureModel.setScale(measuresModel.string2double(foldedFigureResize.getText(), foldedFigureModel.getScale()));
        foldedFigureModel.setRotation(measuresModel.string2double(foldedFigureRotate.getText(), foldedFigureModel.getRotation()));
        foldedFigureModel.setFoldedCases(StringOp.String2int(goToFoldedFigureTextField.getText(), foldedFigureModel.getFoldedCases()));
    }

    private static class IndexCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
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
