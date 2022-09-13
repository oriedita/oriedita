package oriedita.editor.action2;

public interface OrieditaAction extends javax.swing.Action {
    default ActionType getActionType() {
        ActionHandler annotation = getClass().getAnnotation(ActionHandler.class);

        if (annotation == null){
            throw new IllegalStateException("MouseHandler does not have an @Handles annotation: " + getClass());
        }

        return annotation.value();
    }
}
