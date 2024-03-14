package oriedita.editor.canvas;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.save.TextSave;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows displaying text on the canvas
 */
@ApplicationScoped
public class TextWorker {
    private final List<Text> texts;

    @Inject
    public TextWorker() {
        this.texts = new ArrayList<>();
    }

    public void draw(Graphics2D g2, Camera camera) {
        Text.setGraphics(g2);
        for (Text text : texts) {
            Point textPos = camera.object2TV(text.getPos());
            int height = g2.getFontMetrics().getHeight();
            int textY = (int) textPos.getY();

            for (String line : text.getText().split("\n")) {
                g2.drawString(line, (int) textPos.getX(), textY);
                textY += height;
            }
        }
    }

    public void getSave(TextSave save) {
        texts.forEach(t -> save.addText(new Text(t)));
    }

    public void setSave(TextSave memo1) {
        texts.clear();
        texts.addAll(memo1.getTexts());
    }

    public void addText(Text text) {
        this.texts.add(text);
    }

    public List<Text> getTexts() {
        return this.texts;
    }

    public void removeText(Text text) {
        this.getTexts().remove(text);
    }

    /**
     * Deletes all texts whose bounding boxes are (at least partially) contained inside the Rectangle spanned by pa and pb.
     *
     * @param pa     one corner of the deletion box (in canvas coordinates)
     * @param pb     opposite corner to pa of the deletion box (in canvas coordinates)
     * @param camera current camera, to account for zoom level
     * @return true if any text was deleted, false otherwise
     */
    public boolean deleteInsideRectangle(Point pa, Point pb, Camera camera) {
        boolean changed = false;

        // Text does not rotate with the camera, so we can always assume both the bounding boxes of the text, and the
        // Selection box are rectangles whose sides are parallel to the x/y axes
        if (pa.getX() > pb.getX()) {
            double tmp = pa.getX();
            pa = pa.withX(pb.getX());
            pb = pb.withX(tmp);
        }
        if (pa.getY() > pb.getY()) {
            double tmp = pa.getY();
            pa = pa.withY(pb.getY());
            pb = pb.withY(tmp);
        }
        List<Text> toRemove = new ArrayList<>();

        for (Text text : texts) {
            Rectangle r = text.calculateBounds();
            Point p1 = camera.object2TV(text.getPos());
            r.setLocation((int) p1.getX(), (int) p1.getY());
            Rectangle selection = new Rectangle((int) pa.getX(), (int) pa.getY(), (int) (pb.getX() - pa.getX()), (int) (pb.getY() - pa.getY()));

            if (selection.contains(r) || selection.intersects(r) || r.contains(selection)) {
                changed = true;
                toRemove.add(text);
            }
        }
        texts.removeAll(toRemove);

        return changed;
    }
}
