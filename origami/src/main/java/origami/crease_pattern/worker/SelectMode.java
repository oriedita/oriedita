package origami.crease_pattern.worker;

public enum SelectMode {
    SELECT(2),
    UNSELECT(0);

    final int mode;

    SelectMode(int mode) { this.mode = mode; }

    public int getMode() { return mode; }
}
