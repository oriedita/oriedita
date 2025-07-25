package origami.crease_pattern;

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

    public static CustomLineTypes from(int customType){
        for(CustomLineTypes ls : values()){
            if(ls.customType == customType){
                return ls;
            }
        }
        throw new IllegalArgumentException();
    }
}
