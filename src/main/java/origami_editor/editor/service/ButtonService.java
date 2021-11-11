package origami_editor.editor.service;

import org.springframework.stereotype.Component;
import origami_editor.editor.HelpDialog;
import origami_editor.editor.SelectKeyStrokeDialog;
import origami_editor.editor.action.Click;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.canvas.MouseHandlerVoronoiCreate;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.tools.KeyStrokeUtil;
import origami_editor.tools.ResourceUtil;
import origami_editor.tools.StringOp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

@Component
public class ButtonService {
    private final HelpDialog explanation;
    private final CreasePattern_Worker mainCreasePatternWorker;
    public Map<KeyStroke, AbstractButton> helpInputMap = new HashMap<>();
    private Frame owner;
    private final MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate;
    private final CanvasModel canvasModel;

    public ButtonService(HelpDialog explanation, CreasePattern_Worker mainCreasePatternWorker, MouseHandlerVoronoiCreate mouseHandlerVoronoiCreate, CanvasModel canvasModel) {
        this.explanation = explanation;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.mouseHandlerVoronoiCreate = mouseHandlerVoronoiCreate;
        this.canvasModel = canvasModel;
    }

    public void setOwner(Frame owner) {
        this.owner = owner;
    }

    public void setTooltip(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String tooltip = ResourceUtil.getBundleString("tooltip", key);
        String help = ResourceUtil.getBundleString("help", key);

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

    public void registerButton(AbstractButton button, String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String tooltip = ResourceUtil.getBundleString("tooltip", key);
        String help = ResourceUtil.getBundleString("help", key);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);

        if (!StringOp.isEmpty(keyStrokeString) && keyStroke == null) {
            System.err.println("Keystroke for \"" + key + "\": \"" + keyStrokeString + "\" is invalid");
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
                helpInputMap.put(keyStroke, button);
                button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
            }
            button.getActionMap().put(key, new Click(button));
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
        Action addKeybindAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyStroke currentKeyStroke = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys() != null && button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys().length > 0
                        ? button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys()[0]
                        : null;

                new SelectKeyStrokeDialog(owner, button, helpInputMap, currentKeyStroke, newKeyStroke -> {
                    if (newKeyStroke != null && helpInputMap.containsKey(newKeyStroke) && helpInputMap.get(newKeyStroke) != button) {
                        String conflictingButton = (String) helpInputMap.get(newKeyStroke).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(newKeyStroke);
                        JOptionPane.showMessageDialog(owner, "Conflicting KeyStroke! Conflicting with " + conflictingButton);
                        return false;
                    }

                    ResourceUtil.updateBundleKey("hotkey", key, newKeyStroke == null ? null : newKeyStroke.toString());

                    helpInputMap.remove(currentKeyStroke);
                    button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(currentKeyStroke);

                    if (newKeyStroke != null) {
                        helpInputMap.put(newKeyStroke, button);
                        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(newKeyStroke, key);
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

        java.awt.Point point = new java.awt.Point();

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
}
