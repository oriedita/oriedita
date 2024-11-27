package oriedita.editor.swing.dialog;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.tinylog.Logger;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.action.ActionType;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.LookAndFeelService;
import oriedita.editor.swing.CollapsiblePanel;
import oriedita.editor.swing.component.ColorIcon;
import oriedita.editor.swing.component.GlyphIcon;
import oriedita.editor.tools.KeyStrokeUtil;
import oriedita.editor.tools.ResourceUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private JPanel appearance1Panel;
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
    private JScrollPane scrollPane1;
    private JSlider gridDensitySlider;
    private JCheckBox roundedEndsCheckbox;
    private JPanel display1Panel;
    private JPanel display3Panel;
    private JTextField searchBarTF;
    private JCheckBox hasHotkeyCB;
    private JButton defaultGridSizeMinus;
    private JButton defaultGridSizePlus;
    private JTextField defaultGridSizeTF;
    private JCheckBox detachGridColorCB;
    private JCheckBox detachFigureColorCB;
    private int tempTransparency;
    private final ApplicationModel applicationModel;
    private final ButtonService buttonService;
    private final ApplicationModel tempModel;
    private final FoldedFigureModel foldedFigureModel;
    private final FoldedFigureModel tempfoldedModel;
    private Map<String, List<String>> hotkeyCategoryMap;
    private List<String> categoryHeaderList;
    private List<String> searchPhrases;
    private List<String[]> allData;

    private final List<PropertyChangeListener> activeListeners = new ArrayList<>();

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
        displayNumbersCB.setSelected(applicationModel.getDisplayNumbers());
        ck4TF.setText(Integer.toString((applicationModel.getCheck4ColorTransparency() / 5) * 2));
        lineWidthTF.setText(Integer.toString(applicationModel.getLineWidth()));
        auxLineTF.setText(Integer.toString(applicationModel.getAuxLineWidth()));
        pointSizeTF.setText(Integer.toString(applicationModel.getPointSize()));
        defaultGridSizeTF.setText(Integer.toString(applicationModel.getDefaultGridSize()));
        gridWidthTF.setText(Integer.toString(applicationModel.getGridLineWidth()));
        gridColorButton.setIcon(new ColorIcon(applicationModel.getGridColor()));
        gridScaleColorButton.setIcon(new ColorIcon(applicationModel.getGridScaleColor()));
        detachGridColorCB.setSelected(applicationModel.getIsGridColorDetached());
        detachFigureColorCB.setSelected(applicationModel.getIsFoldedFigureColorDetached());
        lineStyleDropBox.setSelectedIndex(applicationModel.getLineStyle().getType() - 1);
        topPanelCB.setSelected(applicationModel.getDisplayTopPanel());
        bottomPanelCB.setSelected(applicationModel.getDisplayBottomPanel());
        leftPanelCB.setSelected(applicationModel.getDisplayLeftPanel());
        rightPanelCB.setSelected(applicationModel.getDisplayRightPanel());
        zoomSpeedSlider.setValue((int) (applicationModel.getZoomSpeed() * 10));
        checkBoxAnimation.setSelected(applicationModel.getAnimations());
        animationSpeedSlider.setValue((int) ((applicationModel.getAnimationSpeed()) * 8));
        mouseRangeSlider.setValue((int) applicationModel.getMouseRadius());
        roundedEndsCheckbox.setSelected(applicationModel.getRoundedEnds());
        gridDensitySlider.setValue((int) (gridDensitySlider.getMaximum() - applicationModel.getMinGridUnitSize() + 0.6));

        ck4Plus.setEnabled(applicationModel.getCheck4ColorTransparency() < 250);
        ck4Minus.setEnabled(applicationModel.getCheck4ColorTransparency() > 50);
        lineWidthMinus.setEnabled(applicationModel.getLineWidth() > 0);
        auxLineMinus.setEnabled(applicationModel.getAuxLineWidth() > 0);
        pointSizeMinus.setEnabled(applicationModel.getPointSize() > 0);
        gridWidthMinus.setEnabled(applicationModel.getGridLineWidth() > 1);
        animationSpeedSlider.setEnabled(applicationModel.getAnimations());
    }

    public void setData(FoldedFigureModel foldedFigureModel) {
        foldAntiAliasCheckBox.setSelected(foldedFigureModel.getAntiAlias());
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
        this.buttonService = buttonService;
        this.tempModel = new ApplicationModel();
        this.tempModel.set(appModel);
        this.foldedFigureModel = foldedFigureModel;
        this.tempfoldedModel = new FoldedFigureModel();
        this.tempfoldedModel.set(foldedFigureModel);

        $$$setupUI$$$();
        setData(applicationModel);
        setData(foldedFigureModel);
        setContentPane($$$getRootComponent$$$());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(null);

        setupHotKey(buttonService, frameProvider);

        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));
        foldedFigureModel.addPropertyChangeListener(e -> setData(foldedFigureModel));

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
            lookAndFeelService.toggleDarkMode();

            EventQueue.invokeLater(() -> {
                if (FlatLaf.isLafDark()) {
                    if (!applicationModel.getIsGridColorDetached()) {
                        applicationModel.setGridColor(Colors.GRID_LINE_DARK);
                        applicationModel.setGridScaleColor(Colors.GRID_SCALE_DARK);
                    }

                    if (!applicationModel.getIsFoldedFigureColorDetached()) {
                        foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT_DARK);
                        foldedFigureModel.setBackColor(Colors.FIGURE_BACK_DARK);
                    }
                } else {
                    if (!applicationModel.getIsGridColorDetached()) {
                        applicationModel.setGridColor(Colors.GRID_LINE);
                        applicationModel.setGridScaleColor(Colors.GRID_SCALE);
                    }

                    if (!applicationModel.getIsFoldedFigureColorDetached()) {
                        foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT);
                        foldedFigureModel.setBackColor(Colors.FIGURE_BACK);
                    }
                }
                gridColorButton.setIcon(new ColorIcon(applicationModel.getGridColor()));
                gridScaleColorButton.setIcon(new ColorIcon(applicationModel.getGridScaleColor()));
            });
        });
        antiAliasCB.addActionListener(e -> applicationModel.setAntiAlias(antiAliasCB.isSelected()));
        foldAntiAliasCheckBox.addActionListener(e -> foldedFigureModel.setAntiAlias(foldAntiAliasCheckBox.isSelected()));
        displayNumbersCB.addActionListener(e -> applicationModel.setDisplayNumbers(displayNumbersCB.isSelected()));
        roundedEndsCheckbox.addActionListener(e -> applicationModel.setRoundedEnds(roundedEndsCheckbox.isSelected()));
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
        defaultGridSizePlus.addActionListener(e -> {
            applicationModel.setDefaultGridSize(Integer.parseInt(defaultGridSizeTF.getText()) + 1);
            defaultGridSizeTF.setText(Integer.toString(applicationModel.getDefaultGridSize()));
            defaultGridSizeMinus.setEnabled(true);
        });
        defaultGridSizeMinus.addActionListener(e -> {
            if (applicationModel.getDefaultGridSize() > 1) {
                applicationModel.setDefaultGridSize(Integer.parseInt(defaultGridSizeTF.getText()) - 1);
                defaultGridSizeMinus.setEnabled(applicationModel.getDefaultGridSize() > 1);
                defaultGridSizeTF.setText(Integer.toString(applicationModel.getDefaultGridSize()));
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
            Color gridColor = JColorChooser.showDialog(frameProvider.get(), "F_col", FlatLaf.isLafDark() ? Colors.GRID_LINE_DARK : Colors.GRID_LINE);

            if (gridColor != null) {
                applicationModel.setGridColor(gridColor);
                gridColorButton.setIcon(new ColorIcon(gridColor));
            }
        });
        gridScaleColorButton.addActionListener(e -> {
            Color gridScaleColor = JColorChooser.showDialog(frameProvider.get(), "F_col", FlatLaf.isLafDark() ? Colors.GRID_SCALE_DARK : Colors.GRID_SCALE);

            if (gridScaleColor != null) {
                applicationModel.setGridScaleColor(gridScaleColor);
                gridScaleColorButton.setIcon(new ColorIcon(gridScaleColor));
            }
        });
        animationSpeedSlider.addChangeListener(e -> applicationModel.setAnimationSpeed(animationSpeedSlider.getValue() / 8.0));
        lineStyleDropBox.addActionListener(e -> {
            applicationModel.setLineStyle(LineStyle.from(lineStyleDropBox.getSelectedIndex() + 1));
            lineStyleDropBox.setSelectedIndex(applicationModel.getLineStyle().getType() - 1);
        });
        gridDensitySlider.addChangeListener(e -> applicationModel.setMinGridUnitSize(
                gridDensitySlider.getMaximum() - gridDensitySlider.getValue() + .5));

        detachGridColorCB.addActionListener(e -> applicationModel.setIsGridColorDetached(detachGridColorCB.isSelected()));
        detachFigureColorCB.addActionListener(e -> applicationModel.setIsFoldedFigureColorDetached(detachFigureColorCB.isSelected()));

        topPanelCB.addActionListener(e -> applicationModel.setDisplayTopPanel(topPanelCB.isSelected()));
        bottomPanelCB.addActionListener(e -> applicationModel.setDisplayBottomPanel(bottomPanelCB.isSelected()));
        leftPanelCB.addActionListener(e -> applicationModel.setDisplayLeftPanel(leftPanelCB.isSelected()));
        rightPanelCB.addActionListener(e -> applicationModel.setDisplayRightPanel(rightPanelCB.isSelected()));
        checkBoxAnimation.addActionListener(e -> {
            applicationModel.setAnimations(checkBoxAnimation.isSelected());
            animationSpeedSlider.setEnabled(applicationModel.getAnimations());
        });
        mouseRangeSlider.addChangeListener(e -> applicationModel.setMouseRadius(mouseRangeSlider.getValue()));

        searchBarTF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshHotkeyPanel(frameProvider);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshHotkeyPanel(frameProvider);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshHotkeyPanel(frameProvider);
            }
        });

        hasHotkeyCB.addActionListener(e -> refreshHotkeyPanel(frameProvider));

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        restoreDefaultsButton.addActionListener(e -> onReset());
        importButton.addActionListener(e -> {
            fileSaveService.importPref();
            setData(applicationModel);
            setData(foldedFigureModel);
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
        dispose();
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
            ResourceUtil.clearBundle("hotkey");
            buttonService.removeAllKeyBinds();
            buttonService.loadAllKeyStrokes();
            contentPane.repaint();
        }
    }

    private List<String> parseSearchPhrases() {
        String phrase = searchBarTF.getText();
        return phrase.isEmpty() ? Collections.emptyList() : Arrays.asList(phrase.split("\\s+"));
    }

    private void refreshHotkeyPanel(FrameProvider frameProvider) {
        searchPhrases = parseSearchPhrases();
        // Reset hotkeyCategoryMap keeping headers
        categoryHeaderList.forEach(header -> hotkeyCategoryMap.put(header, new ArrayList<>()));
        updateList(allData); // Update the action list in each header
        setupHotKey(buttonService, frameProvider);
        contentPane.repaint();
    }

    public void updateTempModel(ApplicationModel applicationModel) {
        tempModel.set(applicationModel);
    }

    public KeyStroke getKeyBind(FrameProvider owner, String key) {
        InputMap map = owner.get().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        return Arrays.stream(map.keys()).filter(ks -> map.get(ks).equals(key)).findFirst().orElse(null);
    }

    private JLabel getIconLabel(ButtonService buttonService, String key) {
        JLabel icon = new JLabel();
        icon.setEnabled(true);
        icon.setFocusable(false);
        icon.setName("");
        icon.setText("");
        buttonService.setIcon(icon, key);

        return icon;
    }

    private JButton getRestoreHotkeyButton(String key, JButton keystrokeButton) {
        Action restoreHotkeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get default keystroke string
                String defaultKeyStrokeString = ResourceUtil.getDefaultHotkeyBundleString(key);
                KeyStroke keyStroke = KeyStroke.getKeyStroke(defaultKeyStrokeString);

                // If string matches the current string of an action and not empty
                // send error message
                assert defaultKeyStrokeString != null;
                String conflictingAction = buttonService.getActionFromKeystroke(keyStroke);
                if (keyStroke != null
                        && conflictingAction != null
                        && !Objects.equals(conflictingAction, key)) {
                    JOptionPane.showMessageDialog(null, "Default has conflict with ".concat(conflictingAction), "Conflict", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // else update the action to default
                ResourceUtil.updateBundleKey("hotkey", key, defaultKeyStrokeString.isEmpty() ? "" : defaultKeyStrokeString);
                String ksString = KeyStrokeUtil.toStringWithMetaIcon(KeyStrokeUtil.toString(keyStroke));
                keystrokeButton.setText(ksString.isEmpty() ? " " : ksString);
                buttonService.setKeyStroke(keyStroke, key);
            }
        };

        JButton button = new JButton(restoreHotkeyAction);
        String restoreIcon = "\ue02e";
        button.setIcon(new GlyphIcon(restoreIcon, button.getForeground()));

        return button;
    }

    private JLabel getTextLabel(String key) {
        JLabel label = new JLabel();
        label.setEnabled(true);
        label.setFocusable(false);
        label.setIconTextGap(4);
        String actionText = ResourceUtil.getBundleString("name", key);
        label.setText(actionText != null ? actionText.replaceAll("_", "") : "Invalid name: " + key);

        return label;
    }

    private JButton getKeyStrokeButton(FrameProvider frameProvider, String key) {
        KeyStroke currentKeyStroke = getKeyBind(frameProvider, key);

        Action hotkeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyStroke tempKeyStroke = getKeyBind(frameProvider, key);
                new SelectKeyStrokeDialog(frameProvider.get(), key, buttonService, tempKeyStroke);
            }
        };

        JButton keyStrokeButton = new JButton(hotkeyAction);
        String ksString = KeyStrokeUtil.toStringWithMetaIcon(KeyStrokeUtil.toString(currentKeyStroke));

        PropertyChangeListener listener = e -> {
            if (Objects.equals(e.getPropertyName(), key)) {
                KeyStroke ks = (KeyStroke) e.getNewValue();
                String temp = KeyStrokeUtil.toStringWithMetaIcon(KeyStrokeUtil.toString(ks));
                hotkeyAction.putValue(Action.NAME, ks == null ? " " : KeyStrokeUtil.toString(ks));
                keyStrokeButton.setText(!temp.isEmpty() ? temp : " ");
            }
        };

        activeListeners.add(listener);
        buttonService.addKeystrokeChangeListener(listener);
        keyStrokeButton.setText(!ksString.isEmpty() ? ksString : " ");

        return keyStrokeButton;
    }

    @Override
    public void dispose() {
        super.dispose();
        // remove all listeners from buttonService to prevent memory leak
        for (PropertyChangeListener listener : activeListeners) {
            buttonService.removeKeystrokeChangeListener(listener);
        }
    }

    private void setupCategoryPanel(JPanel listPanel, String categoryHeader) {
        CollapsiblePanel categoryPanel = new CollapsiblePanel(categoryHeader.toUpperCase(), listPanel);
        hotkeyPanel.add(categoryPanel, new GridConstraints(categoryHeaderList.indexOf(categoryHeader), 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
    }

    private void addIconTextHotkey(ButtonService buttonService, FrameProvider frameProvider, JPanel listPanel, String categoryHeader) {
        for (String key : hotkeyCategoryMap.get(categoryHeader)) {
            int index = hotkeyCategoryMap.get(categoryHeader).indexOf(key);

            JLabel iconLabel = getIconLabel(buttonService, key);
            listPanel.add(iconLabel, new GridConstraints(index, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, 1, null, null, null, 0, false));

            JLabel nameLabel = getTextLabel(key);
            listPanel.add(nameLabel, new GridConstraints(index, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            JButton keystrokeButton = getKeyStrokeButton(frameProvider, key);
            listPanel.add(keystrokeButton, new GridConstraints(index, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.ALIGN_RIGHT, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(70, 21), null, null, 0, false));

            JButton restoreHotkeyButton = getRestoreHotkeyButton(key, keystrokeButton);
            listPanel.add(restoreHotkeyButton, new GridConstraints(index, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(21, 21), new Dimension(21, 21), null, 0, false));
        }
    }

    private void loadHeaders(List<String[]> allData) {
        categoryHeaderList.addAll(Arrays.asList(allData.get(0)));
        categoryHeaderList.forEach(header -> hotkeyCategoryMap.put(header, new ArrayList<>()));
    }

    private void updateList(List<String[]> allData) {
        for (int i = 1; i < allData.size(); i++) {
            List<String> actions = List.of(allData.get(i));
            for (String action : actions) {
                if (action.isEmpty()) continue;

                String hotkey = ResourceUtil.getBundleString("hotkey", action);
                if ((hotkey == null || hotkey.isEmpty()) && hasHotkeyCB.isSelected()) {
                    continue;
                }

                String actionName = ResourceUtil.getBundleString("name", action);
                if (actionName == null) continue;

                String finalActionName = actionName.replaceAll("_", "").toLowerCase();
                if (searchPhrases.isEmpty() ||
                        searchPhrases.stream().allMatch(phrase -> finalActionName.contains(phrase.toLowerCase()))) {
                    hotkeyCategoryMap.get(categoryHeaderList.get(actions.indexOf(action))).add(action);
                }
            }
        }
    }

    private void readCSV() { // Only run once
        try {
            // Create an object of input stream reader class with CSV file as a parameter.
            InputStream is = this.getClass().getResourceAsStream("/categories.csv");

            assert is != null;
            PushbackInputStream pushbackInputStream = new PushbackInputStream(is, 3);
            InputStreamReader inputStreamReader = new InputStreamReader(pushbackInputStream, "UTF-8");

            // Check for BOM
            byte[] bom = new byte[3];
            int bytesRead = pushbackInputStream.read(bom, 0, 3);
            boolean hasBOM = bytesRead == 3 && bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF;
            if (!hasBOM) pushbackInputStream.unread(bom, 0, bytesRead);

            // create csvParser object with custom separator semicolon
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

            // create csvReader object with parameter
            // file-reader and parser
            try (CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).withCSVParser(parser).build()) {
                allData = csvReader.readAll(); // Read all data at once
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public void setupHotKey(ButtonService buttonService, FrameProvider frameProvider) {
        hotkeyPanel.removeAll();
        for (String categoryHeader : categoryHeaderList) {
            if (!hotkeyCategoryMap.get(categoryHeader).isEmpty()) {
                JPanel listPanel = new JPanel();
                listPanel.setLayout(new GridLayoutManager(ActionType.values().length + 1, 4, new Insets(0, 5, 10, 0), -1, -1));

                setupCategoryPanel(listPanel, categoryHeader);
                addIconTextHotkey(buttonService, frameProvider, listPanel, categoryHeader);
            }
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
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setFocusTraversalPolicyProvider(true);
        contentPane.setMinimumSize(new Dimension(361, 565));
        contentPane.setPreferredSize(new Dimension(361, 565));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.setMinimumSize(new Dimension(530, 530));
        contentPane.add(topPanel, BorderLayout.CENTER);
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setName("");
        tabbedPane1.setTabLayoutPolicy(0);
        tabbedPane1.setTabPlacement(1);
        topPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 14));
        tabbedPane1.addTab("DISPLAY", panel1);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2);
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, 22));
        scrollPane2.setViewportView(panel2);
        display1Panel = new JPanel();
        display1Panel.setLayout(new GridBagLayout());
        panel2.add(display1Panel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        CPLabel = new JLabel();
        CPLabel.setText("CREASE PATTERN");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        display1Panel.add(CPLabel, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 20, 0, 0);
        display1Panel.add(panel3, gbc);
        cpLinesCB = new JCheckBox();
        cpLinesCB.setText("CP lines");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(cpLinesCB, gbc);
        auxLinesCB = new JCheckBox();
        auxLinesCB.setText("Aux lines");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(auxLinesCB, gbc);
        liveAuxCB = new JCheckBox();
        liveAuxCB.setText("Live aux lines");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(liveAuxCB, gbc);
        markingsCB = new JCheckBox();
        markingsCB.setText("Markings");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(markingsCB, gbc);
        cpOnTopCB = new JCheckBox();
        cpOnTopCB.setText("CP on top");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(cpOnTopCB, gbc);
        display2Panel = new JPanel();
        display2Panel.setLayout(new GridBagLayout());
        display2Panel.setName("");
        display2Panel.setRequestFocusEnabled(true);
        panel2.add(display2Panel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("PANELS");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        display2Panel.add(label1, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 20, 0, 0);
        display2Panel.add(panel4, gbc);
        topPanelCB = new JCheckBox();
        topPanelCB.setText("Top panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(topPanelCB, gbc);
        bottomPanelCB = new JCheckBox();
        bottomPanelCB.setText("Bottom panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(bottomPanelCB, gbc);
        leftPanelCB = new JCheckBox();
        leftPanelCB.setText("Left panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(leftPanelCB, gbc);
        rightPanelCB = new JCheckBox();
        rightPanelCB.setText("Right panel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(rightPanelCB, gbc);
        display3Panel = new JPanel();
        display3Panel.setLayout(new GridBagLayout());
        panel2.add(display3Panel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("MISCELLANEOUS");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        display3Panel.add(label2, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 20, 0, 0);
        display3Panel.add(panel5, gbc);
        inputAssistCB = new JCheckBox();
        inputAssistCB.setText("Grid input assist");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(inputAssistCB, gbc);
        commentCB = new JCheckBox();
        commentCB.setText("Comment");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(commentCB, gbc);
        foldingProgressCB = new JCheckBox();
        foldingProgressCB.setText("Folding progress");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(foldingProgressCB, gbc);
        selfIntersectionCB = new JCheckBox();
        selfIntersectionCB.setText("Self intersection");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(selfIntersectionCB, gbc);
        foldWarningCB = new JCheckBox();
        foldWarningCB.setText("Fold warning");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(foldWarningCB, gbc);
        toggleHelpCB = new JCheckBox();
        toggleHelpCB.setText("Help dialog");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(toggleHelpCB, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 14));
        panel6.setMinimumSize(new Dimension(354, 403));
        tabbedPane1.addTab("APPEARANCE", panel6);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel6.add(scrollPane3);
        scrollPane3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane3.setViewportView(panel7);
        appearance1Panel = new JPanel();
        appearance1Panel.setLayout(new GridBagLayout());
        panel7.add(appearance1Panel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("CP line width: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText(" Live aux width: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText(" Point radius: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("MV Line style: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label6, gbc);
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
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        appearance1Panel.add(lineStyleDropBox, gbc);
        final JLabel label7 = new JLabel();
        label7.setText(" Main grid color: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label7, gbc);
        gridColorButton = new JButton();
        gridColorButton.setText("Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        appearance1Panel.add(gridColorButton, gbc);
        final JLabel label8 = new JLabel();
        label8.setText(" Sub grid color: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label8, gbc);
        gridScaleColorButton = new JButton();
        gridScaleColorButton.setText("Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        appearance1Panel.add(gridScaleColorButton, gbc);
        final JLabel label9 = new JLabel();
        label9.setText(" Grid line width: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label9, gbc);
        ck4Label = new JLabel();
        ck4Label.setText("cAMV Opacity: ");
        ck4Label.setToolTipText("Change cAMV opacity 20-100%");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(ck4Label, gbc);
        ck4Panel = new JPanel();
        ck4Panel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        appearance1Panel.add(ck4Panel, gbc);
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
        appearance1Panel.add(CPLinePanel, gbc);
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
        appearance1Panel.add(liveAuxPanel, gbc);
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
        appearance1Panel.add(pointRadPanel, gbc);
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
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        appearance1Panel.add(gridLinePanel, gbc);
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
        final JLabel label10 = new JLabel();
        label10.setHorizontalAlignment(4);
        label10.setHorizontalTextPosition(4);
        label10.setOpaque(true);
        label10.setPreferredSize(new Dimension(121, 17));
        label10.setText("Max Grid Density: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(3, 0, 20, 0);
        appearance1Panel.add(label10, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 0, 0);
        appearance1Panel.add(gridDensitySlider, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Anti-alias: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label11, gbc);
        antiAliasCB = new JCheckBox();
        antiAliasCB.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        appearance1Panel.add(antiAliasCB, gbc);
        final JLabel label12 = new JLabel();
        label12.setText("Display numbers: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label12, gbc);
        displayNumbersCB = new JCheckBox();
        displayNumbersCB.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        appearance1Panel.add(displayNumbersCB, gbc);
        final JLabel label13 = new JLabel();
        label13.setText("Fold anti-alias: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label13, gbc);
        foldAntiAliasCheckBox = new JCheckBox();
        foldAntiAliasCheckBox.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        appearance1Panel.add(foldAntiAliasCheckBox, gbc);
        final JLabel label14 = new JLabel();
        label14.setText("Round Line-ends: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label14, gbc);
        roundedEndsCheckbox = new JCheckBox();
        roundedEndsCheckbox.setSelected(false);
        roundedEndsCheckbox.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        appearance1Panel.add(roundedEndsCheckbox, gbc);
        final JLabel label15 = new JLabel();
        label15.setText(" Default grid size: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label15, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        appearance1Panel.add(panel8, gbc);
        defaultGridSizeMinus = new JButton();
        defaultGridSizeMinus.setText("-");
        panel8.add(defaultGridSizeMinus, BorderLayout.WEST);
        defaultGridSizeTF = new JTextField();
        defaultGridSizeTF.setColumns(1);
        defaultGridSizeTF.setEnabled(false);
        defaultGridSizeTF.setPreferredSize(new Dimension(17, -1));
        panel8.add(defaultGridSizeTF, BorderLayout.CENTER);
        defaultGridSizePlus = new JButton();
        defaultGridSizePlus.setText("+");
        panel8.add(defaultGridSizePlus, BorderLayout.EAST);
        final JLabel label16 = new JLabel();
        label16.setText("Detach Grid Color: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label16, gbc);
        detachGridColorCB = new JCheckBox();
        detachGridColorCB.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        appearance1Panel.add(detachGridColorCB, gbc);
        final JLabel label17 = new JLabel();
        label17.setText("Detach Figure Color: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        appearance1Panel.add(label17, gbc);
        detachFigureColorCB = new JCheckBox();
        detachFigureColorCB.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 20, 0);
        appearance1Panel.add(detachFigureColorCB, gbc);
        final JLabel label18 = new JLabel();
        label18.setText("Dark mode: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.EAST;
        appearance1Panel.add(label18, gbc);
        darkModeCheckBox = new JCheckBox();
        darkModeCheckBox.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        appearance1Panel.add(darkModeCheckBox, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 14));
        panel9.setMinimumSize(new Dimension(334, 226));
        tabbedPane1.addTab("BEHAVIOR", panel9);
        behavior2Panel = new JPanel();
        behavior2Panel.setLayout(new GridBagLayout());
        panel9.add(behavior2Panel);
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
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("HOTKEYS", panel10);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane1.setAutoscrolls(false);
        scrollPane1.setVerticalScrollBarPolicy(20);
        panel11.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane1.setViewportView(hotkeyPanel);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 3, new Insets(10, 10, 0, 10), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        searchBarTF = new JTextField();
        searchBarTF.setMargin(new Insets(2, 9, 2, 6));
        panel12.add(searchBarTF, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setOpaque(true);
        label19.setText("Search:");
        panel12.add(label19, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hasHotkeyCB.setText("Has hotkey");
        panel12.add(hasHotkeyCB, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 10, 0), -1, -1));
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 5, new Insets(10, 0, 0, 10), -1, -1));
        bottomPanel.add(panel13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel13.add(buttonOK, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel13.add(buttonCancel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        restoreDefaultsButton = new JButton();
        restoreDefaultsButton.setText("Restore defaults");
        panel13.add(restoreDefaultsButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importButton = new JButton();
        importButton.setText("Import");
        panel13.add(importButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportButton = new JButton();
        exportButton.setText("Export");
        panel13.add(exportButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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

        gridDensitySlider = new JSlider(0, 20);

        scrollPane1 = new JScrollPane();
        scrollPane1.getVerticalScrollBar().setUnitIncrement(16);

        hotkeyCategoryMap = new LinkedHashMap<>();
        categoryHeaderList = new ArrayList<>();
        searchPhrases = new ArrayList<>();
        hasHotkeyCB = new JCheckBox();
        allData = new ArrayList<>();
        readCSV();
        loadHeaders(allData); // Extract headers
        updateList(allData); // Extract Data excluding headers

        hotkeyPanel = new JPanel();
        hotkeyPanel.setLayout(new GridLayoutManager(hotkeyCategoryMap.size() + 1, 2, new Insets(10, 10, 0, 10), -1, -1));
    }
}
