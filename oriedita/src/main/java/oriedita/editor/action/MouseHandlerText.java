package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

@Singleton
public class MouseHandlerText extends BaseMouseHandlerBoxSelect {
    private final SelectedTextModel textModel;
    private final TextWorker textWorker;

    private int mouseButton;

    @Inject
    public MouseHandlerText(SelectedTextModel textModel,
                            TextWorker textWorker) {
        this.textModel = textModel;
        this.textWorker = textWorker;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.TEXT;
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0, MouseEvent e) {
        super.mousePressed(p0);
        mouseButton = e.getButton();
        if (e.getButton() == MouseEvent.BUTTON1) {
            mousePressed(p0);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = d.camera.TV2object(p0);
        Text nearest = findNearest(p0);
        if (nearest == null) {
            if (textModel.isSelected()) {
                d.record(); // save the currently selected text before creating the new one
            }
            Text t = new Text(p.getX(), p.getY(), "");
            textWorker.addText(t);
            textModel.setSelectedText(t);
        } else {
            if (textModel.isSelected() && textModel.getSelectedText() != nearest) {
                d.record(); // save the currently selected text before selecting the new one
            }
            textModel.setSelectedText(nearest);
        }
        textModel.setSelected(true);
    }

    private Text findNearest(Point p0) {
        Point p = d.camera.TV2object(p0);
        double minDist = 100000000;
        Text nearest = null;
        for (Text text : textWorker.getTexts()) {
            Point posCam = d.camera.object2TV(text.getPos());
            Rectangle bounds = text.calculateBounds();
            int selectionRadius = text == textModel.getSelectedText()? 7 : 0;
            bounds.setLocation((int) posCam.getX()-3-selectionRadius, (int) posCam.getY()-10-selectionRadius);
            bounds.setSize(bounds.width+6+selectionRadius*2, bounds.height+11+selectionRadius*2);
            java.awt.Point p0Awt = new java.awt.Point((int) p0.getX(), (int) p0.getY());
            if (bounds.contains(p0Awt)) {
                if (p.distance(text.getPos()) < minDist) {
                    minDist = p.distance(text.getPos());
                    nearest = text;
                }
            }
        }
        return nearest;
    }

    @Override
    public void mouseDragged(Point p0, MouseEvent event) {
        if (mouseButton == MouseEvent.BUTTON1) {
            this.mouseDragged(p0);
        } else if (mouseButton == MouseEvent.BUTTON3) {
            super.mouseDragged(p0);
        }
    }

    @Override
    public void mouseDragged(Point p0) {
        Point p = d.camera.TV2object(p0);
        if (selectionStart != null) {
            Point p2 = d.camera.TV2object(selectionStart);
            if (textModel.isSelected()) {
                Text t = textModel.getSelectedText();
                t.setY(t.getY() + p.getY() - p2.getY());
                t.setX(t.getX() + p.getX() - p2.getX());
                textModel.markDirty();
            }
        }
        if (textModel.isSelected()) {
            selectionStart = p0;
        }
    }

    @Override
    public void mouseReleased(Point p0, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseReleased(p0);
        } else {
            if (selectionStart.distance(p0) > 2) {
                if (d.deleteInside_text(selectionStart, p0)) {
                    textModel.setSelected(false);
                    textModel.markDirty();
                    d.record();
                }
            } else {
                Text nearest = findNearest(p0);
                if (nearest != null) {
                    textWorker.removeText(nearest);
                    if (textModel.getSelectedText() == nearest) {
                        textModel.setSelected(false);
                    }
                    textModel.markDirty();
                    d.record();
                }
            }
        }
        mouseButton = 0;
        super.mouseReleased(p0);
    }

    @Override
    public void mouseReleased(Point p0) {
        if (selectionStart != null
            && selectionStart.distance(p0) > 2
            && !textModel.getSelectedText().getText().isEmpty()
        ) {
            d.record();
        }
        super.mouseReleased(p0);
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (mouseButton == MouseEvent.BUTTON3) {
            super.drawPreview(g2, camera, settings);
        }
    }
}
