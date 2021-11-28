package oriedita.editor.export.fold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FoldObjectMapper extends ObjectMapper {
    {
        SimpleModule module = new SimpleModule();
        registerModule(module);

        setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(SerializationFeature.INDENT_OUTPUT, true);
    }
}
