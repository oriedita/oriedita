package jp.gr.java_conf.mt777.origami.orihime;

import javax.sound.sampled.Line;

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
    PURPLE_8(8),
    OTHER_9(9);

    private int type;

    LineType(int type) {
        this.type = type;
    }

    public static LineType fromNumber(int type) {
        for (var val : values()) {
            if (val.getNumber() == type) {
                return val;
            }
        }

        throw new IllegalArgumentException("Type " + type + " unknown");
    }

    public static LineType from(String type) {
        return fromNumber(Integer.parseInt(type));
    }

    public LineType advanceFolding() {
        if (!isFoldingLine()) {
            throw new IllegalArgumentException("Cannot advance folding on non folding line " + type);
        }

        if (type == 2) {
            return BLACK_0;
        }

        return fromNumber(type + 1);
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
