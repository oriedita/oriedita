package oriedita.editor.handler;

public class StepNode {
     private final Runnable action;

     public StepNode(Runnable action) { this.action = action; }

     public void run() { action.run(); }
}
