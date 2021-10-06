//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package origami.folding;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import origami_editor.editor.json.DefaultObjectMapper;

public class JacksonSnapshotSerializer implements SnapshotSerializer {
    private final PrettyPrinter pp = new DefaultPrettyPrinter("") {
        {
            Indenter lfOnlyIndenter = new DefaultIndenter("  ", "\n");
            this.indentArraysWith(lfOnlyIndenter);
            this.indentObjectsWith(lfOnlyIndenter);
        }

        public DefaultPrettyPrinter createInstance() {
            return new DefaultPrettyPrinter(this);
        }

        public DefaultPrettyPrinter withSeparators(Separators separators) {
            this._separators = separators;
            this._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
            return this;
        }
    };
    private final ObjectMapper objectMapper = new DefaultObjectMapper() {
        {
            this.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
            this.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            this.setSerializationInclusion(Include.NON_NULL);
            this.registerModule(new JavaTimeModule());
            this.registerModule(new Jdk8Module());
            this.setVisibility(this.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(Visibility.ANY).withGetterVisibility(Visibility.NONE).withSetterVisibility(Visibility.NONE).withCreatorVisibility(Visibility.NONE));
            JacksonSnapshotSerializer.this.configure(this);
        }
    };

    public JacksonSnapshotSerializer() {
    }

    public void configure(ObjectMapper objectMapper) {
    }

    public String apply(Object[] objects) {
        try {
            return this.objectMapper.writer(this.pp).writeValueAsString(objects);
        } catch (Exception var3) {
            throw new SnapshotExtensionException("Jackson Serialization failed", var3);
        }
    }

    public String getOutputFormat() {
        return SerializerType.JSON.name();
    }
}
