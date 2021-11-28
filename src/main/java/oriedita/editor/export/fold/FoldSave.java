package oriedita.editor.export.fold;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonPropertyOrder({"file_spec", "file_creator", "file_classes", "frame_classes", "vertices_coords", "rootFrame", "foldCustomProps"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FoldSave  extends FoldFrame{
    private double fileSpec = 1.1;
    private String fileCreator = "oriedita";
    private String fileAuthor;
    private String fileTitle;
    private String fileDescription;
    private String[] fileClasses;
    private FoldFrame[] fileFrames;

    @JsonProperty("file_spec")
    public double getFileSpec() {
        return fileSpec;
    }

    @JsonProperty("file_spec")
    public void setFileSpec(double fileSpec) {
        this.fileSpec = fileSpec;
    }

    @JsonProperty("file_creator")
    public String getFileCreator() {
        return fileCreator;
    }

    @JsonProperty("file_creator")
    public void setFileCreator(String fileCreator) {
        this.fileCreator = fileCreator;
    }

    @JsonProperty("file_author")
    public String getFileAuthor() {
        return fileAuthor;
    }

    @JsonProperty("file_author")
    public void setFileAuthor(String fileAuthor) {
        this.fileAuthor = fileAuthor;
    }

    @JsonProperty("file_title")
    public String getFileTitle() {
        return fileTitle;
    }

    @JsonProperty("file_title")
    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    @JsonProperty("file_description")
    public String getFileDescription() {
        return fileDescription;
    }

    @JsonProperty("file_description")
    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    @JsonProperty("file_classes")
    public String[] getFileClasses() {
        return fileClasses;
    }

    @JsonProperty("file_classes")
    public void setFileClasses(String[] fileClasses) {
        this.fileClasses = fileClasses;
    }

    @JsonProperty("file_frames")
    public FoldFrame[] getFileFrames() {
        return fileFrames;
    }

    @JsonProperty("file_frames")
    public void setFileFrames(FoldFrame[] fileFrames) {
        this.fileFrames = fileFrames;
    }

    private Map<String, Object> customPropertyMap = new HashMap<>();

    @JsonAnySetter
    public void setCustomProperty(String name, Object value) {
        this.customPropertyMap.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getCustomPropertyMap() {
        return customPropertyMap;
    }
}
