package fold.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class FoldObjectMapper extends ObjectMapper {
    {
        SimpleModule module = new SimpleModule();
        registerModule(module);

        setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(SerializationFeature.INDENT_OUTPUT, true);
    }
}
