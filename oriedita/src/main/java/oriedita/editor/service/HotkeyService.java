package oriedita.editor.service;

import oriedita.editor.databinding.HotkeyModel;
import oriedita.editor.swing.action.AddKeyBindAction;
import oriedita.editor.tools.ResourceUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Singleton
public class HotkeyService {
    private final JFrame owner;
    private final HotkeyModel hotkeyModel;

    public Map<KeyStroke, AbstractButton> helpInputMap = new HashMap<>();

    @Inject
    public HotkeyService(@Named("mainFrame") JFrame owner, HotkeyModel hotkeyModel) {
        this.owner = owner;
        this.hotkeyModel = hotkeyModel;

        hotkeyModel.addPropertyChangeListener(this::handleHotkeyChangeEvent);
    }

    private void handleHotkeyChangeEvent(PropertyChangeEvent e) {
        Object newValue = e.getNewValue();
        Object oldValue = e.getOldValue();
        String key = e.getPropertyName();

        InputMap inputMap = owner.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        if (oldValue instanceof KeyStroke) {
            inputMap.remove((KeyStroke) e.getOldValue());
        }

        if (key != null && newValue instanceof KeyStroke) {
            inputMap.put((KeyStroke) newValue, key);
        }

        if (key != null) {
            ResourceUtil.updateBundleKey("hotkey", key, newValue == null ? null : newValue.toString());
        }
    }

    public void init() {
        ResourceBundle hotkeyBundle = ResourceUtil.getDefaultBundle("hotkey");
        Map<String, KeyStroke> hotkeyMap = new HashMap<>();

        for (Enumeration<String> e = hotkeyBundle.getKeys(); e.hasMoreElements();) {
            String key = e.nextElement();
            hotkeyMap.put(key, KeyStroke.getKeyStroke(hotkeyBundle.getString(key)));
        }

        hotkeyModel.initHotkeys(hotkeyMap);
    }

    public Action getAddKeybindAction(JFrame owner, String key) {
        return new AddKeyBindAction(owner, key, hotkeyModel);
    }

    public void addKeyStroke(KeyStroke keyStroke, String key) {
        hotkeyModel.setHotkey(key, keyStroke);
    }
}
