package oriedita.editor.action;

import java.awt.event.ActionEvent;

public class LambdaAction extends AbstractOrieditaAction {
    private final Runnable actionMethod;

    public LambdaAction(Runnable actionMethod) {
        this.actionMethod = actionMethod;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionMethod.run();
    }
}
