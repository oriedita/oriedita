package oriedita.editor.swing.component;


import oriedita.editor.Colors;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class GridColorIcon implements Icon {
    private final Color color1;
    private final Color color2;
    private final Color color3;
    private final Color color4;

    public GridColorIcon(Color colorNW, Color colorNE, Color colorSW, Color colorSE) {
        this.color1 = colorNW;
        this.color2 = colorNE;
        this.color3 = colorSW;
        this.color4 = colorSE;
    }

    public GridColorIcon(Color colorLeft, Color colorRight) {
        this(colorLeft, colorRight, colorLeft, colorRight);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color1);
        g.fillRect(x, y, getIconWidth()/2+1, getIconHeight()/2+1);
        g.setColor(color2);
        g.fillRect(x + 7, y, getIconWidth()/2, getIconHeight()/2+1);
        g.setColor(color3);
        g.fillRect(x, y + 7, getIconWidth()/2+1, getIconHeight()/2);
        g.setColor(color4);
        g.fillRect(x + 7, y + 7, getIconWidth()/2, getIconHeight()/2);
    }

    @Override
    public int getIconWidth() {
        return 14;
    }

    @Override
    public int getIconHeight() {
        return 14;
    }
}
