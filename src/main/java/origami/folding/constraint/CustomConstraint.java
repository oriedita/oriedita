package origami.folding.constraint;

import origami.crease_pattern.element.Point;

import java.util.List;

/**
 * says that some face in top has to lay above every face in bottom (if faceOrder is NORMAL, or below every bottom if type is FLIPPED)
 */
public class CustomConstraint {
    private final FaceOrder faceOrder;
    private final List<Integer> top;
    private final List<Integer> bottom;
    private final Point pos;
    private final boolean inverted;


    public CustomConstraint(FaceOrder faceOrder, List<Integer> top, List<Integer> bottom, Point pos, boolean inverted) {
        this.faceOrder = faceOrder;
        this.top = top;
        this.bottom = bottom;
        this.pos = pos;
        this.inverted = inverted;
    }


    public Point getPos() {
        return pos;
    }

    public List<Integer> getTop() {
        return inverted? bottom:  top;
    }

    public FaceOrder getFaceOrder() {
        return faceOrder;
    }

    public Iterable<Integer> getBottom() {
        return inverted? top : bottom;
    }

    public CustomConstraint inverted() {
        return new CustomConstraint(faceOrder, this.top, bottom, pos, !inverted);
    }

    public boolean isInverted() {
        return inverted;
    }

    public enum FaceOrder {
        NORMAL,
        FLIPPED
    }
}
