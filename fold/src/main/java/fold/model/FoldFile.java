package fold.model;

import com.fasterxml.jackson.annotation.*;
import fold.model.file.FileMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * Following the FOLD Specification (version 1.1)
 *
 * @see <a href="https://github.com/edemaine/fold/blob/v0.11.3/doc/spec.md">FOLD Specification (version 1.1)</a>
 */
@JsonPropertyOrder({"file_spec", "file_creator", "file_classes", "frame_classes", "vertices_coords", "rootFrame", "foldCustomProps"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonRootName("fold")
public class FoldFile extends FoldFrame {
    /**
     * Custom properties.
     *
     * To add custom data to the FOLD format specific to your software, include a colon (:) in the property key,
     * where the part before the colon identifies your software.
     */
    private final Map<String, Object> customPropertyMap = new HashMap<>();
    /**
     * File-level metadata.
     */
    private FileMetadata file = new FileMetadata();

    @JsonUnwrapped(prefix = "file_")
    public FileMetadata getFile() {
        return file;
    }

    @JsonUnwrapped(prefix = "file_")
    public void setFile(FileMetadata file) {
        this.file = file;
    }

    @JsonAnySetter
    public void setCustomProperty(String name, Object value) {
        this.customPropertyMap.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getCustomPropertyMap() {
        return customPropertyMap;
    }
}
