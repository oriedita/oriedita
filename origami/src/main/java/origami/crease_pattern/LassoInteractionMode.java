package origami.crease_pattern;

public enum LassoInteractionMode {
    INTERSECT(0),
    CONTAIN(1);

    private final int mode;

    LassoInteractionMode(int mode) {
        this.mode = mode;
    }
}