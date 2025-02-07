package origami.crease_pattern.element;

public enum LineColor {
    ANGLE(-2),
    NONE(-1),
    BLACK_0(0),
    RED_1(1),
    BLUE_2(2),
    CYAN_3(3),
    ORANGE_4(4),
    MAGENTA_5(5),
    GREEN_6(6),
    YELLOW_7(7),
    PURPLE_8(8),
    OTHER_9(9),
    GREY_10(10);

    private final int type;

    LineColor(int type) {
        this.type = type;
    }

    public static LineColor fromNumber(int type) {
        for (LineColor val : values()) {
            if (val.getNumber() == type) {
                return val;
            }
        }

        throw new IllegalArgumentException("Type " + type + " unknown");
    }

    public static LineColor from(String type) {
        return fromNumber(Integer.parseInt(type));
    }

    public LineColor advanceFolding() {
        if (!isFoldingLine()) {
            throw new IllegalArgumentException("Cannot advance folding on non folding line " + type);
        }

        if (type == 2) {
            return BLACK_0;
        }

        return fromNumber(type + 1);
    }

    public LineColor changeMV() {
        if (this == RED_1) return BLUE_2;
        if (this == BLUE_2) return RED_1;

        //Zaema's change #337
        if (this == BLACK_0) return CYAN_3;
        if (this == CYAN_3) return BLACK_0;

        return this;
    }

    public LineColor changeAuxColor() {
        if (this == ORANGE_4) return YELLOW_7;
        if (this == YELLOW_7) return ORANGE_4;

        return this;
    }

    public int getNumber() {
        return this.type;
    }

    public boolean isFoldingLine() {
        return this == BLACK_0 || this == RED_1 || this == BLUE_2;
    }

    @Override
    public String toString() {
        return Integer.toString(type);
    }
}
