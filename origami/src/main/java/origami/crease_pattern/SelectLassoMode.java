package origami.crease_pattern;

public enum SelectLassoMode {
    INTERSECT(0),
    CONTAIN(1);

    private final int mode;

    SelectLassoMode(int mode) {
        this.mode = mode;
    }
}