package oriedita.editor.handler;

import origami.crease_pattern.element.Point;

import java.util.HashMap;
import java.util.Map;

public class StepGraph<T extends Enum<T>> {
    private final Map<T, StepNode<T>> nodes;
    private StepNode<T> currentNode;
    private T currentStep;

    public StepGraph(T step) {
        this.nodes = new HashMap<>();
        this.currentStep = step;
    }

    public void addNode(StepNode<T> node) {
        if(currentNode == null) currentNode = node;
        nodes.put(node.getStep(), node);
    }

    public void setCurrentStep(T step) {
        currentNode = nodes.get(step);
        currentStep = step;
    }
    public T getCurrentStep() { return currentStep; }

    public void runCurrentMoveAction(Point mousePos) {
        if (currentNode == null) return;
        currentNode.runHighlightSelection(mousePos);
    }

    public void runCurrentPressAction(Point mousePos) {
        if (currentNode == null) return;
        currentNode.runPressAction(mousePos);
    }

    public void runCurrentDragAction(Point mousePos) {
        if (currentNode == null) return;
        currentNode.runDragAction(mousePos);
    }

    public void runCurrentReleaseAction(Point mousePos){
        if (currentNode == null) return;
        currentStep = currentNode.runReleaseAction(mousePos);
        if(currentStep == null) throw new RuntimeException ("Returned step value is null. Returning...");
        currentNode = nodes.get(currentStep);
        currentNode.runHighlightSelection(mousePos);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\n{\n");
        for (Map.Entry<T, StepNode<T>> entry : nodes.entrySet()) {
            str.append("\t").append(entry.getKey()).append(": ").append(entry.getValue().toString()).append(",\n");
        }
        str.append("}");
        return str.toString();
    }
}
