package oriedita.editor.handler.step;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.handler.Handles;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.Polygon;

import java.awt.Rectangle;
import java.util.EnumSet;

@ApplicationScoped
@Handles(MouseMode.TEXT)
public class MouseHandlerStepText extends StepMouseHandler<MouseHandlerStepText.Step> {

    @Inject
    private SelectedTextModel textModel;
    @Inject
    private TextWorker textWorker;

    private Point selectionStart;

    public MouseHandlerStepText() {
        super();
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1, Feature.BUTTON_3);
    }

    protected StepGraph<Step> initStepGraph() {
        var steps = new StepGraph<>(Step.CHOOSE);
        steps.addNode(ObjCoordStepNode.createNode_M_P(Step.CHOOSE,
                (p) -> {},
                (p, b) -> b == Feature.BUTTON_1? Step.CREATE_OR_MOVE : Step.DELETE));
        steps.addNode(ObjCoordStepNode.createNode(Step.CREATE_OR_MOVE,
                p -> {},
                this::createPressed,
                this::createDragged,
                p -> Step.CHOOSE));
        steps.addNode(BoxSelectStepNode.createNode(Step.DELETE, this::deleteReleased));
        return steps;
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

    private Step deleteReleased(Polygon p) {
        Point p0 = d.getCamera().object2TV(p.get(0));
        Point p1 = d.getCamera().object2TV(p.get(2));
        if (p0.distance(p1) > 2) {
            if (d.deleteInside_text(p0, p1)) {
                if (!d.getTextWorker().getTexts().contains(textModel.getSelectedText())) {
                    textModel.setSelected(false);
                }
                d.record();
                textModel.markClean();
            }
        } else {
            Text nearest = findNearest(p1);
            if (nearest != null) {
                textWorker.removeText(nearest);
                if (textModel.getSelectedText() == nearest) {
                    textModel.setSelected(false);
                }
                d.record();
                textModel.markClean();
            }
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
