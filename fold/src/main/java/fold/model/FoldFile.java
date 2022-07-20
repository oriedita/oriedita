package fold.model;

import java.util.*;

public class FoldFile extends FoldFrame {
    /**
     * Custom properties.
     * <p>
     * To add custom data to the FOLD format specific to your software, include a colon (:) in the property key,
     * where the part before the colon identifies your software.
     */
    private final Map<String, Object> customPropertyMap = new HashMap<>();
    /**
     * The version of the FOLD spec that the file assumes.
     * See the top of this spec for the current value. Strongly recommended,
     * in case we ever have to make backward-incompatible changes.
     */
    private double spec = 1.1;
    /**
     * The software that created the file. Recommended for files
     * output by computer software; less important for files made by hand.
     */
    private String creator = "oriedita";
    /**
     * The human author.
     */
    private String author;
    /**
     * A title for the entire file.
     */
    private String title;
    /**
     * A description of the entire file.
     */
    private String description;
    /**
     * A subjective interpretation about what the entire file represents.
     * Some standard file classes include:
     * <p>
     * "singleModel": A single origami model, possibly still in multiple frames to represent crease pattern, folded form, etc.
     * "multiModel": Multiple origami models collected together into one file
     * "animation": Animation of sequence of frames, e.g., illustrating a continuous folding motion
     * "diagrams": A sequence of frames representing folding steps, as in origami diagrams
     * Custom classes should have a colon in them; see Custom Properties below.
     */
    private List<String> classes = new ArrayList<>();
    /**
     * Array of frame dictionaries.
     */
    private List<FoldFrame> frames = new ArrayList<>();

    public Map<String, Object> getCustomPropertyMap() {
        return customPropertyMap;
    }

    public void setCustomProperty(String namespace, String key, Object value) {
        customPropertyMap.put(formatCustomProperty(namespace, key), value);
    }

    public Object getCustomProperty(String namespace, String key) {
        return customPropertyMap.getOrDefault(formatCustomProperty(namespace, key), null);
    }

    public void removeCustomProperty(String ns, String key) {
        customPropertyMap.remove(formatCustomProperty(ns, key));
    }

    private String formatCustomProperty(String namespace, String key) {
        return String.format("%s:%s", namespace, key);
    }

    public double getSpec() {
        return spec;
    }

    public void setSpec(double spec) {
        this.spec = spec;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

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

    public List<FoldFrame> getFrames() {
        return frames;
    }

    public void setFrames(List<FoldFrame> frames) {
        this.frames = frames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoldFile foldFile = (FoldFile) o;
        return Double.compare(foldFile.getSpec(), getSpec()) == 0 && Objects.equals(getCustomPropertyMap(), foldFile.getCustomPropertyMap()) && Objects.equals(getCreator(), foldFile.getCreator()) && Objects.equals(getAuthor(), foldFile.getAuthor()) && Objects.equals(getTitle(), foldFile.getTitle()) && Objects.equals(getDescription(), foldFile.getDescription()) && Objects.equals(getClasses(), foldFile.getClasses()) && Objects.equals(getFrames(), foldFile.getFrames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomPropertyMap(), getSpec(), getCreator(), getAuthor(), getTitle(), getDescription(), getClasses(), getFrames());
    }
}
