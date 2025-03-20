package oriedita.editor.handler;

import java.util.HashMap;
import java.util.Map;

 public class StepNode {
     private final Runnable action;
     Map<Enum<?>, StepNode> nextNodes;

     public StepNode(Runnable action) {
         this.action = action;
         this.nextNodes = new HashMap<>();
     }

     public void addNext(Enum<?> step, StepNode stepNode) {
         nextNodes.put(step, stepNode);
     }

     public void run() { action.run(); }
}
