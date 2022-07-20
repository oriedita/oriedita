package fold.model.internal.file;

import fold.model.internal.InternalFoldFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * File Metadata (file_...)
 */
public class FileMetadata {
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
    private List<InternalFoldFrame> frames = new ArrayList<>();

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

    public List<InternalFoldFrame> getFrames() {
        return frames;
    }

    public void setFrames(List<InternalFoldFrame> frames) {
        this.frames = frames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadata that = (FileMetadata) o;
        return Double.compare(that.getSpec(), getSpec()) == 0 && Objects.equals(getCreator(), that.getCreator()) && Objects.equals(getAuthor(), that.getAuthor()) && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription()) && getClasses().equals(that.getClasses()) && getFrames().equals(that.getFrames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpec(), getCreator(), getAuthor(), getTitle(), getDescription(), getClasses(), getFrames());
    }
}
