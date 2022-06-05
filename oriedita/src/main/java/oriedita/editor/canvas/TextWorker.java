package oriedita.editor.canvas;

import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.save.TextSave;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class TextWorker {
    private final List<Text> texts;

    @Inject
    public TextWorker() {
        this.texts = new ArrayList<>();
    }

    public void draw(Graphics2D g2, Camera camera) {
        Text.setGraphics(g2);
        for (Text text : texts) {
            Point pt = camera.object2TV(text.getPos());
            int height = g2.getFontMetrics().getHeight();
            int y = (int) pt.getY();
            for (String line : text.getText().split("\n")) {
                g2.drawString(line, (int) pt.getX(), y);
                y += height;
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

    public boolean deleteInsideRectangle(Point pa, Point pb, Camera camera) {
        boolean changed = false;
        List<Text> toRemove = new ArrayList<>();
        for (Text text : texts) {
            Rectangle r = text.calculateBounds();
            Point p1 = camera.object2TV(text.getPos());
            r.setLocation((int)p1.getX(), (int) p1.getY());
            if (pa.getX() > pb.getX()) {
                double tmp = pa.getX();
                pa.setX(pb.getX());
                pb.setX(tmp);
            }
            if (pa.getY() > pb.getY()) {
                double tmp = pa.getY();
                pa.setY(pb.getY());
                pb.setY(tmp);
            }
            Rectangle selection = new Rectangle((int) pa.getX(), (int) pa.getY(), (int)(pb.getX() - pa.getX()), (int)(pb.getY() - pa.getY()));
            if (selection.contains(r) || selection.intersects(r) || r.contains(selection)) {
                changed = true;
                toRemove.add(text);
            }
        }
        texts.removeAll(toRemove);
        return changed;
    }
}
