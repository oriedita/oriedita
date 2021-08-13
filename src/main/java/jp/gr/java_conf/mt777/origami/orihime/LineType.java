package jp.gr.java_conf.mt777.origami.orihime;

public enum LineType {
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

    private int type;

    LineType(int type) {
        this.type = type;
    }
}
