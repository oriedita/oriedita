package oriedita.editor.handler;

import com.google.common.util.concurrent.ExecutionError;

import java.util.HashMap;
import java.util.Map;

public class StepCollection<T extends Enum<T>> {
    private final Map<T, StepNode> nodes;
    private T currentStep;

    public StepCollection(T step, Runnable action) {
        this.currentStep = step;
        this.nodes = new HashMap<>();
        nodes.put(currentStep, new StepNode(action));
    }

    public T getCurrentStep() { return currentStep; }

    public void setCurrentStep(T step) { currentStep = step; }

    public void addNode(T step, Runnable action) {
        nodes.put(step, new StepNode(action));
    }

    public void connectNodes(T from, T to) {
    }

    public void runAction(T step) {
        if (nodes.get(step) == null) {
            throw new ExecutionError(new Error("currentNode doesn't exist."));
        }
        nodes.get(step).run();
    }

    public void runCurrentAction() {
        runAction(currentStep);
    }
}
