package oriedita.editor.action2;

public enum ActionType {
    lineWidthDecreaseAction("lineWidthDecreaseAction"),
    importAction("importAction"),
    importAddAction("inputDataAction"),
    drawCreaseFreeAction("drawCreaseFreeAction"),
    ;

    private final String key;

    public String getKey() {
        return key;
    }

    ActionType(String key) {
        this.key = key;
    }
}
