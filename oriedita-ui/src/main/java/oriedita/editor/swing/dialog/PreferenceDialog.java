package oriedita.editor.swing.dialog;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.service.LookAndFeelService;
import oriedita.editor.swing.component.ColorIcon;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Dictionary;
import java.util.Hashtable;

public class PreferenceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel bottomPanel;
    private JPanel topPanel;
    private JCheckBox spotlightCB;
    private JCheckBox offsetCB;
    private JCheckBox inputAssistCB;
    private JCheckBox commentCB;
    private JCheckBox cpLinesCB;
    private JCheckBox auxLinesCB;
    private JCheckBox liveAuxCB;
    private JCheckBox markingsCB;
    private JCheckBox cpOnTopCB;
    private JCheckBox foldingProgressCB;
    private JCheckBox selfIntersectionCB;
    private JCheckBox topPanelCB;
    private JCheckBox bottomPanelCB;
    private JCheckBox leftPanelCB;
    private JCheckBox rightPanelCB;
    private JCheckBox preciseZoomCB;
    private JCheckBox antiAliasCB;
    private JCheckBox mousewheelMovesCPCB;
    private JTextField lineWidthTF;
    private JTextField auxLineTF;
    private JTextField pointSizeTF;
    private JCheckBox displayNumbersCB;
    private JCheckBox foldWarningCB;
    private JPanel displayPanel;
    private JPanel panelsPanel;
    private JPanel behaviorPanel;
    private JPanel firstColumn;
    private JPanel secondColumn;
    private JCheckBox darkModeCheckBox;
    private JCheckBox toggleHelpCB;
    private JPanel appearance2Panel;
    private JPanel appearancePanel;
    private JComboBox<String> lineStyleDropBox;
    private JButton lineWidthPlus;
    private JButton auxLinePlus;
    private JButton pointSizePlus;
    private JButton lineWidthMinus;
    private JButton auxLineMinus;
    private JButton pointSizeMinus;
    private JButton gridColorButton;
    private JButton gridScaleColorButton;
    private JButton gridWidthMinus;
    private JButton gridWidthPlus;
    private JTextField gridWidthTF;
    private JButton restoreDefaultsButton;
    private JCheckBox foldAntiAliasCheckBox;
    private JButton ck4Minus;
    private JButton ck4Plus;
    private JTextField ck4TF;
    private JPanel appearance1Panel;
    private JPanel behavior1Panel;
    private JPanel behavior2Panel;
    private JPanel display1Panel;
    private JPanel display2Panel;
    private JPanel panels1Panel;
    private JPanel panels2Panel;
    private JLabel ck4Label;
    private JSlider zoomSpeedSlider;
    private JCheckBox checkBoxAnimation;
    private int tempTransparency;
    private final ApplicationModel applicationModel;
    private final ApplicationModel tempModel;
    private final FoldedFigureModel foldedFigureModel;
    private final FoldedFigureModel tempfoldedModel;

    public void setData(ApplicationModel applicationModel) {
        spotlightCB.setSelected(applicationModel.getDisplayPointSpotlight());
        offsetCB.setSelected(applicationModel.getDisplayPointOffset());
        inputAssistCB.setSelected(applicationModel.getDisplayGridInputAssist());
        commentCB.setSelected(applicationModel.getDisplayComments());
        cpLinesCB.setSelected(applicationModel.getDisplayCpLines());
        auxLinesCB.setSelected(applicationModel.getDisplayAuxLines());
        liveAuxCB.setSelected(applicationModel.getDisplayLiveAuxLines());
        markingsCB.setSelected(applicationModel.getDisplayMarkings());
        cpOnTopCB.setSelected(applicationModel.getDisplayCreasePatternOnTop());
        foldingProgressCB.setSelected(applicationModel.getDisplayFoldingProgress());
        foldWarningCB.setSelected(applicationModel.getFoldWarning());
        toggleHelpCB.setSelected(applicationModel.getHelpVisible());
        darkModeCheckBox.setSelected(applicationModel.getLaf().equals(FlatDarkLaf.class.getName()));
        preciseZoomCB.setSelected(applicationModel.isPreciseZoom());
        mousewheelMovesCPCB.setSelected(applicationModel.getMouseWheelMovesCreasePattern());
        selfIntersectionCB.setSelected(applicationModel.getDisplaySelfIntersection());
        antiAliasCB.setSelected(applicationModel.getAntiAlias());
        foldAntiAliasCheckBox.setSelected(foldedFigureModel.getAntiAlias());
        displayNumbersCB.setSelected(applicationModel.getDisplayNumbers());
        ck4TF.setText(Integer.toString((applicationModel.getCheck4ColorTransparency() / 5) * 2));
        lineWidthTF.setText(Integer.toString(applicationModel.getLineWidth()));
        auxLineTF.setText(Integer.toString(applicationModel.getAuxLineWidth()));
        pointSizeTF.setText(Integer.toString(applicationModel.getPointSize()));
        gridWidthTF.setText(Integer.toString(applicationModel.getGridLineWidth()));
        gridColorButton.setIcon(new ColorIcon(applicationModel.getGridColor()));
        gridScaleColorButton.setIcon(new ColorIcon(applicationModel.getGridScaleColor()));
        lineStyleDropBox.setSelectedIndex(applicationModel.getLineStyle().getType() - 1);
        topPanelCB.setSelected(applicationModel.getDisplayTopPanel());
        bottomPanelCB.setSelected(applicationModel.getDisplayBottomPanel());
        leftPanelCB.setSelected(applicationModel.getDisplayLeftPanel());
        rightPanelCB.setSelected(applicationModel.getDisplayRightPanel());
        zoomSpeedSlider.setValue((int) (applicationModel.getZoomSpeed() * 10));
        checkBoxAnimation.setSelected(applicationModel.getAnimations());
    }

    public PreferenceDialog(
            ApplicationModel appModel,
            LookAndFeelService lookAndFeelService,
            FrameProvider frameProvider,
            FoldedFigureModel foldedFigureModel,
            String name,
            Frame owner
    ) {
        super(owner, name);
        this.applicationModel = appModel;
        this.tempModel = new ApplicationModel();
        $$$setupUI$$$();
        this.tempModel.set(appModel);
        this.foldedFigureModel = foldedFigureModel;
        this.tempfoldedModel = new FoldedFigureModel();
        this.tempfoldedModel.set(foldedFigureModel);
        setData(applicationModel);
        setContentPane($$$getRootComponent$$$());
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getRootPane().setDefaultButton(buttonOK);

        if (applicationModel.getCheck4ColorTransparency() >= 250) {
            ck4Plus.setEnabled(false);
        }
        if (applicationModel.getCheck4ColorTransparency() <= 50) {
            ck4Minus.setEnabled(false);
        }
        if (applicationModel.getLineWidth() <= 0) {
            lineWidthMinus.setEnabled(false);
        }
        if (applicationModel.getAuxLineWidth() <= 0) {
            auxLineMinus.setEnabled(false);
        }
        if (applicationModel.getPointSize() <= 0) {
            pointSizeMinus.setEnabled(false);
        }
        if (applicationModel.getGridLineWidth() <= 1) {
            gridWidthMinus.setEnabled(false);
        }

        spotlightCB.addActionListener(e -> applicationModel.setDisplayPointSpotlight(spotlightCB.isSelected()));
        offsetCB.addActionListener(e -> applicationModel.setDisplayPointOffset(offsetCB.isSelected()));
        inputAssistCB.addActionListener(e -> applicationModel.setDisplayGridInputAssist(inputAssistCB.isSelected()));
        commentCB.addActionListener(e -> applicationModel.setDisplayComments(commentCB.isSelected()));
        cpLinesCB.addActionListener(e -> applicationModel.setDisplayCpLines(cpLinesCB.isSelected()));
        auxLinesCB.addActionListener(e -> applicationModel.setDisplayAuxLines(auxLinesCB.isSelected()));
        liveAuxCB.addActionListener(e -> applicationModel.setDisplayLiveAuxLines(liveAuxCB.isSelected()));
        markingsCB.addActionListener(e -> applicationModel.setDisplayMarkings(markingsCB.isSelected()));
        cpOnTopCB.addActionListener(e -> applicationModel.setDisplayCreasePatternOnTop(cpOnTopCB.isSelected()));
        foldingProgressCB.addActionListener(e -> applicationModel.setDisplayFoldingProgress(foldingProgressCB.isSelected()));
        selfIntersectionCB.addActionListener(e -> applicationModel.setDisplaySelfIntersection(selfIntersectionCB.isSelected()));
        foldWarningCB.addActionListener(e -> applicationModel.setFoldWarning(foldWarningCB.isSelected()));
        toggleHelpCB.addActionListener(e -> applicationModel.setHelpVisible(toggleHelpCB.isSelected()));
        preciseZoomCB.addActionListener(e -> applicationModel.setPreciseZoom(preciseZoomCB.isSelected()));
        zoomSpeedSlider.addChangeListener(e -> applicationModel.setZoomSpeed(zoomSpeedSlider.getValue() / 10.0));
        mousewheelMovesCPCB.addActionListener(e -> applicationModel.setMouseWheelMovesCreasePattern(mousewheelMovesCPCB.isSelected()));
        darkModeCheckBox.addActionListener(e -> {
            lookAndFeelService.toggleDarkMode();

            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(frameProvider.get(), "Restore custom colors in grid and folded figure for this color scheme?", "Restore colors", JOptionPane.YES_NO_OPTION)) {
                if (FlatLaf.isLafDark()) {
                    applicationModel.setGridColor(Colors.GRID_LINE_DARK);
                    applicationModel.setGridScaleColor(Colors.GRID_SCALE_DARK);

                    foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT_DARK);
                    foldedFigureModel.setBackColor(Colors.FIGURE_BACK_DARK);
                } else {
                    applicationModel.setGridColor(Colors.GRID_LINE);
                    applicationModel.setGridScaleColor(Colors.GRID_SCALE);

                    foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT);
                    foldedFigureModel.setBackColor(Colors.FIGURE_BACK);
                }
            }
        });
        antiAliasCB.addActionListener(e -> applicationModel.setAntiAlias(antiAliasCB.isSelected()));
        foldAntiAliasCheckBox.addActionListener(e -> foldedFigureModel.setAntiAlias(foldAntiAliasCheckBox.isSelected()));
        displayNumbersCB.addActionListener(e -> applicationModel.setDisplayNumbers(displayNumbersCB.isSelected()));
        ck4Plus.addActionListener(e -> {
            tempTransparency = (applicationModel.getCheck4ColorTransparency() / 5) * 2;
            if (tempTransparency <= 100) {
                applicationModel.setCheck4ColorTransparency((tempTransparency / 2) * 5 + 10);
                tempTransparency += 4;
                if (tempTransparency >= 100) {
                    ck4Plus.setEnabled(false);
                }
                ck4TF.setText(Integer.toString(tempTransparency));
                ck4Minus.setEnabled(true);
            }
        });
        ck4Minus.addActionListener(e -> {
            tempTransparency = (applicationModel.getCheck4ColorTransparency() / 5) * 2;
            if (tempTransparency > 20) {
                applicationModel.setCheck4ColorTransparency((tempTransparency / 2) * 5 - 10);
                tempTransparency -= 4;
                if (tempTransparency <= 20) {
                    ck4Minus.setEnabled(false);
                }
                ck4TF.setText(Integer.toString(tempTransparency));
                ck4Plus.setEnabled(true);
            }
        });
        lineWidthPlus.addActionListener(e -> {
            applicationModel.setLineWidth(Integer.parseInt(lineWidthTF.getText()) + 1);
            lineWidthTF.setText(Integer.toString(applicationModel.getLineWidth()));
            lineWidthMinus.setEnabled(true);
        });
        lineWidthMinus.addActionListener(e -> {
            if (applicationModel.getLineWidth() > 0) {
                applicationModel.setLineWidth(Integer.parseInt(lineWidthTF.getText()) - 1);
                if (applicationModel.getLineWidth() <= 0) {
                    lineWidthMinus.setEnabled(false);
                }
                lineWidthTF.setText(Integer.toString(applicationModel.getLineWidth()));
            }
        });
        auxLinePlus.addActionListener(e -> {
            applicationModel.setAuxLineWidth(Integer.parseInt(auxLineTF.getText()) + 1);
            auxLineTF.setText(Integer.toString(applicationModel.getAuxLineWidth()));
            auxLineMinus.setEnabled(true);
        });
        auxLineMinus.addActionListener(e -> {
            if (applicationModel.getAuxLineWidth() > 0) {
                applicationModel.setAuxLineWidth(Integer.parseInt(auxLineTF.getText()) - 1);
                if (applicationModel.getAuxLineWidth() <= 0) {
                    auxLineMinus.setEnabled(false);
                }
                auxLineTF.setText(Integer.toString(applicationModel.getAuxLineWidth()));
            }
        });
        pointSizePlus.addActionListener(e -> {
            applicationModel.setPointSize(Integer.parseInt(pointSizeTF.getText()) + 1);
            pointSizeTF.setText(Integer.toString(applicationModel.getPointSize()));
            pointSizeMinus.setEnabled(true);
        });
        pointSizeMinus.addActionListener(e -> {
            if (applicationModel.getPointSize() > 0) {
                applicationModel.setPointSize(Integer.parseInt(pointSizeTF.getText()) - 1);
                if (applicationModel.getPointSize() <= 0) {
                    pointSizeMinus.setEnabled(false);
                }
                pointSizeTF.setText(Integer.toString(applicationModel.getPointSize()));
            }
        });
        gridWidthPlus.addActionListener(e -> {
            applicationModel.setGridLineWidth(Integer.parseInt(gridWidthTF.getText()) + 1);
            gridWidthTF.setText(Integer.toString(applicationModel.getGridLineWidth()));
            gridWidthMinus.setEnabled(true);
        });
        gridWidthMinus.addActionListener(e -> {
            if (applicationModel.getGridLineWidth() > 0) {
                applicationModel.setGridLineWidth(Integer.parseInt(gridWidthTF.getText()) - 1);
                if (applicationModel.getGridLineWidth() <= 1) {
                    gridWidthMinus.setEnabled(false);
                }
                gridWidthTF.setText(Integer.toString(applicationModel.getGridLineWidth()));
            }
        });
        gridColorButton.addActionListener(e -> {
            Color gridColor = JColorChooser.showDialog(frameProvider.get(), "F_col", Color.white);

            if (gridColor != null) {
                applicationModel.setGridColor(gridColor);
                gridColorButton.setIcon(new ColorIcon(applicationModel.getGridColor()));
            }
        });
        gridScaleColorButton.addActionListener(e -> {
            Color gridScaleColor = JColorChooser.showDialog(frameProvider.get(), "F_col", Color.white);

            if (gridScaleColor != null) {
                applicationModel.setGridScaleColor(gridScaleColor);
                gridScaleColorButton.setIcon(new ColorIcon(applicationModel.getGridScaleColor()));
            }
        });
        lineStyleDropBox.addActionListener(e -> {
            applicationModel.setLineStyle(LineStyle.from(lineStyleDropBox.getSelectedIndex() + 1));
            lineStyleDropBox.setSelectedIndex(applicationModel.getLineStyle().getType() - 1);
        });
        topPanelCB.addActionListener(e -> applicationModel.setDisplayTopPanel(topPanelCB.isSelected()));
        bottomPanelCB.addActionListener(e -> applicationModel.setDisplayBottomPanel(bottomPanelCB.isSelected()));
        leftPanelCB.addActionListener(e -> applicationModel.setDisplayLeftPanel(leftPanelCB.isSelected()));
        rightPanelCB.addActionListener(e -> applicationModel.setDisplayRightPanel(rightPanelCB.isSelected()));
        checkBoxAnimation.addActionListener(e -> applicationModel.setAnimations(checkBoxAnimation.isSelected()));

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        restoreDefaultsButton.addActionListener(e -> onReset());

        // call onCancel() when cross is clicked
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (!applicationModel.isSame(tempModel) || !foldedFigureModel.isSame(tempfoldedModel)) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to confirm changes?", "Confirm changes", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void onCancel() {
        if (!applicationModel.isSame(tempModel) || !foldedFigureModel.isSame(tempfoldedModel)) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel all changes?", "Cancel changes", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                setData(tempModel);
                applicationModel.set(tempModel);
                foldedFigureModel.set(tempfoldedModel);
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void onReset() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to restore all the defaults?", "Restore defaults", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            applicationModel.restorePrefDefaults();
            foldedFigureModel.restorePrefDefaults();
            dispose();
        }
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
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setFocusTraversalPolicyProvider(false);
        contentPane.setMinimumSize(new Dimension(450, 600));
        contentPane.setPreferredSize(new Dimension(450, 550));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(bottomPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        bottomPanel.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        bottomPanel.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel1.add(buttonOK, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel1.add(buttonCancel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        restoreDefaultsButton = new JButton();
        restoreDefaultsButton.setText("Restore defaults");
        panel1.add(restoreDefaultsButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(topPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        firstColumn = new JPanel();
        firstColumn.setLayout(new GridLayoutManager(3, 1, new Insets(10, 0, 0, 0), 1, 1));
        topPanel.add(firstColumn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 20, 0), -1, -1));
        displayPanel.setEnabled(true);
        displayPanel.setForeground(new Color(-4473925));
        displayPanel.setOpaque(true);
        firstColumn.add(displayPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        display1Panel = new JPanel();
        display1Panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 5, 0), -1, -1));
        displayPanel.add(display1Panel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("DISPLAY\n");
        display1Panel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(169, 16), null, 0, false));
        display2Panel = new JPanel();
        display2Panel.setLayout(new GridLayoutManager(13, 1, new Insets(0, 0, 0, 0), 1, 2));
        displayPanel.add(display2Panel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spotlightCB = new JCheckBox();
        spotlightCB.setText("Point spotlight");
        display2Panel.add(spotlightCB, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        offsetCB = new JCheckBox();
        offsetCB.setText("Point offset");
        display2Panel.add(offsetCB, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inputAssistCB = new JCheckBox();
        inputAssistCB.setText("Grid input assist");
        display2Panel.add(inputAssistCB, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commentCB = new JCheckBox();
        commentCB.setText("Comment");
        display2Panel.add(commentCB, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cpLinesCB = new JCheckBox();
        cpLinesCB.setText("CP lines");
        display2Panel.add(cpLinesCB, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        auxLinesCB = new JCheckBox();
        auxLinesCB.setText("Aux lines");
        display2Panel.add(auxLinesCB, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        liveAuxCB = new JCheckBox();
        liveAuxCB.setText("Live aux lines");
        display2Panel.add(liveAuxCB, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        markingsCB = new JCheckBox();
        markingsCB.setText("Markings");
        display2Panel.add(markingsCB, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cpOnTopCB = new JCheckBox();
        cpOnTopCB.setText("CP on top");
        display2Panel.add(cpOnTopCB, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foldingProgressCB = new JCheckBox();
        foldingProgressCB.setText("Folding progress");
        display2Panel.add(foldingProgressCB, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selfIntersectionCB = new JCheckBox();
        selfIntersectionCB.setText("Self intersection");
        display2Panel.add(selfIntersectionCB, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foldWarningCB = new JCheckBox();
        foldWarningCB.setText("Fold warning");
        display2Panel.add(foldWarningCB, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toggleHelpCB = new JCheckBox();
        toggleHelpCB.setText("Help dialog");
        display2Panel.add(toggleHelpCB, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        firstColumn.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelsPanel = new JPanel();
        panelsPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 20, 0), 1, 1));
        firstColumn.add(panelsPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panels1Panel = new JPanel();
        panels1Panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 5, 0), -1, -1));
        panelsPanel.add(panels1Panel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("PANELS");
        panels1Panel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panels2Panel = new JPanel();
        panels2Panel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), 1, 2));
        panelsPanel.add(panels2Panel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topPanelCB = new JCheckBox();
        topPanelCB.setText("Top panel");
        panels2Panel.add(topPanelCB, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomPanelCB = new JCheckBox();
        bottomPanelCB.setText("Bottom panel");
        panels2Panel.add(bottomPanelCB, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        leftPanelCB = new JCheckBox();
        leftPanelCB.setText("Left panel");
        panels2Panel.add(leftPanelCB, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rightPanelCB = new JCheckBox();
        rightPanelCB.setText("Right panel");
        panels2Panel.add(rightPanelCB, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        secondColumn = new JPanel();
        secondColumn.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 0, 0), 1, 1));
        topPanel.add(secondColumn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appearancePanel = new JPanel();
        appearancePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 20, 0), -1, -1));
        secondColumn.add(appearancePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appearance2Panel = new JPanel();
        appearance2Panel.setLayout(new GridLayoutManager(13, 4, new Insets(0, 0, 0, 0), 1, 2));
        appearancePanel.add(appearance2Panel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        final JLabel label3 = new JLabel();
        label3.setText("CP line width: ");
        appearance2Panel.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineWidthTF = new JTextField();
        lineWidthTF.setColumns(1);
        lineWidthTF.setEnabled(false);
        lineWidthTF.setToolTipText("Input an integer");
        appearance2Panel.add(lineWidthTF, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText(" Live aux width: ");
        appearance2Panel.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        auxLineTF = new JTextField();
        auxLineTF.setColumns(1);
        auxLineTF.setEnabled(false);
        auxLineTF.setText("");
        auxLineTF.setToolTipText("Input an integer");
        appearance2Panel.add(auxLineTF, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText(" Point radius: ");
        appearance2Panel.add(label5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pointSizeTF = new JTextField();
        pointSizeTF.setColumns(1);
        pointSizeTF.setEnabled(false);
        pointSizeTF.setToolTipText("Input an integer");
        appearance2Panel.add(pointSizeTF, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("MV Line style: ");
        appearance2Panel.add(label6, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineStyleDropBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Color solid");
        defaultComboBoxModel1.addElement("Color dashes");
        defaultComboBoxModel1.addElement("BW - 1 dot/dash");
        defaultComboBoxModel1.addElement("BW - 2 dots/dash");
        lineStyleDropBox.setModel(defaultComboBoxModel1);
        lineStyleDropBox.setToolTipText("Select line style");
        appearance2Panel.add(lineStyleDropBox, new GridConstraints(7, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineWidthMinus = new JButton();
        lineWidthMinus.setText("-");
        appearance2Panel.add(lineWidthMinus, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        auxLinePlus = new JButton();
        auxLinePlus.setText("+");
        appearance2Panel.add(auxLinePlus, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        auxLineMinus = new JButton();
        auxLineMinus.setText("-");
        appearance2Panel.add(auxLineMinus, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineWidthPlus = new JButton();
        lineWidthPlus.setText("+");
        appearance2Panel.add(lineWidthPlus, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pointSizePlus = new JButton();
        pointSizePlus.setText("+");
        appearance2Panel.add(pointSizePlus, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pointSizeMinus = new JButton();
        pointSizeMinus.setText("-");
        appearance2Panel.add(pointSizeMinus, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText(" Main grid color: ");
        appearance2Panel.add(label7, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridColorButton = new JButton();
        gridColorButton.setText("Color");
        appearance2Panel.add(gridColorButton, new GridConstraints(5, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText(" Sub grid color: ");
        appearance2Panel.add(label8, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridScaleColorButton = new JButton();
        gridScaleColorButton.setText("Color");
        appearance2Panel.add(gridScaleColorButton, new GridConstraints(6, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText(" Grid line width: ");
        appearance2Panel.add(label9, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridWidthMinus = new JButton();
        gridWidthMinus.setText("-");
        appearance2Panel.add(gridWidthMinus, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridWidthPlus = new JButton();
        gridWidthPlus.setText("+");
        appearance2Panel.add(gridWidthPlus, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gridWidthTF = new JTextField();
        gridWidthTF.setColumns(1);
        gridWidthTF.setEnabled(false);
        gridWidthTF.setText("");
        gridWidthTF.setToolTipText("Input an integer");
        appearance2Panel.add(gridWidthTF, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ck4Minus = new JButton();
        ck4Minus.setText("-");
        appearance2Panel.add(ck4Minus, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ck4Plus = new JButton();
        ck4Plus.setText("+");
        appearance2Panel.add(ck4Plus, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ck4TF = new JTextField();
        ck4TF.setColumns(1);
        ck4TF.setEnabled(false);
        appearance2Panel.add(ck4TF, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        darkModeCheckBox = new JCheckBox();
        darkModeCheckBox.setText("Dark mode");
        appearance2Panel.add(darkModeCheckBox, new GridConstraints(8, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        antiAliasCB = new JCheckBox();
        antiAliasCB.setText("Anti-alias");
        appearance2Panel.add(antiAliasCB, new GridConstraints(9, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foldAntiAliasCheckBox = new JCheckBox();
        foldAntiAliasCheckBox.setText("Fold anti-alias");
        appearance2Panel.add(foldAntiAliasCheckBox, new GridConstraints(10, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayNumbersCB = new JCheckBox();
        displayNumbersCB.setText("Display numbers");
        appearance2Panel.add(displayNumbersCB, new GridConstraints(11, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ck4Label = new JLabel();
        ck4Label.setText("cAMV Opacity: ");
        ck4Label.setToolTipText("Change cAMV opacity 20-100%");
        appearance2Panel.add(ck4Label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        appearance1Panel = new JPanel();
        appearance1Panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 5, 0), -1, -1));
        appearancePanel.add(appearance1Panel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("APPEARANCE");
        appearance1Panel.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        secondColumn.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        behaviorPanel = new JPanel();
        behaviorPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 20, 0), -1, -1));
        secondColumn.add(behaviorPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        behavior1Panel = new JPanel();
        behavior1Panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 5, 0), -1, -1));
        behaviorPanel.add(behavior1Panel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("BEHAVIOR");
        behavior1Panel.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        behavior2Panel = new JPanel();
        behavior2Panel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), 1, 2));
        behaviorPanel.add(behavior2Panel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        preciseZoomCB = new JCheckBox();
        preciseZoomCB.setText("Precise zoom");
        behavior2Panel.add(preciseZoomCB, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mousewheelMovesCPCB = new JCheckBox();
        mousewheelMovesCPCB.setText("Mousewheel moves CP");
        behavior2Panel.add(mousewheelMovesCPCB, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Zoom Speed: ");
        behavior2Panel.add(label12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zoomSpeedSlider.setMajorTickSpacing(10);
        zoomSpeedSlider.setMaximum(100);
        zoomSpeedSlider.setMinimum(0);
        zoomSpeedSlider.setPaintLabels(true);
        zoomSpeedSlider.setPaintTicks(true);
        zoomSpeedSlider.setPaintTrack(true);
        zoomSpeedSlider.setSnapToTicks(true);
        behavior2Panel.add(zoomSpeedSlider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxAnimation = new JCheckBox();
        checkBoxAnimation.setText("Animations");
        behavior2Panel.add(checkBoxAnimation, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        topPanel.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        zoomSpeedSlider = new JSlider(0, 100);
        zoomSpeedSlider.setMajorTickSpacing(10);
        zoomSpeedSlider.setMinorTickSpacing(5);
        Dictionary<Integer, JLabel> labels = new Hashtable<>();
        for (int i = 0; i <= 10; i++) {
            labels.put(i * 10, new JLabel(String.valueOf(i)));
        }
        zoomSpeedSlider.setLabelTable(labels);
        zoomSpeedSlider.setPaintLabels(true);
    }
}
