package oriedita.editor.swing.component;

import oriedita.editor.Colors;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class ColorIcon implements Icon {
    private final Color color;

    public ColorIcon(Color color) {
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
        g.setColor(Colors.get(Color.black));
        g.drawRect(x, y, getIconWidth(), getIconHeight());
    }

    @Override
    public int getIconWidth() {
        return 10;
    }

    @Override
    public int getIconHeight() {
        return 10;
    }
}
