package jp.gr.java_conf.mt777.origami.orihime;

public enum LineType {
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
    PURPLE_8(8);

    public int getNumber() {
        return this.type;
    }

    private int type;

    LineType(int type) {
        this.type = type;
    }

    public boolean isFoldingLine() {
        return this == BLACK_0 || this == RED_1 || this == BLUE_2;
    }

    public static LineType fromNumber(int type) {
        for (var val : values()) {
            if (val.getNumber() == type) {
                return val;
            }
        }

        throw new IllegalArgumentException();
    }

    public static LineType from(String type) {
        return fromNumber(Integer.parseInt(type));
    }

    @Override
    public String toString() {
        return Integer.toString(type);
    }
}
