package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.TEXT)
public class MouseHandlerText extends StepMouseHandler<MouseHandlerText.Step> {

    @Inject
    private SelectedTextModel textModel;
    @Inject
    private TextWorker textWorker;
    @Inject
    private CanvasModel canvasModel;

    private Point selectionStart;

    public MouseHandlerText() {
        super();
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    protected StepGraph<Step> initStepGraph(StepFactory sf) {
        var steps = new StepGraph<>(Step.CHOOSE);
        steps.addNode(sf.createSwitchNode(Step.CHOOSE,
                this::updateCursor,
                (b) -> b == Feature.BUTTON_1? Step.CREATE_OR_MOVE : Step.DELETE));
        steps.addNode(sf.createNode(Step.CREATE_OR_MOVE,
                p -> {},
                this::createPressed,
                this::createDragged,
                p -> Step.CHOOSE));
        steps.addNode(sf.createBoxSelectNode(Step.DELETE, this::deleteReleasedBox, this::deleteReleased));
        return steps;
    }

    private void updateCursor(Point p){
        var p0 = d.getCamera().object2TV(p);
        var pp = new java.awt.Point((int) p0.getX(), (int) p0.getY());
        if (textModel.isSelected() && calculateBounds(textModel.getSelectedText()).contains(pp)) {
            canvasModel.setCursor(Cursor.MOVE_CURSOR);
        } else {
            boolean textCursor = false;
            for (Text text : textWorker.getTexts()) {
                if (calculateBounds(text).contains(pp)) {
                    canvasModel.setCursor(Cursor.TEXT_CURSOR);
                    textCursor = true;
                }
            }
            if (!textCursor) {
                canvasModel.setCursor(Cursor.getDefaultCursor().getType());
            }
        }
    }

    private void createPressed(Point p) {
        var p0 = d.getCamera().object2TV(p);
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
        selectionStart = p;
    }

    private void createDragged(Point p) {
        if (textModel.isSelected()) {
            Text t = textModel.getSelectedText();
            t.setY(t.getY() + p.getY() - selectionStart.getY());
            t.setX(t.getX() + p.getX() - selectionStart.getX());
            textModel.markDirty();
            selectionStart = p;
        }
    }

    private Step deleteReleasedBox(Polygon p) {
        Point p0 = d.getCamera().object2TV(p.get(0));
        Point p1 = d.getCamera().object2TV(p.get(2));
        if (d.deleteInside_text(p0, p1)) {
            if (!d.getTextWorker().getTexts().contains(textModel.getSelectedText())) {
                textModel.setSelected(false);
            }
            d.record();
            textModel.markClean();
        }
        return Step.CHOOSE;
    }

    private Step deleteReleased(Point p) {
        Point p1 = d.getCamera().object2TV(p);
        Text nearest = findNearest(p1);
        if (nearest != null) {
            textWorker.removeText(nearest);
            if (textModel.getSelectedText() == nearest) {
                textModel.setSelected(false);
            }
            d.record();
            textModel.markClean();
        }
        return Step.CHOOSE;
    }

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

    private Rectangle calculateBounds(Text text) {
        Point posCam = d.getCamera().object2TV(text.getPos());
        Rectangle bounds = text.calculateBounds();
        int selectionRadius = textModel.isSelected(text) ? 7 : 1; // make selection area bigger if editing ui is visible on this text
        bounds.setLocation((int) posCam.getX() - 3 - selectionRadius, (int) posCam.getY() - 10 - selectionRadius);
        bounds.setSize(bounds.width + 8 + selectionRadius * 5, bounds.height + 10 + selectionRadius * 5);
        return bounds;
    }

    @Override
    public void reset(){
        selectionStart = null;
    }

    public enum Step{
        CHOOSE,
        CREATE_OR_MOVE,
        DELETE,
    }
}
