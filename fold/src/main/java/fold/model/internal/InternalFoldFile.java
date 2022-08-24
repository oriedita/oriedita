package fold.model.internal;

import com.fasterxml.jackson.annotation.*;
import fold.FoldFileFormatException;
import fold.model.internal.file.FileMetadata;

import java.util.*;

/**
 * This file maps directly to the FOLD Specification (version 1.1)
 *
 * @see <a href="https://github.com/edemaine/fold/blob/v0.11.3/doc/spec.md">FOLD Specification (version 1.1)</a>
 */
@JsonPropertyOrder({"file_spec", "file_creator", "file_classes", "frame_classes", "vertices_coords", "rootFrame", "foldCustomProps"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonRootName("fold")
public class InternalFoldFile extends InternalFoldFrame {
    private static final List<String> KNOWN_PROPERTIES = Arrays.asList(
            "file_spec", "file_creator", "file_author", "file_title", "file_description", "file_classes", "file_frames",
            "frame_author", "frame_title", "frame_description", "frame_classes", "frame_attributes", "frame_unit",
            "vertices_coords", "vertices_vertices", "vertices_faces",
            "edges_vertices", "edges_faces", "edges_assignment", "edges_foldAngle", "edges_length",
            "faces_vertices", "faces_edges",
            "faceOrders", "edgeOrders"
    );
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
    public void setCustomProperty(String name, Object value) throws FoldFileFormatException {
        // Due to how @JsonAnySetter works together with @JsonUnwrapped
        // Every value is passed to setCustomProperty. Only values containing
        // a : are actually allowed as custom properties.
        if (name.indexOf(':') != -1) {
            this.customPropertyMap.put(name, value);
            return;
        }

        // If name is not in KNOWN_PROPERTIES there is something wrong about this fold file.
        // Reject the file.
        if (KNOWN_PROPERTIES.contains(name)) {
            return;
        }

        throw new FoldFileFormatException("Name not allowed in fold file: " + name);
    }

    @JsonAnyGetter
    public Map<String, Object> getCustomPropertyMap() {
        return customPropertyMap;
    }
}
