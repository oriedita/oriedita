package origami.folding.constraint;

import java.util.List;

public class LayerOrderConstraint {
    private final int faceId;
    private final Type type;
    private final List<Integer> overlapping;


    public LayerOrderConstraint(int faceId, Type type, List<Integer> overlapping) {
        this.faceId = faceId;
        this.type = type;
        this.overlapping = overlapping;
    }

    public int getFaceId() {
        return faceId;
    }

    public Type getType() {
        return type;
    }

    public Iterable<Integer> getOverlapping() {
        return overlapping;
    }

    public enum Type {
        TOP,
        BOTTOM
    }
}
