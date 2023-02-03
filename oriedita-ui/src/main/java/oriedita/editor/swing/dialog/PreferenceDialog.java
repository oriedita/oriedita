package oriedita.editor.swing.dialog;

import com.formdev.flatlaf.FlatDarkLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.tools.LookAndFeelUtil;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


@ApplicationScoped
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
    private JCheckBox presizeZoomCB;
    private JCheckBox antiAliasCB;
    private JCheckBox mousewheelMovesCPCB;
    private JTextField lineWidthTF;
    private JTextField auxLineTF;
    private JTextField pointSizeTF;
    private JCheckBox displayNumbersCB;
    private JCheckBox foldWarningCB;
    private JPanel displayPanel;
    private JPanel panelsPanel;
    private JPanel appearance1Panel;
    private JPanel behaviorPanel;
    private JPanel firstColumn;
    private JPanel secondColumn;
    private JCheckBox darkModeCheckBox;
    private JCheckBox toggleHelpCB;
    private JPanel appearance2Panel;
    private JPanel appearancePanel;
    private JComboBox lineStyleDropBox;
    private JButton lineWidthPlus;
    private JButton auxLinePlus;
    private JButton pointSizePlus;
    private JButton lineWidthMinus;
    private JButton auxLineMinus;
    private JButton pointSizeMinus;
    private final ApplicationModel applicationModel;
    private final ApplicationModel tempModel;


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
        presizeZoomCB.setSelected(applicationModel.isPreciseZoom());
        mousewheelMovesCPCB.setSelected(applicationModel.getMouseWheelMovesCreasePattern());
        selfIntersectionCB.setSelected(applicationModel.getDisplaySelfIntersection());
        antiAliasCB.setSelected(applicationModel.getAntiAlias());
        displayNumbersCB.setSelected(applicationModel.getDisplayNumbers());
        lineWidthTF.setText(Integer.toString(applicationModel.getLineWidth()));
        auxLineTF.setText(Integer.toString(applicationModel.getAuxLineWidth()));
        pointSizeTF.setText(Integer.toString(applicationModel.getPointSize()));
        topPanelCB.setSelected(applicationModel.getDisplayTopPanel());
        bottomPanelCB.setSelected(applicationModel.getDisplayBottomPanel());
        leftPanelCB.setSelected(applicationModel.getDisplayLeftPanel());
        rightPanelCB.setSelected(applicationModel.getDisplayRightPanel());
    }

    @Inject
    public PreferenceDialog(ApplicationModel appModel) {
        this.applicationModel = appModel;
        this.tempModel = new ApplicationModel();
        this.tempModel.set(appModel);
        setData(applicationModel);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        if (applicationModel.getLineWidth() <= 0) {
            lineWidthMinus.setEnabled(false);
        }
        if (applicationModel.getAuxLineWidth() <= 0) {
            auxLineMinus.setEnabled(false);
        }
        if (applicationModel.getPointSize() <= 0) {
            pointSizeMinus.setEnabled(false);
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
        presizeZoomCB.addActionListener(e -> applicationModel.setPreciseZoom(presizeZoomCB.isSelected()));
        toggleHelpCB.addActionListener(e -> applicationModel.setHelpVisible(toggleHelpCB.isSelected()));
        mousewheelMovesCPCB.addActionListener(e -> applicationModel.setMouseWheelMovesCreasePattern(mousewheelMovesCPCB.isSelected()));
        darkModeCheckBox.addActionListener(e -> applicationModel.setLaf(LookAndFeelUtil.determineLafForDarkMode(darkModeCheckBox.isSelected())));
        antiAliasCB.addActionListener(e -> applicationModel.setAntiAlias(antiAliasCB.isSelected()));
        displayNumbersCB.addActionListener(e -> applicationModel.setDisplayNumbers(displayNumbersCB.isSelected()));
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
        lineStyleDropBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = lineStyleDropBox.getSelectedIndex();
                applicationModel.setLineStyle(LineStyle.from(index + 1));
                lineStyleDropBox.setSelectedIndex(index);
            }
        });
        topPanelCB.addActionListener(e -> applicationModel.setDisplayTopPanel(topPanelCB.isSelected()));
        bottomPanelCB.addActionListener(e -> applicationModel.setDisplayBottomPanel(bottomPanelCB.isSelected()));
        leftPanelCB.addActionListener(e -> applicationModel.setDisplayLeftPanel(leftPanelCB.isSelected()));
        rightPanelCB.addActionListener(e -> applicationModel.setDisplayRightPanel(rightPanelCB.isSelected()));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        setData(tempModel);
        applicationModel.set(tempModel);
        dispose();
    }

