package origami_editor.tools;

import javax.swing.*;

public class KeyStrokeUtil {
    public static String toString(KeyStroke keyStroke) {
        if (keyStroke == null) {
            return "";
        }

        return keyStroke.toString()
                .replaceAll("pressed ", "")
                .replaceAll(" ", "+");
    }
}
