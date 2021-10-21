package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Colors {
    private static final Map<Color, Color> colorMap;
    private static final Map<Color, Color> darkColorMap;

    static {
        colorMap = new HashMap<>();
        darkColorMap = new HashMap<>();

        add(Color.black, Color.black, new Color(176, 176, 176));
        add(Color.white, Color.white, new Color(37, 37, 37));
        add(Color.red, Color.red, new Color(229, 115, 115));
        add(Color.blue, Color.blue, new Color(33, 150, 243));
        add(Color.cyan, Color.cyan, new Color(0, 100, 100));
        add(Color.magenta, Color.magenta, new Color(100, 0, 100));
        add(Color.green, Color.green, new Color(0, 100, 0));
        add(new Color(255, 0, 0, 75), new Color(255, 0, 0, 75), new Color(255, 0, 0, 75));
        add(new Color(230, 230, 230), new Color(230, 230, 230), new Color(54, 54, 54));
    }

    private static void add(Color color, Color lightColor, Color darkColor) {
        colorMap.put(color, lightColor);
        darkColorMap.put(color, darkColor);
    }

    public static Color get(Color color) {
        return FlatLaf.isLafDark() ? darkColorMap.getOrDefault(color, color) : colorMap.getOrDefault(color, color);
    }
}
