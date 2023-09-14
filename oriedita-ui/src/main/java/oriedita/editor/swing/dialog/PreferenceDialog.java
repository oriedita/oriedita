package oriedita.editor.swing.dialog;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.opencsv.CSVReaderBuilder;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.action.ActionType;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.LookAndFeelService;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.tools.KeyStrokeUtil;
import oriedita.editor.tools.ResourceUtil;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private JCheckBox darkModeCheckBox;
    private JCheckBox toggleHelpCB;
    private JPanel appearance2Panel;
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
    private JPanel behavior2Panel;
    private JPanel display2Panel;
    private JLabel ck4Label;
    private JSlider zoomSpeedSlider;
    private JCheckBox checkBoxAnimation;
    private JSlider animationSpeedSlider;
    private JSlider mouseRangeSlider;
    private JTabbedPane tabbedPane1;
    private JPanel ck4Panel;
    private JPanel CPLinePanel;
    private JPanel liveAuxPanel;
    private JPanel pointRadPanel;
    private JPanel gridLinePanel;
    private JLabel CPLabel;
    private JPanel hotkeyPanel;
    private JButton importButton;
    private JButton exportButton;
    private int tempTransparency;
    private final ApplicationModel applicationModel;
    private final ApplicationModel tempModel;
    private final FoldedFigureModel foldedFigureModel;
    private final FoldedFigureModel tempfoldedModel;
    private Map<String, List<String>> hotkeyCategoryMap;
    private List<String> categoryHeaderList;

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
        foldWarningCB.setSelected(!applicationModel.getFoldWarning());
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
        animationSpeedSlider.setValue((int) ((applicationModel.getAnimationSpeed()) * 8));
        mouseRangeSlider.setValue((int) applicationModel.getMouseRadius());
    }

    public PreferenceDialog(
            ApplicationModel appModel,
            LookAndFeelService lookAndFeelService,
            FrameProvider frameProvider,
            FoldedFigureModel foldedFigureModel,
            String name,
            Frame owner,
            ButtonService buttonService,
            FileSaveService fileSaveService
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

        setupHotKey(buttonService, frameProvider);

        ck4Plus.setEnabled(applicationModel.getCheck4ColorTransparency() < 250);
        ck4Minus.setEnabled(applicationModel.getCheck4ColorTransparency() > 50);
        lineWidthMinus.setEnabled(applicationModel.getLineWidth() > 0);
        auxLineMinus.setEnabled(applicationModel.getAuxLineWidth() > 0);
        pointSizeMinus.setEnabled(applicationModel.getPointSize() > 0);
        gridWidthMinus.setEnabled(applicationModel.getGridLineWidth() > 1);
        animationSpeedSlider.setEnabled(applicationModel.getAnimations());

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
        foldWarningCB.addActionListener(e -> applicationModel.setFoldWarning(!foldWarningCB.isSelected()));
        toggleHelpCB.addActionListener(e -> applicationModel.setHelpVisible(toggleHelpCB.isSelected()));
        preciseZoomCB.addActionListener(e -> applicationModel.setPreciseZoom(preciseZoomCB.isSelected()));
        zoomSpeedSlider.addChangeListener(e -> applicationModel.setZoomSpeed(zoomSpeedSlider.getValue() / 10.0));
        mousewheelMovesCPCB.addActionListener(e -> applicationModel.setMouseWheelMovesCreasePattern(mousewheelMovesCPCB.isSelected()));
        darkModeCheckBox.addActionListener(e -> {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(frameProvider.get(), "Restore custom colors in grid and folded figure for this color scheme?", "Restore colors", JOptionPane.YES_NO_OPTION)) {
                lookAndFeelService.toggleDarkMode();

                EventQueue.invokeLater(() -> {
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
                    gridColorButton.setIcon(new ColorIcon(applicationModel.getGridColor()));
                    gridScaleColorButton.setIcon(new ColorIcon(applicationModel.getGridScaleColor()));
                });
            } else {
                lookAndFeelService.toggleDarkMode();
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
                ck4Plus.setEnabled(tempTransparency < 100);
                ck4TF.setText(Integer.toString(tempTransparency));
                ck4Minus.setEnabled(true);
            }
        });
        ck4Minus.addActionListener(e -> {
            tempTransparency = (applicationModel.getCheck4ColorTransparency() / 5) * 2;
            if (tempTransparency > 20) {
                applicationModel.setCheck4ColorTransparency((tempTransparency / 2) * 5 - 10);
                tempTransparency -= 4;
                ck4Minus.setEnabled(tempTransparency > 20);
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
                lineWidthMinus.setEnabled(applicationModel.getLineWidth() > 0);
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
                auxLineMinus.setEnabled(applicationModel.getAuxLineWidth() > 0);
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
                pointSizeMinus.setEnabled(applicationModel.getPointSize() > 0);
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
                gridWidthMinus.setEnabled(applicationModel.getGridLineWidth() > 1);
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
        animationSpeedSlider.addChangeListener(e -> applicationModel.setAnimationSpeed(animationSpeedSlider.getValue() / 8.0));
        lineStyleDropBox.addActionListener(e -> {
            applicationModel.setLineStyle(LineStyle.from(lineStyleDropBox.getSelectedIndex() + 1));
            lineStyleDropBox.setSelectedIndex(applicationModel.getLineStyle().getType() - 1);
        });
        topPanelCB.addActionListener(e -> applicationModel.setDisplayTopPanel(topPanelCB.isSelected()));
        bottomPanelCB.addActionListener(e -> applicationModel.setDisplayBottomPanel(bottomPanelCB.isSelected()));
        leftPanelCB.addActionListener(e -> applicationModel.setDisplayLeftPanel(leftPanelCB.isSelected()));
        rightPanelCB.addActionListener(e -> applicationModel.setDisplayRightPanel(rightPanelCB.isSelected()));
        checkBoxAnimation.addActionListener(e -> {
            applicationModel.setAnimations(checkBoxAnimation.isSelected());
            animationSpeedSlider.setEnabled(applicationModel.getAnimations());
        });
        mouseRangeSlider.addChangeListener(e -> applicationModel.setMouseRadius(mouseRangeSlider.getValue()));

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        restoreDefaultsButton.addActionListener(e -> onReset());
        importButton.addActionListener(e -> {
            fileSaveService.importPref(contentPane, frameProvider, buttonService);
            setData(applicationModel);
            setupHotKey(buttonService, frameProvider);
        });
        exportButton.addActionListener(e -> fileSaveService.exportPref());

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
        setVisible(false);
    }

    private void onCancel() {
        setData(tempModel);
        applicationModel.set(tempModel);
        foldedFigureModel.set(tempfoldedModel);
        setVisible(false);
    }

    private void onReset() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to restore all the defaults?", "Restore defaults", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            applicationModel.restorePrefDefaults();
            foldedFigureModel.restorePrefDefaults();
            dispose();
        }
    }

    public void updateTempModel(ApplicationModel applicationModel) {
        tempModel.set(applicationModel);
    }

    public KeyStroke getKeyBind(FrameProvider owner, String key) {
        InputMap map = owner.get().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke stroke = null;
        for (KeyStroke keyStroke : map.keys()) {
            if (map.get(keyStroke).equals(key)) {
                stroke = keyStroke;
            }
        }
        return stroke;
    }

    private JLabel getIconLabel(ButtonService buttonService, String key) {
        JLabel icon = new JLabel();
        icon.setEnabled(true);
        icon.setFocusable(false);
        icon.setName("");
        icon.setText("");
        buttonService.registerLabel(icon, key);

        return icon;
    }

    private JLabel getTextLabel(String key) {
        JLabel label = new JLabel();
        label.setEnabled(true);
        label.setFocusable(false);
        label.setIconTextGap(4);
        String actionText = ResourceUtil.getBundleString("name", key);
        if (actionText != null) {
            actionText = actionText.replaceAll("_", "");
        }
        label.setText(actionText);

        return label;
    }

    private JButton getKeyStrokeButton(ButtonService buttonService, FrameProvider frameProvider, String key) {
        Map<KeyStroke, AbstractButton> helpInputMap = buttonService.getHelpInputMap();
        KeyStroke currentKeyStroke = getKeyBind(frameProvider, key);
        AbstractButton button = buttonService.getPrefHotkeyMap().get(key);

        Action hotkeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyStroke tempKeyStroke = getKeyBind(frameProvider, key);
                new SelectKeyStrokeDialog(frameProvider.get(), button, helpInputMap, tempKeyStroke, newKeyStroke -> {
                    if (newKeyStroke != null && helpInputMap.containsKey(newKeyStroke) && helpInputMap.get(newKeyStroke) != button) {
                        String conflictingButton = (String) helpInputMap.get(newKeyStroke).getRootPane()
                                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                                .get(newKeyStroke);
                        JOptionPane.showMessageDialog(frameProvider.get(), "Conflicting KeyStroke! Conflicting with " + conflictingButton);
                        return false;
                    }

                    ResourceUtil.updateBundleKey("hotkey", key, newKeyStroke == null ? "" : newKeyStroke.toString());

                    helpInputMap.remove(tempKeyStroke);
                    frameProvider.get().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(tempKeyStroke);

                    if (newKeyStroke != null) {
                        buttonService.addKeyStroke(newKeyStroke, button, key, true);
                        putValue(Action.NAME, KeyStrokeUtil.toString(newKeyStroke));
                    } else {
                        putValue(Action.NAME, " ");
                    }

                    if (button != null) buttonService.setTooltip(button, key);

                    return true;
                });
            }
        };
        JButton keyStrokeButton = new JButton(hotkeyAction);
        String ksString = KeyStrokeUtil.toString(currentKeyStroke);
        keyStrokeButton.setText(!ksString.isEmpty() ? ksString : " ");

        return keyStrokeButton;
    }

    private void setupCategoryPanel(JPanel categoryPanel, JLabel clickLabel, JPanel listPanel, String categoryHeader) {
        // Category Panel
        categoryPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 5, 0), -1, -1));
        hotkeyPanel.add(categoryPanel, new GridConstraints(categoryHeaderList.indexOf(categoryHeader), 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        //Category Label
        clickLabel.setText("▸ ".concat(categoryHeader.toUpperCase()));
        categoryPanel.add(clickLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        //List panel showing the hotkey list
        listPanel.setLayout(new GridLayoutManager(ActionType.values().length + 1, 4, new Insets(0, 15, 0, 0), -1, -1));
        listPanel.setEnabled(false);
        listPanel.setVisible(false);
        categoryPanel.add(listPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
    }

    private void addIconTextHotkey(ButtonService buttonService, FrameProvider frameProvider, JPanel listPanel, String categoryHeader) {
        final Spacer spacer1 = new Spacer();
        final Spacer spacer2 = new Spacer();


        for (String key : hotkeyCategoryMap.get(categoryHeader)) {
            int index = hotkeyCategoryMap.get(categoryHeader).indexOf(key);

            JLabel iconLabel = getIconLabel(buttonService, key);
            listPanel.add(iconLabel, new GridConstraints(index, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, 1, 1, null, null, null, 0, false));

            JLabel nameLabel = getTextLabel(key);
            listPanel.add(nameLabel, new GridConstraints(index, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            JButton keystrokeButton = getKeyStrokeButton(buttonService, frameProvider, key);
            listPanel.add(keystrokeButton, new GridConstraints(index, 3, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            //TODO: a restore default button for hotkeys specifically
        }
        listPanel.add(spacer1, new GridConstraints(hotkeyCategoryMap.get(categoryHeader).size() - 1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listPanel.add(spacer2, new GridConstraints(0, 2, hotkeyCategoryMap.get(categoryHeader).size(), 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

    }

    private void extractHeaders(List<String[]> allData) {
        String[] headers = allData.get(0);
        categoryHeaderList.addAll(Arrays.asList(headers));
        for (String header : headers) {
            hotkeyCategoryMap.put(header, new ArrayList<>());
        }
    }

    private void extractData(List<String[]> allData) {
        for (int i = 1; i < allData.size(); i++) {
            String[] row = allData.get(i);
            for (int j = 0; j < row.length; j++) {
                if (!row[j].isEmpty()) {
                    hotkeyCategoryMap.get(categoryHeaderList.get(j)).add(row[j]);
                }
            }
        }
    }

    private void readCSV() {
        try {
            // Create an object of input stream reader class with CSV file as a parameter.
            InputStream is = this.getClass().getResourceAsStream("/categories.csv");

            assert is != null;
            InputStreamReader inputStreamReader = new InputStreamReader(is);

            // create csvParser object with
            // custom separator semicolon
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

            // create csvReader object with parameter
            // file-reader and parser
            CSVReader csvReader = new CSVReaderBuilder(inputStreamReader)
                    .withCSVParser(parser)
                    .build();

            // Read all data at once
            List<String[]> allData = csvReader.readAll();

            // Extract headers
            extractHeaders(allData);

            // Extract Data excluding headers
            extractData(allData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupHotKey(ButtonService buttonService, FrameProvider frameProvider) {
        hotkeyPanel.removeAll();
        for (String categoryHeader : categoryHeaderList) {
            JPanel categoryPanel = new JPanel();
            JLabel clickLabel = new JLabel();
            JPanel listPanel = new JPanel();

            setupCategoryPanel(categoryPanel, clickLabel, listPanel, categoryHeader);

            addIconTextHotkey(buttonService, frameProvider, listPanel, categoryHeader);

            clickLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listPanel.setEnabled(!listPanel.isEnabled());
                    listPanel.setVisible(listPanel.isEnabled());
                    clickLabel.setText(listPanel.isEnabled() ? "▾ ".concat(categoryHeader.toUpperCase()) : "▸ ".concat(categoryHeader.toUpperCase()));
                }
            });
        }
        final Spacer hotkeyPanelSpacer = new Spacer();
        hotkeyPanel.add(hotkeyPanelSpacer, new GridConstraints(hotkeyCategoryMap.size(), 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        contentPane.setLayout(new GridBagLayout());
        contentPane.setFocusTraversalPolicyProvider(true);
        contentPane.setMinimumSize(new Dimension(540, 610));
        contentPane.setPreferredSize(new Dimension(540, 610));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 10, 0), -1, -1));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(bottomPanel, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 10), -1, -1));
        bottomPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel1.add(buttonOK, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel1.add(buttonCancel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        restoreDefaultsButton = new JButton();
        restoreDefaultsButton.setText("Restore defaults");
        panel1.add(restoreDefaultsButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importButton = new JButton();
        importButton.setText("Import");
        panel1.add(importButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportButton = new JButton();
        exportButton.setText("Export");
        panel1.add(exportButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        bottomPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.setMinimumSize(new Dimension(530, 530));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(topPanel, gbc);
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setName("");
        tabbedPane1.setTabLayoutPolicy(0);
        tabbedPane1.setTabPlacement(2);
        topPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        tabbedPane1.addTab("DISPLAY", panel2);
        display2Panel = new JPanel();
        display2Panel.setLayout(new GridBagLayout());
        display2Panel.setName("");
        display2Panel.setRequestFocusEnabled(true);
        panel2.add(display2Panel);
        CPLabel = new JLabel();
        CPLabel.setText("Crease Pattern");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        display2Panel.add(CPLabel, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Miscellaneous");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        display2Panel.add(label1, gbc);
        commentCB = new JCheckBox();
        commentCB.setText("Comment");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(commentCB, gbc);
        foldingProgressCB = new JCheckBox();
        foldingProgressCB.setText("Folding progress");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(foldingProgressCB, gbc);
        selfIntersectionCB = new JCheckBox();
        selfIntersectionCB.setText("Self intersection");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(selfIntersectionCB, gbc);
        foldWarningCB = new JCheckBox();
        foldWarningCB.setText("Fold warning");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(foldWarningCB, gbc);
        toggleHelpCB = new JCheckBox();
        toggleHelpCB.setText("Help dialog");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(toggleHelpCB, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Panels");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        display2Panel.add(label2, gbc);
        inputAssistCB = new JCheckBox();
        inputAssistCB.setText("Grid input assist");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(inputAssistCB, gbc);
        topPanelCB = new JCheckBox();
        topPanelCB.setText("Top panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(topPanelCB, gbc);
        bottomPanelCB = new JCheckBox();
        bottomPanelCB.setText("Bottom panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(bottomPanelCB, gbc);
        leftPanelCB = new JCheckBox();
        leftPanelCB.setText("Left panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(leftPanelCB, gbc);
        rightPanelCB = new JCheckBox();
        rightPanelCB.setText("Right panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        display2Panel.add(rightPanelCB, gbc);
        cpLinesCB = new JCheckBox();
        cpLinesCB.setText("CP lines");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        display2Panel.add(cpLinesCB, gbc);
        auxLinesCB = new JCheckBox();
        auxLinesCB.setText("Aux lines");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        display2Panel.add(auxLinesCB, gbc);
        liveAuxCB = new JCheckBox();
        liveAuxCB.setText("Live aux lines");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        display2Panel.add(liveAuxCB, gbc);
        markingsCB = new JCheckBox();
        markingsCB.setText("Markings");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        display2Panel.add(markingsCB, gbc);
        cpOnTopCB = new JCheckBox();
        cpOnTopCB.setText("CP on top");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        display2Panel.add(cpOnTopCB, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel3.setMinimumSize(new Dimension(354, 403));
        tabbedPane1.addTab("APPEARANCE", panel3);
        appearance2Panel = new JPanel();
        appearance2Panel.setLayout(new GridBagLayout());
        panel3.add(appearance2Panel);
        final JLabel label3 = new JLabel();
        label3.setText("CP line width: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText(" Live aux width: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText(" Point radius: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("MV Line style: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(label6, gbc);
        lineStyleDropBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Color solid");
        defaultComboBoxModel1.addElement("BW solid");
        defaultComboBoxModel1.addElement("Color dashes");
        defaultComboBoxModel1.addElement("BW - 1 dot/dash");
        defaultComboBoxModel1.addElement("BW - 2 dot/dash");
        lineStyleDropBox.setModel(defaultComboBoxModel1);
        lineStyleDropBox.setToolTipText("Select line style");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        appearance2Panel.add(lineStyleDropBox, gbc);
        final JLabel label7 = new JLabel();
        label7.setText(" Main grid color: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(label7, gbc);
        gridColorButton = new JButton();
        gridColorButton.setText("Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        appearance2Panel.add(gridColorButton, gbc);
        final JLabel label8 = new JLabel();
        label8.setText(" Sub grid color: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(label8, gbc);
        gridScaleColorButton = new JButton();
        gridScaleColorButton.setText("Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        appearance2Panel.add(gridScaleColorButton, gbc);
        final JLabel label9 = new JLabel();
        label9.setText(" Grid line width: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(label9, gbc);
        ck4Label = new JLabel();
        ck4Label.setText("cAMV Opacity: ");
        ck4Label.setToolTipText("Change cAMV opacity 20-100%");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance2Panel.add(ck4Label, gbc);
        darkModeCheckBox = new JCheckBox();
        darkModeCheckBox.setText("Dark mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        appearance2Panel.add(darkModeCheckBox, gbc);
        antiAliasCB = new JCheckBox();
        antiAliasCB.setText("Anti-alias");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        appearance2Panel.add(antiAliasCB, gbc);
        displayNumbersCB = new JCheckBox();
        displayNumbersCB.setText("Display numbers");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        appearance2Panel.add(displayNumbersCB, gbc);
        foldAntiAliasCheckBox = new JCheckBox();
        foldAntiAliasCheckBox.setText("Fold anti-alias");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        appearance2Panel.add(foldAntiAliasCheckBox, gbc);
        ck4Panel = new JPanel();
        ck4Panel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        appearance2Panel.add(ck4Panel, gbc);
        ck4Minus = new JButton();
        ck4Minus.setText("-");
        ck4Panel.add(ck4Minus, BorderLayout.WEST);
        ck4TF = new JTextField();
        ck4TF.setColumns(1);
        ck4TF.setEnabled(false);
        ck4TF.setPreferredSize(new Dimension(17, -1));
        ck4Panel.add(ck4TF, BorderLayout.CENTER);
        ck4Plus = new JButton();
        ck4Plus.setText("+");
        ck4Panel.add(ck4Plus, BorderLayout.EAST);
        CPLinePanel = new JPanel();
        CPLinePanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        appearance2Panel.add(CPLinePanel, gbc);
        lineWidthPlus = new JButton();
        lineWidthPlus.setText("+");
        CPLinePanel.add(lineWidthPlus, BorderLayout.EAST);
        lineWidthTF = new JTextField();
        lineWidthTF.setColumns(1);
        lineWidthTF.setEnabled(false);
        lineWidthTF.setToolTipText("Input an integer");
        CPLinePanel.add(lineWidthTF, BorderLayout.CENTER);
        lineWidthMinus = new JButton();
        lineWidthMinus.setText("-");
        CPLinePanel.add(lineWidthMinus, BorderLayout.WEST);
        liveAuxPanel = new JPanel();
        liveAuxPanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        appearance2Panel.add(liveAuxPanel, gbc);
        auxLineMinus = new JButton();
        auxLineMinus.setText("-");
        liveAuxPanel.add(auxLineMinus, BorderLayout.WEST);
        auxLineTF = new JTextField();
        auxLineTF.setColumns(1);
        auxLineTF.setEnabled(false);
        auxLineTF.setText("");
        auxLineTF.setToolTipText("Input an integer");
        liveAuxPanel.add(auxLineTF, BorderLayout.CENTER);
        auxLinePlus = new JButton();
        auxLinePlus.setText("+");
        liveAuxPanel.add(auxLinePlus, BorderLayout.EAST);
        pointRadPanel = new JPanel();
        pointRadPanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        appearance2Panel.add(pointRadPanel, gbc);
        pointSizeMinus = new JButton();
        pointSizeMinus.setText("-");
        pointRadPanel.add(pointSizeMinus, BorderLayout.WEST);
        pointSizeTF = new JTextField();
        pointSizeTF.setColumns(1);
        pointSizeTF.setEnabled(false);
        pointSizeTF.setToolTipText("Input an integer");
        pointRadPanel.add(pointSizeTF, BorderLayout.CENTER);
        pointSizePlus = new JButton();
        pointSizePlus.setText("+");
        pointRadPanel.add(pointSizePlus, BorderLayout.EAST);
        gridLinePanel = new JPanel();
        gridLinePanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        appearance2Panel.add(gridLinePanel, gbc);
        gridWidthMinus = new JButton();
        gridWidthMinus.setText("-");
        gridLinePanel.add(gridWidthMinus, BorderLayout.WEST);
        gridWidthTF = new JTextField();
        gridWidthTF.setColumns(1);
        gridWidthTF.setEnabled(false);
        gridWidthTF.setText("");
        gridWidthTF.setToolTipText("Input an integer");
        gridLinePanel.add(gridWidthTF, BorderLayout.CENTER);
        gridWidthPlus = new JButton();
        gridWidthPlus.setText("+");
        gridLinePanel.add(gridWidthPlus, BorderLayout.EAST);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel4.setMinimumSize(new Dimension(334, 226));
        tabbedPane1.addTab("BEHAVIOR", panel4);
        behavior2Panel = new JPanel();
        behavior2Panel.setLayout(new GridBagLayout());
        panel4.add(behavior2Panel);
        zoomSpeedSlider.setMajorTickSpacing(10);
        zoomSpeedSlider.setMaximum(100);
        zoomSpeedSlider.setMinimum(0);
        zoomSpeedSlider.setPaintLabels(true);
        zoomSpeedSlider.setPaintTicks(true);
        zoomSpeedSlider.setPaintTrack(true);
        zoomSpeedSlider.setSnapToTicks(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        behavior2Panel.add(zoomSpeedSlider, gbc);
        mouseRangeSlider = new JSlider();
        mouseRangeSlider.setMaximum(100);
        mouseRangeSlider.setMinimum(2);
        mouseRangeSlider.setValue(20);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        behavior2Panel.add(mouseRangeSlider, gbc);
        animationSpeedSlider.setMajorTickSpacing(8);
        animationSpeedSlider.setMaximum(24);
        animationSpeedSlider.setPaintLabels(true);
        animationSpeedSlider.setPaintTicks(true);
        animationSpeedSlider.setPaintTrack(true);
        animationSpeedSlider.setSnapToTicks(false);
        animationSpeedSlider.setValue(8);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        behavior2Panel.add(animationSpeedSlider, gbc);
        checkBoxAnimation = new JCheckBox();
        checkBoxAnimation.setText("Animations");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        behavior2Panel.add(checkBoxAnimation, gbc);
        preciseZoomCB = new JCheckBox();
        preciseZoomCB.setText("Precise zoom");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        behavior2Panel.add(preciseZoomCB, gbc);
        mousewheelMovesCPCB = new JCheckBox();
        mousewheelMovesCPCB.setText("Mousewheel moves CP");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        behavior2Panel.add(mousewheelMovesCPCB, gbc);
        spotlightCB = new JCheckBox();
        spotlightCB.setText("Selection range");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        behavior2Panel.add(spotlightCB, gbc);
        offsetCB = new JCheckBox();
        offsetCB.setText("Offset mouse");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        behavior2Panel.add(offsetCB, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setAutoscrolls(false);
        scrollPane1.setVerticalScrollBarPolicy(20);
        tabbedPane1.addTab("HOTKEYS", scrollPane1);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane1.setViewportView(hotkeyPanel);
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
        for (int i = 0; i <= 5; i++) {
            labels.put(i * 20, new JLabel(String.valueOf(i * 2)));
        }
        zoomSpeedSlider.setLabelTable(labels);
        zoomSpeedSlider.setPaintLabels(true);

        animationSpeedSlider = new JSlider(0, 24);
        Dictionary<Integer, JLabel> labelsAnimSpeed = new Hashtable<>();
        labelsAnimSpeed.put(0, new JLabel("Fast"));
        labelsAnimSpeed.put(8, new JLabel("Normal"));
        labelsAnimSpeed.put(16, new JLabel("Slow"));
        labelsAnimSpeed.put(24, new JLabel("Slowest"));
        animationSpeedSlider.setLabelTable(labelsAnimSpeed);

        hotkeyCategoryMap = new LinkedHashMap<>();
        categoryHeaderList = new ArrayList<>();
        readCSV();

        hotkeyPanel = new JPanel();
        hotkeyPanel.setLayout(new GridLayoutManager(hotkeyCategoryMap.size() + 1, 2, new Insets(10, 10, 0, 10), -1, -1));
    }
}
