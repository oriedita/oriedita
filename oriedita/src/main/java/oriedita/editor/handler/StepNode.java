package oriedita.editor.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StepNode <T extends Enum<T>> {
     private final Supplier<T> action;
     Map<T, StepNode<T>> nextNodes;

     public StepNode(Supplier<T> action) {
         this.action = action;
         this.nextNodes = new HashMap<>();
     }

     public void addNext(T step, StepNode<T> stepNode) {
         nextNodes.put(step, stepNode);
     }

     public T run() { return action.get(); }
}
