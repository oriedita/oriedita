package jp.gr.java_conf.mt777.origami.orihime.oriagari_zu;

public enum FoldedFigureState {
    FRONT_0,
    BACK_1,
    BOTH_2,
    TRANSPARENT_3,
    ;

    public FoldedFigureState advance() {
        return values()[(ordinal() + 1) % values().length];
    }
}
