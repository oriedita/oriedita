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
            }
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = d.camera.TV2object(p0);
        Text nearest = findNearest(p0);
        if (nearest == null) {
            Text t = new Text(p.getX(), p.getY(), "Test");
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
        double minDist = 1000000;
        Text nearest = null;
        for (Text text : textWorker.getTexts()) {
            Point posCam = d.camera.object2TV(text.getPos());
            Rectangle bounds = text.calculateBounds();
            bounds.setLocation((int) posCam.getX(), (int) posCam.getY()-bounds.height);
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

    }

    @Override
    public void mouseReleased(Point p0) {

    }
}
