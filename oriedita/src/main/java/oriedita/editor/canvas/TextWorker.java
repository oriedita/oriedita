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
        for (Text text : texts) {
            text.setGraphics(g2);
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
}
