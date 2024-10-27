package oriedita.editor.service;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import java.awt.Container;
import java.beans.PropertyChangeListener;
import java.util.Map;

public interface ButtonService {

    void setIcon(JLabel label, String key);

    void registerButton(AbstractButton button, String key);

    void registerButton(AbstractButton button, String key, boolean replaceUnderscoresInMenus);

    void loadAllKeyStrokes();

    void Button_shared_operation(boolean resetLineStep);

    default void Button_shared_operation() {
        Button_shared_operation(true);
    }

    Map<KeyStroke, AbstractButton> getHelpInputMap();

    String getActionFromKeystroke(KeyStroke stroke);

    void addDefaultListener(Container root);

    void addDefaultListener(Container root, boolean replaceUnderscoresInMenus);

    void setKeyStroke(KeyStroke keyStroke, String key);

    void addKeystrokeChangeListener(PropertyChangeListener listener);

    void removeKeystrokeChangeListener(PropertyChangeListener listener);

    void registerTextField(JTextComponent textField, String key);

    void removeAllKeyBinds();
}
