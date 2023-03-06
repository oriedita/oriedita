package oriedita.editor.canvas;

/**
 * Expression of polygonal line, 1 = color, 2 = color and shape, 3 = black one-dot chain line, 4 = black two-dot chain line
 */
public enum LineStyle {
    COLOR(1),
    BLACK_WHITE(2),
    COLOR_AND_SHAPE(3),
    BLACK_ONE_DOT(4),
    BLACK_TWO_DOT(5),
    ;

    private final int type;

    LineStyle(int type) {
        this.type = type;
    }

    public static LineStyle from(String type) {
        return from(Integer.parseInt(type));
    }

    public static LineStyle from(int type) {
        for (LineStyle ls : values()) {
            if (ls.type == type) {
                return ls;
            }
        }

        throw new IllegalArgumentException();
    }

    public LineStyle advance() {
        return values()[(ordinal() + 1) % values().length];
    }

    @Override
    public String toString() {
        return Integer.toString(type);
    }

    public int getType(){
        return this.type;
    }
}
