package fold.json.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.jr.ob.api.ValueWriter;
import com.fasterxml.jackson.jr.ob.impl.JSONWriter;
import fold.FoldFileFormatException;
import fold.model.FoldFile;
import fold.model.FoldFrame;

import java.io.IOException;
import java.util.Map;

public class FoldFileWriter implements ValueWriter {
    @Override
    public void writeValue(JSONWriter context, JsonGenerator g, Object value) throws IOException {
        FoldFile foldFile = (FoldFile) value;

        g.writeStartObject();
        g.writeNumberField("file_spec", foldFile.getSpec());
        if (foldFile.getCreator() != null)
            g.writeStringField("file_creator", foldFile.getCreator());
        if (foldFile.getAuthor() != null)
            g.writeStringField("file_author", foldFile.getAuthor());
        if (foldFile.getTitle() != null)
            g.writeStringField("file_title", foldFile.getTitle());
        if (foldFile.getDescription() != null)
            g.writeStringField("file_description", foldFile.getDescription());

        if (foldFile.getClasses() != null && foldFile.getClasses().size() > 0) {
            g.writeFieldName("file_classes");
            g.writeArray(foldFile.getClasses().toArray(new String[0]), 0, foldFile.getClasses().size());
        }

        if (foldFile.getFrames().size() > 0) {
            g.writeArrayFieldStart("file_frames");
            for (FoldFrame frame : foldFile.getFrames()) {
                context.writeValue(frame);
            }
            g.writeEndArray();
        }

        new FoldFrameWriter().partialWriteValue(context, g, foldFile);

        for (Map.Entry<String, Object> entry : foldFile.getCustomPropertyMap().entrySet()) {
            if (!entry.getKey().contains(":")) {
                throw new FoldFileFormatException("Property not valid");
            }
            g.writeFieldName(entry.getKey());
            context.writeValue(entry.getValue());
        }

        g.writeEndObject();
    }

    @Override
    public Class<?> valueType() {
        return FoldFile.class;
    }


}
