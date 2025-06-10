package oriedita.editor.swing.component;

import oriedita.editor.Colors;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class ColorIcon implements Icon {
    private final Color color;
    private final int width;
    private final int height;
    private final boolean border;

    public ColorIcon(Color color) {
        this.color = color;
        width = 14;
        height = 14;
        border = true;
    }

    public ColorIcon(Color color, int size, boolean border) {
        this.color = color;
        this.width = size;
        this.height = size;
        this.border = border;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
        if (border) {
            g.setColor(Colors.get(Color.black));
            g.drawRect(x, y, getIconWidth(), getIconHeight());
        }
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}
