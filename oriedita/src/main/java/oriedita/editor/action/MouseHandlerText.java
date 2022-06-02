package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

@Singleton
public class MouseHandlerText extends BaseMouseHandler{
    private final SelectedTextModel textModel;
    private final TextWorker textWorker;

    private Point dragStart;
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
        if (e.getButton() == MouseEvent.BUTTON1) {
            mousePressed(p0);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
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

    @Override
    public void mousePressed(Point p0) {
        dragStart = p0;
        Point p = d.camera.TV2object(p0);
        Text nearest = findNearest(p0);
        if (nearest == null) {
            Text t = new Text(p.getX(), p.getY(), "");
            textWorker.addText(t);
            textModel.setSelectedText(t);
            textModel.setSelected(true);
        } else {
            textModel.setSelectedText(nearest);
            textModel.setSelected(true);
        }
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
    public void mouseDragged(Point p0) {
        Point p = d.camera.TV2object(p0);
        Point p2 = d.camera.TV2object(dragStart);
        dragStart = p0;
        Text t = textModel.getSelectedText();
        t.setY(t.getY() + p.getY() - p2.getY());
        t.setX(t.getX() + p.getX() - p2.getX());
        textModel.markDirty();
    }

    @Override
    public void mouseReleased(Point p0) {
        if (dragStart != null) {
            d.record();
        }
        dragStart = null;
    }
}
