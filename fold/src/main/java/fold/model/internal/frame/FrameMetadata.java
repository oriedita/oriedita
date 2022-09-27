package fold.model.internal.frame;

import java.util.List;

/**
 * Frame Metadata (frame_...)
 */
public class FrameMetadata {
    /**
     * The human author.
     */
    private String author;
    /**
     * A title for the frame.
     */
    private String title;
    /**
     * A description of the frame.
     */
    private String description;
    /**
     * A subjective interpretation about what the frame represents. Some standard frame classes:
     * <p>
     * "creasePattern": a crease pattern (unfolded)
     * "foldedForm": a folded form/state, e.g. flat folding or 3D folding
     * "graph": vertices and edges, but no lengths or faces
     * "linkage": vertices and edges and edge lengths, but no faces
     * Custom classes should have a colon in them; see Custom Properties below.
     */
    private List<String> classes;
    /**
     * Attributes that objectively describe properties of the folded structure being represented (array of strings).
     * Some standard frame attributes include
     * <p>
     * "2D": the coordinates lie in 2D (xy); z coordinates are all implicitly or explicitly 0
     * "3D": the coordinates lie in 3D (xyz) and not 2D (xy)
     * "abstract": the polyhedral complex is not embedded in Euclidean space, so there are no vertex coordinates
     * (but there might be edge lengths defining intrinsic geometry)
     * "manifold": the polyhedral complex is a manifold (has at most two faces incident to each edge)
     * "nonManifold": the polyhedral complex is not a manifold (has more than two faces incident to an edge)
     * "orientable": the polyhedral complex is orientable, meaning it can be assigned a consistent normal direction (and hence it is also manifold)
     * "nonOrientable": the polyhedral complex is not orientable, meaning it cannot be assigned a consistent normal direction
     * "selfTouching": the polyhedral complex has faces that touch in their relative interiors, so you probably want a face ordering
     * "nonSelfTouching": the polyhedral complex has no touching faces, so face ordering isn't needed
     * "selfIntersecting": the polyhedral complex has properly intersecting faces
     * "nonSelfIntersecting": the polyhedral complex has no properly intersecting faces
     * Custom attributes should have a colon in them; see Custom Properties below.
     */
    private List<String> attributes;
    /**
     * Physical or logical unit that all coordinates are relative to (a string). Standard defined values
     * are as follows. You can also use a custom string, but it will probably not be understood by software.
     * <p>
     * "unit" (equivalent to not specifying a unit): no physical meaning
     * "in": inches (25.4 mm)
     * "pt": desktop publishing/PostScript points (1/72 in)
     * "m": meters (1/299,792,458 light seconds)
     * "cm": centimeters (1/100 meters)
     * "mm": millimeters (1/1000 meters)
     * "um": microns (1/1,000,000 meters)
     * "nm": nanometers (1/1,000,000,000 meters)
     */
    private String unit;
    /**
     * Parent frame ID. Intuitively, this frame (the child) is a modification (or, in general, is related to) the
     * parent frame. This property is optional, but enables organizing frames into a tree structure.
     */
    private Integer parent;
    /**
     * Boolean. If true, any properties in the parent frame (or recursively inherited from an ancestor) that is
     * not overridden in this frame are automatically inherited, allowing you to avoid duplicated data in many
     * cases. For example, the frame can change the vertex coordinates (vertices_coords) while inheriting the
     * structure of the parent's mesh.
     */
    private Boolean inherit;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Boolean getInherit() {
        return inherit;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }
}
