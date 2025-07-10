package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.TEXT)
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
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    @Override
    public void mouseMoved(Point p0, MouseEvent e) {

        if (textModel.isSelected() && calculateBounds(textModel.getSelectedText()).contains(e.getPoint())) {
            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else {
            boolean changed = false;
            for (Text text : textWorker.getTexts()) {
                if (calculateBounds(text).contains(e.getPoint())) {
                    e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    changed = true;
                }
            }
            if (!changed) {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    @Override
    public void mousePressed(Point p0, MouseEvent e, int pressedButton) {
        super.mousePressed(p0);  // initializes box for dragging
        mouseButton = pressedButton;  // so we know which button it was in mouseReleased
        if (pressedButton == MouseEvent.BUTTON1) {
            mousePressed(p0);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        if (textModel.isSelected()) {
            if (!trySelectText(p0)) {
                textModel.setSelected(false);
                if (textModel.isDirty()) {
                    d.record();
                    textModel.markClean();
                }
            }
        } else {
            selectOrCreateText(p0);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean trySelectText(Point p0) {
        Text nearest = findNearest(p0);
        if (nearest != null) {
            if (textModel.isSelected() && textModel.getSelectedText() != nearest && textModel.isDirty()) {
                d.record(); // save the currently selected text before selecting the new one
                textModel.markClean();
            }
            textModel.setSelectedText(nearest);
            return true;
        }
        return false;
    }

    private void selectOrCreateText(Point p0) {

        Point p = d.getCamera().TV2object(p0);
        if (!trySelectText(p0)) {
            if (textModel.isSelected() && textModel.isDirty()) {
                d.record(); // save the currently selected text before creating the new one
                textModel.markClean();
            }
            Text t = new Text(p.getX(), p.getY(), "");
            textWorker.addText(t);
            textModel.setSelectedText(t);
        }
        textModel.setSelected(true);
    }

    private Text findNearest(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        double minDist = 100000000;
        Text nearest = null;
        for (Text text : textWorker.getTexts()) {
            Rectangle bounds = calculateBounds(text);
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
        Point p = d.getCamera().TV2object(p0);
        if (textModel.isSelected()) {
            if (selectionStart == null) {
                selectionStart = p0;
            } else {
                Point selStart = d.getCamera().TV2object(selectionStart);
                Text t = textModel.getSelectedText();
                t.setY(t.getY() + p.getY() - selStart.getY());
                t.setX(t.getX() + p.getX() - selStart.getX());
                textModel.markDirty();
                selectionStart = p0;
            }
        }
    }

    @Override
    public void mouseReleased(Point p0, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseReleased(p0);
        } else {
            if (selectionStart.distance(p0) > 2) {
                if (d.deleteInside_text(selectionStart, p0)) {
                    if (!d.getTextWorker().getTexts().contains(textModel.getSelectedText())) {
                        textModel.setSelected(false);
                    }
                    d.record();
                    textModel.markClean();
                }
            } else {
                Text nearest = findNearest(p0);
                if (nearest != null) {
                    textWorker.removeText(nearest);
                    if (textModel.getSelectedText() == nearest) {
                        textModel.setSelected(false);
                    }
                    d.record();
                    textModel.markClean();
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
            textModel.markClean();
        }
        super.mouseReleased(p0);
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (mouseButton == MouseEvent.BUTTON3) {
            super.drawPreview(g2, camera, settings);
        }
    }

    private Rectangle calculateBounds(Text text) {
        Point posCam = d.getCamera().object2TV(text.getPos());
        Rectangle bounds = text.calculateBounds();
        int selectionRadius = textModel.isSelected(text) ? 7 : 1; // make selection area bigger if editing ui is visible on this text
        bounds.setLocation((int) posCam.getX() - 3 - selectionRadius, (int) posCam.getY() - 10 - selectionRadius);
        bounds.setSize(bounds.width + 8 + selectionRadius * 5, bounds.height + 10 + selectionRadius * 5);
        return bounds;
    }
}
