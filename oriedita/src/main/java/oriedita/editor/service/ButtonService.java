package oriedita.editor.service;

import oriedita.editor.action.MouseHandlerVoronoiCreate;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.HotkeyModel;
import oriedita.editor.swing.component.GlyphIcon;
import oriedita.editor.swing.dialog.HelpDialog;
import oriedita.editor.tools.KeyStrokeUtil;
import oriedita.editor.tools.ResourceUtil;
import oriedita.editor.tools.StringOp;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Singleton
public class ButtonService {
    private final HotkeyService hotkeyService;
    private final HotkeyModel hotkeyModel;
    private final HelpDialog explanation;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final JFrame owner;
    private final MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate;
    private final CanvasModel canvasModel;

    @Inject
    public ButtonService(
            @Named("mainFrame") JFrame frame,
            HotkeyService hotkeyService,
            HotkeyModel hotkeyModel,
            HelpDialog explanation,
            CreasePattern_Worker mainCreasePatternWorker,
            MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate,
            CanvasModel canvasModel) {
        this.owner = frame;
        this.hotkeyService = hotkeyService;
        this.hotkeyModel = hotkeyModel;
        this.explanation = explanation;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.mouseHandlerVoronoiCreate = mouseHandlerVoronoiCreate;
        this.canvasModel = canvasModel;
    }


    public void setTooltip(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String tooltip = ResourceUtil.getBundleString("tooltip", key);
        // String help = ResourceUtil.getBundleString("help", key);

        KeyStroke keyStroke = hotkeyModel.getHotkey(key);

        String tooltipText = "<html>";
        if (!StringOp.isEmpty(name)) {
            tooltipText += "<i>" + name + "</i><br/>";
        }
        if (!StringOp.isEmpty(tooltip)) {
            tooltipText += tooltip + "<br/>";
        }
        if (keyStroke != null) {
            tooltipText += "Hotkey: " + KeyStrokeUtil.toString(keyStroke) + "<br/>";
        }

        if (!tooltipText.equals("<html>")) {
            button.setToolTipText(tooltipText);
        }
    }

    public void registerLabel(JLabel label, String key) {
        String icon = ResourceUtil.getBundleString("icons", key);
        if (!StringOp.isEmpty(icon)) {
            GlyphIcon glyphIcon = new GlyphIcon(icon, label.getForeground());
            label.addPropertyChangeListener("foreground", glyphIcon);
            // Reset the text if there is no icon.
            if (label.getIcon() == null) {
                label.setText(null);
            }
            label.setIcon(glyphIcon);
        }
    }

    private void addKeyStroke(KeyStroke keyStroke, String key) {
        hotkeyService.addKeyStroke(keyStroke, key);
    }

    public void registerButton(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        // String tooltip = ResourceUtil.getBundleString("tooltip", key);
        String help = ResourceUtil.getBundleString("help", key);
        String icon = ResourceUtil.getBundleString("icons", key);

        KeyStroke keyStroke = hotkeyModel.getHotkey(key);

        // Update tooltip when hotkey changes.
        hotkeyModel.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName() == null || evt.getPropertyName().equals(key)) {
                setTooltip(button, key);
            }
        });

        setTooltip(button, key);

        if (button instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) button;

            if (!StringOp.isEmpty(name)) {
                int mnemonicIndex = name.indexOf('_');
                if (mnemonicIndex > -1) {
                    String formattedName = name.replaceAll("_", "");

                    menuItem.setText(formattedName);
                    menuItem.setMnemonic(formattedName.charAt(mnemonicIndex));
                    menuItem.setDisplayedMnemonicIndex(mnemonicIndex);
                } else {
                    menuItem.setText(name);
                }
            }

            if (keyStroke != null) {
                // Menu item can handle own accelerator (and shows a nice hint).
                menuItem.setAccelerator(keyStroke);
            }
        } else {
            KeyStrokeUtil.resetButton(button);

            addContextMenu(button, key, keyStroke);

            if (keyStroke != null) {
                addKeyStroke(keyStroke, key);
            }
            owner.getRootPane().getActionMap().put(key, new Click(button));

            if (!StringOp.isEmpty(icon)) {
                GlyphIcon glyphIcon = new GlyphIcon(icon, button.getForeground());
                button.addPropertyChangeListener("foreground", glyphIcon);
                // Reset the text if there is no icon.
                if (button.getIcon() == null) {
                    button.setText(null);
                }
                button.setIcon(glyphIcon);

                if (button instanceof JCheckBox) {
                    GlyphIcon selectedGlyphIcon = new GlyphIcon(String.valueOf((char)(icon.toCharArray()[0] + 1)), button.getForeground());
                    button.addPropertyChangeListener("foreground", selectedGlyphIcon);
                    button.setSelectedIcon(selectedGlyphIcon);
                }
            }
        }

        if (!StringOp.isEmpty(help)) {
            button.addActionListener(e -> {
                explanation.setExplanation(key);

                Button_shared_operation();
            });
        }
    }

    public void Button_shared_operation() {
        mainCreasePatternWorker.setDrawingStage(0);
        mainCreasePatternWorker.resetCircleStep();
        mouseHandlerVoronoiCreate.voronoiLineSet.clear();

        canvasModel.markDirty();
    }

    private void addContextMenu(AbstractButton button, String key, KeyStroke keyStroke) {
        JPopupMenu popup = new JPopupMenu();

        Action addKeybindAction = hotkeyService.getAddKeybindAction(owner, key);

        String actionName = "Change key stroke";
        if (keyStroke != null) {
            actionName += " (Current: " + KeyStrokeUtil.toString(keyStroke) + ")";
        }
        addKeybindAction.putValue(Action.NAME, actionName);
        popup.add(addKeybindAction);

        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });
    }

    private static class Click extends AbstractAction {
    private final AbstractButton button;

        public Click(AbstractButton button) {
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            button.doClick();
        }
    }
}
