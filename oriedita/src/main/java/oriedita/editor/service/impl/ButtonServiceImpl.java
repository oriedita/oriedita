package oriedita.editor.service.impl;

import org.tinylog.Logger;
import oriedita.editor.action.MouseHandlerVoronoiCreate;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.GlyphIcon;
import oriedita.editor.swing.dialog.HelpDialog;
import oriedita.editor.swing.dialog.SelectKeyStrokeDialog;
import oriedita.editor.tools.KeyStrokeUtil;
import oriedita.editor.tools.ResourceUtil;
import oriedita.editor.tools.StringOp;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ButtonServiceImpl implements ButtonService {
    private final HelpDialog explanation;
    private final CreasePattern_Worker mainCreasePatternWorker;
    public Map<KeyStroke, AbstractButton> helpInputMap = new HashMap<>();
    private JFrame owner;
    private final MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate;
    private final CanvasModel canvasModel;

    @Inject
    public ButtonServiceImpl(
            @Named("mainFrame") JFrame frame,
            HelpDialog explanation,
            CreasePattern_Worker mainCreasePatternWorker,
            MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate,
            CanvasModel canvasModel) {
        this.owner = frame;
        this.explanation = explanation;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.mouseHandlerVoronoiCreate = mouseHandlerVoronoiCreate;
        this.canvasModel = canvasModel;
    }

    @Override public void setOwner(JFrame owner) {
        this.owner = owner;
    }

    public void setTooltip(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String tooltip = ResourceUtil.getBundleString("tooltip", key);
        // String help = ResourceUtil.getBundleString("help", key);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);


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

    @Override public void registerLabel(JLabel label, String key) {
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

    private void addKeyStroke(KeyStroke keyStroke, AbstractButton button, String key) {
        helpInputMap.put(keyStroke, button);
        owner.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
    }

    @Override public void registerButton(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        // String tooltip = ResourceUtil.getBundleString("tooltip", key);
        String help = ResourceUtil.getBundleString("help", key);
        String icon = ResourceUtil.getBundleString("icons", key);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);

        if (!StringOp.isEmpty(keyStrokeString) && keyStroke == null) {
            Logger.error("Keystroke for \"" + key + "\": \"" + keyStrokeString + "\" is invalid");
        }

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
                addKeyStroke(keyStroke, button, key);
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

    @Override public void Button_shared_operation() {
        mainCreasePatternWorker.setDrawingStage(0);
        mainCreasePatternWorker.resetCircleStep();
        mouseHandlerVoronoiCreate.voronoiLineSet.clear();

        canvasModel.markDirty();
    }

    @Override
    public Map<KeyStroke, AbstractButton> getHelpInputMap() {
        return helpInputMap;
    }

    private void addContextMenu(AbstractButton button, String key, KeyStroke keyStroke) {
        JPopupMenu popup = new JPopupMenu();
        Action addKeybindAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputMap map = owner.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                KeyStroke stroke = null;
                for (KeyStroke keyStroke : map.keys()) {
                    if (map.get(keyStroke).equals(key)) {
                        stroke = keyStroke;
                    }
                }
                KeyStroke currentKeyStroke = stroke;

                new SelectKeyStrokeDialog(owner, button, helpInputMap, currentKeyStroke, newKeyStroke -> {
                    if (newKeyStroke != null && helpInputMap.containsKey(newKeyStroke) && helpInputMap.get(newKeyStroke) != button) {
                        String conflictingButton = (String) helpInputMap.get(newKeyStroke).getRootPane()
                                                                        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                                                                        .get(newKeyStroke);
                        JOptionPane.showMessageDialog(owner, "Conflicting KeyStroke! Conflicting with " + conflictingButton);
                        return false;
                    }

                    ResourceUtil.updateBundleKey("hotkey", key, newKeyStroke == null ? "" : newKeyStroke.toString());

                    helpInputMap.remove(currentKeyStroke);
                    owner.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(currentKeyStroke);

                    if (newKeyStroke != null) {
                        addKeyStroke(newKeyStroke, button, key);
                        putValue(Action.NAME, "Change key stroke (Current: " + KeyStrokeUtil.toString(newKeyStroke) + ")");
                    } else {
                        putValue(Action.NAME, "Change key stroke");
                    }

                    setTooltip(button, key);

                    return true;
                });
            }
        };
        String actionName = "Change key stroke";
        if (keyStroke != null) {
            actionName += " (Current: " + KeyStrokeUtil.toString(keyStroke) + ")";
        }
        addKeybindAction.putValue(Action.NAME, actionName);
        popup.add(addKeybindAction);

        Point point = new Point();

        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();

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

    public static class Click extends AbstractAction {
        private final AbstractButton button;

        public Click(AbstractButton button) {
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() instanceof JTextComponent)) {
                button.doClick();
            }
        }
    }
}
