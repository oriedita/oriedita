package oriedita.editor.service.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.FrameProvider;
import oriedita.editor.action.ActionService;
import oriedita.editor.action.ActionType;
import oriedita.editor.action.OrieditaAction;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.component.DropdownToolButton;
import oriedita.editor.swing.component.GlyphIcon;
import oriedita.editor.swing.dialog.HelpDialog;
import oriedita.editor.swing.dialog.SelectKeyStrokeDialog;
import oriedita.editor.tools.KeyStrokeUtil;
import oriedita.editor.tools.ResourceUtil;
import oriedita.editor.tools.StringOp;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class ButtonServiceImpl implements ButtonService {
    private final SetMultimap<String, AbstractButton> registeredButtons;
    private final BiMap<String, KeyStroke> keystrokes;
    private final Map<AbstractButton, String> buttonKeys;

    private final FrameProvider owner;
    private final HelpDialog explanation;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final CanvasModel canvasModel;
    private final ActionService actionService;

    private final PropertyChangeSupport keystrokeChangeSupport = new PropertyChangeSupport(this);

    @Inject
    public ButtonServiceImpl(
            FrameProvider frame,
            HelpDialog explanation,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            CanvasModel canvasModel,
            ActionService actionService
    ) {
        this.owner = frame;
        this.explanation = explanation;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.canvasModel = canvasModel;
        this.actionService = actionService;
        registeredButtons = HashMultimap.create();
        keystrokes = HashBiMap.create();
        buttonKeys = new HashMap<>();
    }

    @Override
    public void setIcon(JLabel label, String key) {
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

    @Override
    public void registerButton(AbstractButton button, String key) {
        registerButton(button, key, true);
    }

    @Override
    public synchronized void registerButton(AbstractButton button, String key, boolean replaceUnderscoresInMenus) {
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);
        if (!StringOp.isEmpty(keyStrokeString) && keyStroke == null) {
            Logger.error("Keystroke for \"" + key + "\": \"" + keyStrokeString + "\" is invalid");
        }

        addButtonToRegisteredButtons(key, button);
        setAction(button, key);
        setTooltip(key);

        if (button instanceof JMenuItem menuItem) {
            registerJMenuItem(menuItem, key, replaceUnderscoresInMenus);
        } else {
            registerAbstractButton(button, key);
        }

        addHelpUpdater(button);
    }

    private void addHelpUpdater(AbstractButton button) {
        ActionListener explanationUpdater = e -> {
            String key = buttonKeys.get(button);
            explanation.setExplanation(key);
            Action action = button.getAction();
            if (action instanceof OrieditaAction oAction) {
                Button_shared_operation(oAction.resetLineStep());
            } else {
                Button_shared_operation(true);
            }
        };
        button.addActionListener(explanationUpdater);
    }
    public void registerTextField(JTextComponent textField, String key){
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                explanation.setExplanation(key);
            }
        });
    }

    @Override
    public void removeAllKeyBinds() {
        for (ActionType value : ActionType.values()) {
           removeKeyStroke(value.action());
        }
    }

    @Override
    public void loadAllKeyStrokes() {
        for (ActionType value : ActionType.values()) {
            loadKeyStroke(value.action());
        }
    }

    public void loadKeyStroke(String key) {
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);
        setKeyStroke(keyStroke, key);
    }

    private void registerAbstractButton(AbstractButton button, String key) {
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String icon = ResourceUtil.getBundleString("icons", key);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);

        if (button instanceof DropdownToolButton tb) {
            for (Component component : tb.getDropdownMenu().getComponents()) {
                if (component instanceof JMenuItem item) {
                    // Since these aren't in a proper JMenu, JMenuItem.setAccelerator is not enough
                    String itemKey = item.getActionCommand();
                    if (itemKey != null) {
                        setKeyStroke(item.getAccelerator(), itemKey);
                    }
                }
            }
        }
        KeyStrokeUtil.resetButton(button);

        addContextMenu(button, key);

        if (keyStroke != null && button instanceof JButton) {
            setKeyStroke(keyStroke, key);
        }

        if (!StringOp.isEmpty(icon)) {
            GlyphIcon glyphIcon = new GlyphIcon(icon, button.getForeground());
            button.addPropertyChangeListener("foreground", glyphIcon);
            // Reset the text if there is no icon.
            if (button.getIcon() == null) {
                button.setText(null);
            }
            button.setIcon(glyphIcon);

            if (button instanceof JCheckBox) {
                GlyphIcon selectedGlyphIcon = new GlyphIcon(String.valueOf((char) (icon.toCharArray()[0] + 1)), button.getForeground());
                button.addPropertyChangeListener("foreground", selectedGlyphIcon);
                button.setSelectedIcon(selectedGlyphIcon);
            }
        }
    }

    private void registerJMenuItem(JMenuItem menuItem, String key, boolean replaceUnderscoresInMenus) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String icon = ResourceUtil.getBundleString("icons", key);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);

        if (!StringOp.isEmpty(name) && replaceUnderscoresInMenus) {
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

        if (!StringOp.isEmpty(icon)) {
            GlyphIcon glyphIcon = new GlyphIcon(icon, menuItem.getForeground());
            menuItem.addPropertyChangeListener("foreground", glyphIcon);
            menuItem.setIcon(glyphIcon);
        }

        if (keyStroke != null) {
            // Menu item can handle own accelerator (and shows a nice hint).
            menuItem.setAccelerator(keyStroke);
        }
    }

    private void setAction(AbstractButton button, String key) {
        ActionType type = ActionType.fromAction(key);

        if (type != null && actionService != null) {
            String text = button.getText();

            Action action = actionService.getAllRegisteredActions().get(type);

            if (action != null) {
                button.setAction(action);
            } else {
                Logger.debug("No handler for {}", key);
            }

            button.setText(text); // setAction replaces the text with the action name, this is to keep the original text
        }  else {
            Logger.debug("No action found for {}", key);
        }
    }

    private void addButtonToRegisteredButtons(String key, AbstractButton button) {
        registeredButtons.put(key, button);
        buttonKeys.put(button, key);
    }

    @Override
    public void Button_shared_operation(boolean resetLineStep) {
        if (resetLineStep) {
            mainCreasePatternWorker.resetLineStep(0);
        }
        mainCreasePatternWorker.resetCircleStep();
        // TODO RESET VORONOI mouseHandlerVoronoiCreate.getVoronoiLineSet().clear();

        canvasModel.markDirty();
    }

    @Override
    public Map<KeyStroke, AbstractButton> getHelpInputMap() {
        //noinspection OptionalGetWithoutIsPresent
        return registeredButtons.asMap().entrySet().stream()
                .filter(e -> keystrokes.containsKey(e.getKey()) && keystrokes.get(e.getKey()) != null)
                .collect(Collectors.toMap(
                        e -> keystrokes.get(e.getKey()),
                        e -> e.getValue().stream().findFirst().get() // get is safe because MultiMap only contains
                                                                     // keys with at least one entry
                )
        );
    }

    @Override
    public void addDefaultListener(Container component) {
        addDefaultListener(component, true);
    }

    @Override
    public void addDefaultListener(Container root, boolean replaceUnderscoresInMenus) {
        Component[] components = root.getComponents();
        if (root instanceof DropdownToolButton tb) {
            addDefaultListener(tb.getDropdownMenu());
        }

        for (Component component1 : components) {
            if (component1 instanceof Container) {
                addDefaultListener((Container) component1);
            }

            if (component1 instanceof AbstractButton button) {
                String key = button.getActionCommand();

                if (key != null && !key.isEmpty()) {
                    registerButton(button, key, replaceUnderscoresInMenus);
                }
            }

            if (component1 instanceof JMenu) {
                for (MenuElement element : ((JMenu) component1).getSubElements()) {
                    if (element instanceof Container) {
                        addDefaultListener((Container) element);
                    }
                }
            }
        }
    }

    @Override
    public void setKeyStroke(KeyStroke keyStroke, String key) {
        KeyStroke oldValue = keystrokes.get(key);
        removeKeyStroke(key);
        if (keyStroke != null){
            keystrokes.put(key, keyStroke);
            if (!GraphicsEnvironment.isHeadless()) {
                addUIKeystroke(key, keyStroke);
            }
        }
        setTooltip(key);
        keystrokeChangeSupport.firePropertyChange(key, oldValue, keyStroke);
    }

    public String getActionFromKeystroke(KeyStroke stroke) {
        return keystrokes.inverse().get(stroke);
    }

    private void addContextMenu(AbstractButton button, String key) {
        JPopupMenu popup = new JPopupMenu();
        Action addKeybindAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyStroke currentKeyStroke = keystrokes.get(key);
                new SelectKeyStrokeDialog(owner.get(), key, ButtonServiceImpl.this, currentKeyStroke);
            }
        };
        String actionName = "Change key stroke";

        if (keystrokes.containsKey(key)) {
            actionName += " (Current: " + KeyStrokeUtil.toString(keystrokes.get(key)) + ")";
        }
        addKeybindAction.putValue(Action.NAME, actionName);
        popup.add(addKeybindAction);
        this.addKeystrokeChangeListener(e -> {
            if (Objects.equals(e.getPropertyName(), key)) {
                KeyStroke ks = (KeyStroke) e.getNewValue();
                if (ks != null) {
                    addKeybindAction.putValue(Action.NAME, "Change key stroke (Current: " + KeyStrokeUtil.toString(ks) + ")");
                } else {
                    addKeybindAction.putValue(Action.NAME, "Change key stroke");
                }
            }
        });
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

    private void addUIKeystroke(String key, KeyStroke keyStroke) {
        if (GraphicsEnvironment.isHeadless() || actionService == null) {
            Logger.warn("running in headless mode or testing, skipping ButtonServiceImpl.addUIKeystroke");
            return;
        }
        Action action = actionService.getAllRegisteredActions().get(ActionType.fromAction(key));
        if (action == null) {
            action = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() instanceof JTextComponent)) {
                        registeredButtons.get(key).stream().findFirst().ifPresentOrElse(
                                AbstractButton::doClick,
                                () -> Logger.error("Unknown action activated: " + key)
                        );
                    }
                }
            };
        } else {
            Action tempAction = action;
            action = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() instanceof JTextComponent)) {
                        tempAction.actionPerformed(e);
                    }
                }
            };
        }
        owner.get().getRootPane().getActionMap().put(key, action);
        owner.get().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
    }

    private void removeKeyStroke(String key) {
        if (!GraphicsEnvironment.isHeadless())
            owner.get().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(keystrokes.get(key));
        keystrokes.remove(key);
    }

    public void setTooltip(String key) {
        String name = ResourceUtil.getBundleString("name", key);
        String keyStrokeString = ResourceUtil.getBundleString("hotkey", key);
        String tooltip = ResourceUtil.getBundleString("tooltip", key);

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
            String finalTooltipText = tooltipText;
            registeredButtons.get(key).forEach(b -> b.setToolTipText(finalTooltipText));
        }
    }

    @Override
    public void addKeystrokeChangeListener(PropertyChangeListener listener) {
        keystrokeChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removeKeystrokeChangeListener(PropertyChangeListener listener) {
        keystrokeChangeSupport.removePropertyChangeListener(listener);
    }
}
