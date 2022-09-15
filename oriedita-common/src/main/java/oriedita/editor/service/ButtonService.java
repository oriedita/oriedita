package oriedita.editor.service;

import javax.swing.*;
import java.util.Map;

public interface ButtonService {
    void registerLabel(JLabel label, String key);
    void registerButton(AbstractButton button, String key);
    void Button_shared_operation();
    Map<KeyStroke, AbstractButton> getHelpInputMap();
}
