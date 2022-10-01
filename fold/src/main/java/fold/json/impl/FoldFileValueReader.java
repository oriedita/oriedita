package fold.json.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.jr.ob.api.ValueReader;
import com.fasterxml.jackson.jr.ob.impl.JSONReader;
import fold.model.FoldFile;
import fold.model.FoldFrame;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class FoldFileValueReader<T extends FoldFile> extends ValueReader {
    private final Class<T> type;

    public FoldFileValueReader(Class<T> type) {
        super(FoldFile.class);
        this.type = type;
    }

    @Override
    public Object read(JSONReader reader, JsonParser p) throws IOException {
        FoldFile instance;
        try {
            instance = type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        FoldFrameValueReader.FoldFrameFactory foldFrameFactory = new FoldFrameValueReader.FoldFrameFactory(instance);

        String fieldName;
        while ((fieldName = p.nextFieldName()) != null) {
            switch (fieldName) {
                case "file_spec":
                    p.nextToken();
                    instance.setSpec(p.getDoubleValue());
                    break;
                case "file_creator":
                    instance.setCreator(p.nextTextValue());
                    break;
                case "file_author":
                    instance.setAuthor(p.nextTextValue());
                    break;
                case "file_title":
                    instance.setTitle(p.nextTextValue());
                    break;
                case "file_description":
                    instance.setDescription(p.nextTextValue());
                    break;
                case "file_classes":
                    p.nextToken();
                    instance.getClasses().addAll(reader.readListOf(String.class));
                    break;
                case "file_frames":
                    p.nextToken();
                    instance.getFrames().addAll(reader.readListOf(FoldFrame.class));
                    break;
                default:
                    if (fieldName.contains(":")) {
                        p.nextToken();
                        instance.getCustomPropertyMap().put(fieldName, reader.readValue());
                    } else {
                        foldFrameFactory.readField(fieldName, reader, p);
                    }
                    break;
            }
        }

        foldFrameFactory.postProcess();

        return instance;
    }
}
