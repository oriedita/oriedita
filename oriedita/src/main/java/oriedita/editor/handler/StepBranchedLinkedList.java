package oriedita.editor.handler;

import java.util.HashMap;
import java.util.Map;

public class StepBranchedLinkedList {
    private final Map<Enum<?>, StepNode> nodes;
    private StepNode currentNode;
    private Enum<?> currentStep;

    public StepBranchedLinkedList(Enum<?> step, Runnable action) {
        this.currentNode = new StepNode(action);
        this.currentStep = step;
        this.nodes = new HashMap<>();
        nodes.put(currentStep, currentNode);
    }

    public Enum<?> getCurrentStep() { return currentStep; }

    public void runCurrentAction() { currentNode.run(); }

    public void addNode(Enum<?> step, Runnable action) {
        nodes.put(step, new StepNode(action));
    }

    public void connectNodes(Enum<?> from, Enum<?> to) {
        if (!(nodes.containsKey(from) && nodes.containsKey(to))) return;
        nodes.get(from).addNext(to, nodes.get(to));
    }

    public void moveForward(Enum<?> step) {
        if (currentNode == null) return;
        if (!currentNode.nextNodes.containsKey(step)) return;
        currentNode = currentNode.nextNodes.get(step);
        currentStep = step;
    }

    public void moveForwardAndRun(Enum<?> step) {
        moveForward(step);
        runCurrentAction();
    }
}
