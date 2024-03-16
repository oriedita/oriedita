package oriedita.editor.action;

public interface OrieditaAction extends javax.swing.Action {
    default boolean resetLineStep() {
        return true;
    }
}
