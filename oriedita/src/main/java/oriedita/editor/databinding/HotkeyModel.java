package oriedita.editor.databinding;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.annotation.CheckForNull;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Keeps track of which hotkeys there are.
 */
@Singleton
public class HotkeyModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final Map<String, KeyStroke> hotkeyMap = new HashMap<>();

    private final Map<KeyStroke, String> inverseHotkeyMap = new HashMap<>();

    @Inject
    public HotkeyModel() {
    }

    public void initHotkeys(Map<String, KeyStroke> hotkeyMap) {
        this.inverseHotkeyMap.putAll(hotkeyMap.entrySet()
                .stream()
                .filter(e -> !Objects.isNull(e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
        this.hotkeyMap.putAll(hotkeyMap);
        this.pcs.firePropertyChange(null, null, null);
    }

    public void setHotkey(String key, KeyStroke hotkey) {
        KeyStroke oldHotkey = this.hotkeyMap.get(key);

        if (hotkey != null && this.inverseHotkeyMap.containsKey(hotkey)) {
            // overwrite another hotkey.
            String overwriteKey = this.inverseHotkeyMap.get(hotkey);

            this.hotkeyMap.put(overwriteKey, hotkey);

            this.pcs.firePropertyChange(overwriteKey, hotkey, null);
        }

        this.hotkeyMap.put(key, hotkey);

        if (hotkey == null) {
            this.inverseHotkeyMap.remove(oldHotkey);
        } else {
            this.inverseHotkeyMap.put(hotkey, key);
        }
        this.pcs.firePropertyChange(key, oldHotkey, hotkey);
    }

    public Map<String, KeyStroke> getAll() {
        return hotkeyMap;
    }

    public @CheckForNull KeyStroke getHotkey(String key) {
        return this.hotkeyMap.get(key);
    }

    public String getKey(KeyStroke keyStroke) {
        return this.inverseHotkeyMap.get(keyStroke);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
}
