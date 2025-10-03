package origami.crease_pattern;

import origami.crease_pattern.element.LineColor;

public enum CustomLineTypes {
    ANY(-1),
    EDGE(0),
    MANDV(1),
    MOUNTAIN(2),
    VALLEY(3),
    AUX(4);
    private final int customType;

    CustomLineTypes(int customType) {
        this.customType = customType;
    }

    public int getNumber(){
        return customType;
    }

    public int getNumberForLineColor(){
        if(this == CustomLineTypes.ANY){
            return EDGE.customType;
        }
        return this == CustomLineTypes.EDGE ? this.getNumber() : this.getNumber() - 1;
    }

    public LineColor getLineColor(){
        return switch (this){
            case ANY, EDGE -> LineColor.BLACK_0;
            case MANDV, MOUNTAIN -> LineColor.RED_1;
            case VALLEY -> LineColor.BLUE_2;
            case AUX -> LineColor.CYAN_3;
        };
    }

    public boolean matches(LineColor lineColor){
        return switch (this) {
            case ANY -> true;
            case EDGE -> lineColor == LineColor.BLACK_0;
            case MANDV -> lineColor == LineColor.RED_1 || lineColor == LineColor.BLUE_2;
            case MOUNTAIN -> lineColor == LineColor.RED_1;
            case VALLEY -> lineColor == LineColor.BLUE_2;
            case AUX -> lineColor == LineColor.CYAN_3;
        };
    }

    public static CustomLineTypes from(int customType){
        for(CustomLineTypes ls : values()){
            if(ls.customType == customType){
                return ls;
            }
        }
        throw new IllegalArgumentException();
    }
}
