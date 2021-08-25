package jp.gr.java_conf.mt777.origami.orihime;

/**
 * Expression of polygonal line, 1 = color, 2 = color and shape, 3 = black one-dot chain line, 4 = black two-dot chain line
 */
public enum LineStyle {
    COLOR,
    COLOR_AND_SHAPE,
    BLACK_ONE_DOT,
    BLACK_TWO_DOT,
    ;

    public LineStyle advance() {
        return values()[(ordinal() + 1) % values().length];
    }
}
