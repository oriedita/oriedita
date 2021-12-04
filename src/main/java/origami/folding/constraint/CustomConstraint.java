package origami.folding.constraint;

import origami.crease_pattern.element.Point;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Constrains the layer order in that some face in top has to lay above every face
 * in bottom (if faceOrder is NORMAL, or below every bottom if type is FLIPPED)
 * the faces in bottom (union) top have to be all faces of some subface (all faces of the subface must be in either
 * bottom or top, and all faces from bottom and top have to be in the subface)
 */
public class CustomConstraint {
    private final FaceOrder faceOrder;
    private final Set<Integer> bottom;
    private final Set<Integer> top;
    private final Set<Integer> all;
    private final Point pos;
    // used for drawing the constraints
    private final Type type;

    public CustomConstraint(FaceOrder faceOrder, Collection<Integer> top, Collection<Integer> bottom, Point pos, Type type) {
        this.faceOrder = faceOrder;
        this.bottom = new HashSet<>(bottom);
        this.top = new HashSet<>(top);
        this.pos = pos;
        this.type = type;
        this.all = new HashSet<>(top);
        this.all.addAll(bottom);
    }


    public Point getPos() {
        return pos;
    }

    public Collection<Integer> getBottom() {
        return bottom;
    }

    public FaceOrder getFaceOrder() {
        return faceOrder;
    }

    public Collection<Integer> getTop() {
        return top;
    }

    public Collection<Integer> getAll() {
        return all;
    }

    public CustomConstraint inverted() {
        return new CustomConstraint(faceOrder, bottom, top, pos, type.opposite());
    }

    public Type getType() {
        return type;
    }

    public enum FaceOrder {
        NORMAL,
        FLIPPED
    }

    public enum Type {
        COLOR_BACK,
        COLOR_FRONT,
        CUSTOM;

        public Type opposite() {
            switch (this) {
                case COLOR_FRONT:
                    return COLOR_BACK;
                case COLOR_BACK:
                    return COLOR_FRONT;
                default:
                    return CUSTOM;
            }
        }
    }
}
