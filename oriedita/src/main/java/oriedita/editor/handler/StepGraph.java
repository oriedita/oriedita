package oriedita.editor.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StepGraph<T extends Enum<T>> {
    private final Map<T, StepNode<T>> nodes;
    private StepNode<T> currentNode;
    private T currentStep;

    public StepGraph(T step, Supplier<T> action) {
        this.currentNode = new StepNode<>(action);
        this.currentStep = step;
        this.nodes = new HashMap<>();
        nodes.put(currentStep, currentNode);
    }

    public T getCurrentStep() { return currentStep; }

    public void addNode(T step, Supplier<T> action) {
        nodes.put(step, new StepNode<>(action));
    }

    public void connectNodes(T from, T to) {
        if (!(nodes.containsKey(from) && nodes.containsKey(to))) return;
        nodes.get(from).addNext(to, nodes.get(to));
    }

    public void runCurrentAction() {
        T step = currentNode.run();
        if (currentNode == null) return;
        if (!currentNode.nextNodes.containsKey(step)) return;
        currentNode = currentNode.nextNodes.get(step);
        currentStep = step;
    }
}
