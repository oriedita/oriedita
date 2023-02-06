package oriedita.editor.handler;

public enum CustomLineTypes {
    ANY(-1),
    EGDE(0),
    MOUNTAIN(1),
    VALLEY(2),
    AUX(3);
    private final int customType;

    CustomLineTypes(int customType) {
        this.customType = customType;
    }

    public int getType(){
        return customType;
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