//    public static void main(String[] args) {
//        PreferenceDialog dialog = new PreferenceDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMinimumSize(new Dimension(450, 550));
        contentPane.setPreferredSize(new Dimension(450, 550));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(bottomPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        bottomPanel.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        bottomPanel.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel1.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel1.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(topPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        firstColumn = new JPanel();
        firstColumn.setLayout(new GridLayoutManager(3, 1, new Insets(10, 0, 0, 0), 1, 1));
        topPanel.add(firstColumn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayoutManager(14, 1, new Insets(0, 0, 20, 0), -1, -1));
        displayPanel.setEnabled(true);
        displayPanel.setForeground(new Color(-4473925));
        displayPanel.setOpaque(true);
        firstColumn.add(displayPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("DISPLAY\n");
        displayPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(169, 16), null, 0, false));
        spotlightCB = new JCheckBox();
        spotlightCB.setText("Point spotlight");
        displayPanel.add(spotlightCB, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        offsetCB = new JCheckBox();
        offsetCB.setText("Point offset");
        displayPanel.add(offsetCB, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        inputAssistCB = new JCheckBox();
        inputAssistCB.setText("Grid input assist");
        displayPanel.add(inputAssistCB, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        commentCB = new JCheckBox();
        commentCB.setText("Comment");
        displayPanel.add(commentCB, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        cpLinesCB = new JCheckBox();
        cpLinesCB.setText("CP lines");
        displayPanel.add(cpLinesCB, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        auxLinesCB = new JCheckBox();
        auxLinesCB.setText("Aux lines");
        displayPanel.add(auxLinesCB, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        liveAuxCB = new JCheckBox();
        liveAuxCB.setText("Live aux lines");
        displayPanel.add(liveAuxCB, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        markingsCB = new JCheckBox();
        markingsCB.setText("Markings");
        displayPanel.add(markingsCB, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        cpOnTopCB = new JCheckBox();
        cpOnTopCB.setText("CP on top");
        displayPanel.add(cpOnTopCB, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        foldingProgressCB = new JCheckBox();
        foldingProgressCB.setText("Folding progress");
        displayPanel.add(foldingProgressCB, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        selfIntersectionCB = new JCheckBox();
        selfIntersectionCB.setText("Self intersection");
        displayPanel.add(selfIntersectionCB, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(169, 21), null, 0, false));
        foldWarningCB = new JCheckBox();
        foldWarningCB.setText("Fold warning");
        displayPanel.add(foldWarningCB, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toggleHelpCB = new JCheckBox();
        toggleHelpCB.setText("Help");
        displayPanel.add(toggleHelpCB, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        behaviorPanel = new JPanel();
        behaviorPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 20, 0), -1, -1));
        firstColumn.add(behaviorPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("BEHAVIOR");
        behaviorPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mousewheelMovesCPCB = new JCheckBox();
        mousewheelMovesCPCB.setText("Mousewheel moves CP");
        behaviorPanel.add(mousewheelMovesCPCB, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        presizeZoomCB = new JCheckBox();
        presizeZoomCB.setText("Presize zoom");
        behaviorPanel.add(presizeZoomCB, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        firstColumn.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        secondColumn = new JPanel();
        secondColumn.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 0, 0), 1, 1));
        topPanel.add(secondColumn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelsPanel = new JPanel();
        panelsPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 20, 0), 1, 1));
        secondColumn.add(panelsPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("PANELS");
        panelsPanel.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topPanelCB = new JCheckBox();
        topPanelCB.setText("Top panel");
        panelsPanel.add(topPanelCB, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomPanelCB = new JCheckBox();
        bottomPanelCB.setText("Bottom panel");
        panelsPanel.add(bottomPanelCB, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        leftPanelCB = new JCheckBox();
        leftPanelCB.setText("Left panel");
        panelsPanel.add(leftPanelCB, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rightPanelCB = new JCheckBox();
        rightPanelCB.setText("Right panel");
        panelsPanel.add(rightPanelCB, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        appearancePanel = new JPanel();
        appearancePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 20, 0), -1, -1));
        secondColumn.add(appearancePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appearance1Panel = new JPanel();
        appearance1Panel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 7, 0), 1, 1));
        appearancePanel.add(appearance1Panel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("APPEARANCE");
        appearance1Panel.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        antiAliasCB = new JCheckBox();
        antiAliasCB.setText("Anti-alias");
        appearance1Panel.add(antiAliasCB, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayNumbersCB = new JCheckBox();
        displayNumbersCB.setText("Display numbers");
        appearance1Panel.add(displayNumbersCB, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        darkModeCheckBox = new JCheckBox();
        darkModeCheckBox.setText("Dark mode");
        appearance1Panel.add(darkModeCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        appearance2Panel = new JPanel();
        appearance2Panel.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), 1, 1));
        appearancePanel.add(appearance2Panel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText(" Line width: ");
        appearance2Panel.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineWidthTF = new JTextField();
        lineWidthTF.setColumns(1);
        lineWidthTF.setEnabled(false);
        lineWidthTF.setToolTipText("Input an integer");
        appearance2Panel.add(lineWidthTF, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText(" Aux line width: ");
        appearance2Panel.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        auxLineTF = new JTextField();
        auxLineTF.setColumns(1);
        auxLineTF.setEnabled(false);
        auxLineTF.setText("");
        auxLineTF.setToolTipText("Input an integer");
        appearance2Panel.add(auxLineTF, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText(" Point size: ");
        appearance2Panel.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pointSizeTF = new JTextField();
        pointSizeTF.setColumns(1);
        pointSizeTF.setEnabled(false);
        pointSizeTF.setToolTipText("Input an integer");
        appearance2Panel.add(pointSizeTF, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText(" Line style: ");
        appearance2Panel.add(label8, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineStyleDropBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Color solid");
        defaultComboBoxModel1.addElement("Color dashes");
        defaultComboBoxModel1.addElement("BW - 1 dot/dash");
        defaultComboBoxModel1.addElement("BW - 2 dots/dash");
        lineStyleDropBox.setModel(defaultComboBoxModel1);
        lineStyleDropBox.setToolTipText("Select line style");
        appearance2Panel.add(lineStyleDropBox, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineWidthMinus = new JButton();
        lineWidthMinus.setText("-");
        appearance2Panel.add(lineWidthMinus, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        auxLinePlus = new JButton();
        auxLinePlus.setText("+");
        appearance2Panel.add(auxLinePlus, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        auxLineMinus = new JButton();
        auxLineMinus.setText("-");
        appearance2Panel.add(auxLineMinus, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lineWidthPlus = new JButton();
        lineWidthPlus.setText("+");
        appearance2Panel.add(lineWidthPlus, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pointSizePlus = new JButton();
        pointSizePlus.setText("+");
        appearance2Panel.add(pointSizePlus, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pointSizeMinus = new JButton();
        pointSizeMinus.setText("-");
        appearance2Panel.add(pointSizeMinus, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        secondColumn.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        topPanel.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
