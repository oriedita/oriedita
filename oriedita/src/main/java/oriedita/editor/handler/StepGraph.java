package oriedita.editor.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StepGraph<T extends Enum<T>> {
    private final Map<T, StepNode<T>> nodes;
    private StepNode<T> currentNode;
    private T currentStep;

    public StepGraph(T step, Runnable moveAction, Runnable pressAction, Runnable dragAction, Supplier<T> releaseAction) {
        this.currentNode = new StepNode<>(step, moveAction, pressAction, dragAction, releaseAction);
        this.currentStep = step;
        this.nodes = new HashMap<>();
        nodes.put(currentStep, currentNode);
    }

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

    public void runCurrentMoveAction() {
        if (currentNode == null) return;
        currentNode.runHighlightSelection();
    }

    public void runCurrentPressAction() {
        if (currentNode == null) return;
        currentNode.runPressAction();
    }

    public void runCurrentDragAction() {
        if (currentNode == null) return;
        currentNode.runDragAction();
    }

    public void runCurrentReleaseAction(){
        if (currentNode == null) return;
        currentStep = currentNode.runReleaseAction();
        if(currentStep == null) throw new RuntimeException ("Returned step value is null. Returning...");
        currentNode = nodes.get(currentStep);
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
