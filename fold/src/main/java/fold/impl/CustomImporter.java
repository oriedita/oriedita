package fold.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fold.FoldFileFormatException;
import fold.Importer;
import fold.json.FoldObjectMapper;
import fold.model.FoldFile;
import fold.model.internal.InternalFoldFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class CustomImporter<T extends FoldFile> implements Importer<T> {
    private final Class<T> tClass;

    public CustomImporter(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T importFile(File file) throws FoldFileFormatException {
        try {
            ObjectMapper mapper = new FoldObjectMapper();

            T instance = tClass.getDeclaredConstructor().newInstance();

            return AdapterFactory.getCustomFoldFileAdapter(InternalFoldFile.class, tClass)
                    .convert(mapper.readValue(file, InternalFoldFile.class), instance);
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
