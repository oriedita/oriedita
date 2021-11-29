package fold.model;

/**
 * For an edge, representing its fold direction assignment.
 */
public enum FoldEdgeAssignment {
    /**
     * Border/boundary edge
     */
    B,
    /**
     * Mountain fold
     */
    M,
    /**
     * Valley fold
     */
    V,
    /**
     * Flat (unfolded) fold
     */
    F,
    /**
     * Unassigned/unknown
     */
    U,
}
