package fold.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * For an edge, representing its fold direction assignment.
 */
public enum FoldEdgeAssignment {
    /**
     * Border/boundary edge
     */
    BORDER("B"),
    /**
     * Mountain fold
     */
    MOUNTAIN_FOLD("M"),
    /**
     * Valley fold
     */
    VALLEY_FOLD("V"),
    /**
     * Flat (unfolded) fold
     */
    FLAT_FOLD("F"),
    /**
     * Unassigned/unknown
     */
    UNASSIGNED("U"),
    ;

    private final String letter;

    FoldEdgeAssignment(String letter) {
        this.letter = letter;
    }

    @JsonValue
    public String getLetter() {
        return letter;
    }
}
