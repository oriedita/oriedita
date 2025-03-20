package oriedita.editor.handler;

import java.util.HashMap;
import java.util.Map;

 public class StepNode <T extends Enum<T>> {
     private final Runnable action;
     Map<T, StepNode<T>> nextNodes;

     public StepNode(Runnable action) {
         this.action = action;
         this.nextNodes = new HashMap<>();
     }

     public void addNext(T step, StepNode<T> stepNode) {
         nextNodes.put(step, stepNode);
     }

     public void run() { action.run(); }
}
