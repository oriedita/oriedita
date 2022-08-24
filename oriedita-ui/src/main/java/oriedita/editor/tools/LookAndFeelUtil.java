package oriedita.editor.tools;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class LookAndFeelUtil {
    public static boolean determineLafDark(String laf) {
        try {
            Class<?> lnfClass = Class.forName(laf);

            LookAndFeel lookAndFeel = (LookAndFeel) lnfClass.getDeclaredConstructor().newInstance();
            return lookAndFeel instanceof FlatLaf && ((FlatLaf) lookAndFeel).isDark();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            return false;
        }
    }

    public static String determineLafForDarkMode(boolean darkMode) {
        if (darkMode) {
            return FlatDarkLaf.class.getName();
        } else {
            return FlatLightLaf.class.getName();
        }
    }
}
