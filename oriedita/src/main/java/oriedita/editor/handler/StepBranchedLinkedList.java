package oriedita.editor.handler;

import java.util.HashMap;
import java.util.Map;

public class StepBranchedLinkedList<T extends Enum<T>> {
    private final Map<T, StepNode<T>> nodes;
    private StepNode<T> currentNode;
    private T currentStep;

    public StepBranchedLinkedList(T step, Runnable action) {
        this.currentNode = new StepNode<>(action);
        this.currentStep = step;
        this.nodes = new HashMap<>();
        nodes.put(currentStep, currentNode);
    }

    public T getCurrentStep() { return currentStep; }

    public void runCurrentAction() { currentNode.run(); }

    public void addNode(T step, Runnable action) {
        nodes.put(step, new StepNode<T>(action));
    }

    public void connectNodes(T from, T to) {
        if (!(nodes.containsKey(from) && nodes.containsKey(to))) return;
        nodes.get(from).addNext(to, nodes.get(to));
    }

    public void moveForward(T step) {
        if (currentNode == null) return;
        if (!currentNode.nextNodes.containsKey(step)) return;
        currentNode = currentNode.nextNodes.get(step);
        currentStep = step;
    }

    public void moveForwardAndRun(T step) {
        moveForward(step);
        runCurrentAction();
    }
}
