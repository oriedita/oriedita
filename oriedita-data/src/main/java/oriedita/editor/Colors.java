package oriedita.editor;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Colors {
    public static final Color GRID_LINE_DARK = new Color(50, 50, 50);
    public static final Color GRID_SCALE_DARK = new Color(17, 75, 10);

    public static final Color GRID_LINE = new Color(230, 230, 230);
    public static final Color GRID_SCALE = new Color(180, 200, 180);

    public static final Color FIGURE_FRONT_DARK = new Color(129, 15, 94);
    public static final Color FIGURE_BACK_DARK = new Color(89, 89, 89);

    public static final Color FIGURE_FRONT = new Color(255, 255, 50);
    public static final Color FIGURE_BACK = new Color(233, 233, 233);

    public static final Color INVALID_INPUT = new Color(255, 153, 153);
    public static final Color INVALID_INPUT_DARK = new Color(160, 60, 80);

    private static final Map<Color, Color> colorMap;
    private static final Map<Color, Color> darkColorMap;

    static {
        colorMap = new HashMap<>();
        darkColorMap = new HashMap<>();

        add(Color.black, Color.black, new Color(210, 210, 210));
        add(Color.white, Color.white, new Color(37, 37, 37));
        add(Color.red, Color.red, new Color(229, 115, 115));
        add(Color.blue, Color.blue, new Color(33, 150, 243));
        add(Color.cyan, Color.cyan, new Color(0, 100, 100));
        add(Color.magenta, Color.magenta, new Color(100, 0, 100));
        add(Color.green, Color.green, new Color(0, 100, 0));
        add(new Color(150, 150, 150), new Color(150, 150, 150), new Color(50, 50, 50));
        add(new Color(255, 0, 0, 75), new Color(255, 0, 0, 75), new Color(255, 0, 0, 75));
        add(new Color(230, 230, 230), new Color(230, 230, 230), new Color(54, 54, 54));
        add(new Color(162, 162, 162), new Color(162, 162, 162), new Color(120, 120, 120)); //placeholder
        add(Color.gray, new Color(128,128,128,128), new Color(128,128,128,128));
        add(Color.yellow, Color.magenta, Color.yellow);
        add(INVALID_INPUT, INVALID_INPUT, INVALID_INPUT_DARK);
    }

    private static Map<Color, Color> activeColorMap = colorMap;

    private static void add(Color color, Color lightColor, Color darkColor) {
        colorMap.put(color, lightColor);
        darkColorMap.put(color, darkColor);
    }

    public static Color get(Color color) {
        return activeColorMap.getOrDefault(color, color);
    }

    public static void update(boolean isDark) {
        if (isDark) {
            activeColorMap = darkColorMap;
        } else {
            activeColorMap = colorMap;
        }
    }

    /**
     * Reverse lookup of colors. Find key in darkColorMap when going from light to dark, find value in darkColormap when going from dark to light.
     *
     * @param color  Current color
     * @param isDark The desired mode for this color to be in.
     * @return Restored color
     */
    public static Color restore(Color color, boolean isDark) {
        if (color == null) {
            return null;
        }

        if (isDark) {
            return darkColorMap.getOrDefault(color, color);
        }

        return darkColorMap.entrySet().stream().filter(entry -> color.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(color);
    }
}
