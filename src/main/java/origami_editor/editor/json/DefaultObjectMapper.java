package origami_editor.editor.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.io.IOException;

public class DefaultObjectMapper extends ObjectMapper {
    {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Color.class, new ColorSerializer());
        module.addDeserializer(Color.class, new ColorDeserializer());
        module.addSerializer(Point.class, new PointSerializer());
        module.addDeserializer(Point.class, new PointDeserializer());
        registerModule(module);
    }

    private static class ColorSerializer extends JsonSerializer<Color> {
        @Override
        public void serialize(Color value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeString(Integer.toHexString(value.getRGB()));
        }
    }

    private static class ColorDeserializer extends JsonDeserializer<Color> {
        @Override
        public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new Color(Integer.parseUnsignedInt(p.getValueAsString(), 16), true);
        }
    }

    private static class PointSerializer extends JsonSerializer<Point> {
        @Override
        public void serialize(Point point, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(point.getX() + "," + point.getY());
        }
    }

    private static class PointDeserializer extends JsonDeserializer<Point> {
        @Override
        public Point deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String[] values = jsonParser.getValueAsString().split(",");
            return new Point(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
        }
    }
}
