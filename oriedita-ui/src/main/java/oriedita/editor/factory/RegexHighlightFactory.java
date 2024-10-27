package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.Colors;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.regex.Pattern;

@ApplicationScoped
public class RegexHighlightFactory {
    /**
     * A DocumentListener custom adapter that highlights an error color when the current string value isn't matching a given regular expression. Purely visual.
     * @param tf JTextField component
     * @param regex regular expression string
     * @return DocumentListener
     *
     * @see javax.swing.event.DocumentListener
     */
    public static DocumentListener genericRegexAdapter(JTextField tf, String regex){
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { keyPressed(); }

            @Override
            public void removeUpdate(DocumentEvent e) { keyPressed(); }

            @Override
            public void changedUpdate(DocumentEvent e) { keyPressed(); }

            public void keyPressed() {
                tf.setBackground(ifMatchRegex() ? UIManager.getColor("TextField.background") : Colors.get(Colors.INVALID_INPUT));
            }

            public boolean ifMatchRegex() {
                return Pattern.compile(regex).matcher(tf.getText()).matches();
            }
        };
    }

    public static DocumentListener intRegexAdapter(JTextField tf){
        return genericRegexAdapter(tf, "^-?\\d+(E-?\\d+)?$");
    }

    public static DocumentListener doubleRegexAdapter(JTextField tf){
        return genericRegexAdapter(tf, "^-?\\d+(\\.\\d+)?(E-?\\d+)?$");
    }

    public static DocumentListener hexColorRegexAdapter(JTextField tf){
        return genericRegexAdapter(tf, "^([a-fA-F0-9]){6}$");
    }
}
