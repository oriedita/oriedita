package oriedita.editor;


public enum ToolTab {
    DRAW, REFERENCE, FOLD, SETTINGS;

    public static ToolTab fromIndex(int index) {
        return switch (index) {
            case 0 -> DRAW;
            case 1 -> REFERENCE;
            case 2 -> FOLD;
            case 3 -> SETTINGS;
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }

    public int toIndex() {
        return switch (this) {
            case DRAW -> 0;
            case REFERENCE -> 1;
            case FOLD -> 2;
            case SETTINGS -> 3;
        };
    }
}