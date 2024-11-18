package oriedita.editor.text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import origami.crease_pattern.element.Point;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

public class Text implements Serializable {
    private double x, y;
    private String text;
    private static Graphics g;

    @SuppressWarnings("unused") // Used for unit test
	private Text() {
        this(0, 0, "");
    }

    public Text(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public Text(Text t) {
        this.x = t.getX();
        this.y = t.getY();
        this.text = t.getText();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @JsonIgnore
    public static void setGraphics(Graphics g) {
        Text.g = g;
    }

    @JsonIgnore
    public Point getPos() {
        return new Point(getX(), getY());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Rectangle calculateBounds() {
        if (g == null) {
            return new Rectangle(25, 3);
        }
        int width = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            int newWidth = g.getFontMetrics().stringWidth(line);
            if (newWidth > width) {
                width = newWidth;
            }
        }
        int height = g.getFontMetrics().getHeight();
        return new Rectangle(width, (height) * (int) text.chars().filter(c -> c == '\n').count() + 1);
    }
}
