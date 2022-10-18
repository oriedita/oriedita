package oriedita.editor.action2;

public interface OrieditaAction extends javax.swing.Action {
    default ActionType getActionType() {
        ActionHandler annotation = getClass().getAnnotation(ActionHandler.class);

        if (annotation == null){
            throw new IllegalStateException("Action does not have an @ActionHandler annotation: " + getClass());
        }

        return annotation.value();
    }
}
