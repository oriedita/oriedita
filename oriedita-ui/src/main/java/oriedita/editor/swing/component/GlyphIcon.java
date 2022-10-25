package oriedita.editor.swing.component;

import oriedita.editor.Colors;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Icon displaying a single Glyph, defined by the glyph field.
 */
public class GlyphIcon implements Icon, PropertyChangeListener {
    private final String glyph;
    private final Font font;
    private final int width;
    private final int offset;

    private Color color;

    public GlyphIcon(String glyph, Color color) {
        this.glyph = glyph;
        this.color = color;
        font = new Font("Icons", Font.PLAIN, 21);

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, this.glyph);
        Rectangle2D box = gv.getPixelBounds(frc, 0, getIconHeight());

        offset = (int) box.getX();
        width = (int) box.getWidth();
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;

        Font originalFont = g2.getFont();
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2.setColor(Colors.get(color));
        g2.drawString(glyph, x - offset, y + getIconHeight());
        g2.setFont(originalFont);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return 21;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() != null && evt.getPropertyName().equals("foreground")) {
            setColor((Color) evt.getNewValue());
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
